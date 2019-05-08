/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
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
 */

package com.smartpack.kernelmanager.utils;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Originally created by Willi on 09.05.16
 *
 * Modified for SmartPack-Kernel Manager by
 *
 * sunilpaulmathew <sunil.kde@gmail.com> on April 22, 2019
 */

public class AutoSMP {

    private static final String HOTPLUG_AUTOSMP_PARAMETERS = "/sys/module/autosmp/parameters";
    private static final String HOTPLUG_AUTOSMP_CONF = "/sys/kernel/autosmp/conf";
    private static final String HOTPLUG_AUTOSMP_ENABLE = HOTPLUG_AUTOSMP_PARAMETERS + "/enabled";
    private static final String HOTPLUG_AUTOSMP_HOTPLUG_SUSPEND = HOTPLUG_AUTOSMP_PARAMETERS + "/hotplug_suspend";
    private static final String HOTPLUG_AUTOSMP_CPUFREQ_DOWN = HOTPLUG_AUTOSMP_CONF + "/cpufreq_down";
    private static final String HOTPLUG_AUTOSMP_CPUFREQ_UP = HOTPLUG_AUTOSMP_CONF + "/cpufreq_up";
    private static final String HOTPLUG_AUTOSMP_CYCLE_DOWN = HOTPLUG_AUTOSMP_CONF + "/cycle_down";
    private static final String HOTPLUG_AUTOSMP_CYCLE_UP = HOTPLUG_AUTOSMP_CONF + "/cycle_up";
    private static final String HOTPLUG_AUTOSMP_MIN_BOOST_FREQ = HOTPLUG_AUTOSMP_CONF + "/min_boost_freq";
    private static final String HOTPLUG_AUTOSMP_DELAY = HOTPLUG_AUTOSMP_CONF + "/delay";
    private static final String HOTPLUG_AUTOSMP_MAX_CPUS = HOTPLUG_AUTOSMP_CONF + "/max_cpus";
    private static final String HOTPLUG_AUTOSMP_MIN_CPUS = HOTPLUG_AUTOSMP_CONF + "/min_cpus";
    private static final String HOTPLUG_AUTOSMP_SCROFF_SINGLE_CORE = HOTPLUG_AUTOSMP_CONF + "/scroff_single_core";

    public static void enableAutoSmpScroffSingleCoreActive(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_AUTOSMP_SCROFF_SINGLE_CORE),
                HOTPLUG_AUTOSMP_SCROFF_SINGLE_CORE, context);
    }

    public static boolean isAutoSmpScroffSingleCoreEnabled() {
        return Utils.readFile(HOTPLUG_AUTOSMP_SCROFF_SINGLE_CORE).equals("1");
    }

    public static boolean hasAutoSmpScroffSingleCore() {
        return Utils.existFile(HOTPLUG_AUTOSMP_SCROFF_SINGLE_CORE);
    }

    public static void setAutoSmpMinCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_MIN_CPUS),
                HOTPLUG_AUTOSMP_MIN_CPUS, context);
    }

    public static int getAutoSmpMinCpus() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_MIN_CPUS));
    }

    public static boolean hasAutoSmpMinCpus() {
        return Utils.existFile(HOTPLUG_AUTOSMP_MIN_CPUS);
    }

    public static void setAutoSmpMaxCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_MAX_CPUS),
                HOTPLUG_AUTOSMP_MAX_CPUS, context);
    }

    public static int getAutoSmpMaxCpus() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_MAX_CPUS));
    }

    public static boolean hasAutoSmpMaxCpus() {
        return Utils.existFile(HOTPLUG_AUTOSMP_MAX_CPUS);
    }

    public static void setAutoSmpDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_DELAY), HOTPLUG_AUTOSMP_DELAY, context);
    }

    public static int getAutoSmpDelay() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_DELAY));
    }

    public static boolean hasAutoSmpDelay() {
        return Utils.existFile(HOTPLUG_AUTOSMP_DELAY);
    }

    public static void setMinBoostFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_MIN_BOOST_FREQ), HOTPLUG_AUTOSMP_MIN_BOOST_FREQ, context);
    }

    public static int getMinBoostFreq() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_MIN_BOOST_FREQ));
    }

    public static boolean hasMinBoostFreq() {
        return Utils.existFile(HOTPLUG_AUTOSMP_MIN_BOOST_FREQ);
    }

    public static void enableHotplugSuspend(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_AUTOSMP_HOTPLUG_SUSPEND), HOTPLUG_AUTOSMP_HOTPLUG_SUSPEND, context);
    }

    public static boolean isHotplugSuspendEnabled() {
        return Utils.readFile(HOTPLUG_AUTOSMP_HOTPLUG_SUSPEND).equals("1");
    }

    public static boolean hasHotplugSuspend() {
        return Utils.existFile(HOTPLUG_AUTOSMP_HOTPLUG_SUSPEND);
    }

    public static void setAutoSmpCycleUp(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_CYCLE_UP),
                HOTPLUG_AUTOSMP_CYCLE_UP, context);
    }

    public static int getAutoSmpCycleUp() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_CYCLE_UP));
    }

    public static boolean hasAutoSmpCycleUp() {
        return Utils.existFile(HOTPLUG_AUTOSMP_CYCLE_UP);
    }

    public static void setAutoSmpCycleDown(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_CYCLE_DOWN),
                HOTPLUG_AUTOSMP_CYCLE_DOWN, context);
    }

    public static int getAutoSmpCycleDown() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_CYCLE_DOWN));
    }

    public static boolean hasAutoSmpCycleDown() {
        return Utils.existFile(HOTPLUG_AUTOSMP_CYCLE_DOWN);
    }

    public static void setAutoSmpCpufreqUp(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_CPUFREQ_UP),
                HOTPLUG_AUTOSMP_CPUFREQ_UP, context);
    }

    public static int getAutoSmpCpufreqUp() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_CPUFREQ_UP));
    }

    public static boolean hasAutoSmpCpufreqUp() {
        return Utils.existFile(HOTPLUG_AUTOSMP_CPUFREQ_UP);
    }

    public static void setAutoSmpCpufreqDown(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_AUTOSMP_CPUFREQ_DOWN),
                HOTPLUG_AUTOSMP_CPUFREQ_DOWN, context);
    }

    public static int getAutoSmpCpufreqDown() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_AUTOSMP_CPUFREQ_DOWN));
    }

    public static boolean hasAutoSmpCpufreqDown() {
        return Utils.existFile(HOTPLUG_AUTOSMP_CPUFREQ_DOWN);
    }

    public static void enableAutoSmp(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", HOTPLUG_AUTOSMP_ENABLE), HOTPLUG_AUTOSMP_ENABLE, context);
    }

    public static boolean isAutoSmpEnabled() {
        return Utils.readFile(HOTPLUG_AUTOSMP_ENABLE).equals("Y");
    }

    public static boolean hasAutoSmpEnable() {
        return Utils.existFile(HOTPLUG_AUTOSMP_ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_AUTOSMP_PARAMETERS) || Utils.existFile(HOTPLUG_AUTOSMP_CONF);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
