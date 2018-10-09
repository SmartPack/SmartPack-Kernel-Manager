/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.grarak.kerneladiutor.utils.kernel.cpu;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 15, 2018
 */

public class CPUInputBoost {

    private static final String CPU_INPUT_BOOST = "/sys/kernel/cpu_input_boost";
    private static final String CPU_INPUT_BOOST_ENABLED = CPU_INPUT_BOOST + "/enabled";
    private static final String CPU_INPUT_BOOST_DURATION = CPU_INPUT_BOOST + "/ib_duration_ms";
    private static final String CPU_INPUT_BOOST_FREQ = CPU_INPUT_BOOST + "/ib_freqs";

    private static final String CPU_INPUT_BOOST_MODULE = "/sys/module/cpu_input_boost/parameters";
    private static final String CPU_INPUT_BOOST_MODULE_DURATION = CPU_INPUT_BOOST_MODULE + "/input_boost_duration";
    private static final String CPU_INPUT_BOOST_LF = CPU_INPUT_BOOST_MODULE + "/input_boost_freq_lp";
    private static final String CPU_INPUT_BOOST_HF = CPU_INPUT_BOOST_MODULE + "/input_boost_freq_hp";

    public static void enablecpuinputboost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CPU_INPUT_BOOST_ENABLED), CPU_INPUT_BOOST_ENABLED, context);
    }

    public static boolean iscpuinputboostEnabled() {
        return Utils.readFile(CPU_INPUT_BOOST_ENABLED).equals("1");
    }

    public static boolean hascpuinputboost() {
        return Utils.existFile(CPU_INPUT_BOOST_ENABLED);
    }

    public static void setcpuiboostduration(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_INPUT_BOOST_DURATION), CPU_INPUT_BOOST_DURATION, context);
    }

    public static int getcpuiboostduration() {
        return Utils.strToInt(Utils.readFile(CPU_INPUT_BOOST_DURATION));
    }

    public static boolean hascpuiboostduration() {
        return Utils.existFile(CPU_INPUT_BOOST_DURATION);
    }

    public static void setcpuiboostfreq(String value1, String value2, Context context) {
        String value = value1 + " " + value2;
        run(Control.write(String.valueOf(value), CPU_INPUT_BOOST_FREQ), CPU_INPUT_BOOST_FREQ, context);
    }

    public static List<String> getcpuiboostfreq() {
        String freqs[] = Utils.readFile(CPU_INPUT_BOOST_FREQ).split(" ");
        List<String> ibfreqs = new ArrayList<>();
        for (String freq : freqs) {
            ibfreqs.add(freq.trim());
        }
        return ibfreqs;
    }

    public static boolean hascpuiboostfreq() {
        return Utils.existFile(CPU_INPUT_BOOST_FREQ);
    }

    public static String getcpuinputboostduration() {
        return Utils.readFile(CPU_INPUT_BOOST_MODULE_DURATION);
    }

    public static void setcpuinputboostduration(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_INPUT_BOOST_MODULE_DURATION), CPU_INPUT_BOOST_MODULE_DURATION, context);
    }

    public static boolean hascpuinputboostduration() {
        return Utils.existFile(CPU_INPUT_BOOST_MODULE_DURATION);
    }

    public static String getcpuinputboostlf() {
        return Utils.readFile(CPU_INPUT_BOOST_LF);
    }

    public static void setcpuinputboostlf(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_INPUT_BOOST_LF), CPU_INPUT_BOOST_LF, context);
    }

    public static boolean hascpuinputboostlf() {
        return Utils.existFile(CPU_INPUT_BOOST_LF);
    }

    public static String getcpuinputboosthf() {
        return Utils.readFile(CPU_INPUT_BOOST_HF);
    }

    public static void setcpuinputboosthf(String value, Context context) {
        run(Control.write(String.valueOf(value), CPU_INPUT_BOOST_HF), CPU_INPUT_BOOST_HF, context);
    }

    public static boolean hascpuinputboosthf() {
        return Utils.existFile(CPU_INPUT_BOOST_HF);
    }

    public static boolean supported() {
        return Utils.existFile(CPU_INPUT_BOOST) || Utils.existFile(CPU_INPUT_BOOST_MODULE);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
