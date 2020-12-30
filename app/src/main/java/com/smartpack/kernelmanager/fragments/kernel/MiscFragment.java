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

import android.content.ContentResolver;
import android.content.Context;
import android.os.Vibrator;
import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.misc.Misc;
import com.smartpack.kernelmanager.utils.kernel.misc.PowerSuspend;
import com.smartpack.kernelmanager.utils.kernel.misc.Vibration;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.GenericInputView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class MiscFragment extends RecyclerViewFragment {

	private Vibration mVibration;
	private Misc mMisc;

	@Override
	protected void init() {
		super.init();

		mVibration = Vibration.getInstance();
		mMisc = Misc.getInstance();
		addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
	}

	@Override
	protected void addItems(List<RecyclerViewItem> items) {
		if (mVibration.supported() || mMisc.hasLoggerEnable() || Misc.hasPrintKMode() || mMisc.hasCrc()
				|| mMisc.hasFsync() || mMisc.hasDynamicFsync() || mMisc.hasGentleFairSleepers() || mMisc.hasArchPower()
				|| mMisc.hasHapticOverride() || Misc.hasHapticUser() || Misc.hasHapticsNotification()
				|| Misc.hasHapticsCall() || Misc.hasLeases() || Misc.hasLeaseBreakTime()
				|| Misc.hasSELinux() || Misc.hasDoze()) {
			miscInit(items);
		}
		if (PowerSuspend.supported()) {
			powersuspendInit(items);
		}
		if (Misc.hasCPUSet()) {
			cpusetInit(items);
		}
		networkInit(items);
	}

	private void miscInit(List<RecyclerViewItem> items) {
		CardView miscCard = new CardView(getActivity());
		miscCard.setTitle(getString(R.string.misc));

		if (mVibration.supported()) {
			final Vibrator vibrator = (Vibrator) getActivity()
					.getSystemService(Context.VIBRATOR_SERVICE);

			final int min = mVibration.getMin();
			int max = mVibration.getMax();
			final float offset = (max - min) / 100f;

			SeekBarView vibration = new SeekBarView();
			vibration.setTitle(getString(R.string.vibration_strength));
			vibration.setUnit(" %");
			vibration.setProgress(Math.round((mVibration.get() - min) / offset));
			vibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
				@Override
				public void onStop(SeekBarView seekBarView, int position, String value) {
					mVibration.setVibration(Math.round(position * offset + min), getActivity());
					getHandler().postDelayed(() -> {
						if (vibrator != null) {
							vibrator.vibrate(300);
						}
					}, 250);
				}

				@Override
				public void onMove(SeekBarView seekBarView, int position, String value) {
				}
			});

			miscCard.addItem(vibration);
		}

		if (mMisc.hasHapticOverride()) {
			SwitchView override = new SwitchView();
			override.setTitle(getString(R.string.override_vib));
			override.setSummary(getString(R.string.override_vib_summary));
			override.setChecked(mMisc.isHapticOverrideEnabled());
			override.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableHapticOverride(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							override.setChecked(mMisc.isHapticOverrideEnabled());
						},
						500);
			});

			miscCard.addItem(override);
		}

		if (Misc.hasHapticUser()) {
			SeekBarView hapticUser = new SeekBarView();
			hapticUser.setSummary(getString(R.string.system_vib));
			hapticUser.setUnit(" %");
			hapticUser.setProgress(Misc.getHapticUser() / 36);
			hapticUser.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
				@Override
				public void onStop(SeekBarView seekBarView, int position, String value) {
					mMisc.setHapticUser((position * 36), getActivity());
					getHandler().postDelayed(() -> {
								hapticUser.setProgress(Misc.getHapticUser() / 36);
							},
							500);
				}

				@Override
				public void onMove(SeekBarView seekBarView, int position, String value) {
				}
			});

			miscCard.addItem(hapticUser);
		}

		if (Misc.hasHapticsNotification()) {
			SeekBarView hapticNoti = new SeekBarView();
			hapticNoti.setSummary(getString(R.string.notification_vib));
			hapticNoti.setUnit(" %");
			hapticNoti.setProgress(Misc.getHapticsNotification() / 36);
			hapticNoti.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
				@Override
				public void onStop(SeekBarView seekBarView, int position, String value) {
					mMisc.setHapticsNotification((position * 36), getActivity());
					getHandler().postDelayed(() -> {
								hapticNoti.setProgress(Misc.getHapticsNotification() / 36);
							},
							500);
				}

				@Override
				public void onMove(SeekBarView seekBarView, int position, String value) {
				}
			});

			miscCard.addItem(hapticNoti);
		}

		if (Misc.hasHapticsCall()) {
			SeekBarView hapticCalls = new SeekBarView();
			hapticCalls.setSummary(getString(R.string.calls_vib));
			hapticCalls.setUnit(" %");
			hapticCalls.setProgress(Misc.getHapticsCall() / 36);
			hapticCalls.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
				@Override
				public void onStop(SeekBarView seekBarView, int position, String value) {
					mMisc.setHapticsCall((position * 36), getActivity());
					getHandler().postDelayed(() -> {
								hapticCalls.setProgress(Misc.getHapticsCall() / 36);
							},
							500);
				}

				@Override
				public void onMove(SeekBarView seekBarView, int position, String value) {
				}
			});

			miscCard.addItem(hapticCalls);
		}

		if (mMisc.hasLoggerEnable()) {
			SwitchView logger = new SwitchView();
			logger.setTitle(getString(R.string.android_logger));
			logger.setSummary(getString(R.string.android_logger_summary));
			logger.setChecked(mMisc.isLoggerEnabled());
			logger.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableLogger(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							logger.setChecked(mMisc.isLoggerEnabled());
						},
						500);
			});

			miscCard.addItem(logger);
		}

		if (Misc.hasPrintKMode()) {
			SwitchView printk = new SwitchView();
			printk.setTitle(getString(R.string.printk_logger));
			printk.setSummary(getString(R.string.printk_logger_summary));
			printk.setChecked(Misc.isPrintKModeEnabled());
			printk.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enablePrintKMode(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							printk.setChecked(Misc.isPrintKModeEnabled());
						},
						500);
			});

			miscCard.addItem(printk);
		}

		if (mMisc.hasCrc()) {
			SwitchView crc = new SwitchView();
			crc.setTitle(getString(R.string.crc));
			crc.setSummary(getString(R.string.crc_summary));
			crc.setChecked(mMisc.isCrcEnabled());
			crc.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableCrc(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							crc.setChecked(mMisc.isCrcEnabled());
						},
						500);
			});

			miscCard.addItem(crc);
		}

		if (mMisc.hasFsync()) {
			SwitchView fsync = new SwitchView();
			fsync.setTitle(getString(R.string.fsync));
			fsync.setSummary(getString(R.string.fsync_summary));
			fsync.setChecked(mMisc.isFsyncEnabled());
			fsync.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableFsync(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							fsync.setChecked(mMisc.isFsyncEnabled());
						},
						500);
			});

			miscCard.addItem(fsync);
		}

		if (mMisc.hasDynamicFsync()) {
			SwitchView dynamicFsync = new SwitchView();
			dynamicFsync.setTitle(getString(R.string.dynamic_fsync));
			dynamicFsync.setSummary(getString(R.string.dynamic_fsync_summary));
			dynamicFsync.setChecked(mMisc.isDynamicFsyncEnabled());
			dynamicFsync.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableDynamicFsync(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							dynamicFsync.setChecked(mMisc.isDynamicFsyncEnabled());
						},
						500);
			});

			miscCard.addItem(dynamicFsync);
		}

		if (mMisc.hasGentleFairSleepers()) {
			SwitchView gentleFairSleepers = new SwitchView();
			gentleFairSleepers.setTitle(getString(R.string.gentlefairsleepers));
			gentleFairSleepers.setSummary(getString(R.string.gentlefairsleepers_summary));
			gentleFairSleepers.setChecked(mMisc.isGentleFairSleepersEnabled());
			gentleFairSleepers.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableGentleFairSleepers(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							gentleFairSleepers.setChecked(mMisc.isGentleFairSleepersEnabled());
						},
						500);
			});

			miscCard.addItem(gentleFairSleepers);
		}

		if (mMisc.hasArchPower()) {
			SwitchView archPower = new SwitchView();
			archPower.setTitle(getString(R.string.arch_power));
			archPower.setSummary(getString(R.string.arch_power_summary));
			archPower.setChecked(mMisc.isArchPowerEnabled());
			archPower.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableArchPower(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							archPower.setChecked(mMisc.isArchPowerEnabled());
						},
						500);
			});

			miscCard.addItem(archPower);
		}

		if (Misc.hasLeases()) {
			SwitchView enable = new SwitchView();
			enable.setTitle(getString(R.string.leases_enable));
			enable.setSummary(getString(R.string.leases_enable_summary));
			enable.setChecked(mMisc.isLeasesEnabled());
			enable.addOnSwitchListener((switchView, isChecked) -> {
				mMisc.enableLeases(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							enable.setChecked(mMisc.isLeasesEnabled());
						},
						500);
			});

			miscCard.addItem(enable);
		}

		if (Misc.hasLeaseBreakTime()) {
			GenericSelectView leaseBreakTime = new GenericSelectView();
			leaseBreakTime.setTitle(getString(R.string.lease_break_time) + (" (s)"));
			leaseBreakTime.setSummary(getString(R.string.lease_break_time_summary));
			leaseBreakTime.setValue(Misc.getLeaseBreakTime());
			leaseBreakTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			leaseBreakTime.setOnGenericValueListener((genericSelectView, value) -> {
				mMisc.setLeaseBreakTime(value, getActivity());
				genericSelectView.setValue(value);
				getHandler().postDelayed(() -> {
							leaseBreakTime.setValue(Misc.getLeaseBreakTime());
						},
						500);
			});

			miscCard.addItem(leaseBreakTime);
		}

		if (Misc.hasSELinux()) {
			SelectView SELinux = new SelectView();
			SELinux.setTitle(getString(R.string.selinux));
			SELinux.setSummary(getString(R.string.selinux_summary));
			SELinux.setItems(Misc.seLinux(requireActivity()));
			SELinux.setItem(Misc.getSELinux());
			SELinux.setOnItemSelected((selectView, position, item) -> {
				mMisc.setSELinux(position, getActivity());
				getHandler().postDelayed(() -> SELinux.setItem(Misc.getSELinux()),
						500);
			});

			miscCard.addItem(SELinux);
		}

		if (Misc.hasDoze()) {
			SwitchView doze = new SwitchView();
			doze.setTitle(getString(R.string.doze));
			doze.setSummary(getString(R.string.doze_summary));
			doze.setChecked(Misc.isDozeEnabled());
			doze.addOnSwitchListener((switchView, isChecked)
					-> mMisc.enableDoze(isChecked, requireActivity()));
			getHandler().postDelayed(() -> doze.setChecked(Misc.isDozeEnabled()),
					500);

			miscCard.addItem(doze);
		}

		SwitchView userSync = new SwitchView();
		userSync.setTitle(getString(R.string.auto_sync));
		userSync.setSummary(getString(R.string.auto_sync_summary));
		userSync.setChecked(ContentResolver.getMasterSyncAutomatically());
		userSync.addOnSwitchListener((switchView, isChecked) -> {
			if(isChecked) {
				ContentResolver.setMasterSyncAutomatically(true);
			} else {
				ContentResolver.setMasterSyncAutomatically(false);
			}
		});

		miscCard.addItem(userSync);

		if (miscCard.size() > 0) {
			items.add(miscCard);
		}
	}

	private void powersuspendInit(List<RecyclerViewItem> items) {
		CardView psCard = new CardView(getActivity());
		psCard.setTitle(getString(R.string.power_suspend));

		if (PowerSuspend.hasMode()) {
			SelectView mode = new SelectView();
			mode.setTitle(getString(R.string.power_suspend_mode));
			mode.setSummary(getString(R.string.power_suspend_mode_summary));
			mode.setItems(Arrays.asList(getResources().getStringArray(R.array.powersuspend_items)));
			mode.setItem(PowerSuspend.getMode());
			mode.setOnItemSelected((selectView, position, item) -> {
				PowerSuspend.setMode(position, getActivity());
				getHandler().postDelayed(() -> {
							mode.setItem(PowerSuspend.getMode());
						},
						500);
			});

			psCard.addItem(mode);
		}

		if (PowerSuspend.hasOldState()) {
			SwitchView state = new SwitchView();
			state.setTitle(getString(R.string.power_suspend_state));
			state.setSummary(getString(R.string.power_suspend_state_summary));
			state.setChecked(PowerSuspend.isOldStateEnabled());
			state.addOnSwitchListener((switchView, isChecked) -> {
				PowerSuspend.enableOldState(isChecked, getActivity());
				getHandler().postDelayed(() -> {
							state.setChecked(PowerSuspend.isOldStateEnabled());
						},
						500);
			});

			psCard.addItem(state);
		}

		if (PowerSuspend.hasNewState()) {
			SeekBarView state = new SeekBarView();
			state.setTitle(getString(R.string.power_suspend_state));
			state.setSummary(getString(R.string.power_suspend_state_summary));
			state.setMax(2);
			state.setProgress(PowerSuspend.getNewState());
			state.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
				@Override
				public void onStop(SeekBarView seekBarView, int position, String value) {
					PowerSuspend.setNewState(position, getActivity());
					getHandler().postDelayed(() -> {
								state.setProgress(PowerSuspend.getNewState());
							},
							500);
				}

				@Override
				public void onMove(SeekBarView seekBarView, int position, String value) {
				}
			});

			psCard.addItem(state);
		}

		if (psCard.size() > 0) {
			items.add(psCard);
		}
	}

	private void cpusetInit(List<RecyclerViewItem> items) {
		CardView cpusetCard = new CardView(getActivity());
		cpusetCard.setTitle(getString(R.string.cpuset));

		for (int i = 0; i < Misc.size(); i++) {
			if (Misc.exists(i)) {
				GenericInputView cpuset = new GenericInputView();
				cpuset.setTitle(Misc.getName(i));
				cpuset.setValue(Misc.getValue(i));
				cpuset.setValueRaw(cpuset.getValue());

				final int position = i;
				cpuset.setOnGenericValueListener((genericSelectView, value) -> {
					mMisc.setValue(value, position, getActivity());
					genericSelectView.setValue(value);
				});
				cpusetCard.addItem(cpuset);
			}
		}

		if (cpusetCard.size() > 0) {
			items.add(cpusetCard);
		}

	}

	private void networkInit(List<RecyclerViewItem> items) {
		CardView networkCard = new CardView(getActivity());
		networkCard.setTitle(getString(R.string.network));

		try {
			SelectView tcp = new SelectView();
			tcp.setTitle(getString(R.string.tcp));
			tcp.setSummary(getString(R.string.tcp_summary));
			tcp.setItems(mMisc.getTcpAvailableCongestions());
			tcp.setItem(mMisc.getTcpCongestion());
			tcp.setOnItemSelected((selectView, position, item) -> {
				mMisc.setTcpCongestion(item, getActivity());
				getHandler().postDelayed(() -> {
							tcp.setItem(mMisc.getTcpCongestion());
						},
						500);
			});

			networkCard.addItem(tcp);
		} catch (Exception ignored) {
		}

		if (Misc.hasWireguard()) {
			DescriptionView wireguard = new DescriptionView();
			wireguard.setTitle(getString(R.string.wireguard));
			wireguard.setSummary(("Version: ") + Misc.getWireguard());

			networkCard.addItem(wireguard);
		}

		GenericSelectView hostname = new GenericSelectView();
		hostname.setSummary(getString(R.string.hostname));
		hostname.setValue(mMisc.getHostname());
		hostname.setValueRaw(hostname.getValue());
		hostname.setOnGenericValueListener((genericSelectView, value) -> {
			mMisc.setHostname(value, getActivity());
			getHandler().postDelayed(() -> {
						hostname.setValue(mMisc.getHostname());
					},
					500);
		});

		networkCard.addItem(hostname);

		items.add(networkCard);
	}

}