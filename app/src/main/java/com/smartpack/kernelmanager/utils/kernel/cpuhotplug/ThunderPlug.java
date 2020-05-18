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

package com.smartpack.kernelmanager.utils.kernel.cpuhotplug;

import android.content.Context;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
* Created by sunilpaulmathew <sunil.kde@gmail.com> on May 17, 2020
*
*/

public class ThunderPlug {

    private static final String THUNDERPLUG = "/sys/kernel/thunderplug";
    private static final String ENABLE = THUNDERPLUG + "/hotplug_enabled";
    private static final String ENDURANCE_LEVEL = THUNDERPLUG + "/endurance_level";
    private static final String LOAD_THRESHOLD = THUNDERPLUG + "/load_threshold";
    private static final String SAMPLING_RATE = THUNDERPLUG + "/sampling_rate";
    private static final String SUSPEND_CPUS = THUNDERPLUG + "/suspend_cpus";
    private static final String TOUCH_BOOST = THUNDERPLUG + "/touch_boost";

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ENABLE), ENABLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(ENABLE).equals("1");
    }

    public static boolean hasEnable() {
        return Utils.existFile(ENABLE);
    }

    public static List<String> enduranceLevel(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.quad_core_mode));
        list.add(context.getString(R.string.dual_core_mode));
        return list;
    }

    public static boolean hasEnduranceLevel() {
        return Utils.existFile(ENDURANCE_LEVEL);
    }

    public static int getEnduranceLevel() {
        return Utils.strToInt(Utils.readFile(ENDURANCE_LEVEL));
    }

    public static void setEnduranceLevel(int value, Context context) {
        run(Control.write(String.valueOf(value), ENDURANCE_LEVEL), ENDURANCE_LEVEL, context);
    }

    public static void setLoadThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), LOAD_THRESHOLD), LOAD_THRESHOLD, context);
    }

    public static int getLoadThreshold() {
        return Utils.strToInt(Utils.readFile(LOAD_THRESHOLD));
    }

    public static boolean hasLoadThreshold() {
        return Utils.existFile(LOAD_THRESHOLD);
    }

    public static void setSamplingRate(int value, Context context) {
        run(Control.write(String.valueOf(value), SAMPLING_RATE), SAMPLING_RATE, context);
    }

    public static int getSamplingRate() {
        return Utils.strToInt(Utils.readFile(SAMPLING_RATE));
    }

    public static boolean hasSamplingRate() {
        return Utils.existFile(SAMPLING_RATE);
    }

    public static void setSuspendCPUs(int value, Context context) {
        run(Control.write(String.valueOf(value), SUSPEND_CPUS), SUSPEND_CPUS, context);
    }

    public static int getSuspendCPUs() {
        return Utils.strToInt(Utils.readFile(SUSPEND_CPUS));
    }

    public static boolean hasSuspendCPUs() {
        return Utils.existFile(SUSPEND_CPUS);
    }

    public static void enableTouchBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TOUCH_BOOST), TOUCH_BOOST, context);
    }

    public static boolean isTouchBoostEnabled() {
        return Utils.readFile(TOUCH_BOOST).equals("1");
    }

    public static boolean hasTouchBoost() {
        return Utils.existFile(TOUCH_BOOST);
    }

    public static boolean supported() {
        return Utils.existFile(THUNDERPLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}