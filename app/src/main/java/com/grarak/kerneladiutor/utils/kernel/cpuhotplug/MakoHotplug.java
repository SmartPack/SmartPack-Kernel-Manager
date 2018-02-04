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
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 09.05.16.
 */
public class MakoHotplug {

    private static final String MAKO_HOTPLUG = "/sys/class/misc/mako_hotplug_control";
    private static final String MAKO_HOTPLUG_ENABLED = MAKO_HOTPLUG + "/enabled";
    private static final String MAKO_HOTPLUG_CORES_ON_TOUCH = MAKO_HOTPLUG + "/cores_on_touch";
    private static final String MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT = MAKO_HOTPLUG + "/cpufreq_unplug_limit";
    private static final String MAKO_HOTPLUG_FIRST_LEVEL = MAKO_HOTPLUG + "/first_level";
    private static final String MAKO_HOTPLUG_HIGH_LOAD_COUNTER = MAKO_HOTPLUG + "/high_load_counter";
    private static final String MAKO_HOTPLUG_LOAD_THRESHOLD = MAKO_HOTPLUG + "/load_threshold";
    private static final String MAKO_HOTPLUG_MAX_LOAD_COUNTER = MAKO_HOTPLUG + "/max_load_counter";
    private static final String MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE = MAKO_HOTPLUG + "/min_time_cpu_online";
    private static final String MAKO_HOTPLUG_MIN_CORES_ONLINE = MAKO_HOTPLUG + "/min_cores_online";
    private static final String MAKO_HOTPLUG_TIMER = MAKO_HOTPLUG + "/timer";
    private static final String MAKO_HOTPLUG_SUSPEND_FREQ = MAKO_HOTPLUG + "/suspend_frequency";

    public static void setMakoHotplugSuspendFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_SUSPEND_FREQ), MAKO_HOTPLUG_SUSPEND_FREQ, context);
    }

    public static int getMakoHotplugSuspendFreq() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_SUSPEND_FREQ));
    }

    public static boolean hasMakoHotplugSuspendFreq() {
        return !CPUFreq.getInstance().hasMaxScreenOffFreq() && Utils.existFile(MAKO_HOTPLUG_SUSPEND_FREQ);
    }

    public static void setMakoHotplugTimer(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_TIMER), MAKO_HOTPLUG_TIMER, context);
    }

    public static int getMakoHotplugTimer() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_TIMER));
    }

    public static boolean hasMakoHotplugTimer() {
        return Utils.existFile(MAKO_HOTPLUG_TIMER);
    }

    public static void setMakoHotplugMinCoresOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_MIN_CORES_ONLINE),
                MAKO_HOTPLUG_MIN_CORES_ONLINE, context);
    }

    public static int getMakoHotplugMinCoresOnline() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_MIN_CORES_ONLINE));
    }

    public static boolean hasMakoHotplugMinCoresOnline() {
        return Utils.existFile(MAKO_HOTPLUG_MIN_CORES_ONLINE);
    }

    public static void setMakoHotplugMinTimeCpuOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE),
                MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE, context);
    }

    public static int getMakoHotplugMinTimeCpuOnline() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE));
    }

    public static boolean hasMakoHotplugMinTimeCpuOnline() {
        return Utils.existFile(MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE);
    }

    public static void setMakoHotplugMaxLoadCounter(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_MAX_LOAD_COUNTER),
                MAKO_HOTPLUG_MAX_LOAD_COUNTER, context);
    }

    public static int getMakoHotplugMaxLoadCounter() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_MAX_LOAD_COUNTER));
    }

    public static boolean hasMakoHotplugMaxLoadCounter() {
        return Utils.existFile(MAKO_HOTPLUG_MAX_LOAD_COUNTER);
    }

    public static void setMakoHotplugLoadThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_LOAD_THRESHOLD),
                MAKO_HOTPLUG_LOAD_THRESHOLD, context);
    }

    public static int getMakoHotplugLoadThreshold() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_LOAD_THRESHOLD));
    }

    public static boolean hasMakoHotplugLoadThreshold() {
        return Utils.existFile(MAKO_HOTPLUG_LOAD_THRESHOLD);
    }

    public static void setMakoHotplugHighLoadCounter(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_HIGH_LOAD_COUNTER),
                MAKO_HOTPLUG_HIGH_LOAD_COUNTER, context);
    }

    public static int getMakoHotplugHighLoadCounter() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_HIGH_LOAD_COUNTER));
    }

    public static boolean hasMakoHotplugHighLoadCounter() {
        return Utils.existFile(MAKO_HOTPLUG_HIGH_LOAD_COUNTER);
    }

    public static void setMakoHotplugFirstLevel(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_FIRST_LEVEL), MAKO_HOTPLUG_FIRST_LEVEL, context);
    }

    public static int getMakoHotplugFirstLevel() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_FIRST_LEVEL));
    }

    public static boolean hasMakoHotplugFirstLevel() {
        return Utils.existFile(MAKO_HOTPLUG_FIRST_LEVEL);
    }

    public static void setMakoHotplugCpuFreqUnplugLimit(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT),
                MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT, context);
    }

    public static int getMakoHotplugCpuFreqUnplugLimit() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT));
    }

    public static boolean hasMakoHotplugCpuFreqUnplugLimit() {
        return Utils.existFile(MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT);
    }

    public static void setMakoHotplugCoresOnTouch(int value, Context context) {
        run(Control.write(String.valueOf(value), MAKO_HOTPLUG_CORES_ON_TOUCH),
                MAKO_HOTPLUG_CORES_ON_TOUCH, context);
    }

    public static int getMakoHotplugCoresOnTouch() {
        return Utils.strToInt(Utils.readFile(MAKO_HOTPLUG_CORES_ON_TOUCH));
    }

    public static boolean hasMakoHotplugCoresOnTouch() {
        return Utils.existFile(MAKO_HOTPLUG_CORES_ON_TOUCH);
    }

    public static void enableMakoHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MAKO_HOTPLUG_ENABLED), MAKO_HOTPLUG_ENABLED, context);
    }

    public static boolean isMakoHotplugEnabled() {
        return Utils.readFile(MAKO_HOTPLUG_ENABLED).equals("1");
    }

    public static boolean hasMakoHotplugEnable() {
        return Utils.existFile(MAKO_HOTPLUG_ENABLED);
    }

    public static boolean supported() {
        return Utils.existFile(MAKO_HOTPLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
