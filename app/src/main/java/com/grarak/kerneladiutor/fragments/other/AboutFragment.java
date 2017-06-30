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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
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
        sLibraries.put("Google,v4 Support Library", "https://developer.android.com/topic/libraries/support-library/features.html#v4");
        sLibraries.put("Google,v7 appcompat library", "https://developer.android.com/topic/libraries/support-library/features.html#v7");
        sLibraries.put("Google,v7 cardview library", "https://developer.android.com/topic/libraries/support-library/features.html#v7");
        sLibraries.put("Google,Design Support Library", "https://developer.android.com/topic/libraries/support-library/features.html#design");
        sLibraries.put("Google,v7/v14 Preference", "https://developer.android.com/reference/android/support/v7/preference/package-summary.html");
        sLibraries.put("Google,v7 recyclerview library", "https://developer.android.com/topic/libraries/support-library/features.html#v7");
        sLibraries.put("Ozodrukh,CircularReveal", "https://github.com/ozodrukh/CircularReveal");
        sLibraries.put("Roman Nurik,dashclock", "https://github.com/romannurik/dashclock");
        sLibraries.put("Google,AdMob", "https://developers.google.com/android/guides/setup");
        sLibraries.put("Matthew Precious,swirl", "https://github.com/mattprecious/swirl");
        sLibraries.put("Lopez Mikhael,CircularImageView", "https://github.com/lopspower/CircularImageView");
        sLibraries.put("Square,picasso", "https://github.com/square/picasso");
        sLibraries.put("CyanogenMod,CyanogenMod Platform SDK", "https://github.com/CyanogenMod/cm_platform_sdk");
        sLibraries.put("Twitter,Fabric", "https://get.fabric.io");
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new InfoFragment());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        librariesInit(items);
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
                    Utils.launchUrl("https://github.com/Grarak", getActivity());
                }
            });
            return rootView;
        }
    }

}
