/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.utils;

import android.os.Build;
import android.os.Environment;

import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    private static final String SMARTPACK_VERSION = "/version";

    private static final String SMARTPACK_DOWNLOADED = Environment.getExternalStorageDirectory().toString() + "/Download/SmartPack-Kernel.zip";

    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";

    private static final String LATEST_VERSION = Environment.getDataDirectory() + "/version";

    private static final String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";

    private static final String RECOVERY = "/cache/recovery/";

    public enum FLASHMENU {
        FLASH
    }

    /**
     * Check SmartPack-Kernel is installed
     */
    public static boolean hasSmartPackInstalled() {
        return Utils.readFile("/proc/version").contains("SmartPack-Kernel");
    }

    public static boolean hasSmartPackVersion() {
        return Utils.existFile(SMARTPACK_VERSION);
    }

    /**
     * Check device support
     */
    public static boolean isOnePlusmsm8998() {
        return Device.getFingerprint().contains("OnePlus5");
    }

    /**
     * Check for Oxygen OS
     */
    public static boolean isOnePlusOOS() {
        return Utils.hasProp("ro.oxygen.version");
    }

    /**
     * Get current SmartPack version
     */
    public static String getSmartPackVersion() {
        return Utils.readFile(SMARTPACK_VERSION);
    }

    public static int getSmartPackVersionNumber() {
        return Utils.strToInt(Utils.readFile(SMARTPACK_VERSION).replace("stable", "").replace("beta", "").replace("alpha", "").replace("test", "").replace("-", "").replace("v", ""));
    }

    /**
     * Get latest SmartPack version
     */
    public static String getlatestSmartPackVersion() {
        return Utils.readFile(LATEST_VERSION);
    }

    public static boolean SmartPackRelease() {
        return getlatestSmartPackVersion().contains("alpha-v") || getlatestSmartPackVersion().contains("beta-v") || getlatestSmartPackVersion().contains("stable-v") || getlatestSmartPackVersion().contains("test");
    }

    public static int getlatestSmartPackVersionNumber() {
        return Utils.strToInt(Utils.readFile(LATEST_VERSION).replace("stable", "").replace("beta", "").replace("alpha", "").replace("test", "").replace("-", "").replace("v", ""));
    }

    public static boolean haslatestSmartPackVersion() {
        return Utils.existFile(LATEST_VERSION);
    }

    public static boolean isSmartPackDownloaded() {
        return Utils.existFile(SMARTPACK_DOWNLOADED);
    }

    public static boolean isZIPFileExtracted() {
        return Utils.existFile(ZIPFILE_EXTRACTED);
    }

    public static boolean hasFlashFolder() {
        return Utils.existFile(FLASH_FOLDER);
    }

    public static boolean hasRecovery() {
        return Utils.existFile(RECOVERY);
    }

    public static void deleteVersionInfo() {
        RootUtils.runCommand("rm -r " + LATEST_VERSION );
    }

    public static void getVersionInfo() {
        if (isOnePlusmsm8998() && Build.VERSION.SDK_INT == 27) {
            RootUtils.runCommand("curl -L -o " + LATEST_VERSION + " https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Oreo/anykernel_SmartPack/ramdisk/version?raw=true");
        } else if (isOnePlusmsm8998() && Build.VERSION.SDK_INT == 28) {
            RootUtils.runCommand("curl -L -o " + LATEST_VERSION + " https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Pie/anykernel_SmartPack/ramdisk/version?raw=true");
        }
    }

    public static void deleteLatestKernel() {
        RootUtils.runCommand("rm -rf " + SMARTPACK_DOWNLOADED);
    }

    public static void getLatestKernel() {
        if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 27)) {
            RootUtils.runCommand("curl -L -o " + SMARTPACK_DOWNLOADED + " https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Oreo/kernel-release/SmartPack-Kernel-dumpling.zip?raw=true");
        } else if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 28)) {
            RootUtils.runCommand("curl -L -o " + SMARTPACK_DOWNLOADED + " https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Pie/kernel-release/SmartPack-Kernel-dumpling.zip?raw=true");
        }
    }

    public static void makeInternalStorageFolder() {
        File file = new File(Utils.getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static void makeFlashFolder() {
        RootUtils.runCommand("mkdir " + FLASH_FOLDER);
    }

    public static void extractLatestKernel() {
        RootUtils.runCommand("unzip " + SMARTPACK_DOWNLOADED + " -d " + FLASH_FOLDER);
    }

    public static void cleanFlashFolder() {
        RootUtils.runCommand("rm -r " + FLASH_FOLDER + "/*");
    }

    public static void manualFlash(File file) {
        String path = file.toString();
        String flashFolder = Utils.getInternalDataStorage() + "/flash";
        String RECOVERY_API = "3";
        String CleanUpCommand = "rm -r '" + flashFolder + "'/*";
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        // Check and create, if necessary, internal storage folder
        makeInternalStorageFolder();
        if (Utils.existFile(flashFolder)) {
            RootUtils.runCommand(CleanUpCommand);
        } else {
            RootUtils.runCommand("mkdir '" + flashFolder + "'");
        }
        RootUtils.runCommand("unzip '" + path + "' -d '" + flashFolder + "'");
        if (isZIPFileExtracted()) {
            RootUtils.runCommand("cd '" + flashFolder + "' && mount -o remount,rw / && mkdir /tmp");
            RootUtils.runCommand("mke2fs -F tmp.ext4 250000 && mount -o loop tmp.ext4 /tmp/");
            RootUtils.runCommand("sh META-INF/com/google/android/update-binary '" + RECOVERY_API + "' 1 '" + path + "'");
            RootUtils.runCommand(CleanUpCommand);
        }
    }

    public static boolean supported() {
        return isOnePlusmsm8998() && isOnePlusOOS();
    }
}
