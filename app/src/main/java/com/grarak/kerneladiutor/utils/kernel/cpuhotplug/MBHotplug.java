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
package com.grarak.kerneladiutor.utils.kernel.cpuhotplug;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 09.05.16.
 */
public class MBHotplug {

    private static MBHotplug sInstance;

    public static MBHotplug getInstance() {
        if (sInstance == null) {
            sInstance = new MBHotplug();
        }
        return sInstance;
    }

    private static final String MSM_MPDECISION_HOTPLUG = "/sys/kernel/msm_mpdecision/conf";
    private static final String BRICKED_HOTPLUG = "/sys/kernel/bricked_hotplug/conf";
    private static final String MB_ENABLED = "enabled";
    private static final String MB_SCROFF_SINGLE_CORE = "scroff_single_core";
    private static final String MB_MIN_CPUS = "min_cpus";
    private static final String MB_MAX_CPUS = "max_cpus";
    private static final String MB_MIN_CPUS_ONLINE = "min_cpus_online";
    private static final String MB_MAX_CPUS_ONLINE = "max_cpus_online";
    private static final String MB_CPUS_ONLINE_SUSP = "max_cpus_online_susp";
    private static final String MB_IDLE_FREQ = "idle_freq";
    private static final String MB_BOOST_ENABLED = "boost_enabled";
    private static final String MB_BOOST_TIME = "boost_time";
    private static final String MB_CPUS_BOOSTED = "cpus_boosted";
    private static final String MB_BOOST_FREQS = "boost_freqs";
    private static final String MB_STARTDELAY = "startdelay";
    private static final String MB_DELAY = "delay";
    private static final String MB_PAUSE = "pause";

    private String PARENT;
    private String MIN_CPUS_FILE;
    private String MAX_CPUS_FILE;

    private MBHotplug() {
        if (Utils.existFile(MSM_MPDECISION_HOTPLUG)) PARENT = MSM_MPDECISION_HOTPLUG;
        else if (Utils.existFile(BRICKED_HOTPLUG)) PARENT = BRICKED_HOTPLUG;

        if (PARENT == null) return;

        if (Utils.existFile(PARENT + "/" + MB_MIN_CPUS))
            MIN_CPUS_FILE = PARENT + "/" + MB_MIN_CPUS;
        else if (Utils.existFile(PARENT + "/" + MB_MIN_CPUS_ONLINE))
            MIN_CPUS_FILE = PARENT + "/" + MB_MIN_CPUS_ONLINE;

        if (Utils.existFile(PARENT + "/" + MB_MAX_CPUS))
            MAX_CPUS_FILE = PARENT + "/" + MB_MAX_CPUS;
        else if (Utils.existFile(PARENT + "/" + MB_MAX_CPUS_ONLINE))
            MAX_CPUS_FILE = PARENT + "/" + MB_MAX_CPUS_ONLINE;
    }

    public void setMBHotplugPause(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_PAUSE), PARENT + "/" + MB_PAUSE, context);
    }

    public int getMBHotplugPause() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_PAUSE));
    }

    public boolean hasMBHotplugPause() {
        return Utils.existFile(PARENT + "/" + MB_PAUSE);
    }

    public void setMBHotplugDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_DELAY), PARENT + "/" + MB_DELAY, context);
    }

    public int getMBHotplugDelay() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_DELAY));
    }

    public boolean hasMBHotplugDelay() {
        return Utils.existFile(PARENT + "/" + MB_DELAY);
    }

    public void setMBHotplugStartDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_STARTDELAY),
                PARENT + "/" + MB_STARTDELAY, context);
    }

    public int getMBHotplugStartDelay() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_STARTDELAY));
    }

    public boolean hasMBHotplugStartDelay() {
        return Utils.existFile(PARENT + "/" + MB_STARTDELAY);
    }

    public void setMBHotplugBoostFreqs(int core, int value, Context context) {
        run(Control.write(core + " " + value, PARENT + "/" + MB_BOOST_FREQS),
                PARENT + "/" + MB_BOOST_FREQS, context);
    }

    public List<Integer> getMBHotplugBoostFreqs() {
        List<Integer> list = new ArrayList<>();
        for (String freq : Utils.readFile(PARENT + "/" + MB_BOOST_FREQS).split(" "))
            list.add(Utils.strToInt(freq));
        return list;
    }

    public boolean hasMBHotplugBoostFreqs() {
        return Utils.existFile(PARENT + "/" + MB_BOOST_FREQS);
    }

    public void setMBHotplugCpusBoosted(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_CPUS_BOOSTED),
                PARENT + "/" + MB_CPUS_BOOSTED, context);
    }

    public int getMBHotplugCpusBoosted() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_CPUS_BOOSTED));
    }

    public boolean hasMBHotplugCpusBoosted() {
        return Utils.existFile(PARENT + "/" + MB_CPUS_BOOSTED);
    }

    public void setMBHotplugBoostTime(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_BOOST_TIME),
                PARENT + "/" + MB_BOOST_TIME, context);
    }

    public int getMBHotplugBoostTime() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_BOOST_TIME));
    }

    public boolean hasMBHotplugBoostTime() {
        return Utils.existFile(PARENT + "/" + MB_BOOST_TIME);
    }

    public void enableMBHotplugBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARENT + "/" + MB_BOOST_ENABLED),
                PARENT + "/" + MB_BOOST_ENABLED, context);
    }

    public boolean isMBHotplugBoostEnabled() {
        return Utils.readFile(PARENT + "/" + MB_BOOST_ENABLED).equals("1");
    }

    public boolean hasMBHotplugBoostEnable() {
        return Utils.existFile(PARENT + "/" + MB_BOOST_ENABLED);
    }

    public void setMBHotplugIdleFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_IDLE_FREQ),
                PARENT + "/" + MB_IDLE_FREQ, context);
    }

    public int getMBHotplugIdleFreq() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_IDLE_FREQ));
    }

    public boolean hasMBHotplugIdleFreq() {
        return Utils.existFile(PARENT + "/" + MB_IDLE_FREQ);
    }

    public void setMBHotplugMaxCpusOnlineSusp(int value, Context context) {
        run(Control.write(String.valueOf(value), PARENT + "/" + MB_CPUS_ONLINE_SUSP),
                PARENT + "/" + MB_CPUS_ONLINE_SUSP, context);
    }

    public int getMBHotplugMaxCpusOnlineSusp() {
        return Utils.strToInt(Utils.readFile(PARENT + "/" + MB_CPUS_ONLINE_SUSP));
    }

    public boolean hasMBHotplugMaxCpusOnlineSusp() {
        return Utils.existFile(PARENT + "/" + MB_CPUS_ONLINE_SUSP);
    }

    public void setMBHotplugMaxCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_CPUS_FILE), MAX_CPUS_FILE, context);
    }

    public int getMBHotplugMaxCpus() {
        return Utils.strToInt(Utils.readFile(MAX_CPUS_FILE));
    }

    public boolean hasMBHotplugMaxCpus() {
        return MAX_CPUS_FILE != null;
    }

    public void setMBHotplugMinCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), MIN_CPUS_FILE), MIN_CPUS_FILE, context);
    }

    public int getMBHotplugMinCpus() {
        return Utils.strToInt(Utils.readFile(MIN_CPUS_FILE));
    }

    public boolean hasMBHotplugMinCpus() {
        return MIN_CPUS_FILE != null;
    }

    public void enableMBHotplugScroffSingleCore(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARENT + "/" + MB_SCROFF_SINGLE_CORE),
                PARENT + "/" + MB_SCROFF_SINGLE_CORE, context);
    }

    public boolean isMBHotplugScroffSingleCoreEnabled() {
        return Utils.readFile(PARENT + "/" + MB_SCROFF_SINGLE_CORE).equals("1");
    }

    public boolean hasMBHotplugScroffSingleCore() {
        return Utils.existFile(PARENT + "/" + MB_SCROFF_SINGLE_CORE);
    }

    public void enableMBHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", PARENT + "/" + MB_ENABLED), PARENT + "/" + MB_ENABLED, context);
    }

    public boolean isMBHotplugEnabled() {
        return Utils.readFile(PARENT + "/" + MB_ENABLED).equals("1");
    }

    public boolean hasMBGHotplugEnable() {
        return Utils.existFile(PARENT + "/" + MB_ENABLED);
    }

    public String getMBName(Context context) {
        switch (PARENT) {
            case MSM_MPDECISION_HOTPLUG:
                return context.getString(R.string.msm_mpdecision_hotplug);
            case BRICKED_HOTPLUG:
                return context.getString(R.string.bricked_hotplug);
            default:
                return null;
        }
    }

    public boolean supported() {
        return PARENT != null;
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
