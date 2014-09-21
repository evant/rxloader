package me.tatarka.rxloader.sample.test;

import android.app.Activity;
import android.os.Bundle;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public class RxLoaderActivityWithFragment extends Activity implements TestableRxLoaderActivity {
    private RxLoaderFragment mFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
            mFragment = new RxLoaderFragment();
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, mFragment)
                    .commit();
            getFragmentManager().executePendingTransactions();
        } else {
            mFragment = (RxLoaderFragment) getFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    public RxLoaderFragment getFragment() {
        return mFragment;
    }

    @Override
    public <T> RxLoader<T> createLoader(Observable<T> observable) {
        return mFragment.createLoader(observable);
    }

    @Override
    public void waitForNext() throws InterruptedException {
        mFragment.waitForNext();
    }

    @Override
    public <T> T getNext() {
        return mFragment.getNext();
    }

    @Override
    public void waitForError() throws InterruptedException {
        mFragment.waitForError();
    }

    @Override
    public Throwable getError() {
        return mFragment.getError();
    }

    @Override
    public void waitForStarted() throws InterruptedException {
        mFragment.waitForStarted();
    }

    @Override
    public boolean isStarted() {
        return mFragment.isStarted();
    }

    @Override
    public void waitForCompleted() throws InterruptedException {
        mFragment.waitForCompleted();
    }

    @Override
    public boolean isCompleted() {
        return mFragment.isCompleted();
    }
}
