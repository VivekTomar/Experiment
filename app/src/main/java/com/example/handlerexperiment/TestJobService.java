package com.example.handlerexperiment;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class TestJobService extends JobService {
    private FileOutputStream fos;
    String filename = "discover-logs.txt";
    final File file = new File(Environment.getExternalStorageDirectory(), filename);



    private static final int JOB_ID = 1;
    private static final int ONE_MIN =  1000;
    private boolean jobCancelled = false;

    public static void schedule(Context context, int time) {
        ComponentName component = new ComponentName(context, TestJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("time",2);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                // schedule it to run any time between 1 - 5 minutes
                .setMinimumLatency(ONE_MIN)
                .setOverrideDeadline(15 * ONE_MIN)
                .setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(builder.build());
        Log.d("TestJobService", "schedule: called " + resultCode);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("TestJobService", "schedule: started ");
        } else {
            Log.d("TestJobService", "schedule: failed ");
        }


    }

    public static void cancelJob(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(JOB_ID);
        Log.d("TestJobService", "Job cancelled");
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("TestJobService", "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        PersistableBundle pb=params.getExtras();
        final int time = pb.getInt("time");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!jobCancelled) {
                    Log.d("TestJobService", "doBackgroundWork started " );
                    updateFile();
                    try {
                        Thread.sleep(time * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("TestJobService", "Job cancelled before completion");
        jobCancelled = true;
        return true;

    }

    public void updateFile(){
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