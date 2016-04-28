package com.hzp.pedometer.service.step;

import com.hzp.pedometer.persistance.sp.StepConfigManager;

import java.util.LinkedList;
import java.util.List;

import math.utils.BaseMath;

/**
 * @author 何志鹏 on 2016/1/17.
 * @email hoholiday@hotmail.com
 * <p/>
 * 计步算法模块
 */
public class StepDetector {

    ///////////////////////////////////////////////////////////////////////////
    // configs
    ///////////////////////////////////////////////////////////////////////////
    //计算mjuA时的常量
    private double ALPHA;
    // 计算Thv和Thp时的常量
    private double BETA;
    //选取的最近加速度的样本数
    private int K;
    // 选取的最近峰谷数量
    private int M;
    // 上一个样本点的状态
    private WaveState S;
    //当前样本点的状态
    private WaveState Sc;
    //上上个和上个加速度
    private double anm1, an;
    //最近一次检测到的波峰和波谷的加速度
    private double ap, av;
    //波峰波谷的加速度阈值
    private double ThAp, ThAv;
    //当前的时间、上一次波峰的时间、上一次波谷的时间
    private double n, np, nv;
    //波峰和波谷的时间阈值
    private double Thp, Thv;

    private double mjuA, sigmaA;
    private double mjuP, mjuV;
    private double sigmaP, sigmaV;

    //当前记录的步数
    public int stepCount;

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
    //加速度列表
    private List<Double> aList;
    //波峰波谷时间间隔列表
    private List<Double> peakList, valleyList;

    private OnStepCountListener listener;


    public StepDetector() {
        reset();
    }

    /**
     * 初始化参数
     */
    private void initConfig() {
        StepConfigManager config = StepConfigManager.getInstance();
        ALPHA = config.getAlpha();
        BETA = config.getBeta();
        K = config.getKNumber();
        M = config.getMNumber();

        S = WaveState.INIT;

        anm1 = an = 0;
        ap = av = 0;
        ThAp = ThAv = 0;

        n = 0;
        np = nv = 0;

        Thp = Thv = 0;

        mjuA = 0;
        sigmaA = 0;
        mjuP = mjuV = 0;
        sigmaP = sigmaV = 0;

        stepCount = 0;
    }

    /**
     * 初始化列表
     */
    private void initLists() {
        if (aList == null) {
            aList = new LinkedList<>();
        }
        if (peakList == null) {
            peakList = new LinkedList<>();
        }
        if (valleyList == null) {
            valleyList = new LinkedList<>();
        }
        aList.clear();
        peakList.clear();
        valleyList.clear();

        //初始进行计算的三个加速度
        for (int i = 0; i < 3; i++) {
            aList.add(0.0);
        }
    }

    /**
     * 重置计步算法
     */
    public void reset() {
        initConfig();
        initLists();
    }

    /**
     * 波峰波谷判定
     *
     * @param anp1 当前读入的加速度
     */
    private void detectCandidate(double anp1) {
        Sc = WaveState.INTMD;

        ThAp = mjuA + sigmaA / ALPHA;
        ThAv = mjuA - sigmaA / ALPHA;
        if (an >= BaseMath.max3(anm1, anp1, ThAp)) {
            Sc = WaveState.PEAK;
        } else if (an <= BaseMath.min3(anm1, anp1, ThAv)) {
            Sc = WaveState.VALLEY;
        }
    }

    private void updatePeak() {
        updateMjuPandSigmaP();
        np = n;
        ap = an;
        mjuA = (ap + av) / 2;
    }

    private void updateValley() {
        updateMjvVandSigmaV();
        nv = n;
        av = an;
        mjuA = (ap + av) / 2;
    }

    private void updateMjuPandSigmaP() {

        if (peakList.size() >= M)
            peakList.remove(0);

        double interval = n - np;
        if (interval > StepConfigManager.DEFAULT_STEP_INTERVAL_MAX) {
            interval = StepConfigManager.DEFAULT_STEP_INTERVAL_MAX;
        }
        if (interval < StepConfigManager.DEFAULT_STEP_INTERVAL_MIN) {
            interval = StepConfigManager.DEFAULT_STEP_INTERVAL_MIN;
        }
        peakList.add(interval);

        mjuP = BaseMath.avg(peakList);
        sigmaP = BaseMath.stdev(peakList, mjuP);


        Thp = Math.abs(mjuP + sigmaP / BETA);
    }

    private void updateMjvVandSigmaV() {

        if (valleyList.size() >= M)
            valleyList.remove(0);

        double interval = n - nv;
        if (interval > StepConfigManager.DEFAULT_STEP_INTERVAL_MAX) {
            interval = StepConfigManager.DEFAULT_STEP_INTERVAL_MAX;
        }
        if (interval < StepConfigManager.DEFAULT_STEP_INTERVAL_MIN) {
            interval = StepConfigManager.DEFAULT_STEP_INTERVAL_MIN;
        }
        valleyList.add(interval);

        mjuV = BaseMath.avg(valleyList);
        sigmaV = BaseMath.stdev(valleyList, mjuV);

        Thv = Math.abs(mjuV + sigmaV / BETA);
    }

    private void updateSigmaA(double anp1) {
        if (aList.size() >= K)
            aList.remove(0);
        aList.add(anp1);
        sigmaA = BaseMath.stdev(aList);

        // TODO: 2016/2/19 固定上下限阈值测试
        if (sigmaA < 0.2) {
            sigmaA = 0.2 * ALPHA;
        } else if (sigmaA > 2) {
            sigmaA = 2 * ALPHA;
        }
    }

    public void stepDetection(double anp1, long np1) {

        anm1 = aList.get(aList.size() - 3);
        an = aList.get(aList.size() - 2);

        n = np1;

        detectCandidate(anp1);

        if (Sc == WaveState.PEAK) {

            if (S == WaveState.INIT) {
                S = WaveState.PEAK;
                updatePeak();

            } else if (S == WaveState.VALLEY && n - np > Thp) {
                S = WaveState.PEAK;
                updatePeak();

            } else if (S == WaveState.PEAK && n - np <= Thp && an > ap) {
                updatePeak();

            }
        } else if (Sc == WaveState.VALLEY) {

            if (S == WaveState.PEAK && n - nv > Thv) {
                S = WaveState.VALLEY;
                updateValley();

                stepCount++;

                if (listener != null) {
                    listener.onStepCounted(stepCount);
                }

            } else if (S == WaveState.VALLEY && n - nv <= Thv && an < av) {
                updateValley();

            }
        }
        updateSigmaA(anp1);

    }

    public void setStepCountListener(OnStepCountListener listener) {
        this.listener = listener;
    }

    public void removeStepCountListener() {
        listener = null;
    }

}
