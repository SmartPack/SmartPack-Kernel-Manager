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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    private static final String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
    private static final String CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'";
    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";
    private static final String UNZIP_BINARY = "/system/bin/unzip";
    private static final String MAGISK_UNZIP = "/sbin/.magisk/busybox/unzip";
    private static final String FLASHER_LOG = Utils.getInternalDataStorage() + "/flasher_log";

    private static String mountFS(String command, String fs) {
        return "mount " + command + " " + fs;
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

    public static void prepareFlashFolder() {
        if (Utils.existFile(FLASH_FOLDER)) {
            RootUtils.runCommand(CLEANING_COMMAND);
        } else {
            RootUtils.runCommand("mkdir '" + FLASH_FOLDER + "'");
        }
    }

    public static void flashingTask(File file, Context context) {
        new AsyncTask<Void, Void, String>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.flashing) + " " + file.getName());
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            protected String doInBackground(Void... voids) {
                Utils.create("## Flasher log created by SmartPack-Kernel Manager\n\n", FLASHER_LOG);
                Utils.append("Preparing to flash " + file.getName() + "...", FLASHER_LOG);
                Utils.append("Path: " + file.toString() + "\n", FLASHER_LOG);
                return manualFlash(file);
            }
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (s != null && !s.isEmpty()) {
                    Utils.append(s, FLASHER_LOG);
                    new Dialog(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(context.getString(R.string.flash_log))
                            .setMessage(s)
                            .setCancelable(false)
                            .setNeutralButton(context.getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(context.getString(R.string.reboot), (dialog, id) -> {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        mProgressDialog = new ProgressDialog(context);
                                        mProgressDialog.setMessage(context.getString(R.string.rebooting) + ("..."));
                                        mProgressDialog.setCancelable(false);
                                        mProgressDialog.show();
                                    }
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        RootUtils.runCommand(Utils.prepareReboot());
                                        return null;
                                    }
                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        try {
                                            mProgressDialog.dismiss();
                                        } catch (IllegalArgumentException ignored) {
                                        }
                                    }
                                }.execute();
                            })
                            .show();

                } else {
                    ViewUtils.dialogError(context.getString(R.string.empty_flasher_log), FLASHER_LOG, context);
                }
            }
        }.execute();
    }

    private static String manualFlash(File file) {
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        FileDescriptor fd = new FileDescriptor();
        int RECOVERY_API = 3;
        String flashingCommnd = "sh '" + ZIPFILE_EXTRACTED + "' '" + RECOVERY_API + "' '" +
                fd + "' '" + file.toString() + "'";
        prepareFlashFolder();
        Utils.append("Checking BusyBox binaries...", FLASHER_LOG);
        if ((Utils.readFile(UNZIP_BINARY).isEmpty() || !Utils.existFile(UNZIP_BINARY)) &&
                Utils.existFile(MAGISK_UNZIP)) {
            Utils.append("Native BusyBox binaries unavailable...\nUsing Magisk BusyBox...", FLASHER_LOG);
            RootUtils.runCommand(mountFS("-o remount,rw", "/system"));
            Utils.create("", UNZIP_BINARY);
            Utils.mount("-o bind", MAGISK_UNZIP, UNZIP_BINARY);
        }
        Utils.append("Extracting " + file.getName() + " to working folder...", FLASHER_LOG);
        RootUtils.runCommand("unzip '" + file.toString() + "' -d '" + FLASH_FOLDER + "'");
        if (Utils.existFile(ZIPFILE_EXTRACTED)) {
            Utils.append("Preparing a recovery-like environment for flashing...", FLASHER_LOG);
            RootUtils.runCommand("cd '" + FLASH_FOLDER + "'");
            Utils.append("Mounting root file system ...", FLASHER_LOG);
            RootUtils.runCommand(mountFS("-o remount,rw", "/"));
            RootUtils.runCommand("mkdir /tmp");
            Utils.append("Preparing a temporary ext4 image and loop mounting to '/tmp' ...", FLASHER_LOG);
            RootUtils.runCommand("mke2fs -F tmp.ext4 500000");
            Utils.mount("-o loop", "tmp.ext4", "/tmp/");
            Utils.append("\nFlashing " + file.getName() + " ...\n", FLASHER_LOG);
            return RootUtils.runCommand(flashingCommnd + " && " + CLEANING_COMMAND + " && " +
                    mountFS("-o remount,ro", "/ /system"));
        } else {
            return Utils.append("\nExtracting zip file failed! Aborting...", FLASHER_LOG);
        }
    }

}