/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.services.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grarak.kerneladiutor.services.monitor.Monitor;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 03.05.16.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            RootUtils.SU su = new RootUtils.SU();
            su.runCommand("echo /testRoot/");
            if (!su.denied) {
                Utils.startService(context, new Intent(context, ApplyOnBootService.class));
                if (Prefs.getBoolean("data_sharing", true, context)) {
                    Utils.startService(context, new Intent(context, Monitor.class));
                }
            }
            su.close();
        }
    }
}
