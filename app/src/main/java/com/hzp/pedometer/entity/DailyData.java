package com.hzp.pedometer.entity;

/**
 * @author 何志鹏 on 2016/2/16.
 * @email hoholiday@hotmail.com
 */
public class DailyData {
    private long modifyTime;
    private long startTime,endTime;
    private int stepCount;
    private double miles;
    private double calorie;

    public long getModifyTime() {
        return modifyTime;
    }

    public DailyData setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public DailyData setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getEndTime() {
        return endTime;
    }

    public DailyData setEndTime(long endTime) {
        this.endTime = endTime;
        return this;
    }

    public int getStepCount() {
        return stepCount;
    }

    public DailyData setStepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }

    public double getMiles() {
        return miles;
    }

    public DailyData setMiles(double miles) {
        this.miles = miles;
        return this;
    }

    public double getCalorie() {
        return calorie;
    }

    public DailyData setCalorie(double calorie) {
        this.calorie = calorie;
        return this;
    }
}
