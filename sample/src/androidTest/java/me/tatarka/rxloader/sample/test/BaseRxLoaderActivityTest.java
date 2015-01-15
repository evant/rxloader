package me.tatarka.rxloader.sample.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.robotium.solo.Solo;

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
    Solo solo;
    
    public BaseRxLoaderActivityTest(Class<T> activityClass) {
        super(activityClass);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testScheduler = new TestScheduler();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @SmallTest
    public void testLoaderStart() throws InterruptedException {
        final TestSubject<String> subject = TestSubject.create(testScheduler);
        final RxLoader<String> loader = createLoader(getActivity(), subject);

        assertThat(getActivity().isStarted()).isFalse().as("onStarted() not called until loader is started");

        loader.start();
        getActivity().waitForStarted();

        assertThat(getActivity().isStarted()).isTrue().as("onStarted() called when loader is started");
    }

    @SmallTest
    public void testLoaderStartNext() throws InterruptedException {
        final TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
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
        createLoader(getActivity(), subject).start();
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
        createLoader(getActivity(), subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().recreate();
            }
        });
        createLoader(getActivity(), subject);
        getActivity().waitForStarted();

        assertThat(getActivity().isStarted()).isTrue().as("onStarted() called again after a configuration change");
    }

    @SmallTest
    public void testLoaderStartRotationNext() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
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
        createLoader(getActivity(), subject);
        getActivity().waitForNext();
        getActivity().waitForCompleted();

        assertThat(getActivity().<String>getNext()).isEqualTo("test").as("result is delivered again after a configuration change");
        assertThat(getActivity().isCompleted()).isTrue().as("onCompleted() is called again after a configuration change");
    }

    @SmallTest
    public void testLoaderStartRotationError() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
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
        createLoader(getActivity(), subject);
        getActivity().waitForError();

        assertThat(getActivity().<String>getError()).hasMessage("test").as("onError() is called again after a configuration change");
    }

    @SmallTest
    public void testLoaderStartNextAfterDestroyed() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        createLoader(getActivity(), subject).start();
        getActivity().waitForStarted();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
        Thread.sleep(500); // Need to wait for onDestroy() to be called.
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getInstrumentation().waitForIdleSync();

        assertThat(getActivity().<String>getNext()).isNull();
        assertThat(getActivity().isCompleted()).isFalse().as("onCompleted() is not called if the activity is destroyed");

        // Needed to recreate the activity since the test runner expects it to exist. 
        getActivity();
    }
    
    @SmallTest
    public void testLoaderStartNextRotationClear() throws InterruptedException {
        TestSubject<String> subject = TestSubject.create(testScheduler);
        RxLoader<String> loader = createLoader(getActivity(), subject).start();
        getActivity().waitForStarted();
        subject.onNext("test");
        subject.onCompleted();
        testScheduler.triggerActions();
        getActivity().waitForNext();
        getActivity().waitForCompleted();
        loader.clear();
        T newActivity = recreateActivity();
        createLoader(newActivity, subject);
        getInstrumentation().waitForIdleSync();
        Thread.sleep(500); // Give loader a chance to deliver the result.

        assertThat(newActivity.<String>getNext()).isNull();
        assertThat(newActivity.isCompleted()).isFalse().as("onCompleted() is not called if the result was cleared");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        solo.finishOpenedActivities();
    }

    protected <RT> RxLoader<RT> createLoader(final T activity, final Observable<RT> observable) {
        final RxLoader<?>[] currentLoader = new RxLoader<?>[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                currentLoader[0] = activity.createLoader(observable);
            }
        });
        return (RxLoader<RT>) currentLoader[0];
    }
    
    protected T recreateActivity() {
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(getActivity().getClass().getName(), null, false);
        getInstrumentation().addMonitor(activityMonitor);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().recreate();
            }
        });
        activityMonitor.waitForActivity();
        getInstrumentation().waitForIdleSync();
        return (T) activityMonitor.getLastActivity();
    }
}
