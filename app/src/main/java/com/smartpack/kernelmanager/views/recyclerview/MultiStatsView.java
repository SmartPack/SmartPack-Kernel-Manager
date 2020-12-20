/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.views.recyclerview;

import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 03, 2019
 *
 * Largely based on the original implementation of StatsView by Willi Ye
 */
public class MultiStatsView extends RecyclerViewItem {

    private CharSequence mTitle, mProgressTitle, mStatOne, mStatTwo, mStatThree;
    private MaterialTextView mTitleView, mProgressTitleView, mStatViewOne, mStatViewTwo, mStatViewThree;
    private CircularProgressIndicator mProgressBar;
    private int mMax, mProgressPercent;
    private LinearLayout mProgressLayout;

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
        mProgressTitleView = view.findViewById(R.id.progress_title);
        mProgressBar = view.findViewById(R.id.progress);
        mProgressLayout = view.findViewById(R.id.progress_layout);
        LinearLayout mParentLayout = view.findViewById(R.id.parent_layout);

        if (Utils.getScreenDPI(view.getContext()) < 390) {
            mParentLayout.setOrientation(LinearLayout.VERTICAL);
        }

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

    public void setStatsTwo(CharSequence statsTwo) {
        mStatTwo = statsTwo;
        refresh();
    }

    public void setStatsThree(CharSequence statsThree) {
        mStatThree = statsThree;
        refresh();
    }

    public void setMax(int progress) {
        mMax = progress;
        refresh();
    }

    public void setProgress(int progress) {
        mProgressPercent = progress;
        refresh();
    }

    public void setProgressTitle(CharSequence text) {
        mProgressTitle = text;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitleView != null && mTitle != null) {
            mTitleView.setText(mTitle);
            mTitleView.setVisibility(View.VISIBLE);
        }
        if (mStatViewOne != null && mStatOne != null) {
            mStatViewOne.setText(mStatOne);
            mStatViewOne.setVisibility(View.VISIBLE);
        }
        if (mStatViewTwo != null && mStatTwo != null) {
            mStatViewTwo.setText(mStatTwo);
            mStatViewTwo.setVisibility(View.VISIBLE);
        }
        if (mStatViewThree != null && mStatThree != null) {
            mStatViewThree.setText(mStatThree);
            mStatViewThree.setVisibility(View.VISIBLE);
        }
        if (mProgressBar != null && mMax != 0) {
            mProgressBar.setMax(mMax);
        }
        if (mProgressBar != null && mProgressPercent != 0) {
            mProgressBar.setProgress(mProgressPercent);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressLayout.setVisibility(View.VISIBLE);
        }
        if (mProgressTitleView != null && mProgressTitle != null) {
            mProgressTitleView.setText(mProgressTitle);
            mProgressTitleView.setVisibility(View.VISIBLE);
        }
    }
}