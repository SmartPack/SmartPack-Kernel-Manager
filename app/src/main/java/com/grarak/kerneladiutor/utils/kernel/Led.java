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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 27.10.15.
 */
public class Led implements Constants {

    public static void setLedHighpowerCurrent(int value, Context context) {
    Control.runCommand(String.valueOf(value), LHC, Control.CommandType.GENERIC, context);
    }

    public static int getLedHighpowerCurrent() {
        return Utils.stringToInt(Utils.readFile(LHC));
    }

    public static boolean hasLedHighpowerCurrent() {
        return Utils.existFile(LHC);
    }

    public static void setLedLowpowerCurrent(int value, Context context) {
    Control.runCommand(String.valueOf(value), LLC, Control.CommandType.GENERIC, context);
    }

    public static int getLedLowpowerCurrent() {
        return Utils.stringToInt(Utils.readFile(LLC));
    }

    public static boolean hasLedLowpowerCurrent() {
        return Utils.existFile(LLC);
    }

    public static void setLedNotificationDelayOn(int value, Context context) {
    Control.runCommand(String.valueOf(value), LNDO, Control.CommandType.GENERIC, context);
    }

    public static int getLedNotificationDelayOn() {
        return Utils.stringToInt(Utils.readFile(LNDO));
    }

    public static boolean hasLedNotificationDelayOn() {
        return Utils.existFile(LNDO);
    }

    public static void setLedNotificationDelayOff(int value, Context context) {
    Control.runCommand(String.valueOf(value), LNDOFF, Control.CommandType.GENERIC, context);
    }

    public static int getLedNotificationDelayOff() {
        return Utils.stringToInt(Utils.readFile(LNDOFF));
    }

    public static boolean hasLedNotificationDelayOff() {
        return Utils.existFile(LNDOFF);
    }

    public static void activateLedNotificationRampControl(boolean active, Context context) {
        String command = active ? "1" : "0";
        Control.runCommand(command, LNRC, Control.CommandType.GENERIC, context);
    }

    public static boolean isLedNotificationRampControlActive() {
        String value = Utils.readFile(LNRC);
        return value.equals("1");
    }

    public static boolean hasLedNotificationRampControl() {
        return Utils.existFile(LNRC);
    }

    public static void setLedNotificationRampUp(int value, Context context) {
    Control.runCommand(String.valueOf(value), LNRU, Control.CommandType.GENERIC, context);
    }

    public static int getLedNotificationRampUp() {
        return Utils.stringToInt(Utils.readFile(LNRU));
    }

    public static boolean hasLedNotificationRampUp() {
        return Utils.existFile(LNRU);
    }

    public static void setLedNotificationRampDown(int value, Context context) {
    Control.runCommand(String.valueOf(value), LNRD, Control.CommandType.GENERIC, context);
    }

    public static int getLedNotificationRampDown() {
        return Utils.stringToInt(Utils.readFile(LNRD));
    }

    public static boolean hasLedNotificationRampDown() {
        return Utils.existFile(LNRD);
    }

    public static void activateLedPattern(boolean active, Context context) {
        String command = active ? "3" : "0";
        Control.runCommand(command, LP, Control.CommandType.GENERIC, context);
    }

    public static boolean isLedPatternActive() {
        String value = Utils.readFile(LP);
        return value.equals("3");
    }

    public static boolean hasLedPattern() {
        return Utils.existFile(LP);
    }

}
