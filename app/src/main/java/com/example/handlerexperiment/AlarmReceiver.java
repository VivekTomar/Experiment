package com.example.handlerexperiment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        doLogging();
        Log.v("TIMER", "EXECUTED");
        setAlarm(context);
    }

    public static void cancelAlarm(Context ctx) {
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(getPendingIntent(ctx));
    }

    public static void setAlarm(Context ctx) {
        cancelAlarm(ctx);

        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        long delay = (1000 * 60);
        long when = System.currentTimeMillis() + delay;

        PendingIntent pendingIntent = getPendingIntent(ctx);

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarm.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            alarm.setExact(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        }
        Log.v("TIMER", "SCHEDULED");
    }

    private static PendingIntent getPendingIntent(Context ctx) {
//        Context ctx;   /* get the application context */
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);
        return PendingIntent.getBroadcast(ctx, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    String filename = "discover-logs.txt";
    final File file = new File(Environment.getExternalStorageDirectory(), filename);
    private static FileOutputStream fos;
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
