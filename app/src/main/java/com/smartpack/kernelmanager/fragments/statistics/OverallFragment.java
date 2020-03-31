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
package com.smartpack.kernelmanager.fragments.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.view.Menu;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.fragments.kernel.BatteryFragment;
import com.smartpack.kernelmanager.fragments.kernel.VMFragment;
import com.smartpack.kernelmanager.utils.Device;

import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.kernel.battery.Battery;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPUFreq;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.StatsView;
import com.smartpack.kernelmanager.views.recyclerview.overallstatistics.TemperatureView;
import com.smartpack.kernelmanager.views.recyclerview.MultiStatsView;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUTimes;

import java.util.List;

/**
 * Created by willi on 19.04.16.
 */
public class OverallFragment extends RecyclerViewFragment {

    private Device.MemInfo mMemInfo;

    private GPUFreq mGPUFreq;

    private StatsView mGPUFreqStatsView;
    private MultiStatsView mUpTime;
    private MultiStatsView mBatteryInfo;
    private MultiStatsView mVM;
    private TemperatureView mTemperature;

    private double mBatteryRaw;

    @Override
    protected void init() {
        super.init();

        mGPUFreq = GPUFreq.getInstance();
        mMemInfo = Device.MemInfo.getInstance();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.overall),
                getString(R.string.overall_summary)));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        statsInit(items);
    }

    private void statsInit(List<RecyclerViewItem> items) {
        CardView cardView = new CardView(getActivity());
        cardView.setDrawable(getResources().getDrawable(R.drawable.ic_chart));
        cardView.setTitle(getString(R.string.overall) + " " + getString(R.string.statistics));
        cardView.setOnMenuListener((cardView1, popupMenu) -> {
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.cpu_times));

            popupMenu.setOnMenuItemClickListener(item -> {
                switchFragment(new CPUTimes());
                return false;
            });
        });
        if (mGPUFreq.hasCurFreq()) {
            mGPUFreqStatsView = new StatsView();
            mGPUFreqStatsView.setTitle(getString(R.string.gpu_freq));
            cardView.addItem(mGPUFreqStatsView);
        }

        mTemperature = new TemperatureView();
        mTemperature.setFullSpan(mGPUFreqStatsView == null);
        cardView.addItem(mTemperature);
        items.add(cardView);

        CardView device = new CardView(getActivity());
        device.setDrawable(getResources().getDrawable(R.drawable.ic_device));
        device.setTitle(Device.getModel());
        device.setOnMenuListener((device1, popupMenu) -> {
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

            popupMenu.setOnMenuItemClickListener(item -> {
                switchFragment(new DeviceFragment());
                return false;
            });
        });
        mUpTime = new MultiStatsView();
        mUpTime.setTitle(getString(R.string.device) + " " + getString(R.string.uptime));
        device.addItem(mUpTime);
        items.add(device);

        if (Battery.haschargingstatus() || Battery.hasBatteryVoltage()) {
            CardView battery = new CardView(getActivity());
            battery.setDrawable(getResources().getDrawable(R.drawable.ic_battery));
            battery.setTitle(getString(R.string.battery));
            battery.setOnMenuListener((battery1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                popupMenu.setOnMenuItemClickListener(item -> {
                    switchFragment(new BatteryFragment());
                    return false;
                });
            });

            mBatteryInfo = new MultiStatsView();
            mBatteryInfo.setTitle((" Health: ") + Battery.BatteryHealth());
            battery.addItem(mBatteryInfo);
            items.add(battery);
        }

        if (mMemInfo.getItemMb("MemTotal") != 0 || Device.MemInfo.getInstance().
                getItemMb("SwapTotal") != 0) {
            CardView memmory = new CardView(getActivity());
            memmory.setDrawable(getResources().getDrawable(R.drawable.ic_cpu));
            memmory.setTitle(getString(R.string.memory));
            memmory.setOnMenuListener((memmory1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                popupMenu.setOnMenuItemClickListener(item -> {
                    switchFragment(new VMFragment());
                    return false;
                });
            });
            mVM = new MultiStatsView();
            memmory.addItem(mVM);
            items.add(memmory);
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
    }

    private Integer mGPUCurFreq;

    @Override
    protected void refreshThread() {
        super.refreshThread();

        mGPUCurFreq = mGPUFreq.getCurFreq();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mGPUFreqStatsView != null && mGPUCurFreq != null) {
            mGPUFreqStatsView.setStat(mGPUCurFreq / mGPUFreq.getCurFreqOffset() + getString(R.string.mhz));
        }
        if (mTemperature != null) {
            mTemperature.setBattery(mBatteryRaw);
        }
        if (mUpTime != null) {
            mUpTime.setStatsOne(("Total: ") + Utils.getDurationBreakdown(SystemClock.elapsedRealtime()));
            mUpTime.setStatsTwo(("Awake: ") + Utils.getDurationBreakdown(SystemClock.uptimeMillis()));
            mUpTime.setStatsThree(("Deep Sleep: ") + Utils.getDurationBreakdown(SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()));
        }
        if (mBatteryInfo != null) {
            mBatteryInfo.setStatsOne(("Voltage: ") + Battery.BatteryVoltage() + (" mV"));
            mBatteryInfo.setStatsTwo(Battery.ChargingInfoTitle() + (": ") + Battery.getchargingstatus() + (" mA"));
        }
        if (mVM != null) {
            if (mMemInfo.getItemMb("MemTotal") != 0) {
                mVM.setStatsOne("RAM - Total: " + mMemInfo.getItemMb("MemTotal") + " MB, Used: " + (mMemInfo.getItemMb("MemTotal")
                        - (mMemInfo.getItemMb("Cached") + mMemInfo.getItemMb("MemFree"))) + " MB");
            }
            if (Device.MemInfo.getInstance().getItemMb("SwapTotal") != 0) {
                mVM.setStatsTwo("Swap - Total: " + Device.MemInfo.getInstance().getItemMb("SwapTotal") + " MB, Used: "
                        + (Device.MemInfo.getInstance().getItemMb("SwapTotal") - mMemInfo.getItemMb("SwapFree")) + " MB");
            } else {
                mVM.setStatsTwo("Swap: N/A");
            }
        }
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10D;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            requireActivity().unregisterReceiver(mBatteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

}