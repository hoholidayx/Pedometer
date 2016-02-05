package com.hzp.pedometer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.hzp.pedometer.R;
import com.hzp.pedometer.fragment.GoalFragment;
import com.hzp.pedometer.fragment.HomePageFragment;
import com.hzp.pedometer.fragment.SettingFragment;
import com.hzp.pedometer.fragment.StepSettingFragment;
import com.hzp.pedometer.fragment.StatisticsFragment;

public class MainActivity extends BindingActivity implements
        HomePageFragment.OnFragmentInteractionListener {
    private Bundle savedInstanceState;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBar actionBar;
    private Toolbar toolbar;

    private FrameLayout frameLayout;
    private Fragment homePageFragment;
    private StatisticsFragment statisticsFragment;
    private GoalFragment goalFragment;
    private SettingFragment settingFragment;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);

        handler = new Handler();

        initViews();
    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.drawer_navigation_view);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.activity_main_content);

        initFragments();

        setupViews();
        setupDrawerContent(navigationView);
    }

    private void initFragments() {
        //防止重叠
        if (savedInstanceState != null) {
            return;
        }
        //初始化fragment
        homePageFragment = HomePageFragment.newInstance();
        statisticsFragment = StatisticsFragment.newInstance();
        goalFragment = GoalFragment.newInstance();
        settingFragment = SettingFragment.newInstance();
        replaceFragment(homePageFragment);
    }

    private void replaceFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), fragment)
                .commit();
    }

    private void setupViews() {
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("今日");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //items 事件
                        menuFragmentsSwitch(menuItem);

                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void menuFragmentsSwitch(final MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.menu_drawer_step_count) {
            actionBar.setTitle(menuItem.getTitle());
        }
        switch (menuItem.getItemId()) {
            case R.id.menu_drawer_home:
                replaceFragment(homePageFragment);
                break;
            case R.id.menu_drawer_statistic:
                replaceFragment(statisticsFragment);
                break;
            case R.id.menu_drawer_goal:
                replaceFragment(goalFragment);
                break;
            case R.id.menu_drawer_step_count:
                //TODO 等待当前工作停止，弹出waiting框
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), StepCountActivity.class));
                    }
                }, 1000);
                break;
            case R.id.menu_drawer_settings:
                replaceFragment(settingFragment);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_drawer, menu);
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
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
