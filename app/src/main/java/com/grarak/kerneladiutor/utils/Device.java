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
package com.grarak.kerneladiutor.utils;

import android.os.Build;

import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 31.12.15.
 */
public class Device {

    public static class Input {

        private static final String BUS_INPUT = "/proc/bus/input/devices";
        private static List<Item> mItems;

        public static List<Item> getItems() {
            if (mItems != null) {
                return mItems;
            }

            String value = Utils.readFile(BUS_INPUT);
            mItems = new ArrayList<>();
            List<String> input = new ArrayList<>();
            for (String line : value.split("\\r?\\n")) {
                if (line.isEmpty()) {
                    mItems.add(new Item(input));
                    input = new ArrayList<>();
                } else {
                    input.add(line);
                }
            }

            return mItems;
        }

        public static void supported() {
            getItems();
        }

        public static class Item {

            private String mBus;
            private String mVendor;
            private String mProduct;
            private String mVersion;
            private String mName;
            private String mSysfs;
            private String mHandlers;

            public Item(List<String> input) {
                for (String line : input) {
                    if (line.startsWith("I:")) {
                        line = line.replace("I:", "").trim();
                        try {
                            mBus = line.split("Bus=")[1].split(" ")[0];
                        } catch (Exception ignored) {
                        }
                        try {
                            mVendor = line.split("Vendor=")[1].split(" ")[0];
                        } catch (Exception ignored) {
                        }
                        try {
                            mProduct = line.split("Product=")[1].split(" ")[0];
                        } catch (Exception ignored) {
                        }
                        try {
                            mVersion = line.split("Version=")[1].split(" ")[0];
                        } catch (Exception ignored) {
                        }
                    } else if (line.startsWith("N:")) {
                        mName = line.replace("N:", "").trim().replace("Name=", "").replace("\"", "");
                    } else if (line.startsWith("S:")) {
                        mSysfs = line.replace("S:", "").trim().replace("Sysfs=", "").replace("\"", "");
                    } else if (line.startsWith("H:")) {
                        mHandlers = line.replace("H:", "").trim().replace("Handlers=", "").replace("\"", "");
                    }
                }
            }

            public String getBus() {
                return mBus;
            }

            public String getVendor() {
                return mVendor;
            }

            public String getProduct() {
                return mProduct;
            }

            public String getVersion() {
                return mVersion;
            }

            public String getName() {
                return mName;
            }

            public String getSysfs() {
                return mSysfs;
            }

            public String getHandlers() {
                return mHandlers;
            }

        }

    }

    public static class ROMInfo {

        private static String[] sProps = {
                "ro.cm.version",
                "ro.pa.version",
                "ro.pac.version",
                "ro.carbon.version",
                "ro.slim.version",
                "ro.mod.version",
        };

        private static String ROM_VERSION;

        public static String getVersion() {
            return ROM_VERSION;
        }

        public static void load() {
            for (String prop : sProps) {
                ROM_VERSION = RootUtils.getProp(prop);
                if (!ROM_VERSION.isEmpty()) {
                    break;
                }
            }
        }

    }

    public static class MemInfo {

        private static final String MEMINFO_PROC = "/proc/meminfo";

        private static String MEMINFO;

        public static long getTotalMem() {
            return getSize("MemTotal") / 1024L;
        }

        private static long getSize(String prefix) {
            try {
                for (String line : MEMINFO.split("\\r?\\n")) {
                    if (line.startsWith(prefix)) {
                        return Long.parseLong(line.split("\\s+")[1]);
                    }
                }
            } catch (Exception ignored) {
            }
            return -1;
        }

        public static void load() {
            MEMINFO = Utils.readFile(MEMINFO_PROC);
        }

    }

    public static class CPUInfo {

        private static final String CPUINFO_PROC = "/proc/cpuinfo";

        private static String CPUINFO;

        public static String getFeatures() {
            String features = getString("Features");
            if (!features.isEmpty()) return features;
            return getString("flags");
        }

        public static String getProcessor() {
            String pro = getString("Processor");
            if (!pro.isEmpty()) return pro;
            return getString("model name");
        }

        public static String getVendor() {
            String vendor = getString("Hardware");
            if (!vendor.isEmpty()) return vendor;
            return getString("vendor_id");
        }

        private static String getString(String prefix) {
            if (CPUINFO == null) {
                load();
            }
            try {
                for (String line : CPUINFO.split("\\r?\\n")) {
                    if (line.startsWith(prefix)) {
                        return line.split(":")[1].trim();
                    }
                }
            } catch (Exception ignored) {
            }
            return "";
        }

        public static void load() {
            CPUINFO = Utils.readFile(CPUINFO_PROC);
        }

    }

    public static class TrustZone {
        private static HashMap<String, String> PARTITIONS = new HashMap<>();

        static {
            PARTITIONS.put("/dev/block/platform/msm_sdcc.1/by-name/tz", "QC_IMAGE_VERSION_STRING=");
            PARTITIONS.put("/dev/block/bootdevice/by-name/tz", "QC_IMAGE_VERSION_STRING=");
        }

        private static String PARTITION;
        private static String VERSION;

        public static String getVersion() {
            if (PARTITION == null) {
                supported();
                if (PARTITION == null) return "";
            }
            if (VERSION != null) return VERSION;
            String raw = RootUtils.runCommand("strings " + PARTITION + " | grep "
                    + PARTITIONS.get(PARTITION));
            for (String line : raw.split("\\r?\\n")) {
                if (line.startsWith(PARTITIONS.get(PARTITION))) {
                    return VERSION = line.replace(PARTITIONS.get(PARTITION), "");
                }
            }
            return "";
        }

        public static boolean supported() {
            if (PARTITION != null) return true;
            for (String partition : PARTITIONS.keySet()) {
                if (Utils.existFile(partition)) {
                    PARTITION = partition;
                    getVersion();
                    return true;
                }
            }
            return false;
        }
    }

    public static String getKernelVersion(boolean extended) {
        return extended ? Utils.readFile("/proc/version") : RootUtils.runCommand("uname -r");
    }

    public static String getArchitecture() {
        return RootUtils.runCommand("uname -m");
    }

    public static String getHardware() {
        return Build.HARDWARE;
    }

    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    public static String getBaseBand() {
        return Build.getRadioVersion();
    }

    public static String getCodename() {
        String codeName = "";
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException ignored) {
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                codeName = fieldName;
                break;
            }
        }
        return codeName;
    }

    public static int getSDK() {
        return Build.VERSION.SDK_INT;
    }

    public static String getBoard() {
        String hardware = CPUInfo.getVendor().toLowerCase();
        if (hardware.matches(".*msm\\d.*")) {
            String board = hardware.split("msm")[1].trim().replaceAll("[^0-9]+", "");
            return "msm" + board;
        } else if (hardware.matches("mt\\d*.")) {
            return "mt" + hardware.split("mt")[1].trim();
        }
        return Build.BOARD.toLowerCase();
    }

    public static String getBuildDisplayId() {
        return Build.DISPLAY;
    }

    public static String getFingerprint() {
        return Build.FINGERPRINT;
    }

    public static String getVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getVendor() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceName() {
        return Build.DEVICE;
    }

    public static String getModel() {
        return Build.MODEL;
    }

}
