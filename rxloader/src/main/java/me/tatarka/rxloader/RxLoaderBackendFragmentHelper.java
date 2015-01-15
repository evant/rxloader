package me.tatarka.rxloader;

import android.os.Bundle;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;

class RxLoaderBackendFragmentHelper implements RxLoaderBackend {
    private State state = new State();
    private Map<String, State> childFragmentStates = new HashMap<String, State>();

    public void onCreate(Bundle savedState) {
        onCreate(null, savedState);
    }

    public void onCreate(String id, Bundle savedState) {
        getState(id).savedState = savedState;
    }

    public void onDestroy() {
        onDestroy(null);
    }

    public void onDestroy(String id) {
        unsubscribeAll(id);
        getState(id).subscriptionMap.clear();
    }

    public void onDetach() {
        onDetach(null);
    }

    public void onDetach(String id) {
        getState(id).rxLoader = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        onSaveInstanceState(null, outState);
    }

    public void onSaveInstanceState(String id, Bundle outState) {
        for (SaveItem<?> item : getState(id).saveItemMap.values()) {
            onSave(item, outState);
        }
    }

    private static <T> void onSave(SaveItem<T> item, Bundle outState) {
        SaveCallback<T> saveCallback = item.saveCallbackRef.get();
        if (saveCallback != null) {
            saveCallback.onSave(item.tag, item.value, outState);
        }
    }

    public void onDestroyView(String id) {
        for (CachingWeakRefSubscriber subscription : getState(id).subscriptionMap.values()) {
            subscription.set(null);
        }
    }

    @Override
    public <T> CachingWeakRefSubscriber<T> get(String tag) {
        return get(null, tag);
    }

    public <T> CachingWeakRefSubscriber<T> get(String id, String tag) {
        return getState(id).subscriptionMap.get(tag);
    }

    @Override
    public <T> void put(final String tag, BaseRxLoader<T> rxLoader, CachingWeakRefSubscriber<T> subscriber) {
        put(null, tag, rxLoader, subscriber);
    }

    public <T> void put(String id, final String tag, BaseRxLoader<T> rxLoader, CachingWeakRefSubscriber<T> subscriber) {
        final State state = getState(id);
        state.rxLoader = rxLoader;
        state.subscriptionMap.put(tag, subscriber);
        if (state.saveItemMap.containsKey(tag)) {
            subscriber.setSave(new CachingWeakRefSubscriber.SaveCallback<T>() {
                @Override
                public void onNext(Object value) {
                    SaveItem item = state.saveItemMap.get(tag);
                    if (item != null) item.value = value;
                }
            });
        }
    }

    @Override
    public <T> void setSave(final String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
        setSave(null, tag, observer, saveCallbackRef);
    }

    public <T> void setSave(String id, final String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
        final State state = getState(id);
        SaveItem<T> item = new SaveItem<T>(tag, saveCallbackRef);

        if (state.savedState != null) {
            SaveCallback<T> saveCallback = saveCallbackRef.get();
            if (saveCallback != null) {
                T value = saveCallback.onRestore(tag, state.savedState);
                item.value = value;
                observer.onNext(value);
            }
        }

        state.saveItemMap.put(tag, item);

        CachingWeakRefSubscriber subscriber = get(tag);
        if (subscriber != null) {
            subscriber.setSave(new CachingWeakRefSubscriber.SaveCallback() {
                @Override
                public void onNext(Object value) {
                    SaveItem item = state.saveItemMap.get(tag);
                    if (item != null) item.value = value;
                }
            });
        }
    }

    @Override
    public void unsubscribeAll() {
        unsubscribeAll(null);
    }

    @Override
    public void clearAll() {
        clearAll(null);
    }

    public void unsubscribeAll(String id) {
        for (CachingWeakRefSubscriber subscription : getState(id).subscriptionMap.values()) {
            subscription.unsubscribe();
        }
    }
    
    public void clearAll(String id) {
        for (CachingWeakRefSubscriber subscription : getState(id).subscriptionMap.values()) {
            subscription.clear();
        }
    }

    private State getState(String id) {
        return id == null ? state : getChildFragmentState(id);
    }

    private State getChildFragmentState(String id) {
        State state = childFragmentStates.get(id);
        if (state == null) {
            state = new State();
            childFragmentStates.put(id, state);
        }
        return state;
    }

    private static class State {
        private BaseRxLoader rxLoader;
        private Map<String, CachingWeakRefSubscriber> subscriptionMap = new HashMap<String, CachingWeakRefSubscriber>();
        private Map<String, SaveItem> saveItemMap = new HashMap<String, SaveItem>();
        private Bundle savedState;
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
