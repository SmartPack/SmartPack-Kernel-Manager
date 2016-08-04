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

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 17.04.16.
 */
public class DescriptionView extends RecyclerViewItem {

    private View mRootView;
    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mSummaryView;

    private Drawable mImage;
    private CharSequence mTitle;
    private CharSequence mSummary;
    private MovementMethod mLinkMovementMethod;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_description_view;
    }

    @Override
    public void onCreateView(View view) {
        mRootView = view;
        mImageView = (ImageView) view.findViewById(R.id.image);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mSummaryView = (TextView) view.findViewById(R.id.summary);

        if (Utils.DARK_THEME) {
            mTitleView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }

        super.onCreateView(view);
    }

    public void setDrawable(Drawable drawable) {
        mImage = drawable;
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

    public void setMovementMethod(MovementMethod movementMethod) {
        mLinkMovementMethod = movementMethod;
        refresh();
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
        if (mRootView != null && getOnItemClickListener() != null && mTitleView != null
                && mSummaryView != null) {
            mTitleView.setTextIsSelectable(false);
            mSummaryView.setTextIsSelectable(false);
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getOnItemClickListener() != null) {
                        getOnItemClickListener().onClick(DescriptionView.this);
                    }
                }
            });
        }
    }
}
