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

    private static String CPU_VOLTAGE_FILE;
    private static String[] mCpuFreqs;

    public static void setVoltage(String freq, String voltage, Context context) {
        String command = "";
        int position = 0;
        for (int i = 0; i < getFreqs().size(); i++) {
            if (freq.equals(getFreqs().get(i)))
                position = i;
        }
        if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE)) {
            List<String> voltages = getVoltages();
            for (int i = 0; i < voltages.size(); i++)
                if (i == position)
                    command += command.isEmpty() ? voltage : " " + voltage;
                else
                    command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
        }
        if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
            command = getFreqs().get(position) + " " + Utils.stringToInt(voltage) * 1000;
        Control.runCommand(command, CPU_VOLTAGE_FILE, Control.CommandType.GENERIC, context);
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(CPU_VOLTAGE);
        if (value != null) {
            String[] lines = null;
            value = value.replace(" ", "");
            if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE)) lines = value.split("mV");
            if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE)) lines = value.split("\\r?\\n");
            assert lines != null;
            String[] voltages = new String[lines.length];
            for (int i = 0; i < voltages.length; i++) {
                String[] voltageLine = null;
                if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
                    voltageLine = lines[i].split("mhz:");
                if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                    voltageLine = lines[i].split(":");
                assert voltageLine != null;
                if (voltageLine.length > 1) {
                    if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
                        voltages[i] = voltageLine[1];
                    if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                        voltages[i] = String.valueOf(Utils.stringToInt(voltageLine[1]) / 1000);
                }
            }
            return new ArrayList<>(Arrays.asList(voltages));
        }
        return null;
    }

    public static List<String> getFreqs() {
        if (mCpuFreqs == null) {
            String value = Utils.readFile(CPU_VOLTAGE_FILE);
            if (value != null) {
                String[] lines = null;
                value = value.replace(" ", "");
                if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE)) lines = value.split("mV");
                if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE)) lines = value.split("\\r?\\n");
                assert lines != null;
                mCpuFreqs = new String[lines.length];
                for (int i = 0; i < lines.length; i++) {
                    if (CPU_VOLTAGE_FILE.equals(CPU_VOLTAGE))
                        mCpuFreqs[i] = lines[i].split("mhz:")[0];
                    if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                        mCpuFreqs[i] = String.valueOf(Utils.stringToInt(lines[i].split(":")[0]) / 1000);
                }
            }
        }
        if (mCpuFreqs == null) return null;
        return new ArrayList<>(Arrays.asList(mCpuFreqs));
    }

    public static boolean hasCpuVoltage() {
        for (String file : CPU_VOLTAGE_ARRAY)
            if (Utils.existFile(file)) {
                CPU_VOLTAGE_FILE = file;
                break;
            }
        return CPU_VOLTAGE_FILE != null;
    }

}
