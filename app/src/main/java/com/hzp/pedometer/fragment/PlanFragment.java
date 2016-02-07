package com.hzp.pedometer.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hzp.pedometer.R;
import com.hzp.pedometer.entity.PlanCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlanFragment extends Fragment {

    private RecyclerView recyclerView;
    private int[] colors = new int[7];


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
        PlanCardAdapter adapter = new PlanCardAdapter();
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        colors[0] = getActivity().getResources().getColor(R.color.colorBlue);
        colors[1] = getActivity().getResources().getColor(R.color.colorGreen);
        colors[2] = getActivity().getResources().getColor(R.color.colorOrange);
        colors[3] = getActivity().getResources().getColor(R.color.colorPink);
        colors[4] = getActivity().getResources().getColor(R.color.colorYellow);
        colors[5] = getActivity().getResources().getColor(R.color.colorPuple);
        colors[6] = getActivity().getResources().getColor(R.color.colorRed);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class PlanCardAdapter extends RecyclerView.Adapter<PlanCardAdapter.PlanCardHolder>
            implements ItemTouchHelperAdapter {

        private List<PlanCard> planCards;

        public PlanCardAdapter() {
            planCards = new ArrayList<>();
            for (int i = 0; i < 7; i++)
                planCards.add(new PlanCard());
        }

        @Override
        public PlanCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_plan_card
                            , parent
                            , false);
            return new PlanCardHolder(view);
        }

        @Override
        public void onBindViewHolder(PlanCardHolder holder, final int position) {
            holder.cardView.setCardBackgroundColor(colors[position]);
        }

        @Override
        public int getItemCount() {
            return planCards.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(planCards, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            planCards.remove(position);
            notifyItemRemoved(position);
        }

        class PlanCardHolder extends RecyclerView.ViewHolder {

            CardView cardView;

            public PlanCardHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.plan_card_view);
            }
        }
    }

    class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(
                ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(),
                    target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    public interface ItemTouchHelperAdapter {

        /**
         * Called when an item has been dragged far enough to trigger a move. This is called every time
         * an item is shifted, and <strong>not</strong> at the end of a "drop" event.<br/>
         * <br/>
         * Implementations should call {@link RecyclerView.Adapter#notifyItemMoved(int, int)} after
         * adjusting the underlying data to reflect this move.
         *
         * @param fromPosition The start position of the moved item.
         * @param toPosition   Then resolved position of the moved item.
         *
         * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
         * @see RecyclerView.ViewHolder#getAdapterPosition()
         */
        void onItemMove(int fromPosition, int toPosition);


        /**
         * Called when an item has been dismissed by a swipe.<br/>
         * <br/>
         * Implementations should call {@link RecyclerView.Adapter#notifyItemRemoved(int)} after
         * adjusting the underlying data to reflect this removal.
         *
         * @param position The position of the item dismissed.
         *
         * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
         * @see RecyclerView.ViewHolder#getAdapterPosition()
         */
        void onItemDismiss(int position);
    }

}
