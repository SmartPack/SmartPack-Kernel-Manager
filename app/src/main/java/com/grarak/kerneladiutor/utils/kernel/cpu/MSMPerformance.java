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

/**
 * Created by willi on 02.05.16.
 */
public class MSMPerformance {

    private static MSMPerformance sInstance;

    public static MSMPerformance getInstance() {
        if (sInstance == null) {
            sInstance = new MSMPerformance();
        }
        return sInstance;
    }

    public static final String PARENT = "/sys/module/msm_performance";
    public static final String MAX_CPUS = PARENT + "/parameters/max_cpus";
    private static final String CPU_MAX_FREQ = PARENT + "/parameters/cpu_max_freq";
    private static final String MAX_CPU_FREQ = PARENT + "/parameters/max_cpu_freq";
    private static final String CPU_MIN_FREQ = PARENT + "/parameters/cpu_min_freq";

    private Boolean MAX_CPUS_SUPPORTED;
    private String CPU_MAX_FREQ_FILE;
    private Boolean CPU_MIN_FREQ_SUPPORTED;

    private MSMPerformance() {
        MAX_CPUS_SUPPORTED = Utils.existFile(MAX_CPUS);

        if (Utils.existFile(CPU_MAX_FREQ)) {
            CPU_MAX_FREQ_FILE = CPU_MAX_FREQ;
        } else if (Utils.existFile(MAX_CPU_FREQ)) {
            CPU_MAX_FREQ_FILE = MAX_CPU_FREQ;
        }

        CPU_MIN_FREQ_SUPPORTED = Utils.existFile(CPU_MIN_FREQ);
    }

    public void setCpuMinFreq(int freq, int cpu, Context context) {
        run(Control.write(cpu + ":" + freq, CPU_MIN_FREQ), CPU_MIN_FREQ + cpu, context);
    }

    public boolean hasCpuMinFreq() {
        return CPU_MIN_FREQ_SUPPORTED;
    }

    public void setCpuMaxFreq(int freq, int cpu, Context context) {
        run(Control.write(cpu + ":" + freq, CPU_MAX_FREQ_FILE), CPU_MAX_FREQ_FILE + cpu, context);
    }

    public boolean hasCpuMaxFreq() {
        return CPU_MAX_FREQ_FILE != null;
    }

    public void setMaxCpus(int big, int little, Context context) {
        setMaxCpus(big, little, ApplyOnBootFragment.CPU, context);
    }

    public void setMaxCpus(int big, int little, String category, Context context) {
        Control.runSetting(Control.write(little + ":" + big, MAX_CPUS), category, MAX_CPUS, context);
    }

    public boolean hasMaxCpus() {
        return MAX_CPUS_SUPPORTED;
    }

    public boolean supported() {
        return hasMaxCpus() || hasCpuMaxFreq() || hasCpuMinFreq();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
