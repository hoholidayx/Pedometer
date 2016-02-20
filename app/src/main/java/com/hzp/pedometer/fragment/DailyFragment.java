package com.hzp.pedometer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.hzp.pedometer.R;
import com.hzp.pedometer.adapter.DailyListAdapter;
import com.hzp.pedometer.adapter.SimpleItemTouchHelperCallback;
import com.hzp.pedometer.entity.DailyData;
import com.hzp.pedometer.persistance.db.DailyDataManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;


public class DailyFragment extends Fragment {

    private RecyclerView recyclerView;
    private MaterialCalendarView calendarView;
    private DailyListAdapter adapter;

    public DailyFragment() {
    }

    public static DailyFragment newInstance() {
        DailyFragment fragment = new DailyFragment();
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
        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_daily_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DailyListAdapter(getActivity(), new ArrayList<DailyData>());
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView);

        calendarView = (MaterialCalendarView) view.findViewById(R.id.fragment_daily_calendar_view);
        calendarView.setOnDateChangedListener(new DateChangeListener());
        calendarView.setDateSelected(Calendar.getInstance(),true);

        //更新当天的数据
        Calendar ca = Calendar.getInstance();
        updateDataList(
                ca.get(Calendar.YEAR),
                ca.get(Calendar.MONTH),
                ca.get(Calendar.DAY_OF_MONTH));

        return view;
    }

    class DateChangeListener implements OnDateSelectedListener {

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget,
                                   @NonNull CalendarDay date,
                                   boolean selected) {
            updateDataList(date.getYear(), date.getMonth(), date.getDay());
        }
    }

    private void updateDataList(int year, int month, int dayOfMonth) {
        DailyData[] dataList = DailyDataManager.getInstance()
                .getDataListByDay(
                        year,
                        month,
                        dayOfMonth
                );
        adapter.clearItems();
        if (dataList != null) {
            for (DailyData data : dataList) {
                adapter.addItem(data, 0);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
