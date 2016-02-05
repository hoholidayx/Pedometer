package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hzp.pedometer.R;

public class StepSettingFragment extends PreferenceFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StepSettingFragment() {
        // Required empty public constructor
    }

    public static StepSettingFragment newInstance() {
        StepSettingFragment fragment = new StepSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_setting_step);
        getPreferenceManager().setSharedPreferencesName("test_setting");
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
