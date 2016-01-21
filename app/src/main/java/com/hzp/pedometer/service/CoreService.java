package com.hzp.pedometer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hzp.pedometer.persistance.sp.StepConfig;
import com.hzp.pedometer.service.step.StepDetector;
import com.hzp.pedometer.service.step.StepManager;

import java.math.BigDecimal;

import math.utils.BaseMath;

/**
 * 核心工作服务
 */
public class CoreService extends Service implements SensorEventListener {

    private CoreBinder binder;

    private SensorManager sensorManager;
    private Sensor sensor;

    private Mode mode = Mode.NORMAL;//当前的计步模式
    private boolean WORKING = false;//算法运行标识

    public CoreService() {
        binder = new CoreBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initSensors();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 传感器初始化
     */
    private void initSensors() {
        //初始化重力传感器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //合加速度
//        double a = Math.sqrt(
//                Math.pow(event.values[0], 2) +
//                        Math.pow(event.values[1], 2) +
//                        Math.pow(event.values[2], 2));
        //TODO 修改
        double x= event.values[0];
        double y= event.values[1];
        double z= event.values[2];
        double a = Math.sqrt(x*x+y*y+z*z);

        switch (mode) {
            case NORMAL: {
                break;
            }
            case REAL_TIME: {
                processRealTimeMode(a,System.currentTimeMillis());
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //empty
    }

    /**
     * 处理实时计步模式数据
     * @param a 加速度
     * @param n 时间
     */
    private void processRealTimeMode(double a,long n){
        StepManager.getInstance(this).inputPoint(a,n);
    }

    /**
     * 开始计步
     *
     * @param mode 计步模式
     */
    public void startStepCount(Mode mode) {
        this.mode = mode;
        sensorManager.registerListener(this, sensor,
                (1/StepConfig.getInstance(this).getSamplingRate())*1000*1000 );//微秒
        WORKING = true;

//        switch (mode){
//            case NORMAL:{
//                break;
//            }
//            case REAL_TIME:{
//                break;
//            }
//        }
    }

    /**
     * 停止计步
     */
    public void stopStepCount() {
        if(WORKING){
            WORKING = false;
            StepManager.getInstance(this).resetData();
            sensorManager.unregisterListener(this);
        }
    }

    public boolean isWorking() {
        return WORKING;
    }

    public Mode getMode() {
        return mode;
    }


    public class CoreBinder extends Binder {
        public CoreService getService() {
            return CoreService.this;
        }
    }
}
