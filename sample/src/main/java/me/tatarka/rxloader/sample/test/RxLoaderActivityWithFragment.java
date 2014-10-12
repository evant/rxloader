package me.tatarka.rxloader.sample.test;

import android.app.Activity;
import android.os.Bundle;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;

/**
 * Created by evan on 9/20/14.
 */
public class RxLoaderActivityWithFragment extends Activity implements TestableRxLoaderActivityWithFragment {
    private RxLoaderFragment mFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, mFragment = new RxLoaderFragment())
                    .commit();
            getFragmentManager().executePendingTransactions();
        } else {
            mFragment = (RxLoaderFragment) getFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    @Override
    public void removeFragment() {
        getFragmentManager().beginTransaction()
                .remove(getFragment())
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void detachFragment() {
        getFragmentManager().beginTransaction()
                .detach(getFragment())
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void reattchFragment() {
        getFragmentManager().beginTransaction()
                .attach(getFragment())
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public <T> RxLoader<T> createLoader(Observable<T> observable, String tag) {
        return findFragmentByTag(tag).createLoader(observable);
    }

    public RxLoaderFragment findFragmentByTag(String tag) {
        return (RxLoaderFragment) getFragmentManager().findFragmentByTag(tag);
    }
    
    public RxLoaderFragment getFragment() {
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
        getFragmentManager().beginTransaction()
                .add(new RxLoaderFragment(), tag)
                .commit();
        getFragmentManager().executePendingTransactions();
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
