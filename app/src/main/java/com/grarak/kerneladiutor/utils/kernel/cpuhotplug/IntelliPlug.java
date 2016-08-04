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
 */
public class IntelliPlug {

    private static final String HOTPLUG_INTELLI_PLUG = "/sys/module/intelli_plug/parameters";
    private static final String HOTPLUG_INTELLI_PLUG_ENABLE = HOTPLUG_INTELLI_PLUG + "/intelli_plug_active";
    private static final String HOTPLUG_INTELLI_PLUG_INSANITY = HOTPLUG_INTELLI_PLUG + "/is_insanity";
    private static final String HOTPLUG_INTELLI_PLUG_PROFILE = HOTPLUG_INTELLI_PLUG + "/nr_run_profile_sel";
    private static final String HOTPLUG_INTELLI_PLUG_ECO = HOTPLUG_INTELLI_PLUG + "/eco_mode_active";
    private static final String HOTPLUG_INTELLI_PLUG_TOUCH_BOOST = HOTPLUG_INTELLI_PLUG + "/touch_boost_active";
    private static final String HOTPLUG_INTELLI_PLUG_HYSTERESIS = HOTPLUG_INTELLI_PLUG + "/nr_run_hysteresis";
    private static final String HOTPLUG_INTELLI_PLUG_THRESHOLD = HOTPLUG_INTELLI_PLUG + "/cpu_nr_run_threshold";
    private static final String HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX = HOTPLUG_INTELLI_PLUG + "/screen_off_max";
    private static final String MIN_ONLINE_CPUS = HOTPLUG_INTELLI_PLUG + "/min_online_cpus";

    private static final String HOTPLUG_INTELLI_PLUG_5 = "/sys/kernel/intelli_plug";
    private static final String HOTPLUG_INTELLI_PLUG_5_ENABLE = HOTPLUG_INTELLI_PLUG_5 + "/intelli_plug_active";
    private static final String HOTPLUG_INTELLI_PLUG_5_DEBUG = HOTPLUG_INTELLI_PLUG_5 + "/debug_intelli_plug";
    private static final String HOTPLUG_INTELLI_PLUG_5_PROFILE = HOTPLUG_INTELLI_PLUG_5 + "/full_mode_profile";
    private static final String HOTPLUG_INTELLI_PLUG_5_SUSPEND = HOTPLUG_INTELLI_PLUG_5 + "/hotplug_suspend";
    private static final String HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED = HOTPLUG_INTELLI_PLUG_5 + "/cpus_boosted";
    private static final String HOTPLUG_INTELLI_PLUG_5_HYSTERESIS = HOTPLUG_INTELLI_PLUG_5 + "/nr_run_hysteresis";
    private static final String HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE = HOTPLUG_INTELLI_PLUG_5 + "/min_cpus_online";
    private static final String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE = HOTPLUG_INTELLI_PLUG_5 + "/max_cpus_online";
    private static final String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP = HOTPLUG_INTELLI_PLUG_5 + "/max_cpus_online_susp";
    private static final String HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME = HOTPLUG_INTELLI_PLUG_5 + "/suspend_defer_time";
    private static final String HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING = HOTPLUG_INTELLI_PLUG_5 + "/def_sampling_ms";
    private static final String HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION = HOTPLUG_INTELLI_PLUG_5 + "/boost_lock_duration";
    private static final String HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION = HOTPLUG_INTELLI_PLUG_5 + "/down_lock_duration";
    private static final String HOTPLUG_INTELLI_PLUG_5_THRESHOLD = HOTPLUG_INTELLI_PLUG_5 + "/cpu_nr_run_threshold";
    private static final String HOTPLUG_INTELLI_PLUG_5_FSHIFT = HOTPLUG_INTELLI_PLUG_5 + "/nr_fshift";
    private static final String HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX = HOTPLUG_INTELLI_PLUG_5 + "/screen_off_max";

    private static Boolean sUseVersion5;
    private static Boolean sHasInsanity;

    public static void setIntelliPlugFShift(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_FSHIFT),
                HOTPLUG_INTELLI_PLUG_5_FSHIFT, context);
    }

    public static int getIntelliPlugFShift() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_FSHIFT));
    }

    public static boolean hasIntelliPlugFShift() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_FSHIFT);
    }

    public static void setIntelliPlugDownLockDuration(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION),
                HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION, context);
    }

    public static int getIntelliPlugDownLockDuration() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION));
    }

    public static boolean hasIntelliPlugDownLockDuration() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION);
    }

    public static void setIntelliPlugBoostLockDuration(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION),
                HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION, context);
    }

    public static int getIntelliPlugBoostLockDuration() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION));
    }

    public static boolean hasIntelliPlugBoostLockDuration() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION);
    }

    public static void setIntelliPlugDeferSampling(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING),
                HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING, context);
    }

    public static int getIntelliPlugDeferSampling() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING));
    }

    public static boolean hasIntelliPlugDeferSampling() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING);
    }

    public static void setIntelliPlugSuspendDeferTime(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME),
                HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME, context);
    }

    public static int getIntelliPlugSuspendDeferTime() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME));
    }

    public static boolean hasIntelliPlugSuspendDeferTime() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME);
    }

    public static void setIntelliPlugMaxCpusOnlineSusp(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP),
                HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP, context);
    }

    public static int getIntelliPlugMaxCpusOnlineSusp() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP));
    }

    public static boolean hasIntelliPlugMaxCpusOnlineSusp() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP);
    }

    public static void setIntelliPlugMaxCpusOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE),
                HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE, context);
    }

    public static int getIntelliPlugMaxCpusOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE));
    }

    public static boolean hasIntelliPlugMaxCpusOnline() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE);
    }

    public static void setIntelliPlugMinCpusOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), sUseVersion5 ? HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE
                : MIN_ONLINE_CPUS), sUseVersion5 ? HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE
                : MIN_ONLINE_CPUS, context);
    }

    public static int getIntelliPlugMinCpusOnline() {
        return Utils.strToInt(Utils.readFile(sUseVersion5 ? HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE
                : MIN_ONLINE_CPUS));
    }

    public static boolean hasIntelliPlugMinCpusOnline() {
        return Utils.existFile(sUseVersion5 ? HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE : MIN_ONLINE_CPUS);
    }

    public static void setIntelliPlugCpusBoosted(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED),
                HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED, context);
    }

    public static int getIntelliPlugCpusBoosted() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED));
    }

    public static boolean hasIntelliPlugCpusBoosted() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED);
    }

    public static void enableIntelliPlugSuspend(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_INTELLI_PLUG_5_SUSPEND),
                HOTPLUG_INTELLI_PLUG_5_SUSPEND, context);
    }

    public static boolean isIntelliPlugSuspendEnabled() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND).equals("1");
    }

    public static boolean hasIntelliPlugSuspend() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND);
    }

    public static void enableIntelliPlugDebug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_INTELLI_PLUG_5_DEBUG), HOTPLUG_INTELLI_PLUG_5_DEBUG, context);
    }

    public static boolean isIntelliPlugDebugEnabled() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_5_DEBUG).equals("1");
    }

    public static boolean hasIntelliPlugDebug() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_DEBUG);
    }

    public static void setIntelliPlugScreenOffMax(int position, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX;

        String command = position == 0 ? String.valueOf(1 << 32 - 1) : String.valueOf(CPUFreq.getFreqs()
                .get(position - 1));
        run(Control.write(command, file), file, context);
    }

    public static int getIntelliPlugScreenOffMax() {
        String file = HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX;

        String value = Utils.readFile(file);
        if (value.equals(String.valueOf(1 << 32 - 1))
                || value.equals("0")) return 0;
        return CPUFreq.getFreqs().indexOf(Utils.strToInt(value)) + 1;
    }

    public static boolean hasIntelliPlugScreenOffMax() {
        if (CPUFreq.hasMaxScreenOffFreq()) return false;
        String file = HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX;
        return Utils.existFile(file);
    }

    public static void setIntelliPlugThresold(int value, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_THRESHOLD;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_THRESHOLD;
        run(Control.write(String.valueOf(value), file), file, context);
    }

    public static int getIntelliPlugThresold() {
        String file = HOTPLUG_INTELLI_PLUG_THRESHOLD;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_THRESHOLD;
        return Utils.strToInt(Utils.readFile(file));
    }

    public static boolean hasIntelliPlugThresold() {
        String file = HOTPLUG_INTELLI_PLUG_THRESHOLD;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_THRESHOLD;
        return Utils.existFile(file);
    }

    public static void setIntelliPlugHysteresis(int value, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_HYSTERESIS;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_HYSTERESIS;
        run(Control.write(String.valueOf(value), file), file, context);
    }

    public static int getIntelliPlugHysteresis() {
        String file = HOTPLUG_INTELLI_PLUG_HYSTERESIS;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_HYSTERESIS;
        return Utils.strToInt(Utils.readFile(file));
    }

    public static boolean hasIntelliPlugHysteresis() {
        String file = HOTPLUG_INTELLI_PLUG_HYSTERESIS;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_HYSTERESIS;
        return Utils.existFile(file);
    }

    public static void enableIntelliPlugTouchBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_INTELLI_PLUG_TOUCH_BOOST),
                HOTPLUG_INTELLI_PLUG_TOUCH_BOOST, context);
    }

    public static boolean isIntelliPlugTouchBoostEnabled() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_TOUCH_BOOST).equals("1");
    }

    public static boolean hasIntelliPlugTouchBoost() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_TOUCH_BOOST);
    }

    public static void enableIntelliPlugEco(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_INTELLI_PLUG_ECO), HOTPLUG_INTELLI_PLUG_ECO, context);
    }

    public static boolean isIntelliPlugEcoEnabled() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_ECO).equals("1");
    }

    public static boolean hasIntelliPlugEco() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_ECO);
    }

    public static void setIntelliPlugProfile(int value, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_PROFILE;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_PROFILE;
        run(Control.write(String.valueOf(value), file), file, context);
    }

    public static int getIntelliPlugProfile() {
        String file = HOTPLUG_INTELLI_PLUG_PROFILE;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_PROFILE;
        return Utils.strToInt(Utils.readFile(file));
    }

    public static List<String> getIntelliPlugProfileMenu(Context context) {
        List<String> list = new ArrayList<>();
        if (sHasInsanity) {
            list.add(context.getString(R.string.insanity));
        }
        list.add(context.getString(R.string.balanced));
        list.add(context.getString(R.string.performance));
        list.add(context.getString(R.string.conservative));
        if (sUseVersion5) {
            list.add(context.getString(R.string.disabled));
            list.add(context.getString(R.string.tri));
            list.add(context.getString(R.string.eco));
            list.add(context.getString(R.string.strict));
        } else {
            if (sHasInsanity) {
                list.add(context.getString(R.string.eco_insanity));
            }
            list.add(context.getString(R.string.eco_performance));
            list.add(context.getString(R.string.eco_conservative));
        }
        return list;
    }

    public static boolean hasIntelliPlugProfile() {
        String file = HOTPLUG_INTELLI_PLUG_PROFILE;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_PROFILE;
        return Utils.existFile(file);
    }

    public static void enableIntelliPlug(boolean enable, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_ENABLE;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_ENABLE;
        run(Control.write(enable ? "1" : "0", file), file, context);
    }

    public static boolean isIntelliPlugEnabled() {
        String file = HOTPLUG_INTELLI_PLUG_ENABLE;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_ENABLE;
        return Utils.readFile(file).equals("1");
    }

    public static boolean hasIntelliPlugEnable() {
        String file = HOTPLUG_INTELLI_PLUG_ENABLE;
        if (sUseVersion5) file = HOTPLUG_INTELLI_PLUG_5_ENABLE;
        return Utils.existFile(file);
    }

    public static boolean supported() {
        if (Utils.existFile(HOTPLUG_INTELLI_PLUG)) sUseVersion5 = false;
        else if (Utils.existFile(HOTPLUG_INTELLI_PLUG_5)) sUseVersion5 = true;
        if (sUseVersion5 != null) {
            sHasInsanity = Utils.existFile(HOTPLUG_INTELLI_PLUG_INSANITY);
        }
        return sUseVersion5 != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
