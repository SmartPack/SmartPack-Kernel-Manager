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
package com.grarak.kerneladiutor.utils.tools;

import android.os.Environment;
import android.util.Log;

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.io.File;

/**
 * Created by willi on 09.07.16.
 */
public class Backup {

    private static final String TAG = Backup.class.getSimpleName();

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
            "/dev/bootimg",
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
        String command = "dd if='" + file.toString() + "' of=" + getPartition(partition_type);
        Log.i(TAG, "Executing: " + command);
        RootUtils.runCommand(command);
    }

    public static void backup(String name, PARTITION partition_type) {
        String command = "dd if=" + getPartition(partition_type) + " of='" + getPath(partition_type) + "/" + name + "'";
        Log.i(TAG, "Executing: " + command);
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

    public static String getPath(PARTITION PARTITION_type) {
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
        File file = new File(Utils.getInternalDataStorage() + "/backup/" + folder);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
        if (Utils.existFile(file.toString())) return file.toString();
        return Utils.getInternalDataStorage().replace(
                Environment.getExternalStorageDirectory().toString(), "/sdcard") + "/backup/" + folder;
    }

    public static String getBootPartition() {
        if (boot == null) {
            for (String partition : Boot) {
                if (Utils.existFile(partition)) {
                    boot = partition;
                    return partition;
                }
            }
        }
        return boot;
    }

    public static String getRecoveryPartition() {
        if (recovery == null) {
            for (String partition : Recovery) {
                if (Utils.existFile(partition)) {
                    recovery = partition;
                    return partition;
                }
            }
        }
        return recovery;
    }

    public static String getFotaPartition() {
        if (fota == null) {
            for (String partition : Fota) {
                if (Utils.existFile(partition)) {
                    fota = partition;
                    return partition;
                }
            }
        }
        return fota;
    }

    public static boolean hasBackup() {
        return getBootPartition() != null || getRecoveryPartition() != null || getFotaPartition() != null;
    }

}
