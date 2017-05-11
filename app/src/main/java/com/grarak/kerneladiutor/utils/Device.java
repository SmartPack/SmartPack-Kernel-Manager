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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            private Item(List<String> input) {
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
                if (ROM_VERSION != null && !ROM_VERSION.isEmpty()) {
                    break;
                }
            }
        }

    }

    public static class MemInfo {

        private static final String MEMINFO_PROC = "/proc/meminfo";

        private static String MEMINFO;

        public static long getTotalMem() {
            try {
                return Long.parseLong(getItem("MemTotal").replaceAll("[^\\d]", "")) / 1024L;
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }

        public static List<String> getItems() {
            List<String> list = new ArrayList<>();
            try {
                for (String line : MEMINFO.split("\\r?\\n")) {
                    list.add(line.split(":")[0]);
                }
            } catch (Exception ignored) {
            }
            return list;
        }

        public static String getItem(String prefix) {
            try {
                for (String line : MEMINFO.split("\\r?\\n")) {
                    if (line.startsWith(prefix)) {
                        return line.split(":")[1].trim();
                    }
                }
            } catch (Exception ignored) {
            }
            return "";
        }

        public static void load() {
            MEMINFO = Utils.readFile(MEMINFO_PROC);
        }

    }

    public static class CPUInfo {

        private static final String CPUINFO_PROC = "/proc/cpuinfo";

        private static String CPUINFO;

        public static String getFeatures() {
            String features = getString("Features", true);
            if (!features.isEmpty()) return features;
            return getString("flags", true);
        }

        public static String getProcessor() {
            String pro = getString("Processor", true);
            if (!pro.isEmpty()) return pro;
            return getString("model name", true);
        }

        public static String getVendor() {
            return getVendor(true);
        }

        public static String getVendor(boolean root) {
            String vendor = getString("Hardware", root);
            if (!vendor.isEmpty()) return vendor;
            return getString("vendor_id", root);
        }

        public static String getCpuInfo(boolean root) {
            if (CPUINFO == null) {
                load(root);
            }
            return CPUINFO;
        }

        private static String getString(String prefix, boolean root) {
            if (CPUINFO == null) {
                load(root);
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
            load(true);
        }

        public static void load(boolean root) {
            CPUINFO = Utils.readFile(CPUINFO_PROC, root);
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
        return getKernelVersion(extended, true);
    }

    public static String getKernelVersion(boolean extended, boolean root) {
        return extended ? Utils.readFile("/proc/version", root) : RootUtils.runCommand("uname -r");
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
        return getBoard(true);
    }

    private interface BoardFormatter {
        String format(String board);
    }

    private static HashMap<String, BoardFormatter> sBoardFormatters = new HashMap<>();
    private static HashMap<String, String> sBoardAliases = new HashMap<>();

    static {
        sBoardFormatters.put(".*msm.+.\\d+.*", new BoardFormatter() {
            @Override
            public String format(String board) {
                return "msm" + board.split("msm")[1].trim().split(" ")[0];
            }
        });

        sBoardFormatters.put("mt\\d*.", new BoardFormatter() {
            @Override
            public String format(String board) {
                return "mt" + board.split("mt")[1].trim().split(" ")[0];
            }
        });

        sBoardFormatters.put(".*apq.+.\\d+.*", new BoardFormatter() {
            @Override
            public String format(String board) {
                return "apq" + board.split("apq")[1].trim().split(" ")[0];
            }
        });

        sBoardFormatters.put(".*omap+\\d.*", new BoardFormatter() {
            @Override
            public String format(String board) {
                Matcher matcher = Pattern.compile("omap+\\d").matcher(board);
                if (matcher.find()) {
                    return matcher.group();
                }
                return null;
            }
        });

        sBoardFormatters.put("sun+\\d.", new BoardFormatter() {
            @Override
            public String format(String board) {
                return board;
            }
        });

        sBoardFormatters.put("spyder", new BoardFormatter() {
            @Override
            public String format(String board) {
                return "omap4";
            }
        });
        sBoardFormatters.put("tuna", new BoardFormatter() {
            @Override
            public String format(String board) {
                return "omap4";
            }
        });

        sBoardAliases.put("msm8994v2.1", "msm8994");
        sBoardAliases.put("msm8974pro.*", "msm8974pro");
    }

    public static String getBoard(boolean root) {
        String hardware = CPUInfo.getVendor(root).toLowerCase();
        String ret = null;
        for (String boardregex : sBoardFormatters.keySet()) {
            if (hardware.matches(boardregex)) {
                ret = sBoardFormatters.get(boardregex).format(hardware);
            }
        }
        if (ret != null) {
            for (String alias : sBoardAliases.keySet()) {
                if (ret.matches(alias)) {
                    ret = sBoardAliases.get(alias);
                }
            }
        }
        return ret != null ? ret : Build.BOARD.toLowerCase();
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
