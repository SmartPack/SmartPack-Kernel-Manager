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

import io.karim.MaterialTabs;

/**
 * Created by willi on 07.04.15.
 */
public class ViewPagerFragment extends BaseFragment {

    protected LayoutInflater inflater;
    protected ViewGroup container;
    private Adapter adapter;
    private CustomViewPager mViewPager;
    protected MaterialTabs mTabs;

    private List<ViewPagerItem> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        items.clear();

        View view = getParentView();
        mViewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        mTabs = (MaterialTabs) view.findViewById(R.id.tabs);

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
                adapter = new Adapter(getChildFragmentManager(), items);
                try {
                    if (isAdded()) preInit(savedInstanceState);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (isAdded()) init(savedInstanceState);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mViewPager.setAdapter(adapter);
                mViewPager.setOffscreenPageLimit(items.size());
                mViewPager.setCurrentItem(0);
                try {
                    if (isAdded()) postInit(savedInstanceState);

                    mTabs.setTextColorSelected(getResources().getColor(R.color.white));
                    mTabs.setTextColorUnselected(getResources().getColor(R.color.textcolor_dark));
                    mTabs.setIndicatorColor(getResources().getColor(R.color.white));
                    mTabs.setViewPager(mViewPager);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        return view;
    }

    public View getParentView() {
        return inflater.inflate(R.layout.viewpager, container, false);
    }

    public void preInit(Bundle savedInstanceState) {
    }

    public void init(Bundle savedInstanceState) {
    }

    public void postInit(Bundle savedInstanceState) {
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

    public void addFragment(ViewPagerItem item) {
        if (items.indexOf(item) < 0) {
            items.add(item);
            adapter.notifyDataSetChanged();
        }
    }

    public int getCount() {
        return items.size();
    }

    public Fragment getFragment(int position) {
        return items.get(position).getFragment();
    }

    public void showTabs(boolean visible) {
        mTabs.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private class Adapter extends FragmentPagerAdapter {

        private final List<ViewPagerItem> items;

        public Adapter(FragmentManager fragmentManager, List<ViewPagerItem> items) {
            super(fragmentManager);
            this.items = items;
        }

        @Override
        public Fragment getItem(int i) {
            return items.get(i).getFragment();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items.get(position).getTitle() != null ? items.get(position).getTitle() : super.getPageTitle(position);
        }

    }

    public class ViewPagerItem {

        private final Fragment fragment;
        private final String title;

        public ViewPagerItem(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public String getTitle() {
            return title;
        }

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
