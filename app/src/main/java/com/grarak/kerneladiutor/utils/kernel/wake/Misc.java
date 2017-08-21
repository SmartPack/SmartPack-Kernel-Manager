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

    private static final HashMap<String, List<Integer>> sWakeFiles = new HashMap<>();
    private static final List<Integer> sScreenWakeOptionsMenu = new ArrayList<>();

    private static final List<String> sCameraFiles = new ArrayList<>();

    private static final List<String> sPocketFiles = new ArrayList<>();

    private static final HashMap<String, Integer> sTimeoutFiles = new HashMap<>();

    static {
        sScreenWakeOptionsMenu.add(R.string.disabled);
        sScreenWakeOptionsMenu.add(R.string.s2w);
        sScreenWakeOptionsMenu.add(R.string.s2w);
        sScreenWakeOptionsMenu.add(R.string.s2w_charging);
        sScreenWakeOptionsMenu.add(R.string.dt2w);
        sScreenWakeOptionsMenu.add(R.string.dt2w_charging);
        sScreenWakeOptionsMenu.add(R.string.dt2w_s2w);
        sScreenWakeOptionsMenu.add(R.string.dt2w_s2w_charging);

        sWakeFiles.put(SCREEN_WAKE_OPTIONS, sScreenWakeOptionsMenu);
    }

    static {
        sCameraFiles.add(CAMERA_GESTURE);
        sCameraFiles.add(CAMERA_ENABLE);
    }

    static {
        sPocketFiles.add(POCKET_MODE);
        sPocketFiles.add(POCKET_DETECT);
    }

    static {
        sTimeoutFiles.put(WAKE_TIMEOUT, 30);
        sTimeoutFiles.put(WAKE_TIMEOUT_2, 10);
        sTimeoutFiles.put(T2W_TIMEOUT_SMDK4412, 60);
    }

    private static String WAKE;
    private static String CAMERA;
    private static String POCKET;
    private static String TIMEOUT;

    public static void setVibVibration(int value, Context context) {
        run(Control.write(String.valueOf(value), VIB_VIBRATION), VIB_VIBRATION, context);
    }

    public static int getVibVibration() {
        return Utils.strToInt(Utils.readFile(VIB_VIBRATION));
    }

    public static boolean hasVibVibration() {
        return Utils.existFile(VIB_VIBRATION);
    }

    public static void enableVibration(boolean enable, Context context) {
        run(Control.write(enable ? "0" : "1", VIBRATION), VIB_VIBRATION, context);
    }

    public static boolean isVibrationEnabled() {
        return Utils.readFile(VIBRATION).equals("0");
    }

    public static boolean hasVibration() {
        return Utils.existFile(VIBRATION);
    }

    public static void enablePowerKeySuspend(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", POWER_KEY_SUSPEND), POWER_KEY_SUSPEND, context);
    }

    public static boolean isPowerKeySuspendEnabled() {
        return Utils.readFile(POWER_KEY_SUSPEND).equals("1");
    }

    public static boolean hasPowerKeySuspend() {
        return Utils.existFile(POWER_KEY_SUSPEND);
    }

    public static void enableKeyPowerMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", KEYPOWER_MODE_SMDK4412), KEYPOWER_MODE_SMDK4412, context);
    }

    public static boolean isKeyPowerModeEnabled() {
        return Utils.readFile(KEYPOWER_MODE_SMDK4412).equals("1");
    }

    public static boolean hasKeyPowerMode() {
        return Utils.existFile(KEYPOWER_MODE_SMDK4412);
    }

    public static void enableChargingMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGING_MODE_SMDK4412), CHARGING_MODE_SMDK4412, context);
    }

    public static boolean isChargingModeEnabled() {
        return Utils.readFile(CHARGING_MODE_SMDK4412).equals("1");
    }

    public static boolean hasChargingMode() {
        return Utils.existFile(CHARGING_MODE_SMDK4412);
    }

    public static void setChargeTimeout(int value, Context context) {
        run(Control.write(String.valueOf(value), T2W_CHARGE_TIMEOUT_SMDK4412),
                T2W_CHARGE_TIMEOUT_SMDK4412, context);
    }

    public static int getChargeTimeout() {
        return Utils.strToInt(Utils.readFile(T2W_CHARGE_TIMEOUT_SMDK4412));
    }

    public static boolean hasChargeTimeout() {
        return Utils.existFile(T2W_CHARGE_TIMEOUT_SMDK4412);
    }

    public static void setTimeout(int value, Context context) {
        run(Control.write(String.valueOf(value), TIMEOUT), TIMEOUT, context);
    }

    public static int getTimeout() {
        return Utils.strToInt(Utils.readFile(TIMEOUT));
    }

    public static int getTimeoutMax() {
        return sTimeoutFiles.get(TIMEOUT);
    }

    public static boolean hasTimeout() {
        if (TIMEOUT == null) {
            for (String file : sTimeoutFiles.keySet()) {
                if (Utils.existFile(file)) {
                    TIMEOUT = file;
                    return true;
                }
            }
        }
        return TIMEOUT != null;
    }

    public static void enablePocket(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", POCKET), POCKET, context);
    }

    public static boolean isPocketEnabled() {
        return Utils.readFile(POCKET).equals("1");
    }

    public static boolean hasPocket() {
        if (POCKET == null) {
            for (String file : sPocketFiles) {
                if (Utils.existFile(file)) {
                    POCKET = file;
                    return true;
                }
            }
        }
        return POCKET != null;
    }

    public static void enableCamera(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CAMERA), CAMERA, context);
    }

    public static boolean isCameraEnabled() {
        return Utils.readFile(CAMERA).equals("1");
    }

    public static boolean hasCamera() {
        if (CAMERA == null) {
            for (String file : sCameraFiles) {
                if (Utils.existFile(file)) {
                    CAMERA = file;
                    return true;
                }
            }
        }
        return CAMERA != null;
    }

    public static void setWake(int value, Context context) {
        run(Control.write(String.valueOf(value), WAKE), WAKE, context);
    }

    public static int getWake() {
        return Utils.strToInt(Utils.readFile(WAKE));
    }

    public static List<String> getWakeMenu(Context context) {
        List<String> list = new ArrayList<>();
        for (int id : sWakeFiles.get(WAKE)) {
            list.add(context.getString(id));
        }
        return list;
    }

    public static boolean hasWake() {
        if (WAKE == null) {
            for (String file : sWakeFiles.keySet()) {
                if (Utils.existFile(file)) {
                    WAKE = file;
                    return true;
                }
            }
        }
        return WAKE != null;
    }

    public static boolean supported() {
        return hasWake() || hasCamera() || hasPocket() || hasTimeout() || hasChargeTimeout() || hasPowerKeySuspend()
                || hasKeyPowerMode() || hasChargingMode() || hasVibration() || hasVibVibration();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }

}
