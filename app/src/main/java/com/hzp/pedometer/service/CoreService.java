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
import android.util.Log;

import com.hzp.pedometer.persistance.db.DailyDataManager;
import com.hzp.pedometer.utils.AppConstants;
import com.hzp.pedometer.persistance.file.StepDataStorage;
import com.hzp.pedometer.persistance.sp.StepConfig;
import com.hzp.pedometer.service.step.StepManager;
import com.hzp.pedometer.utils.FileUtils;

import java.math.BigDecimal;
import java.util.Calendar;
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

    private ScheduledExecutorService normalStepCountService;
    //进行数据记录的时间间隔
    private static final int RECORD_TASK_INTERVAL = 10;//min
    private static final int RECORD_TASK_WAIT_TIME = 2000;//ms
    //进行数据计算的时间间隔
    private static final int COUNT_STEP_TASK_INTERVAL = 30;//min
    private int recordTempCount = 0;

    public CoreService() {
        binder = new CoreBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wakeLock = ServiceUtil.getWakeLock(this);

        initManagers();
        registerScreenReceiver();
        initSensors();
    }

    private void initManagers() {
        StepDataStorage.getInstance().init(getApplicationContext());
        StepConfig.getInstance().init(getApplicationContext());
        StepManager.getInstance().init(getApplicationContext());
        DailyDataManager.getInstance().init(getApplicationContext());
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
        DailyDataManager.getInstance().closeDatabase();
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
        double a =
                ServiceUtil.
                        accelerationCorrection(event.values[0], event.values[1], event.values[2]);

        BigDecimal bd = new BigDecimal(a);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

        switch (mode) {
            case NORMAL: {
                processNormalMode(bd.doubleValue());
                break;
            }
            case REAL_TIME: {
                processRealTimeMode(bd.doubleValue());
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
     */
    private void processRealTimeMode(double a) {
        StepManager.getInstance().inputPoint(a);
    }

    /**
     * 处理正常计步模式数据
     *
     * @param a 加速度
     */
    private void processNormalMode(double a) {
        StepDataStorage.getInstance().saveData(a + AppConstants.Separator);
    }

    private void startNormalMode() {
        //开启定时任务
        normalStepCountService = Executors.newScheduledThreadPool(2);
        normalStepCountService.scheduleAtFixedRate(new RecordStepDataTask()
                , 0
                , RECORD_TASK_INTERVAL
                , TimeUnit.MINUTES);
    }

    private void stopNormalMode() {
        //关闭定时任务
        normalStepCountService.shutdown();
        StepDataStorage.getInstance().endRecord();
        recordTempCount = 0;
    }

    private void startRealTimeMode() {
        StepManager.getInstance().start(Calendar.getInstance().getTimeInMillis());
    }

    private void stopRealTimeMode() {

    }

    class RecordStepDataTask implements Runnable {
        @Override
        public void run() {
            if (recordTempCount * RECORD_TASK_INTERVAL >=
                    COUNT_STEP_TASK_INTERVAL) {
                countStepFromFiles();
                recordTempCount = 0;
            } else {
                //开启新的记录
                StepDataStorage.getInstance().startNewRecord();
                StepDataStorage.getInstance().clearBuffer();
                StepDataStorage.getInstance().saveData(
                        Calendar.getInstance().getTimeInMillis() + AppConstants.Separator
                );
                recordTempCount++;
            }
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
            toggleWorkingState(true);

            switch (mode) {
                case NORMAL: {
                    startNormalMode();
                    break;
                }
                case REAL_TIME: {
                    startRealTimeMode();
                    break;
                }
            }
            //等待模式初始化后才读入加速度数据
            sensorManager.registerListener(this, sensor,
                    (int) ((1.0 / StepConfig.getInstance().getSamplingRate()) * 1000 * 1000));//微秒
        }

    }

    /**
     * 停止计步
     */
    public void stopStepCount() {
        if (Working) {
            toggleWorkingState(false);

            sensorManager.unregisterListener(this);

            switch (mode) {
                case NORMAL: {
                    stopNormalMode();
                    break;
                }
                case REAL_TIME: {
                    stopRealTimeMode();
                    break;
                }
            }

            StepManager.getInstance().resetData();
        }
    }

    /**
     * 从数据文件计算步数
     *
     * @return 文件的总数
     */
    public int countStepFromFiles() {
        //保存现场
        final boolean flag;
        if (isWorking()) {
            stopStepCount();
            flag = getMode().equals(Mode.NORMAL);
        } else {
            flag = false;
        }
        StepManager.getInstance().resetData();
        final String[] filenames = StepDataStorage.getInstance().getDataFileNames();

        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (StepDataStorage.getInstance()) {
                        StepDataStorage.getInstance().wait(RECORD_TASK_WAIT_TIME);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    int stepCount;
                    long startTime, endTime;

                    if (filenames.length != 0) {

                        StepManager.getInstance().setBroadcastEnable(false);

                        for (String filename : filenames) {
                            //读取数据文件的起始记录时间
                            startTime = StepDataStorage.getInstance().getDataStartTime(filename);
                            if (startTime == 0) {
                                continue;
                            }
                            StepManager.getInstance().resetData();
                            StepManager.getInstance().start(startTime);

                            StepManager.getInstance().inputPointSync(filename);

                            stepCount = StepManager.getInstance().getStepCount();
                            endTime = StepManager.getInstance().getEndTime();

                            //如该时间段计步数不为0才进行数据库记录
                            if (stepCount != 0) {
                                DailyDataManager.getInstance().saveData(
                                        FileUtils.getFileLastModified(getApplicationContext(), filename),
                                        startTime,
                                        endTime,
                                        stepCount
                                );
                            }
                        }
                        StepDataStorage.getInstance().deleteFile(filenames);
                    }
                    //恢复工作现场
                    StepManager.getInstance().setBroadcastEnable(true);
                    if (flag) {
                        startStepCount(Mode.NORMAL);
                    }
                }
            }
        }.start();

        return filenames.length;
    }

    public boolean isWorking() {
        return Working;
    }

    private void toggleWorkingState(boolean working) {
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
                        Log.i("CoreService","lock release");
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //唤醒cpu
                    if (isWorking()) {
                        wakeLock.acquire();
                        Log.i("CoreService", "lock acquire");
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
