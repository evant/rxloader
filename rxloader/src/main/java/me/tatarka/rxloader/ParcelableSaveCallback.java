package me.tatarka.rxloader;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * A default implementation of {@link me.tatarka.rxloader.SaveCallback} that saves and restores an
 * object that is {@link android.os.Parcelable}.
 *
 * @param <T> the value type
 */
public class ParcelableSaveCallback<T> implements SaveCallback<T> {
    @Override
    public void onSave(String key, T value, Bundle outState) {
        outState.putParcelable(key, (Parcelable) value);
    }

    @Override
    public T onRestore(String key, Bundle savedState) {
        return (T) savedState.getParcelable(key);
    }
}
