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

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.smartpack.kernelmanager.activities.FlashingActivity;
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    public static String getLogFolderPath() {
        return new File(Environment.getExternalStorageDirectory(), "SP/logs").getAbsolutePath();
    }

    public static void prepareFolder(String path) {
        File file = SuFile.open(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static long fileSize(File file) {
        return file.length();
    }

    public static void flashingTask(File file, Context context) {
        new AsyncTasks() {
            @Override
            public void onPreExecute() {
                Common.isFlashing(true);
                Common.setZipName(file.getName());
                Common.getFlashingResult().setLength(0);
                Common.getFlashingOutput().clear();
                Intent flashingIntent = new Intent(context, FlashingActivity.class);
                context.startActivity(flashingIntent);
            }
            @Override
            public void doInBackground() {
                Common.getFlashingResult().append("** Preparing to flash ").append(file.getName()).append("...\n\n");
                Common.getFlashingResult().append("** Path: '").append(file.toString()).append("'\n\n");
                Utils.delete(context.getCacheDir() + "/flash.zip");
                Common.getFlashingResult().append("** Copying '").append(file.getName()).append("' into temporary folder: ");
                Common.getFlashingResult().append(RootUtils.runAndGetError("cp '" + file.toString() + "' " + context.getCacheDir() + "/flash.zip"));
                Common.getFlashingResult().append(Utils.existFile(context.getCacheDir() + "/flash.zip") ? "Done *\n\n" : "\n\n");
                manualFlash(context);
            }
            @Override
            public void onPostExecute() {
                Common.isFlashing(false);
            }
        }.execute();
    }

    private static void manualFlash(Context context) {
        /*
         * Flashing recovery zip without rebooting to custom recovery (Credits to osm0sis @ xda-developers.com)
         * Also include code from https://github.com/topjohnwu/Magisk/
         * Ref: https://github.com/topjohnwu/Magisk/blob/a848f10bba4f840248ecf314f7c9d55511d05a0f/app/src/main/java/com/topjohnwu/magisk/core/tasks/FlashZip.kt#L47
         */
        String mScriptPath = Utils.getInternalDataStorage(context) + "/flash/META-INF/com/google/android/update-binary",
                FLASH_FOLDER = Utils.getInternalDataStorage(context) + "/flash",
                CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'",
                mZipPath = context.getCacheDir() + "/flash.zip";
        String flashingCommand = "BOOTMODE=true sh " + mScriptPath + " dummy 1 " + mZipPath + " 2>/dev/null && echo success";
        if (Utils.existFile(FLASH_FOLDER)) {
            RootUtils.runCommand(CLEANING_COMMAND);
        } else {
            prepareFolder(FLASH_FOLDER);
        }
        Common.getFlashingResult().append("** Extracting ").append(Common.getZipName()).append(" into working folder: ");
        RootUtils.runAndGetError((Utils.isMagiskBinaryExist("unzip") ? Utils.magiskBusyBox() + " unzip " : "unzip ") + mZipPath + " -d '" + FLASH_FOLDER + "'");
        if (Utils.existFile(mScriptPath)) {
            Common.getFlashingResult().append(" Done *\n\n");
            Common.getFlashingResult().append("** Checking recovery zip file: ");
            if (Utils.readFile(mScriptPath.replace("update-binary","updater-script")).equals("#MAGISK")) {
                Common.getFlashingResult().append(" Magisk Module *\n\n");
                Common.isMagiskModule(true);
            } else if (Utils.existFile(Utils.getInternalDataStorage(context) + "/flash/anykernel.sh")) {
                Common.getFlashingResult().append(" AnyKernel *\n\n");
            } else {
                Common.getFlashingResult().append(" Unknown *\n\n");
            }
            Common.getFlashingResult().append("** Preparing a recovery-like environment for flashing...\n\n");
            RootUtils.runCommand("cd '" + FLASH_FOLDER + "'");
            if (!Common.isMagiskModule()) {
                Common.getFlashingResult().append("** Mounting Root filesystem: ");
                if (!RootUtils.isWritableRoot()) {
                    Common.isWritableRoot(false);
                    Common.getFlashingResult().append("Failed *\nPlease Note: Flashing may not work properly on this device!\n\n");
                } else {
                    Common.getFlashingResult().append("Done *\n\n");
                    Common.getFlashingResult().append(RootUtils.runAndGetError(Utils.isMagiskBinaryExist("mkdir") ? Utils.magiskBusyBox() + " mkdir /tmp" : "mkdir /tmp")).append(" \n");
                    Common.getFlashingResult().append(RootUtils.runAndGetError(Utils.isMagiskBinaryExist("mke2fs") ? Utils.magiskBusyBox() + " mke2fs -F tmp.ext4 500000" : "mke2fs -F tmp.ext4 500000")).append(" \n");
                    Common.getFlashingResult().append(RootUtils.runAndGetError(Utils.isMagiskBinaryExist("mount") ? Utils.magiskBusyBox() + " mount -o loop tmp.ext4 /tmp/" : "mount -o loop tmp.ext4 /tmp/")).append(" \n\n");
                }
            }
            Common.getFlashingResult().append("** Flashing ").append(Common.getZipName()).append(" ...\n\n");
            RootUtils.runAndGetLiveOutput(flashingCommand, Common.getFlashingOutput());
            Common.getFlashingResult().append(Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ? Utils.getOutput(Common.getFlashingOutput()).replace("\nsuccess","") :
                    "Unfortunately, flashing " + Common.getZipName() + " is failed!");
        } else {
            Common.getFlashingResult().append(" Failed *\n\n");
            Common.getFlashingResult().append("** Flashing Failed *");
        }
        RootUtils.runCommand(CLEANING_COMMAND);
        Utils.delete(context.getCacheDir() + "/flash.zip");
        if (!Common.isMagiskModule() && Common.isWritableRoot()) {
            Common.getFlashingResult().append("\n\n** Unmount Root filesystem: ");
            RootUtils.mount("ro", "/");
            Common.getFlashingResult().append(" Done *");
        }
        if (Common.isMagiskModule()) {
            Common.isMagiskModule(false);
        }
    }

}