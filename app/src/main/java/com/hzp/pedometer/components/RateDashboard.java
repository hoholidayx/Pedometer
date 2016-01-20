package com.hzp.pedometer.components;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.hzp.pedometer.R;

import java.util.Random;

/**
 * @author 何志鹏 on 2016/1/19.
 * @email hoholiday@hotmail.com
 */
public class RateDashboard extends FrameLayout{
    private ImageView pointer;
    private Animation rotateAnimation;


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

    }
int temp1 =-120,temp2 =0;
    public void setValue(){

        temp2 = new Random().nextInt(120);
        switch (new Random().nextInt(2)){
            case 0:
                break;
            case 1:
                temp2 = -temp2;
                break;
        }
        rotateAnimation = new
                RotateAnimation(temp1,temp2
                ,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        temp1 = temp2;
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(0);
        pointer.startAnimation(rotateAnimation);

    }

}
