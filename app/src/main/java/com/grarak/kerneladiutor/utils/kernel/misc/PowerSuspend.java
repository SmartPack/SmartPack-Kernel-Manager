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
package com.grarak.kerneladiutor.utils.kernel.misc;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 30.06.16.
 */
public class PowerSuspend {

    private static final String PARENT = "/sys/kernel/power_suspend";
    private static final String MODE = PARENT + "/power_suspend_mode";
    private static final String STATE = PARENT + "/power_suspend_state";
    private static final String VERSION = PARENT + "/power_suspend_version";

    public static void setNewState(int value, Context context) {
        run(Control.write(String.valueOf(value), STATE), STATE, context);
    }

    public static int getNewState() {
        return Utils.strToInt(Utils.readFile(STATE));
    }

    public static boolean hasNewState() {
        if (Utils.existFile(STATE) && Utils.existFile(VERSION)) {
            String version = Utils.readFile(VERSION);
            return version.contains("1.3") || version.contains("1.5");
        }
        return false;
    }

    public static void enableOldState(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", STATE), STATE, context);
    }

    public static boolean isOldStateEnabled() {
        return Utils.readFile(STATE).equals("1");
    }

    public static boolean hasOldState() {
        return Utils.existFile(STATE) && Utils.existFile(VERSION)
                && Utils.readFile(VERSION).contains("1.2");
    }

    public static void setMode(int value, Context context) {
        run(Control.write(String.valueOf(value), MODE), MODE, context);
    }

    public static int getMode() {
        return Utils.strToInt(Utils.readFile(MODE));
    }

    public static boolean hasMode() {
        return Utils.existFile(MODE);
    }

    public static boolean supported() {
        return hasMode() || hasOldState() || hasNewState();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
