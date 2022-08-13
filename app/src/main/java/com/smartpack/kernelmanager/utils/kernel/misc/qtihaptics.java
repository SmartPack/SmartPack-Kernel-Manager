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
package com.smartpack.kernelmanager.utils.kernel.misc;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by willi on 29.06.16.
 */
public class Vibration {

    private static Vibration sInstance;

    public static Vibration getInstance() {
        if (sInstance == null) {
            sInstance = new Vibration();
        }
        return sInstance;
    }

    private static final String VIB_LIGHT = "/sys/module/qti_haptics/parameters/vmax_mv_override";
    private static final String VIB_ENABLE = "/sys/module/qti_haptics/parameters/vmax_mv_enable";

    private final HashMap<String, MinMax> mVibrations = new HashMap<>();

    {
        mVibrations.put("/sys/module/qti_haptics/parameters/vmax_mv_override", new MinMax(116, 3596));
    }

    private String FILE;
    private Integer MIN;
    private Integer MAX;

    private Vibration() {
        for (String file : mVibrations.keySet()) {
            if (Utils.existFile(file)) {
                FILE = file;
                break;
            }
        }

        if (FILE == null) return;
        MIN = Objects.requireNonNull(mVibrations.get(FILE)).getMin();
        MAX = Objects.requireNonNull(mVibrations.get(FILE)).getMax();
    }

    public void setVibration(int value, Context context) {
        boolean enable = Utils.existFile(VIB_ENABLE);
        if (enable) {
            run(Control.write("1", VIB_ENABLE), VIB_ENABLE + "enable", context);
        }
        run(Control.write(String.valueOf(value), FILE), FILE, context);
        if (Utils.existFile(VIB_LIGHT)) {
            run(Control.write(String.valueOf(Math.max(value - 300, 116)), VIB_LIGHT), VIB_LIGHT, context);
        }
        if (enable) {
            run(Control.write("0", VIB_ENABLE), VIB_ENABLE + "disable", context);
        }
    }

    public int getMax() {
        return MAX;
    }

    public int getMin() {
        return MIN;
    }

    public int get() {
        if (FILE != null) {
            return Utils.strToInt(Utils.readFile(FILE).replace("%", ""));
        }
        supported();
        if (FILE == null) return 0;
        return get();
    }

    public boolean supported() {
        return FILE != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

    private static class MinMax {

        private final int mMin;
        private final int mMax;
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
