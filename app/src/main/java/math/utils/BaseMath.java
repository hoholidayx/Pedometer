package math.utils;

import android.util.Log;

import java.util.List;

/**
 * @author 何志鹏 on 2016/1/16.
 * @email hoholiday@hotmail.com
 */
public class BaseMath {

    public static double max3(double x, double y, double z) {
        double temp = x > y ? x : y;
        return temp > z ? temp : z;
    }

    public static double min3(double x, double y, double z) {
        double temp = x < y ? x : y;
        return temp < z ? temp : z;
    }
    
    public static double avg(List<Double> data) {
        double sum = 0.0;
        for(Double num:data){
            sum+= num;
        }
        return sum/data.size();
    }


    /**
     * 计算标准差
     *
     * @param data   数据
     * @return 标准差
     */
    public static double stdev(List<Double> data){
        return stdev(data,avg(data));
    }

    /**
     * 带输入平均值的计算标准差
     *
     * @param data   数据
     * @param avg    平均值
     * @return 标准差
     */
    public static double stdev(List<Double> data, double avg){
        double S = 0;
        for(Double num:data){
            S += Math.pow((num - avg),2);
        }
        return Math.sqrt(S / data.size());
    }

}
