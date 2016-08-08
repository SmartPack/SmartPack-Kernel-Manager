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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;
import com.startapp.android.publish.banner.BannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 08.08.16.
 */
public class AdBanner extends LinearLayout {

    private static final List<OfflineAd> sOfflineAds = new ArrayList<>();

    static {
        sOfflineAds.add(new OfflineAd("m5_kernel", R.drawable.banner_m5_kernel, "http://forum.xda-developers.com/z3/orig-development/kernel-m5-kernel-t3045319"));
        sOfflineAds.add(new OfflineAd("om5z_kernel", R.drawable.banner_om5z_kernel, "http://forum.xda-developers.com/xperia-z5/orig-development/kernel-om5z-kernel-t3405660"));
    }

    private Bitmap mOfflineAdBitmap;
    private boolean mLoaded;

    public AdBanner(Context context) {
        this(context, null);
    }

    public AdBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdBanner, defStyleAttr, 0);
        boolean onlyOfflineAds = a.getBoolean(R.styleable.AdBanner_onlyOfflineAds, false);
        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.adbanner_view, this);

        final ImageView adOffline = (ImageView) findViewById(R.id.offline_ad);
        if (mOfflineAdBitmap == null) {
            OfflineAd offlineAd = null;
            int min = -1;
            for (OfflineAd ad : sOfflineAds) {
                int shown = Prefs.getInt(ad.mName + "_shown", 0, context);
                if (min < 0 || shown < min) {
                    min = shown;
                    offlineAd = ad;
                }
            }

            if (offlineAd == null) {
                offlineAd = sOfflineAds.get(0);
            }

            mOfflineAdBitmap = BitmapFactory.decodeResource(getResources(), offlineAd.mBanner);
            int max = Math.round(getResources().getDimension(R.dimen.adbanner_width));
            adOffline.setImageBitmap(Utils.scaleDownBitmap(mOfflineAdBitmap, max, max));
            Prefs.saveInt(offlineAd.mName + "_shown", min + 1, context);

            final String link = offlineAd.mLink;
            adOffline.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.launchUrl(link, v.getContext());
                }
            });
        }

        if (!onlyOfflineAds) {
            Banner banner = (Banner) findViewById(R.id.ad_banner);
            banner.setBannerListener(new BannerListener() {
                @Override
                public void onReceiveAd(View view) {
                    adOffline.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    mLoaded = true;
                }

                @Override
                public void onFailedToReceiveAd(View view) {
                    adOffline.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    mLoaded = false;
                }

                @Override
                public void onClick(View view) {
                }
            });
        } else {
            mLoaded = true;
        }

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

    private static class OfflineAd {
        private String mName;
        private int mBanner;
        private String mLink;

        private OfflineAd(String name, @DrawableRes int banner, String link) {
            mName = name;
            mBanner = banner;
            mLink = link;
        }
    }

}
