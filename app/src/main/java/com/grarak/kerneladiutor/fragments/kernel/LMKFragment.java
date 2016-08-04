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
package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.lmk.LMK;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class LMKFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<Integer, String> sProfiles = new LinkedHashMap<>();

    static {
        sProfiles.put(R.string.very_light, "512,1024,1280,2048,3072,4096");
        sProfiles.put(R.string.light, "1024,2048,2560,4096,6144,8192");
        sProfiles.put(R.string.medium, "1024,2048,4096,8192,12288,16384");
        sProfiles.put(R.string.aggressive, "2048,4096,8192,16384,24576,32768");
        sProfiles.put(R.string.very_aggressive, "4096,8192,16384,32768,49152,65536");
    }

    private List<SeekBarView> mMinFrees = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.LMK));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (LMK.hasAdaptive()) {
            adaptiveInit(items);
        }
        minfreeInit(items);
        profileInit(items);
        swapWait(items);
    }

    private void adaptiveInit(List<RecyclerViewItem> items) {
        SwitchView adaptive = new SwitchView();
        adaptive.setTitle(getString(R.string.lmk_adaptive));
        adaptive.setSummary(getString(R.string.lmk_adaptive_summary));
        adaptive.setChecked(LMK.isAdaptiveEnabled());
        adaptive.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                LMK.enableAdaptive(isChecked, getActivity());
            }
        });

        items.add(adaptive);
    }

    private void minfreeInit(List<RecyclerViewItem> items) {
        mMinFrees.clear();
        final List<String> minfrees = LMK.getMinFrees();
        String[] descriptions = getResources().getStringArray(R.array.lmk_names);

        for (int i = 0; i < minfrees.size(); i++) {
            SeekBarView minfree = new SeekBarView();
            minfree.setTitle(descriptions[i]);
            minfree.setUnit(getString(R.string.mb));
            minfree.setMax(1024);
            minfree.setProgress(Math.round(Utils.strToInt(minfrees.get(i)) / 256));

            final int minfreeposition = i;
            minfree.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    StringBuilder values = new StringBuilder();
                    for (int i = 0; i < minfrees.size(); i++) {
                        values.append(minfreeposition == i ? position * 256 : minfrees.get(i)).append(",");
                    }
                    values.setLength(values.length() - 1);
                    LMK.setMinFree(values.toString(), getActivity());
                    refreshMinFree();
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(minfree);
            mMinFrees.add(minfree);
        }
    }

    private void profileInit(List<RecyclerViewItem> items) {
        TitleView profilesTitle = new TitleView();
        profilesTitle.setText(getString(R.string.profile));
        items.add(profilesTitle);

        for (int id : sProfiles.keySet()) {
            DescriptionView profile = new DescriptionView();
            profile.setTitle(getString(id));
            profile.setSummary(sProfiles.get(id));
            profile.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    LMK.setMinFree(((DescriptionView) item).getSummary().toString(), getActivity());
                    refreshMinFree();
                }
            });

            items.add(profile);
        }
    }

    private void swapWait(List<RecyclerViewItem> items) {
        if (LMK.hasSwapWait()) {
            SwitchView swapWait = new SwitchView();
            swapWait.setTitle(getString(R.string.kill_lmk));
            swapWait.setSummary(getString(R.string.kill_lmk_summary));
            swapWait.setChecked(LMK.isSwapWaitEnabled());
            swapWait.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    LMK.enableSwapWait(isChecked, getActivity());
                }
            });

            items.add(swapWait);
        }

        if (LMK.hasSwapWaitPercent()) {
            Integer[] percentages = {0, 50, 66, 75, 80, 90};
            final Integer[] values = {1, 2, 3, 4, 5, 10};
            List<String> list = new ArrayList<>();
            for (int i : percentages) {
                list.add(i + "%");
            }

            SeekBarView swapWaitPercent = new SeekBarView();
            swapWaitPercent.setTitle(getString(R.string.kill_lmk_threshold));
            swapWaitPercent.setSummary(getString(R.string.kill_lmk_threshold_summary));
            swapWaitPercent.setItems(list);
            swapWaitPercent.setProgress(Arrays.asList(values).indexOf(LMK.getSwapWaitPercent()));
            swapWaitPercent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    LMK.setSwapWaitPercent(values[position], getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(swapWaitPercent);
        }
    }

    private void refreshMinFree() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final List<String> minfrees = LMK.getMinFrees();
                for (int i = 0; i < minfrees.size(); i++) {
                    mMinFrees.get(i).setProgress(Math.round(Utils.strToInt(minfrees.get(i)) / 256));
                }
            }
        }, 250);
    }

}
