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

import com.hzp.pedometer.service.step.StepDetector;

/**
 * 核心工作服务
 */
public class CoreService extends Service implements SensorEventListener{

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
    private void initSensors(){
        //初始化重力传感器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //empty
    }


    /**
     * 开始计步
     * @param mode 计步模式
     */
    public void startStepCount(Mode mode){
        this.mode = mode;
        switch (mode){
            case NORMAL:{
                break;
            }
            case REAL_TIME:{
                break;
            }
        }
    }

    public boolean isWorking(){
        return WORKING;
    }

    public Mode getMode(){
        return mode;
    }


    public class CoreBinder extends Binder{
       public CoreService getService(){
            return CoreService.this;
        }
    }
}
