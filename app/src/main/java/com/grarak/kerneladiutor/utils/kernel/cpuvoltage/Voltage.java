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
package com.grarak.kerneladiutor.utils.kernel.cpuvoltage;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class Voltage {

    private static final String CPU_OVERRIDE_VMIN = "/sys/devices/system/cpu/cpu0/cpufreq/override_vmin";

    private static final String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    private static final String CPU_VDD_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/vdd_levels";
    private static final String CPU_FAUX_VOLTAGE = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

    private static final HashMap<String, Boolean> sVoltages = new HashMap<>();
    private static final HashMap<String, Integer> sOffset = new HashMap<>();
    private static final HashMap<String, String> sSplitNewline = new HashMap<>();
    private static final HashMap<String, String> sSplitLine = new HashMap<>();
    private static final HashMap<String, Boolean> sAppend = new HashMap<>();

    static {
        sVoltages.put(CPU_VOLTAGE, false);
        sVoltages.put(CPU_VDD_VOLTAGE, true);
        sVoltages.put(CPU_FAUX_VOLTAGE, true);

        sOffset.put(CPU_VOLTAGE, 1);
        sOffset.put(CPU_VDD_VOLTAGE, 1);
        sOffset.put(CPU_FAUX_VOLTAGE, 1000);

        sSplitNewline.put(CPU_VOLTAGE, "mV");
        sSplitNewline.put(CPU_VDD_VOLTAGE, "\\r?\\n");
        sSplitNewline.put(CPU_FAUX_VOLTAGE, "\\r?\\n");

        sSplitLine.put(CPU_VOLTAGE, "mhz:");
        sSplitLine.put(CPU_VDD_VOLTAGE, ":");
        sSplitLine.put(CPU_FAUX_VOLTAGE, ":");

        sAppend.put(CPU_VOLTAGE, true);
        sAppend.put(CPU_VDD_VOLTAGE, false);
        sAppend.put(CPU_FAUX_VOLTAGE, false);
    }

    private static String PATH;
    private static String[] sFreqs;

    public static void setGlobalOffset(int adjust, Context context) {
        String value = "";
        List<String> voltages = getVoltages();
        if (voltages == null) return;
        if (sAppend.get(PATH)) {
            for (String volt : voltages) {
                if (!value.isEmpty()) {
                    value += " ";
                }
                value += String.valueOf(Utils.strToInt(volt) + adjust);
            }
        } else {
            value = String.valueOf(adjust * sOffset.get(PATH));
            if (adjust > 0) value = "+" + value;
        }

        run(Control.write(value, PATH), PATH, context);
    }

    public static void setVoltage(String freq, String voltage, Context context) {
        int position = getFreqs().indexOf(freq);
        if (sAppend.get(PATH)) {
            String command = "";
            List<String> voltages = getVoltages();
            for (int i = 0; i < voltages.size(); i++) {
                if (i == position) {
                    command += command.isEmpty() ? voltage : " " + voltage;
                } else {
                    command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
                }
            }
            run(Control.write(command, PATH), PATH, context);
        } else {
            run(Control.write(freq + " " + Utils.strToInt(voltage) * sOffset.get(PATH), PATH), PATH + freq, context);
        }

    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(PATH).replace(" ", "");
        if (!value.isEmpty()) {
            String[] lines = value.split(sSplitNewline.get(PATH));
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(sSplitLine.get(PATH));
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToInt(voltageLine[1].trim()) / sOffset.get(PATH)));
                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getFreqs() {
        if (sFreqs == null) {
            String value = Utils.readFile(PATH).replace(" ", "");
            if (!value.isEmpty()) {
                String[] lines = value.split(sSplitNewline.get(PATH));
                sFreqs = new String[lines.length];
                for (int i = 0; i < sFreqs.length; i++) {
                    sFreqs[i] = lines[i].split(sSplitLine.get(PATH))[0].trim();
                }
            }
        }
        if (sFreqs == null) return null;
        return Arrays.asList(sFreqs);
    }

    public static boolean isVddVoltage() {
        return sVoltages.get(PATH);
    }

    public static void enableOverrideVmin(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CPU_OVERRIDE_VMIN), CPU_OVERRIDE_VMIN, context);
    }

    public static boolean isOverrideVminEnabled() {
        return Utils.readFile(CPU_OVERRIDE_VMIN).equals("1");
    }

    public static boolean hasOverrideVmin() {
        return Utils.existFile(CPU_OVERRIDE_VMIN);
    }

    public static boolean supported() {
        if (PATH != null) return true;
        for (String path : sVoltages.keySet()) {
            if (Utils.existFile(path)) {
                PATH = path;
                return true;
            }
        }
        return false;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_VOLTAGE, id, context);
    }

}
