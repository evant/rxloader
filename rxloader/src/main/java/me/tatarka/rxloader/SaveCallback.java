package me.tatarka.rxloader;

import android.os.Bundle;

/**
 * A set of callbacks to handle saving and restoring a value from an Activity's or Fragment's
 * instance state.
 *
 * @param <T> the value type
 */
public interface SaveCallback<T> {
    /**
     * Called when the value need to be saved.
     *
     * @param key      A unique key for the given value. It is suggested that you use this to prefix
     *                 keys you add to the {@code Bundle}.
     * @param value    the value
     * @param outState the {@code Bundle} to save the value in
     */
    void onSave(String key, T value, Bundle outState);

    /**
     * Called when the value needs to be restored.
     *
     * @param key        A unique key for the given value. It is suggested that you use this to
     *                   prefix keys you add to the {@code Bundle}.
     * @param savedState the {@code Bundle} to restore the value from
     * @return the value
     */
    T onRestore(String key, Bundle savedState);
}
