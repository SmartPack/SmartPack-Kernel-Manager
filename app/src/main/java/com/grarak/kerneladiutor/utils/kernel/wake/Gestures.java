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
import java.util.List;

/**
 * Created by willi on 25.06.16.
 */
public class Gestures {

    private static final String GESTURE_CRTL = "/sys/devices/virtual/touchscreen/touchscreen_dev/gesture_ctrl";
    private static final Integer[] GESTURE_HEX_VALUES = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512};
    private static final String[] GESTURE_STRING_VALUES = {"up", "down", "left", "right", "e", "o", "w", "c", "m", "double_click"};

    public static void enable(boolean enable, int gesture, Context context) {
        run(Control.write(GESTURE_STRING_VALUES[gesture] + "=" + enable, GESTURE_CRTL), GESTURE_CRTL +
                GESTURE_STRING_VALUES[gesture], context);
    }

    public static boolean isEnabled(int gesture) {
        try {
            return (Long.decode(Utils.readFile(GESTURE_CRTL)) & GESTURE_HEX_VALUES[gesture]) != 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getMenu(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.slide_up));
        list.add(context.getString(R.string.slide_down));
        list.add(context.getString(R.string.slide_left));
        list.add(context.getString(R.string.slide_right));
        list.add(context.getString(R.string.draw_e));
        list.add(context.getString(R.string.draw_o));
        list.add(context.getString(R.string.draw_w));
        list.add(context.getString(R.string.draw_c));
        list.add(context.getString(R.string.draw_m));
        list.add(context.getString(R.string.dt2w));
        return list;
    }

    public static boolean supported() {
        return Utils.existFile(GESTURE_CRTL);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }

}
