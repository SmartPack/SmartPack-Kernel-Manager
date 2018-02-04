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

    private static Voltage sIOInstance;

    public static Voltage getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new Voltage();
        }
        return sIOInstance;
    }

    private static final String CPU_OVERRIDE_VMIN = "/sys/devices/system/cpu/cpu0/cpufreq/override_vmin";

    private static final String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    private static final String CPU_VDD_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/vdd_levels";
    private static final String CPU_FAUX_VOLTAGE = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

    private final HashMap<String, Boolean> mVoltages = new HashMap<>();
    private final HashMap<String, Integer> mOffset = new HashMap<>();
    private final HashMap<String, String> mSplitNewline = new HashMap<>();
    private final HashMap<String, String> mSplitLine = new HashMap<>();
    private final HashMap<String, Boolean> mAppend = new HashMap<>();

    {
        mVoltages.put(CPU_VOLTAGE, false);
        mVoltages.put(CPU_VDD_VOLTAGE, true);
        mVoltages.put(CPU_FAUX_VOLTAGE, true);

        mOffset.put(CPU_VOLTAGE, 1);
        mOffset.put(CPU_VDD_VOLTAGE, 1);
        mOffset.put(CPU_FAUX_VOLTAGE, 1000);

        mSplitNewline.put(CPU_VOLTAGE, "mV");
        mSplitNewline.put(CPU_VDD_VOLTAGE, "\\r?\\n");
        mSplitNewline.put(CPU_FAUX_VOLTAGE, "\\r?\\n");

        mSplitLine.put(CPU_VOLTAGE, "mhz:");
        mSplitLine.put(CPU_VDD_VOLTAGE, ":");
        mSplitLine.put(CPU_FAUX_VOLTAGE, ":");

        mAppend.put(CPU_VOLTAGE, true);
        mAppend.put(CPU_VDD_VOLTAGE, false);
        mAppend.put(CPU_FAUX_VOLTAGE, false);
    }

    private String PATH;
    private String[] sFreqs;

    private Voltage() {
        for (String path : mVoltages.keySet()) {
            if (Utils.existFile(path)) {
                PATH = path;
                break;
            }
        }

        if (PATH == null) return;
        String value = Utils.readFile(PATH).replace(" ", "");
        if (!value.isEmpty()) {
            String[] lines = value.split(mSplitNewline.get(PATH));
            sFreqs = new String[lines.length];
            for (int i = 0; i < sFreqs.length; i++) {
                sFreqs[i] = lines[i].split(mSplitLine.get(PATH))[0].trim();
            }
        }
    }

    public void setGlobalOffset(int adjust, Context context) {
        StringBuilder value = new StringBuilder();
        List<String> voltages = getVoltages();
        if (voltages == null) return;
        if (mAppend.get(PATH)) {
            for (String volt : voltages) {
                if (value.length() > 0) {
                    value.append(" ");
                }
                value.append(String.valueOf(Utils.strToInt(volt) + adjust));
            }
        } else {
            value = new StringBuilder(String.valueOf(adjust * mOffset.get(PATH)));
            if (adjust > 0) value.insert(0, "+");
        }

        run(Control.write(value.toString(), PATH), PATH, context);
    }

    public void setVoltage(String freq, String voltage, Context context) {
        int position = getFreqs().indexOf(freq);
        if (mAppend.get(PATH)) {
            StringBuilder command = new StringBuilder();
            List<String> voltages = getVoltages();
            for (int i = 0; i < voltages.size(); i++) {
                if (i == position) {
                    command.append((command.length() == 0) ? voltage : " " + voltage);
                } else {
                    command.append((command.length() == 0) ? voltages.get(i) : " " + voltages.get(i));
                }
            }
            run(Control.write(command.toString(), PATH), PATH, context);
        } else {
            run(Control.write(freq + " " + Utils.strToInt(voltage) * mOffset.get(PATH), PATH), PATH + freq, context);
        }

    }

    public List<String> getVoltages() {
        String value = Utils.readFile(PATH).replace(" ", "");
        if (!value.isEmpty()) {
            String[] lines = value.split(mSplitNewline.get(PATH));
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(mSplitLine.get(PATH));
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToInt(voltageLine[1].trim()) / mOffset.get(PATH)));
                }
            }
            return voltages;
        }
        return null;
    }

    public List<String> getFreqs() {
        if (sFreqs == null) return null;
        return Arrays.asList(sFreqs);
    }

    public boolean isVddVoltage() {
        return mVoltages.get(PATH);
    }

    public void enableOverrideVmin(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CPU_OVERRIDE_VMIN), CPU_OVERRIDE_VMIN, context);
    }

    public boolean isOverrideVminEnabled() {
        return Utils.readFile(CPU_OVERRIDE_VMIN).equals("1");
    }

    public boolean hasOverrideVmin() {
        return Utils.existFile(CPU_OVERRIDE_VMIN);
    }

    public boolean supported() {
        return PATH != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_VOLTAGE, id, context);
    }

}
