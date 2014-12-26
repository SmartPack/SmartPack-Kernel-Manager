package com.grarak.kerneladiutor.utils.kernel;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 22.12.14.
 */
public class Info implements Constants {

    public static String getMemInfo() {
        return Utils.readFile(PROC_MEMINFO);
    }

    public static String getCpuInfo() {
        return Utils.readFile(PROC_CPUINFO);
    }

    public static String getKernelVersion() {
        return Utils.readFile(PROC_VERSION);
    }

}
