package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.hzp.pedometer.R;

import java.util.Map;

public class SettingFragment extends PreferenceFragment {
    private static final String ARG_SETTING_PREFERENCE = "setting_preference";

    private int preferenceResId;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(int preferenceResId) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SETTING_PREFERENCE,preferenceResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            preferenceResId = getArguments().getInt(ARG_SETTING_PREFERENCE);
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(preferenceResId);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preferenceResId){
            case R.xml.preference_app:
                if(preference.getKey().equals(getString(R.string.KEY_ENABLE_NORMAL_STEP_COUNT))){
                    onEnableNormalStepCountChange(preference.getSharedPreferences().getBoolean(
                            getString(R.string.KEY_ENABLE_NORMAL_STEP_COUNT),
                            true
                    ));
                }
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void onEnableNormalStepCountChange(boolean enable){
        // TODO: 2016/2/9  
        Log.e("启动",""+enable);
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
