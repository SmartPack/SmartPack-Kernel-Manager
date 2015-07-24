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
import com.grarak.kerneladiutor.utils.GammaProfiles;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class Screen implements Constants {

    private static String SCREEN_CALIBRATION;
    private static String SCREEN_CALIBRATION_CTRL;

    private static GammaProfiles GAMMA_PROFILES;

    private static String MIN_BRIGHTNESS;

    public static void activateGloveMode(boolean active, Context context) {
        Control.runCommand(active ? "glove" : "normal", GLOVE_MODE, Control.CommandType.GENERIC, context);
    }

    public static boolean isGloveModeActive() {
        return Utils.readFile(GLOVE_MODE).equals("glove");
    }

    public static boolean hasGloveMode() {
        return Utils.existFile(GLOVE_MODE);
    }

    public static void activateMasterSequence(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", MASTER_SEQUENCE, Control.CommandType.GENERIC, context);
    }

    public static boolean isMasterSequenceActive() {
        return Utils.readFile(MASTER_SEQUENCE).equals("1");
    }

    public static boolean hasMasterSequence() {
        return Utils.existFile(MASTER_SEQUENCE);
    }

    public static void activateRegisterHook(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", REGISTER_HOOK, Control.CommandType.GENERIC, context);
    }

    public static boolean isRegisterHookActive() {
        return Utils.readFile(REGISTER_HOOK).equals("1");
    }

    public static boolean hasRegisterHook() {
        return Utils.existFile(REGISTER_HOOK);
    }

    public static void activateNegativeToggle(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", NEGATIVE_TOGGLE, Control.CommandType.GENERIC, context);
    }

    public static boolean isNegativeToggleActive() {
        return Utils.readFile(NEGATIVE_TOGGLE).equals("1");
    }

    public static boolean hasNegativeToggle() {
        return Utils.existFile(NEGATIVE_TOGGLE);
    }

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
                    return true;
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

    public static void setLcdMaxBrightness(int value, Context context) {
        Control.runCommand(String.valueOf(value), LM3530_MAX_BRIGHTNESS, Control.CommandType.GENERIC, context);
    }

    public static int getLcdMaxBrightness() {
        return Utils.stringToInt(Utils.readFile(LM3530_MAX_BRIGHTNESS));
    }

    public static boolean hasLcdMaxBrightness() {
        return Utils.existFile(LM3530_MAX_BRIGHTNESS);
    }

    public static void setLcdMinBrightness(int value, Context context) {
        Control.runCommand(String.valueOf(value), LM3530_MIN_BRIGHTNESS, Control.CommandType.GENERIC, context);
    }

    public static int getLcdMinBrightness() {
        return Utils.stringToInt(Utils.readFile(LM3530_MIN_BRIGHTNESS));
    }

    public static boolean hasLcdMinBrightness() {
        return Utils.existFile(LM3530_MIN_BRIGHTNESS);
    }

    public static void activateBrightnessMode(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", LM3530_BRIGTHNESS_MODE, Control.CommandType.GENERIC, context);
    }

    public static boolean isBrightnessModeActive() {
        return Utils.readFile(LM3530_BRIGTHNESS_MODE).equals("1");
    }

    public static boolean hasBrightnessMode() {
        return Utils.existFile(LM3530_BRIGTHNESS_MODE);
    }

    public static void setDsiPanelProfile(int profile, GammaProfiles.DsiPanelProfiles dsiPanelProfiles, Context context) {
        setBlueNegative(dsiPanelProfiles.getBlueNegative(profile), context);
        setBluePositive(dsiPanelProfiles.getBluePositive(profile), context);
        setGreenNegative(dsiPanelProfiles.getGreenNegative(profile), context);
        setGreenPositive(dsiPanelProfiles.getGreenPositive(profile), context);
        setRedNegative(dsiPanelProfiles.getRedNegative(profile), context);
        setRedPositive(dsiPanelProfiles.getRedPositive(profile), context);
        setWhitePoint(dsiPanelProfiles.getWhitePoint(profile), context);
    }

    public static void setWhitePoint(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_W, Control.CommandType.GENERIC, context);
    }

    public static void setBlueNegative(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_BN, Control.CommandType.GENERIC, context);
    }

    public static void setBluePositive(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_BP, Control.CommandType.GENERIC, context);
    }

    public static void setGreenNegative(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_GN, Control.CommandType.GENERIC, context);
    }

    public static void setGreenPositive(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_GP, Control.CommandType.GENERIC, context);
    }

    public static void setRedNegative(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_RN, Control.CommandType.GENERIC, context);
    }

    public static void setRedPositive(String value, Context context) {
        Control.runCommand(value, DSI_PANEL_RP, Control.CommandType.GENERIC, context);
    }

    public static GammaProfiles.DsiPanelProfiles getDsiPanelProfiles(Context context) {
        if (GAMMA_PROFILES == null)
            GAMMA_PROFILES = new GammaProfiles(Utils.readAssetFile(context, "gamma_profiles.json"));
        return GAMMA_PROFILES.getDsiPanelProfiles();
    }

    public static String getWhitePoint() {
        return Utils.readFile(DSI_PANEL_W);
    }

    public static String getBlueNegative() {
        return Utils.readFile(DSI_PANEL_BN);
    }

    public static String getBluePositive() {
        return Utils.readFile(DSI_PANEL_BP);
    }

    public static String getGreenNegative() {
        return Utils.readFile(DSI_PANEL_GN);
    }

    public static String getGreenPositive() {
        return Utils.readFile(DSI_PANEL_GP);
    }

    public static String getRedNegative() {
        return Utils.readFile(DSI_PANEL_RN);
    }

    public static String getRedPositive() {
        return Utils.readFile(DSI_PANEL_RP);
    }

    public static boolean hasDsiPanel() {
        for (String file : DSI_PANEL_ARRAY) if (Utils.existFile(file)) return true;
        return false;
    }

    public static void setGammaControlProfile(int profile, GammaProfiles.GammaControlProfiles gammaControlProfiles,
                                              Context context) {
        setColorCalibration(gammaControlProfiles.getKCAL(profile), context);
        setRedGreys(gammaControlProfiles.getRedGreys(profile), context);
        setRedMids(gammaControlProfiles.getRedMids(profile), context);
        setRedBlacks(gammaControlProfiles.getRedBlacks(profile), context);
        setRedWhites(gammaControlProfiles.getRedWhites(profile), context);
        setGreenGreys(gammaControlProfiles.getGreenGreys(profile), context);
        setGreenMids(gammaControlProfiles.getGreenMids(profile), context);
        setGreenBlacks(gammaControlProfiles.getGreenBlacks(profile), context);
        setGreenWhites(gammaControlProfiles.getGreenWhites(profile), context);
        setBlueGreys(gammaControlProfiles.getBlueGreys(profile), context);
        setBlueMids(gammaControlProfiles.getBlueMids(profile), context);
        setBlueBlacks(gammaControlProfiles.getBlueBlacks(profile), context);
        setBlueWhites(gammaControlProfiles.getBlueWhites(profile), context);
        setGammaContrast(gammaControlProfiles.getContrast(profile), context);
        setGammaBrightness(gammaControlProfiles.getBrightness(profile), context);
        setGammaSaturation(gammaControlProfiles.getSaturation(profile), context);
    }

    public static void setGammaSaturation(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_SATURATION, Control.CommandType.GENERIC, context);
    }

    public static void setGammaBrightness(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_BRIGHTNESS, Control.CommandType.GENERIC, context);
    }

    public static void setGammaContrast(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_CONTRAST, Control.CommandType.GENERIC, context);
    }

    public static void setBlueWhites(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_BLUE_WHITES, Control.CommandType.GENERIC, context);
    }

    public static void setBlueBlacks(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_BLUE_BLACKS, Control.CommandType.GENERIC, context);
    }

    public static void setBlueMids(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_BLUE_MIDS, Control.CommandType.GENERIC, context);
    }

    public static void setBlueGreys(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_BLUE_GREYS, Control.CommandType.GENERIC, context);
    }

    public static void setGreenWhites(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_GREEN_WHITES, Control.CommandType.GENERIC, context);
    }

    public static void setGreenBlacks(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_GREEN_BLACKS, Control.CommandType.GENERIC, context);
    }

    public static void setGreenMids(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_GREEN_MIDS, Control.CommandType.GENERIC, context);
    }

    public static void setGreenGreys(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_GREEN_GREYS, Control.CommandType.GENERIC, context);
    }

    public static void setRedWhites(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_RED_WHITES, Control.CommandType.GENERIC, context);
    }

    public static void setRedBlacks(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_RED_BLACKS, Control.CommandType.GENERIC, context);
    }

    public static void setRedMids(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_RED_MIDS, Control.CommandType.GENERIC, context);
    }

    public static void setRedGreys(String value, Context context) {
        Control.runCommand(value, GAMMACONTROL_RED_GREYS, Control.CommandType.GENERIC, context);
    }

    public static GammaProfiles.GammaControlProfiles getGammaControlProfiles(Context context) {
        if (GAMMA_PROFILES == null)
            GAMMA_PROFILES = new GammaProfiles(Utils.readAssetFile(context, "gamma_profiles.json"));
        return GAMMA_PROFILES.getGammaControl();
    }

    public static String getGammaSaturation() {
        return Utils.readFile(GAMMACONTROL_SATURATION);
    }

    public static String getGammaBrightness() {
        return Utils.readFile(GAMMACONTROL_BRIGHTNESS);
    }

    public static String getGammaContrast() {
        return Utils.readFile(GAMMACONTROL_CONTRAST);
    }

    public static String getBlueWhites() {
        return Utils.readFile(GAMMACONTROL_BLUE_WHITES);
    }

    public static String getBlueBlacks() {
        return Utils.readFile(GAMMACONTROL_BLUE_BLACKS);
    }

    public static String getBlueMids() {
        return Utils.readFile(GAMMACONTROL_BLUE_MIDS);
    }

    public static String getBlueGreys() {
        return Utils.readFile(GAMMACONTROL_BLUE_GREYS);
    }

    public static String getGreenWhites() {
        return Utils.readFile(GAMMACONTROL_GREEN_WHITES);
    }

    public static String getGreenBlacks() {
        return Utils.readFile(GAMMACONTROL_GREEN_BLACKS);
    }

    public static String getGreenMids() {
        return Utils.readFile(GAMMACONTROL_GREEN_MIDS);
    }

    public static String getGreenGreys() {
        return Utils.readFile(GAMMACONTROL_GREEN_GREYS);
    }

    public static String getRedWhites() {
        return Utils.readFile(GAMMACONTROL_RED_WHITES);
    }

    public static String getRedBlacks() {
        return Utils.readFile(GAMMACONTROL_RED_BLACKS);
    }

    public static String getRedMids() {
        return Utils.readFile(GAMMACONTROL_RED_MIDS);
    }

    public static String getRedGreys() {
        return Utils.readFile(GAMMACONTROL_RED_GREYS);
    }

    public static boolean hasGammaControl() {
        for (String file : GAMMACONTROL_ARRAY) if (Utils.existFile(file)) return true;
        return false;
    }

    public static void setKGammaProfile(int profile, GammaProfiles.KGammaProfiles kGammaProfiles, Context context) {
        if (kGammaProfiles == null) return;
        setColorCalibration(kGammaProfiles.getKCAL(profile), context);
        setKGammaBlue(kGammaProfiles.getGammaBlue(profile), context);
        setKGammaGreen(kGammaProfiles.getGammaGreen(profile), context);
        setKGammaRed(kGammaProfiles.getGammaRed(profile), context);
    }

    public static void setKGammaBlue(String value, Context context) {
        if (Utils.existFile(K_GAMMA_BLUE))
            Control.runCommand(value, K_GAMMA_BLUE, Control.CommandType.GENERIC, context);
        else
            Control.runCommand(value, K_GAMMA_B, Control.CommandType.GENERIC, context);
    }

    public static void setKGammaGreen(String value, Context context) {
        if (Utils.existFile(K_GAMMA_GREEN))
            Control.runCommand(value, K_GAMMA_GREEN, Control.CommandType.GENERIC, context);
        else
            Control.runCommand(value, K_GAMMA_G, Control.CommandType.GENERIC, context);
    }

    public static void setKGammaRed(String value, Context context) {
        if (Utils.existFile(K_GAMMA_RED))
            Control.runCommand(value, K_GAMMA_RED, Control.CommandType.GENERIC, context);
        else
            Control.runCommand(value, K_GAMMA_R, Control.CommandType.GENERIC, context);
    }

    public static GammaProfiles.KGammaProfiles getKGammaProfiles(Context context) {
        if (GAMMA_PROFILES == null)
            GAMMA_PROFILES = new GammaProfiles(Utils.readAssetFile(context, "gamma_profiles.json"));
        return GAMMA_PROFILES.getKGamma();
    }

    public static String getKGammaBlue() {
        if (Utils.existFile(K_GAMMA_BLUE)) return Utils.readFile(K_GAMMA_BLUE);
        return Utils.readFile(K_GAMMA_B);
    }

    public static String getKGammaGreen() {
        if (Utils.existFile(K_GAMMA_GREEN)) return Utils.readFile(K_GAMMA_GREEN);
        return Utils.readFile(K_GAMMA_G);
    }

    public static String getKGammaRed() {
        if (Utils.existFile(K_GAMMA_RED)) return Utils.readFile(K_GAMMA_RED);
        return Utils.readFile(K_GAMMA_R);
    }

    public static boolean hasKGamma() {
        for (String file : K_GAMMA_ARRAY) if (Utils.existFile(file)) return true;
        return false;
    }

    public static void activateScreenHBM(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", SCREEN_HBM, Control.CommandType.GENERIC, context);
    }

    public static boolean isScreenHBMActive() {
        return Utils.readFile(SCREEN_HBM).equals("1");
    }

    public static boolean hasScreenHBM() {
        return Utils.existFile(SCREEN_HBM);
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
        if (SCREEN_CALIBRATION == null) return;

        if (hasColorCalibrationCtrl() && SCREEN_CALIBRATION_CTRL.equals(SCREEN_COLOR_CONTROL_CTRL))
            Control.runCommand("0", SCREEN_CALIBRATION_CTRL, Control.CommandType.GENERIC, context);

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

        if (hasColorCalibrationCtrl() && !SCREEN_CALIBRATION_CTRL.equals(SCREEN_COLOR_CONTROL_CTRL))
            Control.runCommand("1", SCREEN_CALIBRATION_CTRL, Control.CommandType.GENERIC, context);
    }

    public static List<String> getColorCalibrationLimits() {
        List<String> list = new ArrayList<>();
        switch (SCREEN_CALIBRATION) {
            case SCREEN_SAMOLED_COLOR_RED:
            case SCREEN_COLOR_CONTROL:
                for (int i = 60; i < 401; i++)
                    list.add(String.valueOf(i));
                break;
            case SCREEN_FB0_RGB:
                for (int i = 255; i < 32769; i++)
                    list.add(String.valueOf(i));
                break;
            default:
                int max = 255;
                for (String file : SCREEN_KCAL_CTRL_NEW_ARRAY)
                    if (Utils.existFile(file)) {
                        max = 256;
                        break;
                    }
                for (int i = 0; i < max + 1; i++)
                    list.add(String.valueOf(i));
                break;
        }
        return list;
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
            for (String file : SCREEN_RGB_CTRL_ARRAY)
                if (Utils.existFile(file)) {
                    SCREEN_CALIBRATION_CTRL = file;
                    return true;
                }
        return SCREEN_CALIBRATION_CTRL != null;
    }

    public static boolean hasColorCalibration() {
        if (SCREEN_CALIBRATION == null) for (String file : SCREEN_RGB_ARRAY)
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
