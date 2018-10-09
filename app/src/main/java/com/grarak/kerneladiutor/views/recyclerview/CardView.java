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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;

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

    private androidx.cardview.widget.CardView mRootView;
    private View mTitleParent;
    private TextView mTitle;
    private AppCompatImageView mArrow;
    private View mLayoutParent;
    private LinearLayout mLayout;
    private View mMenuButton;

    private CharSequence mTitleText;
    private PopupMenu mPopupMenu;
    private OnMenuListener mOnMenuListener;

    private List<RecyclerViewItem> mItems = new ArrayList<>();
    private HashMap<RecyclerViewItem, View> mViews = new HashMap<>();

    private List<RecyclerViewItem> mLoading = new ArrayList<>();
    private List<Runnable> mRunnables = new ArrayList<>();

    private int mLayoutHeight;
    private ValueAnimator mLayoutAnimator;
    private boolean mShowLayout = true;
    private boolean mExpandable = true;

    private boolean mGrxIsInitSelected = false;
    private int mGrxColor = 0;

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
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        for (RecyclerViewItem item : mItems) {
            item.onRecyclerViewCreate(activity);
        }
    }

    private void initLayouts(View view) {
        mRootView = (androidx.cardview.widget.CardView) view;
        mTitleParent = view.findViewById(R.id.title_parent);
        mTitle = view.findViewById(R.id.card_title);
        mArrow = view.findViewById(R.id.arrow_image);
        mLayoutParent = view.findViewById(R.id.layout_parent);
        mLayout = view.findViewById(R.id.card_layout);
        if(mGrxIsInitSelected) this.setCardBackgroundColor(mGrxColor);
    }

    @Override
    void onCreateHolder(ViewGroup parent, View view) {
        super.onCreateHolder(parent, view);
        initLayouts(view);
        setupLayout();
    }

    @Override
    public void onCreateView(View view) {
        initLayouts(view);

        mMenuButton = view.findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenu != null) {
                    mPopupMenu.show();
                }
            }
        });

        mLayoutParent.setVisibility(mShowLayout ? View.VISIBLE : View.GONE);
        mArrow.setRotationX(mShowLayout ? 0 : 180);

        if(mExpandable) {
            mTitleParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLayoutParent.getVisibility() == View.VISIBLE) {
                        mLayoutHeight = mLayoutParent.getHeight();
                    }
                    if (mLayoutAnimator == null) {
                        mShowLayout = !mShowLayout;
                        animateLayout(!mShowLayout);
                        viewChanged();
                    }
                }
            });
        }
        super.onCreateView(view);
    }

    private void animateLayout(final boolean collapse) {
        mArrow.animate().rotationX(collapse ? 180 : 0).setDuration(500).start();
        mLayoutAnimator = ValueAnimator.ofInt(collapse ? mLayoutHeight : 0, collapse ? 0 : mLayoutHeight);
        mLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParentHeight((int) animation.getAnimatedValue());
            }
        });
        mLayoutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLayoutParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLayoutParent.setVisibility(collapse ? View.GONE : View.VISIBLE);
                setLayoutParentHeight(collapse ? 0 : ViewGroup.LayoutParams.MATCH_PARENT);
                mLayoutAnimator = null;
            }
        });
        mLayoutAnimator.setDuration(500);
        mLayoutAnimator.start();
    }

    private void setLayoutParentHeight(int height) {
        ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
        layoutParams.height = height;
        mLayout.requestLayout();
        viewChanged();
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setExpandable(boolean expandable) {
        mExpandable = expandable;
        refresh();
    }

    public void GrxSetInitSelection(boolean isInitSelected, int color ){
        mGrxIsInitSelected = isInitSelected;
        mGrxColor = color;
    }

    public void setCardBackgroundColor(int color){
        mRootView.setCardBackgroundColor(color);
    }

    public void setCardBackgroundColor (ColorStateList color){
        mRootView.setCardBackgroundColor(color);
    }

    public ColorStateList getCardBackgroundColor(){
        return mRootView.getCardBackgroundColor();
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
                mTitleParent.setVisibility(View.VISIBLE);
                if (mLayoutParent != null) {
                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) mLayout.getLayoutParams();
                    layoutParams.topMargin = -mLayout.getPaddingLeft();
                    mLayout.requestLayout();
                    mLayout.setPadding(mLayout.getPaddingLeft(), 0,
                            mLayout.getPaddingRight(), mLayout.getPaddingBottom());
                }
            } else {
                mTitleParent.setVisibility(View.GONE);
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
