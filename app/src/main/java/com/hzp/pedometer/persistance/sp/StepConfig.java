package com.hzp.pedometer.persistance.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author 何志鹏 on 2016/1/17.
 * @email hoholiday@hotmail.com
 *
 * 计步参数配置文件
 */
public class StepConfig extends BaseSp{


    private static final String KEY_ALPHA = "ALPHA";
    private static final String KEY_BETA = "BETA";
    private static final String KEY_K_NUMBER = "K_NUMBER";
    private static final String KEY_M_NUMBER = "M_NUMBER";
    private static final String KEY_FILTER_WINDOW_SIZE = "FILTER_WINDOW_SIZE";
    private static final String KEY_SAMPLING_RATE = "SAMPLING_RATE";

    public static final double DEFAULT_ALPHA = 2.5;
    public static final double DEFAULT_BETA = -3.0;
    public static final int DEFAULT_K_NUMBER = 25;
    public static final int DEFAULT_M_NUMBER = 10;
    public static final int DEFAULT_STEP_INTERVAL_MAX = 2000;//ms
    public static final int DEFAULT_STEP_INTERVAL_MIN = 0;//ms
    public static final int DEFAULT_FILTER_WINDOW_SIZE = 200;//过滤模块默认窗口大小
    public static final double DEFAULT_SAMPLING_RATE = 100;//默认采样率Hz
    //默认重力加速度
    public static final double DEFAULT_GRAVITY = 9.8;

    private static StepConfig instance;

    private StepConfig(){
    }

    public static StepConfig getInstance() {
        if(instance == null){
            synchronized(StepConfig.class){
                if(instance == null){
                    instance = new StepConfig();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context){
        super.init(context);
    }

    public double getAlpha(){
        return getDouble(KEY_ALPHA,DEFAULT_ALPHA);
    }

    public void setAlpha(double alpha){
        putDouble(KEY_ALPHA,alpha);
    }

    public double getBeta(){
        return getDouble(KEY_BETA,DEFAULT_BETA);
    }

    public void setBeta(double beta){
       putDouble(KEY_BETA,beta);
    }

    public int getKNumber(){
        return getInt(KEY_K_NUMBER,DEFAULT_K_NUMBER);
    }

    public void setKNumber(int k){
        putInt(KEY_K_NUMBER,k);
    }

    public int getMNumber(){
        return getInt(KEY_M_NUMBER,DEFAULT_M_NUMBER);
    }

    public void setMNumber(int m){
        putInt(KEY_M_NUMBER,m);
    }

    public int getFilterWindowSize(){
        return getInt(KEY_FILTER_WINDOW_SIZE,DEFAULT_FILTER_WINDOW_SIZE);
    }

    public void setFilterWindowSize(int size){
        putInt(KEY_FILTER_WINDOW_SIZE,size);
    }

    public double getSamplingRate(){
        return getDouble(KEY_SAMPLING_RATE,DEFAULT_SAMPLING_RATE);
    }

    public void setSamplingRate(int rate){
        putInt(KEY_SAMPLING_RATE,rate);
    }
}
