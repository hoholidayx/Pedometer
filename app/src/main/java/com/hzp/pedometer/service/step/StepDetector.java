package com.hzp.pedometer.service.step;

import android.content.Context;

import com.hzp.pedometer.persistance.sp.StepConfig;

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
    private long n, np, nv;
    //波峰和波谷的时间阈值
    private long Thp, Thv;

    private double mjuA, sigmaA;
    private double mjuP, sigmaP;
    private double mjuV, sigmaV;

    //当前记录的步数
    private int count;

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
    private Context context;
    //加速度列表
    private List<Double> aList;
    //波峰波谷时间间隔列表
    private List<Long> peakList, valleyList;

    private OnStepCountListener listener;


    public StepDetector() {
        reset();
    }

    /**
     * 初始化参数
     */
    private void initConfig() {
        StepConfig config = StepConfig.getInstance(context);
        ALPHA = config.getAlpha();
        BETA = config.getBeta();
        K = config.getKNumber();
        M = config.getMNumber();

        S = WaveState.INIT;

        anm1 = an = 0;
        ap = av = StepConfig.DEFAULT_GRAVITY;
        ThAp = ThAv = 0;

        n = np = nv = 0;
        Thp = Thv = 0;

        mjuA = StepConfig.DEFAULT_GRAVITY;
        sigmaA = 0;
        mjuP = sigmaP = 0;
        mjuV = sigmaV = 0;

        count = 0;
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
            aList.add(StepConfig.DEFAULT_GRAVITY);
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
    private void detectCanditate(double anp1) {
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

        peakList.add(n - np);

        mjuP = (double) BaseMath.avg(peakList, Double.class);
        sigmaP = BaseMath.stdev(peakList, mjuP);


        Thp = (long) (mjuP + sigmaP / BETA);
        //固定阈值上下限
        Thp = Thp > StepConfig.DEFAULT_STEP_INTERVAL_MAX ?
                StepConfig.DEFAULT_STEP_INTERVAL_MAX : Thp;
        Thp = Thp < StepConfig.DEFAULT_STEP_INTERVAL_MIN ?
                StepConfig.DEFAULT_STEP_INTERVAL_MIN : Thp;
    }

    private void updateMjvVandSigmaV() {

        if (valleyList.size() >= M)
            valleyList.remove(0);

        valleyList.add(n - nv);

        mjuV = (double) BaseMath.avg(valleyList, Double.class);
        sigmaV = BaseMath.stdev(valleyList, mjuV);

        Thv = (long) (mjuV + sigmaV / BETA);
        //固定阈值上下限
        Thv = Thv > StepConfig.DEFAULT_STEP_INTERVAL_MAX ?
                StepConfig.DEFAULT_STEP_INTERVAL_MAX : Thv;
        Thv = Thv < StepConfig.DEFAULT_STEP_INTERVAL_MIN ?
                StepConfig.DEFAULT_STEP_INTERVAL_MIN : Thv;
    }

    private void updateSigmaA(double anp1) {
        if (aList.size() >= K)
            aList.remove(0);
        aList.add(anp1);
        sigmaA = BaseMath.stdev(aList);

        // TODO: 2016/1/17  测试固定上下限
        if (sigmaA < 0.3f) {
            sigmaA = 0.3 * ALPHA;
        } else if (sigmaA > 5.0) {
            sigmaA = 5.0 * ALPHA;
        }
    }

    public void stepDetection(double anp1, long np1) {

        anm1 = aList.get(aList.size() - 3);

        n = np1;
        an = aList.get(aList.size() - 2);

        detectCanditate(anp1);

        if (Sc == WaveState.PEAK) {

            if (S == WaveState.INIT) {
                S = WaveState.PEAK;
                updatePeak();

            } else if (S == WaveState.VALLEY && n - np >= Thp) {
                S = WaveState.PEAK;
                updatePeak();

            } else if (S == WaveState.PEAK && n - np <= Thp && an >= ap) {
                updatePeak();

            }
        } else if (Sc == WaveState.VALLEY) {

            if (S == WaveState.PEAK && n - nv >= Thv) {
                S = WaveState.VALLEY;
                updateValley();

                count++;

                if (listener != null) {
                    listener.onStepCounted(count);
                }

            } else if (S == WaveState.VALLEY && n - nv <= Thv && an <= av) {
                updateValley();

            }
        }
        updateSigmaA(anp1);
    }

    public void setStepCountListener(OnStepCountListener listener){
        this.listener = listener;
    }

    public void removeStepCountListener(){
        listener = null;
    }

    public interface OnStepCountListener{
        /**
         * 步数增加后回调
         * @param count 总步数
         */
        void onStepCounted(int count);
    }

}
