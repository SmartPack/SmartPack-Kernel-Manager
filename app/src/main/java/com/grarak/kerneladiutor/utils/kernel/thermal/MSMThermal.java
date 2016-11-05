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
package com.grarak.kerneladiutor.utils.kernel.thermal;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 04.05.16.
 */
public class MSMThermal {

    private static final String MSM_THERMAL = "/sys/module/msm_thermal";
    private static final String MSM_THERMAL_V2 = "/sys/module/msm_thermal_v2";
    private static final String PARAMETERS_ENABLED = "parameters/enabled";
    private static final String PARAMETERS_INTELLI_ENABLED = "parameters/intelli_enabled";
    private static final String PARAMETERS_THERMAL_DEBUG_MODE = "parameters/thermal_debug_mode";
    private static final String CORE_CONTROL_ENABLED = "core_control/enabled";
    private static final String CORE_CONTROL_ENABLED_2 = "core_control/core_control";
    private static final String VDD_RESTRICTION_ENABLED = "vdd_restriction/enabled";
    private static final String PARAMETERS_LIMIT_TEMP_DEGC = "parameters/limit_temp_degC";
    private static final String PARAMETERS_CORE_LIMIT_TEMP_DEGC = "parameters/core_limit_temp_degC";
    private static final String PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC = "parameters/core_temp_hysteresis_degC";
    private static final String PARAMETERS_FREQ_STEP = "parameters/freq_step";
    private static final String PARAMETERS_IMMEDIATELY_LIMIT_STOP = "parameters/immediately_limit_stop";
    private static final String PARAMETERS_POLL_MS = "parameters/poll_ms";
    private static final String PARAMETERS_TEMP_HYSTERESIS_DEGC = "parameters/temp_hysteresis_degC";
    private static final String PARAMETERS_THERMAL_LIMIT_LOW = "parameters/thermal_limit_low";
    private static final String PARAMETERS_THERMAL_LIMIT_HIGH = "parameters/thermal_limit_high";
    private static final String PARAMETERS_TEMP_SAFETY = "parameters/temp_safety";
    private static final String MSM_THERMAL_TEMP_THROTTLE = MSM_THERMAL + "/" + PARAMETERS_ENABLED;

    private static final String MSM_THERMAL_THROTTLE_TEMP = MSM_THERMAL + "/parameters/throttle_temp";
    private static final String MSM_THERMAL_TEMP_MAX = MSM_THERMAL + "/parameters/temp_max";
    private static final String MSM_THERMAL_TEMP_THRESHOLD = MSM_THERMAL + "/parameters/temp_threshold";
    private static final String MSM_THERMAL_FREQ_LIMIT_DEBUG = MSM_THERMAL + "/parameters/freq_limit_debug";
    private static final String MSM_THERMAL_MIN_FREQ_INDEX = MSM_THERMAL + "/parameters/min_freq_index";
    private static final String TEMPCONTROL_TEMP_LIMIT = "/sys/class/misc/tempcontrol/templimit";

    private static final String MSM_THERMAL_CONF = "/sys/kernel/msm_thermal/conf";
    private static final String CONF_ALLOWED_LOW_LOW = MSM_THERMAL_CONF + "/allowed_low_low";
    private static final String CONF_ALLOWED_LOW_HIGH = MSM_THERMAL_CONF + "/allowed_low_high";
    private static final String CONF_ALLOWED_LOW_FREQ = MSM_THERMAL_CONF + "/allowed_low_freq";
    private static final String CONF_ALLOWED_MID_LOW = MSM_THERMAL_CONF + "/allowed_mid_low";
    private static final String CONF_ALLOWED_MID_HIGH = MSM_THERMAL_CONF + "/allowed_mid_high";
    private static final String CONF_ALLOWED_MID_FREQ = MSM_THERMAL_CONF + "/allowed_mid_freq";
    private static final String CONF_ALLOWED_MAX_LOW = MSM_THERMAL_CONF + "/allowed_max_low";
    private static final String CONF_ALLOWED_MAX_HIGH = MSM_THERMAL_CONF + "/allowed_max_high";
    private static final String CONF_ALLOWED_MAX_FREQ = MSM_THERMAL_CONF + "/allowed_max_freq";
    private static final String CONF_CHECK_INTERVAL_MS = MSM_THERMAL_CONF + "/check_interval_ms";
    private static final String CONF_SHUTDOWN_TEMP = MSM_THERMAL_CONF + "/shutdown_temp";

    private static final HashMap<String, Integer> sTempLimitOffset = new HashMap<>();
    private static final HashMap<String, Integer> sTempLimitMin = new HashMap<>();
    private static final HashMap<String, Integer> sTempLimitMax = new HashMap<>();

    static {
        sTempLimitOffset.put(MSM_THERMAL_THROTTLE_TEMP, 1);
        sTempLimitOffset.put(MSM_THERMAL_TEMP_MAX, 1);
        sTempLimitOffset.put(MSM_THERMAL_TEMP_THRESHOLD, 1);
        sTempLimitOffset.put(TEMPCONTROL_TEMP_LIMIT, 1000);

        sTempLimitMin.put(MSM_THERMAL_THROTTLE_TEMP, 50);
        sTempLimitMin.put(MSM_THERMAL_TEMP_MAX, 50);
        sTempLimitMin.put(MSM_THERMAL_TEMP_THRESHOLD, 50);
        sTempLimitMin.put(TEMPCONTROL_TEMP_LIMIT, 60);

        sTempLimitMax.put(MSM_THERMAL_THROTTLE_TEMP, 100);
        sTempLimitMax.put(MSM_THERMAL_TEMP_MAX, 100);
        sTempLimitMax.put(MSM_THERMAL_TEMP_THRESHOLD, 100);
        sTempLimitMax.put(TEMPCONTROL_TEMP_LIMIT, 80);
    }

    private static String PARENT;
    private static String CORE_CONTROL;
    private static String TEMP_LIMIT;

    public static void setShutdownTemp(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_SHUTDOWN_TEMP), CONF_SHUTDOWN_TEMP, context);
    }

    public static int getShutdownTemp() {
        return Utils.strToInt(Utils.readFile(CONF_SHUTDOWN_TEMP));
    }

    public static boolean hasShutdownTemp() {
        return Utils.existFile(CONF_SHUTDOWN_TEMP);
    }

    public static void setCheckIntervalMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_CHECK_INTERVAL_MS), CONF_CHECK_INTERVAL_MS, context);
    }

    public static int getCheckIntervalMs() {
        return Utils.strToInt(Utils.readFile(CONF_CHECK_INTERVAL_MS));
    }

    public static boolean hasCheckIntervalMs() {
        return Utils.existFile(CONF_CHECK_INTERVAL_MS);
    }

    public static void setAllowedMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MAX_FREQ), CONF_ALLOWED_MAX_FREQ, context);
    }

    public static int getAllowedMaxFreq() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MAX_FREQ));
    }

    public static boolean hasAllowedMaxFreq() {
        return Utils.existFile(CONF_ALLOWED_MAX_FREQ);
    }

    public static void setAllowedMaxHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MAX_HIGH), CONF_ALLOWED_MAX_HIGH, context);
    }

    public static int getAllowedMaxHigh() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MAX_HIGH));
    }

    public static boolean hasAllowedMaxHigh() {
        return Utils.existFile(CONF_ALLOWED_MAX_HIGH);
    }

    public static void setAllowedMaxLow(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MAX_LOW), CONF_ALLOWED_MAX_LOW, context);
    }

    public static int getAllowedMaxLow() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MAX_LOW));
    }

    public static boolean hasAllowedMaxLow() {
        return Utils.existFile(CONF_ALLOWED_MAX_LOW);
    }

    public static void setAllowedMidFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MID_FREQ), CONF_ALLOWED_MID_FREQ, context);
    }

    public static int getAllowedMidFreq() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MID_FREQ));
    }

    public static boolean hasAllowedMidFreq() {
        return Utils.existFile(CONF_ALLOWED_MID_FREQ);
    }

    public static void setAllowedMidHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MID_HIGH), CONF_ALLOWED_MID_HIGH, context);
    }

    public static int getAllowedMidHigh() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MID_HIGH));
    }

    public static boolean hasAllowedMidHigh() {
        return Utils.existFile(CONF_ALLOWED_MID_HIGH);
    }

    public static void setAllowedMidLow(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MID_LOW), CONF_ALLOWED_MID_LOW, context);
    }

    public static int getAllowedMidLow() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MID_LOW));
    }

    public static boolean hasAllowedMidLow() {
        return Utils.existFile(CONF_ALLOWED_MID_LOW);
    }

    public static void setAllowedLowFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_LOW_FREQ), CONF_ALLOWED_LOW_FREQ, context);
    }

    public static int getAllowedLowFreq() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_LOW_FREQ));
    }

    public static boolean hasAllowedLowFreq() {
        return Utils.existFile(CONF_ALLOWED_LOW_FREQ);
    }

    public static void setAllowedLowHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_LOW_HIGH), CONF_ALLOWED_LOW_HIGH, context);
    }

    public static int getAllowedLowHigh() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_LOW_HIGH));
    }

    public static boolean hasAllowedLowHigh() {
        return Utils.existFile(CONF_ALLOWED_LOW_HIGH);
    }

    public static void setAllowedLowLow(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_LOW_LOW), CONF_ALLOWED_LOW_LOW, context);
    }

    public static int getAllowedLowLow() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_LOW_LOW));
    }

    public static boolean hasAllowedLowLow() {
        return Utils.existFile(CONF_ALLOWED_LOW_LOW);
    }

    public static void setMinFreqIndex(int value, Context context) {
        run(Control.write(String.valueOf(value), MSM_THERMAL_MIN_FREQ_INDEX),
                MSM_THERMAL_MIN_FREQ_INDEX, context);
    }

    public static int getMinFreqIndex() {
        return Utils.strToInt(Utils.readFile(MSM_THERMAL_MIN_FREQ_INDEX));
    }

    public static boolean hasMinFreqIndex() {
        return Utils.existFile(MSM_THERMAL_MIN_FREQ_INDEX);
    }

    public static void enableFreqLimitDebug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MSM_THERMAL_FREQ_LIMIT_DEBUG),
                MSM_THERMAL_FREQ_LIMIT_DEBUG, context);
    }

    public static boolean isFreqLimitDebugEnabled() {
        return Utils.readFile(MSM_THERMAL_FREQ_LIMIT_DEBUG).equals("1");
    }

    public static boolean hasFreqLimitDebug() {
        return Utils.existFile(MSM_THERMAL_FREQ_LIMIT_DEBUG);
    }

    public static void setTempLimit(int value, Context context) {
        run(Control.write(String.valueOf(value * sTempLimitOffset.get(TEMP_LIMIT)), TEMP_LIMIT),
                TEMP_LIMIT, context);
    }

    public static int getTempLimitMax() {
        return sTempLimitMax.get(TEMP_LIMIT);
    }

    public static int getTempLimitMin() {
        return sTempLimitMin.get(TEMP_LIMIT);
    }

    public static List<String> getTempLimitList(boolean fahrenheit) {
        List<String> list = new ArrayList<>();
        for (double i = getTempLimitMin(); i <= getTempLimitMax(); i++) {
            list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
        }
        return list;
    }

    public static int getCurTempLimit() {
        return Utils.strToInt(Utils.readFile(TEMP_LIMIT)) / sTempLimitOffset.get(TEMP_LIMIT);
    }

    public static boolean hasTempLimit() {
        if (TEMP_LIMIT == null) {
            for (String file : sTempLimitOffset.keySet()) {
                if (Utils.existFile(file)) {
                    TEMP_LIMIT = file;
                    return true;
                }
            }
        }
        return TEMP_LIMIT != null;
    }

    public static void enableTempThrottle(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", MSM_THERMAL_TEMP_THROTTLE), MSM_THERMAL_TEMP_THROTTLE, context);
    }

    public static boolean isTempThrottleEnabled() {
        return Utils.readFile(MSM_THERMAL_TEMP_THROTTLE).equals("Y");
    }

    public static boolean hasTempThrottleEnable() {
        return Utils.existFile(MSM_THERMAL_TEMP_THROTTLE) && !hasCoreLimitTempDegC();
    }

    public static void enableTempSafety(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getParent(PARAMETERS_TEMP_SAFETY)),
                getParent(PARAMETERS_TEMP_SAFETY), context);
    }

    public static boolean isTempSafetyEnabled() {
        return Utils.readFile(getParent(PARAMETERS_TEMP_SAFETY)).equals("1");
    }

    public static boolean hasTempSafety() {
        return Utils.existFile(getParent(PARAMETERS_TEMP_SAFETY));
    }

    public static void setThermalLimitHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_THERMAL_LIMIT_HIGH)),
                getParent(PARAMETERS_THERMAL_LIMIT_HIGH), context);
    }

    public static int getThermalLimitHigh() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_THERMAL_LIMIT_HIGH)));
    }

    public static boolean hasThermalLimitHigh() {
        return Utils.existFile(getParent(PARAMETERS_THERMAL_LIMIT_HIGH));
    }

    public static void setThermalLimitLow(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_THERMAL_LIMIT_LOW)),
                getParent(PARAMETERS_THERMAL_LIMIT_LOW), context);
    }

    public static int getThermalLimitLow() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_THERMAL_LIMIT_LOW)));
    }

    public static boolean hasThermalLimitLow() {
        return Utils.existFile(getParent(PARAMETERS_THERMAL_LIMIT_LOW));
    }

    public static void setTempHysteresisDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC)),
                getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC), context);
    }

    public static int getTempHysteresisDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC)));
    }

    public static boolean hasTempHysteresisDegC() {
        return Utils.existFile(getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC));
    }

    public static void setPollMs(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_POLL_MS)),
                getParent(PARAMETERS_POLL_MS), context);
    }

    public static int getPollMs() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_POLL_MS)));
    }

    public static boolean hasPollMs() {
        return Utils.existFile(getParent(PARAMETERS_POLL_MS));
    }

    public static void enableImmediatelyLimitStop(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP)),
                getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP), context);
    }

    public static boolean isImmediatelyLimitStopEnabled() {
        return Utils.readFile(getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP)).equals("Y");
    }

    public static boolean hasImmediatelyLimitStop() {
        return Utils.existFile(getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP));
    }

    public static void setFreqStep(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_FREQ_STEP)),
                getParent(PARAMETERS_FREQ_STEP), context);
    }

    public static int getFreqStep() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_FREQ_STEP)));
    }

    public static boolean hasFreqStep() {
        return Utils.existFile(getParent(PARAMETERS_FREQ_STEP));
    }

    public static void setCoreTempHysteresisDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC)),
                getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC), context);
    }

    public static int getCoreTempHysteresisDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC)));
    }

    public static boolean hasCoreTempHysteresisDegC() {
        return Utils.existFile(getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC));
    }

    public static void setCoreLimitTempDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC)),
                getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC), context);
    }

    public static int getCoreLimitTempDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC)));
    }

    public static boolean hasCoreLimitTempDegC() {
        return Utils.existFile(getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC));
    }

    public static void setLimitTempDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_LIMIT_TEMP_DEGC)),
                getParent(PARAMETERS_LIMIT_TEMP_DEGC), context);
    }

    public static int getLimitTempDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_LIMIT_TEMP_DEGC)));
    }

    public static boolean hasLimitTempDegC() {
        return Utils.existFile(getParent(PARAMETERS_LIMIT_TEMP_DEGC));
    }

    public static void enableVddRestriction(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getParent(VDD_RESTRICTION_ENABLED)),
                getParent(VDD_RESTRICTION_ENABLED), context);
    }

    public static boolean isVddRestrictionEnabled() {
        return Utils.readFile(getParent(VDD_RESTRICTION_ENABLED)).equals("1");
    }

    public static boolean hasVddRestrictionEnable() {
        return Utils.existFile(getParent(VDD_RESTRICTION_ENABLED));
    }

    public static void enableCoreControl(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CORE_CONTROL), CORE_CONTROL, context);
    }

    public static boolean isCoreControlEnabled() {
        return Utils.readFile(CORE_CONTROL).equals("1");
    }

    public static boolean hasCoreControl() {
        if (CORE_CONTROL == null) {
            if (Utils.existFile(getParent(CORE_CONTROL_ENABLED))) {
                CORE_CONTROL = getParent(CORE_CONTROL_ENABLED);
            } else if (Utils.existFile(getParent(CORE_CONTROL_ENABLED_2))) {
                CORE_CONTROL = getParent(CORE_CONTROL_ENABLED_2);
            }
        }
        return CORE_CONTROL != null;
    }

    public static void enableThermalDebugMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getParent(PARAMETERS_THERMAL_DEBUG_MODE)),
                getParent(PARAMETERS_THERMAL_DEBUG_MODE), context);
    }

    public static boolean isThermalDebugModeEnabled() {
        return Utils.readFile(getParent(PARAMETERS_THERMAL_DEBUG_MODE)).equals("1");
    }

    public static boolean hasThermalDebugMode() {
        return Utils.existFile(getParent(PARAMETERS_THERMAL_DEBUG_MODE));
    }

    public static void enableIntelliThermalOptimized(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", getParent(PARAMETERS_INTELLI_ENABLED)),
                getParent(PARAMETERS_INTELLI_ENABLED), context);
    }

    public static boolean isIntelliThermalOptimizedEnabled() {
        return Utils.readFile(getParent(PARAMETERS_INTELLI_ENABLED)).equals("Y");
    }

    public static boolean hasIntelliThermalOptimizedEnable() {
        return Utils.existFile(getParent(PARAMETERS_INTELLI_ENABLED));
    }

    public static void enableIntelliThermal(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", getParent(PARAMETERS_ENABLED)),
                getParent(PARAMETERS_ENABLED), context);
    }

    public static boolean isIntelliThermalEnabled() {
        return Utils.readFile(getParent(PARAMETERS_ENABLED)).equals("Y");
    }

    public static boolean hasIntelliThermalEnable() {
        return Utils.existFile(getParent(PARAMETERS_ENABLED)) && hasCoreLimitTempDegC();
    }

    private static String getParent(String file) {
        return PARENT + "/" + file;
    }

    public static boolean supported() {
        if (PARENT == null) {
            if (Utils.existFile(MSM_THERMAL)) {
                PARENT = MSM_THERMAL;
            } else if (Utils.existFile(MSM_THERMAL_V2)) {
                PARENT = MSM_THERMAL_V2;
            }
        }
        return PARENT != null || hasTempLimit();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.THERMAL, id, context);
    }

}
