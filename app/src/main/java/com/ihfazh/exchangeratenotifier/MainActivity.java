package com.ihfazh.exchangeratenotifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStart, btnCancel, btnSetOnce;
    TextView textStatus;
    private int jobId = 10;
    private PeriodicWorkRequest periodicWorkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btn_start);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSetOnce = findViewById(R.id.btn_run_once);
        textStatus = findViewById(R.id.status_detail);

        btnCancel.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnSetOnce.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                startJob();
                break;
            case R.id.btn_cancel:
                cancelJob();
                break;
            case R.id.btn_run_once:
                startOneTimeTask();
                break;
        }

    }

    private void startOneTimeTask() {
        Data dataBuilder = new Data.Builder().build();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CurrencyWorker.class)
                .setInputData(dataBuilder)
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);

        WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(
                MainActivity.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        String status = workInfo.getState().name();
                        textStatus.append("\n" + status);
                    }
                }
        );
    }

    private void cancelJob() {
//        getPeriodicWorkRequest();
//        WorkManager.getInstance().cancelWorkById(periodicWorkRequest.getId());
        WorkManager.getInstance().cancelAllWorkByTag("GETCURRENCYNOTIFIER");
    }



    private void startJob() {
        getPeriodicWorkRequest();

        WorkManager.getInstance().enqueue(periodicWorkRequest);

        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                String status = workInfo.getState().name();
                textStatus.append("\n" + status);

                btnCancel.setEnabled(false);

                if (workInfo.getState() == WorkInfo.State.ENQUEUED){
                    btnCancel.setEnabled(true);
                }
            }
        });

    }

    private void getPeriodicWorkRequest() {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            periodicWorkRequest = new PeriodicWorkRequest.Builder(CurrencyWorker.class, 60, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .addTag("GETCURRENCYNOTIFIER")
                    .build();
    }

}