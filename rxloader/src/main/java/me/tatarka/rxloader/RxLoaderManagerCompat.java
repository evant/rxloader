package me.tatarka.rxloader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import static me.tatarka.rxloader.RxLoaderManager.FRAGMENT_TAG;

/**
 * Get an instance of {@link me.tatarka.rxloader.RxLoaderManager} that works with the support
 * library.
 *
 * @author Evan Tatarka
 */
public final class RxLoaderManagerCompat {
    private RxLoaderManagerCompat() {

    }

    /**
     * Get an instance of {@code RxLoaderManager} that is tied to the lifecycle of the given {@link
     * android.support.v4.app.FragmentActivity}.
     *
     * @param activity the activity
     * @return the {@code RxLoaderManager}
     */
    public static RxLoaderManager get(FragmentActivity activity) {
        RxLoaderBackendFragmentCompat fragment = (RxLoaderBackendFragmentCompat) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new RxLoaderBackendFragmentCompat();
            activity.getSupportFragmentManager().beginTransaction().add(fragment, FRAGMENT_TAG).commit();
        }
        return new RxLoaderManager(fragment);
    }

    /**
     * Get an instance of {@code RxLoaderManager} that is tied to the lifecycle of the given {@link
     * android.support.v4.app.Fragment}.
     *
     * @param fragment the fragment
     * @return the {@code RxLoaderManager}
     */
    public static RxLoaderManager get(Fragment fragment) {
        RxLoaderBackendFragmentCompat manager = (RxLoaderBackendFragmentCompat) fragment.getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (manager == null) {
            manager = new RxLoaderBackendFragmentCompat();
            fragment.getChildFragmentManager().beginTransaction().add(fragment, FRAGMENT_TAG).commit();
        }
        return new RxLoaderManager(manager);
    }

}
