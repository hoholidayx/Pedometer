package com.hzp.pedometer.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzp.pedometer.R;

/**
 * @author 何志鹏 on 2016/1/19.
 * @email hoholiday@hotmail.com
 */
public class RateDashboard extends FrameLayout {
    public static final int MAX_ANGLE = 111;
    public static final int MIN_ANGLE = -MAX_ANGLE;
    public static final int DEFAULT_SAMPLING_INTERVAL = 1000;//ms

    private ImageView pointer;
    private TextView stepPerMin;

    private int startAngle;
    private int samplingInterval;//面板采集数据时间间隔


    public RateDashboard(Context context) {
        this(context, null);
    }

    public RateDashboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateDashboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.ui_rate_dashboard, this);
        pointer = (ImageView) findViewById(R.id.rate_dashboard_pointer);
        stepPerMin = (TextView) findViewById(R.id.rate_dashboard_step_per_min);

        Typeface typeface = ComponentsUtil.getDefaultTypeface(context);
        if (typeface != null) {
            stepPerMin.setTypeface(typeface);
            ((TextView) findViewById(R.id.rate_dashboard_step_per_min_info)).setTypeface(typeface);
        }

        reset();
    }

    public void setDashboardValue(double percentage) {
        int rotateToAngle;
        if (percentage >= 0.5) {
            rotateToAngle = (int) (((percentage - 0.5) / 0.5) * MAX_ANGLE);
        } else {
            rotateToAngle = (int) ((1 - percentage / 0.5) * MIN_ANGLE);
        }

        Animation rotateAnimation = new
                RotateAnimation(startAngle, rotateToAngle
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        startAngle = rotateToAngle;

        rotateAnimation.setDuration(samplingInterval);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);

        pointer.startAnimation(rotateAnimation);
    }

    public void setSamplingInterval(int samplingInterval) {
        this.samplingInterval = samplingInterval;
    }

    public int getSamplingInterval() {
        return samplingInterval;
    }

    public void reset() {
        startAngle = MIN_ANGLE;
        samplingInterval = DEFAULT_SAMPLING_INTERVAL;

        Animation rotateAnimation = new
                RotateAnimation(0, startAngle
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(samplingInterval);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);

        pointer.startAnimation(rotateAnimation);
    }

}
