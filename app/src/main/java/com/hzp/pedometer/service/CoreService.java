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

import com.hzp.pedometer.AppConstants;
import com.hzp.pedometer.persistance.file.StepDataStorage;
import com.hzp.pedometer.persistance.sp.StepConfig;
import com.hzp.pedometer.service.step.StepManager;

import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 核心工作服务
 */
public class CoreService extends Service implements SensorEventListener {

    private CoreBinder binder;

    private SensorManager sensorManager;
    private Sensor sensor;

    private Mode mode = Mode.NORMAL;//当前的计步模式
    private boolean Working = false;//运行标识

    private StepDataStorage stepDataStorage;
    private ScheduledExecutorService stepCalcScheduleService;

    public CoreService() {
        binder = new CoreBinder();
        stepDataStorage = new StepDataStorage(this);
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
        double a = Math.sqrt(
                Math.pow(event.values[0], 2) +
                        Math.pow(event.values[1], 2) +
                        Math.pow(event.values[2], 2));

        switch (mode) {
            case NORMAL: {
                processNormalMode(a,System.currentTimeMillis());
                break;
            }
            case REAL_TIME: {
                processRealTimeMode(a, System.currentTimeMillis());
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
     *
     * @param a 加速度
     * @param n 时间
     */
    private void processRealTimeMode(double a, long n) {
        StepManager.getInstance().inputPoint(a, n);
    }

    /**
     * 处理正常计步模式数据
     *
     * @param a 加速度
     * @param n 时间
     */
    private void processNormalMode(double a, long n) {
        if (stepDataStorage != null) {
            stepDataStorage.saveData(a + " " + n + AppConstants.Separator);
        }
    }

    private void startNormalMode(){
        StepManager.getInstance().resetData();
        //开启定时任务
        stepCalcScheduleService = Executors.newScheduledThreadPool(1);
        //设置间隔每30分钟计算一次数据
        stepCalcScheduleService.scheduleAtFixedRate(new NormalStepCountTask(), 30, 30, TimeUnit.MINUTES);
    }

    private void stopNormalMode(){
        //// TODO: 2016/2/1 关闭定时任务
    }

    class NormalStepCountTask implements Runnable{

        @Override
        public void run() {
           //获取计步数据文件
            String[] filenames = stepDataStorage.getDataFileNames();
            //todo 输入数据进行计步计算
            //开启新的记录
            try {
                stepDataStorage.startNewRecord();
            } catch (FileNotFoundException e) {
                // TODO: 2016/2/4 异常处理 无法创建新的文件
            }
            //删除旧的数据
            stepDataStorage.deleteFile(filenames);
        }
    }

    /**
     * 开始计步
     *
     * @param mode 计步模式
     */
    public void startStepCount(Mode mode) {
        if (!isWorking()) {
            this.mode = mode;

            sensorManager.registerListener(this, sensor,
                    (int) (1.0 / StepConfig.getInstance().getSamplingRate()) * 1000 * 1000);//微秒
            Working = true;

            switch (mode){
                case NORMAL:{
                    startNormalMode();
                    break;
                }
                case REAL_TIME:{
                    break;
                }
            }
        }

    }

    /**
     * 停止计步
     */
    public void stopStepCount() {
        if (Working) {
            Working = false;

            StepManager.getInstance().resetData();
            sensorManager.unregisterListener(this);

            switch (mode){
                case NORMAL:{
                    stopNormalMode();
                    break;
                }
                case REAL_TIME:{
                    break;
                }
            }
        }
    }

    public boolean isWorking() {
        return Working;
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
