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
package com.grarak.kerneladiutor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUHotplug;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltage;
import com.grarak.kerneladiutor.fragments.kernel.EntropyFragment;
import com.grarak.kerneladiutor.fragments.kernel.GPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.IOFragment;
import com.grarak.kerneladiutor.fragments.kernel.KSMFragment;
import com.grarak.kerneladiutor.fragments.kernel.LEDFragment;
import com.grarak.kerneladiutor.fragments.kernel.LMKFragment;
import com.grarak.kerneladiutor.fragments.kernel.MiscFragment;
import com.grarak.kerneladiutor.fragments.kernel.ScreenFragment;
import com.grarak.kerneladiutor.fragments.kernel.SoundFragment;
import com.grarak.kerneladiutor.fragments.kernel.ThermalFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.fragments.kernel.WakeFrament;
import com.grarak.kerneladiutor.fragments.other.AboutFragment;
import com.grarak.kerneladiutor.fragments.other.ContributorsFragment;
import com.grarak.kerneladiutor.fragments.other.FAQFragment;
import com.grarak.kerneladiutor.fragments.statistics.DeviceFragment;
import com.grarak.kerneladiutor.fragments.statistics.InputsFragment;
import com.grarak.kerneladiutor.fragments.statistics.OverallFragment;
import com.grarak.kerneladiutor.fragments.tools.BackupFragment;
import com.grarak.kerneladiutor.fragments.tools.BuildpropFragment;
import com.grarak.kerneladiutor.fragments.tools.InitdFragment;
import com.grarak.kerneladiutor.fragments.tools.ProfileFragment;
import com.grarak.kerneladiutor.fragments.tools.RecoveryFragment;
import com.grarak.kerneladiutor.fragments.tools.customcontrols.CustomControlsFragment;
import com.grarak.kerneladiutor.fragments.tools.downloads.DownloadsFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.Hotplug;
import com.grarak.kerneladiutor.utils.kernel.cpuvoltage.Voltage;
import com.grarak.kerneladiutor.utils.kernel.entropy.Entropy;
import com.grarak.kerneladiutor.utils.kernel.gpu.GPU;
import com.grarak.kerneladiutor.utils.kernel.io.IO;
import com.grarak.kerneladiutor.utils.kernel.ksm.KSM;
import com.grarak.kerneladiutor.utils.kernel.led.LED;
import com.grarak.kerneladiutor.utils.kernel.lmk.LMK;
import com.grarak.kerneladiutor.utils.kernel.screen.Screen;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.utils.kernel.thermal.Thermal;
import com.grarak.kerneladiutor.utils.kernel.wake.Wake;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Backup;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static LinkedHashMap<Integer, Fragment> sFragments = new LinkedHashMap<>();
    public final static LinkedHashMap<Integer, Fragment> sActualFragments = new LinkedHashMap<>();
    public final static HashMap<Integer, Class> sActivities = new HashMap<>();

    static {
        sFragments.put(R.string.statistics, null);
        sFragments.put(R.string.overall, new OverallFragment());
        sFragments.put(R.string.device, new DeviceFragment());
        sFragments.put(R.string.inputs, new InputsFragment());
        sFragments.put(R.string.kernel, null);
        sFragments.put(R.string.cpu, new CPUFragment());
        if (Voltage.supported()) {
            sFragments.put(R.string.cpu_voltage, new CPUVoltage());
        }
        if (Hotplug.supported()) {
            sFragments.put(R.string.cpu_hotplug, new CPUHotplug());
        }
        if (Thermal.supported()) {
            sFragments.put(R.string.thermal, new ThermalFragment());
        }
        if (GPU.supported()) {
            sFragments.put(R.string.gpu, new GPUFragment());
        }
        if (Screen.supported()) {
            sFragments.put(R.string.screen, new ScreenFragment());
        }
        if (Wake.supported()) {
            sFragments.put(R.string.wake, new WakeFrament());
        }
        if (Sound.supported()) {
            sFragments.put(R.string.sound, new SoundFragment());
        }
        sFragments.put(R.string.battery, new BatteryFragment());
        if (LED.supported()) {
            sFragments.put(R.string.led, new LEDFragment());
        }
        if (IO.supported()) {
            sFragments.put(R.string.io_scheduler, new IOFragment());
        }
        if (KSM.supported()) {
            sFragments.put(R.string.ksm, new KSMFragment());
        }
        if (LMK.supported()) {
            sFragments.put(R.string.lmk, new LMKFragment());
        }
        sFragments.put(R.string.virtual_memory, new VMFragment());
        if (Entropy.supported()) {
            sFragments.put(R.string.entropy, new EntropyFragment());
        }
        sFragments.put(R.string.misc, new MiscFragment());
        sFragments.put(R.string.tools, null);
        sFragments.put(R.string.custom_controls, new CustomControlsFragment());
        sFragments.put(R.string.downloads, null);
        if (Backup.hasBackup()) {
            sFragments.put(R.string.backup, new BackupFragment());
        }
        sFragments.put(R.string.build_prop_editor, new BuildpropFragment());
        sFragments.put(R.string.profile, new ProfileFragment());
        sFragments.put(R.string.recovery, new RecoveryFragment());
        sFragments.put(R.string.initd, new InitdFragment());
        sFragments.put(R.string.other, null);
        sFragments.put(R.string.about, new AboutFragment());
        sFragments.put(R.string.contributors, new ContributorsFragment());
        sFragments.put(R.string.faq, new FAQFragment());
        sFragments.put(R.string.settings, null);

        sActivities.put(R.string.settings, SettingsActivity.class);
    }

    private static NavigationActivity mActivity;
    private Handler mHandler = new Handler();
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private boolean mExit;

    private int mSelection;
    private boolean mShowPirateDialog = true;

    @Override
    protected boolean setStatusBarColor() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = getToolBar();
        setSupportActionBar(toolbar);

        SupportedDownloads support = new SupportedDownloads(this);
        if (support.getLink() != null) {
            sFragments.put(R.string.downloads, DownloadsFragment.newInstance(support));
        } else {
            sFragments.remove(R.string.downloads);
        }

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        appendFragments();

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt("selection");
        }

        if (mSelection == 0 || !sActualFragments.containsKey(mSelection)) {
            mSelection = firstTab();
        }
        onItemSelected(mSelection);

        int result = Prefs.getInt("license", -1, this);
        int intentResult = getIntent().getIntExtra("result", -1);
        Prefs.saveInt("license", -1, this);
        if (savedInstanceState != null) {
            mShowPirateDialog = savedInstanceState.getBoolean("pirate");
        }
        if ((result != intentResult || result == 3) && mShowPirateDialog) {
            ViewUtils.dialogBuilder(getString(R.string.pirated), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mShowPirateDialog = false;
                }
            }, this).show();
        } else {
            mShowPirateDialog = false;
            if (result == 0) {
                Utils.DONATED = true;
            }
        }
    }

    private int firstTab() {
        for (int id : sActualFragments.keySet()) {
            if (sActualFragments.get(id) != null || sActivities.containsKey(id)) {
                return id;
            }
        }
        return 0;
    }

    private void appendFragments() {
        sActualFragments.clear();
        Menu menu = mNavigationView.getMenu();
        menu.clear();

        SubMenu lastSubMenu = null;
        for (int id : sFragments.keySet()) {
            if (sFragments.get(id) == null && !sActivities.containsKey(id)) {
                lastSubMenu = menu.addSubMenu(id);
                sActualFragments.put(id, null);
            } else if (Prefs.getBoolean(sActivities.containsKey(id) ?
                    sActivities.get(id).getSimpleName() + "_enabled" :
                    sFragments.get(id).getClass().getSimpleName() + "_enabled", true, this)) {
                MenuItem menuItem = lastSubMenu == null ? menu.add(0, id, 0, id) :
                        lastSubMenu.add(0, id, 0, id);
                menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_blank));
                menuItem.setCheckable(!sActivities.containsKey(id));
                sActualFragments.put(id, sFragments.get(id));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (sActualFragments.get(mSelection) instanceof BaseFragment
                    && !((BaseFragment) sActualFragments.get(mSelection)).onBackPressed()) {
                if (mExit) {
                    mExit = false;
                    super.onBackPressed();
                } else {
                    Utils.toast(R.string.press_back_again_exit, this);
                    mExit = true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mExit = false;
                        }
                    }, 2000);
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int key : sActualFragments.keySet()) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(key + "_key");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
        }
        fragmentTransaction.commit();
        RootUtils.closeSU();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection", mSelection);
        outState.putBoolean("pirate", mShowPirateDialog);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        onItemSelected(item.getItemId());
        return true;
    }

    private void onItemSelected(int res) {
        if (sActivities.containsKey(res)) {
            startActivity(new Intent(this, sActivities.get(res)));
        } else {
            mDrawer.closeDrawer(GravityCompat.START);
            getSupportActionBar().setTitle(getString(res));
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(res),
                    res + "_key").commit();
            mNavigationView.setCheckedItem(res);
            mSelection = res;
        }
    }

    private Fragment getFragment(int res) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(res + "_key");
        if (fragment == null) {
            return sActualFragments.get(res);
        }
        return fragment;
    }

    public static void restart() {
        if (mActivity != null) {
            mActivity.recreate();
        }
    }

}
