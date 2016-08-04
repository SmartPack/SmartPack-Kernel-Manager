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
package com.grarak.kerneladiutor.views.recyclerview;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 22.06.16.
 */
public abstract class ValueView extends RecyclerViewItem {

    private TextView mTitleView;
    private TextView mSummaryView;
    private TextView mValueView;
    private View mProgress;

    private CharSequence mTitle;
    private CharSequence mSummary;
    private String mValue;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_value_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitleView = (TextView) view.findViewById(R.id.title);
        mSummaryView = (TextView) view.findViewById(R.id.summary);
        mValueView = (TextView) view.findViewById(R.id.value);
        mProgress = view.findViewById(R.id.progress);

        if (Utils.DARK_THEME) {
            mTitleView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }

    public void setValue(String value) {
        mValue = value;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }

        if (mSummaryView != null && mSummary != null) {
            mSummaryView.setText(mSummary);
        }

        if (mValueView != null && mValue != null) {
            mValueView.setText(mValue);
            mValueView.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }
    }

}
