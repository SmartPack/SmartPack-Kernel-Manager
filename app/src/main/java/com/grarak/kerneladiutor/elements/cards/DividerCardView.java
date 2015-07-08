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

package com.grarak.kerneladiutor.elements.cards;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;

import java.util.Locale;

/**
 * Created by willi on 01.03.15.
 */
public class DividerCardView extends BaseCardView {

    private TextView textView;
    private String text;
    private boolean lowercase;

    public DividerCardView(Context context) {
        super(context, R.layout.divider_cardview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setTranslationZ(getResources()
                .getDimensionPixelSize(R.dimen.divider_card_elevation));
    }

    @Override
    public void setUpInnerLayout(View view) {
        textView = (TextView) view;

        if (text != null)
            textView.setText(lowercase ? text.toLowerCase(Locale.getDefault()) : text.toUpperCase(Locale.getDefault()));
    }

    public void setText(String text) {
        this.text = text;
        if (textView != null)
            textView.setText(lowercase ? text.toLowerCase(Locale.getDefault()) : text.toUpperCase(Locale.getDefault()));
    }

    public void toLowerCase() {
        lowercase = true;
        setText(text);
    }

    public void setDescription(final String description) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setMessage(description)
                        .setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });
    }

    public static class DDividerCard implements DAdapter.DView {

        private DividerCardView dividerCardView;

        private String text;
        private String description;
        private boolean lowercase;

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new DividerCardView(viewGroup.getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            dividerCardView = (DividerCardView) viewHolder.itemView;

            if (text != null) dividerCardView.setText(text);
            if (description != null) dividerCardView.setDescription(description);
            if (lowercase) dividerCardView.toLowerCase();

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            int padding = dividerCardView.getContext().getResources().getDimensionPixelSize(R.dimen.basecard_padding);
            layoutParams.setMargins(padding, padding, padding, padding);
            dividerCardView.setLayoutParams(layoutParams);
        }

        public void setText(String text) {
            this.text = text;
            if (dividerCardView != null) dividerCardView.setText(text);
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void toLowerCase() {
            lowercase = true;
            if (dividerCardView != null) dividerCardView.toLowerCase();
        }

    }

}
