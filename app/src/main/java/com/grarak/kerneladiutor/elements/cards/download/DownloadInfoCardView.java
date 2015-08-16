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
import android.widget.ImageButton;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.json.Downloads;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 01.07.15.
 */
public class DownloadInfoCardView extends BaseCardView {

    private View linksLayout;
    private ImageButton xdaButton;
    private ImageButton githubButton;
    private ImageButton googlePlusButton;
    private ImageButton paypalButton;

    private TextView shortDescription;
    private TextView longDescription;

    public DownloadInfoCardView(Context context, @NonNull Downloads.KernelContent kernelContent) {
        super(context, R.layout.downloadinfo_cardview);

        final String xda = kernelContent.getXDA();
        final String github = kernelContent.getGithub();
        final String googlePlus = kernelContent.getGooglePlus();
        final String paypal = kernelContent.getPaypal();

        if (xda != null || github != null || googlePlus != null || paypal != null) {
            linksLayout.setVisibility(VISIBLE);

            if (xda != null) {
                xdaButton.setVisibility(VISIBLE);
                xdaButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(getContext(), xda);
                    }
                });
            }

            if (github != null) {
                githubButton.setVisibility(VISIBLE);
                githubButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(getContext(), github);
                    }
                });
            }

            if (googlePlus != null) {
                googlePlusButton.setVisibility(VISIBLE);
                googlePlusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(getContext(), googlePlus);
                    }
                });
            }

            if (paypal != null) {
                paypalButton.setVisibility(VISIBLE);
                paypalButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(getContext(), paypal);
                    }
                });
            }
        }

        shortDescription.setText(Html.fromHtml(kernelContent.getShortDescription()));
        shortDescription.setMovementMethod(LinkMovementMethod.getInstance());
        longDescription.setText(Html.fromHtml(kernelContent.getLongDescription()));
        longDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setUpInnerLayout(View view) {
        linksLayout = view.findViewById(R.id.links_layout);
        xdaButton = (ImageButton) view.findViewById(R.id.xda_button);
        githubButton = (ImageButton) view.findViewById(R.id.github_button);
        googlePlusButton = (ImageButton) view.findViewById(R.id.googleplus_button);
        paypalButton = (ImageButton) view.findViewById(R.id.paypal_button);

        shortDescription = (TextView) view.findViewById(R.id.short_description);
        longDescription = (TextView) view.findViewById(R.id.long_description);

        if (Utils.DARKTHEME)
            view.findViewById(R.id.links_divider).setBackgroundColor(getResources().getColor(R.color.divider_background_dark));
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

    public static class DDDownloadInfoCard implements DAdapter.DView {

        private final Downloads.KernelContent kernelContent;

        public DDDownloadInfoCard(Downloads.KernelContent kernelContent) {
            this.kernelContent = kernelContent;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new DownloadInfoCardView(viewGroup.getContext(), kernelContent)) {
            };
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }
    }

}
