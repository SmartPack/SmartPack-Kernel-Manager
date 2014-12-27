package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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
    }

    @Override
    protected void setUpInnerLayout(View view) {
        super.setUpInnerLayout(view);

        headerCardView = new HeaderCardView(getContext());
        addHeader(headerCardView);

        descriptionView = (TextView) view.findViewById(R.id.description_view);
        checkBoxView = (CheckBox) view.findViewById(R.id.checkbox_view);

        if (titleText != null) headerCardView.setText(titleText);
        if (descriptionText != null) descriptionView.setText(descriptionText);
        checkBoxView.setChecked(checked);
    }

    public void setTitle(String title) {
        titleText = title;
        if (headerCardView != null) headerCardView.setText(title);
    }

    public void setDescription(String description) {
        descriptionText = description;
        if (descriptionView != null) descriptionView.setText(descriptionText);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checkBoxView != null) checkBoxView.setChecked(checked);
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
        public Holder onCreateViewHolder(ViewGroup viewGroup) {
            return new Holder(new CheckBoxCardItem(viewGroup.getContext()));
        }

        @Override
        public void onBindViewHolder(Holder viewHolder) {
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