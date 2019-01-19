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
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 29, 2018
 */

public class MSMLimiter {

    private static final String MSM_LIMITER = "/sys/kernel/msm_limiter";
    private static final String MSM_LIMITER_ENABLE = MSM_LIMITER + "/limiter_enabled";
    private static final String MSM_LIMITER_FREQ_CONTROL = MSM_LIMITER + "/freq_control";
    private static final String DEBUG_MASK = MSM_LIMITER + "/debug_mask";

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MSM_LIMITER_ENABLE), MSM_LIMITER_ENABLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(MSM_LIMITER_ENABLE).equals("1");
    }

    public static boolean hasenable() {
        return Utils.existFile(MSM_LIMITER_ENABLE);
    }

    public static void enableFreqControl(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", MSM_LIMITER_FREQ_CONTROL), MSM_LIMITER_FREQ_CONTROL, context);
    }

    public static boolean isFreqControlEnabled() {
        return Utils.readFile(MSM_LIMITER_FREQ_CONTROL).equals("1");
    }

    public static boolean hasFreqControl() {
        return Utils.existFile(MSM_LIMITER_FREQ_CONTROL);
    }

    public static void enableDebugMask(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", DEBUG_MASK), DEBUG_MASK, context);
    }

    public static boolean isDebugMaskEnabled() {
        return Utils.readFile(DEBUG_MASK).equals("1");
    }

    public static boolean hasDebugMask() {
        return Utils.existFile(DEBUG_MASK);
    }

    public static boolean supported() {
        return MSM_LIMITER != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
