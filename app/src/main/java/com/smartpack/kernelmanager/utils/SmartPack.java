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

import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 29, 2018
 */

public class SmartPack {

    private static final String SMARTPACK_VERSION = "/version";

    private static final String SMARTPACK_DOWNLOADED = "/sdcard/Download/SmartPack-Kernel.zip";

    private static final String LATEST_VERSION = "/data/data/com.smartpack.kernelmanager/version";

    private static final String RECOVERY = "/cache/recovery/";

    /**
     * Check SmartPack-Kernel is installed
     */
    public static boolean hasSmartPackInstalled() {
        return Utils.readFile("/proc/version").contains("SmartPack-Kernel");
    }

    public static boolean hasSmartPackVersion() {
        return Utils.existFile(SMARTPACK_VERSION);
    }

    /**
     * Check device support
     */
    public static boolean isOnePlusmsm8998() {
        return Device.getFingerprint().contains("OnePlus5");
    }

    /**
     * Get current SmartPack version
     */
    public static String getSmartPackVersion() {
        return Utils.readFile(SMARTPACK_VERSION);
    }

    public static int getSmartPackVersionNumber() {
        return Utils.strToInt(Utils.readFile(SMARTPACK_VERSION).replace("stable", "").replace("beta", "").replace("alpha", "").replace("test", "").replace("-", "").replace("v", ""));
    }

    /**
     * Get latest SmartPack version
     */
    public static String getlatestSmartPackVersion() {
        return Utils.readFile(LATEST_VERSION);
    }

    public static boolean SmartPackRelease() {
        return getlatestSmartPackVersion().contains("alpha-v") || getlatestSmartPackVersion().contains("beta-v") || getlatestSmartPackVersion().contains("stable-v") || getlatestSmartPackVersion().contains("test");
    }

    public static int getlatestSmartPackVersionNumber() {
        return Utils.strToInt(Utils.readFile(LATEST_VERSION).replace("stable", "").replace("beta", "").replace("alpha", "").replace("test", "").replace("-", "").replace("v", ""));
    }

    public static boolean haslatestSmartPackVersion() {
        return Utils.existFile(LATEST_VERSION);
    }

    public static boolean isSmartPackDownloaded() {
        return Utils.existFile(SMARTPACK_DOWNLOADED);
    }

    public static boolean hasRecovery() {
        return Utils.existFile(RECOVERY);
    }

    public static boolean supported() {
        return hasSmartPackVersion() || isOnePlusmsm8998() || hasSmartPackInstalled();
    }
}
