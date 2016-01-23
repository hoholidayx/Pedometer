package com.hzp.pedometer.activity;


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.ComponentsUtil;
import com.hzp.pedometer.components.RateDashboard;
import com.hzp.pedometer.service.Mode;

/**
 * 计步器工作页面
 */
public class StepCountActivity extends BindingActivity {

    private RateDashboard rateDashboard;
    private ImageButton buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onServiceBind() {
    }

    private void initViews() {
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
        buttonStart = (ImageButton) findViewById(R.id.step_count_start_button);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceBinded()){
                    if(getService().isWorking()){
                        troggleStartButton(false);
                        stopStepCount();
                    }else{
                        troggleStartButton(true);
                        startStepCount();
                    }
                }
            }
        });

        //设置字体
        RelativeLayout textInfoLayout = (RelativeLayout) findViewById(R.id.step_count_text_info);
        Typeface typeface = ComponentsUtil.getDefaultTypeface(this);
        if (typeface != null) {
            for (int i = 0; i < textInfoLayout.getChildCount(); i++) {
                ((TextView) textInfoLayout.getChildAt(i)).setTypeface(typeface);
            }
        }
    }

    private void troggleStartButton(final boolean state){
        buttonStart.post(new Runnable() {
            @Override
            public void run() {
                buttonStart.setPressed(state);
            }
        });
    }

    private void startStepCount() {
        getService().startStepCount(Mode.REAL_TIME);
    }

    private void stopStepCount() {
        getService().stopStepCount();
    }

}
