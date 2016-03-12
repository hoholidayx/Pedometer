package com.hzp.pedometer.service.step;

/**
 * @author 何志鹏 on 2016/3/12.
 * @email hoholiday@hotmail.com
 */
public interface OnStepCountListener {
    /**
     * 步数增加后回调
     *
     * @param count 总步数
     */
    void onStepCounted(int count);
}