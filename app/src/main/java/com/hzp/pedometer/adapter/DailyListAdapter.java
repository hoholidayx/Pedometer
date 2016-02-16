package com.hzp.pedometer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzp.pedometer.R;

import java.util.Collections;
import java.util.List;

/**
 * @author 何志鹏 on 2016/2/16.
 * @email hoholiday@hotmail.com
 */
public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.PlanCardHolder>
        implements ItemTouchHelperAdapter {

    private List<Object> dataList;
    private Context context;

    public DailyListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public PlanCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_plan_card
                        , parent
                        , false);
        return new PlanCardHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanCardHolder holder, final int position) {
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(dataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    class PlanCardHolder extends RecyclerView.ViewHolder {

        public PlanCardHolder(View itemView) {
            super(itemView);
        }
    }
}
