package com.grarak.kerneladiutor.utils.kernel.cpuhotplug;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 19.06.17.
 */

public class AiOHotplug {

    private static final String PARENT = "/sys/kernel/AiO_HotPlug";
    private static final String TOGGLE = PARENT + "/toggle";
    private static final String CORES = PARENT + "/cores";
    private static final String BIG_CORES = PARENT + "/big_cores";
    private static final String LITTLE_CORES = PARENT + "/LITTLE_cores";

    public static void setLITTLECores(int cores, Context context) {
        run(Control.write(String.valueOf(cores), LITTLE_CORES), LITTLE_CORES, context);
    }

    public static int getLITTLECores() {
        return Utils.strToInt(Utils.readFile(LITTLE_CORES));
    }

    public static boolean hasLITTLECores() {
        return Utils.existFile(LITTLE_CORES);
    }

    public static void setBigCores(int cores, Context context) {
        run(Control.write(String.valueOf(cores), BIG_CORES), BIG_CORES, context);
    }

    public static int getBigCores() {
        return Utils.strToInt(Utils.readFile(BIG_CORES));
    }

    public static boolean hasBigCores() {
        return Utils.existFile(BIG_CORES);
    }

    public static void setCores(int cores, Context context) {
        run(Control.write(String.valueOf(cores), CORES), CORES, context);
    }

    public static int getCores() {
        return Utils.strToInt(Utils.readFile(CORES));
    }

    public static boolean hasCores() {
        return Utils.existFile(CORES);
    }

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TOGGLE), TOGGLE, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(TOGGLE).equals("1");
    }

    public static boolean hasToggle() {
        return Utils.existFile(TOGGLE);
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
