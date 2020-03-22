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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    private static SmartPack sSmartPack;

    public static SmartPack getInstance() {
        if (sSmartPack == null) {
            sSmartPack = new SmartPack();
        }
        return sSmartPack;
    }

    private static final String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
    private static final String CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'";
    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";
    private static final String FLASHER_LOG = Utils.getInternalDataStorage() + "/flasher_log";

     private static StringBuilder mFlashingResult = null;

    private static String mountRootFS(String command) {
        return "mount " + command + " /";
    }

    private static boolean isUnzipAvailable() {
        return Utils.existFile("/system/bin/unzip") || Utils.existFile("/system/xbin/unzip");
    }

    private static String busyboxUnzip() {
        if (Utils.existFile("/sbin/.core/busybox/unzip")) {
            return "/sbin/.core/busybox/unzip";
        } else if (Utils.existFile("/sbin/.magisk/busybox/unzip")) {
            return "/sbin/.magisk/busybox/unzip";
        } else {
            return null;
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

    private static void prepareFlashFolder() {
        if (Utils.existFile(FLASH_FOLDER)) {
            RootUtils.runCommand(CLEANING_COMMAND);
        } else {
            RootUtils.runCommand("mkdir '" + FLASH_FOLDER + "'");
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void flashingTask(File file, Context context) {
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
                mFlashingResult = new StringBuilder();
                mFlashingResult.append("## Flasher log created by SmartPack-Kernel Manager\n\n");
                mFlashingResult.append("** Preparing to flash ").append(file.getName()).append("...\n\n");
                mFlashingResult.append("** Path: '").append(file.toString()).append("'\n\n");
                return manualFlash(file);
            }
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                boolean flashResult = s != null && !s.isEmpty();
                Dialog flashingResult = new Dialog(context);
                flashingResult.setIcon(R.mipmap.ic_launcher);
                flashingResult.setTitle(context.getString(R.string.flash_log));
                flashingResult.setCancelable(false);
                flashingResult.setMessage(mFlashingResult.toString() + (flashResult ? "\n" + s : ""));
                flashingResult.setNeutralButton(context.getString(R.string.save_log), (dialog, id) -> {
                    Utils.create(mFlashingResult.toString() + "\n" + s, FLASHER_LOG + "_" +
                            file.getName().replace(".zip", ""));
                    Utils.toast(context.getString(R.string.flash_log_summary, FLASHER_LOG + "_" + file.getName()
                            .replace(".zip", "")), context);
                });
                flashingResult.setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                });
                flashingResult.setPositiveButton(context.getString(R.string.reboot), (dialog, id) -> {
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
                });
                flashingResult.show();
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
        String flashingCommand = "sh '" + ZIPFILE_EXTRACTED + "' '" + RECOVERY_API + "' '" +
                fd + "' '" + file.toString() + "'";
        prepareFlashFolder();
        mFlashingResult.append("** Checking for unzip binary! ");
        if (isUnzipAvailable()) {
            mFlashingResult.append("Native binary available...\n");
            RootUtils.runCommand("unzip " + file.toString() + " -d '" + FLASH_FOLDER + "'");
        } else {
            mFlashingResult.append("BusyBox binary available...\n");
            RootUtils.runCommand(busyboxUnzip() + " " + file.toString() + " -d '" + FLASH_FOLDER + "'");
        }
        if (Utils.existFile(ZIPFILE_EXTRACTED)) {
            mFlashingResult.append("\n** Extracting ").append(file.getName()).append(" into working folder: Done *\n\n");
            mFlashingResult.append("** Preparing a recovery-like environment for flashing...\n\n");
            RootUtils.runCommand("cd '" + FLASH_FOLDER + "'");
            RootUtils.runCommand(mountRootFS("-o remount,rw"));
            mFlashingResult.append("** Mounting root file system: Done *\n\n");
            RootUtils.runCommand("mkdir /tmp");
            RootUtils.runCommand("mke2fs -F tmp.ext4 500000");
            mFlashingResult.append("** Preparing a temporary ext4 image and loop mounting to '/tmp': Done *\n\n");
            Utils.mount("-o loop", "tmp.ext4", "/tmp/");
            mFlashingResult.append("\n** Flashing ").append(file.getName()).append(" ...\n");
            return RootUtils.runCommand(flashingCommand + " && " + CLEANING_COMMAND + " && " +
                    mountRootFS("-o remount,ro"));
        } else {
            mFlashingResult.append("** Extracting zip file failed! *\n\n");
            mFlashingResult.append("** Flashing Failed! *\n** Reason: Necessary BusyBox binaries not available! *");
            return RootUtils.runCommand(CLEANING_COMMAND + " && " +
                    mountRootFS("-o remount,ro"));
        }
    }

}