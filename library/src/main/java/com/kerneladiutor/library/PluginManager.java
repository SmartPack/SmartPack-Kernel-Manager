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

package com.kerneladiutor.library;

import android.content.Context;
import android.content.Intent;

/**
 * Created by willi on 11.08.15.
 */
public class PluginManager {

    private static int VERSION_CODE = BuildConfig.VERSION_CODE;

    /**
     * Publish a Tab {@link Tab}
     *
     * @param tab your Tab
     */
    public static void publishTab(Tab tab) {
        Intent i = new Intent(com.kerneladiutor.library.action.Intent.RECEIVE_DATA);
        i.putExtra(com.kerneladiutor.library.action.Intent.VERSION_CODE, BuildConfig.VERSION_CODE);
        i.putExtra(com.kerneladiutor.library.action.Intent.TAB, tab);
        tab.getContext().sendBroadcast(i);
    }

    /**
     * Sends a command to KA in order to make apply on boot to work for your plugin
     *
     * @param command shell command
     * @param tab     Tab where actions gets executed
     * @param tag     Tag of item which is related to the shell command
     * @param context context needed to send broadcast
     */
    public static void executeCommand(String command, Tab tab, String tag, Context context) {
        Intent i = new Intent(com.kerneladiutor.library.action.Intent.EXECUTE_COMMAND);
        i.putExtra(com.kerneladiutor.library.action.Intent.COMMAND, command);
        i.putExtra(com.kerneladiutor.library.action.Intent.TAB, tab);
        i.putExtra(com.kerneladiutor.library.action.Intent.TAG, tag);
        context.sendBroadcast(i);
    }

    public static void setVersion(int versioncode) {
        VERSION_CODE = versioncode;
    }

    public static int getVersionCode() {
        return VERSION_CODE;
    }

}
