package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 05.02.15.
 */
public class UsageCardView extends BaseCardView {

    private CircleChart circleChart;
    private int progress;

    public UsageCardView(Context context) {
        super(context, R.layout.usage_cardview);
    }

    @Override
    public void setUpInnerLayout(View view) {
        circleChart = (CircleChart) view.findViewById(R.id.circle_chart);
        circleChart.setProgress(progress);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (circleChart != null) circleChart.setProgress(progress);
    }

    public static class DUsageCard implements DAdapter.DView {

        private UsageCardView usageCardView;
        private int progress;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            usageCardView = (UsageCardView) viewHolder.itemView;
            usageCardView.setProgress(progress);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new UsageCardView(viewGroup.getContext())) {
            };
        }

        public void setProgress(int progress) {
            this.progress = progress;
            if (usageCardView != null) usageCardView.setProgress(progress);
        }

    }

}
