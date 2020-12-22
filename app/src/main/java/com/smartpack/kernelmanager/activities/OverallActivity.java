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

import com.google.android.material.tabs.TabLayout;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.kernel.CPUFragment;
import com.smartpack.kernelmanager.fragments.kernel.CPUVoltageFragment;
import com.smartpack.kernelmanager.fragments.kernel.GPUFragment;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUTimes;
import com.smartpack.kernelmanager.utils.kernel.cpuvoltage.Voltage;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPU;
import com.smartpack.kernelmanager.views.recyclerview.PagerAdapter;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 18, 2020
 */

public class OverallActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new CPUFragment(), getString(R.string.cpu));
        adapter.AddFragment(new CPUTimes(), getString(R.string.cpu_times));
        if (Voltage.getInstance().supported()) {
            adapter.AddFragment(new CPUVoltageFragment(), getString(R.string.cpu_voltage));
        }
        if (GPU.supported()) {
            adapter.AddFragment(new GPUFragment(), getString(R.string.gpu));
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
