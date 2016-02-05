package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzp.pedometer.R;


public class SettingFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] items;

    public SettingFragment() {
        initListContent();
    }

    private void initListContent() {
        items = new String[2];
        items[0] = "算法设置";
        items[1] = "应用设置";
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_setting_kind);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SettingKindListAdapter());

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

    class SettingKindListAdapter extends RecyclerView.Adapter<SettingKindListAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder = new Holder(
                    LayoutInflater.from(getActivity())
                            .inflate(R.layout.list_item_setting_kind
                                    , parent
                                    , false)
            );
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.title.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView title;

            public Holder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.list_item_title);
            }
        }
    }

}
