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

package com.grarak.cardview;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 25.12.14.
 */
public class HeaderCardView {

    /**
     * Default layout
     */
    private static final int DEFAULT_LAYOUT = R.layout.header_cardview;

    private final Context context;

    /**
     * Views
     */
    private TextView textView;
    private CharSequence title;
    private final View view;

    public HeaderCardView(Context context) {
        this(context, DEFAULT_LAYOUT);
    }

    public HeaderCardView(Context context, int layout) {
        this.context = context;

        // Inflate the layout of the header
        view = LayoutInflater.from(context).inflate(layout, null, false);

        // If sub class overwrites the default layout then don't try to get the TextView
        if (layout == DEFAULT_LAYOUT) {
            textView = (TextView) view.findViewById(R.id.header_view);

            if (title != null) textView.setText(title);
        } else {
            setUpHeaderLayout(view);
        }
    }

    /**
     * Will get executed if sub class overwrites the default layout
     * to use a custom one
     *
     * @param view is the parent innerlayout of the custom layout
     */
    public void setUpHeaderLayout(View view) {
    }

    /**
     * Sets the string value of TextView in the header
     *
     * @param title new Text of the header
     */
    public void setText(CharSequence title) {
        this.title = title;
        if (textView != null) textView.setText(title);
    }

    /**
     * BaseCardView needs this to add the header
     *
     * @return the header view
     */
    public View getView() {
        return view;
    }

    /**
     * Since this class does not extending the View class
     * create this function
     *
     * @return resources
     */
    public Resources getResources() {
        return context.getResources();
    }

}
