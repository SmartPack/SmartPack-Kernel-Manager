package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 02.12.14.
 */
public class CPU implements Constants {

    private static Integer[] mFreqs;
    private static String[] mAvailableGovernors;
    private static String[] mMcPowerSavingItems;

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

    public static String[] getMcPowerSavingItems(Context context) {
        if (mMcPowerSavingItems == null && context != null)
            mMcPowerSavingItems = context.getResources().getStringArray(R.array.mc_power_saving_items);
        return mMcPowerSavingItems;
    }

    public static void setMcPowerSaving(int value, Context context) {
        Control.runCommand(String.valueOf(value), CPU_MC_POWER_SAVING, Control.CommandType.GENERIC, context);
    }

    public static int getCurMcPowerSaving() {
        return Integer.parseInt(Utils.readFile(CPU_MC_POWER_SAVING));
    }

    public static boolean hasMcPowerSaving() {
        return Utils.existFile(CPU_MC_POWER_SAVING);
    }

    public static ArrayList<String> getAvailableGovernors() {
        if (mAvailableGovernors == null)
            if (Utils.existFile(CPU_AVAILABLE_GOVERNORS))
                mAvailableGovernors = Utils.readFile(CPU_AVAILABLE_GOVERNORS).split(" ");
        return new ArrayList<>(Arrays.asList(mAvailableGovernors));
    }

    public static void setGovernor(String governor, Context context) {
        Control.runCommand(governor, CPU_SCALING_GOVERNOR, Control.CommandType.CPU, context);
    }

    public static String getCurGovernor(int core) {
        if (Utils.existFile(String.format(CPU_SCALING_GOVERNOR, core))) {
            String value = Utils.readFile(String.format(CPU_SCALING_GOVERNOR,
                    core));
            if (value != null) return value;
        }
        return "";
    }

    public static ArrayList<Integer> getFreqs() {
        if (mFreqs == null) {
            if (Utils.existFile(CPU_AVAILABLE_FREQS)) {
                String values = Utils.readFile(CPU_AVAILABLE_FREQS);
                if (values != null) {
                    String[] valueArray = values.split(" ");
                    mFreqs = new Integer[valueArray.length];
                    for (int i = 0; i < mFreqs.length; i++)
                        mFreqs[i] = Integer.parseInt(valueArray[i]);
                }
            } else if (Utils.existFile(CPU_TIME_STATE)) {
                String values = Utils.readFile(CPU_TIME_STATE);
                if (values != null) {
                    String[] valueArray = values.split("\\r?\\n");
                    mFreqs = new Integer[valueArray.length];
                    for (int i = 0; i < mFreqs.length; i++)
                        mFreqs[i] = Integer.parseInt(valueArray[i].split(" ")[0]);

                    if (mFreqs[0] > mFreqs[mFreqs.length - 1]) {
                        List<Integer> freqs = new ArrayList<>();
                        for (int x = mFreqs.length - 1; x >= 0; x--)
                            freqs.add(mFreqs[x]);
                        for (int i = 0; i < mFreqs.length; i++)
                            mFreqs[i] = freqs.get(i);
                    }
                }
            }
        }
        if (mFreqs == null) return null;
        return new ArrayList<>(Arrays.asList(mFreqs));
    }

    public static void setMinFreq(int freq, Context context) {
        Control.runCommand(String.valueOf(freq), CPU_MIN_FREQ, Control.CommandType.CPU, context);
    }

    public static int getMinFreq(int core) {
        if (Utils.existFile(String.format(CPU_MIN_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_MIN_FREQ, core));
            if (value != null) return Integer.parseInt(value);
        }
        return 0;
    }

    public static void setMaxFreq(int freq, Context context) {
        Control.runCommand(String.valueOf(freq), CPU_MAX_FREQ, Control.CommandType.CPU, context);
    }

    public static int getMaxFreq(int core) {
        if (Utils.existFile(String.format(CPU_MAX_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_MAX_FREQ, core));
            if (value != null) return Integer.parseInt(value);
        }
        return 0;
    }

    public static int getCurFreq(int core) {
        if (Utils.existFile(String.format(CPU_CUR_FREQ, core))) {
            String value = Utils.readFile(String.format(CPU_CUR_FREQ, core));
            if (value != null) return Integer.parseInt(value);
        }
        return 0;
    }

    public static void activateCore(int core, boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", String.format(CPU_CORE_ONLINE, core),
                Control.CommandType.GENERIC, context);
    }

    public static int getCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

}
