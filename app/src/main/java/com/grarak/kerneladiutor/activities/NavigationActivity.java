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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUHotplugFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltageFragment;
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
import com.grarak.kerneladiutor.fragments.other.HelpFragment;
import com.grarak.kerneladiutor.fragments.other.SettingsFragment;
import com.grarak.kerneladiutor.fragments.statistics.DeviceFragment;
import com.grarak.kerneladiutor.fragments.statistics.InputsFragment;
import com.grarak.kerneladiutor.fragments.statistics.MemoryFragment;
import com.grarak.kerneladiutor.fragments.statistics.OverallFragment;
import com.grarak.kerneladiutor.fragments.tools.BackupFragment;
import com.grarak.kerneladiutor.fragments.tools.BuildpropFragment;
import com.grarak.kerneladiutor.fragments.tools.InitdFragment;
import com.grarak.kerneladiutor.fragments.tools.OnBootFragment;
import com.grarak.kerneladiutor.fragments.tools.ProfileFragment;
import com.grarak.kerneladiutor.fragments.tools.RecoveryFragment;
import com.grarak.kerneladiutor.fragments.tools.customcontrols.CustomControlsFragment;
import com.grarak.kerneladiutor.fragments.tools.downloads.DownloadsFragment;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.WebpageReader;
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
import com.grarak.kerneladiutor.views.AdNativeExpress;

import java.util.LinkedHashMap;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static LinkedHashMap<Integer, Fragment> sFragments = new LinkedHashMap<>();
    public final static LinkedHashMap<Integer, Fragment> sActualFragments = new LinkedHashMap<>();

    static {
        sFragments.put(R.string.statistics, null);
        sFragments.put(R.string.overall, new OverallFragment());
        sFragments.put(R.string.device, new DeviceFragment());
        if (Device.MemInfo.getItems().size() > 0) {
            sFragments.put(R.string.memory, new MemoryFragment());
        }
        sFragments.put(R.string.inputs, new InputsFragment());
        sFragments.put(R.string.kernel, null);
        sFragments.put(R.string.cpu, new CPUFragment());
        if (Voltage.supported()) {
            sFragments.put(R.string.cpu_voltage, new CPUVoltageFragment());
        }
        if (Hotplug.supported()) {
            sFragments.put(R.string.cpu_hotplug, new CPUHotplugFragment());
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
        sFragments.put(R.string.on_boot, new OnBootFragment());
        sFragments.put(R.string.other, null);
        sFragments.put(R.string.settings, new SettingsFragment());
        sFragments.put(R.string.about, new AboutFragment());
        sFragments.put(R.string.contributors, new ContributorsFragment());
        sFragments.put(R.string.help, new HelpFragment());
    }

    private static Thread mPatchingThread;
    private static Callback sCallback;

    private interface Callback {
        void onBannerResize();
    }

    private Handler mHandler = new Handler();
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private boolean mExit;

    private int mSelection;
    private boolean mLicenseDialog = true;

    private WebpageReader mAdsFetcher;
    private boolean mFetchingAds;

    @Override
    protected boolean setStatusBarColor() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sCallback = new Callback() {
            @Override
            public void onBannerResize() {
                Fragment fragment = sActualFragments.get(mSelection);
                if (fragment instanceof RecyclerViewFragment) {
                    ((RecyclerViewFragment) fragment).resizeBanner();
                }
            }
        };
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
        mNavigationView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.clearFocus();
                }
            }
        });
        appendFragments();

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt("selection");
            mLicenseDialog = savedInstanceState.getBoolean("license");
            mFetchingAds = savedInstanceState.getBoolean("fetching_ads");
        }

        if (mSelection == 0 || !sActualFragments.containsKey(mSelection)) {
            mSelection = firstTab();
        }
        onItemSelected(mSelection, false);

        int result = Prefs.getInt("license", -1, this);
        int intentResult = getIntent().getIntExtra("result", -1);

        if ((result == intentResult && result == 2) && mLicenseDialog) {
            ViewUtils.dialogBuilder(getString(R.string.license_invalid), null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mLicenseDialog = false;
                            Prefs.saveInt("license", -1, NavigationActivity.this);
                        }
                    }, this).show();
        } else if ((result != intentResult || result == 3) && mLicenseDialog) {
            ViewUtils.dialogBuilder(getString(R.string.pirated), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLicenseDialog = false;
                    Prefs.saveInt("license", -1, NavigationActivity.this);
                }
            }, this).show();
        } else {
            mLicenseDialog = false;
            if (result == 0) {
                Utils.DONATED = true;
            }
        }

        String id;
        if ((id = Prefs.getString("android_id", "", this)).isEmpty()) {
            Prefs.saveString("android_id", id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID), this);
        }
        final String androidId = id;
        if (Utils.DONATED && mPatchingThread == null) {
            mPatchingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (Utils.isPatched(getPackageManager().getApplicationInfo(
                                "com.grarak.kerneladiutordonate", 0))) {
                            Utils.DONATED = false;
                            if (!BuildConfig.DEBUG) {
                                Answers.getInstance().logCustom(new CustomEvent("Pirated")
                                        .putCustomAttribute("android_id", androidId));
                            }
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                    mPatchingThread = null;
                }
            });
            mPatchingThread.start();
        }

        if (!mFetchingAds && !Utils.DONATED) {
            mFetchingAds = true;
            mAdsFetcher = new WebpageReader(this, new WebpageReader.WebpageCallback() {
                @Override
                public void onCallback(String raw, CharSequence html) {
                    if (raw == null || raw.isEmpty()) return;
                    AdNativeExpress.GHAds ghAds = new AdNativeExpress.GHAds(raw);
                    if (ghAds.readable()) {
                        ghAds.cache(NavigationActivity.this);
                        for (int id : sActualFragments.keySet()) {
                            Fragment fragment = sActualFragments.get(id);
                            if (fragment instanceof RecyclerViewFragment) {
                                ((RecyclerViewFragment) fragment).ghAdReady();
                            }
                        }
                    }
                }
            });
            mAdsFetcher.execute(AdNativeExpress.ADS_FETCH);
        }
    }

    private int firstTab() {
        for (int id : sActualFragments.keySet()) {
            if (sActualFragments.get(id) != null) {
                return id;
            }
        }
        return 0;
    }

    public void appendFragments() {
        sActualFragments.clear();
        Menu menu = mNavigationView.getMenu();
        menu.clear();

        SubMenu lastSubMenu = null;
        for (int id : sFragments.keySet()) {
            if (sFragments.get(id) == null) {
                lastSubMenu = menu.addSubMenu(id);
                sActualFragments.put(id, null);
            } else if (Prefs.getBoolean(sFragments.get(id).getClass().getSimpleName() + "_enabled",
                    true, this)) {
                MenuItem menuItem = lastSubMenu == null ? menu.add(0, id, 0, id) :
                        lastSubMenu.add(0, id, 0, id);
                menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_blank));
                menuItem.setCheckable(true);
                if (mSelection != 0) {
                    mNavigationView.setCheckedItem(mSelection);
                }
                sActualFragments.put(id, sFragments.get(id));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if ((sActualFragments.get(mSelection) instanceof BaseFragment
                    && !((BaseFragment) sActualFragments.get(mSelection)).onBackPressed())
                    || sActualFragments.get(mSelection).getClass() == SettingsFragment.class) {
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
        fragmentTransaction.commitAllowingStateLoss();
        RootUtils.closeSU();
        if (mAdsFetcher != null) {
            mAdsFetcher.cancel();
        }
        if (mPatchingThread != null) {
            mPatchingThread.interrupt();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection", mSelection);
        outState.putBoolean("license", mLicenseDialog);
        outState.putBoolean("fetching_ads", mFetchingAds);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        onItemSelected(item.getItemId(), true);
        return true;
    }

    private void onItemSelected(final int res, boolean delay) {
        mDrawer.closeDrawer(GravityCompat.START);
        getSupportActionBar().setTitle(getString(res));
        mNavigationView.setCheckedItem(res);
        mSelection = res;
        Fragment fragment = getFragment(res);
        if (fragment instanceof RecyclerViewFragment) {
            ((RecyclerViewFragment) fragment).mDelay = delay;
        } else if (fragment instanceof SettingsFragment) {
            ((SettingsFragment) fragment).mDelay = delay;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment,
                res + "_key").commit();
    }

    private Fragment getFragment(int res) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(res + "_key");
        if (fragment == null) {
            return sActualFragments.get(res);
        }
        return fragment;
    }

    public static void bannerResize() {
        if (sCallback != null) {
            sCallback.onBannerResize();
        }
    }

}
