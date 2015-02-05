package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 05.02.15.
 */
public class UsageCardView extends BaseCardView {

    private CircleChart circleChart;
    private TextView textView;

    private int progress;
    private String text;

    public UsageCardView(Context context) {
        super(context, R.layout.usage_cardview);
    }

    @Override
    public void setUpInnerLayout(View view) {
        circleChart = (CircleChart) view.findViewById(R.id.circle_chart);
        circleChart.setProgress(progress);

        textView = (TextView) view.findViewById(R.id.text);
        if (text != null) textView.setText(text);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (circleChart != null) circleChart.setProgress(progress);
    }

    public void setText(String text) {
        this.text = text;
        if (textView != null) textView.setText(text);
    }

    public static class DUsageCard implements DAdapter.DView {

        private UsageCardView usageCardView;

        private int progress;
        private String text;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            usageCardView = (UsageCardView) viewHolder.itemView;
            usageCardView.setProgress(progress);
            if (text != null) usageCardView.setText(text);
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

        public void setText(String text) {
            this.text = text;
            if (usageCardView != null) usageCardView.setText(text);
        }

    }

}
