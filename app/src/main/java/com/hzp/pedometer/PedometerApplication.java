package com.hzp.pedometer;

import android.app.Application;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.hzp.pedometer.persistance.db.DailyDataManager;
import com.hzp.pedometer.persistance.file.StepDataStorage;
import com.hzp.pedometer.persistance.sp.StepConfig;
import com.hzp.pedometer.service.step.StepManager;

/**
 * @author 何志鹏 on 2016/1/16.
 * @email hoholiday@hotmail.com
 */
public class PedometerApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //开启服务
        startService(new Intent(getApplicationContext(), com.hzp.pedometer.service.CoreService.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
