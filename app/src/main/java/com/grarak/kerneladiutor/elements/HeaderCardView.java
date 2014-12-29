package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 25.12.14.
 */
public class HeaderCardView {

    private static final int DEFAULT_LAYOUT = R.layout.header_cardview;

    private TextView textView;
    private String title;
    private View view;

    public HeaderCardView(Context context) {
        this(context, DEFAULT_LAYOUT);
    }

    public HeaderCardView(Context context, int layout) {

        view = LayoutInflater.from(context).inflate(layout, null, false);

        if (layout == DEFAULT_LAYOUT) {
            textView = (TextView) view.findViewById(R.id.header_view);

            if (title != null) textView.setText(title);
        } else {
            setUpHeaderLayout(view);
        }
    }

    public void setUpHeaderLayout(View view) {
    }

    public void setText(String title) {
        this.title = title;
        if (textView != null) textView.setText(title);
    }

    public View getView() {
        return view;
    }

}
