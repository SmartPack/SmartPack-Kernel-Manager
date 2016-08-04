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
package com.grarak.kerneladiutor.utils.kernel.cpuhotplug;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 11.05.16.
 */
public class ThunderPlug {

    private static final String HOTPLUG_THUNDER_PLUG = "/sys/kernel/thunderplug";
    private static final String HOTPLUG_THUNDER_PLUG_ENABLE = HOTPLUG_THUNDER_PLUG + "/hotplug_enabled";
    private static final String HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS = HOTPLUG_THUNDER_PLUG + "/suspend_cpus";
    private static final String HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL = HOTPLUG_THUNDER_PLUG + "/endurance_level";
    private static final String HOTPLUG_THUNDER_PLUG_SAMPLING_RATE = HOTPLUG_THUNDER_PLUG + "/sampling_rate";
    private static final String HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD = HOTPLUG_THUNDER_PLUG + "/load_threshold";
    private static final String HOTPLUG_THUNDER_PLUG_TOUCH_BOOST = HOTPLUG_THUNDER_PLUG + "/touch_boost";

    public static void enableThunderPlugTouchBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_THUNDER_PLUG_TOUCH_BOOST),
                HOTPLUG_THUNDER_PLUG_TOUCH_BOOST, context);
    }

    public static boolean isThunderPlugTouchBoostEnabled() {
        return Utils.readFile(HOTPLUG_THUNDER_PLUG_TOUCH_BOOST).equals("1");
    }

    public static boolean hasThunderPlugTouchBoost() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_TOUCH_BOOST);
    }

    public static void setThunderPlugLoadThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD),
                HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD, context);
    }

    public static int getThunderPlugLoadThreshold() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD));
    }

    public static boolean hasThunderPlugLoadThreshold() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD);
    }

    public static void setThunderPlugSamplingRate(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_SAMPLING_RATE),
                HOTPLUG_THUNDER_PLUG_SAMPLING_RATE, context);
    }

    public static int getThunderPlugSamplingRate() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_SAMPLING_RATE));
    }

    public static boolean hasThunderPlugSamplingRate() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_SAMPLING_RATE);
    }

    public static void setThunderPlugEnduranceLevel(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL),
                HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL, context);
    }

    public static int getThunderPlugEnduranceLevel() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL));
    }

    public static boolean hasThunderPlugEnduranceLevel() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL);
    }

    public static void setThunderPlugSuspendCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS),
                HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS, context);
    }

    public static int getThunderPlugSuspendCpus() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS));
    }

    public static boolean hasThunderPlugSuspendCpus() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS);
    }

    public static void enableThunderPlug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_THUNDER_PLUG_ENABLE), HOTPLUG_THUNDER_PLUG_ENABLE, context);
    }

    public static boolean isThunderPlugEnabled() {
        return Utils.readFile(HOTPLUG_THUNDER_PLUG_ENABLE).equals("1");
    }

    public static boolean hasThunderPlugEnable() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
