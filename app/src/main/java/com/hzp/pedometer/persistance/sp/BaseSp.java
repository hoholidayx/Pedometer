package com.hzp.pedometer.persistance.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author 何志鹏 on 2016/1/17.
 * @email hoholiday@hotmail.com
 */
public class BaseSp {

    private SharedPreferences preferences;

    public BaseSp(){
    }

    public void init(Context context,String fileName){
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void init(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public double getDouble(String key,double defaultValue){
        return preferences.getFloat(key, (float) defaultValue);
    }

    public void putDouble(String key,double value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, (float) value);
        editor.apply();
    }

    public int getInt(String key,int defaultValue){
        return preferences.getInt(key, defaultValue);
    }

    public void putInt(String key,int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

}
