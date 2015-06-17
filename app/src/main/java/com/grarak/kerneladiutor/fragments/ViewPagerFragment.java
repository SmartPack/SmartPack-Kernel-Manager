/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.04.15.
 */
public class ViewPagerFragment extends BaseFragment {

    private Adapter adapter;
    private CustomViewPager mViewPager;

    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        fragments.clear();

        View view = inflater.inflate(R.layout.viewpager, container, false);
        mViewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onSwipe(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                adapter = new Adapter(getChildFragmentManager(), fragments);
            }

            @Override
            protected Void doInBackground(Void... params) {
                init(savedInstanceState);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mViewPager.setAdapter(adapter);
                mViewPager.setOffscreenPageLimit(fragments.size());
                mViewPager.setCurrentItem(0);
            }
        }.execute();

        return view;
    }

    public void init(Bundle savedInstanceState) {
    }

    public void onSwipe(int page) {
    }

    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item);
    }

    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    public void allowSwipe(boolean swipe) {
        mViewPager.allowSwipe(swipe);
    }

    public void addFragment(Fragment fragment) {
        if (fragments.indexOf(fragment) < 0) {
            fragments.add(fragment);
            adapter.notifyDataSetChanged();
        }
    }

    private class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments;

        public Adapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
