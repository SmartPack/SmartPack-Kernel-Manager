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

import android.content.Context;
import android.os.Vibrator;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.misc.Misc;
import com.grarak.kerneladiutor.utils.kernel.misc.PowerSuspend;
import com.grarak.kerneladiutor.utils.kernel.misc.Vibration;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
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
        if (mVibration.supported()) {
            vibrationInit(items);
        }
        if (mMisc.hasLoggerEnable()) {
            loggerInit(items);
        }
        if (mMisc.hasPrintKMode()) {
            printkInit(items);
        }
        if (mMisc.hasCrc()) {
            crcInit(items);
        }
        fsyncInit(items);
        if (mMisc.hasGentleFairSleepers()) {
            gentlefairsleepersInit(items);
        }
        if (mMisc.hasArchPower()) {
            archPowerInit(items);
        }
        if (mMisc.hasSELinux()) {
            selinuxInit(items);
        }
        if (PowerSuspend.supported()) {
            powersuspendInit(items);
        }
        networkInit(items);
    }

    private void vibrationInit(List<RecyclerViewItem> items) {
        final Vibrator vibrator = (Vibrator) getActivity()
                .getSystemService(Context.VIBRATOR_SERVICE);

        final int min = mVibration.getMin();
        int max = mVibration.getMax();
        final float offset = (max - min) / 100f;

        SeekBarView vibration = new SeekBarView();
        vibration.setTitle(getString(R.string.vibration_strength));
        vibration.setUnit("%");
        vibration.setProgress(Math.round((mVibration.get() - min) / offset));
        vibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mVibration.setVibration(Math.round(position * offset + min), getActivity());
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (vibrator != null) {
                            vibrator.vibrate(300);
                        }
                    }
                }, 250);
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(vibration);
    }

    private void loggerInit(List<RecyclerViewItem> items) {
        SwitchView logger = new SwitchView();
        logger.setTitle(getString(R.string.android_logger));
        logger.setSummary(getString(R.string.android_logger_summary));
        logger.setChecked(mMisc.isLoggerEnabled());
        logger.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableLogger(isChecked, getActivity());
            }
        });

        items.add(logger);
    }

    private void printkInit(List<RecyclerViewItem> items) {
        SwitchView printk = new SwitchView();
        printk.setTitle(getString(R.string.printk_logger));
        printk.setSummary(getString(R.string.printk_logger_summary));
        printk.setChecked(mMisc.isPrintKModeEnabled());
        printk.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enablePrintKMode(isChecked, getActivity());
            }
        });

        items.add(printk);
    }

    private void crcInit(List<RecyclerViewItem> items) {
        SwitchView crc = new SwitchView();
        crc.setTitle(getString(R.string.crc));
        crc.setSummary(getString(R.string.crc_summary));
        crc.setChecked(mMisc.isCrcEnabled());
        crc.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableCrc(isChecked, getActivity());
            }
        });

        items.add(crc);
    }

    private void fsyncInit(List<RecyclerViewItem> items) {
        if (mMisc.hasFsync()) {
            SwitchView fsync = new SwitchView();
            fsync.setTitle(getString(R.string.fsync));
            fsync.setSummary(getString(R.string.fsync_summary));
            fsync.setChecked(mMisc.isFsyncEnabled());
            fsync.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMisc.enableFsync(isChecked, getActivity());
                }
            });

            items.add(fsync);
        }

        if (mMisc.hasDynamicFsync()) {
            SwitchView dynamicFsync = new SwitchView();
            dynamicFsync.setTitle(getString(R.string.dynamic_fsync));
            dynamicFsync.setSummary(getString(R.string.dynamic_fsync_summary));
            dynamicFsync.setChecked(mMisc.isDynamicFsyncEnabled());
            dynamicFsync.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMisc.enableDynamicFsync(isChecked, getActivity());
                }
            });

            items.add(dynamicFsync);
        }
    }

    private void gentlefairsleepersInit(List<RecyclerViewItem> items) {
        SwitchView gentleFairSleepers = new SwitchView();
        gentleFairSleepers.setTitle(getString(R.string.gentlefairsleepers));
        gentleFairSleepers.setSummary(getString(R.string.gentlefairsleepers_summary));
        gentleFairSleepers.setChecked(mMisc.isGentleFairSleepersEnabled());
        gentleFairSleepers.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableGentleFairSleepers(isChecked, getActivity());
            }
        });

        items.add(gentleFairSleepers);
    }

    private void archPowerInit(List<RecyclerViewItem> items) {
        SwitchView archPower = new SwitchView();
        archPower.setTitle(getString(R.string.arch_power));
        archPower.setSummary(getString(R.string.arch_power_summary));
        archPower.setChecked(mMisc.isArchPowerEnabled());
        archPower.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableArchPower(isChecked, getActivity());
            }
        });

        items.add(archPower);
    }

    private void selinuxInit(List<RecyclerViewItem> items) {
        SwitchView selinux = new SwitchView();
        selinux.setTitle(getString(R.string.selinux_switch));
        selinux.setSummary(getString(R.string.selinux_switch_summary));
        selinux.setChecked(mMisc.isSELinuxEnabled());
        selinux.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableSELinux(isChecked, getActivity());
            }
        });

        items.add(selinux);
    }

    private void powersuspendInit(List<RecyclerViewItem> items) {
        if (PowerSuspend.hasMode()) {
            SelectView mode = new SelectView();
            mode.setTitle(getString(R.string.power_suspend_mode));
            mode.setSummary(getString(R.string.power_suspend_mode_summary));
            mode.setItems(Arrays.asList(getResources().getStringArray(R.array.powersuspend_items)));
            mode.setItem(PowerSuspend.getMode());
            mode.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    PowerSuspend.setMode(position, getActivity());
                }
            });

            items.add(mode);
        }

        if (PowerSuspend.hasOldState()) {
            SwitchView state = new SwitchView();
            state.setTitle(getString(R.string.power_suspend_state));
            state.setSummary(getString(R.string.power_suspend_state_summary));
            state.setChecked(PowerSuspend.isOldStateEnabled());
            state.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    PowerSuspend.enableOldState(isChecked, getActivity());
                }
            });

            items.add(state);
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
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(state);
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
            tcp.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mMisc.setTcpCongestion(item, getActivity());
                }
            });

            networkCard.addItem(tcp);
        } catch (Exception ignored) {
        }

        if (mMisc.hasWireguard()) {
            DescriptionView wireguard = new DescriptionView();
            wireguard.setTitle(getString(R.string.wireguard));
            wireguard.setSummary(("Version: ") + mMisc.getWireguard());

            networkCard.addItem(wireguard);
        }

        GenericSelectView hostname = new GenericSelectView();
        hostname.setSummary(getString(R.string.hostname));
        hostname.setValue(mMisc.getHostname());
        hostname.setValueRaw(hostname.getValue());
        hostname.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                mMisc.setHostname(value, getActivity());
            }
        });

        networkCard.addItem(hostname);

        items.add(networkCard);
    }

}
