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

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.kerneladiutor.library.root.RootUtils;

import java.util.LinkedHashMap;

/**
 * Created by willi on 01.01.15.
 */
public class Buildprop implements Constants {

    public static void overwrite(String oldKey, String oldValue, String newKey, String newValue) {
        RootUtils.mount(true, "/system");
        RootUtils.runCommand("sed 's|" + oldKey + "=" + oldValue + "|" + newKey + "=" + newValue
                + "|g' -i /system/build.prop");
    }

    public static void addKey(String key, String value) {
        RootUtils.mount(true, "/system");
        RootUtils.runCommand("echo " + key + "=" + value + " >> " + BUILD_PROP);
    }

    public static LinkedHashMap<String, String> getProps() {
        LinkedHashMap<String, String> list = new LinkedHashMap<>();
        String buildprop;
        if ((buildprop = Utils.readFile(BUILD_PROP)) != null) {
            String[] values = buildprop.split("\\r?\\n");
            for (String prop : values)
                if (!prop.isEmpty() && !prop.startsWith("#")) {
                    String[] line = prop.split("=");

                    StringBuilder value = new StringBuilder();
                    if (line.length > 1) {
                        for (int i = 1; i < line.length; i++) value.append(line[i]).append("=");
                        value.setLength(value.length() - 1);
                    }
                    list.put(line.length > 0 ? line[0].trim() : "", value.toString().trim());
                }
        }
        return list;
    }

    public static boolean hasBuildprop() {
        return getProps().size() > 0;
    }

}
