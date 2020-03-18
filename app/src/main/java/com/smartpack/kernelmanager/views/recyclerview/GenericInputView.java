/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.views.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.ViewUtils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 21, 2020
 */

public class GenericInputView extends InputValueView {

    public interface OnGenericValueListener {
        void onGenericValueSelected(GenericInputView genericSelectView, String value);
    }

    public interface OnMenuListener {
        void onMenuReady(GenericInputView cardView, PopupMenu popupMenu);
    }

    private View mMenuButton;

    private String mValueRaw;
    private OnGenericValueListener mOnGenericValueListener;
    private PopupMenu mPopupMenu;
    private OnMenuListener mOnMenuListener;
    private int mInputType = -1;
    private boolean mShowDialog;

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mShowDialog) {
            showDialog(activity);
        }
    }

    @Override
    public void onCreateView(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v.getContext());
            }
        });

        mMenuButton = view.findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(v -> {
            if (mPopupMenu != null) {
                mPopupMenu.show();
            }
        });
        super.onCreateView(view);
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        mOnMenuListener = onMenuListener;
        refresh();
    }

    public void setValueRaw(String value) {
        mValueRaw = value;
    }

    public void setOnGenericValueListener(OnGenericValueListener onGenericValueListener) {
        mOnGenericValueListener = onGenericValueListener;
    }

    public void setInputType(int inputType) {
        mInputType = inputType;
    }

    private void showDialog(Context context) {
        if (mValueRaw == null) {
            mValueRaw = getValue();
        }
        if (mValueRaw == null) return;

        mShowDialog = true;
        ViewUtils.dialogEditText(mValueRaw, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }, new ViewUtils.OnDialogEditTextListener() {
            @Override
            public void onClick(String text) {
                setValueRaw(text);
                if (mOnGenericValueListener != null) {
                    mOnGenericValueListener.onGenericValueSelected(GenericInputView.this, text);
                }
            }
        }, mInputType, context).setTitle(getTitle()).setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mShowDialog = false;
                    }
                }).show();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mMenuButton != null && mOnMenuListener != null) {
            mMenuButton.setVisibility(View.VISIBLE);
            mPopupMenu = new PopupMenu(mMenuButton.getContext(), mMenuButton);
            mOnMenuListener.onMenuReady(this, mPopupMenu);
        }
    }

}