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
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 * Updated for lazyplug by javelinanddart on 02.01.2017
 */
public class LazyPlug {

    private static final String HOTPLUG_LAZY_PLUG = "/sys/module/lazyplug/parameters";
    private static final String HOTPLUG_LAZY_PLUG_ENABLE = HOTPLUG_LAZY_PLUG + "/lazyplug_active";
    private static final String HOTPLUG_LAZY_PLUG_PROFILE = HOTPLUG_LAZY_PLUG + "/nr_run_profile_sel";
    private static final String HOTPLUG_LAZY_PLUG_TOUCH_BOOST = HOTPLUG_LAZY_PLUG + "/touch_boost_active";
    private static final String HOTPLUG_LAZY_PLUG_HYSTERESIS = HOTPLUG_LAZY_PLUG + "/nr_run_hysteresis";
    private static final String HOTPLUG_LAZY_PLUG_THRESHOLD = HOTPLUG_LAZY_PLUG + "/cpu_nr_run_threshold";
    private static final String HOTPLUG_LAZY_PLUG_POSSIBLE_CORES = HOTPLUG_LAZY_PLUG + "/nr_possible_cores";

    public static void setLazyPlugPossibleCores(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LAZY_PLUG_POSSIBLE_CORES),
                HOTPLUG_LAZY_PLUG_POSSIBLE_CORES, context);
    }

    public static int getLazyPlugPossibleCores() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LAZY_PLUG_POSSIBLE_CORES));
    }

    public static boolean hasLazyPlugPossibleCores() {
        return Utils.existFile(HOTPLUG_LAZY_PLUG_POSSIBLE_CORES);
    }

    public static void setLazyPlugThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LAZY_PLUG_THRESHOLD),
                HOTPLUG_LAZY_PLUG_THRESHOLD, context);
    }

    public static int getLazyPlugThreshold() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LAZY_PLUG_THRESHOLD));
    }

    public static boolean hasLazyPlugThreshold() {
        return Utils.existFile(HOTPLUG_LAZY_PLUG_THRESHOLD);
    }

    public static void setLazyPlugHysteresis(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LAZY_PLUG_HYSTERESIS),
                HOTPLUG_LAZY_PLUG_HYSTERESIS, context);
    }

    public static int getLazyPlugHysteresis() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LAZY_PLUG_HYSTERESIS));
    }

    public static boolean hasLazyPlugHysteresis() {
        return Utils.existFile(HOTPLUG_LAZY_PLUG_HYSTERESIS);
    }

    public static void enableLazyPlugTouchBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_LAZY_PLUG_TOUCH_BOOST),
                HOTPLUG_LAZY_PLUG_TOUCH_BOOST, context);
    }

    public static boolean isLazyPlugTouchBoostEnabled() {
        return Utils.readFile(HOTPLUG_LAZY_PLUG_TOUCH_BOOST).equals("1");
    }

    public static boolean hasLazyPlugTouchBoost() {
        return Utils.existFile(HOTPLUG_LAZY_PLUG_TOUCH_BOOST);
    }

    public static void setLazyPlugProfile(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_LAZY_PLUG_PROFILE),
                HOTPLUG_LAZY_PLUG_PROFILE, context);
    }

    public static int getLazyPlugProfile() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_LAZY_PLUG_PROFILE));
    }

    public static List<String> getLazyPlugProfileMenu(Context context) {
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

    public static boolean hasLazyPlugProfile() {
        return Utils.existFile(HOTPLUG_LAZY_PLUG_PROFILE);
    }

    public static void enableLazyPlug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_LAZY_PLUG_ENABLE),
                HOTPLUG_LAZY_PLUG_ENABLE, context);
    }

    public static boolean isLazyPlugEnabled() {
        return Utils.readFile(HOTPLUG_LAZY_PLUG_ENABLE).equals("1");
    }

    public static boolean hasLazyPlugEnable() {
        return Utils.existFile(HOTPLUG_LAZY_PLUG_ENABLE);
    }

    public static boolean supported() {
        return (Utils.existFile(HOTPLUG_LAZY_PLUG));
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
