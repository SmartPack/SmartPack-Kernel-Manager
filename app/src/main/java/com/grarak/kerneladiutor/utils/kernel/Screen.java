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
public class Screen implements Constants {

    private static String SCREEN_CALIBRATION;
    private static String SCREEN_CALIBRATION_CTRL;

    public static void setColorCalibration(String colors, Context context) {
        Control.runCommand(colors, SCREEN_CALIBRATION, Control.CommandType.GENERIC, context);
        if (hasColorCalibrationCtrl())
            Control.runCommand("1", SCREEN_CALIBRATION_CTRL, Control.CommandType.GENERIC, context);
    }

    public static List<String> getLimits() {
        String[] values = new String[226];
        for (int i = 0; i < values.length; i++)
            values[i] = String.valueOf(i + 30);
        return new ArrayList<>(Arrays.asList(values));
    }

    public static List<String> getColorCalibration() {
        if (SCREEN_CALIBRATION != null)
            if (Utils.existFile(SCREEN_CALIBRATION)) {
                String value = Utils.readFile(SCREEN_CALIBRATION);
                if (value != null) {
                    List<String> list = new ArrayList<>();
                    for (String color : value.split(" "))
                        list.add(color);
                    return list;
                }
            }
        return null;
    }

    public static boolean hasColorCalibrationCtrl() {
        if (SCREEN_CALIBRATION_CTRL == null)
            for (String file : SCREEN_KCAL_CTRL_ARRAY)
                if (Utils.existFile(file)) SCREEN_CALIBRATION_CTRL = file;
        return SCREEN_CALIBRATION_CTRL != null;
    }

    public static boolean hasColorCalibration() {
        if (SCREEN_CALIBRATION == null) for (String file : SCREEN_KCAL_ARRAY)
            if (Utils.existFile(file)) SCREEN_CALIBRATION = file;
        return Utils.readFile(SCREEN_CALIBRATION) != null;
    }

    public static boolean hasScreen() {
        for (String[] array : SCREEN_ARRAY)
            for (String file : array)
                if (Utils.existFile(file)) return Utils.readFile(file) != null;
        return false;
    }

}
