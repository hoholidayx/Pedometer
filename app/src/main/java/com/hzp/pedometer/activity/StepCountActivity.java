package com.hzp.pedometer.activity;


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.ComponentsUtil;
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

        RelativeLayout textInfoLayout = (RelativeLayout) findViewById(R.id.step_count_text_info);
        Typeface typeface = ComponentsUtil.getDefaultTypeface(this);
        if(typeface!=null){
            for(int i =0;i<textInfoLayout.getChildCount();i++){
                ((TextView)textInfoLayout.getChildAt(i)).setTypeface(typeface);
            }
        }
    }


}
