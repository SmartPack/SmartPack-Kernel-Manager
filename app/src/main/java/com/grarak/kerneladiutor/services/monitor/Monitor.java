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
package com.grarak.kerneladiutor.services.monitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.database.Settings;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.server.ServerCreateDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by willi on 01.12.16.
 */

public class Monitor extends Service {

    private int mLevel;
    private long mTime;
    private List<Long> mTimes = new ArrayList<>();
    private ServerCreateDevice mServerCreateDevice = new ServerCreateDevice("https://www.grarak.com");
    private boolean mScreenOn;
    private boolean mCalculating;

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING;

            if (charging || !mScreenOn) {
                mLevel = 0;
                mTime = 0;
            } else {
                mCalculating = true;

                long time = System.nanoTime();
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

                if (mLevel != 0 && mLevel > level && mTime != 0 && mTime < time && mLevel - level > 0) {
                    long seconds = TimeUnit.SECONDS.convert((time - mTime) / (mLevel - level),
                            TimeUnit.NANOSECONDS);
                    if (seconds >= 100) {
                        mTimes.add(seconds);

                        if (mTimes.size() % 15 == 0) {
                            postCreate(mTimes.toArray(new Long[mTimes.size()]));
                            if (mTimes.size() >= 100) {
                                mTimes.clear();
                            }
                        }
                    }
                }
                mLevel = level;
                mTime = time;
            }

            mCalculating = false;
        }
    };

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mScreenOn = intent.getAction().equals(Intent.ACTION_SCREEN_ON);
            if (!mScreenOn && !mCalculating) {
                mLevel = 0;
                mTime = 0;
            }
        }
    };

    private void postCreate(final Long[] times) {
        if (mLevel < 15 || !Prefs.getBoolean("data_sharing", true, this)) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject();
                    data.put("android_id", Utils.getAndroidId(Monitor.this));
                    data.put("android_version", Device.getVersion());
                    data.put("kernel_version", Device.getKernelVersion(true, false));
                    data.put("app_version", BuildConfig.VERSION_NAME);
                    data.put("board", Device.getBoard(false));
                    data.put("model", Device.getModel());
                    data.put("vendor", Device.getVendor());
                    data.put("cpuinfo", Utils.encodeString(Device.CPUInfo.getCpuInfo(false)));
                    data.put("fingerprint", Device.getFingerprint());

                    JSONArray commands = new JSONArray();
                    Settings settings = new Settings(Monitor.this);
                    for (Settings.SettingsItem item : settings.getAllSettings()) {
                        commands.put(item.getSetting());
                    }
                    data.put("commands", commands);

                    JSONArray batteryTimes = new JSONArray();
                    for (long time : times) {
                        batteryTimes.put(time);
                    }
                    data.put("times", batteryTimes);

                    try {
                        long time = 0;
                        for (int i = 0; i < 100000; i++) {
                            time += Utils.computeSHAHash(Utils.getRandomString(16));
                        }
                        data.put("cpu", time);
                    } catch (Exception ignored) {
                    }

                    mServerCreateDevice.postDeviceCreate(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private IMonitor.Stub mBinder = new IMonitor.Stub() {
        @Override
        public void onSettingsChange() throws RemoteException {
            if (mTimes != null) {
                mTimes.clear();
                mLevel = 0;
                mTime = 0;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenReceiver, screenFilter);

        mScreenOn = Utils.isScreenOn(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatteryReceiver);
        unregisterReceiver(mScreenReceiver);
    }

}
