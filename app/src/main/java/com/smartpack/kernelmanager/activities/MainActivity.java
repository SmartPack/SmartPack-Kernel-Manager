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
package com.smartpack.kernelmanager.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.services.profile.Tile;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.kernel.battery.Battery;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.cpu.MSMPerformance;
import com.smartpack.kernelmanager.utils.kernel.cpu.Temperature;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUBoost;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.Hotplug;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.QcomBcl;
import com.smartpack.kernelmanager.utils.kernel.cpuvoltage.Voltage;
import com.smartpack.kernelmanager.utils.kernel.gpu.GPU;
import com.smartpack.kernelmanager.utils.kernel.io.IO;
import com.smartpack.kernelmanager.utils.kernel.ksm.KSM;
import com.smartpack.kernelmanager.utils.kernel.misc.Vibration;
import com.smartpack.kernelmanager.utils.kernel.screen.Screen;
import com.smartpack.kernelmanager.utils.kernel.sound.Sound;
import com.smartpack.kernelmanager.utils.kernel.thermal.Thermal;
import com.smartpack.kernelmanager.utils.kernel.wake.Wake;
import com.smartpack.kernelmanager.utils.kernel.wakelock.Wakelocks;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.UpdateCheck;

import org.frap129.spectrum.Spectrum;

import java.lang.ref.WeakReference;

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

        // Initialize Google Ads
        if (Prefs.getBoolean("allow_ads", true, this)) {
            MobileAds.initialize(this, "ca-app-pub-7791710838910455~4931988555");
        }

        /*
         * Initialize Spectrum Profiles & Wakelock Blocker
         */
        if (RootUtils.rootAccess()) {
            if (Spectrum.supported()) {
                int Profile = Utils.strToInt(Spectrum.getProfile());
                Prefs.saveInt("spectrum_profile", Profile, this);
            }
            if (Wakelocks.boefflawlsupported()) {
                Wakelocks.CopyWakelockBlockerDefault();
            }
        }

        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = findViewById(R.id.root_access_text);
        mBusybox = findViewById(R.id.busybox_text);
        mCollectInfo = findViewById(R.id.info_collect_text);

        /*
         * Hide huge banner in landscape mode
         */
        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            /*
             * Launch password activity when one is set,
             * otherwise run {@link CheckingTask}
             */
            String password;
            if (!(password = Prefs.getString("password", "", this)).isEmpty()) {
                Intent intent = new Intent(this, SecurityActivity.class);
                intent.putExtra(SecurityActivity.PASSWORD_INTENT, password);
                startActivityForResult(intent, 0);
            } else {
                new CheckingTask(this).execute();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == 1) {
                new CheckingTask(this).execute();
            } else {
                finish();
            }
        }
    }

    private void launch() {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private static class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private WeakReference<MainActivity> mRefActivity;

        private CheckingTask(MainActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Utils.mHasRoot = RootUtils.rootAccess();
            publishProgress(0);

            if (Utils.mHasRoot) {
                Utils.mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);

                if (Utils.mHasBusybox) {
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
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            Battery.getInstance(activity);
            CPUBoost.getInstance();

            // Assign core ctl min cpu
            CPUFreq.getInstance(activity);

            Device.CPUInfo.getInstance();
            Device.Input.getInstance();
            Device.MemInfo.getInstance();
            Device.ROMInfo.getInstance();
            Device.TrustZone.getInstance();
            GPU.supported();
            Hotplug.supported();
            IO.getInstance();
            KSM.getInstance();
            MSMPerformance.getInstance();
            QcomBcl.supported();
            Screen.supported();
            Sound.getInstance();
            Temperature.getInstance(activity);
            Thermal.supported();
            Tile.publishProfileTile(new Profiles(activity).getAllProfiles(), activity);
            Vibration.getInstance();
            Voltage.getInstance();
            Wake.supported();

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
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            int red = ContextCompat.getColor(activity, R.color.red);
            int green = ContextCompat.getColor(activity, R.color.green);
            switch (values[0]) {
                case 0:
                    activity.mRootAccess.setTextColor(Utils.mHasRoot ? green : red);
                    break;
                case 1:
                    activity.mBusybox.setTextColor(Utils.mHasBusybox ? green : red);
                    break;
                case 2:
                    activity.mCollectInfo.setTextColor(green);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
            if (!Utils.mHasRoot || !Utils.mHasBusybox) {
                Intent noRoot = new Intent(activity, NoRootActivity.class);
                activity.startActivity(noRoot);
                activity.finish();
                return;
            }

            activity.launch();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        // Initialize auto app update check
        if (Utils.isPlayStoreInstalled(this)) {
            return;
        }
        if (!Utils.isDownloadBinaries()) {
            return;
        }
        if (Utils.isNetworkAvailable(this) && Prefs.getBoolean("auto_update", true, this)) {
            if (!UpdateCheck.hasVersionInfo() || (UpdateCheck.lastModified() + 3720000L < System.currentTimeMillis())) {
                UpdateCheck.getVersionInfo(this);
            }
            if (UpdateCheck.hasVersionInfo() && BuildConfig.VERSION_CODE < UpdateCheck.versionCode()) {
                UpdateCheck.updateAvailableDialog(this);
            }
        }
    }

}