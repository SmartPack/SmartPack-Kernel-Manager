/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.smartpack.kernelmanager.utils.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 19.09.17.
 */

public class DeviceInfo {

    private String mVendor;
    private String mFingerprint;
    private long mAverageSOT;
    private long mCpu;

    DeviceInfo(JSONObject json) {
        try {
            String mAndroidVersion = json.getString("android_version");
            String mKernelVersion = json.getString("kernel_version");
            String mAppVersion = json.getString("app_version");
            String mBoard = json.getString("board");
            String mModel = json.getString("model");
            mVendor = json.getString("vendor");
            String mCpuInfo = json.getString("cpuinfo");
            mFingerprint = json.getString("fingerprint");

            JSONArray commands = json.getJSONArray("commands");
            List<String> mCommands = new ArrayList<>();

            for (int i = 0; i < commands.length(); i++) {
                mCommands.add(commands.getString(i));
            }

            JSONArray times = json.getJSONArray("times");
            for (int i = 0; i < times.length(); i++) {
                mAverageSOT += times.getInt(i);
            }
            mAverageSOT /= times.length();
            mAverageSOT *= 100;

            mCpu = json.getLong("cpu");
            long mScore = Math.round(json.getDouble("score"));
        } catch (JSONException ignored) {
            boolean mValid = false;
        }
    }

    public String getVendor() {
        return mVendor;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public long getCpu() {
        return mCpu;
    }

}