package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by willi on 23.12.14.
 */
public class CardViewItem extends BaseCardView {

    private HeaderCardView headerCardView;

    private String title;
    private String description;

    public CardViewItem(Context context) {
        super(context);

        headerCardView = new HeaderCardView(getContext());

        setUpTitle();
        if (description != null) setText(description);
    }

    public void setTitle(String title) {
        this.title = title;
        setUpTitle();
    }

    public void setDescription(String description) {
        this.description = description;
        setText(description);
    }

    private void setUpTitle() {
        if (headerCardView != null) {
            if (title == null) removeHeader();
            else addHeader(headerCardView);
        }
        if (headerCardView != null && title != null)
            headerCardView.setText(title);
    }

    public static class DCardView implements DAdapter.DView {

        private CardViewItem cardViewItem;
        private OnDCardListener onDCardListener;

        private String title;
        private String description;
        private View view;

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup) {
            return new Holder(new CardViewItem(viewGroup.getContext()));
        }

        @Override
        public void onBindViewHolder(Holder viewHolder) {
            cardViewItem = (CardViewItem) viewHolder.view;

            if (title != null) cardViewItem.setTitle(title);
            if (description != null) cardViewItem.setDescription(description);
            if (view != null) cardViewItem.setView(view);
            setUpListener();
        }

        public void setTitle(String title) {
            this.title = title;
            if (cardViewItem != null) cardViewItem.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (cardViewItem != null) cardViewItem.setDescription(description);
        }

        public void setView(View view) {
            this.view = view;
            if (cardViewItem != null) cardViewItem.setView(view);
        }

        public String getDescription() {
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
            public void onClick(DCardView dCardView);
        }

    }

}