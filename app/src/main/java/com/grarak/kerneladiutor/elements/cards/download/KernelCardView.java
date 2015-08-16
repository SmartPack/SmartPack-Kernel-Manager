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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.KernelActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.json.Downloads;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 20.06.15.
 */
public class KernelCardView extends BaseCardView {

    private ImageView logoView;
    private TextView nameView;
    private TextView descriptionView;

    public KernelCardView(Context context, @NonNull final Downloads.KernelContent kernelContent) {
        super(context, R.layout.kernel_cardview);

        Utils.loadImagefromUrl(kernelContent.getLogo(), logoView);

        nameView.setText(Html.fromHtml(kernelContent.getName()));
        nameView.setMovementMethod(LinkMovementMethod.getInstance());

        descriptionView.setText(Html.fromHtml(kernelContent.getShortDescription()));
        descriptionView.setMovementMethod(LinkMovementMethod.getInstance());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), KernelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KernelActivity.KERNEL_JSON_ARG, kernelContent.getJSON());
                i.putExtras(bundle);

                ActivityOptionsCompat activityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(),
                                logoView, KernelActivity.LOGO_ARG);
                ActivityCompat.startActivity((Activity) getContext(), i, activityOptions.toBundle());
            }
        });
    }

    @Override
    public void setUpInnerLayout(View view) {
        logoView = (ImageView) view.findViewById(R.id.logo);
        nameView = (TextView) view.findViewById(R.id.name);
        descriptionView = (TextView) view.findViewById(R.id.description);
    }

    public static class DKernelCard implements DAdapter.DView {

        private final Downloads.KernelContent kernelContent;

        public DKernelCard(Downloads.KernelContent kernelContent) {
            this.kernelContent = kernelContent;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new KernelCardView(viewGroup.getContext(), kernelContent)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        }

    }

}
