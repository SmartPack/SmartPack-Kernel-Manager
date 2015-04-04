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

import android.content.Context;
import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.SysDB;
import com.grarak.kerneladiutor.utils.kernel.CPU;

import java.util.List;

/**
 * Created by willi on 14.12.14.
 */
public class Control implements Constants {

    public enum CommandType {
        GENERIC, CPU, FAUX_GENERIC, SELINUX, CUSTOM
    }

    public static void commandSaver(final Context context, final String sys, final String command) {
        SysDB sysDB = new SysDB(context);
        sysDB.create();

        List<SysDB.SysItem> sysList = sysDB.getAllSys();
        for (int i = 0; i < sysList.size(); i++)
            if (sysList.get(i).getSys().equals(sys))
                sysDB.deleteItem(sysList.get(i).getId());

        sysDB.insertSys(sys, command);
        sysDB.close();

        Log.i(TAG, "Run command: " + command);

    }

    private static void run(String command, String sys, Context context) {
        RootUtils.runCommand(command);
        commandSaver(context, sys, command);
    }

    private static int getChecksum(int arg1, int arg2) {
        return 255 & (Integer.MAX_VALUE ^ (arg1 & 255) + (arg2 & 255));
    }

    private static void setPermission(String file, int permission, Context context) {
        run("chmod " + permission + " " + file, file + "permission" + permission, context);
    }

    private static void runGeneric(String file, String value, String id, Context context) {
        run("echo " + value + " > " + file, id != null ? file + id : file, context);
    }

    private static void runFauxGeneric(String file, String value, Context context) {
        String command = value.contains(" ") ? value + " " + getChecksum(Utils.stringToInt(value.split(" ")[0]),
                Utils.stringToInt(value.split(" ")[1])) : value + " " + getChecksum(Utils.stringToInt(value), 0);
        run("echo " + value + " > " + file, file + "nochecksum", context);
        run("echo " + command + " > " + file, file, context);
    }

    private static void runSelinux(int value, Context context) {
        run("setenforce " + value, SELINUX, context);
    }

    public static void startService(String service, boolean save, Context context) {
        RootUtils.runCommand("start " + service);

        if (save) commandSaver(context, service, "start " + service);
    }

    public static void stopService(String service, boolean save, Context context) {
        RootUtils.runCommand("stop " + service);
        if (service.equals(HOTPLUG_MPDEC)) bringCoresOnline();

        if (save) commandSaver(context, service, "stop " + service);
    }

    public static void bringCoresOnline() {
        try {
            for (int i = 0; i < CPU.getCoreCount(); i++)
                RootUtils.runCommand("echo 1 > " + String.format(CPU_CORE_ONLINE, i));
            // Give CPU some time to bring core online
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Thread mThread;

    public static void runCommand(final String value, final String file, final CommandType command, final String id,
                                  final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (command == CommandType.CPU) {
                    for (int i = 0; i < CPU.getCoreCount(); i++) {
                        setPermission(String.format(file, i), 644, context);
                        runGeneric(String.format(file, i), value, id, context);
                        setPermission(String.format(file, i), 444, context);
                    }
                } else if (command == CommandType.GENERIC) {
                    runGeneric(file, value, id, context);
                } else if (command == CommandType.FAUX_GENERIC) {
                    runFauxGeneric(file, value, context);
                } else if (command == CommandType.SELINUX) {
                    runSelinux(Utils.stringToInt(value), context);
                } else if (command == CommandType.CUSTOM) {
                    Control.run(value, id == null ? file : file + id, context);
                }
            }
        });

        try {
            if (mThread != null) mThread.join();
            thread.start();
            mThread = thread;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void runCommand(final String value, final String file, final CommandType command, final Context context) {
        runCommand(value, file, command, null, context);
    }

}
