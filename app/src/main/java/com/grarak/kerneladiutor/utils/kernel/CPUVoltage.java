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

    public static void activateOverrideVmin(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_OVERRIDE_VMIN, Control.CommandType.GENERIC, context);
    }

    public static boolean isOverrideVminActive() {
        return Utils.readFile(CPU_OVERRIDE_VMIN).equals("1");
    }

    public static boolean hasOverrideVmin() {
        return Utils.existFile(CPU_OVERRIDE_VMIN);
    }

    public static void setGlobalOffset(String voltage, Context context) {
        int adjust = Utils.stringToInt(voltage);
        String command = String.valueOf(adjust);

        switch (CPU_VOLTAGE_FILE) {
            case CPU_VDD_VOLTAGE:
            case CPU_FAUX_VOLTAGE:
                if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                    command = String.valueOf(adjust * 1000);
                if (adjust > 0) command = "+" + command;
                break;
            default:
                command = "";
                for (String volt : getVoltages())
                    if (volt != null)
                        command += command.isEmpty() ? (Utils.stringToInt(volt) + adjust) :
                                " " + (Utils.stringToInt(volt) + adjust);
                break;
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
        switch (CPU_VOLTAGE_FILE) {
            case CPU_VDD_VOLTAGE:
            case CPU_FAUX_VOLTAGE:
                command = getFreqs().get(position) + " " + Utils.stringToInt(voltage) * 1000;
                Control.runCommand(command, CPU_VOLTAGE_FILE, Control.CommandType.GENERIC, String.valueOf(position), context);
                break;
            default:
                List<String> voltages = getVoltages();
                for (int i = 0; i < voltages.size(); i++)
                    if (i == position)
                        command += command.isEmpty() ? voltage : " " + voltage;
                    else
                        command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
                Control.runCommand(command, CPU_VOLTAGE_FILE, Control.CommandType.GENERIC, context);
                break;
        }
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(CPU_VOLTAGE_FILE);
        if (value != null) {
            String[] lines;
            value = value.replace(" ", "");
            switch (CPU_VOLTAGE_FILE) {
                case CPU_VDD_VOLTAGE:
                case CPU_FAUX_VOLTAGE:
                    lines = value.split("\\r?\\n");
                    break;
                default:
                    lines = value.split("mV");
                    break;
            }
            String[] voltages = new String[lines.length];
            for (int i = 0; i < voltages.length; i++) {
                String[] voltageLine;
                switch (CPU_VOLTAGE_FILE) {
                    case CPU_VDD_VOLTAGE:
                    case CPU_FAUX_VOLTAGE:
                        voltageLine = lines[i].split(":");
                        break;
                    default:
                        voltageLine = lines[i].split("mhz:");
                        break;
                }
                if (voltageLine.length > 1) {
                    switch (CPU_VOLTAGE_FILE) {
                        case CPU_FAUX_VOLTAGE:
                            voltages[i] = String.valueOf(Utils.stringToInt(voltageLine[1]) / 1000).trim();
                            break;
                        default:
                            voltages[i] = voltageLine[1].trim();
                            break;
                    }
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
                String[] lines;
                value = value.replace(" ", "");
                switch (CPU_VOLTAGE_FILE) {
                    case CPU_VDD_VOLTAGE:
                    case CPU_FAUX_VOLTAGE:
                        lines = value.split("\\r?\\n");
                        break;
                    default:
                        lines = value.split("mV");
                        break;
                }
                mCpuFreqs = new String[lines.length];
                for (int i = 0; i < lines.length; i++) {
                    switch (CPU_VOLTAGE_FILE) {
                        case CPU_VDD_VOLTAGE:
                        case CPU_FAUX_VOLTAGE:
                            mCpuFreqs[i] = lines[i].split(":")[0].trim();
                            break;
                        default:
                            mCpuFreqs[i] = lines[i].split("mhz:")[0].trim();
                            break;
                    }
                }
            }
        }
        if (mCpuFreqs == null) return null;
        return new ArrayList<>(Arrays.asList(mCpuFreqs));
    }

    public static boolean isVddVoltage() {
        return CPU_VOLTAGE_FILE.equals(CPU_VDD_VOLTAGE) || CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE);
    }

    public static boolean hasCpuVoltage() {
        for (String file : CPU_VOLTAGE_ARRAY)
            if (Utils.existFile(file)) {
                CPU_VOLTAGE_FILE = file;
                return true;
            }
        return false;
    }

}
