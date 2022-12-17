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
package com.smartpack.kernelmanager.utils.tools;

import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import java.util.LinkedHashMap;

/**
 * Created by willi on 10.07.16.
 */
public class Buildprop {

    public static final String BUILD_PROP = "/system/build.prop";

    public static boolean mWritableSystem = true;
    public static boolean mWritableRoot = true;

    public static void backup() {
        Utils.getInternalDataStorage().mkdirs();
        Utils.create(Utils.readFile(BUILD_PROP), Utils.getInternalDataStorage() + "/build.prop");
    }

    public static void overwrite(String oldKey, String oldValue, String newKey, String newValue) {
        if (!mWritableSystem) RootUtils.mount("rw", "/");
        RootUtils.runCommand("sed 's|" + oldKey + "=" + oldValue + "|" + newKey + "=" + newValue
                + "|g' -i /system/build.prop");
    }

    public static void addKey(String key, String value) {
        if (!mWritableSystem) RootUtils.mount("rw", "/");
        RootUtils.runCommand("echo " + key + "=" + value + " >> " + BUILD_PROP);
    }

    public static LinkedHashMap<String, String> getProps() {
        LinkedHashMap<String, String> list = new LinkedHashMap<>();
        String[] values = Utils.readFile(BUILD_PROP).split("\\r?\\n");
        for (String prop : values) {
            if (!prop.isEmpty() && !prop.startsWith("#")) {
                String[] line = prop.split("=");

                StringBuilder value = new StringBuilder();
                if (line.length > 1) {
                    for (int i = 1; i < line.length; i++) {
                        value.append(line[i]).append("=");
                    }
                    value.setLength(value.length() - 1);
                }
                list.put(line.length > 0 ? line[0].trim() : "", value.toString().trim());
            }
        }
        return list;
    }

}
