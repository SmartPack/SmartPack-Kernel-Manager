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
package com.grarak.kerneladiutor.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;
import com.startapp.android.publish.banner.BannerListener;

/**
 * Created by willi on 08.08.16.
 */
public class AdBanner extends LinearLayout {

    private boolean mLoaded;

    public AdBanner(Context context) {
        this(context, null);
    }

    public AdBanner(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdBanner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.adbanner_view, this);

        final View text = findViewById(R.id.ad_text);
        Banner banner = (Banner) findViewById(R.id.ad_banner);
        banner.setBannerListener(new BannerListener() {
            @Override
            public void onReceiveAd(View view) {
                text.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                mLoaded = true;
            }

            @Override
            public void onFailedToReceiveAd(View view) {
                text.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                mLoaded = false;
            }

            @Override
            public void onClick(View view) {
            }
        });

        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.dialogDonate(view.getContext()).show();
            }
        });
    }

    public void load(StartAppAd startAppAd) {
        if (!mLoaded) {
            startAppAd.loadAd();
        }
    }

}
