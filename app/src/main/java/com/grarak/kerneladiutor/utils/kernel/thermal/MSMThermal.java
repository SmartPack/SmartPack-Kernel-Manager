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

    private static MSMThermal sInstance;

    public static MSMThermal getInstance() {
        if (sInstance == null) {
            sInstance = new MSMThermal();
        }
        return sInstance;
    }

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

    private static final String POLL_MS = "/sys/module/msm_thermal/parameters/poll_ms";
    private static final String TEMP_THRESHOLD = "/sys/module/msm_thermal/parameters/temperature_threshold";
    private static final String CORE_TEMP_LIMIT_DEGC = "/sys/module/msm_thermal/parameters/core_temp_limit_degC";
    private static final String FREQ_MITIG_TEMP = "/sys/module/msm_thermal/parameters/freq_mitig_temp_degc";
    private static final String HOTPLUG_TEMP = "/sys/module/msm_thermal/parameters/hotplug_temp_degC";
    private static final String CORE_CONTROL_MASK = "/sys/module/msm_thermal/parameters/core_control_mask";
    private static final String FREQ_CONTROL_MASK = "/sys/module/msm_thermal/parameters/freq_control_mask";
    private static final String FREQ_MITIG_CONTROL_MASK = "/sys/module/msm_thermal/parameters/freq_mitig_control_mask";

    private final HashMap<String, Integer> mTempLimitOffset = new HashMap<>();
    private final HashMap<String, Integer> mTempLimitMin = new HashMap<>();
    private final HashMap<String, Integer> mTempLimitMax = new HashMap<>();

    {
        mTempLimitOffset.put(MSM_THERMAL_THROTTLE_TEMP, 1);
        mTempLimitOffset.put(MSM_THERMAL_TEMP_MAX, 1);
        mTempLimitOffset.put(MSM_THERMAL_TEMP_THRESHOLD, 1);
        mTempLimitOffset.put(TEMPCONTROL_TEMP_LIMIT, 1000);

        mTempLimitMin.put(MSM_THERMAL_THROTTLE_TEMP, 50);
        mTempLimitMin.put(MSM_THERMAL_TEMP_MAX, 50);
        mTempLimitMin.put(MSM_THERMAL_TEMP_THRESHOLD, 50);
        mTempLimitMin.put(TEMPCONTROL_TEMP_LIMIT, 60);

        mTempLimitMax.put(MSM_THERMAL_THROTTLE_TEMP, 100);
        mTempLimitMax.put(MSM_THERMAL_TEMP_MAX, 100);
        mTempLimitMax.put(MSM_THERMAL_TEMP_THRESHOLD, 100);
        mTempLimitMax.put(TEMPCONTROL_TEMP_LIMIT, 80);
    }

    private String PARENT;
    private String CORE_CONTROL;
    private String TEMP_LIMIT;

    private MSMThermal() {
        if (Utils.existFile(MSM_THERMAL)) {
            PARENT = MSM_THERMAL;
        } else if (Utils.existFile(MSM_THERMAL_V2)) {
            PARENT = MSM_THERMAL_V2;
        }
        if (PARENT == null) return;

        if (Utils.existFile(getParent(CORE_CONTROL_ENABLED))) {
            CORE_CONTROL = getParent(CORE_CONTROL_ENABLED);
        } else if (Utils.existFile(getParent(CORE_CONTROL_ENABLED_2))) {
            CORE_CONTROL = getParent(CORE_CONTROL_ENABLED_2);
        }

        for (String file : mTempLimitOffset.keySet()) {
            if (Utils.existFile(file)) {
                TEMP_LIMIT = file;
                break;
            }
        }
    }

    public void setShutdownTemp(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_SHUTDOWN_TEMP), CONF_SHUTDOWN_TEMP, context);
    }

    public int getShutdownTemp() {
        return Utils.strToInt(Utils.readFile(CONF_SHUTDOWN_TEMP));
    }

    public boolean hasShutdownTemp() {
        return Utils.existFile(CONF_SHUTDOWN_TEMP);
    }

    public void setCheckIntervalMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_CHECK_INTERVAL_MS), CONF_CHECK_INTERVAL_MS, context);
    }

    public int getCheckIntervalMs() {
        return Utils.strToInt(Utils.readFile(CONF_CHECK_INTERVAL_MS));
    }

    public boolean hasCheckIntervalMs() {
        return Utils.existFile(CONF_CHECK_INTERVAL_MS);
    }

    public void setAllowedMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MAX_FREQ), CONF_ALLOWED_MAX_FREQ, context);
    }

    public int getAllowedMaxFreq() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MAX_FREQ));
    }

    public boolean hasAllowedMaxFreq() {
        return Utils.existFile(CONF_ALLOWED_MAX_FREQ);
    }

    public void setAllowedMaxHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MAX_HIGH), CONF_ALLOWED_MAX_HIGH, context);
    }

    public int getAllowedMaxHigh() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MAX_HIGH));
    }

    public boolean hasAllowedMaxHigh() {
        return Utils.existFile(CONF_ALLOWED_MAX_HIGH);
    }

    public void setAllowedMaxLow(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MAX_LOW), CONF_ALLOWED_MAX_LOW, context);
    }

    public int getAllowedMaxLow() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MAX_LOW));
    }

    public boolean hasAllowedMaxLow() {
        return Utils.existFile(CONF_ALLOWED_MAX_LOW);
    }

    public void setAllowedMidFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MID_FREQ), CONF_ALLOWED_MID_FREQ, context);
    }

    public int getAllowedMidFreq() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MID_FREQ));
    }

    public boolean hasAllowedMidFreq() {
        return Utils.existFile(CONF_ALLOWED_MID_FREQ);
    }

    public void setAllowedMidHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MID_HIGH), CONF_ALLOWED_MID_HIGH, context);
    }

    public int getAllowedMidHigh() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MID_HIGH));
    }

    public boolean hasAllowedMidHigh() {
        return Utils.existFile(CONF_ALLOWED_MID_HIGH);
    }

    public void setAllowedMidLow(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_MID_LOW), CONF_ALLOWED_MID_LOW, context);
    }

    public int getAllowedMidLow() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_MID_LOW));
    }

    public boolean hasAllowedMidLow() {
        return Utils.existFile(CONF_ALLOWED_MID_LOW);
    }

    public void setAllowedLowFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_LOW_FREQ), CONF_ALLOWED_LOW_FREQ, context);
    }

    public int getAllowedLowFreq() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_LOW_FREQ));
    }

    public boolean hasAllowedLowFreq() {
        return Utils.existFile(CONF_ALLOWED_LOW_FREQ);
    }

    public void setAllowedLowHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_LOW_HIGH), CONF_ALLOWED_LOW_HIGH, context);
    }

    public int getAllowedLowHigh() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_LOW_HIGH));
    }

    public boolean hasAllowedLowHigh() {
        return Utils.existFile(CONF_ALLOWED_LOW_HIGH);
    }

    public void setAllowedLowLow(int value, Context context) {
        run(Control.write(String.valueOf(value), CONF_ALLOWED_LOW_LOW), CONF_ALLOWED_LOW_LOW, context);
    }

    public int getAllowedLowLow() {
        return Utils.strToInt(Utils.readFile(CONF_ALLOWED_LOW_LOW));
    }

    public boolean hasAllowedLowLow() {
        return Utils.existFile(CONF_ALLOWED_LOW_LOW);
    }

    public void setMinFreqIndex(int value, Context context) {
        run(Control.write(String.valueOf(value), MSM_THERMAL_MIN_FREQ_INDEX),
                MSM_THERMAL_MIN_FREQ_INDEX, context);
    }

    public int getMinFreqIndex() {
        return Utils.strToInt(Utils.readFile(MSM_THERMAL_MIN_FREQ_INDEX));
    }

    public boolean hasMinFreqIndex() {
        return Utils.existFile(MSM_THERMAL_MIN_FREQ_INDEX);
    }

    public void enableFreqLimitDebug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MSM_THERMAL_FREQ_LIMIT_DEBUG),
                MSM_THERMAL_FREQ_LIMIT_DEBUG, context);
    }

    public boolean isFreqLimitDebugEnabled() {
        return Utils.readFile(MSM_THERMAL_FREQ_LIMIT_DEBUG).equals("1");
    }

    public boolean hasFreqLimitDebug() {
        return Utils.existFile(MSM_THERMAL_FREQ_LIMIT_DEBUG);
    }

    public void setTempLimit(int value, Context context) {
        run(Control.write(String.valueOf(value * mTempLimitOffset.get(TEMP_LIMIT)), TEMP_LIMIT),
                TEMP_LIMIT, context);
    }

    public int getTempLimitMax() {
        return mTempLimitMax.get(TEMP_LIMIT);
    }

    public int getTempLimitMin() {
        return mTempLimitMin.get(TEMP_LIMIT);
    }

    public List<String> getTempLimitList(boolean fahrenheit) {
        List<String> list = new ArrayList<>();
        for (double i = getTempLimitMin(); i <= getTempLimitMax(); i++) {
            list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
        }
        return list;
    }

    public int getCurTempLimit() {
        return Utils.strToInt(Utils.readFile(TEMP_LIMIT)) / mTempLimitOffset.get(TEMP_LIMIT);
    }

    public boolean hasTempLimit() {
        return TEMP_LIMIT != null;
    }

    public void enableTempThrottle(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", MSM_THERMAL_TEMP_THROTTLE), MSM_THERMAL_TEMP_THROTTLE, context);
    }

    public boolean isTempThrottleEnabled() {
        return Utils.readFile(MSM_THERMAL_TEMP_THROTTLE).equals("Y");
    }

    public boolean hasTempThrottleEnable() {
        return Utils.existFile(MSM_THERMAL_TEMP_THROTTLE) && !hasCoreLimitTempDegC();
    }

    public void enableTempSafety(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getParent(PARAMETERS_TEMP_SAFETY)),
                getParent(PARAMETERS_TEMP_SAFETY), context);
    }

    public boolean isTempSafetyEnabled() {
        return Utils.readFile(getParent(PARAMETERS_TEMP_SAFETY)).equals("1");
    }

    public boolean hasTempSafety() {
        return Utils.existFile(getParent(PARAMETERS_TEMP_SAFETY));
    }

    public void setThermalLimitHigh(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_THERMAL_LIMIT_HIGH)),
                getParent(PARAMETERS_THERMAL_LIMIT_HIGH), context);
    }

    public int getThermalLimitHigh() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_THERMAL_LIMIT_HIGH)));
    }

    public boolean hasThermalLimitHigh() {
        return Utils.existFile(getParent(PARAMETERS_THERMAL_LIMIT_HIGH));
    }

    public void setThermalLimitLow(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_THERMAL_LIMIT_LOW)),
                getParent(PARAMETERS_THERMAL_LIMIT_LOW), context);
    }

    public int getThermalLimitLow() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_THERMAL_LIMIT_LOW)));
    }

    public boolean hasThermalLimitLow() {
        return Utils.existFile(getParent(PARAMETERS_THERMAL_LIMIT_LOW));
    }

    public void setTempHysteresisDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC)),
                getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC), context);
    }

    public int getTempHysteresisDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC)));
    }

    public boolean hasTempHysteresisDegC() {
        return Utils.existFile(getParent(PARAMETERS_TEMP_HYSTERESIS_DEGC));
    }

    public void setPollMs(String value, Context context) {
        run(Control.write(String.valueOf(value), POLL_MS), POLL_MS, context);
    }

    public static String getPollMs() {
        return Utils.readFile(POLL_MS);
    }

    public boolean hasPollMs() {
        return Utils.existFile(POLL_MS);
    }

    public void enableImmediatelyLimitStop(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP)),
                getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP), context);
    }

    public boolean isImmediatelyLimitStopEnabled() {
        return Utils.readFile(getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP)).equals("Y");
    }

    public boolean hasImmediatelyLimitStop() {
        return Utils.existFile(getParent(PARAMETERS_IMMEDIATELY_LIMIT_STOP));
    }

    public void setFreqStep(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_FREQ_STEP)),
                getParent(PARAMETERS_FREQ_STEP), context);
    }

    public int getFreqStep() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_FREQ_STEP)));
    }

    public boolean hasFreqStep() {
        return Utils.existFile(getParent(PARAMETERS_FREQ_STEP));
    }

    public void setCoreTempHysteresisDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC)),
                getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC), context);
    }

    public int getCoreTempHysteresisDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC)));
    }

    public boolean hasCoreTempHysteresisDegC() {
        return Utils.existFile(getParent(PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC));
    }

    public void setCoreLimitTempDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC)),
                getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC), context);
    }

    public int getCoreLimitTempDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC)));
    }

    public boolean hasCoreLimitTempDegC() {
        return Utils.existFile(getParent(PARAMETERS_CORE_LIMIT_TEMP_DEGC));
    }

    public void setLimitTempDegC(int value, Context context) {
        run(Control.write(String.valueOf(value), getParent(PARAMETERS_LIMIT_TEMP_DEGC)),
                getParent(PARAMETERS_LIMIT_TEMP_DEGC), context);
    }

    public int getLimitTempDegC() {
        return Utils.strToInt(Utils.readFile(getParent(PARAMETERS_LIMIT_TEMP_DEGC)));
    }

    public boolean hasLimitTempDegC() {
        return Utils.existFile(getParent(PARAMETERS_LIMIT_TEMP_DEGC));
    }

    public void enableVddRestriction(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getParent(VDD_RESTRICTION_ENABLED)),
                getParent(VDD_RESTRICTION_ENABLED), context);
    }

    public boolean isVddRestrictionEnabled() {
        return Utils.readFile(getParent(VDD_RESTRICTION_ENABLED)).equals("1");
    }

    public boolean hasVddRestrictionEnable() {
        return Utils.existFile(getParent(VDD_RESTRICTION_ENABLED));
    }

    public void enableCoreControl(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CORE_CONTROL), CORE_CONTROL, context);
    }

    public boolean isCoreControlEnabled() {
        return Utils.readFile(CORE_CONTROL).equals("1");
    }

    public boolean hasCoreControl() {
        return CORE_CONTROL != null;
    }

    public void enableThermalDebugMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getParent(PARAMETERS_THERMAL_DEBUG_MODE)),
                getParent(PARAMETERS_THERMAL_DEBUG_MODE), context);
    }

    public boolean isThermalDebugModeEnabled() {
        return Utils.readFile(getParent(PARAMETERS_THERMAL_DEBUG_MODE)).equals("1");
    }

    public boolean hasThermalDebugMode() {
        return Utils.existFile(getParent(PARAMETERS_THERMAL_DEBUG_MODE));
    }

    public void enableIntelliThermalOptimized(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", getParent(PARAMETERS_INTELLI_ENABLED)),
                getParent(PARAMETERS_INTELLI_ENABLED), context);
    }

    public boolean isIntelliThermalOptimizedEnabled() {
        return Utils.readFile(getParent(PARAMETERS_INTELLI_ENABLED)).equals("Y");
    }

    public boolean hasIntelliThermalOptimizedEnable() {
        return Utils.existFile(getParent(PARAMETERS_INTELLI_ENABLED));
    }

    public void enableIntelliThermal(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", getParent(PARAMETERS_ENABLED)),
                getParent(PARAMETERS_ENABLED), context);
    }

    public boolean isIntelliThermalEnabled() {
        return Utils.readFile(getParent(PARAMETERS_ENABLED)).equals("Y");
    }

    public boolean hasIntelliThermalEnable() {
        return Utils.existFile(getParent(PARAMETERS_ENABLED)) && hasCoreLimitTempDegC();
    }

    private String getParent(String file) {
        return PARENT + "/" + file;
    }

    public boolean supported() {
        return PARENT != null || hasTempLimit();
    }

    public static String getTemp_Limit() {
        return Utils.readFile(TEMP_THRESHOLD);
    }

    public void setTemp_Limit(String value, Context context) {
        run(Control.write(String.valueOf(value), TEMP_THRESHOLD), TEMP_THRESHOLD, context);
    }

    public static boolean hasTemp_Limit() {
        return Utils.existFile(TEMP_THRESHOLD);
    }

    public static String getCoreTempLimit() {
        return Utils.readFile(CORE_TEMP_LIMIT_DEGC);
    }

    public void setCoreTempLimit(String value, Context context) {
        run(Control.write(String.valueOf(value), CORE_TEMP_LIMIT_DEGC), CORE_TEMP_LIMIT_DEGC, context);
    }

    public static boolean hasCoreTempLimit() {
        return Utils.existFile(CORE_TEMP_LIMIT_DEGC);
    }

    public static String getFreqmitigTemp() {
        return Utils.readFile(FREQ_MITIG_TEMP);
    }

    public void setFreqmitigTemp(String value, Context context) {
        run(Control.write(String.valueOf(value), FREQ_MITIG_TEMP), FREQ_MITIG_TEMP, context);
    }

    public static boolean hasFreqmitigTemp() {
        return Utils.existFile(FREQ_MITIG_TEMP);
    }

    public static String getHotPlugTemp() {
        return Utils.readFile(HOTPLUG_TEMP);
    }

    public void setHotPlugTemp(String value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_TEMP), HOTPLUG_TEMP, context);
    }

    public static boolean hasHotPlugTemp() {
        return Utils.existFile(HOTPLUG_TEMP);
    }

    public static String getCoreControlMask() {
        return Utils.readFile(CORE_CONTROL_MASK);
    }

    public void setCoreControlMask(String value, Context context) {
        run(Control.write(String.valueOf(value), CORE_CONTROL_MASK), CORE_CONTROL_MASK, context);
    }

    public static boolean hasCoreControlMask() {
        return Utils.existFile(CORE_CONTROL_MASK);
    }

    public static String getFreqControlMask() {
        return Utils.readFile(FREQ_CONTROL_MASK);
    }

    public void setFreqControlMask(String value, Context context) {
        run(Control.write(String.valueOf(value), FREQ_CONTROL_MASK), FREQ_CONTROL_MASK, context);
    }

    public static boolean hasFreqControlMask() {
        return Utils.existFile(FREQ_CONTROL_MASK);
    }

    public static String getFreqMitigControlMask() {
        return Utils.readFile(FREQ_MITIG_CONTROL_MASK);
    }

    public void setFreqMitigControlMask(String value, Context context) {
        run(Control.write(String.valueOf(value), FREQ_MITIG_CONTROL_MASK), FREQ_MITIG_CONTROL_MASK, context);
    }

    public static boolean hasFreqMitigControlMask() {
        return Utils.existFile(FREQ_MITIG_CONTROL_MASK);
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.THERMAL, id, context);
    }

}
