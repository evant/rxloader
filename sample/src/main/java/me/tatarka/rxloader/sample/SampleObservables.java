package me.tatarka.rxloader.sample;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by evan on 8/22/14.
 */
public class SampleObservables {
    private static final String TAG = "RxLoader Sample";
    
    public static Observable<String> delay() {
        return Observable.timer(2, TimeUnit.SECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                Log.d(TAG, "2 second delay!");
                return "Async Complete!";
            }
        });
    }
    
    public static Func1<String, Observable<String>> inputDelay() {
        return new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(final String input) {
                return Observable.timer(2, TimeUnit.SECONDS).map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        Log.d(TAG, "2 second delay! [" + input + "]");
                        return "Async Complete! [" + input + "]";
                    }
                });
            }
        };
    }

    public static Observable<Long> count() {
        return Observable.interval(100, TimeUnit.MILLISECONDS).doOnEach(new Action1<Notification<? super Long>>() {
            @Override
            public void call(Notification<? super Long> notification) {
                Log.d(TAG, "tick!");
            }
        }).take(100);
    }
}
