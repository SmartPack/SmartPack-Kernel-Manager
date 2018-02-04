/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.utils.kernel.wake;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 25.06.16.
 */
public class Misc {

    private static Misc sInstance;

    public static Misc getInstance() {
        if (sInstance == null) {
            sInstance = new Misc();
        }
        return sInstance;
    }

    private static final String SCREEN_WAKE_OPTIONS = "/sys/devices/f9924000.i2c/i2c-2/2-0020/input/input2/screen_wake_options";

    private static final String CAMERA_GESTURE = "/sys/android_touch/camera_gesture";
    private static final String CAMERA_ENABLE = "/proc/touchpanel/camera_enable";

    private static final String POCKET_MODE = "/sys/android_touch/pocket_mode";
    private static final String POCKET_DETECT = "/sys/android_touch/pocket_detect";

    private static final String WAKE_TIMEOUT = "/sys/android_touch/wake_timeout";
    private static final String WAKE_TIMEOUT_2 = "/sys/android_touch2/wake_timeout";
    private static final String T2W_TIMEOUT_SMDK4412 = "/sys/devices/virtual/misc/touchwake/delay";

    private static final String T2W_CHARGE_TIMEOUT_SMDK4412 = "/sys/devices/virtual/misc/touchwake/charging_delay";

    private static final String POWER_KEY_SUSPEND = "/sys/module/qpnp_power_on/parameters/pwrkey_suspend";
    private static final String KEYPOWER_MODE_SMDK4412 = "/sys/devices/virtual/misc/touchwake/keypower_mode";

    private static final String VIBRATION = "/proc/touchpanel/haptic_feedback_disable";
    private static final String VIB_VIBRATION = "/sys/android_touch2/vib_strength";

    private static final String CHARGING_MODE_SMDK4412 = "/sys/devices/virtual/misc/touchwake/charging_mode";

    private final HashMap<String, List<Integer>> mWakeFiles = new HashMap<>();
    private final List<Integer> mScreenWakeOptionsMenu = new ArrayList<>();

    private final List<String> mCameraFiles = new ArrayList<>();

    private final List<String> mPocketFiles = new ArrayList<>();

    private final HashMap<String, Integer> mTimeoutFiles = new HashMap<>();

    {
        mScreenWakeOptionsMenu.add(R.string.disabled);
        mScreenWakeOptionsMenu.add(R.string.s2w);
        mScreenWakeOptionsMenu.add(R.string.s2w);
        mScreenWakeOptionsMenu.add(R.string.s2w_charging);
        mScreenWakeOptionsMenu.add(R.string.dt2w);
        mScreenWakeOptionsMenu.add(R.string.dt2w_charging);
        mScreenWakeOptionsMenu.add(R.string.dt2w_s2w);
        mScreenWakeOptionsMenu.add(R.string.dt2w_s2w_charging);

        mWakeFiles.put(SCREEN_WAKE_OPTIONS, mScreenWakeOptionsMenu);
    }

    {
        mCameraFiles.add(CAMERA_GESTURE);
        mCameraFiles.add(CAMERA_ENABLE);
    }

    {
        mPocketFiles.add(POCKET_MODE);
        mPocketFiles.add(POCKET_DETECT);
    }

    {
        mTimeoutFiles.put(WAKE_TIMEOUT, 30);
        mTimeoutFiles.put(WAKE_TIMEOUT_2, 10);
        mTimeoutFiles.put(T2W_TIMEOUT_SMDK4412, 60);
    }

    private String WAKE;
    private String CAMERA;
    private String POCKET;
    private String TIMEOUT;

    private Misc() {
        for (String file : mWakeFiles.keySet()) {
            if (Utils.existFile(file)) {
                WAKE = file;
                break;
            }
        }
        for (String file : mCameraFiles) {
            if (Utils.existFile(file)) {
                CAMERA = file;
                break;
            }
        }
        for (String file : mPocketFiles) {
            if (Utils.existFile(file)) {
                POCKET = file;
                break;
            }
        }
        for (String file : mTimeoutFiles.keySet()) {
            if (Utils.existFile(file)) {
                TIMEOUT = file;
                break;
            }
        }
    }

    public void setVibVibration(int value, Context context) {
        run(Control.write(String.valueOf(value), VIB_VIBRATION), VIB_VIBRATION, context);
    }

    public int getVibVibration() {
        return Utils.strToInt(Utils.readFile(VIB_VIBRATION));
    }

    public boolean hasVibVibration() {
        return Utils.existFile(VIB_VIBRATION);
    }

    public void enableVibration(boolean enable, Context context) {
        run(Control.write(enable ? "0" : "1", VIBRATION), VIB_VIBRATION, context);
    }

    public boolean isVibrationEnabled() {
        return Utils.readFile(VIBRATION).equals("0");
    }

    public boolean hasVibration() {
        return Utils.existFile(VIBRATION);
    }

    public void enablePowerKeySuspend(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", POWER_KEY_SUSPEND), POWER_KEY_SUSPEND, context);
    }

    public boolean isPowerKeySuspendEnabled() {
        return Utils.readFile(POWER_KEY_SUSPEND).equals("1");
    }

    public boolean hasPowerKeySuspend() {
        return Utils.existFile(POWER_KEY_SUSPEND);
    }

    public void enableKeyPowerMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", KEYPOWER_MODE_SMDK4412), KEYPOWER_MODE_SMDK4412, context);
    }

    public boolean isKeyPowerModeEnabled() {
        return Utils.readFile(KEYPOWER_MODE_SMDK4412).equals("1");
    }

    public boolean hasKeyPowerMode() {
        return Utils.existFile(KEYPOWER_MODE_SMDK4412);
    }

    public void enableChargingMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGING_MODE_SMDK4412), CHARGING_MODE_SMDK4412, context);
    }

    public boolean isChargingModeEnabled() {
        return Utils.readFile(CHARGING_MODE_SMDK4412).equals("1");
    }

    public boolean hasChargingMode() {
        return Utils.existFile(CHARGING_MODE_SMDK4412);
    }

    public void setChargeTimeout(int value, Context context) {
        run(Control.write(String.valueOf(value), T2W_CHARGE_TIMEOUT_SMDK4412),
                T2W_CHARGE_TIMEOUT_SMDK4412, context);
    }

    public int getChargeTimeout() {
        return Utils.strToInt(Utils.readFile(T2W_CHARGE_TIMEOUT_SMDK4412));
    }

    public boolean hasChargeTimeout() {
        return Utils.existFile(T2W_CHARGE_TIMEOUT_SMDK4412);
    }

    public void setTimeout(int value, Context context) {
        run(Control.write(String.valueOf(value), TIMEOUT), TIMEOUT, context);
    }

    public int getTimeout() {
        return Utils.strToInt(Utils.readFile(TIMEOUT));
    }

    public int getTimeoutMax() {
        return mTimeoutFiles.get(TIMEOUT);
    }

    public boolean hasTimeout() {
        return TIMEOUT != null;
    }

    public void enablePocket(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", POCKET), POCKET, context);
    }

    public boolean isPocketEnabled() {
        return Utils.readFile(POCKET).equals("1");
    }

    public boolean hasPocket() {
        return POCKET != null;
    }

    public void enableCamera(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CAMERA), CAMERA, context);
    }

    public boolean isCameraEnabled() {
        return Utils.readFile(CAMERA).equals("1");
    }

    public boolean hasCamera() {
        return CAMERA != null;
    }

    public void setWake(int value, Context context) {
        run(Control.write(String.valueOf(value), WAKE), WAKE, context);
    }

    public int getWake() {
        return Utils.strToInt(Utils.readFile(WAKE));
    }

    public List<String> getWakeMenu(Context context) {
        List<String> list = new ArrayList<>();
        for (int id : mWakeFiles.get(WAKE)) {
            list.add(context.getString(id));
        }
        return list;
    }

    public boolean hasWake() {
        return WAKE != null;
    }

    public boolean supported() {
        return hasWake() || hasCamera() || hasPocket() || hasTimeout() || hasChargeTimeout() || hasPowerKeySuspend()
                || hasKeyPowerMode() || hasChargingMode() || hasVibration() || hasVibVibration();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }

}
