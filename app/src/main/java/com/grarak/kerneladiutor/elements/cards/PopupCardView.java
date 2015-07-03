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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;

import java.util.List;

/**
 * Created by willi on 22.12.14.
 */
public class PopupCardView extends BaseCardView {

    private final List<String> list;

    private HeaderCardView headerCardView;

    private TextView descriptionView;
    private TextView valueView;

    private String titleText;
    private CharSequence descriptionText;
    private String valueText;

    private PopupMenu popup;
    private OnPopupCardListener onPopupCardListener;

    public PopupCardView(Context context, final List<String> list) {
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
                        onPopupCardListener.onItemSelected(PopupCardView.this, item.getItemId());
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
        if (valueText != null) valueView.setText(valueText);
    }

    public void setTitle(String title) {
        titleText = title;
        setUpTitle();
    }

    public void setDescription(CharSequence description) {
        descriptionText = description;
        setUpDescription();
    }

    public void setItem(int position) {
        valueText = list.get(position) + " ";
        if (valueView != null) valueView.setText(valueText);
    }

    public void setItem(String value) {
        valueText = value + " ";
        if (valueView != null) valueView.setText(valueText);
    }

    public CharSequence getDescription() {
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
        void onItemSelected(PopupCardView popupCardView, int position);
    }

    public static class DPopupCard implements DAdapter.DView {

        private final List<String> list;

        private PopupCardView popupCardView;

        private String title;
        private CharSequence description;
        private String value;

        private OnDPopupCardListener onDPopupCardListener;
        private OnClickListener onClickListener;

        public DPopupCard(List<String> list) {
            this.list = list;
        }

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
            return new RecyclerView.ViewHolder(new PopupCardView(viewGroup.getContext(), list)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            popupCardView = (PopupCardView) viewHolder.itemView;

            if (title != null) popupCardView.setTitle(title);
            if (description != null) popupCardView.setDescription(description);
            if (value != null) popupCardView.setItem(value);
            setListener();
        }

        public void setTitle(String title) {
            this.title = title;
            if (popupCardView != null) popupCardView.setTitle(title);
        }

        public void setDescription(CharSequence description) {
            this.description = description;
            if (popupCardView != null) popupCardView.setDescription(description);
        }

        public void setItem(int position) {
            try {
                value = list.get(position);
            } catch (IndexOutOfBoundsException e) {
                value = "Unknown";
            }
            if (popupCardView != null) popupCardView.setItem(value);
        }

        public void setItem(String value) {
            this.value = value;
            if (popupCardView != null) popupCardView.setItem(value);
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
            if (popupCardView != null) {
                if (onDPopupCardListener != null)
                    popupCardView.setOnPopupCardListener(new PopupCardView.OnPopupCardListener() {
                        @Override
                        public void onItemSelected(PopupCardView popupCardView, int position) {
                            if (onDPopupCardListener != null)
                                onDPopupCardListener.onItemSelected(DPopupCard.this, position);
                        }
                    });
                if (onClickListener != null)
                    popupCardView.setOnClickListener(new OnClickListener() {
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