package me.tatarka.rxloader;

import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;

class RxLoaderBackendFragmentHelper implements RxLoaderBackend {
    private Map<String, CachingWeakRefSubscriber> subscriptionMap = new HashMap<String, CachingWeakRefSubscriber>();
    private Map<String, SaveItem> saveItemMap = new HashMap<String, SaveItem>();
    private Bundle savedState;

    public void onCreate(Bundle savedState) {
        this.savedState = savedState;
    }

    public void onDestroy() {
        unsubscribeAll();
        subscriptionMap.clear();
    }

    public void onSaveInstanceState(Bundle outState) {
        for (SaveItem<?> item : saveItemMap.values()) {
            onSave(item, outState);
        }
    }

    private <T> void onSave(SaveItem<T> item, Bundle outState) {
        SaveCallback<T> saveCallback = item.saveCallbackRef.get();
        if (saveCallback != null) {
            saveCallback.onSave(item.tag, item.value, outState);
        }
    }

    @Override
    public <T> CachingWeakRefSubscriber<T> get(String tag) {
        return subscriptionMap.get(tag);
    }

    @Override
    public <T> void put(final String tag, CachingWeakRefSubscriber<T> subscriber) {
        subscriptionMap.put(tag, subscriber);
        if (saveItemMap.containsKey(tag)) {
            subscriber.setSave(new CachingWeakRefSubscriber.SaveCallback<T>() {
                @Override
                public void onNext(Object value) {
                    SaveItem item = saveItemMap.get(tag);
                    if (item != null) item.value = value;
                }
            });
        }
    }

    @Override
    public <T> void setSave(final String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
        SaveItem<T> item = new SaveItem<T>(tag, saveCallbackRef);

        if (savedState != null) {
            SaveCallback<T> saveCallback = saveCallbackRef.get();
            if (saveCallback != null) {
                T value = saveCallback.onRestore(tag, savedState);
                item.value = value;
                observer.onNext(value);
            }
        }

        saveItemMap.put(tag, item);

        CachingWeakRefSubscriber subscriber = get(tag);
        if (subscriber != null) {
            subscriber.setSave(new CachingWeakRefSubscriber.SaveCallback() {
                @Override
                public void onNext(Object value) {
                    SaveItem item = saveItemMap.get(tag);
                    if (item != null) item.value = value;
                }
            });
        }
    }

    @Override
    public void unsubscribeAll() {
        for (CachingWeakRefSubscriber subscription : subscriptionMap.values()) {
            subscription.unsubscribe();
            subscription.set(null);
        }
    }

    private static class SaveItem<T> {
        final String tag;
        final WeakReference<SaveCallback<T>> saveCallbackRef;
        T value;

        private SaveItem(String tag, WeakReference<SaveCallback<T>> saveCallbackRef) {
            this.tag = tag;
            this.saveCallbackRef = saveCallbackRef;
        }
    }
}
