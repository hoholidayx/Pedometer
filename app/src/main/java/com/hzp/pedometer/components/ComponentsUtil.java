package com.hzp.pedometer.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

/**
 * @author 何志鹏 on 2016/1/22.
 * @email hoholiday@hotmail.com
 */
public class ComponentsUtil {


    public static Typeface getDefaultTypeface(Context context) {
        try{
            return Typeface.createFromAsset(context.getAssets(), "fonts/trends.ttf");
        }catch (RuntimeException e){
            Log.e("ComponentsUtil",e.getMessage());
            return null;
        }
    }
}
