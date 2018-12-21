/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 01, 2018
 *
 * Based on the original implementation by MoroGoku
 */

public class WakeLockInfo {

    public String wName = "";
    public int wTime = 0;
    public int wWakeups = 0;
    public boolean wState = true;

    WakeLockInfo(String name, int time, int wakeups){
        wName = name;
        wTime = time;
        wWakeups = wakeups;
    }
}
