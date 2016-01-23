package com.hzp.pedometer.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.hzp.pedometer.service.step.StepManager;

/**
 * 计步器工作页面
 */
public class StepCountActivity extends BindingActivity {

    private RateDashboard rateDashboard;
    private TextView stepCountText;
    private ImageButton buttonStart;

    private StepReceiver stepReceiver;
    private IntentFilter intentFilter = new IntentFilter(StepManager.ACTION_STEP_COUNT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        stepReceiver = new StepReceiver();

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stepReceiver);
    }

    @Override
    protected void onServiceBind() {
        //恢复现场
        if(getService().isWorking()){
            toggleStartButton(true);
            registerReceiver(stepReceiver, intentFilter);
        }

    }

    private void initViews() {
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
        stepCountText = (TextView) findViewById(R.id.step_count_textView);
        buttonStart = (ImageButton) findViewById(R.id.step_count_start_button);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceBinded()){
                    if(getService().isWorking()){
                        toggleStartButton(false);
                        stopStepCount();
                    }else{
                        toggleStartButton(true);
                        startStepCount();
                    }
                }
            }
        });

        //设置字体
        RelativeLayout textInfoLayout = (RelativeLayout) findViewById(R.id.step_count_text_info_panel);
        Typeface typeface = ComponentsUtil.getDefaultTypeface(this);
        if (typeface != null) {
            for (int i = 0; i < textInfoLayout.getChildCount(); i++) {
                ((TextView) textInfoLayout.getChildAt(i)).setTypeface(typeface);
            }
        }
    }

    private void toggleStartButton(final boolean state){
        buttonStart.post(new Runnable() {
            @Override
            public void run() {
                if(state){
                    buttonStart.setImageResource(R.drawable.ic_button_start_pressed);
                }else{
                    buttonStart.setImageResource(R.drawable.ic_button_start_normal);
                }
//                 buttonStart.setPressed(state);// FIXME: 2016/1/23 按虚拟键盘会导致button的press状态改变
            }
        });
    }

    private void setStepCount(final int count){
        if(stepCountText!=null){
            stepCountText.post(new Runnable() {
                @Override
                public void run() {
                    stepCountText.setText(String.valueOf(count));
                }
            });
        }
    }

    private void setStepPerMin(final double stepPerMin){
        if(rateDashboard!=null){
            rateDashboard.post(new Runnable() {
                @Override
                public void run() {
                    rateDashboard.setDashboardValue((int) stepPerMin);
                    rateDashboard.setDashboardPercentage(stepPerMin/300);
                }
            });
        }
    }

    private void startStepCount() {
        setStepCount(0);
        setStepPerMin(0);
        getService().startStepCount(Mode.REAL_TIME);

        registerReceiver(stepReceiver, intentFilter);
    }

    private void stopStepCount() {
        getService().stopStepCount();

        unregisterReceiver(stepReceiver);
    }

    class StepReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent !=null){
                int stepCount = intent.getIntExtra(StepManager.KEY_STEP_COUNT,0);
                double stepPerMin = intent.getDoubleExtra(StepManager.KEY_STEP_PER_MIN,0);

                setStepCount(stepCount);
                setStepPerMin(stepPerMin);
            }
        }
    }
}
