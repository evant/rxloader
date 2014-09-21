package me.tatarka.rxloader.sample.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.Semaphore;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderObserver;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public class RxLoaderFragment extends Fragment implements TestableRxLoaderActivity {
    private RxLoaderManager mLoaderManager;
    private boolean mStarted;
    private Semaphore mStartedSemaphore = new Semaphore(0);
    private Object mNext;
    private Semaphore mNextSemaphore = new Semaphore(0);
    private Throwable mError;
    private Semaphore mErrorSemaphore = new Semaphore(0);
    private boolean mCompleted;
    private Semaphore mCompletedSemaphore = new Semaphore(0);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoaderManager = RxLoaderManager.get(this);
    }

    public <T> RxLoader<T> createLoader(Observable<T> observable) {
        return mLoaderManager.create(observable, new RxLoaderObserver<T>() {
            @Override
            public void onStarted() {
                mStarted = true;
                mStartedSemaphore.release();
            }

            @Override
            public void onNext(T value) {
                mNext = value;
                mNextSemaphore.release();
            }

            @Override
            public void onError(Throwable e) {
                mError = e;
                mErrorSemaphore.release();
            }

            @Override
            public void onCompleted() {
                mCompleted = true;
                mCompletedSemaphore.release();
            }
        });
    }

    public void waitForNext() throws InterruptedException {
        mNextSemaphore.acquire();
    }

    public <T> T getNext() {
        return (T) mNext;
    }

    public void waitForError() throws InterruptedException {
        mErrorSemaphore.acquire();
    }

    public Throwable getError() {
        return mError;
    }

    public void waitForStarted() throws InterruptedException {
        mStartedSemaphore.acquire();
    }

    public boolean isStarted() {
        return mStarted;
    }

    public void waitForCompleted() throws InterruptedException {
        mCompletedSemaphore.acquire();
    }

    public boolean isCompleted() {
        return mCompleted;
    }
}
