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

import android.content.DialogInterface;
import android.text.InputType;
import android.util.SparseArray;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.cpu.Misc;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;
import com.smartpack.kernelmanager.views.recyclerview.XYGraphView;

import com.smartpack.kernelmanager.utils.kernel.cpu.CPUInputBoost;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.MSMLimiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class CPUFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;
    private CPUBoost mCPUBoost;
    private CPUInputBoost mCPUInputBoost;

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
        mCPUBoost = CPUBoost.getInstance();
        mCPUInputBoost = CPUInputBoost.getInstance();
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
        if (mCPUBoost.supported() || mCPUInputBoost.supported() || Misc.hasCpuTouchBoost()
                || Misc.hasDynStuneBoost() || Misc.hasDynStuneBoostDuration()
                || Misc.hasSchedBoostSettings()) {
            cpuBoostInit(items);
        }
        if (MSMLimiter.supported()) {
            msmlimiterInit(items);
        }
        if (Misc.hasVoxpopuliTunable()) {
            powerhalTunabledInit(items);
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
        mCPUMaxBig.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mCPUFreq.setMaxFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                        bigCores.get(bigCores.size() - 1), getActivity());
            }
        });
        cpuCard.addItem(mCPUMaxBig);

        mCPUMinBig = new SelectView();
        mCPUMinBig.setTitle(getString(R.string.cpu_min_freq));
        mCPUMinBig.setSummary(getString(R.string.cpu_min_freq_summary));
        mCPUMinBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
        mCPUMinBig.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mCPUFreq.setMinFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                        bigCores.get(bigCores.size() - 1), getActivity());
            }
        });
        cpuCard.addItem(mCPUMinBig);

        if (mCPUFreq.hasMaxScreenOffFreq()) {
            mCPUMaxScreenOffBig = new SelectView();
            mCPUMaxScreenOffBig.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mCPUMaxScreenOffBig.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            mCPUMaxScreenOffBig.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            mCPUMaxScreenOffBig.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs().get(position), bigCores.get(0),
                            bigCores.get(bigCores.size() - 1), getActivity());
                }
            });
            cpuCard.addItem(mCPUMaxScreenOffBig);
        }

        if (mCPUFreq.isBigLITTLE()) {
            cpuCard.setTitle(getString(R.string.cluster_big));
        }

        mCPUGovernorBig = new SelectView();
        mCPUGovernorBig.setTitle(getString(R.string.cpu_governor));
        mCPUGovernorBig.setSummary(getString(R.string.cpu_governor_summary));
        mCPUGovernorBig.setItems(mCPUFreq.getGovernors());
        mCPUGovernorBig.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mCPUFreq.setGovernor(item, bigCores.get(0), bigCores.get(bigCores.size() - 1),
                        getActivity());
            }
        });
        cpuCard.addItem(mCPUGovernorBig);

        DescriptionView governorTunablesBig = new DescriptionView();
        governorTunablesBig.setTitle(getString(R.string.cpu_governor_tunables));
        governorTunablesBig.setSummary(getString(R.string.governor_tunables_summary));
        governorTunablesBig.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                showGovernorTunables(bigCores.get(0), bigCores.get(bigCores.size() - 1));
            }
        });
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
            mCPUMaxLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setMaxFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                            LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity());
                }
            });
            cpuLITTLECard.addItem(mCPUMaxLITTLE);

            mCPUMinLITTLE = new SelectView();
            mCPUMinLITTLE.setTitle(getString(R.string.cpu_min_freq));
            mCPUMinLITTLE.setSummary(getString(R.string.cpu_min_freq_summary));
            mCPUMinLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            mCPUMinLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setMinFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                            LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity());
                }
            });
            cpuLITTLECard.addItem(mCPUMinLITTLE);

            if (mCPUFreq.hasMaxScreenOffFreq(mCPUFreq.getLITTLECpu())) {
                mCPUMaxScreenOffLITTLE = new SelectView();
                mCPUMaxScreenOffLITTLE.setTitle(getString(R.string.cpu_max_screen_off_freq));
                mCPUMaxScreenOffLITTLE.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
                mCPUMaxScreenOffLITTLE.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                mCPUMaxScreenOffLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                    @Override
                    public void onItemSelected(SelectView selectView, int position, String item) {
                        mCPUFreq.setMaxScreenOffFreq(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position),
                                LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1), getActivity());
                    }
                });
                cpuLITTLECard.addItem(mCPUMaxScreenOffLITTLE);
            }

            mCPUGovernorLITTLE = new SelectView();
            mCPUGovernorLITTLE.setTitle(getString(R.string.cpu_governor));
            mCPUGovernorLITTLE.setSummary(getString(R.string.cpu_governor_summary));
            mCPUGovernorLITTLE.setItems(mCPUFreq.getGovernors());
            mCPUGovernorLITTLE.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUFreq.setGovernor(item, LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1),
                            getActivity());
                }
            });
            cpuLITTLECard.addItem(mCPUGovernorLITTLE);

            DescriptionView governorTunablesLITTLE = new DescriptionView();
            governorTunablesLITTLE.setTitle(getString(R.string.cpu_governor_tunables));
            governorTunablesLITTLE.setSummary(getString(R.string.governor_tunables_summary));
            governorTunablesLITTLE.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    showGovernorTunables(LITTLECores.get(0), LITTLECores.get(LITTLECores.size() - 1));
                }
            });
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
                    null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mGovernorTunableErrorDialog = null;
                        }
                    }, getActivity());
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
        mcPowerSaving.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Misc.setMcPowerSaving(position, getActivity());
            }
        });

        items.add(mcPowerSaving);
    }

    private void powerSavingWqInit(List<RecyclerViewItem> items) {
        SwitchView powerSavingWq = new SwitchView();
        powerSavingWq.setSummary(getString(R.string.power_saving_wq));
        powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled());
        powerSavingWq.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enablePowerSavingWq(isChecked, getActivity());
		getHandler().postDelayed(() -> {
		powerSavingWq.setChecked(Misc.isPowerSavingWqEnabled());
		},
	    500);
            }
        });

        items.add(powerSavingWq);
    }

    private void cfsSchedulerInit(List<RecyclerViewItem> items) {
        SelectView cfsScheduler = new SelectView();
        cfsScheduler.setTitle(getString(R.string.cfs_scheduler_policy));
        cfsScheduler.setSummary(getString(R.string.cfs_scheduler_policy_summary));
        cfsScheduler.setItems(Misc.getAvailableCFSSchedulers());
        cfsScheduler.setItem(Misc.getCurrentCFSScheduler());
        cfsScheduler.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Misc.setCFSScheduler(item, getActivity());
		getHandler().postDelayed(() -> {
		cfsScheduler.setItem(Misc.getCurrentCFSScheduler());
		},
	    500);
            }
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
            cpuQuietEnable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableCpuQuiet(isChecked, getActivity());
		    getHandler().postDelayed(() -> {
		    cpuQuietEnable.setChecked(Misc.isCpuQuietEnabled());
		    },
	    	500);
                }
            });

            views.add(cpuQuietEnable);
        }

        if (Misc.hasCpuQuietGovernors()) {
            SelectView cpuQuietGovernors = new SelectView();
            cpuQuietGovernors.setSummary(getString(R.string.cpu_quiet_governor));
            cpuQuietGovernors.setItems(Misc.getCpuQuietAvailableGovernors());
            cpuQuietGovernors.setItem(Misc.getCpuQuietCurGovernor());
            cpuQuietGovernors.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    Misc.setCpuQuietGovernor(item, getActivity());
                }
            });

            views.add(cpuQuietGovernors);
        }

        if (Misc.hasCpuQuietNrMaxCPUs()) {
            GenericSelectView NrMaxCPUs = new GenericSelectView();
            NrMaxCPUs.setSummary(getString(R.string.nr_max_cpu));
            NrMaxCPUs.setValue(Misc.getCpuQuietNrMaxCPUs());
            NrMaxCPUs.setInputType(InputType.TYPE_CLASS_NUMBER);
            NrMaxCPUs.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Misc.setCpuQuietNrMaxCPUs(value, getActivity());
                    genericSelectView.setValue(value);
                    getHandler().postDelayed(() -> {
                                NrMaxCPUs.setValue(Misc.getCpuQuietNrMaxCPUs());
                            },
                            500);
                }
            });

            views.add(NrMaxCPUs);
        }

        if (Misc.hasCpuQuietNrMinCPUs()) {
            GenericSelectView NrMinCPUs = new GenericSelectView();
            NrMinCPUs.setSummary(getString(R.string.nr_min_cpu));
            NrMinCPUs.setValue(Misc.getCpuQuietNrMinCPUs());
            NrMinCPUs.setInputType(InputType.TYPE_CLASS_NUMBER);
            NrMinCPUs.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Misc.setCpuQuietNrMinCPUs(value, getActivity());
                    genericSelectView.setValue(value);
                    getHandler().postDelayed(() -> {
                                NrMinCPUs.setValue(Misc.getCpuQuietNrMinCPUs());
                            },
                            500);
                }
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
        cpuBoost.setTitle(getString(R.string.cpu_boost));

        if (mCPUBoost.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.cpu_boost));
            enable.setChecked(mCPUBoost.isEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoost(isChecked, getActivity());
                }
            });

            cpuBoost.addItem(enable);
        }

        if (mCPUBoost.hasCpuBoostDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mCPUBoost.isCpuBoostDebugMaskEnabled());
            debugMask.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoostDebugMask(isChecked, getActivity());
                }
            });

            cpuBoost.addItem(debugMask);
        }

        if (mCPUBoost.hasCpuBoostMs()) {
            SeekBarView ms = new SeekBarView();
            ms.setTitle(getString(R.string.interval));
            ms.setSummary(getString(R.string.interval_summary));
            ms.setUnit(" ms");
            ms.setMax(5000);
            ms.setOffset(10);
            ms.setProgress(mCPUBoost.getCpuBootMs() / 10);
            ms.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostMs(position * 10, getActivity());
		    getHandler().postDelayed(() -> {
		    ms.setProgress(mCPUBoost.getCpuBootMs() / 10);
		    },
	    	500);
                }
            });

            cpuBoost.addItem(ms);
        }

        if (mCPUBoost.hasCpuBoostSyncThreshold() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SelectView syncThreshold = new SelectView();
            syncThreshold.setTitle(getString(R.string.sync_threshold));
            syncThreshold.setSummary(getString(R.string.sync_threshold_summary));
            syncThreshold.setItems(list);
            syncThreshold.setItem(mCPUBoost.getCpuBootSyncThreshold());
            syncThreshold.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mCPUBoost.setCpuBoostSyncThreshold(position == 0 ? 0 : mCPUFreq.getFreqs().get(position - 1),
                            getActivity());
                }
            });

            cpuBoost.addItem(syncThreshold);
        }

        if (mCPUBoost.hasCpuBoostInputMs()) {
            SeekBarView inputMs = new SeekBarView();
            inputMs.setTitle(getString(R.string.input_interval));
            inputMs.setSummary(getString(R.string.input_interval_summary));
            inputMs.setUnit(" ms");
            inputMs.setMax(5000);
            inputMs.setOffset(10);
            inputMs.setProgress(mCPUBoost.getCpuBootInputMs() / 10);
            inputMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostInputMs(position * 10, getActivity());
		    getHandler().postDelayed(() -> {
		    inputMs.setProgress(mCPUBoost.getCpuBootInputMs() / 10);
		    },
	    	500);
                }
            });

            cpuBoost.addItem(inputMs);
        }

        if (Misc.hasCpuTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(Misc.isCpuTouchBoostEnabled());
            touchBoost.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableCpuTouchBoost(isChecked, getActivity());
                }
            });

            cpuBoost.addItem(touchBoost);
        }

        if (mCPUInputBoost.hascpuinputboost()) {
            SwitchView cpuinputboost = new SwitchView();
            cpuinputboost.setTitle(getString(R.string.cpu_input_boost));
            cpuinputboost.setSummary(getString(R.string.cpu_input_boost_summary));
            cpuinputboost.setChecked(mCPUInputBoost.iscpuinputboostEnabled());
            cpuinputboost.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
			mCPUInputBoost.enablecpuinputboost(isChecked, getActivity());
		}
            });
            cpuBoost.addItem(cpuinputboost);
	}

	if (mCPUInputBoost.hascpuiboostduration()) {
            GenericSelectView cpuiboostduration = new GenericSelectView();
            cpuiboostduration.setTitle(getString(R.string.cpuiboost_duration) + (" (ms)"));
            cpuiboostduration.setSummary(getString(R.string.cpuiboost_duration_summary));
            cpuiboostduration.setValue(mCPUInputBoost.getcpuiboostduration());
            cpuiboostduration.setInputType(InputType.TYPE_CLASS_NUMBER);
            cpuiboostduration.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mCPUInputBoost.setcpuiboostduration(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    cpuiboostduration.setValue(mCPUInputBoost.getcpuiboostduration());
		    },
	    	500);
                }
            });

            cpuBoost.addItem(cpuiboostduration);
	}

	if (mCPUInputBoost.haswakeboostduration()) { 
            GenericSelectView wakeBoostMS = new GenericSelectView();
            wakeBoostMS.setTitle(getString(R.string.wake_boost_duration) + (" (ms)"));
            wakeBoostMS.setSummary(("Set ") + getString(R.string.wake_boost_duration));
            wakeBoostMS.setValue(mCPUInputBoost.getwakeboostduration());
            wakeBoostMS.setInputType(InputType.TYPE_CLASS_NUMBER);
            wakeBoostMS.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mCPUInputBoost.setwakeboostduration(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    wakeBoostMS.setValue(mCPUInputBoost.getwakeboostduration());
		    },
	    	500);
                }
            });

            cpuBoost.addItem(wakeBoostMS);
	}

	if (mCPUInputBoost.hascpuiboostfreq()) { 
            GenericSelectView iboostfreq = new GenericSelectView();
            iboostfreq.setTitle(getString(R.string.input_boost_freq) + (" (Hz)"));
            iboostfreq.setSummary(getString(R.string.input_boost_freq_summary));
            iboostfreq.setValue(mCPUInputBoost.getcpuiboostfreq());
            iboostfreq.setInputType(InputType.TYPE_CLASS_NUMBER);
            iboostfreq.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mCPUInputBoost.setcpuiboostfreq(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    iboostfreq.setValue(mCPUInputBoost.getcpuiboostfreq());
		    },
	    	500);
                }
            });

            cpuBoost.addItem(iboostfreq);
	}

	if (mCPUInputBoost.hascpuinputboostlf()) {
            SelectView cpuiboostlf = new SelectView();
            cpuiboostlf.setTitle(getString(R.string.input_boost_freq));
            cpuiboostlf.setSummary("LITTLE");
            cpuiboostlf.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            cpuiboostlf.setItem((mCPUInputBoost.getcpuinputboostlf() / 1000)
		+ getString(R.string.mhz));
            cpuiboostlf.setOnItemSelected((selectView, position, item)
		-> mCPUInputBoost.setcpuinputboostlf(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));
		getHandler().postDelayed(() -> {
		cpuiboostlf.setItem((mCPUInputBoost.getcpuinputboostlf() / 1000)
		+ getString(R.string.mhz));
		},
	    500);

            cpuBoost.addItem(cpuiboostlf);
	}

	if (mCPUInputBoost.hascpuinputboosthf()) {
            SelectView cpuiboosthf = new SelectView();
            cpuiboosthf.setSummary("big");
            cpuiboosthf.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpuiboosthf.setItem((mCPUInputBoost.getcpuinputboosthf() / 1000)
		+ getString(R.string.mhz));
            cpuiboosthf.setOnItemSelected((selectView, position, item)
		-> mCPUInputBoost.setcpuinputboosthf(mCPUFreq.getFreqs().get(position), getActivity()));
		getHandler().postDelayed(() -> {
		cpuiboosthf.setItem((mCPUInputBoost.getcpuinputboosthf() / 1000)
		+ getString(R.string.mhz));
		},
	    500);

            cpuBoost.addItem(cpuiboosthf);
	}

	if (mCPUInputBoost.hasremoveinputboostlf()) {
            SelectView iboostfreq = new SelectView();
            iboostfreq.setTitle(getString(R.string.cluster_return_freq));
            iboostfreq.setSummary("LITTLE");
            iboostfreq.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            iboostfreq.setItem((mCPUInputBoost.getremoveinputboostlf() / 1000)
		+ getString(R.string.mhz));
            iboostfreq.setOnItemSelected((selectView, position, item)
		-> mCPUInputBoost.setremoveinputboostlf(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));
		getHandler().postDelayed(() -> {
		iboostfreq.setItem((mCPUInputBoost.getremoveinputboostlf() / 1000)
		+ getString(R.string.mhz));
		},
	    500);

            cpuBoost.addItem(iboostfreq);
	}

	if (mCPUInputBoost.hasremoveinputboosthf()) {
            SelectView iboostfreq = new SelectView();
            iboostfreq.setSummary("big");
            iboostfreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            iboostfreq.setItem((mCPUInputBoost.getremoveinputboosthf() / 1000)
		+ getString(R.string.mhz));
            iboostfreq.setOnItemSelected((selectView, position, item)
		-> mCPUInputBoost.setremoveinputboosthf(mCPUFreq.getFreqs().get(position), getActivity()));
		getHandler().postDelayed(() -> {
		iboostfreq.setItem((mCPUInputBoost.getremoveinputboosthf() / 1000)
		+ getString(R.string.mhz));
		},
	    500);

            cpuBoost.addItem(iboostfreq);
	}

	if (mCPUInputBoost.hasinputboostFreq()) {
            SelectView cpuiboostfreq = new SelectView();
            cpuiboostfreq.setTitle(getString(R.string.input_boost_freq));
            cpuiboostfreq.setSummary(getString(R.string.input_boost_freq_summary));
            cpuiboostfreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpuiboostfreq.setItem((mCPUInputBoost.getinputboostFreq() / 1000)
		+ getString(R.string.mhz));
            cpuiboostfreq.setOnItemSelected((selectView, position, item)
		-> mCPUInputBoost.setinputboostFreq(mCPUFreq.getFreqs().get(position), getActivity()));
		getHandler().postDelayed(() -> {
		cpuiboostfreq.setItem((mCPUInputBoost.getinputboostFreq() / 1000)
		+ getString(R.string.mhz));
		},
	    500);

            cpuBoost.addItem(cpuiboostfreq);
	}

	if (mCPUBoost.hasCpuBoostInputFreq()) {
            List<Integer> freqs = mCPUBoost.getCpuBootInputFreq();
            for (int i = 0; i < freqs.size(); i++) {
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.disabled));
                list.addAll(mCPUFreq.getAdjustedFreq(i, getActivity()));

                SelectView inputCard = new SelectView();
                if (freqs.size() > 1) {
                    inputCard.setTitle(getString(R.string.input_boost_freq_core, i + 1));
                } else {
                    inputCard.setTitle(getString(R.string.input_boost_freq));
                }
                inputCard.setSummary(getString(R.string.input_boost_freq_summary));
                inputCard.setItems(list);
                inputCard.setItem(freqs.get(i));

                final int core = i;
                inputCard.setOnItemSelected(new SelectView.OnItemSelected() {
                    @Override
                    public void onItemSelected(SelectView selectView, int position, String item) {
                        mCPUBoost.setCpuBoostInputFreq(position == 0 ? 0
                                : mCPUFreq.getFreqs(core).get(position - 1), core, getActivity());
                    }
                });

                cpuBoost.addItem(inputCard);
            }
	}

	if (mCPUBoost.hasCpuBoostWakeup()) {
            SwitchView wakeup = new SwitchView();
            wakeup.setTitle(getString(R.string.wakeup_boost));
            wakeup.setSummary(getString(R.string.wakeup_boost_summary));
            wakeup.setChecked(mCPUBoost.isCpuBoostWakeupEnabled());
            wakeup.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoostWakeup(isChecked, getActivity());
                }
            });

            cpuBoost.addItem(wakeup);
	}

	if (mCPUBoost.hasCpuBoostHotplug()) {
            SwitchView hotplug = new SwitchView();
            hotplug.setTitle(getString(R.string.hotplug_boost));
            hotplug.setSummary(getString(R.string.hotplug_boost_summary));
            hotplug.setChecked(mCPUBoost.isCpuBoostHotplugEnabled());
            hotplug.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCPUBoost.enableCpuBoostHotplug(isChecked, getActivity());
                }
            });

            cpuBoost.addItem(hotplug);
	}

        if (Misc.hasDynStuneBoost() || mCPUInputBoost.hasDynStuneBoost()) {
            SeekBarView dynstuneBoost = new SeekBarView();
            dynstuneBoost.setTitle(getString(R.string.dyn_stune_boost));
            if (Misc.hasDynStuneBoost()) {
		dynstuneBoost.setProgress(Misc.getDynStuneBoost());
            } else if (mCPUInputBoost.hasDynStuneBoost()) {
		dynstuneBoost.setProgress(mCPUInputBoost.getDynStuneBoost());
            }
            dynstuneBoost.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    if (Misc.hasDynStuneBoost()) {
			Misc.setDynStuneBoost(position, getActivity());
                    } else if (mCPUInputBoost.hasDynStuneBoost()) {
			mCPUInputBoost.setDynStuneBoost(position, getActivity());
                    }
		}
            });

            cpuBoost.addItem(dynstuneBoost);

            if (Misc.hasDynStuneBoostDuration()) {
            	GenericSelectView stuneBoostDuration = new GenericSelectView();
            	stuneBoostDuration.setTitle(getString(R.string.stune_boost_ms) + (" (ms)"));
            	stuneBoostDuration.setSummary(("Set ") + getString(R.string.stune_boost_ms));
            	stuneBoostDuration.setValue(Misc.getDynStuneBoostDuration());
            	stuneBoostDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
            	stuneBoostDuration.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                    @Override
                    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    	Misc.setDynStuneBoostDuration(value, getActivity());
                    	genericSelectView.setValue(value);
                    }
            	});

                cpuBoost.addItem(stuneBoostDuration);
	    }
	}

	if (!(Prefs.getBoolean("stune_boost", false, getActivity())))
	    Prefs.saveBoolean("stune_boost", false, getActivity());

	final SwitchView stuneBoost = new SwitchView();
	stuneBoost.setTitle(getString(R.string.stune_boost_sett));
	stuneBoost.setSummary(getString(R.string.stune_boost_sett_summary));
	stuneBoost.setChecked(Prefs.getBoolean("stune_boost", false, getActivity()));

	if (Misc.hasSchedBoostSettings()) {
	    cpuBoost.addItem(stuneBoost);
	}

	for (int i = 0; i < Misc.size(); i++) {
	    if (Misc.exists(i)) {
		GenericSelectView advStune = new GenericSelectView();
		advStune.setSummary(Misc.getName(i));
		advStune.setValue(Misc.getValue(i));
		advStune.setValueRaw(advStune.getValue());
		advStune.setInputType(InputType.TYPE_CLASS_NUMBER);

		final int position = i;
		advStune.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
		    @Override
		    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
			Misc.setValue(value, position, getActivity());
			genericSelectView.setValue(value);
			getHandler().postDelayed(() -> {
			advStune.setValueRaw(advStune.getValue());
			},
		    500);
		    }
		});
		class stuneBoostManager {
		public void showstuneBoost (boolean enable) {
		if (enable == true) {
		    cpuBoost.addItem(advStune);
		} else {
		    cpuBoost.removeItem(advStune);
		}
	    }
	}
	
	final stuneBoostManager manager = new stuneBoostManager();
	    if (Prefs.getBoolean("stune_boost", false, getActivity()) == true) {
		manager.showstuneBoost(true);
	    } else {
		manager.showstuneBoost(false);
	    }
	    stuneBoost.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchview, boolean isChecked) {
		    Prefs.saveBoolean("stune_boost", isChecked, getActivity());
		    manager.showstuneBoost(isChecked);
		}
	    });
	    }
	}

	if (cpuBoost.size() > 0) {
            items.add(cpuBoost);
	}
    }

    private void msmlimiterInit(List<RecyclerViewItem> items) {
        CardView msmLimiter = new CardView(getActivity());
        msmLimiter.setTitle(getString(R.string.msm_limiter));

	if (MSMLimiter.hasenable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.msm_limiter_summary));
            enable.setChecked(MSMLimiter.isEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMLimiter.enable(isChecked, getActivity());
                }
            });

            msmLimiter.addItem(enable);
	}

	if (MSMLimiter.hasFreqControl()) {
            SwitchView freqControl = new SwitchView();
            freqControl.setTitle(getString(R.string.freq_control));
            freqControl.setSummary(getString(R.string.freq_control_summary));
            freqControl.setChecked(MSMLimiter.isFreqControlEnabled());
            freqControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMLimiter.enableFreqControl(isChecked, getActivity());
                }
            });

            msmLimiter.addItem(freqControl);
	}

	if (MSMLimiter.hasDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(MSMLimiter.isDebugMaskEnabled());
            debugMask.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMLimiter.enableDebugMask(isChecked, getActivity());
                }
            });

            msmLimiter.addItem(debugMask);
	}

	if (msmLimiter.size() > 0) {
            items.add(msmLimiter);
	}
    }

    private void powerhalTunabledInit(List<RecyclerViewItem> items) {
        CardView powerhal = new CardView(getActivity());
        powerhal.setTitle(getString(R.string.powerhal_tunables));

        for (int i = 0; i < Misc.VoxpopuliTunablesize(); i++) {
            if (Misc.VoxpopuliTunableexists(i)) {
                GenericSelectView tunables = new GenericSelectView();
                tunables.setSummary(Misc.getVoxpopuliTunableName(i));
                tunables.setValue(Misc.getVoxpopuliTunableValue(i));
                tunables.setValueRaw(tunables.getValue());
                tunables.setInputType(InputType.TYPE_CLASS_NUMBER);

                final int position = i;
                tunables.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                    @Override
                    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                        Misc.setVoxpopuliTunableValue(value, position, getActivity());
                        genericSelectView.setValue(value);
                        getHandler().postDelayed(() -> {
                                    for (int i = 0; i < Misc.VoxpopuliTunablesize(); i++) {
                                        tunables.setValue(Misc.getVoxpopuliTunableValue(i));
                                    }
                                },
                                500);
                    }
                });

                powerhal.addItem(tunables);
            }
        }

        if (powerhal.size() > 0) {
            items.add(powerhal);
        }
    }

    private float[] mCPUUsages;
    private boolean[] mCPUStates;
    private int[] mCPUFreqs;
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
                    switchView.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                        @Override
                        public void onChanged(SwitchView switchView, boolean isChecked) {
                            if (core == 0) {
                                Utils.toast(R.string.no_offline_core, getActivity());
                            } else {
                                mCPUFreq.onlineCpu(core, isChecked, true, getActivity());
                            }
                        }
                    });
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }
}
