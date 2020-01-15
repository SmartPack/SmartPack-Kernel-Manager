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
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 5, 2019
 */

public class KLapse {

    private static final String KLAPSE = "/sys/module/klapse/parameters";
    private static final String KLAPSE_ENABLE = KLAPSE + "/enabled_mode";
    private static final String KLAPSE_TARGET_R = KLAPSE + "/target_r";
    private static final String KLAPSE_TARGET_G = KLAPSE + "/target_g";
    private static final String KLAPSE_TARGET_B = KLAPSE + "/target_b";
    private static final String DAYTIME_R = KLAPSE + "/daytime_r";
    private static final String DAYTIME_G = KLAPSE + "/daytime_g";
    private static final String DAYTIME_B = KLAPSE + "/daytime_b";
    private static final String KLAPSE_START_MIN = KLAPSE + "/start_minute";
    private static final String KLAPSE_END_MIN = KLAPSE + "/stop_minute";
    private static final String KLAPSE_TARGET_MIN = KLAPSE + "/target_minutes";
    private static final String FADEBACK_MINUTES = KLAPSE + "/fadeback_minutes";
    private static final String DIMMER_FACTOR = KLAPSE + "/dimmer_factor";
    private static final String DIMMER_FACTOR_AUTO = KLAPSE + "/dimmer_factor_auto";
    private static final String DIMMER_START = KLAPSE + "/dimmer_auto_start_minute";
    private static final String DIMMER_END = KLAPSE + "/dimmer_auto_stop_minute";
    private static final String PULSE_FREQ = KLAPSE + "/pulse_freq";
    private static final String FLOW_FREQ = KLAPSE + "/flow_freq";
    private static final String BACKLIGHT_RANGE_UPPER = KLAPSE + "/bl_range_upper";
    private static final String BACKLIGHT_RANGE_LOWER = KLAPSE + "/bl_range_lower";
    private static final String KLAPSE_VERSION = "/sys/module/klapse/version";

    public static boolean hasEnable() {
        return Utils.existFile(KLAPSE_ENABLE);
    }

    public static List<String> enable(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.klapse_off));
        list.add(context.getString(R.string.time_scale));
        list.add(context.getString(R.string.bright_scale));
        return list;
    }

    public static int getklapseEnable() {
        return Utils.strToInt(Utils.readFile(KLAPSE_ENABLE));
    }

    public static void setklapseEnable(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_ENABLE), KLAPSE_ENABLE, context);
    }

    public static void setklapseStart(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_START_MIN), KLAPSE_START_MIN, context);
    }

    public static String getklapseStartRaw() {
        return Utils.readFile(KLAPSE_START_MIN);
    }

    public static String getklapseStart() {
        return getAdjustedTime(getklapseStartRaw());
    }

    public static boolean hasklapseStart() {
        return Utils.existFile(KLAPSE_START_MIN);
    }

    public static void setklapseStop(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_END_MIN), KLAPSE_END_MIN, context);
    }

    public static String getklapseStopRaw() {
        return Utils.readFile(KLAPSE_END_MIN);
    }

    public static String getklapseStop() {
        return getAdjustedTime(getklapseStopRaw());
    }

    public static boolean hasklapseStop() {
        return Utils.existFile(KLAPSE_END_MIN);
    }

    public static void setScalingRate(String value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_MIN), KLAPSE_TARGET_MIN, context);
    }

    public static String getScalingRate() {
        return Utils.readFile(KLAPSE_TARGET_MIN);
    }

    public static boolean hasScalingRate() {
        return Utils.existFile(KLAPSE_TARGET_MIN);
    }

    public static void setFadeBackMinutes(String value, Context context) {
        run(Control.write(String.valueOf(value), FADEBACK_MINUTES), FADEBACK_MINUTES, context);
    }

    public static String getFadeBackMinutes() {
        return Utils.readFile(FADEBACK_MINUTES);
    }

    public static boolean hasFadeBackMinutes() {
        return Utils.existFile(FADEBACK_MINUTES);
    }

    public static void setklapseRed(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_R), KLAPSE_TARGET_R, context);
    }

    public static int getklapseRed() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_R));
    }

    public static boolean hasklapseRed() {
        return Utils.existFile(KLAPSE_TARGET_R);
    }

    public static void setklapseGreen(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_G), KLAPSE_TARGET_G, context);
    }

    public static int getklapseGreen() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_G));
    }

    public static boolean hasklapseGreen() {
        return Utils.existFile(KLAPSE_TARGET_G);
    }

    public static void setklapseBlue(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_B), KLAPSE_TARGET_B, context);
    }

    public static int getklapseBlue() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_B));
    }

    public static boolean hasklapseBlue() {
        return Utils.existFile(KLAPSE_TARGET_B);
    }

    public static void setDayTimeRed(int value, Context context) {
        run(Control.write(String.valueOf(value), DAYTIME_R), DAYTIME_R, context);
    }

    public static int getDayTimeRed() {
        return Utils.strToInt(Utils.readFile(DAYTIME_R));
    }

    public static boolean hasDayTimeRed() {
        return Utils.existFile(DAYTIME_R);
    }

    public static void setDayTimeGreen(int value, Context context) {
        run(Control.write(String.valueOf(value), DAYTIME_G), DAYTIME_G, context);
    }

    public static int getDayTimeGreen() {
        return Utils.strToInt(Utils.readFile(DAYTIME_G));
    }

    public static boolean hasDayTimeGreen() {
        return Utils.existFile(DAYTIME_G);
    }

    public static void setDayTimeBlue(int value, Context context) {
        run(Control.write(String.valueOf(value), DAYTIME_B), DAYTIME_B, context);
    }

    public static int getDayTimeBlue() {
        return Utils.strToInt(Utils.readFile(DAYTIME_B));
    }

    public static boolean hasDayTimeBlue() {
        return Utils.existFile(DAYTIME_B);
    }

    public static void setBrightnessFactor(int value, Context context) {
        run(Control.write(String.valueOf(value), DIMMER_FACTOR), DIMMER_FACTOR, context);
    }

    public static int getBrightnessFactor() {
        return Utils.strToInt(Utils.readFile(DIMMER_FACTOR));
    }

    public static boolean hasDimmerFactor() {
        return Utils.existFile(DIMMER_FACTOR);
    }

    public static void enableAutoBrightnessFactor(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", DIMMER_FACTOR_AUTO), DIMMER_FACTOR_AUTO, context);
    }

    public static boolean isAutoBrightnessFactorEnabled() {
        return Utils.readFile(DIMMER_FACTOR_AUTO).startsWith("1")
                || Utils.readFile(DIMMER_FACTOR_AUTO).startsWith("Y");
    }

    public static boolean hasAutoBrightnessFactor() {
        return Utils.existFile(DIMMER_FACTOR_AUTO);
    }

    public static void setBrightFactStart(int value, Context context) {
        run(Control.write(String.valueOf(value), DIMMER_START), DIMMER_START, context);
    }

    public static String getBrightFactStartRaw() {
        return Utils.readFile(DIMMER_START);
    }

    public static String getBrightFactStart() {
        return getAdjustedTime(getBrightFactStartRaw());
    }

    public static boolean hasDimmerStart() {
        return Utils.existFile(DIMMER_START);
    }

    public static void setBrightFactStop(int value, Context context) {
        run(Control.write(String.valueOf(value), DIMMER_END), DIMMER_END, context);
    }

    public static String getBrightFactStopRaw() {
        return Utils.readFile(DIMMER_END);
    }

    public static String getBrightFactStop() {
        return getAdjustedTime(getBrightFactStopRaw());
    }

    public static boolean hasDimmerStop() {
        return Utils.existFile(DIMMER_END);
    }

    public static void setBLRangeUpper(String value, Context context) {
        run(Control.write(String.valueOf(value), BACKLIGHT_RANGE_UPPER), BACKLIGHT_RANGE_UPPER, context);
    }

    public static String getBLRangeUpper() {
        return Utils.readFile(BACKLIGHT_RANGE_UPPER);
    }

    public static boolean hasBLRangeUpper() {
        return Utils.existFile(BACKLIGHT_RANGE_UPPER);
    }

    public static void setBLRangeLower(String value, Context context) {
        run(Control.write(String.valueOf(value), BACKLIGHT_RANGE_LOWER), BACKLIGHT_RANGE_LOWER, context);
    }

    public static String getBLRangeLower() {
        return Utils.readFile(BACKLIGHT_RANGE_LOWER);
    }

    public static boolean hasBLRangeLower() {
        return Utils.existFile(BACKLIGHT_RANGE_LOWER);
    }

    public static void setPulseFreq(String value, Context context) {
        run(Control.write(String.valueOf(value), PULSE_FREQ), PULSE_FREQ, context);
    }

    public static String getPulseFreq() {
        return Utils.readFile(PULSE_FREQ);
    }

    public static boolean hasPulseFreq() {
        return Utils.existFile(PULSE_FREQ);
    }

    public static void setFlowFreq(String value, Context context) {
        run(Control.write(String.valueOf(value), FLOW_FREQ), FLOW_FREQ, context);
    }

    public static String getFlowFreq() {
        return Utils.readFile(FLOW_FREQ);
    }

    public static boolean hasFlowFreq() {
        return Utils.existFile(FLOW_FREQ);
    }

    public static boolean hasklapseVersion() {
        return Utils.existFile(KLAPSE_VERSION);
    }

    public static String getklapseVersion() {
        return Utils.readFile(KLAPSE_VERSION);
    }

    /*
     * Convert K-lapse schedule times (in minutes) into a human readable format
     * (hr:min & AM/PM)
     */
    public static String getAdjustedTime(String string) {
        int time = Utils.strToInt(string);
        int timeHr = time / 60;
        int timeMin = time - (timeHr * 60);
        return (timeHr > 12 ? timeHr - 12 : timeHr) + ":" + (timeMin < 10 ?
                "0" + timeMin : timeMin) + (timeHr > 12 ? " PM" : " AM");
    }

    private static final String[] KLAPSE_PROFILE = {
            KLAPSE_ENABLE, KLAPSE_TARGET_R, KLAPSE_TARGET_G, KLAPSE_TARGET_B,
            DAYTIME_R, DAYTIME_G, DAYTIME_B, KLAPSE_START_MIN, KLAPSE_END_MIN, KLAPSE_TARGET_MIN,
            FADEBACK_MINUTES, DIMMER_FACTOR, DIMMER_FACTOR_AUTO, DIMMER_START, DIMMER_END, PULSE_FREQ,
            FLOW_FREQ, BACKLIGHT_RANGE_UPPER, BACKLIGHT_RANGE_LOWER
    };

    public static File profileFolder() {
        return new File(Utils.getInternalDataStorage () + "/klapse/");
    }

    public static void prepareProfileFolder() {
        if (profileFolder().exists() && profileFolder().isDirectory()) {
            profileFolder().delete();
        }
        profileFolder().mkdirs();
    }

    public static void exportKlapseSettings(String name, int position) {
        prepareProfileFolder();
        String value = Utils.readFile(KLAPSE_PROFILE[position]);
        if (value.contains("Y")) {
            value = "1";
        } else if (value.contains("N")) {
            value = "0";
        }
        if (Utils.existFile(KLAPSE_PROFILE[position])) {
            String command = "echo " + value + " > " + KLAPSE_PROFILE[position];
            Utils.append (command, profileFolder().toString() + "/" + name);
        }
    }

    public static int size() {
        return KLAPSE_PROFILE.length;
    }

    public static boolean supported() {
        return Utils.existFile(KLAPSE);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.KLAPSE, id, context);
    }

}
