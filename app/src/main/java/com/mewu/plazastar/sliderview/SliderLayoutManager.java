package com.mewu.plazastar.sliderview;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SliderLayoutManager extends LinearLayoutManager {

    public SliderLayoutManager(Context context) {
        super(context);
        setOrientation(RecyclerView.HORIZONTAL);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        new LinearSnapHelper().attachToRecyclerView(view);
        scrollToPosition(0);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scaleDownView();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
        scaleDownView();
        return scrolled;
    }


    private void scaleDownView() {
        float mid = getWidth() / 2.0f;

        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);

            assert child != null;

            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float distanceFromCenter = Math.abs(mid - childMid);

            // The scaling formula
            float scale = 1-((float)((double)Math.sqrt((distanceFromCenter/getWidth()))))*0.9f;

            // Set scale to view
            child.setScaleX(scale);
            child.setScaleY(scale);
            child.setAlpha(scale);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }


}
