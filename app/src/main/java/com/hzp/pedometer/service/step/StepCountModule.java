package com.hzp.pedometer.service.step;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.hzp.pedometer.persistance.file.StepDataStorageManager;
import com.hzp.pedometer.persistance.sp.StepConfigManager;
import com.hzp.pedometer.service.StepCountMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 何志鹏 on 2016/1/18.
 * @email hoholiday@hotmail.com
 */
public class StepCountModule implements OnStepCountListener, DataProcessModule.OnDataProcessListener {

    public static final String ACTION_STEP_COUNT = "step_count";
    public static final String KEY_STEP_COUNT = "STEP_COUNT";
    public static final String KEY_STEP_PER_MIN = "STEP_PER_MIN";
    public StepAlgorithm stepAlgorithm;//算法模块
    private Context context;
    private int waveletWindowSize, medianWindowSize;
    private int samplingRate;
    private long samplingInterval; //采样间隔 微秒
    private DataProcessModule dataProcessModule;

    private boolean broadcastEnable = true;//是否开启步数广播

    private int stepPerMin;//步数每分钟
    private int lastStep;//上一次记录的步数
    private int calcRate;//每分钟步数计算间隔 ms

    private long startTime;//管理器计步的开始时间 ms
    private long endTime;//计算结束的时间

    private StepCountMode stepCountMode;

    private ExecutorService executorService;

    public StepCountModule(Context context, StepCountMode stepCountMode) {
        this.context = context;
        this.stepCountMode = stepCountMode;
        setMode(stepCountMode);

        executorService = Executors.newSingleThreadExecutor();

        stepAlgorithm = new StepAlgorithm();
        stepAlgorithm.setStepCountListener(this);

        reset();
    }

    private void reset() {

        stepPerMin = 0;
        lastStep = 0;

        startTime = endTime = 0;

        stepAlgorithm.initConfig();

        waveletWindowSize = StepConfigManager.getInstance().getWaveletWindowSize();
        medianWindowSize = StepConfigManager.getInstance().getMedianWindowSize();
        samplingRate = StepConfigManager.getInstance().getSamplingRate();
        calcRate = (int) ((1000.0 / samplingRate) * waveletWindowSize);
        samplingInterval = 1000 / samplingRate;

        dataProcessModule = new DataProcessModule(medianWindowSize, waveletWindowSize);
        dataProcessModule.setOnDataProcessListener(this);
    }

    /**
     * 开始计步
     * 在inputPoint函数之前调用
     * 否则无法计算
     *
     * @param startTime 开始时间 （毫秒）
     */
    public void start(long startTime) {

        reset();

        this.startTime = startTime;
        this.endTime = this.startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void inputPoint(double x, double y, double z) {
        //直接从内存读入数据进行处理
        dataProcessModule.processData(x, y, z);
    }

    public int inputPoint(String stepDataName) {
        //从文本文件读入处理数据
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            context.getFilesDir().getPath() + File.separator + stepDataName));
            if (reader.ready()) {
                //跳过第一行的时间记录
                reader.readLine();
                String data = reader.readLine();
                while (data != null) {
                    //处理数据
                    String[] splits = data.split(" ");
                    double[] temp = {Double.valueOf(splits[0])
                            , Double.valueOf(splits[1])
                            , Double.valueOf(splits[2])};

                    dataProcessModule.processDataSync(temp[0], temp[1], temp[2]);

                    data = reader.readLine();
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getStepCount();
    }


    /**
     * 计算步数每分钟
     */
    private void calcStepPerMin() {
        stepPerMin = (getStepCount() - lastStep) * 60 * 1000 / calcRate;
        lastStep = getStepCount();
    }

    /**
     * 发送包含步数数据的广播
     */
    private void sendStepBroadcast() {
        if (broadcastEnable) {
            Intent intent = new Intent();
            intent.setAction(ACTION_STEP_COUNT);
            intent.putExtra(KEY_STEP_COUNT, getStepCount());
            intent.putExtra(KEY_STEP_PER_MIN, getStepPerMin());
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void onStepCounted(int count) {
        /*empty*/
    }

    /**
     * 获取当前计算的总步数
     */
    public int getStepCount() {
        return stepAlgorithm.stepCount;
    }

    /**
     * 获得每分钟的步行速率
     *
     * @return 步数每分钟
     */
    public int getStepPerMin() {
        return stepPerMin;
    }

    /**
     * 开关步数广播
     */
    public void setBroadcastEnable(boolean enable) {
        broadcastEnable = enable;
    }

    public void setMode(StepCountMode mode) {
        switch (mode) {
            case NORMAL:
                setBroadcastEnable(false);
                break;
            case REAL_TIME:
                setBroadcastEnable(true);
                break;
        }
    }

    @Override
    public void OnDataProcessed(double[] data) {
        switch (stepCountMode) {
            case REAL_TIME:
                calcStepPerMin();
                executorService.submit(new StepCountTask(data, getEndTime()));
                break;
            case NORMAL:
                try {
                    Thread temp = new Thread(new StepCountTask(data, getEndTime()));
                    temp.start();
                    temp.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    class StepCountTask implements Runnable {
        double[] data;
        long startTime;

        public StepCountTask(double[] data, long startTime) {
            this.data = data;
            this.startTime = startTime;
        }

        @Override
        public void run() {

            int length = data.length;
            for (int i = 0; i < length; i++) {
                setEndTime(getEndTime() + samplingInterval);
                stepAlgorithm.inputPoint(data[i], getEndTime());
            }

            sendStepBroadcast();
        }
    }

}
