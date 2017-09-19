/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.views.recyclerview.datasharing;

import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 22.09.17.
 */

public class DataSharingPageView extends RecyclerViewItem {

    public interface DataSharingPageListener {
        void onPrevious();

        void onNext();
    }

    private View mPrevious;
    private View mNext;
    private TextView mPageText;

    private boolean mShowPrevious;
    private boolean mShowNext;
    private int mPage;

    private DataSharingPageListener mDataSharingPageListener;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_datasharing_page;
    }

    @Override
    public void onCreateView(View view) {
        mPrevious = view.findViewById(R.id.previous_btn);
        mNext = view.findViewById(R.id.next_btn);
        mPageText = view.findViewById(R.id.page);

        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataSharingPageListener.onPrevious();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataSharingPageListener.onNext();
            }
        });

        setFullSpan(true);
        super.onCreateView(view);

        setup();
    }

    private void setup() {
        if (mPrevious != null) {
            mPrevious.setVisibility(mShowPrevious ? View.VISIBLE : View.INVISIBLE);
        }
        if (mNext != null) {
            mNext.setVisibility(mShowNext ? View.VISIBLE : View.INVISIBLE);
        }
        if (mPageText != null) {
            mPageText.setText(mPageText.getContext().getString(R.string.page) + " " + mPage);
        }
    }

    public void setDataSharingPageListener(DataSharingPageListener dataSharingListener) {
        mDataSharingPageListener = dataSharingListener;
    }

    public void showPrevious(boolean show) {
        mShowPrevious = show;
        setup();
    }

    public void showNext(boolean show) {
        mShowNext = show;
        setup();
    }

    public void setPage(int page) {
        mPage = page;
        setup();
    }

}
