package com.hzp.pedometer.activity;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.RateDashboard;

/**
 * 计步器工作页面
 */
public class StepCountActivity extends BindingActivity {

    private RateDashboard rateDashboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);


        initViews();
    }

    private void initViews(){
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
        rateDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDashboard.setValue();
            }
        });
    }
}
