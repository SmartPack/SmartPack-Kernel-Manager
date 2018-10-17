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
package com.grarak.kerneladiutor.fragments.other;

import android.os.Bundle;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.AppUpdaterTask;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 22.07.16.
 */
public class AboutFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<String, String> sLibraries = new LinkedHashMap<>();

    static {
        sLibraries.put("Google,AndroidX Library", "https://developer.android.com/jetpack/androidx");
        sLibraries.put("Google,NavigationView library", "https://developer.android.com/reference/com/google/android/material/navigation/NavigationView");
        sLibraries.put("Joe Maples,Spectrum", "https://github.com/frap129/spectrum");
        sLibraries.put("Ozodrukh,CircularReveal", "https://github.com/ozodrukh/CircularReveal");
        sLibraries.put("Roman Nurik,dashclock", "https://github.com/romannurik/dashclock");
        sLibraries.put("Matthew Precious,swirl", "https://github.com/mattprecious/swirl");
        sLibraries.put("Lopez Mikhael,CircularImageView", "https://github.com/lopspower/CircularImageView");
        sLibraries.put("Square,picasso", "https://github.com/square/picasso");
        sLibraries.put("Square,okhttp", "https://github.com/square/okhttp");
        sLibraries.put("CyanogenMod,CyanogenMod Platform SDK", "https://github.com/CyanogenMod/cm_platform_sdk");
        sLibraries.put("Javier Santos,AppUpdater", "https://github.com/javiersantos/AppUpdater");
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new InfoFragment());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        aboutInit(items);
        librariesInit(items);
    }

    private void aboutInit(List<RecyclerViewItem> items) {

        CardView about = new CardView(getActivity());
        about.setTitle(getString(R.string.app_name));

        DescriptionView versioninfo = new DescriptionView();
        versioninfo.setTitle(getString(R.string.version));
        versioninfo.setSummary("v" + BuildConfig.VERSION_NAME);

        about.addItem(versioninfo);

        DescriptionView updatecheck = new DescriptionView();
        updatecheck.setTitle(getString(R.string.check_update));
        updatecheck.setSummary(getString(R.string.check_update_summary));
        updatecheck.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                AppUpdaterTask.appCheck(getActivity());
            }
        });

        DescriptionView support = new DescriptionView();
        support.setTitle(getString(R.string.support));
        support.setSummary(getString(R.string.support_summary));
        support.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://forum.xda-developers.com/android/apps-games/app-smartpack-kernel-manager-t3854717", getActivity());
            }
        });

        DescriptionView sourcecode = new DescriptionView();
        sourcecode.setTitle(getString(R.string.source_code));
        sourcecode.setSummary(getString(R.string.source_code_summary));
        sourcecode.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Manager", requireActivity());
            }
        });

        DescriptionView changelogs = new DescriptionView();
        changelogs.setTitle(getString(R.string.change_logs));
        changelogs.setSummary(getString(R.string.change_logs_summary));
        changelogs.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Manager/master/change-logs.md", getActivity());
            }
        });

        DescriptionView donatetome = new DescriptionView();
        donatetome.setTitle(getString(R.string.donate_me));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://www.paypal.me/sunilpaulmathew", getActivity());
            }
        });

        about.addItem(updatecheck);
        about.addItem(support);
        about.addItem(sourcecode);
        about.addItem(changelogs);
        about.addItem(donatetome);
        items.add(about);
    }

    private void librariesInit(List<RecyclerViewItem> items) {

        CardView cardView = new CardView(getActivity());
        cardView.setTitle(getString(R.string.libraries_used));

        for (final String lib : sLibraries.keySet()) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setTitle(lib.split(",")[1]);
            descriptionView.setSummary(lib.split(",")[0]);
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    Utils.launchUrl(sLibraries.get(lib), getActivity());
                }
            });

            cardView.addItem(descriptionView);
        }
        items.add(cardView);
    }

    public static class InfoFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
            rootView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.launchUrl("https://github.com/sunilpaulmathew", getActivity());
                }
            });
            return rootView;
        }
    }

}
