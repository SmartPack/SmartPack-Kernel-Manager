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

    private static final String KLAPSE_NEW = "/sys/module/klapse/parameters";
    private static final String KLAPSE_ENABLE_NEW = KLAPSE_NEW + "/enabled_mode";
    private static final String KLAPSE_TARGET_R = KLAPSE_NEW + "/target_r";
    private static final String KLAPSE_TARGET_G = KLAPSE_NEW + "/target_g";
    private static final String KLAPSE_TARGET_B = KLAPSE_NEW + "/target_b";
    private static final String DAYTIME_R = KLAPSE_NEW + "/daytime_r";
    private static final String DAYTIME_G = KLAPSE_NEW + "/daytime_g";
    private static final String DAYTIME_B = KLAPSE_NEW + "/daytime_b";
    private static final String KLAPSE_START_MIN = KLAPSE_NEW + "/start_minute";
    private static final String KLAPSE_END_MIN = KLAPSE_NEW + "/stop_minute";
    private static final String KLAPSE_TARGET_MIN = KLAPSE_NEW + "/target_minutes";
    private static final String FADEBACK_MINUTES_NEW = KLAPSE_NEW + "/fadeback_minutes";
    private static final String DIMMER_FACTOR = KLAPSE_NEW + "/dimmer_factor";
    private static final String DIMMER_FACTOR_AUTO = KLAPSE_NEW + "/dimmer_factor_auto";
    private static final String DIMMER_START = KLAPSE_NEW + "/dimmer_auto_start_minute";
    private static final String DIMMER_END = KLAPSE_NEW + "/dimmer_auto_stop_minute";
    private static final String PULSE_FREQ_NEW = KLAPSE_NEW + "/pulse_freq";
    private static final String FLOW_FREQ = KLAPSE_NEW + "/flow_freq";
    private static final String BACKLIGHT_RANGE_UPPER = KLAPSE_NEW + "/bl_range_upper";
    private static final String BACKLIGHT_RANGE_LOWER = KLAPSE_NEW + "/bl_range_lower";
    private static final String KLAPSE_VERSION = "/sys/module/klapse/version";

    public static boolean hasEnable() {
        return Utils.existFile(KLAPSE_ENABLE) || Utils.existFile(KLAPSE_ENABLE_NEW);
    }

    public static List<String> enable(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.turn_off));
        list.add(context.getString(R.string.time_scale));
        list.add(context.getString(R.string.bright_scale));
        return list;
    }

    public static int getklapseEnable() {
        if (Utils.existFile(KLAPSE_ENABLE)) {
            return Utils.strToInt(Utils.readFile(KLAPSE_ENABLE));
        } else {
            return Utils.strToInt(Utils.readFile(KLAPSE_ENABLE_NEW));
        }
    }

    public static void setklapseEnable(int value, Context context) {
        if (Utils.existFile(KLAPSE_ENABLE)) {
            run(Control.write(String.valueOf(value), KLAPSE_ENABLE), KLAPSE_ENABLE, context);
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_ENABLE_NEW), KLAPSE_ENABLE_NEW, context);
        }
    }

    public static void setklapseStart(int value, Context context) {
        if (hasklapseStartMin()) {
            if (value >= 1439) {
                run(Control.write(String.valueOf(1439), KLAPSE_START_MIN), KLAPSE_START_MIN, context);
            } else {
                run(Control.write(String.valueOf(value), KLAPSE_START_MIN), KLAPSE_START_MIN, context);
            }
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_START_HOUR), KLAPSE_START_HOUR, context);
        }
    }

    public static int getklapseStart() {
        if (hasklapseStartMin()) {
            int value = Utils.strToInt(Utils.readFile(KLAPSE_START_MIN));
            if (value >= 1439) {
                return 1440;
            } else {
                return value;
            }
        } else {
            return Utils.strToInt(Utils.readFile(KLAPSE_START_HOUR));
        }
    }

    public static boolean hasklapseStart() {
        return Utils.existFile(KLAPSE_START_HOUR);
    }

    public static boolean hasklapseStartMin() {
        return Utils.existFile(KLAPSE_START_MIN);
    }

    public static void setklapseStop(int value, Context context) {
        if (hasklapseStopMin()) {
            if (value >= 1439) {
                run(Control.write(String.valueOf(1439), KLAPSE_END_MIN), KLAPSE_END_MIN, context);
            } else {
                run(Control.write(String.valueOf(value), KLAPSE_END_MIN), KLAPSE_END_MIN, context);
            }
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_END_HOUR), KLAPSE_END_HOUR, context);
        }
    }

    public static int getklapseStop() {
        if (hasklapseStopMin()) {
            int value = Utils.strToInt(Utils.readFile(KLAPSE_END_MIN));
            if (value >= 1439) {
                return 1440;
            } else {
                return value;
            }
        } else {
            return Utils.strToInt(Utils.readFile(KLAPSE_END_HOUR));
        }
    }

    public static boolean hasklapseStop() {
        return Utils.existFile(KLAPSE_END_HOUR);
    }

    public static boolean hasklapseStopMin() {
        return Utils.existFile(KLAPSE_END_MIN);
    }

    public static void setScalingRate(String value, Context context) {
        if (Utils.existFile(KLAPSE_TARGET_MIN)) {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_MIN), KLAPSE_TARGET_MIN, context);
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_SCALING_RATE), KLAPSE_SCALING_RATE, context);
        }
    }

    public static String getScalingRate() {
        if (Utils.existFile(KLAPSE_TARGET_MIN)) {
            return Utils.readFile(KLAPSE_TARGET_MIN);
        } else {
            return Utils.readFile(KLAPSE_SCALING_RATE);
        }
    }

    public static boolean hasScalingRate() {
        return Utils.existFile(KLAPSE_SCALING_RATE) || Utils.existFile(KLAPSE_TARGET_MIN);
    }

    public static void setFadeBackMinutes(String value, Context context) {
        if (Utils.existFile(FADEBACK_MINUTES_NEW)) {
            run(Control.write(String.valueOf(value), FADEBACK_MINUTES_NEW), FADEBACK_MINUTES_NEW, context);
        } else {
            run(Control.write(String.valueOf(value), FADEBACK_MINUTES), FADEBACK_MINUTES, context);
        }
    }

    public static String getFadeBackMinutes() {
        if (Utils.existFile(FADEBACK_MINUTES_NEW)) {
            return Utils.readFile(FADEBACK_MINUTES_NEW);
        } else {
            return Utils.readFile(FADEBACK_MINUTES);
        }
    }

    public static boolean hasFadeBackMinutes() {
        return Utils.existFile(FADEBACK_MINUTES) || Utils.existFile(FADEBACK_MINUTES_NEW);
    }

    public static void setklapseRed(int value, Context context) {
        if (Utils.existFile(KLAPSE_TARGET_R)) {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_R), KLAPSE_TARGET_R, context);
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_RED), KLAPSE_TARGET_RED, context);
        }
    }

    public static int getklapseRed() {
        if (Utils.existFile(KLAPSE_TARGET_R)) {
            return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_R));
        } else {
            return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_RED));
        }
    }

    public static boolean hasklapseRed() {
        return Utils.existFile(KLAPSE_TARGET_RED) || Utils.existFile(KLAPSE_TARGET_R);
    }

    public static void setklapseGreen(int value, Context context) {
        if (Utils.existFile(KLAPSE_TARGET_G)) {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_G), KLAPSE_TARGET_G, context);
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_GREEN), KLAPSE_TARGET_GREEN, context);
        }
    }

    public static int getklapseGreen() {
        if (Utils.existFile(KLAPSE_TARGET_G)) {
            return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_G));
        } else {
            return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_GREEN));
        }
    }

    public static boolean hasklapseGreen() {
        return Utils.existFile(KLAPSE_TARGET_GREEN) || Utils.existFile(KLAPSE_TARGET_G);
    }

    public static void setklapseBlue(int value, Context context) {
        if (Utils.existFile(KLAPSE_TARGET_B)) {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_B), KLAPSE_TARGET_B, context);
        } else {
            run(Control.write(String.valueOf(value), KLAPSE_TARGET_BLUE), KLAPSE_TARGET_BLUE, context);
        }
    }

    public static int getklapseBlue() {
        if (Utils.existFile(KLAPSE_TARGET_B)) {
            return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_B));
        } else {
            return Utils.strToInt(Utils.readFile(KLAPSE_TARGET_BLUE));
        }
    }

    public static boolean hasklapseBlue() {
        return Utils.existFile(KLAPSE_TARGET_BLUE) || Utils.existFile(KLAPSE_TARGET_B);
    }

    public static void setDayTimeRed(int value, Context context) {
        if (Utils.existFile(DAYTIME_R)) {
            run(Control.write(String.valueOf(value), DAYTIME_R), DAYTIME_R, context);
        } else {
            run(Control.write(String.valueOf(value), DAYTIME_RED), DAYTIME_RED, context);
        }
    }

    public static int getDayTimeRed() {
        if (Utils.existFile(DAYTIME_R)) {
            return Utils.strToInt(Utils.readFile(DAYTIME_R));
        } else {
            return Utils.strToInt(Utils.readFile(DAYTIME_RED));
        }
    }

    public static boolean hasDayTimeRed() {
        return Utils.existFile(DAYTIME_RED) || Utils.existFile(DAYTIME_R);
    }

    public static void setDayTimeGreen(int value, Context context) {
        if (Utils.existFile(DAYTIME_G)) {
            run(Control.write(String.valueOf(value), DAYTIME_G), DAYTIME_G, context);
        } else {
            run(Control.write(String.valueOf(value), DAYTIME_GREEN), DAYTIME_GREEN, context);
        }
    }

    public static int getDayTimeGreen() {
        if (Utils.existFile(DAYTIME_G)) {
            return Utils.strToInt(Utils.readFile(DAYTIME_G));
        } else {
            return Utils.strToInt(Utils.readFile(DAYTIME_GREEN));
        }
    }

    public static boolean hasDayTimeGreen() {
        return Utils.existFile(DAYTIME_GREEN) || Utils.existFile(DAYTIME_G);
    }

    public static void setDayTimeBlue(int value, Context context) {
        if (Utils.existFile(DAYTIME_B)) {
            run(Control.write(String.valueOf(value), DAYTIME_B), DAYTIME_B, context);
        } else {
            run(Control.write(String.valueOf(value), DAYTIME_BLUE), DAYTIME_BLUE, context);
        }
    }

    public static int getDayTimeBlue() {
        if (Utils.existFile(DAYTIME_B)) {
            return Utils.strToInt(Utils.readFile(DAYTIME_B));
        } else {
            return Utils.strToInt(Utils.readFile(DAYTIME_BLUE));
        }
    }

    public static boolean hasDayTimeBlue() {
        return Utils.existFile(DAYTIME_BLUE) || Utils.existFile(DAYTIME_B);
    }

    public static void setBrightnessFactor(int value, Context context) {
        if (Utils.existFile(DIMMER_FACTOR)) {
            run(Control.write(String.valueOf(value), DIMMER_FACTOR), DIMMER_FACTOR, context);
        } else {
            run(Control.write(String.valueOf(value), BRIGHTNESS_FACTOR), BRIGHTNESS_FACTOR, context);
        }
    }

    public static int getBrightnessFactor() {
        if (Utils.existFile(DIMMER_FACTOR)) {
            return Utils.strToInt(Utils.readFile(DIMMER_FACTOR));
        } else {
            return Utils.strToInt(Utils.readFile(BRIGHTNESS_FACTOR));
        }
    }

    public static boolean hasBrightnessFactor() {
        return Utils.existFile(BRIGHTNESS_FACTOR);
    }

    public static boolean hasDimmerFactor() {
        return Utils.existFile(DIMMER_FACTOR);
    }

    public static void enableAutoBrightnessFactor(boolean enable, Context context) {
        if (Utils.existFile(DIMMER_FACTOR_AUTO)) {
            run(Control.write(enable ? "1" : "0", DIMMER_FACTOR_AUTO), DIMMER_FACTOR_AUTO, context);
        } else {
            run(Control.write(enable ? "1" : "0", BRIGHTNESS_FACTOR_AUTO), BRIGHTNESS_FACTOR_AUTO, context);
        }
    }

    public static boolean isAutoBrightnessFactorEnabled() {
        if (Utils.existFile(DIMMER_FACTOR_AUTO)) {
            return Utils.readFile(DIMMER_FACTOR_AUTO).startsWith("1") || Utils.readFile(DIMMER_FACTOR_AUTO).startsWith("Y");
        } else {
            return Utils.readFile(BRIGHTNESS_FACTOR_AUTO).startsWith("1");
        }
    }

    public static boolean hasAutoBrightnessFactor() {
        return Utils.existFile(BRIGHTNESS_FACTOR_AUTO) || Utils.existFile(DIMMER_FACTOR_AUTO);
    }

    public static void setBrightFactStart(int value, Context context) {
        if (hasDimmerStart()) {
            if (value >= 1439) {
                run(Control.write(String.valueOf(1439), DIMMER_START), DIMMER_START, context);
            } else {
                run(Control.write(String.valueOf(value), DIMMER_START), DIMMER_START, context);
            }
        } else {
            run(Control.write(String.valueOf(value), BRIGHTNESS_FACTOR_START), BRIGHTNESS_FACTOR_START, context);
        }
    }

    public static int getBrightFactStart() {
        if (hasDimmerStart()) {
            int value = Utils.strToInt(Utils.readFile(DIMMER_START));
            if (value >= 1439) {
                return 1440;
            } else {
                return value;
            }
        } else {
            return Utils.strToInt(Utils.readFile(BRIGHTNESS_FACTOR_START));
        }
    }

    public static boolean hasBrightFactStart() {
        return Utils.existFile(BRIGHTNESS_FACTOR_START);
    }

    public static boolean hasDimmerStart() {
        return Utils.existFile(DIMMER_START);
    }

    public static void setBrightFactStop(int value, Context context) {
        if (hasDimmerStop()) {
            if (value >= 1439) {
                run(Control.write(String.valueOf(1439), DIMMER_END), DIMMER_END, context);
            } else {
                run(Control.write(String.valueOf(value), DIMMER_END), DIMMER_END, context);
            }
        } else {
            run(Control.write(String.valueOf(value), BRIGHTNESS_FACTOR_END), BRIGHTNESS_FACTOR_END, context);
        }
    }

    public static int getBrightFactStop() {
        if (hasDimmerStop()) {
            int value = Utils.strToInt(Utils.readFile(DIMMER_END));
            if (value >= 1439) {
                return 1440;
            } else {
                return value;
            }
        } else {
            return Utils.strToInt(Utils.readFile(BRIGHTNESS_FACTOR_END));
        }
    }

    public static boolean hasBrightFactStop() {
        return Utils.existFile(BRIGHTNESS_FACTOR_END);
    }

    public static boolean hasDimmerStop() {
        return Utils.existFile(DIMMER_END);
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
        if (Utils.existFile(PULSE_FREQ_NEW)) {
            run(Control.write(String.valueOf(value), PULSE_FREQ_NEW), PULSE_FREQ_NEW, context);
        } else {
            run(Control.write(String.valueOf(value), PULSE_FREQ), PULSE_FREQ, context);
        }
    }

    public static String getPulseFreq() {
        if (Utils.existFile(PULSE_FREQ_NEW)) {
            return Utils.readFile(PULSE_FREQ_NEW);
        } else {
            return Utils.readFile(PULSE_FREQ);
        }
    }

    public static boolean hasPulseFreq() {
        return Utils.existFile(PULSE_FREQ) || Utils.existFile(PULSE_FREQ_NEW);
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

    public static boolean supported() {
        return Utils.existFile(KLAPSE) || Utils.existFile(KLAPSE_NEW);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.KLAPSE, id, context);
    }

}
