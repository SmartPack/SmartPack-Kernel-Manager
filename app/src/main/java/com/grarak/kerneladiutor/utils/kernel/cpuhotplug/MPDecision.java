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
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 07.05.16.
 */
public class MPDecision {

    public static final String HOTPLUG_MPDEC = "mpdecision";

    public static void enableMpdecision(boolean enable, Context context) {
        if (enable) {
            run(Control.startService(HOTPLUG_MPDEC), HOTPLUG_MPDEC, context);
        } else {
            CPUFreq cpuFreq = CPUFreq.getInstance(context);
            run(Control.stopService(HOTPLUG_MPDEC), HOTPLUG_MPDEC, context);
            for (int i = 0; i < cpuFreq.getCpuCount(); i++) {
                cpuFreq.onlineCpu(i, true, ApplyOnBootFragment.CPU_HOTPLUG, false, context);
            }
        }
    }

    public static boolean isMpdecisionEnabled() {
        return Utils.isPropRunning(HOTPLUG_MPDEC);
    }

    public static boolean supported() {
        return Utils.hasProp(HOTPLUG_MPDEC);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
