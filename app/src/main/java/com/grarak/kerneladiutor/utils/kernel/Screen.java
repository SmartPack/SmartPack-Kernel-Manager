/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class Screen implements Constants {

    private static String SCREEN_CALIBRATION;
    private static String SCREEN_CALIBRATION_CTRL;

    private static String MIN_BRIGHTNESS;

    public static void setBackLightDimmerOffset(int value, Context context) {
        Control.runCommand(String.valueOf(value), LM3630_BACKLIGHT_DIMMER_OFFSET, Control.CommandType.GENERIC, context);
    }

    public static int getBackLightDimmerOffset() {
        return Utils.stringToInt(Utils.readFile(LM3630_BACKLIGHT_DIMMER_OFFSET));
    }

    public static boolean hasBackLightDimmerOffset() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER_OFFSET);
    }

    public static void setBackLightDimmerThreshold(int value, Context context) {
        Control.runCommand(String.valueOf(value), LM3630_BACKLIGHT_DIMMER_THRESHOLD, Control.CommandType.GENERIC, context);
    }

    public static int getBackLightDimmerThreshold() {
        return Utils.stringToInt(Utils.readFile(LM3630_BACKLIGHT_DIMMER_THRESHOLD));
    }

    public static boolean hasBackLightDimmerThreshold() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER_THRESHOLD);
    }

    public static void setMinBrightness(int value, Context context) {
        Control.runCommand(String.valueOf(value), MIN_BRIGHTNESS, Control.CommandType.GENERIC, context);
    }

    public static int getMaxMinBrightness() {
        if (MIN_BRIGHTNESS != null) {
            switch (MIN_BRIGHTNESS) {
                case LM3630_MIN_BRIGHTNESS:
                    return 50;
                case MSM_BACKLIGHT_DIMMER:
                    return 100;
            }
        }
        return 0;
    }

    public static int getCurMinBrightness() {
        return Utils.stringToInt(Utils.readFile(MIN_BRIGHTNESS));
    }

    public static boolean hasMinBrightness() {
        if (MIN_BRIGHTNESS == null)
            for (String file : MIN_BRIGHTNESS_ARRAY)
                if (Utils.existFile(file)) {
                    MIN_BRIGHTNESS = file;
                    break;
                }
        return MIN_BRIGHTNESS != null;
    }

    public static void activateBackLightDimmer(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", LM3630_BACKLIGHT_DIMMER, Control.CommandType.GENERIC, context);
    }

    public static boolean isBackLightDimmerActive() {
        return Utils.readFile(LM3630_BACKLIGHT_DIMMER).equals("Y");
    }

    public static boolean hasBackLightDimmerEnable() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER);
    }

    public static void setScreenContrast(int value, Context context) {
        Control.runCommand(String.valueOf(value), SCREEN_KCAL_CTRL_CONT, Control.CommandType.GENERIC, context);
    }

    public static int getScreenContrast() {
        return Utils.stringToInt(Utils.readFile(SCREEN_KCAL_CTRL_CONT));
    }

    public static boolean hasScreenContrast() {
        return Utils.existFile(SCREEN_KCAL_CTRL_CONT);
    }

    public static void setScreenValue(int value, Context context) {
        Control.runCommand(String.valueOf(value), SCREEN_KCAL_CTRL_VAL, Control.CommandType.GENERIC, context);
    }

    public static int getScreenValue() {
        return Utils.stringToInt(Utils.readFile(SCREEN_KCAL_CTRL_VAL));
    }

    public static boolean hasScreenValue() {
        return Utils.existFile(SCREEN_KCAL_CTRL_VAL);
    }

    public static void setScreenHue(int value, Context context) {
        Control.runCommand(String.valueOf(value), SCREEN_KCAL_CTRL_HUE, Control.CommandType.GENERIC, context);
    }

    public static int getScreenHue() {
        return Utils.stringToInt(Utils.readFile(SCREEN_KCAL_CTRL_HUE));
    }

    public static boolean hasScreenHue() {
        return Utils.existFile(SCREEN_KCAL_CTRL_HUE);
    }

    public static void activateGrayscaleMode(boolean active, Context context) {
        Control.runCommand(active ? "128" : "255", SCREEN_KCAL_CTRL_SAT, Control.CommandType.GENERIC, context);
    }

    public static void setSaturationIntensity(int value, Context context) {
        Control.runCommand(String.valueOf(value), SCREEN_KCAL_CTRL_SAT, Control.CommandType.GENERIC, context);
    }

    public static int getSaturationIntensity() {
        return Utils.stringToInt(Utils.readFile(SCREEN_KCAL_CTRL_SAT));
    }

    public static boolean hasSaturationIntensity() {
        return Utils.existFile(SCREEN_KCAL_CTRL_SAT);
    }

    public static void activateInvertScreen(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", SCREEN_KCAL_CTRL_INVERT, Control.CommandType.GENERIC, context);
    }

    public static boolean isInvertScreenActive() {
        return Utils.readFile(SCREEN_KCAL_CTRL_INVERT).equals("1");
    }

    public static boolean hasInvertScreen() {
        return Utils.existFile(SCREEN_KCAL_CTRL_INVERT);
    }

    public static void setColorCalibrationMin(int value, Context context) {
        Control.runCommand(String.valueOf(value), SCREEN_KCAL_CTRL_MIN, Control.CommandType.GENERIC, context);
    }

    public static int getColorCalibrationMin() {
        return Utils.stringToInt(Utils.readFile(SCREEN_KCAL_CTRL_MIN));
    }

    public static boolean hasColorCalibrationMin() {
        return Utils.existFile(SCREEN_KCAL_CTRL_MIN);
    }

    public static void setColorCalibration(final String colors, final Context context) {
        String[] col = colors.split(" ");
        switch (SCREEN_CALIBRATION) {
            case SCREEN_SAMOLED_COLOR_RED:
                Control.runCommand(String.valueOf(Utils.stringToLong(col[0]) * 10000000), SCREEN_SAMOLED_COLOR_RED,
                        Control.CommandType.GENERIC, context);
                Control.runCommand(String.valueOf(Utils.stringToLong(col[1]) * 10000000), SCREEN_SAMOLED_COLOR_GREEN,
                        Control.CommandType.GENERIC, context);
                Control.runCommand(String.valueOf(Utils.stringToLong(col[2]) * 10000000), SCREEN_SAMOLED_COLOR_BLUE,
                        Control.CommandType.GENERIC, context);
                break;
            case SCREEN_COLOR_CONTROL:
                String red = String.valueOf(Utils.stringToLong(col[0]) * 10000000);
                String green = String.valueOf(Utils.stringToLong(col[1]) * 10000000);
                String blue = String.valueOf(Utils.stringToLong(col[2]) * 10000000);
                Control.runCommand(red + " " + green + " " + blue,
                        SCREEN_COLOR_CONTROL, Control.CommandType.GENERIC, context);
                break;
            default:
                Control.runCommand(colors, SCREEN_CALIBRATION, Control.CommandType.GENERIC, context);
                break;
        }

        if (hasColorCalibrationCtrl())
            Control.runCommand(SCREEN_CALIBRATION_CTRL.equals(SCREEN_COLOR_CONTROL_CTRL) ? "0" : "1",
                    SCREEN_CALIBRATION_CTRL, Control.CommandType.GENERIC, context);
    }

    public static List<String> getColorCalibrationLimits() {
        String[] values;
        switch (SCREEN_CALIBRATION) {
            case SCREEN_SAMOLED_COLOR_RED:
            case SCREEN_COLOR_CONTROL:
                values = new String[341];
                for (int i = 0; i < values.length; i++)
                    values[i] = String.valueOf(i + 60);
                break;
            default:
                int max = 255;
                int min = 0;
                for (String file : SCREEN_KCAL_CTRL_NEW_ARRAY)
                    if (Utils.existFile(file)) {
                        max = 256;
                        min = 0;
                        break;
                    }
                values = new String[max - min + 1];
                for (int i = 0; i < values.length; i++)
                    values[i] = String.valueOf(i);
                break;
        }
        return new ArrayList<>(Arrays.asList(values));
    }

    public static List<String> getColorCalibration() {
        List<String> list = new ArrayList<>();
        if (SCREEN_CALIBRATION != null) {
            if (SCREEN_CALIBRATION.equals(SCREEN_SAMOLED_COLOR_RED)) {
                long red = Utils.stringToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_RED));
                long green = Utils.stringToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_GREEN));
                long blue = Utils.stringToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_BLUE));
                list.add(String.valueOf(red / 10000000));
                list.add(String.valueOf(green / 10000000));
                list.add(String.valueOf(blue / 10000000));
            } else {
                String value = Utils.readFile(SCREEN_CALIBRATION);
                if (value != null) {
                    for (String color : value.split(" ")) {
                        if (SCREEN_CALIBRATION.equals(SCREEN_COLOR_CONTROL))
                            list.add(String.valueOf(Utils.stringToLong(color) / 10000000));
                        else list.add(color);
                    }
                }
            }
        }
        return list;
    }

    public static boolean hasColorCalibrationCtrl() {
        if (SCREEN_CALIBRATION_CTRL == null)
            for (String file : SCREEN_KCAL_CTRL_ARRAY)
                if (Utils.existFile(file)) {
                    SCREEN_CALIBRATION_CTRL = file;
                    return true;
                }
        return SCREEN_CALIBRATION_CTRL != null;
    }

    public static boolean hasColorCalibration() {
        if (SCREEN_CALIBRATION == null) for (String file : SCREEN_KCAL_ARRAY)
            if (Utils.existFile(file)) {
                SCREEN_CALIBRATION = file;
                return true;
            }
        return SCREEN_CALIBRATION != null;
    }

    public static boolean hasScreen() {
        for (String[] array : SCREEN_ARRAY)
            for (String file : array) if (Utils.existFile(file)) return true;
        return false;
    }

}
