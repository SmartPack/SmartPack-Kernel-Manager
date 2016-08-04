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
package com.grarak.kerneladiutor.utils.kernel.io;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.06.16.
 */
public abstract class IO {

    private static final List<String> sInternalScheduler = new ArrayList<>();
    private static final List<String> sInternalIOSched = new ArrayList<>();
    private static final List<String> sInternalReadAhead = new ArrayList<>();

    private static final List<String> sExternalScheduler = new ArrayList<>();
    private static final List<String> sExternalIOSched = new ArrayList<>();
    private static final List<String> sExternalReadAhead = new ArrayList<>();

    static {
        sInternalScheduler.add("/sys/block/mmcblk0/queue/scheduler");
        sInternalScheduler.add("/sys/block/dm-0/queue/scheduler");
        sInternalScheduler.add("/sys/block/sda/queue/scheduler");

        sInternalIOSched.add("/sys/block/mmcblk0/queue/iosched");
        sInternalIOSched.add("/sys/block/dm-0/queue/iosched");
        sInternalIOSched.add("/sys/block/sda/queue/iosched");

        sInternalReadAhead.add("/sys/block/mmcblk0/queue/read_ahead_kb");
        sInternalReadAhead.add("/sys/block/dm-0/queue/read_ahead_kb");
        sInternalReadAhead.add("/sys/block/sda/queue/read_ahead_kb");

        sExternalScheduler.add("/sys/block/mmcblk1/queue/scheduler");

        sExternalIOSched.add("/sys/block/mmcblk1/queue/iosched");

        sExternalReadAhead.add("/sys/block/mmcblk1/queue/read_ahead_kb");
    }

    private static String INTERNAL_SCHEDULER;
    private static String INTERNAL_IOSCHED;
    private static String INTERNAL_READ_AHEAD;

    private static String EXTERNAL_SCHEDULER;
    private static String EXTERNAL_IOSCHED;
    private static String EXTERNAL_READ_AHEAD;

    public static void setExternalReadahead(int value, Context context) {
        run(Control.write(String.valueOf(value), EXTERNAL_READ_AHEAD), EXTERNAL_READ_AHEAD, context);
    }

    public static int getExternalReadahead() {
        return getReadahead(EXTERNAL_READ_AHEAD);
    }

    public static String getExternalIOSched() {
        return EXTERNAL_IOSCHED;
    }

    public static void setExternalScheduler(String value, Context context) {
        run(Control.write(value, EXTERNAL_SCHEDULER), EXTERNAL_SCHEDULER, context);
    }

    public static String getExternalScheduler() {
        return getScheduler(EXTERNAL_SCHEDULER);
    }

    public static List<String> getExternalSchedulers() {
        return getSchedulers(EXTERNAL_SCHEDULER);
    }

    public static boolean hasExternal() {
        return EXTERNAL_SCHEDULER != null && EXTERNAL_IOSCHED != null && EXTERNAL_READ_AHEAD != null;
    }

    public static void setInternalReadahead(int value, Context context) {
        run(Control.write(String.valueOf(value), INTERNAL_READ_AHEAD), INTERNAL_READ_AHEAD, context);
    }

    public static int getInternalReadahead() {
        return getReadahead(INTERNAL_READ_AHEAD);
    }

    public static String getInternalIOSched() {
        return INTERNAL_IOSCHED;
    }

    public static void setInternalScheduler(String value, Context context) {
        run(Control.write(value, INTERNAL_SCHEDULER), INTERNAL_SCHEDULER, context);
    }

    public static String getInternalScheduler() {
        return getScheduler(INTERNAL_SCHEDULER);
    }

    public static List<String> getInternalSchedulers() {
        return getSchedulers(INTERNAL_SCHEDULER);
    }

    public static boolean supported() {
        if (INTERNAL_SCHEDULER == null) {
            INTERNAL_SCHEDULER = exists(sInternalScheduler);
        }
        if (INTERNAL_IOSCHED == null) {
            INTERNAL_IOSCHED = exists(sInternalIOSched);
        }
        if (INTERNAL_READ_AHEAD == null) {
            INTERNAL_READ_AHEAD = exists(sInternalReadAhead);
        }
        if (EXTERNAL_SCHEDULER == null) {
            EXTERNAL_SCHEDULER = exists(sExternalScheduler);
        }
        if (EXTERNAL_IOSCHED == null) {
            EXTERNAL_IOSCHED = exists(sExternalIOSched);
        }
        if (EXTERNAL_READ_AHEAD == null) {
            EXTERNAL_READ_AHEAD = exists(sExternalReadAhead);
        }
        return INTERNAL_SCHEDULER != null && INTERNAL_IOSCHED != null && INTERNAL_READ_AHEAD != null;
    }

    private static int getReadahead(String path) {
        return Utils.strToInt(Utils.readFile(path));
    }

    private static String getScheduler(String path) {
        String[] schedulers = Utils.readFile(path).split(" ");
        for (String scheduler : schedulers) {
            if (scheduler.startsWith("[") && scheduler.endsWith("]")) {
                return scheduler.replace("[", "").replace("]", "");
            }
        }
        return "";
    }

    private static List<String> getSchedulers(String path) {
        String[] schedulers = Utils.readFile(path).split(" ");
        List<String> list = new ArrayList<>();
        for (String scheduler : schedulers) {
            list.add(scheduler.replace("[", "").replace("]", ""));
        }
        return list;
    }

    private static String exists(List<String> files) {
        for (String file : files) {
            if (Utils.existFile(file)) {
                return file;
            }
        }
        return null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.IO, id, context);
    }

}
