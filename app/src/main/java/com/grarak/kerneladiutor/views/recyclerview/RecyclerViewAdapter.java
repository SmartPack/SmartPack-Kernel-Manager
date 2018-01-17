/*
 * Copyright (C) 2015-2018 Willi Ye <williye97@gmail.com>
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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by willi on 17.04.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnViewChangedListener {
        void viewChanged();
    }

    private final List<RecyclerViewItem> mItems;
    private final Map<RecyclerViewItem, View> mViews = new HashMap<>();
    private OnViewChangedListener mOnViewChangedListener;
    private View mFirstItem;

    public RecyclerViewAdapter(List<RecyclerViewItem> items, OnViewChangedListener onViewChangedListener) {
        mItems = items;
        mOnViewChangedListener = onViewChangedListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mItems.get(position).onCreateView(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewItem item = mItems.get(viewType);
        View view;
        if (item.cacheable()) {
            if (mViews.containsKey(item)) {
                view = mViews.get(item);
            } else {
                mViews.put(item, view = LayoutInflater.from(parent.getContext())
                        .inflate(item.getLayoutRes(), parent, false));
            }
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(item.getLayoutRes(), parent, false);
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        if (item.cardCompatible()
                && Prefs.getBoolean("forcecards", false, view.getContext())) {
            android.support.v7.widget.CardView cardView = new android.support.v7.widget.CardView(view.getContext());
            cardView.setRadius(view.getResources().getDimension(R.dimen.cardview_radius));
            cardView.setCardElevation(view.getResources().getDimension(R.dimen.cardview_elevation));
            cardView.setUseCompatPadding(true);
            cardView.setFocusable(false);
            cardView.addView(view);
            view = cardView;
        }
        if (viewType == item.getLayoutRes()) {
            mFirstItem = view;
        }
        item.setOnViewChangeListener(mOnViewChangedListener);
        item.onCreateHolder(parent, view);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public View getFirstItem() {
        return mFirstItem;
    }

}
