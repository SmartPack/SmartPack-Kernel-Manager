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
package com.grarak.kerneladiutor.views.recyclerview;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.ViewUtils;

/**
 * Created by willi on 06.08.16.
 */
public class AdView extends RecyclerViewItem {

    private boolean mLoaded;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_ad_view;
    }

    @Override
    public void onCreateView(View view) {
        final View text = view.findViewById(R.id.ad_text);

        final com.google.android.gms.ads.AdView ad =
                (com.google.android.gms.ads.AdView) view.findViewById(R.id.ad);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                ad.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
                mLoaded = false;
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ad.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                mLoaded = true;
            }
        });
        if (!mLoaded) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            ad.loadAd(adRequest);
        }

        view.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.dialogDonate(view.getContext()).show();
            }
        });

        setFullSpan(true);
        super.onCreateView(view);
    }

}
