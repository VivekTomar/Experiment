package com.example.handlerexperiment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static byte[] getStringBytes(int type){
        String log = "";
        switch(type){
            case 0:
                log = getCurrentTimeStamp() + " TASK EXECUTED!!!\n";
                break;
            case 1:
                log = getCurrentTimeStamp() + " Scheduled the TASK again!!!\n";
                break;
        }

        return log.getBytes();
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
