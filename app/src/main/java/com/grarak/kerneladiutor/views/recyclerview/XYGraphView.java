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
import com.grarak.kerneladiutor.views.XYGraph;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by willi on 12.05.16.
 */
public class XYGraphView extends RecyclerViewItem {

    private TextView mTitle;
    private TextView mText;
    private XYGraph mGraph;

    private CharSequence mTitleStr;
    private CharSequence mTextStr;
    private Queue<Integer> mPercentages = new LinkedBlockingQueue<>();

    @Override
    public int getLayoutRes() {
        return R.layout.rv_xygraph_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = view.findViewById(R.id.title);
        mText = view.findViewById(R.id.text);
        mGraph = view.findViewById(R.id.graph);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleStr = title;
        refresh();
    }

    public void setText(CharSequence text) {
        mTextStr = text;
        refresh();
    }

    public void addPercentage(int percentage) {
        mPercentages.offer(percentage);
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null && mTitleStr != null) {
            mTitle.setText(mTitleStr);
        }
        if (mText != null && mTextStr != null) {
            mText.setText(mTextStr);
        }
        if (mGraph != null) {
            while (mPercentages.size() != 0) {
                mGraph.addPercentage(mPercentages.poll());
            }
        }
    }
}
