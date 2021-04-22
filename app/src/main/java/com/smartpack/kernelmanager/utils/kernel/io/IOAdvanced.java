/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils.kernel.io;

import android.content.Context;

import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.Control;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 14, 2021
 */
public class IOAdvanced {

    private static IOAdvanced sInstance;

    public static IOAdvanced getInstance() {
        if (sInstance == null) {
            sInstance = new IOAdvanced();
        }
        return sInstance;
    }

    private static final String IO_PARENT = "/sys/block";
    private static final String SCHEDULER = "scheduler";
    private static final String IOSCHED = "iosched";
    private static final String READ_AHEAD = "read_ahead_kb";
    private static final String ROTATIONAL = "rotational";
    private static final String IOSTATS = "iostats";
    private static final String ADD_RANDOM = "add_random";
    private static final String RQ_AFFINITY = "rq_affinity";
    private static final String NOMERGES = "nomerges";
    private static final String NR_REQUESTS = "nr_requests";

    public static List<String> getIOBlockList() {
        List<String> mTQLList = new ArrayList<>();
        for (File file : Objects.requireNonNull(SuFile.open(IO_PARENT).listFiles())) {
            if (Utils.existFile(IO_PARENT + "/" + file.getName() + "/queue")) {
                mTQLList.add(file.getName());
            }
        }
        return mTQLList;
    }

    public static String getCurrentBlock(Context context) {
        return Prefs.getString("mCurrentBlock", getIOBlockList().get(0), context);
    }

    public static void setCurrentBlock(int position, Context context) {
        Prefs.saveString("mCurrentBlock", getIOBlockList().get(position), context);
    }

    public static boolean hasScheduler(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + SCHEDULER);
    }

    public static String getScheduler(Context context) {
        String[] schedulers = Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + SCHEDULER).split(" ");
        for (String scheduler : schedulers) {
            if (scheduler.startsWith("[") && scheduler.endsWith("]")) {
                return scheduler.replace("[", "").replace("]", "");
            }
        }
        return "";
    }

    public static String getSchedulerPath(Context context) {
        return IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + IOSCHED;
    }

    public static List<String> getSchedulers(Context context) {
        String[] schedulers = Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + SCHEDULER).split(" ");
        List<String> list = new ArrayList<>();
        for (String scheduler : schedulers) {
            list.add(scheduler.replace("[", "").replace("]", ""));
        }
        return list;
    }

    public void setScheduler(String value, Context context) {
        run(Control.write(value, IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + SCHEDULER), IO_PARENT + "/" +
                getCurrentBlock(context) + "/queue/" + SCHEDULER, context);
    }

    public static boolean hasReadAhead(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + READ_AHEAD);
    }

    public static int getReadAhead(Context context) {
        return Utils.strToInt(Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + READ_AHEAD));
    }

    public void setReadAhead(int value, Context context) {
        run(Control.write(String.valueOf(value), IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + READ_AHEAD),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + READ_AHEAD, context);
    }

    public static boolean hasRotational(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ROTATIONAL);
    }

    public static boolean isRotationalEnabled(Context context) {
        return Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ROTATIONAL).equals("1");
    }

    public void enableRotational(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ROTATIONAL),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ROTATIONAL, context);
    }

    public static boolean hasIOStats(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + IOSTATS);
    }

    public static boolean isIOStatsEnabled(Context context) {
        return Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + IOSTATS).equals("1");
    }

    public void enableIOStats(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + IOSTATS),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + IOSTATS, context);
    }

    public static boolean hasRandom(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ADD_RANDOM);
    }

    public static boolean isRandomEnabled(Context context) {
        return Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ADD_RANDOM).equals("1");
    }

    public void enableRandom(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ADD_RANDOM),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + ADD_RANDOM, context);
    }

    public static boolean hasAffinity(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + RQ_AFFINITY);
    }

    public static int getAffinity(Context context) {
        return Utils.strToInt(Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + RQ_AFFINITY));
    }

    public void setAffinity(int value, Context context) {
        run(Control.write(String.valueOf(value), IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + RQ_AFFINITY),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + RQ_AFFINITY, context);
    }

    public static boolean hasNoMerges(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NOMERGES);
    }

    public static int getNoMerges(Context context) {
        return Utils.strToInt(Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NOMERGES));
    }

    public void setNoMerges(int value, Context context) {
        run(Control.write(String.valueOf(value), IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NOMERGES),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NOMERGES, context);
    }

    public static boolean hasNRRequests(Context context) {
        return Utils.existFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NR_REQUESTS);
    }

    public static String getNRRequests(Context context) {
        return Utils.readFile(IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NR_REQUESTS);
    }

    public void setNRRequests(String value, Context context) {
        run(Control.write(value, IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NR_REQUESTS),
                IO_PARENT + "/" + getCurrentBlock(context) + "/queue/" + NR_REQUESTS, context);
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.IO_ADVANCED, id, context);
    }

}