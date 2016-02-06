package com.hzp.pedometer.persistance.sp;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * @author 何志鹏 on 2016/2/6.
 * @email hoholiday@hotmail.com
 */
public class IntEditTextPreference extends EditTextPreference {
    public IntEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean persistString(String value) {
        return persistInt(Integer.valueOf(value));
    }
}
