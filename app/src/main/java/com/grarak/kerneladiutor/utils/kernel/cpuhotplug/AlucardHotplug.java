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
public class AlucardHotplug {

    private static final String ALUCARD_HOTPLUG = "/sys/kernel/alucard_hotplug";
    private static final String ALUCARD_HOTPLUG_ENABLE = ALUCARD_HOTPLUG + "/hotplug_enable";
    private static final String ALUCARD_HOTPLUG_HP_IO_IS_BUSY = ALUCARD_HOTPLUG + "/hp_io_is_busy";
    private static final String ALUCARD_HOTPLUG_SAMPLING_RATE = ALUCARD_HOTPLUG + "/hotplug_sampling_rate";
    private static final String ALUCARD_HOTPLUG_SUSPEND = ALUCARD_HOTPLUG + "/hotplug_suspend";
    private static final String ALUCARD_HOTPLUG_MIN_CPUS_ONLINE = ALUCARD_HOTPLUG + "/min_cpus_online";
    private static final String ALUCARD_HOTPLUG_MAX_CORES_LIMIT = ALUCARD_HOTPLUG + "/maxcoreslimit";
    private static final String ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP = ALUCARD_HOTPLUG + "/maxcoreslimit_sleep";
    private static final String ALUCARD_HOTPLUG_CPU_DOWN_RATE = ALUCARD_HOTPLUG + "/cpu_down_rate";
    private static final String ALUCARD_HOTPLUG_CPU_UP_RATE = ALUCARD_HOTPLUG + "/cpu_up_rate";

    private static final String[] HOTPLUG_TUNABLES = {"hotplug_freq_1_1", "hotplug_freq_2_0", "hotplug_freq_2_1", "hotplug_freq_3_0",
		"hotplug_freq_3_1", "hotplug_freq_4_0", "hotplug_load_1_1", "hotplug_load_2_0", "hotplug_load_2_1", "hotplug_load_3_0",
		"hotplug_load_3_1", "hotplug_load_4_0", "hotplug_rate_1_1", "hotplug_rate_2_0", "hotplug_rate_2_1", "hotplug_rate_3_0",
		"hotplug_rate_3_1", "hotplug_rate_4_0", "hotplug_rq_1_1", "hotplug_rq_2_1", "hotplug_rq_2_0", "hotplug_rq_3_0",
		"hotplug_rq_3_1", "hotplug_rq_4_0"};

    public static void setAlucardHotplugCpuUpRate(int value, Context context) {
        run(Control.write(String.valueOf(value), ALUCARD_HOTPLUG_CPU_UP_RATE),
                ALUCARD_HOTPLUG_CPU_UP_RATE, context);
    }

    public static int getAlucardHotplugCpuUpRate() {
        return Utils.strToInt(Utils.readFile(ALUCARD_HOTPLUG_CPU_UP_RATE));
    }

    public static boolean hasAlucardHotplugCpuUpRate() {
        return Utils.existFile(ALUCARD_HOTPLUG_CPU_UP_RATE);
    }

    public static void setAlucardHotplugCpuDownRate(int value, Context context) {
        run(Control.write(String.valueOf(value), ALUCARD_HOTPLUG_CPU_DOWN_RATE),
                ALUCARD_HOTPLUG_CPU_DOWN_RATE, context);
    }

    public static int getAlucardHotplugCpuDownRate() {
        return Utils.strToInt(Utils.readFile(ALUCARD_HOTPLUG_CPU_DOWN_RATE));
    }

    public static boolean hasAlucardHotplugCpuDownRate() {
        return Utils.existFile(ALUCARD_HOTPLUG_CPU_DOWN_RATE);
    }

    public static void setAlucardHotplugMaxCoresLimitSleep(int value, Context context) {
        run(Control.write(String.valueOf(value), ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP),
                ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP, context);
    }

    public static int getAlucardHotplugMaxCoresLimitSleep() {
        return Utils.strToInt(Utils.readFile(ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP));
    }

    public static boolean hasAlucardHotplugMaxCoresLimitSleep() {
        return Utils.existFile(ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP);
    }

    public static void setAlucardHotplugMaxCoresLimit(int value, Context context) {
        run(Control.write(String.valueOf(value), ALUCARD_HOTPLUG_MAX_CORES_LIMIT),
                ALUCARD_HOTPLUG_MAX_CORES_LIMIT, context);
    }

    public static int getAlucardHotplugMaxCoresLimit() {
        return Utils.strToInt(Utils.readFile(ALUCARD_HOTPLUG_MAX_CORES_LIMIT));
    }

    public static boolean hasAlucardHotplugMaxCoresLimit() {
        return Utils.existFile(ALUCARD_HOTPLUG_MAX_CORES_LIMIT);
    }


    public static void setAlucardHotplugMinCpusOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), ALUCARD_HOTPLUG_MIN_CPUS_ONLINE),
                ALUCARD_HOTPLUG_MIN_CPUS_ONLINE, context);
    }

    public static int getAlucardHotplugMinCpusOnline() {
        return Utils.strToInt(Utils.readFile(ALUCARD_HOTPLUG_MIN_CPUS_ONLINE));
    }

    public static boolean hasAlucardHotplugMinCpusOnline() {
        return Utils.existFile(ALUCARD_HOTPLUG_MIN_CPUS_ONLINE);
    }

    public static void enableAlucardHotplugSuspend(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ALUCARD_HOTPLUG_SUSPEND), ALUCARD_HOTPLUG_SUSPEND, context);
    }

    public static boolean isAlucardHotplugSuspendEnabled() {
        return Utils.readFile(ALUCARD_HOTPLUG_SUSPEND).equals("1");
    }

    public static boolean hasAlucardHotplugSuspend() {
        return Utils.existFile(ALUCARD_HOTPLUG_SUSPEND);
    }

    public static String getAlucardHotplugSamplingRate() {
        return Utils.readFile(ALUCARD_HOTPLUG_SAMPLING_RATE);
    }

    public static void setAlucardHotplugSamplingRate(String value, Context context) {
        run(Control.write(String.valueOf(value), ALUCARD_HOTPLUG_SAMPLING_RATE), ALUCARD_HOTPLUG_SAMPLING_RATE, context);
    }

    public static boolean hasAlucardHotplugSamplingRate() {
        return Utils.existFile(ALUCARD_HOTPLUG_SAMPLING_RATE);
    }

    public static void enableAlucardHotplugHpIoIsBusy(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ALUCARD_HOTPLUG_HP_IO_IS_BUSY),
                ALUCARD_HOTPLUG_HP_IO_IS_BUSY, context);
    }

    public static boolean isAlucardHotplugHpIoIsBusyEnable() {
        return Utils.readFile(ALUCARD_HOTPLUG_HP_IO_IS_BUSY).equals("1");
    }

    public static boolean hasAlucardHotplugHpIoIsBusy() {
        return Utils.existFile(ALUCARD_HOTPLUG_HP_IO_IS_BUSY);
    }

    public static void enableAlucardHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ALUCARD_HOTPLUG_ENABLE), ALUCARD_HOTPLUG_ENABLE, context);
    }

    public static boolean isAlucardHotplugEnable() {
        return Utils.readFile(ALUCARD_HOTPLUG_ENABLE).equals("1");
    }

    public static boolean hasAlucardHotplugEnable() {
        return Utils.existFile(ALUCARD_HOTPLUG_ENABLE);
    }

    public static void setValue(String value, int position, Context context) {
        run(Control.write(value, ALUCARD_HOTPLUG + "/" + HOTPLUG_TUNABLES[position]), ALUCARD_HOTPLUG + "/" +
                HOTPLUG_TUNABLES[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(ALUCARD_HOTPLUG + "/" + HOTPLUG_TUNABLES[position]);
    }

    public static String getName(int position) {
        return HOTPLUG_TUNABLES[position];
    }

    public static boolean exists(int position) {
        return Utils.existFile(ALUCARD_HOTPLUG + "/" + HOTPLUG_TUNABLES[position]);
    }

    public static int size() {
        return HOTPLUG_TUNABLES.length;
    }

    public static boolean supported() {
        return Utils.existFile(ALUCARD_HOTPLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
