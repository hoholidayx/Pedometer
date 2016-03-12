package com.hzp.pedometer.service.step;

import android.content.Context;
import android.content.Intent;

import com.hzp.pedometer.persistance.sp.StepConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class StepManager implements OnStepCountListener {

    public static final String ACTION_STEP_COUNT = "step_count";
    public static final String KEY_STEP_COUNT = "STEP_COUNT";
    public static final String KEY_STEP_PER_MIN = "STEP_PER_MIN";


    private static StepManager instance;
    private Context context;

    private int windowSize;

    public StepDetector stepDetector;//算法模块
    private ExecutorService executorService;

    private List<Double> accelerationList;

    private boolean broadcastEnable = true;//是否开启步数广播

    private int stepPerMin;//步数每分钟
    private int lastStep;//上一次记录的步数
    private int calcRate;//每分钟步数计算间隔 ms

    private long startTime;//管理器计步的开始时间 ms
    private long endTime;//计算结束的时间
    private long samplingInterval;//采样点的时间间隔 ms

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

        lastStep = 0;
        calcRate = (int) ((1000.0 / StepConfig.getInstance().getSamplingRate()) * windowSize);

        samplingInterval = (long) (1000.0 / StepConfig.getInstance().getSamplingRate());

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

        stepPerMin = 0;
        lastStep = 0;

        startTime = endTime = 0;

        accelerationList.clear();
        stepDetector.reset();

        windowSize = StepConfig.getInstance().getFilterWindowSize();
        calcRate = (int) ((1000.0 / StepConfig.getInstance().getSamplingRate()) * windowSize);

        samplingInterval = (long) (1000.0 / StepConfig.getInstance().getSamplingRate());
    }

    /**
     * 开始输入数据
     * 在inputPoint函数之前调用
     * 否则无法计算
     * @param startTime 开始时间 ms
     */
    public void start(long startTime){
        this.startTime = startTime;
        endTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    /**
     * 读入加速度和时间点数据
     *
     * @param a    加速度
     */
    public void inputPoint(double a) {
        accelerationList.add(a);
        if (accelerationList.size() >= windowSize) {
            processData();
        }
    }

    public void inputPoints(List<Double> aList) {
        for (int i = 0; i < aList.size(); i++) {
            inputPoint(aList.get(i));
        }
    }

    /**
     * 同步计算数据
     * @param filename 计步数据文件名
     */
    public void inputPointSync(String filename) {
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            context.getFilesDir().getPath() + File.separator + filename));
            if (reader.ready()) {
                String temp;
                reader.readLine();//跳过第一行的时间记录
                while ((temp = reader.readLine()) != null) {
                    accelerationList.add(Double.valueOf(temp));
                    if (accelerationList.size() >= windowSize) {
                        processDataSync();
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            // TODO: 2016/2/4 无法读入文件处理
            e.printStackTrace();
        }
    }

    /**
     * 窗口满时进行数据处理
     */
    private void processData() {
        calcStepPerMin();
        executorService.submit(new ProcessThread());
    }

    private void processDataSync(){
        calcStepPerMin();
        coreCalculateWork();
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
        return stepDetector.stepCount;
    }

    /**
     * 获得每分钟的步行速率
     *
     * @return 步数每分钟
     */
    public int getStepPerMin() {
        return stepPerMin;
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * 开关步数广播
     */
    public void setBroadcastEnable(boolean enable) {
        broadcastEnable = enable;
    }

    class ProcessThread implements Runnable {

        @Override
        public void run() {
            coreCalculateWork();
            //发送计步结果广播
            sendStepBroadcast();
        }
    }

    private void coreCalculateWork(){
        double[] data;
        //复制window size个数据来进行计算
        data = new double[windowSize];

        for (int i = 0; i < windowSize; i++) {
            data[i] = accelerationList.remove(0);
        }

        //预处理和小波变换
        double[] result =
                Wavelet.waveletFilter(
                        Wavelet.medianFilter(data, windowSize)
                        , windowSize);

        //计步计算
        for (int i = 0; i < windowSize; i++) {
            stepDetector.stepDetection(result[i], endTime);
            endTime += samplingInterval;
        }
    }

}
