package com.hzp.pedometer.activity;


import android.os.Bundle;

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

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onServiceBind() {
    }

    private void initViews() {
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
    }


}
