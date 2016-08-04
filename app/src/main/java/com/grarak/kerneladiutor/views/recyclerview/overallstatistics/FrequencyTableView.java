/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.views.recyclerview.overallstatistics;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 29.04.16.
 */
public class FrequencyTableView extends RecyclerViewItem {

    private TextView mFrequency;
    private TextView mPercentage;
    private TextView mDuration;
    private ProgressBar mProgress;

    private CharSequence mFreqText;
    private CharSequence mDurationText;
    private int mUsage = -1;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_frequencytable_view;
    }

    @Override
    public void onCreateView(View view) {
        mFrequency = (TextView) view.findViewById(R.id.frequency);
        mPercentage = (TextView) view.findViewById(R.id.percentage);
        mDuration = (TextView) view.findViewById(R.id.duration);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);

        if (Utils.DARK_THEME) {
            mFrequency.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }

        super.onCreateView(view);
    }

    public void setFrequency(CharSequence frequency) {
        mFreqText = frequency;
        refresh();
    }

    public void setPercentage(int percentage) {
        mUsage = percentage;
        refresh();
    }

    public void setDuration(CharSequence duration) {
        mDurationText = duration;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mFreqText != null && mFrequency != null) {
            mFrequency.setText(mFreqText);
        }
        if (mDurationText != null && mDuration != null) {
            mDuration.setText(mDurationText);
        }
        if (mUsage > -1 && mPercentage != null && mProgress != null) {
            mPercentage.setText(Utils.strFormat("%d%%", mUsage));
            mProgress.setProgress(mUsage);
        }
    }
}
