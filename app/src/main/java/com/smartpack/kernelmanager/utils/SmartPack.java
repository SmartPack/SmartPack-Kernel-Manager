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

    public static boolean hasRecovery() {
        return Utils.existFile("/cache/recovery/");
    }

    public static boolean hasPathLog() {
        return Utils.existFile(Utils.getInternalDataStorage() + "/last_flash.txt");
    }

    public static boolean hasFlashLog() {
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

    public static void manualFlash(File file) {
        FileDescriptor fd = new FileDescriptor();
        String path = file.toString();
        String flashFolder = Utils.getInternalDataStorage() + "/flash";
        String RECOVERY_API = "3";
        String CleanUpCommand = "rm -r '" + flashFolder + "'";
        if (Utils.existFile(flashFolder)) {
            RootUtils.runCommand(CleanUpCommand);
        }
        RootUtils.runCommand("mkdir " + flashFolder);

        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        RootUtils.runCommand("unzip '" + path + "' -d '" + flashFolder + "'");
        if (Utils.existFile(Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary")) {
            RootUtils.runCommand("cd '" + flashFolder + "' && mount -o remount,rw / && mkdir /tmp");
            RootUtils.runCommand("mke2fs -F tmp.ext4 250000 && mount -o loop tmp.ext4 /tmp/");
            RootUtils.runCommand("sh META-INF/com/google/android/update-binary '" + RECOVERY_API + "' " + fd + " '" + path + "'| tee '" + Utils.getInternalDataStorage() + "'/flasher_log.txt");
            RootUtils.runCommand(CleanUpCommand);
        }
    }

    public static long fileSize(File file) {
        return file.length();
    }

}