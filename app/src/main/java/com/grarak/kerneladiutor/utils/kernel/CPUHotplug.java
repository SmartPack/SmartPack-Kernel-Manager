package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 06.02.15.
 */
public class CPUHotplug implements Constants {

    public static void activateIntelliPlugEco(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_INTELLI_PLUG_ECO, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugEcoActive() {
        return Utils.readFile(CPU_INTELLI_PLUG_ECO).equals("1");
    }

    public static boolean hasIntelliPlugEco() {
        return Utils.existFile(CPU_INTELLI_PLUG_ECO);
    }

    public static void activateIntelliPlug(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_INTELLI_PLUG, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugActive() {
        return Utils.readFile(CPU_INTELLI_PLUG).equals("1");
    }

    public static boolean hasIntelliPlug() {
        return Utils.existFile(CPU_INTELLI_PLUG);
    }

    public static void activateMpdecision(boolean active, Context context) {
        if (active) Control.startModule(CPU_MPDEC, true, context);
        else Control.stopModule(CPU_MPDEC, true, context);
    }

    public static boolean isMpdecisionActive() {
        return RootUtils.moduleActive(CPU_MPDEC);
    }

    public static boolean hasMpdecision() {
        return Utils.existFile(CPU_MPDECISION_BINARY);
    }

    public static boolean hasCpuHotplug() {
        for (String file : CPU_HOTPLUG_ARRAY)
            if (Utils.existFile(file)) return true;
        return false;
    }

}
