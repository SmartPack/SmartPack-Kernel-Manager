/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.utils.tools;

import android.os.Environment;
import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.kerneladiutor.library.Tools;
import com.kerneladiutor.library.root.RootUtils;

import java.io.File;

/**
 * Created by willi on 17.05.15.
 */
public class Backup {

    private static String boot;
    private static String recovery;
    private static String fota;

    public enum PARTITION {
        BOOT, RECOVERY, FOTA
    }

    private static final String[] Boot = {
            "/dev/block/bootdevice/by-name/boot",
            "/dev/block/platform/omap/omap_hsmmc.0/by-name/boot",
            "/dev/block/platform/sprd-sdhci.3/by-name/KERNEL",
            "/dev/block/platform/sdhci-tegra.3/by-name/LX",
            "/dev/block/platform/sdhci-tegra.3/by-name/LNX",
            "/dev/block/platform/dw_mmc.0/by-name/BOOT",
            "/dev/block/platform/12200000.dwmmc0/by-name/BOOT",
            "/dev/block/platform/msm_sdcc.1/by-name/Kernel",
            "/dev/block/platform/msm_sdcc.1/by-name/boot",
            "/dev/block/platform/sdhci.1/by-name/KERNEL",
            "/dev/block/platform/sdhci.1/by-name/boot",
            "/dev/block/nandc",
            "/dev/boot"
    };

    private static final String[] Recovery = {
            "/dev/block/bootdevice/by-name/recovery",
            "/dev/block/platform/omap/omap_hsmmc.0/by-name/recovery",
            "/dev/block/platform/omap/omap_hsmmc.1/by-name/recovery",
            "/dev/block/platform/sdhci-tegra.3/by-name/recovery",
            "/dev/block/platform/sdhci-pxav3.2/by-name/RECOVERY",
            "/dev/block/platform/comip-mmc.1/by-name/recovery",
            "/dev/block/platform/msm_sdcc.1/by-name/recovery",
            "/dev/block/platform/sprd-sdhci.3/by-name/KERNEL",
            "/dev/block/platform/sdhci-tegra.3/by-name/SOS",
            "/dev/block/platform/sdhci-tegra.3/by-name/USP",
            "/dev/block/platform/dw_mmc.0/by-name/recovery",
            "/dev/block/platform/dw_mmc.0/by-name/RECOVERY",
            "/dev/block/platform/12200000.dwmmc0/by-name/RECOVERY",
            "/dev/block/platform/hi_mci.1/by-name/recovery",
            "/dev/block/platform/sdhci-tegra.3/by-name/UP",
            "/dev/block/platform/sdhci-tegra.3/by-name/SS",
            "/dev/block/platform/sdhci.1/by-name/RECOVERY",
            "/dev/block/platform/sdhci.1/by-name/recovery",
            "/dev/block/platform/dw_mmc/by-name/recovery",
            "/dev/block/platform/dw_mmc/by-name/RECOVERY",
            "/dev/block/recovery",
            "/dev/block/nandg",
            "/dev/block/acta",
            "/dev/recovery"
    };

    private static final String[] Fota = {
            "/dev/block/platform/msm_sdcc.1/by-name/FOTAKernel"
    };

    public static void restore(File file, PARTITION partition_type) {
        String parentFile = file.getParent();
        String sdcard = Environment.getExternalStorageDirectory().getPath();
        if (parentFile.startsWith(sdcard))
            parentFile = parentFile.replace(sdcard, Tools.getInternalStorage());
        String command = "dd if='" + parentFile + "/" + file.getName() + "' of=" + getPartition(partition_type);
        Log.i(Constants.TAG, "Executing: " + command);
        RootUtils.runCommand(command);
    }

    public static void backup(String name, PARTITION partition_type) {
        if (!name.endsWith(".img")) name += ".img";

        String command = "dd if=" + getPartition(partition_type) + " of='" + getPath(partition_type) + "/" + name + "'";
        Log.i(Constants.TAG, "Executing: " + command);
        RootUtils.runCommand(command);
    }

    public static String getPartition(PARTITION partition_type) {
        switch (partition_type) {
            case BOOT:
                return getBootPartition();
            case RECOVERY:
                return getRecoveryPartition();
            case FOTA:
                return getFotaPartition();
            default:
                return null;
        }
    }

    private static String getPath(PARTITION PARTITION_type) {
        String folder = null;
        switch (PARTITION_type) {
            case BOOT:
                folder = "boot";
                break;
            case RECOVERY:
                folder = "recovery";
                break;
            case FOTA:
                folder = "fota";
                break;
        }
        folder = Tools.getInternalStorage() + "/KernelAdiutor/" + folder;
        if (Utils.existFile(folder)) return folder;
        return "/sdcard/KernelAdiutor/" + folder;
    }

    public static String getBootPartition() {
        if (boot == null)
            for (String partition : Boot)
                if (Utils.existFile(partition)) {
                    boot = partition;
                    return partition;
                }
        return boot;
    }

    public static String getRecoveryPartition() {
        if (recovery == null)
            for (String partition : Recovery)
                if (Utils.existFile(partition)) {
                    recovery = partition;
                    return partition;
                }
        return recovery;
    }

    public static String getFotaPartition() {
        if (fota == null)
            for (String partition : Fota)
                if (Utils.existFile(partition)) {
                    fota = partition;
                    return partition;
                }
        return fota;
    }

    public static boolean hasBackup() {
        return getBootPartition() != null || getRecoveryPartition() != null || getFotaPartition() != null;
    }

}
