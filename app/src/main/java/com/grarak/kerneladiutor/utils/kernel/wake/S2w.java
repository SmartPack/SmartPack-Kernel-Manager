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

    private static S2w sInstance;

    public static S2w getInstance() {
        if (sInstance == null) {
            sInstance = new S2w();
        }
        return sInstance;
    }

    private static final String SW2 = "/sys/android_touch/sweep2wake";
    private static final String SW2_2 = "/sys/android_touch2/sweep2wake";

    private final String S2W_ONLY = "/sys/android_touch/s2w_s2sonly";
    private final String SW2_3 = "/proc/touchpanel/sweep_wake_enable";
    private final String LENIENT = "/sys/android_touch/sweep2wake_sensitive";

    private final HashMap<String, List<Integer>> mFiles = new HashMap<>();
    private final List<Integer> mS2wMenu = new ArrayList<>();
    private final List<Integer> mS2w2Menu = new ArrayList<>();

    {
        mS2wMenu.add(R.string.right);
        mS2wMenu.add(R.string.left);
        mS2wMenu.add(R.string.up);
        mS2wMenu.add(R.string.down);

        mS2w2Menu.add(R.string.disabled);
        mS2w2Menu.add(R.string.s2w_right);
        mS2w2Menu.add(R.string.s2w_left);
        mS2w2Menu.add(R.string.s2w_up);
        mS2w2Menu.add(R.string.s2w_down);
        mS2w2Menu.add(R.string.s2w_any);

        mFiles.put(SW2, mS2wMenu);
        mFiles.put(SW2_2, mS2w2Menu);
    }

    private String FILE;

    private S2w() {
        for (String file : mFiles.keySet()) {
            if (Utils.existFile(file)) {
                FILE = file;
                break;
            }
        }
    }

    public List<String> enableS2W(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.enabled));
        return list;
    }

    public boolean hasS2W() {
        return Utils.existFile(SW2_3) ||
                Utils.existFile(S2W_ONLY);
    }

    public int getS2W() {
        if (Utils.existFile(SW2_3)) {
            return Utils.strToInt(Utils.readFile(SW2_3));
        } else {
            return Utils.strToInt(Utils.readFile(S2W_ONLY));
        }
    }

    public void setS2W(int value, Context context) {
        if (Utils.existFile(SW2_3)) {
            run(Control.write(String.valueOf(value), SW2_3), SW2_3, context);
        } else {
            run(Control.write(String.valueOf(value), S2W_ONLY), S2W_ONLY, context);
        }
    }

    public void enableLenient(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LENIENT), LENIENT, context);
    }

    public boolean isLenientEnabled() {
        return Utils.readFile(LENIENT).equals("1");
    }

    public boolean hasLenient() {
        return Utils.existFile(LENIENT);
    }

    public void set(int value, Context context) {
        run(Control.write(String.valueOf(value), FILE), FILE, context);
    }

    public int get() {
        return Utils.strToInt(Utils.readFile(FILE));
    }

    public String getStringValue(Context context, int value) {
        switch (value) {
            case 0:
                return context.getResources().getString(R.string.disabled);
            case 1:
                return context.getResources().getString(R.string.right);
            case 2:
                return context.getResources().getString(R.string.left);
            case 3:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.left);
            case 4:
                return context.getResources().getString(R.string.up);
            case 5:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.up);
            case 6:
                return context.getResources().getString(R.string.left) + ", " +
                        context.getResources().getString(R.string.up);
            case 7:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.left) + ", " +
                        context.getResources().getString(R.string.up);
            case 8:
                return context.getResources().getString(R.string.down);
            case 9:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.down);
            case 10:
                return context.getResources().getString(R.string.left) + ", " +
                        context.getResources().getString(R.string.down);
            case 11:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.left) + ", " +
                        context.getResources().getString(R.string.down);
            case 12:
                return context.getResources().getString(R.string.up) + ", " +
                        context.getResources().getString(R.string.down);
            case 13:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.up) + ", " +
                        context.getResources().getString(R.string.down);
            case 14:
                return context.getResources().getString(R.string.left) + ", " +
                        context.getResources().getString(R.string.up) + ", " +
                        context.getResources().getString(R.string.down);
            case 15:
                return context.getResources().getString(R.string.right) + ", " +
                        context.getResources().getString(R.string.left) + ", " +
                        context.getResources().getString(R.string.up) + ", " +
                        context.getResources().getString(R.string.down);
        }
        return context.getResources().getString(R.string.not_in_range);
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
