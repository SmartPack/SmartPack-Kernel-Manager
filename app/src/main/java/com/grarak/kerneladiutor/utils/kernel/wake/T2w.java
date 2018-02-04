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
 * Created by willi on 24.06.16.
 */
public class T2w {

    private static T2w sInstance;

    public static T2w getInstance() {
        if (sInstance == null) {
            sInstance = new T2w();
        }
        return sInstance;
    }

    private static final String TSP_T2W = "/sys/devices/f9966000.i2c/i2c-1/1-004a/tsp";
    private static final String TOUCHWAKE_T2W = "/sys/class/misc/touchwake/enabled";

    private final List<String> mFiles = new ArrayList<>();

    {
        mFiles.add(TSP_T2W);
        mFiles.add(TOUCHWAKE_T2W);
    }

    private String FILE;

    private T2w() {
        for (String file : mFiles) {
            if (Utils.existFile(file)) {
                FILE = file;
                break;
            }
        }
    }

    public void set(int value, Context context) {
        if (FILE.equals(TSP_T2W)) {
            run(Control.write(value == 0 ? "OFF" : "AUTO", FILE), FILE, context);
        } else {
            run(Control.write(String.valueOf(value), FILE), FILE, context);
        }
    }

    public int get() {
        if (FILE.equals(TSP_T2W)) {
            return Utils.readFile(FILE).equals("OFF") ? 0 : 1;
        } else {
            return Utils.strToInt(Utils.readFile(FILE));
        }
    }

    public List<String> getMenu(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.enabled));
        return list;
    }

    public boolean supported() {
        return FILE != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }

}
