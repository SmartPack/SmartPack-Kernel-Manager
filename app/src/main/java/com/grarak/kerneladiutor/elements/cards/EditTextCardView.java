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
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DParent;
import com.grarak.kerneladiutor.utils.Utils;

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

                final AppCompatEditText editText = new AppCompatEditText(getContext());
                editText.setGravity(Gravity.CENTER);
                editText.setTextColor(getContext().getResources().getColor(
                        Utils.DARKTHEME ? R.color.white : R.color.black));
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (value != null) editText.setText(value);
                if (inputType > -1) editText.setInputType(inputType);

                layout.addView(editText);

                new AlertDialog.Builder(getContext()).setView(layout)
                        .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        value = editText.getText().toString();
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
        void onApply(EditTextCardView editTextCardView, String value);
    }

    public static class DEditTextCard extends DParent {

        private EditTextCardView editTextCardView;

        private String title;
        private String description;
        private String value;
        private int inputType = -1;

        private OnDEditTextCardListener onDEditTextCardListener;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            super.onBindViewHolder(viewHolder);

            editTextCardView = (EditTextCardView) viewHolder.itemView;

            if (title != null) editTextCardView.setTitle(title);
            if (description != null) editTextCardView.setDescription(description);
            if (value != null) editTextCardView.setValue(value);
            if (inputType > -1) editTextCardView.setInputType(inputType);

            editTextCardView.setOnEditTextCardListener(new EditTextCardView.OnEditTextCardListener() {
                @Override
                public void onApply(EditTextCardView editTextCardView, String value) {
                    if (onDEditTextCardListener != null)
                        onDEditTextCardListener.onApply(DEditTextCard.this, value);
                }
            });
        }

        @Override
        public View getView(ViewGroup viewGroup) {
            return new EditTextCardView(viewGroup.getContext());
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

        public void setOnDEditTextCardListener(OnDEditTextCardListener onDEditTextCardListener) {
            this.onDEditTextCardListener = onDEditTextCardListener;
        }

        public interface OnDEditTextCardListener {
            void onApply(DEditTextCard dEditTextCard, String value);
        }

    }

}
