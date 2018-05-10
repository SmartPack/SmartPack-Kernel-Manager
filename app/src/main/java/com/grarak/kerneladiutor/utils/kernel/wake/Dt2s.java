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

    private static Dt2s sInstance;

    public static Dt2s getInstance() {
        if (sInstance == null) {
            sInstance = new Dt2s();
        }
        return sInstance;
    }

    private static final String DT2S = "/sys/android_touch2/doubletap2sleep";
    private static final String SCREEN_SLEEP_OPTIONS = "/sys/devices/f9924000.i2c/i2c-2/2-0020/input/input2/screen_sleep_options";
    private static final String WIDTH = "/sys/android_touch2/doubletap2sleep_x";
    private static final String HEIGHT = "/sys/android_touch2/doubletap2sleep_y";

    private static final HashMap<String, List<Integer>> mFiles = new HashMap<>();
    private static final List<Integer> mGenericMenu = new ArrayList<>();

    static {
        mGenericMenu.add(R.string.disabled);
        mGenericMenu.add(R.string.enabled);

        mFiles.put(DT2S, mGenericMenu);
        mFiles.put(SCREEN_SLEEP_OPTIONS, mGenericMenu);
    }

    private String FILE;

    private Dt2s() {
        for (String file : mFiles.keySet()) {
            if (Utils.existFile(file)) {
                FILE = file;
                break;
            }
        }
    }

    public void setHeight(int value, Context context) {
        run(Control.write(String.valueOf(value), HEIGHT), HEIGHT, context);
    }

    public int getHeight() {
        return Utils.strToInt(Utils.readFile(HEIGHT));
    }

    public boolean hasHeight() {
        return Utils.existFile(HEIGHT);
    }

    public void setWidth(int value, Context context) {
        run(Control.write(String.valueOf(value), WIDTH), WIDTH, context);
    }

    public int getWidth() {
        return Utils.strToInt(Utils.readFile(WIDTH));
    }

    public boolean hasWidth() {
        return Utils.existFile(WIDTH);
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
