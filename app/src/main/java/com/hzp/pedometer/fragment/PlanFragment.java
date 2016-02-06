package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzp.pedometer.R;
import com.hzp.pedometer.entity.PlanCard;

import java.util.ArrayList;
import java.util.List;


public class PlanFragment extends Fragment {

    private RecyclerView recyclerView;



    public PlanFragment() {
    }

    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
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
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_goal_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new PlanCardAdapter());

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class PlanCardAdapter extends RecyclerView.Adapter<PlanCardAdapter.PlanCardHolder>{

        private List<PlanCard> planCards;

        public PlanCardAdapter(){
            planCards = new ArrayList<>();
            planCards.add(new PlanCard());
        }

        @Override
        public PlanCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_plan_card
                    ,parent
                    ,false);
            return new PlanCardHolder(view);
        }

        @Override
        public void onBindViewHolder(PlanCardHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return planCards.size();
        }

        class PlanCardHolder extends RecyclerView.ViewHolder {

            public PlanCardHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
