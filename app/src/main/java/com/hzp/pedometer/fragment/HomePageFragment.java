package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzp.pedometer.R;
import com.hzp.pedometer.activity.BindingActivity;
import com.hzp.pedometer.components.ArcProgress;
import com.hzp.pedometer.service.CoreService;


public class HomePageFragment extends LazyFragment {
    ;
    private ArcProgress progressStep;
    private CoreService service;

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
        View view =  inflater.inflate(R.layout.fragment_home_page, container, false);

        progressStep = (ArcProgress) view.findViewById(R.id.arc_progress_step);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof BindingActivity){
            service = ((BindingActivity)activity).getService();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void lazyLoad() {
        if(service!=null){
            //加载步数相关数据
            progressStep.setMax(service.countStepFromFiles(new CoreService.CountStepFromFilesListener() {
                @Override
                public void onStepCount(final int stepCount) {
                    progressStep.post(new Runnable() {
                        @Override
                        public void run() {
                            progressStep.setProgress(stepCount);
                        }
                    });
                }
            }));
        }
    }
}
