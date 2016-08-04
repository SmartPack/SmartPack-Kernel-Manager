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
package com.grarak.kerneladiutor.views.recyclerview.customcontrols;

import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 02.07.16.
 */
public class ErrorView extends RecyclerViewItem {

    private TextView mText;
    private Exception mError;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_error_view;
    }

    @Override
    public void onCreateView(View view) {
        mText = (TextView) view.findViewById(R.id.text);

        super.onCreateView(view);
    }

    public void setException(Exception error) {
        mError = error;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mText != null && mError != null) {
            mText.setText(mError.getMessage());
        }
    }
}
