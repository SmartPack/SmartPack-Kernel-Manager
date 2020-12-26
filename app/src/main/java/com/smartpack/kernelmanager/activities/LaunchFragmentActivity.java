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
import androidx.fragment.app.Fragment;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.kernel.BatteryFragment;
import com.smartpack.kernelmanager.fragments.kernel.VMFragment;
import com.smartpack.kernelmanager.fragments.other.AboutFragment;
import com.smartpack.kernelmanager.fragments.statistics.DeviceFragment;
import com.smartpack.kernelmanager.fragments.tools.TranslatorFragment;
import com.smartpack.kernelmanager.utils.Utils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 17, 2020
 */

public class LaunchFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchfragment);

        Fragment selectedFragment = null;
        if (Utils.mAbout) {
            selectedFragment = new AboutFragment();
        } else if (Utils.mBattery) {
            selectedFragment = new BatteryFragment();
        } else if (Utils.mDevice) {
            selectedFragment = new DeviceFragment();
        } else if (Utils.mMemory) {
            selectedFragment = new VMFragment();
        } else if (Utils.mTranslator) {
            selectedFragment = new TranslatorFragment();
        }
        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (Utils.mAbout) Utils.mAbout = false;
        if (Utils.mBattery) Utils.mBattery = false;
        if (Utils.mDevice) Utils.mDevice = false;
        if (Utils.mMemory) Utils.mMemory = false;
        if (Utils.mTranslator) Utils.mTranslator = false;
        super.onBackPressed();
    }

}