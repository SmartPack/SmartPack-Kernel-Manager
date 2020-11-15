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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 21, 2020
 */

public class KernelUpdater {

    private static void updateChannel(String value, Context context) {
        Utils.create(value, updateChannelInfo(context));
    }

    public static void updateInfo(String value, Context context) {
        Utils.prepareInternalDataStorage();
        Utils.downloadFile(updateInfo(context), value, context);
    }

    public static void clearUpdateInfo(Context context) {
        Utils.delete(updateChannelInfo(context));
        Utils.delete(updateInfo(context));
    }

    private static String getKernelInfo(Context context) {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(updateInfo(context)));
            return (obj.getString("kernel"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static String getSupportInfo(Context context) {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(updateInfo(context)));
            return (obj.getString("support"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getUpdateChannel(Context context) {
        if (Utils.existFile(updateChannelInfo(context))) {
            return Utils.readFile(updateChannelInfo(context));
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
                clearUpdateInfo(context);
                updateInfo(value, context);
                updateChannel(value, context);
                Utils.sleep(1);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (getKernelName(context).equals("Unavailable")) {
                    Utils.toast(R.string.update_channel_invalid, context);
                }
            }
        }.execute();
    }

    public static String getKernelName(Context context) {
        try {
            JSONObject obj = new JSONObject(getKernelInfo(context));
            return (obj.getString("name"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getLatestVersion(Context context) {
        try {
            JSONObject obj = new JSONObject(getKernelInfo(context));
            return (obj.getString("version"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getUrl(Context context) {
        try {
            JSONObject obj = new JSONObject(getKernelInfo(context));
            return (obj.getString("link"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getChecksum(Context context) {
        try {
            JSONObject obj = new JSONObject(getKernelInfo(context));
            return (obj.getString("sha1"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getChangeLog(Context context) {
        try {
            JSONObject obj = new JSONObject(getKernelInfo(context));
            return (obj.getString("changelog_url"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getSupport(Context context) {
        try {
            JSONObject obj = new JSONObject(getSupportInfo(context));
            return (obj.getString("link"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getDonationLink(Context context) {
        try {
            JSONObject obj = new JSONObject(getSupportInfo(context));
            return (obj.getString("donation"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static String updateInfo(Context context) {
        return context.getFilesDir().getPath() + "/release";
    }

    public static String updateChannelInfo(Context context) {
        return context.getFilesDir().getPath() + "/updatechannel";
    }

    public static long lastModified(Context context) {
        return new File(updateInfo(context)).lastModified();
    }

}