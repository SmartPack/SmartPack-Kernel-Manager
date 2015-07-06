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
import java.util.List;

/**
 * Created by willi on 03.05.15.
 */
public class Thermal implements Constants {

    private static String THERMAL_FILE;
    private static String CORE_CONTROL_ENABLE_FILE;

    private static String TEMP_LIMIT_FILE;

    public static void setShutdownTemp(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_SHUTDOWN_TEMP, Control.CommandType.GENERIC, context);
    }

    public static int getShutdownTemp() {
        return Utils.stringToInt(Utils.readFile(CONF_SHUTDOWN_TEMP));
    }

    public static boolean hasShutdownTemp() {
        return Utils.existFile(CONF_SHUTDOWN_TEMP);
    }

    public static void setCheckIntervalMs(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_CHECK_INTERVAL_MS, Control.CommandType.GENERIC, context);
    }

    public static int getCheckIntervalMs() {
        return Utils.stringToInt(Utils.readFile(CONF_CHECK_INTERVAL_MS));
    }

    public static boolean hasCheckIntervalMs() {
        return Utils.existFile(CONF_CHECK_INTERVAL_MS);
    }

    public static void setAllowedMaxFreq(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_MAX_FREQ, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedMaxFreq() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_MAX_FREQ));
    }

    public static boolean hasAllowedMaxFreq() {
        return Utils.existFile(CONF_ALLOWED_MAX_FREQ);
    }

    public static void setAllowedMaxHigh(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_MAX_HIGH, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedMaxHigh() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_MAX_HIGH));
    }

    public static boolean hasAllowedMaxHigh() {
        return Utils.existFile(CONF_ALLOWED_MAX_HIGH);
    }

    public static void setAllowedMaxLow(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_MAX_LOW, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedMaxLow() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_MAX_LOW));
    }

    public static boolean hasAllowedMaxLow() {
        return Utils.existFile(CONF_ALLOWED_MAX_LOW);
    }

    public static void setAllowedMidFreq(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_MID_FREQ, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedMidFreq() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_MID_FREQ));
    }

    public static boolean hasAllowedMidFreq() {
        return Utils.existFile(CONF_ALLOWED_MID_FREQ);
    }

    public static void setAllowedMidHigh(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_MID_HIGH, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedMidHigh() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_MID_HIGH));
    }

    public static boolean hasAllowedMidHigh() {
        return Utils.existFile(CONF_ALLOWED_MID_HIGH);
    }

    public static void setAllowedMidLow(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_MID_LOW, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedMidLow() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_MID_LOW));
    }

    public static boolean hasAllowedMidLow() {
        return Utils.existFile(CONF_ALLOWED_MID_LOW);
    }

    public static void setAllowedLowFreq(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_LOW_FREQ, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedLowFreq() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_LOW_FREQ));
    }

    public static boolean hasAllowedLowFreq() {
        return Utils.existFile(CONF_ALLOWED_LOW_FREQ);
    }

    public static void setAllowedLowHigh(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_LOW_HIGH, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedLowHigh() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_LOW_HIGH));
    }

    public static boolean hasAllowedLowHigh() {
        return Utils.existFile(CONF_ALLOWED_LOW_HIGH);
    }

    public static void setAllowedLowLow(int value, Context context) {
        Control.runCommand(String.valueOf(value), CONF_ALLOWED_LOW_LOW, Control.CommandType.GENERIC, context);
    }

    public static int getAllowedLowLow() {
        return Utils.stringToInt(Utils.readFile(CONF_ALLOWED_LOW_LOW));
    }

    public static boolean hasAllowedLowLow() {
        return Utils.existFile(CONF_ALLOWED_LOW_LOW);
    }

    public static boolean hasMsmThermal() {
        return Utils.existFile(MSM_THERMAL_CONF);
    }

    public static void setMinFreqIndex(int value, Context context) {
        Control.runCommand(String.valueOf(value), MSM_THERMAL_MIN_FREQ_INDEX, Control.CommandType.GENERIC, context);
    }

    public static int getMinFreqIndex() {
        return Utils.stringToInt(Utils.readFile(MSM_THERMAL_MIN_FREQ_INDEX));
    }

    public static boolean hasMinFreqIndex() {
        return Utils.existFile(MSM_THERMAL_MIN_FREQ_INDEX);
    }

    public static void activateFreqLimitDebug(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", MSM_THERMAL_FREQ_LIMIT_DEBUG, Control.CommandType.GENERIC, context);
    }

    public static boolean isFreqLimitDebugActive() {
        return Utils.readFile(MSM_THERMAL_FREQ_LIMIT_DEBUG).equals("1");
    }

    public static boolean hasFreqLimitDebug() {
        return Utils.existFile(MSM_THERMAL_FREQ_LIMIT_DEBUG);
    }

    public static void setTempLimit(int value, Context context) {
        if (TEMP_LIMIT_FILE.equals(TEMPCONTROL_TEMP_LIMIT))
            value *= 1000;

        Control.runCommand(String.valueOf(value), TEMP_LIMIT_FILE, Control.CommandType.GENERIC, context);
    }

    public static int getTempLimitMax() {
        if (TEMP_LIMIT_FILE.equals(TEMPCONTROL_TEMP_LIMIT)) return 80;
        return 100;
    }

    public static int getTempLimitMin() {
        if (TEMP_LIMIT_FILE.equals(TEMPCONTROL_TEMP_LIMIT)) return 60;
        return 50;
    }

    public static List<String> getTempLimitList() {
        List<String> list = new ArrayList<>();
        for (double i = getTempLimitMin(); i <= getTempLimitMax(); i++)
            list.add(Utils.formatCelsius(i) + " " + Utils.celsiusToFahrenheit(i));
        return list;
    }

    public static int getCurTempLimit() {
        if (TEMP_LIMIT_FILE != null) {
            int value = Utils.stringToInt(Utils.readFile(TEMP_LIMIT_FILE));
            if (TEMP_LIMIT_FILE.equals(TEMPCONTROL_TEMP_LIMIT))
                value /= 1000;

            return value;
        }
        return 0;
    }

    public static boolean hasTempLimit() {
        if (TEMP_LIMIT_FILE == null)
            for (String file : TEMP_LIMIT_ARRAY)
                if (Utils.existFile(file)) {
                    TEMP_LIMIT_FILE = file;
                    return true;
                }
        return TEMP_LIMIT_FILE != null;
    }

    public static void activateTempThrottle(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", MSM_THERMAL_TEMP_THROTTLE, Control.CommandType.GENERIC, context);
    }

    public static boolean isTempThrottleActive() {
        return Utils.readFile(MSM_THERMAL_TEMP_THROTTLE).equals("Y");
    }

    public static boolean hasTempThrottleEnable() {
        return Utils.existFile(MSM_THERMAL_TEMP_THROTTLE) && !Utils.existFile("/sys/module/msm_thermal/parameters/core_limit_temp_degC");
    }

    public static void activateTempSafety(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", getThermalFile(PARAMETERS_TEMP_SAFETY), Control.CommandType.GENERIC, context);
    }

    public static boolean isTempSafetyActive() {
        return Utils.readFile(getThermalFile(PARAMETERS_TEMP_SAFETY)).equals("1");
    }

    public static boolean hasTempSafety() {
        return Utils.existFile(getThermalFile(PARAMETERS_TEMP_SAFETY));
    }

    public static void setThermalLimitHigh(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_THERMAL_LIMIT_HIGH), Control.CommandType.GENERIC, context);
    }

    public static int getThermalLimitHigh() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_THERMAL_LIMIT_HIGH)));
    }

    public static boolean hasThermalLimitHigh() {
        return Utils.existFile(getThermalFile(PARAMETERS_THERMAL_LIMIT_HIGH));
    }

    public static void setThermalLimitLow(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_THERMAL_LIMIT_LOW), Control.CommandType.GENERIC, context);
    }

    public static int getThermalLimitLow() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_THERMAL_LIMIT_LOW)));
    }

    public static boolean hasThermalLimitLow() {
        return Utils.existFile(getThermalFile(PARAMETERS_THERMAL_LIMIT_LOW));
    }

    public static void setTempHysteresisDegC(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_TEMP_HYSTERESIS_DEGC), Control.CommandType.GENERIC, context);
    }

    public static int getTempHysteresisDegC() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_TEMP_HYSTERESIS_DEGC)));
    }

    public static boolean hasTempHysteresisDegC() {
        return Utils.existFile(getThermalFile(PARAMETERS_TEMP_HYSTERESIS_DEGC));
    }

    public static void setPollMs(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_POLL_MS), Control.CommandType.GENERIC, context);
    }

    public static int getPollMs() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_POLL_MS)));
    }

    public static boolean hasPollMs() {
        return Utils.existFile(getThermalFile(PARAMETERS_POLL_MS));
    }

    public static void activateImmediatelyLimitStop(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", getThermalFile(PARAMETERS_IMMEDIATELY_LIMIT_STOP), Control.CommandType.GENERIC, context);
    }

    public static boolean isImmediatelyLimitStopActive() {
        return Utils.readFile(getThermalFile(PARAMETERS_IMMEDIATELY_LIMIT_STOP)).equals("Y");
    }

    public static boolean hasImmediatelyLimitStop() {
        return Utils.existFile(getThermalFile(PARAMETERS_IMMEDIATELY_LIMIT_STOP));
    }

    public static void setFreqStep(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_FREQ_STEP), Control.CommandType.GENERIC, context);
    }

    public static int getFreqStep() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_FREQ_STEP)));
    }

    public static boolean hasFreqStep() {
        return Utils.existFile(getThermalFile(PARAMETERS_FREQ_STEP));
    }

    public static void setCoreTempHysteresisDegC(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC), Control.CommandType.GENERIC, context);
    }

    public static int getCoreTempHysteresisDegC() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC)));
    }

    public static boolean hasCoreTempHysteresisDegC() {
        return Utils.existFile(getThermalFile(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC));
    }

    public static void setCoreLimitTempDegC(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_CORE_LIMIT_TEMP_DEGC), Control.CommandType.GENERIC, context);
    }

    public static int getCoreLimitTempDegC() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_CORE_LIMIT_TEMP_DEGC)));
    }

    public static boolean hasCoreLimitTempDegC() {
        return Utils.existFile(getThermalFile(PARAMETERS_CORE_LIMIT_TEMP_DEGC));
    }

    public static void setLimitTempDegC(int value, Context context) {
        Control.runCommand(String.valueOf(value), getThermalFile(PARAMETERS_LIMIT_TEMP_DEGC), Control.CommandType.GENERIC, context);
    }

    public static int getLimitTempDegC() {
        return Utils.stringToInt(Utils.readFile(getThermalFile(PARAMETERS_LIMIT_TEMP_DEGC)));
    }

    public static boolean hasLimitTempDegC() {
        return Utils.existFile(getThermalFile(PARAMETERS_LIMIT_TEMP_DEGC));
    }

    public static void activateVddRestriction(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", getThermalFile(VDD_RESTRICTION_ENABLED), Control.CommandType.GENERIC, context);
    }

    public static boolean isVddRestrictionActive() {
        return Utils.readFile(getThermalFile(VDD_RESTRICTION_ENABLED)).equals("1");
    }

    public static boolean hasVddRestrictionEnable() {
        return Utils.existFile(getThermalFile(VDD_RESTRICTION_ENABLED));
    }

    public static void activateCoreControl(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", getThermalFile(CORE_CONTROL_ENABLE_FILE), Control.CommandType.GENERIC, context);
    }

    public static boolean isCoreControlActive() {
        return Utils.readFile(getThermalFile(CORE_CONTROL_ENABLE_FILE)).equals("1");
    }

    public static boolean hasCoreControlEnable() {
        if (Utils.existFile(getThermalFile(CORE_CONTROL_ENABLED)))
            CORE_CONTROL_ENABLE_FILE = CORE_CONTROL_ENABLED;
        else if (Utils.existFile(getThermalFile(CORE_CONTROL_ENABLED_2)))
            CORE_CONTROL_ENABLE_FILE = CORE_CONTROL_ENABLED_2;
        return CORE_CONTROL_ENABLE_FILE != null;
    }

    public static void activateThermalDebugMode(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", getThermalFile(PARAMETERS_THERMAL_DEBUG_MODE), Control.CommandType.GENERIC, context);
    }

    public static boolean isThermalDebugModeActive() {
        return Utils.readFile(getThermalFile(PARAMETERS_THERMAL_DEBUG_MODE)).equals("1");
    }

    public static boolean hasThermalDebugMode() {
        return Utils.existFile(getThermalFile(PARAMETERS_THERMAL_DEBUG_MODE));
    }

    public static void activateIntelliThermalOptimized(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", getThermalFile(PARAMETERS_INTELLI_ENABLED), Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliThermalOptimizedActive() {
        return Utils.readFile(getThermalFile(PARAMETERS_INTELLI_ENABLED)).equals("Y");
    }

    public static boolean hasIntelliThermalOptimizedEnable() {
        return Utils.existFile(getThermalFile(PARAMETERS_INTELLI_ENABLED));
    }

    public static void activateIntelliThermal(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", getThermalFile(PARAMETERS_ENABLED), Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliThermalActive() {
        return Utils.readFile(getThermalFile(PARAMETERS_ENABLED)).equals("Y");
    }

    public static boolean hasIntelliThermalEnable() {
        return Utils.existFile(getThermalFile(PARAMETERS_ENABLED)) && hasCoreLimitTempDegC();
    }

    public static boolean hasThermalSettings() {
        if (THERMAL_FILE == null) {
            if (Utils.existFile(MSM_THERMAL)) THERMAL_FILE = MSM_THERMAL;
            else if (Utils.existFile(MSM_THERMAL_V2)) THERMAL_FILE = MSM_THERMAL_V2;
        }
        return THERMAL_FILE != null;
    }

    private static String getThermalFile(String file) {
        return THERMAL_FILE + "/" + file;
    }

    public static void activateThermald(boolean active, Context context) {
        if (active) Control.startService(THERMALD, true, context);
        else Control.stopService(THERMALD, true, context);
    }

    public static boolean isThermaldActive() {
        return Utils.isPropActive(THERMALD);
    }

    public static boolean hasThermald() {
        return Utils.hasProp(THERMALD);
    }

    public static boolean hasThermal() {
        if (hasThermald()) return true;
        for (String[] arrays : THERMAL_ARRAYS)
            for (String file : arrays) if (Utils.existFile(file)) return true;
        return false;
    }

}
