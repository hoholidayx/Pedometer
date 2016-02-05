package com.hzp.pedometer.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hzp.pedometer.R;
import com.hzp.pedometer.fragment.StepSettingFragment;

public class SettingActivity extends AppCompatActivity {
    public static final String KEY_SETTING_INFO = "SETTING_INFO";
    public static final String KEY_TITLE = "TITLE";

    private Bundle savedInstanceState;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.savedInstanceState = savedInstanceState;
        Bundle bundle = getIntent().getBundleExtra(KEY_SETTING_INFO);
        if(bundle!=null){
            title = bundle.getString(KEY_TITLE,"error");
        }

        initViews();
        setupViews();
    }

    private void initViews(){
        initFragment();
    }

    private void initFragment(){
        if(savedInstanceState!=null){
            return;
        }
        if(getString(R.string.navigation_step_setting_title).equals(title)){
            getFragmentManager().beginTransaction()
                    .replace(R.id.setting_content,StepSettingFragment.newInstance())
                            .commit();
        }else if(getString(R.string.navigation_app_setting_title).equals(title)){
//            getFragmentManager().beginTransaction()
//                    .add(R.id.setting_content,StepSettingFragment.newInstance())
//                    .commit();
        }

    }

    private void setupViews(){
        setSupportActionBar((Toolbar) findViewById(R.id.setting_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_step_count_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
