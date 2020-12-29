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

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 12, 2020
 */
public class CircularProgressView extends RecyclerViewItem {

    private CharSequence mTitleTextLeft, mHeadingTextOneLeft, mHeadingTextTwoLeft,
            mSummaryTextOneLeft, mSummaryTextTwoLeft, mTitleTextRight, mHeadingTextOneRight,
            mHeadingTextTwoRight, mSummaryTextOneRight, mSummaryTextTwoRight;
    private int mMaxLeft, mProgressLeft, mMaxRight, mProgressRight;
    private MaterialTextView mTitleLeft, mHeadingOneLeft, mHeadingTwoLeft, mSummaryOneLeft,
            mSummaryTwoLeft, mTitleRight, mHeadingOneRight, mHeadingTwoRight, mSummaryOneRight,
            mSummaryTwoRight;
    private CircularProgressIndicator mProgressBarLeft, mProgressBarRight;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_circular_progress_view;
    }

    @Override
    public void onCreateView(View view) {
        mProgressBarLeft = view.findViewById(R.id.progressLeft);
        mProgressBarRight = view.findViewById(R.id.progressRight);
        mTitleLeft = view.findViewById(R.id.titleLeft);
        mTitleRight = view.findViewById(R.id.titleRight);
        mHeadingOneLeft = view.findViewById(R.id.heading_oneLeft);
        mHeadingTwoLeft = view.findViewById(R.id.heading_twoLeft);
        mHeadingOneRight = view.findViewById(R.id.heading_oneRight);
        mHeadingTwoRight = view.findViewById(R.id.heading_twoRight);
        mSummaryOneLeft = view.findViewById(R.id.summary_oneLeft);
        mSummaryTwoLeft = view.findViewById(R.id.summary_twoLeft);
        mSummaryOneRight = view.findViewById(R.id.summary_oneRight);
        mSummaryTwoRight = view.findViewById(R.id.summary_twoRight);
        LinearLayout mParentLayout = view.findViewById(R.id.parent_layout);

        if (Utils.getScreenDPI(view.getContext()) < 390 || Utils.isTablet(view.getContext()) &&
                Utils.getScreenDPI(view.getContext()) < 500) {
            mParentLayout.setOrientation(LinearLayout.VERTICAL);
        }
        super.onCreateView(view);
    }

    public void setMaxLeft(int progress) {
        mMaxLeft = progress;
        refresh();
    }

    public void setProgressLeft(int progress) {
        mProgressLeft = progress;
        refresh();
    }

    public void setMaxRight(int progress) {
        mMaxRight = progress;
        refresh();
    }

    public void setProgressRight(int progress) {
        mProgressRight = progress;
        refresh();
    }

    public void setTitleLeft(CharSequence text) {
        mTitleTextLeft = text;
        refresh();
    }

    public void setTitleRight(CharSequence text) {
        mTitleTextRight = text;
        refresh();
    }

    public void setHeadingOneLeft(CharSequence text) {
        mHeadingTextOneLeft = text;
        refresh();
    }

    public void setHeadingTwoLeft(CharSequence text) {
        mHeadingTextTwoLeft = text;
        refresh();
    }

    public void setHeadingOneRight(CharSequence text) {
        mHeadingTextOneRight = text;
        refresh();
    }

    public void setHeadingTwoRight(CharSequence text) {
        mHeadingTextTwoRight= text;
        refresh();
    }

    public void setSummaryOneLeft(CharSequence text) {
        mSummaryTextOneLeft = text;
        refresh();
    }

    public void setSummaryTwoLeft(CharSequence text) {
        mSummaryTextTwoLeft = text;
        refresh();
    }

    public void setSummaryOneRight(CharSequence text) {
        mSummaryTextOneRight = text;
        refresh();
    }

    public void setSummaryTwoRight(CharSequence text) {
        mSummaryTextTwoRight = text;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mProgressBarLeft != null && mMaxLeft != 0) {
            mProgressBarLeft.setMax(mMaxLeft);
        }
        if (mProgressBarLeft != null && mProgressLeft != 0) {
            mProgressBarLeft.setProgress(mProgressLeft);
            mProgressBarLeft.setVisibility(View.VISIBLE);
        }
        if (mProgressBarRight != null && mMaxRight != 0) {
            mProgressBarRight.setMax(mMaxRight);
        }
        if (mProgressBarRight != null && mProgressRight != 0) {
            mProgressBarRight.setProgress(mProgressRight);
            mProgressBarRight.setVisibility(View.VISIBLE);
        }
        if (mTitleLeft != null && mTitleTextLeft != null) {
            mTitleLeft.setText(mTitleTextLeft);
            mTitleLeft.setVisibility(View.VISIBLE);
        }
        if (mTitleRight != null && mTitleTextRight != null) {
            mTitleRight.setText(mTitleTextRight);
            mTitleRight.setVisibility(View.VISIBLE);
        }
        if (mHeadingOneLeft != null && mHeadingTextOneLeft != null) {
            mHeadingOneLeft.setText(mHeadingTextOneLeft);
            mHeadingOneLeft.setVisibility(View.VISIBLE);
        }
        if (mHeadingTwoLeft != null && mHeadingTextTwoLeft != null) {
            mHeadingTwoLeft.setText(mHeadingTextTwoLeft);
            mHeadingTwoLeft.setVisibility(View.VISIBLE);
        }
        if (mHeadingOneRight != null && mHeadingTextOneRight != null) {
            mHeadingOneRight.setText(mHeadingTextOneRight);
            mHeadingOneRight.setVisibility(View.VISIBLE);
        }
        if (mHeadingTwoRight != null && mHeadingTextTwoRight != null) {
            mHeadingTwoRight.setText(mHeadingTextTwoRight);
            mHeadingTwoRight.setVisibility(View.VISIBLE);
        }
        if (mSummaryOneLeft != null && mSummaryTextOneLeft != null) {
            mSummaryOneLeft.setText(mSummaryTextOneLeft);
            mSummaryOneLeft.setVisibility(View.VISIBLE);
        }
        if (mSummaryTwoLeft != null && mSummaryTextTwoLeft != null) {
            mSummaryTwoLeft.setText(mSummaryTextTwoLeft);
            mSummaryTwoLeft.setVisibility(View.VISIBLE);
        }
        if (mSummaryOneRight != null && mSummaryTextOneRight != null) {
            mSummaryOneRight.setText(mSummaryTextOneRight);
            mSummaryOneRight.setVisibility(View.VISIBLE);
        }
        if (mSummaryTwoRight != null && mSummaryTextTwoRight != null) {
            mSummaryTwoRight.setText(mSummaryTextTwoRight);
            mSummaryTwoRight.setVisibility(View.VISIBLE);
        }
    }

}