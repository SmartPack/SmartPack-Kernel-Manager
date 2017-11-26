/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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
import com.grarak.kerneladiutor.utils.kernel.gpu.GPU;
import com.grarak.kerneladiutor.utils.kernel.io.IO;
import com.grarak.kerneladiutor.utils.kernel.ksm.KSM;
import com.grarak.kerneladiutor.utils.kernel.misc.Vibration;
import com.grarak.kerneladiutor.utils.kernel.screen.Screen;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.utils.kernel.thermal.Thermal;
import com.grarak.kerneladiutor.utils.kernel.wake.Wake;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

/**
 * Created by willi on 14.04.16.
 */
public class MainActivity extends BaseActivity {

    private TextView mRootAccess;
    private TextView mBusybox;
    private TextView mCollectInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't initialize analytics with debug build
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = (TextView) findViewById(R.id.root_access_text);
        mBusybox = (TextView) findViewById(R.id.busybox_text);
        mCollectInfo = (TextView) findViewById(R.id.info_collect_text);

        // Hide huge banner in landscape mode
        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            /**
             * Launch password activity when one is set,
             * otherwise run {@link CheckingTask}
             */
            String password;
            if (!(password = Prefs.getString("password", "", this)).isEmpty()) {
                Intent intent = new Intent(this, SecurityActivity.class);
                intent.putExtra(SecurityActivity.PASSWORD_INTENT, password);
                startActivityForResult(intent, 1);
            } else {
                new CheckingTask().execute();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * 0: License check result
         * 1: Password check result
         */
        if (requestCode == 0) {

            /*
             * -1: Default (no license check executed)
             *  0: License check was successful
             *  1: Something went wrong when checking license
             *  2: License is invalid
             *  3: Donate apk is patched/cracked
             */
            int result = data == null ? -1 : data.getIntExtra("result", -1);
            if (result == 0) {
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(
                            "com.grarak.kerneladiutordonate", 0);
                    Utils.writeFile(applicationInfo.dataDir + "/license",
                            Utils.encodeString(Utils.getAndroidId(this)), false, true);
                } catch (PackageManager.NameNotFoundException ignored) {
                }
            }
            launch(result);

        } else if (requestCode == 1) {

            /*
             * 0: Password is wrong
             * 1: Password is correct
             */
            if (resultCode == 1) {
                new CheckingTask().execute();
            } else {
                finish();
            }

        }
    }

    /**
     * Launch {@link NavigationActivity} which is the actual interface
     *
     * @param code license check result see {@link #onActivityResult(int, int, Intent)}
     */
    private void launch(int code) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("result", code);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        Prefs.saveInt("license", code, this);
        startActivity(intent);
        finish();
    }

    private class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private boolean mHasRoot;
        private boolean mHasBusybox;

        @Override
        protected Void doInBackground(Void... params) {
            // Check for root access
            mHasRoot = RootUtils.rootAccess();
            publishProgress(0);

            // If root is available continue
            if (mHasRoot) {
                // Check for busybox/toybox
                mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);

                // If busybox/toybox is available continue
                if (mHasBusybox) {
                    // Collect information for caching
                    collectData();
                    publishProgress(2);
                }
            }
            return null;
        }

        /**
         * Determinate what sections are supported
         */
        private void collectData() {
            Battery.supported(MainActivity.this);
            CPUBoost.supported();

            // Assign core ctl min cpu
            CPUFreq.sCoreCtlMinCpu = Prefs.getInt("core_ctl_min_cpus_big", 2, MainActivity.this);

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
                // Send SoC type to analytics to collect stats
                Answers.getInstance().logCustom(new CustomEvent("SoC")
                        .putCustomAttribute("type", Device.getBoard()));
            }
        }

        /**
         * Let the user know what we are doing right now
         *
         * @param values progress
         *               0: Checking root
         *               1: Checking busybox/toybox
         *               2: Collecting information
         */
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

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
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
                    // Send problem to analytics to collect stats
                    Answers.getInstance().logCustom(new CustomEvent("Can't access")
                            .putCustomAttribute("no_found", mHasRoot ? "no busybox" : "no root"));
                }
                return;
            }

            // Initialize Google Ads
            MobileAds.initialize(MainActivity.this, "ca-app-pub-1851546461606210~9501142287");

            // Execute another AsyncTask for license checking
            new AsyncTask<Void, Void, Boolean>() {

                private ApplicationInfo mApplicationInfo;
                private PackageInfo mPackageInfo;
                private boolean mPatched;
                private boolean mInternetAvailable;
                private boolean mLicensedCached;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    try {
                        mApplicationInfo = getPackageManager().getApplicationInfo(
                                "com.grarak.kerneladiutordonate", 0);
                        mPackageInfo = getPackageManager().getPackageInfo(
                                "com.grarak.kerneladiutordonate", 0);
                        if (BuildConfig.DEBUG) {
                            Utils.DONATED = false;
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    if (mApplicationInfo != null && mPackageInfo != null
                            && mPackageInfo.versionCode == 130) {
                        try {
                            mPatched = !Utils.checkMD5("5c7a92a5b2dcec409035e1114e815b00",
                                    new File(mApplicationInfo.publicSourceDir))
                                    || Utils.isPatched(mApplicationInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Utils.existFile(mApplicationInfo.dataDir + "/license")) {
                            String content = Utils.readFile(mApplicationInfo.dataDir + "/license");
                            if (!content.isEmpty() && (content = Utils.decodeString(content)) != null) {
                                if (content.equals(Utils.getAndroidId(MainActivity.this))) {
                                    mLicensedCached = true;
                                }
                            }
                        }

                        try {
                            if (!mLicensedCached) {
                                HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
                                urlConnection.setRequestProperty("User-Agent", "Test");
                                urlConnection.setRequestProperty("Connection", "close");
                                urlConnection.setConnectTimeout(3000);
                                urlConnection.connect();
                                mInternetAvailable = urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
                            }
                        } catch (IOException ignored) {
                        }

                        return !mPatched;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean donationValid) {
                    super.onPostExecute(donationValid);
                    if (donationValid && mLicensedCached) {
                        launch(0);
                    } else if (donationValid && mInternetAvailable) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.grarak.kerneladiutordonate",
                                "com.grarak.kerneladiutordonate.MainActivity"));
                        startActivityForResult(intent, 0);
                    } else if (donationValid) {
                        launch(1);
                    } else {
                        if (mPatched && !BuildConfig.DEBUG) {
                            Answers.getInstance().logCustom(new CustomEvent("Pirated")
                                    .putCustomAttribute("android_id", Utils.getAndroidId(MainActivity.this)));
                        }
                        launch(mPatched ? 3 : -1);
                    }
                }
            }.execute();
        }

    }

}
