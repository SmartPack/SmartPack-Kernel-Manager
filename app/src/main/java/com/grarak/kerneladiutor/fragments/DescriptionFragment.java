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
package com.grarak.kerneladiutor.fragments;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 01.05.16.
 */
public class DescriptionFragment extends BaseFragment {

    public static DescriptionFragment newInstance(CharSequence title, CharSequence summary) {
        Bundle args = new Bundle();
        DescriptionFragment fragment = new DescriptionFragment();
        args.putCharSequence("title", title);
        args.putCharSequence("summary", summary);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private TextView mSummaryView;

    private CharSequence mTitle;
    private CharSequence mSummary;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.title);
        mSummaryView = (TextView) rootView.findViewById(R.id.summary);

        if (Utils.isTv(getActivity())) {
            mSummaryView.setFocusable(true);
        } else {
            mTitleView.setTextIsSelectable(true);
            mSummaryView.setTextIsSelectable(true);
        }

        mSummaryView.setSelected(true);
        mSummaryView.setMovementMethod(LinkMovementMethod.getInstance());

        mTitle = getArguments().getCharSequence("title");
        mSummary = getArguments().getCharSequence("summary");

        refresh();
        return rootView;
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }

    private void refresh() {
        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setFocusable(false);
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }

        if (mSummaryView != null) {
            if (mSummary != null) {
                mSummaryView.setText(mSummary);
                mSummaryView.setVisibility(View.VISIBLE);
            } else {
                mSummaryView.setVisibility(View.GONE);
            }
        }
    }
}
