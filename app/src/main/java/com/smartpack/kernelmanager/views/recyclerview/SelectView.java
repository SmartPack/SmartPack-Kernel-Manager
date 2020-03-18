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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class SelectView extends ValueView {

    public interface OnItemSelected {
        void onItemSelected(SelectView selectView, int position, String item);
    }

    private View mView;
    private OnItemSelected mOnItemSelected;
    private Dialog mDialog;
    private List<String> mItems = new ArrayList<>();

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void onCreateView(View view) {
        mView = view;
        super.onCreateView(view);
    }

    public void setItem(String item) {
        setValue(item);
    }

    public void setItem(int position) {
        if (position >= 0 && position < mItems.size()) {
            setValue(mItems.get(position));
        } else {
            setValue(R.string.not_in_range);
        }
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
    }

    private void showDialog(Context context) {
        String[] items = mItems.toArray(new String[mItems.size()]);

        mDialog = new Dialog(context).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setItem(which);
                if (mOnItemSelected != null) {
                    mOnItemSelected.onItemSelected(SelectView.this, which, mItems.get(which));
                }
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog = null;
            }
        });
        if (getTitle() != null) {
            mDialog.setTitle(getTitle());
        }
        mDialog.show();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mView != null && getValue() != null) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v.getContext());
                }
            });
        }
    }
}
