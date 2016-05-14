package com.hzp.pedometer.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.hzp.pedometer.R;
import com.hzp.pedometer.entity.DailyData;
import com.hzp.pedometer.persistance.db.DailyDataManager;
import com.hzp.pedometer.persistance.sp.ApplyDataManager;

import java.util.ArrayList;
import java.util.Calendar;


public class StatisticsFragment extends Fragment {

    private int recentDays = 7;

    private TextView stepsSum, milesSum, calorieSum;
    private CombinedChart combinedChart;
    private PieChart pieChart;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        stepsSum = (TextView) view.findViewById(R.id.statistics_steps_sum);
        milesSum = (TextView) view.findViewById(R.id.statistics_miles_sum);
        calorieSum = (TextView) view.findViewById(R.id.statistics_calorie_sum);
        setupTableData(stepsSum, milesSum, calorieSum);

        combinedChart = (CombinedChart) view.findViewById(R.id.statistics_combined_chart);
        setupCombinedBarChart(combinedChart);

        pieChart = (PieChart) view.findViewById(R.id.statistics_pie_chart);
        setupPieChart(pieChart);

        return view;
    }

    /**
     * 配置生成混合柱状图表
     *
     * @param barChart
     */
    private void setupCombinedBarChart(CombinedChart barChart) {
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setDescription("");
        combinedChart.setDescriptionPosition(0, 0);


        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        String[] d = generateXAxisArray();
        CombinedData data = new CombinedData(d);

        DailyData[][] dayList = getDataRecentDays(recentDays);
        data.setData(generateLineDataAvgPerDay(dayList));
        data.setData(generateBarDataStepPerDay(dayList));

        combinedChart.setData(data);
        combinedChart.invalidate();

    }

    /**
     * 生成x轴的日期列表
     */
    private String[] generateXAxisArray() {
        Calendar ca = Calendar.getInstance();
        String[] d = new String[recentDays];

        ca.add(Calendar.DAY_OF_MONTH, -6);

        for (int i = 0; i < recentDays; i++) {
            d[i] = ca.get(Calendar.DAY_OF_MONTH) + "日";
            ca.add(Calendar.DAY_OF_MONTH, +1);
        }

        return d;
    }

    /**
     * 生成混合图表的线性数据
     */
    private LineData generateLineDataAvgPerDay(DailyData[][] dataList) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        //加载数据
        for (int i = 0; i < dataList.length; i++) {
            int dayAvg = 0;
            int dayStepSum = 0;
            if (dataList[i] != null) {
                for (int j = 0; j < dataList[i].length; j++) {
                    dayStepSum += dataList[i][j].getStepCount();
                }
            }
            dayAvg = dayStepSum / (i + 1);
            entries.add(new BarEntry(dayAvg, i));
        }

        LineDataSet set = new LineDataSet(entries, "近7天平均步数");
        set.setLineWidth(2.5f);
        set.setCircleRadius(5f);
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setColor(getResources().getColor(R.color.colorGreen));
        set.setCircleColor(getResources().getColor(R.color.colorGreen));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }

    /**
     * 生成混合图表的柱状数据
     */
    private BarData generateBarDataStepPerDay(DailyData[][] dataList) {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        //加载数据
        for (int i = 0; i < dataList.length; i++) {
            int dayStepSum = 0;
            if (dataList[i] != null) {
                for (int j = 0; j < dataList[i].length; j++) {
                    dayStepSum += dataList[i][j].getStepCount();
                }
                entries.add(new BarEntry(dayStepSum, i));
            } else {
                entries.add(new BarEntry(0, i));
            }

        }

        BarDataSet set = new BarDataSet(entries, "步数");
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    /**
     * 配置生成饼状图
     *
     * @param pieChart
     */
    private void setupPieChart(PieChart pieChart) {
        pieChart.setHoleColorTransparent(true);

        pieChart.setHoleRadius(60f);  //半径
        pieChart.setTransparentCircleRadius(64f); // 半透明圈

        pieChart.setDescription("步数统计的时间分布");

        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字

        pieChart.setDrawHoleEnabled(true);

        pieChart.setRotationAngle(0); // 初始旋转角度

        //pieChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转

        // display percentage values
        pieChart.setUsePercentValues(false);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
//		mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

        //		mChart.setOnAnimationListener(this);

        int[] data = getPieData();
        pieChart.setCenterText("总步数 " + data[0]);  //饼状图中间的文字

        //设置数据
        pieChart.setData(generatePieData(4, 1.1f, data));

        // undo all highlights
//		pieChart.highlightValues(null);
//		pieChart.invalidate();

        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);  //最右边显示
//		mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);

        pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
    }

    /**
     * 获取饼状图数据
     *
     * @param count 分成几部分
     * @param range 用于饼状图的数据内容
     */
    private PieData generatePieData(int count, float range, int[] data) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容

        xValues.add("早上");
        xValues.add("中午");
        xValues.add("下午");
        xValues.add("晚上");

        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */

        yValues.add(new Entry(data[1], 0));
        yValues.add(new Entry(data[2], 1));
        yValues.add(new Entry(data[3], 2));
        yValues.add(new Entry(data[4], 3));

        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "最近7天"/*显示在比例图上*/);
        pieDataSet.setSliceSpace(5f); //设置个饼状图之间的距离

        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色
        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(255, 123, 124));
        colors.add(Color.rgb(114, 188, 223));
        colors.add(Color.rgb(57, 135, 200));

        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);

        return pieData;
    }

    /**
     * 获得饼状图数据
     *
     * @return int[]: 0 总数 1 ~4 饼图的四项数据
     */
    private int[] getPieData() {
        DailyData[][] dataList = getDataRecentDays(recentDays);
        int stepsSum = 0;
        int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0;

        //时段1下限
        Calendar ca1Min = Calendar.getInstance();
        ca1Min.set(Calendar.HOUR_OF_DAY, 0);
        ca1Min.set(Calendar.MINUTE, 1);
        //时段1上限
        Calendar ca1Max = Calendar.getInstance();
        ca1Max.set(Calendar.HOUR_OF_DAY, 10);
        ca1Max.set(Calendar.MINUTE, 0);

        //时段2下限
        Calendar ca2Min = Calendar.getInstance();
        ca2Min.set(Calendar.HOUR_OF_DAY, 10);
        ca2Min.set(Calendar.MINUTE, 1);
        //时段2上限
        Calendar ca2Max = Calendar.getInstance();
        ca2Max.set(Calendar.HOUR_OF_DAY, 14);
        ca2Max.set(Calendar.MINUTE, 0);

        //时段3下限
        Calendar ca3Min = Calendar.getInstance();
        ca3Min.set(Calendar.HOUR_OF_DAY, 14);
        ca3Min.set(Calendar.MINUTE, 1);
        //时段3上限
        Calendar ca3Max = Calendar.getInstance();
        ca3Max.set(Calendar.HOUR_OF_DAY, 19);
        ca3Max.set(Calendar.MINUTE, 0);

        //时段4下限
        Calendar ca4Min = Calendar.getInstance();
        ca4Min.set(Calendar.HOUR_OF_DAY, 19);
        ca4Min.set(Calendar.MINUTE, 1);
        //时段4上限
        Calendar ca4Max = Calendar.getInstance();
        ca4Max.set(Calendar.HOUR_OF_DAY, 23);
        ca4Max.set(Calendar.MINUTE, 59);

        for (int i = 0; i < dataList.length; i++) {
            if (dataList[i] != null) {

                for (int j = 0; j < dataList[i].length; j++) {

                    DailyData tempData = dataList[i][j];

                    stepsSum += tempData.getStepCount();

                    long startTime = tempData.getStartTime();
                    long endTime = tempData.getEndTime();
                    Calendar tempMin = Calendar.getInstance();
                    tempMin.setTimeInMillis(startTime);
                    Calendar tempMax = Calendar.getInstance();
                    tempMax.setTimeInMillis(endTime);
                    //判断所处时间段
                    if (tempMax.before(ca1Max) && tempMin.after(ca1Min)) {
                        sum1 += tempData.getStepCount();
                    } else if (tempMax.before(ca2Max) && tempMin.after(ca2Min)) {
                        sum2 += tempData.getStepCount();
                    } else if (tempMax.before(ca3Max) && tempMin.after(ca3Min)) {
                        sum3 += tempData.getStepCount();
                    } else if (tempMax.before(ca4Max) && tempMin.after(ca4Min)) {
                        sum4 += tempData.getStepCount();
                    }
                }
            }
        }
        return new int[]{stepsSum, sum1, sum2, sum3, sum4};
    }

    /**
     * 设置表格内的统计数据
     *
     * @param stepsSum   总步数
     * @param milesSum   总里程
     * @param calorieSum 总卡路里消耗
     */
    private void setupTableData(TextView stepsSum, TextView milesSum, TextView calorieSum) {
        stepsSum.setText(String.valueOf(ApplyDataManager.getInstance().getStepsSum()));
        milesSum.setText(String.format("%.2f",
                        ApplyDataManager.getInstance().getMileSum())
        );
        calorieSum.setText(String.format("%.2f",
                        ApplyDataManager.getInstance().getCalorieSum())
        );
    }

    private DailyData[][] getDataRecentDays(int days) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -days + 1);

        DailyData[][] dataList = new DailyData[days][];

        for (int i = 0; i < days; i++) {

            DailyData[] temp = DailyDataManager.getInstance().getDataListByDay(
                    ca.get(Calendar.YEAR),
                    ca.get(Calendar.MONTH),
                    ca.get(Calendar.DAY_OF_MONTH)
            );
            dataList[i] = temp;

            ca.add(Calendar.DAY_OF_MONTH, +1);
        }
        return dataList;
    }


}
