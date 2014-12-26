package com.grarak.kerneladiutor.elements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 26.12.14.
 */
public class EditTextCardView extends CardViewItem {

    private String value;
    private int inputType = -1;
    private OnEditTextCardListener onEditTextCardListener;

    public EditTextCardView(Context context) {
        super(context);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout layout = new LinearLayout(getContext());
                layout.setPadding(30, 30, 30, 30);

                final EditText editText = new EditText(getContext());
                editText.setGravity(Gravity.CENTER);
                editText.setTextColor(getContext().getResources().getColor(android.R.color.black));
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (value != null) editText.setText(value);
                if (inputType > -1) editText.setInputType(inputType);

                layout.addView(editText);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(layout)
                        .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onEditTextCardListener != null)
                            onEditTextCardListener.onApply(EditTextCardView.this, editText.getText().toString());
                    }
                }).show();

            }
        });
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setOnEditTextCardListener(OnEditTextCardListener onEditTextCardListener) {
        this.onEditTextCardListener = onEditTextCardListener;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public interface OnEditTextCardListener {
        public void onApply(EditTextCardView editTextCardView, String value);
    }

    public static class DEditTextCard implements DAdapter.DView {

        private EditTextCardView editTextCardView;

        private String title;
        private String description;
        private String value;
        private int inputType = -1;

        private OnEditTextCardListener onEditTextCardListener;

        @Override
        public void onBindViewHolder(Holder viewHolder) {
            editTextCardView = (EditTextCardView) viewHolder.view;

            if (title != null) editTextCardView.setTitle(title);
            if (description != null) editTextCardView.setDescription(description);
            if (value != null) editTextCardView.setValue(value);
            if (inputType > -1) editTextCardView.setInputType(inputType);

            editTextCardView.setOnEditTextCardListener(new EditTextCardView.OnEditTextCardListener() {
                @Override
                public void onApply(EditTextCardView editTextCardView, String value) {
                    if (onEditTextCardListener != null)
                        onEditTextCardListener.onApply(DEditTextCard.this, value);
                }
            });
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup) {
            return new Holder(new EditTextCardView(viewGroup.getContext()));
        }

        public void setTitle(String title) {
            this.title = title;
            if (editTextCardView != null) editTextCardView.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (editTextCardView != null) editTextCardView.setDescription(description);
        }

        public void setValue(String value) {
            this.value = value;
            if (editTextCardView != null) editTextCardView.setValue(value);
        }

        public void setInputType(int inputType) {
            this.inputType = inputType;
            if (editTextCardView != null) editTextCardView.setInputType(inputType);
        }

        public void setOnEditTextCardListener(OnEditTextCardListener onEditTextCardListener) {
            this.onEditTextCardListener = onEditTextCardListener;
        }

        public interface OnEditTextCardListener {
            public void onApply(DEditTextCard dEditTextCard, String value);
        }

    }

}
