package com.hzp.pedometer.service.step;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hzp.pedometer.persistance.sp.StepConfig;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wavelet.utils.Wavelet;

/**
 * @author 何志鹏 on 2016/1/18.
 * @email hoholiday@hotmail.com
 */
public class StepManager implements StepDetector.OnStepCountListener {

    public static final String ACTION_STEP_COUNT = "step_count";
    public static final String KEY_STEP_COUNT = "STEP_COUNT";
    public static final String KEY_STEP_PER_MIN = "STEP_PER_MIN";


    private static StepManager instance;
    private Context context;

    private int windowSize;

    public StepDetector stepDetector;//算法模块
    private ExecutorService executorService;

    private List<Double> accelerationList;
    private List<Long> timeList;

    private boolean broadcastEnable = true;//是否开启步数广播

    private double stepPerMin;//步数每分钟
    private int lastStep;
    private long lastTime;

    //载入native库
    static {
        System.loadLibrary("wavelet");
    }

    private StepManager() {
    }

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

    public void init(Context context) {
        this.context = context;

        windowSize = StepConfig.getInstance().getFilterWindowSize();

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
        stepPerMin = 0;
        lastStep = 0;
        lastTime = 0;

        accelerationList.clear();
        timeList.clear();
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
     * 窗口满时进行数据处理
     */
    private void processData() {
        calcStepPerMin();
        executorService.submit(new ProcessThread());
    }

    /**
     * 计算步数每分钟
     */
    private void calcStepPerMin(){
        stepPerMin = ((double)(getStepCount() - lastStep)/(timeList.get(0) - lastTime))*60*1000;
        lastStep = getStepCount();
        lastTime = timeList.get(0);
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
        //empty
    }

    /**
     * 获取当前计算的总步数
     */
    public int getStepCount() {
        return stepDetector.stepCount;
    }

    /**
     * 获得每分钟的步行速率
     * @return 步数每分钟
     */
    public double getStepPerMin() {
        return stepPerMin;
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
            //发送计步结果广播
            sendStepBroadcast();
        }
    }


}
