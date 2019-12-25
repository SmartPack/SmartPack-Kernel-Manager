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

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    private static String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
    private static String CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'";

    public static boolean hasRecovery() {
        return Utils.existFile("/cache/recovery/");
    }

    private static boolean hasPathLog() {
        return Utils.existFile(Utils.getInternalDataStorage() + "/last_flash.txt");
    }

    private static boolean hasFlashLog() {
        return Utils.existFile(Utils.getInternalDataStorage() + "/flasher_log.txt");
    }

    public static void cleanLogs() {
        if (hasPathLog()) {
            RootUtils.runCommand("rm -r " + Utils.getInternalDataStorage() + "/last_flash.txt");
        }
        if (hasFlashLog()) {
            RootUtils.runCommand("rm -r " + Utils.getInternalDataStorage() + "/flasher_log.txt");
        }
    }

    public static void prepareLogFolder() {
        File logPath = new File(Utils.getInternalDataStorage() + "/logs");
        if (logPath.exists() && logPath.isFile()) {
            logPath.delete();
        }
        logPath.mkdirs();
    }

    public static long fileSize(File file) {
        return file.length();
    }

    /*
     * Flashing recovery zip without rebooting to custom recovery
     * Credits to osm0sis @ xda-developers.com
     */
    public static void prepareManualFlash(File file) {
        String path = file.toString();
        if (Utils.existFile(FLASH_FOLDER)) {
            RootUtils.runCommand(CLEANING_COMMAND);
        }
        File flashPath = new File(FLASH_FOLDER);
        if (flashPath.exists() && flashPath.isFile()) {
            flashPath.delete();
        }
        flashPath.mkdirs();
        RootUtils.runCommand("unzip '" + path + "' -d '" + FLASH_FOLDER + "'");
        if (Utils.existFile(Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary")) {
            RootUtils.runCommand("cd '" + FLASH_FOLDER + "' && mount -o remount,rw / && mkdir /tmp");
            RootUtils.runCommand("mke2fs -F tmp.ext4 500000 && mount -o loop tmp.ext4 /tmp/");
        }
    }

    public static String manualFlash(File file) {
        FileDescriptor fd = new FileDescriptor();
        String RECOVERY_API = "3";
        String path = file.toString();
        String date = RootUtils.runCommand("date");
        String flashingCommnd = "sh META-INF/com/google/android/update-binary '" + RECOVERY_API + "' " + fd + " '" + path + "'| tee '" + Utils.getInternalDataStorage() + "'/flasher_log.txt";
        String logTime = "echo '" + date + "' >> '" + Utils.getInternalDataStorage() + "'/flasher_history.txt";
        String logPath = "echo -- '" + path + "' >> '" + Utils.getInternalDataStorage() + "'/flasher_history.txt";
        String logEmptyLine = "echo ' ' >> '" + Utils.getInternalDataStorage() + "'/flasher_history.txt";
        return RootUtils.runCommand(flashingCommnd + " && " + logTime + " && " + logPath + " && " + logEmptyLine + " && " + CLEANING_COMMAND);
    }

}