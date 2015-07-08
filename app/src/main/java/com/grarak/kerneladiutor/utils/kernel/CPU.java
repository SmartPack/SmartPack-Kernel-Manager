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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.LinuxUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 02.12.14.
 */
public class CPU implements Constants {

    private static Integer[] mFreqs;
    private static String[] mAvailableGovernors;
    private static String[] mMcPowerSavingItems;
    private static String[] mAvailableCFSSchedulers;

    private static String TEMP_FILE;

    private static String[] mCpuQuietAvailableGovernors;

    private static String CPU_BOOST_ENABLE_FILE;

    public static void setCpuBoostInputMs(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_BOOST_INPUT_MS, Control.CommandType.GENERIC, context);
    }

    public static int getCpuBootInputMs() {
        return Utils.stringToInt(Utils.readFile(CPU_BOOST_INPUT_MS));
    }

    public static boolean hasCpuBoostInputMs() {
        return Utils.existFile(CPU_BOOST_INPUT_MS);
    }

    public static void setCpuBoostInputFreq(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_BOOST_INPUT_BOOST_FREQ, Control.CommandType.GENERIC, context);
    }

    public static int getCpuBootInputFreq() {
        String value = Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ);
        try {
            value = value.split("0:")[1].split(" ")[0];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        if (value.equals("0")) return 0;
        return CPU.getFreqs().indexOf(Utils.stringToInt(value)) + 1;
    }

    public static boolean hasCpuBoostInputFreq() {
        return Utils.existFile(CPU_BOOST_INPUT_BOOST_FREQ);
    }

    public static void setCpuBoostSyncThreshold(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_BOOST_SYNC_THRESHOLD, Control.CommandType.GENERIC, context);
    }

    public static int getCpuBootSyncThreshold() {
        String value = Utils.readFile(CPU_BOOST_SYNC_THRESHOLD);
        if (value.equals("0")) return 0;
        return CPU.getFreqs().indexOf(Utils.stringToInt(value)) + 1;
    }

    public static boolean hasCpuBoostSyncThreshold() {
        return Utils.existFile(CPU_BOOST_SYNC_THRESHOLD);
    }

    public static void setCpuBoostMs(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_BOOST_MS, Control.CommandType.GENERIC, context);
    }

    public static int getCpuBootMs() {
        return Utils.stringToInt(Utils.readFile(CPU_BOOST_MS));
    }

    public static boolean hasCpuBoostMs() {
        return Utils.existFile(CPU_BOOST_MS);
    }

    public static void activateCpuBoostDebugMask(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_BOOST_DEBUG_MASK, Control.CommandType.GENERIC, context);
    }

    public static boolean isCpuBoostDebugMaskActive() {
        return Utils.readFile(CPU_BOOST_DEBUG_MASK).equals("1");
    }

    public static boolean hasCpuBoostDebugMask() {
        return Utils.existFile(CPU_BOOST_DEBUG_MASK);
    }

    public static void activateCpuBoost(boolean active, Context context) {
        String command = active ? "1" : "0";
        if (CPU_BOOST_ENABLE_FILE.equals(CPU_BOOST_ENABLE_2)) command = active ? "Y" : "N";
        Control.runCommand(command, CPU_BOOST_ENABLE_FILE, Control.CommandType.GENERIC, context);
    }

    public static boolean isCpuBoostActive() {
        String value = Utils.readFile(CPU_BOOST_ENABLE_FILE);
        return value.equals("1") || value.equals("Y");
    }

    public static boolean hasCpuBoostEnable() {
        if (Utils.existFile(CPU_BOOST_ENABLE)) CPU_BOOST_ENABLE_FILE = CPU_BOOST_ENABLE;
        else if (Utils.existFile(CPU_BOOST_ENABLE_2)) CPU_BOOST_ENABLE_FILE = CPU_BOOST_ENABLE_2;
        return CPU_BOOST_ENABLE_FILE != null;
    }

    public static boolean hasCpuBoost() {
        return Utils.existFile(CPU_BOOST);
    }

    public static void setCpuQuietGovernor(String value, Context context) {
        Control.runCommand(value, CPU_QUIET_CURRENT_GOVERNOR, Control.CommandType.GENERIC, context);
    }

    public static String getCpuQuietCurGovernor() {
        return Utils.readFile(CPU_QUIET_CURRENT_GOVERNOR);
    }

    public static List<String> getCpuQuietAvailableGovernors() {
        if (mCpuQuietAvailableGovernors == null) {
            String[] governors = Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).split(" ");
            if (governors.length > 0) {
                mCpuQuietAvailableGovernors = new String[governors.length];
                System.arraycopy(governors, 0, mCpuQuietAvailableGovernors, 0, mCpuQuietAvailableGovernors.length);
            }
        }
        if (mCpuQuietAvailableGovernors == null) return null;
        return new ArrayList<>(Arrays.asList(mCpuQuietAvailableGovernors));
    }

    public static boolean hasCpuQuietGovernors() {
        return Utils.existFile(CPU_QUIET_AVAILABLE_GOVERNORS) && Utils.existFile(CPU_QUIET_CURRENT_GOVERNOR)
                && !Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).equals("none");
    }

    public static void activateCpuQuiet(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_QUIET_ENABLE, Control.CommandType.GENERIC, context);
    }

    public static boolean isCpuQuietActive() {
        return Utils.readFile(CPU_QUIET_ENABLE).equals("1");
    }

    public static boolean hasCpuQuietEnable() {
        return Utils.existFile(CPU_QUIET_ENABLE);
    }

    public static boolean hasCpuQuiet() {
        return Utils.existFile(CPU_QUIET);
    }

    public static void setCFSScheduler(String value, Context context) {
        Control.runCommand(value, CPU_CURRENT_CFS_SCHEDULER, Control.CommandType.GENERIC, context);
    }

    public static String getCurrentCFSScheduler() {
        return Utils.readFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static List<String> getAvailableCFSSchedulers() {
        if (mAvailableCFSSchedulers == null)
            mAvailableCFSSchedulers = Utils.readFile(CPU_AVAILABLE_CFS_SCHEDULERS).split(" ");
        return new ArrayList<>(Arrays.asList(mAvailableCFSSchedulers));
    }

    public static boolean hasCFSScheduler() {
        return Utils.existFile(CPU_AVAILABLE_CFS_SCHEDULERS) && Utils.existFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static String[] getMcPowerSavingItems(Context context) {
        if (mMcPowerSavingItems == null && context != null)
            mMcPowerSavingItems = context.getResources().getStringArray(R.array.mc_power_saving_items);
        return mMcPowerSavingItems;
    }

    public static void setMcPowerSaving(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_MC_POWER_SAVING, Control.CommandType.GENERIC, context);
    }

    public static int getCurMcPowerSaving() {
        return Utils.stringToInt(Utils.readFile(CPU_MC_POWER_SAVING));
    }

    public static boolean hasMcPowerSaving() {
        return Utils.existFile(CPU_MC_POWER_SAVING);
    }

    public static void activatePowerSavingWq(boolean active, Context context) {
        String command = active ? "Y" : "N";
        Control.runCommand(command, CPU_WQ_POWER_SAVING, Control.CommandType.GENERIC, context);
    }

    public static boolean isPowerSavingWqActive() {
        String value = Utils.readFile(CPU_WQ_POWER_SAVING);
        return value.equals("Y");
    }

    public static boolean hasPowerSavingWq() {
        return Utils.existFile(CPU_WQ_POWER_SAVING);
    }

    public static ArrayList<String> getAvailableGovernors() {
        if (mAvailableGovernors == null) {
            String value = Utils.readFile(CPU_AVAILABLE_GOVERNORS);
            if (value != null) mAvailableGovernors = value.split(" ");
            else return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(mAvailableGovernors));
    }

    public static void setGovernor(String governor, Context context) {
        Control.runCommand(governor, CPU_SCALING_GOVERNOR, Control.CommandType.CPU, context);
    }

    public static String getCurGovernor(int core) {
        if (Utils.existFile(String.format(CPU_SCALING_GOVERNOR, core))) {
            String value = Utils.readFile(String.format(CPU_SCALING_GOVERNOR, core));
            if (value != null) return value;
        }
        return "";
    }

    public static ArrayList<Integer> getFreqs() {
        if (mFreqs == null) {
            if (Utils.existFile(CPU_AVAILABLE_FREQS)) {
                String values = Utils.readFile(CPU_AVAILABLE_FREQS);
                if (values != null) {
                    String[] valueArray = values.split(" ");
                    mFreqs = new Integer[valueArray.length];
                    for (int i = 0; i < mFreqs.length; i++)
                        mFreqs[i] = Utils.stringToInt(valueArray[i]);
                }
            } else if (Utils.existFile(CPU_TIME_STATE)) {
                String values = Utils.readFile(CPU_TIME_STATE);
                if (values != null) {
                    String[] valueArray = values.split("\\r?\\n");
                    mFreqs = new Integer[valueArray.length];
                    for (int i = 0; i < mFreqs.length; i++)
                        mFreqs[i] = Utils.stringToInt(valueArray[i].split(" ")[0]);

                    if (mFreqs[0] > mFreqs[mFreqs.length - 1]) {
                        List<Integer> freqs = new ArrayList<>();
                        for (int x = mFreqs.length - 1; x >= 0; x--)
                            freqs.add(mFreqs[x]);
                        for (int i = 0; i < mFreqs.length; i++)
                            mFreqs[i] = freqs.get(i);
                    }
                }
            }
        }
        if (mFreqs == null) return null;
        return new ArrayList<>(Arrays.asList(mFreqs));
    }

    public static void setMaxScreenOffFreq(int freq, Context context) {
        Control.runCommand(String.valueOf(freq), CPU_MAX_SCREEN_OFF_FREQ, Control.CommandType.CPU, context);
    }

    public static int getMaxScreenOffFreq(int core) {
        if (Utils.existFile(String.format(CPU_MAX_SCREEN_OFF_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_MAX_SCREEN_OFF_FREQ, core));
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static boolean hasMaxScreenOffFreq() {
        return Utils.existFile(String.format(CPU_MAX_SCREEN_OFF_FREQ, 0));
    }

    public static void setMinFreq(int freq, Context context) {
        if (getMaxFreq(0) < freq) setMaxFreq(freq, context);
        Control.runCommand(String.valueOf(freq), CPU_MIN_FREQ, Control.CommandType.CPU, context);
    }

    public static int getMinFreq(int core) {
        if (Utils.existFile(String.format(CPU_MIN_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_MIN_FREQ, core));
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static void setMaxFreq(int freq, Context context) {
        if (Utils.existFile(CPU_MSM_CPUFREQ_LIMIT))
            Control.runCommand(String.valueOf(freq), CPU_MSM_CPUFREQ_LIMIT, Control.CommandType.GENERIC, context);
        if (getMinFreq(0) > freq) setMinFreq(freq, context);
        Control.runCommand(String.valueOf(freq), CPU_MAX_FREQ, Control.CommandType.CPU, context);
    }

    public static int getMaxFreq(int core) {
        if (Utils.existFile(String.format(CPU_MAX_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_MAX_FREQ, core));
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static int getCurFreq(int core) {
        if (Utils.existFile(String.format(CPU_CUR_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_CUR_FREQ, core));
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static void activateCore(int core, boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", String.format(CPU_CORE_ONLINE, core),
                Control.CommandType.GENERIC, context);
    }

    public static int getCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getTemp() {
        double temp = Utils.stringToLong(Utils.readFile(TEMP_FILE));
        if (temp > 1000) temp /= 1000;
        else if (temp > 200) temp /= 10;
        return Utils.formatCelsius(temp) + " " + Utils.celsiusToFahrenheit(temp);
    }

    public static boolean hasTemp() {
        if (Utils.existFile(CPU_TEMP_ZONE1)) {
            int temp = Utils.stringToInt(Utils.readFile(CPU_TEMP_ZONE1));
            if (temp > -1 && temp < 1000000) {
                TEMP_FILE = CPU_TEMP_ZONE1;
                return true;
            }
        }
        if (Utils.existFile(CPU_TEMP_ZONE0)) TEMP_FILE = CPU_TEMP_ZONE0;
        return TEMP_FILE != null;
    }

    /**
     * This code is from http://stackoverflow.com/a/13342738
     */
    private static LinuxUtils linuxUtils;

    public static float getCpuUsage() {
        if (linuxUtils == null) linuxUtils = new LinuxUtils();

        try {
            String cpuStat1 = linuxUtils.readSystemStat();
            Thread.sleep(1000);
            String cpuStat2 = linuxUtils.readSystemStat();
            float usage = linuxUtils.getSystemCpuUsage(cpuStat1, cpuStat2);
            if (usage > -1) return usage;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
