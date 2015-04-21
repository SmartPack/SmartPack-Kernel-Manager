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
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 22.12.14.
 */
public class SwitchCompatCardItem extends BaseCardView {

    private HeaderCardView headerCardView;

    private TextView descriptionView;
    private SwitchCompat switchCompatView;

    private String titleText;
    private String descriptionText;
    private boolean checked;

    private OnSwitchCompatCardListener onSwitchCompatCardListener;

    public SwitchCompatCardItem(Context context) {
        super(context, R.layout.switchcompat_cardview);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompatView != null) {
                    switchCompatView.setChecked(!switchCompatView.isChecked());
                    checked = switchCompatView.isChecked();

                    if (onSwitchCompatCardListener != null)
                        onSwitchCompatCardListener.onChecked(SwitchCompatCardItem.this, checked);
                }
            }
        });

        switchCompatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = switchCompatView.isChecked();

                if (onSwitchCompatCardListener != null)
                    onSwitchCompatCardListener.onChecked(SwitchCompatCardItem.this, checked);
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
        if (headerCardView != null) {
            if (titleText == null) {
                removeHeader();
                layoutView.setPadding(32, 32, 32, 32);
            } else {
                addHeader(headerCardView);
                layoutView.setPadding(32, 0, 32, 32);
            }
        }
        if (headerCardView != null && titleText != null)
            headerCardView.setText(titleText);
    }

    public void setOnSwitchCompatCardListener(OnSwitchCompatCardListener onSwitchCompatCardListener) {
        this.onSwitchCompatCardListener = onSwitchCompatCardListener;
    }

    public interface OnSwitchCompatCardListener {
        void onChecked(SwitchCompatCardItem switchCompatCardItem, boolean checked);
    }

    public static class DSwitchCompatCard implements DAdapter.DView {

        private SwitchCompatCardItem switchCompatCardItem;

        private String title;
        private String description;
        private boolean checked;
        private boolean fullspan;

        private OnDSwitchCompatCardListener onDSwitchCompatCardListener;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new SwitchCompatCardItem(viewGroup.getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            switchCompatCardItem = (SwitchCompatCardItem) viewHolder.itemView;

            if (title != null) switchCompatCardItem.setTitle(title);
            if (description != null) switchCompatCardItem.setDescription(description);
            switchCompatCardItem.setChecked(checked);

            if (fullspan) {
                StaggeredGridLayoutManager.LayoutParams params =
                        new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                switchCompatCardItem.setLayoutParams(params);
            }

            setUpListener();
        }

        public void setTitle(String title) {
            this.title = title;
            if (switchCompatCardItem != null) switchCompatCardItem.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (switchCompatCardItem != null) switchCompatCardItem.setDescription(description);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (switchCompatCardItem != null) switchCompatCardItem.setChecked(checked);
        }

        public void setFullSpan(boolean fullspan) {
            this.fullspan = fullspan;
        }

        public void setOnDSwitchCompatCardListener(OnDSwitchCompatCardListener onDSwitchCompatCardListener) {
            this.onDSwitchCompatCardListener = onDSwitchCompatCardListener;
            setUpListener();
        }

        private void setUpListener() {
            if (onDSwitchCompatCardListener != null && switchCompatCardItem != null) {
                switchCompatCardItem.setOnSwitchCompatCardListener(new OnSwitchCompatCardListener() {
                    @Override
                    public void onChecked(SwitchCompatCardItem switchCompatCardItem, boolean checked) {
                        DSwitchCompatCard.this.checked = checked;
                        onDSwitchCompatCardListener.onChecked(DSwitchCompatCard.this, checked);
                    }
                });
            }
        }

        public interface OnDSwitchCompatCardListener {
            void onChecked(DSwitchCompatCard dSwitchCompatCard, boolean checked);
        }

    }

}