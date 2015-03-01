package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 01.03.15.
 */
public class DividerCardView extends BaseCardView {

    private TextView textView;
    private String text;

    public DividerCardView(Context context) {
        super(context, R.layout.divider_cardview);
    }

    @Override
    public void setUpInnerLayout(View view) {
        textView = (TextView) view;

        if (text != null) textView.setText(text);
    }

    public void setText(String text) {
        this.text = text;
        if (textView != null) textView.setText(text);
    }

    public static class DDividerCard implements DAdapter.DView {

        private DividerCardView dividerCardView;

        private String text;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new DividerCardView(viewGroup.getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            dividerCardView = (DividerCardView) viewHolder.itemView;

            if (text != null) dividerCardView.setText(text);

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            dividerCardView.setLayoutParams(layoutParams);
        }

        public void setText(String text) {
            this.text = text;
            if (dividerCardView != null) dividerCardView.setText(text);
        }

    }

}
