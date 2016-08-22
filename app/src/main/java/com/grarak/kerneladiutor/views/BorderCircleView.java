package com.grarak.kerneladiutor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

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
    private final Paint paint;
    private final Paint paintBorder;
    private final int borderWidth;

    private Paint paintCheck;
    private PorterDuffColorFilter blackFilter;
    private PorterDuffColorFilter whiteFilter;

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
        borderWidth = (int) getResources().getDimension(R.dimen.circleview_border);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ViewUtils.getThemeAccentColor(context));

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(ViewUtils.getColorPrimaryColor(context));

        paintCheck = new Paint();
        paintCheck.setAntiAlias(true);

        blackFilter = new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        whiteFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        setWillNotDraw(false);
    }

    @Override
    public void setBackgroundColor(int color) {
        paint.setColor(color);
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

        int canvasSize = Math.min(width, height);
        int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
                ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
                ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);

        if (mChecked) {
            paintCheck.setColorFilter(paint.getColor() == Color.WHITE ? blackFilter : whiteFilter);
            mCheck.setBounds(borderWidth, borderWidth, width - borderWidth, height - borderWidth);
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
