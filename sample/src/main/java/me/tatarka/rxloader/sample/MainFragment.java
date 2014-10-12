package me.tatarka.rxloader.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoader1;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import me.tatarka.rxloader.SaveCallback;

/**
 * Created by evan on 9/20/14.
 */
public class MainFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loaderManager = RxLoaderManagerCompat.get(this);

        progressLaunch = (ProgressBar) view.findViewById(R.id.progress_launch);
        buttonLaunch = (Button) view.findViewById(R.id.button_launch);
        progressInit = (ProgressBar) view.findViewById(R.id.progress_init);
        buttonInit = (Button) view.findViewById(R.id.button_init);
        progressRestart = (ProgressBar) view.findViewById(R.id.progress_restart);
        buttonRestart = (Button) view.findViewById(R.id.button_restart);
        progressProgress = (ProgressBar) view.findViewById(R.id.progress_progress);
        buttonProgress = (Button) view.findViewById(R.id.button_progress);
        progressInput = (ProgressBar) view.findViewById(R.id.progress_input);
        buttonInput = (Button) view.findViewById(R.id.button_input);
        editInput = (EditText) view.findViewById(R.id.edit_input);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        progressLaunch = null;
        buttonLaunch = null;
        progressInit = null;
        buttonInit = null;
        progressRestart = null;
        buttonRestart = null;
        progressProgress = null;
        buttonProgress = null;
        progressInput = null;
        buttonInput = null;
        editInput = null;
    }
}
