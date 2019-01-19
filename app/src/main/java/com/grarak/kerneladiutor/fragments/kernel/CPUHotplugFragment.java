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
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import com.smartpack.kernelmanager.utils.MSMSleeper;

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
    private CoreCtl mCoreCtl;

    private List<SwitchView> mEnableViews = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mIntelliPlug = IntelliPlug.getInstance();
        mMSMHotplug = MSMHotplug.getInstance();
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
	enable.setSummary(getString(R.string.intelliplug_summary));
	enable.setChecked(mIntelliPlug.isIntelliPlugEnabled());
	enable.addOnSwitchListener((switchView, isChecked)
            -> mIntelliPlug.enableIntelliPlug(isChecked, getActivity()));

	intelliplug.addItem(enable);
	mEnableViews.add(enable);

	if (mIntelliPlug.hasIntelliPlugProfile()) {
            SelectView profile = new SelectView();
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.cpu_hotplug_profile_summary));
            profile.setItems(mIntelliPlug.getIntelliPlugProfileMenu(getActivity()));
            profile.setItem(mIntelliPlug.getIntelliPlugProfile());
            profile.setOnItemSelected((selectView, position, item)
		-> mIntelliPlug.setIntelliPlugProfile(position, getActivity()));

            intelliplug.addItem(profile);
	}

	if (mIntelliPlug.hasIntelliPlugEco()) {
            SwitchView eco = new SwitchView();
            eco.setTitle(getString(R.string.eco_mode));
            eco.setSummary(getString(R.string.eco_mode_summary));
            eco.setChecked(mIntelliPlug.isIntelliPlugEcoEnabled());
            eco.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugEco(isChecked, getActivity()));

            intelliplug.addItem(eco);
	}

	if (mIntelliPlug.hasIntelliPlugTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(mIntelliPlug.isIntelliPlugTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugTouchBoost(isChecked, getActivity()));

            intelliplug.addItem(touchBoost);
	}

	if (mIntelliPlug.hasIntelliPlugHysteresis()) {
            SeekBarView hysteresis = new SeekBarView();
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

            intelliplug.addItem(hysteresis);
	}

	if (mIntelliPlug.hasIntelliPlugThresold()) {
            SeekBarView threshold = new SeekBarView();
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

            intelliplug.addItem(threshold);
	}

	if (mIntelliPlug.hasIntelliPlugScreenOffMax() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SelectView maxScreenOffFreq = new SelectView();
            maxScreenOffFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxScreenOffFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxScreenOffFreq.setItems(list);
            maxScreenOffFreq.setItem(mIntelliPlug.getIntelliPlugScreenOffMax());
            maxScreenOffFreq.setOnItemSelected((selectView, position, item)
		-> mIntelliPlug.setIntelliPlugScreenOffMax(position, getActivity()));

            intelliplug.addItem(maxScreenOffFreq);
	}

	if (mIntelliPlug.hasIntelliPlugDebug()) {
            SwitchView debug = new SwitchView();
            debug.setTitle(getString(R.string.debug_mask));
            debug.setSummary(getString(R.string.debug_mask_summary));
            debug.setChecked(mIntelliPlug.isIntelliPlugDebugEnabled());
            debug.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugDebug(isChecked, getActivity()));

            intelliplug.addItem(debug);
	}

	if (mIntelliPlug.hasIntelliPlugSuspend()) {
            SwitchView suspend = new SwitchView();
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(mIntelliPlug.isIntelliPlugSuspendEnabled());
            suspend.addOnSwitchListener((switchView, isChecked)
		-> mIntelliPlug.enableIntelliPlugSuspend(isChecked, getActivity()));

            intelliplug.addItem(suspend);
	}

	if (mIntelliPlug.hasIntelliPlugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
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

            intelliplug.addItem(cpusBoosted);
	}

	if (mIntelliPlug.hasIntelliPlugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
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

            intelliplug.addItem(minCpusOnline);
	}

	if (mIntelliPlug.hasIntelliPlugMaxCpusOnline()) {
            SeekBarView maxCpusOnline = new SeekBarView();
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

            intelliplug.addItem(maxCpusOnline);
	}

	if (mIntelliPlug.hasIntelliPlugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
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

            intelliplug.addItem(maxCpusOnlineSusp);
	}

	if (mIntelliPlug.hasIntelliPlugSuspendDeferTime()) {
            SeekBarView suspendDeferTime = new SeekBarView();
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

            intelliplug.addItem(suspendDeferTime);
	}

	if (mIntelliPlug.hasIntelliPlugDeferSampling()) {
            SeekBarView deferSampling = new SeekBarView();
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

            intelliplug.addItem(deferSampling);        
	}

	if (mIntelliPlug.hasIntelliPlugBoostLockDuration()) {
            SeekBarView boostLockDuration = new SeekBarView();
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

            intelliplug.addItem(boostLockDuration);
	}

	if (mIntelliPlug.hasIntelliPlugDownLockDuration()) {
            SeekBarView downLockDuration = new SeekBarView();
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

            intelliplug.addItem(downLockDuration);
	}

	if (mIntelliPlug.hasIntelliPlugFShift()) {
            SeekBarView fShift = new SeekBarView();
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

            intelliplug.addItem(fShift);
        }
	if (intelliplug.size() > 0) {
            items.add(intelliplug);
	}
    }

    private void lazyPlugInit(List<RecyclerViewItem> items) {
	CardView lazyplug = new CardView(getActivity());
	lazyplug.setTitle(getString(R.string.lazyplug));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.lazyplug_summary));
	enable.setChecked(LazyPlug.isEnabled());
	enable.addOnSwitchListener((switchView, isChecked)
		-> LazyPlug.enable(isChecked, getActivity()));

	lazyplug.addItem(enable);
	mEnableViews.add(enable);

	if (LazyPlug.hasProfile()) {
            SelectView profile = new SelectView();
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.cpu_hotplug_profile_summary));
            profile.setItems(LazyPlug.getProfileMenu(getActivity()));
            profile.setItem(LazyPlug.getProfile());
            profile.setOnItemSelected((selectView, position, item)
		-> LazyPlug.setProfile(position, getActivity()));

            lazyplug.addItem(profile);
	}

	if (LazyPlug.hasTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(LazyPlug.isTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
		-> LazyPlug.enableTouchBoost(isChecked, getActivity()));

            lazyplug.addItem(touchBoost);
	}

	if (LazyPlug.hasHysteresis()) {
            SeekBarView hysteresis = new SeekBarView();
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

            lazyplug.addItem(hysteresis);
	}

	if (LazyPlug.hasThreshold()) {
            SeekBarView threshold = new SeekBarView();
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

            lazyplug.addItem(threshold);
	}

	if (LazyPlug.hasPossibleCores()) {
            SeekBarView possibleCores = new SeekBarView();
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

            lazyplug.addItem(possibleCores);
        }

        if (lazyplug.size() > 0) {
            items.add(lazyplug);
        }
    }

    private void MSMSleeperInit(List<RecyclerViewItem> items) {
	CardView msmsleeper = new CardView(getActivity());
	msmsleeper.setTitle(getString(R.string.msm_sleeper));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.msm_sleeper_summary));
	enable.setChecked(MSMSleeper.isEnabled());
	enable.addOnSwitchListener((switchView, isChecked)
		-> MSMSleeper.enable(isChecked, getActivity()));

	msmsleeper.addItem(enable);
	mEnableViews.add(enable);

	if (MSMSleeper.hasMaxOnline()) {
            SeekBarView MaxOnline = new SeekBarView();
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

            msmsleeper.addItem(MaxOnline);
	}
	if (MSMSleeper.hasUpCountMax()) {
            SeekBarView UpCountMax = new SeekBarView();
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

            msmsleeper.addItem(UpCountMax);
	}
	if (MSMSleeper.hasDownCountMax()) {
            SeekBarView DownCountMax = new SeekBarView();
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

            msmsleeper.addItem(DownCountMax);
	}

	if (MSMSleeper.hasSusMaxOnline()) {
            SeekBarView SusMaxOnline = new SeekBarView();
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

            msmsleeper.addItem(SusMaxOnline);
	}

	if (MSMSleeper.hasUpThreshold()) {
            SeekBarView UpThreshold = new SeekBarView();
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

            msmsleeper.addItem(UpThreshold);
	}

        if (msmsleeper.size() > 0) {
            items.add(msmsleeper);
        }
    }

    private void bluPlugInit(List<RecyclerViewItem> items) {
	CardView bluplug = new CardView(getActivity());
	bluplug.setTitle(getString(R.string.blu_plug));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.blu_plug_summary));
	enable.setChecked(BluPlug.isBluPlugEnabled());
	enable.addOnSwitchListener((switchView, isChecked)
		-> BluPlug.enableBluPlug(isChecked, getActivity()));

	bluplug.addItem(enable);
	mEnableViews.add(enable);

	if (BluPlug.hasBluPlugPowersaverMode()) {
            SwitchView powersaverMode = new SwitchView();
            powersaverMode.setTitle(getString(R.string.powersaver_mode));
            powersaverMode.setSummary(getString(R.string.powersaver_mode_summary));
            powersaverMode.setChecked(BluPlug.isBluPlugPowersaverModeEnabled());
            powersaverMode.addOnSwitchListener((switchView, isChecked)
		-> BluPlug.enableBluPlugPowersaverMode(isChecked, getActivity()));

            bluplug.addItem(powersaverMode);
	}

	if (BluPlug.hasBluPlugMinOnline()) {
            SeekBarView minOnline = new SeekBarView();
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

            bluplug.addItem(minOnline);
	}

	if (BluPlug.hasBluPlugMaxOnline()) {
            SeekBarView maxOnline = new SeekBarView();
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

            bluplug.addItem(maxOnline);
	}

	if (BluPlug.hasBluPlugMaxCoresScreenOff()) {
            SeekBarView maxCoresScreenOff = new SeekBarView();
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

            bluplug.addItem(maxCoresScreenOff);
	}

	if (BluPlug.hasBluPlugMaxFreqScreenOff() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SeekBarView maxFreqScreenOff = new SeekBarView();
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

            bluplug.addItem(maxFreqScreenOff);
	}

	if (BluPlug.hasBluPlugUpThreshold()) {
            SeekBarView upThreshold = new SeekBarView();
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

            bluplug.addItem(upThreshold);
	}

	if (BluPlug.hasBluPlugUpTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
		list.add(String.valueOf(i * 0.5f));
            }

            SeekBarView upTimerCnt = new SeekBarView();
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

            bluplug.addItem(upTimerCnt);
	}

	if (BluPlug.hasBluPlugDownTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
		list.add(String.valueOf(i * 0.5f));
            }

            SeekBarView downTimerCnt = new SeekBarView();
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

            bluplug.addItem(downTimerCnt);
	}

        if (bluplug.size() > 0) {
            items.add(bluplug);
        }
    }

    private void makoHotplugInit(List<RecyclerViewItem> items) {
	CardView makoHotplug = new CardView(getActivity());
	makoHotplug.setTitle(getString(R.string.mako_hotplug));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.mako_hotplug_summary));
	enable.setChecked(MakoHotplug.isMakoHotplugEnabled());
	enable.addOnSwitchListener((switchView, isChecked)
		-> MakoHotplug.enableMakoHotplug(isChecked, getActivity()));

	makoHotplug.addItem(enable);
	mEnableViews.add(enable);
        
	if (MakoHotplug.hasMakoHotplugCoresOnTouch()) {
            SeekBarView coresOnTouch = new SeekBarView();
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

            makoHotplug.addItem(coresOnTouch);
	}

	if (MakoHotplug.hasMakoHotplugCpuFreqUnplugLimit() && mCPUFreq.getFreqs() != null) {
            SelectView cpufreqUnplugLimit = new SelectView();
            cpufreqUnplugLimit.setSummary(getString(R.string.cpu_freq_unplug_limit));
            cpufreqUnplugLimit.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpufreqUnplugLimit.setItem((MakoHotplug.getMakoHotplugCpuFreqUnplugLimit() / 1000)
		+ getString(R.string.mhz));
            cpufreqUnplugLimit.setOnItemSelected((selectView, position, item)
		-> MakoHotplug.setMakoHotplugCpuFreqUnplugLimit(
		mCPUFreq.getFreqs().get(position), getActivity()));

            makoHotplug.addItem(cpufreqUnplugLimit);
	}

	if (MakoHotplug.hasMakoHotplugFirstLevel()) {
            SeekBarView firstLevel = new SeekBarView();
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

            makoHotplug.addItem(firstLevel);
	}

	if (MakoHotplug.hasMakoHotplugHighLoadCounter()) {
            SeekBarView highLoadCounter = new SeekBarView();
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

            makoHotplug.addItem(highLoadCounter);
	}

	if (MakoHotplug.hasMakoHotplugLoadThreshold()) {
            SeekBarView loadThreshold = new SeekBarView();
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

            makoHotplug.addItem(loadThreshold);
	}

	if (MakoHotplug.hasMakoHotplugMaxLoadCounter()) {
            SeekBarView maxLoadCounter = new SeekBarView();
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

            makoHotplug.addItem(maxLoadCounter);
	}

	if (MakoHotplug.hasMakoHotplugMinTimeCpuOnline()) {
            SeekBarView minTimeCpuOnline = new SeekBarView();
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

            makoHotplug.addItem(minTimeCpuOnline);
	}

	if (MakoHotplug.hasMakoHotplugMinCoresOnline()) {
            SeekBarView minCoresOnline = new SeekBarView();
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

            makoHotplug.addItem(minCoresOnline);
	}

	if (MakoHotplug.hasMakoHotplugTimer()) {
            SeekBarView timer = new SeekBarView();
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

            makoHotplug.addItem(timer);
	}

	if (MakoHotplug.hasMakoHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
            SelectView suspendFreq = new SelectView();
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((MakoHotplug.getMakoHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected((selectView, position, item)
		-> MakoHotplug.setMakoHotplugSuspendFreq(mCPUFreq.getFreqs().get(position), getActivity()));

            makoHotplug.addItem(suspendFreq);
        }

        if (makoHotplug.size() > 0) {
            items.add(makoHotplug);
        }
    }

    private void msmHotplugInit(List<RecyclerViewItem> items) {
	CardView msmHotplug = new CardView(getActivity());
	msmHotplug.setTitle(getString(R.string.msm_hotplug));

        if (mMSMHotplug.hasMsmHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.msm_hotplug_summary));
            enable.setChecked(mMSMHotplug.isMsmHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplug(isChecked, getActivity()));

            msmHotplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (mMSMHotplug.hasMsmHotplugDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mMSMHotplug.isMsmHotplugDebugMaskEnabled());
            debugMask.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplugDebugMask(isChecked, getActivity()));

            msmHotplug.addItem(debugMask);
        }

        if (mMSMHotplug.hasMsmHotplugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
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

            msmHotplug.addItem(minCpusOnline);
        }

        if (mMSMHotplug.hasMsmHotplugMaxCpusOnline()) {
            SeekBarView maxCpusOnline = new SeekBarView();
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

            msmHotplug.addItem(maxCpusOnline);
        }

        if (mMSMHotplug.hasMsmHotplugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
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

            msmHotplug.addItem(cpusBoosted);
        }

        if (mMSMHotplug.hasMsmHotplugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
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

            msmHotplug.addItem(maxCpusOnlineSusp);
        }

        if (mMSMHotplug.hasMsmHotplugBoostLockDuration()) {
            SeekBarView boostLockDuration = new SeekBarView();
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

            msmHotplug.addItem(boostLockDuration);
        }

        if (mMSMHotplug.hasMsmHotplugDownLockDuration()) {
            SeekBarView downLockDuration = new SeekBarView();
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

            msmHotplug.addItem(downLockDuration);
        }

        if (mMSMHotplug.hasMsmHotplugHistorySize()) {
            SeekBarView historySize = new SeekBarView();
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

            msmHotplug.addItem(historySize);
        }

        if (mMSMHotplug.hasMsmHotplugUpdateRate()) {
            SeekBarView updateRate = new SeekBarView();
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

            msmHotplug.addItem(updateRate);
        }

        if (mMSMHotplug.hasMsmHotplugFastLaneLoad()) {
            SeekBarView fastLaneLoad = new SeekBarView();
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

            msmHotplug.addItem(fastLaneLoad);
        }

        if (mMSMHotplug.hasMsmHotplugFastLaneMinFreq() && mCPUFreq.getFreqs() != null) {
            SelectView fastLaneMinFreq = new SelectView();
            fastLaneMinFreq.setTitle(getString(R.string.fast_lane_min_freq));
            fastLaneMinFreq.setSummary(getString(R.string.fast_lane_min_freq_summary));
            fastLaneMinFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            fastLaneMinFreq.setItem((mMSMHotplug.getMsmHotplugFastLaneMinFreq() / 1000) + getString(R.string.mhz));
            fastLaneMinFreq.setOnItemSelected((selectView, position, item)
                    -> mMSMHotplug.setMsmHotplugFastLaneMinFreq(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            msmHotplug.addItem(fastLaneMinFreq);
        }

        if (mMSMHotplug.hasMsmHotplugOfflineLoad()) {
            SeekBarView offlineLoad = new SeekBarView();
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

            msmHotplug.addItem(offlineLoad);
        }

        if (mMSMHotplug.hasMsmHotplugIoIsBusy()) {
            SwitchView ioIsBusy = new SwitchView();
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(mMSMHotplug.isMsmHotplugIoIsBusyEnabled());
            ioIsBusy.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplugIoIsBusy(isChecked, getActivity()));

            msmHotplug.addItem(ioIsBusy);
        }

        if (mMSMHotplug.hasMsmHotplugSuspendMaxCpus()) {
            SeekBarView suspendMaxCpus = new SeekBarView();
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

            msmHotplug.addItem(suspendMaxCpus);
        }

        if (mMSMHotplug.hasMsmHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
            SelectView suspendFreq = new SelectView();
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((mMSMHotplug.getMsmHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected((selectView, position, item)
                    -> mMSMHotplug.setMsmHotplugSuspendFreq(mCPUFreq.getFreqs().get(position), getActivity()));

            msmHotplug.addItem(suspendFreq);
        }

        if (mMSMHotplug.hasMsmHotplugSuspendDeferTime()) {
            SeekBarView suspendDeferTime = new SeekBarView();
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

            msmHotplug.addItem(suspendDeferTime);
        }

        if (msmHotplug.size() > 0) {
            items.add(msmHotplug);
        }
    }

    private void alucardHotplugInit(List<RecyclerViewItem> items) {
	CardView alucardHotplug = new CardView(getActivity());
	alucardHotplug.setTitle(getString(R.string.alucard_hotplug));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.alucard_hotplug_summary));
	enable.setChecked(AlucardHotplug.isAlucardHotplugEnable());
	enable.addOnSwitchListener((switchView, isChecked)
		-> AlucardHotplug.enableAlucardHotplug(isChecked, getActivity()));

	alucardHotplug.addItem(enable);
	mEnableViews.add(enable);

	if (AlucardHotplug.hasAlucardHotplugHpIoIsBusy()) {
            SwitchView ioIsBusy = new SwitchView();
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(AlucardHotplug.isAlucardHotplugHpIoIsBusyEnable());
            ioIsBusy.addOnSwitchListener((switchView, isChecked)
		-> AlucardHotplug.enableAlucardHotplugHpIoIsBusy(isChecked, getActivity()));

            alucardHotplug.addItem(ioIsBusy);
	}

	if (AlucardHotplug.hasAlucardHotplugSamplingRate()) {
            GenericSelectView samplingRate = new GenericSelectView();
            samplingRate.setTitle(getString(R.string.sampling_rate));
            samplingRate.setSummary(("Set ") + getString(R.string.sampling_rate));
            samplingRate.setValue(AlucardHotplug.getAlucardHotplugSamplingRate());
            samplingRate.setInputType(InputType.TYPE_CLASS_NUMBER);
            samplingRate.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    AlucardHotplug.setAlucardHotplugSamplingRate(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });
            alucardHotplug.addItem(samplingRate);
	}

	if (AlucardHotplug.hasAlucardHotplugSuspend()) {
            SwitchView suspend = new SwitchView();
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(AlucardHotplug.isAlucardHotplugSuspendEnabled());
            suspend.addOnSwitchListener((switchView, isChecked)
		-> AlucardHotplug.enableAlucardHotplugSuspend(isChecked, getActivity()));

            alucardHotplug.addItem(suspend);
	}

	if (AlucardHotplug.hasAlucardHotplugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
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

            alucardHotplug.addItem(minCpusOnline);
	}

	if (AlucardHotplug.hasAlucardHotplugMaxCoresLimit()) {
            SeekBarView maxCoresLimit = new SeekBarView();
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

            alucardHotplug.addItem(maxCoresLimit);
	}

	if (AlucardHotplug.hasAlucardHotplugMaxCoresLimitSleep()) {
            SeekBarView maxCoresLimitSleep = new SeekBarView();
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

            alucardHotplug.addItem(maxCoresLimitSleep);
	}

	if (AlucardHotplug.hasAlucardHotplugCpuDownRate()) {
            SeekBarView cpuDownRate = new SeekBarView();
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

            alucardHotplug.addItem(cpuDownRate);
	}

	if (AlucardHotplug.hasAlucardHotplugCpuUpRate()) {
            SeekBarView cpuUpRate = new SeekBarView();
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

            alucardHotplug.addItem(cpuUpRate);
        }

	if (!(Prefs.getBoolean("advanced_tunables", false, getActivity())))
            Prefs.saveBoolean("advanced_tunables", false, getActivity());

	final SwitchView tunables = new SwitchView();
	tunables.setSummary(getString(R.string.adv_sett));
	tunables.setChecked(Prefs.getBoolean("advanced_tunables", false, getActivity()));

	alucardHotplug.addItem(tunables);

	for (int i = 0; i < AlucardHotplug.size(); i++) {
            if (AlucardHotplug.exists(i)) {

                final GenericSelectView advancedtunables = new GenericSelectView();
                advancedtunables.setSummary(AlucardHotplug.getName(i));
                advancedtunables.setValue(AlucardHotplug.getValue(i));
                advancedtunables.setValueRaw(advancedtunables.getValue());
                advancedtunables.setInputType(InputType.TYPE_CLASS_NUMBER);
                final int position = i;
                advancedtunables.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                    @Override
                    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                        AlucardHotplug.setValue(value, position, getActivity());
                        genericSelectView.setValue(value);
                    }
                });
                class tunablesManager {
                public void showadvancedtunables (boolean enable) {
                if (enable == true) {
                    alucardHotplug.addItem(advancedtunables);
                } else {
                    alucardHotplug.removeItem(advancedtunables);
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
            });
            }
        }

        if (alucardHotplug.size() > 0) {
            items.add(alucardHotplug);
        }
    }

    private void coreCtlInit(List<RecyclerViewItem> items) {
	CardView coreCtl = new CardView(getActivity());
	if (mCoreCtl.hasEnable()) {
            coreCtl.setTitle(getString(R.string.hcube));
	} else {
            coreCtl.setTitle(getString(R.string.core_control));
	}

	if (mCoreCtl.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.hcube_summary));
            enable.setChecked(mCoreCtl.isEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
		-> mCoreCtl.enable(isChecked, getActivity()));

            coreCtl.addItem(enable);
            mEnableViews.add(enable);
	}

	if (mCoreCtl.hasMinCpus()) {
            SeekBarView minCpus = new SeekBarView();
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
            coreCtl.addItem(minCpus);
	}

	if (mCoreCtl.hasBusyDownThreshold()) {
            SeekBarView busyDownThreshold = new SeekBarView();
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
            coreCtl.addItem(busyDownThreshold);
	}

	if (mCoreCtl.hasBusyUpThreshold()) {
            SeekBarView busyUpThreshold = new SeekBarView();
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
            coreCtl.addItem(busyUpThreshold);
	}

	if (mCoreCtl.hasTaskThreshold()) {
            SeekBarView taskThreshold = new SeekBarView();
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
            coreCtl.addItem(taskThreshold);
	}

	if (mCoreCtl.hasOfflineDelayMs()) {
            SeekBarView offlineDelayMs = new SeekBarView();
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
            coreCtl.addItem(offlineDelayMs);
	}

	if (mCoreCtl.hasOnlineDelayMs()) {
            SeekBarView onlineDelayMs = new SeekBarView();
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
            coreCtl.addItem(onlineDelayMs);
	}

	if (coreCtl.size() > 0) {
            items.add(coreCtl);
	}
    }

}
