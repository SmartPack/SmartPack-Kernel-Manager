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
package com.grarak.kerneladiutor.utils.kernel.entropy;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 29.06.16.
 */
public class Entropy {

    private static final String PARENT = "/proc/sys/kernel/random";
    private static final String AVAILABLE = PARENT + "/entropy_avail";
    private static final String POOLSIZE = PARENT + "/poolsize";
    private static final String READ = PARENT + "/read_wakeup_threshold";
    private static final String WRITE = PARENT + "/write_wakeup_threshold";

    public static void setWrite(int value, Context context) {
        run(Control.write(String.valueOf(value), WRITE), WRITE, context);
    }

    public static int getWrite() {
        return Utils.strToInt(Utils.readFile(WRITE));
    }

    public static void setRead(int value, Context context) {
        run(Control.write(String.valueOf(value), READ), READ, context);
    }

    public static int getRead() {
        return Utils.strToInt(Utils.readFile(READ));
    }

    public static int getPoolsize() {
        return Utils.strToInt(Utils.readFile(POOLSIZE));
    }

    public static int getAvailable() {
        return Utils.strToInt(Utils.readFile(AVAILABLE));
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.ENTROPY, id, context);
    }

}
