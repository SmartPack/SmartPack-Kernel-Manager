/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.recyclerview;

import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 03, 2019
 *
 * Largely based on the original implementation of StatsView by Willi Ye
 */

public class MultiStatsView extends RecyclerViewItem {
    private TextView mTitleView;
    private TextView mStatViewOne;
    private TextView mStatViewTwo;
    private TextView mStatViewThree;

    private CharSequence mTitle;
    private CharSequence mStatOne;
    private CharSequence mStatTwo;
    private CharSequence mStatThree;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_multistats_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitleView = view.findViewById(R.id.title);
        mStatViewOne = view.findViewById(R.id.statsOne);
        mStatViewTwo = view.findViewById(R.id.statsTwo);
        mStatViewThree = view.findViewById(R.id.statsThree);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setStatsOne(CharSequence statsOne) {
        mStatOne = statsOne;
        refresh();
    }

    public void setmStatsTwo(CharSequence statsTwo) {
        mStatTwo = statsTwo;
        refresh();
    }

    public void setmStatsThree(CharSequence statsThree) {
        mStatThree = statsThree;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitleView != null && mTitle != null) {
            mTitleView.setText(mTitle);
        }
        if (mStatViewOne != null && mStatOne != null) {
            mStatViewOne.setText(mStatOne);
        }
        if (mStatViewTwo != null && mStatTwo != null) {
            mStatViewTwo.setText(mStatTwo);
        }
        if (mStatViewThree != null && mStatThree != null) {
            mStatViewThree.setText(mStatThree);
        }
    }
}
