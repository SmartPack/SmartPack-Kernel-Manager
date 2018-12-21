/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 21, 2018
 */

public class MSMThermalSimple {

    private static final String PARENT = "/sys/kernel/msm_thermal";
    private static final String ENABLE = PARENT + "/enabled";
    private static final String SAMPLING_MS = PARENT + "/sampling_ms";
    private static final String USER_MAXFREQ = PARENT + "/user_maxfreq";
    private static final String[] THERMAL_ZONES = {"zone0", "zone1", "zone2",
	"zone3", "zone4", "zone5", "zone6", "zone7", "zone8", "zone9", "zone10",
	"zone11", "zone12", "zone13", "zone14", "zone15", "zone16", "zone17",
	"zone18", "zone19", "zone20"};

    public static void enablesimplemsmthermal(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ENABLE), ENABLE, context);
    }

    public static boolean issimplemsmthermalEnabled() {
        return Utils.readFile(ENABLE).equals("1");
    }

    public static boolean hasenableswitch() {
        return Utils.existFile(ENABLE);
    }

    public static String getSamplingMS() {
        return Utils.readFile(SAMPLING_MS);
    }

    public static void setSamplingMS(String value, Context context) {
        run(Control.write(String.valueOf(value), SAMPLING_MS), SAMPLING_MS, context);
    }

    public static boolean hasSamplingMS() {
        return Utils.existFile(SAMPLING_MS);
    }

    public static String getUserMaxFreq() {
        return Utils.readFile(USER_MAXFREQ);
    }

    public static void setUserMaxFreq(String value, Context context) {
        run(Control.write(String.valueOf(value), USER_MAXFREQ), USER_MAXFREQ, context);
    }

    public static boolean hasUserMaxFreq() {
        return Utils.existFile(USER_MAXFREQ);
    }

    public static void setValue(String value, int position, Context context) {
        run(Control.write(value, PARENT + "/" + THERMAL_ZONES[position]), PARENT + "/" +
                THERMAL_ZONES[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(PARENT + "/" + THERMAL_ZONES[position]);
    }

    public static String getName(int position) {
        return THERMAL_ZONES[position];
    }

    public static boolean exists(int position) {
        return Utils.existFile(PARENT + "/" + THERMAL_ZONES[position]);
    }

    public static int size() {
        return THERMAL_ZONES.length;
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.THERMAL, id, context);
    }
}
