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
import com.smartpack.kernelmanager.activities.TabLayoutActivity;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.battery.Battery;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPU;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPUFreq;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.CircularProgressView;
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
    private float mMemTotal, mMemFree, mSwapTotal, mSwapFree;

    private String mBatteryChargingStatus, mBatteryInfoTile;
    private int mBatteryVolt, mBatteryLevel;

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
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.cpu));
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.cpu_times));
            if (GPU.supported()) {
                menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.gpu));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        Utils.mCPU = true;
                        switchFragment();
                        break;
                    case 1:
                        Utils.mCPUTimes = true;
                        switchFragment();
                        break;
                    case 2:
                        Utils.mGPU = true;
                        switchFragment();
                        break;
                }
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
                Utils.mDevice = true;
                switchFragment();
                return false;
            });
        });
        mUpTime = new MultiStatsView();
        mUpTime.setTitle(getString(R.string.device) + " " + getString(R.string.uptime));
        device.addItem(mUpTime);
        items.add(device);

        if (Battery.haschargingstatus() || Battery.hasBatteryVoltage()) {
            CardView battery = new CardView(getActivity());
            battery.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_battery, requireContext()));
            battery.setTitle(getString(R.string.battery));
            battery.setOnMenuListener((battery1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                popupMenu.setOnMenuItemClickListener(item -> {
                    Utils.mBattery = true;
                    switchFragment();
                    return false;
                });
            });

            mBatteryInfo = new MultiStatsView();
            mBatteryInfo.setTitle(("Health: ") + Battery.BatteryHealth());
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
                    Utils.mMemory = true;
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
        Intent intent = new Intent(getActivity(), TabLayoutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void refreshThread() {
        super.refreshThread();

        mGPUCurFreq = mGPUFreq.getCurFreq();
        mCurFreqoffset = mGPUFreq.getCurFreqOffset();

        mDeviceToatlTime = Utils.getDurationBreakdown(SystemClock.elapsedRealtime());
        mDeviceAwakeTime = Utils.getDurationBreakdown(SystemClock.uptimeMillis());
        mDeviceDeepsleepTime = Utils.getDurationBreakdown(SystemClock.elapsedRealtime() - SystemClock.uptimeMillis());

        mBatteryLevel = Battery.getBatteryLevel();
        mBatteryVolt = Battery.getBatteryVoltage();
        mBatteryInfoTile = Battery.ChargingInfoTitle();
        mBatteryChargingStatus = Battery.getchargingstatus();

        mMemTotal = mMemInfo.getItemMb("MemTotal");
        if (mMemTotal != 0) {
            mMemFree = mMemInfo.getItemMb("MemTotal")
                    - (mMemInfo.getItemMb("Cached") + mMemInfo.getItemMb("MemFree"));
        }
        mSwapTotal = Device.MemInfo.getInstance().getItemMb("SwapTotal");
        if (mSwapTotal != 0) {
            mSwapFree = Device.MemInfo.getInstance().getItemMb("SwapTotal") - mMemInfo.getItemMb("SwapFree");
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
            mUpTime.setStatsTwo(("Awake: ") + mDeviceAwakeTime);
            mUpTime.setStatsThree(("Deep Sleep: ") + mDeviceDeepsleepTime);
        }
        if (mBatteryInfo != null) {
            mBatteryInfo.setStatsOne(("Voltage: ") + mBatteryVolt + (" mV"));
            mBatteryInfo.setStatsTwo(mBatteryInfoTile + (": ") + mBatteryChargingStatus + (" mA"));
            mBatteryInfo.setMax(100);
            mBatteryInfo.setProgress(mBatteryLevel);
            mBatteryInfo.setProgressTitle(mBatteryLevel + "%");
        }
        if (mVM != null) {
            if (mMemTotal != 0) {
                mVM.setTitleLeft(getString(R.string.ram));
                mVM.setMaxLeft((int)mMemTotal);
                mVM.setProgressLeft((int)mMemFree);
                mVM.setHeadingOneLeft("Total");
                mVM.setHeadingTwoLeft("Used");
                mVM.setSummaryOneLeft(mMemTotal + " MB");
                mVM.setSummaryTwoLeft(mMemFree + " MB");
            }
            if (mSwapTotal != 0) {
                mVM.setTitleRight("Swap");
                mVM.setMaxRight((int)mSwapTotal);
                mVM.setProgressRight((int)mSwapFree);
                mVM.setHeadingOneRight("Total");
                mVM.setHeadingTwoRight("Used");
                mVM.setSummaryOneRight(mSwapTotal + " MB");
                mVM.setSummaryTwoRight(mSwapFree + " MB");
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