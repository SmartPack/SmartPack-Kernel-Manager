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
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 31.05.16.
 */
public class ColorTable extends View {

    private static final String[][] sColors = {
            {"#000000", "#404040", "#ed0021", "#ef6222", "#f5dc2b", "#b8ff28", "#67ff21"},
            {"#158dd7", "#22cdbf", "#6da387", "#08d04d", "#458491", "#392069", "#8c1e91"},
            {"#ffffff", "#808080", "#55ff92", "#67fffa", "#528dfb", "#b100fb", "#f00071"}
    };

    private final Paint mColorPaint;

    public ColorTable(Context context) {
        this(context, null);
    }

    public ColorTable(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        for (int i = 0; i < sColors.length; i++) {
            float top = height / sColors.length * i;
            float bottom = height / sColors.length * (i + 1);
            for (int x = 0; x < sColors[i].length; x++) {
                float left = width / sColors[i].length * x;
                float right = width / sColors[i].length * (x + 1);
                mColorPaint.setColor(Color.parseColor(sColors[i][x]));
                canvas.drawRect(left, top, right, bottom, mColorPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = getResources().getDimension(R.dimen.colortable_width);
        float desiredHeight = getResources().getDimension(R.dimen.colortable_height);

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
