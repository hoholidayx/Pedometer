package com.hzp.pedometer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import com.hzp.pedometer.R;
import com.hzp.pedometer.utils.AppConstants;
import com.hzp.pedometer.persistance.file.StepDataStorage;
import com.hzp.pedometer.persistance.sp.StepConfig;
import com.hzp.pedometer.service.step.StepManager;

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

    private ScreenReceiver screenReceiver;//监听屏幕关闭系统睡眠
    private PowerManager.WakeLock wakeLock;

    private Mode mode = Mode.NORMAL;//当前的计步模式
    private boolean Working = false;//运行标识

    private ScheduledExecutorService stepCalcScheduleService;
    private static final int TASK_INTERVAL = 5;//min

    public CoreService() {
        binder = new CoreBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wakeLock = ServiceUtil.getWakeLock(this);

        registerScreenReceiver();
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
        unregisterReceiver(screenReceiver);
        //解除cpu锁定省电
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

    }

    /**
     * 传感器初始化
     */
    private void initSensors() {
        //初始化重力传感器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void registerScreenReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        screenReceiver = new ScreenReceiver();
        registerReceiver(screenReceiver, filter);
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
                processNormalMode(a, System.currentTimeMillis());
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
        StepDataStorage.getInstance().saveData(a + " " + n + AppConstants.Separator);
    }

    private void startNormalMode() {
        StepDataStorage.getInstance().startNewRecord();
        //开启定时任务
        stepCalcScheduleService = Executors.newScheduledThreadPool(1);
        stepCalcScheduleService.scheduleAtFixedRate(new NormalStepCountTask()
                , TASK_INTERVAL
                , TASK_INTERVAL
                , TimeUnit.SECONDS);
    }

    private void stopNormalMode() {
        //关闭定时任务
        stepCalcScheduleService.shutdown();
        StepDataStorage.getInstance().endRecord();
    }

    class NormalStepCountTask implements Runnable {

        @Override
        public void run() {
            //获取所有未进行计算的计步数据文件
//            String[] filenames = stepDataStorage.getDataFileNames();
            //开启新的记录
            StepDataStorage.getInstance().startNewRecord();
//            //输入数据进行计步计算
//            StepManager.getInstance().inputPoints(filenames);
//            //删除旧的数据
//            stepDataStorage.deleteFile(filenames);
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
            toggleWorkingState(true);

            switch (mode) {
                case NORMAL: {
                    startNormalMode();
                    break;
                }
                case REAL_TIME: {
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
            toggleWorkingState(false);

            StepManager.getInstance().resetData();
            sensorManager.unregisterListener(this);

            switch (mode) {
                case NORMAL: {
                    stopNormalMode();
                    break;
                }
                case REAL_TIME: {
                    break;
                }
            }
        }
    }

    /**
     * 从数据文件计算步数
     *
     * @return 文件的总数
     */
    public int countStepFromFiles(final CountStepFromFilesListener listener) {
        if (isWorking()) {
            stopStepCount();
        }
        StepManager.getInstance().resetData();
        final String[] filenames = StepDataStorage.getInstance().getDataFileNames();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (filenames.length != 0) {
                        StepManager.getInstance().setBroadcastEnable(false);
                        for (String filename : filenames) {
                            StepManager.getInstance().inputPoint(filename);
                            if (listener != null) {
                                listener.onStepCount(StepManager.getInstance().getStepCount());
                            }
                        }
                        StepDataStorage.getInstance().deleteFile(filenames);
                        StepManager.getInstance().setBroadcastEnable(true);
                    }
                }
            }
        }.start();

        return filenames.length;
    }

    public interface CountStepFromFilesListener {
        void onStepCount(int stepCount);
    }

    public boolean isWorking() {
        return Working;
    }

    private void toggleWorkingState(boolean working){
        this.Working = working;
    }

    public StepManager getStepManager() {
        return StepManager.getInstance();
    }

    public Mode getMode() {
        return mode;
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    //解除唤醒
                    if (wakeLock.isHeld()) {
                        wakeLock.release();
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //唤醒cpu
                    if (isWorking()) {
                        wakeLock.acquire();
                    }
                    break;
            }
        }
    }

    public class CoreBinder extends Binder {
        public CoreService getService() {
            return CoreService.this;
        }
    }
}
