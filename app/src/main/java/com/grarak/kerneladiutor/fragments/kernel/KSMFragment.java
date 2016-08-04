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
import com.grarak.kerneladiutor.utils.kernel.ksm.KSM;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 28.06.16.
 */
public class KSMFragment extends RecyclerViewFragment {

    private List<DescriptionView> mInfos = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.KSM));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        infoInit(items);

        if (KSM.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.ksm));
            enable.setSummary(getString(R.string.ksm_summary));
            enable.setChecked(KSM.isEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    KSM.enableKsm(isChecked, getActivity());
                }
            });

            items.add(enable);
        }

        if (KSM.hasDeferredTimer()) {
            SwitchView deferredTimer = new SwitchView();
            deferredTimer.setTitle(getString(R.string.deferred_timer));
            deferredTimer.setSummary(getString(R.string.deferred_timer_summary));
            deferredTimer.setChecked(KSM.isDeferredTimerEnabled());
            deferredTimer.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    KSM.enableDeferredTimer(isChecked, getActivity());
                }
            });

            items.add(deferredTimer);
        }

        if (KSM.hasPagesToScan()) {
            SeekBarView pagesToScan = new SeekBarView();
            pagesToScan.setTitle(getString(R.string.pages_to_scan));
            pagesToScan.setMax(1024);
            pagesToScan.setProgress(KSM.getPagesToScan());
            pagesToScan.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KSM.setPagesToScan(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(pagesToScan);
        }

        if (KSM.hasSleepMilliseconds()) {
            SeekBarView sleepMilliseconds = new SeekBarView();
            sleepMilliseconds.setTitle(getString(R.string.sleep_milliseconds));
            sleepMilliseconds.setUnit(getString(R.string.ms));
            sleepMilliseconds.setMax(5000);
            sleepMilliseconds.setProgress(KSM.getSleepMilliseconds());
            sleepMilliseconds.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KSM.setSleepMilliseconds(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(sleepMilliseconds);
        }
    }

    private void infoInit(List<RecyclerViewItem> items) {
        mInfos.clear();
        for (int i = 0; i < KSM.getInfosSize(); i++) {
            if (KSM.hasInfo(i)) {
                DescriptionView info = new DescriptionView();
                info.setTitle(KSM.getInfoText(i, getActivity()));

                items.add(info);
                mInfos.add(info);
            }
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mInfos.size() > 0) {
            for (int i = 0; i < mInfos.size(); i++) {
                mInfos.get(i).setSummary(KSM.getInfo(i));
            }
        }
    }
}
