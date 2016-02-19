package com.hzp.pedometer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzp.pedometer.R;
import com.hzp.pedometer.components.ArcProgress;
import com.hzp.pedometer.entity.DailyData;
import com.hzp.pedometer.persistance.db.DailyDataManager;

import java.util.Calendar;
import java.util.Random;


public class HomePageFragment extends Fragment {
    private ArcProgress progressStep,progressCa,progressKm;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        progressStep = (ArcProgress) view.findViewById(R.id.arc_progress_step);
        progressCa = (ArcProgress) view.findViewById(R.id.arc_progress_ca);
        progressKm = (ArcProgress) view.findViewById(R.id.arc_progress_km);

        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private DailyData[] loadDailyData(){
        Calendar ca = Calendar.getInstance();
        DailyData[] dataList = DailyDataManager.getInstance().getDataListByDay(
                ca.get(Calendar.YEAR),
                ca.get(Calendar.MONTH),
                ca.get(Calendar.DAY_OF_MONTH)
        );
        return dataList;
    }

    private void displayProgress(final DailyData[] dataList){
        progressStep.setMax(dataList.length);
        progressCa.setMax(dataList.length);
        progressKm.setMax(dataList.length);

        final Handler handler = new Handler();
        new Thread(){
            @Override
            public void run() {
                int stepCount = 0;
                Random rd = new Random();
                for (DailyData data : dataList) {
                    stepCount += data.getStepCount();
                    final int finalStepCount = stepCount;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressStep.setContent(String.valueOf(finalStepCount));
                            progressStep.setProgress(progressStep.getProgress() + 1);

                            progressCa.setContent(String.format("%.2f",finalStepCount*0.04));
                            progressCa.setProgress(progressCa.getProgress() + 1);

                            progressKm.setContent(String.format("%.2f",finalStepCount * 0.8 / 1000));
                            progressKm.setProgress(progressKm.getProgress() + 1);
                        }
                    });
                    try {
                        Thread.sleep(rd.nextInt(500));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void loadData() {
        //加载数据
        DailyData[] dataList = loadDailyData();
        if(dataList!=null){
            //绑定数据到控件
            displayProgress(dataList);
        }
    }
}
