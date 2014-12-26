package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class CPUVoltage implements Constants {

    private static String[] mCpuFreqs;

    public static void setVoltage(String voltages, Context context) {
        Control.runCommand(voltages, CPU_VOLTAGE, Control.CommandType.GENERIC, context);
    }

    public static List<String> getVoltages() {
        if (Utils.existFile(String.format(CPU_VOLTAGE, 0))) {
            String value = Utils.readFile(String.format(CPU_VOLTAGE, 0));
            if (value != null) {
                String[] lines = value.split(" mV");
                String[] voltages = new String[lines.length];
                for (int i = 0; i < lines.length; i++)
                    voltages[i] = lines[i].split("mhz: ")[1];
                return new ArrayList<>(Arrays.asList(voltages));
            }
        }
        return null;
    }

    public static List<String> getFreqs() {
        if (mCpuFreqs == null)
            if (Utils.existFile(String.format(CPU_VOLTAGE, 0))) {
                String value = Utils.readFile(String.format(CPU_VOLTAGE, 0));
                if (value != null) {
                    String[] lines = value.split(" mV");
                    mCpuFreqs = new String[lines.length];
                    for (int i = 0; i < lines.length; i++)
                        mCpuFreqs[i] = lines[i].split("mhz: ")[0].trim();
                }
            }
        return new ArrayList<>(Arrays.asList(mCpuFreqs));
    }

    public static boolean hasCpuVoltage() {
        return Utils.existFile(String.format(CPU_VOLTAGE, 0));
    }

}
