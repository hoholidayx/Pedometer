package com.hzp.pedometer.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.RateDashboard;
import com.hzp.pedometer.service.CoreService;
import com.hzp.pedometer.service.Mode;
import com.hzp.pedometer.service.step.StepManager;

/**
 * 计步器工作页面
 */
public class StepCountActivity extends BindingActivity {

    private RateDashboard rateDashboard;
    private TextView textView;

    private SensorManager sensorManager;
    private Sensor sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        initViews();

        //初始化重力传感器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
            final int count = StepManager.getInstance().getStepCount();
            final double count2 = StepManager.getInstance().getStepPerMin();
            rateDashboard.post(new Runnable() {
                @Override
                public void run() {
                    rateDashboard.setDashboardValue(count2 / 300.0);
                }
            });
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(count+":"+ StepManager.getInstance().stepDetector.mjuP+" "
                    +StepManager.getInstance().stepDetector.mjuV+" "
                    +StepManager.getInstance().stepDetector.sigmaP+" "
                    +StepManager.getInstance().stepDetector.sigmaV);
                }
            });
        }
    }


    private void initViews() {
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
        textView = (TextView) findViewById(R.id.step_test);

        rateDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(reciver);
                getService().stopStepCount();
            }
        });
    }


}
