/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

 /**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 18, 2019
 *
 * Based on the original implementation on Kernel Adiutor by Willi Ye
 *
 */

public class AiOHotplug {

    private static final String AIO = "/sys/kernel/AiO_HotPlug";
    private static final String TOGGLE = AIO + "/toggle";
    private static final String CORES = AIO + "/cores";
    private static final String BIG_CORES = AIO + "/big_cores";
    private static final String LITTLE_CORES = AIO + "/LITTLE_cores";

    public static void setLITTLECores(int cores, Context context) {
        run(Control.write(String.valueOf(cores), LITTLE_CORES), LITTLE_CORES, context);
    }

    public static int getLITTLECores() {
        return Utils.strToInt(Utils.readFile(LITTLE_CORES));
    }

    public static boolean hasLITTLECores() {
        return Utils.existFile(LITTLE_CORES);
    }

    public static void setBigCores(int cores, Context context) {
        run(Control.write(String.valueOf(cores), BIG_CORES), BIG_CORES, context);
    }

    public static int getBigCores() {
        return Utils.strToInt(Utils.readFile(BIG_CORES));
    }

    public static boolean hasBigCores() {
        return Utils.existFile(BIG_CORES);
    }

    public static void setCores(int cores, Context context) {
        run(Control.write(String.valueOf(cores), CORES), CORES, context);
    }

    public static int getCores() {
        return Utils.strToInt(Utils.readFile(CORES));
    }

    public static boolean hasCores() {
        return Utils.existFile(CORES);
    }

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TOGGLE), TOGGLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(TOGGLE).equals("1");
    }

    public static boolean hasToggle() {
        return Utils.existFile(TOGGLE);
    }

    public static boolean supported() {
        return Utils.existFile(AIO);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}