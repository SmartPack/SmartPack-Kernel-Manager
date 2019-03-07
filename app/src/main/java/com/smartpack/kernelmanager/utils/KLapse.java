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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 5, 2019
 */

public class KLapse {

    private static final String KLAPSE = "/sys/klapse";
    private static final String KLAPSE_ENABLE = KLAPSE + "/enable_klapse";
    private static final String KLAPSE_TARGET_RED = KLAPSE + "/target_r";
    private static final String KLAPSE_TARGET_GREEN = KLAPSE + "/target_g";
    private static final String KLAPSE_TARGET_BLUE = KLAPSE + "/target_b";
    private static final String DAYTIME_RED = KLAPSE + "/daytime_r";
    private static final String DAYTIME_GREEN = KLAPSE + "/daytime_g";
    private static final String DAYTIME_BLUE = KLAPSE + "/daytime_b";
    private static final String KLAPSE_START_HOUR = KLAPSE + "/klapse_start_hour";
    private static final String KLAPSE_END_HOUR = KLAPSE + "/klapse_stop_hour";
    private static final String KLAPSE_SCALING_RATE = KLAPSE + "/klapse_scaling_rate";
    private static final String FADEBACK_MINUTES = KLAPSE + "/fadeback_minutes";
    private static final String BRIGHTNESS_FACTOR = KLAPSE + "/brightness_factor";
    private static final String BRIGHTNESS_FACTOR_AUTO = KLAPSE + "/brightness_factor_auto";
    private static final String BRIGHTNESS_FACTOR_START = KLAPSE + "/brightness_factor_auto_start_hour";
    private static final String BRIGHTNESS_FACTOR_END = KLAPSE + "/brightness_factor_auto_stop_hour";
    private static final String PULSE_FREQ = KLAPSE + "/pulse_freq";
    private static final String BACKLIGHT_RANGE = KLAPSE + "/backlight_range";

    public static boolean hasEnable() {
        return Utils.existFile(KLAPSE_ENABLE);
    }

    public static List<String> enable(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.turn_off));
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
        run(Control.write(String.valueOf(value), KLAPSE_START_HOUR), KLAPSE_START_HOUR, context);
    }

    public static int getklapseStart() {
        return Utils.strToInt(Utils.readFile(KLAPSE_START_HOUR));
    }

    public static boolean hasklapseStart() {
        return Utils.existFile(KLAPSE_START_HOUR);
    }

    public static void setklapseStop(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_END_HOUR), KLAPSE_END_HOUR, context);
    }

    public static int getklapseStop() {
        return Utils.strToInt(Utils.readFile(KLAPSE_END_HOUR));
    }

    public static boolean hasklapseStop() {
        return Utils.existFile(KLAPSE_END_HOUR);
    }

    public static void setScalingRate(String value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_SCALING_RATE), KLAPSE_SCALING_RATE, context);
    }

    public static String getScalingRate() {
        return Utils.readFile(KLAPSE_SCALING_RATE);
    }

    public static boolean hasScalingRate() {
        return Utils.existFile(KLAPSE_SCALING_RATE);
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
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_RED), KLAPSE_TARGET_RED, context);
    }

    public static int getklapseRed() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_RED));
    }

    public static boolean hasklapseRed() {
        return Utils.existFile(KLAPSE_TARGET_RED);
    }

    public static void setklapseGreen(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_GREEN), KLAPSE_TARGET_GREEN, context);
    }

    public static int getklapseGreen() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_GREEN));
    }

    public static boolean hasklapseGreen() {
        return Utils.existFile(KLAPSE_TARGET_GREEN);
    }

    public static void setklapseBlue(int value, Context context) {
        run(Control.write(String.valueOf(value), KLAPSE_TARGET_BLUE), KLAPSE_TARGET_BLUE, context);
    }

    public static int getklapseBlue() {
        return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_BLUE));
    }

    public static boolean hasklapseBlue() {
        return Utils.existFile(KLAPSE_TARGET_BLUE);
    }

    public static void setDayTimeRed(int value, Context context) {
        run(Control.write(String.valueOf(value), DAYTIME_RED), DAYTIME_RED, context);
    }

    public static int getDayTimeRed() {
        return Utils.strToInt(Utils.readFile(DAYTIME_RED));
    }

    public static boolean hasDayTimeRed() {
        return Utils.existFile(DAYTIME_RED);
    }

    public static void setDayTimeGreen(int value, Context context) {
        run(Control.write(String.valueOf(value), DAYTIME_GREEN), DAYTIME_GREEN, context);
    }

    public static int getDayTimeGreen() {
        return Utils.strToInt(Utils.readFile(DAYTIME_GREEN));
    }

    public static boolean hasDayTimeGreen() {
        return Utils.existFile(DAYTIME_GREEN);
    }

    public static void setDayTimeBlue(int value, Context context) {
        run(Control.write(String.valueOf(value), DAYTIME_BLUE), DAYTIME_BLUE, context);
    }

    public static int getDayTimeBlue() {
        return Utils.strToInt(Utils.readFile(DAYTIME_BLUE));
    }

    public static boolean hasDayTimeBlue() {
        return Utils.existFile(DAYTIME_BLUE);
    }

    public static void setBrightnessFactor(String value, Context context) {
        run(Control.write(String.valueOf(value), BRIGHTNESS_FACTOR), BRIGHTNESS_FACTOR, context);
    }

    public static String getBrightnessFactor() {
        return Utils.readFile(BRIGHTNESS_FACTOR);
    }

    public static boolean hasBrightnessFactor() {
        return Utils.existFile(BRIGHTNESS_FACTOR);
    }

    public static void enableAutoBrightnessFactor(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", BRIGHTNESS_FACTOR_AUTO), BRIGHTNESS_FACTOR_AUTO, context);
    }

    public static boolean isAutoBrightnessFactorEnabled() {
        return Utils.readFile(BRIGHTNESS_FACTOR_AUTO).startsWith("1");
    }

    public static boolean hasAutoBrightnessFactor() {
        return Utils.existFile(BRIGHTNESS_FACTOR_AUTO);
    }

    public static void setBrightFactStart(int value, Context context) {
        run(Control.write(String.valueOf(value), BRIGHTNESS_FACTOR_START), BRIGHTNESS_FACTOR_START, context);
    }

    public static int getBrightFactStart() {
        return Utils.strToInt(Utils.readFile(BRIGHTNESS_FACTOR_START));
    }

    public static boolean hasBrightFactStart() {
        return Utils.existFile(BRIGHTNESS_FACTOR_START);
    }

    public static void setBrightFactStop(int value, Context context) {
        run(Control.write(String.valueOf(value), BRIGHTNESS_FACTOR_END), BRIGHTNESS_FACTOR_END, context);
    }

    public static int getBrightFactStop() {
        return Utils.strToInt(Utils.readFile(BRIGHTNESS_FACTOR_END));
    }

    public static boolean hasBrightFactStop() {
        return Utils.existFile(BRIGHTNESS_FACTOR_END);
    }

    private static int getChecksum(int arg0, int arg1) {
        return (Integer.MAX_VALUE ^ (arg0 & 0xff) + (arg1 & 0xff));
    }

    private static void KLpaseRun(String value, String path, String id, Context context) {
        int checksum = value.contains(" ") ?
                getChecksum(Utils.strToInt(value.split(" ")[0]),
                        Utils.strToInt(value.split(" ")[1])) :
                getChecksum(Utils.strToInt(value), 0);
        run(Control.write(value + " " + checksum, path), id, context);
        run(Control.write(value, path), id + "nochecksum", context);
    }

    public static void setBacklightRange(String channel, String value, Context context) {
        switch (channel) {
            case "min":
                String currentMax = getBacklightRange("max");
		KLpaseRun(value + " " + currentMax, BACKLIGHT_RANGE, BACKLIGHT_RANGE, context);
                break;
            case "max":
                String currentMin = getBacklightRange("min");
		KLpaseRun(currentMin + " " + value, BACKLIGHT_RANGE, BACKLIGHT_RANGE, context);
                break;
        }
    }

    public static String getBacklightRange(String channel) {
        String[] values = Utils.readFile(BACKLIGHT_RANGE).split(" ");
        String Min = String.valueOf(Utils.strToInt(values[0])),
            Max = String.valueOf(Utils.strToInt(values[1]));
        switch (channel) {
            case "min":
                return Min;
            case "max":
                return Max;
        }
        return "";
    }

    public static boolean hasBacklightRange() {
        return Utils.existFile(BACKLIGHT_RANGE);
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

    public static boolean hasKlapse() {
        return Utils.existFile(KLAPSE);
    }

    public static boolean supported() {
        return hasKlapse();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
