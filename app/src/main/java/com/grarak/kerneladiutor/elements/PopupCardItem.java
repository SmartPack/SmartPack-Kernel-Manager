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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;

import java.util.List;

/**
 * Created by willi on 22.12.14.
 */
public class PopupCardItem extends BaseCardView {

    private final List<String> list;

    private HeaderCardView headerCardView;

    private TextView descriptionView;
    private TextView valueView;

    private String titleText;
    private String descriptionText;
    private String valueText;

    private PopupMenu popup;
    private OnPopupCardListener onPopupCardListener;

    public PopupCardItem(Context context, final List<String> list) {
        super(context, R.layout.popup_cardview);
        this.list = list;

        if (list != null) {
            popup = new PopupMenu(getContext(), valueView);
            for (int i = 0; i < list.size(); i++)
                popup.getMenu().add(Menu.NONE, i, Menu.NONE, list.get(i));
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (valueView != null)
                        valueView.setText(list.get(item.getItemId()) + " ");
                    if (onPopupCardListener != null)
                        onPopupCardListener.onItemSelected(PopupCardItem.this, item.getItemId());
                    return false;
                }
            });
        }

        if (onPopupCardListener != null) setListener();
    }

    @Override
    public void setUpInnerLayout(View view) {
        headerCardView = new HeaderCardView(getContext());

        descriptionView = (TextView) view.findViewById(R.id.description_view);
        valueView = (TextView) view.findViewById(R.id.value_view);

        setUpTitle();
        setUpDescription();
        if (valueText != null) valueView.setText(valueText + " ");
    }

    public void setTitle(String title) {
        titleText = title;
        setUpTitle();
    }

    public void setDescription(String description) {
        descriptionText = description;
        setUpDescription();
    }

    public void setItem(int position) {
        valueText = list.get(position);
        if (valueView != null) valueView.setText(valueText + " ");
    }

    public void setItem(String value) {
        valueText = value;
        if (valueView != null) valueView.setText(valueText + " ");
    }

    public String getDescription() {
        return descriptionText;
    }

    public String getItem() {
        return valueText;
    }

    private void setUpTitle() {
        int padding = getResources().getDimensionPixelSize(R.dimen.card_padding);
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

    private void setUpDescription() {
        if (descriptionView != null) {
            if (descriptionText == null) descriptionView.setVisibility(GONE);
            else descriptionView.setVisibility(VISIBLE);
        }
        if (descriptionView != null && descriptionText != null)
            descriptionView.setText(descriptionText);
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup != null) popup.show();
            }
        });
    }

    public void setOnPopupCardListener(OnPopupCardListener onPopupCardListener) {
        this.onPopupCardListener = onPopupCardListener;
        setListener();
    }

    public interface OnPopupCardListener {
        void onItemSelected(PopupCardItem popupCardItem, int position);
    }

    public static class DPopupCard implements DAdapter.DView {

        private final List<String> list;

        private PopupCardItem popupCardItem;

        private String title;
        private String description;
        private String value;

        private OnDPopupCardListener onDPopupCardListener;
        private OnClickListener onClickListener;

        public DPopupCard(List<String> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new PopupCardItem(viewGroup.getContext(), list)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            popupCardItem = (PopupCardItem) viewHolder.itemView;

            if (title != null) popupCardItem.setTitle(title);
            if (description != null) popupCardItem.setDescription(description);
            if (value != null) popupCardItem.setItem(value);
            setListener();
        }

        public void setTitle(String title) {
            this.title = title;
            if (popupCardItem != null) popupCardItem.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (popupCardItem != null) popupCardItem.setDescription(description);
        }

        public void setItem(int position) {
            try {
                value = list.get(position);
            } catch (IndexOutOfBoundsException e) {
                value = "Unknown";
            }
            if (popupCardItem != null) popupCardItem.setItem(value);
        }

        public void setItem(String value) {
            this.value = value;
            if (popupCardItem != null) popupCardItem.setItem(value);
        }

        public void setOnDPopupCardListener(OnDPopupCardListener onDPopupCardListener) {
            this.onDPopupCardListener = onDPopupCardListener;
            setListener();
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            setListener();
        }

        private void setListener() {
            if (popupCardItem != null) {
                if (onDPopupCardListener != null)
                    popupCardItem.setOnPopupCardListener(new PopupCardItem.OnPopupCardListener() {
                        @Override
                        public void onItemSelected(PopupCardItem popupCardItem, int position) {
                            if (onDPopupCardListener != null)
                                onDPopupCardListener.onItemSelected(DPopupCard.this, position);
                        }
                    });
                if (onClickListener != null)
                    popupCardItem.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onClickListener != null) onClickListener.onClick(v);
                        }
                    });
            }
        }

        public interface OnDPopupCardListener {
            void onItemSelected(DPopupCard dPopupCard, int position);
        }

    }

}