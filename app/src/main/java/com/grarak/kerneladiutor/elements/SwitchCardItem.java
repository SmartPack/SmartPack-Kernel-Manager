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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 22.12.14.
 */
public class SwitchCardItem extends BaseCardView {

    private HeaderCardView headerCardView;

    private TextView descriptionView;
    private SwitchCompat switchCompatView;

    private String titleText;
    private String descriptionText;
    private boolean checked;

    private OnSwitchCardListener onSwitchCardListener;

    public SwitchCardItem(Context context) {
        super(context, R.layout.switchcompat_cardview);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompatView != null)
                    switchCompatView.setChecked(!switchCompatView.isChecked());
            }
        });

        switchCompatView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checked != isChecked) {
                    checked = isChecked;
                    if (onSwitchCardListener != null)
                        onSwitchCardListener.onChecked(SwitchCardItem.this, checked);
                }
            }
        });
    }

    @Override
    public void setUpInnerLayout(View view) {
        headerCardView = new HeaderCardView(getContext());

        descriptionView = (TextView) view.findViewById(R.id.description_view);
        switchCompatView = (SwitchCompat) view.findViewById(R.id.switchcompat_view);

        setUpTitle();
        if (descriptionText != null) descriptionView.setText(descriptionText);
        switchCompatView.setChecked(checked);
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
        if (switchCompatView != null) switchCompatView.setChecked(checked);
    }

    private void setUpTitle() {
        int padding = 16 * (int) getResources().getDisplayMetrics().density;
        if (headerCardView != null) {
            if (titleText == null) {
                removeHeader();
                layoutView.setPadding(padding, padding, padding, padding);
            } else {
                addHeader(headerCardView);
                layoutView.setPadding(padding, 0, padding, padding);
            }
        }
        if (headerCardView != null && titleText != null)
            headerCardView.setText(titleText);
    }

    public void setOnSwitchCardListener(OnSwitchCardListener onSwitchCardListener) {
        this.onSwitchCardListener = onSwitchCardListener;
    }

    public interface OnSwitchCardListener {
        void onChecked(SwitchCardItem switchCardItem, boolean checked);
    }

    public static class DSwitchCard implements DAdapter.DView {

        private SwitchCardItem switchCardItem;

        private String title;
        private String description;
        private boolean checked;
        private boolean fullspan;

        private OnDSwitchCardListener onDSwitchCardListener;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new SwitchCardItem(viewGroup.getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            switchCardItem = (SwitchCardItem) viewHolder.itemView;

            if (title != null) switchCardItem.setTitle(title);
            if (description != null) switchCardItem.setDescription(description);
            switchCardItem.setChecked(checked);

            if (fullspan) {
                StaggeredGridLayoutManager.LayoutParams params =
                        new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                switchCardItem.setLayoutParams(params);
            }

            setUpListener();
        }

        public void setTitle(String title) {
            this.title = title;
            if (switchCardItem != null) switchCardItem.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (switchCardItem != null) switchCardItem.setDescription(description);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (switchCardItem != null) switchCardItem.setChecked(checked);
        }

        public void setFullSpan(boolean fullspan) {
            this.fullspan = fullspan;
        }

        public void setOnDSwitchCardListener(OnDSwitchCardListener onDSwitchCardListener) {
            this.onDSwitchCardListener = onDSwitchCardListener;
            setUpListener();
        }

        private void setUpListener() {
            if (onDSwitchCardListener != null && switchCardItem != null) {
                switchCardItem.setOnSwitchCardListener(new OnSwitchCardListener() {
                    @Override
                    public void onChecked(SwitchCardItem switchCardItem, boolean checked) {
                        DSwitchCard.this.checked = checked;
                        onDSwitchCardListener.onChecked(DSwitchCard.this, checked);
                    }
                });
            }
        }

        public interface OnDSwitchCardListener {
            void onChecked(DSwitchCard dSwitchCard, boolean checked);
        }

    }

}