/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
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

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 07, 2018
 */

public class MSMSleeper {

    private static final String PARENT = "/sys/devices/platform/msm_sleeper";
    private static final String ENABLE = PARENT + "/enabled";
    private static final String DOWN_COUNT_MAX = PARENT + "/down_count_max";
    private static final String UP_COUNT_MAX = PARENT + "/up_count_max";
    private static final String SUSPEND_MAX_ONLINE = PARENT + "/suspend_max_online";
    private static final String UP_THRESHOLD = PARENT + "/up_threshold";
    private static final String MAX_ONLINE = PARENT + "/max_online";

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ENABLE), ENABLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(ENABLE).equals("1");
    }

    public static boolean hasEnable() {
        return Utils.existFile(ENABLE);
    }

    public static void setMaxOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_ONLINE), MAX_ONLINE, context);
    }

    public static int getMaxOnline() {
        return Utils.strToInt(Utils.readFile(MAX_ONLINE));
    }

    public static boolean hasMaxOnline() {
        return Utils.existFile(MAX_ONLINE);
    }

    public static void setUpCountMax(int value, Context context) {
        run(Control.write(String.valueOf(value), UP_COUNT_MAX), UP_COUNT_MAX, context);
    }

    public static int getUpCountMax() {
        return Utils.strToInt(Utils.readFile(UP_COUNT_MAX));
    }

    public static boolean hasUpCountMax() {
        return Utils.existFile(UP_COUNT_MAX);
    }

    public static void setDownCountMax(int value, Context context) {
        run(Control.write(String.valueOf(value), DOWN_COUNT_MAX), DOWN_COUNT_MAX, context);
    }

    public static int getDownCountMax() {
        return Utils.strToInt(Utils.readFile(DOWN_COUNT_MAX));
    }

    public static boolean hasDownCountMax() {
        return Utils.existFile(DOWN_COUNT_MAX);
    }

    public static void setSusMaxOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), SUSPEND_MAX_ONLINE), SUSPEND_MAX_ONLINE, context);
    }

    public static int getSusMaxOnline() {
        return Utils.strToInt(Utils.readFile(SUSPEND_MAX_ONLINE));
    }

    public static boolean hasSusMaxOnline() {
        return Utils.existFile(SUSPEND_MAX_ONLINE);
    }

    public static void setUpThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), UP_THRESHOLD), UP_THRESHOLD, context);
    }

    public static int getUpThreshold() {
        return Utils.strToInt(Utils.readFile(UP_THRESHOLD));
    }

    public static boolean hasUpThreshold() {
        return Utils.existFile(UP_THRESHOLD);
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
