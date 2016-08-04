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
import com.grarak.kerneladiutor.views.CircularTextView;

/**
 * Created by willi on 22.04.16.
 */
public class CircularText extends RecyclerViewItem {

    private CircularTextView mCircle;
    private TextView mTitleView;

    private String mMessage;
    private CharSequence mTitle;
    private int mColor;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_circluar_textview;
    }

    @Override
    public void onCreateView(View view) {
        mCircle = (CircularTextView) view.findViewById(R.id.circle);
        mTitleView = (TextView) view.findViewById(R.id.title);

        super.onCreateView(view);
    }

    public void setMessage(String messageListener) {
        mMessage = messageListener;
        refresh();
    }

    public void setTitle(CharSequence titleListener) {
        mTitle = titleListener;
        refresh();
    }

    public void setColor(int color) {
        mColor = color;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mCircle != null && mMessage != null) {
            mCircle.setText(mMessage);
        }
        if (mTitleView != null && mTitle != null) {
            mTitleView.setText(mTitle);
        }
        if (mCircle != null && mColor != 0) {
            mCircle.setColor(mColor);
        }
    }
}
