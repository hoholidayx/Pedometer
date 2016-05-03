package com.hzp.pedometer.service;

import android.content.Context;
import android.os.PowerManager;

/**
 * @author 何志鹏 on 2016/2/4.
 * @email hoholiday@hotmail.com
 */
public class ServiceUtil {

    public static PowerManager.WakeLock getWakeLock(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"CoreServiceWakeLock");
    }
}
