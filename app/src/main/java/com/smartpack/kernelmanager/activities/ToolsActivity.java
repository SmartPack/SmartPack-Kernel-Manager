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
import com.smartpack.kernelmanager.fragments.tools.BackupFragment;
import com.smartpack.kernelmanager.fragments.tools.BuildpropFragment;
import com.smartpack.kernelmanager.fragments.tools.CustomControlsFragment;
import com.smartpack.kernelmanager.fragments.tools.ScriptMangerFragment;
import com.smartpack.kernelmanager.fragments.tools.SmartPackFragment;
import com.smartpack.kernelmanager.utils.tools.Backup;

import in.sunilpaulmathew.sCommon.Adapters.sPagerAdapter;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 24, 2020
 */

public class ToolsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        sPagerAdapter adapter = new sPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new SmartPackFragment(), getString(R.string.smartpack));
        adapter.AddFragment(new ScriptMangerFragment(), getString(R.string.script_manger));
        adapter.AddFragment(new CustomControlsFragment(), getString(R.string.custom_controls));
        if (Backup.hasBackup()) {
            adapter.AddFragment(new BackupFragment(), getString(R.string.backup));
        }
        adapter.AddFragment(new BuildpropFragment(), getString(R.string.build_prop_editor));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
