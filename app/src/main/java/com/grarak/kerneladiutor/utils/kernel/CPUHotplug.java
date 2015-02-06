/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.02.15.
 */
public class CPUHotplug implements Constants {

    private enum INTELLIPLUG_TYPE {
        INTELLIPLUG, INTELLIPLUG_5
    }

    private static INTELLIPLUG_TYPE TYPE;

    public static void setBluPlugDownTimerCnt(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugDownTimerCnt() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT));
    }

    public static boolean hasBluPlugDownTimerCnt() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT);
    }

    public static void setBluPlugUpTimerCnt(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_BLU_PLUG_UP_TIMER_CNT, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugUpTimerCnt() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_BLU_PLUG_UP_TIMER_CNT));
    }

    public static boolean hasBluPlugUpTimerCnt() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_UP_TIMER_CNT);
    }

    public static void setBluPlugUpThreshold(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_BLU_PLUG_UP_THRESHOLD, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugUpThreshold() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_BLU_PLUG_UP_THRESHOLD));
    }

    public static boolean hasBluPlugUpThreshold() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_UP_THRESHOLD);
    }

    public static void setBluPlugMaxFreqScreenOff(int position, Context context) {
        String command = position == 0 ? "0" : String.valueOf(CPU.getFreqs().get(position - 1));
        Control.runCommand(command, HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugMaxFreqScreenOff() {
        String value = Utils.readFile(HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF);
        if (value.equals("0")) return 0;
        return CPU.getFreqs().indexOf(Utils.stringToInt(value)) + 1;
    }

    public static boolean hasBluPlugMaxFreqScreenOff() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF);
    }

    public static void setBluPlugMaxCoresScreenOff(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugMaxCoresScreenOff() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF));
    }

    public static boolean hasBluPlugMaxCoresScreenOff() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF);
    }

    public static void setBluPlugMaxOnline(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_BLU_PLUG_MAX_ONLINE, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugMaxOnline() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_BLU_PLUG_MAX_ONLINE));
    }

    public static boolean hasBluPlugMaxOnline() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MAX_ONLINE);
    }

    public static void setBluPlugMinOnline(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_BLU_PLUG_MIN_ONLINE, Control.CommandType.GENERIC, context);
    }

    public static int getBluPlugMinOnline() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_BLU_PLUG_MIN_ONLINE));
    }

    public static boolean hasBluPlugMinOnline() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MIN_ONLINE);
    }

    public static void activateBluPlugPowersaverMode(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", HOTPLUG_BLU_PLUG_POWERSAVER_MODE, Control.CommandType.GENERIC, context);
    }

    public static boolean isBluPlugPowersaverModeActive() {
        return Utils.readFile(HOTPLUG_BLU_PLUG_POWERSAVER_MODE).equals("Y");
    }

    public static boolean hasBluPlugPowersaverMode() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_POWERSAVER_MODE);
    }

    public static void activateBluPlug(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", HOTPLUG_BLU_PLUG, Control.CommandType.GENERIC, context);
    }

    public static boolean isBluPlugActive() {
        return Utils.readFile(HOTPLUG_BLU_PLUG).equals("1");
    }

    public static boolean hasBluPlug() {
        return Utils.existFile(HOTPLUG_BLU_PLUG);
    }

    public static void setIntelliPlugFShift(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_FSHIFT, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugFShift() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_FSHIFT));
    }

    public static boolean hasIntelliPlugFShift() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_FSHIFT);
    }

    public static void setIntelliPlugDownLockDuration(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugDownLockDuration() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION));
    }

    public static boolean hasIntelliPlugDownLockDuration() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION);
    }

    public static void setIntelliPlugBoostLockDuration(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugBoostLockDuration() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION));
    }

    public static boolean hasIntelliPlugBoostLockDuration() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION);
    }

    public static void setIntelliPlugDeferSampling(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugDeferSampling() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING));
    }

    public static boolean hasIntelliPlugDeferSampling() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING);
    }

    public static void setIntelliPlugSuspendDeferTime(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugSuspendDeferTime() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME));
    }

    public static boolean hasIntelliPlugSuspendDeferTime() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME);
    }

    public static void setIntelliPlugMaxCpusOnlineSusp(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugMaxCpusOnlineSusp() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP));
    }

    public static boolean hasIntelliPlugMaxCpusOnlineSusp() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP);
    }

    public static void setIntelliPlugMaxCpusOnline(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugMaxCpusOnline() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE));
    }

    public static boolean hasIntelliPlugMaxCpusOnline() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE);
    }

    public static void setIntelliPlugMinCpusOnline(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugMinCpusOnline() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE));
    }

    public static boolean hasIntelliPlugMinCpusOnline() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE);
    }

    public static void setIntelliPlugCpusBoosted(int value, Context context) {
        Control.runCommand(String.valueOf(value), HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugCpusBoosted() {
        return Utils.stringToInt(Utils.readFile(HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED));
    }

    public static boolean hasIntelliPlugCpusBoosted() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED);
    }

    public static void activateIntelliPlugSuspend(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", HOTPLUG_INTELLI_PLUG_5_SUSPEND, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugSuspendActive() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND).equals("1");
    }

    public static boolean hasIntelliPlugSuspend() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_SUSPEND);
    }

    public static void activateIntelliPlugDebug(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", HOTPLUG_INTELLI_PLUG_5_DEBUG, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugDebugActive() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_5_DEBUG).equals("1");
    }

    public static boolean hasIntelliPlugDebug() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_5_DEBUG);
    }

    public static void setIntelliPlugScreenOffMax(int position, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX;

        String command = position == 0 ? "4294967295" : String.valueOf(CPU.getFreqs().get(position - 1));
        Control.runCommand(command, file, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugScreenOffMax() {
        String file = HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX;

        String value = Utils.readFile(file);
        if (value.equals("4294967295") // 4294967295 is 32-Bit max unsigned integer
                || value.equals("0")) return 0;
        return CPU.getFreqs().indexOf(Utils.stringToInt(value)) + 1;
    }

    public static boolean hasIntelliPlugScreenOffMax() {
        String file = HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX;
        return Utils.existFile(file);
    }

    public static void setIntelliPlugThresold(int value, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_THRESHOLD;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_THRESHOLD;
        Control.runCommand(String.valueOf(value), file, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugThresold() {
        String file = HOTPLUG_INTELLI_PLUG_THRESHOLD;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_THRESHOLD;
        return Utils.stringToInt(Utils.readFile(file));
    }

    public static boolean hasIntelliPlugThresold() {
        String file = HOTPLUG_INTELLI_PLUG_THRESHOLD;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_THRESHOLD;
        return Utils.existFile(file);
    }

    public static void setIntelliPlugHysteresis(int value, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_HYSTERESIS;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_HYSTERESIS;
        Control.runCommand(String.valueOf(value), file, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugHysteresis() {
        String file = HOTPLUG_INTELLI_PLUG_HYSTERESIS;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_HYSTERESIS;
        return Utils.stringToInt(Utils.readFile(file));
    }

    public static boolean hasIntelliPlugHysteresis() {
        String file = HOTPLUG_INTELLI_PLUG_HYSTERESIS;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_HYSTERESIS;
        return Utils.existFile(file);
    }

    public static void activateIntelliPlugTouchBoost(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", HOTPLUG_INTELLI_PLUG_TOUCH_BOOST, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugTouchBoostActive() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_TOUCH_BOOST).equals("1");
    }

    public static boolean hasIntelliPlugTouchBoost() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_TOUCH_BOOST);
    }

    public static void activateIntelliPlugEco(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", HOTPLUG_INTELLI_PLUG_ECO, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugEcoActive() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG_ECO).equals("1");
    }

    public static boolean hasIntelliPlugEco() {
        return Utils.existFile(HOTPLUG_INTELLI_PLUG_ECO);
    }

    public static void setIntelliPlugProfile(int value, Context context) {
        String file = HOTPLUG_INTELLI_PLUG_PROFILE;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_PROFILE;
        Control.runCommand(String.valueOf(value), file, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugProfile() {
        String file = HOTPLUG_INTELLI_PLUG_PROFILE;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_PROFILE;
        return Utils.stringToInt(Utils.readFile(file));
    }

    public static List<String> getIntelliPlugProfileMenu(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.balanced));
        list.add(context.getString(R.string.performance));
        list.add(context.getString(R.string.conservative));
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) {
            list.add(context.getString(R.string.disabled));
            list.add(context.getString(R.string.tri));
            list.add(context.getString(R.string.eco));
            list.add(context.getString(R.string.strict));
        } else {
            list.add(context.getString(R.string.eco_performance));
            list.add(context.getString(R.string.eco_conservative));
        }
        return list;
    }

    public static boolean hasIntelliPlugProfile() {
        String file = HOTPLUG_INTELLI_PLUG_PROFILE;
        if (TYPE == INTELLIPLUG_TYPE.INTELLIPLUG_5) file = HOTPLUG_INTELLI_PLUG_5_PROFILE;
        return Utils.existFile(file);
    }

    public static void activateIntelliPlug(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", HOTPLUG_INTELLI_PLUG, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugActive() {
        return Utils.readFile(HOTPLUG_INTELLI_PLUG).equals("1");
    }

    public static boolean hasIntelliPlug() {
        if (Utils.existFile(HOTPLUG_INTELLI_PLUG)) TYPE = INTELLIPLUG_TYPE.INTELLIPLUG;
        if (Utils.existFile(HOTPLUG_INTELLI_PLUG_5)) TYPE = INTELLIPLUG_TYPE.INTELLIPLUG_5;
        return TYPE != null;
    }

    public static void activateMpdecision(boolean active, Context context) {
        if (active) Control.startService(HOTPLUG_MPDEC, true, context);
        else Control.stopService(HOTPLUG_MPDEC, true, context);
    }

    public static boolean isMpdecisionActive() {
        return Utils.isServiceActive(HOTPLUG_MPDEC);
    }

    public static boolean hasMpdecision() {
        return Utils.existFile(HOTPLUG_MPDECISION_BINARY);
    }

    public static boolean hasCpuHotplug() {
        for (String[] array : CPU_HOTPLUG_ARRAY)
            for (String file : array) if (Utils.existFile(file)) return true;
        return false;
    }

}
