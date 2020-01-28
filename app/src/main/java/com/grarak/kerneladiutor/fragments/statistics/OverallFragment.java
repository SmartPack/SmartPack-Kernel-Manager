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
package com.grarak.kerneladiutor.fragments.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;

import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.battery.Battery;
import com.grarak.kerneladiutor.utils.kernel.gpu.GPUFreq;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.StatsView;
import com.grarak.kerneladiutor.views.recyclerview.overallstatistics.TemperatureView;
import com.smartpack.kernelmanager.recyclerview.MultiStatsView;
import com.smartpack.kernelmanager.utils.CPUTimes;
import com.smartpack.kernelmanager.utils.UpdateCheck;

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
        cardView.setOnMenuListener(new CardView.OnMenuListener() {
            @Override
            public void onMenuReady(CardView cardView, androidx.appcompat.widget.PopupMenu popupMenu) {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.cpu_times));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, new CPUTimes()).commit();
                        return false;
                    }
                });
            }
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
        device.setOnMenuListener(new CardView.OnMenuListener() {
            @Override
            public void onMenuReady(CardView device, androidx.appcompat.widget.PopupMenu popupMenu) {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, new DeviceFragment()).commit();
                        return false;
                    }
                });
            }
        });
        mUpTime = new MultiStatsView();
        mUpTime.setTitle(getString(R.string.device) + " " + getString(R.string.uptime));
        device.addItem(mUpTime);
        items.add(device);

        if (Battery.haschargingstatus() || Battery.hasBatteryVoltage()) {
            CardView battery = new CardView(getActivity());
            battery.setDrawable(getResources().getDrawable(R.drawable.ic_battery));
            battery.setTitle(getString(R.string.battery));
            battery.setOnMenuListener(new CardView.OnMenuListener() {
                @Override
                public void onMenuReady(CardView battery, androidx.appcompat.widget.PopupMenu popupMenu) {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, new BatteryFragment()).commit();
                            return false;
                        }
                    });
                }
            });

            mBatteryInfo = new MultiStatsView();
            mBatteryInfo.setTitle((" Health: ") + Battery.BatteryHealth());
            battery.addItem(mBatteryInfo);
            items.add(battery);
        }

        if (mMemInfo.getItemMb("MemTotal") != 0 || mMemInfo.getInstance().
                getItemMb("SwapTotal") != 0) {
            CardView memmory = new CardView(getActivity());
            memmory.setDrawable(getResources().getDrawable(R.drawable.ic_cpu));
            memmory.setTitle(getString(R.string.memory));
            memmory.setOnMenuListener(new CardView.OnMenuListener() {
                @Override
                public void onMenuReady(CardView memmory, androidx.appcompat.widget.PopupMenu popupMenu) {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.more));

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, new VMFragment()).commit();
                            return false;
                        }
                    });
                }
            });
            mVM = new MultiStatsView();
            memmory.addItem(mVM);
            items.add(memmory);
        }
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
            if (mMemInfo.getInstance().getItemMb("SwapTotal") != 0) {
                mVM.setStatsTwo("Swap - Total: " + mMemInfo.getInstance().getItemMb("SwapTotal") + " MB, Used: "
                        + (mMemInfo.getInstance().getItemMb("SwapTotal") - mMemInfo.getItemMb("SwapFree")) + " MB");
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
        getActivity().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mBatteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        // Initialize auto app update check
        if (Utils.isNetworkAvailable(getActivity()) && Prefs.getBoolean("auto_update", true, getActivity())) {
            if (!UpdateCheck.hasVersionInfo() || (UpdateCheck.lastModified() + 3720000L < System.currentTimeMillis())) {
                UpdateCheck.getVersionInfo();
            }
            if (UpdateCheck.hasVersionInfo() && !BuildConfig.VERSION_NAME.equals(UpdateCheck.versionName())) {
                UpdateCheck.updateAvailableDialog(getActivity());
            }
        }
    }


}
