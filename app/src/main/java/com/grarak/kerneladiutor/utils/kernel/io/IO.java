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

    private static final String SCHEDULER = "scheduler";
    private static final String IOSCHED = "iosched";
    private static final String READ_AHEAD = "read_ahead_kb";
    private static final String ROTATIONAL = "rotational";
    private static final String IOSTATS = "iostats";
    private static final String ADD_RANDOM = "add_random";
    private static final String RQ_AFFINITY = "rq_affinity";

    private static final List<String> sInternal = new ArrayList<>();
    private static final List<String> sExternal = new ArrayList<>();

    static {
        sInternal.add("/sys/block/dm-0/queue");
        sInternal.add("/sys/block/sda/queue");
        sInternal.add("/sys/block/mmcblk0/queue");

        sExternal.add("/sys/block/mmcblk1/queue");
        sExternal.add("/sys/block/mmcblk0/queue");
    }

    public enum Storage {
        Internal,
        External
    }

    private static String INTERNAL;
    private static String EXTERNAL;

    public static boolean hasExternal() {
        return EXTERNAL != null;
    }

    public static void setRqAffinity(Storage storage, int value, Context context) {
        run(Control.write(String.valueOf(value), getPath(storage, RQ_AFFINITY)),
                getPath(storage, RQ_AFFINITY), context);
    }

    public static int getRqAffinity(Storage storage) {
        return Utils.strToInt(Utils.readFile(getPath(storage, RQ_AFFINITY)));
    }

    public static boolean hasRqAffinity(Storage storage) {
        return Utils.existFile(getPath(storage, RQ_AFFINITY));
    }

    public static void enableAddRandom(Storage storage, boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getPath(storage, ADD_RANDOM)), getPath(storage, ADD_RANDOM),
                context);
    }

    public static boolean isAddRandomEnabled(Storage storage) {
        return Utils.readFile(getPath(storage, ADD_RANDOM)).equals("1");
    }

    public static boolean hasAddRandom(Storage storage) {
        return Utils.existFile(getPath(storage, ADD_RANDOM));
    }

    public static void enableIOstats(Storage storage, boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getPath(storage, IOSTATS)), getPath(storage, IOSTATS),
                context);
    }

    public static boolean isIOStatsEnabled(Storage storage) {
        return Utils.readFile(getPath(storage, IOSTATS)).equals("1");
    }

    public static boolean hasIOStats(Storage storage) {
        return Utils.existFile(getPath(storage, IOSTATS));
    }

    public static void enableRotational(Storage storage, boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", getPath(storage, ROTATIONAL)),
                getPath(storage, ROTATIONAL), context);
    }

    public static boolean isRotationalEnabled(Storage storage) {
        return Utils.readFile(getPath(storage, ROTATIONAL)).equals("1");
    }

    public static boolean hasRotational(Storage storage) {
        return Utils.existFile(getPath(storage, ROTATIONAL));
    }

    public static void setReadahead(Storage storage, int value, Context context) {
        run(Control.write(String.valueOf(value), getPath(storage, READ_AHEAD)),
                getPath(storage, READ_AHEAD), context);
    }

    public static int getReadahead(Storage storage) {
        return getReadahead(getPath(storage, READ_AHEAD));
    }

    public static boolean hasReadahead(Storage storage) {
        return Utils.existFile(getPath(storage, READ_AHEAD));
    }

    public static String getIOSched(Storage storage) {
        return getPath(storage, IOSCHED);
    }

    public static void setScheduler(Storage storage, String value, Context context) {
        run(Control.write(value, getPath(storage, SCHEDULER)), getPath(storage, SCHEDULER), context);
    }

    public static String getScheduler(Storage storage) {
        return getScheduler(getPath(storage, SCHEDULER));
    }

    public static List<String> getSchedulers(Storage storage) {
        return getSchedulers(getPath(storage, SCHEDULER));
    }

    public static boolean hasScheduler(Storage storage) {
        return Utils.existFile(getPath(storage, SCHEDULER));
    }

    public static boolean supported() {
        if (INTERNAL == null) {
            INTERNAL = exists(sInternal);
        }
        if (EXTERNAL == null) {
            EXTERNAL = exists(sExternal);
        }
        return INTERNAL != null;
    }

    private static String getPath(Storage storage, String file) {
        return storage == Storage.Internal ? INTERNAL + "/" + file : EXTERNAL + "/" + file;
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
