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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

/**
 * Created by willi on 06.08.16.
 */
public class AdView extends RecyclerViewItem {

    private MoPubView mMoPubView;
    private boolean mLoaded;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_ad_view;
    }

    @Override
    public void onCreateView(View view) {
        final View text = view.findViewById(R.id.ad_text);

        mMoPubView = (MoPubView) view.findViewById(R.id.ad);
        mMoPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                banner.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                mLoaded = true;
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                banner.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
                mLoaded = false;
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
            }
        });
        mMoPubView.setAdUnitId("d82dbc3941bc446fa327fb840c4695f7");
        if (!mLoaded) {
            mMoPubView.loadAd();
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

    public void destroy() {
        if (mMoPubView != null) {
            mMoPubView.destroy();
            mLoaded = false;
        }
    }

}
