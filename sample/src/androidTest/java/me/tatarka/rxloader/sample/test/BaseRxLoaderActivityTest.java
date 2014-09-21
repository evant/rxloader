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
public abstract class BaseRxLoaderActivityTest<T extends Activity & TestableRxLoaderActivity> extends ActivityInstrumentationTestCase2<T> {
    TestScheduler testScheduler;
    
    public BaseRxLoaderActivityTest(Class<T> activityClass) {
        super(activityClass);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testScheduler = new TestScheduler();
        getActivity();
    }

    @SmallTest
    public void testLoaderStart() throws InterruptedException {
        final TestSubject<String> subject = TestSubject.create(testScheduler);
        final RxLoader<String> loader = createLoader(subject);

        assertThat(getActivity().isStarted()).isFalse().as("onStarted() not called until loader is started");

        loader.start();
        getActivity().waitForStarted();

        assertThat(getActivity().isStarted()).isTrue().as("onStarted() called when loader is started");
    }

    @SmallTest
    public void testLoaderStartNext() throws InterruptedException {
        final TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(subject).start();
        getActivity().waitForStarted();
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getActivity().waitForNext();
        getActivity().waitForCompleted();

        assertThat(getActivity().<String>getNext()).isEqualTo("test").as("result is value delivered from observable");
        assertThat(getActivity().isCompleted()).isTrue().as("onCompleted() called when observable completed");
    }

    @SmallTest
    public void testLoaderStartError() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(subject).start();
        getActivity().waitForStarted();
        subject.onError(new Exception("test"));
        subject.onCompleted();
        testScheduler.triggerActions();
        getActivity().waitForError();

        assertThat(getActivity().<String>getError()).hasMessage("test").as("onError() is called when sent by observable");
    }

    @SmallTest
    public void testLoaderStartRotation() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().recreate();
            }
        });
        createLoader(subject);
        getActivity().waitForStarted();

        assertThat(getActivity().isStarted()).isTrue().as("onStarted() called again after a configuration change");
    }

    @SmallTest
    public void testLoaderStartRotationNext() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(subject).start();
        getActivity().waitForStarted();
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getActivity().waitForNext();
        getActivity().waitForCompleted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().recreate();
            }
        });
        createLoader(subject);
        getActivity().waitForNext();
        getActivity().waitForCompleted();

        assertThat(getActivity().<String>getNext()).isEqualTo("test").as("result is delivered again after a configuration change");
        assertThat(getActivity().isCompleted()).isTrue().as("onCompleted() is called again after a configuration change");
    }

    @SmallTest
    public void testLoaderStartRotationError() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(subject).start();
        getActivity().waitForStarted();
        subject.onError(new Exception("test"));
        testScheduler.triggerActions();
        getActivity().waitForError();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().recreate();
            }
        });
        createLoader(subject);
        getActivity().waitForError();

        assertThat(getActivity().<String>getError()).hasMessage("test").as("onError() is called again after a configuration change");
    }

    @SmallTest
    public void testLoaderStartNextAfterDestroyed() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();

        assertThat(getActivity().<String>getNext()).isNull();
        assertThat(getActivity().isCompleted()).isFalse().as("onCompleted() is not called if the activity is destroyed");

        // Needed to recreate the activity since the test runner expects it to exist. 
        getActivity();
    }

    private <T> RxLoader<T> createLoader(final Observable<T> observable) {
        final RxLoader<?>[] currentLoader = new RxLoader<?>[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                currentLoader[0] = getActivity().createLoader(observable);
            }
        });
        return (RxLoader<T>) currentLoader[0];
    }
}
