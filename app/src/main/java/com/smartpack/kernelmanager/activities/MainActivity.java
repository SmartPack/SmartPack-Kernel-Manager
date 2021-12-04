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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.services.profile.Tile;
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
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
import com.smartpack.kernelmanager.utils.tools.KernelUpdater;
import com.smartpack.kernelmanager.utils.tools.UpdateCheck;

import org.frap129.spectrum.Spectrum;

import java.lang.ref.WeakReference;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;

/**
 * Created by willi on 14.04.16.
 */
public class MainActivity extends BaseActivity {

    private MaterialTextView mBusybox, mCollectInfo, mRootAccess, mUpdateInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force English Language, if applicable
        if (Prefs.getBoolean("forceenglish", false, this)) {
            Utils.setLanguage("en_US", this);
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
        mUpdateInfo = findViewById(R.id.info_update);

        /*
         * Hide huge banner in landscape mode
         */
        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            /*
             * Launch biometric authentication dialogue if enabled by user,
             * otherwise run {@link CheckingTask}
             */
            if (Prefs.getBoolean("use_biometric", false, this)) {
                Intent intent = new Intent(this, SecurityActivity.class);
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

    private static class CheckingTask extends sExecutor {

        private final WeakReference<MainActivity> mRefActivity;

        private CheckingTask(MainActivity activity) {
            mRefActivity = new WeakReference<>(activity);
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

        private void publishProgress(Integer... values) {
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            int red = Color.RED;
            int accent = ViewUtils.getThemeAccentColor(activity);
            switch (values[0]) {
                case 0:
                    new Handler(Looper.getMainLooper()).post(() -> activity.mRootAccess.setTextColor(Common.hasRoot() ? accent : red));
                    break;
                case 1:
                    new Handler(Looper.getMainLooper()).post(() -> activity.mBusybox.setTextColor(Common.hasBusyBox() ? accent : red));
                    break;
                case 2:
                    new Handler(Looper.getMainLooper()).post(() -> activity.mCollectInfo.setTextColor(accent));
                    break;
                case 3:
                    new Handler(Looper.getMainLooper()).post(() -> activity.mUpdateInfo.setTextColor(accent));
                    break;
            }
        }

        /**
         * Let the user know what we are doing right now
         *
         * @param values progress
         *               0: Checking root
         *               1: Checking busybox/toybox
         *               2: Collecting information
         *               3: Check for updates
         */

        @Override
        public void onPreExecute() {

        }

        @Override
        public void doInBackground() {
            Common.hasRoot(RootUtils.rootAccess());
            publishProgress(0);

            if (Common.hasRoot()) {
                Common.hasBusyBox(RootUtils.busyboxInstalled());
                publishProgress(1);

                if (Common.hasBusyBox()) {
                    collectData();
                    publishProgress(2);
                }

                // Initialize auto app update check
                if (Common.isUpdateCheckEnabled()) {
                    publishProgress(3);
                }
            }
        }

        @Override
        public void onPostExecute() {
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
            if (!Common.hasRoot() || !Common.hasBusyBox()) {
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
        if (UpdateCheck.isUpdateTime(this)) {
            Common.enableUpdateCheck(true);
            mUpdateInfo.setVisibility(View.VISIBLE);
        }

        if (KernelUpdater.isUpdateTime(this)) {
            mUpdateInfo.setVisibility(View.VISIBLE);
        }
    }

}