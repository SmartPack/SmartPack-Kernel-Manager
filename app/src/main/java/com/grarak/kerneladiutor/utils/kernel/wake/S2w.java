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

    private static final String S2W_ONLY = "/sys/android_touch/s2w_s2sonly";
    private static final String SW2 = "/sys/android_touch/sweep2wake";
    private static final String SW2_2 = "/sys/android_touch2/sweep2wake";

    private final String LENIENT = "/sys/android_touch/sweep2wake_sensitive";

    private final HashMap<String, List<Integer>> mFiles = new HashMap<>();
    private final List<Integer> mS2wMenu = new ArrayList<>();
    private final List<Integer> mS2w2Menu = new ArrayList<>();
    private final List<Integer> mGenericMenu = new ArrayList<>();

    {
        mS2wMenu.add(R.string.disabled);
        mS2wMenu.add(R.string.s2w_s2s);
        mS2wMenu.add(R.string.s2s);

        mS2w2Menu.add(R.string.disabled);
        mS2w2Menu.add(R.string.s2w_right);
        mS2w2Menu.add(R.string.s2w_left);
        mS2w2Menu.add(R.string.s2w_up);
        mS2w2Menu.add(R.string.s2w_down);
        mS2w2Menu.add(R.string.s2w_any);

        mGenericMenu.add(R.string.disabled);
        mGenericMenu.add(R.string.enabled);

        mFiles.put(S2W_ONLY, mGenericMenu);
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
