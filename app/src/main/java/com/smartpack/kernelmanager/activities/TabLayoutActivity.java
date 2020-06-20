/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.kernel.BatteryFragment;
import com.smartpack.kernelmanager.fragments.kernel.CPUFragment;
import com.smartpack.kernelmanager.fragments.kernel.GPUFragment;
import com.smartpack.kernelmanager.fragments.kernel.VMFragment;
import com.smartpack.kernelmanager.fragments.statistics.DeviceFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUTimes;
import com.smartpack.kernelmanager.views.recyclerview.PagerAdapter;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 17, 2020
 */

public class TabLayoutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        ViewPager viewPager = findViewById(R.id.viewPagerID);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        if (Utils.mBattery) {
            adapter.AddFragment(new BatteryFragment(), getString(R.string.battery));
        } else if (Utils.mCPU) {
            adapter.AddFragment(new CPUFragment(), getString(R.string.cpu));
        } else if (Utils.mCPUTimes) {
            adapter.AddFragment(new CPUTimes(), getString(R.string.cpu_times));
        } else if (Utils.mDevice) {
            adapter.AddFragment(new DeviceFragment(), getString(R.string.device));
        } else if (Utils.mGPU) {
            adapter.AddFragment(new GPUFragment(), getString(R.string.gpu));
        } else if (Utils.mMemory) {
            adapter.AddFragment(new VMFragment(), getString(R.string.virtual_memory));
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (Utils.mBattery) Utils.mBattery = false;
        if (Utils.mCPU) Utils.mCPU = false;
        if (Utils.mCPUTimes) Utils.mCPUTimes = false;
        if (Utils.mDevice) Utils.mDevice = false;
        if (Utils.mGPU) Utils.mGPU = false;
        if (Utils.mMemory) Utils.mMemory = false;
        super.onBackPressed();
    }

}