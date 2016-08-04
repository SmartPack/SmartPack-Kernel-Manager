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

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 10.07.16.
 */
public class EditTextFragment extends BaseFragment {

    public static EditTextFragment newInstance(CharSequence title, TextWatcher textWatcher) {
        EditTextFragment fragment = new EditTextFragment();
        fragment.mTitleText = title;
        fragment.mTextWatcher = textWatcher;
        return fragment;
    }

    private TextView mTitle;
    private AppCompatEditText mEditText;

    private CharSequence mTitleText;
    private TextWatcher mTextWatcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edittext, container, false);

        mTitle = (TextView) rootView.findViewById(R.id.title);
        mEditText = (AppCompatEditText) rootView.findViewById(R.id.edittext);
        mEditText.getBackground().mutate().setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        refresh();

        return rootView;
    }

    private void refresh() {
        if (mTitle != null && mTitleText != null) {
            mTitle.setText(mTitleText);
        }
        if (mEditText != null && mTextWatcher != null) {
            mEditText.addTextChangedListener(mTextWatcher);
        }
    }

}
