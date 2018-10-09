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
package com.grarak.kerneladiutor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.ViewUtils;

public class BorderCircleView extends FrameLayout {

    public static final SparseArray<String> sAccentColors = new SparseArray<>();

    static {
        sAccentColors.put(R.color.red_accent, "red_accent");
        sAccentColors.put(R.color.pink_accent, "pink_accent");
        sAccentColors.put(R.color.purple_accent, "purple_accent");
        sAccentColors.put(R.color.blue_accent, "blue_accent");
        sAccentColors.put(R.color.green_accent, "green_accent");
        sAccentColors.put(R.color.orange_accent, "orange_accent");
        sAccentColors.put(R.color.brown_accent, "brown_accent");
        sAccentColors.put(R.color.grey_accent, "grey_accent");
        sAccentColors.put(R.color.blue_grey_accent, "blue_grey_accent");
        sAccentColors.put(R.color.teal_accent, "teal_accent");
    }

    private final Drawable mCheck;
    private boolean mChecked;
    private final Paint mPaint;
    private final Paint mPaintBorder;

    public BorderCircleView(Context context) {
        this(context, null);
    }

    public BorderCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isClickable()) {
            setForeground(ViewUtils.getSelectableBackground(context));
        }
        mCheck = ContextCompat.getDrawable(context, R.drawable.ic_done);
        DrawableCompat.setTint(mCheck, Color.WHITE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ViewUtils.getThemeAccentColor(context));

        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder.setColor(ViewUtils.getColorPrimaryColor(context));
        mPaintBorder.setStrokeWidth((int) getResources().getDimension(R.dimen.circleview_border));
        mPaintBorder.setStyle(Paint.Style.STROKE);

        setWillNotDraw(false);
    }

    @Override
    public void setBackgroundColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        float radius = Math.min(width, height) / 2f - 4f;

        canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        canvas.drawCircle(width / 2, height / 2, radius, mPaintBorder);

        if (mChecked) {
            mCheck.setBounds(Math.round(width / 2 - radius), 0, Math.round(width / 2 + radius), height);
            mCheck.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = getResources().getDimension(R.dimen.circleview_width);
        float desiredHeight = getResources().getDimension(R.dimen.circleview_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width;
        float height;

        if (widthMode == MeasureSpec.EXACTLY) width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST) width = Math.min(desiredWidth, widthSize);
        else width = desiredWidth;

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST) height = Math.min(desiredHeight, heightSize);
        else height = desiredHeight;

        setMeasuredDimension((int) width, (int) height);
    }
}
