package me.tatarka.rxloader.sample.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public class RxLoaderActivityWithSupportFragment extends FragmentActivity implements TestableRxLoaderActivity {
    private RxLoaderSupportFragment mFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
            mFragment = new RxLoaderSupportFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mFragment)
                    .commit();
            getFragmentManager().executePendingTransactions();
        } else {
            mFragment = (RxLoaderSupportFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    public RxLoaderSupportFragment getFragment() {
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
