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

    public static void setIntelliPlugScreenOffMax(int position, Context context) {
        String command = position == 0 ? "4294967295" : String.valueOf(CPU.getFreqs().get(position - 1));
        Control.runCommand(command, CPU_INTELLI_PLUG_SCREEN_OFF_MAX, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugScreenOffMax() {
        String value = Utils.readFile(CPU_INTELLI_PLUG_SCREEN_OFF_MAX);
        if (value.equals("4294967295") // 4294967295 is 32-Bit max unsigned integer
                || value.equals("0")) return 0;
        return CPU.getFreqs().indexOf(Utils.stringToInt(value)) + 1;
    }

    public static boolean hasIntelliPlugScreenOffMax() {
        return Utils.existFile(CPU_INTELLI_PLUG_SCREEN_OFF_MAX);
    }

    public static void setIntelliPlugThresold(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_INTELLI_PLUG_THRESOLD, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugThresold() {
        return Utils.stringToInt(Utils.readFile(CPU_INTELLI_PLUG_THRESOLD));
    }

    public static boolean hasIntelliPlugThresold() {
        return Utils.existFile(CPU_INTELLI_PLUG_THRESOLD);
    }

    public static void setIntelliPlugHysteresis(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_INTELLI_PLUG_HYSTERESIS, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugHysteresis() {
        return Utils.stringToInt(Utils.readFile(CPU_INTELLI_PLUG_HYSTERESIS));
    }

    public static boolean hasIntelliPlugHysteresis() {
        return Utils.existFile(CPU_INTELLI_PLUG_HYSTERESIS);
    }

    public static void activateIntelliPlugTouchBoost(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_INTELLI_PLUG_TOUCH_BOOST, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugTouchBoostActive() {
        return Utils.readFile(CPU_INTELLI_PLUG_TOUCH_BOOST).equals("1");
    }

    public static boolean hasIntelliPlugTouchBoost() {
        return Utils.existFile(CPU_INTELLI_PLUG_TOUCH_BOOST);
    }

    public static void activateIntelliPlugEco(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", CPU_INTELLI_PLUG_ECO, Control.CommandType.GENERIC, context);
    }

    public static boolean isIntelliPlugEcoActive() {
        return Utils.readFile(CPU_INTELLI_PLUG_ECO).equals("1");
    }

    public static boolean hasIntelliPlugEco() {
        return Utils.existFile(CPU_INTELLI_PLUG_ECO);
    }

    public static void setIntelliPlugProfile(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_INTELLI_PLUG_PROFILE, Control.CommandType.GENERIC, context);
    }

    public static int getIntelliPlugProfile() {
        return Utils.stringToInt(Utils.readFile(CPU_INTELLI_PLUG_PROFILE));
    }

    public static boolean hasIntelliPlugProfile() {
        return Utils.existFile(CPU_INTELLI_PLUG_PROFILE);
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
