package com.example.handlerexperiment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class TaskService extends JobIntentService {

    private static final String TAG = TaskService.class.getSimpleName();
    private FileOutputStream fos;
    public static final String RECEIVER = "receiver";
    public static final int SHOW_RESULT = 123;

    //Todo need to find some work around should not be static
    private static Context mContext;

    private ResultReceiver mResultReceiver;
    static final int TIMER_JOB_ID = 1000;
    private static final String ACTION_TIMER = "action.TIMER";

    String filename = "discover-logs.txt";
    final File file = new File(Environment.getExternalStorageDirectory(), filename);

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork() {
        if(null != mContext){
            Intent intent = new Intent(mContext, TaskService.class);
            intent.setAction(ACTION_TIMER);
            enqueueWork(mContext, TaskService.class, TIMER_JOB_ID, intent);
        }
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void resetContext() {
        mContext = null;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork() called with: intent = [" + intent + "]");
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_TIMER:
                    mResultReceiver = intent.getParcelableExtra(RECEIVER);
                    doLogging();
                    Log.v("TAG", "TASK EXECUTED!!!");

//                    9*60*1000
                    try {
                        Thread.sleep(60*1000);
                        enqueueWork();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //Todo handle exception
                    }

                    Log.v("TAG", "ReScheduled the TASK!!!");
                    Bundle bundle = new Bundle();
                    bundle.putString("data", String.format("Task Execution DONE"));
                    if(null != mResultReceiver) {
                        mResultReceiver.send(SHOW_RESULT, bundle);
                    }
                    break;
            }
        }
    }


    private void doLogging() {
        if(null == fos){
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            fos.write(Util.getStringBytes(0));
            fos.write(Util.getStringBytes(1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

