rxloader
========
Asynchronous operations in Android are very hard to get right due to the
Activity lifecycle. AsyncTasks don't handle any of it making them difficult to
use. Loaders handle many things for you, but have a clunky api and fall down
anytime you want to do anything more complex than loading data immediately when
the Activity or Fragment is shown.

This library builds upon [rxjava](https://github.com/Netflix/RxJava) to handle
all these things for you with an easy-to-use api.

Install
-------
### Gradle
```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile 'me.tatarka.rxloader:rxloader:1.0'
}
```

### Maven
```xml
<dependency>
  <groupId>me.tatarka.rxloader</groupId>
  <artifactId>rxloader</artifactId>
  <version>1.0</version>
</dependency>
```

Usage
-----
The [sample app](https://github.com/evant/rxloader/blob/master/sample/src/main/java/me/tatarka/rxloader/sample/MainActivity.java)
shows some common use cases of handling observables.

Here is a simple example of loading data as soon as your activity starts.
```java
public class MyActivity extends Activity {
  private RxLoaderManager loaderManager;

  public void onCreate(Bundle savedState) {
    // If you are using the support library, 
    // use RxLoaderManagerCompat.get(this) instead.
    loaderManager = RxLoaderManager.get(this);

    loaderManager.create(
      asyncThatReturnsObservable(),
      new RxLoaderObserver<Result>() {
        @Override
        public void onStarted() {
          // Show your progress indicator.
        }

        @Override
        public void onNext(Result result) {
          // Hide your progress indicator and show the result.
        }

        @Override
        public void onError(Throwable error) {
          // Hide your progress indicator and show that there was an error.
        }
      }
    ).start(); // Make sure you call this to kick things off.
  }
}
```

All observer callbacks run on the UI thread. If the Activity or Fragment is
destroyed, the callbacks will not be called. If there is a configuration change,
the relevant callbacks will be called again.

Here is an example of loading and reloading on a button press. Try doing this
with loaders!

```java
public class MyActivity extends Activity {
  private RxLoaderManager loaderManager;

  public void onCreate(Bundle savedState) {
    loaderManager = RxLoaderManager.get(this);

    final RxLoader<Result> myLoader = loaderManager.create(
      asyncThatReturnsObservable(),
      new RxLoaderObserver<Result>() {
        @Override
        public void onStarted() {
          // Show your progress indicator.
        }

        @Override
        public void onNext(Result result) {
          // Hide your progress indicator and show the result.
        }

        @Override
        public void onError(Throwable error) {
          // Hide your progress indicator and show that there was an error.
        }
      }
    );

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View button) {
        myLoader.restart();
      }
    });
  }
}
```

Note that the loader is still created in `onCreate()` and not on the button
callback. This is necessary to handle configuration changes properly if the
button was pressed first.

### Tags
It is possible that you have multiple loaders for a given `RxLoaderManager`. In
that case you must pass each one a unique tag (`loaderManager.create(MY_TAG, 
observable, callback)`).
This is a string that identifies which callback is attached to which observable
so it can be reattached on a configuration change.

You may notice the above examples do not have a tag. If none is provided, then
then `RxLoaderManager#DEFAULT` is used.

### Saving state
The Android OS might destroy and recreate your Activity sometimes. If you don't
want to re-request data for your UI, you can save the result in the Activity's
instance state.

This can be as easy as
```java
loaderManager.create(observable, callback).save().start();
```

This assumes that the observable's value implements `Parceable`. If it doesn't,
you can handle saving and restoring it yourself by passing in a `SaveCallback`.
```java
loaderManager.create(observable, callback)
  .save(new SaveCallback<Result>() {
    @Override
    public void onSave(String tag, Result value, Bundle outBundle) {
      // Save the value in the bundle.
    }

    @Override
    public Result onRestore(String tag, Bundle savedState) {
      // Return the value from the bundle.
    }
  }).start();
```
