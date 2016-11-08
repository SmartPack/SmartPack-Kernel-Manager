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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by willi on 16.04.16.
 */
public class ViewUtils {

    public static int getTextSecondaryColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, value, true);
        return value.data;
    }

    public static Drawable getSelectableBackground(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public static void showDialog(FragmentManager manager, DialogFragment fragment) {
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        fragment.show(ft, "dialog");
    }

    public static void dismissDialog(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag("dialog");
        if (fragment != null) {
            ft.remove(fragment).commit();
        }
    }

    public static float getActionBarSize(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        float size = typedArray.getDimension(0, 0);
        typedArray.recycle();
        return size;
    }

    public static int getColorPrimaryColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    public static int getColorPrimaryDarkColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public static int getThemeAccentColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    public interface OnDialogEditTextListener {
        void onClick(String text);
    }

    public interface onDialogEditTextsListener {
        void onClick(String text, String text2);
    }

    public static Dialog dialogDonate(final Context context) {
        return new Dialog(context).setTitle(context.getString(R.string.donate))
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

    public static Dialog dialogEditTexts(String text, String text2, String hint, String hint2,
                                         final DialogInterface.OnClickListener negativeListener,
                                         final onDialogEditTextsListener onDialogEditTextListener,
                                         Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (text != null) {
            editText.append(text);
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

        Dialog dialog = new Dialog(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
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
        return dialog;
    }

    public static Dialog dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                        final OnDialogEditTextListener onDialogEditTextListener,
                                        Context context) {
        return dialogEditText(text, negativeListener, onDialogEditTextListener, -1, context);
    }

    public static Dialog dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                        final OnDialogEditTextListener onDialogEditTextListener, int inputType,
                                        Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (text != null) {
            editText.append(text);
        }
        editText.setSingleLine(true);
        if (inputType >= 0) {
            editText.setInputType(inputType);
        }

        layout.addView(editText);

        Dialog dialog = new Dialog(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
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
        return dialog;
    }

    public static Dialog dialogBuilder(CharSequence message, DialogInterface.OnClickListener negativeListener,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnDismissListener dismissListener, Context context) {
        Dialog dialog = new Dialog(context).setMessage(message);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (positiveListener != null) {
            dialog.setPositiveButton(context.getString(R.string.ok), positiveListener);
        }
        if (dismissListener != null) {
            dialog.setOnDismissListener(dismissListener);
        }
        return dialog;
    }

    private static final Set<CustomTarget> mProtectedFromGarbageCollectorTargets = new HashSet<>();

    public static void loadImagefromUrl(String url, ImageView imageView, int maxWidth, int maxHeight) {
        CustomTarget target = new CustomTarget(imageView, maxWidth, maxHeight);
        mProtectedFromGarbageCollectorTargets.add(target);
        Picasso.with(imageView.getContext()).load(url).into(target);
    }

    private static class CustomTarget implements Target {
        private ImageView mImageView;
        private int mMaxWidth;
        private int mMaxHeight;

        private CustomTarget(ImageView imageView, int maxWidth, int maxHeight) {
            mImageView = imageView;
            mMaxWidth = maxWidth;
            mMaxHeight = maxHeight;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageView.setImageBitmap(scaleDownBitmap(bitmap, mMaxWidth, mMaxHeight));
            mProtectedFromGarbageCollectorTargets.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mProtectedFromGarbageCollectorTargets.remove(this);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
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

    private static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

}
