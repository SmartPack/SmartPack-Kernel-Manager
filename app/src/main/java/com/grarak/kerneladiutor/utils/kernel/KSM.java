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

    private static String KSM_FILE;

    public static void setSleepMilliseconds(int ms, Context context) {
        Control.runCommand(String.valueOf(ms), getKsmFile(SLEEP_MILLISECONDS), Control.CommandType.GENERIC, context);
    }

    public static int getSleepMilliseconds() {
        return Utils.stringToInt(Utils.readFile(getKsmFile(SLEEP_MILLISECONDS)));
    }

    public static boolean hasSleepMilliseconds() {
        return Utils.existFile(getKsmFile(SLEEP_MILLISECONDS));
    }

    public static void setPagesToScan(int pages, Context context) {
        Control.runCommand(String.valueOf(pages), getKsmFile(PAGES_TO_SCAN), Control.CommandType.GENERIC, context);
    }

    public static int getPagesToScan() {
        return Utils.stringToInt(Utils.readFile(getKsmFile(PAGES_TO_SCAN)));
    }

    public static boolean hasPagesToScan() {
        return Utils.existFile(getKsmFile(PAGES_TO_SCAN));
    }

    public static void activateDeferredTimer(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", getKsmFile(DEFERRED_TIMER), Control.CommandType.GENERIC, context);
    }

    public static boolean isDeferredTimerActive() {
        return Utils.readFile(getKsmFile(DEFERRED_TIMER)).equals("1");
    }

    public static boolean hasDeferredTimer() {
        return Utils.existFile(getKsmFile(DEFERRED_TIMER));
    }

    public static void activateKsm(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", getKsmFile(RUN), Control.CommandType.GENERIC, context);
    }

    public static boolean isKsmActive() {
        return Utils.readFile(getKsmFile(RUN)).equals("1");
    }

    public static String getInfo(int position) {
        return Utils.readFile(getKsmFile(KSM_INFOS[position]));
    }

    public static boolean hasInfo(int position) {
        return Utils.existFile(getKsmFile(KSM_INFOS[position]));
    }

    public static int getInfoLength() {
        return KSM_INFOS.length;
    }

    private static String getKsmFile(String file) {
        return KSM_FILE + "/" + file;
    }

    public static boolean hasKsm() {
        if (Utils.existFile(UKSM_FOLDER)) KSM_FILE = UKSM_FOLDER;
        else if (Utils.existFile(KSM_FOLDER)) KSM_FILE = KSM_FOLDER;
        return KSM_FILE != null;
    }

}
