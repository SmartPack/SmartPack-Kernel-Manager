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
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 08.05.16.
 */
public class BluPlug {

    private static final String HOTPLUG_BLU_PLUG = "/sys/module/blu_plug/parameters";
    private static final String HOTPLUG_BLU_PLUG_ENABLE = HOTPLUG_BLU_PLUG + "/enabled";
    private static final String HOTPLUG_BLU_PLUG_POWERSAVER_MODE = HOTPLUG_BLU_PLUG + "/powersaver_mode";
    private static final String HOTPLUG_BLU_PLUG_MIN_ONLINE = HOTPLUG_BLU_PLUG + "/min_online";
    private static final String HOTPLUG_BLU_PLUG_MAX_ONLINE = HOTPLUG_BLU_PLUG + "/max_online";
    private static final String HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF = HOTPLUG_BLU_PLUG + "/max_cores_screenoff";
    private static final String HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF = HOTPLUG_BLU_PLUG + "/max_freq_screenoff";
    private static final String HOTPLUG_BLU_PLUG_UP_THRESHOLD = HOTPLUG_BLU_PLUG + "/up_threshold";
    private static final String HOTPLUG_BLU_PLUG_UP_TIMER_CNT = HOTPLUG_BLU_PLUG + "/up_timer_cnt";
    private static final String HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT = HOTPLUG_BLU_PLUG + "/down_timer_cnt";

    public static void setBluPlugDownTimerCnt(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT),
                HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT, context);
    }

    public static int getBluPlugDownTimerCnt() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT));
    }

    public static boolean hasBluPlugDownTimerCnt() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT);
    }

    public static void setBluPlugUpTimerCnt(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_BLU_PLUG_UP_TIMER_CNT),
                HOTPLUG_BLU_PLUG_UP_TIMER_CNT, context);
    }

    public static int getBluPlugUpTimerCnt() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_BLU_PLUG_UP_TIMER_CNT));
    }

    public static boolean hasBluPlugUpTimerCnt() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_UP_TIMER_CNT);
    }

    public static void setBluPlugUpThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_BLU_PLUG_UP_THRESHOLD),
                HOTPLUG_BLU_PLUG_UP_THRESHOLD, context);
    }

    public static int getBluPlugUpThreshold() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_BLU_PLUG_UP_THRESHOLD));
    }

    public static boolean hasBluPlugUpThreshold() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_UP_THRESHOLD);
    }

    public static void setBluPlugMaxFreqScreenOff(int position, Context context) {
        String command = position == 0 ? "0" : String.valueOf(CPUFreq.getInstance(context)
                .getFreqs().get(position - 1));
        run(Control.write(command, HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF),
                HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF, context);
    }

    public static int getBluPlugMaxFreqScreenOff() {
        String value = Utils.readFile(HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF);
        if (value.equals("0")) return 0;
        return CPUFreq.getInstance().getFreqs().indexOf(Utils.strToInt(value)) + 1;
    }

    public static boolean hasBluPlugMaxFreqScreenOff() {
        return !CPUFreq.getInstance().hasMaxScreenOffFreq()
                && Utils.existFile(HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF);
    }

    public static void setBluPlugMaxCoresScreenOff(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF),
                HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF, context);
    }

    public static int getBluPlugMaxCoresScreenOff() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF));
    }

    public static boolean hasBluPlugMaxCoresScreenOff() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF);
    }

    public static void setBluPlugMaxOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_BLU_PLUG_MAX_ONLINE),
                HOTPLUG_BLU_PLUG_MAX_ONLINE, context);
    }

    public static int getBluPlugMaxOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_BLU_PLUG_MAX_ONLINE));
    }

    public static boolean hasBluPlugMaxOnline() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MAX_ONLINE);
    }

    public static void setBluPlugMinOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_BLU_PLUG_MIN_ONLINE),
                HOTPLUG_BLU_PLUG_MIN_ONLINE, context);
    }

    public static int getBluPlugMinOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_BLU_PLUG_MIN_ONLINE));
    }

    public static boolean hasBluPlugMinOnline() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_MIN_ONLINE);
    }

    public static void enableBluPlugPowersaverMode(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", HOTPLUG_BLU_PLUG_POWERSAVER_MODE),
                HOTPLUG_BLU_PLUG_POWERSAVER_MODE, context);
    }

    public static boolean isBluPlugPowersaverModeEnabled() {
        return Utils.readFile(HOTPLUG_BLU_PLUG_POWERSAVER_MODE).equals("Y");
    }

    public static boolean hasBluPlugPowersaverMode() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_POWERSAVER_MODE);
    }

    public static void enableBluPlug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_BLU_PLUG_ENABLE), HOTPLUG_BLU_PLUG_ENABLE, context);
    }

    public static boolean isBluPlugEnabled() {
        return Utils.readFile(HOTPLUG_BLU_PLUG_ENABLE).equals("1");
    }

    public static boolean hasBluPlugEnable() {
        return Utils.existFile(HOTPLUG_BLU_PLUG_ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_BLU_PLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
