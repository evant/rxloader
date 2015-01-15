package me.tatarka.rxloader.sample.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import me.tatarka.rxloader.RxLoader;
import rx.Observable;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by evan on 9/20/14.
 */
public abstract class BaseRxLoaderActivityWithFragmentTest<T extends Activity & TestableRxLoaderActivityWithFragment> extends BaseRxLoaderActivityTest<T> {
    public BaseRxLoaderActivityWithFragmentTest(Class<T> activityClass) {
        super(activityClass);
    }
    
    @SmallTest
    public void testLoaderStartRemoveFragment() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().removeFragment();
            }
        });
        Thread.sleep(500); // Need to wait for onDestroy() to be called.
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getInstrumentation().waitForIdleSync();

        assertThat(getActivity().<String>getNext()).isNull();
        assertThat(getActivity().isCompleted()).isFalse().as("onCompleted() is not called if the activity is destroyed");
    }
    
    @SmallTest
    public void testLoaderStartDetachFragment() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().detachFragment();
            }
        });
        Thread.sleep(500); // Need to wait for onDestroy() to be called.
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getInstrumentation().waitForIdleSync();

        assertThat(getActivity().<String>getNext()).isNull();
        assertThat(getActivity().isCompleted()).isFalse().as("onCompleted() is not called if the fragment is detached");
    }
    
    @SmallTest
    public void testLoaderStartDetachAndAttachFragment() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().detachFragment();
            }
        });
        Thread.sleep(500); // Need to wait for onDestroy() to be called.
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().reattchFragment();
            }
        });
        getInstrumentation().waitForIdleSync();
        createLoader(getActivity(), subject);
        getActivity().waitForNext();
        getActivity().waitForCompleted();

        assertThat(getActivity().<String>getNext()).isEqualTo("test").as("result is value delivered from observable");
        assertThat(getActivity().isCompleted()).isTrue().as("onCompleted() called when fragment is reattached");
    }
    
    @SmallTest
    public void testMultipleLoaderFragments() throws InterruptedException {
        final String fragment1 = "fragment1";
        final String fragment2 = "fragment2";
        TestSubject<String> subject1 = TestSubject.create(testScheduler);
        TestSubject<String> subject2 = TestSubject.create(testScheduler);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().addFragment(fragment1);
                getActivity().addFragment(fragment2);
            }
        });
        createLoader(subject1, fragment1).start();
        createLoader(subject2, fragment2).start();
        getActivity().waitForStarted(fragment1); 
        getActivity().waitForStarted(fragment2);
        subject1.onNext("test1");
        subject2.onNext("test2");
        subject1.onCompleted();
        subject2.onCompleted();
        testScheduler.triggerActions();
        getActivity().waitForNext(fragment1);
        getActivity().waitForCompleted(fragment1);
        getActivity().waitForNext(fragment2);
        getActivity().waitForCompleted(fragment2);

        assertThat(getActivity().<String>getNext(fragment1)).isEqualTo("test1").as("result is value delivered from observable");
        assertThat(getActivity().isCompleted(fragment1)).isTrue().as("onCompleted() called when observable completed");

        assertThat(getActivity().<String>getNext(fragment2)).isEqualTo("test2").as("result is value delivered from observable");
        assertThat(getActivity().isCompleted(fragment2)).isTrue().as("onCompleted() called when observable completed");
    }

    protected <T> RxLoader<T> createLoader(final Observable<T> observable, final String tag) {
        final RxLoader<?>[] currentLoader = new RxLoader<?>[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                currentLoader[0] = getActivity().createLoader(observable, tag);
            }
        });
        return (RxLoader<T>) currentLoader[0];
    }
}
