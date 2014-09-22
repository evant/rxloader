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
        createLoader(subject).start();
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
}
