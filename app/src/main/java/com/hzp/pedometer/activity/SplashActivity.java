package com.hzp.pedometer.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hzp.pedometer.R;

/**
 * 启动页面
 *
 * 闪屏
 */
public class SplashActivity extends AppCompatActivity {

    private long waitTime = 1000;//闪屏等待时间
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
    }

    /**
     * 开启启动主界面的任务
     */
    private void startMainActivity(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: 2016/1/18 完成主界面跳转
                Intent intent = new Intent(SplashActivity.this,);
                startActivity(intent);
            }
        },waitTime);
    }
}
