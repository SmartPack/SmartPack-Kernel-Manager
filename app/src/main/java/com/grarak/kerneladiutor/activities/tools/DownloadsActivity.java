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
package com.grarak.kerneladiutor.activities.tools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.fragments.tools.downloads.AboutFragment;
import com.grarak.kerneladiutor.fragments.tools.downloads.DownloadKernelFragment;
import com.grarak.kerneladiutor.fragments.tools.downloads.FeaturesFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 06.07.16.
 */
public class DownloadsActivity extends BaseActivity {

    public static final String JSON_INTENT = "json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        initToolBar();

        SupportedDownloads.KernelContent content = new SupportedDownloads.KernelContent(getIntent().getStringExtra(JSON_INTENT));
        getSupportActionBar().setTitle(Utils.htmlFrom(content.getName()).toString());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        LinkedHashMap<String, Fragment> items = new LinkedHashMap<>();

        List<SupportedDownloads.KernelContent.Feature> features = content.getFeatures();
        List<SupportedDownloads.KernelContent.Download> downloads = content.getDownloads();

        if (content.getShortDescription() != null && content.getLongDescription() != null) {
            items.put(getString(R.string.about), AboutFragment.newInstance(content));
        }

        if (features.size() > 0) {
            items.put(getString(R.string.features), FeaturesFragment.newInstance(features));
        }

        if (downloads.size() > 0) {
            items.put(getString(R.string.downloads), DownloadKernelFragment.newInstance(downloads));
        }

        viewPager.setOffscreenPageLimit(items.size());
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), items);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private final LinkedHashMap<String, Fragment> mFragments;

        public PagerAdapter(FragmentManager fm, LinkedHashMap<String, Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(mFragments.keySet().toArray(new String[mFragments.size()])[position]);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.keySet().toArray(new String[mFragments.size()])[position];
        }

    }

}
