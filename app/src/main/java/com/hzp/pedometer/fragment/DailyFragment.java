package com.hzp.pedometer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzp.pedometer.R;
import com.hzp.pedometer.adapter.DailyListAdapter;
import com.hzp.pedometer.adapter.SimpleItemTouchHelperCallback;
import com.hzp.pedometer.entity.DailyData;

import java.util.ArrayList;


public class DailyFragment extends Fragment {

    private RecyclerView recyclerView;

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
        DailyListAdapter adapter = new DailyListAdapter(getActivity(), new ArrayList<DailyData>());
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
