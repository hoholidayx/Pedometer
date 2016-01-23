package com.hzp.pedometer.service.step;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hzp.pedometer.persistance.sp.StepConfig;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wavelet.utils.Wavelet;

/**
 * @author 何志鹏 on 2016/1/18.
 * @email hoholiday@hotmail.com
 */
public class StepManager implements StepDetector.OnStepCountListener {

    public static final String ACTION_STEP_COUNT = "step_count";
    private static final String KEY_STEP_COUNT = "STEP_COUNT";

    private static StepManager instance;
    private Context context;

    private int windowSize;

    public StepDetector stepDetector;//算法模块
    private ExecutorService executorService;
    private double samplingRate; //数据采样率

    private List<Double> accelerationList;
    private List<Long> timeList;

    private boolean broadcastEnable = true;//是否开启步数广播

    private double stepPerMin;//步数每分钟
    private double timeSpendPerWindow;//填充满一个窗口需要的时间 (ms)
    private int stepCountLastProcess;//上次处理数据时记录的步数

    //载入native库
    static {
        System.loadLibrary("wavelet");
    }

    private StepManager() {}

    public static StepManager getInstance() {
        if (instance == null) {
            synchronized (StepManager.class) {
                if (instance == null) {
                    instance = new StepManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;

        samplingRate = StepConfig.getInstance().getSamplingRate();
        windowSize = StepConfig.getInstance().getFilterWindowSize();
        timeSpendPerWindow =  (1.0/samplingRate)*windowSize*1000;

        executorService = Executors.newSingleThreadExecutor();
        stepDetector = new StepDetector();
        stepDetector.setStepCountListener(this);

        resetData();
    }

    public void resetData() {
        if (accelerationList == null) {
            accelerationList = Collections.synchronizedList(
                    new LinkedList<Double>());
        }
        if (timeList == null) {
            timeList = Collections.synchronizedList(
                    new LinkedList<Long>());
        }
        accelerationList.clear();
        timeList.clear();
        stepCountLastProcess = 0;
        stepDetector.reset();
        //TODO 线程池关闭后不能再打开？
//        executorService.shutdown();
    }

    /**
     * 读入加速度和时间点数据
     *
     * @param a    加速度
     * @param time 时间戳
     */
    public void inputPoint(double a, long time) {
        accelerationList.add(a);
        timeList.add(time);
        if (accelerationList.size() >= windowSize) {
            processData();
        }
    }

    public void inputPoints(List<Double> aList, List<Long> timeList) {
        for (int i = 0; i < aList.size(); i++) {
            inputPoint(aList.get(i), timeList.get(i));
        }
    }

    /**
     * 获取当前计算的总步数
     */
    public int getStepCount() {
        return stepDetector.stepCount;
    }

    /**
     * 获取每分钟的步数
     *
     * @return 步数/分
     */
    public double getStepPerMin() {
        return stepPerMin;
    }

    /**
     * 窗口满时进行数据处理
     */
    private void processData() {
        calculateStepPerMin(getStepCount());
        executorService.submit(new ProcessThread());
    }

    /**
     * 计算步数每分钟
     *
     * @param count 步数每分钟
     */
    private void calculateStepPerMin(int count) {
        double increment = count - stepCountLastProcess;
        stepCountLastProcess = count;
        stepPerMin = ((increment / timeSpendPerWindow) * 60 * 1000);
    }

    /**
     * 发送包含步数数据的广播
     */
    private void sendStepBroadcast() {
        if (broadcastEnable) {
            Intent intent = new Intent();
            intent.setAction(ACTION_STEP_COUNT);
            intent.putExtra(KEY_STEP_COUNT, getStepCount());
            context.sendBroadcast(intent);
            Log.e("Bushu",String.valueOf(getStepCount()));
        }
    }

    @Override
    public void onStepCounted(int count) {
        //empty
    }

    /**
     * 开关步数广播
     */
    public void setBroadcastEnable(boolean enable) {
        broadcastEnable = enable;
    }

    class ProcessThread implements Runnable {
        private double[] data;
        private long[] time;

        @Override
        public void run() {
            //复制window size个数据来进行计算
            data = new double[windowSize];
            time = new long[windowSize];
            for (int i = 0; i < windowSize; i++) {
                data[i] = accelerationList.remove(0);
                time[i] = timeList.remove(0);
            }

            //预处理和小波变换
            double[] result =
                    Wavelet.waveletFilter(
                            Wavelet.medianFilter(data, windowSize)
                            , windowSize);

            //计步计算
            for (int i = 0; i < windowSize; i++) {
                stepDetector.stepDetection(result[i], time[i]);
            }

            sendStepBroadcast();
        }
    }

}
