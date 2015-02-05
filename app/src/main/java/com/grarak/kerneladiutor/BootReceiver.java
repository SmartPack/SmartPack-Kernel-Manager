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
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.kernel.CPUVoltage;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class BootReceiver extends BroadcastReceiver implements Constants {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasRoot = false;
        boolean hasBusybox = false;
        if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
        if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();

        if (!hasRoot || !hasBusybox) return;

        apply(context);
    }

    private static void apply(Context context) {
        List<String> applys = new ArrayList<>();
        if (Utils.getBoolean(BatteryFragment.class.getSimpleName() + "onboot", false, context))
            applys.addAll(new ArrayList<>(Arrays.asList(BATTERY_ARRAY)));

        if (Utils.getBoolean(CPUFragment.class.getSimpleName() + "onboot", false, context)) {
            int cores = CPU.getCoreCount();
            for (String cpu : CPU_ARRAY)
                if (cpu.startsWith("/sys/devices/system/cpu/cpu%d/cpufreq"))
                    for (int i = 0; i < cores; i++)
                        applys.add(String.format(cpu, i));
                else applys.add(cpu);
        }

        if (Utils.getBoolean(CPUVoltage.class.getSimpleName() + "onboot", false, context))
            applys.addAll(new ArrayList<>(Arrays.asList(CPU_VOLTAGE_ARRAY)));

        if (Utils.getBoolean(GPUFragment.class.getSimpleName() + "onboot", false, context))
            for (String[] arrays : GPU_ARRAY) applys.addAll(new ArrayList<>(Arrays.asList(arrays)));

        if (Utils.getBoolean(IOFragment.class.getSimpleName() + "onboot", false, context))
            applys.addAll(new ArrayList<>(Arrays.asList(IO_ARRAY)));

        if (Utils.getBoolean(KSMFragment.class.getSimpleName() + "onboot", false, context))
            applys.add(KSM_FOLDER);

        if (Utils.getBoolean(LMKFragment.class.getSimpleName() + "onboot", false, context))
            applys.add(LMK_MINFREE);

        if (Utils.getBoolean(MiscFragment.class.getSimpleName() + "onboot", false, context))
            applys.addAll(new ArrayList<>(Arrays.asList(MISC_ARRAY)));

        if (Utils.getBoolean(ScreenFragment.class.getSimpleName() + "onboot", false, context))
            applys.addAll(new ArrayList<>(Arrays.asList(SCREEN_ARRAY)));

        if (Utils.getBoolean(SoundFragment.class.getSimpleName() + "onboot", false, context))
            applys.addAll(new ArrayList<>(Arrays.asList(SOUND_ARRAY)));

        if (Utils.getBoolean(VMFragment.class.getSimpleName() + "onboot", false, context))
            applys.add(VM_PATH);

        if (Utils.getBoolean(WakeFragment.class.getSimpleName() + "onboot", false, context))
            for (String[] arrays : WAKE_ARRAY)
                applys.addAll(new ArrayList<>(Arrays.asList(arrays)));

        if (applys.size() > 0) {
            for (String sys : applys)
                Log.i(TAG, context.getClass().getSimpleName() + ": applys: " + sys);

            SysDB sysDB = new SysDB(context);
            sysDB.create();

            RootUtils.su = new RootUtils.SU();
            for (SysDB.SysItem sysItem : sysDB.getAllSys())
                for (String sys : applys)
                    if (sys.contains(sysItem.getSys()) || sysItem.getSys().contains(sys)) {
                        Log.i(TAG, context.getClass().getSimpleName() + ": run: " + sysItem.getCommand());
                        RootUtils.runCommand(sysItem.getCommand());
                    }

            sysDB.close();

            if (RootUtils.su != null) RootUtils.su.close();
            Utils.toast(context.getString(R.string.apply_on_boot_summary), context);
        }

    }

}
