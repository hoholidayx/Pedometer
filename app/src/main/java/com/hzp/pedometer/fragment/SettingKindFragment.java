package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzp.pedometer.R;
import com.hzp.pedometer.activity.SettingActivity;


public class SettingKindFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] items;

    public SettingKindFragment() {
        initListContent();
    }

    private void initListContent() {
        items = new String[2];
        items[0] = "算法设置";
        items[1] = "应用设置";
    }

    public static SettingKindFragment newInstance() {
        SettingKindFragment fragment = new SettingKindFragment();
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

    class SettingKindListAdapter extends RecyclerView.Adapter<SettingKindListAdapter.Holder>
            implements View.OnClickListener{

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_setting_kind
                            , parent
                            , false);
            view.setOnClickListener(this);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            holder.title.setText(items[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(getActivity(),SettingActivity.class);
                    switch (position){
                        case 0:
                            bundle.putString(SettingActivity.KEY_TITLE
                                    , getString(R.string.navigation_step_setting_title));
                            break;
                        case 1:
                            bundle.putString(SettingActivity.KEY_TITLE
                                    , getString(R.string.navigation_app_setting_title));
                            break;
                    }
                    intent.putExtra(SettingActivity.KEY_SETTING_INFO, bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        @Override
        public void onClick(View v) {

        }

        class Holder extends RecyclerView.ViewHolder {

            TextView title;

            public Holder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.list_item_title);
            }
        }

    }

    interface OnListItemClickListener{
        void onItemClick(View v,int id);
    }

}
