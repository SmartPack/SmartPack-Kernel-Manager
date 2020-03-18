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
package com.smartpack.kernelmanager.utils.kernel.cpuhotplug;

import com.smartpack.kernelmanager.utils.kernel.cpu.MSMSleeper;

/**
 * Created by willi on 07.05.16.
 */
public class Hotplug {

    public static boolean supported() {
        return MPDecision.supported() || IntelliPlug.getInstance().supported() || LazyPlug.supported()
		|| BluPlug.supported() || MakoHotplug.supported() || MSMHotplug.getInstance().supported()
		|| AlucardHotplug.supported() || CoreCtl.getInstance().supported() || AutoSMP.supported()
		|| MSMSleeper.supported() || MBHotplug.getInstance().supported() || AiOHotplug.supported();
    }

}
