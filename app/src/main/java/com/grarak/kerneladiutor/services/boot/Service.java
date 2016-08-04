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
package com.grarak.kerneladiutor.services.boot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.MainActivity;
import com.grarak.kerneladiutor.activities.StartActivity;
import com.grarak.kerneladiutor.database.Settings;
import com.grarak.kerneladiutor.database.tools.customcontrols.Controls;
import com.grarak.kerneladiutor.database.tools.profiles.Profiles;
import com.grarak.kerneladiutor.services.profile.Tile;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 03.05.16.
 */
public class Service extends android.app.Service {

    private static final String TAG = Service.class.getSimpleName();
    private static boolean sCancel;

    private HashMap<String, Boolean> mCategoryEnabled = new HashMap<>();
    private HashMap<String, String> mCustomControls = new HashMap<>();
    private List<String> mProfiles = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        RootUtils.SU su = new RootUtils.SU(false, null);
        String prop = su.runCommand("getprop ro.kerneladiutor.hide");
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, StartActivity.class),
                prop != null && prop.equals("true") ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        su.close();

        Messenger messenger = null;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                messenger = (Messenger) extras.get("messenger");
            }
        }

        boolean enabled = false;
        final Settings settings = new Settings(this);
        Controls controls = new Controls(this);
        List<Profiles.ProfileItem> profiles = new Profiles(this).getAllProfiles();

        if (messenger == null) {
            Tile.publishProfileTile(profiles, this);
        }

        for (Settings.SettingsItem item : settings.getAllSettings()) {
            if (!mCategoryEnabled.containsKey(item.getCategory())) {
                mCategoryEnabled.put(item.getCategory(), Prefs.getBoolean(item.getCategory(), false, this));
            }
        }
        for (String key : mCategoryEnabled.keySet()) {
            if (mCategoryEnabled.get(key)) {
                enabled = true;
                break;
            }
        }
        for (Controls.ControlItem item : controls.getAllControls()) {
            if (item.isOnBootEnabled() && item.getArguments() != null) {
                mCustomControls.put(item.getApply(), item.getArguments());
            }
        }
        for (Profiles.ProfileItem profileItem : profiles) {
            if (profileItem.isOnBootEnabled()) {
                for (String commad : profileItem.getCommands()) {
                    mProfiles.add(commad);
                }
            }
        }

        final boolean initdEnabled = Prefs.getBoolean("initd_onboot", false, this);
        enabled = enabled || mCustomControls.size() > 0 || mProfiles.size() > 0 || initdEnabled;
        if (!enabled) {
            if (messenger != null) {
                try {
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    messenger.send(message);
                } catch (RemoteException ignored) {
                }
            }
            stopSelf();
            return;
        }

        final int seconds = Utils.strToInt(Prefs.getString("applyonbootdelay", "10", this));
        PendingIntent cancelIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, CancelReceiver.class), 0);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.apply_on_boot_text, seconds))
                .setSmallIcon(R.drawable.ic_restore)
                .addAction(0, getString(R.string.cancel), cancelIntent)
                .setAutoCancel(true)
                .setWhen(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_MAX);
        }

        final NotificationCompat.Builder builderComplete = new NotificationCompat.Builder(this);
        builderComplete.setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_restore);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < seconds; i++) {
                    if (sCancel) {
                        break;
                    }
                    builder.setContentText(getString(R.string.apply_on_boot_text, seconds - i));
                    builder.setProgress(seconds, i, false);
                    notificationManager.notify(0, builder.build());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                builderComplete.setContentText(getString(sCancel ? R.string.apply_on_boot_canceled :
                        R.string.apply_on_boot_complete));
                notificationManager.notify(0, builderComplete.build());

                if (sCancel) {
                    sCancel = false;
                    stopSelf();
                    return;
                }
                RootUtils.SU su = new RootUtils.SU(true, TAG);

                if (initdEnabled) {
                    RootUtils.mount(true, "/system", su);
                    su.runCommand("for i in `ls /system/etc/init.d`;do chmod 755 $i;done");
                    su.runCommand("[ -d /system/etc/init.d ] && run-parts /system/etc/init.d");
                    RootUtils.mount(false, "/system", su);
                }

                for (Settings.SettingsItem item : settings.getAllSettings()) {
                    if (mCategoryEnabled.get(item.getCategory())) {
                        synchronized (this) {
                            su.runCommand(item.getSetting());
                        }
                    }
                }

                for (String script : mCustomControls.keySet()) {
                    RootFile file = new RootFile("/data/local/tmp/kerneladiutortmp.sh", su);
                    file.mkdir();
                    file.write(script, false);
                    file.execute(mCustomControls.get(script));
                }

                for (String command : mProfiles) {
                    synchronized (this) {
                        su.runCommand(command);
                    }
                }

                su.close();
                stopSelf();
            }
        }).start();
    }

    public static class CancelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sCancel = true;
        }

    }

}
