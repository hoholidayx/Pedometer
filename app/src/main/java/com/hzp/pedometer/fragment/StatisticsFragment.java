package com.hzp.pedometer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.hzp.pedometer.R;
import com.hzp.pedometer.entity.DailyData;
import com.hzp.pedometer.persistance.db.DailyDataManager;

import java.util.ArrayList;
import java.util.Calendar;


public class StatisticsFragment extends Fragment {

    private CombinedChart combinedChart;

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

        combinedChart = (CombinedChart) view.findViewById(R.id.statistics_weekly_combined_chart);
        setupWeeklyBarChart(combinedChart);

        return view;
    }

    private void setupWeeklyBarChart(CombinedChart barChart) {
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

        String[] d = new String[]{ "周日","周一", "周二", "周三", "周四", "周五", "周六"};
        CombinedData data = new CombinedData(d);

        DailyData[][] dayList = getDataByWeek();
        data.setData(generateLineDataAvgPerDay(dayList));
        data.setData(generateBarDataStepPerDay(dayList));

        combinedChart.setData(data);
        combinedChart.invalidate();

    }

    //生成混合图表的线性数据
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
                dayAvg = dayStepSum/(i+1);
            }
            entries.add(new BarEntry(dayAvg, i));
        }

        LineDataSet set = new LineDataSet(entries, "周平均步数");
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

    //生成混合图表的柱状数据
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
            }
            entries.add(new BarEntry(dayStepSum, i));
        }

        BarDataSet set = new BarDataSet(entries, "步数");
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    private DailyData[][] getDataByWeek() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY,0);
        ca.set(Calendar.MINUTE,1);
        ca.set(Calendar.SECOND,0);

        int days = 7;
        DailyData[][] dataList = new DailyData[days][];

        for (int i = Calendar.SUNDAY, j = 0; i < Calendar.SATURDAY; i++, j++) {
            ca.set(Calendar.DAY_OF_WEEK, i);
            DailyData[] temp = DailyDataManager.getInstance().getDataListByDay(
                    ca.get(Calendar.YEAR),
                    ca.get(Calendar.MONTH),
                    ca.get(Calendar.DAY_OF_MONTH)
            );
            dataList[j] = temp;
        }
        return dataList;
    }


}
