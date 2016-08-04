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
package com.grarak.kerneladiutor.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 16.04.16.
 */
public class ViewUtils {

    public interface OnDialogEditTextListener {
        void onClick(String text);
    }

    public interface onDialogEditTextsListener {
        void onClick(String text, String text2);
    }

    public static AlertDialog.Builder dialogDonate(final Context context) {
        return new AlertDialog.Builder(context).setTitle(context.getString(R.string.donate))
                .setMessage(context.getString(R.string.donate_summary)).setNegativeButton(
                        context.getString(R.string.donate_nope), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setPositiveButton(context.getString(R.string.donate_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.launchUrl(
                                "https://play.google.com/store/apps/details?id=com.grarak.kerneladiutordonate", context);
                    }
                });
    }

    public static AlertDialog.Builder dialogEditTexts(String text, String text2, String hint, String hint2,
                                                      final DialogInterface.OnClickListener negativeListener,
                                                      final onDialogEditTextsListener onDialogEditTextListener,
                                                      Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_edittext_padding);
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (text != null) {
            editText.setText(text);
        }
        if (hint != null) {
            editText.setHint(hint);
        }
        editText.setSingleLine(true);

        final AppCompatEditText editText2 = new AppCompatEditText(context);
        editText2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        if (text2 != null) {
            editText2.setText(text2);
        }
        if (hint2 != null) {
            editText2.setHint(hint2);
        }
        editText2.setSingleLine(true);

        layout.addView(editText);
        layout.addView(editText2);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        if (negativeListener != null) {
            builder.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogEditTextListener.onClick(editText.getText().toString(), editText2.getText().toString());
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (negativeListener != null) {
                        negativeListener.onClick(dialog, 0);
                    }
                }
            });
        }
        return builder;
    }

    public static AlertDialog.Builder dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                                     final OnDialogEditTextListener onDialogEditTextListener,
                                                     Context context) {
        return dialogEditText(text, negativeListener, onDialogEditTextListener, -1, context);
    }

    public static AlertDialog.Builder dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                                     final OnDialogEditTextListener onDialogEditTextListener, int inputType,
                                                     Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_edittext_padding);
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (text != null) {
            editText.setText(text);
        }
        editText.setSingleLine(true);
        if (inputType >= 0) {
            editText.setInputType(inputType);
        }

        layout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        if (negativeListener != null) {
            builder.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogEditTextListener.onClick(editText.getText().toString());
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (negativeListener != null) {
                        negativeListener.onClick(dialog, 0);
                    }
                }
            });
        }
        return builder;
    }

    public static AlertDialog.Builder dialogBuilder(CharSequence message, DialogInterface.OnClickListener negativeListener,
                                                    DialogInterface.OnClickListener positiveListener,
                                                    DialogInterface.OnDismissListener dismissListener, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        if (negativeListener != null) {
            builder.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (positiveListener != null) {
            builder.setPositiveButton(context.getString(R.string.ok), positiveListener);
        }
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener);
        }
        return builder;
    }

    public static Bitmap scaleDownBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = width;
        int newHeight = height;

        if (maxWidth != 0 && newWidth > maxWidth) {
            newHeight = Math.round((float) maxWidth / newWidth * newHeight);
            newWidth = maxWidth;
        }

        if (maxHeight != 0 && newHeight > maxHeight) {
            newWidth = Math.round((float) maxHeight / newHeight * newWidth);
            newHeight = maxHeight;
        }

        return width != newWidth || height != newHeight ? resizeBitmap(bitmap, newWidth, newHeight) : bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

}
