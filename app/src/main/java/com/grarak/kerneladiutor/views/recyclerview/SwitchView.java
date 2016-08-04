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
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 05.05.16.
 */
public class SwitchView extends RecyclerViewItem {

    public interface OnSwitchListener {
        void onChanged(SwitchView switchView, boolean isChecked);
    }

    private TextView mTitle;
    private TextView mSummary;
    private SwitchCompat mSwitcher;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private boolean mChecked;

    private List<OnSwitchListener> mOnSwitchListeners = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.rv_switch_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = (TextView) view.findViewById(R.id.title);
        mSummary = (TextView) view.findViewById(R.id.summary);
        mSwitcher = (SwitchCompat) view.findViewById(R.id.switcher);

        super.onCreateView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitcher.setChecked(!mChecked);
            }
        });
        mSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChecked = isChecked;
                List<OnSwitchListener> applied = new ArrayList<>();
                for (OnSwitchListener onSwitchListener : mOnSwitchListeners) {
                    if (applied.indexOf(onSwitchListener) == -1) {
                        onSwitchListener.onChanged(SwitchView.this, isChecked);
                        applied.add(onSwitchListener);
                    }
                }
            }
        });

        if (Utils.DARK_THEME) {
            mTitle.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void addOnSwitchListener(OnSwitchListener onSwitchListener) {
        mOnSwitchListeners.add(onSwitchListener);
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mSwitcher != null) {
            mSwitcher.setChecked(mChecked);
        }
    }
}
