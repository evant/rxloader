package me.tatarka.rxloader.sample.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public class RxLoaderActivityWithSupportFragment extends FragmentActivity implements TestableRxLoaderActivityWithFragment {
    private RxLoaderSupportFragment mFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mFragment = new RxLoaderSupportFragment())
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        } else {
            mFragment = (RxLoaderSupportFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    @Override
    public void removeFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(getFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void detachFragment() {
        getSupportFragmentManager().beginTransaction()
                .detach(getFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void reattchFragment() {
        getSupportFragmentManager().beginTransaction()
                .attach(getFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public <T> RxLoader<T> createLoader(Observable<T> observable, String tag) {
        return findFragmentByTag(tag).createLoader(observable);
    }

    public RxLoaderSupportFragment findFragmentByTag(String tag) {
        return (RxLoaderSupportFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    public RxLoaderSupportFragment getFragment() {
        return mFragment;
    }

    @Override
    public void waitForNext(String tag) throws InterruptedException {
        findFragmentByTag(tag).waitForNext();
    }

    @Override
    public <T> T getNext(String tag) {
        return findFragmentByTag(tag).getNext();
    }

    @Override
    public void waitForError(String tag) throws InterruptedException {
        findFragmentByTag(tag).waitForError();
    }

    @Override
    public Throwable getError(String tag) {
        return findFragmentByTag(tag).getError();
    }

    @Override
    public void waitForStarted(String tag) throws InterruptedException {
        findFragmentByTag(tag).waitForStarted();
    }

    @Override
    public boolean isStarted(String tag) {
        return findFragmentByTag(tag).isStarted();
    }

    @Override
    public void waitForCompleted(String tag) throws InterruptedException {
        findFragmentByTag(tag).waitForCompleted();
    }

    @Override
    public boolean isCompleted(String tag) {
        return findFragmentByTag(tag).isCompleted();
    }

    @Override
    public void addFragment(String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(new RxLoaderSupportFragment(), tag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public <T> RxLoader<T> createLoader(Observable<T> observable) {
        return getFragment().createLoader(observable);
    }

    @Override
    public void waitForNext() throws InterruptedException {
        getFragment().waitForNext();
    }

    @Override
    public <T> T getNext() {
        return getFragment().getNext();
    }

    @Override
    public void waitForError() throws InterruptedException {
        getFragment().waitForError();
    }

    @Override
    public Throwable getError() {
        return getFragment().getError();
    }

    @Override
    public void waitForStarted() throws InterruptedException {
        getFragment().waitForStarted();
    }

    @Override
    public boolean isStarted() {
        return getFragment().isStarted();
    }

    @Override
    public void waitForCompleted() throws InterruptedException {
        getFragment().waitForCompleted();
    }

    @Override
    public boolean isCompleted() {
        return getFragment().isCompleted();
    }
}
