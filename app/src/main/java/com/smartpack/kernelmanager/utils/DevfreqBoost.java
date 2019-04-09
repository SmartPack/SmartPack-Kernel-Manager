/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
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
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 26, 2019
 */

public class DevfreqBoost {

    private static final String DEVFREQ_BOOST = "/sys/module/devfreq_boost/parameters";

    private static final String DEVFREQ_BOOST_DURATION = DEVFREQ_BOOST + "/input_boost_duration";
    private static final String WAKE_BOOST_DURATION = DEVFREQ_BOOST + "/wake_boost_duration";
    private static final String DEVFREQ_BOOST_FREQ = DEVFREQ_BOOST + "/msm_cpubw_boost_freq";

    public static void setDevfreqboostDuration(String value, Context context) {
        run(Control.write(String.valueOf(value), DEVFREQ_BOOST_DURATION), DEVFREQ_BOOST_DURATION, context);
    }

    public static String getDevfreqboostDuration() {
        return Utils.readFile(DEVFREQ_BOOST_DURATION);
    }

    public static boolean hasDevfreqboostDuration() {
        return Utils.existFile(DEVFREQ_BOOST_DURATION);
    }

    public static void setDevfreqboostFreq(String value, Context context) {
        run(Control.write(String.valueOf(value), DEVFREQ_BOOST_FREQ), DEVFREQ_BOOST_FREQ, context);
    }

    public static String getDevfreqboostFreq() {
        return Utils.readFile(DEVFREQ_BOOST_FREQ);
    }

    public static boolean hasDevfreqboostFreq() {
        return Utils.existFile(DEVFREQ_BOOST_FREQ);
    }

    public static void setwakeboostduration(String value, Context context) {
        run(Control.write(String.valueOf(value), WAKE_BOOST_DURATION), WAKE_BOOST_DURATION, context);
    }

    public static String getwakeboostduration() {
        return Utils.readFile(WAKE_BOOST_DURATION);
    }

    public static boolean haswakeboostduration() {
        return Utils.existFile(WAKE_BOOST_DURATION);
    }

    public static boolean supported() {
        return Utils.existFile(DEVFREQ_BOOST);
    }

    private static void run(String command, String id, Context context) {
	Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
