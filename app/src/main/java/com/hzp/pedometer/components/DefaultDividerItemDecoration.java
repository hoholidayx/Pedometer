package com.hzp.pedometer.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hzp.pedometer.R;

/**
 * @author 何志鹏 on 2016/2/20.
 * @email hoholiday@hotmail.com
 */
public class DefaultDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public DefaultDividerItemDecoration(Resources resources) {
        mDivider = resources.getDrawable(R.drawable.default_recyclerview_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}