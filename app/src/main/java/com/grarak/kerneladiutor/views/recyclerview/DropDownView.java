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

import android.animation.ValueAnimator;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 04.06.16.
 */
public class DropDownView extends RecyclerViewItem {

    private TextView mTitle;
    private TextView mSummary;
    private ImageView mArrow;
    private LinearLayout mParent;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private List<String> mItems;
    private int mSelection = -1;
    private boolean mExpanded;

    private List<View> mDoneViews = new ArrayList<>();

    private float mItemHeight;
    private ValueAnimator mAnimator;

    private OnDropDownListener mOnDropDownListener;

    public interface OnDropDownListener {
        void onSelect(DropDownView dropDownView, int position, String value);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_drop_down_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = (TextView) view.findViewById(R.id.title);
        mSummary = (TextView) view.findViewById(R.id.summary);
        mArrow = (ImageView) view.findViewById(R.id.arrow_image);
        mParent = (LinearLayout) view.findViewById(R.id.parent_layout);

        mItemHeight = view.getResources().getDimension(R.dimen.rv_drop_down_item_height);

        mArrow.setRotationX(mExpanded ? 0 : 180);
        setHeight(mExpanded && mItems != null ? Math.round(mItemHeight * mItems.size()) : 0);

        view.findViewById(R.id.title_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });

        if (Utils.DARK_THEME) {
            mTitle.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setSelection(int selection) {
        mSelection = selection;
        refresh();
    }

    public void setOnDropDownListener(OnDropDownListener onDropDownListener) {
        mOnDropDownListener = onDropDownListener;
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
        if (mSummary != null) {
            if (mSummaryText != null) {
                mSummary.setText(mSummaryText);
                mSummary.setVisibility(View.VISIBLE);
            } else {
                mSummary.setVisibility(View.GONE);
            }
        }

        if (mParent != null && mItems != null) {
            mParent.removeAllViews();
            mDoneViews.clear();
            for (int i = 0; i < mItems.size(); i++) {
                View item = LayoutInflater.from(mParent.getContext()).inflate(R.layout.rv_drop_down_item_view,
                        mParent, false);
                ((TextView) item.findViewById(R.id.title)).setText(mItems.get(i));
                mDoneViews.add(item.findViewById(R.id.done_image));
                item.findViewById(R.id.done_image).setVisibility(View.GONE);

                final int position = i;
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelection = position;
                        for (int i = 0; i < mDoneViews.size(); i++) {
                            mDoneViews.get(i).setVisibility(position == i ? View.VISIBLE : View.INVISIBLE);
                        }
                        if (mOnDropDownListener != null) {
                            mOnDropDownListener.onSelect(DropDownView.this, position, mItems.get(position));
                        }
                    }
                });
                mParent.addView(item);
            }
            if (mSelection >= 0 && mSelection < mDoneViews.size()) {
                mDoneViews.get(mSelection).setVisibility(View.VISIBLE);
            }
        }
    }

    public void expand() {
        mExpanded = true;
        if (mArrow != null) {
            mArrow.animate().rotationX(0).setDuration(500).start();
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            if (mItems == null) return;
            mAnimator = ValueAnimator.ofFloat(0, mItemHeight * mItems.size());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setHeight(Math.round((float) animation.getAnimatedValue()));
                }
            });
            mAnimator.setDuration(500);
            mAnimator.start();
        }
    }

    public void collapse() {
        mExpanded = false;
        if (mArrow != null) {
            mArrow.animate().rotationX(180).setDuration(500).start();
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            if (mItems == null) return;
            mAnimator = ValueAnimator.ofFloat(mItemHeight * mItems.size(), 0);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setHeight(Math.round((float) animation.getAnimatedValue()));
                }
            });
            mAnimator.setDuration(500);
            mAnimator.start();
        }
    }

    private void setHeight(int height) {
        if (mParent != null) {
            ViewGroup.LayoutParams params = mParent.getLayoutParams();
            params.height = height;
            mParent.requestLayout();
            viewChanged();
        }
    }

}
