package me.tatarka.rxloader;

import android.os.Bundle;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;

class RxLoaderBackendFragmentHelper implements RxLoaderBackend {
    private static final int MY_ID = -1;
    
    private State state = new State();
    private SparseArray<State> childFragmentStates = new SparseArray<State>();
    
    public void onCreate(Bundle savedState) {
        onCreate(MY_ID, savedState);
    }
    
    public void onCreate(int id, Bundle savedState) {
        getState(id).savedState = savedState;
    }

    public void onDestroy() {
        onDestroy(MY_ID);
    }
    
    public void onDestroy(int id) {
        unsubscribeAll();
        getState(id).subscriptionMap.clear();
    }

    public void onSaveInstanceState(Bundle outState) {
        onSaveInstanceState(MY_ID, outState);
    }
    
    public void onSaveInstanceState(int id, Bundle outState) {
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

    @Override
    public <T> CachingWeakRefSubscriber<T> get(String tag) {
        return get(MY_ID, tag);
    }
    
    public <T> CachingWeakRefSubscriber<T> get(int id, String tag) {
        return getState(id).subscriptionMap.get(tag);
    }

    @Override
    public <T> void put(final String tag, CachingWeakRefSubscriber<T> subscriber) {
        put(MY_ID, tag, subscriber);
    }
    
    public <T> void put(int id, final String tag, CachingWeakRefSubscriber<T> subscriber) {
        final State state = getState(id);
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
        setSave(MY_ID, tag, observer, saveCallbackRef); 
    }

    public <T> void setSave(int id, final String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
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
        unsubscribeAll(MY_ID);
    }
    
    public void unsubscribeAll(int id) {
        for (CachingWeakRefSubscriber subscription : getState(id).subscriptionMap.values()) {
            subscription.unsubscribe();
            subscription.set(null);
        }
    }
    
    private State getState(int id) {
        return id == MY_ID ? state : getChildFragmentState(id);
    }
    
    private State getChildFragmentState(int id) {
        State state = childFragmentStates.get(id);
        if (state == null) {
            state = new State();
            childFragmentStates.put(id, state);
        }
        return state;
    }
    
    private static class State {
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
