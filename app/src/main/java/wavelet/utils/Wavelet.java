package wavelet.utils;

/**
 * @author 何志鹏 on 2016/1/9.
 * @email hoholiday@hotmail.com
 *
 * 数据波形处理相关
 */
public class Wavelet {

    public static native double[] linearSmooth5(double[] data, int N);

    public static native double[] waveletFilter(double[] data, int N);

    public static native double[] medianFilter(double[] data, int N);
}
