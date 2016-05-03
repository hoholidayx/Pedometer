package com.hzp.pedometer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.hzp.pedometer.persistance.db.DailyDataManager;
import com.hzp.pedometer.persistance.file.FileUtils;
import com.hzp.pedometer.persistance.file.StepDataStorageManager;
import com.hzp.pedometer.persistance.sp.StepConfigManager;
import com.hzp.pedometer.service.step.StepCountModule;
import com.hzp.pedometer.utils.AppConstants;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 核心工作服务
 */
public class CoreService extends Service implements DataCollectionManager.OnDataCollectionListener {

    private CoreBinder binder;

    private ScreenReceiver screenReceiver;//监听屏幕关闭系统睡眠
    private PowerManager.WakeLock wakeLock;


    private StepCountModule normalStepCountModule, realTimeStepCountModule;
    private boolean normalModeSwitch = false, realTimeModeSwitch = false;

    private ScheduledExecutorService normalStepCountService;
    private static final int RECORD_TASK_INTERVAL = 5;//进行数据记录的时间间隔min
    private static final int CALCULATE_STEP_TASK_INTERVAL = 30 ;//进行数据计算的时间间隔min

    public CoreService() {
        binder = new CoreBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wakeLock = ServiceUtil.getWakeLock(this);

        initModules();
        registerScreenReceiver();
    }

    private void initModules() {
        DataCollectionManager.getInstance().init(getApplicationContext());
        DataCollectionManager.getInstance().setOnDataCollectionListener(this);
        StepDataStorageManager.getInstance().init(getApplicationContext());
        StepConfigManager.getInstance().init(getApplicationContext());
        DailyDataManager.getInstance().init(getApplicationContext());

        normalStepCountModule = new StepCountModule(getApplicationContext(),StepCountMode.NORMAL);
        realTimeStepCountModule = new StepCountModule(getApplicationContext(),StepCountMode.REAL_TIME);
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
        //关闭数据库连接
        DailyDataManager.getInstance().closeDatabase();
        //关闭数据采集
        DataCollectionManager.getInstance().stop();
    }

    private void registerScreenReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        screenReceiver = new ScreenReceiver();
        registerReceiver(screenReceiver, filter);
    }

    public void startStepCount(StepCountMode mode) {
        switch (mode) {
            case NORMAL:
                startNormalMode();
                break;
            case REAL_TIME:
                startRealTimeMode();
                break;
        }
    }

    public void stopStepCount(StepCountMode mode) {
        switch (mode) {
            case NORMAL:
                stopNormalMode();
                break;
            case REAL_TIME:
                stopRealTimeMode();
                break;
        }
    }

    private void startNormalMode() {
        if (normalModeSwitch) {
            return;
        }

        //判断数据采集模块是否已经开启
        if (!DataCollectionManager.getInstance().isWorking()) {
            DataCollectionManager.getInstance().start();
        }

        normalStepCountService = Executors.newScheduledThreadPool(2);
        //开启数据定时存储任务
        normalStepCountService.scheduleAtFixedRate(new recordTaskOfNormalStepCount()
                , 0
                , RECORD_TASK_INTERVAL
                , TimeUnit.MINUTES);
        //开启数据定时计算任务
        normalStepCountService.scheduleAtFixedRate(new calcTaskOfNormalStepCount()
                , 0
                , CALCULATE_STEP_TASK_INTERVAL
                , TimeUnit.MINUTES);

    }

    private void stopNormalMode() {
        normalModeSwitch = false;

        //判断是否还需要开启数据采集功能
        if (canStop()) {
            DataCollectionManager.getInstance().stop();
        }

        StepDataStorageManager.getInstance().endRecord();

    }

    private void startRealTimeMode() {
        if (realTimeModeSwitch) {
            return;
        }
        //判断数据采集模块是否已经开启
        if (!DataCollectionManager.getInstance().isWorking()) {
            DataCollectionManager.getInstance().start();
        }
        realTimeStepCountModule.start(Calendar.getInstance().getTimeInMillis());

        realTimeModeSwitch = true;
    }

    private void stopRealTimeMode() {
        realTimeModeSwitch = false;
        //判断是否还需要开启数据采集功能
        if (canStop()) {
            DataCollectionManager.getInstance().stop();
        }
    }

    public boolean isRealTimeModeSwitch() {
        return realTimeModeSwitch;
    }

    public boolean isNormalModeSwitch() {
        return normalModeSwitch;
    }

    @Override
    public void onDataReceived(double x, double y, double z) {
        if (realTimeModeSwitch) {
            realTimeStepCountModule.inputPoint(x, y, z);
        }
        if (normalModeSwitch) {
            String data = new StringBuffer()
                    .append(x)
                    .append(" ")
                    .append(y)
                    .append(" ")
                    .append(z)
                    .append(AppConstants.Separator).toString();
            StepDataStorageManager.getInstance().saveData(data);
        }
    }

    private boolean canStop() {
        return !isNormalModeSwitch() && !isRealTimeModeSwitch();
    }

    /**
     * 数据定时存储任务
     */
    class recordTaskOfNormalStepCount implements Runnable {
        @Override
        public void run() {
            //开启新的记录
            StepDataStorageManager.getInstance().startNewRecord();
            StepDataStorageManager.getInstance().saveData(
                    Calendar.getInstance().getTimeInMillis() + AppConstants.Separator
            );
            normalModeSwitch = true;
        }
    }

    /**
     * 数据定时计算任务
     */
    class calcTaskOfNormalStepCount implements Runnable {
        @Override
        public void run() {
            //获取未计算的数据文件名
            String[] filenames = StepDataStorageManager.getInstance().getDataFileNames();
            for (String name : filenames) {
                //获取记录的开始时间
                long startTime = StepDataStorageManager.getInstance().getDataStartTime(name);
                if(startTime==0){
                    continue;
                }
                normalStepCountModule.start(startTime);
                //输入计步数据
                int stepCount = normalStepCountModule.inputPoint(name);
                //存储数据到数据库
                 if(stepCount !=0){
                    DailyDataManager.getInstance().saveData(
                            Calendar.getInstance().getTimeInMillis(),
                            startTime,
                            normalStepCountModule.getEndTime()
                            ,stepCount
                            );
                }
            }
            //删除已经计算过的数据文件
            FileUtils.deleteFile(getApplicationContext(), filenames);
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    //解除唤醒
                    if (wakeLock.isHeld()) {
                        wakeLock.release();
                        Log.i("CoreService", "lock release");
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //唤醒cpu
                    if (DataCollectionManager.getInstance().isWorking()) {
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
