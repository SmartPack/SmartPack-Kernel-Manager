/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.elements.cards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CircleChart;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;

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
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

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
