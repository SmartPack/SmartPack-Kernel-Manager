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
 * Created by willi on 25.06.16.
 */
public class Dt2s {

    private static final String DT2S = "/sys/android_touch2/doubletap2sleep";
    private static final String SCREEN_SLEEP_OPTIONS = "/sys/devices/f9924000.i2c/i2c-2/2-0020/input/input2/screen_sleep_options";

    private static final HashMap<String, List<Integer>> sFiles = new HashMap<>();
    private static final List<Integer> sGenericMenu = new ArrayList<>();

    static {
        sGenericMenu.add(R.string.disabled);
        sGenericMenu.add(R.string.enabled);

        sFiles.put(DT2S, sGenericMenu);
        sFiles.put(SCREEN_SLEEP_OPTIONS, sGenericMenu);
    }

    private static String FILE;

    public static void set(int value, Context context) {
        run(Control.write(String.valueOf(value), FILE), FILE, context);
    }

    public static int get() {
        return Utils.strToInt(Utils.readFile(FILE));
    }

    public static List<String> getMenu(Context context) {
        List<String> list = new ArrayList<>();
        for (int id : sFiles.get(FILE)) {
            list.add(context.getString(id));
        }
        return list;
    }

    public static boolean supported() {
        if (FILE == null) {
            for (String file : sFiles.keySet()) {
                if (Utils.existFile(file)) {
                    FILE = file;
                    return true;
                }
            }
        }
        return FILE != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }

}
