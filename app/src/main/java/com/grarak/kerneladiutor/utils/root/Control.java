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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.grarak.kerneladiutor.database.Settings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by willi on 02.05.16.
 */
public class Control {

    private static final String TAG = Control.class.getSimpleName();

    private static Control sControl;

    private boolean mProfileMode;
    private LinkedHashMap<String, String> mProfileCommands = new LinkedHashMap<>();

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public static String setProp(String prop, String value) {
        return "setprop " + prop + " " + value;
    }

    public static String startService(String prop) {
        return setProp("ctl.start", prop) + ";start " + prop;
    }

    public static String stopService(String prop) {
        return setProp("ctl.stop", prop) + ";stop " + prop;
    }

    public static String write(String text, String path) {
        return "echo '" + text + "' > " + path;
    }

    public static String chmod(String permission, String file) {
        return "chmod " + permission + " " + file;
    }

    public static String chown(String group, String file) {
        return "chown " + group + " " + file;
    }

    private void apply(String command, String category, String id, final Context context) {
        if (context != null) {
            if (mProfileMode) {
                Log.i(TAG, "Added to profile: " + id);
                mProfileCommands.put(id, command);
            } else {
                Settings settings = new Settings(context);
                List<Settings.SettingsItem> items = settings.getAllSettings();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getId().equals(id)
                            && items.get(i).getCategory().equals(category)) {
                        settings.delete(i);
                    }
                }
                settings.putSetting(category, command, id);
                settings.commit();
                Log.i(TAG, "saved " + id);
            }
        }

        if (command.startsWith("#")) return;
        RootUtils.runCommand(command);
        Log.i(TAG, command);
    }

    private synchronized void run(final String command, final String category, final String id,
                                  final Context context) {
        mExecutor.execute(() -> apply(command, category, id, context));
    }

    private static Control getInstance() {
        if (sControl == null) {
            sControl = new Control();
        }
        return sControl;
    }

    public static void setProfileMode(boolean mode) {
        getInstance().mProfileMode = mode;
    }

    public static LinkedHashMap<String, String> getProfileCommands() {
        return getInstance().mProfileCommands;
    }

    public static void clearProfileCommands() {
        getInstance().mProfileCommands.clear();
    }

    public static void runSetting(String command, String category, String id, Context context) {
        getInstance().run(command, category, id, context);
    }

}
