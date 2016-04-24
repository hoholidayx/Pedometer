package com.hzp.pedometer.service;

import android.content.Context;
import android.os.PowerManager;

/**
 * @author 何志鹏 on 2016/2/4.
 * @email hoholiday@hotmail.com
 */
public class ServiceUtil {

    /**
     * 竖直加速度修正
     */
    static final float alpha = 0.8f;
    static float[] gravity = new float[3];
    static float[] linear_acceleration = new float[3];

    /**
     * 竖直方向加速度修正
     *
     * @return 修正后的合加速度
     */
    public static float accelerationCorrection(float value1, float value2, float value3) {
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * value1;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * value2;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * value3;

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = value1 - gravity[0];
        linear_acceleration[1] = value2 - gravity[1];
        linear_acceleration[2] = value3 - gravity[2];

        float gravitySum = (float) Math.sqrt(
                gravity[0]*gravity[0]+gravity[1]*gravity[1]+gravity[2]*gravity[2]
        );

        return linear_acceleration[0] * gravity[0] / gravitySum
                + linear_acceleration[1] * gravity[1] / gravitySum
                + linear_acceleration[2] * gravity[2] / gravitySum;
    }

    public static PowerManager.WakeLock getWakeLock(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"CoreServiceWakeLock");
    }
}
