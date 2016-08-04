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

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 04.05.16.
 */
public class QcomBcl {

    private static final String PATH = "/sys/devices/soc.0/qcom,bcl.*/";
    private static String PARENT;
    private static String DEFAULT_HOTPLUG_MASK;
    private static String DEFAULT_SOC_HOTPLUG_MASK;

    public static void online(boolean online, Context context) {
        online(online, ApplyOnBootFragment.CPU_HOTPLUG, context);
    }

    public static void online(boolean online, String category, Context context) {
        Control.runSetting(Control.write(online ? "disable" : "enable", PARENT + "/mode"), category,
                PARENT + "/mode" + online, context);
        if (DEFAULT_HOTPLUG_MASK != null) {
            Control.runSetting(Control.write(online ? "0" : DEFAULT_HOTPLUG_MASK, PARENT + "/hotplug_mask"),
                    category, PARENT + "/hotplug_mask" + online, context);
        }
        if (DEFAULT_SOC_HOTPLUG_MASK != null) {
            Control.runSetting(Control.write(online ? "0" : DEFAULT_SOC_HOTPLUG_MASK,
                    PARENT + "/hotplug_soc_mask"), category, PARENT + "/hotplug_soc_mask" + online, context);
        }
        Control.runSetting(Control.write(online ? "enable" : "disable", PARENT + "/mode"), category,
                PARENT + "/mode" + online + "1", context);
    }

    public static boolean supported() {
        if (PARENT != null && !PARENT.isEmpty()) return true;
        if (Utils.existFile(PATH)) {
            PARENT = RootUtils.runCommand("realpath " + PATH);
            if (Utils.existFile(PARENT + "/hotplug_mask")) {
                DEFAULT_HOTPLUG_MASK = Utils.readFile(PARENT + "/hotplug_mask");
            }
            if (Utils.existFile(PARENT + "/hotplug_soc_mask")) {
                DEFAULT_SOC_HOTPLUG_MASK = Utils.readFile(PARENT + "/hotplug_soc_mask");
            }
            return true;
        }
        PARENT = "";
        return false;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
