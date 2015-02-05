package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutor.R;

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
    private final int mBackgroundColor;
    private final float density;

    public CircleChart(Context context) {
        this(context, null);
    }

    public CircleChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mCircleColor = getResources().getColor(R.color.color_primary);
        mBackgroundColor = getResources().getColor(R.color.color_primary_dark);
        density = getResources().getDisplayMetrics().density;

        mPaintBackground = new Paint();
        mPaintBackground.setStrokeWidth(Math.round(7.5 * density));
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mBackgroundColor);
        mPaintBackground.setStrokeCap(Paint.Cap.ROUND);

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(Math.round(5 * density));
        mPaintCircle.setStrokeCap(Paint.Cap.ROUND);
        mPaintCircle.setColor(mCircleColor);

        mRectF = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        draw(canvas, getWidth() - 10, getHeight() - 10);
    }

    private void draw(Canvas canvas, int x, int y) {
        int padding = Math.round(5 * density);
        mRectF.set(padding, padding, x - padding, y - padding);
        canvas.drawArc(mRectF, 0, 360, false, mPaintBackground);
        canvas.drawArc(mRectF, 270, Math.round(mProgress * (360 / mMax)), false, mPaintCircle);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(mCircleColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Math.round(20 * density));
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();

        RectF bounds = new RectF(padding, padding, x - padding, y - padding);
        String text = mProgress + "";
        canvas.drawText(text, bounds.centerX(), bounds.centerY() + textOffset, textPaint);
    }

    public void setProgress(final int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setMax(int max) {
        mMax = max;
    }

}
