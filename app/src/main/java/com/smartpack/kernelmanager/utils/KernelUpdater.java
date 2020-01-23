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

package com.smartpack.kernelmanager.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.views.dialog.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 21, 2020
 */

public class KernelUpdater {

    private static final String UPDATE_CHANNEL = Utils.getInternalDataStorage() + "/updatechannel";
    private static final String UPDATE_INFO = Utils.getInternalDataStorage() + "/updateinfo";
    private static final String LATEST_KERNEL = Utils.getInternalDataStorage() + "/Kernel.zip";

    private static void updateChannel(String value) {
        Utils.create(value, UPDATE_CHANNEL);
    }

    public static void updateInfo(String value) {
        SmartPack.prepareFlashFolder();
        Utils.downloadFile(UPDATE_INFO, value);
    }

    public static void clearUpdateInfo() {
        Utils.delete(UPDATE_CHANNEL);
        Utils.delete(UPDATE_INFO);
    }

    private static String getKernelInfo() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(UPDATE_INFO));
            return (obj.getString("kernel"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static String getSupportInfo() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(UPDATE_INFO));
            return (obj.getString("support"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getUpdateChannel() {
        if (Utils.existFile(UPDATE_CHANNEL)) {
            return Utils.readFile(UPDATE_CHANNEL);
        } else {
            return "Unavailable";
        }
    }

    public static void acquireUpdateInfo(String value, Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.acquiring));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                clearUpdateInfo();
                updateInfo(value);
                updateChannel(value);
                RootUtils.runCommand("sleep 1");
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (getKernelName().equals("Unavailable")) {
                    Utils.toast(R.string.update_channel_invalid, context);
                }
            }
        }.execute();
    }

    public static void downloadKernel(Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.downloading_update, getKernelName() + "-" + getLatestVersion()) + "...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                SmartPack.prepareFlashFolder();
                Utils.downloadFile(LATEST_KERNEL, getUrl());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (getChecksum().equals("Unavailable") || !getChecksum().equals("Unavailable") &&
                        Utils.getChecksum(LATEST_KERNEL).contains(getChecksum())) {
                    new Dialog(context)
                            .setMessage(context.getString(R.string.download_completed,
                                    getKernelName() + "-" + getLatestVersion()))
                            .setCancelable(false)
                            .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(context.getString(R.string.flash), (dialog, id) -> {
                                SmartPack.flashingTask(new File(LATEST_KERNEL), context);
                            })
                            .show();
                } else {
                    new Dialog(context)
                            .setMessage(context.getString(R.string.download_failed))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.cancel), (dialog, id) -> {
                            })
                            .show();
                }
            }
        }.execute();
    }

    public static String getKernelName() {
        try {
            JSONObject obj = new JSONObject(getKernelInfo());
            return (obj.getString("name"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getLatestVersion() {
        try {
            JSONObject obj = new JSONObject(getKernelInfo());
            return (obj.getString("version"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getUrl() {
        try {
            JSONObject obj = new JSONObject(getKernelInfo());
            return (obj.getString("link"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static String getChecksum() {
        try {
            JSONObject obj = new JSONObject(getKernelInfo());
            return (obj.getString("sha1"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getChangeLog() {
        try {
            JSONObject obj = new JSONObject(getKernelInfo());
            return (obj.getString("changelog_url"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getSupport() {
        try {
            JSONObject obj = new JSONObject(getSupportInfo());
            return (obj.getString("link"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getDonationLink() {
        try {
            JSONObject obj = new JSONObject(getSupportInfo());
            return (obj.getString("donation"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static long lastModified() {
        return new File(UPDATE_INFO).lastModified();
    }

}