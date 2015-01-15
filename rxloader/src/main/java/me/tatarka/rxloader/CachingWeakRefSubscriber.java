package me.tatarka.rxloader;

import java.lang.ref.WeakReference;

import rx.Observer;
import rx.Subscription;

class CachingWeakRefSubscriber<T> implements Observer<T>, Subscription {
    private WeakReference<Observer<T>> subscriberRef;
    private Subscription subscription;
    private SaveCallback<T> saveCallback;
    private boolean isComplete;
    private boolean isError;
    private boolean hasValue;
    private Throwable error;
    private T lastValue;

    CachingWeakRefSubscriber() {
        set(null);
    }

    CachingWeakRefSubscriber(RxLoaderObserver<T> observer) {
        set(observer);
    }

    public void set(RxLoaderObserver<T> observer) {
        subscriberRef = new WeakReference<Observer<T>>(observer);
        if (observer == null) return;

        if (!(isComplete || isError)) {
            observer.onStarted();
        }

        if (hasValue) {
            observer.onNext(lastValue);
        }

        if (isComplete) {
            observer.onCompleted();
        } else if (isError) {
            observer.onError(error);
        }
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void setSave(SaveCallback<T> callback) {
        saveCallback = callback;
        if (callback == null) return;

        if (hasValue) {
            callback.onNext(lastValue);
        }
    }

    public Observer<T> get() {
        return subscriberRef.get();
    }

    @Override
    public void onCompleted() {
        isComplete = true;
        Observer<T> subscriber = subscriberRef.get();
        if (subscriber != null) subscriber.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        isError = true;
        error = e;
        Observer<T> subscriber = subscriberRef.get();
        if (subscriber != null) subscriber.onError(e);
    }

    @Override
    public void onNext(T value) {
        hasValue = true;
        lastValue = value;
        Observer<T> subscriber = subscriberRef.get();
        if (subscriber != null) subscriber.onNext(value);
        if (saveCallback != null) saveCallback.onNext(value);
    }

    @Override
    public void unsubscribe() {
        saveCallback = null;
        subscriberRef.clear();
        if (subscription != null) subscription.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        Observer<T> subscriber = subscriberRef.get();
        return subscriber == null;
    }

    public void clear() {
        unsubscribe();
        isComplete = false;
        isError = false;
        hasValue = false;
        error = null;
        lastValue = null;
    }

    interface SaveCallback<T> {
        void onNext(T value);
    }
}
