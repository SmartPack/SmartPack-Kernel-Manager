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
package com.smartpack.kernelmanager.views.recyclerview;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.method.MovementMethod;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;

/**
 * Created by willi on 17.04.16.
 */
public class DescriptionView extends RecyclerViewItem {

    public interface OnMenuListener {
        void onMenuReady(DescriptionView descriptionView, PopupMenu popupMenu);
    }

    private View mRootView;
    private AppCompatImageButton mMenuIconView;
    private AppCompatImageView mImageView, mIndicatorView;
    private MaterialTextView mTitleView, mSummaryView;

    private Drawable mImage, mIndicator, mMenuIcon;
    private CharSequence mTitle, mSummary;
    private MovementMethod mLinkMovementMethod;
    private PopupMenu mPopupMenu;
    private OnMenuListener mOnMenuListener;

    private boolean mGrxIsInitSelected = false;
    private int mGrxColor = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_description_view;
    }

    @Override
    public void onCreateView(View view) {
        mRootView = view;
        mImageView = view.findViewById(R.id.image);
        mIndicatorView = view.findViewById(R.id.indicator);

        mTitleView = view.findViewById(R.id.title);
        if (mTitleView != null) {
            mTitleView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    mRootView.requestFocus();
                }
            });
        }

        mSummaryView = view.findViewById(R.id.summary);
        if (mSummaryView != null) {
            mSummaryView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    mRootView.requestFocus();
                }
            });
        }

        if(mGrxIsInitSelected) this.setTextColor(mGrxColor);

        mMenuIconView = view.findViewById(R.id.menu_button);
        if (mMenuIconView != null) {
            mMenuIconView.setOnClickListener(v -> {
                if (mPopupMenu != null) {
                    mPopupMenu.show();
                }
            });
        }

        super.onCreateView(view);
    }

    public void setDrawable(Drawable drawable) {
        mImage = drawable;
        refresh();
    }

    public void setIndicator(Drawable indicatorDrawable) {
        mIndicator = indicatorDrawable;
        refresh();
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }

    public void setMenuIcon(Drawable menuIcon) {
        mMenuIcon = menuIcon;
        refresh();
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        mOnMenuListener = onMenuListener;
        refresh();
    }

    public void GrxSetInitSelection(boolean isInitSelected, int color ){
        mGrxIsInitSelected = isInitSelected;
        mGrxColor = color;
    }

    public void setTextColor(int color) {
        mSummaryView.setTextColor(color);
    }

    public void setTextColor(ColorStateList color) {
        mSummaryView.setTextColor(color);
    }

    public ColorStateList getTextColors(){
        return mSummaryView.getTextColors();
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public CharSequence getSummary() {
        return mSummary;
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mImageView != null && mImage != null) {
            mImageView.setImageDrawable(mImage);
            mImageView.setVisibility(View.VISIBLE);
        }
        if (mIndicatorView != null && mIndicator != null) {
            mIndicatorView.setImageDrawable(mIndicator);
            mIndicatorView.setVisibility(View.VISIBLE);
        }
        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }
        if (mSummaryView != null && mSummary != null) {
            mSummaryView.setText(mSummary);
            if (mLinkMovementMethod != null) {
                mSummaryView.setMovementMethod(mLinkMovementMethod);
            }
        }
        if (mMenuIconView != null && mMenuIcon != null && mOnMenuListener != null) {
            mMenuIconView.setImageDrawable(mMenuIcon);
            mMenuIconView.setVisibility(View.VISIBLE);
            mPopupMenu = new PopupMenu(mMenuIconView.getContext(), mMenuIconView);
            mOnMenuListener.onMenuReady(this, mPopupMenu);
        }
        if (mRootView != null && getOnItemClickListener() != null && mTitleView != null
                && mSummaryView != null) {
            mTitleView.setTextIsSelectable(false);
            mSummaryView.setTextIsSelectable(false);
            mRootView.setOnClickListener(v -> {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onClick(DescriptionView.this);
                }
            });
        }
    }

}