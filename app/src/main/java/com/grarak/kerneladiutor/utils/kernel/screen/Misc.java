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

import java.util.HashMap;

/**
 * Created by willi on 23.06.16.
 */
public class Misc {

    private static final String LM3530_BRIGTHNESS_MODE = "/sys/devices/i2c-0/0-0038/lm3530_br_mode";
    private static final String LM3530_MIN_BRIGHTNESS = "/sys/devices/i2c-0/0-0038/lm3530_min_br";
    private static final String LM3530_MAX_BRIGHTNESS = "/sys/devices/i2c-0/0-0038/lm3530_max_br";

    private static final String LM3630_BACKLIGHT_DIMMER = "/sys/module/lm3630_bl/parameters/backlight_dimmer";
    private static final String LM3630_MIN_BRIGHTNESS = "/sys/module/lm3630_bl/parameters/min_brightness";
    private static final String MSM_BACKLIGHT_DIMMER = "/sys/module/msm_fb/parameters/backlight_dimmer";
    private static final String LM3630_BACKLIGHT_DIMMER_THRESHOLD = "/sys/module/lm3630_bl/parameters/backlight_threshold";
    private static final String LM3630_BACKLIGHT_DIMMER_OFFSET = "/sys/module/lm3630_bl/parameters/backlight_offset";

    private static final String NEGATIVE_TOGGLE = "/sys/module/cypress_touchkey/parameters/mdnie_shortcut_enabled";

    private static final String REGISTER_HOOK = "/sys/class/misc/mdnie/hook_intercept";
    private static final String MASTER_SEQUENCE = "/sys/class/misc/mdnie/sequence_intercept";

    private static final String GLOVE_MODE = "/sys/devices/virtual/touchscreen/touchscreen_dev/mode";

    private static final HashMap<String, Integer> sMinBrightnessFiles = new HashMap<>();

    static {
        sMinBrightnessFiles.put(LM3630_MIN_BRIGHTNESS, 50);
        sMinBrightnessFiles.put(MSM_BACKLIGHT_DIMMER, 100);
    }

    private static String MIN_BRIGHTNESS;

    public static void enableGloveMode(boolean enable, Context context) {
        run(Control.write(enable ? "glove" : "normal", GLOVE_MODE), GLOVE_MODE, context);
    }

    public static boolean isGloveModeEnabled() {
        return Utils.readFile(GLOVE_MODE).equals("glove");
    }

    public static boolean hasGloveMode() {
        return Utils.existFile(GLOVE_MODE);
    }

    public static void enableMasterSequence(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MASTER_SEQUENCE), MASTER_SEQUENCE, context);
    }

    public static boolean isMasterSequenceEnable() {
        return Utils.readFile(MASTER_SEQUENCE).equals("1");
    }

    public static boolean hasMasterSequence() {
        return Utils.existFile(MASTER_SEQUENCE);
    }

    public static void enableRegisterHook(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", REGISTER_HOOK), REGISTER_HOOK, context);
    }

    public static boolean isRegisterHookEnabled() {
        return Utils.readFile(REGISTER_HOOK).equals("1");
    }

    public static boolean hasRegisterHook() {
        return Utils.existFile(REGISTER_HOOK);
    }

    public static void enableNegativeToggle(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", NEGATIVE_TOGGLE), NEGATIVE_TOGGLE, context);
    }

    public static boolean isNegativeToggleEnabled() {
        return Utils.readFile(NEGATIVE_TOGGLE).equals("1");
    }

    public static boolean hasNegativeToggle() {
        return Utils.existFile(NEGATIVE_TOGGLE);
    }

    public static void setBackLightDimmerOffset(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3630_BACKLIGHT_DIMMER_OFFSET),
                LM3630_BACKLIGHT_DIMMER_OFFSET, context);
    }

    public static int getBackLightDimmerOffset() {
        return Utils.strToInt(Utils.readFile(LM3630_BACKLIGHT_DIMMER_OFFSET));
    }

    public static boolean hasBackLightDimmerOffset() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER_OFFSET);
    }

    public static void setBackLightDimmerThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3630_BACKLIGHT_DIMMER_THRESHOLD),
                LM3630_BACKLIGHT_DIMMER_THRESHOLD, context);
    }

    public static int getBackLightDimmerThreshold() {
        return Utils.strToInt(Utils.readFile(LM3630_BACKLIGHT_DIMMER_THRESHOLD));
    }

    public static boolean hasBackLightDimmerThreshold() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER_THRESHOLD);
    }

    public static void setMinBrightness(int value, Context context) {
        run(Control.write(String.valueOf(value), MIN_BRIGHTNESS), MIN_BRIGHTNESS, context);
    }

    public static int getMaxMinBrightness() {
        return sMinBrightnessFiles.get(MIN_BRIGHTNESS);
    }

    public static int getCurMinBrightness() {
        return Utils.strToInt(Utils.readFile(MIN_BRIGHTNESS));
    }

    public static boolean hasMinBrightness() {
        if (MIN_BRIGHTNESS == null) {
            for (String file : sMinBrightnessFiles.keySet()) {
                if (Utils.existFile(file)) {
                    MIN_BRIGHTNESS = file;
                    return true;
                }
            }
        }
        return MIN_BRIGHTNESS != null;
    }

    public static void enableBackLightDimmer(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", LM3630_BACKLIGHT_DIMMER), LM3630_BACKLIGHT_DIMMER, context);
    }

    public static boolean isBackLightDimmerEnabled() {
        return Utils.readFile(LM3630_BACKLIGHT_DIMMER).equals("Y");
    }

    public static boolean hasBackLightDimmerEnable() {
        return Utils.existFile(LM3630_BACKLIGHT_DIMMER);
    }

    public static void setLcdMaxBrightness(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3530_MAX_BRIGHTNESS), LM3530_MAX_BRIGHTNESS, context);
    }

    public static int getLcdMaxBrightness() {
        return Utils.strToInt(Utils.readFile(LM3530_MAX_BRIGHTNESS));
    }

    public static boolean hasLcdMaxBrightness() {
        return Utils.existFile(LM3530_MAX_BRIGHTNESS);
    }

    public static void setLcdMinBrightness(int value, Context context) {
        run(Control.write(String.valueOf(value), LM3530_MIN_BRIGHTNESS), LM3530_MIN_BRIGHTNESS, context);
    }

    public static int getLcdMinBrightness() {
        return Utils.strToInt(Utils.readFile(LM3530_MIN_BRIGHTNESS));
    }

    public static boolean hasLcdMinBrightness() {
        return Utils.existFile(LM3530_MIN_BRIGHTNESS);
    }

    public static void enableBrightnessMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LM3530_BRIGTHNESS_MODE), LM3530_BRIGTHNESS_MODE, context);
    }

    public static boolean isBrightnessModeEnabled() {
        return Utils.readFile(LM3530_BRIGTHNESS_MODE).equals("1");
    }

    public static boolean hasBrightnessMode() {
        return Utils.existFile(LM3530_BRIGTHNESS_MODE);
    }

    public static boolean supported() {
        return hasBrightnessMode() || hasLcdMinBrightness() || hasLcdMaxBrightness()
                || hasBackLightDimmerEnable() || hasMinBrightness() || hasBackLightDimmerThreshold()
                || hasBackLightDimmerOffset() || hasNegativeToggle() || hasRegisterHook()
                || hasMasterSequence() || hasGloveMode();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
