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

/**
 * Created by willi on 27.12.14.
 */
public class KSM implements Constants {

    public static void setSleepMilliseconds(int ms, Context context) {
        Control.runCommand(String.valueOf(ms), KSM_SLEEP_MILLISECONDS, Control.CommandType.GENERIC, context);
    }

    public static int getSleepMilliseconds() {
        return Utils.stringToInt(Utils.readFile(KSM_SLEEP_MILLISECONDS));
    }

    public static boolean hasSleepMilliseconds() {
        return Utils.existFile(KSM_SLEEP_MILLISECONDS);
    }

    public static void setPagesToScan(int pages, Context context) {
        Control.runCommand(String.valueOf(pages), KSM_PAGES_TO_SCAN, Control.CommandType.GENERIC, context);
    }

    public static int getPagesToScan() {
        return Utils.stringToInt(Utils.readFile(KSM_PAGES_TO_SCAN));
    }

    public static boolean hasPagesToScan() {
        return Utils.existFile(KSM_PAGES_TO_SCAN);
    }

    public static void activateDeferredTimer(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", KSM_DEFERRED_TIMER, Control.CommandType.GENERIC, context);
    }

    public static boolean isDeferredTimerActive() {
        return Utils.readFile(KSM_DEFERRED_TIMER).equals("1");
    }

    public static boolean hasDeferredTimer() {
        return Utils.existFile(KSM_DEFERRED_TIMER);
    }

    public static void activateKSM(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", KSM_RUN, Control.CommandType.GENERIC, context);
    }

    public static int getInfos(int position) {
        if (Utils.existFile(KSM_INFOS[position])) {
            String value = Utils.readFile(KSM_INFOS[position]);
            if (value != null) return Utils.stringToInt(value);
        }
        return 0;
    }

    public static boolean isKsmActive() {
        return Utils.readFile(KSM_RUN).equals("1");
    }

    public static boolean hasKsm() {
        return Utils.existFile(KSM_RUN);
    }

}
