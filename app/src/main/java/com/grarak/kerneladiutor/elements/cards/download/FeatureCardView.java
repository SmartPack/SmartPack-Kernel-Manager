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

package com.grarak.kerneladiutor.elements.cards.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.json.Downloads;

/**
 * Created by willi on 01.07.15.
 */
public class FeatureCardView extends BaseCardView {

    private TextView mainFeatureText;
    private TextView subFeatureText;

    public FeatureCardView(Context context, @NonNull Downloads.Feature feature) {
        super(context, R.layout.feature_cardview);

        mainFeatureText.setText(Html.fromHtml(feature.getItem()));
        mainFeatureText.setMovementMethod(LinkMovementMethod.getInstance());
        if (feature.hasItems()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String subFeature : feature.getItems())
                if (stringBuilder.length() == 0)
                    stringBuilder.append("\u2022").append(" ").append(subFeature);
                else stringBuilder.append("<br>").append("\u2022").append(" ").append(subFeature);

            subFeatureText.setText(Html.fromHtml(stringBuilder.toString()));
            subFeatureText.setMovementMethod(LinkMovementMethod.getInstance());
        } else subFeatureText.setVisibility(GONE);
    }

    @Override
    public void setUpInnerLayout(View view) {
        mainFeatureText = (TextView) view.findViewById(R.id.main_feature);
        subFeatureText = (TextView) view.findViewById(R.id.sub_feature);
    }

    @Override
    public void setMargin() {
        int padding = getResources().getDimensionPixelSize(R.dimen.basecard_padding);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, padding, 0, 0);
        setLayoutParams(layoutParams);
    }

    @Override
    public void setRadius() {
        setRadius(0);
    }

    public static class DFeatureCard implements DAdapter.DView {

        private final Downloads.Feature feature;

        public DFeatureCard(Downloads.Feature feature) {
            this.feature = feature;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new FeatureCardView(viewGroup.getContext(), feature)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

    }

}
