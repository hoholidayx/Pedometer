package com.hzp.pedometer.service;

/**
 * @author hoholiday on 2016/5/13.
 * @email hoholiday@hotmail.com
 */
public class ApplyUtils {

    public static double getCalorieConsume(int steps){
        return steps *0.04;
    }

    public static double getmMileage(int steps){
       return steps * 0.8 / 1000;
    }
}
