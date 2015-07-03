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
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 05.02.15.
 */
public class CircleChart extends View {

    private int mProgress = 0;
    private int mMax = 100;
    private final Paint mPaintCircle;
    private final Paint mPaintBackground;
    private final RectF mRectF;
    private final int mCircleColor;
    private final int mPadding;
    private final int mTextsize;

    public CircleChart(Context context) {
        this(context, null);
    }

    public CircleChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mCircleColor = getResources().getColor(R.color.circlebar_text);
        mPadding = getResources().getDimensionPixelSize(R.dimen.circlechart_padding);
        mTextsize = getResources().getDimensionPixelSize(R.dimen.circlechart_textsize);

        mPaintBackground = new Paint();
        mPaintBackground.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.circlechart_background_stroke));
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(getResources().getColor(Utils.DARKTHEME ? R.color.circlebar_background_dark :
                R.color.circlebar_background_light));
        mPaintBackground.setStrokeCap(Paint.Cap.ROUND);

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.circlechart_stroke));
        mPaintCircle.setStrokeCap(Paint.Cap.ROUND);
        mPaintCircle.setColor(mCircleColor);

        mRectF = new RectF();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        draw(canvas, getMeasuredWidth() - 10, getMeasuredHeight() - 10);
    }

    private void draw(Canvas canvas, int x, int y) {
        mRectF.set(mPadding, mPadding, x - mPadding, y - mPadding);
        canvas.drawArc(mRectF, 0, 360, false, mPaintBackground);
        float offset = 360 / (float) mMax;
        canvas.drawArc(mRectF, 270, offset * mProgress, false, mPaintCircle);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(mCircleColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(mTextsize);
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();

        RectF bounds = new RectF(mPadding, mPadding, x - mPadding, y - mPadding);
        String text = String.valueOf(mProgress);
        canvas.drawText(text, bounds.centerX(), bounds.centerY() + textOffset, textPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setMax(int max) {
        mMax = max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = getResources().getDimensionPixelSize(R.dimen.circlechart_width);
        int desiredHeight = getResources().getDimensionPixelSize(R.dimen.circlechart_height);

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
