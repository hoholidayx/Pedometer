package com.hzp.pedometer.service.step;

import android.content.Context;

import com.hzp.pedometer.persistance.sp.StepConfig;

import java.util.Arrays;
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
public class StepManager implements StepDetector.OnStepCountListener{

    private static StepManager instance;
    private int windowSize;
    private int length = 0;

    private StepDetector stepDetector;//算法模块
    ExecutorService executorService;

    private List<Double> accelerationList;
    private List<Long> timeList;

    private StepManager(Context context) {
        windowSize = StepConfig.getInstance(context).getFilterWindowSize();
        executorService = Executors.newSingleThreadExecutor();
        stepDetector = new StepDetector();
        resetData();
    }

    public static StepManager getInstance(Context context) {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null) {
                    instance = new StepManager(context);
                }
            }
        }
        return instance;
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
        length++;
        if (length >= windowSize) {
            length = 0;
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

    private void processData() {
        executorService.submit(new ProcessThread());
    }

    @Override
    public void onStepCounted(int count) {
        //TODO
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
        }
    }

}
