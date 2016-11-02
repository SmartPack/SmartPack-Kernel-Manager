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
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static List<NavigationFragment> sFragments = new ArrayList<>();
    public final static LinkedHashMap<Integer, Fragment> sActualFragments = new LinkedHashMap<>();

    private static final NavigationFragment mDownloadsFragment = new NavigationFragment(R.string.downloads);

    static {
        sFragments.add(new NavigationFragment(R.string.statistics));
        sFragments.add(new NavigationFragment(R.string.overall, new OverallFragment(), R.drawable.ic_chart));
        sFragments.add(new NavigationFragment(R.string.device, new DeviceFragment(), R.drawable.ic_device));
        if (Device.MemInfo.getItems().size() > 0) {
            sFragments.add(new NavigationFragment(R.string.memory, new MemoryFragment(), R.drawable.ic_save));
        }
        sFragments.add(new NavigationFragment(R.string.inputs, new InputsFragment(), R.drawable.ic_keyboard));
        sFragments.add(new NavigationFragment(R.string.kernel));
        sFragments.add(new NavigationFragment(R.string.cpu, new CPUFragment(), R.drawable.ic_cpu));
        if (Voltage.supported()) {
            sFragments.add(new NavigationFragment(R.string.cpu_voltage, new CPUVoltageFragment(), R.drawable.ic_bolt));
        }
        if (Hotplug.supported()) {
            sFragments.add(new NavigationFragment(R.string.cpu_hotplug, new CPUHotplugFragment(), R.drawable.ic_switch));
        }
        if (Thermal.supported()) {
            sFragments.add(new NavigationFragment(R.string.thermal, new ThermalFragment(), R.drawable.ic_temperature));
        }
        if (GPU.supported()) {
            sFragments.add(new NavigationFragment(R.string.gpu, new GPUFragment(), R.drawable.ic_gpu));
        }
        if (Screen.supported()) {
            sFragments.add(new NavigationFragment(R.string.screen, new ScreenFragment(), R.drawable.ic_display));
        }
        if (Wake.supported()) {
            sFragments.add(new NavigationFragment(R.string.wake, new WakeFrament(), R.drawable.ic_unlock));
        }
        if (Sound.supported()) {
            sFragments.add(new NavigationFragment(R.string.sound, new SoundFragment(), R.drawable.ic_music));
        }
        sFragments.add(new NavigationFragment(R.string.battery, new BatteryFragment(), R.drawable.ic_battery));
        if (LED.supported()) {
            sFragments.add(new NavigationFragment(R.string.led, new LEDFragment(), R.drawable.ic_led));
        }
        if (IO.supported()) {
            sFragments.add(new NavigationFragment(R.string.io_scheduler, new IOFragment(), R.drawable.ic_sdcard));
        }
        if (KSM.supported()) {
            sFragments.add(new NavigationFragment(R.string.ksm, new KSMFragment(), R.drawable.ic_merge));
        }
        if (LMK.supported()) {
            sFragments.add(new NavigationFragment(R.string.lmk, new LMKFragment(), R.drawable.ic_stackoverflow));
        }
        sFragments.add(new NavigationFragment(R.string.virtual_memory, new VMFragment(), R.drawable.ic_server));
        if (Entropy.supported()) {
            sFragments.add(new NavigationFragment(R.string.entropy, new EntropyFragment(), R.drawable.ic_numbers));
        }
        sFragments.add(new NavigationFragment(R.string.misc, new MiscFragment(), R.drawable.ic_clear));
        sFragments.add(new NavigationFragment(R.string.tools));
        sFragments.add(new NavigationFragment(R.string.custom_controls, new CustomControlsFragment(), R.drawable.ic_console));
        sFragments.add(mDownloadsFragment);
        if (Backup.hasBackup()) {
            sFragments.add(new NavigationFragment(R.string.backup, new BackupFragment(), R.drawable.ic_restore));
        }
        sFragments.add(new NavigationFragment(R.string.build_prop_editor, new BuildpropFragment(), R.drawable.ic_edit));
        sFragments.add(new NavigationFragment(R.string.profile, new ProfileFragment(), R.drawable.ic_layers));
        sFragments.add(new NavigationFragment(R.string.recovery, new RecoveryFragment(), R.drawable.ic_security));
        sFragments.add(new NavigationFragment(R.string.initd, new InitdFragment(), R.drawable.ic_shell));
        sFragments.add(new NavigationFragment(R.string.on_boot, new OnBootFragment(), R.drawable.ic_start));
        sFragments.add(new NavigationFragment(R.string.other));
        sFragments.add(new NavigationFragment(R.string.settings, new SettingsFragment(), R.drawable.ic_settings));
        sFragments.add(new NavigationFragment(R.string.about, new AboutFragment(), R.drawable.ic_about));
        sFragments.add(new NavigationFragment(R.string.contributors, new ContributorsFragment(), R.drawable.ic_people));
        sFragments.add(new NavigationFragment(R.string.help, new HelpFragment(), R.drawable.ic_help));
    }

    private static Thread mPatchingThread;
    private static Callback sCallback;

    private interface Callback {
        void onBannerResize();
    }

    private SparseArray<Drawable> mDrawables = new SparseArray<>();
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
            mDownloadsFragment.mFragment = DownloadsFragment.newInstance(support);
            mDownloadsFragment.mDrawable = R.drawable.ic_download;
        } else {
            sFragments.remove(mDownloadsFragment);
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
        appendFragments(false);

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt("selection");
            mLicenseDialog = savedInstanceState.getBoolean("license");
            mFetchingAds = savedInstanceState.getBoolean("fetching_ads");
        }

        String section = getIntent().getStringExtra("section");
        if (section != null) {
            for (int id : sActualFragments.keySet()) {
                if (sActualFragments.get(id) != null
                        && sActualFragments.get(id).getClass().getCanonicalName().equals(section)) {
                    mSelection = id;
                    break;
                }
            }
            getIntent().removeExtra("section");
        }

        if (mSelection == 0 || !sActualFragments.containsKey(mSelection)) {
            mSelection = firstTab();
        }
        onItemSelected(mSelection, false, false);

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
        appendFragments(true);
    }

    private void appendFragments(boolean setShortcuts) {
        sActualFragments.clear();
        Menu menu = mNavigationView.getMenu();
        menu.clear();

        SubMenu lastSubMenu = null;
        for (NavigationFragment navigationFragment : sFragments) {
            Fragment fragment = navigationFragment.mFragment;
            int id = navigationFragment.mId;
            Drawable drawable = getNavigationDrawable(navigationFragment.mDrawable == 0
                    || !Prefs.getBoolean("section_icons", false, this) || !Utils.DONATED ?
                    R.drawable.ic_blank : navigationFragment.mDrawable);

            if (fragment == null) {
                lastSubMenu = menu.addSubMenu(id);
                sActualFragments.put(id, null);
            } else if (Prefs.getBoolean(fragment.getClass().getSimpleName() + "_enabled",
                    true, this)) {
                MenuItem menuItem = lastSubMenu == null ? menu.add(0, id, 0, id) :
                        lastSubMenu.add(0, id, 0, id);
                menuItem.setIcon(drawable);
                menuItem.setCheckable(true);
                if (mSelection != 0) {
                    mNavigationView.setCheckedItem(mSelection);
                }
                sActualFragments.put(id, fragment);
            }
        }
        if (setShortcuts) {
            setShortcuts();
        }
    }

    private NavigationFragment getNavigationFragment(Fragment fragment) {
        for (NavigationFragment navigationFragment : sFragments) {
            if (fragment == navigationFragment.mFragment) {
                return navigationFragment;
            }
        }
        return null;
    }

    private void setShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return;
        HashMap<Fragment, Integer> openendFragmentsCount = new HashMap<>();

        for (int id : sActualFragments.keySet()) {
            Fragment fragment = sActualFragments.get(id);
            if (fragment == null || fragment.getClass() == SettingsFragment.class) continue;

            int opened = Prefs.getInt(fragment.getClass().getSimpleName() + "_opened", 0, this);
            openendFragmentsCount.put(fragment, opened);
        }

        int max = 0;
        for (Map.Entry<Fragment, Integer> map : openendFragmentsCount.entrySet()) {
            if (max < map.getValue()) {
                max = map.getValue();
            }
        }

        int count = 0;
        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.removeAllDynamicShortcuts();
        for (int i = max; i >= 0; i--) {
            for (Map.Entry<Fragment, Integer> map : openendFragmentsCount.entrySet()) {
                if (i == map.getValue()) {
                    NavigationFragment navFragment = getNavigationFragment(map.getKey());
                    if (navFragment == null) continue;

                    if (count == 4) break;
                    count++;

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.putExtra("section", navFragment.mFragment.getClass().getCanonicalName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    ShortcutInfo shortcut = new ShortcutInfo.Builder(this,
                            navFragment.mFragment.getClass().getSimpleName())
                            .setShortLabel(getString(navFragment.mId))
                            .setLongLabel(getString(R.string.open, getString(navFragment.mId)))
                            .setIcon(Icon.createWithResource(this, navFragment.mDrawable == 0 ?
                                    R.drawable.ic_blank : navFragment.mDrawable))
                            .setIntent(intent)
                            .build();
                    shortcutInfos.add(shortcut);
                }
            }
        }
        shortcutManager.setDynamicShortcuts(shortcutInfos);
    }

    private Drawable getNavigationDrawable(int drawableId) {
        if (mDrawables.indexOfKey(drawableId) >= 0) {
            return mDrawables.get(drawableId);
        } else {
            mDrawables.put(drawableId, ContextCompat.getDrawable(this, drawableId));
            return getNavigationDrawable(drawableId);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if ((sActualFragments.get(mSelection) instanceof BaseFragment
                    && !((BaseFragment) sActualFragments.get(mSelection)).onBackPressed())
                    || (sActualFragments.get(mSelection) != null
                    && sActualFragments.get(mSelection).getClass() == SettingsFragment.class)) {
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
        if (mAdsFetcher != null) {
            mAdsFetcher.cancel();
        }
        if (mPatchingThread != null) {
            mPatchingThread.interrupt();
        }
        RootUtils.closeSU();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection", mSelection);
        outState.putBoolean("license", mLicenseDialog);
        outState.putBoolean("fetching_ads", mFetchingAds);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        onItemSelected(item.getItemId(), true, true);
        return true;
    }

    private void onItemSelected(final int res, boolean delay, boolean saveOpened) {
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

        if (saveOpened) {
            String openedName = fragment.getClass().getSimpleName() + "_opened";
            Prefs.saveInt(openedName, Prefs.getInt(openedName, 0, this) + 1, this);
        }
        setShortcuts();
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

    public static class NavigationFragment {

        public int mId;
        public Fragment mFragment;
        private int mDrawable;

        private NavigationFragment(int id) {
            this(id, null, 0);
        }

        private NavigationFragment(int id, Fragment fragment, int drawable) {
            mId = id;
            mFragment = fragment;
            mDrawable = drawable;
        }

        @Override
        public String toString() {
            return String.valueOf(mId);
        }
    }

}
