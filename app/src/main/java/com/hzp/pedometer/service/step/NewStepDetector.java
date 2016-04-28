package com.hzp.pedometer.service.step;

import android.util.Log;



import java.util.LinkedList;
import java.util.List;

import math.utils.BaseMath;

/**
 * @author 何志鹏 on 2016/3/12.
 * @email hoholiday@hotmail.com
 */
public class NewStepDetector {
    enum State {
        STAY,//静止态
        PRE_MOVE,//运动预备
        ENTER_PEAK,//进入波峰
        LEAVE_PEAK,//离开波峰
        ENTER_VALLEY,//进入波谷
        LEAVE_VALLEY,//离开波谷
        PRE_STEP,//计步预备
        STEP_FINISH,//计步完成
        UNDEFINED,//未确定
    }

    public State cState;//当前状态

    public double Th_preMove;//预备运动阈值
    public double Th_enterPeak;//波峰判定阈值
    public double Th_enterValley;//波谷判定阈值
    public double Th_stepFinish;//步伐结束阈值
    public long Th_stepTime;//步伐间隔时间阈值
    public long Th_stepSwitchTime;//计步锁定加锁的时间阈值
    public int Th_stepSwitchCount = 3;//计步锁定加锁的步数限制

    public double ALPHA = 0.3; //波峰自适应阈值调整参数 影响收敛速度
    public double BETA = 0.4;//波谷自适应阈值调整参数
    public double GAMMA = 0.7 ;//时间间隔自适应阈值调整参数

    public int windowSize = 15;//加速度分析窗口 影响  适应性《——》稳定性
    public int timeWindowSize = 3;//时间分析窗口

    private List<Double> peakList = new LinkedList<>();
    private List<Double> valleyList = new LinkedList<>();
    private List<Double> stepTimeList = new LinkedList<>();


    public double anm1,anm2;//最近检测到的两次加速度

    public double peakAvg;//波峰平均值
    public double valleyAvg;//波谷平均值

    public boolean stepSwitch;//计步锁定的次数锁定开关
    public int tempStepCount;//临时锁定计步数

    public long endTime;

    public int stepCount;

    public NewStepDetector() {
        initConfig();
    }

    private void initConfig() {
        Th_preMove = 0.5;
        Th_enterPeak = 1.0;
        Th_enterValley = -0.8;
        Th_stepFinish = -0.4;
        Th_stepTime =0;
        Th_stepSwitchTime = 5000;

        peakAvg = Th_enterPeak;
        valleyAvg = Th_enterValley;

        cState = State.STAY;

        stepSwitch = false;

        peakList.clear();
        valleyList.clear();
        stepTimeList.clear();

        anm1 = anm2 =0;

        stepCount = 0;
        tempStepCount =0;
    }

    private boolean detectiveEnterPeak(double a){
        if(anm1>=anm2){
            if(a>=anm1 && a>=Th_preMove){
                return true;
            }
        }
        return false;
    }

    private boolean detectiveEnterValley(double a){

        if(anm1<=anm2){
            if(a<=anm1 && a<=Th_stepFinish){
                return true;
            }
        }
        return false;
    }

    private void updateLastTowPoint(double a){
        anm2=anm1;
        anm1 = a;
    }


    public void preAdjustPeak(double a) {
        if (peakList.size() > windowSize) {
            peakList.remove(0);
        }
        peakList.add(a-Th_preMove);

    }

    public void preAdjustValley(double a) {
        if (valleyList.size() > windowSize) {
            valleyList.remove(0);
        }
        valleyList.add( a - Th_stepFinish);

    }

    public void preAdjustTime(long timeInterval) {
        if (stepTimeList.size() > timeWindowSize) {
            stepTimeList.remove(0);
        }

        if (timeInterval > 2000) {
            timeInterval = 2000;
        }

        stepTimeList.add((double) timeInterval);
    }

    public void adjustThEnterPeak() {
        peakAvg = BaseMath.avg(peakList);

        Th_enterPeak = Th_preMove + ALPHA * peakAvg;
    }

    public void adjustThEnterValley() {
        valleyAvg = BaseMath.avg(valleyList);

        Th_enterValley =Th_stepFinish + BETA * valleyAvg;
    }

    public void adjustThStepTime() {
        double avg = BaseMath.avg(stepTimeList);
        Th_stepTime = (long) ((GAMMA*avg) - (1-GAMMA)*BaseMath.stdev(stepTimeList,avg));
    }

    public void inputPoint(double a, long t) {

        Log.e("State:",cState.name());

        updateLastTowPoint(a);

        switch (cState) {
            case STAY:
                Stay(a);
                break;
            case PRE_MOVE:
                PreMove(a, t);
                break;
            case ENTER_PEAK:
                EnterPeak(a);
                break;
            case LEAVE_PEAK:
                leavePeak(a,t);
                break;
            case ENTER_VALLEY:
                enterValley(a);
                break;
            case LEAVE_VALLEY:
                leaveValley(a,t);
                break;
            case PRE_STEP:
                preStep(a);
                break;
            case STEP_FINISH:
                StepFinished(a, t);
                break;
            case UNDEFINED:
                Undefined(a);
                break;
        }
    }

    public void Stay(double a) {
        if (a < Th_preMove) {
            cState = State.STAY;
        } else if (a >= Th_preMove) {
            cState = State.PRE_MOVE;
        }
    }

    public void PreMove(double a, long t) {

        if (a < Th_enterPeak && a > Th_preMove) {
            cState = State.PRE_MOVE;
            if(detectiveEnterPeak(a)){
                preAdjustPeak(a);
                adjustThEnterPeak();
            }
        }
        else if (a <= Th_preMove) {
            cState = State.UNDEFINED;
        }
        else if( a>Th_enterPeak){
            preAdjustPeak(a);
            cState = State.ENTER_PEAK;
        }
        else if (detectiveEnterPeak(a)) {
            preAdjustPeak(a);
            cState = State.PRE_MOVE;
        }
    }

    public void Undefined(double a) {
        if (a >= Th_preMove) {
            cState = State.PRE_MOVE;
        }
        else if (a < Th_preMove && a> Th_stepFinish) {
            cState = State.STAY;
        }
        else if(detectiveEnterValley(a)){
            cState = State.UNDEFINED;
            preAdjustValley(a);
            adjustThEnterValley();
        }
    }

    public void EnterPeak(double a) {

        if (a>=Th_enterPeak) {
            cState = State.ENTER_PEAK;
            preAdjustPeak(a);
        } else if (a > Th_enterValley && a <= Th_enterPeak) {
            cState = State.LEAVE_PEAK;
        } else if (a<=Th_enterValley) {
            cState = State.ENTER_VALLEY;
            preAdjustValley(a);
        }
    }

    public void leavePeak(double a,long t) {

        long timeInterval =  t - endTime;

        if (a>Th_enterPeak && timeInterval<=Th_stepTime) {
            cState = State.ENTER_PEAK;
            preAdjustPeak(a);
        } else if (a<=Th_enterValley) {
            cState = State.ENTER_VALLEY;
            preAdjustValley(a);
        } else if (a > Th_enterValley && a < Th_enterPeak ) {
            cState = State.LEAVE_PEAK;
        }
    }

    public void enterValley(double a) {

        if (a<Th_enterValley) {
            cState = State.ENTER_VALLEY;
            preAdjustValley(a);
        } else if (a >= Th_enterValley) {
            cState = State.LEAVE_VALLEY;
        }
    }

    public void leaveValley(double a, long t) {

        long timeInterval;
        timeInterval = t - endTime;


        if (a<Th_enterValley && timeInterval<=Th_stepTime) {
            cState = State.ENTER_VALLEY;
            preAdjustValley(a);
        }
        else if (a >= Th_enterValley && a < Th_stepFinish ) {
            cState = State.LEAVE_VALLEY;
        }
        else if (a >= Th_stepFinish) {


            preAdjustTime(timeInterval);

            //判断是否超时锁定
            if(timeInterval > Th_stepSwitchTime){
                stepSwitch = false;
                tempStepCount = 0;
            }

            if(timeInterval>=Th_stepTime){

                endTime = t;

                if(stepSwitch){
                    cState = State.STEP_FINISH;
                } else {
                    cState = State.PRE_STEP;
                }
            }
            else if(timeInterval < Th_stepTime){
                cState = State.PRE_MOVE;
                adjustThStepTime();
            }
        }
    }

    public void preStep(double a){

        if(tempStepCount< Th_stepSwitchCount){
            tempStepCount++;
            stepSwitch = false;

            if (a > Th_preMove) {
                cState = State.PRE_MOVE;
            }
            else if (a <= Th_preMove) {
                cState = State.STAY;
            }
        }else{
            stepCount += tempStepCount;
            tempStepCount = 0;
            stepSwitch = true;

            cState = State.STEP_FINISH;
        }
    }

    public void StepFinished(double a, long t) {
        stepCount++;

        if (a > Th_preMove) {
            cState = State.PRE_MOVE;
        } else if (a <= Th_preMove) {
            cState = State.STAY;
        }

        adjustThEnterPeak();
        adjustThEnterValley();
        adjustThStepTime();
    }

}