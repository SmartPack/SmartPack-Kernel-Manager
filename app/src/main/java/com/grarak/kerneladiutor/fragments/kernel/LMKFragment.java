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
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.lmk.LMK;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class LMKFragment extends RecyclerViewFragment {

    private final LinkedHashMap<Integer, String> sProfiles = new LinkedHashMap<>();

    private List<SeekBarView> mMinFrees = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        sProfiles.clear();
        sProfiles.put(R.string.very_light, getAdjustedSize(1, 2, 3, 4, 5, 6));
        sProfiles.put(R.string.light, getAdjustedSize(2, 3, 4, 5, 6, 7));
        sProfiles.put(R.string.medium, getAdjustedSize(3, 4, 5, 6, 7, 9));
        sProfiles.put(R.string.aggressive, getAdjustedSize(2, 3, 6, 10, 14, 15));
        sProfiles.put(R.string.very_aggressive, getAdjustedSize(3, 4, 5, 11, 15, 16));

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    private String getAdjustedSize(int... offsets) {
        long memTotal = Device.MemInfo.getInstance().getTotalMem() * 1024L / 100L / 4L;
        StringBuilder stringBuilder = new StringBuilder();
        for (int offset : offsets) {
            stringBuilder.append(String.valueOf((int) memTotal * offset)).append(",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (LMK.hasAdaptive()) {
            adaptiveInit(items);
        }
        profileInit(items);
    }

    private void adaptiveInit(List<RecyclerViewItem> items) {
        CardView lmkCard = new CardView(getActivity());
        lmkCard.setTitle(getString(R.string.lmk));

	if (LMK.hasAdaptive()) {
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

            lmkCard.addItem(adaptive);
	}

        mMinFrees.clear();
        List<String> minfrees = LMK.getMinFrees();
        String[] descriptions = getResources().getStringArray(R.array.lmk_names);

        for (int i = 0; i < minfrees.size(); i++) {
            if (i == descriptions.length) break;
            SeekBarView minfree = new SeekBarView();
            minfree.setTitle(descriptions[i]);
            minfree.setUnit(getString(R.string.mb));
            minfree.setMax(1024);
            minfree.setProgress(Math.round(Utils.strToInt(minfrees.get(i)) / 256));

            final int minfreeposition = i;
            minfree.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    List<String> minfrees = LMK.getMinFrees();
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

            lmkCard.addItem(minfree);
            mMinFrees.add(minfree);
        }

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

            lmkCard.addItem(swapWait);
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

            lmkCard.addItem(swapWaitPercent);
        }

        if (lmkCard.size() > 0) {
            items.add(lmkCard);
        }
    }

    private void profileInit(List<RecyclerViewItem> items) {
        CardView profileCard = new CardView(getActivity());
        profileCard.setTitle(getString(R.string.profile));

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

            profileCard.addItem(profile);
        }

	if (profileCard.size() > 0) {
            items.add(profileCard);
        }
    }

    private void refreshMinFree() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final List<String> minfrees = LMK.getMinFrees();
                for (int i = 0; i < minfrees.size(); i++) {
                    if (i == mMinFrees.size()) break;
                    mMinFrees.get(i).setProgress(Math.round(Utils.strToInt(minfrees.get(i)) / 256));
                }
            }
        }, 250);
    }

}
