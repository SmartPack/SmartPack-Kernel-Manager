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

package com.grarak.kerneladiutor.utils.root;

import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by willi on 14.12.14.
 */
public class RootUtils implements Constants {

    public static SU su;

    public enum SELINUX_STATUS {
        NOSELINUX, DISABLED, PERMISSIVE, ENFORCING
    }

    public static SELINUX_STATUS getSelinux() {
        String status = runCommand("getenforce");
        switch (status.toLowerCase()) {
            case "disabled":
                return SELINUX_STATUS.DISABLED;
            case "permissive":
                return SELINUX_STATUS.PERMISSIVE;
            case "enforcing":
                return SELINUX_STATUS.ENFORCING;
            default:
                return SELINUX_STATUS.NOSELINUX;
        }
    }

    public static boolean rooted() {
        return RootTools.isRootAvailable();
    }

    public static boolean rootAccess() {
        return RootTools.isAccessGiven();
    }

    public static boolean busyboxInstalled() {
        return RootTools.isBusyboxAvailable();
    }

    public static void mount(boolean writeable, String mountpoint) {
        runCommand(writeable ? "mount -o rw,remount " + mountpoint : "mount -o ro,remount " + mountpoint);
    }

    public static String runCommand(String command) {
        if (su == null) su = new SU();
        if (su.closed) su = new SU();
        return su.runCommand(command);
    }

    /**
     * Based on AndreiLux's SU code in Synapse
     * https://github.com/AndreiLux/Synapse/blob/master/src/main/java/com/af/synapse/utils/Utils.java#L238
     */
    public static class SU {

        private Process process;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;
        private boolean closed;

        public SU() {
            try {
                Log.i(TAG, "SU initialized");
                process = Runtime.getRuntime().exec("su");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } catch (IOException e) {
                Log.e(TAG, "Failed to run shell as su");
            }
        }

        public synchronized String runCommand(final String command) {
            try {
                StringBuilder sb = new StringBuilder();
                String callback = "/shellCallback/";
                bufferedWriter.write(command + "\necho " + callback + "\n");
                bufferedWriter.flush();

                int i;
                char[] buffer = new char[256];
                while (true) {
                    sb.append(buffer, 0, bufferedReader.read(buffer));
                    if ((i = sb.indexOf(callback)) > -1) {
                        sb.delete(i, i + callback.length());
                        break;
                    }
                }
                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void close() {
            try {
                bufferedWriter.write("exit\n");
                bufferedWriter.flush();

                process.waitFor();
                Log.i(TAG, "SU closed: " + process.exitValue());
                closed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
