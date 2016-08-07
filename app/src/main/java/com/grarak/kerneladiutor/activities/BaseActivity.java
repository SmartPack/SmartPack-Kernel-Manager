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
package com.grarak.kerneladiutor.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

/**
 * Created by willi on 14.04.16.
 */
public class BaseActivity extends AppCompatActivity {

    private MoPubView mMoPubView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setTheme((Utils.DARK_THEME = Prefs.getBoolean("darktheme", false, this))
                ? R.style.ThemeDark : R.style.Theme);
        super.onCreate(savedInstanceState);
        if (Prefs.getBoolean("forceenglish", false, this)) {
            Utils.setLocale("en_US", this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && setStatusBarColor()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Utils.getColorPrimaryDarkColor(this));
        }
    }

    public AppBarLayout getAppBarLayout() {
        return (AppBarLayout) findViewById(R.id.appbarlayout);
    }

    public Toolbar getToolBar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    public void initToolBar() {
        Toolbar toolbar = getToolBar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initAd(String unit) {
        if (Utils.DONATED) return;

        findViewById(R.id.ad_parent).setVisibility(View.VISIBLE);
        final View text = findViewById(R.id.ad_text);
        mMoPubView = (MoPubView) findViewById(R.id.ad);
        mMoPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                banner.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                banner.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
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
        mMoPubView.setAdUnitId(unit);
        mMoPubView.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMoPubView != null) {
            mMoPubView.destroy();
        }
    }

    protected boolean setStatusBarColor() {
        return true;
    }

}
