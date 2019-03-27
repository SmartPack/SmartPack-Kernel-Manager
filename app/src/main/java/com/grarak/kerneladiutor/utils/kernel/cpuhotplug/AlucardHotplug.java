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

    private static final String HOTPLUG_FREQ11 = ALUCARD_HOTPLUG + "/hotplug_freq_1_1";
    private static final String HOTPLUG_FREQ20 = ALUCARD_HOTPLUG + "/hotplug_freq_2_0";
    private static final String HOTPLUG_FREQ21 = ALUCARD_HOTPLUG + "/hotplug_freq_2_1";
    private static final String HOTPLUG_FREQ30 = ALUCARD_HOTPLUG + "/hotplug_freq_3_0";
    private static final String HOTPLUG_FREQ31 = ALUCARD_HOTPLUG + "/hotplug_freq_3_1";
    private static final String HOTPLUG_FREQ40 = ALUCARD_HOTPLUG + "/hotplug_freq_4_0";

    private static final String HOTPLUG_LOAD11 = ALUCARD_HOTPLUG + "/hotplug_load_1_1";
    private static final String HOTPLUG_LOAD20 = ALUCARD_HOTPLUG + "/hotplug_load_2_0";
    private static final String HOTPLUG_LOAD21 = ALUCARD_HOTPLUG + "/hotplug_load_2_1";
    private static final String HOTPLUG_LOAD30 = ALUCARD_HOTPLUG + "/hotplug_load_3_0";
    private static final String HOTPLUG_LOAD31 = ALUCARD_HOTPLUG + "/hotplug_load_3_1";
    private static final String HOTPLUG_LOAD40 = ALUCARD_HOTPLUG + "/hotplug_load_4_0";

    private static final String HOTPLUG_RATE11 = ALUCARD_HOTPLUG + "/hotplug_rate_1_1";
    private static final String HOTPLUG_RATE20 = ALUCARD_HOTPLUG + "/hotplug_rate_2_0";
    private static final String HOTPLUG_RATE21 = ALUCARD_HOTPLUG + "/hotplug_rate_2_1";
    private static final String HOTPLUG_RATE30 = ALUCARD_HOTPLUG + "/hotplug_rate_3_0";
    private static final String HOTPLUG_RATE31 = ALUCARD_HOTPLUG + "/hotplug_rate_3_1";
    private static final String HOTPLUG_RATE40 = ALUCARD_HOTPLUG + "/hotplug_rate_4_0";

    private static final String HOTPLUG_RQ11 = ALUCARD_HOTPLUG + "/hotplug_rq_1_1";
    private static final String HOTPLUG_RQ20 = ALUCARD_HOTPLUG + "/hotplug_rq_2_1";
    private static final String HOTPLUG_RQ21 = ALUCARD_HOTPLUG + "/hotplug_rq_2_0";
    private static final String HOTPLUG_RQ30 = ALUCARD_HOTPLUG + "/hotplug_rq_3_0";
    private static final String HOTPLUG_RQ31 = ALUCARD_HOTPLUG + "/hotplug_rq_3_1";
    private static final String HOTPLUG_RQ40 = ALUCARD_HOTPLUG + "/hotplug_rq_4_0";

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

    public static void setHotplugFreq11(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_FREQ11), HOTPLUG_FREQ11, context);
    }

    public static int getHotplugFreq11() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_FREQ11));
    }

    public static boolean hasHotplugFreq11() {
        return Utils.existFile(HOTPLUG_FREQ11);
    }

    public static int getHotplugFreq20() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_FREQ20));
    }

    public static void setHotplugFreq20(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_FREQ20), HOTPLUG_FREQ20, context);
    }

    public static boolean hasHotplugFreq20() {
        return Utils.existFile(HOTPLUG_FREQ20);
    }

    public static int getHotplugFreq21() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_FREQ21));
    }

    public static void setHotplugFreq21(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_FREQ21), HOTPLUG_FREQ21, context);
    }

    public static boolean hasHotplugFreq21() {
        return Utils.existFile(HOTPLUG_FREQ21);
    }

    public static int getHotplugFreq30() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_FREQ30));
    }

    public static void setHotplugFreq30(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_FREQ30), HOTPLUG_FREQ30, context);
    }

    public static boolean hasHotplugFreq30() {
        return Utils.existFile(HOTPLUG_FREQ30);
    }

    public static int getHotplugFreq31() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_FREQ31));
    }

    public static void setHotplugFreq31(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_FREQ31), HOTPLUG_FREQ31, context);
    }

    public static boolean hasHotplugFreq31() {
        return Utils.existFile(HOTPLUG_FREQ31);
    }

    public static int getHotplugFreq40() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_FREQ40));
    }

    public static void setHotplugFreq40(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_FREQ40), HOTPLUG_FREQ40, context);
    }

    public static boolean hasHotplugFreq40() {
        return Utils.existFile(HOTPLUG_FREQ40);
    }

    public static int getHotplugLoad11() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LOAD11));
    }

    public static void setHotplugLoad11(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LOAD11), HOTPLUG_LOAD11, context);
    }

    public static boolean hasHotplugLoad11() {
        return Utils.existFile(HOTPLUG_LOAD11);
    }

    public static int getHotplugLoad20() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LOAD20));
    }

    public static void setHotplugLoad20(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LOAD20), HOTPLUG_LOAD20, context);
    }

    public static boolean hasHotplugLoad20() {
        return Utils.existFile(HOTPLUG_LOAD20);
    }

    public static int getHotplugLoad21() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LOAD21));
    }

    public static void setHotplugLoad21(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LOAD21), HOTPLUG_LOAD21, context);
    }

    public static boolean hasHotplugLoad21() {
        return Utils.existFile(HOTPLUG_LOAD21);
    }

    public static int getHotplugLoad30() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LOAD30));
    }

    public static void setHotplugLoad30(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LOAD30), HOTPLUG_LOAD30, context);
    }

    public static boolean hasHotplugLoad30() {
        return Utils.existFile(HOTPLUG_LOAD30);
    }

    public static int getHotplugLoad31() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LOAD31));
    }

    public static void setHotplugLoad31(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LOAD31), HOTPLUG_LOAD31, context);
    }

    public static boolean hasHotplugLoad31() {
        return Utils.existFile(HOTPLUG_LOAD31);
    }

    public static int getHotplugLoad40() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LOAD40));
    }

    public static void setHotplugLoad40(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LOAD40), HOTPLUG_LOAD40, context);
    }

    public static boolean hasHotplugLoad40() {
        return Utils.existFile(HOTPLUG_LOAD40);
    }

    public static String getHotplugRate11() {
        return Utils.readFile(HOTPLUG_RATE11);
    }

    public static void setHotplugRate11(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RATE11), HOTPLUG_RATE11, context);
    }

    public static boolean hasHotplugRate11() {
        return Utils.existFile(HOTPLUG_RATE11);
    }

    public static String getHotplugRate20() {
        return Utils.readFile(HOTPLUG_RATE20);
    }

    public static void setHotplugRate20(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RATE20), HOTPLUG_RATE20, context);
    }

    public static boolean hasHotplugRate20() {
        return Utils.existFile(HOTPLUG_RATE20);
    }

    public static String getHotplugRate21() {
        return Utils.readFile(HOTPLUG_RATE21);
    }

    public static void setHotplugRate21(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RATE21), HOTPLUG_RATE21, context);
    }

    public static boolean hasHotplugRate21() {
        return Utils.existFile(HOTPLUG_RATE21);
    }

    public static String getHotplugRate30() {
        return Utils.readFile(HOTPLUG_RATE30);
    }

    public static void setHotplugRate30(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RATE30), HOTPLUG_RATE30, context);
    }

    public static boolean hasHotplugRate30() {
        return Utils.existFile(HOTPLUG_RATE30);
    }

    public static String getHotplugRate31() {
        return Utils.readFile(HOTPLUG_RATE31);
    }

    public static void setHotplugRate31(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RATE31), HOTPLUG_RATE31, context);
    }

    public static boolean hasHotplugRate31() {
        return Utils.existFile(HOTPLUG_RATE31);
    }

    public static String getHotplugRate40() {
        return Utils.readFile(HOTPLUG_RATE40);
    }

    public static void setHotplugRate40(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RATE40), HOTPLUG_RATE40, context);
    }

    public static boolean hasHotplugRate40() {
        return Utils.existFile(HOTPLUG_RATE40);
    }

    public static String getHotplugRQ11() {
        return Utils.readFile(HOTPLUG_RQ11);
    }

    public static void setHotplugRQ11(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RQ11), HOTPLUG_RQ11, context);
    }

    public static boolean hasHotplugRQ11() {
        return Utils.existFile(HOTPLUG_RQ11);
    }

    public static String getHotplugRQ20() {
        return Utils.readFile(HOTPLUG_RQ20);
    }

    public static void setHotplugRQ20(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RQ20), HOTPLUG_RQ20, context);
    }

    public static boolean hasHotplugRQ20() {
        return Utils.existFile(HOTPLUG_RQ20);
    }

    public static String getHotplugRQ21() {
        return Utils.readFile(HOTPLUG_RQ21);
    }

    public static void setHotplugRQ21(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RQ21), HOTPLUG_RQ21, context);
    }

    public static boolean hasHotplugRQ21() {
        return Utils.existFile(HOTPLUG_RQ21);
    }

    public static String getHotplugRQ30() {
        return Utils.readFile(HOTPLUG_RQ30);
    }

    public static void setHotplugRQ30(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RQ30), HOTPLUG_RQ30, context);
    }

    public static boolean hasHotplugRQ30() {
        return Utils.existFile(HOTPLUG_RQ30);
    }

    public static String getHotplugRQ31() {
        return Utils.readFile(HOTPLUG_RQ31);
    }

    public static void setHotplugRQ31(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RQ31), HOTPLUG_RQ31, context);
    }

    public static boolean hasHotplugRQ31() {
        return Utils.existFile(HOTPLUG_RQ31);
    }

    public static String getHotplugRQ40() {
        return Utils.readFile(HOTPLUG_RQ40);
    }

    public static void setHotplugRQ40(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_RQ40), HOTPLUG_RQ40, context);
    }

    public static boolean hasHotplugRQ40() {
        return Utils.existFile(HOTPLUG_RQ40);
    }

    public static boolean supported() {
        return Utils.existFile(ALUCARD_HOTPLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
