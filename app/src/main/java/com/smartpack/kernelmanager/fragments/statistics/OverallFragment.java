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

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.LaunchFragmentActivity;
import com.smartpack.kernelmanager.activities.OverallActivity;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.fragments.kernel.BatteryFragment;
import com.smartpack.kernelmanager.fragments.kernel.VMFragment;
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.battery.Battery;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPUFreq;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.CircularProgressView;
import com.smartpack.kernelmanager.views.recyclerview.MultiItemsView;
import com.smartpack.kernelmanager.views.recyclerview.MultiStatsView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.StatsView;
import com.smartpack.kernelmanager.views.recyclerview.overallstatistics.TemperatureView;

import java.util.List;

/**
 * Created by willi on 19.04.16.
 */
public class OverallFragment extends RecyclerViewFragment {

    private double mBatteryRaw;

    private Device.MemInfo mMemInfo;

    private GPUFreq mGPUFreq;

    private Integer mCurFreqoffset, mGPUCurFreq;

    private MultiStatsView mUpTime, mBatteryInfo;
    private CircularProgressView mVM;

    private StatsView mGPUFreqStatsView;

    private String mDeviceToatlTime, mDeviceAwakeTime, mDeviceDeepsleepTime;
    private String mBatteryChargingStatus, mBatteryInfoTile;
    private int mBatteryVolt, mBatteryLevel, mMemTotal, mMemFree, mSwapTotal, mSwapFree,
            mAwakePercentage, mDeepSleepPercentage;

    private TemperatureView mTemperature;

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
        cardView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_chart, requireContext()));
        cardView.setTitle(getString(R.string.overall) + " " + getString(R.string.statistics));
        cardView.setOnMenuListener((cardView1, popupMenu) -> {
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));
            popupMenu.setOnMenuItemClickListener(item -> {
                Intent intent = new Intent(getActivity(), OverallActivity.class);
                startActivity(intent);
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
        device.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_device, requireContext()));
        device.setTitle(Device.getModel());
        device.setOnMenuListener((device1, popupMenu) -> {
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

            popupMenu.setOnMenuItemClickListener(item -> {
                Common.setSelectedFragment(new DeviceFragment());
                switchFragment();
                return false;
            });
        });
        mUpTime = new MultiStatsView();
        mUpTime.setTitle(getString(R.string.device) + " " + getString(R.string.uptime));
        device.addItem(mUpTime);
        items.add(device);

        MultiItemsView mMenu = new MultiItemsView();
        items.add(mMenu);

        if (Battery.haschargingstatus() || Battery.hasBatteryVoltage()) {
            CardView battery = new CardView(getActivity());
            battery.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_battery, requireContext()));
            battery.setTitle(getString(R.string.battery));
            if (Battery.getInstance(requireActivity()).supported()) {
                battery.setOnMenuListener((battery1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                    popupMenu.setOnMenuItemClickListener(item -> {
                        Common.setSelectedFragment(new BatteryFragment());
                        switchFragment();
                        return false;
                    });
                });
            }

            mBatteryInfo = new MultiStatsView();
            mBatteryInfo.setTitle(("Health: ") + Battery.BatteryHealth());
            if (Battery.getInstance(requireActivity()).hasCapacity()) {
                mBatteryInfo.setStatsTwo(getString(R.string.capacity) + ": " + Battery.getInstance(requireActivity())
                        .getCapacity() + getString(R.string.mah));
            }
            battery.addItem(mBatteryInfo);
            items.add(battery);
        }

        if (mMemInfo.getItemMb("MemTotal") != 0 || Device.MemInfo.getInstance().
                getItemMb("SwapTotal") != 0) {
            CardView memmory = new CardView(getActivity());
            memmory.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_cpu, requireContext()));
            memmory.setTitle(getString(R.string.memory));
            memmory.setOnMenuListener((memmory1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                popupMenu.setOnMenuItemClickListener(item -> {
                    Common.setSelectedFragment(new VMFragment());
                    switchFragment();
                    return false;
                });
            });
            mVM = new CircularProgressView();
            memmory.addItem(mVM);

            items.add(memmory);
        }
    }

    private void switchFragment() {
        Intent intent = new Intent(getActivity(), LaunchFragmentActivity.class);
        startActivity(intent);
    }

    @Override
    protected void refreshThread() {
        super.refreshThread();

        mGPUCurFreq = mGPUFreq.getCurFreq();
        mCurFreqoffset = mGPUFreq.getCurFreqOffset();

        mDeviceToatlTime = Utils.getDurationBreakdown(SystemClock.elapsedRealtime());
        mDeviceAwakeTime = Utils.getDurationBreakdown(SystemClock.uptimeMillis());
        mAwakePercentage = Math.round((float) (SystemClock.uptimeMillis() * 100) / SystemClock.elapsedRealtime());
        mDeviceDeepsleepTime = Utils.getDurationBreakdown(SystemClock.elapsedRealtime() - SystemClock.uptimeMillis());
        mDeepSleepPercentage = Math.round((float) ((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) * 100) / SystemClock.elapsedRealtime());

        mBatteryLevel = Battery.getBatteryLevel();
        mBatteryVolt = Battery.getBatteryVoltage();
        mBatteryInfoTile = Battery.ChargingInfoTitle();
        mBatteryChargingStatus = Battery.getchargingstatus();

        mMemTotal = (int)mMemInfo.getItemMb("MemTotal");
        if (mMemTotal != 0) {
            mMemFree = (int)(mMemInfo.getItemMb("MemTotal")
                    - (mMemInfo.getItemMb("Cached") + mMemInfo.getItemMb("MemFree")));
        }
        mSwapTotal = (int)Device.MemInfo.getInstance().getItemMb("SwapTotal");
        if (mSwapTotal != 0) {
            mSwapFree = (int)(Device.MemInfo.getInstance().getItemMb("SwapTotal") - mMemInfo.getItemMb("SwapFree"));
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mGPUFreqStatsView != null && mGPUCurFreq != null) {
            mGPUFreqStatsView.setStat(mGPUCurFreq / mCurFreqoffset + getString(R.string.mhz));
        }
        if (mTemperature != null) {
            mTemperature.setBattery(mBatteryRaw);
        }
        if (mUpTime != null) {
            mUpTime.setStatsOne(("Total: ") + mDeviceToatlTime);
            mUpTime.setStatsTwo(("Awake: ") + mDeviceAwakeTime + " (" + mAwakePercentage + "%)");
            mUpTime.setStatsThree(("Deep Sleep: ") + mDeviceDeepsleepTime + " (" + mDeepSleepPercentage + "%)");
        }
        if (mBatteryInfo != null) {
            mBatteryInfo.setStatsOne(("Voltage: ") + mBatteryVolt + (" mV"));
            mBatteryInfo.setStatsThree(mBatteryInfoTile + (": ") + mBatteryChargingStatus + (" mA"));
            if (Battery.hasBatteryLevel()) {
                mBatteryInfo.setMax(100);
                mBatteryInfo.setProgress(mBatteryLevel);
                mBatteryInfo.setProgressTitle(mBatteryLevel + "%");
            }
        }
        if (mVM != null) {
            // RAM
            mVM.setTitleLeft(getString(R.string.ram));
            mVM.setMaxLeft(mMemTotal);
            mVM.setProgressLeft(mMemFree);
            mVM.setHeadingOneLeft("Total");
            mVM.setHeadingTwoLeft("Used");
            mVM.setSummaryOneLeft(mMemTotal + " MB");
            mVM.setSummaryTwoLeft(mMemFree + " MB");
            // Swap
            mVM.setTitleRight("Swap");
            mVM.setMaxRight(mSwapTotal);
            mVM.setProgressRight(mSwapFree);
            mVM.setHeadingOneRight("Total");
            mVM.setHeadingTwoRight("Used");
            mVM.setSummaryOneRight(mSwapTotal + " MB");
            mVM.setSummaryTwoRight(mSwapFree + " MB");
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