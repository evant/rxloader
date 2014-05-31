package me.tatarka.rxloader;

import rx.Observer;

/**
 * A specialized {@link rx.Observer} that can also respond to when it's subscribed.
 *
 * @param <T> the observer value type
 */
public abstract class RxLoaderObserver<T> implements Observer<T> {
    /**
     * Called either when the {@link rx.Observer} is subscribed to or when there is the
     * configuration change and the {@code Observer} has not completed. This is where you would show
     * that you are loading something.
     */
    public void onStarted() {

    }

    /**
     * Called either when the {@link rx.Observer} delivers a value or when there is a configuration
     * change and the {@code Observer} has delivered a value. If multiple values have been
     * delivered, only the last one will be re-sent. This is where you would show the result of your
     * async call.
     *
     * @param value the value
     */
    @Override
    public abstract void onNext(T value);

    /**
     * Called either when the {@link rx.Observer} completes or when there is a configuration change
     * and the {@code Observer} has been closed. This is where you would show that your async call
     * has completed.
     */
    @Override
    public void onCompleted() {

    }

    /**
     * Called either when the {@link rx.Observer} has an error or when there is a configuration
     * change and the {@code Observer} has received an error. This is where you would display the
     * error.
     *
     * @param e the error
     */
    @Override
    public void onError(Throwable e) {

    }
}
