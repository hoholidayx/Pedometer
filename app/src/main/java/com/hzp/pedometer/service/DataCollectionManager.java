package com.hzp.pedometer.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.hzp.pedometer.persistance.sp.StepConfigManager;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author 何志鹏 on 2016/4/28.
 * @email hoholiday@hotmail.com
 */
public class DataCollectionManager  implements SensorEventListener {

    private static DataCollectionManager instance;
    private Context context;

    private SensorManager sensorManager;
    private Sensor sensor;

    private long startTime = 0;

    private OnDataCollectionListener listener;

    private boolean isWorking =false;

    private DataCollectionManager() {
    }

    public static DataCollectionManager getInstance() {
        if(instance==null){
            synchronized (DataCollectionManager.class){
                if (instance==null){
                    instance = new DataCollectionManager();
                }
            }
        }
        return instance;
    }

    public  void init(Context context){
        this.context = context;
        initSensors();
    }


    public void start(){
        sensorManager.registerListener(this, sensor,
                (int) ((1.0 / StepConfigManager.getInstance().getSamplingRate()) * 1000 * 1000));//微秒
        startTime = Calendar.getInstance().getTimeInMillis();

        isWorking = true;
    }

    public void stop(){
        sensorManager.unregisterListener(this);
        startTime = 0;

        isWorking = false;
    }

    public long getStartTime(){
        return startTime;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setOnDataCollectionListener(OnDataCollectionListener listener){
        this.listener = listener;
    }


    /**
     * 传感器初始化
     */
    private void initSensors() {
        //初始化重力传感器
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        BigDecimal x = new BigDecimal(event.values[0]);
        x = x.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal y = new BigDecimal(event.values[1]);
        y = y.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal z = new BigDecimal(event.values[2]);
        z = z.setScale(2, BigDecimal.ROUND_HALF_UP);

        if(listener !=null){
            listener.onDataReceived(x.doubleValue(), y.doubleValue(), z.doubleValue());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    interface OnDataCollectionListener {
        public void onDataReceived(double x, double y, double z);
    }
}
