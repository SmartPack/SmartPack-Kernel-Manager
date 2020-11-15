/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.utils.tools;

import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    private static final String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
    private static final String CLEANING_COMMAND = (Utils.isMagiskBinaryExist("rm") ? Utils.magiskBusyBox() + " rm -r '" : "rm -r '") + FLASH_FOLDER + "'";
    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";
    public static String mZipName;
    public static String mFlashingOutput = null;

    public static StringBuilder mFlashingResult = null;

    public static boolean mFlashing = false;
    public static boolean mDebugMode = false;
    private static boolean mWritableRoot = true;

    public static void prepareFolder(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static long fileSize(File file) {
        return file.length();
    }

    public static void manualFlash() {
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        FileDescriptor fd = new FileDescriptor();
        int RECOVERY_API = 3;
        String path = "/data/local/tmp/flash.zip";
        String flashingCommand = "sh '" + ZIPFILE_EXTRACTED + "' '" + RECOVERY_API + "' '" + fd + "' '" + path + "'";
        if (Utils.existFile(FLASH_FOLDER)) {
            RootUtils.runCommand(CLEANING_COMMAND);
        } else {
            prepareFolder(FLASH_FOLDER);
        }
        mFlashingResult.append("** Extracting ").append(mZipName).append(" into working folder: ");
        RootUtils.runAndGetError((Utils.isMagiskBinaryExist("unzip") ? Utils.magiskBusyBox() + " unzip " : "unzip ") + path + " -d '" + FLASH_FOLDER + "'");
        if (Utils.existFile(ZIPFILE_EXTRACTED)) {
            mFlashingResult.append(" Done *\n\n");
            mFlashingResult.append("** Preparing a recovery-like environment for flashing...\n\n");
            RootUtils.runCommand("cd '" + FLASH_FOLDER + "'");
            mFlashingResult.append("** Mounting Root filesystem: ");
            if (!RootUtils.isWritableRoot()) {
                mWritableRoot = false;
                mFlashingResult.append("Failed *\nPlease Note: Flashing may not work properly on this device!\n\n");
            } else {
                mFlashingResult.append("Done *\n\n");
                mFlashingResult.append(RootUtils.runAndGetError(Utils.isMagiskBinaryExist("mkdir") ? Utils.magiskBusyBox() + " mkdir /tmp" : "mkdir /tmp")).append(" \n");
                mFlashingResult.append(RootUtils.runAndGetError(Utils.isMagiskBinaryExist("mke2fs") ? Utils.magiskBusyBox() + " mke2fs -F tmp.ext4 500000" : "mke2fs -F tmp.ext4 500000")).append(" \n");
                mFlashingResult.append(RootUtils.runAndGetError(Utils.isMagiskBinaryExist("mount") ? Utils.magiskBusyBox() + " mount -o loop tmp.ext4 /tmp/" : "mount -o loop tmp.ext4 /tmp/")).append(" \n\n");
            }
            mFlashingResult.append("** Flashing ").append(mZipName).append(" ...\n\n");
            mFlashingOutput = mDebugMode ? RootUtils.runAndGetError(flashingCommand) : RootUtils.runAndGetOutput(flashingCommand);
            mFlashingResult.append(mFlashingOutput.isEmpty() ? "Unfortunately, flashing " + mZipName + " failed due to some unknown reasons!" : mFlashingOutput);
        } else {
            mFlashingResult.append(" Failed *\n\n");
            mFlashingResult.append("** Flashing Failed *");
        }
        RootUtils.runCommand(CLEANING_COMMAND);
        Utils.delete("/data/local/tmp/flash.zip");
        if (mWritableRoot) RootUtils.mount("ro", "/");
    }

}