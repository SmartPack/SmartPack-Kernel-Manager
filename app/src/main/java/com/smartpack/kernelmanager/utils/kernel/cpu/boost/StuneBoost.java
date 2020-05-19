/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.utils.kernel.cpu.boost;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 19, 2020
 */

public class StuneBoost {

    private static final String DYN_STUNE_BOOST = getParent() + "/dynamic_stune_boost";
    private static final String DYN_STUNE_BOOST_MS = getParent() + "/dynamic_stune_boost_ms";

    private static final String ADVANCED = "/dev/stune";
    private static final String SCHED_BOOST = ADVANCED + "/schedtune.sched_boost";
    private static final String SCHED_BOOST_ENABLED = ADVANCED + "/schedtune.sched_boost_enabled";
    private static final String SCHED_BOOST_NO_OVERRIDE = ADVANCED + "/schedtune.sched_boost_no_override";

    private static final String[] STUNE = {"schedtune.boost", "schedtune.sched_boost", "schedtune.sched_boost_enabled",
            "schedtune.sched_boost_no_override", "schedtune.prefer_idle", "schedtune.colocate", "cgroup.clone_children",
            "cgroup.sane_behavior", "top-app/schedtune.boost", "top-app/schedtune.sched_boost", "top-app/schedtune.sched_boost_enabled",
            "top-app/schedtune.sched_boost_no_override", "top-app/schedtune.prefer_idle", "top-app/cgroup.clone_children",
            "top-app/cgroup.sane_behavior", "rt/schedtune.boost", "rt/schedtune.sched_boost", "rt/schedtune.sched_boost_enabled",
            "rt/schedtune.sched_boost_no_override", "rt/schedtune.prefer_idle", "rt/cgroup.clone_children", "rt/cgroup.sane_behavior",
            "foreground/schedtune.boost", "foreground/schedtune.sched_boost", "foreground/schedtune.sched_boost_enabled",
            "foreground/schedtune.sched_boost_no_override", "foreground/schedtune.prefer_idle", "foreground/cgroup.clone_children",
            "foreground/cgroup.sane_behavior", "background/schedtune.boost", "background/schedtune.sched_boost",
            "background/schedtune.sched_boost_enabled", "background/schedtune.sched_boost_no_override",
            "background/schedtune.prefer_idle", "background/cgroup.clone_children", "background/cgroup.sane_behavior"};

    public static String getParent() {
        if (Utils.existFile("/sys/module/cpu_boost/parameters/dynamic_stune_boost")) {
            return "/sys/module/cpu_boost/parameters";
        } else if (Utils.existFile("/sys/module/cpu_input_boost/parameters/dynamic_stune_boost")) {
            return "/sys/module/cpu_input_boost/parameters";
        } else if (Utils.existFile("/sys/kernel/cpu_input_boost/dynamic_stune_boost")) {
            return "/sys/kernel/cpu_input_boost";
        }
        return null;
    }

    public static void setDynStuneBoost(int value, Context context) {
        run(Control.write(String.valueOf(value), DYN_STUNE_BOOST), DYN_STUNE_BOOST, context);
    }

    public static int getDynStuneBoost() {
        return Utils.strToInt(Utils.readFile(DYN_STUNE_BOOST));
    }

    public static boolean hasDynStuneBoost() {
        return Utils.existFile(DYN_STUNE_BOOST);
    }

    public static void setDynStuneBoostDuration(String value, Context context) {
        run(Control.write(String.valueOf(value), DYN_STUNE_BOOST_MS), DYN_STUNE_BOOST_MS, context);
    }

    public static String getDynStuneBoostDuration() {
        return Utils.readFile(DYN_STUNE_BOOST_MS);
    }

    public static boolean hasDynStuneBoostDuration() {
        return Utils.existFile(DYN_STUNE_BOOST_MS);
    }

    public static void setValue(String value, int position, Context context) {
        run(Control.write(value, ADVANCED + "/" + STUNE[position]), ADVANCED + "/" +
                STUNE[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(ADVANCED + "/" + STUNE[position]);
    }

    public static String getName(int position) {
        return STUNE[position].replace("_", " ").replace(".", " ");
    }

    public static boolean exists(int position) {
        return Utils.existFile(ADVANCED + "/" + STUNE[position]);
    }

    public static int size() {
        return STUNE.length;
    }

    private static boolean isStuneBoostSupported() {
        return Utils.existFile(ADVANCED);
    }

    public static boolean supported() {
        return hasDynStuneBoost() || hasDynStuneBoostDuration() || isStuneBoostSupported();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.STUNE_BOOST, id, context);
    }

}