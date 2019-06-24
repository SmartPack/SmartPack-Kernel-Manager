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
import java.io.FileDescriptor;

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
    private static final String RECOVERY_API = "3";

    private static final FileDescriptor fd = new FileDescriptor();

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

    public static boolean hasRecovery() {
        return Utils.existFile(RECOVERY);
    }

    public static void deleteVersionInfo() {
        if (haslatestSmartPackVersion()) {
            RootUtils.runCommand("rm -r " + LATEST_VERSION);
        }
    }

    public static void getVersionInfo() {
        if (isOnePlusmsm8998() && Build.VERSION.SDK_INT == 27) {
            RootUtils.runCommand("curl -L -o " + LATEST_VERSION + " https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Oreo/anykernel_SmartPack/ramdisk/version?raw=true");
        } else if (isOnePlusmsm8998() && Build.VERSION.SDK_INT == 28) {
            RootUtils.runCommand("curl -L -o " + LATEST_VERSION + " https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Pie/anykernel_SmartPack/ramdisk/version?raw=true");
        }
    }

    public static void deleteLatestKernel() {
        if (isSmartPackDownloaded()) {
            RootUtils.runCommand("rm -rf " + SMARTPACK_DOWNLOADED);
        }
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
        File flashFolderPath = new File(FLASH_FOLDER);
        if (flashFolderPath.exists() && flashFolderPath.isFile()) {
            flashFolderPath.delete();
        }
        flashFolderPath.mkdirs();
    }

    public static boolean isPathLog() {
        return Utils.existFile(Utils.getInternalDataStorage() + "/last_flash.txt");
    }

    public static boolean isFlashLog() {
        return Utils.existFile(Utils.getInternalDataStorage() + "/flasher_log.txt");
    }

    public static void cleanLogs() {
        File PathLog = new File(Utils.getInternalDataStorage() + "/last_flash.txt");
        File FlashLog = new File(Utils.getInternalDataStorage() + "/flasher_log.txt");
        if (isPathLog()) {
            PathLog.delete();
        }
        if (isFlashLog()) {
            FlashLog.delete();
        }
    }

    public static void cleanFlashFolder() {
        if (!FLASH_FOLDER.isEmpty()) {
            RootUtils.runCommand("rm -r " + FLASH_FOLDER + "/*");
        }
    }

    public static void extractLatestKernel(String path) {
        RootUtils.runCommand("unzip " + path + " -d " + FLASH_FOLDER);
    }

    public static void flashCommand(String path) {
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        RootUtils.runCommand("cd '" + FLASH_FOLDER + "' && mount -o remount,rw / && mkdir /tmp");
        RootUtils.runCommand("mke2fs -F tmp.ext4 250000 && mount -o loop tmp.ext4 /tmp/");
        RootUtils.runCommand("sh META-INF/com/google/android/update-binary '" + RECOVERY_API + "' " + fd + " '" + path + "'| tee '" + Utils.getInternalDataStorage() + "'/flasher_log.txt");
        cleanFlashFolder();
    }

    public static void prepareFlashFolder() {
        // Check and create, if necessary, internal storage folder
        makeInternalStorageFolder();
        if (Utils.existFile(FLASH_FOLDER)) {
            cleanFlashFolder();
        } else {
            makeFlashFolder();
        }
    }

    public static void autoFlash() {
        cleanLogs();
        String path = SMARTPACK_DOWNLOADED;
        RootUtils.runCommand("echo '" + path + "' > " + Utils.getInternalDataStorage() + "/last_flash.txt");
        flashCommand(path);
    }

    public static void manualFlash(File file) {
        String path = file.toString();
        prepareFlashFolder();
        extractLatestKernel(path);
        if (isZIPFileExtracted()) {
            flashCommand(path);
        }
    }

    public static long fileSize(File file) {
        return file.length();
    }

    public static void recoveryFlash() {
        String path = SMARTPACK_DOWNLOADED;
        String RecoveryFlashCommand = "echo --update_package=/" + path + " > /cache/recovery/command";
        RootUtils.runCommand(RecoveryFlashCommand);
    }

    public static boolean supported() {
        return isOnePlusmsm8998() && isOnePlusOOS();
    }
}
