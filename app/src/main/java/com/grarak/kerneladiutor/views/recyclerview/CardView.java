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
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 18.04.16.
 */
public class CardView extends RecyclerViewItem {

    public interface OnMenuListener {
        void onMenuReady(CardView cardView, PopupMenu popupMenu);
    }

    private Activity mActivity;

    private android.support.v7.widget.CardView mRootView;
    private TextView mTitle;
    private LinearLayout mLayout;
    private AppCompatImageButton mMenuButton;

    private CharSequence mTitleText;
    private PopupMenu mPopupMenu;
    private OnMenuListener mOnMenuListener;

    private List<RecyclerViewItem> mItems = new ArrayList<>();
    private HashMap<RecyclerViewItem, View> mViews = new HashMap<>();

    private List<RecyclerViewItem> mLoading = new ArrayList<>();
    private List<Runnable> mRunnables = new ArrayList<>();

    public CardView(Activity activity) {
        if (activity == null) {
            throw new IllegalStateException("Activity can't be null");
        }
        mActivity = activity;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_card_view;
    }

    @Override
    void onCreateHolder(ViewGroup parent, View view) {
        super.onCreateHolder(parent, view);
        mRootView = (android.support.v7.widget.CardView) view;
        mTitle = (TextView) view.findViewById(R.id.card_title);
        mLayout = (LinearLayout) view.findViewById(R.id.card_layout);
        setupLayout();
    }

    @Override
    public void onCreateView(View view) {
        mMenuButton = (AppCompatImageButton) view.findViewById(R.id.menu_button);
        mMenuButton.setRotation(90);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenu != null) {
                    mPopupMenu.show();
                }
            }
        });
        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void addItem(final RecyclerViewItem item) {
        if (item == this) {
            throw new IllegalStateException("Cardinception!");
        }
        mItems.add(item);
        addView(item);
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        mOnMenuListener = onMenuListener;
        refresh();
    }

    public int size() {
        return mItems.size();
    }

    public void removeItem(RecyclerViewItem item) {
        mItems.remove(item);
        if (mLayout != null) {
            mLayout.removeView(mViews.get(item));
        }
    }

    public void clearItems() {
        mRunnables.clear();
        mItems.clear();
        if (mLayout != null) {
            mLayout.removeAllViews();
        }
    }

    private void setupLayout() {
        if (mLayout != null) {
            mLayout.removeAllViews();
            for (final RecyclerViewItem item : mItems) {
                addView(item);
            }
        }
    }

    private void addView(final RecyclerViewItem item) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mLoading.contains(item)) {
                    return;
                }
                mLoading.add(item);
                View view = LayoutInflater.from(mActivity).inflate(item.getLayoutRes(), null, false);
                mViews.put(item, view);
                item.setOnViewChangeListener(getOnViewChangedListener());
                item.onCreateView(view);
                if (mLayout != null) {
                    mLayout.addView(view);
                }

                quit();
            }

            private void quit() {
                mLoading.remove(item);
                mRunnables.remove(this);
                if (mRunnables.size() > 0 && mRunnables.get(0) != null) {
                    mActivity.runOnUiThread(mRunnables.get(0));
                }
            }
        };
        mRunnables.add(runnable);
        if (mRunnables.size() == 1) {
            mActivity.runOnUiThread(mRunnables.get(0));
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (RecyclerViewItem item : mItems) {
            item.onDestroy();
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mMenuButton != null && mOnMenuListener != null) {
            mMenuButton.setVisibility(View.VISIBLE);
            mPopupMenu = new PopupMenu(mMenuButton.getContext(), mMenuButton);
            mOnMenuListener.onMenuReady(this, mPopupMenu);
        }
        if (mRootView != null && getOnItemClickListener() != null) {
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getOnItemClickListener().onClick(CardView.this);
                }
            });
        }
    }

    @Override
    protected boolean cardCompatible() {
        return false;
    }

}
