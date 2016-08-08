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
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;
import com.startapp.android.publish.banner.BannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by willi on 08.08.16.
 */
public class AdBanner extends LinearLayout {

    private static final List<OfflineAd> sOfflineAds = new ArrayList<>();

    static {
        sOfflineAds.add(new OfflineAd("m5_kernel", R.drawable.banner_m5_kernel, "http://forum.xda-developers.com/z3/orig-development/kernel-m5-kernel-t3045319"));
        sOfflineAds.add(new OfflineAd("om5z_kernel", R.drawable.banner_om5z_kernel, "http://forum.xda-developers.com/xperia-z5/orig-development/kernel-om5z-kernel-t3405660"));
    }

    private boolean mLoaded;

    public AdBanner(Context context) {
        this(context, null);
    }

    public AdBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final boolean onlyOfflineAds = new Random().nextInt(4) == 1;

        mLoaded = onlyOfflineAds;
        LayoutInflater.from(context).inflate(R.layout.adbanner_view, this);

        final View progress = findViewById(R.id.progress);
        final ImageView adOffline = (ImageView) findViewById(R.id.offline_ad);
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

        adOffline.setImageResource(offlineAd.mBanner);
        Prefs.saveInt(offlineAd.mName + "_shown", min + 1, context);

        final String title = offlineAd.mName;
        final String link = offlineAd.mLink;
        adOffline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentType("Offline ad")
                        .putContentId(title));

                new AlertDialog.Builder(v.getContext()).setMessage(v.getContext()
                        .getString(R.string.offline_ad)).setPositiveButton(v.getContext()
                        .getString(R.string.open_ad_anyway), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.launchUrl(link, v.getContext());
                    }
                }).setTitle(v.getContext().getString(R.string.warning)).show();
            }
        });

        Banner banner = (Banner) findViewById(R.id.ad_banner);
        if (!onlyOfflineAds) {
            banner.setBannerListener(new BannerListener() {
                @Override
                public void onReceiveAd(View view) {
                    progress.setVisibility(GONE);
                    adOffline.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    mLoaded = true;
                }

                @Override
                public void onFailedToReceiveAd(View view) {
                    progress.setVisibility(GONE);
                    adOffline.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    mLoaded = false;
                }

                @Override
                public void onClick(View view) {
                }
            });
        } else {
            progress.setVisibility(GONE);
            ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
            layoutParams.height = 0;
            layoutParams.width = 0;
            banner.requestLayout();
            adOffline.setVisibility(VISIBLE);
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
