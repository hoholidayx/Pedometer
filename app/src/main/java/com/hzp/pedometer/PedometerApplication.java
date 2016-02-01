package com.hzp.pedometer;

import android.app.Application;
import android.content.Intent;

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

        StepConfig.getInstance().init(this, StepConfig.FILE_NAME);
        StepManager.getInstance().init(this);
        //开启服务
        startService(new Intent(this, com.hzp.pedometer.service.CoreService.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
