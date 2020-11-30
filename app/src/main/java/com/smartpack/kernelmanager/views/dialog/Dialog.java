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
package com.smartpack.kernelmanager.views.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Created by willi on 07.11.16.
 */

public class Dialog extends MaterialAlertDialogBuilder {

    private DialogInterface.OnDismissListener mOnDismissListener;

    public Dialog(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public Dialog setTitle(CharSequence title) {
        return (Dialog) super.setTitle(title);
    }

    @NonNull
    @Override
    public Dialog setTitle(int titleId) {
        return (Dialog) super.setTitle(titleId);
    }

    @NonNull
    @Override
    public Dialog setMessage(CharSequence message) {
        return (Dialog) super.setMessage(message);
    }

    @NonNull
    @Override
    public Dialog setMessage(int messageId) {
        return (Dialog) super.setMessage(messageId);
    }

    @NonNull
    @Override
    public Dialog setView(int layoutResId) {
        return (Dialog) super.setView(layoutResId);
    }

    @NonNull
    @Override
    public Dialog setView(View view) {
        return (Dialog) super.setView(view);
    }

    @NonNull
    @Override
    public Dialog setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setItems(items, listener);
    }

    @NonNull
    @Override
    public Dialog setItems(int itemsId, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setItems(itemsId, listener);
    }

    @NonNull
    @Override
    public Dialog setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setPositiveButton(text, listener);
    }

    @NonNull
    @Override
    public Dialog setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setPositiveButton(textId, listener);
    }

    @NonNull
    @Override
    public Dialog setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setNegativeButton(text, listener);
    }

    @NonNull
    @Override
    public Dialog setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setNegativeButton(textId, listener);
    }

    @NonNull
    @Override
    public Dialog setMultiChoiceItems(int itemsId, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener){
        return (Dialog) super.setMultiChoiceItems(itemsId, checkedItems, listener);
    }

    @NonNull
    @Override
    public Dialog setMultiChoiceItems(Cursor cursor, @NonNull String isCheckedColumn, @NonNull String labelColumn, DialogInterface.OnMultiChoiceClickListener listener){
        return (Dialog) super.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
    }

    @NonNull
    @Override
    public Dialog
    setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener){
        return (Dialog) super.setMultiChoiceItems(items, checkedItems, listener);
    }

    @NonNull
    public Dialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        setOnCancelListener(dialogInterface -> {
            if (mOnDismissListener != null) {
                mOnDismissListener.onDismiss(dialogInterface);
            }
        });
        return this;
    }

}
