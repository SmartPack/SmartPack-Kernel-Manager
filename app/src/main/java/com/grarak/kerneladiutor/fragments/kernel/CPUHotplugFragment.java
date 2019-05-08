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

import android.text.InputType;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.AlucardHotplug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.BluPlug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.CoreCtl;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.IntelliPlug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.LazyPlug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MPDecision;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MakoHotplug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.MSMHotplug;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import com.smartpack.kernelmanager.utils.MSMSleeper;
import com.smartpack.kernelmanager.utils.MBHotplug;
import com.smartpack.kernelmanager.utils.AutoSMP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUHotplugFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;
    private IntelliPlug mIntelliPlug;
    private MSMHotplug mMSMHotplug;
    private MBHotplug mMBHotplug;
    private CoreCtl mCoreCtl;

    private List<SwitchView> mEnableViews = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mIntelliPlug = IntelliPlug.getInstance();
        mMSMHotplug = MSMHotplug.getInstance();
        mMBHotplug = MBHotplug.getInstance();
        mCoreCtl = CoreCtl.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mEnableViews.clear();

        if (MPDecision.supported()) {
            mpdecisionInit(items);
        }
        if (mIntelliPlug.supported()) {
            intelliPlugInit(items);
        }
        if (LazyPlug.supported()) {
            lazyPlugInit(items);
        }
        if (MSMSleeper.supported()) {
            MSMSleeperInit(items);
        }
        if (BluPlug.supported()) {
            bluPlugInit(items);
        }
        if (MakoHotplug.supported()) {
            makoHotplugInit(items);
        }
        if (mMSMHotplug.supported()) {
            msmHotplugInit(items);
        }
        if (AlucardHotplug.supported()) {
            alucardHotplugInit(items);
        }
        if (mMBHotplug.supported()) {
            mbHotplugInit(items);
        }
        if (AutoSMP.supported()) {
            autoSMPInit(items);
        }
        if (mCoreCtl.supported()) {
            coreCtlInit(items);
        }

        for (SwitchView view : mEnableViews) {
            view.addOnSwitchListener((switchView, isChecked) -> {
                boolean enabled = false;
                for (SwitchView view1 : mEnableViews) {
                    if (!enabled && view1.isChecked()) {
                        enabled = true;
                        continue;
                    }
                    if (enabled && view1.isChecked()) {
                        Utils.toast(R.string.hotplug_warning, getActivity());
                        break;
                    }
                }
            });
        }
    }

    private void mpdecisionInit(List<RecyclerViewItem> items) {
        CardView MPD = new CardView(getActivity());
        MPD.setTitle(getString(R.string.mpdecision));

        SwitchView mpdecision = new SwitchView();
        mpdecision.setSummary(getString(R.string.mpdecision_summary));
        mpdecision.setChecked(MPDecision.isMpdecisionEnabled());
        mpdecision.addOnSwitchListener((switchView, isChecked)
                -> MPDecision.enableMpdecision(isChecked, getActivity()));

        MPD.addItem(mpdecision);
        mEnableViews.add(mpdecision);

	if (MPD.size() > 0) {
            items.add(MPD);
	}
    }

    private void intelliPlugInit(List<RecyclerViewItem> items) {
	CardView intelliplug = new CardView(getActivity());
	intelliplug.setTitle(getString(R.string.intelliplug));

	SwitchView enable = new SwitchView();
	SelectView profile = new SelectView();
	SwitchView eco = new SwitchView();
	SwitchView touchBoost = new SwitchView();
	SeekBarView hysteresis = new SeekBarView();
	SeekBarView threshold = new SeekBarView();
	SelectView maxScreenOffFreq = new SelectView();
	SwitchView debug = new SwitchView();
	SwitchView suspend = new SwitchView();
	SeekBarView cpusBoosted = new SeekBarView();
	SeekBarView minCpusOnline = new SeekBarView();
	SeekBarView maxCpusOnline = new SeekBarView();
	SeekBarView maxCpusOnlineSusp = new SeekBarView();
	SeekBarView suspendDeferTime = new SeekBarView();
	SeekBarView deferSampling = new SeekBarView();
	SeekBarView boostLockDuration = new SeekBarView();
	SeekBarView downLockDuration = new SeekBarView();
	SeekBarView fShift = new SeekBarView();

	enable.setSummary(getString(R.string.intelliplug_summary));
	enable.setChecked(mIntelliPlug.isIntelliPlugEnabled());
	enable.addOnSwitchListener((switchView, isChecked) -> {
	    mIntelliPlug.enableIntelliPlug(isChecked, getActivity());
	    getHandler().postDelayed(() -> {
	    // Show or hide other options on the basis of the main driver status
	    if (mIntelliPlug.isIntelliPlugEnabled()) {
		if (mIntelliPlug.hasIntelliPlugProfile()) {
		    profile.setItem(mIntelliPlug.getIntelliPlugProfile());
		    intelliplug.addItem(profile);
		}
		if (mIntelliPlug.hasIntelliPlugEco()) {
		    profile.setItem(mIntelliPlug.getIntelliPlugProfile());
		    intelliplug.addItem(eco);
		}
		if (mIntelliPlug.hasIntelliPlugTouchBoost()) {
		    touchBoost.setChecked(mIntelliPlug.isIntelliPlugTouchBoostEnabled());
		    intelliplug.addItem(touchBoost);
		}
		if (mIntelliPlug.hasIntelliPlugHysteresis()) {
		    hysteresis.setProgress(mIntelliPlug.getIntelliPlugHysteresis());
		    intelliplug.addItem(hysteresis);
		}
		if (mIntelliPlug.hasIntelliPlugThresold()) {
		    threshold.setProgress(mIntelliPlug.getIntelliPlugThresold());
		    intelliplug.addItem(threshold);
		}
		if (mIntelliPlug.hasIntelliPlugScreenOffMax() && mCPUFreq.getFreqs() != null) {
		    maxScreenOffFreq.setItem(mIntelliPlug.getIntelliPlugScreenOffMax());
		    intelliplug.addItem(maxScreenOffFreq);
		}
		if (mIntelliPlug.hasIntelliPlugDebug()) {
		    debug.setChecked(mIntelliPlug.isIntelliPlugDebugEnabled());
		    intelliplug.addItem(debug);
		}
		if (mIntelliPlug.hasIntelliPlugCpusBoosted()) {
		    cpusBoosted.setProgress(mIntelliPlug.getIntelliPlugCpusBoosted() - 1);
		    intelliplug.addItem(cpusBoosted);
		}
		if (mIntelliPlug.hasIntelliPlugMinCpusOnline()) {
		    minCpusOnline.setProgress(mIntelliPlug.getIntelliPlugMinCpusOnline() - 1);
		    intelliplug.addItem(minCpusOnline);
		}
		if (mIntelliPlug.hasIntelliPlugMaxCpusOnline()) {
		    maxCpusOnline.setProgress(mIntelliPlug.getIntelliPlugMaxCpusOnline() - 1);
		    intelliplug.addItem(maxCpusOnline);
		}
		if (mIntelliPlug.hasIntelliPlugMaxCpusOnlineSusp()) {
		    maxCpusOnlineSusp.setProgress(mIntelliPlug.getIntelliPlugMaxCpusOnlineSusp() - 1);
		    intelliplug.addItem(maxCpusOnlineSusp);
		}
		if (mIntelliPlug.hasIntelliPlugSuspendDeferTime()) {
		    suspendDeferTime.setProgress(mIntelliPlug.getIntelliPlugSuspendDeferTime() / 10);
		    intelliplug.addItem(suspendDeferTime);
		}
		if (mIntelliPlug.hasIntelliPlugDeferSampling()) {
		    deferSampling.setProgress(mIntelliPlug.getIntelliPlugDeferSampling());
		    intelliplug.addItem(deferSampling);
		}
		if (mIntelliPlug.hasIntelliPlugBoostLockDuration()) {
		    boostLockDuration.setProgress(mIntelliPlug.getIntelliPlugBoostLockDuration() - 1);
		    intelliplug.addItem(boostLockDuration);
		}
		if (mIntelliPlug.hasIntelliPlugDownLockDuration()) {
		    downLockDuration.setProgress(mIntelliPlug.getIntelliPlugDownLockDuration() - 1);
		    intelliplug.addItem(downLockDuration);
		}
		if (mIntelliPlug.hasIntelliPlugFShift()) {
		    fShift.setProgress(mIntelliPlug.getIntelliPlugFShift());
		    intelliplug.addItem(fShift);
		}
	    } else {
		intelliplug.removeItem(profile);
		intelliplug.removeItem(eco);
		intelliplug.removeItem(touchBoost);
		intelliplug.removeItem(hysteresis);
		intelliplug.removeItem(threshold);
		intelliplug.removeItem(maxScreenOffFreq);
		intelliplug.removeItem(debug);
		intelliplug.removeItem(cpusBoosted);
		intelliplug.removeItem(minCpusOnline);
		intelliplug.removeItem(maxCpusOnline);
		intelliplug.removeItem(maxCpusOnlineSusp);
		intelliplug.removeItem(suspendDeferTime);
		intelliplug.removeItem(deferSampling);
		intelliplug.removeItem(boostLockDuration);
		intelliplug.removeItem(downLockDuration);
		intelliplug.removeItem(fShift);
	    }
	}, 100);
	});

	intelliplug.addItem(enable);
	mEnableViews.add(enable);

	if (mIntelliPlug.hasIntelliPlugProfile()) {
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.cpu_hotplug_profile_summary));
            profile.setItems(mIntelliPlug.getIntelliPlugProfileMenu(getActivity()));
            profile.setItem(mIntelliPlug.getIntelliPlugProfile());
            profile.setOnItemSelected((selectView, position, item)
		-> mIntelliPlug.setIntelliPlugProfile(position, getActivity()));

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(profile);
	    } else {
            	intelliplug.removeItem(profile);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugEco()) {
            eco.setTitle(getString(R.string.eco_mode));
            eco.setSummary(getString(R.string.eco_mode_summary));
            eco.setChecked(mIntelliPlug.isIntelliPlugEcoEnabled());
            eco.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugEco(isChecked, getActivity()));

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(eco);
	    } else {
            	intelliplug.removeItem(eco);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugTouchBoost()) {
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(mIntelliPlug.isIntelliPlugTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugTouchBoost(isChecked, getActivity()));

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(touchBoost);
	    } else {
            	intelliplug.removeItem(touchBoost);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugHysteresis()) {
            hysteresis.setTitle(getString(R.string.hysteresis));
            hysteresis.setSummary(getString(R.string.hysteresis_summary));
            hysteresis.setMax(17);
            hysteresis.setProgress(mIntelliPlug.getIntelliPlugHysteresis());
            hysteresis.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugHysteresis(position, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(hysteresis);
	    } else {
            	intelliplug.removeItem(hysteresis);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugThresold()) {
            threshold.setTitle(getString(R.string.cpu_threshold));
            threshold.setSummary(getString(R.string.cpu_threshold_summary));
            threshold.setMax(1000);
            threshold.setProgress(mIntelliPlug.getIntelliPlugThresold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugThresold(position, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(threshold);
	    } else {
            	intelliplug.removeItem(threshold);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugScreenOffMax() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            maxScreenOffFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxScreenOffFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxScreenOffFreq.setItems(list);
            maxScreenOffFreq.setItem(mIntelliPlug.getIntelliPlugScreenOffMax());
            maxScreenOffFreq.setOnItemSelected((selectView, position, item)
		-> mIntelliPlug.setIntelliPlugScreenOffMax(position, getActivity()));

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(maxScreenOffFreq);
	    } else {
            	intelliplug.removeItem(maxScreenOffFreq);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugDebug()) {
            debug.setTitle(getString(R.string.debug_mask));
            debug.setSummary(getString(R.string.debug_mask_summary));
            debug.setChecked(mIntelliPlug.isIntelliPlugDebugEnabled());
            debug.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugDebug(isChecked, getActivity()));

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(debug);
	    } else {
            	intelliplug.removeItem(debug);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugSuspend()) {
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(mIntelliPlug.isIntelliPlugSuspendEnabled());
            suspend.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugSuspend(isChecked, getActivity()));

            intelliplug.addItem(suspend);
	}

	if (mIntelliPlug.hasIntelliPlugCpusBoosted()) {
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(mCPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(mIntelliPlug.getIntelliPlugCpusBoosted() - 1);
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugCpusBoosted(position + 1, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(cpusBoosted);
	    } else {
            	intelliplug.removeItem(cpusBoosted);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugMinCpusOnline()) {
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(mCPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(mIntelliPlug.getIntelliPlugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugMinCpusOnline(position + 1, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(minCpusOnline);
	    } else {
            	intelliplug.removeItem(minCpusOnline);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugMaxCpusOnline()) {
            maxCpusOnline.setTitle(getString(R.string.max_cpu_online));
            maxCpusOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpusOnline.setMax(mCPUFreq.getCpuCount());
            maxCpusOnline.setMin(1);
            maxCpusOnline.setProgress(mIntelliPlug.getIntelliPlugMaxCpusOnline() - 1);
            maxCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugMaxCpusOnline(position + 1, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(maxCpusOnline);
	    } else {
            	intelliplug.removeItem(maxCpusOnline);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugMaxCpusOnlineSusp()) {
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(mCPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(mIntelliPlug.getIntelliPlugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugMaxCpusOnlineSusp(position + 1, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(maxCpusOnlineSusp);
	    } else {
            	intelliplug.removeItem(maxCpusOnlineSusp);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugSuspendDeferTime()) {
            suspendDeferTime.setTitle(getString(R.string.suspend_defer_time));
            suspendDeferTime.setUnit(getString(R.string.ms));
            suspendDeferTime.setMax(5000);
            suspendDeferTime.setOffset(10);
            suspendDeferTime.setProgress(mIntelliPlug.getIntelliPlugSuspendDeferTime() / 10);
            suspendDeferTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugSuspendDeferTime(position * 10, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(suspendDeferTime);
	    } else {
            	intelliplug.removeItem(suspendDeferTime);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugDeferSampling()) {
            deferSampling.setTitle(getString(R.string.defer_sampling));
            deferSampling.setUnit(getString(R.string.ms));
            deferSampling.setMax(1000);
            deferSampling.setProgress(mIntelliPlug.getIntelliPlugDeferSampling());
            deferSampling.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugDeferSampling(position, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(deferSampling);
	    } else {
            	intelliplug.removeItem(deferSampling);
	    }    
	}

	if (mIntelliPlug.hasIntelliPlugBoostLockDuration()) {
            boostLockDuration.setTitle(getString(R.string.boost_lock_duration));
            boostLockDuration.setSummary(getString(R.string.boost_lock_duration_summary));
            boostLockDuration.setUnit(getString(R.string.ms));
            boostLockDuration.setMax(5000);
            boostLockDuration.setMin(1);
            boostLockDuration.setProgress(mIntelliPlug.getIntelliPlugBoostLockDuration() - 1);
            boostLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugBoostLockDuration(position + 1, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(boostLockDuration);
	    } else {
            	intelliplug.removeItem(boostLockDuration);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugDownLockDuration()) {
            downLockDuration.setTitle(getString(R.string.down_lock_duration));
            downLockDuration.setSummary(getString(R.string.down_lock_duration_summary));
            downLockDuration.setUnit(getString(R.string.ms));
            downLockDuration.setMax(5000);
            downLockDuration.setMin(1);
            downLockDuration.setProgress(mIntelliPlug.getIntelliPlugDownLockDuration() - 1);
            downLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugDownLockDuration(position + 1, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(downLockDuration);
	    } else {
            	intelliplug.removeItem(downLockDuration);
	    }
	}

	if (mIntelliPlug.hasIntelliPlugFShift()) {
            fShift.setTitle(getString(R.string.fshift));
            fShift.setMax(4);
            fShift.setProgress(mIntelliPlug.getIntelliPlugFShift());
            fShift.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mIntelliPlug.setIntelliPlugFShift(position, getActivity());
		}
            });

	    if (mIntelliPlug.isIntelliPlugEnabled()) {
            	intelliplug.addItem(fShift);
	    } else {
            	intelliplug.removeItem(fShift);
	    }
        }
	if (intelliplug.size() > 0) {
            items.add(intelliplug);
	}
    }

    private void lazyPlugInit(List<RecyclerViewItem> items) {
	CardView lazyplug = new CardView(getActivity());
	lazyplug.setTitle(getString(R.string.lazyplug));

	SwitchView enable = new SwitchView();
	SelectView profile = new SelectView();
	SwitchView touchBoost = new SwitchView();
	SeekBarView hysteresis = new SeekBarView();
	SeekBarView threshold = new SeekBarView();
	SeekBarView possibleCores = new SeekBarView();

	enable.setSummary(getString(R.string.lazyplug_summary));
	enable.setChecked(LazyPlug.isEnabled());
	enable.addOnSwitchListener((switchView, isChecked) -> {
	    LazyPlug.enable(isChecked, getActivity());
	    getHandler().postDelayed(() -> {
	    // Show or hide other options on the basis of the main driver status
	    if (LazyPlug.isEnabled()) {
		if (LazyPlug.hasProfile()) {
		    profile.setItem(LazyPlug.getProfile());
		    lazyplug.addItem(profile);
		}
		if (LazyPlug.hasTouchBoost()) {
		    touchBoost.setChecked(LazyPlug.isTouchBoostEnabled());
		    lazyplug.addItem(touchBoost);
		}
		if (LazyPlug.hasHysteresis()) {
		    hysteresis.setProgress(LazyPlug.getHysteresis());
		    lazyplug.addItem(hysteresis);
		}
		if (LazyPlug.hasThreshold()) {
		    threshold.setProgress(LazyPlug.getThreshold());
		    lazyplug.addItem(threshold);
		}
		if (LazyPlug.hasPossibleCores()) {
		    possibleCores.setProgress(LazyPlug.getPossibleCores() - 1);
		    lazyplug.addItem(possibleCores);
		}
	    } else {
		lazyplug.removeItem(profile);
		lazyplug.removeItem(touchBoost);
		lazyplug.removeItem(hysteresis);
		lazyplug.removeItem(threshold);
		lazyplug.removeItem(possibleCores);

	    }
	}, 100);
	});

	lazyplug.addItem(enable);
	mEnableViews.add(enable);

	if (LazyPlug.hasProfile()) {
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.cpu_hotplug_profile_summary));
            profile.setItems(LazyPlug.getProfileMenu(getActivity()));
            profile.setItem(LazyPlug.getProfile());
            profile.setOnItemSelected((selectView, position, item)
		-> LazyPlug.setProfile(position, getActivity()));

	    if (LazyPlug.isEnabled()) {
		lazyplug.addItem(profile);
	    } else {
		lazyplug.removeItem(profile);
	    }
	}

	if (LazyPlug.hasTouchBoost()) {
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(LazyPlug.isTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
		-> LazyPlug.enableTouchBoost(isChecked, getActivity()));

	    if (LazyPlug.isEnabled()) {
		lazyplug.addItem(touchBoost);
	    } else {
		lazyplug.removeItem(touchBoost);
	    }
	}

	if (LazyPlug.hasHysteresis()) {
            hysteresis.setTitle(getString(R.string.hysteresis));
            hysteresis.setSummary(getString(R.string.hysteresis_summary));
            hysteresis.setMax(17);
            hysteresis.setProgress(LazyPlug.getHysteresis());
            hysteresis.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			LazyPlug.setHysteresis(position, getActivity());
		}
            });

	    if (LazyPlug.isEnabled()) {
		lazyplug.addItem(hysteresis);
	    } else {
		lazyplug.removeItem(hysteresis);
	    }
	}

	if (LazyPlug.hasThreshold()) {
            threshold.setTitle(getString(R.string.cpu_threshold));
            threshold.setSummary(getString(R.string.cpu_threshold_summary));
            threshold.setMax(1250);
            threshold.setProgress(LazyPlug.getThreshold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			LazyPlug.setThreshold(position, getActivity());
		}
            });

	    if (LazyPlug.isEnabled()) {
		lazyplug.addItem(threshold);
	    } else {
		lazyplug.removeItem(threshold);
	    }
	}

	if (LazyPlug.hasPossibleCores()) {
            possibleCores.setTitle(getString(R.string.max_cpu_online));
            possibleCores.setSummary(getString(R.string.possible_cpu_cores_summary));
            possibleCores.setMax(mCPUFreq.getCpuCount());
            possibleCores.setMin(1);
            possibleCores.setProgress(LazyPlug.getPossibleCores() - 1);
            possibleCores.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			LazyPlug.setPossibleCores(position + 1, getActivity());
		}
            });

	    if (LazyPlug.isEnabled()) {
		lazyplug.addItem(possibleCores);
	    } else {
		lazyplug.removeItem(possibleCores);
	    }
        }

        if (lazyplug.size() > 0) {
            items.add(lazyplug);
        }
    }

    private void MSMSleeperInit(List<RecyclerViewItem> items) {
	CardView msmsleeper = new CardView(getActivity());
	msmsleeper.setTitle(getString(R.string.msm_sleeper));

	SwitchView enable = new SwitchView();
	SeekBarView MaxOnline = new SeekBarView();
	SeekBarView UpCountMax = new SeekBarView();
	SeekBarView DownCountMax = new SeekBarView();
	SeekBarView SusMaxOnline = new SeekBarView();
	SeekBarView UpThreshold = new SeekBarView();

	enable.setSummary(getString(R.string.msm_sleeper_summary));
	enable.setChecked(MSMSleeper.isEnabled());
	enable.addOnSwitchListener((switchView, isChecked) -> {
	    MSMSleeper.enable(isChecked, getActivity());
	    getHandler().postDelayed(() -> {
	    // Show or hide other options on the basis of the main driver status
	    if (MSMSleeper.isEnabled()) {
		if (MSMSleeper.hasMaxOnline()) {
		    MaxOnline.setProgress(MSMSleeper.getMaxOnline() - 2);
		    msmsleeper.addItem(MaxOnline);
		}
		if (MSMSleeper.hasUpCountMax()) {
		    UpCountMax.setProgress(MSMSleeper.getUpCountMax());
		    msmsleeper.addItem(UpCountMax);
		}
		if (MSMSleeper.hasDownCountMax()) {
		    DownCountMax.setProgress(MSMSleeper.getDownCountMax());
		    msmsleeper.addItem(DownCountMax);
		}
		if (MSMSleeper.hasSusMaxOnline()) {
		    SusMaxOnline.setProgress(MSMSleeper.getSusMaxOnline() - 1);
		    msmsleeper.addItem(SusMaxOnline);
		}
		if (MSMSleeper.hasUpThreshold()) {
		    UpThreshold.setProgress(MSMSleeper.getUpThreshold());
		    msmsleeper.addItem(UpThreshold);
		}
	    } else {
		msmsleeper.removeItem(MaxOnline);
		msmsleeper.removeItem(UpCountMax);
		msmsleeper.removeItem(DownCountMax);
		msmsleeper.removeItem(SusMaxOnline);
		msmsleeper.removeItem(UpThreshold);
	    }
	}, 100);
	});

	msmsleeper.addItem(enable);
	mEnableViews.add(enable);

	if (MSMSleeper.hasMaxOnline()) {
            MaxOnline.setTitle(getString(R.string.max_cpu_online));
            MaxOnline.setMax(mCPUFreq.getCpuCount());
            MaxOnline.setMin(2);
            MaxOnline.setProgress(MSMSleeper.getMaxOnline() - 2);
            MaxOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MSMSleeper.setMaxOnline(position + 2, getActivity());
		}
            });

	    if (MSMSleeper.isEnabled()) {
		msmsleeper.addItem(MaxOnline);
	    } else {
		msmsleeper.removeItem(MaxOnline);
	    }
	}
	if (MSMSleeper.hasUpCountMax()) {
            UpCountMax.setTitle(getString(R.string.max_up_count));
            UpCountMax.setMax(40);
            UpCountMax.setProgress(MSMSleeper.getUpCountMax());
            UpCountMax.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MSMSleeper.setUpCountMax(position, getActivity());
		}
            });

	    if (MSMSleeper.isEnabled()) {
		msmsleeper.addItem(UpCountMax);
	    } else {
		msmsleeper.removeItem(UpCountMax);
	    }
	}

	if (MSMSleeper.hasDownCountMax()) {
            DownCountMax.setTitle(getString(R.string.max_down_count));
            DownCountMax.setMax(40);
            DownCountMax.setProgress(MSMSleeper.getDownCountMax());
            DownCountMax.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MSMSleeper.setDownCountMax(position, getActivity());
		}
            });

	    if (MSMSleeper.isEnabled()) {
		msmsleeper.addItem(DownCountMax);
	    } else {
		msmsleeper.removeItem(DownCountMax);
	    }
	}

	if (MSMSleeper.hasSusMaxOnline()) {
            SusMaxOnline.setTitle(getString(R.string.suspend_max_online));
            SusMaxOnline.setMax(mCPUFreq.getCpuCount());
            SusMaxOnline.setMin(1);
            SusMaxOnline.setProgress(MSMSleeper.getSusMaxOnline() - 1);
            SusMaxOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MSMSleeper.setSusMaxOnline(position + 1, getActivity());
		}
            });

	    if (MSMSleeper.isEnabled()) {
		msmsleeper.addItem(SusMaxOnline);
	    } else {
		msmsleeper.removeItem(SusMaxOnline);
	    }
	}

	if (MSMSleeper.hasUpThreshold()) {
            UpThreshold.setTitle(getString(R.string.up_threshold));
            UpThreshold.setMax(100);
            UpThreshold.setProgress(MSMSleeper.getUpThreshold());
            UpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MSMSleeper.setUpThreshold(position, getActivity());
		}
            });

	    if (MSMSleeper.isEnabled()) {
		msmsleeper.addItem(UpThreshold);
	    } else {
		msmsleeper.removeItem(UpThreshold);
	    }
	}

        if (msmsleeper.size() > 0) {
            items.add(msmsleeper);
        }
    }

    private void bluPlugInit(List<RecyclerViewItem> items) {
	CardView bluplug = new CardView(getActivity());
	bluplug.setTitle(getString(R.string.blu_plug));

	SwitchView enable = new SwitchView();
	SwitchView powersaverMode = new SwitchView();
	SeekBarView minOnline = new SeekBarView();
	SeekBarView maxOnline = new SeekBarView();
	SeekBarView maxCoresScreenOff = new SeekBarView();
	SeekBarView maxFreqScreenOff = new SeekBarView();
	SeekBarView upThreshold = new SeekBarView();
	SeekBarView upTimerCnt = new SeekBarView();
	SeekBarView downTimerCnt = new SeekBarView();

	enable.setSummary(getString(R.string.blu_plug_summary));
	enable.setChecked(BluPlug.isBluPlugEnabled());
	enable.addOnSwitchListener((switchView, isChecked) -> {
	    BluPlug.enableBluPlug(isChecked, getActivity());
	    getHandler().postDelayed(() -> {
	    // Show or hide other options on the basis of the main driver status
	    if (BluPlug.isBluPlugEnabled()) {
		if (BluPlug.hasBluPlugPowersaverMode()) {
		    powersaverMode.setChecked(BluPlug.isBluPlugPowersaverModeEnabled());
		    bluplug.addItem(powersaverMode);
		}
		if (BluPlug.hasBluPlugMinOnline()) {
		    minOnline.setProgress(BluPlug.getBluPlugMinOnline() - 1);
		    bluplug.addItem(minOnline);
		}
		if (BluPlug.hasBluPlugMaxOnline()) {
		    maxOnline.setProgress(BluPlug.getBluPlugMaxOnline() - 1);
		    bluplug.addItem(maxOnline);
		}
		if (BluPlug.hasBluPlugMaxCoresScreenOff()) {
		    maxCoresScreenOff.setProgress(BluPlug.getBluPlugMaxCoresScreenOff() - 1);
		    bluplug.addItem(maxCoresScreenOff);
		}
		if (BluPlug.hasBluPlugMaxFreqScreenOff() && mCPUFreq.getFreqs() != null) {
		    maxFreqScreenOff.setProgress(BluPlug.getBluPlugMaxFreqScreenOff());
		    bluplug.addItem(maxFreqScreenOff);
		}
		if (BluPlug.hasBluPlugUpThreshold()) {
		    upThreshold.setProgress(BluPlug.getBluPlugUpThreshold());
		    bluplug.addItem(upThreshold);
		}
		if (BluPlug.hasBluPlugUpTimerCnt()) {
		    upTimerCnt.setProgress(BluPlug.getBluPlugUpTimerCnt());
		    bluplug.addItem(upTimerCnt);
		}
		if (BluPlug.hasBluPlugDownTimerCnt()) {
		    downTimerCnt.setProgress(BluPlug.getBluPlugDownTimerCnt());
		    bluplug.addItem(downTimerCnt);
		}
	    } else {
		bluplug.removeItem(powersaverMode);
		bluplug.removeItem(minOnline);
		bluplug.removeItem(maxOnline);
		bluplug.removeItem(maxCoresScreenOff);
		bluplug.removeItem(maxFreqScreenOff);
		bluplug.removeItem(upThreshold);
		bluplug.removeItem(upTimerCnt);
		bluplug.removeItem(downTimerCnt);
	    }
	}, 100);
	});

	bluplug.addItem(enable);
	mEnableViews.add(enable);

	if (BluPlug.hasBluPlugPowersaverMode()) {
            powersaverMode.setTitle(getString(R.string.powersaver_mode));
            powersaverMode.setSummary(getString(R.string.powersaver_mode_summary));
            powersaverMode.setChecked(BluPlug.isBluPlugPowersaverModeEnabled());
            powersaverMode.addOnSwitchListener((switchView, isChecked)
		-> BluPlug.enableBluPlugPowersaverMode(isChecked, getActivity()));

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(powersaverMode);
	    } else {
		bluplug.removeItem(powersaverMode);
	    }
	}

	if (BluPlug.hasBluPlugMinOnline()) {
            minOnline.setTitle(getString(R.string.min_cpu_online));
            minOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minOnline.setMax(mCPUFreq.getCpuCount());
            minOnline.setMin(1);
            minOnline.setProgress(BluPlug.getBluPlugMinOnline() - 1);
            minOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugMinOnline(position + 1, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(minOnline);
	    } else {
		bluplug.removeItem(minOnline);
	    }
	}

	if (BluPlug.hasBluPlugMaxOnline()) {
            maxOnline.setTitle(getString(R.string.max_cpu_online));
            maxOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxOnline.setMax(mCPUFreq.getCpuCount());
            maxOnline.setMin(1);
            maxOnline.setProgress(BluPlug.getBluPlugMaxOnline() - 1);
            maxOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugMaxOnline(position + 1, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(maxOnline);
	    } else {
		bluplug.removeItem(maxOnline);
	    }
	}

	if (BluPlug.hasBluPlugMaxCoresScreenOff()) {
            maxCoresScreenOff.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCoresScreenOff.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCoresScreenOff.setMax(mCPUFreq.getCpuCount());
            maxCoresScreenOff.setMin(1);
            maxCoresScreenOff.setProgress(BluPlug.getBluPlugMaxCoresScreenOff() - 1);
            maxCoresScreenOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugMaxCoresScreenOff(position + 1, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(maxCoresScreenOff);
	    } else {
		bluplug.removeItem(maxCoresScreenOff);
	    }
	}

	if (BluPlug.hasBluPlugMaxFreqScreenOff() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            maxFreqScreenOff.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxFreqScreenOff.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxFreqScreenOff.setItems(list);
            maxFreqScreenOff.setProgress(BluPlug.getBluPlugMaxFreqScreenOff());
            maxFreqScreenOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugMaxFreqScreenOff(position, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(maxFreqScreenOff);
	    } else {
		bluplug.removeItem(maxFreqScreenOff);
	    }
	}

	if (BluPlug.hasBluPlugUpThreshold()) {
            upThreshold.setTitle(getString(R.string.up_threshold));
            upThreshold.setSummary(getString(R.string.up_threshold_summary));
            upThreshold.setUnit("%");
            upThreshold.setMax(100);
            upThreshold.setProgress(BluPlug.getBluPlugUpThreshold());
            upThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugUpThreshold(position, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(upThreshold);
	    } else {
		bluplug.removeItem(upThreshold);
	    }
	}

	if (BluPlug.hasBluPlugUpTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
		list.add(String.valueOf(i * 0.5f));
            }

            upTimerCnt.setTitle(getString(R.string.up_timer_cnt));
            upTimerCnt.setSummary(getString(R.string.up_timer_cnt_summary));
            upTimerCnt.setItems(list);
            upTimerCnt.setProgress(BluPlug.getBluPlugUpTimerCnt());
            upTimerCnt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugUpTimerCnt(position, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(upTimerCnt);
	    } else {
		bluplug.removeItem(upTimerCnt);
	    }
	}

	if (BluPlug.hasBluPlugDownTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
		list.add(String.valueOf(i * 0.5f));
            }

            downTimerCnt.setTitle(getString(R.string.down_timer_cnt));
            downTimerCnt.setSummary(getString(R.string.down_timer_cnt_summary));
            downTimerCnt.setItems(list);
            downTimerCnt.setProgress(BluPlug.getBluPlugDownTimerCnt());
            downTimerCnt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			BluPlug.setBluPlugDownTimerCnt(position, getActivity());
		}
            });

	    if (BluPlug.isBluPlugEnabled()) {
		bluplug.addItem(downTimerCnt);
	    } else {
		bluplug.removeItem(downTimerCnt);
	    }
	}

        if (bluplug.size() > 0) {
            items.add(bluplug);
        }
    }

    private void makoHotplugInit(List<RecyclerViewItem> items) {
	CardView makoHotplug = new CardView(getActivity());
	makoHotplug.setTitle(getString(R.string.mako_hotplug));

	SwitchView enable = new SwitchView();
	SeekBarView coresOnTouch = new SeekBarView();
	SelectView cpufreqUnplugLimit = new SelectView();
	SeekBarView firstLevel = new SeekBarView();
	SeekBarView highLoadCounter = new SeekBarView();
	SeekBarView loadThreshold = new SeekBarView();
	SeekBarView maxLoadCounter = new SeekBarView();
	SeekBarView minTimeCpuOnline = new SeekBarView();
	SeekBarView minCoresOnline = new SeekBarView();
	SeekBarView timer = new SeekBarView();
	SelectView suspendFreq = new SelectView();

	enable.setSummary(getString(R.string.mako_hotplug_summary));
	enable.setChecked(MakoHotplug.isMakoHotplugEnabled());
	enable.addOnSwitchListener((switchView, isChecked) -> {
	    MakoHotplug.enableMakoHotplug(isChecked, getActivity());
	    getHandler().postDelayed(() -> {
	    // Show or hide other options on the basis of the main driver status
	    if (MakoHotplug.isMakoHotplugEnabled()) {
		if (MakoHotplug.hasMakoHotplugCoresOnTouch()) {
		    coresOnTouch.setProgress(MakoHotplug.getMakoHotplugCoresOnTouch() - 1);
		    makoHotplug.addItem(coresOnTouch);
		}
		if (MakoHotplug.hasMakoHotplugCpuFreqUnplugLimit() && mCPUFreq.getFreqs() != null) {
		    cpufreqUnplugLimit.setItem((MakoHotplug.getMakoHotplugCpuFreqUnplugLimit() / 1000)
			+ getString(R.string.mhz));
		    makoHotplug.addItem(cpufreqUnplugLimit);
		}
		if (MakoHotplug.hasMakoHotplugFirstLevel()) {
		    firstLevel.setProgress(MakoHotplug.getMakoHotplugFirstLevel());
		    makoHotplug.addItem(firstLevel);
		}
		if (MakoHotplug.hasMakoHotplugHighLoadCounter()) {
		    highLoadCounter.setProgress(MakoHotplug.getMakoHotplugHighLoadCounter());
		    makoHotplug.addItem(highLoadCounter);
		}
		if (MakoHotplug.hasMakoHotplugLoadThreshold()) {
		    loadThreshold.setProgress(MakoHotplug.getMakoHotplugLoadThreshold());
		    makoHotplug.addItem(loadThreshold);
		}
		if (MakoHotplug.hasMakoHotplugMaxLoadCounter()) {
		    maxLoadCounter.setProgress(MakoHotplug.getMakoHotplugMaxLoadCounter());
		    makoHotplug.addItem(maxLoadCounter);
		}
		if (MakoHotplug.hasMakoHotplugMinTimeCpuOnline()) {
		    minTimeCpuOnline.setProgress(MakoHotplug.getMakoHotplugMinTimeCpuOnline());
		    makoHotplug.addItem(minTimeCpuOnline);
		}
		if (MakoHotplug.hasMakoHotplugMinCoresOnline()) {
		    minCoresOnline.setProgress(MakoHotplug.getMakoHotplugMinCoresOnline() - 1);
		    makoHotplug.addItem(minCoresOnline);
		}
		if (MakoHotplug.hasMakoHotplugTimer()) {
		    timer.setProgress(MakoHotplug.getMakoHotplugTimer());
		    makoHotplug.addItem(timer);
		}
		if (MakoHotplug.hasMakoHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
		    suspendFreq.setItem((MakoHotplug.getMakoHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
		    makoHotplug.addItem(suspendFreq);
		}
	    } else {
		makoHotplug.removeItem(coresOnTouch);
		makoHotplug.removeItem(cpufreqUnplugLimit);
		makoHotplug.removeItem(firstLevel);
		makoHotplug.removeItem(highLoadCounter);
		makoHotplug.removeItem(loadThreshold);
		makoHotplug.removeItem(maxLoadCounter);
		makoHotplug.removeItem(minTimeCpuOnline);
		makoHotplug.removeItem(minCoresOnline);
		makoHotplug.removeItem(timer);
		makoHotplug.removeItem(suspendFreq);
	    }
	}, 100);

	});

	makoHotplug.addItem(enable);
	mEnableViews.add(enable);
        
	if (MakoHotplug.hasMakoHotplugCoresOnTouch()) {
            coresOnTouch.setTitle(getString(R.string.cpus_on_touch));
            coresOnTouch.setSummary(getString(R.string.cpus_on_touch_summary));
            coresOnTouch.setMax(mCPUFreq.getCpuCount());
            coresOnTouch.setMin(1);
            coresOnTouch.setProgress(MakoHotplug.getMakoHotplugCoresOnTouch() - 1);
            coresOnTouch.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugCoresOnTouch(position + 1, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(coresOnTouch);
	    } else {
            	makoHotplug.removeItem(coresOnTouch);
	    }
	}

	if (MakoHotplug.hasMakoHotplugCpuFreqUnplugLimit() && mCPUFreq.getFreqs() != null) {
            cpufreqUnplugLimit.setSummary(getString(R.string.cpu_freq_unplug_limit));
            cpufreqUnplugLimit.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpufreqUnplugLimit.setItem((MakoHotplug.getMakoHotplugCpuFreqUnplugLimit() / 1000)
		+ getString(R.string.mhz));
            cpufreqUnplugLimit.setOnItemSelected((selectView, position, item)
		-> MakoHotplug.setMakoHotplugCpuFreqUnplugLimit(
		mCPUFreq.getFreqs().get(position), getActivity()));

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(cpufreqUnplugLimit);
	    } else {
            	makoHotplug.removeItem(cpufreqUnplugLimit);
	    }
	}

	if (MakoHotplug.hasMakoHotplugFirstLevel()) {
            firstLevel.setTitle(getString(R.string.first_level));
            firstLevel.setSummary(getString(R.string.first_level_summary));
            firstLevel.setUnit("%");
            firstLevel.setProgress(MakoHotplug.getMakoHotplugFirstLevel());
            firstLevel.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugFirstLevel(position, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(firstLevel);
	    } else {
            	makoHotplug.removeItem(firstLevel);
	    }
	}

	if (MakoHotplug.hasMakoHotplugHighLoadCounter()) {
            highLoadCounter.setTitle(getString(R.string.high_load_counter));
            highLoadCounter.setProgress(MakoHotplug.getMakoHotplugHighLoadCounter());
            highLoadCounter.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugHighLoadCounter(position, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(highLoadCounter);
	    } else {
            	makoHotplug.removeItem(highLoadCounter);
	    }
	}

	if (MakoHotplug.hasMakoHotplugLoadThreshold()) {
            loadThreshold.setTitle(getString(R.string.load_threshold));
            loadThreshold.setSummary(getString(R.string.load_threshold_summary));
            loadThreshold.setUnit("%");
            loadThreshold.setProgress(MakoHotplug.getMakoHotplugLoadThreshold());
            loadThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugLoadThreshold(position, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(loadThreshold);
	    } else {
            	makoHotplug.removeItem(loadThreshold);
	    }
	}

	if (MakoHotplug.hasMakoHotplugMaxLoadCounter()) {
            maxLoadCounter.setTitle(getString(R.string.max_load_counter));
            maxLoadCounter.setProgress(MakoHotplug.getMakoHotplugMaxLoadCounter());
            maxLoadCounter.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugMaxLoadCounter(position, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(maxLoadCounter);
	    } else {
            	makoHotplug.removeItem(maxLoadCounter);
	    }
	}

	if (MakoHotplug.hasMakoHotplugMinTimeCpuOnline()) {
            minTimeCpuOnline.setTitle(getString(R.string.min_time_cpu_online));
            minTimeCpuOnline.setProgress(MakoHotplug.getMakoHotplugMinTimeCpuOnline());
            minTimeCpuOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugMinTimeCpuOnline(position, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(minTimeCpuOnline);
	    } else {
            	makoHotplug.removeItem(minTimeCpuOnline);
	    }
	}

	if (MakoHotplug.hasMakoHotplugMinCoresOnline()) {
            minCoresOnline.setTitle(getString(R.string.min_cpu_online));
            minCoresOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCoresOnline.setMax(mCPUFreq.getCpuCount());
            minCoresOnline.setMin(1);
            minCoresOnline.setProgress(MakoHotplug.getMakoHotplugMinCoresOnline() - 1);
            minCoresOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugMinCoresOnline(position + 1, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(minCoresOnline);
	    } else {
            	makoHotplug.removeItem(minCoresOnline);
	    }
	}

	if (MakoHotplug.hasMakoHotplugTimer()) {
            timer.setTitle(getString(R.string.timer));
            timer.setProgress(MakoHotplug.getMakoHotplugTimer());
            timer.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			MakoHotplug.setMakoHotplugTimer(position, getActivity());
		}
            });

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(timer);
	    } else {
            	makoHotplug.removeItem(timer);
	    }
	}

	if (MakoHotplug.hasMakoHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((MakoHotplug.getMakoHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected((selectView, position, item)
		-> MakoHotplug.setMakoHotplugSuspendFreq(mCPUFreq.getFreqs().get(position), getActivity()));

	    if (MakoHotplug.isMakoHotplugEnabled()) {
            	makoHotplug.addItem(suspendFreq);
	    } else {
            	makoHotplug.removeItem(suspendFreq);
	    }
        }

        if (makoHotplug.size() > 0) {
            items.add(makoHotplug);
        }
    }

    private void msmHotplugInit(List<RecyclerViewItem> items) {
	CardView msmHotplug = new CardView(getActivity());
	msmHotplug.setTitle(getString(R.string.msm_hotplug));

	SwitchView enable = new SwitchView();
	SwitchView debugMask = new SwitchView();
	SeekBarView minCpusOnline = new SeekBarView();
	SeekBarView maxCpusOnline = new SeekBarView();
	SeekBarView cpusBoosted = new SeekBarView();
	SeekBarView maxCpusOnlineSusp = new SeekBarView();
	SeekBarView boostLockDuration = new SeekBarView();
	SeekBarView downLockDuration = new SeekBarView();
	SeekBarView historySize = new SeekBarView();
	SeekBarView updateRate = new SeekBarView();
	SeekBarView fastLaneLoad = new SeekBarView();
	SelectView fastLaneMinFreq = new SelectView();
	SeekBarView offlineLoad = new SeekBarView();
	SwitchView ioIsBusy = new SwitchView();
	SeekBarView suspendMaxCpus = new SeekBarView();
	SelectView suspendFreq = new SelectView();
	SeekBarView suspendDeferTime = new SeekBarView();

        if (mMSMHotplug.hasMsmHotplugEnable()) {
            enable.setSummary(getString(R.string.msm_hotplug_summary));
            enable.setChecked(mMSMHotplug.isMsmHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
		mMSMHotplug.enableMsmHotplug(isChecked, getActivity());
		getHandler().postDelayed(() -> {
		// Show or hide other options on the basis of the main driver status
		if (mMSMHotplug.isMsmHotplugEnabled()) {
		    if (mMSMHotplug.hasMsmHotplugDebugMask()) {
			debugMask.setChecked(mMSMHotplug.isMsmHotplugDebugMaskEnabled());
		        msmHotplug.addItem(debugMask);
		    }
		    if (mMSMHotplug.hasMsmHotplugMinCpusOnline()) {
			minCpusOnline.setProgress(mMSMHotplug.getMsmHotplugMinCpusOnline() - 1);
		        msmHotplug.addItem(minCpusOnline);
		    }
		    if (mMSMHotplug.hasMsmHotplugMaxCpusOnline()) {
			maxCpusOnline.setProgress(mMSMHotplug.getMsmHotplugMaxCpusOnline() - 1);
		        msmHotplug.addItem(maxCpusOnline);
		    }
		    if (mMSMHotplug.hasMsmHotplugCpusBoosted()) {
			cpusBoosted.setProgress(mMSMHotplug.getMsmHotplugCpusBoosted());
		        msmHotplug.addItem(cpusBoosted);
		    }
		    if (mMSMHotplug.hasMsmHotplugMaxCpusOnlineSusp()) {
			maxCpusOnlineSusp.setProgress(mMSMHotplug.getMsmHotplugMaxCpusOnlineSusp() - 1);
		        msmHotplug.addItem(maxCpusOnlineSusp);
		    }
		    if (mMSMHotplug.hasMsmHotplugBoostLockDuration()) {
			boostLockDuration.setProgress(mMSMHotplug.getMsmHotplugBoostLockDuration() - 1);
		        msmHotplug.addItem(boostLockDuration);
		    }
		    if (mMSMHotplug.hasMsmHotplugDownLockDuration()) {
			downLockDuration.setProgress(mMSMHotplug.getMsmHotplugDownLockDuration() - 1);
		        msmHotplug.addItem(downLockDuration);
		    }
		    if (mMSMHotplug.hasMsmHotplugHistorySize()) {
			historySize.setProgress(mMSMHotplug.getMsmHotplugHistorySize() - 1);
		        msmHotplug.addItem(historySize);
		    }
		    if (mMSMHotplug.hasMsmHotplugUpdateRate()) {
			updateRate.setProgress(mMSMHotplug.getMsmHotplugUpdateRate());
		        msmHotplug.addItem(updateRate);
		    }
		    if (mMSMHotplug.hasMsmHotplugFastLaneLoad()) {
			fastLaneLoad.setProgress(mMSMHotplug.getMsmHotplugFastLaneLoad());
		        msmHotplug.addItem(fastLaneLoad);
		    }
		    if (mMSMHotplug.hasMsmHotplugFastLaneMinFreq() && mCPUFreq.getFreqs() != null) {
			fastLaneMinFreq.setItem((mMSMHotplug.getMsmHotplugFastLaneMinFreq() / 1000) + getString(R.string.mhz));
		        msmHotplug.addItem(fastLaneMinFreq);
		    }
		    if (mMSMHotplug.hasMsmHotplugOfflineLoad()) {
			offlineLoad.setProgress(mMSMHotplug.getMsmHotplugOfflineLoad());
		        msmHotplug.addItem(offlineLoad);
		    }
		    if (mMSMHotplug.hasMsmHotplugIoIsBusy()) {
			ioIsBusy.setChecked(mMSMHotplug.isMsmHotplugIoIsBusyEnabled());
		        msmHotplug.addItem(ioIsBusy);
		    }
		    if (mMSMHotplug.hasMsmHotplugSuspendMaxCpus()) {
			suspendMaxCpus.setProgress(mMSMHotplug.getMsmHotplugSuspendMaxCpus());
		        msmHotplug.addItem(suspendMaxCpus);
		    }
		    if (mMSMHotplug.hasMsmHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
			suspendFreq.setItem((mMSMHotplug.getMsmHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
		        msmHotplug.addItem(suspendFreq);
		    }
		    if (mMSMHotplug.hasMsmHotplugSuspendDeferTime()) {
			suspendDeferTime.setProgress(mMSMHotplug.getMsmHotplugSuspendDeferTime() / 10);
		        msmHotplug.addItem(suspendDeferTime);
		    }
		} else {
		    msmHotplug.removeItem(debugMask);
		    msmHotplug.removeItem(minCpusOnline);
		    msmHotplug.removeItem(maxCpusOnline);
		    msmHotplug.removeItem(cpusBoosted);
		    msmHotplug.removeItem(maxCpusOnlineSusp);
		    msmHotplug.removeItem(boostLockDuration);
		    msmHotplug.removeItem(downLockDuration);
		    msmHotplug.removeItem(historySize);
		    msmHotplug.removeItem(updateRate);
		    msmHotplug.removeItem(fastLaneLoad);
		    msmHotplug.removeItem(fastLaneMinFreq);
		    msmHotplug.removeItem(offlineLoad);
		    msmHotplug.removeItem(ioIsBusy);
		    msmHotplug.removeItem(suspendMaxCpus);
		    msmHotplug.removeItem(suspendFreq);
		    msmHotplug.removeItem(suspendDeferTime);
		}
	    }, 100);

	    });

	    msmHotplug.addItem(enable);
	    mEnableViews.add(enable);
        }

        if (mMSMHotplug.hasMsmHotplugDebugMask()) {
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mMSMHotplug.isMsmHotplugDebugMaskEnabled());
            debugMask.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplugDebugMask(isChecked, getActivity()));

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(debugMask);
	    } else {
            	msmHotplug.addItem(debugMask);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugMinCpusOnline()) {
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(mCPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(mMSMHotplug.getMsmHotplugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugMinCpusOnline(position + 1, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(minCpusOnline);
	    } else {
            	msmHotplug.addItem(minCpusOnline);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugMaxCpusOnline()) {
            maxCpusOnline.setTitle(getString(R.string.max_cpu_online));
            maxCpusOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpusOnline.setMax(mCPUFreq.getCpuCount());
            maxCpusOnline.setMin(1);
            maxCpusOnline.setProgress(mMSMHotplug.getMsmHotplugMaxCpusOnline() - 1);
            maxCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugMaxCpusOnline(position + 1, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(maxCpusOnline);
	    } else {
            	msmHotplug.addItem(maxCpusOnline);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugCpusBoosted()) {
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(mCPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(mMSMHotplug.getMsmHotplugCpusBoosted());
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugCpusBoosted(position, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(cpusBoosted);
	    } else {
            	msmHotplug.addItem(cpusBoosted);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugMaxCpusOnlineSusp()) {
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(mCPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(mMSMHotplug.getMsmHotplugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(maxCpusOnlineSusp);
	    } else {
            	msmHotplug.addItem(maxCpusOnlineSusp);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugBoostLockDuration()) {
            boostLockDuration.setTitle(getString(R.string.boost_lock_duration));
            boostLockDuration.setSummary(getString(R.string.boost_lock_duration_summary));
            boostLockDuration.setMax(5000);
            boostLockDuration.setMin(1);
            boostLockDuration.setProgress(mMSMHotplug.getMsmHotplugBoostLockDuration() - 1);
            boostLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugBoostLockDuration(position + 1, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(boostLockDuration);
	    } else {
            	msmHotplug.addItem(boostLockDuration);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugDownLockDuration()) {
            downLockDuration.setTitle(getString(R.string.down_lock_duration));
            downLockDuration.setSummary(getString(R.string.down_lock_duration_summary));
            downLockDuration.setMax(5000);
            downLockDuration.setMin(1);
            downLockDuration.setProgress(mMSMHotplug.getMsmHotplugDownLockDuration() - 1);
            downLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugDownLockDuration(position + 1, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(downLockDuration);
	    } else {
            	msmHotplug.addItem(downLockDuration);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugHistorySize()) {
            historySize.setTitle(getString(R.string.history_size));
            historySize.setSummary(getString(R.string.history_size_summary));
            historySize.setMax(60);
            historySize.setMin(1);
            historySize.setProgress(mMSMHotplug.getMsmHotplugHistorySize() - 1);
            historySize.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugHistorySize(position + 1, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(historySize);
	    } else {
            	msmHotplug.addItem(historySize);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugUpdateRate()) {
            updateRate.setTitle(getString(R.string.update_rate));
            updateRate.setSummary(getString(R.string.update_rate_summary));
            updateRate.setMax(60);
            updateRate.setProgress(mMSMHotplug.getMsmHotplugUpdateRate());
            updateRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugUpdateRate(position, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(updateRate);
	    } else {
            	msmHotplug.addItem(updateRate);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugFastLaneLoad()) {
            fastLaneLoad.setTitle(getString(R.string.fast_lane_load));
            fastLaneLoad.setSummary(getString(R.string.fast_lane_load_summary));
            fastLaneLoad.setMax(400);
            fastLaneLoad.setProgress(mMSMHotplug.getMsmHotplugFastLaneLoad());
            fastLaneLoad.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugFastLaneLoad(position, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(fastLaneLoad);
	    } else {
            	msmHotplug.addItem(fastLaneLoad);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugFastLaneMinFreq() && mCPUFreq.getFreqs() != null) {
            fastLaneMinFreq.setTitle(getString(R.string.fast_lane_min_freq));
            fastLaneMinFreq.setSummary(getString(R.string.fast_lane_min_freq_summary));
            fastLaneMinFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            fastLaneMinFreq.setItem((mMSMHotplug.getMsmHotplugFastLaneMinFreq() / 1000) + getString(R.string.mhz));
            fastLaneMinFreq.setOnItemSelected((selectView, position, item)
                    -> mMSMHotplug.setMsmHotplugFastLaneMinFreq(
                    mCPUFreq.getFreqs().get(position), getActivity()));

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(fastLaneMinFreq);
	    } else {
            	msmHotplug.addItem(fastLaneMinFreq);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugOfflineLoad()) {
            offlineLoad.setTitle(getString(R.string.offline_load));
            offlineLoad.setSummary(getString(R.string.offline_load_summary));
            offlineLoad.setProgress(mMSMHotplug.getMsmHotplugOfflineLoad());
            offlineLoad.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugOfflineLoad(position, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(offlineLoad);
	    } else {
            	msmHotplug.addItem(offlineLoad);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugIoIsBusy()) {
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(mMSMHotplug.isMsmHotplugIoIsBusyEnabled());
            ioIsBusy.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplugIoIsBusy(isChecked, getActivity()));

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(ioIsBusy);
	    } else {
            	msmHotplug.addItem(ioIsBusy);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugSuspendMaxCpus()) {
            suspendMaxCpus.setTitle(getString(R.string.max_cpu_online_screen_off));
            suspendMaxCpus.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            suspendMaxCpus.setMax(mCPUFreq.getCpuCount());
            suspendMaxCpus.setMin(1);
            suspendMaxCpus.setProgress(mMSMHotplug.getMsmHotplugSuspendMaxCpus());
            suspendMaxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugSuspendMaxCpus(position, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(suspendMaxCpus);
	    } else {
            	msmHotplug.addItem(suspendMaxCpus);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((mMSMHotplug.getMsmHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected((selectView, position, item)
                    -> mMSMHotplug.setMsmHotplugSuspendFreq(mCPUFreq.getFreqs().get(position), getActivity()));

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(suspendFreq);
	    } else {
            	msmHotplug.addItem(suspendFreq);
	    }
        }

        if (mMSMHotplug.hasMsmHotplugSuspendDeferTime()) {
            suspendDeferTime.setTitle(getString(R.string.suspend_defer_time));
            suspendDeferTime.setUnit(getString(R.string.ms));
            suspendDeferTime.setMax(5000);
            suspendDeferTime.setOffset(10);
            suspendDeferTime.setProgress(mMSMHotplug.getMsmHotplugSuspendDeferTime() / 10);
            suspendDeferTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugSuspendDeferTime(position * 10, getActivity());
                }
            });

	    if (!mMSMHotplug.hasMsmHotplugEnable() || mMSMHotplug.hasMsmHotplugEnable() && mMSMHotplug.isMsmHotplugEnabled()) {
            	msmHotplug.addItem(suspendDeferTime);
	    } else {
            	msmHotplug.addItem(suspendDeferTime);
	    }
        }

        if (msmHotplug.size() > 0) {
            items.add(msmHotplug);
        }
    }

    private void alucardHotplugInit(List<RecyclerViewItem> items) {
	CardView alucardHotplug = new CardView(getActivity());
	alucardHotplug.setTitle(getString(R.string.alucard_hotplug));

	SwitchView enable = new SwitchView();
	SwitchView ioIsBusy = new SwitchView();
	GenericSelectView samplingRate = new GenericSelectView();
	SwitchView suspend = new SwitchView();
	SeekBarView minCpusOnline = new SeekBarView();
	SeekBarView maxCoresLimit = new SeekBarView();
	SeekBarView maxCoresLimitSleep = new SeekBarView();
	SeekBarView cpuDownRate = new SeekBarView();
	SeekBarView cpuUpRate = new SeekBarView();
	final SwitchView tunables = new SwitchView();
	SelectView hf1 = new SelectView();
	SelectView hf2 = new SelectView();
	SelectView hf3 = new SelectView();
	SelectView hf4 = new SelectView();
	SelectView hf5 = new SelectView();
	SelectView hf6 = new SelectView();
	SeekBarView hl1 = new SeekBarView();
	SeekBarView hl2 = new SeekBarView();
	SeekBarView hl3 = new SeekBarView();
	SeekBarView hl4 = new SeekBarView();
	SeekBarView hl5 = new SeekBarView();
	SeekBarView hl6 = new SeekBarView();
	GenericSelectView hr1 = new GenericSelectView();
	GenericSelectView hr2 = new GenericSelectView();
	GenericSelectView hr3 = new GenericSelectView();
	GenericSelectView hr4 = new GenericSelectView();
	GenericSelectView hr5 = new GenericSelectView();
	GenericSelectView hr6 = new GenericSelectView();
	GenericSelectView hrq1 = new GenericSelectView();
	GenericSelectView hrq2 = new GenericSelectView();
	GenericSelectView hrq3 = new GenericSelectView();
	GenericSelectView hrq4 = new GenericSelectView();
	GenericSelectView hrq5 = new GenericSelectView();
	GenericSelectView hrq6 = new GenericSelectView();


	enable.setSummary(getString(R.string.alucard_hotplug_summary));
	enable.setChecked(AlucardHotplug.isAlucardHotplugEnable());
	enable.addOnSwitchListener((switchView, isChecked) -> {
	    AlucardHotplug.enableAlucardHotplug(isChecked, getActivity());
	    getHandler().postDelayed(() -> {
	    // Show or hide other options on the basis of the main driver status
	    if (AlucardHotplug.isAlucardHotplugEnable()) {
		if (AlucardHotplug.hasAlucardHotplugHpIoIsBusy()) {
		    ioIsBusy.setChecked(AlucardHotplug.isAlucardHotplugHpIoIsBusyEnable());
		    alucardHotplug.addItem(ioIsBusy);
		}
		if (AlucardHotplug.hasAlucardHotplugSamplingRate()) {
		    samplingRate.setValue(AlucardHotplug.getAlucardHotplugSamplingRate());
		    alucardHotplug.addItem(samplingRate);
		}
		if (AlucardHotplug.hasAlucardHotplugSuspend()) {
		    suspend.setChecked(AlucardHotplug.isAlucardHotplugSuspendEnabled());
		    alucardHotplug.addItem(suspend);
		}
		if (AlucardHotplug.hasAlucardHotplugMinCpusOnline()) {
		    minCpusOnline.setProgress(AlucardHotplug.getAlucardHotplugMinCpusOnline() - 1);
		    alucardHotplug.addItem(minCpusOnline);
		}
		if (AlucardHotplug.hasAlucardHotplugMaxCoresLimit()) {
		    maxCoresLimit.setProgress(AlucardHotplug.getAlucardHotplugMaxCoresLimit() - 1);
		    alucardHotplug.addItem(maxCoresLimit);
		}
		if (AlucardHotplug.hasAlucardHotplugMaxCoresLimitSleep()) {
		    maxCoresLimitSleep.setProgress(AlucardHotplug.getAlucardHotplugMaxCoresLimitSleep() - 1);
		    alucardHotplug.addItem(maxCoresLimitSleep);
		}
		if (AlucardHotplug.hasAlucardHotplugCpuDownRate()) {
		    cpuDownRate.setProgress(AlucardHotplug.getAlucardHotplugCpuDownRate() - 1);
		    alucardHotplug.addItem(cpuDownRate);
		}
		if (AlucardHotplug.hasAlucardHotplugCpuUpRate()) {
		    cpuUpRate.setProgress(AlucardHotplug.getAlucardHotplugCpuUpRate() - 1);
		    alucardHotplug.addItem(cpuUpRate);
		}
		alucardHotplug.addItem(tunables);
		if (Prefs.getBoolean("advanced_tunables", false, getActivity()) == true) {
		    if (AlucardHotplug.hasHotplugFreq11()) {
		    	alucardHotplug.addItem(hf1);
		    }
		    if (AlucardHotplug.hasHotplugFreq20()) {
		    	alucardHotplug.addItem(hf2);
		    }
		    if (AlucardHotplug.hasHotplugFreq21()) {
		    	alucardHotplug.addItem(hf3);
		    }
		    if (AlucardHotplug.hasHotplugFreq30()) {
		    	alucardHotplug.addItem(hf4);
		    }
		    if (AlucardHotplug.hasHotplugFreq31()) {
		    	alucardHotplug.addItem(hf5);
		    }
		    if (AlucardHotplug.hasHotplugFreq40()) {
		    	alucardHotplug.addItem(hf6);
		    }
		    if (AlucardHotplug.hasHotplugLoad11()) {
		    	alucardHotplug.addItem(hl1);
		    }
		    if (AlucardHotplug.hasHotplugLoad20()) {
		    	alucardHotplug.addItem(hl2);
		    }
		    if (AlucardHotplug.hasHotplugLoad21()) {
		    	alucardHotplug.addItem(hl3);
		    }
		    if (AlucardHotplug.hasHotplugLoad30()) {
		    	alucardHotplug.addItem(hl4);
		    }
		    if (AlucardHotplug.hasHotplugLoad31()) {
		    	alucardHotplug.addItem(hl5);
		    }
		    if (AlucardHotplug.hasHotplugLoad40()) {
		    	alucardHotplug.addItem(hl6);
		    }
		    if (AlucardHotplug.hasHotplugRate11()) {
		    	alucardHotplug.addItem(hr1);
		    }
		    if (AlucardHotplug.hasHotplugRate20()) {
		    	alucardHotplug.addItem(hr2);
		    }
		    if (AlucardHotplug.hasHotplugRate21()) {
		    	alucardHotplug.addItem(hr3);
		    }
		    if (AlucardHotplug.hasHotplugRate30()) {
		    	alucardHotplug.addItem(hr4);
		    }
		    if (AlucardHotplug.hasHotplugRate31()) {
		    	alucardHotplug.addItem(hr5);
		    }
		    if (AlucardHotplug.hasHotplugRate40()) {
		    	alucardHotplug.addItem(hr6);
		    }
		    if (AlucardHotplug.hasHotplugRQ11()) {
		    	alucardHotplug.addItem(hrq1);
		    }
		    if (AlucardHotplug.hasHotplugRQ20()) {
		    	alucardHotplug.addItem(hrq2);
		    }
		    if (AlucardHotplug.hasHotplugRQ21()) {
		    	alucardHotplug.addItem(hrq3);
		    }
		    if (AlucardHotplug.hasHotplugRQ30()) {
		    	alucardHotplug.addItem(hrq4);
		    }
		    if (AlucardHotplug.hasHotplugRQ31()) {
		    	alucardHotplug.addItem(hrq5);
		    }
		    if (AlucardHotplug.hasHotplugRQ40()) {
		    	alucardHotplug.addItem(hrq6);
		    }
		}
	    } else {
		alucardHotplug.removeItem(ioIsBusy);
		alucardHotplug.removeItem(samplingRate);
		alucardHotplug.removeItem(suspend);
		alucardHotplug.removeItem(minCpusOnline);
		alucardHotplug.removeItem(maxCoresLimit);
		alucardHotplug.removeItem(maxCoresLimitSleep);
		alucardHotplug.removeItem(cpuDownRate);
		alucardHotplug.removeItem(cpuUpRate);
		alucardHotplug.removeItem(tunables);
		alucardHotplug.removeItem(hf1);
		alucardHotplug.removeItem(hf2);
		alucardHotplug.removeItem(hf3);
		alucardHotplug.removeItem(hf4);
		alucardHotplug.removeItem(hf5);
		alucardHotplug.removeItem(hf6);
		alucardHotplug.removeItem(hl1);
		alucardHotplug.removeItem(hl2);
		alucardHotplug.removeItem(hl3);
		alucardHotplug.removeItem(hl4);
		alucardHotplug.removeItem(hl5);
		alucardHotplug.removeItem(hl6);
		alucardHotplug.removeItem(hr1);
		alucardHotplug.removeItem(hr2);
		alucardHotplug.removeItem(hr3);
		alucardHotplug.removeItem(hr4);
		alucardHotplug.removeItem(hr5);
		alucardHotplug.removeItem(hr6);
		alucardHotplug.removeItem(hrq1);
		alucardHotplug.removeItem(hrq2);
		alucardHotplug.removeItem(hrq3);
		alucardHotplug.removeItem(hrq4);
		alucardHotplug.removeItem(hrq5);
		alucardHotplug.removeItem(hrq6);
	    }
	}, 100);
	});

	alucardHotplug.addItem(enable);
	mEnableViews.add(enable);

	if (AlucardHotplug.hasAlucardHotplugHpIoIsBusy()) {
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(AlucardHotplug.isAlucardHotplugHpIoIsBusyEnable());
            ioIsBusy.addOnSwitchListener((switchView, isChecked)
		-> AlucardHotplug.enableAlucardHotplugHpIoIsBusy(isChecked, getActivity()));

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(ioIsBusy);
	    } else {
            	alucardHotplug.removeItem(ioIsBusy);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugSamplingRate()) {
            samplingRate.setTitle(getString(R.string.sampling_rate));
            samplingRate.setSummary(("Set ") + getString(R.string.sampling_rate));
            samplingRate.setValue(AlucardHotplug.getAlucardHotplugSamplingRate());
            samplingRate.setInputType(InputType.TYPE_CLASS_NUMBER);
            samplingRate.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    AlucardHotplug.setAlucardHotplugSamplingRate(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    samplingRate.setValue(AlucardHotplug.getAlucardHotplugSamplingRate());
		    },
	        500);
                }
            });

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(samplingRate);
	    } else {
            	alucardHotplug.removeItem(samplingRate);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugSuspend()) {
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(AlucardHotplug.isAlucardHotplugSuspendEnabled());
            suspend.addOnSwitchListener((switchView, isChecked)
		-> AlucardHotplug.enableAlucardHotplugSuspend(isChecked, getActivity()));

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(suspend);
	    } else {
            	alucardHotplug.removeItem(suspend);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugMinCpusOnline()) {
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(mCPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(AlucardHotplug.getAlucardHotplugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			AlucardHotplug.setAlucardHotplugMinCpusOnline(position + 1, getActivity());
		}
            });

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(minCpusOnline);
	    } else {
            	alucardHotplug.removeItem(minCpusOnline);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugMaxCoresLimit()) {
            maxCoresLimit.setTitle(getString(R.string.max_cpu_online));
            maxCoresLimit.setSummary(getString(R.string.max_cpu_online_summary));
            maxCoresLimit.setMax(mCPUFreq.getCpuCount());
            maxCoresLimit.setMin(1);
            maxCoresLimit.setProgress(AlucardHotplug.getAlucardHotplugMaxCoresLimit() - 1);
            maxCoresLimit.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			AlucardHotplug.setAlucardHotplugMaxCoresLimit(position + 1, getActivity());
		}
            });

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(maxCoresLimit);
	    } else {
            	alucardHotplug.removeItem(maxCoresLimit);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugMaxCoresLimitSleep()) {
            maxCoresLimitSleep.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCoresLimitSleep.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCoresLimitSleep.setMax(mCPUFreq.getCpuCount());
            maxCoresLimitSleep.setMin(1);
            maxCoresLimitSleep.setProgress(AlucardHotplug.getAlucardHotplugMaxCoresLimitSleep() - 1);
            maxCoresLimitSleep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			AlucardHotplug.setAlucardHotplugMaxCoresLimitSleep(position + 1, getActivity());
		}
            });

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(maxCoresLimitSleep);
	    } else {
            	alucardHotplug.removeItem(maxCoresLimitSleep);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugCpuDownRate()) {
            cpuDownRate.setTitle(getString(R.string.cpu_down_rate));
            cpuDownRate.setUnit("%");
            cpuDownRate.setMin(1);
            cpuDownRate.setProgress(AlucardHotplug.getAlucardHotplugCpuDownRate() - 1);
            cpuDownRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			AlucardHotplug.setAlucardHotplugCpuDownRate(position + 1, getActivity());
		}
            });

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(cpuDownRate);
	    } else {
            	alucardHotplug.removeItem(cpuDownRate);
	    }
	}

	if (AlucardHotplug.hasAlucardHotplugCpuUpRate()) {
            cpuUpRate.setTitle(getString(R.string.cpu_up_rate));
            cpuUpRate.setUnit("%");
            cpuUpRate.setMin(1);
            cpuUpRate.setProgress(AlucardHotplug.getAlucardHotplugCpuUpRate() - 1);
            cpuUpRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			AlucardHotplug.setAlucardHotplugCpuUpRate(position + 1, getActivity());
		}
            });

	    if (AlucardHotplug.isAlucardHotplugEnable()) {
            	alucardHotplug.addItem(cpuUpRate);
	    } else {
            	alucardHotplug.removeItem(cpuUpRate);
	    }
        }

	if (!(Prefs.getBoolean("advanced_tunables", false, getActivity())))
            Prefs.saveBoolean("advanced_tunables", false, getActivity());

	tunables.setSummary(getString(R.string.adv_sett));
	tunables.setChecked(Prefs.getBoolean("advanced_tunables", false, getActivity()));

	if (AlucardHotplug.isAlucardHotplugEnable()) {
	    alucardHotplug.addItem(tunables);
	} else {
	    alucardHotplug.removeItem(tunables);
	}

	hf1.setSummary(getString(R.string.hotplug_frequency) + (" 1 1"));
	hf1.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
	hf1.setItem((AlucardHotplug.getHotplugFreq11() / 1000)
		+ getString(R.string.mhz));
	hf1.setOnItemSelected((selectView, position, item)
		-> AlucardHotplug.setHotplugFreq11(
		mCPUFreq.getFreqs().get(position), getActivity()));

	hf2.setSummary(getString(R.string.hotplug_frequency) + (" 2 0"));
	hf2.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
	hf2.setItem((AlucardHotplug.getHotplugFreq20() / 1000)
		+ getString(R.string.mhz));
	hf2.setOnItemSelected((selectView, position, item)
		-> AlucardHotplug.setHotplugFreq20(
		mCPUFreq.getFreqs().get(position), getActivity()));

	hf3.setSummary(getString(R.string.hotplug_frequency) + (" 2 1"));
	hf3.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
	hf3.setItem((AlucardHotplug.getHotplugFreq21() / 1000)
		+ getString(R.string.mhz));
	hf3.setOnItemSelected((selectView, position, item)
		-> AlucardHotplug.setHotplugFreq21(
		mCPUFreq.getFreqs().get(position), getActivity()));

	hf4.setSummary(getString(R.string.hotplug_frequency) + (" 3 0"));
	hf4.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
	hf4.setItem((AlucardHotplug.getHotplugFreq30() / 1000)
		+ getString(R.string.mhz));
	hf4.setOnItemSelected((selectView, position, item)
		-> AlucardHotplug.setHotplugFreq30(
		mCPUFreq.getFreqs().get(position), getActivity()));

	hf5.setSummary(getString(R.string.hotplug_frequency) + (" 3 1"));
	hf5.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
	hf5.setItem((AlucardHotplug.getHotplugFreq31() / 1000)
		+ getString(R.string.mhz));
	hf5.setOnItemSelected((selectView, position, item)
		-> AlucardHotplug.setHotplugFreq31(
		mCPUFreq.getFreqs().get(position), getActivity()));

	hf6.setSummary(getString(R.string.hotplug_frequency) + (" 4 0"));
	hf6.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
	hf6.setItem((AlucardHotplug.getHotplugFreq40() / 1000)
		+ getString(R.string.mhz));
	hf6.setOnItemSelected((selectView, position, item)
		-> AlucardHotplug.setHotplugFreq40(
		mCPUFreq.getFreqs().get(position), getActivity()));

	hl1.setTitle(getString(R.string.hotplug_load) + (" 1 1"));
	hl1.setProgress(AlucardHotplug.getHotplugLoad11());
	hl1.setUnit(" %");
	hl1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
	    @Override
	    public void onMove(SeekBarView seekBarView, int position, String value) {
	    }

	    @Override
	    public void onStop(SeekBarView seekBarView, int position, String value) {
		AlucardHotplug.setHotplugLoad11(position, getActivity());
	    }
	});

	hl2.setTitle(getString(R.string.hotplug_load) + (" 2 0"));
	hl2.setProgress(AlucardHotplug.getHotplugLoad20());
	hl2.setUnit(" %");
	hl2.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
	    @Override
	    public void onMove(SeekBarView seekBarView, int position, String value) {
	    }

	    @Override
	    public void onStop(SeekBarView seekBarView, int position, String value) {
		AlucardHotplug.setHotplugLoad20(position, getActivity());
	    }
	});

	hl3.setTitle(getString(R.string.hotplug_load) + (" 2 1"));
	hl3.setProgress(AlucardHotplug.getHotplugLoad21());
	hl3.setUnit(" %");
	hl3.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
	    @Override
	    public void onMove(SeekBarView seekBarView, int position, String value) {
	    }

	    @Override
	    public void onStop(SeekBarView seekBarView, int position, String value) {
		AlucardHotplug.setHotplugLoad21(position, getActivity());
	    }
	});

	hl4.setTitle(getString(R.string.hotplug_load) + (" 3 0"));
	hl4.setProgress(AlucardHotplug.getHotplugLoad30());
	hl4.setUnit(" %");
	hl4.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
	    @Override
	    public void onMove(SeekBarView seekBarView, int position, String value) {
	    }

	    @Override
	    public void onStop(SeekBarView seekBarView, int position, String value) {
		AlucardHotplug.setHotplugLoad30(position, getActivity());
	    }
	});

	hl5.setTitle(getString(R.string.hotplug_load) + (" 3 1"));
	hl5.setProgress(AlucardHotplug.getHotplugLoad31());
	hl5.setUnit(" %");
	hl5.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
	    @Override
	    public void onMove(SeekBarView seekBarView, int position, String value) {
	    }

	    @Override
	    public void onStop(SeekBarView seekBarView, int position, String value) {
		AlucardHotplug.setHotplugLoad31(position, getActivity());
	    }
	});

	hl6.setTitle(getString(R.string.hotplug_load) + (" 4 0"));
	hl6.setProgress(AlucardHotplug.getHotplugLoad40());
	hl6.setUnit(" %");
	hl6.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
	    @Override
	    public void onMove(SeekBarView seekBarView, int position, String value) {
	    }

	    @Override
	    public void onStop(SeekBarView seekBarView, int position, String value) {
		AlucardHotplug.setHotplugLoad40(position, getActivity());
	    }
	});

	hr1.setSummary(getString(R.string.hotplug_rate) + (" 1 1"));
	hr1.setValue(AlucardHotplug.getHotplugRate11());
	hr1.setInputType(InputType.TYPE_CLASS_NUMBER);
	hr1.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRate11(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hr1.setValue(AlucardHotplug.getHotplugRate11());
		},
	    500);
	    }
	});

	hr2.setSummary(getString(R.string.hotplug_rate) + (" 2 0"));
	hr2.setValue(AlucardHotplug.getHotplugRate20());
	hr2.setInputType(InputType.TYPE_CLASS_NUMBER);
	hr2.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRate20(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hr2.setValue(AlucardHotplug.getHotplugRate20());
		},
	    500);
	    }
	});

	hr3.setSummary(getString(R.string.hotplug_rate) + (" 2 1"));
	hr3.setValue(AlucardHotplug.getHotplugRate21());
	hr3.setInputType(InputType.TYPE_CLASS_NUMBER);
	hr3.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRate21(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hr3.setValue(AlucardHotplug.getHotplugRate21());
		},
	    500);
	    }
	});

	hr4.setSummary(getString(R.string.hotplug_rate) + (" 3 0"));
	hr4.setValue(AlucardHotplug.getHotplugRate30());
	hr4.setInputType(InputType.TYPE_CLASS_NUMBER);
	hr4.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRate30(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hr4.setValue(AlucardHotplug.getHotplugRate30());
		},
	    500);
	    }
	});

	hr5.setSummary(getString(R.string.hotplug_rate) + (" 3 1"));
	hr5.setValue(AlucardHotplug.getHotplugRate31());
	hr5.setInputType(InputType.TYPE_CLASS_NUMBER);
	hr5.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRate31(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hr5.setValue(AlucardHotplug.getHotplugRate31());
		},
	    500);
	    }
	});

	hr6.setSummary(getString(R.string.hotplug_rate) + (" 4 0"));
	hr6.setValue(AlucardHotplug.getHotplugRate40());
	hr6.setInputType(InputType.TYPE_CLASS_NUMBER);
	hr6.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRate40(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hr6.setValue(AlucardHotplug.getHotplugRate40());
		},
	    500);
	    }
	});

	hrq1.setSummary(getString(R.string.hotplug_rq) + (" 1 1"));
	hrq1.setValue(AlucardHotplug.getHotplugRQ11());
	hrq1.setInputType(InputType.TYPE_CLASS_NUMBER);
	hrq1.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRQ11(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hrq1.setValue(AlucardHotplug.getHotplugRQ11());
		},
	    500);
	    }
	});

	hrq2.setSummary(getString(R.string.hotplug_rq) + (" 2 0"));
	hrq2.setValue(AlucardHotplug.getHotplugRQ20());
	hrq2.setInputType(InputType.TYPE_CLASS_NUMBER);
	hrq2.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRQ20(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hrq2.setValue(AlucardHotplug.getHotplugRQ20());
		},
	    500);
	    }
	});

	hrq3.setSummary(getString(R.string.hotplug_rq) + (" 2 1"));
	hrq3.setValue(AlucardHotplug.getHotplugRQ21());
	hrq3.setInputType(InputType.TYPE_CLASS_NUMBER);
	hrq3.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRQ21(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hrq3.setValue(AlucardHotplug.getHotplugRQ21());
		},
	    500);
	    }
	});

	hrq4.setSummary(getString(R.string.hotplug_rq) + (" 3 0"));
	hrq4.setValue(AlucardHotplug.getHotplugRQ30());
	hrq4.setInputType(InputType.TYPE_CLASS_NUMBER);
	hrq4.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRQ30(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hrq4.setValue(AlucardHotplug.getHotplugRQ30());
		},
	    500);
	    }
	});

	hrq5.setSummary(getString(R.string.hotplug_rq) + (" 3 1"));
	hrq5.setValue(AlucardHotplug.getHotplugRQ31());
	hrq5.setInputType(InputType.TYPE_CLASS_NUMBER);
	hrq5.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRQ31(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hrq5.setValue(AlucardHotplug.getHotplugRQ31());
		},
	    500);
	    }
	});

	hrq6.setSummary(getString(R.string.hotplug_rq) + (" 4 0"));
	hrq6.setValue(AlucardHotplug.getHotplugRQ40());
	hrq6.setInputType(InputType.TYPE_CLASS_NUMBER);
	hrq6.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    @Override
	    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		AlucardHotplug.setHotplugRQ40(value, getActivity());
		genericSelectView.setValue(value);
		getHandler().postDelayed(() -> {
		hrq6.setValue(AlucardHotplug.getHotplugRQ40());
		},
	    500);
	    }
	});

	class tunablesManager {
	    public void showadvancedtunables (boolean enable) {
		if (AlucardHotplug.hasHotplugFreq11() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hf1);
		} else {
		    alucardHotplug.removeItem(hf1);
	    	}
		if (AlucardHotplug.hasHotplugFreq20() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hf2);
		} else {
		    alucardHotplug.removeItem(hf2);
	    	}
		if (AlucardHotplug.hasHotplugFreq21() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hf3);
		} else {
		    alucardHotplug.removeItem(hf3);
	    	}
		if (AlucardHotplug.hasHotplugFreq30() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hf4);
		} else {
		    alucardHotplug.removeItem(hf4);
	    	}
		if (AlucardHotplug.hasHotplugFreq31() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hf5);
		} else {
		    alucardHotplug.removeItem(hf5);
	    	}
		if (AlucardHotplug.hasHotplugFreq40() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hf6);
		} else {
		    alucardHotplug.removeItem(hf6);
	    	}
		if (AlucardHotplug.hasHotplugLoad11() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hl1);
		} else {
		    alucardHotplug.removeItem(hl1);
	    	}
		if (AlucardHotplug.hasHotplugLoad20() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hl2);
		} else {
		    alucardHotplug.removeItem(hl2);
	    	}
		if (AlucardHotplug.hasHotplugLoad21() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hl3);
		} else {
		    alucardHotplug.removeItem(hl3);
	    	}
		if (AlucardHotplug.hasHotplugLoad30() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hl4);
		} else {
		    alucardHotplug.removeItem(hl4);
	    	}
		if (AlucardHotplug.hasHotplugLoad31() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hl5);
		} else {
		    alucardHotplug.removeItem(hl5);
	    	}
		if (AlucardHotplug.hasHotplugLoad40() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hl6);
		} else {
		    alucardHotplug.removeItem(hl6);
	    	}
		if (AlucardHotplug.hasHotplugRate11() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hr1);
		} else {
		    alucardHotplug.removeItem(hr1);
	    	}
		if (AlucardHotplug.hasHotplugRate20() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hr2);
		} else {
		    alucardHotplug.removeItem(hr2);
	    	}
		if (AlucardHotplug.hasHotplugRate21() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hr3);
		} else {
		    alucardHotplug.removeItem(hr3);
	    	}
		if (AlucardHotplug.hasHotplugRate30() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hr4);
		} else {
		    alucardHotplug.removeItem(hr4);
	    	}
		if (AlucardHotplug.hasHotplugRate31() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hr5);
		} else {
		    alucardHotplug.removeItem(hr5);
	    	}
		if (AlucardHotplug.hasHotplugRate40() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hr6);
		} else {
		    alucardHotplug.removeItem(hr6);
	    	}
		if (AlucardHotplug.hasHotplugRQ11() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hrq1);
		} else {
		    alucardHotplug.removeItem(hrq1);
	    	}
		if (AlucardHotplug.hasHotplugRQ20() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hrq2);
		} else {
		    alucardHotplug.removeItem(hrq2);
	    	}
		if (AlucardHotplug.hasHotplugRQ21() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hrq3);
		} else {
		    alucardHotplug.removeItem(hrq3);
	    	}
		if (AlucardHotplug.hasHotplugRQ30() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hrq4);
		} else {
		    alucardHotplug.removeItem(hrq4);
	    	}
		if (AlucardHotplug.hasHotplugRQ31() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hrq5);
		} else {
		    alucardHotplug.removeItem(hrq5);
	    	}
		if (AlucardHotplug.hasHotplugRQ40() && AlucardHotplug.isAlucardHotplugEnable() && enable == true) {
		    alucardHotplug.addItem(hrq6);
		} else {
		    alucardHotplug.removeItem(hrq6);
	    	}
            }
	}

        final tunablesManager manager = new tunablesManager();
        if (Prefs.getBoolean("advanced_tunables", false, getActivity()) == true) {
            manager.showadvancedtunables(true);
        } else {
            manager.showadvancedtunables(false);
        }
        tunables.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("advanced_tunables", isChecked, getActivity());
                manager.showadvancedtunables(isChecked);
                }
            }
	);


        if (alucardHotplug.size() > 0) {
            items.add(alucardHotplug);
        }
    }

    private void mbHotplugInit(List<RecyclerViewItem> items) {
	CardView mbHotplugCard = new CardView(getActivity());
	mbHotplugCard.setTitle(mMBHotplug.getMBName(getActivity()));

	SwitchView enable = new SwitchView();
	SwitchView scroffSingleCore = new SwitchView();
	SeekBarView minCpus = new SeekBarView();
	SeekBarView maxCpus = new SeekBarView();
	SeekBarView maxCpusOnlineSusp = new SeekBarView();
	SelectView idleFreq = new SelectView();
	SwitchView boost = new SwitchView();
	SeekBarView boostTime = new SeekBarView();
	SeekBarView cpusBoosted = new SeekBarView();
	SelectView boostFreq = new SelectView();
	GenericSelectView startDelay = new GenericSelectView();
	SeekBarView delay = new SeekBarView();
	GenericSelectView pause = new GenericSelectView();

        if (mMBHotplug.hasMBGHotplugEnable()) {
            enable.setSummary(getString(R.string.mb_hotplug_summary));
            enable.setChecked(mMBHotplug.isMBHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
		mMBHotplug.enableMBHotplug(isChecked, getActivity());
		getHandler().postDelayed(() -> {
		// Show or hide other options on the basis of the main driver status
		if (mMBHotplug.isMBHotplugEnabled()) {
		    if (mMBHotplug.hasMBHotplugScroffSingleCore()) {
			scroffSingleCore.setChecked(mMBHotplug.isMBHotplugScroffSingleCoreEnabled());
			mbHotplugCard.addItem(scroffSingleCore);
		    }
		    if (mMBHotplug.hasMBHotplugMinCpus()) {
			minCpus.setProgress(mMBHotplug.getMBHotplugMinCpus() - 1);
			mbHotplugCard.addItem(minCpus);
		    }
		    if (mMBHotplug.hasMBHotplugMaxCpus()) {
			maxCpus.setProgress(mMBHotplug.getMBHotplugMaxCpus() - 1);
			mbHotplugCard.addItem(maxCpus);
		    }
		    if (mMBHotplug.hasMBHotplugMaxCpusOnlineSusp()) {
			maxCpusOnlineSusp.setProgress(mMBHotplug.getMBHotplugMaxCpusOnlineSusp() - 1);
			mbHotplugCard.addItem(maxCpusOnlineSusp);
		    }
		    if (mMBHotplug.hasMBHotplugIdleFreq() && mCPUFreq.getFreqs() != null) {
			idleFreq.setItem((mMBHotplug.getMBHotplugIdleFreq() / 1000) + getString(R.string.mhz));
			mbHotplugCard.addItem(idleFreq);
		    }
		    if (mMBHotplug.hasMBHotplugBoostEnable()) {
			boost.setChecked(mMBHotplug.isMBHotplugBoostEnabled());
			mbHotplugCard.addItem(boost);
		    }
		    if (mMBHotplug.hasMBHotplugBoostTime()) {
			boostTime.setProgress(mMBHotplug.getMBHotplugBoostTime() / 100);
			mbHotplugCard.addItem(boostTime);
		    }
		    if (mMBHotplug.hasMBHotplugCpusBoosted()) {
			cpusBoosted.setProgress(mMBHotplug.getMBHotplugCpusBoosted());
			mbHotplugCard.addItem(cpusBoosted);
		    }
		    if (mMBHotplug.hasMBHotplugBoostFreqs() && mCPUFreq.getFreqs() != null) {
			List<Integer> freqs = mMBHotplug.getMBHotplugBoostFreqs();
			for (int i = 0; i < freqs.size(); i++) {
			    boostFreq.setItem((freqs.get(i) / 1000) + getString(R.string.mhz));
			}
			mbHotplugCard.addItem(boostFreq);
		    }
		    if (mMBHotplug.hasMBHotplugStartDelay()) {
			startDelay.setValue(mMBHotplug.getMBHotplugStartDelay());
			mbHotplugCard.addItem(startDelay);
		    }
		    if (mMBHotplug.hasMBHotplugDelay()) {
			delay.setProgress(mMBHotplug.getMBHotplugDelay());
			mbHotplugCard.addItem(delay);
		    }
		    if (mMBHotplug.hasMBHotplugPause()) {
			pause.setValue(mMBHotplug.getMBHotplugPause());
			mbHotplugCard.addItem(pause);
		    }
		} else {
		    mbHotplugCard.removeItem(scroffSingleCore);
		    mbHotplugCard.removeItem(minCpus);
		    mbHotplugCard.removeItem(maxCpus);
		    mbHotplugCard.removeItem(maxCpusOnlineSusp);
		    mbHotplugCard.removeItem(idleFreq);
		    mbHotplugCard.removeItem(boost);
		    mbHotplugCard.removeItem(boostTime);
		    mbHotplugCard.removeItem(cpusBoosted);
		    mbHotplugCard.removeItem(boostFreq);
		    mbHotplugCard.removeItem(startDelay);
		    mbHotplugCard.removeItem(delay);
		    mbHotplugCard.removeItem(pause);
	    	}
	    }, 100);
	    });

            mbHotplugCard.addItem(enable);
            mEnableViews.add(enable);
        }

        if (mMBHotplug.hasMBHotplugScroffSingleCore()) {
            scroffSingleCore.setTitle(getString(R.string.screen_off_single_cpu));
            scroffSingleCore.setSummary(getString(R.string.screen_off_single_cpu_summary));
            scroffSingleCore.setChecked(mMBHotplug.isMBHotplugScroffSingleCoreEnabled());
            scroffSingleCore.addOnSwitchListener((switchView, isChecked)
		-> mMBHotplug.enableMBHotplugScroffSingleCore(isChecked, getActivity()));

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(scroffSingleCore);
	    } else {
            	mbHotplugCard.removeItem(scroffSingleCore);
	    }
        }

        if (mMBHotplug.hasMBHotplugMinCpus()) {
            minCpus.setTitle(getString(R.string.min_cpu_online));
            minCpus.setSummary(getString(R.string.min_cpu_online_summary));
            minCpus.setMax(mCPUFreq.getCpuCount());
            minCpus.setMin(1);
            minCpus.setProgress(mMBHotplug.getMBHotplugMinCpus() - 1);
            minCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugMinCpus(position + 1, getActivity());
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(minCpus);
	    } else {
            	mbHotplugCard.removeItem(minCpus);
	    }
        }

        if (mMBHotplug.hasMBHotplugMaxCpus()) {
            maxCpus.setTitle(getString(R.string.max_cpu_online));
            maxCpus.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpus.setMax(mCPUFreq.getCpuCount());
            maxCpus.setMin(1);
            maxCpus.setProgress(mMBHotplug.getMBHotplugMaxCpus() - 1);
            maxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugMaxCpus(position + 1, getActivity());
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(maxCpus);
	    } else {
            	mbHotplugCard.removeItem(maxCpus);
	    }
        }

        if (mMBHotplug.hasMBHotplugMaxCpusOnlineSusp()) {
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(mCPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(mMBHotplug.getMBHotplugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(maxCpusOnlineSusp);
	    } else {
            	mbHotplugCard.removeItem(maxCpusOnlineSusp);
	    }
        }

        if (mMBHotplug.hasMBHotplugIdleFreq() && mCPUFreq.getFreqs() != null) {
            idleFreq.setTitle(getString(R.string.idle_frequency));
            idleFreq.setSummary(getString(R.string.idle_frequency_summary));
            idleFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            idleFreq.setItem((mMBHotplug.getMBHotplugIdleFreq() / 1000) + getString(R.string.mhz));
            idleFreq.setOnItemSelected((selectView, position, item)
		-> mMBHotplug.setMBHotplugIdleFreq(
		mCPUFreq.getFreqs().get(position), getActivity()));

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(idleFreq);
	    } else {
            	mbHotplugCard.removeItem(idleFreq);
	    }
        }

        if (mMBHotplug.hasMBHotplugBoostEnable()) {
            boost.setTitle(getString(R.string.touch_boost));
            boost.setSummary(getString(R.string.touch_boost_summary));
            boost.setChecked(mMBHotplug.isMBHotplugBoostEnabled());
            boost.addOnSwitchListener((switchView, isChecked)
		-> mMBHotplug.enableMBHotplugBoost(isChecked, getActivity()));

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(boost);
	    } else {
            	mbHotplugCard.removeItem(boost);
	    }
        }

        if (mMBHotplug.hasMBHotplugBoostTime()) {
            boostTime.setTitle(getString(R.string.touch_boost_time));
            boostTime.setSummary(getString(R.string.touch_boost_time_summary));
            boostTime.setUnit(getString(R.string.ms));
            boostTime.setMax(5000);
            boostTime.setOffset(100);
            boostTime.setProgress(mMBHotplug.getMBHotplugBoostTime() / 100);
            boostTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugBoostTime(position * 100, getActivity());
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(boostTime);
	    } else {
            	mbHotplugCard.removeItem(boostTime);
	    }
        }

        if (mMBHotplug.hasMBHotplugCpusBoosted()) {
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(mCPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(mMBHotplug.getMBHotplugCpusBoosted());
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugCpusBoosted(position, getActivity());
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(cpusBoosted);
	    } else {
            	mbHotplugCard.removeItem(cpusBoosted);
	    }
        }

        if (mMBHotplug.hasMBHotplugBoostFreqs() && mCPUFreq.getFreqs() != null) {
            List<Integer> freqs = mMBHotplug.getMBHotplugBoostFreqs();

            for (int i = 0; i < freqs.size(); i++) {
                boostFreq.setSummary(getString(R.string.boost_frequency_core, i));
                boostFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
                boostFreq.setItem((freqs.get(i) / 1000) + getString(R.string.mhz));
                final int pos = i;
                boostFreq.setOnItemSelected((selectView, position, item)
		    -> mMBHotplug.setMBHotplugBoostFreqs(
		    pos, mCPUFreq.getFreqs().get(position), getActivity()));

	    	if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	    mbHotplugCard.addItem(boostFreq);
	    	} else {
            	    mbHotplugCard.removeItem(boostFreq);
	    	}
            }
        }

        if (mMBHotplug.hasMBHotplugStartDelay()) {
            startDelay.setTitle(getString(R.string.start_delay));
            startDelay.setSummary(getString(R.string.start_delay_summary));
            startDelay.setValue(mMBHotplug.getMBHotplugStartDelay());
            startDelay.setInputType(InputType.TYPE_CLASS_NUMBER);
            startDelay.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    	@Override
	    	public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		    mMBHotplug.setMBHotplugStartDelay(value, getActivity());
		    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    startDelay.setValue(mMBHotplug.getMBHotplugStartDelay());
		    },
	        500);
	    	}
	    });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(startDelay);
	    } else {
            	mbHotplugCard.removeItem(startDelay);
	    }
        }

        if (mMBHotplug.hasMBHotplugDelay()) {
            delay.setTitle(getString(R.string.delay));
            delay.setSummary(getString(R.string.delay_summary));
            delay.setUnit(getString(R.string.ms));
            delay.setMax(200);
            delay.setProgress(mMBHotplug.getMBHotplugDelay());
            delay.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugDelay(position, getActivity());
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(delay);
	    } else {
            	mbHotplugCard.removeItem(delay);
	    }
        }

        if (mMBHotplug.hasMBHotplugPause()) {
            pause.setTitle(getString(R.string.pause));
            pause.setSummary(getString(R.string.pause_summary));
            pause.setValue(mMBHotplug.getMBHotplugPause());
            pause.setInputType(InputType.TYPE_CLASS_NUMBER);
            pause.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
	    	@Override
	    	public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
		    mMBHotplug.setMBHotplugPause(value, getActivity());
		    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    pause.setValue(mMBHotplug.getMBHotplugPause());
		    },
	        500);
                }
            });

	    if (!mMBHotplug.hasMBGHotplugEnable() || mMBHotplug.hasMBGHotplugEnable() && mMBHotplug.isMBHotplugEnabled()) {
            	mbHotplugCard.addItem(pause);
	    } else {
            	mbHotplugCard.removeItem(pause);
	    }
        }

        if (mbHotplugCard.size() > 0) {
            items.add(mbHotplugCard);
        }
    }

    private void autoSMPInit(List<RecyclerViewItem> items) {
	CardView autoSMPCard = new CardView(getActivity());
	autoSMPCard.setTitle(getString(R.string.autosmp));

	SwitchView enable = new SwitchView();
	SwitchView hotplugSuspend = new SwitchView();
	SeekBarView cpuFreqDown = new SeekBarView();
	SeekBarView cpuFreqUp = new SeekBarView();
	SeekBarView cycleDown = new SeekBarView();
	SeekBarView cycleUp = new SeekBarView();
	SelectView minBoostFreq = new SelectView();
	SeekBarView delay = new SeekBarView();
	SeekBarView maxCpus = new SeekBarView();
	SeekBarView minCpus = new SeekBarView();
	SwitchView scroffSingleCore = new SwitchView();

        if (AutoSMP.hasAutoSmpEnable()) {
            enable.setSummary(getString(R.string.autosmp_summary));
            enable.setChecked(AutoSMP.isAutoSmpEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
		AutoSMP.enableAutoSmp(isChecked, getActivity());
		getHandler().postDelayed(() -> {
		// Show or hide other options on the basis of the main driver status
		if (AutoSMP.isAutoSmpEnabled()) {
		    if (AutoSMP.hasHotplugSuspend()) {
			hotplugSuspend.setChecked(AutoSMP.isHotplugSuspendEnabled());
			autoSMPCard.addItem(hotplugSuspend);
		    }
		    if (AutoSMP.hasAutoSmpCpufreqDown()) {
			cpuFreqDown.setProgress(AutoSMP.getAutoSmpCpufreqDown());
			autoSMPCard.addItem(cpuFreqDown);
		    }
		    if (AutoSMP.hasAutoSmpCpufreqUp()) {
			cpuFreqUp.setProgress(AutoSMP.getAutoSmpCpufreqUp());
			autoSMPCard.addItem(cpuFreqUp);
		    }
		    if (AutoSMP.hasAutoSmpCycleDown()) {
			cycleDown.setProgress(AutoSMP.getAutoSmpCycleDown());
			autoSMPCard.addItem(cycleDown);
		    }
		    if (AutoSMP.hasAutoSmpCycleUp()) {
			cycleUp.setProgress(AutoSMP.getAutoSmpCycleUp());
			autoSMPCard.addItem(cycleUp);
		    }
		    if (AutoSMP.hasMinBoostFreq()) {
			minBoostFreq.setItem((AutoSMP.getMinBoostFreq() / 1000) + getString(R.string.mhz));
			autoSMPCard.addItem(minBoostFreq);
		    }
		    if (AutoSMP.hasAutoSmpDelay()) {
			delay.setProgress(AutoSMP.getAutoSmpDelay());
			autoSMPCard.addItem(delay);
		    }
		    if (AutoSMP.hasAutoSmpMaxCpus()) {
			maxCpus.setProgress(AutoSMP.getAutoSmpMaxCpus() - 1);
			autoSMPCard.addItem(maxCpus);
		    }
		    if (AutoSMP.hasAutoSmpMinCpus()) {
			minCpus.setProgress(AutoSMP.getAutoSmpMinCpus() - 1);
			autoSMPCard.addItem(minCpus);
		    }
		    if (AutoSMP.hasAutoSmpScroffSingleCore()) {
			scroffSingleCore.setChecked(AutoSMP.isAutoSmpScroffSingleCoreEnabled());
			autoSMPCard.addItem(scroffSingleCore);
		    }
		} else {
		   autoSMPCard.removeItem(hotplugSuspend);
		   autoSMPCard.removeItem(cpuFreqDown);
		   autoSMPCard.removeItem(cpuFreqUp);
		   autoSMPCard.removeItem(cycleDown);
		   autoSMPCard.removeItem(cycleUp);
		   autoSMPCard.removeItem(minBoostFreq);
		   autoSMPCard.removeItem(delay);
		   autoSMPCard.removeItem(maxCpus);
		   autoSMPCard.removeItem(minCpus);
		   autoSMPCard.removeItem(scroffSingleCore);
	    	}
	    }, 100);
	    });

	    autoSMPCard.addItem(enable);
            mEnableViews.add(enable);
        }

        if (AutoSMP.hasHotplugSuspend()) {
            hotplugSuspend.setSummary(getString(R.string.hotplug_suspend));
            hotplugSuspend.setChecked(AutoSMP.isHotplugSuspendEnabled());
            hotplugSuspend.addOnSwitchListener((switchView, isChecked)
		-> AutoSMP.enableHotplugSuspend(isChecked, getActivity()));

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(hotplugSuspend);
	    } else {
            	autoSMPCard.removeItem(hotplugSuspend);
	    }
	}

        if (AutoSMP.hasAutoSmpCpufreqDown()) {
            cpuFreqDown.setTitle(getString(R.string.downrate_limits));
            cpuFreqDown.setUnit("%");
            cpuFreqDown.setProgress(AutoSMP.getAutoSmpCpufreqDown());
            cpuFreqDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpCpufreqDown(position, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(cpuFreqDown);
	    } else {
            	autoSMPCard.removeItem(cpuFreqDown);
	    }
        }

        if (AutoSMP.hasAutoSmpCpufreqUp()) {
            cpuFreqUp.setTitle(getString(R.string.uprate_limits));
            cpuFreqUp.setUnit("%");
            cpuFreqUp.setProgress(AutoSMP.getAutoSmpCpufreqUp());
            cpuFreqUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpCpufreqUp(position, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(cpuFreqUp);
	    } else {
            	autoSMPCard.removeItem(cpuFreqUp);
	    }
        }

        if (AutoSMP.hasAutoSmpCycleDown()) {
            cycleDown.setTitle(getString(R.string.cycle_down));
            cycleDown.setSummary(getString(R.string.cycle_down_summary));
            cycleDown.setMax(mCPUFreq.getCpuCount());
            cycleDown.setProgress(AutoSMP.getAutoSmpCycleDown());
            cycleDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpCycleDown(position, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(cycleDown);
	    } else {
            	autoSMPCard.removeItem(cycleDown);
	    }
        }

        if (AutoSMP.hasAutoSmpCycleUp()) {
            cycleUp.setTitle(getString(R.string.cycle_up));
            cycleUp.setSummary(getString(R.string.cycle_up_summary));
            cycleUp.setMax(mCPUFreq.getCpuCount());
            cycleUp.setProgress(AutoSMP.getAutoSmpCycleUp());
            cycleUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpCycleUp(position, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(cycleUp);
	    } else {
            	autoSMPCard.removeItem(cycleUp);
	    }
        }

        if (AutoSMP.hasMinBoostFreq()) {
            minBoostFreq.setTitle(getString(R.string.min_boost_freq));
            minBoostFreq.setSummary(("Select ") + getString(R.string.min_boost_freq));
            minBoostFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            minBoostFreq.setItem((AutoSMP.getMinBoostFreq() / 1000) + getString(R.string.mhz));
	    minBoostFreq.setOnItemSelected((selectView, position, item)
		-> AutoSMP.setMinBoostFreq(
		mCPUFreq.getFreqs().get(position), getActivity()));

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(minBoostFreq);
	    } else {
            	autoSMPCard.removeItem(minBoostFreq);
	    }
	}

        if (AutoSMP.hasAutoSmpDelay()) {
            delay.setTitle(getString(R.string.delay));
            delay.setSummary(getString(R.string.delay_summary));
            delay.setUnit(getString(R.string.ms));
            delay.setMax(500);
            delay.setProgress(AutoSMP.getAutoSmpDelay());
            delay.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpDelay(position, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(delay);
	    } else {
            	autoSMPCard.removeItem(delay);
	    }
        }

        if (AutoSMP.hasAutoSmpMaxCpus()) {
            maxCpus.setTitle(getString(R.string.max_cpu_online));
            maxCpus.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpus.setMax(mCPUFreq.getCpuCount());
            maxCpus.setMin(1);
            maxCpus.setProgress(AutoSMP.getAutoSmpMaxCpus() - 1);
            maxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpMaxCpus(position + 1, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(maxCpus);
	    } else {
            	autoSMPCard.removeItem(maxCpus);
	    }
        }

        if (AutoSMP.hasAutoSmpMinCpus()) {
            minCpus.setTitle(getString(R.string.min_cpu_online));
            minCpus.setSummary(getString(R.string.min_cpu_online_summary));
            minCpus.setMax(mCPUFreq.getCpuCount());
            minCpus.setMin(1);
            minCpus.setProgress(AutoSMP.getAutoSmpMinCpus() - 1);
            minCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSMP.setAutoSmpMinCpus(position + 1, getActivity());
                }
            });

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(minCpus);
	    } else {
            	autoSMPCard.removeItem(minCpus);
	    }
        }

        if (AutoSMP.hasAutoSmpScroffSingleCore()) {
            scroffSingleCore.setSummary(getString(R.string.screen_off_single_cpu));
            scroffSingleCore.setChecked(AutoSMP.isAutoSmpScroffSingleCoreEnabled());
            scroffSingleCore.addOnSwitchListener((switchView, isChecked)
		-> AutoSMP.enableAutoSmpScroffSingleCoreActive(isChecked, getActivity()));

	    if (!AutoSMP.hasAutoSmpEnable() || AutoSMP.hasAutoSmpEnable() && AutoSMP.isAutoSmpEnabled()) {
            	autoSMPCard.addItem(scroffSingleCore);
	    } else {
            	autoSMPCard.removeItem(scroffSingleCore);
	    }
        }

        if (autoSMPCard.size() > 0) {
            items.add(autoSMPCard);
        }
    }

    private void coreCtlInit(List<RecyclerViewItem> items) {
	CardView coreCtl = new CardView(getActivity());
	if (mCoreCtl.hasEnable()) {
            coreCtl.setTitle(getString(R.string.hcube));
	} else {
            coreCtl.setTitle(getString(R.string.core_control));
	}

	SwitchView enable = new SwitchView();
	SeekBarView minCpus = new SeekBarView();
	SeekBarView busyDownThreshold = new SeekBarView();
	SeekBarView busyUpThreshold = new SeekBarView();
	SeekBarView taskThreshold = new SeekBarView();
	SeekBarView offlineDelayMs = new SeekBarView();
	SeekBarView onlineDelayMs = new SeekBarView();

	if (mCoreCtl.hasEnable()) {
            enable.setSummary(getString(R.string.hcube_summary));
            enable.setChecked(mCoreCtl.isEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
		mCoreCtl.enable(isChecked, getActivity());
		getHandler().postDelayed(() -> {
		// Show or hide other options on the basis of the main driver status
		if (mCoreCtl.isEnabled()) {
		    if (mCoreCtl.hasMinCpus()) {
			minCpus.setProgress(mCoreCtl.getMinCpus(mCPUFreq.getBigCpu()));
			coreCtl.addItem(minCpus);
		    }
		    if (mCoreCtl.hasBusyDownThreshold()) {
			busyDownThreshold.setProgress(mCoreCtl.getBusyDownThreshold());
			coreCtl.addItem(busyDownThreshold);
		    }
		    if (mCoreCtl.hasBusyUpThreshold()) {
			busyUpThreshold.setProgress(mCoreCtl.getBusyUpThreshold());
			coreCtl.addItem(busyUpThreshold);
		    }
		    if (mCoreCtl.hasTaskThreshold()) {
			taskThreshold.setProgress(mCoreCtl.getTaskThreshold());
			coreCtl.addItem(taskThreshold);
		    }
		    if (mCoreCtl.hasOfflineDelayMs()) {
			offlineDelayMs.setProgress(mCoreCtl.getOfflineDelayMs() / 100);
			coreCtl.addItem(offlineDelayMs);
		    }
		    if (mCoreCtl.hasOnlineDelayMs()) {
			onlineDelayMs.setProgress(mCoreCtl.getOnlineDelayMs() / 100);
			coreCtl.addItem(onlineDelayMs);
		    }
		} else {
		   coreCtl.removeItem(minCpus);
		   coreCtl.removeItem(busyDownThreshold);
		   coreCtl.removeItem(busyUpThreshold);
		   coreCtl.removeItem(taskThreshold);
		   coreCtl.removeItem(offlineDelayMs);
		   coreCtl.removeItem(onlineDelayMs);

	    	}
	   }, 100);

	   });

            coreCtl.addItem(enable);
            mEnableViews.add(enable);
	}

	if (mCoreCtl.hasMinCpus()) {
            minCpus.setTitle(getString(R.string.min_cpus_big));
            minCpus.setSummary(getString(R.string.min_cpus_big_summary));
            minCpus.setMax(mCPUFreq.getBigCpuRange().size());
            minCpus.setProgress(mCoreCtl.getMinCpus(mCPUFreq.getBigCpu()));
            minCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mCoreCtl.setMinCpus(position, mCPUFreq.getBigCpu(), getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            if (!mCoreCtl.hasEnable() || mCoreCtl.hasEnable() && mCoreCtl.isEnabled()) {
            	coreCtl.addItem(minCpus);
	    } else {
            	coreCtl.removeItem(minCpus);
	    }
	}

	if (mCoreCtl.hasBusyDownThreshold()) {
            busyDownThreshold.setTitle(getString(R.string.busy_down_threshold));
            busyDownThreshold.setSummary(getString(R.string.busy_down_threshold_summary));
            busyDownThreshold.setProgress(mCoreCtl.getBusyDownThreshold());
            busyDownThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mCoreCtl.setBusyDownThreshold(position, getActivity());
		}
            });

	    if (!mCoreCtl.hasEnable() || mCoreCtl.hasEnable() && mCoreCtl.isEnabled()) {
            	coreCtl.addItem(busyDownThreshold);
	    } else {
            	coreCtl.removeItem(busyDownThreshold);
	    }
	}

	if (mCoreCtl.hasBusyUpThreshold()) {
            busyUpThreshold.setTitle(getString(R.string.busy_up_threshold));
            busyUpThreshold.setSummary(getString(R.string.busy_up_threshold_summary));
            busyUpThreshold.setProgress(mCoreCtl.getBusyUpThreshold());
            busyUpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mCoreCtl.setBusyUpThreshold(position, getActivity());
		}
            });

	    if (!mCoreCtl.hasEnable() || mCoreCtl.hasEnable() && mCoreCtl.isEnabled()) {
            	coreCtl.addItem(busyUpThreshold);
	    } else {
            	coreCtl.removeItem(busyUpThreshold);
	    }
	}

	if (mCoreCtl.hasTaskThreshold()) {
            taskThreshold.setTitle(getString(R.string.task_threshold));
            taskThreshold.setSummary(getString(R.string.task_threshold_summary));
            taskThreshold.setProgress(mCoreCtl.getTaskThreshold());
            taskThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mCoreCtl.setTaskThreshold(position, getActivity());
		}
            });

	    if (!mCoreCtl.hasEnable() || mCoreCtl.hasEnable() && mCoreCtl.isEnabled()) {
            	coreCtl.addItem(taskThreshold);
	    } else {
            	coreCtl.removeItem(taskThreshold);
	    }
	}

	if (mCoreCtl.hasOfflineDelayMs()) {
            offlineDelayMs.setTitle(getString(R.string.offline_delay));
            offlineDelayMs.setSummary(getString(R.string.offline_delay_summary));
            offlineDelayMs.setUnit(getString(R.string.ms));
            offlineDelayMs.setMax(5000);
            offlineDelayMs.setOffset(100);
            offlineDelayMs.setProgress(mCoreCtl.getOfflineDelayMs() / 100);
            offlineDelayMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mCoreCtl.setOfflineDelayMs(position * 100, getActivity());
		}
            });

	    if (!mCoreCtl.hasEnable() || mCoreCtl.hasEnable() && mCoreCtl.isEnabled()) {
            	coreCtl.addItem(offlineDelayMs);
	    } else {
            	coreCtl.removeItem(offlineDelayMs);
	    }
	}

	if (mCoreCtl.hasOnlineDelayMs()) {
            onlineDelayMs.setTitle(getString(R.string.online_delay));
            onlineDelayMs.setSummary(getString(R.string.online_delay_summary));
            onlineDelayMs.setUnit(getString(R.string.ms));
            onlineDelayMs.setMax(5000);
            onlineDelayMs.setOffset(100);
            onlineDelayMs.setProgress(mCoreCtl.getOnlineDelayMs() / 100);
            onlineDelayMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}

		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mCoreCtl.setOnlineDelayMs(position * 100, getActivity());
		}
            });

	    if (!mCoreCtl.hasEnable() || mCoreCtl.hasEnable() && mCoreCtl.isEnabled()) {
            	coreCtl.addItem(onlineDelayMs);
	    } else {
            	coreCtl.removeItem(onlineDelayMs);
	    }
	}

	if (coreCtl.size() > 0) {
            items.add(coreCtl);
	}
    }

}
