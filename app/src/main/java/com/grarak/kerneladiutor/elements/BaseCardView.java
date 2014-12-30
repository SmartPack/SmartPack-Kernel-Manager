package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 23.12.14.
 */
public class BaseCardView extends CardView {

    private static final int DEFAULT_LAYOUT = R.layout.inner_cardview;

    private HeaderCardView headerCardView;
    private LinearLayout headerLayout;

    private TextView innerView;
    private String mTitle;

    private LinearLayout customLayout;
    private View customView;

    public BaseCardView(Context context) {
        this(context, DEFAULT_LAYOUT);
    }

    public BaseCardView(Context context, int layout) {
        this(context, null, layout);
    }

    public BaseCardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, DEFAULT_LAYOUT);
    }

    public BaseCardView(Context context, AttributeSet attributeSet, int layout) {
        super(context, attributeSet);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(20, 10, 20, 10);
        setLayoutParams(layoutParams);
        setRadius(10);

        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        Drawable d = ta.getDrawable(0);
        ta.recycle();
        setForeground(d);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.base_cardview, null, false);
        addView(view);

        headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);

        setUpHeader();

        LinearLayout innerLayout = (LinearLayout) view.findViewById(R.id.inner_layout);
        customLayout = (LinearLayout) view.findViewById(R.id.custom_layout);

        View layoutView = LayoutInflater.from(getContext()).inflate(layout, null, false);
        if (layout == DEFAULT_LAYOUT) {
            innerView = (TextView) layoutView.findViewById(R.id.inner_view);
            if (mTitle != null) innerView.setText(mTitle);
        } else setUpInnerLayout(layoutView);

        innerLayout.addView(layoutView);
    }

    protected void setUpInnerLayout(View view) {
    }

    public final void setText(String mTitle) {
        this.mTitle = mTitle;
        if (innerView != null) innerView.setText(mTitle);
    }

    public void setView(View view) {
        customView = view;
        setUpCustomLayout();
    }

    public void addHeader(HeaderCardView headerCardView) {
        this.headerCardView = headerCardView;
        setUpHeader();
    }

    private void setUpHeader() {
        if (headerCardView != null && headerLayout != null) {
            headerLayout.removeAllViews();
            headerLayout.addView(headerCardView.getView());
        }
    }

    public void removeHeader() {
        headerCardView = null;
        if (headerLayout != null) headerLayout.removeAllViews();
    }

    private void setUpCustomLayout() {
        if (customLayout != null && customView != null) {
            innerView.setVisibility(GONE);
            customLayout.setVisibility(VISIBLE);
            customLayout.removeAllViews();
            customLayout.addView(customView);
        }
    }

}