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
package com.grarak.kerneladiutor.activities.tools.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.activities.NavigationActivity;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by willi on 11.07.16.
 */
public class ProfileActivity extends BaseActivity {

    public static final String RESULT_ID_INTENT = "result_id";
    public static final String RESULT_COMMAND_INTENT = "result_command";

    private LinkedHashMap<String, Fragment> mItems = new LinkedHashMap<>();
    private int mCurPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItems.clear();
        boolean add = false;
        for (int id : NavigationActivity.sActualFragments.keySet()) {
            if (id == R.string.kernel) {
                add = true;
            } else if (add && NavigationActivity.sActualFragments.get(id) != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(id + "_key");
                mItems.put(getString(id), fragment == null ? NavigationActivity.sActualFragments.get(id)
                        : fragment);
            } else if (id == R.string.tools) {
                break;
            }
        }

        if (mItems.size() < 1) {
            Utils.toast(R.string.sections_disabled, this);
            finish();
            return;
        }

        setContentView(R.layout.activity_profile);

        Control.clearProfileCommands();
        Control.setProfileMode(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState == null) {
            ViewUtils.dialogBuilder(getString(R.string.profile_warning), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }, null, this).show();
        }

        viewPager.setOffscreenPageLimit(mItems.size());
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mItems);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkedHashMap<String, String> commandsList = Control.getProfileCommands();
                ArrayList<String> ids = new ArrayList<>();
                ArrayList<String> commands = new ArrayList<>();
                Collections.addAll(ids, commandsList.keySet().toArray(new String[commands.size()]));
                Collections.addAll(commands, commandsList.values().toArray(new String[commands.size()]));
                if (commands.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_ID_INTENT, ids);
                    intent.putExtra(RESULT_COMMAND_INTENT, commands);
                    setResult(0, intent);
                    finish();
                } else {
                    Utils.toast(R.string.no_changes, ProfileActivity.this);
                }
            }
        });
    }

    @Override
    public void finish() {
        Control.setProfileMode(false);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) mItems.values().toArray(new Fragment[mItems.size()])[mCurPosition];
        if (!fragment.onBackPressed()) {
            super.onBackPressed();
        }
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
