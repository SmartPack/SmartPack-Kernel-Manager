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

package com.smartpack.kernelmanager.utils;

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

    private static CPUInputBoost sInstance;

    public static CPUInputBoost getInstance() {
        if (sInstance == null) {
            sInstance = new CPUInputBoost();
        }
        return sInstance;
    }

    private static final String CPU_INPUT_BOOST = "/sys/kernel/cpu_input_boost";
    private static final String CPU_INPUT_BOOST_MODULE = "/sys/module/cpu_input_boost/parameters";

    private static final String CPU_INPUT_BOOST_ENABLED = "/enabled";
    private static final String CPU_INPUT_BOOST_DURATION = "/ib_duration_ms";
    private static final String CPU_INPUT_BOOST_FREQ = "/ib_freqs";
    private static final String CPU_INPUT_BOOST_MODULE_DURATION = "/input_boost_duration";
    private static final String CPU_INPUT_BOOST_MODULE_DURATION_MS = "/input_boost_duration_ms";
    private static final String WAKE_BOOST_DURATION = "/wake_boost_duration";
    private static final String CPU_INPUT_BOOST_LF = "/input_boost_freq_lp";
    private static final String CPU_INPUT_BOOST_HF = "/input_boost_freq_hp";
    private static final String INPUT_BOOST_FREQ = "/input_boost_freq";
    private static final String REMOVE_INPUT_BOOST_LF = "/remove_input_boost_freq_lp";
    private static final String REMOVE_INPUT_BOOST_HF = "/remove_input_boost_freq_hp";

    private static final String DYN_STUNE_BOOST = "/dynamic_stune_boost";

    private String PARANT;
    private String INPUT_BOOST_DURATION;

    private CPUInputBoost() {
        if (Utils.existFile(CPU_INPUT_BOOST)) {
            PARANT = CPU_INPUT_BOOST;
        } else if (Utils.existFile(CPU_INPUT_BOOST_MODULE)) {
            PARANT = CPU_INPUT_BOOST_MODULE;
        }
        if (Utils.existFile(PARANT + CPU_INPUT_BOOST_DURATION)) {
            INPUT_BOOST_DURATION = PARANT + CPU_INPUT_BOOST_DURATION;
        } else if (Utils.existFile(PARANT + CPU_INPUT_BOOST_MODULE_DURATION)) {
            INPUT_BOOST_DURATION = PARANT + CPU_INPUT_BOOST_MODULE_DURATION;
        } else if (Utils.existFile(PARANT + CPU_INPUT_BOOST_MODULE_DURATION_MS)) {
            INPUT_BOOST_DURATION = PARANT + CPU_INPUT_BOOST_MODULE_DURATION_MS;
        }
    }


    public void enablecpuinputboost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARANT + CPU_INPUT_BOOST_ENABLED), PARANT + CPU_INPUT_BOOST_ENABLED, context);
    }

    public boolean iscpuinputboostEnabled() {
        return Utils.readFile(PARANT + CPU_INPUT_BOOST_ENABLED).equals("1");
    }

    public boolean hascpuinputboost() {
        return Utils.existFile(PARANT + CPU_INPUT_BOOST_ENABLED);
    }

    public void setcpuiboostduration(String value, Context context) {
        run(Control.write(String.valueOf(value), INPUT_BOOST_DURATION), INPUT_BOOST_DURATION, context);
    }

    public String getcpuiboostduration() {
        return Utils.readFile(INPUT_BOOST_DURATION);
    }

    public boolean hascpuiboostduration() {
        return INPUT_BOOST_DURATION != null;
    }

    public void setwakeboostduration(String value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + WAKE_BOOST_DURATION), PARANT + WAKE_BOOST_DURATION, context);
    }

    public String getwakeboostduration() {
        return Utils.readFile(PARANT + WAKE_BOOST_DURATION);
    }

    public boolean haswakeboostduration() {
        return Utils.existFile(PARANT + WAKE_BOOST_DURATION);
    }

    public void setcpuiboostfreq(String value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + CPU_INPUT_BOOST_FREQ), PARANT + CPU_INPUT_BOOST_FREQ, context);
    }

    public String getcpuiboostfreq() {
        return Utils.readFile(PARANT + CPU_INPUT_BOOST_FREQ);
    }

    public boolean hascpuiboostfreq() {
        return Utils.existFile(PARANT + CPU_INPUT_BOOST_FREQ);
    }

    public int getcpuinputboostlf() {
        return Utils.strToInt(Utils.readFile(PARANT + CPU_INPUT_BOOST_LF));
    }

    public void setcpuinputboostlf(int value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + CPU_INPUT_BOOST_LF), PARANT + CPU_INPUT_BOOST_LF, context);
    }

    public boolean hascpuinputboostlf() {
        return Utils.existFile(PARANT + CPU_INPUT_BOOST_LF);
    }

    public int getcpuinputboosthf() {
        return Utils.strToInt(Utils.readFile(PARANT + CPU_INPUT_BOOST_HF));
    }

    public void setcpuinputboosthf(int value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + CPU_INPUT_BOOST_HF), PARANT + CPU_INPUT_BOOST_HF, context);
    }

    public boolean hascpuinputboosthf() {
        return Utils.existFile(PARANT + CPU_INPUT_BOOST_HF);
    }

    public int getremoveinputboostlf() {
        return Utils.strToInt(Utils.readFile(PARANT + REMOVE_INPUT_BOOST_LF));
    }

    public void setremoveinputboostlf(int value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + REMOVE_INPUT_BOOST_LF), PARANT + REMOVE_INPUT_BOOST_LF, context);
    }

    public boolean hasremoveinputboostlf() {
        return Utils.existFile(PARANT + REMOVE_INPUT_BOOST_LF);
    }

    public int getremoveinputboosthf() {
        return Utils.strToInt(Utils.readFile(PARANT + REMOVE_INPUT_BOOST_HF));
    }

    public void setremoveinputboosthf(int value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + REMOVE_INPUT_BOOST_HF), PARANT + REMOVE_INPUT_BOOST_HF, context);
    }

    public boolean hasremoveinputboosthf() {
        return Utils.existFile(PARANT + REMOVE_INPUT_BOOST_HF);
    }

    public int getinputboostFreq() {
        return Utils.strToInt(Utils.readFile(PARANT + INPUT_BOOST_FREQ));
    }

    public void setinputboostFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + INPUT_BOOST_FREQ), PARANT + INPUT_BOOST_FREQ, context);
    }

    public boolean hasinputboostFreq() {
        return Utils.existFile(PARANT + INPUT_BOOST_FREQ);
    }

    public void setDynStuneBoost(int value, Context context) {
        run(Control.write(String.valueOf(value), PARANT + DYN_STUNE_BOOST), PARANT + DYN_STUNE_BOOST, context);
    }

    public int getDynStuneBoost() {
        return Utils.strToInt(Utils.readFile(PARANT + DYN_STUNE_BOOST));
    }

    public boolean hasDynStuneBoost() {
        return Utils.existFile(PARANT + DYN_STUNE_BOOST);
    }

    public boolean supported() {
        return PARANT != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
