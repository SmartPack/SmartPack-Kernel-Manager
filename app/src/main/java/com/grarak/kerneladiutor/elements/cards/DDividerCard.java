package com.grarak.kerneladiutor.elements.cards;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;

/**
 * Created by willi on 08.08.15.
 */
public class DDividerCard implements DAdapter.DView {

    private View parentView;

    private TextView titleView;
    private View moreView;

    private String titleText;
    private String descriptionText;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public BaseFragment getFragment() {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        parentView = viewHolder.itemView;

        titleView = (TextView) parentView.findViewById(R.id.title);
        moreView = parentView.findViewById(R.id.more_view);

        if (titleText != null) titleView.setText(titleText);
        setUpDescription();

        StaggeredGridLayoutManager.LayoutParams layoutParams =
                new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setFullSpan(true);
        int padding = parentView.getContext().getResources().getDimensionPixelSize(R.dimen.basecard_padding);
        layoutParams.setMargins(0, padding, 0, 0);
        parentView.setLayoutParams(layoutParams);
    }

    public void setText(String text) {
        titleText = text;
        if (titleView != null) titleView.setText(titleText);
    }

    public void setDescription(String description) {
        descriptionText = description;
        setUpDescription();
    }

    private void setUpDescription() {
        if (parentView != null && moreView != null && descriptionText != null) {
            moreView.setVisibility(View.VISIBLE);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(parentView.getContext()).setMessage(descriptionText).show();
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.divider_view, viewGroup, false)) {
        };
    }

}
