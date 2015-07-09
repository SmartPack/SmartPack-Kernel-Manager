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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;

/**
 * Created by willi on 23.12.14.
 */
public class CardViewItem extends BaseCardView {

    private HeaderCardView headerCardView;

    private CharSequence title;
    private CharSequence description;

    public CardViewItem(Context context) {
        super(context);

        headerCardView = new HeaderCardView(getContext());

        setUpTitle();
        if (description != null) setText(description);
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        setUpTitle();
    }

    public void setDescription(CharSequence description) {
        this.description = description;
        setText(description);
    }

    private void setUpTitle() {
        int padding = getResources().getDimensionPixelSize(R.dimen.card_padding);
        if (headerCardView != null) {
            if (title == null) {
                removeHeader();
                layoutView.setPadding(padding, padding, padding, padding);
                customLayout.setPadding(padding, padding, padding, padding);
            } else {
                addHeader(headerCardView);
                layoutView.setPadding(padding, 0, padding, padding);
                customLayout.setPadding(padding, 0, padding, padding);
            }
        }
        if (headerCardView != null && title != null)
            headerCardView.setText(title);
    }

    @Override
    public void setUpInnerLayout(View view) {
    }

    public static class DCardView implements DAdapter.DView {

        private CardViewItem cardViewItem;
        private OnDCardListener onDCardListener;

        private CharSequence title;
        private CharSequence description;
        private View view;
        private boolean fullspan;

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
            return new RecyclerView.ViewHolder(new CardViewItem(viewGroup.getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            cardViewItem = (CardViewItem) viewHolder.itemView;

            if (title != null) cardViewItem.setTitle(title);
            if (description != null) cardViewItem.setDescription(description);
            if (view != null) cardViewItem.setView(view);

            if (fullspan) {
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                int padding = cardViewItem.getContext().getResources().getDimensionPixelSize(R.dimen.basecard_padding);
                layoutParams.setMargins(padding, padding, padding, padding);
                cardViewItem.setLayoutParams(layoutParams);
            }

            setUpListener();
        }

        public void setTitle(CharSequence title) {
            this.title = title;
            if (cardViewItem != null) cardViewItem.setTitle(title);
        }

        public void setDescription(CharSequence description) {
            this.description = description;
            if (cardViewItem != null) cardViewItem.setDescription(description);
        }

        public void setView(View view) {
            this.view = view;
            if (cardViewItem != null) cardViewItem.setView(view);
        }

        public void setFullSpan(boolean fullspan) {
            this.fullspan = fullspan;
        }

        public CharSequence getDescription() {
            return description;
        }

        public void setOnDCardListener(OnDCardListener onDCardListener) {
            this.onDCardListener = onDCardListener;
            setUpListener();
        }

        private void setUpListener() {
            if (onDCardListener != null && cardViewItem != null) {
                cardViewItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onDCardListener != null) onDCardListener.onClick(DCardView.this);
                    }
                });
            }
        }

        public interface OnDCardListener {
            void onClick(DCardView dCardView);
        }

    }

}