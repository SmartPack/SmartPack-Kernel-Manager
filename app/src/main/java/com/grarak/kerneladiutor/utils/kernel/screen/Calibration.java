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
package com.grarak.kerneladiutor.utils.kernel.screen;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 01.06.16.
 */
public class Calibration {

    private static final String KCAL = "/sys/devices/platform/kcal_ctrl.0";
    private static final String KCAL_CTRL = KCAL + "/kcal";
    private static final String KCAL_CTRL_CTRL = KCAL + "/kcal_ctrl";
    private static final String KCAL_CTRL_ENABLE = KCAL + "/kcal_enable";
    private static final String KCAL_CTRL_MIN = KCAL + "/kcal_min";
    private static final String KCAL_CTRL_INVERT = KCAL + "/kcal_invert";
    private static final String KCAL_CTRL_SAT = KCAL + "/kcal_sat";
    private static final String KCAL_CTRL_HUE = KCAL + "/kcal_hue";
    private static final String KCAL_CTRL_VAL = KCAL + "/kcal_val";
    private static final String KCAL_CTRL_CONT = KCAL + "/kcal_cont";

    private static final String DIAG0 = "/sys/devices/platform/DIAG0.0";
    private static final String DIAG0_POWER = DIAG0 + "/power_rail";
    private static final String DIAG0_POWER_CTRL = DIAG0 + "/power_rail_ctrl";

    private static final String COLOR_CONTROL = "/sys/class/misc/colorcontrol";
    private static final String COLOR_CONTROL_MUILTIPLIER = COLOR_CONTROL + "/multiplier";
    private static final String COLOR_CONTROL_CTRL = COLOR_CONTROL + "/safety_enabled";

    private static final String SAMOLED_COLOR = "/sys/class/misc/samoled_color";
    private static final String SAMOLED_COLOR_RED = SAMOLED_COLOR + "/red_multiplier";
    private static final String SAMOLED_COLOR_GREEN = SAMOLED_COLOR + "/green_multiplier";
    private static final String SAMOLED_COLOR_BLUE = SAMOLED_COLOR + "/blue_multiplier";

    private static final String FB0_RGB = "/sys/class/graphics/fb0/rgb";
    private static final String FB_KCAL = "/sys/class/graphics/fb0/kcal";

    private static final String HBM = "/sys/class/graphics/fb0/hbm";

    private static final String SRGB = "/sys/class/graphics/fb0/SRGB";

    private static final List<String> sColors = new ArrayList<>();
    private static final List<String> sColorEnables = new ArrayList<>();
    private static final List<String> sNewKCAL = new ArrayList<>();

    static {
        sColors.add(KCAL_CTRL);
        sColors.add(DIAG0_POWER);
        sColors.add(COLOR_CONTROL_MUILTIPLIER);
        sColors.add(SAMOLED_COLOR);
        sColors.add(FB0_RGB);
        sColors.add(FB_KCAL);

        sColorEnables.add(KCAL_CTRL_CTRL);
        sColorEnables.add(KCAL_CTRL_ENABLE);
        sColorEnables.add(DIAG0_POWER_CTRL);
        sColorEnables.add(COLOR_CONTROL_CTRL);

        sNewKCAL.add(KCAL_CTRL_ENABLE);
        sNewKCAL.add(KCAL_CTRL_INVERT);
        sNewKCAL.add(KCAL_CTRL_SAT);
        sNewKCAL.add(KCAL_CTRL_HUE);
        sNewKCAL.add(KCAL_CTRL_VAL);
        sNewKCAL.add(KCAL_CTRL_CONT);
    }

    private static String COLOR;
    private static String COLOR_ENABLE;

    private static boolean HBM_NEW;

    public static void enableSRGB(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", SRGB), SRGB, context);
    }

    public static boolean isSRGBEnabled() {
        return Utils.readFile(SRGB).contains("mode = 1");
    }

    public static boolean hasSRGB() {
        return Utils.existFile(SRGB);
    }

    public static void enableScreenHBM(boolean enable, Context context) {
        run(Control.write(enable ? HBM_NEW ? "2" : "1" : "0", HBM), HBM, context);
    }

    public static boolean isScreenHBMEnabled() {
        if (HBM_NEW) {
            return Utils.readFile(HBM).contains("= 2");
        }
        return Utils.readFile(HBM).equals("1");
    }

    public static boolean hasScreenHBM() {
        boolean supported = Utils.existFile(HBM);
        if (supported) {
            HBM_NEW = Utils.readFile(HBM).contains("2-->HBM Enabled");
            return true;
        }
        return false;
    }

    public static void setScreenContrast(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_CONT), KCAL_CTRL_CONT, context);
    }

    public static int getScreenContrast() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_CONT));
    }

    public static boolean hasScreenContrast() {
        return Utils.existFile(KCAL_CTRL_CONT);
    }

    public static void setScreenValue(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_VAL), KCAL_CTRL_VAL, context);
    }

    public static int getScreenValue() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_VAL));
    }

    public static boolean hasScreenValue() {
        return Utils.existFile(KCAL_CTRL_VAL);
    }

    public static void setScreenHue(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_HUE), KCAL_CTRL_HUE, context);
    }

    public static int getScreenHue() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_HUE));
    }

    public static boolean hasScreenHue() {
        return Utils.existFile(KCAL_CTRL_HUE);
    }

    public static void enableGrayscaleMode(boolean enable, Context context) {
        setSaturationIntensity(enable ? 128 : 255, context);
    }

    public static void setSaturationIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_SAT), KCAL_CTRL_SAT, context);
    }

    public static int getSaturationIntensity() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_SAT));
    }

    public static boolean hasSaturationIntensity() {
        return Utils.existFile(KCAL_CTRL_SAT);
    }

    public static void enableInvertScreen(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", KCAL_CTRL_INVERT), KCAL_CTRL_INVERT, context);
    }

    public static boolean isInvertScreenEnabled() {
        return Utils.readFile(KCAL_CTRL_INVERT).equals("1");
    }

    public static boolean hasInvertScreen() {
        return Utils.existFile(KCAL_CTRL_INVERT);
    }

    public static void setMinColor(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_MIN), KCAL_CTRL_MIN, context);
    }

    public static int getMinColor() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_MIN));
    }

    public static boolean hasMinColor() {
        return Utils.existFile(KCAL_CTRL_MIN);
    }

    public static void setColors(String values, Context context) {
        if (hasColorEnable() && COLOR_CONTROL_CTRL.equals(COLOR_ENABLE)) {
            run(Control.write("0", COLOR_CONTROL_CTRL), COLOR_CONTROL_CTRL, context);
        }

        switch (COLOR) {
            case COLOR_CONTROL_MUILTIPLIER: {
                String[] colors = values.split(" ");
                String red = String.valueOf(Utils.strToLong(colors[0]) * 10000000L);
                String green = String.valueOf(Utils.strToLong(colors[1]) * 10000000L);
                String blue = String.valueOf(Utils.strToLong(colors[2]) * 10000000L);
                run(Control.write(red + " " + green + " " + blue, COLOR_CONTROL_MUILTIPLIER),
                        COLOR_CONTROL_MUILTIPLIER, context);
                break;
            }
            case SAMOLED_COLOR: {
                String[] colors = values.split(" ");
                run(Control.write(String.valueOf(Utils.strToLong(colors[0]) * 10000000),
                        SAMOLED_COLOR_RED), SAMOLED_COLOR_RED, context);
                run(Control.write(String.valueOf(Utils.strToLong(colors[1]) * 10000000),
                        SAMOLED_COLOR_RED), SAMOLED_COLOR_GREEN, context);
                run(Control.write(String.valueOf(Utils.strToLong(colors[2]) * 10000000),
                        SAMOLED_COLOR_RED), SAMOLED_COLOR_BLUE, context);
                break;
            }
            default:
                run(Control.write(values, COLOR), COLOR, context);
                break;
        }

        if (hasColorEnable() && !COLOR_CONTROL_CTRL.equals(COLOR_ENABLE)) {
            run(Control.write("1", COLOR_ENABLE), COLOR_ENABLE, context);
        }
    }

    public static List<String> getLimits() {
        List<String> list = new ArrayList<>();
        switch (COLOR) {
            case COLOR_CONTROL_MUILTIPLIER:
            case SAMOLED_COLOR:
                for (int i = 60; i <= 400; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            case FB0_RGB:
                for (int i = 255; i <= 32768; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            case FB_KCAL:
                for (int i = 0; i < 256; i++) {
                    list.add(String.valueOf(i));
                }
                break;
            default:
                int max = hasNewKCAL() ? 256 : 255;
                for (int i = 0; i <= max; i++) {
                    list.add(String.valueOf(i));
                }
                break;
        }
        return list;
    }

    private static boolean hasNewKCAL() {
        for (String file : sNewKCAL) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getColors() {
        List<String> list = new ArrayList<>();
        switch (COLOR) {
            case COLOR_CONTROL_MUILTIPLIER:
                for (String color : Utils.readFile(COLOR_CONTROL_MUILTIPLIER).split(" ")) {
                    list.add(String.valueOf(Utils.strToLong(color) / 10000000L));
                }
                break;
            case SAMOLED_COLOR:
                if (Utils.existFile(SAMOLED_COLOR_RED)) {
                    long color = Utils.strToLong(Utils.readFile(SAMOLED_COLOR_RED)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                if (Utils.existFile(SAMOLED_COLOR_GREEN)) {
                    long color = Utils.strToLong(Utils.readFile(SAMOLED_COLOR_GREEN)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                if (Utils.existFile(SAMOLED_COLOR_BLUE)) {
                    long color = Utils.strToLong(Utils.readFile(SAMOLED_COLOR_BLUE)) / 10000000L;
                    list.add(String.valueOf(color));
                }
                break;
            default:
                for (String color : Utils.readFile(COLOR).split(" ")) {
                    list.add(String.valueOf(Utils.strToLong(color)));
                }
                break;
        }
        return list;
    }

    private static boolean hasColorEnable() {
        if (COLOR_ENABLE != null) return true;
        for (String file : sColorEnables) {
            if (Utils.existFile(file)) {
                COLOR_ENABLE = file;
                return true;
            }
        }
        return false;
    }

    public static boolean hasColors() {
        if (COLOR != null) return true;
        for (String file : sColors) {
            if (Utils.existFile(file)) {
                COLOR = file;
                return true;
            }
        }
        return false;
    }

    public static boolean supported() {
        return hasColors() || hasInvertScreen() || hasSaturationIntensity() || hasScreenHue()
                || hasScreenValue() | hasScreenContrast() || hasScreenHBM() || hasSRGB();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
