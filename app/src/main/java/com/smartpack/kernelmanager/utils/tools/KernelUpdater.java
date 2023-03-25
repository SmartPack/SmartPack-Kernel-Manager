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

import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.topjohnwu.superuser.io.SuFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 21, 2020
 */

public class KernelUpdater {

    private static JSONObject mJSONObject = null;

    public KernelUpdater() {
    }

    public static boolean isUpdateTime(Context context) {
        return Prefs.getBoolean("update_check", false, context) && !KernelUpdater.getUpdateChannel()
                .equals("Unavailable") && System.currentTimeMillis() > Prefs.getLong("kernelUCTimeStamp",
                0, context) + 24 * 60 * 60 * 1000;
    }

    public static File updateInfo() {
        return SuFile.open(Utils.getInternalDataStorage(), "release");
    }

    public static File updateChannelInfo() {
        return SuFile.open(Utils.getInternalDataStorage(), "updatechannel");
    }

    public static void saveUpdateInfo(Context context) {
        Utils.getInternalDataStorage().mkdirs();
        Utils.create(KernelUpdater.getJSONObject().toString(), KernelUpdater.updateInfo().getAbsolutePath());
        Prefs.saveLong("kernelUCTimeStamp", System.currentTimeMillis(), context);
    }

    public static JSONObject getJSONObject() {
        return mJSONObject;
    }

    private static String getKernelInfo() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(updateInfo().getAbsolutePath()));
            return (obj.getString("kernel"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    private static String getSupportInfo() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(updateInfo().getAbsolutePath()));
            return (obj.getString("support"));
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public static String getUpdateChannel() {
        if (Utils.existFile(updateChannelInfo().getAbsolutePath())) {
            return Utils.readFile(updateChannelInfo().getAbsolutePath());
        } else {
            return "Unavailable";
        }
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

    public static String getChecksum() {
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

    public static void saveUpdateChannel(String value) {
        Utils.getInternalDataStorage().mkdirs();
        Utils.create(value, updateChannelInfo().getAbsolutePath());
    }

    public static void acquireUpdateInfo(String value) {
        try (InputStream is = new URL(value).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = UpdateCheck.readAll(rd);
            mJSONObject = new JSONObject(jsonText);
        } catch (IOException | JSONException ignored) {
        }
    }

    private static void parse(int updateCheckInterval, Context context) {
        new sExecutor() {

            private long ucTimeStamp;
            private int interval;
            @Override
            public void onPreExecute() {
                ucTimeStamp = Prefs.getLong("kernelUCTimeStamp", 0, context);
                interval = updateCheckInterval * 60 * 60 * 1000;
                Utils.getInternalDataStorage().mkdirs();
            }

            @Override
            public void doInBackground() {
                if (System.currentTimeMillis() > ucTimeStamp + interval) {
                    acquireUpdateInfo(Objects.requireNonNull(Utils.readFile(KernelUpdater.updateChannelInfo().getAbsolutePath())));
                }
            }

            @Override
            public void onPostExecute() {
                if (mJSONObject != null) {
                    Utils.create(mJSONObject.toString(), updateInfo().getAbsolutePath());
                    Prefs.saveLong("kernelUCTimeStamp", System.currentTimeMillis(), context);
                }
            }
        }.execute();
    }

    public void initialize(int updateCheckInterval, Context context) {
        parse(updateCheckInterval, context);
    }

}