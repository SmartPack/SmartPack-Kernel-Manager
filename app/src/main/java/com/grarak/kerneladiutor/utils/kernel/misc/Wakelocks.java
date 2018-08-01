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

    public static int getWakelockOrder(){
        return mWakelockOrder;
    }

    public static void setWakelockOrder(int order){
        mWakelockOrder = order;
    }

    public static List<WakeLockInfo> getWakelockInfo(){

        List<WakeLockInfo> wakelocksinfo = new ArrayList<>();

        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    wakelocksinfo.add(new WakeLockInfo(wl[0], Integer.valueOf(wl[6]), Integer.valueOf(wl[3])));
                }
            }
        }catch (Exception ignored) {
        }

        String[] blocked = null;

        try {
            blocked = Utils.readFile(WAKELOCK_BLOCKER).split(";");
        }catch (Exception ignored){
        }

        if( blocked != null){
            for (String name_bloqued : blocked) {
                for (WakeLockInfo wakeLockInfo : wakelocksinfo) {
                    if (wakeLockInfo.wName.equals(name_bloqued)) {
                        wakeLockInfo.wState = false;
                        break;
                    }
                }
            }
        }

        Collections.sort(wakelocksinfo, (w2, w1) -> {
            if(mWakelockOrder == 0) {
                return w2.wName.compareTo(w1.wName);
            } else if ( mWakelockOrder == 1){
                return Integer.compare(w1.wTime, w2.wTime);
            } else if (mWakelockOrder == 2){
                return Integer.compare(w1.wWakeups, w2.wWakeups);
            }
            return 0;
        });

        return wakelocksinfo;
    }

    public static boolean boefflawlsupported() {
        return Utils.existFile(BOEFFLAWL);
    }

    public static boolean supported() {
        return boefflawlsupported();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.MISC, id, context);
    }

}
