package com.hzp.pedometer.persistance.sp;

import android.content.Context;

import com.hzp.pedometer.R;

/**
 * @author hoholiday on 2016/5/13.
 * @email hoholiday@hotmail.com
 */
public class ApplyDataManager extends BaseSp {
    private static ApplyDataManager instance;

    private String KEY_STEPS_SUM;
    private String KEY_MILES_SUM;
    private String KEY_CALORIE_SUM;

    private ApplyDataManager() {
    }

    public static ApplyDataManager getInstance() {
        if (instance == null) {
            synchronized (ApplyDataManager.class) {
                if (instance == null) {
                    instance = new ApplyDataManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        super.init(context);

        KEY_STEPS_SUM = context.getString(R.string.KEY_STEPS_SUM);
        KEY_MILES_SUM = context.getString(R.string.KEY_MILES_SUM);
        KEY_CALORIE_SUM = context.getString(R.string.KEY_CALORIE_SUM);

    }

    public int getStepsSum(){
        return getInt(KEY_STEPS_SUM, 0);
    }

    public double getMileSum(){
        return getDouble(KEY_MILES_SUM,0);
    }

    public double getCalorieSum(){
        return getDouble(KEY_CALORIE_SUM,0);
    }

    public void setStepSum(int steps){
        putInt(KEY_STEPS_SUM,steps);
    }

    public void setMilesSum(double miles){
        putDouble(KEY_MILES_SUM,miles);
    }

    public void setCalorieSum(double calorie){
        putDouble(KEY_CALORIE_SUM,calorie);
    }
}
