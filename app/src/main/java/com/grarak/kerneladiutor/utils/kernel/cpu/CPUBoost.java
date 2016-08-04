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
package com.grarak.kerneladiutor.utils.kernel.cpu;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 05.05.16.
 */
public class CPUBoost {

    private static final String CPU_BOOST = "/sys/module/cpu_boost/parameters";
    private static final String CPU_BOOST_ENABLE = CPU_BOOST + "/cpu_boost";
    private static final String CPU_BOOST_ENABLE_2 = CPU_BOOST + "/cpuboost_enable";
    private static final String CPU_BOOST_DEBUG_MASK = CPU_BOOST + "/debug_mask";
    private static final String CPU_BOOST_MS = CPU_BOOST + "/boost_ms";
    private static final String CPU_BOOST_SYNC_THRESHOLD = CPU_BOOST + "/sync_threshold";
    private static final String CPU_BOOST_INPUT_MS = CPU_BOOST + "/input_boost_ms";
    private static final String CPU_BOOST_INPUT_BOOST_FREQ = CPU_BOOST + "/input_boost_freq";
    private static final String CPU_BOOST_WAKEUP = CPU_BOOST + "/wakeup_boost";
    private static final String CPU_BOOST_HOTPLUG = CPU_BOOST + "/hotplug_boost";

    private static String ENABLE;

    public static void enableCpuBoostWakeup(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_WAKEUP), CPU_BOOST_WAKEUP, context);
    }

    public static boolean isCpuBoostWakeupEnabled() {
        return Utils.readFile(CPU_BOOST_WAKEUP).equals("Y");
    }

    public static boolean hasCpuBoostWakeup() {
        return Utils.existFile(CPU_BOOST_WAKEUP);
    }

    public static void enableCpuBoostHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_HOTPLUG), CPU_BOOST_HOTPLUG, context);
    }

    public static boolean isCpuBoostHotplugEnabled() {
        return Utils.readFile(CPU_BOOST_HOTPLUG).equals("Y");
    }

    public static boolean hasCpuBoostHotplug() {
        return Utils.existFile(CPU_BOOST_HOTPLUG);
    }

    public static void setCpuBoostInputMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_INPUT_MS), CPU_BOOST_INPUT_MS, context);
    }

    public static int getCpuBootInputMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_INPUT_MS));
    }

    public static boolean hasCpuBoostInputMs() {
        return Utils.existFile(CPU_BOOST_INPUT_MS);
    }

    public static void setCpuBoostInputFreq(int value, int core, Context context) {
        if (Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ).contains(":")) {
            run(Control.write(core + ":" + value, CPU_BOOST_INPUT_BOOST_FREQ),
                    CPU_BOOST_INPUT_BOOST_FREQ + core, context);
        } else {
            run(Control.write(String.valueOf(value), CPU_BOOST_INPUT_BOOST_FREQ),
                    CPU_BOOST_INPUT_BOOST_FREQ, context);
        }
    }

    public static List<Integer> getCpuBootInputFreq() {
        List<Integer> list = new ArrayList<>();
        String value = Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ);
        if (value.contains(":")) {
            for (String line : value.split(" ")) {
                int core = Utils.strToInt(line.split(":")[0]);
                String freq = line.split(":")[1];
                try {
                    list.add(freq.equals("0") ? 0 : CPUFreq.getFreqs(core).indexOf(Utils.strToInt(freq)) + 1);
                } catch (NullPointerException ignored) {
                }
            }
        } else {
            list.add(value.equals("0") ? 0 : CPUFreq.getFreqs().indexOf(Utils.strToInt(value)) + 1);
        }
        return list;
    }

    public static boolean hasCpuBoostInputFreq() {
        return Utils.existFile(CPU_BOOST_INPUT_BOOST_FREQ);
    }

    public static void setCpuBoostSyncThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_SYNC_THRESHOLD), CPU_BOOST_SYNC_THRESHOLD, context);
    }

    public static int getCpuBootSyncThreshold() {
        return CPUFreq.getFreqs().indexOf(Utils.strToInt(Utils.readFile(CPU_BOOST_SYNC_THRESHOLD))) + 1;
    }

    public static boolean hasCpuBoostSyncThreshold() {
        return Utils.existFile(CPU_BOOST_SYNC_THRESHOLD);
    }

    public static void setCpuBoostMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_MS), CPU_BOOST_MS, context);
    }

    public static int getCpuBootMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_MS));
    }

    public static boolean hasCpuBoostMs() {
        return Utils.existFile(CPU_BOOST_MS);
    }

    public static void enableCpuBoostDebugMask(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CPU_BOOST_DEBUG_MASK), CPU_BOOST_DEBUG_MASK, context);
    }

    public static boolean isCpuBoostDebugMaskEnabled() {
        return Utils.readFile(CPU_BOOST_DEBUG_MASK).equals("1");
    }

    public static boolean hasCpuBoostDebugMask() {
        return Utils.existFile(CPU_BOOST_DEBUG_MASK);
    }

    public static void enableCpuBoost(boolean enable, Context context) {
        run(Control.write(ENABLE.equals(CPU_BOOST_ENABLE_2) ? enable ? "Y" : "N" : enable ? "1" : "0", ENABLE),
                ENABLE, context);
    }

    public static boolean isEnabled() {
        String value = Utils.readFile(ENABLE);
        return value.equals("1") || value.equals("Y");
    }

    public static boolean hasEnable() {
        if (ENABLE == null) {
            if (Utils.existFile(CPU_BOOST_ENABLE)) ENABLE = CPU_BOOST_ENABLE;
            else if (Utils.existFile(CPU_BOOST_ENABLE_2)) ENABLE = CPU_BOOST_ENABLE_2;
        }
        return ENABLE != null;
    }

    public static boolean supported() {
        return hasEnable() || hasCpuBoostDebugMask() || hasCpuBoostMs() || hasCpuBoostSyncThreshold()
                || hasCpuBoostInputFreq() || hasCpuBoostInputMs() || hasCpuBoostHotplug() || hasCpuBoostWakeup();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
