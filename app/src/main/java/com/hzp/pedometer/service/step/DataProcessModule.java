package com.hzp.pedometer.service.step;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wavelet.utils.Wavelet;

/**
 * @author 何志鹏 on 2016/4/28.
 * @email hoholiday@hotmail.com
 */
public class DataProcessModule {

    private ExecutorService executorService;//工作线程池

    private List<Double> accelerationList;

    private int medianWindow;//中值滤波窗口大小
    private int waveletWindow;//小波变换窗口大小

    private OnDataProcessListener listener;


    /**
     * 竖直加速度修正相关声明
     */
    private final double alpha = 0.9;
    private double[] gravity = new double[]{9.8, 0, 0};
    private double[] linear_acceleration = new double[]{0, 0, 0};


    //载入native库
    static {
        System.loadLibrary("wavelet");
    }

    public DataProcessModule(int medianWindow, int waveletWindow) {
        this.medianWindow = medianWindow;
        this.waveletWindow = waveletWindow;

        executorService = Executors.newSingleThreadExecutor();
        accelerationList = Collections.synchronizedList(
                new LinkedList<Double>());
    }

    public void clear() {
        accelerationList.clear();
    }

    private double accelerationCorrection(double x, double y, double z) {
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = x - gravity[0];
        linear_acceleration[1] = y - gravity[1];
        linear_acceleration[2] = z - gravity[2];

        double gravitySum = Math.sqrt(
                gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]
        );

        return linear_acceleration[0] * gravity[0] / gravitySum
                + linear_acceleration[1] * gravity[1] / gravitySum
                + linear_acceleration[2] * gravity[2] / gravitySum;
    }


    public void setOnDataProcessListener(OnDataProcessListener listener) {
        this.listener = listener;
    }


    public void processData(double x, double y, double z) {

        //1.校正竖直方向加速度
        double a = accelerationCorrection(x, y, z);

        accelerationList.add(a);
        if (accelerationList.size() >= waveletWindow) {
            executorService.submit(new JNIProcess());
        }
    }

    public void processDataSync(double x, double y, double z) {

        //1.校正竖直方向加速度
        double a = accelerationCorrection(x, y, z);

        accelerationList.add(a);

        if (accelerationList.size() >= waveletWindow) {
            new JNIProcess().run();
        }
    }

    class JNIProcess implements Runnable {

        @Override
        public void run() {

            double[] result = mainProcess(getWindowData());

            if (listener != null) {
                listener.OnDataProcessed(result);
            }
        }
    }

    private double[] getWindowData() {
        double[] partOfData = new double[waveletWindow];
        for (int i = 0; i < waveletWindow; i++) {
            partOfData[i] = accelerationList.remove(0);
        }
        return partOfData;
    }

    private double[] mainProcess(double[] data) {
        //2.中值滤波处理
        double[] medianData = Wavelet.medianFilter(data, data.length);

        //3.小波分解重构
        double[] waveletData = Wavelet.waveletFilter(medianData, medianData.length);

        return waveletData;
    }

    interface OnDataProcessListener {
        void OnDataProcessed(double[] data);
    }

}
