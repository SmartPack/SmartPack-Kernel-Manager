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
package com.grarak.kerneladiutor.views.recyclerview;

import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 28.09.17.
 */

public class ButtonView extends RecyclerViewItem {

    private AppCompatButton mButton;

    private String mText;
    private View.OnClickListener mOnClickListener;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_button;
    }

    @Override
    protected boolean cardCompatible() {
        return false;
    }

    @Override
    public void onCreateView(View view) {
        mButton = view.findViewById(R.id.btn);

        super.onCreateView(view);
        setup();
    }

    public void setText(String text) {
        mText = text;
        setup();
    }

    private void setup() {
        if (mButton != null) {
            mButton.setText(mText);
            mButton.setOnClickListener(mOnClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

}
