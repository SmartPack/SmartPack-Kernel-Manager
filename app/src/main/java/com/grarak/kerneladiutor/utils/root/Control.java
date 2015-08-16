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
import com.grarak.kerneladiutor.utils.database.CommandDB;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.kernel.CPUHotplug;
import com.kerneladiutor.library.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 14.12.14.
 */
public class Control implements Constants {

    public enum CommandType {
        GENERIC, CPU, CPU_LITTLE, FAUX_GENERIC, CUSTOM
    }

    public static void commandSaver(final Context context, final String path, final String command) {
        CommandDB commandDB = new CommandDB(context);

        List<CommandDB.CommandItem> commandItems = commandDB.getAllCommands();
        for (int i = 0; i < commandItems.size(); i++) {
            String p = commandItems.get(i).getPath();
            if (p != null && p.equals(path))
                commandDB.delete(i);
        }

        commandDB.putCommand(path, command);
        commandDB.commit();
    }

    private static void run(String command, String path, Context context) {
        RootUtils.runCommand(command);
        commandSaver(context, path, command);
        Log.i(TAG, "Run command: " + command);
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

    public static void setProp(String key, String value, Context context) {
        run("setprop " + key + " " + value, key, context);
    }

    public static void startService(String service, Context context) {
        RootUtils.runCommand("start " + service);

        if (context != null) commandSaver(context, service, "start " + service);
    }

    public static void stopService(String service, Context context) {
        RootUtils.runCommand("stop " + service);

        if (context != null) commandSaver(context, service, "stop " + service);
    }

    private static final List<Thread> tasks = new ArrayList<>();

    public static void runCommand(final String value, final String file, final CommandType command, final String id,
                                  final Context context) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (command == CommandType.CPU || command == CommandType.CPU_LITTLE) {
                    boolean mpd = false;
                    if (CPUHotplug.hasMpdecision() && CPUHotplug.isMpdecisionActive()) {
                        mpd = true;
                        stopService(HOTPLUG_MPDEC, null);
                    }

                    List<Integer> range = command == CommandType.CPU ? CPU.getBigCoreRange() : CPU.getLITTLECoreRange();
                    for (int i = 0; i < range.size(); i++) {
                        if (i != 0)
                            Control.run(String.format("echo 1 > " + CPU_CORE_ONLINE, i),
                                    String.format(CPU_CORE_ONLINE, i) + "cpuonline", context);
                        setPermission(String.format(file, range.get(i)), 644, context);
                        runGeneric(String.format(file, range.get(i)), value, id, context);
                        setPermission(String.format(file, range.get(i)), 444, context);
                    }

                    if (mpd) startService(HOTPLUG_MPDEC, null);
                } else if (command == CommandType.GENERIC) {
                    runGeneric(file, value, id, context);
                } else if (command == CommandType.FAUX_GENERIC) {
                    runFauxGeneric(file, value, context);
                } else if (command == CommandType.CUSTOM) {
                    Control.run(value, id == null ? file : file + id, context);
                }
            }
        });

        tasks.add(thread);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) if (tasks.get(0) == thread) {
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tasks.remove(thread);
                    break;
                }
            }
        }).start();
    }

    public static void runCommand(final String value, final String file, final CommandType command, final Context context) {
        runCommand(value, file, command, null, context);
    }

}
