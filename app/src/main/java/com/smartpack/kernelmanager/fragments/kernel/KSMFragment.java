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
package com.smartpack.kernelmanager.fragments.kernel;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.ksm.KSM;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 28.06.16.
 */
public class KSMFragment extends RecyclerViewFragment {

    private KSM mKSM;

    private List<DescriptionView> mInfos = new ArrayList<>();

    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    @Override
    protected void init() {
        super.init();

        mKSM = KSM.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        infoInit(items);

        CardView ksm = new CardView(getActivity());
        if (mKSM.isUKSM()) {
            ksm.setTitle("Ultra Kernel Samepage Merging");
        } else {
            ksm.setTitle("Kernel Samepage Merging");
        }
        ksm.setFullSpan(true);

        if (mKSM.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.ksm));
            enable.setSummary(getString(R.string.ksm_summary));
            enable.setChecked(mKSM.isEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mKSM.enableKsm(isChecked, getActivity());
                }
            });

            ksm.addItem(enable);
        }

	if (mKSM.hasCpuGovernor()) {
            SelectView governor = new SelectView();
            governor.setTitle(getString(R.string.uksm_governor));
            governor.setSummary(getString(R.string.uksm_governor_summary));
            governor.setItems(mKSM.getCpuGovernors());
            governor.setItem(mKSM.getCpuGovernor());
            governor.setOnItemSelected((selectView, position, item)
                    -> mKSM.setCpuGovernor(item, getActivity()));
             ksm.addItem(governor);
        }

        if (mKSM.hasDeferredTimer()) {
            SwitchView deferredTimer = new SwitchView();
            deferredTimer.setTitle(getString(R.string.deferred_timer));
            deferredTimer.setSummary(getString(R.string.deferred_timer_summary));
            deferredTimer.setChecked(mKSM.isDeferredTimerEnabled());
            deferredTimer.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mKSM.enableDeferredTimer(isChecked, getActivity());
                }
            });

            ksm.addItem(deferredTimer);
        }

        if (mKSM.hasPagesToScan()) {
            SeekBarView pagesToScan = new SeekBarView();
            pagesToScan.setTitle(getString(R.string.pages_to_scan));
            pagesToScan.setMax(1024);
            pagesToScan.setProgress(mKSM.getPagesToScan());
            pagesToScan.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mKSM.setPagesToScan(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            ksm.addItem(pagesToScan);
        }

        if (mKSM.hasSleepMilliseconds()) {
            SeekBarView sleepMilliseconds = new SeekBarView();
            sleepMilliseconds.setTitle(getString(R.string.sleep_milliseconds));
            sleepMilliseconds.setUnit(getString(R.string.ms));
            sleepMilliseconds.setMax(5000);
            sleepMilliseconds.setOffset(50);
            sleepMilliseconds.setProgress(mKSM.getSleepMilliseconds() / 50);
            sleepMilliseconds.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mKSM.setSleepMilliseconds(position * 50, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            ksm.addItem(sleepMilliseconds);
        }

        if (mKSM.hasMaxCpuPercentage()) {
            SeekBarView maxCpuPercentage = new SeekBarView();
            maxCpuPercentage.setTitle(getString(R.string.max_cpu_usage));
            maxCpuPercentage.setSummary(getString(R.string.max_cpu_usage_summary));
            maxCpuPercentage.setUnit("%");
            maxCpuPercentage.setProgress(mKSM.getMaxCpuPercentage());
            maxCpuPercentage.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mKSM.setMaxCpuPercentage(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            ksm.addItem(maxCpuPercentage);
        }

	if (ksm.size() > 0) {
            items.add(ksm);
        }
    }

    private void infoInit(List<RecyclerViewItem> items) {
        mInfos.clear();
        for (int i = 0; i < mKSM.getInfosSize(); i++) {
            if (mKSM.hasInfo(i)) {
                DescriptionView info = new DescriptionView();
                info.setTitle(mKSM.getInfoText(i, getActivity()));

                items.add(info);
                mInfos.add(info);
            }
        }
    }

    private List<String> mInfoSummaries = new ArrayList<>();

    @Override
    protected void refreshThread() {
        super.refreshThread();

        mInfoSummaries.clear();
        for (int i = 0; i < mInfos.size(); i++) {
            mInfoSummaries.add(mKSM.getInfo(i));
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mInfos.size() > 0 && mInfos.size() == mInfoSummaries.size()) {
            for (int i = 0; i < mInfos.size(); i++) {
                mInfos.get(i).setSummary(mInfoSummaries.get(i));
            }
        }
    }
}
