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
package com.grarak.kerneladiutor.utils.kernel.lmk;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class LMK {

    private static final String MINFREE = "/sys/module/lowmemorykiller/parameters/minfree";
    private static final String ADAPTIVE = "/sys/module/lowmemorykiller/parameters/enable_adaptive_lmk";
    private static final String SWAP_WAIT = "/sys/module/lowmemorykiller/parameters/swap_wait";
    private static final String SWAP_WAIT_PERCENT = "/sys/module/lowmemorykiller/parameters/swap_wait_percent";

    public static void setSwapWaitPercent(int value, Context context) {
        run(Control.write(String.valueOf(value), SWAP_WAIT_PERCENT), SWAP_WAIT_PERCENT, context);
    }

    public static int getSwapWaitPercent() {
        return Utils.strToInt(Utils.readFile(SWAP_WAIT_PERCENT));
    }

    public static boolean hasSwapWaitPercent() {
        return Utils.existFile(SWAP_WAIT_PERCENT);
    }

    public static void enableSwapWait(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SWAP_WAIT), SWAP_WAIT, context);
    }

    public static boolean isSwapWaitEnabled() {
        return Utils.readFile(SWAP_WAIT).equals("Y");
    }

    public static boolean hasSwapWait() {
        return Utils.existFile(SWAP_WAIT);
    }

    public static void setMinFree(String value, Context context) {
        run(Control.chmod("755", MINFREE), MINFREE + "chmod755", context);
        run(Control.write(value, MINFREE), MINFREE, context);
    }

    public static List<String> getMinFrees() {
        RootUtils.chmod(MINFREE, "755");
        String value = Utils.readFile(MINFREE);
        return Arrays.asList(value.split(","));
    }

    public static void enableAdaptive(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", ADAPTIVE), ADAPTIVE, context);
    }

    public static boolean isAdaptiveEnabled() {
        return Utils.readFile(ADAPTIVE).equals("1");
    }

    public static boolean hasAdaptive() {
        return Utils.existFile(ADAPTIVE);
    }

    public static boolean supported() {
        return getMinFrees().size() > 0;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.LMK, id, context);
    }

}
