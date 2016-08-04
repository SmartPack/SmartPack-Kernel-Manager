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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 03.05.16.
 */
public class CoreCtl {

    private static final String CORE_CTL = "/sys/devices/system/cpu/cpu%d/core_ctl";
    private static final String HCUBE = "/sys/devices/system/cpu/cpu%d/hcube";
    private static String PARENT;
    private static final String ENABLE = "/hc_on";
    private static final String IS_BIG_CLUSTER = "/is_big_cluster";
    private static final String MIN_CPUS = "/min_cpus";
    private static final String BUSY_DOWN_THRESHOLD = "/busy_down_thres";
    private static final String BUSY_UP_THRESHOLD = "/busy_up_thres";
    private static final String OFFLINE_DELAY_MS = "/offline_delay_ms";

    private static final List<String> sFiles = new ArrayList<>();

    static {
        sFiles.add(CORE_CTL);
        sFiles.add(HCUBE);
    }

    public static void setOfflineDelayMs(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + OFFLINE_DELAY_MS), PARENT + OFFLINE_DELAY_MS, context);
    }

    public static int getOfflineDelayMs() {
        return Utils.strToInt(Utils.readFile(PARENT + OFFLINE_DELAY_MS));
    }

    public static boolean hasOfflineDelayMs() {
        return Utils.existFile(PARENT + OFFLINE_DELAY_MS);
    }

    public static void setBusyUpThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + BUSY_UP_THRESHOLD),
                PARENT + BUSY_UP_THRESHOLD, context);
    }

    public static int getBusyUpThreshold() {
        String value = Utils.readFile(PARENT + BUSY_UP_THRESHOLD);
        if (value.contains(" ")) {
            return Utils.strToInt(value.split(" ")[0]);
        }
        return Utils.strToInt(value);
    }

    public static boolean hasBusyUpThreshold() {
        return Utils.existFile(PARENT + BUSY_UP_THRESHOLD);
    }

    public static void setBusyDownThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + BUSY_DOWN_THRESHOLD),
                PARENT + BUSY_DOWN_THRESHOLD, context);
    }

    public static int getBusyDownThreshold() {
        String value = Utils.readFile(PARENT + BUSY_DOWN_THRESHOLD);
        if (value.contains(" ")) {
            return Utils.strToInt(value.split(" ")[0]);
        }
        return Utils.strToInt(value);
    }

    public static boolean hasBusyDownThreshold() {
        return Utils.existFile(PARENT + BUSY_DOWN_THRESHOLD);
    }

    public static void setMinCpus(int min, int cpu, Context context) {
        setMinCpus(min, cpu, ApplyOnBootFragment.CPU_HOTPLUG, context);
    }

    public static void setMinCpus(int min, int cpu, String category, Context context) {
        Control.runSetting(Control.write(String.valueOf(min), Utils.strFormat(PARENT + MIN_CPUS, cpu)), category,
                Utils.strFormat(PARENT + MIN_CPUS, cpu), context);
    }

    public static boolean hasMinCpus() {
        return Utils.existFile(Utils.strFormat(PARENT + MIN_CPUS, 0));
    }

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARENT + ENABLE), PARENT + ENABLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(PARENT + ENABLE).equals("1");
    }

    public static boolean hasEnable() {
        return Utils.existFile(PARENT + ENABLE);
    }

    public static boolean supported() {
        if (PARENT != null) return true;
        String parent = null;
        for (String file : sFiles) {
            if (Utils.existFile(Utils.strFormat(file, 0))) {
                parent = file;
                break;
            }
        }
        if (parent == null) return false;

        if (Utils.existFile(Utils.strFormat(parent, CPUFreq.getBigCpu()))) {
            PARENT = Utils.strFormat(parent, CPUFreq.getBigCpu());
            if (Utils.existFile(PARENT + IS_BIG_CLUSTER)) {
                PARENT = Utils.readFile(PARENT + IS_BIG_CLUSTER).equals("1") ? PARENT : null;
                return true;
            }
        }
        return false;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
