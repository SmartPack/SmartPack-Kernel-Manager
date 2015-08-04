/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 21.04.15.
 */
public class ColorPalette extends View {

    private final Paint mPaintColor;

    private final String[][] colors = {
            {"#000000", "#404040", "#ed0021", "#ef6222", "#f5dc2b", "#b8ff28", "#67ff21"},
            {"#ffffff", "#808080", "#55ff92", "#67fffa", "#528dfb", "#b100fb", "#f00071"}
    };

    public ColorPalette(Context context) {
        this(context, null);
    }

    public ColorPalette(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPalette(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaintColor = new Paint();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        for (int i = 0; i < colors.length; i++) {
            for (int x = 0; x < colors[i].length; x++) {
                float widthOffset = width / colors[i].length;
                float heightOffset = height / colors.length;
                mPaintColor.setColor(Color.parseColor(colors[i][x]));
                canvas.drawRect(widthOffset * x, heightOffset * i, widthOffset * (x + 1), heightOffset * (i + 1), mPaintColor);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = getResources().getDisplayMetrics().widthPixels;
        int desiredHeight = getResources().getDimensionPixelSize(R.dimen.colorpalette_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST) width = Math.min(desiredWidth, widthSize);
        else width = desiredWidth;

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST) height = Math.min(desiredHeight, heightSize);
        else height = desiredHeight;


        setMeasuredDimension(width, height);
    }

}
