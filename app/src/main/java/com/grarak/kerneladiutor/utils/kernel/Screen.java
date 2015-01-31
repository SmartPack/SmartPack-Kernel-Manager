/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        List<String> list = new ArrayList<>(Arrays.asList(colors.split(" ")));

        switch (SCREEN_CALIBRATION) {
            case SCREEN_SAMOLED_COLOR_RED:
                Control.runCommand(String.valueOf(Utils.stringToLong(list.get(0)) * 10000000), SCREEN_SAMOLED_COLOR_RED,
                        Control.CommandType.GENERIC, context);
                Control.runCommand(String.valueOf(Utils.stringToLong(list.get(1)) * 10000000), SCREEN_SAMOLED_COLOR_GREEN,
                        Control.CommandType.GENERIC, context);
                Control.runCommand(String.valueOf(Utils.stringToLong(list.get(2)) * 10000000), SCREEN_SAMOLED_COLOR_BLUE,
                        Control.CommandType.GENERIC, context);
                break;
            case SCREEN_COLOR_CONTROL:
                String red = String.valueOf(Utils.stringToLong(list.get(0)) * 10000000);
                String green = String.valueOf(Utils.stringToLong(list.get(1)) * 10000000);
                String blue = String.valueOf(Utils.stringToLong(list.get(2)) * 10000000);
                Control.runCommand(red + " " + green + " " + blue, SCREEN_COLOR_CONTROL, Control.CommandType.GENERIC, context);
                break;
            default:
                Control.runCommand(colors, SCREEN_CALIBRATION, Control.CommandType.GENERIC, context);
                break;
        }

        if (hasColorCalibrationCtrl())
            Control.runCommand(SCREEN_CALIBRATION_CTRL.equals(SCREEN_COLOR_CONTROL_CTRL) ? "0" : "1",
                    SCREEN_CALIBRATION_CTRL, Control.CommandType.GENERIC, context);
    }

    public static List<String> getColorCalibrationLimits() {
        String[] values;
        if (SCREEN_CALIBRATION.equals(SCREEN_SAMOLED_COLOR_RED)
                || SCREEN_CALIBRATION.equals(SCREEN_COLOR_CONTROL))
            values = new String[341];
        else values = new String[226];
        for (int i = 0; i < values.length; i++)
            if (SCREEN_CALIBRATION.equals(SCREEN_SAMOLED_COLOR_RED)
                    || SCREEN_CALIBRATION.equals(SCREEN_COLOR_CONTROL))
                values[i] = String.valueOf(i + 60);
            else values[i] = String.valueOf(i + 30);
        return new ArrayList<>(Arrays.asList(values));
    }

    public static List<String> getColorCalibration() {
        List<String> list = new ArrayList<>();
        if (SCREEN_CALIBRATION != null) {
            if (SCREEN_CALIBRATION.equals(SCREEN_SAMOLED_COLOR_RED)) {
                long red = Utils.stringToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_RED));
                long green = Utils.stringToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_GREEN));
                long blue = Utils.stringToLong(Utils.readFile(SCREEN_SAMOLED_COLOR_BLUE));
                list.add(String.valueOf(red / 10000000));
                list.add(String.valueOf(green / 10000000));
                list.add(String.valueOf(blue / 10000000));
            } else {
                String value = Utils.readFile(SCREEN_CALIBRATION);
                if (value != null) {
                    for (String color : value.split(" ")) {
                        if (SCREEN_CALIBRATION.equals(SCREEN_COLOR_CONTROL))
                            list.add(String.valueOf(Utils.stringToLong(color) / 10000000));
                        else list.add(color);
                    }
                }
            }
        }
        return list;
    }

    public static boolean hasColorCalibrationCtrl() {
        if (SCREEN_CALIBRATION_CTRL == null)
            for (String file : SCREEN_KCAL_CTRL_ARRAY)
                if (Utils.existFile(file)) {
                    SCREEN_CALIBRATION_CTRL = file;
                    break;
                }
        return SCREEN_CALIBRATION_CTRL != null;
    }

    public static boolean hasColorCalibration() {
        if (SCREEN_CALIBRATION == null) for (String file : SCREEN_KCAL_ARRAY)
            if (Utils.existFile(file)) {
                SCREEN_CALIBRATION = file;
                break;
            }
        return SCREEN_CALIBRATION != null;
    }

    public static boolean hasScreen() {
        for (String file : SCREEN_ARRAY)
            if (Utils.existFile(file)) return true;
        return false;
    }

}
