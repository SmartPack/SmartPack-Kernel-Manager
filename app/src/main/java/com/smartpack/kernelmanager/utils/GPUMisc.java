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

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 08, 2019
 */

public class GPUMisc {

    private static final String GPU_POWER_LEVEL = "/sys/class/kgsl/kgsl-3d0/default_pwrlevel";

    public static String getgpuPwrLevel() {
        return Utils.readFile(GPU_POWER_LEVEL);
    }

    public static void setgpuPwrLevel(String value, Context context) {
        run(Control.write(String.valueOf(value), GPU_POWER_LEVEL), GPU_POWER_LEVEL, context);
    }

    public static boolean hasgpuPwrLevel() {
        return Utils.existFile(GPU_POWER_LEVEL);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }
}