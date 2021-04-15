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
package com.smartpack.kernelmanager.views.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Prefs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by willi on 17.04.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    public interface OnViewChangedListener {
        void viewChanged();
    }

    private final List<RecyclerViewItem> mItems;
    private final Map<RecyclerViewItem, View> mViews = new HashMap<>();
    private final OnViewChangedListener mOnViewChangedListener;
    private View mFirstItem;

    public RecyclerViewAdapter(List<RecyclerViewItem> items, OnViewChangedListener onViewChangedListener) {
        mItems = items;
        mOnViewChangedListener = onViewChangedListener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewItem item = mItems.get(position);
        if (item != null) {
            item.onCreateView(holder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

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
        assert view != null;
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        if (item.cardCompatible()
                && Prefs.getBoolean("forcecards", false, view.getContext())) {
            MaterialCardView cardView = new MaterialCardView(view.getContext());
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

    /**
     * Not quite sure why but returning 1 prevents
     * "halting" the drag event
     * @see <a href="https://stackoverflow.com/questions/38666255/itemtouchhelper-the-drop-is-forced-after-the-first-jumped-line">here</a>
     *
     * "Not returning {@param position}". I stand corrected.
     * So this is pretty much a TODO
     */
    @Override
    public int getItemViewType(int position) {
        if (containsDraggableView()) {
            return 1;
        }
        return position;
    }

    @Override
    public void onItemDismiss(int position) {
        try {
            RecyclerViewItem item = mItems.get(position);

            mViews.remove(item);
            mItems.remove(position);
            notifyItemRemoved(position);

            if (item instanceof SwipeableDescriptionView) {
                ((SwipeableDescriptionView) item).getOnItemSwipedListener()
                        .onItemSwiped(item, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        RecyclerViewItem item = mItems.get(toPosition);

        if (item instanceof SwipeableDescriptionView) {
            ((SwipeableDescriptionView) item).getOnItemDragListener()
                    .onItemDrag(item, fromPosition, toPosition);
        }
        return true;
    }

    public View getFirstItem() {
        return mFirstItem;
    }

    private boolean containsDraggableView() {
        for (RecyclerViewItem item : mItems) {
            if (item instanceof SwipeableDescriptionView) {
                return true;
            }
        }
        return false;
    }

}