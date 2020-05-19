/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.utils.kernel.cpu.boost;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 19, 2020
 */

public class VoxPopuli {

    private static final String VOXPOPULI = "/dev/voxpopuli";

    private static final String[] TUNABLES = {"enable_interaction_boost", "fling_min_boost_duration",
            "fling_max_boost_duration", "fling_boost_topapp", "fling_min_freq_big", "fling_min_freq_little",
            "touch_boost_duration", "touch_boost_topapp", "touch_min_freq_big", "touch_min_freq_little"};

    public static void setVoxpopuliTunableValue(String value, int position, Context context) {
        run(Control.write(value, VOXPOPULI + "/" + TUNABLES[position]), VOXPOPULI + "/" +
                TUNABLES[position], context);
    }

    public static String getVoxpopuliTunableValue(int position) {
        return Utils.readFile(VOXPOPULI + "/" + TUNABLES[position]);
    }

    public static String getVoxpopuliTunableName(int position) {
        return Utils.upperCaseEachWord(TUNABLES[position]).replace("_", " ");
    }

    public static boolean VoxpopuliTunableexists(int position) {
        return Utils.existFile(VOXPOPULI + "/" + TUNABLES[position]);
    }

    public static int VoxpopuliTunablesize() {
        return TUNABLES.length;
    }

    public static boolean hasVoxpopuliTunable() {
        return Utils.existFile(VOXPOPULI);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.POWERHAL_BOOST, id, context);
    }

}