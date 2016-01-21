package math.utils;

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
    
    public static Object avg(List data,Class<? extends Number> clazz) {

        String name = clazz.getName();
        String name2 = Double.class.getName();
        String name3 = Long.class.getName();
        if(name.equals(name2)){
            double sum = 0.0;
            for(Object num : data){
                sum+= (double)num;
            }
            return sum/data.size();
        }
        else if(name.equals(name3)){
            long sum = 0;
            for(Object num : data){
                sum+= (long)num;
            }
            return sum/data.size();
        }
        else{
            //异常数字类型
           return 0;
        }

    }


    /**
     * 计算标准差
     *
     * @param data   数据
     * @param clazz  数字类
     * @return 标准差
     */
    public static Object stdev(List data,Class<? extends Number> clazz){
        return stdev(data,avg(data,clazz), clazz);
    }

    /**
     * 带输入平均值的计算标准差
     *
     * @param data   数据
     * @param avg    平均值
     * @param clazz  数字类
     * @return 标准差
     */
    public static Object stdev(List data, Object avg,Class<? extends Number> clazz){

        String name = clazz.getName();

        if(name.equals(Double.class.getName())){
            double S = 0;
            for (int i = 0; i < data.size(); i++) {
                S += Math.pow(((double)(data.get(i)) - (double)avg),2);
            }
            return Math.sqrt(S / data.size());
        }
        else if(name.equals(Long.class.getName())){
            long S = 0;
            for (int i = 0; i < data.size(); i++) {
                S += Math.pow(((long)(data.get(i)) - (long)avg),2);
            }
            return Math.sqrt(S / data.size());
        }
        else{
            //异常数字类型
            return 0;
        }
    }

}
