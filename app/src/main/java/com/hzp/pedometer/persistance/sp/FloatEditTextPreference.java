package com.hzp.pedometer.persistance.sp;

import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * @author 何志鹏 on 2016/2/6.
 * @email hoholiday@hotmail.com
 */
public class FloatEditTextPreference extends EditTextPreference{

    public FloatEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean persistString(String value) {
        return persistFloat(Float.valueOf(value));
    }
}
