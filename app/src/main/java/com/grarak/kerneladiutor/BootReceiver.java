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

package com.grarak.kerneladiutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUHotplugFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltageFragment;
import com.grarak.kerneladiutor.fragments.kernel.GPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.IOFragment;
import com.grarak.kerneladiutor.fragments.kernel.KSMFragment;
import com.grarak.kerneladiutor.fragments.kernel.LMKFragment;
import com.grarak.kerneladiutor.fragments.kernel.MiscFragment;
import com.grarak.kerneladiutor.fragments.kernel.ScreenFragment;
import com.grarak.kerneladiutor.fragments.kernel.SoundFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.fragments.kernel.WakeFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.SysDB;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class BootReceiver extends BroadcastReceiver implements Constants {

    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> applys = new ArrayList<>();

        Class[] classes = new Class[]{
                BatteryFragment.class, CPUFragment.class, CPUHotplugFragment.class,
                CPUVoltageFragment.class, GPUFragment.class, IOFragment.class,
                KSMFragment.class, LMKFragment.class, MiscFragment.class,
                ScreenFragment.class, SoundFragment.class, VMFragment.class,
                WakeFragment.class,
        };

        for (Class mClass : classes)
            if (Utils.getBoolean(mClass.getSimpleName() + "onboot", false, context)) {
                log("Applying on boot for " + mClass.getSimpleName());
                applys.addAll(Utils.getApplys(mClass));
            }

        if (applys.size() > 0) {
            boolean hasRoot = false;
            boolean hasBusybox = false;
            if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
            if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();

            String message = context.getString(R.string.failed_apply_on_boot);
            if (!hasRoot) message += ": " + context.getString(R.string.no_root);
            else if (!hasBusybox) message += ": " + context.getString(R.string.no_busybox);

            if (!hasRoot || !hasBusybox) {
                toast(message, context);
                return;
            }

            for (String sys : applys) log(getClass().getSimpleName() + ": applys: " + sys);

            SysDB sysDB = new SysDB(context);
            sysDB.create();

            RootUtils.su = new RootUtils.SU();
            for (SysDB.SysItem sysItem : sysDB.getAllSys())
                for (String sys : applys)
                    if (sys.contains(sysItem.getSys()) || sysItem.getSys().contains(sys)) {
                        log(getClass().getSimpleName() + ": run: " + sysItem.getCommand());
                        RootUtils.runCommand(sysItem.getCommand());
                    }

            sysDB.close();

            if (RootUtils.su != null) RootUtils.su.close();
            toast(context.getString(R.string.apply_on_boot_summary), context);
        }
    }

    private void log(String log) {
        Log.i(TAG, getClass().getSimpleName() + ": " + log);
    }

    private void toast(String message, Context context) {
        Utils.toast(context.getString(R.string.app_name) + ": " + message, context);
    }

}
