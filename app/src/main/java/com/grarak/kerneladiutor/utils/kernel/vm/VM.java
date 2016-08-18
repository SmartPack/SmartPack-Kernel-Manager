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
package com.grarak.kerneladiutor.utils.kernel.vm;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 29.06.16.
 */
public class VM {

    private static final String PATH = "/proc/sys/vm";
    private static final String[] SUPPORTED_VM = {"dirty_ratio", "dirty_background_ratio", "dirty_expire_centisecs",
            "dirty_writeback_centisecs", "min_free_kbytes", "oom_kill_allocating_task", "overcommit_ratio", "swappiness",
            "vfs_cache_pressure", "laptop_mode", "extra_free_kbytes"};

    public static void setValue(String value, int position, Context context) {
        run(Control.write(value, PATH + "/" + SUPPORTED_VM[position]), PATH + "/" +
                SUPPORTED_VM[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(PATH + "/" + SUPPORTED_VM[position]);
    }

    public static String getName(int position) {
        return SUPPORTED_VM[position];
    }

    public static boolean exists(int position) {
        return Utils.existFile(PATH + "/" + SUPPORTED_VM[position]);
    }

    public static int size() {
        return SUPPORTED_VM.length;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
