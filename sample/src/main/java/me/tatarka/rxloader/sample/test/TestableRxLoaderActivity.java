package me.tatarka.rxloader.sample.test;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public interface TestableRxLoaderActivity {
    <T> RxLoader<T> createLoader(Observable<T> observable);

    void waitForNext() throws InterruptedException;

    <T> T getNext();

    void waitForError() throws InterruptedException;

    Throwable getError();

    void waitForStarted() throws InterruptedException;

    boolean isStarted();

    void waitForCompleted() throws InterruptedException;

    boolean isCompleted();
}
