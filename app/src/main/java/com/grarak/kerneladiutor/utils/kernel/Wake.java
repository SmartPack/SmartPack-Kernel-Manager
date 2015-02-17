/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.01.15.
 */
public class Wake implements Constants {

    private static String DT2W_FILE;
    private static String S2W_FILE;
    private static String T2W_FILE;

    public static void activatePowerKeySuspend(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", POWER_KEY_SUSPEND, Control.CommandType.GENERIC, context);
    }

    public static boolean isPowerKeySuspendActive() {
        return Utils.readFile(POWER_KEY_SUSPEND).equals("1");
    }

    public static boolean hasPowerKeySuspend() {
        return Utils.existFile(POWER_KEY_SUSPEND);
    }

    public static void setWakeTimeout(int value, Context context) {
        Control.runCommand(String.valueOf(value), WAKE_TIMEOUT, Control.CommandType.GENERIC, context);
    }

    public static int getWakeTimeout() {
        return Utils.stringToInt(Utils.readFile(WAKE_TIMEOUT));
    }

    public static boolean hasWakeTimeout() {
        return Utils.existFile(WAKE_TIMEOUT);
    }

    public static void setT2w(int value, Context context) {
        String command = String.valueOf(value);
        if (T2W_FILE.equals(TSP_T2W)) command = value == 0 ? "OFF" : "AUTO";

        Control.runCommand(command, T2W_FILE, Control.CommandType.GENERIC, context);
    }

    public static int getT2w() {
        if (T2W_FILE != null && Utils.existFile(T2W_FILE)) {
            String value = Utils.readFile(T2W_FILE);
            if (T2W_FILE.equals(TSP_T2W)) return value.equals("OFF") ? 0 : 1;
            return Utils.stringToInt(value);
        }
        return 0;
    }

    public static List<String> getT2wMenu(Context context) {
        List<String> list = new ArrayList<>();
        if (T2W_FILE != null) {
            list.add(context.getString(R.string.disabled));
            list.add(context.getString(R.string.enabled));
        }
        return list;
    }

    public static boolean hasT2w() {
        if (T2W_FILE == null)
            for (String file : T2W_ARRAY)
                if (Utils.existFile(file)) {
                    T2W_FILE = file;
                    break;
                }
        return T2W_FILE != null;
    }

    public static void setS2w(int value, Context context) {
        Control.runCommand(String.valueOf(value), S2W_FILE, Control.CommandType.GENERIC, context);
    }

    public static int getS2wValue() {
        return Utils.stringToInt(Utils.readFile(S2W_FILE));
    }

    public static List<String> getS2wMenu(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        if (S2W_FILE != null) {
            switch (S2W_FILE) {
                case S2W_ONLY:
                    list.add(context.getString(R.string.enabled));
                    break;
                case SW2:
                    list.add(context.getString(R.string.s2w) + " + " + context.getString(R.string.s2s));
                    list.add(context.getString(R.string.s2s));
                    break;
            }
        }
        return list;
    }

    public static boolean hasS2w() {
        if (S2W_FILE == null)
            for (String file : S2W_ARRY)
                if (Utils.existFile(file)) {
                    S2W_FILE = file;
                    break;
                }
        return S2W_FILE != null;
    }

    public static void setDt2w(int value, Context context) {
        Control.runCommand(String.valueOf(value), DT2W_FILE, Control.CommandType.GENERIC, context);
    }

    public static int getDt2wValue() {
        if (Utils.existFile(DT2W_FILE))
            return Utils.stringToInt(Utils.readFile(DT2W_FILE));
        return 0;
    }

    public static List<String> getDt2wMenu(Context context) {
        List<String> list = new ArrayList<>();
        if (DT2W_FILE != null) {
            list.add(context.getString(R.string.disabled));
            switch (DT2W_FILE) {
                case LGE_TOUCH_CORE_DT2W:
                    list.add(context.getString(R.string.center));
                    list.add(context.getString(R.string.full));
                    list.add(context.getString(R.string.bottom_half));
                    list.add(context.getString(R.string.top_half));
                    break;
                case DT2W:
                    list.add(context.getString(R.string.halfscreen));
                    list.add(context.getString(R.string.fullscreen));
                    break;
                default:
                    list.add(context.getString(R.string.enabled));
                    break;
            }
        }
        return list;
    }

    public static boolean hasDt2w() {
        if (DT2W_FILE == null)
            for (String file : DT2W_ARRAY)
                if (Utils.existFile(file)) {
                    DT2W_FILE = file;
                    break;
                }
        return DT2W_FILE != null;
    }

    public static boolean hasWake() {
        for (String[] wakes : WAKE_ARRAY)
            for (String file : wakes)
                if (Utils.existFile(file)) return true;
        return false;
    }

}
