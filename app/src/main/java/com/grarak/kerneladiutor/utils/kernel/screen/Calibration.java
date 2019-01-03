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

    private static Calibration sInstance;

    public static Calibration getInstance() {
        if (sInstance == null) {
            sInstance = new Calibration();
        }
        return sInstance;
    }

    private static final String KCAL = "/sys/devices/platform/kcal_ctrl.0";
    private static final String KCAL_CTRL = KCAL + "/kcal";
    private static final String KCAL_CTRL_CTRL = KCAL + "/kcal_ctrl";
    private static final String KCAL_CTRL_ENABLE = KCAL + "/kcal_enable";

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

    private final List<String> mKCAL_CTRL_MIN = new ArrayList<>();
    private final List<String> mKCAL_CTRL_INVERT = new ArrayList<>();
    private final List<String> mKCAL_CTRL_SAT = new ArrayList<>();
    private final List<String> mKCAL_CTRL_HUE = new ArrayList<>();
    private final List<String> mKCAL_CTRL_VAL = new ArrayList<>();
    private final List<String> mKCAL_CTRL_CONT = new ArrayList<>();

    private final List<String> mSRGB = new ArrayList<>();

    private final List<String> mColors = new ArrayList<>();
    private final List<String> mColorEnables = new ArrayList<>();
    private final List<String> mNewKCAL = new ArrayList<>();

    {
        mKCAL_CTRL_MIN.add("/sys/devices/platform/kcal_ctrl.0/kcal_min");
        mKCAL_CTRL_MIN.add("/sys/module/msm_drm/parameters/kcal_min");
        mKCAL_CTRL_INVERT.add("/sys/devices/platform/kcal_ctrl.0/kcal_invert");
        mKCAL_CTRL_INVERT.add("/sys/module/msm_drm/parameters/kcal_invert");
        mKCAL_CTRL_SAT.add("/sys/devices/platform/kcal_ctrl.0/kcal_sat");
        mKCAL_CTRL_SAT.add("/sys/module/msm_drm/parameters/kcal_sat");
        mKCAL_CTRL_HUE.add("/sys/devices/platform/kcal_ctrl.0/kcal_hue");
        mKCAL_CTRL_HUE.add("/sys/module/msm_drm/parameters/kcal_hue");
        mKCAL_CTRL_VAL.add("/sys/devices/platform/kcal_ctrl.0/kcal_val");
        mKCAL_CTRL_VAL.add("/sys/module/msm_drm/parameters/kcal_val");
        mKCAL_CTRL_CONT.add("/sys/devices/platform/kcal_ctrl.0/kcal_cont");
        mKCAL_CTRL_CONT.add("/sys/module/msm_drm/parameters/kcal_cont");

        mSRGB.add("/sys/class/graphics/fb0/SRGB");
        mSRGB.add("/sys/class/graphics/fb0/srgb");
        mSRGB.add("/sys/devices/platform/soc/ae00000.qcom,mdss_mdp/drm/card0/card0-DSI-1/SRGB");

        mColors.add(KCAL_CTRL);
        mColors.add(DIAG0_POWER);
        mColors.add(COLOR_CONTROL_MUILTIPLIER);
        mColors.add(SAMOLED_COLOR);
        mColors.add(FB0_RGB);
        mColors.add(FB_KCAL);

        mColorEnables.add(KCAL_CTRL_CTRL);
        mColorEnables.add(KCAL_CTRL_ENABLE);
        mColorEnables.add(DIAG0_POWER_CTRL);
        mColorEnables.add(COLOR_CONTROL_CTRL);

        mNewKCAL.add(KCAL_CTRL_ENABLE);
    }

    private String KCAL_CTRL_MIN;
    private String KCAL_CTRL_INVERT;
    private String KCAL_CTRL_SAT;
    private String KCAL_CTRL_HUE;
    private String KCAL_CTRL_VAL;
    private String KCAL_CTRL_CONT;

    private String SRGB;

    private String COLOR;
    private String COLOR_ENABLE;

    private boolean HBM_NEW;

    private Calibration() {
        for (String file : mKCAL_CTRL_MIN) {
            if (Utils.existFile(file)) {
                KCAL_CTRL_MIN = file;
                break;
            }
        }
        for (String file : mKCAL_CTRL_INVERT) {
            if (Utils.existFile(file)) {
                KCAL_CTRL_INVERT = file;
                break;
            }
        }
        for (String file : mKCAL_CTRL_SAT) {
            if (Utils.existFile(file)) {
                KCAL_CTRL_SAT = file;
                break;
            }
        }
        for (String file : mKCAL_CTRL_HUE) {
            if (Utils.existFile(file)) {
                KCAL_CTRL_HUE = file;
                break;
            }
        }
        for (String file : mKCAL_CTRL_VAL) {
            if (Utils.existFile(file)) {
                KCAL_CTRL_VAL = file;
                break;
            }
        }
        for (String file : mKCAL_CTRL_CONT) {
            if (Utils.existFile(file)) {
                KCAL_CTRL_CONT = file;
                break;
            }
        }

        for (String file : mSRGB) {
            if (Utils.existFile(file)) {
                SRGB = file;
                break;
            }
        }

        for (String file : mColors) {
            if (Utils.existFile(file)) {
                COLOR = file;
                break;
            }
        }

        if (COLOR == null) return;
        for (String file : mColorEnables) {
            if (Utils.existFile(file)) {
                COLOR_ENABLE = file;
                break;
            }
        }
    }

    public void enableSRGB(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", SRGB), SRGB, context);
    }

    public boolean isSRGBEnabled() {
        String value = Utils.readFile(SRGB);
        return value.equals("1") || value.contains("mode = 1");
    }

    public boolean hasSRGB() {
        return SRGB != null;
    }

    public void enableScreenHBM(boolean enable, Context context) {
        run(Control.write(enable ? HBM_NEW ? "2" : "1" : "0", HBM), HBM, context);
    }

    public boolean isScreenHBMEnabled() {
        if (HBM_NEW) {
            return Utils.readFile(HBM).contains("= 2");
        }
        return Utils.readFile(HBM).equals("1");
    }

    public boolean hasScreenHBM() {
        boolean supported = Utils.existFile(HBM);
        if (supported) {
            HBM_NEW = Utils.readFile(HBM).contains("2-->HBM Enabled");
            return true;
        }
        return false;
    }

    public void setScreenContrast(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_CONT), KCAL_CTRL_CONT, context);
    }

    public int getScreenContrast() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_CONT));
    }

    public boolean hasScreenContrast() {
        return Utils.existFile(KCAL_CTRL_CONT);
    }

    public void setScreenValue(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_VAL), KCAL_CTRL_VAL, context);
    }

    public int getScreenValue() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_VAL));
    }

    public boolean hasScreenValue() {
        return Utils.existFile(KCAL_CTRL_VAL);
    }

    public void setScreenHue(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_HUE), KCAL_CTRL_HUE, context);
    }

    public int getScreenHue() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_HUE));
    }

    public boolean hasScreenHue() {
        return Utils.existFile(KCAL_CTRL_HUE);
    }

    public void enableGrayscaleMode(boolean enable, Context context) {
        setSaturationIntensity(enable ? 128 : 255, context);
    }

    public void setSaturationIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_SAT), KCAL_CTRL_SAT, context);
    }

    public int getSaturationIntensity() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_SAT));
    }

    public boolean hasSaturationIntensity() {
        return Utils.existFile(KCAL_CTRL_SAT);
    }

    public void enableInvertScreen(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", KCAL_CTRL_INVERT), KCAL_CTRL_INVERT, context);
    }

    public boolean isInvertScreenEnabled() {
        return Utils.readFile(KCAL_CTRL_INVERT).equals("1");
    }

    public boolean hasInvertScreen() {
        return Utils.existFile(KCAL_CTRL_INVERT);
    }

    public void setMinColor(int value, Context context) {
        run(Control.write(String.valueOf(value), KCAL_CTRL_MIN), KCAL_CTRL_MIN, context);
    }

    public int getMinColor() {
        return Utils.strToInt(Utils.readFile(KCAL_CTRL_MIN));
    }

    public boolean hasMinColor() {
        return Utils.existFile(KCAL_CTRL_MIN);
    }

    public void setColors(String values, Context context) {
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

    public List<String> getLimits() {
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

    private boolean hasNewKCAL() {
        for (String file : mNewKCAL) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getColors() {
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
                String value = Utils.readFile(COLOR);
                if (value != null) {
                    for (String color : value.split(" ")) {
                        list.add(String.valueOf(Utils.strToLong(color)));
                    }
                }
                break;
        }
        return list;
    }

    private boolean hasColorEnable() {
        return COLOR_ENABLE != null;
    }

    public boolean hasColors() {
        return COLOR != null;
    }

    public boolean supported() {
        return hasColors() || hasInvertScreen() || hasSaturationIntensity() || hasScreenHue()
                || hasScreenValue() | hasScreenContrast() || hasScreenHBM() || hasSRGB();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
