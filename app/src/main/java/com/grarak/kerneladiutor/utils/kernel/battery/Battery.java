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
package com.grarak.kerneladiutor.utils.kernel.battery;

import android.content.Context;
import android.text.TextUtils;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class Battery {

    private static final String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    private static final String MTP_FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/use_mtp_during_fast_charge";
    private static final String SCREEN_ON_CURRENT_LIMT = "/sys/kernel/fast_charge/screen_on_current_limit";
    private static final String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";
    private static final String AC_CHARGE_LEVEL = "/sys/kernel/fast_charge/ac_levels";
    private static final String USB_CHARGE_LEVEL = "/sys/kernel/fast_charge/usb_levels";
    private static final String WIRELESS_CHARGE_LEVEL = "/sys/kernel/fast_charge/wireless_levels";
    private static final String FAILSAFE_CONTROL = "/sys/kernel/fast_charge/failsafe";
    private static final String FAST_CHARGE = "/sys/kernel/fast_charge";
    private static final String CUSTOM_AC_CHARGE_LEVEL = FAST_CHARGE + "/ac_charge_level";
    private static final String CUSTOM_USB_CHARGE_LEVEL = FAST_CHARGE + "/usb_charge_level";
    private static final String CUSTOM_WIRELESS_CHARGE_LEVEL = FAST_CHARGE + "/wireless_charge_level";

    private static final String CHARGE_RATE = "/sys/kernel/thundercharge_control";
    private static final String CHARGE_RATE_ENABLE = CHARGE_RATE + "/enabled";
    private static final String CUSTOM_CURRENT = CHARGE_RATE + "/custom_current";

    private static Integer sCapacity;
    private static String[] sBatteryAvailable;
    private static String[] sBatteryUSBAvailable;
    private static String[] sBatteryWIRELESSAvailable;

    public static void setChargingCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_CURRENT), CUSTOM_CURRENT, context);
    }

    public static int getChargingCurrent() {
        return Utils.strToInt(Utils.readFile(CUSTOM_CURRENT));
    }

    public static boolean hasChargingCurrent() {
        return Utils.existFile(CUSTOM_CURRENT);
    }

    public static void enableChargeRate(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGE_RATE_ENABLE), CHARGE_RATE_ENABLE, context);
    }

    public static boolean isChargeRateEnabled() {
        return Utils.readFile(CHARGE_RATE_ENABLE).equals("1");
    }

    public static boolean hasChargeRateEnable() {
        return Utils.existFile(CHARGE_RATE_ENABLE);
    }

    public static void setBlx(int value, Context context) {
        run(Control.write(String.valueOf(value == 0 ? 101 : value - 1), BLX), BLX, context);
    }

    public static int getBlx() {
        int value = Utils.strToInt(Utils.readFile(BLX));
        return value > 100 ? 0 : value + 1;
    }

    public static boolean hasBlx() {
        return Utils.existFile(BLX);
    }
    
    public static List<String> enableForceFastCharge(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disabled));
        list.add(context.getString(R.string.enabled));
        list.add(context.getString(R.string.custom_charge));
        return list;
    }

    public static boolean hasForceFastCharge() {
        return Utils.existFile(FORCE_FAST_CHARGE);
    }
    
    public static int getForceFastCharge() {
        return Utils.strToInt(Utils.readFile(FORCE_FAST_CHARGE));
    }
    
    public static void setForceFastCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), FORCE_FAST_CHARGE), FORCE_FAST_CHARGE, context);
    }

    public static boolean hasChargeLevelControlAC() {
        return Utils.existFile(AC_CHARGE_LEVEL);
    }
    
    public static boolean hasChargeLevelCustomAC() {
        return Utils.existFile(CUSTOM_AC_CHARGE_LEVEL);
    }
    
    public static boolean hasChargeCustomAC() {
        return Utils.existFile(CUSTOM_AC_CHARGE_LEVEL);
    }
    
    public static String getChargeLevelCustomAC() {
        return Utils.readFile(CUSTOM_AC_CHARGE_LEVEL);
    }
    
    public static int getChargeCustomAC() {
        return Utils.strToInt(Utils.readFile(CUSTOM_AC_CHARGE_LEVEL));
    }

    public static void setChargeLevelControlAC (String value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_AC_CHARGE_LEVEL), CUSTOM_AC_CHARGE_LEVEL, context);
    }
    
    public static void setChargeControlAC(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_AC_CHARGE_LEVEL), CUSTOM_AC_CHARGE_LEVEL, context);
    }
    
    public static List<String> getChargeLevelControlAC() {
        if (sBatteryAvailable == null) {
            sBatteryAvailable = Utils.readFile(AC_CHARGE_LEVEL).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sBatteryAvailable));
    }

    public static boolean hasChargeLevelControlUSB() {
        return Utils.existFile(USB_CHARGE_LEVEL);
    }
    
    public static boolean hasChargeLevelCustomUSB() {
        return Utils.existFile(CUSTOM_USB_CHARGE_LEVEL);
    }
    
    public static String getChargeLevelCustomUSB() {
        return Utils.readFile(CUSTOM_USB_CHARGE_LEVEL);
    }
    
    public static int getChargeCustomUSB() {
        return Utils.strToInt(Utils.readFile(CUSTOM_USB_CHARGE_LEVEL));
    }
    
    public static List<String> getChargeLevelControlUSB() {
        if (sBatteryUSBAvailable == null) {
            sBatteryUSBAvailable = Utils.readFile(USB_CHARGE_LEVEL).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sBatteryUSBAvailable));
    }
    
    public static void setChargeLevelControlUSB (String value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_USB_CHARGE_LEVEL), CUSTOM_USB_CHARGE_LEVEL, context);
    }
    
    public static void setChargeControlUSB(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_USB_CHARGE_LEVEL), CUSTOM_USB_CHARGE_LEVEL, context);
    }
    
    public static boolean hasChargeLevelControlWIRELESS() {
        return Utils.existFile(WIRELESS_CHARGE_LEVEL);
    }
    
    public static String getChargeLevelCustomWIRELESS() {
        return Utils.readFile(CUSTOM_WIRELESS_CHARGE_LEVEL);
    }
    
    public static List<String> getChargeLevelControlWIRELESS() {
        if (sBatteryWIRELESSAvailable == null) {
            sBatteryWIRELESSAvailable = Utils.readFile(WIRELESS_CHARGE_LEVEL).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sBatteryWIRELESSAvailable));
    }
    
    public static void setChargeLevelControlWIRELESS (String value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_WIRELESS_CHARGE_LEVEL), CUSTOM_WIRELESS_CHARGE_LEVEL, context);
    }
    
    public static boolean hasChargeLevelCustomWireless() {
        return Utils.existFile(CUSTOM_WIRELESS_CHARGE_LEVEL);
    }
    
    public static int getChargeCustomWireless() {
        return Utils.strToInt(Utils.readFile(CUSTOM_WIRELESS_CHARGE_LEVEL));
    }
    
    public static void setChargeControlWireless(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_WIRELESS_CHARGE_LEVEL), CUSTOM_WIRELESS_CHARGE_LEVEL, context);
    }
    
    public static void enableMtpForceFastCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MTP_FORCE_FAST_CHARGE), MTP_FORCE_FAST_CHARGE, context);
    }

    public static boolean isMtpForceFastChargeEnabled() {
        return Utils.readFile(MTP_FORCE_FAST_CHARGE).equals("1");
    }

    public static boolean hasMtpForceFastCharge() {
        return Utils.existFile(MTP_FORCE_FAST_CHARGE);
    }
    
    public static void enableScreenCurrentLimit(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", SCREEN_ON_CURRENT_LIMT), SCREEN_ON_CURRENT_LIMT, context);
    }

    public static boolean isScreenCurrentLimit() {
        return Utils.readFile(SCREEN_ON_CURRENT_LIMT).equals("1");
    }

    public static boolean hasScreenCurrentLimit() {
        return Utils.existFile(SCREEN_ON_CURRENT_LIMT);
    }
    
    public static void enableFailsafe(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FAILSAFE_CONTROL), FAILSAFE_CONTROL, context);
    }

    public static boolean isFailsafe() {
        return Utils.readFile(FAILSAFE_CONTROL).equals("1");
    }

    public static boolean hasFailsafe() {
        return Utils.existFile(FAILSAFE_CONTROL);
    }

    public static int getCapacity(Context context) {
        if (sCapacity == null) {
            try {
                Class<?> powerProfile = Class.forName("com.android.internal.os.PowerProfile");
                Constructor constructor = powerProfile.getDeclaredConstructor(Context.class);
                Object powerProInstance = constructor.newInstance(context);
                Method batteryCap = powerProfile.getMethod("getBatteryCapacity");
                sCapacity = Math.round((long) (double) batteryCap.invoke(powerProInstance));
            } catch (Exception e) {
                e.printStackTrace();
                sCapacity = 0;
            }
        }
        return sCapacity;
    }

    public static boolean hasCapacity(Context context) {
        return getCapacity(context) != 0;
    }

    public static boolean supported(Context context) {
        return hasCapacity(context);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BATTERY, id, context);
    }

}
