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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 22.12.14.
 */
public class CheckBoxCardItem extends BaseCardView {

    private HeaderCardView headerCardView;

    private TextView descriptionView;
    private CheckBox checkBoxView;

    private String titleText;
    private String descriptionText;
    private boolean checked;

    private OnCheckBoxCardListener onCheckBoxCardListener;

    public CheckBoxCardItem(Context context) {
        super(context, R.layout.checkbox_cardview);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxView != null) {
                    checkBoxView.setChecked(!checkBoxView.isChecked());
                    checked = checkBoxView.isChecked();

                    if (onCheckBoxCardListener != null)
                        onCheckBoxCardListener.onChecked(CheckBoxCardItem.this, checked);
                }
            }
        });

        checkBoxView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = checkBoxView.isChecked();

                if (onCheckBoxCardListener != null)
                    onCheckBoxCardListener.onChecked(CheckBoxCardItem.this, checked);
            }
        });
    }

    @Override
    public void setUpInnerLayout(View view) {
        headerCardView = new HeaderCardView(getContext());

        descriptionView = (TextView) view.findViewById(R.id.description_view);
        checkBoxView = (CheckBox) view.findViewById(R.id.checkbox_view);

        setUpTitle();
        if (descriptionText != null) descriptionView.setText(descriptionText);
        checkBoxView.setChecked(checked);
    }

    public void setTitle(String title) {
        titleText = title;
        setUpTitle();
    }

    public void setDescription(String description) {
        descriptionText = description;
        if (descriptionView != null) descriptionView.setText(descriptionText);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checkBoxView != null) checkBoxView.setChecked(checked);
    }

    private void setUpTitle() {
        if (headerCardView != null) {
            if (titleText == null) removeHeader();
            else addHeader(headerCardView);
        }
        if (headerCardView != null && titleText != null)
            headerCardView.setText(titleText);
    }

    public void setOnCheckBoxCardListener(OnCheckBoxCardListener onCheckBoxCardListener) {
        this.onCheckBoxCardListener = onCheckBoxCardListener;
    }

    public interface OnCheckBoxCardListener {
        public void onChecked(CheckBoxCardItem checkBoxCardItem, boolean checked);
    }

    public static class DCheckBoxCard implements DAdapter.DView {

        private CheckBoxCardItem checkBoxCardItem;

        private String title;
        private String description;
        private boolean checked;

        private OnDCheckBoxCardListener onDCheckBoxCardListener;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new CheckBoxCardItem(viewGroup.getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            checkBoxCardItem = (CheckBoxCardItem) viewHolder.itemView;

            if (title != null) checkBoxCardItem.setTitle(title);
            if (description != null) checkBoxCardItem.setDescription(description);
            checkBoxCardItem.setChecked(checked);

            setUpListener();
        }

        public void setTitle(String title) {
            this.title = title;
            if (checkBoxCardItem != null) checkBoxCardItem.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (checkBoxCardItem != null) checkBoxCardItem.setDescription(description);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (checkBoxCardItem != null) checkBoxCardItem.setChecked(checked);
        }

        public void setOnDCheckBoxCardListener(OnDCheckBoxCardListener onDCheckBoxCardListener) {
            this.onDCheckBoxCardListener = onDCheckBoxCardListener;
            setUpListener();
        }

        private void setUpListener() {
            if (onDCheckBoxCardListener != null && checkBoxCardItem != null) {
                checkBoxCardItem.setOnCheckBoxCardListener(new CheckBoxCardItem.OnCheckBoxCardListener() {
                    @Override
                    public void onChecked(CheckBoxCardItem checkBoxCardItem, boolean checked) {
                        DCheckBoxCard.this.checked = checked;
                        onDCheckBoxCardListener.onChecked(DCheckBoxCard.this, checked);
                    }
                });
            }
        }

        public interface OnDCheckBoxCardListener {
            public void onChecked(DCheckBoxCard dCheckBoxCard, boolean checked);
        }

    }

}