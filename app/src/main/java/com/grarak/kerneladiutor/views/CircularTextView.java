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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 21.04.16.
 */
public class CircularTextView extends View {

    private Paint mCirclePaint;
    private Paint mTextPaint;
    private String mText;

    public CircularTextView(Context context) {
        this(context, null);
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int defaultCircleColor = ContextCompat.getColor(context, R.color.colorAccent);
        int defaultTextColor = ContextCompat.getColor(context, R.color.white);
        float defaultTextSize = getResources().getDimension(R.dimen.circular_textview_textsize);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularTextView, defStyleAttr, 0);
        int circleColor = a.getColor(R.styleable.CircularTextView_circlecolor, defaultCircleColor);
        int textColor = a.getColor(R.styleable.CircularTextView_textcolor, defaultTextColor);
        float textSize = a.getDimension(R.styleable.CircularTextView_textsize, defaultTextSize);
        mText = a.getString(R.styleable.CircularTextView_text);
        a.recycle();

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(circleColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setDither(true);
        mTextPaint.setStrokeWidth(0);
        mTextPaint.setColor(textColor);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setColor(int color) {
        mCirclePaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, Math.min(getMeasuredWidth(),
                getMeasuredHeight()) / 2, mCirclePaint);
        if (mText != null) {
            int x = getMeasuredWidth() / 2;
            int y = (int) ((getMeasuredHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
            canvas.drawText(mText, x, y, mTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = getResources().getDimension(R.dimen.circular_textview_width);
        float desiredHeight = getResources().getDimension(R.dimen.circular_textview_height);

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
