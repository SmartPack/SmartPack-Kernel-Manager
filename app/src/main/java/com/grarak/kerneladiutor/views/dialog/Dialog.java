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
package com.grarak.kerneladiutor.views.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Created by willi on 07.11.16.
 */

public class Dialog extends AlertDialog.Builder {

    private DialogInterface.OnDismissListener mOnDismissListener;

    public Dialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public Dialog setTitle(CharSequence title) {
        return (Dialog) super.setTitle(title);
    }

    @Override
    public Dialog setTitle(int titleId) {
        return (Dialog) super.setTitle(titleId);
    }

    @Override
    public Dialog setMessage(CharSequence message) {
        return (Dialog) super.setMessage(message);
    }

    @Override
    public Dialog setMessage(int messageId) {
        return (Dialog) super.setMessage(messageId);
    }

    @Override
    public Dialog setView(int layoutResId) {
        return (Dialog) super.setView(layoutResId);
    }

    @Override
    public Dialog setView(View view) {
        return (Dialog) super.setView(view);
    }

    @Override
    public Dialog setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setItems(items, listener);
    }

    @Override
    public Dialog setItems(int itemsId, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setItems(itemsId, listener);
    }

    @Override
    public Dialog setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setPositiveButton(text, listener);
    }

    @Override
    public Dialog setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setPositiveButton(textId, listener);
    }

    @Override
    public Dialog setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setNegativeButton(text, listener);
    }

    @Override
    public Dialog setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        return (Dialog) super.setNegativeButton(textId, listener);
    }

    public Dialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(dialogInterface);
                }
            }
        });
        return this;
    }

    @Override
    public AlertDialog show() {
        try {
            AlertDialog dialog = create();
            dialog.setOnDismissListener(mOnDismissListener);
            dialog.show();
            return dialog;
        } catch (WindowManager.BadTokenException ignored) {
            return create();
        }
    }

}
