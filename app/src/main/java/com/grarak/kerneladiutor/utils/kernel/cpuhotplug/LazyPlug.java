/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 * Copyright (C) 2017 Paul Keith <javelinanddart@gmail.com>
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
package com.grarak.kerneladiutor.utils.kernel.cpuhotplug;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 * Updated for lazyplug by javelinanddart on 02.01.2017
 */
public class LazyPlug {

    private static final String PARENT = "/sys/module/lazyplug/parameters";
    private static final String ENABLE = PARENT + "/lazyplug_active";
    private static final String PROFILE = PARENT + "/nr_run_profile_sel";
    private static final String TOUCH_BOOST = PARENT + "/touch_boost_active";
    private static final String HYSTERESIS = PARENT + "/nr_run_hysteresis";
    private static final String CPU_NR_THRESHOLD = PARENT + "/cpu_nr_run_threshold";
    private static final String POSSIBLE_CORES = PARENT + "/nr_possible_cores";

    public static void setPossibleCores(int value, Context context) {
        run(Control.write(String.valueOf(value), POSSIBLE_CORES), POSSIBLE_CORES, context);
    }

    public static int getPossibleCores() {
        return Utils.strToInt(Utils.readFile(POSSIBLE_CORES));
    }

    public static boolean hasPossibleCores() {
        return Utils.existFile(POSSIBLE_CORES);
    }

    public static void setThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_NR_THRESHOLD),
                CPU_NR_THRESHOLD, context);
    }

    public static int getThreshold() {
        return Utils.strToInt(Utils.readFile(CPU_NR_THRESHOLD));
    }

    public static boolean hasThreshold() {
        return Utils.existFile(CPU_NR_THRESHOLD);
    }

    public static void setHysteresis(int value, Context context) {
        run(Control.write(String.valueOf(value), HYSTERESIS),
                HYSTERESIS, context);
    }

    public static int getHysteresis() {
        return Utils.strToInt(Utils.readFile(HYSTERESIS));
    }

    public static boolean hasHysteresis() {
        return Utils.existFile(HYSTERESIS);
    }

    public static void enableTouchBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TOUCH_BOOST),
                TOUCH_BOOST, context);
    }

    public static boolean isTouchBoostEnabled() {
        return Utils.readFile(TOUCH_BOOST).equals("1");
    }

    public static boolean hasTouchBoost() {
        return Utils.existFile(TOUCH_BOOST);
    }

    public static void setProfile(int value, Context context) {
        run(Control.write(String.valueOf(value), PROFILE),
                PROFILE, context);
    }

    public static int getProfile() {
        return Utils.strToInt(Utils.readFile(PROFILE));
    }

    public static List<String> getProfileMenu(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.balanced));
        list.add(context.getString(R.string.performance));
        list.add(context.getString(R.string.conservative));
        list.add(context.getString(R.string.eco_performance));
        list.add(context.getString(R.string.eco_conservative));
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.lazy));
        return list;
    }

    public static boolean hasProfile() {
        return Utils.existFile(PROFILE);
    }

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ENABLE), ENABLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(ENABLE).equals("1");
    }

    public static boolean hasEnable() {
        return Utils.existFile(ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
