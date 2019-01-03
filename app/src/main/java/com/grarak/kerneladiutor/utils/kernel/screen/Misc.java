/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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
import com.grarak.kerneladiutor.utils.kernel.Switch;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 23.06.16.
 */
public class Misc {

    private static Misc sInstance;

    public static Misc getInstance() {
        if (sInstance == null) {
            sInstance = new Misc();
        }
        return sInstance;
    }

    private static final String LM3530_BRIGTHNESS_MODE = "/sys/devices/i2c-0/0-0038/lm3530_br_mode";
    private static final String LM3530_MIN_BRIGHTNESS = "/sys/devices/i2c-0/0-0038/lm3530_min_br";
    private static final String LM3530_MAX_BRIGHTNESS = "/sys/devices/i2c-0/0-0038/lm3530_max_br";

    private static final String LM3630_BACKLIGHT_DIMMER = "/sys/module/lm3630_bl/parameters/backlight_dimmer";
    private static final String MDSS_BACKLIGHT_DIMMER = "/sys/module/mdss_fb/parameters/backlight_dimmer";
    private static final String LM3630_MIN_BRIGHTNESS = "/sys/module/lm3630_bl/parameters/min_brightness";
    private static final String MSM_BACKLIGHT_DIMMER = "/sys/module/msm_fb/parameters/backlight_dimmer";
    private static final String LM3630_BACKLIGHT_DIMMER_THRESHOLD = "/sys/module/lm3630_bl/parameters/backlight_threshold";
    private static final String LM3630_BACKLIGHT_DIMMER_OFFSET = "/sys/module/lm3630_bl/parameters/backlight_offset";
    private static final String PSB_BL_MIN_BRIGHTNESS = "/sys/class/backlight/psb-bl/min_brightness";

    private static final String NEGATIVE_TOGGLE = "/sys/module/cypress_touchkey/parameters/mdnie_shortcut_enabled";

    private static final String REGISTER_HOOK = "/sys/class/misc/mdnie/hook_intercept";
    private static final String MASTER_SEQUENCE = "/sys/class/misc/mdnie/sequence_intercept";

    private static final String DRM_KCAL_RED = "/sys/module/msm_drm/parameters/kcal_red";
    private static final String DRM_KCAL_GREEN = "/sys/module/msm_drm/parameters/kcal_green";
    private static final String DRM_KCAL_BLUE = "/sys/module/msm_drm/parameters/kcal_blue";

    private final List<String> mBackLightDimmer = new ArrayList<>();
    private final HashMap<String, Integer> mMinBrightnessFiles = new HashMap<>();
    private final HashMap<String, Switch> mGloveMode = new HashMap<>();

    {
        mBackLightDimmer.add(LM3630_BACKLIGHT_DIMMER);
        mBackLightDimmer.add(MDSS_BACKLIGHT_DIMMER);

        mMinBrightnessFiles.put(LM3630_MIN_BRIGHTNESS, 50);
        mMinBrightnessFiles.put(MSM_BACKLIGHT_DIMMER, 100);
        mMinBrightnessFiles.put(PSB_BL_MIN_BRIGHTNESS, 13);

        mGloveMode.put("/sys/devices/virtual/touchscreen/touchscreen_dev/mode", new Switch("glove", "normal"));
        mGloveMode.put("/sys/lenovo_tp_gestures/tpd_glove_status", new Switch("1", "0"));
    }

    private String BACKLIGHT_DIMMER;
    private String MIN_BRIGHTNESS;
    private String GLOVE_MODE;

    private Misc() {
        for (String file : mBackLightDimmer) {
            if (Utils.existFile(file)) {
                BACKLIGHT_DIMMER = file;
                break;
            }
        }

        for (String file : mMinBrightnessFiles.keySet()) {
            if (Utils.existFile(file)) {
                MIN_BRIGHTNESS = file;
                break;
            }
        }

        for (String file : mGloveMode.keySet()) {
            if (Utils.existFile(file)) {
                GLOVE_MODE = file;
                break;
            }
        }
    }

    public void enableGloveMode(boolean enable, Context context) {
        Switch gloveSwitch = mGloveMode.get(GLOVE_MODE);
        run(Control.write(enable ? gloveSwitch.getEnable() : gloveSwitch.getDisable(),
                GLOVE_MODE), GLOVE_MODE, context);
    }

    public boolean isGloveModeEnabled() {
        return Utils.readFile(GLOVE_MODE).equals(mGloveMode.get(GLOVE_MODE).getEnable());
    }

    public boolean hasGloveMode() {
        return GLOVE_MODE != null;
    }

    public void enableMasterSequence(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MASTER_SEQUENCE), MASTER_SEQUENCE, context);
    }

    public boolean isMasterSequenceEnable() {
        return Utils.readFile(MASTER_SEQUENCE).equals("1");
    }

    public boolean hasMasterSequence() {
        return Utils.existFile(MASTER_SEQUENCE);
    }

    public void enableRegisterHook(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", REGISTER_HOOK), REGISTER_HOOK, context);
    }

    public boolean isRegisterHookEnabled() {
        return Utils.readFile(REGISTER_HOOK).equals("1");
    }

    public boolean hasRegisterHook() {
        return Utils.existFile(REGISTER_HOOK);
    }

    public void enableNegativeToggle(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", NEGATIVE_TOGGLE), NEGATIVE_TOGGLE, context);
    }

    public boolean isNegativeToggleEnabled() {
        return Utils.readFile(NEGATIVE_TOGGLE).equals("1");
    }

    public boolean hasNegativeToggle() {
        return Utils.existFile(NEGATIVE_TOGGLE);
    }

    public void setBackLightDimmerOffset(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3630_BACKLIGHT_DIMMER_OFFSET),
                LM3630_BACKLIGHT_DIMMER_OFFSET, context);
    }

    public int getBackLightDimmerOffset() {
        return Utils.strToInt(Utils.readFile(LM3630_BACKLIGHT_DIMMER_OFFSET));
    }

    public boolean hasBackLightDimmerOffset() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER_OFFSET);
    }

    public void setBackLightDimmerThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3630_BACKLIGHT_DIMMER_THRESHOLD),
                LM3630_BACKLIGHT_DIMMER_THRESHOLD, context);
    }

    public int getBackLightDimmerThreshold() {
        return Utils.strToInt(Utils.readFile(LM3630_BACKLIGHT_DIMMER_THRESHOLD));
    }

    public boolean hasBackLightDimmerThreshold() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER_THRESHOLD);
    }

    public void setMinBrightness(int value, Context context) {
        run(Control.write(String.valueOf(value), MIN_BRIGHTNESS), MIN_BRIGHTNESS, context);
    }

    public int getMaxMinBrightness() {
        return mMinBrightnessFiles.get(MIN_BRIGHTNESS);
    }

    public int getCurMinBrightness() {
        return Utils.strToInt(Utils.readFile(MIN_BRIGHTNESS));
    }

    public boolean hasMinBrightness() {
        return MIN_BRIGHTNESS != null;
    }

    public void enableBackLightDimmer(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", BACKLIGHT_DIMMER), BACKLIGHT_DIMMER, context);
    }

    public boolean isBackLightDimmerEnabled() {
        return Utils.readFile(BACKLIGHT_DIMMER).equals("Y");
    }

    public boolean hasBackLightDimmerEnable() {
        return BACKLIGHT_DIMMER != null;
    }

    public void setLcdMaxBrightness(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3530_MAX_BRIGHTNESS), LM3530_MAX_BRIGHTNESS, context);
    }

    public int getLcdMaxBrightness() {
        return Utils.strToInt(Utils.readFile(LM3530_MAX_BRIGHTNESS));
    }

    public boolean hasLcdMaxBrightness() {
        return Utils.existFile(LM3530_MAX_BRIGHTNESS);
    }

    public void setLcdMinBrightness(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3530_MIN_BRIGHTNESS), LM3530_MIN_BRIGHTNESS, context);
    }

    public int getLcdMinBrightness() {
        return Utils.strToInt(Utils.readFile(LM3530_MIN_BRIGHTNESS));
    }

    public boolean hasLcdMinBrightness() {
        return Utils.existFile(LM3530_MIN_BRIGHTNESS);
    }

    public void enableBrightnessMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LM3530_BRIGTHNESS_MODE), LM3530_BRIGTHNESS_MODE, context);
    }

    public boolean isBrightnessModeEnabled() {
        return Utils.readFile(LM3530_BRIGTHNESS_MODE).equals("1");
    }

    public boolean hasBrightnessMode() {
        return Utils.existFile(LM3530_BRIGTHNESS_MODE);
    }

    public void setkcalRed(int value, Context context) {
        run(Control.write(String.valueOf(value), DRM_KCAL_RED), DRM_KCAL_RED, context);
    }

    public static int getkcalRed() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_RED));
    }

    public static boolean haskcalRed() {
        return Utils.existFile(DRM_KCAL_RED);
    }

    public void setkcalGreen(int value, Context context) {
        run(Control.write(String.valueOf(value), DRM_KCAL_GREEN), DRM_KCAL_GREEN, context);
    }

    public static int getkcalGreen() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_GREEN));
    }

    public static boolean haskcalGreen() {
        return Utils.existFile(DRM_KCAL_GREEN);
    }

    public void setkcalBlue(int value, Context context) {
        run(Control.write(String.valueOf(value), DRM_KCAL_BLUE), DRM_KCAL_BLUE, context);
    }

    public static int getkcalBlue() {
        return Utils.strToInt(Utils.readFile(DRM_KCAL_BLUE));
    }

    public static boolean haskcalBlue() {
        return Utils.existFile(DRM_KCAL_BLUE);
    }

    public boolean supported() {
        return hasBrightnessMode() || hasLcdMinBrightness() || hasLcdMaxBrightness()
                || hasBackLightDimmerEnable() || hasMinBrightness() || hasBackLightDimmerThreshold()
                || hasBackLightDimmerOffset() || hasNegativeToggle() || hasRegisterHook()
                || hasMasterSequence() || hasGloveMode() || haskcalBlue()
		|| haskcalGreen() || haskcalRed();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
