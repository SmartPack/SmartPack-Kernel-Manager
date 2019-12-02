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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by willi on 14.04.16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static HashMap<String, Integer> sAccentColors = new HashMap<>();
    private static HashMap<String, Integer> sAccentDarkColors = new HashMap<>();

    static {
        sAccentColors.put("red_accent", R.style.Theme_Red);
        sAccentColors.put("pink_accent", R.style.Theme_Pink);
        sAccentColors.put("purple_accent", R.style.Theme_Purple);
        sAccentColors.put("blue_accent", R.style.Theme_Blue);
        sAccentColors.put("green_accent", R.style.Theme_Green);
        sAccentColors.put("orange_accent", R.style.Theme_Orange);
        sAccentColors.put("brown_accent", R.style.Theme_Brown);
        sAccentColors.put("grey_accent", R.style.Theme_Grey);
        sAccentColors.put("blue_grey_accent", R.style.Theme_BlueGrey);
        sAccentColors.put("teal_accent", R.style.Theme_Teal);

        sAccentDarkColors.put("red_accent", R.style.Theme_Red_Dark);
        sAccentDarkColors.put("pink_accent", R.style.Theme_Pink_Dark);
        sAccentDarkColors.put("purple_accent", R.style.Theme_Purple_Dark);
        sAccentDarkColors.put("blue_accent", R.style.Theme_Blue_Dark);
        sAccentDarkColors.put("green_accent", R.style.Theme_Green_Dark);
        sAccentDarkColors.put("orange_accent", R.style.Theme_Orange_Dark);
        sAccentDarkColors.put("brown_accent", R.style.Theme_Brown_Dark);
        sAccentDarkColors.put("grey_accent", R.style.Theme_Grey_Dark);
        sAccentDarkColors.put("blue_grey_accent", R.style.Theme_BlueGrey_Dark);
        sAccentDarkColors.put("teal_accent", R.style.Theme_Teal_Dark);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Utils.DARK_THEME = Prefs.getBoolean("darktheme", true, this);
        int theme;
        String accent = Prefs.getString("accent_color", "blue_accent", this);
        if (Utils.DARK_THEME) {
            theme = sAccentDarkColors.get(accent);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            theme = sAccentColors.get(accent);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setTheme(theme);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && setStatusBarColor()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Prefs.getBoolean("forceenglish", false, newBase)) {
            super.attachBaseContext(wrap(newBase, new Locale("en_US")));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    public static ContextWrapper wrap(Context context, Locale newLocale) {

        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new ContextWrapper(context);
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

    protected boolean setStatusBarColor() {
        return true;
    }

    protected int statusBarColor() {
        return ViewUtils.getColorPrimaryDarkColor(this);
    }

}
