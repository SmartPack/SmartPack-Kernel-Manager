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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.content.FileProvider;

import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.dialog.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 16, 2020
 */

public class UpdateCheck {

    private static final String LATEST_VERSION_URL = "https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Manager/master/download/App-update.json";
    private static final String LATEST_APK = Utils.getInternalDataStorage() + "/com.smartpack.kernelmanager.apk";
    private static final String UPDATE_INFO = Utils.getInternalDataStorage() + "/update_info.json";
    private static final String UPDATE_INFO_STRING = Utils.readFile(UPDATE_INFO);

    private static void prepareInternalStorage() {
        File file = new File(Utils.getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static void getVersionInfo() {
        prepareInternalStorage();
        Utils.downloadFile(UPDATE_INFO, LATEST_VERSION_URL);
    }

    private static void getLatestApp() {
        prepareInternalStorage();
        Utils.downloadFile(LATEST_APK, getURL());
    }

    public static String versionName() {
        try {
            JSONObject obj = new JSONObject(UPDATE_INFO_STRING);
            return (obj.getString("latestVersion"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_NAME;
        }
    }

    private static String getURL() {
        try {
            JSONObject obj = new JSONObject(UPDATE_INFO_STRING);
            return (obj.getString("url"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }
    private static String changelogs() {
        try {
            JSONObject obj = new JSONObject(UPDATE_INFO_STRING);
            return (obj.getString("releaseNotes"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static boolean hasVersionInfo() {
        return Utils.existFile(UPDATE_INFO);
    }

    public static long lastModified() {
        return new File(UPDATE_INFO).lastModified();
    }

    public static void updateAvailableDialog(Context context) {
        new Dialog(context)
                .setTitle(context.getString(R.string.update_available, UpdateCheck.versionName()))
                .setMessage(UpdateCheck.changelogs())
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                })
                .setPositiveButton(context.getString(R.string.get_it), (dialog, id) -> {
                    updaterTask(context);
                })
                .show();
    }

    public static void manualUpdateCheck(Context context) {
        if (UpdateCheck.hasVersionInfo() && !BuildConfig.VERSION_NAME.equals(UpdateCheck.versionName())) {
            updateAvailableDialog(context);
        } else {
            new Dialog(context)
                    .setMessage(context.getString(R.string.updated_dialog))
                    .setNegativeButton(context.getString(R.string.ok), (dialog, id) -> {
                    })
                    .show();
        }
    }

    private static void updaterTask(Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.downloading, UpdateCheck.versionName()) + "...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                getLatestApp();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                File apk = new File(LATEST_APK);
                if (apk.exists() && apk.length() > 500000) {
                    installUpdate(context);
                } else {
                    Utils.toast(R.string.download_failed, context);
                }

            }
        }.execute();
    }

    private static void installUpdate(Context context) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uriFile;
        uriFile = FileProvider.getUriForFile(context, "com.grarak.kerneladiutor.provider",
                new File(LATEST_APK));
        intent.setDataAndType(uriFile, "application/vnd.android.package-archive");
        context.startActivity(Intent.createChooser(intent, ""));
    }

}