/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
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
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 17.09.17.
 */

public class SwitcherFragment extends BaseFragment {

    public static SwitcherFragment newInstance(String title, String summary, boolean checked,
                                               CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        SwitcherFragment fragment = new SwitcherFragment();
        fragment.mTitle = title;
        fragment.mSummary = summary;
        fragment.mChecked = checked;
        fragment.mOnCheckedChangeListener = onCheckedChangeListener;
        return fragment;
    }

    private String mTitle;
    private String mSummary;
    private boolean mChecked;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    private SwitchCompat mSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switcher, container, false);
        if (mTitle != null) {
            ((TextView) view.findViewById(R.id.title)).setText(mTitle);
        }
        if (mSummary != null) {
            ((TextView) view.findViewById(R.id.summary)).setText(mSummary);
        }
        mSwitch = view.findViewById(R.id.switcher);
        mSwitch.setChecked(mChecked);
        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        return view;
    }

}
