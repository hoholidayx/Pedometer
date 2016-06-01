package com.hzp.pedometer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzp.pedometer.R;
import com.hzp.pedometer.entity.DailyData;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author 何志鹏 on 2016/2/16.
 * @email hoholiday@hotmail.com
 */
public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.DailyDataHolder>
        implements ItemTouchHelperAdapter {

    private List<DailyData> dataList;
    private Context context;

    private SimpleDateFormat dateFormat;

    public DailyListAdapter(Context context,List<DailyData> dataList) {
        this.context = context;
        this.dataList = dataList;
        dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public DailyDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_daily_data
                        , parent
                        , false);
        return new DailyDataHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyDataHolder holder, final int position) {
        DailyData data = dataList.get(position);
        holder.time.setText(dateFormat.format(data.getStartTime()));
        holder.stepCount.setText(String.valueOf(data.getStepCount()));
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

    public void addItem(DailyData item,int position){
        dataList.add(position,item);
        notifyItemInserted(position);
    }

    public void clearItems(){
        dataList.clear();
        notifyDataSetChanged();
    }

    class DailyDataHolder extends RecyclerView.ViewHolder {

        private TextView time,stepCount;

        public DailyDataHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.list_item_time);
            stepCount = (TextView) itemView.findViewById(R.id.list_item_step_count);
        }
    }
}
