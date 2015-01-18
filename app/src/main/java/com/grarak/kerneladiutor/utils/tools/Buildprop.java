package com.grarak.kerneladiutor.utils.tools;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

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

    public static List<BuildpropItems> getProps() {
        String[] values = Utils.readFile(BUILD_PROP).split("\\r?\\n");
        List<BuildpropItems> list = new ArrayList<>();
        for (String prop : values)
            if (!prop.isEmpty() && !prop.startsWith("#")) {
                String[] line = prop.split("=");
                String key = "";
                if (line.length > 0) key = line[0];
                String value = line.length > 1 ? line[1] : "";
                list.add(new BuildpropItems(key, value));
            }
        return list;
    }

    public static class BuildpropItems {

        private final String key;
        private final String value;

        public BuildpropItems(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

}
