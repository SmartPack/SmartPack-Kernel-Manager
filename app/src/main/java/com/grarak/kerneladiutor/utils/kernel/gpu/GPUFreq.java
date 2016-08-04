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
package com.grarak.kerneladiutor.utils.kernel.gpu;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFreq {

    private static final String GENERIC_GOVERNORS = "performance powersave ondemand simple conservative";

    private static final String CUR_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk";
    private static final String MAX_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk";
    private static final String AVAILABLE_KGSL2D0_QCOM_FREQS = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpu_available_frequencies";
    private static final String SCALING_KGSL2D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/pwrscale/trustzone/governor";

    private static final String CUR_KGSL3D0_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk";
    private static final String MAX_KGSL3D0_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk";
    private static final String AVAILABLE_KGSL3D0_FREQS = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpu_available_frequencies";
    private static final String SCALING_KGSL3D0_GOVERNOR = "/sys/class/kgsl/kgsl-3d0/pwrscale/trustzone/governor";

    private static final String CUR_KGSL3D0_DEVFREQ_FREQ = "/sys/class/kgsl/kgsl-3d0/gpuclk";
    private static final String MAX_KGSL3D0_DEVFREQ_FREQ = "/sys/class/kgsl/kgsl-3d0/max_gpuclk";
    private static final String MIN_KGSL3D0_DEVFREQ_FREQ = "/sys/class/kgsl/kgsl-3d0/devfreq/min_freq";
    private static final String AVAILABLE_KGSL3D0_DEVFREQ_FREQS = "/sys/class/kgsl/kgsl-3d0/gpu_available_frequencies";
    private static final String SCALING_KGSL3D0_DEVFREQ_GOVERNOR = "/sys/class/kgsl/kgsl-3d0/devfreq/governor";
    private static final String AVAILABLE_KGSL3D0_DEVFREQ_GOVERNORS = "/sys/class/kgsl/kgsl-3d0/devfreq/available_governors";

    private static final String CUR_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency";
    private static final String MAX_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_limit";
    private static final String AVAILABLE_OMAP_FREQS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_list";
    private static final String SCALING_OMAP_GOVERNOR = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor";
    private static final String AVAILABLE_OMAP_GOVERNORS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor_list";

    private static final String CUR_TEGRA_FREQ = "/sys/kernel/tegra_gpu/gpu_rate";
    private static final String MAX_TEGRA_FREQ = "/sys/kernel/tegra_gpu/gpu_cap_rate";
    private static final String MIN_TEGRA_FREQ = "/sys/kernel/tegra_gpu/gpu_floor_rate";
    private static final String AVAILABLE_TEGRA_FREQS = "/sys/kernel/tegra_gpu/gpu_available_rates";

    private static final List<String> sCurrentFreqs = new ArrayList<>();
    private static final List<String> sMaxFreqs = new ArrayList<>();
    private static final List<String> sMinFreqs = new ArrayList<>();
    private static final List<String> sAvailableFreqs = new ArrayList<>();
    private static final List<String> sScalingGovernors = new ArrayList<>();
    private static final List<String> sAvailableGovernors = new ArrayList<>();

    static {
        sCurrentFreqs.add(CUR_KGSL3D0_FREQ);
        sCurrentFreqs.add(CUR_KGSL3D0_DEVFREQ_FREQ);
        sCurrentFreqs.add(CUR_OMAP_FREQ);
        sCurrentFreqs.add(CUR_TEGRA_FREQ);

        sMaxFreqs.add(MAX_KGSL3D0_FREQ);
        sMaxFreqs.add(MAX_KGSL3D0_DEVFREQ_FREQ);
        sMaxFreqs.add(MAX_OMAP_FREQ);
        sMaxFreqs.add(MAX_TEGRA_FREQ);

        sMinFreqs.add(MIN_KGSL3D0_DEVFREQ_FREQ);
        sMinFreqs.add(MIN_TEGRA_FREQ);

        sAvailableFreqs.add(AVAILABLE_KGSL3D0_FREQS);
        sAvailableFreqs.add(AVAILABLE_KGSL3D0_DEVFREQ_FREQS);
        sAvailableFreqs.add(AVAILABLE_OMAP_FREQS);
        sAvailableFreqs.add(AVAILABLE_TEGRA_FREQS);

        sScalingGovernors.add(SCALING_KGSL3D0_GOVERNOR);
        sScalingGovernors.add(SCALING_KGSL3D0_DEVFREQ_GOVERNOR);
        sScalingGovernors.add(SCALING_OMAP_GOVERNOR);

        sAvailableGovernors.add(AVAILABLE_KGSL3D0_DEVFREQ_GOVERNORS);
        sAvailableGovernors.add(AVAILABLE_OMAP_GOVERNORS);
    }

    private static String CUR_FREQ;
    private static Integer[] AVAILABLE_FREQS;
    private static String MAX_FREQ;
    private static String MIN_FREQ;
    private static String GOVERNOR;
    private static String[] AVAILABLE_GOVERNORS;

    private static Integer[] AVAILABLE_2D_FREQS;

    public static void set2dGovernor(String value, Context context) {
        run(Control.write(value, SCALING_KGSL2D0_QCOM_GOVERNOR), SCALING_KGSL2D0_QCOM_GOVERNOR, context);
    }

    public static String get2dGovernor() {
        return Utils.readFile(SCALING_KGSL2D0_QCOM_GOVERNOR);
    }

    public static List<String> get2dAvailableGovernors() {
        return Arrays.asList(GENERIC_GOVERNORS.split(" "));
    }

    public static boolean has2dGovernor() {
        return Utils.existFile(SCALING_KGSL2D0_QCOM_GOVERNOR);
    }

    public static void set2dMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_KGSL2D0_QCOM_FREQ), MAX_KGSL2D0_QCOM_FREQ, context);
    }

    public static int get2dMaxFreq() {
        return Utils.strToInt(Utils.readFile(MAX_KGSL2D0_QCOM_FREQ));
    }

    public static boolean has2dMaxFreq() {
        return Utils.existFile(MAX_KGSL2D0_QCOM_FREQ);
    }

    public static List<String> get2dAdjustedFreqs(Context context) {
        List<String> list = new ArrayList<>();
        for (int freq : get2dAvailableFreqs()) {
            list.add((freq / 1000000) + context.getString(R.string.mhz));
        }
        return list;
    }

    public static List<Integer> get2dAvailableFreqs() {
        if (AVAILABLE_2D_FREQS == null) {
            if (Utils.existFile(AVAILABLE_KGSL2D0_QCOM_FREQS)) {
                String[] freqs = Utils.readFile(AVAILABLE_KGSL2D0_QCOM_FREQS).split(" ");
                AVAILABLE_2D_FREQS = new Integer[freqs.length];
                for (int i = 0; i < freqs.length; i++) {
                    AVAILABLE_2D_FREQS[i] = Utils.strToInt(freqs[i]);
                }
            }
        }
        if (AVAILABLE_2D_FREQS == null) return null;
        List<Integer> list = Arrays.asList(AVAILABLE_2D_FREQS);
        Collections.sort(list);
        return list;
    }

    public static int get2dCurFreq() {
        return Utils.strToInt(Utils.readFile(CUR_KGSL2D0_QCOM_FREQ));
    }

    public static boolean has2dCurFreq() {
        return Utils.existFile(CUR_KGSL2D0_QCOM_FREQ);
    }

    public static void setGovernor(String value, Context context) {
        run(Control.write(value, GOVERNOR), GOVERNOR, context);
    }

    public static List<String> getAvailableGovernors() {
        if (AVAILABLE_GOVERNORS == null) {
            for (String file : sAvailableGovernors) {
                if (Utils.existFile(file)) {
                    AVAILABLE_GOVERNORS = Utils.readFile(file).split(" ");
                    break;
                }
            }
        }
        if (AVAILABLE_GOVERNORS == null) {
            AVAILABLE_GOVERNORS = GENERIC_GOVERNORS.split(" ");
        }
        return Arrays.asList(AVAILABLE_GOVERNORS);
    }

    public static String getGovernor() {
        return Utils.readFile(GOVERNOR);
    }

    public static boolean hasGovernor() {
        if (GOVERNOR == null) {
            for (String file : sScalingGovernors) {
                if (Utils.existFile(file)) {
                    GOVERNOR = file;
                    return true;
                }
            }
        }
        return GOVERNOR != null;
    }

    public static void setMinFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MIN_FREQ), MIN_FREQ, context);
    }

    public static int getMinFreq() {
        return Utils.strToInt(Utils.readFile(MIN_FREQ));
    }

    public static boolean hasMinFreq() {
        if (MIN_FREQ == null) {
            for (String file : sMinFreqs) {
                if (Utils.existFile(file)) {
                    MIN_FREQ = file;
                    return true;
                }
            }
        }
        return MIN_FREQ != null;
    }

    public static void setMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_FREQ), MAX_FREQ, context);
    }

    public static int getMaxFreq() {
        return Utils.strToInt(Utils.readFile(MAX_FREQ));
    }

    public static boolean hasMaxFreq() {
        if (MAX_FREQ == null) {
            for (String file : sMaxFreqs) {
                if (Utils.existFile(file)) {
                    MAX_FREQ = file;
                    return true;
                }
            }
        }
        return MAX_FREQ != null;
    }

    public static List<String> getAdjustedFreqs(Context context) {
        List<String> list = new ArrayList<>();
        for (int freq : getAvailableFreqs()) {
            list.add((freq / 1000000) + context.getString(R.string.mhz));
        }
        return list;
    }

    public static List<Integer> getAvailableFreqs() {
        if (AVAILABLE_FREQS == null) {
            for (String file : sAvailableFreqs) {
                if (Utils.existFile(file)) {
                    String freqs[] = Utils.readFile(file).split(" ");
                    AVAILABLE_FREQS = new Integer[freqs.length];
                    for (int i = 0; i < freqs.length; i++) {
                        AVAILABLE_FREQS[i] = Utils.strToInt(freqs[i]);
                    }
                    break;
                }
            }
        }
        if (AVAILABLE_FREQS == null) return null;
        List<Integer> list = Arrays.asList(AVAILABLE_FREQS);
        Collections.sort(list);
        return list;
    }

    public static int getCurFreq() {
        return Utils.strToInt(Utils.readFile(CUR_FREQ));
    }

    public static boolean hasCurFreq() {
        if (CUR_FREQ == null) {
            for (String file : sCurrentFreqs) {
                if (Utils.existFile(file)) {
                    CUR_FREQ = file;
                    return true;
                }
            }
        }
        return CUR_FREQ != null;
    }

    public static boolean supported() {
        return hasCurFreq() || hasMaxFreq() || hasMinFreq() || hasGovernor() | has2dCurFreq()
                || has2dMaxFreq() || has2dGovernor();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
