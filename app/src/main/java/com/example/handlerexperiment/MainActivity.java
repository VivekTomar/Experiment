package com.example.handlerexperiment;

import android.app.AlarmManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
    Button startHandler, statService, stopService, alarmManager;
    Handler handler;
    Runnable task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startHandler = (Button) findViewById(R.id.handler);
        startHandler.setOnClickListener(this);

        statService = (Button) findViewById(R.id.service);
        statService.setOnClickListener(this);

        stopService = (Button) findViewById(R.id.stop);
        stopService.setOnClickListener(this);

        alarmManager = (Button) findViewById(R.id.alarm);
        alarmManager.setOnClickListener(this);

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
            if(null != fos) {
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
        }
    }
}
