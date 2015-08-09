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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 09.03.15.
 */
public class SplashView extends View {

    private final Paint mPaintCircle;
    private final float density;
    private int radius = 0;
    private int rotate = 0;
    private final Bitmap icon;
    private final Matrix matrix;
    private final int textColor;
    private final int textSize;
    private boolean finished = false;

    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setBackgroundColor(getResources().getColor(R.color.color_primary));
        density = getResources().getDisplayMetrics().density;
        textColor = getResources().getColor(R.color.white);
        textSize = getResources().getDimensionPixelSize(R.dimen.splashview_textsize);

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.FILL);
        mPaintCircle.setStrokeCap(Paint.Cap.ROUND);
        mPaintCircle.setColor(Color.WHITE);

        matrix = new Matrix();
        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(17);
                        rotate++;
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                        if (finished) break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void finish() {
        if (Utils.DARKTHEME)
            mPaintCircle.setColor(getResources().getColor(R.color.navigationdrawer_background_dark));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 1; i <= getHeight() / 2; i += 15) {
                        radius = i;
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                        Thread.sleep(17);
                    }
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(GONE);
                            finished = true;
                            startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        draw(canvas, getWidth(), getHeight(), (int) (radius * density));
    }

    private void draw(Canvas canvas, int x, int y, int radius) {
        if (radius > 0) canvas.drawCircle(x / 2, y / 2, radius, mPaintCircle);
        matrix.postRotate(rotate);
        Bitmap iconRotate = Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, false);
        canvas.drawBitmap(iconRotate, x / 2 - iconRotate.getWidth() / 2, y / 2 - iconRotate.getHeight() / 2, mPaintCircle);

        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();

        canvas.drawText(getResources().getString(R.string.root_waiting), x / 2, y - textOffset - y / 4, textPaint);
    }

}
