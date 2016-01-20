package com.hzp.pedometer.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hzp.pedometer.R;

/**
 * @author 何志鹏 on 2016/1/19.
 * @email hoholiday@hotmail.com
 */
public class RateDashboard extends FrameLayout{
    private ImageView dashArrow;

    public RateDashboard(Context context) {
        this(context, null);
    }

    public RateDashboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateDashboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.ui_rate_dashboard,this);
    }

    public void setValue(){

    }

}
