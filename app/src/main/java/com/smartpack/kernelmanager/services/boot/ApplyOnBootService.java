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
package com.smartpack.kernelmanager.services.boot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.NotificationId;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootFile;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import com.smartpack.kernelmanager.utils.tools.ScriptManager;
import com.smartpack.kernelmanager.utils.kernel.wakelock.Wakelocks;

import org.frap129.spectrum.Spectrum;

/**
 * Created by willi on 03.05.16.
 */
public class ApplyOnBootService extends Service {

    static final String CHANNEL_ID = "onboot_notification_channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.apply_on_boot), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(
                    this, CHANNEL_ID);
            builder.setContentTitle(getString(R.string.apply_on_boot))
                    .setSmallIcon(R.drawable.ic_on_boot_notification);
            startForeground(NotificationId.APPLY_ON_BOOT, builder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * Initialize Spectrum Profiles, Wakelock Blocker & Execute Scripts
         */
        if (RootUtils.rootAccess()) {
            if (Spectrum.supported()) {
                int Profile = Utils.strToInt(Spectrum.getProfile());
                Prefs.saveInt("spectrum_profile", Profile, this);
            }
            if (Wakelocks.boefflawlsupported()) {
                Wakelocks.CopyWakelockBlockerDefault();
            }
            if (Prefs.getBoolean("scripts_onboot", false, this)
                    && !ScriptManager.list().isEmpty()) {
                for (final String script : ScriptManager.list()) {
                    if (Utils.getExtension(script).equals("sh")) {
                        RootFile file = new RootFile(script);
                        file.execute();
                    }
                }
            }
        }
        Messenger messenger = null;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                messenger = (Messenger) extras.get("messenger");
            }
        }

        if (messenger == null) {
            Utils.setupStartActivity(this);
        }

        boolean applyOnBoot = ApplyOnBoot.apply(this, new ApplyOnBoot.ApplyOnBootListener() {
            @Override
            public void onFinish() {
                stopSelf();
            }
        });

        if (!applyOnBoot) {
            if (messenger != null) {
                try {
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    messenger.send(message);
                } catch (RemoteException ignored) {
                }
            }
            stopSelf();
        }
        return START_NOT_STICKY;
    }

}
