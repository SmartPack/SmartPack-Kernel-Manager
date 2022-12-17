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
package com.smartpack.kernelmanager.utils.root;

import androidx.annotation.NonNull;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.utils.Utils;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 30.12.15.
 */
public class RootUtils {

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
    }

    public static boolean rootAccess() {
        return Shell.rootAccess();
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
        assert paths != null;
        for (String path : paths.split(":")) {
            if (!path.endsWith("/")) path += "/";
            if (Utils.existFile(path + binary, false) || Utils.existFile(path + binary)) {
                return true;
            }
        }
        return false;
    }

    public static void chmod(String file, String permission) {
        Shell.cmd("chmod " + permission + " " + file).submit();
    }

    public static String getProp(String prop) {
        return runAndGetOutput("getprop " + prop);
    }

    public static String mount(String command, String mountPoint) {
        return runAndGetError("mount -o remount," + command + " " + mountPoint);
    }

    public static boolean isWritableSystem() {
        return !mount("rw", "/system").equals("mount: '/system' not in /proc/mounts");
    }

    public static boolean isWritableRoot() {
        return !mount("rw", "/").contains("' is read-only");
    }

    public static void closeSU() {
        try {
            Objects.requireNonNull(Shell.getCachedShell()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runCommand(String command) {
        Shell.cmd(command).exec();
    }

    public static void runAndGetLiveOutput(String command, List<String> output) {
        Shell.cmd(command).to(output, output).exec();
    }

    @NonNull
    public static String runAndGetOutput(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            List<String> outputs = Shell.cmd(command).exec().getOut();
            if (ShellUtils.isValidOutput(outputs)) {
                for (String output : outputs) {
                    sb.append(output).append("\n");
                }
            }
            return Utils.removeSuffix(sb.toString(), "\n").trim();
        } catch (Exception e) {
            return "";
        }
    }

    @NonNull
    public static String runAndGetError(String command) {
        StringBuilder sb = new StringBuilder();
        List<String> outputs = new ArrayList<>();
        List<String> stderr = new ArrayList<>();
        try {
            Shell.cmd(command).to(outputs, stderr).exec();
            outputs.addAll(stderr);
            if (ShellUtils.isValidOutput(outputs)) {
                for (String output : outputs) {
                    sb.append(output).append("\n");
                }
            }
            return Utils.removeSuffix(sb.toString(), "\n").trim();
        } catch (Exception e) {
            return "";
        }
    }
}