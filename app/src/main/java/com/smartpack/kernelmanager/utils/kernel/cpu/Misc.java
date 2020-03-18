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
package com.smartpack.kernelmanager.utils.kernel.cpu;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 05.05.16.
 */
public class Misc {

    private static final String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";
    private static final String CPU_WQ_POWER_SAVING = "/sys/module/workqueue/parameters/power_efficient";
    private static final String CPU_AVAILABLE_CFS_SCHEDULERS = "/sys/devices/system/cpu/sched_balance_policy/available_sched_balance_policy";
    private static final String CPU_CURRENT_CFS_SCHEDULER = "/sys/devices/system/cpu/sched_balance_policy/current_sched_balance_policy";

    private static final String CPU_QUIET = "/sys/devices/system/cpu/cpuquiet";
    private static final String CPU_QUIET_ENABLE = CPU_QUIET + "/cpuquiet_driver/enabled";
    private static final String CPU_QUIET_TEGRA_ENABLE = CPU_QUIET + "/tegra_cpuquiet/enable";
    private static final String CPU_QUIET_AVAILABLE_GOVERNORS = CPU_QUIET + "/available_governors";
    private static final String CPU_QUIET_CURRENT_GOVERNOR = CPU_QUIET + "/current_governor";
    private static final String CPU_QUIET_NR_MIN_CPUS = CPU_QUIET + "/nr_min_cpus";
    private static final String CPU_QUIET_NR_MAX_CPUS = CPU_QUIET + "/nr_max_cpus";

    private static final String CPU_TOUCH_BOOST = "/sys/module/msm_performance/parameters/touchboost";

    private static final String DYN_STUNE_BOOST = "/sys/module/cpu_boost/parameters/dynamic_stune_boost";
    private static final String DYN_STUNE_BOOST_MS = "/sys/module/cpu_boost/parameters/dynamic_stune_boost_ms";

    private static final String ADVANCED = "/dev/stune";
    private static final String SCHED_BOOST = ADVANCED + "/schedtune.sched_boost";
    private static final String SCHED_BOOST_ENABLED = ADVANCED + "/schedtune.sched_boost_enabled";
    private static final String SCHED_BOOST_NO_OVERRIDE = ADVANCED + "/schedtune.sched_boost_no_override";

    private static final String[] STUNE = {"schedtune.sched_boost", "schedtune.sched_boost_enabled",
	"schedtune.sched_boost_no_override", "schedtune.prefer_idle", "schedtune.colocate", "top-app/schedtune.sched_boost",
	"top-app/schedtune.sched_boost_enabled", "top-app/schedtune.sched_boost_no_override", "top-app/schedtune.prefer_idle",
	"top-app/schedtune.colocate", "rt/schedtune.sched_boost", "rt/schedtune.sched_boost_enabled",
	"rt/schedtune.sched_boost_no_override", "rt/schedtune.prefer_idle", "rt/schedtune.colocate",
	"foreground/schedtune.sched_boost", "foreground/schedtune.sched_boost_enabled","foreground/schedtune.sched_boost_no_override",
	"foreground/schedtune.prefer_idle", "foreground/schedtune.colocate", "background/schedtune.sched_boost",
	"background/schedtune.sched_boost_enabled", "background/schedtune.sched_boost_no_override",
	"background/schedtune.prefer_idle", "background/schedtune.colocate"};

    private static final String VOXPOPULI = "/dev/voxpopuli";

    private static final String[] TUNABLES = {"enable_interaction_boost", "fling_min_boost_duration",
            "fling_max_boost_duration", "fling_boost_topapp", "fling_min_freq_big", "fling_min_freq_little",
            "touch_boost_duration", "touch_boost_topapp", "touch_min_freq_big", "touch_min_freq_little"};

    private static String[] sAvailableCFSSchedulers;
    private static String[] sCpuQuietAvailableGovernors;

    public static void enableCpuTouchBoost(boolean enabled, Context context) {
        run(Control.write(enabled ? "1" : "0", CPU_TOUCH_BOOST), CPU_TOUCH_BOOST, context);
    }

    public static boolean isCpuTouchBoostEnabled() {
        return Utils.readFile(CPU_TOUCH_BOOST).equals("1");
    }

    public static boolean hasCpuTouchBoost() {
        return Utils.existFile(CPU_TOUCH_BOOST);
    }

    public static void setDynStuneBoost(int value, Context context) {
        run(Control.write(String.valueOf(value), DYN_STUNE_BOOST), DYN_STUNE_BOOST, context);
    }

    public static int getDynStuneBoost() {
        return Utils.strToInt(Utils.readFile(DYN_STUNE_BOOST));
    }

    public static boolean hasDynStuneBoost() {
        return Utils.existFile(DYN_STUNE_BOOST);
    }

    public static void setDynStuneBoostDuration(String value, Context context) {
        run(Control.write(String.valueOf(value), DYN_STUNE_BOOST_MS), DYN_STUNE_BOOST_MS, context);
    }

    public static String getDynStuneBoostDuration() {
        return Utils.readFile(DYN_STUNE_BOOST_MS);
    }

    public static boolean hasDynStuneBoostDuration() {
        return Utils.existFile(DYN_STUNE_BOOST_MS);
    }

    public static void setValue(String value, int position, Context context) {
        run(Control.write(value, ADVANCED + "/" + STUNE[position]), ADVANCED + "/" +
                STUNE[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(ADVANCED + "/" + STUNE[position]);
    }

    public static String getName(int position) {
        return STUNE[position].replace("_", " ").replace(".", " ");
    }

    public static boolean exists(int position) {
        return Utils.existFile(ADVANCED + "/" + STUNE[position]);
    }

    public static int size() {
        return STUNE.length;
    }

    public static boolean hasSchedBoostSettings() {
        return Utils.existFile(SCHED_BOOST) || Utils.existFile(SCHED_BOOST_ENABLED) || Utils.existFile(SCHED_BOOST_NO_OVERRIDE);
    }

    public static void setCpuQuietGovernor(String value, Context context) {
        run(Control.write(value, CPU_QUIET_CURRENT_GOVERNOR), CPU_QUIET_CURRENT_GOVERNOR, context);
    }

    public static String getCpuQuietCurGovernor() {
        return Utils.readFile(CPU_QUIET_CURRENT_GOVERNOR);
    }

    public static void setCpuQuietNrMinCPUs(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_QUIET_NR_MIN_CPUS), CPU_QUIET_NR_MIN_CPUS, context);
    }

    public static String getCpuQuietNrMinCPUs() {
        return Utils.readFile(CPU_QUIET_NR_MIN_CPUS);
    }

    public static boolean hasCpuQuietNrMinCPUs() {
        return Utils.existFile(CPU_QUIET_NR_MIN_CPUS);
    }

    public static void setCpuQuietNrMaxCPUs(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_QUIET_NR_MAX_CPUS), CPU_QUIET_NR_MAX_CPUS, context);
    }

    public static String getCpuQuietNrMaxCPUs() {
        return Utils.readFile(CPU_QUIET_NR_MAX_CPUS);
    }

    public static boolean hasCpuQuietNrMaxCPUs() {
        return Utils.existFile(CPU_QUIET_NR_MAX_CPUS);
    }

    public static List<String> getCpuQuietAvailableGovernors() {
        if (sCpuQuietAvailableGovernors == null) {
            sCpuQuietAvailableGovernors = Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sCpuQuietAvailableGovernors));
    }

    public static boolean hasCpuQuietGovernors() {
        return Utils.existFile(CPU_QUIET_AVAILABLE_GOVERNORS) && Utils.existFile(CPU_QUIET_CURRENT_GOVERNOR)
                && !Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).equals("none");
    }

    public static void enableCpuQuiet(boolean enabled, Context context) {
        if (Utils.existFile(CPU_QUIET_ENABLE)) {
            run(Control.write(enabled ? "1" : "0", CPU_QUIET_ENABLE), CPU_QUIET_ENABLE, context);
        } else {
            run(Control.write(enabled ? "1" : "0", CPU_QUIET_TEGRA_ENABLE), CPU_QUIET_TEGRA_ENABLE, context);
        }
    }

    public static boolean isCpuQuietEnabled() {
        return Utils.readFile(Utils.existFile(CPU_QUIET_ENABLE) ? CPU_QUIET_ENABLE
                : CPU_QUIET_TEGRA_ENABLE).equals("1");
    }

    public static boolean hasCpuQuietEnable() {
        return Utils.existFile(CPU_QUIET_ENABLE) || Utils.existFile(CPU_QUIET_TEGRA_ENABLE);
    }

    public static boolean hasCpuQuiet() {
        return Utils.existFile(CPU_QUIET);
    }

    public static void setCFSScheduler(String value, Context context) {
        run(Control.write(value, CPU_CURRENT_CFS_SCHEDULER), CPU_CURRENT_CFS_SCHEDULER, context);
    }

    public static String getCurrentCFSScheduler() {
        return Utils.readFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static List<String> getAvailableCFSSchedulers() {
        if (sAvailableCFSSchedulers == null) {
            sAvailableCFSSchedulers = Utils.readFile(CPU_AVAILABLE_CFS_SCHEDULERS).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sAvailableCFSSchedulers));
    }

    public static boolean hasCFSScheduler() {
        return Utils.existFile(CPU_AVAILABLE_CFS_SCHEDULERS) && Utils.existFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static void enablePowerSavingWq(boolean enabled, Context context) {
        run(Control.chmod("644", CPU_WQ_POWER_SAVING), CPU_WQ_POWER_SAVING + "chmod", context);
        run(Control.write(enabled ? "Y" : "N", CPU_WQ_POWER_SAVING), CPU_WQ_POWER_SAVING, context);
    }

    public static boolean isPowerSavingWqEnabled() {
        return Utils.readFile(CPU_WQ_POWER_SAVING).equals("Y");
    }

    public static boolean hasPowerSavingWq() {
        return Utils.existFile(CPU_WQ_POWER_SAVING);
    }

    public static void setMcPowerSaving(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_MC_POWER_SAVING), CPU_MC_POWER_SAVING, context);
    }

    public static int getCurMcPowerSaving() {
        return Utils.strToInt(Utils.readFile(CPU_MC_POWER_SAVING));
    }

    public static boolean hasMcPowerSaving() {
        return Utils.existFile(CPU_MC_POWER_SAVING);
    }

    public static void setVoxpopuliTunableValue(String value, int position, Context context) {
        run(Control.write(value, VOXPOPULI + "/" + TUNABLES[position]), VOXPOPULI + "/" +
                TUNABLES[position], context);
    }

    public static String getVoxpopuliTunableValue(int position) {
        return Utils.readFile(VOXPOPULI + "/" + TUNABLES[position]);
    }

    public static String getVoxpopuliTunableName(int position) {
        return Utils.upperCaseEachWord(TUNABLES[position]).replace("_", " ");
    }

    public static boolean VoxpopuliTunableexists(int position) {
        return Utils.existFile(VOXPOPULI + "/" + TUNABLES[position]);
    }

    public static int VoxpopuliTunablesize() {
        return TUNABLES.length;
    }

    public static boolean hasVoxpopuliTunable() {
        return Utils.existFile(VOXPOPULI);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
