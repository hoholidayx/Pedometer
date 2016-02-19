package com.hzp.pedometer.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.RateDashboard;
import com.hzp.pedometer.service.Mode;
import com.hzp.pedometer.service.step.StepManager;

/**
 * 计步器工作页面
 */
public class StepCountActivity extends BindingActivity {

    private Toolbar toolbar;

    private RateDashboard rateDashboard;
    private TextView stepCountText;
    private ToggleButton buttonStart;

    private StepReceiver stepReceiver;
    private IntentFilter intentFilter = new IntentFilter(StepManager.ACTION_STEP_COUNT);

    private boolean registerBroadcast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        stepReceiver = new StepReceiver();

        initViews();
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStepCount();
    }

    @Override
    protected void onServiceBind() {
        //恢复现场
        if(getService().isWorking()){
            toggleStartButton(true);
            registerBroadcast();
        }
    }

    private void initViews() {
        rateDashboard = (RateDashboard) findViewById(R.id.step_rate_dashboard_view);
        stepCountText = (TextView) findViewById(R.id.step_count_textView);
        buttonStart = (ToggleButton) findViewById(R.id.step_count_start_button);
        toolbar = (Toolbar) findViewById(R.id.step_count_activity_toolbar);
    }

    private void setupViews(){
        toolbar.setTitle(R.string.navigation_top_detail_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {;
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceBinded()) {
                    if (getService().isWorking()) {
                        toggleStartButton(false);
                        stopStepCount();
                    } else {
                        toggleStartButton(true);
                        startStepCount();
                    }
                }
            }
        });

        // TODO: 2016/2/19 字体内存无法释放,typeface只能创建一次
        //设置字体
//        RelativeLayout textInfoLayout = (RelativeLayout) findViewById(R.id.step_count_text_info_panel);
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/trends.ttf");
//        if (typeface != null) {
//            for (int i = 0; i < textInfoLayout.getChildCount(); i++) {
//                ((TextView) textInfoLayout.getChildAt(i)).setTypeface(typeface);
//            }
//        }
    }

    private void toggleStartButton(final boolean state){
        buttonStart.post(new Runnable() {
            @Override
            public void run() {
//                if(state){
//                    buttonStart.setImageResource(R.drawable.ic_button_start_pressed);
//                }else{
//                    buttonStart.setImageResource(R.drawable.ic_button_start_normal);
//                }
                buttonStart.setChecked(state);
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

    private void setStepPerMin(final int stepPerMin){
        if(rateDashboard!=null){
            rateDashboard.post(new Runnable() {
                @Override
                public void run() {
                    rateDashboard.setDashboardValue(stepPerMin);
                    rateDashboard.setDashboardPercentage(stepPerMin / 300.0);
                }
            });
        }
    }

    private void startStepCount() {
        setStepCount(0);
        setStepPerMin(0);
        getService().startStepCount(Mode.REAL_TIME);

        registerBroadcast();

    }

    private void stopStepCount() {
        getService().stopStepCount();
        unregisterBroadcast();
    }

    private void registerBroadcast(){
        if(!registerBroadcast){
            registerReceiver(stepReceiver,intentFilter);
            registerBroadcast = true;
        }
    }

    private void unregisterBroadcast(){
        if(registerBroadcast){
            unregisterReceiver(stepReceiver);
            registerBroadcast = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_step_count_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class StepReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent !=null){
                int stepCount = intent.getIntExtra(StepManager.KEY_STEP_COUNT,0);
                int stepPerMin = intent.getIntExtra(StepManager.KEY_STEP_PER_MIN, 0);

                setStepCount(stepCount);
                setStepPerMin(stepPerMin);
            }
        }
    }
}
