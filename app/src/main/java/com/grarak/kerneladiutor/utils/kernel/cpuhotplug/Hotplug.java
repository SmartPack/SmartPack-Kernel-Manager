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

/**
 * Created by willi on 07.05.16.
 */
public class Hotplug {

    public static boolean supported() {
        return MPDecision.supported() || IntelliPlug.supported() || LazyPlug.supported()
                || BluPlug.supported() || MSMHotplug.supported() || MakoHotplug.supported()
                || MBHotplug.supported() || AlucardHotplug.supported() || ThunderPlug.supported()
                || ZenDecision.supported() || AutoSmp.supported() || CoreCtl.supported()
                || AiOHotplug.supported();
    }

}
