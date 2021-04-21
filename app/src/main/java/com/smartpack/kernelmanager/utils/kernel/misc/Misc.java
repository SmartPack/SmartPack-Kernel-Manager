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
package com.smartpack.kernelmanager.utils.kernel.misc;

import android.content.Context;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 29.06.16.
 */
public class Misc {

    private static Misc sInstance;

    public static Misc getInstance() {
        if (sInstance == null) {
            sInstance = new Misc();
        }
        return sInstance;
    }

    private static final String DYNAMIC_FSYNC = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
    private static final String GENTLE_FAIR_SLEEPERS = "/sys/kernel/sched/gentle_fair_sleepers";
    private static final String ARCH_POWER = "/sys/kernel/sched/arch_power";
    private static final String SELINUX = "/sys/fs/selinux/enforce";
    private static final String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";

    private static final String LEASES_ENABLE = "/proc/sys/fs/leases-enable";
    private static final String LEASE_BREAK_TIME = "/proc/sys/fs/lease-break-time";

    private static final String WIREGUARD = "/sys/module/wireguard/version";

    private static final String PRINTK_MODE = "/sys/kernel/printk_mode/printk_mode";
    private static final String PRINTK_DEVKMSG = "/proc/sys/kernel/printk_devkmsg";

    private static final String HOSTNAME_KEY = "net.hostname";

    private static final String DOZE = "dumpsys deviceidle";

    private static final String HAPTICS = "/sys/class/leds/vibrator";
    private static final String HAPTICS_OVERRIDE = HAPTICS + "/vmax_override";
    private static final String HAPTICS_USER = HAPTICS + "/vmax_mv_user";
    private static final String HAPTICS_NOTIFICATION = HAPTICS + "/vmax_mv_strong";
    private static final String HAPTICS_CALL = HAPTICS + "/vmax_mv_call";

    private static final String CPUSET = "/dev/cpuset";
    private static final String[] PARAMETERS = {"audio-app/cpus", "background/cpus", "camera-daemon/cpus",
            "foreground/cpus", "restricted/cpus", "system-background/cpus", "top-app/cpus"};

    private static final String TQL_PARENT = "/sys/class/net";

    private final List<String> mLoggers = new ArrayList<>();
    private final List<String> mCrcs = new ArrayList<>();
    private final List<String> mFsyncs = new ArrayList<>();

    {
        mLoggers.add("/sys/kernel/logger_mode/logger_mode");
        mLoggers.add("/sys/module/logger/parameters/enabled");
        mLoggers.add("/sys/module/logger/parameters/log_enabled");

        mCrcs.add("/sys/module/mmc_core/parameters/crc");
        mCrcs.add("/sys/module/mmc_core/parameters/use_spi_crc");

        mFsyncs.add("/sys/devices/virtual/misc/fsynccontrol/fsync_enabled");
        mFsyncs.add("/sys/module/sync/parameters/fsync_enabled");
    }

    private String LOGGER_FILE;
    private String CRC_FILE;
    private String FSYNC_FILE;
    private Boolean FSYNC_USE_INTEGER;

    private Misc() {
        for (String file : mLoggers) {
            if (Utils.existFile(file)) {
                LOGGER_FILE = file;
                break;
            }
        }

        for (String file : mCrcs) {
            if (Utils.existFile(file)) {
                CRC_FILE = file;
                break;
            }
        }

        for (String file : mFsyncs) {
            if (Utils.existFile(file)) {
                FSYNC_FILE = file;
                FSYNC_USE_INTEGER = Character.isDigit(Utils.readFile(FSYNC_FILE).toCharArray()[0]);
                break;
            }
        }
    }

    public void setHostname(String value, Context context) {
        run(Control.setProp(HOSTNAME_KEY, value), HOSTNAME_KEY, context);
    }

    public String getHostname() {
        return RootUtils.getProp(HOSTNAME_KEY);
    }

    public void setTcpCongestion(String tcpCongestion, Context context) {
        run("sysctl -w net.ipv4.tcp_congestion_control=" + tcpCongestion, TCP_AVAILABLE_CONGESTIONS, context);
    }

    public String getTcpCongestion() {
        return getTcpAvailableCongestions().get(0);
    }

    public List<String> getTcpAvailableCongestions() {
        return new ArrayList<>(Arrays.asList(Utils.readFile(TCP_AVAILABLE_CONGESTIONS).split(" ")));
    }

    public void enableArchPower(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ARCH_POWER), ARCH_POWER, context);
    }

    public boolean isArchPowerEnabled() {
        return Utils.readFile(ARCH_POWER).equals("1");
    }

    public boolean hasArchPower() {
        return Utils.existFile(ARCH_POWER);
    }

    public static List<String> seLinux(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.selinux_permissive));
        list.add(context.getString(R.string.selinux_enforcing));
        return list;
    }

    public static int getSELinux() {
        return Utils.strToInt(Utils.readFile(SELINUX));
    }

    public void setSELinux(int value, Context context) {
        run(Control.write(String.valueOf(value), SELINUX), SELINUX, context);
    }

    public static boolean hasSELinux() {
        return Utils.existFile(SELINUX);
    }

    public static List<String> doze(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.doze_light));
        list.add(context.getString(R.string.doze_deep));
        return list;
    }

    public static int getDozeState() {
        if (isDeepDozeEnabled()) {
            return 2;
        } else if (isLightDozeEnabled()) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setDoze(int value, Context context) {
        switch (value) {
            case 0:
                run(Control.runShellCommand(DOZE + " disable"), DOZE, context);
                break;
            case 1:
                if (isDeepDozeEnabled()) {
                    run(Control.runShellCommand(DOZE + " disable deep"), DOZE, context);
                }
                run(Control.runShellCommand(DOZE + " enable light"), DOZE, context);
                break;
            case 2:
                run(Control.runShellCommand(DOZE + " enable" + " && " + DOZE + " force-idle"), DOZE, context);
                break;
        }
    }

    private static boolean isLightDozeEnabled() {
        return RootUtils.runAndGetOutput(DOZE + " enabled light").equals("1");
    }

    private static boolean isDeepDozeEnabled() {
        return RootUtils.runAndGetOutput(DOZE + " enabled deep").equals("1");
    }

    public static boolean hasDoze() {
        return RootUtils.runAndGetOutput(DOZE + " enabled").equals("1")
                || RootUtils.runAndGetOutput(DOZE + " enabled").equals("0");
    }

    public void enableGentleFairSleepers(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", GENTLE_FAIR_SLEEPERS), GENTLE_FAIR_SLEEPERS, context);
    }

    public boolean isGentleFairSleepersEnabled() {
        return Utils.readFile(GENTLE_FAIR_SLEEPERS).equals("1");
    }

    public boolean hasGentleFairSleepers() {
        return Utils.existFile(GENTLE_FAIR_SLEEPERS);
    }

    public void enableDynamicFsync(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", DYNAMIC_FSYNC), DYNAMIC_FSYNC, context);
    }

    public boolean isDynamicFsyncEnabled() {
        return Utils.readFile(DYNAMIC_FSYNC).equals("1");
    }

    public boolean hasDynamicFsync() {
        return Utils.existFile(DYNAMIC_FSYNC);
    }

    public void enableFsync(boolean enable, Context context) {
        run(Control.write(FSYNC_USE_INTEGER ? enable ? "1" : "0" : enable ? "Y" : "N", FSYNC_FILE),
                FSYNC_FILE, context);
    }

    public boolean isFsyncEnabled() {
        return Utils.readFile(FSYNC_FILE).equals(FSYNC_USE_INTEGER ? "1" : "Y");
    }

    public boolean hasFsync() {
        return FSYNC_FILE != null;
    }

    public void enableCrc(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CRC_FILE), CRC_FILE, context);
    }

    public boolean isCrcEnabled() {
        return Utils.readFile(CRC_FILE).equals("1") || Utils.readFile(CRC_FILE).equals("Y");
    }

    public boolean hasCrc() {
        return CRC_FILE != null;
    }

    public void enableLogger(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LOGGER_FILE), LOGGER_FILE, context);
    }

    public boolean isLoggerEnabled() {
        return Utils.readFile(LOGGER_FILE).equals("1");
    }

    public boolean hasLoggerEnable() {
        return LOGGER_FILE != null;
    }

    public void enablePrintKMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PRINTK_MODE), PRINTK_MODE, context);
    }

    public static boolean isPrintKModeEnabled() {
        return Utils.readFile(PRINTK_MODE).contains("1");
    }

    public static boolean hasPrintKMode() {
        return Utils.existFile(PRINTK_MODE);
    }

    public void enablePrintKDevKMSG(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PRINTK_DEVKMSG), PRINTK_DEVKMSG, context);
    }

    public static boolean isPrintKDevKMSGEnabled() {
        return Utils.readFile(PRINTK_DEVKMSG).contains("1") || Utils.readFile(PRINTK_DEVKMSG).contains("on");
    }

    public static boolean hasPrintKDevKMSG() {
        return Utils.existFile(PRINTK_DEVKMSG);
    }

    public static boolean hasWireguard() {
        return Utils.existFile(WIREGUARD);
    }

    public static String getWireguard() {
        return Utils.readFile(WIREGUARD);
    }

    public void enableLeases(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LEASES_ENABLE), LEASES_ENABLE, context);
    }

    public boolean isLeasesEnabled() {
        return Utils.readFile(LEASES_ENABLE).equals("1");
    }

    public static boolean hasLeases() {
        return Utils.existFile(LEASES_ENABLE);
    }

    public static String getLeaseBreakTime() {
        return Utils.readFile(LEASE_BREAK_TIME);
    }

    public void setLeaseBreakTime(String value, Context context) {
        run(Control.write(String.valueOf(value), LEASE_BREAK_TIME), LEASE_BREAK_TIME, context);
    }

    public static boolean hasLeaseBreakTime() {
        return Utils.existFile(LEASE_BREAK_TIME);
    }

    public void enableHapticOverride(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HAPTICS_OVERRIDE), HAPTICS_OVERRIDE, context);
    }

    public boolean isHapticOverrideEnabled() {
        return Utils.readFile(HAPTICS_OVERRIDE).equals("1");
    }

    public boolean hasHapticOverride() {
        return Utils.existFile(HAPTICS_OVERRIDE);
    }

    public void setHapticUser(int value, Context context) {
        if (value >= 3596) {
            run(Control.write(String.valueOf(3596), HAPTICS_USER), HAPTICS_USER, context);
        } else {
            run(Control.write(String.valueOf(value), HAPTICS_USER), HAPTICS_USER, context);
        }
    }

    public static int getHapticUser() {
        int value = Utils.strToInt(Utils.readFile(HAPTICS_USER));
        if (value >= 3596) {
            return 3600;
        } else {
            return value;
        }
    }

    public static boolean hasHapticUser() {
        return Utils.existFile(HAPTICS_USER);
    }

    public void setHapticsNotification(int value, Context context) {
        if (value >= 3596) {
            run(Control.write(String.valueOf(3596), HAPTICS_NOTIFICATION), HAPTICS_NOTIFICATION, context);
        } else {
            run(Control.write(String.valueOf(value), HAPTICS_NOTIFICATION), HAPTICS_NOTIFICATION, context);
        }
    }

    public static int getHapticsNotification() {
        int value = Utils.strToInt(Utils.readFile(HAPTICS_NOTIFICATION));
        if (value >= 3596) {
            return 3600;
        } else {
            return value;
        }
    }

    public static boolean hasHapticsNotification() {
        return Utils.existFile(HAPTICS_NOTIFICATION);
    }

    public void setHapticsCall(int value, Context context) {
        if (value >= 3596) {
            run(Control.write(String.valueOf(3596), HAPTICS_CALL), HAPTICS_CALL, context);
        } else {
            run(Control.write(String.valueOf(value), HAPTICS_CALL), HAPTICS_CALL, context);
        }
    }

    public static int getHapticsCall() {
        int value = Utils.strToInt(Utils.readFile(HAPTICS_CALL));
        if (value >= 3596) {
            return 3600;
        } else {
            return value;
        }
    }

    public static boolean hasHapticsCall() {
        return Utils.existFile(HAPTICS_CALL);
    }

    public void setValue(String value, int position, Context context) {
        run(Control.write(value, CPUSET + "/" + PARAMETERS[position]), CPUSET + "/" +
                PARAMETERS[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(CPUSET + "/" + PARAMETERS[position]);
    }

    public static String getName(int position) {
        return Utils.upperCaseEachWord(PARAMETERS[position]).replace("/cpus", " CPU's");
    }

    public static boolean exists(int position) {
        return Utils.existFile(CPUSET + "/" + PARAMETERS[position]);
    }

    public static int size() {
        return PARAMETERS.length;
    }

    public static boolean hasCPUSet() {
        return Utils.existFile(CPUSET);
    }

    public static List<String> getTQLList() {
        List<String> mTQLList = new ArrayList<>();
        for (File file : Objects.requireNonNull(SuFile.open(TQL_PARENT).listFiles())) {
            if (Utils.existFile(TQL_PARENT + "/" + file.getName() + "/tx_queue_len")) {
                mTQLList.add(file.getName());
            }
        }
        return mTQLList;
    }

    public static String getTQLValue(String string) {
        return Utils.readFile(TQL_PARENT + "/" + string + "/tx_queue_len");
    }

    public void setTQLValue(String value, String string, Context context) {
        run(Control.write(value, TQL_PARENT + "/" + string + "/tx_queue_len"), TQL_PARENT + "/" + string + "/tx_queue_len", context);
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}