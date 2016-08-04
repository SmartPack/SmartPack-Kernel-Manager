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
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class Misc {

    private static final String DYNAMIC_FSYNC = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
    private static final String GENTLE_FAIR_SLEEPERS = "/sys/kernel/sched/gentle_fair_sleepers";
    private static final String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";
    private static final String HOSTNAME_KEY = "net.hostname";

    private static final List<String> sLoggers = new ArrayList<>();
    private static final List<String> sCrcs = new ArrayList<>();
    private static final List<String> sFsyncs = new ArrayList<>();

    static {
        sLoggers.add("/sys/kernel/logger_mode/logger_mode");
        sLoggers.add("/sys/module/logger/parameters/enabled");
        sLoggers.add("/sys/module/logger/parameters/log_enabled");

        sCrcs.add("/sys/module/mmc_core/parameters/crc");
        sCrcs.add("/sys/module/mmc_core/parameters/use_spi_crc");

        sFsyncs.add("/sys/devices/virtual/misc/fsynccontrol/fsync_enabled");
        sFsyncs.add("/sys/module/sync/parameters/fsync_enabled");
    }

    private static String LOGGER_FILE;
    private static String CRC_FILE;
    private static String FSYNC_FILE;
    private static Boolean FSYNC_USE_INTEGER;


    public static void setHostname(String value, Context context) {
        run(Control.setProp(HOSTNAME_KEY, value), HOSTNAME_KEY, context);
    }

    public static String getHostname() {
        return RootUtils.getProp(HOSTNAME_KEY);
    }

    public static void setTcpCongestion(String tcpCongestion, Context context) {
        run("sysctl -w net.ipv4.tcp_congestion_control=" + tcpCongestion, TCP_AVAILABLE_CONGESTIONS, context);
    }

    public static String getTcpCongestion() {
        return getTcpAvailableCongestions().get(0);
    }

    public static List<String> getTcpAvailableCongestions() {
        return new ArrayList<>(Arrays.asList(Utils.readFile(TCP_AVAILABLE_CONGESTIONS).split(" ")));
    }

    public static void enableGentleFairSleepers(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", GENTLE_FAIR_SLEEPERS), GENTLE_FAIR_SLEEPERS, context);
    }

    public static boolean isGentleFairSleepersEnabled() {
        return Utils.readFile(GENTLE_FAIR_SLEEPERS).equals("1");
    }

    public static boolean hasGentleFairSleepers() {
        return Utils.existFile(GENTLE_FAIR_SLEEPERS);
    }

    public static void enableDynamicFsync(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", DYNAMIC_FSYNC), DYNAMIC_FSYNC, context);
    }

    public static boolean isDynamicFsyncEnabled() {
        return Utils.readFile(DYNAMIC_FSYNC).equals("1");
    }

    public static boolean hasDynamicFsync() {
        return Utils.existFile(DYNAMIC_FSYNC);
    }

    public static void enableFsync(boolean enable, Context context) {
        run(Control.write(FSYNC_USE_INTEGER ? enable ? "1" : "0" : enable ? "Y" : "N", FSYNC_FILE),
                FSYNC_FILE, context);
    }

    public static boolean isFsyncEnabled() {
        return Utils.readFile(FSYNC_FILE).equals(FSYNC_USE_INTEGER ? "1" : "Y");
    }

    public static boolean hasFsync() {
        if (FSYNC_FILE == null) {
            for (String file : sFsyncs) {
                if (Utils.existFile(file)) {
                    FSYNC_FILE = file;
                    FSYNC_USE_INTEGER = Character.isDigit(Utils.readFile(FSYNC_FILE).toCharArray()[0]);
                    return true;
                }
            }
        }
        return FSYNC_FILE != null;
    }

    public static void enableCrc(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CRC_FILE), CRC_FILE, context);
    }

    public static boolean isCrcEnabled() {
        return Utils.readFile(CRC_FILE).equals("1");
    }

    public static boolean hasCrc() {
        if (CRC_FILE == null) {
            for (String file : sCrcs) {
                if (Utils.existFile(file)) {
                    CRC_FILE = file;
                    return true;
                }
            }
        }
        return CRC_FILE != null;
    }

    public static void enableLogger(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LOGGER_FILE), LOGGER_FILE, context);
    }

    public static boolean isLoggerEnabled() {
        return Utils.readFile(LOGGER_FILE).equals("1");
    }

    public static boolean hasLoggerEnable() {
        if (LOGGER_FILE == null) {
            for (String file : sLoggers) {
                if (Utils.existFile(file)) {
                    LOGGER_FILE = file;
                    return true;
                }
            }
        }
        return LOGGER_FILE != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
