package me.tatarka.rxloader;

import rx.Observable;
import rx.functions.Func2;

/**
 * A version of {@link me.tatarka.rxloader.RxLoader} that accepts two arguments to construct the
 * observable.
 *
 * @param <A> the argument type
 * @param <T> the observable's value type
 * @see me.tatarka.rxloader.RxLoader
 */
public class RxLoader2<A, B, T> extends BaseRxLoader<T> {
    private Func2<A, B, Observable<T>> observableFunc;

    RxLoader2(RxLoaderBackend manager, String tag, Func2<A, B, Observable<T>> observableFunc, RxLoaderObserver<T> observer) {
        super(manager, tag, observer);
        this.observableFunc = observableFunc;
    }

    /**
     * Starts the {@link rx.Observable} with the given arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the {@code RxLoader2} for chaining
     * @see RxLoader#start()
     */
    public RxLoader2<A, B, T> start(A arg1, B arg2) {
        start(observableFunc.call(arg1, arg2));
        return this;
    }

    /**
     * Restarts the {@link rx.Observable} with the given arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the {@code RxLoader2} for chaining
     * @see RxLoader#restart()
     */
    public RxLoader2<A, B, T> restart(A arg1, B arg2) {
        restart(observableFunc.call(arg1, arg2));
        return this;
    }

    /**
     * Saves the last value that the {@link rx.Observable} returns in {@link
     * rx.Observer#onNext(Object)} in the Activities'ss ore Fragment's instanceState bundle. When
     * the {@code Activity} or {@code Fragment} is recreated, then the value will be redelivered.
     *
     * The value <b>must</b> implement {@link android.os.Parcelable}. If not, you should use {@link
     * me.tatarka.rxloader.RxLoader#save(SaveCallback)} to save and restore the value yourself.
     *
     * @return the {@code RxLoader2} for chaining
     */
    public RxLoader2<A, B, T> save() {
        super.save();
        return this;
    }

    /**
     * Saves the last value that the {@link rx.Observable} returns in {@link
     * rx.Observer#onNext(Object)} in the Activities'ss ore Fragment's instanceState bundle. When
     * the {@code Activity} or {@code Fragment} is recreated, then the value will be redelivered.
     *
     * The value <b>must</b> implement {@link android.os.Parcelable}. If not, you should use {@link
     * me.tatarka.rxloader.RxLoader#save(SaveCallback)} to save and restore the value yourself.
     *
     * @return the {@code RxLoader2} for chaining
     */
    public RxLoader2<A, B, T> save(SaveCallback<T> saveCallback) {
        super.save(saveCallback);
        return this;
    }
}
