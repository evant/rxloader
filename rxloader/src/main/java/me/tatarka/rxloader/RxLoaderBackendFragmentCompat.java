package me.tatarka.rxloader;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import rx.Observer;

/**
 * Persists the task by running it in a fragment with {@code setRetainInstanceState(true)}. This is
 * used internally by {@link me.tatarka.rxloader.RxLoaderManager}.
 *
 * @author Evan Tatarka
 */
public class RxLoaderBackendFragmentCompat extends Fragment implements RxLoaderBackend {
    private RxLoaderBackendFragmentHelper helper = new RxLoaderBackendFragmentHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        helper.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        helper.onSaveInstanceState(outState);
    }

    @Override
    public <T> CachingWeakRefSubscriber<T> get(String tag) {
        return helper.get(tag);
    }

    @Override
    public <T> void put(String tag, CachingWeakRefSubscriber<T> subscriber) {
        helper.put(tag, subscriber);
    }

    @Override
    public <T> void setSave(String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
        helper.setSave(tag, observer, saveCallbackRef);
    }

    @Override
    public void unsubscribeAll() {
        helper.unsubscribeAll();
    }
}
