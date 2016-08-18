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

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.MobileAds;
import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.database.tools.profiles.Profiles;
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
import com.grarak.kerneladiutor.services.profile.Tile;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.battery.Battery;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUBoost;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.cpu.MSMPerformance;
import com.grarak.kerneladiutor.utils.kernel.cpu.Temperature;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.Hotplug;
import com.grarak.kerneladiutor.utils.kernel.cpuhotplug.QcomBcl;
import com.grarak.kerneladiutor.utils.kernel.cpuvoltage.Voltage;
import com.grarak.kerneladiutor.utils.kernel.entropy.Entropy;
import com.grarak.kerneladiutor.utils.kernel.gpu.GPU;
import com.grarak.kerneladiutor.utils.kernel.io.IO;
import com.grarak.kerneladiutor.utils.kernel.ksm.KSM;
import com.grarak.kerneladiutor.utils.kernel.led.LED;
import com.grarak.kerneladiutor.utils.kernel.lmk.LMK;
import com.grarak.kerneladiutor.utils.kernel.misc.Vibration;
import com.grarak.kerneladiutor.utils.kernel.screen.Screen;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.utils.kernel.thermal.Thermal;
import com.grarak.kerneladiutor.utils.kernel.wake.Wake;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Backup;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by willi on 14.04.16.
 */
public class MainActivity extends BaseActivity {

    private TextView mRootAccess;
    private TextView mBusybox;
    private TextView mCollectInfo;
    private boolean mExecuting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = (TextView) findViewById(R.id.root_access_text);
        mBusybox = (TextView) findViewById(R.id.busybox_text);
        mCollectInfo = (TextView) findViewById(R.id.info_collect_text);

        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            String password;
            if (!(password = Prefs.getString("password", "", this)).isEmpty()) {
                Intent intent = new Intent(this, SecurityActivity.class);
                intent.putExtra(SecurityActivity.PASSWORD_INTENT, password);
                startActivityForResult(intent, 2);
            } else {
                execute();
            }
        } else {
            mExecuting = savedInstanceState.getBoolean("executing");
        }
    }

    private void execute() {
        if (!mExecuting) {
            new CheckingTask().execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("executing", mExecuting);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            launch(data == null ? -1 : data.getIntExtra("result", -1));
        } else if (requestCode == 1) {
            launch(0);
        } else if (requestCode == 2) {
            if (resultCode == 1) {
                execute();
            } else {
                finish();
            }
        }
    }

    private void launch(int code) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("result", code);
        Prefs.saveInt("license", code, this);
        startActivity(intent);
        finish();
    }

    private class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private boolean mHasRoot;
        private boolean mHasBusybox;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mExecuting = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mHasRoot = RootUtils.rootAccess();
            publishProgress(0);
            if (mHasRoot) {
                mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);
                if (mHasBusybox) {
                    collectData();
                    publishProgress(2);
                }
            }
            return null;
        }

        // For caching
        private void collectData() {
            Battery.supported(MainActivity.this);
            CPUBoost.supported();
            CPUFreq.sCoreCtlMinCpu = Prefs.getInt("core_ctl_min_cpus_big",
                    CPUFreq.getCpuCount() == 6 ? 2 : 0, MainActivity.this);
            Device.CPUInfo.load();
            Device.Input.supported();
            Device.MemInfo.load();
            Device.ROMInfo.load();
            Device.TrustZone.supported();
            GPU.supported();
            Hotplug.supported();
            IO.supported();
            KSM.supported();
            MSMPerformance.supported();
            QcomBcl.supported();
            Screen.supported();
            Sound.supported();
            Temperature.supported(MainActivity.this);
            Thermal.supported();
            Tile.publishProfileTile(new Profiles(MainActivity.this).getAllProfiles(), MainActivity.this);
            Vibration.supported();
            Voltage.supported();
            Wake.supported();

            if (!BuildConfig.DEBUG) {
                Answers.getInstance().logCustom(new CustomEvent("SoC")
                        .putCustomAttribute("type", Device.getBoard()));
            }

            LinkedHashMap<Integer, Fragment> sFragments = NavigationActivity.sFragments;
            sFragments.put(R.string.statistics, null);
            sFragments.put(R.string.overall, new OverallFragment());
            sFragments.put(R.string.device, new DeviceFragment());
            if (Device.MemInfo.getItems().size() > 0) {
                sFragments.put(R.string.memory, new MemoryFragment());
            }
            sFragments.put(R.string.inputs, new InputsFragment());
            sFragments.put(R.string.kernel, null);
            if (!Utils.isEmulator()) {
                sFragments.put(R.string.cpu, new CPUFragment());
            }
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
            SupportedDownloads support = new SupportedDownloads(MainActivity.this);
            if (support.getLink() != null) {
                sFragments.put(R.string.downloads, DownloadsFragment.newInstance(support));
            }
            if (Backup.hasBackup()) {
                sFragments.put(R.string.backup, new BackupFragment());
            }
            sFragments.put(R.string.build_prop_editor, new BuildpropFragment());
            sFragments.put(R.string.profile, new ProfileFragment());
            sFragments.put(R.string.recovery, new RecoveryFragment());
            sFragments.put(R.string.initd, new InitdFragment());
            sFragments.put(R.string.on_boot, new OnBootFragment());
            sFragments.put(R.string.other, null);
            sFragments.put(R.string.settings, null);
            sFragments.put(R.string.about, new AboutFragment());
            sFragments.put(R.string.contributors, new ContributorsFragment());
            sFragments.put(R.string.help, new HelpFragment());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int red = ContextCompat.getColor(MainActivity.this, R.color.red);
            int green = ContextCompat.getColor(MainActivity.this, R.color.green);
            switch (values[0]) {
                case 0:
                    mRootAccess.setTextColor(mHasRoot ? green : red);
                    break;
                case 1:
                    mBusybox.setTextColor(mHasBusybox ? green : red);
                    break;
                case 2:
                    mCollectInfo.setTextColor(green);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!mHasRoot || !mHasBusybox) {
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, getString(mHasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        mHasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                startActivity(intent);
                finish();

                if (!BuildConfig.DEBUG) {
                    Answers.getInstance().logCustom(new CustomEvent("Can't access")
                            .putCustomAttribute("no_found", mHasRoot ? "no busybox" : "no root"));
                }
                return;
            }

            MobileAds.initialize(MainActivity.this, "ca-app-pub-1851546461606210~9501142287");
            new AsyncTask<Void, Void, Boolean>() {

                private ApplicationInfo mApplicationInfo;
                private PackageInfo mPackageInfo;
                private boolean mPatched;
                private boolean mInternetAvailable;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    try {
                        mApplicationInfo = getPackageManager().getApplicationInfo(
                                "com.grarak.kerneladiutordonate", 0);
                        mPackageInfo = getPackageManager().getPackageInfo(
                                "com.grarak.kerneladiutordonate", 0);
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    if (mApplicationInfo != null && mPackageInfo != null && mPackageInfo.versionCode == 130) {
                        mPatched = !Utils.checkMD5("5c7a92a5b2dcec409035e1114e815b00",
                                new File(mApplicationInfo.publicSourceDir));

                        try {
                            HttpURLConnection urlc = (HttpURLConnection)
                                    (new URL("http://clients3.google.com/generate_204")
                                            .openConnection());
                            urlc.setRequestProperty("User-Agent", "Android");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(1500);
                            urlc.connect();
                            mInternetAvailable = (urlc.getResponseCode() == 204 &&
                                    urlc.getContentLength() == 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mApplicationInfo != null && mPackageInfo != null && mPackageInfo.versionCode == 130
                            && !mPatched;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (aBoolean) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.grarak.kerneladiutordonate",
                                "com.grarak.kerneladiutordonate.MainActivity"));
                        startActivityForResult(intent, mInternetAvailable ? 0 : 1);
                    } else {
                        launch(mPatched ? 3 : -1);
                    }
                    mExecuting = false;
                }
            }.execute();
        }

    }

}
