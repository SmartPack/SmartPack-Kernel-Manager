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
package com.grarak.kerneladiutor.utils.kernel.misc;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.HashMap;

/**
 * Created by willi on 29.06.16.
 */
public class Vibration {

    private static final String VIB_LIGHT = "/sys/devices/virtual/timed_output/vibrator/vmax_mv_light";
    private static final String VIB_ENABLE = "/sys/devices/i2c-3/3-0033/vibrator/vib0/vib_enable";

    private static final HashMap<String, MinMax> sVibrations = new HashMap<>();

    static {
        sVibrations.put("/sys/class/timed_output/vibrator/amp", new MinMax(0, 100));
        sVibrations.put("/sys/class/timed_output/vibrator/level", new MinMax(12, 31));
        sVibrations.put("/sys/class/timed_output/vibrator/pwm_value", new MinMax("/sys/class/timed_output/vibrator/pwm_min", "/sys/class/timed_output/vibrator/pwm_max", 0, 100));
        sVibrations.put("/sys/class/timed_output/vibrator/pwm_value_1p", new MinMax(53, 99));
        sVibrations.put("/sys/class/timed_output/vibrator/voltage_level", new MinMax(1200, 3199));
        sVibrations.put("/sys/class/timed_output/vibrator/vtg_level", new MinMax("/sys/class/timed_output/vibrator/vtg_min", "/sys/class/timed_output/vibrator/vtg_max", 12, 31));
        sVibrations.put("/sys/class/timed_output/vibrator/vmax_mv", new MinMax(116, 3596));
        sVibrations.put("/sys/class/timed_output/vibrator/vmax_mv_strong", new MinMax(116, 3596));
        sVibrations.put("/sys/devices/platform/tspdrv/nforce_timed", new MinMax(1, 127));
        sVibrations.put("/sys/devices/i2c-3/3-0033/vibrator/vib0/vib_duty_cycle", new MinMax(25, 100));
        sVibrations.put("/sys/module/qpnp_vibrator/parameters/vib_voltage", new MinMax(12, 31));
        sVibrations.put("/sys/vibrator/pwmvalue", new MinMax(0, 127));
        sVibrations.put("/sys/kernel/thunderquake_engine/level", new MinMax(0, 7));
    }

    private static String FILE;
    private static Integer MIN;
    private static Integer MAX;

    public static void setVibration(int value, Context context) {
        boolean enable = Utils.existFile(VIB_ENABLE);
        if (enable) {
            run(Control.write("1", VIB_ENABLE), VIB_ENABLE + "enable", context);
        }
        run(Control.write(String.valueOf(value), FILE), FILE, context);
        if (Utils.existFile(VIB_LIGHT)) {
            run(Control.write(String.valueOf(value - 300 < 116 ? 116 : value - 300), VIB_LIGHT), VIB_LIGHT, context);
        }
        if (enable) {
            run(Control.write("0", VIB_ENABLE), VIB_ENABLE + "disable", context);
        }
    }

    public static int getMax() {
        if (MAX == null) {
            MAX = sVibrations.get(FILE).getMax();
        }
        return MAX;
    }

    public static int getMin() {
        if (MIN == null) {
            MIN = sVibrations.get(FILE).getMin();
        }
        return MIN;
    }

    public static int get() {
        if (FILE != null) {
            return Utils.strToInt(Utils.readFile(FILE).replace("%", ""));
        }
        supported();
        if (FILE == null) return 0;
        return get();
    }

    public static boolean supported() {
        if (FILE == null) {
            for (String file : sVibrations.keySet()) {
                if (Utils.existFile(file)) {
                    FILE = file;
                    return true;
                }
            }
        }
        return FILE != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

    private static class MinMax {

        private int mMin;
        private int mMax;
        private String mMinFile;
        private String mMaxFile;

        private MinMax(int min, int max) {
            mMin = min;
            mMax = max;
        }

        private MinMax(String minfile, String maxfile, int min, int max) {
            mMinFile = minfile;
            mMaxFile = maxfile;
            mMin = min;
            mMax = max;
        }

        public int getMin() {
            if (mMinFile != null && Utils.existFile(mMinFile)) {
                return Utils.strToInt(Utils.readFile(mMinFile));
            }
            return mMin;
        }

        public int getMax() {
            if (mMaxFile != null && Utils.existFile(mMaxFile)) {
                return Utils.strToInt(Utils.readFile(mMaxFile));
            }
            return mMax;
        }

    }

}
