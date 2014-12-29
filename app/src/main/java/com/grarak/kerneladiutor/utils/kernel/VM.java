package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class VM implements Constants {

    private static List<String> vmFiles = new ArrayList<>();

    private static final String[] supportedVM = {"dirty_ratio",
            "dirty_background_ratio", "dirty_expire_centisecs",
            "dirty_writeback_centisecs", "min_free_kbytes", "overcommit_ratio",
            "swappiness", "vfs_cache_pressure"};

    public static void setVM(String value, String name, Context context) {
        Control.runCommand(value, VM_PATH + "/" + name, Control.CommandType.GENERIC, context);
    }

    public static String getVMValue(String file) {
        if (Utils.existFile(VM_PATH + "/" + file)) {
            String value = Utils.readFile(VM_PATH + "/" + file);
            if (value != null) return value;
        }
        return null;
    }

    public static List<String> getVMfiles() {
        if (vmFiles.size() < 1) {
            File[] files = new File(VM_PATH).listFiles();
            if (files.length > 0) {
                for (String supported : supportedVM)
                    for (File file : files)
                        if (file.getName().equals(supported))
                            vmFiles.add(file.getName());
            }
        }
        return vmFiles;
    }

}
