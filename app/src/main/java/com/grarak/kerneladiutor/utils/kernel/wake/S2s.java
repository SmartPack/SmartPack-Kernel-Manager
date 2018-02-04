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
public class S2s {

    private static S2s sInstance;

    public static S2s getInstance() {
        if (sInstance == null) {
            sInstance = new S2s();
        }
        return sInstance;
    }

    private static final String S2S = "/sys/android_touch/sweep2sleep";
    private static final String S2S_2 = "/sys/android_touch2/sweep2sleep";

    private final HashMap<String, List<Integer>> mFiles = new HashMap<>();
    private final List<Integer> mS2s2Menu = new ArrayList<>();
    private final List<Integer> mGenericMenu = new ArrayList<>();

    {
        mS2s2Menu.add(R.string.s2s_right);
        mS2s2Menu.add(R.string.s2s_left);
        mS2s2Menu.add(R.string.s2s_any);

        mGenericMenu.add(R.string.disabled);
        mGenericMenu.add(R.string.enabled);

        mFiles.put(S2S, mGenericMenu);
        mFiles.put(S2S_2, mS2s2Menu);
    }

    private String FILE;

    private S2s() {
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
