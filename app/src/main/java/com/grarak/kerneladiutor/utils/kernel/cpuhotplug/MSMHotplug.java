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
 * Created by willi on 08.05.16.
 */
public class MSMHotplug {

    private static final String HOTPLUG_MSM = "/sys/module/msm_hotplug";
    private static final String HOTPLUG_MSM_ENABLE = HOTPLUG_MSM + "/enabled";
    private static final String HOTPLUG_MSM_ENABLE_2 = HOTPLUG_MSM + "/msm_enabled";
    private static final String HOTPLUG_MSM_DEBUG_MASK = HOTPLUG_MSM + "/parameters/debug_mask";
    private static final String HOTPLUG_MSM_MIN_CPUS_ONLINE = HOTPLUG_MSM + "/min_cpus_online";
    private static final String HOTPLUG_MSM_MAX_CPUS_ONLINE = HOTPLUG_MSM + "/max_cpus_online";
    private static final String HOTPLUG_MSM_CPUS_BOOSTED = HOTPLUG_MSM + "/cpus_boosted";
    private static final String HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP = HOTPLUG_MSM + "/max_cpus_online_susp";
    private static final String HOTPLUG_MSM_BOOST_LOCK_DURATION = HOTPLUG_MSM + "/boost_lock_duration";
    private static final String HOTPLUG_MSM_DOWN_LOCK_DURATION = HOTPLUG_MSM + "/down_lock_duration";
    private static final String HOTPLUG_MSM_HISTORY_SIZE = HOTPLUG_MSM + "/history_size";
    private static final String HOTPLUG_MSM_UPDATE_RATE = HOTPLUG_MSM + "/update_rate";
    private static final String HOTPLUG_MSM_UPDATE_RATES = HOTPLUG_MSM + "/update_rates";
    private static final String HOTPLUG_MSM_FAST_LANE_LOAD = HOTPLUG_MSM + "/fast_lane_load";
    private static final String HOTPLUG_MSM_FAST_LANE_MIN_FREQ = HOTPLUG_MSM + "/fast_lane_min_freq";
    private static final String HOTPLUG_MSM_OFFLINE_LOAD = HOTPLUG_MSM + "/offline_load";
    private static final String HOTPLUG_MSM_IO_IS_BUSY = HOTPLUG_MSM + "/io_is_busy";
    private static final String HOTPLUG_MSM_HP_IO_IS_BUSY = HOTPLUG_MSM + "/hp_io_is_busy";
    private static final String HOTPLUG_MSM_SUSPEND_MAX_CPUS = HOTPLUG_MSM + "/suspend_max_cpus";
    private static final String HOTPLUG_MSM_SUSPEND_FREQ = HOTPLUG_MSM + "/suspend_freq";
    private static final String HOTPLUG_MSM_SUSPEND_MAX_FREQ = HOTPLUG_MSM + "/suspend_max_freq";
    private static final String HOTPLUG_MSM_SUSPEND_DEFER_TIME = HOTPLUG_MSM + "/suspend_defer_time";

    private static String ENABLE_FILE;
    private static String UPDATE_RATE_FILE;
    private static String IO_IS_BUSY_FILE;
    private static String SUSPEND_FREQ_FILE;

    public static void setMsmHotplugSuspendDeferTime(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_SUSPEND_DEFER_TIME),
                HOTPLUG_MSM_SUSPEND_DEFER_TIME, context);
    }

    public static int getMsmHotplugSuspendDeferTime() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_SUSPEND_DEFER_TIME));
    }

    public static boolean hasMsmHotplugSuspendDeferTime() {
        return Utils.existFile(HOTPLUG_MSM_SUSPEND_DEFER_TIME);
    }

    public static void setMsmHotplugSuspendFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), SUSPEND_FREQ_FILE), SUSPEND_FREQ_FILE, context);
    }

    public static int getMsmHotplugSuspendFreq() {
        return Utils.strToInt(Utils.readFile(SUSPEND_FREQ_FILE));
    }

    public static boolean hasMsmHotplugSuspendFreq() {
        if (!CPUFreq.hasMaxScreenOffFreq()) {
            if (Utils.existFile(HOTPLUG_MSM_SUSPEND_FREQ)) {
                SUSPEND_FREQ_FILE = HOTPLUG_MSM_SUSPEND_FREQ;
            } else if (Utils.existFile(HOTPLUG_MSM_SUSPEND_MAX_FREQ)) {
                SUSPEND_FREQ_FILE = HOTPLUG_MSM_SUSPEND_MAX_FREQ;
            }
        }
        return SUSPEND_FREQ_FILE != null;
    }

    public static void setMsmHotplugSuspendMaxCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_SUSPEND_MAX_CPUS),
                HOTPLUG_MSM_SUSPEND_MAX_CPUS, context);
    }

    public static int getMsmHotplugSuspendMaxCpus() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_SUSPEND_MAX_CPUS));
    }

    public static boolean hasMsmHotplugSuspendMaxCpus() {
        return Utils.existFile(HOTPLUG_MSM_SUSPEND_MAX_CPUS);
    }

    public static void enableMsmHotplugIoIsBusy(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", IO_IS_BUSY_FILE), IO_IS_BUSY_FILE, context);
    }

    public static boolean isMsmHotplugIoIsBusyEnabled() {
        return Utils.readFile(IO_IS_BUSY_FILE).equals("1");
    }

    public static boolean hasMsmHotplugIoIsBusy() {
        if (Utils.existFile(HOTPLUG_MSM_IO_IS_BUSY)) {
            IO_IS_BUSY_FILE = HOTPLUG_MSM_IO_IS_BUSY;
        } else if (Utils.existFile(HOTPLUG_MSM_HP_IO_IS_BUSY)) {
            IO_IS_BUSY_FILE = HOTPLUG_MSM_HP_IO_IS_BUSY;
        }
        return IO_IS_BUSY_FILE != null;
    }

    public static void setMsmHotplugOfflineLoad(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_OFFLINE_LOAD), HOTPLUG_MSM_OFFLINE_LOAD, context);
    }

    public static int getMsmHotplugOfflineLoad() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_OFFLINE_LOAD));
    }

    public static boolean hasMsmHotplugOfflineLoad() {
        return Utils.existFile(HOTPLUG_MSM_OFFLINE_LOAD);
    }

    public static void setMsmHotplugFastLaneMinFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_FAST_LANE_MIN_FREQ),
                HOTPLUG_MSM_FAST_LANE_MIN_FREQ, context);
    }

    public static int getMsmHotplugFastLaneMinFreq() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_FAST_LANE_MIN_FREQ));
    }

    public static boolean hasMsmHotplugFastLaneMinFreq() {
        return Utils.existFile(HOTPLUG_MSM_FAST_LANE_MIN_FREQ);
    }

    public static void setMsmHotplugFastLaneLoad(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_FAST_LANE_LOAD), HOTPLUG_MSM_FAST_LANE_LOAD, context);
    }

    public static int getMsmHotplugFastLaneLoad() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_FAST_LANE_LOAD));
    }

    public static boolean hasMsmHotplugFastLaneLoad() {
        return Utils.existFile(HOTPLUG_MSM_FAST_LANE_LOAD);
    }

    public static void setMsmHotplugUpdateRate(int value, Context context) {
        run(Control.write(String.valueOf(value), UPDATE_RATE_FILE), UPDATE_RATE_FILE, context);
    }

    public static int getMsmHotplugUpdateRate() {
        return Utils.strToInt(Utils.readFile(UPDATE_RATE_FILE));
    }

    public static boolean hasMsmHotplugUpdateRate() {
        if (Utils.existFile(HOTPLUG_MSM_UPDATE_RATE)) {
            UPDATE_RATE_FILE = HOTPLUG_MSM_UPDATE_RATE;
        } else if (Utils.existFile(HOTPLUG_MSM_UPDATE_RATES)) {
            UPDATE_RATE_FILE = HOTPLUG_MSM_UPDATE_RATES;
        }
        return UPDATE_RATE_FILE != null;
    }

    public static void setMsmHotplugHistorySize(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_HISTORY_SIZE), HOTPLUG_MSM_HISTORY_SIZE, context);
    }

    public static int getMsmHotplugHistorySize() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_HISTORY_SIZE));
    }

    public static boolean hasMsmHotplugHistorySize() {
        return Utils.existFile(HOTPLUG_MSM_HISTORY_SIZE);
    }

    public static void setMsmHotplugDownLockDuration(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_DOWN_LOCK_DURATION),
                HOTPLUG_MSM_DOWN_LOCK_DURATION, context);
    }

    public static int getMsmHotplugDownLockDuration() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_DOWN_LOCK_DURATION));
    }

    public static boolean hasMsmHotplugDownLockDuration() {
        return Utils.existFile(HOTPLUG_MSM_DOWN_LOCK_DURATION);
    }

    public static void setMsmHotplugBoostLockDuration(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_BOOST_LOCK_DURATION),
                HOTPLUG_MSM_BOOST_LOCK_DURATION, context);
    }

    public static int getMsmHotplugBoostLockDuration() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_BOOST_LOCK_DURATION));
    }

    public static boolean hasMsmHotplugBoostLockDuration() {
        return Utils.existFile(HOTPLUG_MSM_BOOST_LOCK_DURATION);
    }

    public static void setMsmHotplugMaxCpusOnlineSusp(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP),
                HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP, context);
    }

    public static int getMsmHotplugMaxCpusOnlineSusp() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP));
    }

    public static boolean hasMsmHotplugMaxCpusOnlineSusp() {
        return Utils.existFile(HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP) && !CPUFreq.hasMaxScreenOffFreq();
    }

    public static void setMsmHotplugCpusBoosted(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_CPUS_BOOSTED), HOTPLUG_MSM_CPUS_BOOSTED, context);
    }

    public static int getMsmHotplugCpusBoosted() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_CPUS_BOOSTED));
    }

    public static boolean hasMsmHotplugCpusBoosted() {
        return Utils.existFile(HOTPLUG_MSM_CPUS_BOOSTED);
    }

    public static void setMsmHotplugMaxCpusOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_MAX_CPUS_ONLINE),
                HOTPLUG_MSM_MAX_CPUS_ONLINE, context);
    }

    public static int getMsmHotplugMaxCpusOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_MAX_CPUS_ONLINE));
    }

    public static boolean hasMsmHotplugMaxCpusOnline() {
        return Utils.existFile(HOTPLUG_MSM_MAX_CPUS_ONLINE);
    }

    public static void setMsmHotplugMinCpusOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_MSM_MIN_CPUS_ONLINE),
                HOTPLUG_MSM_MIN_CPUS_ONLINE, context);
    }

    public static int getMsmHotplugMinCpusOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_MSM_MIN_CPUS_ONLINE));
    }

    public static boolean hasMsmHotplugMinCpusOnline() {
        return Utils.existFile(HOTPLUG_MSM_MIN_CPUS_ONLINE);
    }

    public static void enableMsmHotplugDebugMask(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_MSM_DEBUG_MASK), HOTPLUG_MSM_DEBUG_MASK, context);
    }

    public static boolean isMsmHotplugDebugMaskEnabled() {
        return Utils.readFile(HOTPLUG_MSM_DEBUG_MASK).equals("1");
    }

    public static boolean hasMsmHotplugDebugMask() {
        return Utils.existFile(HOTPLUG_MSM_DEBUG_MASK);
    }

    public static void enableMsmHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ENABLE_FILE), ENABLE_FILE, context);
    }

    public static boolean isMsmHotplugEnabled() {
        return Utils.readFile(ENABLE_FILE).equals("1");
    }

    public static boolean hasMsmHotplugEnable() {
        if (Utils.existFile(HOTPLUG_MSM_ENABLE)) {
            ENABLE_FILE = HOTPLUG_MSM_ENABLE;
        } else if (Utils.existFile(HOTPLUG_MSM_ENABLE_2)) {
            ENABLE_FILE = HOTPLUG_MSM_ENABLE_2;
        }
        return ENABLE_FILE != null;
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_MSM);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
