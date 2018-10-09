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

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by willi on 24.04.16.
 */
public abstract class RecyclerViewItem {

    private boolean mFullspan;
    private View mView;

    public interface OnItemClickListener {
        void onClick(RecyclerViewItem item);
    }

    private OnItemClickListener mOnItemClickListener;
    private RecyclerViewAdapter.OnViewChangedListener mOnViewChangedListener;

    public void onCreateView(View view) {
        mView = view;
        fullSpan(mFullspan);
        refresh();
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public void onRecyclerViewCreate(Activity activity) {
    }

    void onCreateHolder(ViewGroup parent, View view) {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    void setOnViewChangeListener(RecyclerViewAdapter.OnViewChangedListener onViewChangeListener) {
        mOnViewChangedListener = onViewChangeListener;
    }

    protected OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    RecyclerViewAdapter.OnViewChangedListener getOnViewChangedListener() {
        return mOnViewChangedListener;
    }

    void viewChanged() {
        if (mOnViewChangedListener != null) {
            mOnViewChangedListener.viewChanged();
        }
    }

    public void setFullSpan(boolean fullspan) {
        mFullspan = fullspan;
        fullSpan(fullspan);
    }

    private void fullSpan(boolean fullspan) {
        if (mView != null) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(fullspan);
            mView.setLayoutParams(layoutParams);
        }
    }

    protected void refresh() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    protected boolean cardCompatible() {
        return true;
    }

    boolean cacheable() {
        return false;
    }

}
