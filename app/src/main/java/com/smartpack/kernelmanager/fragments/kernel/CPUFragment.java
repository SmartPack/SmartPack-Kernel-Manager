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

import android.content.Intent;
import android.text.InputType;
import android.util.SparseArray;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.CPUBoostActivity;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.cpu.Misc;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUInputBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.StuneBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.VoxPopuli;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.MSMLimiter;
import com.smartpack.kernelmanager.utils.tools.PathReader;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;
import com.smartpack.kernelmanager.views.recyclerview.XYGraphView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class CPUFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;

    private XYGraphView mCPUUsageBig;
    private SelectView mCPUMaxBig;
    private SelectView mCPUMinBig;
    private SelectView mCPUMaxScreenOffBig;
    private SelectView mCPUGovernorBig;

    private XYGraphView mCPUUsageMid;
    private SelectView mCPUMaxMid;
    private SelectView mCPUMinMid;
    private SelectView mCPUMaxScreenOffMid;
    private SelectView mCPUGovernorMid;

    private XYGraphView mCPUUsageLITTLE;
    private SelectView mCPUMaxLITTLE;
    private SelectView mCPUMinLITTLE;
    private SelectView mCPUMaxScreenOffLITTLE;
    private SelectView mCPUGovernorLITTLE;

    private final SparseArray<SwitchView> mCoresBig = new SparseArray<>();
    private final SparseArray<SwitchView> mCoresMid = new SparseArray<>();
    private final SparseArray<SwitchView> mCoresLITTLE = new SparseArray<>();

    private Dialog mGovernorTunableErrorDialog;

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(DescriptionFragment.newInstance(getString(mCPUFreq.getCpuCount() > 1 ?
                R.string.cores : R.string.cores_singular, mCPUFreq.getCpuCount()), Device.getBoard()));

        if (mGovernorTunableErrorDialog != null) {
            mGovernorTunableErrorDialog.show();
        }

        showCPUNotes();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        cpuInit(items);
        if (Misc.hasMcPowerSaving()) {
            mcPowerSavingInit(items);
        }
        if (Misc.hasPowerSavingWq()) {
            powerSavingWqInit(items);
        }
        if (Misc.hasCFSScheduler()) {
            cfsSchedulerInit(items);
        }
        if (Misc.hasCpuQuiet()) {
            cpuQuietInit(items);
        }
        if (CPUBoost.getInstance().supported() || CPUInputBoost.getInstance().supported()
                || StuneBoost.supported() || VoxPopuli.hasVoxpopuliTunable()) {
            cpuBoostInit(items);
        }
        if (MSMLimiter.supported()) {
            msmlimiterInit(items);
        }
    }

    private void cpuInit(List<RecyclerViewItem> items) {
        CardView cpuCard = new CardView(getActivity());
        if (!mCPUFreq.isBigLITTLE()) {
            cpuCard.setTitle(getString(R.string.cpu));
        }

        mCPUUsageBig = new XYGraphView();
        if (mCPUFreq.isBigLITTLE()) {
            mCPUUsageBig.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_big)));
        } else {
            mCPUUsageBig.setTitle(getString(R.string.cpu_usage));
        }

        cpuCard.addItem(mCPUUsageBig);

        final List<Integer> bigCores = mCPUFreq.getBigCpuRange();

        mCoresBig.clear();
        for (final int core : bigCores) {
            SwitchView coreSwitch = new SwitchView();
            coreSwitch.setSummary(getString(R.string.core, core + 1));

            mCoresBig.put(core, coreSwitch);
            cpuCard.addItem(coreSwitch);
        }

        mCPUMaxBig = new SelectView();
        mCPUMaxBig.setTitle(getString(R.string.cpu_max_freq));
        mCPUMaxBig.setSummary(getString(R.string.cpu_max_freq_summary));
        mCPUMaxBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMaxBig.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                bigCores.get(bigCores.size() - 1), getActivity()));
        cpuCard.addItem(mCPUMaxBig);

        mCPUMinBig = new SelectView();
        mCPUMinBig.setTitle(getString(R.string.cpu_min_freq));
        mCPUMinBig.setSummary(getString(R.string.cpu_min_freq_summary));
        mCPUMinBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMinBig.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                bigCores.get(bigCores.size() - 1), getActivity()));
        cpuCard.addItem(mCPUMinBig);

        if (mCPUFreq.hasMaxScreenOffFreq()) {
            mCPUMaxScreenOffBig = new SelectView();
            mCPUMaxScreenOffBig.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mCPUMaxScreenOffBig.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            mCPUMaxScreenOffBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            mCPUMaxScreenOffBig.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                    bigCores.get(bigCores.size() - 1), getActivity()));
            cpuCard.addItem(mCPUMaxScreenOffBig);
        }

        if (mCPUFreq.isBigLITTLE()) {
            cpuCard.setTitle(getString(R.string.cluster_big));
        }

        mCPUGovernorBig = new SelectView();
        mCPUGovernorBig.setTitle(getString(R.string.cpu_governor));
        mCPUGovernorBig.setSummary(getString(R.string.cpu_governor_summary));
        mCPUGovernorBig.setItems(mCPUFreq.getGovernors());
        mCPUGovernorBig.setOnItemSelected((selectView, position, item) -> mCPUFreq.setGovernor(item, bigCores.get(0), bigCores.get(bigCores.size() - 1),
                getActivity()));
        cpuCard.addItem(mCPUGovernorBig);

        DescriptionView governorTunablesBig = new DescriptionView();
        governorTunablesBig.setTitle(getString(R.string.cpu_governor_tunables));
        governorTunablesBig.setSummary(getString(R.string.governor_tunables_summary));
        governorTunablesBig.setOnItemClickListener(item -> showGovernorTunables(bigCores.get(0), bigCores.get(bigCores.size() - 1)));
        cpuCard.addItem(governorTunablesBig);

        items.add(cpuCard);

        if (mCPUFreq.isBigLITTLE()) {
            if (mCPUFreq.hasMidCpu()) {
                CardView cpuMidCard = new CardView(getActivity());
                cpuMidCard.setTitle(getString(R.string.cluster_middle));

                mCPUUsageMid = new XYGraphView();
                mCPUUsageMid.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_middle)));

                mCPUUsageLITTLE = new XYGraphView();
                mCPUUsageLITTLE.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_middle)));

                cpuMidCard.addItem(mCPUUsageMid);

                final List<Integer> MidCores = mCPUFreq.getMidCpuRange();

                mCoresMid.clear();
                for (final int core : MidCores) {
                    SwitchView coreSwitch = new SwitchView();
                    coreSwitch.setSummary(getString(R.string.core, core + 1));

                    mCoresMid.put(core, coreSwitch);
                    cpuMidCard.addItem(coreSwitch);
                }

                mCPUMaxMid = new SelectView();
                mCPUMaxMid.setTitle(getString(R.string.cpu_max_freq));
                mCPUMaxMid.setSummary(getString(R.string.cpu_max_freq_summary));
                mCPUMaxMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                mCPUMaxMid.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                        MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                cpuMidCard.addItem(mCPUMaxMid);

                mCPUMinMid = new SelectView();
                mCPUMinMid.setTitle(getString(R.string.cpu_min_freq));
                mCPUMinMid.setSummary(getString(R.string.cpu_min_freq_summary));
                mCPUMinMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                mCPUMinMid.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                        MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                cpuMidCard.addItem(mCPUMinMid);

                if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getMidCpu())) {
                    mCPUMaxScreenOffMid = new SelectView();
                    mCPUMaxScreenOffMid.setTitle(getString(R.string.cpu_max_screen_off_freq));
                    mCPUMaxScreenOffMid.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                    mCPUMaxScreenOffMid.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                    mCPUMaxScreenOffMid.setOnItemSelected((selectView, position, item)
                            -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position),
                            MidCores.get(0), MidCores.get(MidCores.size() - 1), getActivity()));
                    cpuMidCard.addItem(mCPUMaxScreenOffMid);
                }

                mCPUGovernorMid = new SelectView();
                mCPUGovernorMid.setTitle(getString(R.string.cpu_governor));
                mCPUGovernorMid.setSummary(getString(R.string.cpu_governor_summary));
                mCPUGovernorMid.setItems(mCPUFreq.getGovernors());
                mCPUGovernorMid.setOnItemSelected((selectView, position, item)
                        -> mCPUFreq.setGovernor(item, MidCores.get(0), MidCores.get(MidCores.size() - 1),
                        getActivity()));
                cpuMidCard.addItem(mCPUGovernorMid);

                DescriptionView governorTunablesMid = new DescriptionView();
                governorTunablesMid.setTitle(getString(R.string.cpu_governor_tunables));
                governorTunablesMid.setSummary(getString(R.string.governor_tunables_summary));
                governorTunablesMid.setOnItemClickListener(item
                        -> showGovernorTunables(MidCores.get(0), MidCores.get(MidCores.size() - 1)));
                cpuMidCard.addItem(governorTunablesMid);

                items.add(cpuMidCard);
            }

            CardView cpuLITTLECard = new CardView(getActivity());
            cpuLITTLECard.setTitle(getString(R.string.cluster_little));

            mCPUUsageLITTLE = new XYGraphView();
            mCPUUsageLITTLE.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_little)));

            cpuLITTLECard.addItem(mCPUUsageLITTLE);

            final List<Integer> LITTLECores = mCPUFreq.getLITTLECpuRange();

            mCoresLITTLE.clear();
            for (final int core : LITTLECores) {
                SwitchView coreSwitch = new SwitchView();
                coreSwitch.setSummary(getString(R.string.core, core + 1));

                mCoresLITTLE.put(core, coreSwitch);
                cpuLITTLECard.addItem(coreSwitch);
            }

            mCPUMaxLITTLE = new SelectView();
            mCPUMaxLITTLE.setTitle(getString(R.string.cpu_max_freq));
            mCPUMaxLITTLE.setSummary(getString(R.string.cpu_max_freq_summary));
            mCPUMaxLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMaxLITTLE.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                    LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
            cpuLITTLECard.addItem(mCPUMaxLITTLE);

            mCPUMinLITTLE = new SelectView();
            mCPUMinLITTLE.setTitle(getString(R.string.cpu_min_freq));
            mCPUMinLITTLE.setSummary(getString(R.string.cpu_min_freq_summary));
            mCPUMinLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMinLITTLE.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                    LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
            cpuLITTLECard.addItem(mCPUMinLITTLE);

            if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getLITTLECpu())) {
                mCPUMaxScreenOffLITTLE = new SelectView();
                mCPUMaxScreenOffLITTLE.setTitle(getString(R.string.cpu_max_screen_off_freq));
                mCPUMaxScreenOffLITTLE.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                mCPUMaxScreenOffLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                mCPUMaxScreenOffLITTLE.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                        LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity()));
                cpuLITTLECard.addItem(mCPUMaxScreenOffLITTLE);
            }

            mCPUGovernorLITTLE = new SelectView();
            mCPUGovernorLITTLE.setTitle(getString(R.string.cpu_governor));
            mCPUGovernorLITTLE.setSummary(getString(R.string.cpu_governor_summary));
            mCPUGovernorLITTLE.setItems(mCPUFreq.getGovernors());
            mCPUGovernorLITTLE.setOnItemSelected((selectView, position, item) -> mCPUFreq.setGovernor(item, LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1),
                    getActivity()));
            cpuLITTLECard.addItem(mCPUGovernorLITTLE);

            DescriptionView governorTunablesLITTLE = new DescriptionView();
            governorTunablesLITTLE.setTitle(getString(R.string.cpu_governor_tunables));
            governorTunablesLITTLE.setSummary(getString(R.string.governor_tunables_summary));
            governorTunablesLITTLE.setOnItemClickListener(item -> showGovernorTunables(LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1)));
            cpuLITTLECard.addItem(governorTunablesLITTLE);

            items.add(cpuLITTLECard);
        }
    }

    private void showGovernorTunables(int min, int max) {
        boolean offline = mCPUFreq.isOffline(min);
        if (offline) {
            mCPUFreq.onlineCpu(min, true, false, null);
        }
        String governor = mCPUFreq.getGovernor(min, false);
        if (governor.isEmpty()) {
            mGovernorTunableErrorDialog = ViewUtils.dialogBuilder(getString(R.string.cpu_governor_tunables_read_error),
                    null, (dialog, which) -> {
                    }, dialog -> mGovernorTunableErrorDialog = null, getActivity());
            mGovernorTunableErrorDialog.show();
        } else {
            new PathReader(min, max, governor, mCPUFreq.getGovernorTunablesPath(min, governor), getString(R.string.tunables_error,
                    governor), ApplyOnBootFragment.CPU, requireActivity()).launch();
        }
        if (offline) {
            mCPUFreq.onlineCpu(min, false, false, null);
        }
    }

    private void mcPowerSavingInit(List<RecyclerViewItem> items) {
        SelectView mcPowerSaving = new SelectView();
        mcPowerSaving.setTitle(getString(R.string.mc_power_saving));
        mcPowerSaving.setSummary(getString(R.string.mc_power_saving_summary));
        mcPowerSaving.setItems(Arrays.asList(getResources().getStringArray(R.array.mc_power_saving_items)));
        mcPowerSaving.setItem(Misc.getCurMcPowerSaving());
        mcPowerSaving.setOnItemSelected((selectView, position, item) -> Misc.setMcPowerSaving(position, getActivity()));

        items.add(mcPowerSaving);
    }

    private void powerSavingWqInit(List<RecyclerViewItem> items) {
        SwitchView powerSavingWq = new SwitchView();
        powerSavingWq.setSummary(getString(R.string.power_saving_wq));
        powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled());
        powerSavingWq.addOnSwitchListener((switchView, isChecked) -> {
            Misc.enablePowerSavingWq(isChecked, getActivity());
            getHandler().postDelayed(() -> powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled()),
                    500);
        });

        items.add(powerSavingWq);
    }

    private void cfsSchedulerInit(List<RecyclerViewItem> items) {
        SelectView cfsScheduler = new SelectView();
        cfsScheduler.setTitle(getString(R.string.cfs_scheduler_policy));
        cfsScheduler.setSummary(getString(R.string.cfs_scheduler_policy_summary));
        cfsScheduler.setItems(Misc.getAvailableCFSSchedulers());
        cfsScheduler.setItem(Misc.getCurrentCFSScheduler());
        cfsScheduler.setOnItemSelected((selectView, position, item) -> {
            Misc.setCFSScheduler(item, getActivity());
            getHandler().postDelayed(() -> cfsScheduler.setItem(Misc.getCurrentCFSScheduler()),
                    500);
        });

        items.add(cfsScheduler);
    }

    private void cpuQuietInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> views = new ArrayList<>();
        CardView cpuQuietCard = new CardView(getActivity());
        cpuQuietCard.setTitle(getString(R.string.cpu_quiet));

        if (Misc.hasCpuQuietEnable()) {
            SwitchView cpuQuietEnable = new SwitchView();
            cpuQuietEnable.setSummary(getString(R.string.cpu_quiet));
            cpuQuietEnable.setChecked(Misc.isCpuQuietEnabled());
            cpuQuietEnable.addOnSwitchListener((switchView, isChecked) -> {
                Misc.enableCpuQuiet(isChecked, getActivity());
                getHandler().postDelayed(() -> cpuQuietEnable.setChecked(Misc.isCpuQuietEnabled()),
                        500);
            });

            views.add(cpuQuietEnable);
        }

        if (Misc.hasCpuQuietGovernors()) {
            SelectView cpuQuietGovernors = new SelectView();
            cpuQuietGovernors.setSummary(getString(R.string.cpu_quiet_governor));
            cpuQuietGovernors.setItems(Misc.getCpuQuietAvailableGovernors());
            cpuQuietGovernors.setItem(Misc.getCpuQuietCurGovernor());
            cpuQuietGovernors.setOnItemSelected((selectView, position, item) -> Misc.setCpuQuietGovernor(item, getActivity()));

            views.add(cpuQuietGovernors);
        }

        if (Misc.hasCpuQuietNrMaxCPUs()) {
            GenericSelectView NrMaxCPUs = new GenericSelectView();
            NrMaxCPUs.setSummary(getString(R.string.nr_max_cpu));
            NrMaxCPUs.setValue(Misc.getCpuQuietNrMaxCPUs());
            NrMaxCPUs.setInputType(InputType.TYPE_CLASS_NUMBER);
            NrMaxCPUs.setOnGenericValueListener((genericSelectView, value) -> {
                Misc.setCpuQuietNrMaxCPUs(value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> NrMaxCPUs.setValue(Misc.getCpuQuietNrMaxCPUs()),
                        500);
            });

            views.add(NrMaxCPUs);
        }

        if (Misc.hasCpuQuietNrMinCPUs()) {
            GenericSelectView NrMinCPUs = new GenericSelectView();
            NrMinCPUs.setSummary(getString(R.string.nr_min_cpu));
            NrMinCPUs.setValue(Misc.getCpuQuietNrMinCPUs());
            NrMinCPUs.setInputType(InputType.TYPE_CLASS_NUMBER);
            NrMinCPUs.setOnGenericValueListener((genericSelectView, value) -> {
                Misc.setCpuQuietNrMinCPUs(value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> NrMinCPUs.setValue(Misc.getCpuQuietNrMinCPUs()),
                        500);
            });

            views.add(NrMinCPUs);
        }

        if (views.size() > 0) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setSummary(getString(R.string.cpu_quiet_summary));
            cpuQuietCard.addItem(descriptionView);

            for (RecyclerViewItem item : views) {
                cpuQuietCard.addItem(item);
            }
            items.add(cpuQuietCard);
        }
    }

    private void cpuBoostInit(List<RecyclerViewItem> items) {
        CardView cpuBoost = new CardView(getActivity());
        DescriptionView cpuBoostSettings = new DescriptionView();
        cpuBoostSettings.setTitle(getString(R.string.cpu_boost));
        cpuBoostSettings.setSummary(getString(R.string.cpu_boost_settings));
        cpuBoostSettings.setOnItemClickListener(item -> {
            Intent cpuboost = new Intent(getActivity(), CPUBoostActivity.class);
            startActivity(cpuboost);
        });
        cpuBoost.addItem(cpuBoostSettings);
        items.add(cpuBoost);
    }

    private void msmlimiterInit(List<RecyclerViewItem> items) {
        CardView msmLimiter = new CardView(getActivity());
        msmLimiter.setTitle(getString(R.string.msm_limiter));

        if (MSMLimiter.hasenable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.msm_limiter_summary));
            enable.setChecked(MSMLimiter.isEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> MSMLimiter.enable(isChecked, getActivity()));

            msmLimiter.addItem(enable);
        }

        if (MSMLimiter.hasFreqControl()) {
            SwitchView freqControl = new SwitchView();
            freqControl.setTitle(getString(R.string.freq_control));
            freqControl.setSummary(getString(R.string.freq_control_summary));
            freqControl.setChecked(MSMLimiter.isFreqControlEnabled());
            freqControl.addOnSwitchListener((switchView, isChecked) -> MSMLimiter.enableFreqControl(isChecked, getActivity()));

            msmLimiter.addItem(freqControl);
        }

        if (MSMLimiter.hasDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(MSMLimiter.isDebugMaskEnabled());
            debugMask.addOnSwitchListener((switchView, isChecked) -> MSMLimiter.enableDebugMask(isChecked, getActivity()));

            msmLimiter.addItem(debugMask);
        }

        if (msmLimiter.size() > 0) {
            items.add(msmLimiter);
        }
    }

    private void showCPUNotes() {
        if (Prefs.getBoolean("cpuNotes", true, requireActivity())) {
            new Dialog(requireActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.cpu_note))
                    .setCancelable(false)
                    .setPositiveButton(R.string.got_it, (dialog, id) -> Prefs.saveBoolean("cpuNotes", false, requireActivity())).show();
        }
    }

    private float[] mCPUUsages;
    private boolean[] mCPUStates;
    private int[] mCPUFreqs;
    private int mCPUMaxFreqBig;
    private int mCPUMinFreqBig;
    private int mCPUMaxScreenOffFreqBig;
    private String mCPUGovernorStrBig;
    private int mCPUMaxFreqMid;
    private int mCPUMinFreqMid;
    private int mCPUMaxScreenOffFreqMid;
    private String mCPUGovernorStrMid;
    private int mCPUMaxFreqLITTLE;
    private int mCPUMinFreqLITTLE;
    private int mCPUMaxScreenOffFreqLITTLE;
    private String mCPUGovernorStrLITTLE;

    @Override
    protected void refreshThread() {
        super.refreshThread();

        try {
            mCPUUsages = mCPUFreq.getCpuUsage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mCPUStates = new boolean[mCPUFreq.getCpuCount()];
        for (int i = 0; i < mCPUStates.length; i++) {
            mCPUStates[i] = !mCPUFreq.isOffline(i);
        }

        mCPUFreqs = new int[mCPUFreq.getCpuCount()];
        for (int i = 0; i < mCPUFreqs.length; i++) {
            mCPUFreqs[i] = mCPUFreq.getCurFreq(i);
        }

        if (mCPUMaxBig != null) {
            mCPUMaxFreqBig = mCPUFreq.getMaxFreq(mCPUMaxFreqBig == 0);
        }
        if (mCPUMinBig != null) {
            mCPUMinFreqBig = mCPUFreq.getMinFreq(mCPUMinFreqBig == 0);
        }
        if (mCPUMaxScreenOffBig != null) {
            mCPUMaxScreenOffFreqBig = mCPUFreq.getMaxScreenOffFreq(mCPUMaxScreenOffFreqBig == 0);
        }
        if (mCPUGovernorBig != null) {
            mCPUGovernorStrBig = mCPUFreq.getGovernor(mCPUGovernorStrBig == null);
        }
        if (mCPUMaxMid != null) {
            mCPUMaxFreqMid = mCPUFreq.getMaxFreq(mCPUFreq.getMidCpu(), mCPUMaxFreqMid == 0);
        }
        if (mCPUMinMid != null) {
            mCPUMinFreqMid = mCPUFreq.getMinFreq(mCPUFreq.getMidCpu(), mCPUMinFreqMid == 0);
        }
        if (mCPUMaxScreenOffMid != null) {
            mCPUMaxScreenOffFreqMid = mCPUFreq.getMaxScreenOffFreq(mCPUFreq.getMidCpu(),
                    mCPUMaxScreenOffFreqMid == 0);
        }
        if (mCPUGovernorMid != null) {
            mCPUGovernorStrMid = mCPUFreq.getGovernor(mCPUFreq.getMidCpu(),
                    mCPUGovernorStrMid == null);
        }
        if (mCPUMaxLITTLE != null) {
            mCPUMaxFreqLITTLE = mCPUFreq.getMaxFreq(mCPUFreq.getLITTLECpu(), mCPUMaxFreqLITTLE == 0);
        }
        if (mCPUMinLITTLE != null) {
            mCPUMinFreqLITTLE = mCPUFreq.getMinFreq(mCPUFreq.getLITTLECpu(), mCPUMinFreqLITTLE == 0);
        }
        if (mCPUMaxScreenOffLITTLE != null) {
            mCPUMaxScreenOffFreqLITTLE = mCPUFreq.getMaxScreenOffFreq(mCPUFreq.getLITTLECpu(),
                    mCPUMaxScreenOffFreqLITTLE == 0);
        }
        if (mCPUGovernorLITTLE != null) {
            mCPUGovernorStrLITTLE = mCPUFreq.getGovernor(mCPUFreq.getLITTLECpu(),
                    mCPUGovernorStrLITTLE == null);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mCPUUsages != null && mCPUStates != null) {
            refreshUsages(mCPUUsages, mCPUUsageBig, mCPUFreq.getBigCpuRange(), mCPUStates);
            if (mCPUFreq.isBigLITTLE()) {
                refreshUsages(mCPUUsages, mCPUUsageLITTLE, mCPUFreq.getLITTLECpuRange(), mCPUStates);
                if (mCPUFreq.hasMidCpu()) {
                    refreshUsages(mCPUUsages, mCPUUsageMid, mCPUFreq.getMidCpuRange(), mCPUStates);
                }
            }
        }

        if (mCPUMaxBig != null && mCPUMaxFreqBig != 0) {
            mCPUMaxBig.setItem((mCPUMaxFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinBig != null && mCPUMinFreqBig != 0) {
            mCPUMinBig.setItem((mCPUMinFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffBig != null && mCPUMaxScreenOffFreqBig != 0) {
            mCPUMaxScreenOffBig.setItem((mCPUMaxScreenOffFreqBig / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorBig != null && mCPUGovernorStrBig != null && !mCPUGovernorStrBig.isEmpty()) {
            mCPUGovernorBig.setItem(mCPUGovernorStrBig);
        }
        if (mCPUMaxMid != null && mCPUMaxFreqMid != 0) {
            mCPUMaxMid.setItem((mCPUMaxFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinMid != null && mCPUMinFreqMid != 0) {
            mCPUMinMid.setItem((mCPUMinFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffMid != null && mCPUMaxScreenOffFreqMid != 0) {
            mCPUMaxScreenOffMid.setItem((mCPUMaxScreenOffFreqMid / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorMid != null && mCPUGovernorStrMid != null && !mCPUGovernorStrMid.isEmpty()) {
            mCPUGovernorMid.setItem(mCPUGovernorStrMid);
        }
        if (mCPUMaxLITTLE != null && mCPUMaxFreqLITTLE != 0) {
            mCPUMaxLITTLE.setItem((mCPUMaxFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinLITTLE != null && mCPUMinFreqLITTLE != 0) {
            mCPUMinLITTLE.setItem((mCPUMinFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffLITTLE != null && mCPUMaxScreenOffFreqLITTLE != 0) {
            mCPUMaxScreenOffLITTLE.setItem((mCPUMaxScreenOffFreqLITTLE / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorLITTLE != null && mCPUGovernorStrLITTLE != null && !mCPUGovernorStrLITTLE.isEmpty()) {
            mCPUGovernorLITTLE.setItem(mCPUGovernorStrLITTLE);
        }

        if (mCPUFreqs != null) {
            refreshCores(mCoresBig, mCPUFreqs);
            if (mCPUFreq.isBigLITTLE()) {
                refreshCores(mCoresLITTLE, mCPUFreqs);
                if (mCPUFreq.hasMidCpu()) {
                    refreshCores(mCoresMid, mCPUFreqs);
                }
            }
        }
    }

    private void refreshUsages(float[] usages, XYGraphView graph, List<Integer> cores,
                               boolean[] coreStates) {
        if (graph != null) {
            float average = 0;
            int size = 0;
            for (int core : cores) {
                if (core + 1 < usages.length) {
                    if (coreStates[core]) {
                        average += usages[core + 1];
                    }
                    size++;
                }
            }
            average /= size;
            graph.setText(Math.round(average) + "%");
            graph.addPercentage(Math.round(average));
        }
    }

    private void refreshCores(SparseArray<SwitchView> array, int[] freqs) {
        try {
            for (int i = 0; i < array.size(); i++) {
                SwitchView switchView = array.valueAt(i);
                if (switchView != null) {
                    final int core = array.keyAt(i);
                    int freq = freqs[core];

                    String freqText = freq == 0 ? getString(R.string.offline) : (freq / 1000)
                            + getString(R.string.mhz);
                    switchView.clearOnSwitchListener();
                    switchView.setChecked(freq != 0);
                    switchView.setSummary(getString(R.string.core, core + 1) + " - " + freqText);
                    switchView.addOnSwitchListener((switchView1, isChecked) -> {
                        if (core == 0) {
                            Utils.snackbar(getRootView(), getString(R.string.no_offline_core));
                        } else {
                            mCPUFreq.onlineCpu(core, isChecked, true, getActivity());
                        }
                    });
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

}