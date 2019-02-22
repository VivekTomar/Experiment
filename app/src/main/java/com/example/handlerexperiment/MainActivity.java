package com.example.handlerexperiment;

import android.Manifest;
import android.app.AlarmManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements ResultCallBack.Receiver, View.OnClickListener {

    FileOutputStream fos;
    ResultCallBack mResultCallBack;
    Button startHandler, statService, stopService, alarmManager, job_service, job_stop_service;
    Handler handler;
    Runnable task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


        startHandler = (Button) findViewById(R.id.handler);
        startHandler.setOnClickListener(this);

        statService = (Button) findViewById(R.id.service);
        statService.setOnClickListener(this);

        stopService = (Button) findViewById(R.id.stop);
        stopService.setOnClickListener(this);

        alarmManager = (Button) findViewById(R.id.alarm);
        alarmManager.setOnClickListener(this);

        job_service = (Button) findViewById(R.id.job_service);
        job_service.setOnClickListener(this);

        job_stop_service = (Button) findViewById(R.id.job_stop_service);
        job_stop_service.setOnClickListener(this);

        mResultCallBack = new ResultCallBack(new Handler());
        mResultCallBack.setReceiver(this);
    }

    private void envokeService() {
        TaskService.setContext(this);
        TaskService.enqueueWork();
    }

    private void envokeHandler() {
        String filename = "discover-logs.txt";
        final File file = new File(Environment.getExternalStorageDirectory(), filename);

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        handler = new Handler();
        task = new Runnable() {
            @Override
            public void run() {
                try {
                    fos.write(Util.getStringBytes(0));
                    fos.write(Util.getStringBytes(1));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("TAG", "TASK EXECUTED!!!");
                handler.postDelayed(task, 5000);
                Log.v("TAG", "ReScheduled the TASK!!!");
            }
        };
        handler.postDelayed(task, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        TaskService.resetContext();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.v("TAG", "onReceiveResult");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.handler:
                envokeHandler();
                break;
            case R.id.service:
                envokeService();
                break;
            case R.id.stop:
//                TaskService.stop();
                break;
            case R.id.alarm:
                AlarmReceiver.setAlarm(getApplication());
                break;
            case R.id.cancelAlarm:
                AlarmReceiver.cancelAlarm(getApplication());
                break;
            case R.id.job_service:
                TestJobService.schedule(getApplication(),2);
                break;
            case R.id.job_stop_service:
                TestJobService.cancelJob(getApplication());
                break;
        }
    }
}
