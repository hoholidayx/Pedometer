package com.hzp.pedometer.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.RateDashboard;
import com.hzp.pedometer.service.CoreService;
import com.hzp.pedometer.service.Mode;
import com.hzp.pedometer.service.step.StepManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 计步器工作页面
 */
public class StepCountActivity extends BindingActivity {

    private RateDashboard rateDashboard;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        handler = new Handler();
        initViews();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onServiceBind() {

        CoreService d = getService();
        d.startStepCount(Mode.REAL_TIME);

        reciver = new MyReciver();
        registerReceiver(reciver, new IntentFilter(StepManager.ACTION_STEP_COUNT));
    }
    private MyReciver reciver;
    class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = StepManager.getInstance(getApplicationContext()).getStepCount();
            final double count2 = StepManager.getInstance(getApplicationContext()).getStepPerMin();
            rateDashboard.post(new Runnable() {
                @Override
                public void run() {
                    rateDashboard.setDashboardValue(count2 / 300.0);
                }
            });
            //TODO
            Log.e("步数", "步数：" + count + "步频：" + count2);
        }
    }


    private void initViews() {
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
        rateDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(reciver);
                getService().stopStepCount();
            }
        });
    }
}
