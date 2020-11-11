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
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUInputBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.Misc;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.StuneBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.VoxPopuli;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.MSMLimiter;
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

    private XYGraphView mCPUUsagePrime;
    private SelectView mCPUMaxPrime;
    private SelectView mCPUMinPrime;
    private SelectView mCPUMaxScreenOffPrime;
    private SelectView mCPUGovernorPrime;

    private XYGraphView mCPUUsageBig;
    private SelectView mCPUMaxBig;
    private SelectView mCPUMinBig;
    private SelectView mCPUMaxScreenOffBig;
    private SelectView mCPUGovernorBig;

    private XYGraphView mCPUUsageLITTLE;
    private SelectView mCPUMaxLITTLE;
    private SelectView mCPUMinLITTLE;
    private SelectView mCPUMaxScreenOffLITTLE;
    private SelectView mCPUGovernorLITTLE;

    private SparseArray<SwitchView> mCoresPrime = new SparseArray<>();
    private SparseArray<SwitchView> mCoresBig = new SparseArray<>();
    private SparseArray<SwitchView> mCoresLITTLE = new SparseArray<>();

    private PathReaderFragment mGovernorTunableFragment;
    private Dialog mGovernorTunableErrorDialog;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mGovernorTunableFragment = new PathReaderFragment();
    }

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

        if (mCPUFreq.isPrimeCpu()) {
            CardView cpuPrimeCard = new CardView(getActivity());
            cpuPrimeCard.setTitle(getString(R.string.cluster_prime));

            mCPUUsagePrime = new XYGraphView();
            mCPUUsagePrime.setTitle(getString(R.string.cpu_usage_string, getString(R.string.cluster_prime)));

            cpuPrimeCard.addItem(mCPUUsagePrime);

            final List<Integer> PrimeCores = mCPUFreq.getPrimeCpuRange();

            mCoresPrime.clear();
            for (final int core : PrimeCores) {
                SwitchView coreSwitch = new SwitchView();
                coreSwitch.setSummary(getString(R.string.core, core + 1));

                mCoresPrime.put(core, coreSwitch);
                cpuPrimeCard.addItem(coreSwitch);
            }

            mCPUMaxPrime = new SelectView();
            mCPUMaxPrime.setTitle(getString(R.string.cpu_max_freq));
            mCPUMaxPrime.setSummary(getString(R.string.cpu_max_freq_summary));
            mCPUMaxPrime.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getPrimeCpu(), getActivity()));
            mCPUMaxPrime.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getPrimeCpu()).get(position),
                    PrimeCores.get(0), PrimeCores.get(PrimeCores.size() - 1), getActivity()));
            cpuPrimeCard.addItem(mCPUMaxPrime);

            mCPUMinPrime = new SelectView();
            mCPUMinPrime.setTitle(getString(R.string.cpu_min_freq));
            mCPUMinPrime.setSummary(getString(R.string.cpu_min_freq_summary));
            mCPUMinPrime.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getPrimeCpu(), getActivity()));
            mCPUMinPrime.setOnItemSelected((selectView, position, item)
                    -> mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getPrimeCpu()).get(position),
                    PrimeCores.get(0), PrimeCores.get(PrimeCores.size() - 1), getActivity()));
            cpuPrimeCard.addItem(mCPUMinPrime);

            if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getPrimeCpu())) {
                mCPUMaxScreenOffPrime = new SelectView();
                mCPUMaxScreenOffPrime.setTitle(getString(R.string.cpu_max_screen_off_freq));
                mCPUMaxScreenOffPrime.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                mCPUMaxScreenOffPrime.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getPrimeCpu(), getActivity()));
                mCPUMaxScreenOffPrime.setOnItemSelected((selectView, position, item) -> mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getPrimeCpu()).get(position),
                        PrimeCores.get(0), PrimeCores.get(PrimeCores.size() - 1), getActivity()));
                cpuPrimeCard.addItem(mCPUMaxScreenOffPrime);
            }

            mCPUGovernorPrime = new SelectView();
            mCPUGovernorPrime.setTitle(getString(R.string.cpu_governor));
            mCPUGovernorPrime.setSummary(getString(R.string.cpu_governor_summary));
            mCPUGovernorPrime.setItems(mCPUFreq.getGovernors());
            mCPUGovernorPrime.setOnItemSelected((selectView, position, item) -> mCPUFreq.setGovernor(item, PrimeCores.get(0), PrimeCores.get(PrimeCores.size() - 1),
                    getActivity()));
            cpuPrimeCard.addItem(mCPUGovernorPrime);

            DescriptionView governorTunablesPrime = new DescriptionView();
            governorTunablesPrime.setTitle(getString(R.string.cpu_governor_tunables));
            governorTunablesPrime.setSummary(getString(R.string.governor_tunables_summary));
            governorTunablesPrime.setOnItemClickListener(item
                    -> showGovernorTunables(PrimeCores.get(0), PrimeCores.get(PrimeCores.size() - 1)));
            cpuPrimeCard.addItem(governorTunablesPrime);

            items.add(cpuPrimeCard);
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
            setForegroundText(governor);
            mGovernorTunableFragment.setError(getString(R.string.tunables_error, governor));
            mGovernorTunableFragment.setPath(mCPUFreq.getGovernorTunablesPath(min, governor), min, max,
                    ApplyOnBootFragment.CPU);
            showForeground();
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
            getHandler().postDelayed(() -> {
                        powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled());
                    },
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
            getHandler().postDelayed(() -> {
                        cfsScheduler.setItem(Misc.getCurrentCFSScheduler());
                    },
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
                getHandler().postDelayed(() -> {
                            cpuQuietEnable.setChecked(Misc.isCpuQuietEnabled());
                        },
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
                getHandler().postDelayed(() -> {
                            NrMaxCPUs.setValue(Misc.getCpuQuietNrMaxCPUs());
                        },
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
                getHandler().postDelayed(() -> {
                            NrMinCPUs.setValue(Misc.getCpuQuietNrMinCPUs());
                        },
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

    private float[] mCPUUsages;
    private boolean[] mCPUStates;
    private int[] mCPUFreqs;
    private int mCPUMaxFreqPrime;
    private int mCPUMinFreqPrime;
    private int mCPUMaxScreenOffFreqPrime;
    private String mCPUGovernorStrPrime;
    private int mCPUMaxFreqBig;
    private int mCPUMinFreqBig;
    private int mCPUMaxScreenOffFreqBig;
    private String mCPUGovernorStrBig;
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

        if (mCPUMaxPrime != null) {
            mCPUMaxFreqPrime = mCPUFreq.getMaxFreq(mCPUFreq.getPrimeCpu(), mCPUMaxFreqPrime == 0);
        }
        if (mCPUMinPrime != null) {
            mCPUMinFreqPrime = mCPUFreq.getMinFreq(mCPUFreq.getPrimeCpu(), mCPUMinFreqPrime == 0);
        }
        if (mCPUMaxScreenOffPrime != null) {
            mCPUMaxScreenOffFreqPrime = mCPUFreq.getMaxScreenOffFreq(mCPUFreq.getPrimeCpu(),
                    mCPUMaxScreenOffFreqPrime == 0);
        }
        if (mCPUGovernorPrime != null) {
            mCPUGovernorStrPrime = mCPUFreq.getGovernor(mCPUFreq.getPrimeCpu(),
                    mCPUGovernorStrPrime == null);
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
                if (mCPUFreq.isPrimeCpu()) {
                    refreshUsages(mCPUUsages, mCPUUsagePrime, mCPUFreq.getPrimeCpuRange(), mCPUStates);
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
        if (mCPUMaxPrime != null && mCPUMaxFreqPrime != 0) {
            mCPUMaxPrime.setItem((mCPUMaxFreqPrime / 1000) + getString(R.string.mhz));
        }
        if (mCPUMinPrime != null && mCPUMinFreqPrime != 0) {
            mCPUMinPrime.setItem((mCPUMinFreqPrime / 1000) + getString(R.string.mhz));
        }
        if (mCPUMaxScreenOffPrime != null && mCPUMaxScreenOffFreqPrime != 0) {
            mCPUMaxScreenOffPrime.setItem((mCPUMaxScreenOffFreqPrime / 1000) + getString(R.string.mhz));
        }
        if (mCPUGovernorPrime != null && mCPUGovernorStrPrime != null && !mCPUGovernorStrPrime.isEmpty()) {
            mCPUGovernorPrime.setItem(mCPUGovernorStrPrime);
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
                if (mCPUFreq.isPrimeCpu()) {
                    refreshCores(mCoresPrime, mCPUFreqs);
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