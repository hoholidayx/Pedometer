package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hzp.pedometer.R;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
