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
package com.smartpack.kernelmanager.activities;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.kernel.BatteryFragment;
import com.smartpack.kernelmanager.fragments.kernel.CPUFragment;
import com.smartpack.kernelmanager.fragments.kernel.CPUHotplugFragment;
import com.smartpack.kernelmanager.fragments.kernel.CPUVoltageFragment;
import com.smartpack.kernelmanager.fragments.kernel.DisplayLEDFragment;
import com.smartpack.kernelmanager.fragments.kernel.EntropyFragment;
import com.smartpack.kernelmanager.fragments.kernel.GPUFragment;
import com.smartpack.kernelmanager.fragments.kernel.IOFragment;
import com.smartpack.kernelmanager.fragments.kernel.KLapseFragment;
import com.smartpack.kernelmanager.fragments.kernel.KSMFragment;
import com.smartpack.kernelmanager.fragments.kernel.LMKFragment;
import com.smartpack.kernelmanager.fragments.kernel.MiscFragment;
import com.smartpack.kernelmanager.fragments.kernel.ScreenFragment;
import com.smartpack.kernelmanager.fragments.kernel.SoundFragment;
import com.smartpack.kernelmanager.fragments.kernel.ThermalFragment;
import com.smartpack.kernelmanager.fragments.kernel.VMFragment;
import com.smartpack.kernelmanager.fragments.kernel.WakeFragment;
import com.smartpack.kernelmanager.fragments.kernel.WakelockFragment;
import com.smartpack.kernelmanager.fragments.other.AboutFragment;
import com.smartpack.kernelmanager.fragments.other.ContributorsFragment;
import com.smartpack.kernelmanager.fragments.other.FAQFragment;
import com.smartpack.kernelmanager.fragments.other.SettingsFragment;
import com.smartpack.kernelmanager.fragments.statistics.DeviceFragment;
import com.smartpack.kernelmanager.fragments.statistics.InputsFragment;
import com.smartpack.kernelmanager.fragments.statistics.MemoryFragment;
import com.smartpack.kernelmanager.fragments.statistics.OverallFragment;
import com.smartpack.kernelmanager.fragments.tools.BackupFragment;
import com.smartpack.kernelmanager.fragments.tools.BuildpropFragment;
import com.smartpack.kernelmanager.fragments.tools.CustomControlsFragment;
import com.smartpack.kernelmanager.fragments.tools.OnBootFragment;
import com.smartpack.kernelmanager.fragments.tools.ProfileFragment;
import com.smartpack.kernelmanager.fragments.tools.ScriptMangerFragment;
import com.smartpack.kernelmanager.fragments.tools.SmartPackFragment;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.battery.Battery;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.Hotplug;
import com.smartpack.kernelmanager.utils.kernel.cpuvoltage.Voltage;
import com.smartpack.kernelmanager.utils.kernel.entropy.Entropy;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPU;
import com.smartpack.kernelmanager.utils.kernel.io.IO;
import com.smartpack.kernelmanager.utils.kernel.ksm.KSM;
import com.smartpack.kernelmanager.utils.kernel.led.LED;
import com.smartpack.kernelmanager.utils.kernel.lmk.LMK;
import com.smartpack.kernelmanager.utils.kernel.screen.KLapse;
import com.smartpack.kernelmanager.utils.kernel.screen.Screen;
import com.smartpack.kernelmanager.utils.kernel.sound.Sound;
import com.smartpack.kernelmanager.utils.kernel.thermal.Thermal;
import com.smartpack.kernelmanager.utils.kernel.wake.Wake;
import com.smartpack.kernelmanager.utils.kernel.wakelock.Wakelocks;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.Backup;

import org.frap129.spectrum.Spectrum;
import org.frap129.spectrum.SpectrumFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<NavigationFragment> mFragments = new ArrayList<>();
    private Map<Integer, Class<? extends Fragment>> mActualFragments = new LinkedHashMap<>();

    private Handler mHandler = new Handler();
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private boolean mExit;

    private int mSelection;

    @Override
    protected boolean setStatusBarColor() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            new FragmentLoader(this).execute();
        } else {
            mFragments = savedInstanceState.getParcelableArrayList("fragments");
            init(savedInstanceState);
        }
    }

    private static class FragmentLoader extends AsyncTask<Void, Void, Void> {

        private WeakReference<NavigationActivity> mRefActivity;

        private FragmentLoader(NavigationActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            NavigationActivity activity = mRefActivity.get();
            if (activity == null) return null;
            activity.initFragments();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NavigationActivity activity = mRefActivity.get();
            if (activity == null) return;
            activity.init(null);
        }
    }

    private void initFragments() {
        mFragments.clear();
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.statistics));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.overall, OverallFragment.class, R.drawable.ic_dashboard));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.device, DeviceFragment.class, R.drawable.ic_device));
        if (Device.MemInfo.getInstance().getItems().size() > 0) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.memory, MemoryFragment.class, R.drawable.ic_save));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.inputs, InputsFragment.class, R.drawable.ic_keyboard));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.kernel));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpu, CPUFragment.class, R.drawable.ic_cpu));
        if (Hotplug.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpu_hotplug, CPUHotplugFragment.class, R.drawable.ic_switch));
        }
        if (Voltage.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpu_voltage, CPUVoltageFragment.class, R.drawable.ic_flash));
        }
        if (GPU.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.gpu, GPUFragment.class, R.drawable.ic_gpu));
        }
        if (Thermal.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.thermal, ThermalFragment.class, R.drawable.ic_temperature));
        }
        if (Battery.getInstance(this).supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.battery, BatteryFragment.class, R.drawable.ic_battery));
        }
        if (Sound.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.sound, SoundFragment.class, R.drawable.ic_music));
        }
        if (Screen.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.screen, ScreenFragment.class, R.drawable.ic_display));
        }
        if (KLapse.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.klapse, KLapseFragment.class, R.drawable.ic_klapse));
        }
        if (Wake.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.wake, WakeFragment.class, R.drawable.ic_gestures));
        }
        if (LED.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.led, DisplayLEDFragment.class, R.drawable.ic_led));
        }
        if (IO.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.io_scheduler, IOFragment.class, R.drawable.ic_sdcard));
        }
        if (KSM.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(KSM.getInstance().isUKSM() ? R.string.uksm : R.string.ksm, KSMFragment.class, R.drawable.ic_merge));
        }
        if (LMK.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.lmk, LMKFragment.class, R.drawable.ic_stackoverflow));
        }
        if (Wakelocks.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.wakelock, WakelockFragment.class, R.drawable.ic_unlock));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.virtual_memory, VMFragment.class, R.drawable.ic_server));
        if (Entropy.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.entropy, EntropyFragment.class, R.drawable.ic_numbers));
        }
        if (Spectrum.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.spectrum, SpectrumFragment.class, R.drawable.ic_spectrum_logo));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.misc, MiscFragment.class, R.drawable.ic_clear));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.tools));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.profile, ProfileFragment.class, R.drawable.ic_layers));
        if (Backup.hasBackup()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.backup, BackupFragment.class, R.drawable.ic_restore));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.script_manger, ScriptMangerFragment.class, R.drawable.ic_shell));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.custom_controls, CustomControlsFragment.class, R.drawable.ic_console));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.build_prop_editor, BuildpropFragment.class, R.drawable.ic_edit));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.smartpack, SmartPackFragment.class, R.drawable.ic_flash));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.on_boot, OnBootFragment.class, R.drawable.ic_start));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.other));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.settings, SettingsFragment.class, R.drawable.ic_settings));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.contributors, ContributorsFragment.class, R.drawable.ic_people));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.faq, FAQFragment.class, R.drawable.ic_help));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.about, AboutFragment.class, R.drawable.ic_about));
    }

    private void init(Bundle savedInstanceState) {

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = getToolBar();
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.clearFocus();
            }
        });

        // Initialize Banner Ad
        if (Prefs.getBoolean("allow_ads", true, this)) {
            AppCompatTextView statusText = findViewById(R.id.ad_status_text);
            AdView mAdView = new AdView(this, "1189034858133626_1189035694800209", AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = findViewById(R.id.banner_container);
            adContainer.addView(mAdView);
            mAdView.loadAd();
            statusText.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt("selection");
        }

        appendFragments(false);
        String section = getIntent().getStringExtra("selection");
        if (section != null) {
            for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
                Class<? extends Fragment> fragmentClass = entry.getValue();
                if (fragmentClass != null && Objects.equals(fragmentClass.getCanonicalName(), section)) {
                    mSelection = entry.getKey();
                    break;
                }
            }
            getIntent().removeExtra("selection");
        } else if (savedInstanceState == null) {
            String defaultFragmentName = Prefs.getString("default_section", "OverallFragment", this);
            for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
                Class<? extends Fragment> fragmentClass = entry.getValue();
                if (fragmentClass != null && Objects.equals(fragmentClass.getSimpleName(), defaultFragmentName)) {
                    mSelection = entry.getKey();
                    break;
                }
            }
        }

        if (mSelection == 0 || mActualFragments.get(mSelection) == null) {
            mSelection = firstTab();
        }
        onItemSelected(mSelection, false, false);
    }

    private int firstTab() {
        for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
            if (entry.getValue() != null) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public void appendFragments() {
        appendFragments(true);
    }

    private void appendFragments(boolean setShortcuts) {
        mActualFragments.clear();
        Menu menu = mNavigationView.getMenu();
        menu.clear();

        SubMenu lastSubMenu = null;
        for (NavigationFragment navigationFragment : mFragments) {
            Class<? extends Fragment> fragmentClass = navigationFragment.mFragmentClass;
            int id = navigationFragment.mId;

            if (fragmentClass == null) {
                lastSubMenu = menu.addSubMenu(id);
                mActualFragments.put(id, null);
            } else if (Prefs.getBoolean(fragmentClass.getSimpleName() + "_enabled",
                    true, this)) {
                MenuItem menuItem = lastSubMenu == null ? menu.add(0, id, 0, id) :
                        lastSubMenu.add(0, id, 0, id);
                menuItem.setIcon(ViewUtils.getColoredIcon(Prefs.getBoolean("section_icons", true, this)
                        && navigationFragment.mDrawable != 0 ? navigationFragment.mDrawable : R.drawable.ic_blank, this));
                menuItem.setCheckable(true);
                if (mSelection != 0) {
                    mNavigationView.setCheckedItem(mSelection);
                }
                mActualFragments.put(id, fragmentClass);
            }
        }
        if (setShortcuts) {
            setShortcuts();
        }
    }

    private NavigationFragment findNavigationFragmentByClass(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null) return null;
        for (NavigationFragment navigationFragment : mFragments) {
            if (fragmentClass == navigationFragment.mFragmentClass) {
                return navigationFragment;
            }
        }
        return null;
    }

    private void setShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return;

        PriorityQueue<Class<? extends Fragment>> queue = new PriorityQueue<>(
                (o1, o2) -> {
                    int opened1 = Prefs.getInt(o1.getSimpleName() + "_opened",
                            0, NavigationActivity.this);
                    int opened2 = Prefs.getInt(o2.getSimpleName() + "_opened",
                            0, NavigationActivity.this);
                    return opened2 - opened1;
                });

        for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
            Class<? extends Fragment> fragmentClass = entry.getValue();
            if (fragmentClass == null || fragmentClass == SettingsFragment.class) continue;

            queue.offer(fragmentClass);
        }

        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        assert shortcutManager != null;
        shortcutManager.removeAllDynamicShortcuts();
        for (int i = 0; i < 4; i++) {
            NavigationFragment fragment = findNavigationFragmentByClass(queue.poll());
            if (fragment == null || fragment.mFragmentClass == null) continue;
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("selection", fragment.mFragmentClass.getCanonicalName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            ShortcutInfo shortcut = new ShortcutInfo.Builder(this,
                    fragment.mFragmentClass.getSimpleName())
                    .setShortLabel(getString(fragment.mId))
                    .setLongLabel(Utils.strFormat(getString(R.string.open), getString(fragment.mId)))
                    .setIcon(Icon.createWithResource(this, fragment.mDrawable == 0 ?
                            R.drawable.ic_blank : fragment.mDrawable))
                    .setIntent(intent)
                    .build();
            shortcutInfos.add(shortcut);
        }
        shortcutManager.setDynamicShortcuts(shortcutInfos);
    }

    public ArrayList<NavigationFragment> getFragments() {
        return mFragments;
    }

    public Map<Integer, Class<? extends Fragment>> getActualFragments() {
        return mActualFragments;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getFragment(mSelection);
            if (!(currentFragment instanceof BaseFragment)
                    || !((BaseFragment) currentFragment).onBackPressed()) {
                if (mExit) {
                    mExit = false;
                    super.onBackPressed();
                } else {
                    Utils.toast(R.string.press_back_again_exit, this);
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int id : mActualFragments.keySet()) {
            Fragment fragment = fragmentManager.findFragmentByTag(id + "_key");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
        RootUtils.closeSU();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("fragments", mFragments);
        outState.putInt("selection", mSelection);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        onItemSelected(item.getItemId(), true, true);
        return true;
    }

    private void onItemSelected(final int res, final boolean delay, boolean saveOpened) {
        mDrawer.closeDrawer(GravityCompat.START);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(res));
        mNavigationView.setCheckedItem(res);
        mSelection = res;
        final Fragment fragment = getFragment(res);

        if (saveOpened) {
            String openedName = fragment.getClass().getSimpleName() + "_opened";
            Prefs.saveInt(openedName, Prefs.getInt(openedName, 0, this) + 1, this);
        }
        setShortcuts();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment,
                res + "_key").commitAllowingStateLoss();
    }

    private Fragment getFragment(int res) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(res + "_key");
        if (fragment == null && mActualFragments.containsKey(res)) {
            fragment = Fragment.instantiate(this, Objects.requireNonNull(Objects.requireNonNull(mActualFragments.get(res)).getCanonicalName()));
        }
        return fragment;
    }

    public static class NavigationFragment implements Parcelable {

        public int mId;
        public Class<? extends Fragment> mFragmentClass;
        private int mDrawable;

        NavigationFragment(int id) {
            this(id, null, 0);
        }

        NavigationFragment(int id, Class<? extends Fragment> fragment, int drawable) {
            mId = id;
            mFragmentClass = fragment;
            mDrawable = drawable;
        }

        NavigationFragment(Parcel parcel) {
            mId = parcel.readInt();
            mFragmentClass = (Class<? extends Fragment>) parcel.readSerializable();
            mDrawable = parcel.readInt();
        }

        @NonNull
        @Override
        public String toString() {
            return String.valueOf(mId);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeSerializable(mFragmentClass);
            dest.writeInt(mDrawable);
        }

        public static final Creator<? extends NavigationFragment> CREATOR = new Creator<NavigationFragment>() {
            @Override
            public NavigationFragment createFromParcel(Parcel source) {
                return new NavigationFragment(source);
            }

            @Override
            public NavigationFragment[] newArray(int size) {
                return new NavigationFragment[0];
            }
        };
    }

}