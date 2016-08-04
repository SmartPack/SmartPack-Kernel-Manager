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
 * Created by willi on 24.06.16.
 */
public class S2w {

    private static final String S2W_ONLY = "/sys/android_touch/s2w_s2sonly";
    private static final String SW2 = "/sys/android_touch/sweep2wake";
    private static final String SW2_2 = "/sys/android_touch2/sweep2wake";

    private static final String LENIENT = "/sys/android_touch/sweep2wake_sensitive";

    private static final HashMap<String, List<Integer>> sFiles = new HashMap<>();
    private static final List<Integer> sS2wMenu = new ArrayList<>();
    private static final List<Integer> sS2w2Menu = new ArrayList<>();
    private static final List<Integer> sGenericMenu = new ArrayList<>();

    static {
        sS2wMenu.add(R.string.disabled);
        sS2wMenu.add(R.string.s2w_s2s);
        sS2wMenu.add(R.string.s2s);

        sS2w2Menu.add(R.string.disabled);
        sS2w2Menu.add(R.string.s2w_right);
        sS2w2Menu.add(R.string.s2w_left);
        sS2w2Menu.add(R.string.s2w_up);
        sS2w2Menu.add(R.string.s2w_down);
        sS2w2Menu.add(R.string.s2w_any);

        sGenericMenu.add(R.string.disabled);
        sGenericMenu.add(R.string.enabled);

        sFiles.put(S2W_ONLY, sGenericMenu);
        sFiles.put(SW2, sS2wMenu);
        sFiles.put(SW2_2, sS2w2Menu);
    }

    private static String FILE;

    public static void enableLenient(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LENIENT), LENIENT, context);
    }

    public static boolean isLenientEnabled() {
        return Utils.readFile(LENIENT).equals("1");
    }

    public static boolean hasLenient() {
        return Utils.existFile(LENIENT);
    }

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
