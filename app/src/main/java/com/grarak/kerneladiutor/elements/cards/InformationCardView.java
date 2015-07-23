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
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;

/**
 * Created by willi on 25.04.15.
 */
public class InformationCardView extends BaseCardView {

    private TextView infoView;
    private Button okButton;
    private String infoText;
    private OnClickListener onClickListener;

    public InformationCardView(Context context) {
        super(context, R.layout.information_cardview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setTranslationZ(getResources()
                .getDimensionPixelSize(R.dimen.information_card_elevation));
    }

    @Override
    public void setUpInnerLayout(View view) {
        infoView = (TextView) view.findViewById(R.id.info_text);
        okButton = (Button) view.findViewById(R.id.ok_button);

        if (infoText != null) infoView.setText(infoText);
        if (onClickListener != null)
            okButton.setOnClickListener(onClickListener);
    }

    @Override
    public void setFocus() {
    }

    public void setText(String text) {
        infoText = text;
        if (infoView != null) infoView.setText(infoText);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (okButton != null) okButton.setOnClickListener(onClickListener);
    }

    public static class DInformationCard implements DAdapter.DView {

        private InformationCardView informationCardView;
        private String infoText;
        private OnClickListener onClickListener;

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
            informationCardView = (InformationCardView) viewHolder.itemView;

            if (infoText != null) informationCardView.setText(infoText);
            if (onClickListener != null)
                informationCardView.setOnClickListener(onClickListener);

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            int padding = informationCardView.getContext().getResources().getDimensionPixelSize(R.dimen.basecard_padding);
            layoutParams.setMargins(padding, padding, padding, padding);
            informationCardView.setLayoutParams(layoutParams);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new InformationCardView(viewGroup.getContext())) {
            };
        }

        public void setText(String text) {
            infoText = text;
            if (informationCardView != null) informationCardView.setText(infoText);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            if (informationCardView != null)
                informationCardView.setOnClickListener(onClickListener);
        }

    }

}
