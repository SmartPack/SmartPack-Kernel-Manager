/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils;

import android.content.Context;

import androidx.annotation.StringRes;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 01, 2018
 *
 * Based on the original implementation by Willi Ye <williye97@gmail.com>
 */

public class Wakelocks {

    private static final String BOEFFLAWL = "/sys/devices/virtual/misc/boeffla_wakelock_blocker";
    private static final String VERSION = BOEFFLAWL + "/version";
    private static final String DEBUG = BOEFFLAWL + "/debug";
    private static final String WAKELOCK_BLOCKER = BOEFFLAWL + "/wakelock_blocker";
    private static final String WAKELOCK_BLOCKER_DEFAULT = BOEFFLAWL + "/wakelock_blocker_default";
    private static final String WAKELOCK_SOURCES = "/sys/kernel/debug/wakeup_sources";
    private static final String WLAN_RX_DIVIDER = "/sys/module/bcmdhd/parameters/wl_divide";
    private static final String MSM_HSIC_DIVIDER = "/sys/module/xhci_hcd/parameters/wl_divide";
    private static final String BCMDHD_DIVIDER = "/sys/module/bcmdhd/parameters/wl_divide";
    private static final String WLAN_CTRL_DIVIDER = "/sys/module/bcmdhd/parameters/wlctrl_divide";

    private static final String WL_PARENT_1 = "/sys/module/wakeup/parameters";
    private static final String WL_PARENT_2 = "/sys/module/smb135x_charger/parameters";

    private static final List<Wakelock> sWakelocks = new ArrayList<>();

    static {
        sWakelocks.add(new Wakelock("/sys/module/smb135x_charger/parameters/use_wlock",
                R.string.smb135x_wakelock, R.string.smb135x_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_smb135x_wake_ws",
                R.string.smb135x_wakelock, R.string.smb135x_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_si_ws",
                R.string.sensor_ind_wakelock, R.string.sensor_ind_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_msm_hsic_ws",
                R.string.msm_hsic_host_wakelock, R.string.msm_hsic_host_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/wlan_rx_wake",
                R.string.wlan_rx_wakelock, R.string.wlan_rx_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_rx_wake_ws",
                R.string.wlan_rx_wakelock, R.string.wlan_rx_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/wlan_ctrl_wake",
                R.string.wlan_ctrl_wakelock, R.string.wlan_ctrl_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_ctrl_wake_ws",
                R.string.wlan_ctrl_wakelock, R.string.wlan_ctrl_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/wlan_wake",
                R.string.wlan_wakelock, R.string.wlan_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_wake_ws",
                R.string.wlan_wakelock, R.string.wlan_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_bluesleep_ws",
                R.string.bluesleep_wakelock, R.string.bluesleep_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_ipa_ws",
                R.string.ipa_wakelock, R.string.ipa_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_netlink_ws",
                R.string.netlink_wakelock, R.string.netlink_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_qcom_rx_wakelock_ws",
                R.string.qcom_rx_wakelock, R.string.qcom_rx_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_timerfd_ws",
                R.string.timerfd_wakelock, R.string.timerfd_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_extscan_wl_ws",
                R.string.wlan_extscan_wl_ws_wakelock, R.string.wlan_extscan_wl_ws_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_ws",
                R.string.wlan_ws_wakelock, R.string.wlan_ws_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_bluedroid_timer_ws",
                R.string.bluedroid_timer_wakelock, R.string.bluedroid_timer_wakelock_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_sensorhub_wl",
                R.string.wkl_sensorhub, R.string.wkl_sensorhub_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_ssp_wl",
                R.string.wkl_ssp, R.string.wkl_ssp_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_bcmdhd4359_wl",
                R.string.wkl_gps, R.string.wkl_gps_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_wake_wl",
                R.string.wkl_wireless, R.string.wkl_wireless_summary));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_bluedroid_timer_wl",
                R.string.wkl_bluetooth, R.string.wkl_bluetooth_summary));
    }

    // Wakelocks Order: 0-Name, 1-Time, 2-Wakeups
    private static int mWakelockOrder = 1;

    public static String getboefflawlVersion(){
        return Utils.readFile(VERSION);
    }

    public static void CopyWakelockBlockerDefault(){
        try {
            String wbd = Utils.readFile(WAKELOCK_BLOCKER_DEFAULT);
            if (!wbd.contentEquals("")) {
                String list = "";
                try {
                    list = Utils.readFile(WAKELOCK_BLOCKER);
                    if (list.contentEquals("")) {
                        list = wbd;
                    } else {
                        list = list + ";" + wbd;
                    }
                } catch (Exception ignored) {
                }

                RootUtils.runCommand("echo '" + list + "' > " + WAKELOCK_BLOCKER);
                RootUtils.runCommand("echo '" + "" + "' > " + WAKELOCK_BLOCKER_DEFAULT);
            }
        }catch(Exception ignored){
        }
    }

    public static void setWakelockBlocked(String wakelock, Context context){
        String list = "";
        try {
            list = Utils.readFile(WAKELOCK_BLOCKER);
            if (list.contentEquals("")) {
                list = wakelock;
            } else {
                list += ";" + wakelock;
            }
        } catch (Exception ignored){
        }

        run(Control.write(list, WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    public static void setWakelockAllowed(String wakelock, Context context){
        String list = "";
        try {
            String[] wakes = Utils.readFile(WAKELOCK_BLOCKER).split(";");
            for(String wake : wakes){
                if(!wake.contentEquals(wakelock)){
                    if (list.contentEquals("")) {
                        list = wake;
                    } else {
                        list += ";" + wake;
                    }
                }
            }
        } catch (Exception ignored){
        }

        run(Control.write(list, WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    public static int getWakelockOrder(){
        return mWakelockOrder;
    }

    public static void setWakelockOrder(int order){
        mWakelockOrder = order;
    }

    public static List<WakeLockInfo> getWakelockInfo(){

        List<WakeLockInfo> wakelocksinfo = new ArrayList<>();

        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    wakelocksinfo.add(new WakeLockInfo(wl[0], Integer.valueOf(wl[6]), Integer.valueOf(wl[3])));
                }
            }
        }catch (Exception ignored) {
        }

        String[] blocked = null;

        try {
            blocked = Utils.readFile(WAKELOCK_BLOCKER).split(";");
        }catch (Exception ignored){
        }

        if( blocked != null){
            for (String name_bloqued : blocked) {
                for (WakeLockInfo wakeLockInfo : wakelocksinfo) {
                    if (wakeLockInfo.wName.equals(name_bloqued)) {
                        wakeLockInfo.wState = false;
                        break;
                    }
                }
            }
        }

        Collections.sort(wakelocksinfo, (w2, w1) -> {
            if(mWakelockOrder == 0) {
                return w2.wName.compareTo(w1.wName);
            } else if ( mWakelockOrder == 1){
                return Integer.compare(w1.wTime, w2.wTime);
            } else if (mWakelockOrder == 2){
                return Integer.compare(w1.wWakeups, w2.wWakeups);
            }
            return 0;
        });

        return wakelocksinfo;
    }

    public static boolean boefflawlsupported() {
        return Utils.existFile(BOEFFLAWL);
    }

    public static class Wakelock {

        private final String mPath;
        @StringRes
        private final int mTitle;
        @StringRes
        private final int mDescription;

        private Boolean mExists;

        private Wakelock(String path) {
            this(path, 0, 0);
        }

        private Wakelock(String path, @StringRes int title, @StringRes int description) {
            mPath = path;
            mTitle = title;
            mDescription = description;
        }

        public String getDescription(Context context) {
            return mDescription == 0 ? null : context.getString(mDescription);
        }

        public String getTitle(Context context) {
            if (mTitle != 0) {
                return context.getString(mTitle);
            }

            String[] paths = mPath.split("/");
            return Utils.upperCaseEachWord(paths[paths.length - 1].replace("enable_", "")
                    .replace("_ws", "").replace("_", " "));
        }

        public void enable(boolean enable, Context context) {
            run(Control.write(enable ? "Y" : "N", mPath), mPath, context);
        }

        public boolean isEnabled() {
            return Utils.readFile(mPath).equals("Y");
        }

        public boolean exists() {
            if (mExists == null) {
                return (mExists = Utils.existFile(mPath));
            }
            return mExists;
        }

    }

    public static void setBCMDHDDivider(int value, Context context) {
        run(Control.write(String.valueOf(value), BCMDHD_DIVIDER), BCMDHD_DIVIDER, context);
    }

    public static int getBCMDHDDivider() {
        return Utils.strToInt(Utils.readFile(BCMDHD_DIVIDER));
    }

    public static boolean hasBCMDHDDivider() {
        return Utils.existFile(BCMDHD_DIVIDER);
    }

    public static void setMsmHsicDivider(int value, Context context) {
        run(Control.write(String.valueOf(value == 15 ? 0 : value + 1), MSM_HSIC_DIVIDER),
                MSM_HSIC_DIVIDER, context);
    }

    public static int getMsmHsicDivider() {
        int value = Utils.strToInt(Utils.readFile(MSM_HSIC_DIVIDER));
        return value == 0 ? 16 : value - 1;
    }

    public static boolean hasMsmHsicDivider() {
        return Utils.existFile(MSM_HSIC_DIVIDER);
    }

    public static void setWlanrxDivider(int value, Context context) {
        run(Control.write(String.valueOf(value == 15 ? 0 : value + 1), WLAN_RX_DIVIDER),
                WLAN_RX_DIVIDER, context);
    }

    public static int getWlanrxDivider() {
        int value = Utils.strToInt(Utils.readFile(WLAN_RX_DIVIDER));
        return value == 0 ? 16 : value - 1;
    }

    public static boolean hasWlanrxDivider() {
        return Utils.existFile(WLAN_RX_DIVIDER);
    }

    public static void setWlanctrlDivider(int value, Context context) {
        run(Control.write(String.valueOf(value == 15 ? 0 : value + 1), WLAN_CTRL_DIVIDER),
                WLAN_CTRL_DIVIDER, context);
    }

    public static int getWlanctrlDivider() {
        int value = Utils.strToInt(Utils.readFile(WLAN_CTRL_DIVIDER));
        return value == 0 ? 16 : value - 1;
    }

    public static boolean hasWlanctrlDivider() {
        return Utils.existFile(WLAN_CTRL_DIVIDER);
    }

    public static List<Wakelock> getWakelocks() {
        return sWakelocks;
    }

    public static boolean hasotherWakeLocks() {
        return Utils.existFile(WL_PARENT_1) || Utils.existFile(WL_PARENT_2);
    }

    public static boolean supported() {
        return boefflawlsupported() || hasWlanctrlDivider() || hasWlanrxDivider() || hasMsmHsicDivider()
	|| hasBCMDHDDivider() || hasotherWakeLocks();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
