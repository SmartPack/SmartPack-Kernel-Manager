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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 30.06.16.
 */
public class Wakelocks {

    private static final String SENSOR_IND = "/sys/module/wakeup/parameters/enable_si_ws";
    private static final String MSM_HSIC_HOST = "/sys/module/wakeup/parameters/enable_msm_hsic_ws";
    private static final String WLAN_RX_DIVIDER = "/sys/module/bcmdhd/parameters/wl_divide";
    private static final String MSM_HSIC_DIVIDER = "/sys/module/xhci_hcd/parameters/wl_divide";

    private static final List<String> sSMB135 = new ArrayList<>();
    private static final List<String> sWlanRx = new ArrayList<>();
    private static final List<String> sWlanctrl = new ArrayList<>();
    private static final List<String> sWlan = new ArrayList<>();

    static {
        sSMB135.add("/sys/module/smb135x_charger/parameters/use_wlock");
        sSMB135.add("/sys/module/wakeup/parameters/enable_smb135x_wake_ws");

        sWlanRx.add("/sys/module/wakeup/parameters/wlan_rx_wake");
        sWlanRx.add("/sys/module/wakeup/parameters/enable_wlan_rx_wake_ws");

        sWlanctrl.add("/sys/module/wakeup/parameters/wlan_ctrl_wake");
        sWlanctrl.add("/sys/module/wakeup/parameters/enable_wlan_ctrl_wake_ws");

        sWlan.add("/sys/module/wakeup/parameters/wlan_wake");
        sWlan.add("/sys/module/wakeup/parameters/enable_wlan_wake_ws");
    }

    private static String SMB135X;
    private static String WLAN_RX;
    private static String WLAN_CTRL;
    private static String WLAN;

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

    public static void enableWlan(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WLAN), WLAN, context);
    }

    public static boolean isWlanEnabled() {
        return Utils.readFile(WLAN).equals("Y");
    }

    public static boolean hasWlan() {
        if (WLAN == null) {
            for (String file : sWlan) {
                if (Utils.existFile(file)) {
                    WLAN = file;
                    return true;
                }
            }
        }
        return WLAN != null;
    }

    public static void enableWlanctrl(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WLAN_CTRL), WLAN_CTRL, context);
    }

    public static boolean isWlanctrlEnabled() {
        return Utils.readFile(WLAN_CTRL).equals("Y");
    }

    public static boolean hasWlanctrl() {
        if (WLAN_CTRL == null) {
            for (String file : sWlanctrl) {
                if (Utils.existFile(file)) {
                    WLAN_CTRL = file;
                    return true;
                }
            }
        }
        return WLAN_CTRL != null;
    }

    public static void enableWlanrx(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", WLAN_RX), WLAN_RX, context);
    }

    public static boolean isWlanrxEnabled() {
        return Utils.readFile(WLAN_RX).equals("Y");
    }

    public static boolean hasWlanrx() {
        if (WLAN_RX == null) {
            for (String file : sWlanRx) {
                if (Utils.existFile(file)) {
                    WLAN_RX = file;
                    return true;
                }
            }
        }
        return WLAN_RX != null;
    }

    public static void enableMsmHsicHost(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", MSM_HSIC_HOST), MSM_HSIC_HOST, context);
    }

    public static boolean isMsmHsicHostEnabled() {
        return Utils.readFile(MSM_HSIC_HOST).equals("Y");
    }

    public static boolean hasMsmHsicHost() {
        return Utils.existFile(MSM_HSIC_HOST);
    }

    public static void enableSensorInd(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SENSOR_IND), SENSOR_IND, context);
    }

    public static boolean isSensorIndEnabled() {
        return Utils.readFile(SENSOR_IND).equals("Y");
    }

    public static boolean hasSensorInd() {
        return Utils.existFile(SENSOR_IND);
    }

    public static void enableSmb135x(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SMB135X), SMB135X, context);
    }

    public static boolean isSmb135xEnabled() {
        return Utils.readFile(SMB135X).equals("Y");
    }

    public static boolean hasSmb135x() {
        if (SMB135X == null) {
            for (String file : sSMB135) {
                if (Utils.existFile(file)) {
                    SMB135X = file;
                    return true;
                }
            }
        }
        return SMB135X != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
