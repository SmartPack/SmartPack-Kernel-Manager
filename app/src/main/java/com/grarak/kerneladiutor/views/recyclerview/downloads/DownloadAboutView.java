/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.views.recyclerview.downloads;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 07.07.16.
 */
public class DownloadAboutView extends RecyclerViewItem {

    private final SupportedDownloads.KernelContent mKernelContent;

    public DownloadAboutView(SupportedDownloads.KernelContent kernelContent) {
        mKernelContent = kernelContent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_download_about_view;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);

        final String xda = mKernelContent.getXDA();
        final String github = mKernelContent.getGitHub();
        final String googlePlus = mKernelContent.getGooglePlus();
        final String paypal = mKernelContent.getPayPal();

        if (xda != null || github != null || googlePlus != null || paypal != null) {
            view.findViewById(R.id.links_layout).setVisibility(View.VISIBLE);

            if (xda != null) {
                view.findViewById(R.id.xda_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.xda_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(xda, view.getContext());
                    }
                });
            }

            if (github != null) {
                view.findViewById(R.id.github_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.github_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(github, view.getContext());
                    }
                });
            }

            if (googlePlus != null) {
                view.findViewById(R.id.googleplus_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.googleplus_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(googlePlus, view.getContext());
                    }
                });
            }

            if (paypal != null) {
                view.findViewById(R.id.paypal_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.paypal_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.launchUrl(paypal, view.getContext());
                    }
                });
            }
        }

        TextView shortDescription = (TextView) view.findViewById(R.id.short_description);
        TextView longDescription = (TextView) view.findViewById(R.id.long_description);

        shortDescription.setText(Utils.htmlFrom(mKernelContent.getShortDescription()));
        longDescription.setText(Utils.htmlFrom(mKernelContent.getLongDescription()));
        shortDescription.setMovementMethod(LinkMovementMethod.getInstance());
        longDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
