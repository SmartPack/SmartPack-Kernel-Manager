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
package com.grarak.kerneladiutor.utils.kernel.wake;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 23.06.16.
 */
public class Dt2w {

    private static Dt2w sInstance;

    public static Dt2w getInstance() {
        if (sInstance == null) {
            sInstance = new Dt2w();
        }
        return sInstance;
    }

    private static final String LGE_TOUCH_DT2W = "/sys/devices/virtual/input/lge_touch/dt_wake_enabled";
    private static final String LGE_TOUCH_CORE_DT2W = "/sys/module/lge_touch_core/parameters/doubletap_to_wake";
    private static final String LGE_TOUCH_GESTURE = "/sys/devices/virtual/input/lge_touch/touch_gesture";
    private static final String ANDROID_TOUCH_DT2W = "/sys/android_touch/doubletap2wake";
    private static final String ANDROID_TOUCH2_DT2W = "/sys/android_touch2/doubletap2wake";
    private static final String TOUCH_PANEL_DT2W = "/proc/touchpanel/double_tap_enable";
    private static final String DT2W_WAKEUP_GESTURE = "/sys/devices/virtual/input/input1/wakeup_gesture";
    private static final String DT2W_ENABLE = "/sys/devices/platform/s3c2440-i2c.3/i2c-3/3-004a/dt2w_enable";
    private static final String DT2W_WAKE_GESTURE = "/sys/devices/platform/spi-tegra114.2/spi_master/spi2/spi2.0/input/input0/wake_gesture";
    private static final String DT2W_WAKE_GESTURE_2 = "/sys/devices/soc.0/f9924000.i2c/i2c-2/2-0070/input/input0/wake_gesture";
    private static final String DT2W_FT5X06 = "/sys/bus/i2c/drivers/ft5x06_i2c/5-0038/d2w_switch";
    private static final String LENOVO_DT2W = "/sys/lenovo_tp_gestures/tpd_suspend_status";
    private static final String DT2W_SMDK4412 = "/sys/devices/virtual/misc/touchwake/knockon";

    private final HashMap<String, List<Integer>> mFiles = new HashMap<>();
    private final List<Integer> mLgeTouchCoreMenu = new ArrayList<>();
    private final List<Integer> mAndroidTouchMenu = new ArrayList<>();
    private final List<Integer> mGenericMenu = new ArrayList<>();

    {
        mLgeTouchCoreMenu.add(R.string.disabled);
        mLgeTouchCoreMenu.add(R.string.center);
        mLgeTouchCoreMenu.add(R.string.full);
        mLgeTouchCoreMenu.add(R.string.bottom_half);
        mLgeTouchCoreMenu.add(R.string.top_half);

        mAndroidTouchMenu.add(R.string.disabled);
        mAndroidTouchMenu.add(R.string.half);
        mAndroidTouchMenu.add(R.string.full);

        mGenericMenu.add(R.string.disabled);
        mGenericMenu.add(R.string.enabled);

        mFiles.put(LGE_TOUCH_DT2W, mGenericMenu);
        mFiles.put(LGE_TOUCH_CORE_DT2W, mLgeTouchCoreMenu);
        mFiles.put(LGE_TOUCH_GESTURE, mGenericMenu);
        mFiles.put(ANDROID_TOUCH_DT2W, mAndroidTouchMenu);
        mFiles.put(ANDROID_TOUCH2_DT2W, mGenericMenu);
        mFiles.put(TOUCH_PANEL_DT2W, mGenericMenu);
        mFiles.put(DT2W_WAKEUP_GESTURE, mGenericMenu);
        mFiles.put(DT2W_ENABLE, mGenericMenu);
        mFiles.put(DT2W_WAKE_GESTURE, mGenericMenu);
        mFiles.put(DT2W_WAKE_GESTURE_2, mGenericMenu);
        mFiles.put(DT2W_FT5X06, mGenericMenu);
        mFiles.put(LENOVO_DT2W, mGenericMenu);
        mFiles.put(DT2W_SMDK4412, mGenericMenu);
    }

    private String FILE;

    private Dt2w() {
        for (String file : mFiles.keySet()) {
            if (Utils.existFile(file)) {
                FILE = file;
                break;
            }
        }
    }

    public void set(int value, Context context) {
        run(Control.write(String.valueOf(value), FILE), FILE, context);
    }

    public int get() {
        return Utils.strToInt(Utils.readFile(FILE));
    }

    public List<String> getMenu(Context context) {
        List<String> list = new ArrayList<>();
        for (int id : mFiles.get(FILE)) {
            list.add(context.getString(id));
        }
        return list;
    }

    public boolean supported() {
        return FILE != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }

}
