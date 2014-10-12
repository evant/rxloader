package me.tatarka.rxloader.sample.test;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public interface TestableRxLoaderActivityWithFragment extends TestableRxLoaderActivity {
    void removeFragment();

    void detachFragment();

    void reattchFragment();

    <T> RxLoader<T> createLoader(Observable<T> observable, String tag);

    void waitForNext(String tag) throws InterruptedException;

    <T> T getNext(String tag);

    void waitForError(String tag) throws InterruptedException;

    Throwable getError(String tag);

    void waitForStarted(String tag) throws InterruptedException;

    boolean isStarted(String tag);

    void waitForCompleted(String tag) throws InterruptedException;

    boolean isCompleted(String tag);

    void addFragment(String tag);
}
