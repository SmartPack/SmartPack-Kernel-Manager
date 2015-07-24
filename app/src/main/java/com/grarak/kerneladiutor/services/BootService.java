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

package com.grarak.kerneladiutor.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.grarak.kerneladiutor.MainActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUHotplugFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltageFragment;
import com.grarak.kerneladiutor.fragments.kernel.EntropyFragment;
import com.grarak.kerneladiutor.fragments.kernel.GPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.IOFragment;
import com.grarak.kerneladiutor.fragments.kernel.KSMFragment;
import com.grarak.kerneladiutor.fragments.kernel.LMKFragment;
import com.grarak.kerneladiutor.fragments.kernel.MiscFragment;
import com.grarak.kerneladiutor.fragments.kernel.ScreenFragment;
import com.grarak.kerneladiutor.fragments.kernel.SoundFragment;
import com.grarak.kerneladiutor.fragments.kernel.ThermalFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.fragments.kernel.WakeFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.CommandDB;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 08.03.15.
 */
public class BootService extends Service {

    private Handler hand = new Handler();

    private int id = 1;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("initialize");
        init();
    }

    private void init() {
        final List<String> applys = new ArrayList<>();

        Class[] classes = {BatteryFragment.class, CPUFragment.class, CPUHotplugFragment.class,
                CPUVoltageFragment.class, EntropyFragment.class, GPUFragment.class, IOFragment.class,
                KSMFragment.class, LMKFragment.class, MiscFragment.class,
                ScreenFragment.class, SoundFragment.class, ThermalFragment.class,
                VMFragment.class, WakeFragment.class
        };

        for (Class mClass : classes)
            if (Utils.getBoolean(mClass.getSimpleName() + "onboot", false, this)) {
                log("Applying on boot for " + mClass.getSimpleName());
                applys.addAll(Utils.getApplys(mClass));
            }

        if (applys.size() > 0) {
            final int delay = Utils.getInt("applyonbootdelay", 5, this);
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle(getString(R.string.apply_on_boot))
                    .setContentText(getString(R.string.apply_on_boot_time, delay))
                    .setSmallIcon(R.drawable.ic_launcher_preview);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean notification = Utils.getBoolean("applyonbootnotification", true, BootService.this);
                    for (int i = delay; i >= 0; i--)
                        try {
                            Thread.sleep(1000);
                            String note = getString(R.string.apply_on_boot_time, i);
                            if (notification) {
                                mBuilder.setContentText(note).setProgress(delay, delay - i, false);
                                mNotifyManager.notify(id, mBuilder.build());
                            } else if ((i % 10 == 0 || i == delay) && i != 0) toast(note);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    if (notification) {
                        mBuilder.setContentText(getString(R.string.apply_on_boot_finished)).setProgress(0, 0, false);
                        mNotifyManager.notify(id, mBuilder.build());
                    }
                    apply(applys);
                    stopSelf();
                }
            }).start();
        } else stopSelf();
    }

    private void apply(List<String> applys) {
        boolean hasRoot = false;
        boolean hasBusybox = false;
        if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
        if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();
        RootUtils.closeSU();

        String message = getString(R.string.apply_on_boot_failed);
        if (!hasRoot) message += ": " + getString(R.string.no_root);
        else if (!hasBusybox) message += ": " + getString(R.string.no_busybox);

        if (!hasRoot || !hasBusybox) {
            toast(message);
            mBuilder.setContentText(message);
            mNotifyManager.notify(id, mBuilder.build());
            return;
        }

        RootUtils.SU su = new RootUtils.SU();
        String[] writePermission = {Constants.LMK_MINFREE};
        for (String file : writePermission)
            su.runCommand("chmod 644 " + file);

        List<String> commands = new ArrayList<>();
        for (CommandDB.CommandItem commandItem : new CommandDB(this).getAllCommands())
            for (String sys : applys) {
                String path = commandItem.getPath();
                if ((sys.contains(path) || path.contains(sys))) {
                    String command = commandItem.getCommand();
                    if (commands.indexOf(command) < 0)
                        commands.add(command);
                }
            }

        for (String command : commands) {
            log("run: " + command);
            su.runCommand(command);
        }

        su.close();
        toast(getString(R.string.apply_on_boot_finished));
    }

    private void log(String log) {
        Log.i(Constants.TAG, "BootService: " + log);
    }

    private void toast(final String message) {
        if (Utils.getBoolean("applyonbootshowtoast", true, getApplicationContext()))
            hand.post(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(getString(R.string.app_name) + ": " + message, BootService.this);
                }
            });
    }

}
