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
package com.grarak.kerneladiutor.utils.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 19.09.17.
 */

public class DeviceInfo {

    private String mAndroidVersion;
    private String mKernelVersion;
    private String mAppVersion;
    private String mBoard;
    private String mModel;
    private String mVendor;
    private String mCpuInfo;
    private String mFingerprint;
    private List<String> mCommands;
    private long mAverageSOT;
    private long mCpu;
    private long mScore;

    private boolean mValid = true;

    public DeviceInfo(JSONObject json) {
        try {
            mAndroidVersion = json.getString("android_version");
            mKernelVersion = json.getString("kernel_version");
            mAppVersion = json.getString("app_version");
            mBoard = json.getString("board");
            mModel = json.getString("model");
            mVendor = json.getString("vendor");
            mCpuInfo = json.getString("cpuinfo");
            mFingerprint = json.getString("fingerprint");

            JSONArray commands = json.getJSONArray("commands");
            mCommands = new ArrayList<>();

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
            mScore = Math.round(json.getDouble("score"));
        } catch (JSONException ignored) {
            mValid = false;
        }
    }

    public String getAndroidVersion() {
        return mAndroidVersion;
    }

    public String getKernelVersion() {
        return mKernelVersion;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public String getBoard() {
        return mBoard;
    }

    public String getModel() {
        return mModel;
    }

    public String getVendor() {
        return mVendor;
    }

    public String getCpuInfo() {
        return mCpuInfo;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public List<String> getCommands() {
        return mCommands;
    }

    public long getAverageSOT() {
        return mAverageSOT;
    }

    public long getCpu() {
        return mCpu;
    }

    public long getScore() {
        return mScore;
    }

    public boolean valid() {
        return mValid;
    }

}
