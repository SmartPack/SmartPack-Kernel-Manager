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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 01.07.16.
 */
public class EditTextView extends RecyclerViewItem {

    private TextView mTextView;
    private AppCompatEditText mEditTextView;

    private CharSequence mTitleText;
    private CharSequence mHintText;
    private int mInputType;
    private TextWatcher mTextWatcher;
    private CharSequence mText;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_edittext_view;
    }

    @Override
    public void onCreateView(View view) {
        mTextView = (TextView) view.findViewById(R.id.title);
        mEditTextView = (AppCompatEditText) view.findViewById(R.id.edittext);

        mEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mTextWatcher != null) {
                    mTextWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextWatcher != null) {
                    mTextWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTextWatcher != null) {
                    mTextWatcher.afterTextChanged(s);
                }
                mText = s;
            }
        });

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setHint(CharSequence hint) {
        mHintText = hint;
        refresh();
    }

    public void setInputType(int inputType) {
        mInputType = inputType;
        refresh();
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        mTextWatcher = textWatcher;
        refresh();
    }

    public void setText(CharSequence text) {
        mText = text;
        refresh();
    }

    public CharSequence getText() {
        return mEditTextView == null ? null : mEditTextView.getText();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTextView != null && mTitleText != null) {
            mTextView.setText(mTitleText);
        }

        if (mEditTextView != null) {
            if (mHintText != null) {
                mEditTextView.setHint(mHintText);
            }
            if (mInputType != 0) {
                mEditTextView.setInputType(mInputType);
            }
            if (mText != null) {
                mEditTextView.setText(mText);
                mEditTextView.setSelection(mText.length());
            }
        }
    }
}
