package com.hzp.pedometer.persistance.sp;

import android.content.Context;

import com.hzp.pedometer.R;

/**
 * @author 何志鹏 on 2016/1/17.
 * @email hoholiday@hotmail.com
 *
 * 计步参数配置文件
 */
public class StepConfigManager extends BaseSp{


    private  String KEY_ALPHA;
    private  String KEY_BETA;
    private  String KEY_GAMMA;
    private  String KEY_K_NUMBER;
    private  String KEY_M_NUMBER ;
    private  String KEY_WAVELET_WINDOW_SIZE;
    private  String KEY_MEDIAN_WINDOW_SIZE;
    private  String KEY_SAMPLING_RATE;

    public static final double DEFAULT_ALPHA = 0.3;
    public static final double DEFAULT_BETA = 0.4;
    public static final double DEFAULT_GAMMA = 0.7;
    public static final int DEFAULT_K_NUMBER = 15;
    public static final int DEFAULT_M_NUMBER = 3;
    public static final int DEFAULT_STEP_INTERVAL_MAX = 2000;//ms
    public static final int DEFAULT_STEP_INTERVAL_MIN = 0;//ms
    public static final int DEFAULT_WAVELET_WINDOW_SIZE = 200;//小波变换窗口大小
    public static final int DEFAULT_MEDIAN_WINDOW_SIZE = 5;//中值滤波窗口大小
    public static final int DEFAULT_SAMPLING_RATE = 50;//默认采样率Hz
    //默认重力加速度
    public static final double DEFAULT_GRAVITY = 9.8;

    private static StepConfigManager instance;

    private StepConfigManager(){
    }

    public static StepConfigManager getInstance() {
        if(instance == null){
            synchronized(StepConfigManager.class){
                if(instance == null){
                    instance = new StepConfigManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context){
        super.init(context);

        KEY_ALPHA = context.getString(R.string.KEY_ALPHA);
        KEY_BETA = context.getString(R.string.KEY_BETA);
        KEY_GAMMA = context.getString(R.string.KEY_GAMMA);
        KEY_K_NUMBER = context.getString(R.string.KEY_K_NUMBER);
        KEY_M_NUMBER = context.getString(R.string.KEY_M_NUMBER);
        KEY_WAVELET_WINDOW_SIZE = context.getString(R.string.KEY_WAVELET_WINDOW_SIZE);
        KEY_MEDIAN_WINDOW_SIZE = context.getString(R.string.KEY_MEDIAN_WINDOW_SIZE);
        KEY_SAMPLING_RATE = context.getString(R.string.KEY_SAMPLING_RATE);
    }

    public double getAlpha(){
        return getDouble(KEY_ALPHA,DEFAULT_ALPHA);
    }

    public void setAlpha(double alpha){
        putDouble(KEY_ALPHA, alpha);
    }

    public double getBeta(){
        return getDouble(KEY_BETA,DEFAULT_BETA);
    }

    public void setBeta(double beta){
       putDouble(KEY_BETA,beta);
    }

    public double getGamma(){
        return getDouble(KEY_GAMMA,DEFAULT_GAMMA);
    }

    public void setGamma(double gamma){
        putDouble(KEY_GAMMA,gamma);
    }

    public int getKNumber(){
        return getInt(KEY_K_NUMBER, DEFAULT_K_NUMBER);
    }

    public void setKNumber(int k){
        putInt(KEY_K_NUMBER, k);
    }

    public int getMNumber(){
        return getInt(KEY_M_NUMBER,DEFAULT_M_NUMBER);
    }

    public void setMNumber(int m){
        putInt(KEY_M_NUMBER,m);
    }

    public int getWaveletWindowSize(){
        return getInt(KEY_WAVELET_WINDOW_SIZE, DEFAULT_WAVELET_WINDOW_SIZE);
    }

    public void setWaveletWindowSize(int size){
        putInt(KEY_WAVELET_WINDOW_SIZE,size);
    }

    public int getMedianWindowSize(){
        return getInt(KEY_MEDIAN_WINDOW_SIZE, DEFAULT_MEDIAN_WINDOW_SIZE);
    }

    public void setMedianWindowSize(int size){
        putInt(KEY_MEDIAN_WINDOW_SIZE,size);
    }

    public int getSamplingRate(){
        return getInt(KEY_SAMPLING_RATE,DEFAULT_SAMPLING_RATE);
    }

    public void setSamplingRate(int rate){
        putInt(KEY_SAMPLING_RATE,rate);
    }
}
