package me.tatarka.rxloader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * A way to manage asynchronous actions in Android using rxjava. It is much easier to get right than
 * an {@link android.os.AsyncTask} or a {@link android.content.Loader}. It properly handles Activity
 * destruction, configuration changes, and posting back to the UI thread.
 *
 * @author Evan Tatarka
 */
public class RxLoaderManager {
    /**
     * The default tag for an async operation. In many cases an Activity or Fragment only needs to
     * run one async operation so it's unnecessary to use tags to differentiate them. If you omit a
     * tag on a method that take one, this tag is used instead.
     */
    public static final String DEFAULT = RxLoaderManager.class.getCanonicalName() + "_default";

    static final String FRAGMENT_TAG = RxLoaderManager.class.getCanonicalName() + "_fragment";

    private final RxLoaderBackend manager;

    /**
     * Get an instance of the {@code RxLoaderManager} that is tied to the lifecycle of the given
     * {@link android.app.Activity}. If you are using the support library, then you should use
     * {@link me.tatarka.rxloader.RxLoaderManagerCompat#get(android.support.v4.app.FragmentActivity)}
     * instead.
     *
     * @param activity the activity
     * @return the {@code RxLoaderManager}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static RxLoaderManager get(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            throw new UnsupportedOperationException("Method only valid in api 11 and above, use RxLoaderManagerCompat to support older versions (requires support library)");
        }

        RxLoaderBackendFragment manager = (RxLoaderBackendFragment) activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (manager == null) {
            manager = new RxLoaderBackendFragment();
            activity.getFragmentManager().beginTransaction().add(manager, FRAGMENT_TAG).commit();
        }
        return new RxLoaderManager(manager);
    }

    /**
     * Get an instance of the {@code RxLoaderManager} that is tied to the lifecycle of the given
     * {@link android.app.Fragment}. If you are using the support library, then you should use
     * {@link me.tatarka.rxloader.RxLoaderManagerCompat#get(android.support.v4.app.Fragment)}}
     * instead.
     *
     * @param fragment the fragment
     * @return the {@code RxLoaderManager}
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static RxLoaderManager get(Fragment fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            throw new UnsupportedOperationException("Method only valid in api 17 and above, use RxLoaderManagerCompat to support older versions (requires support library)");
        }

        RxLoaderBackendNestedFragment manager = (RxLoaderBackendNestedFragment) fragment.getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        if (manager == null) {
            manager = new RxLoaderBackendNestedFragment();
            fragment.getChildFragmentManager().beginTransaction().add(manager, FRAGMENT_TAG).commit();
        }
        return new RxLoaderManager(manager);
    }

    RxLoaderManager(RxLoaderBackend manager) {
        this.manager = manager;
    }

    /**
     * Creates a new {@link me.tatarka.rxloader.RxLoader} that manages the given {@link
     * rx.Observable}. This should be called in {@link android.app.Activity#onCreate(android.os.Bundle)}
     * or similar.
     *
     * @param tag        The loader's tag. This must be unique across all loaders for a given
     *                   manager.
     * @param observable the observable to manage
     * @param observer   the observer that receives the observable's callbacks
     * @param <T>        the observable's value type
     * @return a new {@code RxLoader}
     * @see me.tatarka.rxloader.RxLoader
     */
    public <T> RxLoader<T> create(String tag, Observable<T> observable, RxLoaderObserver<T> observer) {
        return new RxLoader<T>(manager, tag, observable, observer);
    }

    /**
     * Creates a new {@link me.tatarka.rxloader.RxLoader} that manages the given {@link
     * rx.Observable}. It uses the {@link me.tatarka.rxloader.RxLoaderManager#DEFAULT} tag. This
     * should be called in {@link android.app.Activity#onCreate(android.os.Bundle)} or similar.
     *
     * @param observable the observable to manage
     * @param observer   the observer that receives the observable's callbacks
     * @param <T>        the observable's value type
     * @return a new {@code RxLoader}
     * @see me.tatarka.rxloader.RxLoader
     */
    public <T> RxLoader<T> create(Observable<T> observable, RxLoaderObserver<T> observer) {
        return new RxLoader<T>(manager, DEFAULT, observable, observer);
    }

    /**
     * Creates a new {@link me.tatarka.rxloader.RxLoader1} that manages the given {@link
     * rx.Observable}. This should be called in {@link android.app.Activity#onCreate(android.os.Bundle)}
     * or similar.
     *
     * @param tag            The loader's tag. This must be unique across all loaders for a given
     *                       manager.
     * @param observableFunc the function that returns the observable to manage
     * @param observer       the observer that receives the observable's callbacks
     * @param <A>            the argument's type.
     * @param <T>            the observable's value type
     * @return a new {@code RxLoader}
     * @see me.tatarka.rxloader.RxLoader1
     */
    public <A, T> RxLoader1<A, T> create(String tag, Func1<A, Observable<T>> observableFunc, RxLoaderObserver<T> observer) {
        return new RxLoader1<A, T>(manager, tag, observableFunc, observer);
    }

    /**
     * Creates a new {@link me.tatarka.rxloader.RxLoader} that manages the given {@link
     * rx.Observable}. It uses the {@link me.tatarka.rxloader.RxLoaderManager#DEFAULT} tag. This
     * should be called in {@link android.app.Activity#onCreate(android.os.Bundle)} or similar.
     *
     * @param observableFunc the function that returns the observable to manage
     * @param observer       the observer that receives the observable's callbacks
     * @param <T>            the observable's value type
     * @param <A>            the argument's type.
     * @return a new {@code RxLoader}
     * @see me.tatarka.rxloader.RxLoader
     */
    public <A, T> RxLoader1<A, T> create(Func1<A, Observable<T>> observableFunc, RxLoaderObserver<T> observer) {
        return new RxLoader1<A, T>(manager, DEFAULT, observableFunc, observer);
    }

    /**
     * Creates a new {@link me.tatarka.rxloader.RxLoader1} that manages the given {@link
     * rx.Observable}. This should be called in {@link android.app.Activity#onCreate(android.os.Bundle)}
     * or similar.
     *
     * @param tag            The loader's tag. This must be unique across all loaders for a given
     *                       manager.
     * @param observableFunc the function that returns the observable to manage
     * @param observer       the observer that receives the observable's callbacks
     * @param <T>            the observable's value type
     * @param <A>            the fist argument's type.
     * @param <B>            the second argument's type.
     * @return a new {@code RxLoader}
     * @see me.tatarka.rxloader.RxLoader1
     */
    public <A, B, T> RxLoader2<A, B, T> create(String tag, Func2<A, B, Observable<T>> observableFunc, RxLoaderObserver<T> observer) {
        return new RxLoader2<A, B, T>(manager, tag, observableFunc, observer);
    }

    /**
     * Creates a new {@link me.tatarka.rxloader.RxLoader} that manages the given {@link
     * rx.Observable}. It uses the {@link me.tatarka.rxloader.RxLoaderManager#DEFAULT} tag. This
     * should be called in {@link android.app.Activity#onCreate(android.os.Bundle)} or similar.
     *
     * @param observableFunc the function that returns the observable to manage
     * @param observer       the observer that receives the observable's callbacks
     * @param <T>            the observable's value type
     * @param <A>            the fist argument's type.
     * @param <B>            the second argument's type.
     * @return a new {@code RxLoader}
     * @see me.tatarka.rxloader.RxLoader
     */
    public <A, B, T> RxLoader2<A, B, T> create(Func2<A, B, Observable<T>> observableFunc, RxLoaderObserver<T> observer) {
        return new RxLoader2<A, B, T>(manager, DEFAULT, observableFunc, observer);
    }

    /**
     * Returns if the {@code RxLoaderManager} contains a {@link me.tatarka.rxloader.RxLoader} with
     * the given tag.
     *
     * @param tag the loader's tag
     * @return true if {@code RxLoaderManager} contains the tag, false otherwise
     */
    public boolean contains(String tag) {
        return manager.get(tag) != null;
    }

    /**
     * Unsubscribes all observers.
     *
     * @see BaseRxLoader#unsubscribe()
     */
    public void unsubscribeAll() {
        manager.unsubscribeAll();
    }

    /**
     * Clears all loaders.
     *
     * @see BaseRxLoader#clear()
     */
    public void clearAll() {
        manager.clearAll();
    }
}
