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

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 11.05.16.
 */
public class ZenDecision {

    private static final String HOTPLUG_ZEN_DECISION = "/sys/kernel/zen_decision";
    private static final String HOTPLUG_ZEN_DECISION_ENABLE = HOTPLUG_ZEN_DECISION + "/enabled";
    private static final String HOTPLUG_ZEN_DECISION_WAKE_WAIT_TIME = HOTPLUG_ZEN_DECISION + "/wake_wait_time";
    private static final String HOTPLUG_ZEN_DECISION_BAT_THRESHOLD_IGNORE = HOTPLUG_ZEN_DECISION + "/bat_threshold_ignore";

    public static void setZenDecisionBatThresholdIgnore(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_ZEN_DECISION_BAT_THRESHOLD_IGNORE),
                HOTPLUG_ZEN_DECISION_BAT_THRESHOLD_IGNORE, context);
    }

    public static int getZenDecisionBatThresholdIgnore() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_ZEN_DECISION_BAT_THRESHOLD_IGNORE));
    }

    public static boolean hasZenDecisionBatThresholdIgnore() {
        return Utils.existFile(HOTPLUG_ZEN_DECISION_BAT_THRESHOLD_IGNORE);
    }

    public static void setZenDecisionWakeWaitTime(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_ZEN_DECISION_WAKE_WAIT_TIME),
                HOTPLUG_ZEN_DECISION_WAKE_WAIT_TIME, context);
    }

    public static int getZenDecisionWakeWaitTime() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_ZEN_DECISION_WAKE_WAIT_TIME));
    }

    public static boolean hasZenDecisionWakeWaitTime() {
        return Utils.existFile(HOTPLUG_ZEN_DECISION_WAKE_WAIT_TIME);
    }

    public static void enableZenDecision(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_ZEN_DECISION_ENABLE),
                HOTPLUG_ZEN_DECISION_ENABLE, context);
    }

    public static boolean isZenDecisionEnabled() {
        return Utils.readFile(HOTPLUG_ZEN_DECISION_ENABLE).equals("1");
    }

    public static boolean hasZenDecisionEnable() {
        return Utils.existFile(HOTPLUG_ZEN_DECISION_ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_ZEN_DECISION);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
