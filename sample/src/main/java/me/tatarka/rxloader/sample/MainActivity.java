package me.tatarka.rxloader.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.concurrent.TimeUnit;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoader1;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderObserver;
import me.tatarka.rxloader.SaveCallback;
import rx.Notification;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends Activity {
    private static final String DELAY_TASK_INIT = "sleep_task_init";
    private static final String DELAY_TASK_RESTART = "sleep_task_restart";
    private static final String PROGRESS_TASK = "progress_task";
    private static final String INPUT_TASK = "input_task";

    RxLoaderManager loaderManager;
    ProgressBar progressLaunch;
    Button buttonLaunch;
    ProgressBar progressInit;
    Button buttonInit;
    ProgressBar progressRestart;
    Button buttonRestart;
    ProgressBar progressProgress;
    Button buttonProgress;
    ProgressBar progressInput;
    Button buttonInput;
    EditText editInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = RxLoaderManager.get(this);

        setContentView(R.layout.activity_main);

        progressLaunch = (ProgressBar) findViewById(R.id.progress_launch);
        buttonLaunch = (Button) findViewById(R.id.button_launch);
        progressInit = (ProgressBar) findViewById(R.id.progress_init);
        buttonInit = (Button) findViewById(R.id.button_init);
        progressRestart = (ProgressBar) findViewById(R.id.progress_restart);
        buttonRestart = (Button) findViewById(R.id.button_restart);
        progressProgress = (ProgressBar) findViewById(R.id.progress_progress);
        buttonProgress = (Button) findViewById(R.id.button_progress);
        progressInput = (ProgressBar) findViewById(R.id.progress_input);
        buttonInput = (Button) findViewById(R.id.button_input);
        editInput = (EditText) findViewById(R.id.edit_input);

        // Start at launch
        loaderManager.create(
                SampleObservables.delay(),
                new RxLoaderObserver<String>() {
                    @Override
                    public void onStarted() {
                        progressLaunch.setVisibility(View.VISIBLE);
                        buttonLaunch.setEnabled(false);
                    }

                    @Override
                    public void onNext(String message) {
                        progressLaunch.setVisibility(View.INVISIBLE);
                        buttonLaunch.setEnabled(false);
                        buttonLaunch.setText(message + " (launch)");
                    }
                }
        ).start();

        // Init on button press
        final RxLoader<String> initLoader = loaderManager.create(
                DELAY_TASK_INIT,
                SampleObservables.delay(),
                new RxLoaderObserver<String>() {
                    @Override
                    public void onStarted() {
                        progressInit.setVisibility(View.VISIBLE);
                        buttonInit.setEnabled(false);
                    }

                    @Override
                    public void onNext(String message) {
                        progressInit.setVisibility(View.INVISIBLE);
                        buttonInit.setEnabled(false);
                        buttonInit.setText(message + " (init)");
                    }
                }
        ).save(new SaveCallback<String>() {
            @Override
            public void onSave(String key, String value, Bundle outState) {
                outState.putString(key, value);
            }

            @Override
            public String onRestore(String key, Bundle savedState) {
                return savedState.getString(key);
            }
        });

        buttonInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLoader.start();
            }
        });

        // Restart on button press
        final RxLoader<String> restartLoader = loaderManager.create(
                DELAY_TASK_RESTART,
                SampleObservables.delay(),
                new RxLoaderObserver<String>() {
                    @Override
                    public void onStarted() {
                        progressRestart.setVisibility(View.VISIBLE);
                        buttonRestart.setEnabled(false);
                        buttonRestart.setText("Restart on Button Press");
                    }

                    @Override
                    public void onNext(String message) {
                        progressRestart.setVisibility(View.INVISIBLE);
                        buttonRestart.setEnabled(true);
                        buttonRestart.setText(message + " (restart)");
                    }
                }
        );

        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartLoader.restart();
            }
        });

        // Progress on button press
        final RxLoader<Long> progressLoader = loaderManager.create(
                PROGRESS_TASK,
                SampleObservables.count(),
                new RxLoaderObserver<Long>() {
                    @Override
                    public void onStarted() {
                        buttonProgress.setEnabled(false);
                        progressProgress.setProgress(0);
                        progressProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Long progress) {
                        buttonProgress.setText("Progress Running (" + progress + ")");
                        progressProgress.setProgress(progress.intValue());
                    }

                    @Override
                    public void onCompleted() {
                        buttonProgress.setText("Progress Complete! (restart)");
                        buttonProgress.setEnabled(true);
                        progressProgress.setVisibility(View.INVISIBLE);
                    }
                }
        );

        buttonProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoader.restart();
            }
        });

        // Button with input
        final RxLoader1<String, String> inputLoader = loaderManager.create(
                INPUT_TASK,
                SampleObservables.inputDelay(),
                new RxLoaderObserver<String>() {
                    @Override
                    public void onStarted() {
                        progressInput.setVisibility(View.VISIBLE);
                        buttonInput.setEnabled(false);
                        buttonInput.setText("Restart input on Button Press");
                    }

                    @Override
                    public void onNext(String message) {
                        progressInput.setVisibility(View.INVISIBLE);
                        buttonInput.setEnabled(true);
                        buttonInput.setText(message + " (restart)");
                    }
                }
        );

        buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputLoader.restart(editInput.getText().toString());
            }
        });
    }
}
