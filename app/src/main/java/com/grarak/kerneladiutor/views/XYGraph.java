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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.ArrayList;

/**
 * Created by willi on 09.01.16.
 */
public class XYGraph extends View {

    private Paint mPaintLine;
    private Paint mPaintEdge;
    private Paint mPaintGraph;
    private Paint mPaintGraphStroke;
    private Path mPathGraph;
    private boolean mEdgeVisible;
    private int state = 1;
    private ArrayList<Integer> mPercentages = new ArrayList<>();

    public XYGraph(Context context) {
        this(context, null);
    }

    public XYGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintEdge = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGraph = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGraphStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathGraph = new Path();

        mPaintEdge.setStyle(Paint.Style.STROKE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XYGraph, defStyleAttr, 0);

        int accentColor = ContextCompat.getColor(context, R.color.colorAccent);
        mPaintLine.setColor(a.getColor(R.styleable.XYGraph_linecolor, accentColor));
        mPaintEdge.setColor(a.getColor(R.styleable.XYGraph_edgecolor, accentColor));
        mPaintEdge.setStrokeWidth(a.getDimension(R.styleable.XYGraph_edgestrokewidth,
                getResources().getDimension(R.dimen.xygraph_edge_stroke_width)));

        int graphColor = a.getColor(R.styleable.XYGraph_graphcolor, accentColor);

        mPaintGraphStroke.setColor(graphColor);
        mPaintGraphStroke.setStyle(Paint.Style.STROKE);
        mPaintGraphStroke.setStrokeWidth(a.getDimension(R.styleable.XYGraph_graphstrokewidth,
                getResources().getDimension(R.dimen.xygraph_graph_stroke_width)));

        graphColor = Color.argb(120, Color.red(graphColor), Color.green(graphColor), Color.blue(graphColor));

        mPaintGraph.setColor(graphColor);
        mPaintGraph.setStyle(Paint.Style.FILL);
        mPathGraph.setFillType(Path.FillType.EVEN_ODD);

        mEdgeVisible = a.getBoolean(R.styleable.XYGraph_edgevisibile, true);

        a.recycle();
    }

    public void addPercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalStateException("Percentage can only be between 0 and 100");
        }
        mPercentages.add(percentage);
        if (mPercentages.size() > 25) {
            mPercentages.remove(0);
        }
        state++;
        if (state > 4) {
            state = 1;
        }
        invalidate();
    }

    public void clear() {
        mPercentages.clear();
        state = 1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        boolean isRTL = Utils.isRTL(this);

        for (int i = 1; i < 10; i++) {
            float y = ((float) height / 10) * i;
            canvas.drawLine(0, y, width, y, mPaintLine);
        }

        for (int i = 0; i < 7; i++) {
            float x = ((float) width / 6) * i;
            float offset = (float) width / 6 / 4 * state;
            if (isRTL) {
                x += offset;
            } else {
                x -= offset;
            }
            canvas.drawLine(x, 0, x, height, mPaintLine);
        }

        mPathGraph.reset();
        float graphX;
        if (isRTL) {
            graphX = ((float) width / 24) * (mPercentages.size() - 1);
        } else {
            graphX = width - ((float) width / 24) * (mPercentages.size() - 1);
        }
        mPathGraph.moveTo(graphX, height);
        float x = 0;
        float y;
        for (int i = 0; i < mPercentages.size(); i++) {
            if (isRTL) {
                x = graphX - ((float) width / 24) * i;
            } else {
                x = graphX + ((float) width / 24) * i;
            }
            y = ((float) (100 - mPercentages.get(i)) / 100) * height;
            mPathGraph.lineTo(x, y);
        }
        mPathGraph.lineTo(x, height);
        mPathGraph.close();

        canvas.drawPath(mPathGraph, mPaintGraph);
        canvas.drawPath(mPathGraph, mPaintGraphStroke);

        if (mEdgeVisible) {
            canvas.drawRect(0, 0, width, height, mPaintEdge);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            mPercentages = ((Bundle) state).getIntegerArrayList("arrayList");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("arrayList", mPercentages);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = getResources().getDimension(R.dimen.xygraph_width);
        float desiredHeight = getResources().getDimension(R.dimen.xygraph_height);

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
