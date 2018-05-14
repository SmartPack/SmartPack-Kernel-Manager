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
package com.grarak.kerneladiutor.utils.kernel.misc;

import android.content.Context;
import android.support.annotation.StringRes;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by willi on 30.06.16.
 */
public class Wakelocks {

    private static final String BOEFFLAWL = "/sys/devices/virtual/misc/boeffla_wakelock_blocker";
    private static final String VERSION = BOEFFLAWL + "/version";
    private static final String DEBUG = BOEFFLAWL + "/debug";
    private static final String WAKELOCK_BLOCKER = BOEFFLAWL + "/wakelock_blocker";
    private static final String WAKELOCK_BLOCKER_DEFAULT = BOEFFLAWL + "/wakelock_blocker_default";
    private static final String WAKELOCK_SOURCES = "/sys/kernel/debug/wakeup_sources";

    // Wakelocks Order: 0-Name, 1-Time, 2-Wakeups
    private static int mWakelockOrder = 1;

    public static String getboefflawlVersion(){
        return Utils.readFile(VERSION);
    }

    public static void CopyWakelockBlockerDefault(){
        try {
            String wbd = Utils.readFile(WAKELOCK_BLOCKER_DEFAULT);
            if (!wbd.contentEquals("")) {
                String list = "";
                try {
                    list = Utils.readFile(WAKELOCK_BLOCKER);
                    if (list.contentEquals("")) {
                        list = wbd;
                    } else {
                        list = list + ";" + wbd;
                    }
                } catch (Exception ignored) {
                }

                RootUtils.runCommand("echo '" + list + "' > " + WAKELOCK_BLOCKER);
                RootUtils.runCommand("echo '" + "" + "' > " + WAKELOCK_BLOCKER_DEFAULT);
            }
        }catch(Exception ignored){
        }
    }

    public static boolean isWakelockBlocked(String wakelock){
        try {
            String[] wbs = Utils.readFile(WAKELOCK_BLOCKER).split(";");
            for (String wb : wbs) {
                if (wb.contentEquals(wakelock)) {
                    return true;
                }
            }
        }catch (Exception ignored){
        }
        return false;
    }

    public static void setWakelockBlocked(String wakelock, Context context){
        String list = "";
        try {
            list = Utils.readFile(WAKELOCK_BLOCKER);
            if (list.contentEquals("")) {
                list = wakelock;
            } else {
                list += ";" + wakelock;
            }
        } catch (Exception ignored){
        }

        run(Control.write(list, WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    public static void setWakelockAllowed(String wakelock, Context context){
        String list = "";
        try {
            String[] wakes = Utils.readFile(WAKELOCK_BLOCKER).split(";");
            for(String wake : wakes){
                if(!wake.contentEquals(wakelock)){
                    if (list.contentEquals("")) {
                        list = wake;
                    } else {
                        list += ";" + wake;
                    }
                }
            }
        } catch (Exception ignored){
        }

        run(Control.write(list, WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    private static List<String> getWakelockNames(){
        List<String> list = new ArrayList<>();
        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    list.add(wl[0]);
                }
            }
        }catch (Exception ignored) {
        }
        return list;
    }

    private static List<Integer> getWakelockTimes(){
        List<Integer> list = new ArrayList<>();
        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    list.add(Utils.strToInt(wl[6]));
                }
            }
        }catch (Exception ignored) {
        }
        return list;
    }

    private static List<Integer> getWakelockWakeups(){
        List<Integer> list = new ArrayList<>();
        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    list.add(Utils.strToInt(wl[3]));
                }
            }
        }catch (Exception ignored) {
        }
        return list;
    }

    public static void setWakelockOrder(int order){
        mWakelockOrder = order;
    }

    public static List<ListWake> getWakelockList(){

        List<ListWake> list = new ArrayList<>();

        try {
            List<String> ListName = getWakelockNames();
            List<Integer> ListTime = getWakelockTimes();
            List<Integer> ListWakeup = getWakelockWakeups();

            for (int i = 0; i < ListName.size(); i++) {
                list.add(new ListWake(ListName.get(i), ListTime.get(i), ListWakeup.get(i)));
            }

            Collections.sort(list, new Comparator<ListWake>() {
                @Override
                public int compare(ListWake w2, ListWake w1) {
                    if(mWakelockOrder == 0) {
                        return w1.getName().compareTo(w2.getName());
                    } else if ( mWakelockOrder == 1){
                        return Integer.valueOf(w1.getTime()).compareTo(w2.getTime());
                    } else if (mWakelockOrder == 2){
                        return Integer.valueOf(w1.getWakeup()).compareTo(w2.getWakeup());
                    }

                    return 0;
                }
            });

        }catch (Exception ignored){
        }

        return list;
    }

    public static List<ListWake> getWakelockListBlocked(){

        List<ListWake> list = new ArrayList<>();

        try {
            List<String> ListName = getWakelockNames();
            List<Integer> ListTime = getWakelockTimes();
            List<Integer> ListWakeup = getWakelockWakeups();

            for (int i = 0; i < ListName.size(); i++) {
                if(isWakelockBlocked(ListName.get(i))) {
                    list.add(new ListWake(ListName.get(i), ListTime.get(i), ListWakeup.get(i)));
                }
            }

            Collections.sort(list, new Comparator<ListWake>() {
                @Override
                public int compare(ListWake w2, ListWake w1) {
                    return 0;
                }
            });

        }catch (Exception ignored){
        }

        return list;
    }

    public static List<ListWake> getWakelockListAllowed(){

        List<ListWake> list = new ArrayList<>();

        try {
            List<String> ListName = getWakelockNames();
            List<Integer> ListTime = getWakelockTimes();
            List<Integer> ListWakeup = getWakelockWakeups();

            for (int i = 0; i < ListName.size(); i++) {
                if(!isWakelockBlocked(ListName.get(i))) {
                    list.add(new ListWake(ListName.get(i), ListTime.get(i), ListWakeup.get(i)));
                }
            }

            Collections.sort(list, new Comparator<ListWake>() {
                @Override
                public int compare(ListWake w2, ListWake w1) {
                    if(mWakelockOrder == 0) {
                        return w2.getName().compareTo(w1.getName());
                    } else if ( mWakelockOrder == 1){
                        return Integer.valueOf(w1.getTime()).compareTo(w2.getTime());
                    } else if (mWakelockOrder == 2){
                        return Integer.valueOf(w1.getWakeup()).compareTo(w2.getWakeup());
                    }
                    return 0;
                }
            });

        }catch (Exception ignored){
        }

        return list;
    }

    public static boolean boefflawlsupported() {
        return Utils.existFile(BOEFFLAWL);
    }

    public static class ListWake {

        private String mName;
        private int mTime;
        private int mWakeup;

        ListWake(String name, int time, int wakeup){
            mName = name;
            mTime = time;
            mWakeup = wakeup;
        }

        public String getName(){
            return mName;
        }

        public int getTime(){
            return mTime;
        }

        public int getWakeup() {
            return mWakeup;
        }
    }

    public static boolean supported() {
        return boefflawlsupported();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
