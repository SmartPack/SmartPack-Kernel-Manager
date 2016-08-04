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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

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
    private ImageButton mMenuButton;

    private CharSequence mTitleText;
    private PopupMenu mPopupMenu;
    private OnMenuListener mOnMenuListener;

    private List<RecyclerViewItem> mItems = new ArrayList<>();
    private HashMap<RecyclerViewItem, View> mViews = new HashMap<>();

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
    public void onCreateView(View view) {
        mRootView = (android.support.v7.widget.CardView) view;
        mTitle = (TextView) view.findViewById(R.id.card_title);
        mLayout = (LinearLayout) view.findViewById(R.id.card_layout);
        mMenuButton = (ImageButton) view.findViewById(R.id.menu_button);

        mMenuButton.setRotation(90);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenu != null) {
                    mPopupMenu.show();
                }
            }
        });

        if (Utils.DARK_THEME) {
            Drawable drawable = mMenuButton.getDrawable();
            DrawableCompat.setTint(drawable, ContextCompat.getColor(view.getContext(), R.color.white));
            mMenuButton.setImageDrawable(drawable);
        }

        super.onCreateView(view);
        setupLayout();
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void addItem(final RecyclerViewItem item) {
        mItems.add(item);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mViews.containsKey(item)) {
                    mViews.put(item, LayoutInflater.from(mActivity).inflate(item.getLayoutRes(),
                            null, false));
                }
                setupLayout();
            }
        });
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
        mItems.clear();
        if (mLayout != null) {
            mLayout.removeAllViews();
        }
    }

    private void setupLayout() {
        if (mLayout != null) {
            mLayout.removeAllViews();
            for (final RecyclerViewItem item : mItems) {
                View view;
                if (mViews.containsKey(item)) {
                    view = mViews.get(item);
                } else {
                    mViews.put(item, view = LayoutInflater.from(mActivity).inflate(item.getLayoutRes(),
                            null, false));
                }
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(view);
                }
                item.setOnViewChangeListener(getOnViewChangedListener());
                item.onCreateView(view);
                mLayout.addView(view);
            }
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
        refresh();
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
            TypedArray typedArray = mRootView.getContext().obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
            mRootView.setForeground(typedArray.getDrawable(0));
            typedArray.recycle();
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getOnItemClickListener().onClick(CardView.this);
                }
            });
        }
    }
}
