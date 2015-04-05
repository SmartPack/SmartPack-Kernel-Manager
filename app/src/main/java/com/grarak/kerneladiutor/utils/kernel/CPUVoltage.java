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
public class CPUVoltage implements Constants {

    private static String CPU_VOLTAGE_FILE;
    private static String[] mCpuFreqs;

    public static void setGlobalOffset(String voltage, Context context) {
        List<String> voltages = getVoltages();
        String command = "";
        int adjust = Utils.stringToInt(voltage);

        if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
            for (String volt : voltages)
                command += command.isEmpty() ? (Utils.stringToInt(volt) + adjust) :
                        " " + (Utils.stringToInt(volt) + adjust);
        if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE)) {
            command = String.valueOf(adjust * 1000);
            if (adjust > 0) command = "+" + command;
        }

        Control.runCommand(command, CPU_VOLTAGE_FILE, Control.CommandType.GENERIC, context);
    }

    public static void setVoltage(String freq, String voltage, Context context) {
        String command = "";
        int position = 0;
        for (int i = 0; i < getFreqs().size(); i++) {
            if (freq.equals(getFreqs().get(i)))
                position = i;
        }
        if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE)) {
            List<String> voltages = getVoltages();
            for (int i = 0; i < voltages.size(); i++)
                if (i == position)
                    command += command.isEmpty() ? voltage : " " + voltage;
                else
                    command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
            Control.runCommand(command, CPU_VOLTAGE_FILE, Control.CommandType.GENERIC, context);
        } else if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE)) {
            command = getFreqs().get(position) + " " + Utils.stringToInt(voltage) * 1000;
            Control.runCommand(command, CPU_VOLTAGE_FILE, Control.CommandType.GENERIC, String.valueOf(position), context);
        }
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(CPU_VOLTAGE_FILE);
        if (value != null) {
            String[] lines = null;
            value = value.replace(" ", "");
            if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE)) lines = value.split("mV");
            if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE)) lines = value.split("\\r?\\n");
            assert lines != null;
            String[] voltages = new String[lines.length];
            for (int i = 0; i < voltages.length; i++) {
                String[] voltageLine = null;
                if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
                    voltageLine = lines[i].split("mhz:");
                if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                    voltageLine = lines[i].split(":");
                assert voltageLine != null;
                if (voltageLine.length > 1) {
                    if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
                        voltages[i] = voltageLine[1].trim();
                    if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                        voltages[i] = String.valueOf(Utils.stringToInt(voltageLine[1]) / 1000).trim();
                }
            }
            return new ArrayList<>(Arrays.asList(voltages));
        }
        return null;
    }

    public static List<String> getFreqs() {
        if (mCpuFreqs == null) {
            String value = Utils.readFile(CPU_VOLTAGE_FILE);
            if (value != null) {
                String[] lines = null;
                value = value.replace(" ", "");
                if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE)) lines = value.split("mV");
                if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE)) lines = value.split("\\r?\\n");
                assert lines != null;
                mCpuFreqs = new String[lines.length];
                for (int i = 0; i < lines.length; i++) {
                    if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
                        mCpuFreqs[i] = lines[i].split("mhz:")[0].trim();
                    if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                        mCpuFreqs[i] = lines[i].split(":")[0].trim();
                }
            }
        }
        if (mCpuFreqs == null) return null;
        return new ArrayList<>(Arrays.asList(mCpuFreqs));
    }

    public static boolean isFauxVoltage() {
        return CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE);
    }

    public static boolean hasCpuVoltage() {
        for (String file : CPU_VOLTAGE_ARRAY)
            if (Utils.existFile(file)) {
                CPU_VOLTAGE_FILE = file;
                break;
            }
        return CPU_VOLTAGE_FILE != null;
    }

    public static boolean hasOverrideVmin() {
        return Utils.existFile(CPU_OVERRIDE_VMIN);
    }

    public static boolean getOverrideVmin() {
        return Utils.readFile(CPU_OVERRIDE_VMIN).equals("1");
    }

    public static void setOverrideVmin(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_OVERRIDE_VMIN, Control.CommandType.GENERIC, context);
    }
}
