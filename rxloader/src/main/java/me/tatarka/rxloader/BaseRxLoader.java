package me.tatarka.rxloader;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

abstract class BaseRxLoader<T> {
    private RxLoaderBackend manager;
    private String tag;
    private RxLoaderObserver<T> observer;
    private Scheduler scheduler;
    private SaveCallback<T> saveCallback;

    BaseRxLoader(RxLoaderBackend manager, String tag, RxLoaderObserver<T> observer) {
        scheduler = AndroidSchedulers.mainThread();
        this.manager = manager;
        this.tag = tag;
        this.observer = observer;

        CachingWeakRefSubscriber<T> subscription = manager.get(tag);
        if (subscription != null) {
            subscription.set(observer);
        }
    }

    protected BaseRxLoader<T> start(Observable<T> observable) {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber == null) {
            manager.put(tag, this, createSubscriber(observable));
        }
        return this;
    }

    protected BaseRxLoader<T> restart(Observable<T> observable) {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber != null) {
            subscriber.unsubscribe();
        }
        manager.put(tag, this, createSubscriber(observable));
        if (saveCallback != null) {
            manager.setSave(tag, observer, new WeakReference<SaveCallback<T>>(saveCallback));
        }
        return this;
    }

    protected BaseRxLoader<T> save(SaveCallback<T> saveCallback) {
        this.saveCallback = saveCallback;
        manager.setSave(tag, observer, new WeakReference<SaveCallback<T>>(saveCallback));
        return this;
    }

    protected BaseRxLoader<T> save() {
        return save(new ParcelableSaveCallback<T>());
    }

    private CachingWeakRefSubscriber<T> createSubscriber(Observable<T> observable) {
        CachingWeakRefSubscriber<T> subscriber = new CachingWeakRefSubscriber<T>(observer);
        subscriber.setSubscription(observable.observeOn(scheduler).subscribe(subscriber));
        return subscriber;
    }

    /**
     * Cancels the task.
     *
     * @return true if the task was started, false otherwise
     */
    public boolean unsubscribe() {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber != null) {
            subscriber.unsubscribe();
            return true;
        }
        return false;
    }

    /**
     * Clears the loader's state. After a configuration change you will no longer received cached
     * values and {@link #start(rx.Observable)} will cause the observable to be executed again.
     * This is useful if the loader is handling transient state (showing a Toast for example).
     */
    public void clear() {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber != null) {
            subscriber.clear();
        }
    }
}
