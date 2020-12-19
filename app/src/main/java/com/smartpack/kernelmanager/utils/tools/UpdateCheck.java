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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 16, 2020
 */

public class UpdateCheck {

    private static final String LATEST_VERSION_URL = "https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Manager/beta/app/src/main/assets/release.json";
    private static final String LATEST_VERSION_APK = Utils.getInternalDataStorage() + "/" + BuildConfig.APPLICATION_ID + ".apk";

    public static void getVersionInfo(Context context) {
        Utils.prepareInternalDataStorage();
        Utils.downloadFile(releaseInfo(context), LATEST_VERSION_URL, context);
    }

    public static int versionCode(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getInt("latestVersionCode"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_CODE;
        }
    }

    private static String getUrl(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("releaseUrl"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_NAME;
        }
    }

    private static String versionName(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("latestVersion"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_NAME;
        }
    }

    private static String getChangeLogs(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("releaseNotes"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getChecksum(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("sha1"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static void getLatestApp(Context context) {
        Utils.prepareInternalDataStorage();
        Utils.downloadFile(LATEST_VERSION_APK, getUrl(context), context);
    }

    public static boolean hasVersionInfo(Context context) {
        return Utils.existFile(releaseInfo(context));
    }

    public static long lastModified(Context context) {
        return new File(releaseInfo(context)).lastModified();
    }

    private static String releaseInfo(Context context) {
        return context.getFilesDir().getPath() + "/release";
    }

    private static String getReleaseInfo(Context context) {
        return Utils.readFile(releaseInfo(context));
    }

    public static void updateAvailableDialog(Context context) {
        new Dialog(context)
                .setTitle(context.getString(R.string.update_available, UpdateCheck.versionName(context)))
                .setMessage(getChangeLogs(context))
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                })
                .setPositiveButton(context.getString(R.string.get_it), (dialog, id) -> {
                    updaterTask(context);
                })
                .show();
    }

    private static void updaterTask(Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.downloading_update, context.
                        getString(R.string.app_name) + " v" + versionName(context) + " ..."));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                getLatestApp(context);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (Utils.existFile(LATEST_VERSION_APK) && Utils.getChecksum(LATEST_VERSION_APK).contains(getChecksum(context))) {
                    installUpdate(context);
                } else {
                    new Dialog(context)
                            .setMessage(context.getString(R.string.download_failed))
                            .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                            })
                            .show();
                }
            }
        }.execute();
    }

    public static void manualUpdateCheck(Activity activity) {
        if (activity.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getVersionInfo(activity);
            if (UpdateCheck.hasVersionInfo(activity) && BuildConfig.VERSION_CODE < UpdateCheck.versionCode(activity)) {
                updateAvailableDialog(activity);
            } else {
                new Dialog(activity)
                        .setMessage(activity.getString(R.string.updated_dialog))
                        .setNegativeButton(activity.getString(R.string.ok), (dialog, id) -> {
                        })
                        .show();
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            activity.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private static void installUpdate(Context context) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uriFile;
        uriFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                new File(LATEST_VERSION_APK));
        intent.setDataAndType(uriFile, "application/vnd.android.package-archive");
        context.startActivity(Intent.createChooser(intent, ""));
    }

    /*
     * Based on the ApkSignatureVerifier.java in https://github.com/f-droid/fdroidclient
     * Ref: https://raw.githubusercontent.com/f-droid/fdroidclient/master/app/src/main/java/org/fdroid/fdroid/installer/ApkSignatureVerifier.java
     */
    public static boolean isSignatureMatched(Context context) {
        try {
            return !Arrays.equals(getSignature(context.getPackageName(), context), getSignature("org.fdroid.fdroid", context));
        } catch (NullPointerException ignored) {
        }
        return false;
    }

    @SuppressLint("PackageManagerGetSignatures")
    private static byte[] getSignature(String packageid, Context context) {
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(packageid, PackageManager.GET_SIGNATURES);
            return signatureToBytes(pkgInfo.signatures);
        } catch (PackageManager.NameNotFoundException ignored) {}
        return null;
    }

    private static byte[] signatureToBytes(Signature[] signatures) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Signature sig : signatures) {
            try {
                outputStream.write(sig.toByteArray());
            } catch (IOException ignored) {}
        }
        return outputStream.toByteArray();
    }

}