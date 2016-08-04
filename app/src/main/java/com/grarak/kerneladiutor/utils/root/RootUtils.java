/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.utils.root;

import android.util.Log;

import com.grarak.kerneladiutor.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by willi on 30.12.15.
 */
public class RootUtils {

    private static final String TAG = RootUtils.class.getSimpleName();

    private static SU su;

    public static boolean rooted() {
        return existBinary("su");
    }

    public static boolean rootAccess() {
        SU su = getSU();
        su.runCommand("echo /testRoot/");
        return !su.denied;
    }

    public static boolean busyboxInstalled() {
        return existBinary("busybox") || existBinary("toybox");
    }

    private static boolean existBinary(String binary) {
        String paths;
        if (System.getenv("PATH") != null) {
            paths = System.getenv("PATH");
        } else {
            paths = "/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin";
        }
        for (String path : paths.split(":")) {
            if (!path.endsWith("/")) path += "/";
            if (Utils.existFile(path + binary, false) || Utils.existFile(path + binary)) {
                return true;
            }
        }
        return false;
    }

    public static void chmod(String file, String permission) {
        chmod(file, permission, getSU());
    }

    public static void chmod(String file, String permission, SU su) {
        su.runCommand("chmod " + permission + " " + file);
    }

    public static String getProp(String prop) {
        return runCommand("getprop " + prop);
    }

    public static void mount(boolean writeable, String mountpoint) {
        mount(writeable, mountpoint, getSU());
    }

    public static void mount(boolean writeable, String mountpoint, SU su) {
        su.runCommand(writeable ? "mount -o rw,remount " + mountpoint : "mount -o ro,remount " + mountpoint);
    }

    public static String runScript(String text, String... arguments) {
        RootFile script = new RootFile("/data/local/tmp/kerneladiutortmp.sh");
        script.mkdir();
        script.write(text, false);
        return script.execute(arguments);
    }

    public static void closeSU() {
        if (su != null) su.close();
        su = null;
    }

    public static String runCommand(String command) {
        return getSU().runCommand(command);
    }

    public static SU getSU() {
        if (su == null) su = new SU();
        else if (su.closed || su.denied) su = new SU();
        return su;
    }

    /*
     * Based on AndreiLux's SU code in Synapse
     * https://github.com/AndreiLux/Synapse/blob/master/src/main/java/com/af/synapse/utils/Utils.java#L238
     */
    public static class SU {

        private Process process;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;
        private final boolean root;
        private final String mTag;
        private boolean closed;
        private boolean denied;
        private boolean firstTry;

        public SU() {
            this(true, null);
        }

        public SU(boolean root, String tag) {
            this.root = root;
            mTag = tag;
            try {
                Log.i(TAG, root ? "SU initialized" : "SH initialized");
                firstTry = true;
                process = Runtime.getRuntime().exec(root ? "su" : "sh");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } catch (IOException e) {
                Log.e(TAG, root ? "Failed to run shell as su" : "Failed to run shell as sh");
                denied = true;
                closed = true;
            }
        }

        public synchronized String runCommand(final String command) {
            synchronized (this) {
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
                    firstTry = false;
                    if (mTag != null) {
                        Log.i(mTag, "run: " + command + " output: " + sb.toString().trim());
                    }
                    return sb.toString().trim();
                } catch (IOException e) {
                    e.printStackTrace();
                    closed = true;
                    if (firstTry) denied = true;
                } catch (Exception e) {
                    denied = true;
                }
                return null;
            }
        }

        public void close() {
            try {
                bufferedWriter.write("exit\n");
                bufferedWriter.flush();

                process.waitFor();
                Log.i(TAG, root ? "SU closed: " + process.exitValue() : "SH closed: " + process.exitValue());
                closed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
