/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.utils.kernel.gpu;

import android.content.Context;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 08, 2019
 */
public class GPUMisc {

    private static final String KGSL = "/sys/class/kgsl/kgsl-3d0";
    private static final String GPU_POWER_LEVEL = KGSL + "/default_pwrlevel";
    private static final String GPU_THROTTLING = KGSL + "/throttling";

    public static String getgpuPwrLevel() {
        return Utils.readFile(GPU_POWER_LEVEL);
    }

    public static void setgpuPwrLevel(String value, Context context) {
        run(Control.write(String.valueOf(value), GPU_POWER_LEVEL), GPU_POWER_LEVEL, context);
    }

    public static boolean hasgpuPwrLevel() {
        return Utils.existFile(GPU_POWER_LEVEL);
    }

    public static boolean hasGPUThrottling() {
        return Utils.existFile(GPU_THROTTLING);
    }

    public static void enableGPUThrottling(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", GPU_THROTTLING), GPU_THROTTLING, context);
    }

    public static boolean isGPUThrottlingEnabled() {
        return Utils.readFile(GPU_THROTTLING).equals("1");
    }

    public static boolean supported() {
        return hasgpuPwrLevel() || hasGPUThrottling();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}