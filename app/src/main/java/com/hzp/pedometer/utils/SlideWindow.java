package com.hzp.pedometer.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author hoholiday on 2016/5/5.
 * @email hoholiday@hotmail.com
 */
public class SlideWindow<T>{
    private List<T> list;
    private int size;

    public SlideWindow(int size){
        this.size = size;
        list = new LinkedList<>();
    }

    public void add(T data){
        if(list.size()>size){
           poll();
        }
        list.add(data);
    }

    public T poll(){
        return list.remove(0);
    }

    public List<T> getList(){
        return list;
    }
}
