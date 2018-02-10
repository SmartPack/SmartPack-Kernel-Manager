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

import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 23.08.16.
 */

public class StatsView extends RecyclerViewItem {

    private TextView mStatView;
    private TextView mTitleView;

    private CharSequence mStat;
    private CharSequence mTitle;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_stats_view;
    }

    @Override
    public void onCreateView(View view) {
        mStatView = view.findViewById(R.id.stat);
        mTitleView = view.findViewById(R.id.title);

        super.onCreateView(view);
    }

    public void setStat(CharSequence stat) {
        mStat = stat;
        refresh();
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mStatView != null && mStat != null) {
            mStatView.setText(mStat);
        }
        if (mTitleView != null && mTitle != null) {
            mTitleView.setText(mTitle);
        }
    }

}
