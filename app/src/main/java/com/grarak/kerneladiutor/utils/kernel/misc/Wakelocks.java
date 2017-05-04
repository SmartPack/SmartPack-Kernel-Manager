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
import android.support.annotation.StringRes;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 30.06.16.
 */
public class Wakelocks {

    private static final String WLAN_RX_DIVIDER = "/sys/module/bcmdhd/parameters/wl_divide";
    private static final String MSM_HSIC_DIVIDER = "/sys/module/xhci_hcd/parameters/wl_divide";
    private static final String BCMDHD_DIVIDER = "/sys/module/bcmdhd/parameters/wl_divide";

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
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_ipa_ws"));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_netlink_ws"));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_qcom_rx_wakelock_ws"));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_timerfd_ws"));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_extscan_wl_ws"));
        sWakelocks.add(new Wakelock("/sys/module/wakeup/parameters/enable_wlan_ws"));
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

    public static List<Wakelock> getWakelocks() {
        return sWakelocks;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
