package com.hzp.pedometer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.hzp.pedometer.service.CoreService;

/**
 * @author 何志鹏 on 2016/1/18.
 * @email hoholiday@hotmail.com
 *
 * 带服务绑定的基类
 */
public class BindingActivity extends Activity{

    protected CoreService mService;
    protected boolean mBound = false;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, CoreService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if(mBound){
            onServiceBind();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(connection);
            mBound = false;
            onServiceUnbind();
        }
    }

    protected void onServiceBind(){}

    protected void onServiceUnbind(){}

    public CoreService getService(){
        return mService;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CoreService.CoreBinder binder= (CoreService.CoreBinder)service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
}
