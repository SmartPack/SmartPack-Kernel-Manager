/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.MainActivity;
import com.smartpack.kernelmanager.database.Settings;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.kernel.CPUHotplugFragment;
import com.smartpack.kernelmanager.services.profile.Tile;
import com.smartpack.kernelmanager.utils.NotificationId;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.cpu.MSMPerformance;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.CoreCtl;
import com.smartpack.kernelmanager.utils.kernel.cpuhotplug.MPDecision;
import com.smartpack.kernelmanager.utils.root.Control;
import com.smartpack.kernelmanager.utils.root.RootFile;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 24.11.17.
 */

public class ApplyOnBoot {

    private static final String TAG = ApplyOnBoot.class.getSimpleName();
    private static boolean sCancel;

    public interface ApplyOnBootListener {
        void onFinish();
    }

    static boolean apply(final Context context, final ApplyOnBootListener listener) {
        if (!Prefs.getBoolean(ApplyOnBootFragment.getAssignment(CPUHotplugFragment.class), false, context)) {
            Prefs.remove("core_ctl_min_cpus_big", context);
        }

        boolean enabled = false;
        final Settings settings = new Settings(context);

        final HashMap<String, Boolean> mCategoryEnabled = new HashMap<>();
        final List<String> mProfiles = new ArrayList<>();

        List<Profiles.ProfileItem> profiles = new Profiles(context).getAllProfiles();
        Tile.publishProfileTile(profiles, context);

        for (Settings.SettingsItem item : settings.getAllSettings()) {
            if (!mCategoryEnabled.containsKey(item.getCategory())) {
                boolean categoryEnabled = Prefs.getBoolean(item.getCategory(), false, context);
                mCategoryEnabled.put(item.getCategory(), categoryEnabled);
                if (!enabled && categoryEnabled) {
                    enabled = true;
                }
            }
        }
        for (Profiles.ProfileItem profileItem : profiles) {
            if (profileItem.isOnBootEnabled()) {
                for (Profiles.ProfileItem.CommandItem commandItem : profileItem.getCommands()) {
                    mProfiles.add(commandItem.getCommand());
                }
            }
        }

        enabled = enabled || mProfiles.size() > 0;
        if (!enabled) {
            return false;
        }

        final int seconds = Utils.strToInt(Prefs.getString("applyonbootdelay", "10", context));
        final boolean hideNotification = Prefs.getBoolean("applyonboothide", false, context);
        final boolean confirmationNotification = Prefs.getBoolean("applyonbootconfirmationnotification",
                true, context);
        final boolean toast = Prefs.getBoolean("applyonboottoast", false, context);
        final boolean script = Prefs.getBoolean("applyonbootscript", false, context);

        PendingIntent cancelIntent = PendingIntent.getBroadcast(context, 1,
                new Intent(context, CancelReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Intent launchIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);

        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, ApplyOnBootService.CHANNEL_ID);

        if (!hideNotification) {
            builder.setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.apply_on_boot_text, seconds))
                    .setSmallIcon(R.drawable.ic_on_boot_notification)
                    .addAction(0, context.getString(R.string.cancel), cancelIntent)
                    .setOngoing(true)
                    .setWhen(0);
            builder.setPriority(Notification.PRIORITY_MAX);
        }

        final NotificationCompat.Builder builderComplete =
                new NotificationCompat.Builder(context, ApplyOnBootService.CHANNEL_ID);
        if (!hideNotification) {
            builderComplete.setContentTitle(context.getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_on_boot_notification)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);
        }

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sCancel = false;
                for (int i = 0; i < seconds; i++) {
                    if (!hideNotification) {
                        if (sCancel) {
                            break;
                        }
                        builder.setContentText(context.getString(R.string.apply_on_boot_text, seconds - i));
                        builder.setProgress(seconds, i, false);
                        assert notificationManager != null;
                        notificationManager.notify(NotificationId.APPLY_ON_BOOT, builder.build());
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!hideNotification) {
                    assert notificationManager != null;
                    notificationManager.cancel(NotificationId.APPLY_ON_BOOT);
                    if (confirmationNotification) {
                        builderComplete.setContentText(context.getString(sCancel ? R.string.apply_on_boot_canceled :
                                R.string.apply_on_boot_complete));
                        notificationManager.notify(NotificationId.APPLY_ON_BOOT_CONFIRMATION, builderComplete.build());
                    }

                    if (sCancel) {
                        sCancel = false;
                        listener.onFinish();
                        return;
                    }
                }

                List<String> commands = new ArrayList<>();
                for (Settings.SettingsItem item : settings.getAllSettings()) {
                    String category = item.getCategory();
                    String setting = item.getSetting();
                    String id = item.getId();
                    CPUFreq.ApplyCpu applyCpu;
                    if (mCategoryEnabled.get(category)) {
                        if (category.equals(ApplyOnBootFragment.CPU)
                                && id.contains("%d")
                                && setting.startsWith("#")
                                && ((applyCpu =
                                new CPUFreq.ApplyCpu(setting.substring(1))).toString() != null)) {
                            synchronized (this) {
                                commands.addAll(getApplyCpu(applyCpu, context));
                            }
                        } else {
                            commands.add(setting);
                        }
                    }
                }

                if (script) {
                    StringBuilder s = new StringBuilder("#!/system/bin/sh\n\n");
                    for (String command : commands) {
                        s.append(command).append("\n");
                    }
                    RootFile file = new RootFile("/data/local/tmp/kerneladiutortmp.sh");
                    file.mkdir();
                    file.write(s.toString(), false);
                    file.execute();
                } else {
                    for (String command : commands) {
                        // not submit(), already running inside a thread anyways
                        Shell.su(command).exec();
                    }
                }

                List<String> profileCommands = new ArrayList<>();
                for (String command : mProfiles) {
                    CPUFreq.ApplyCpu applyCpu;
                    if (command.startsWith("#")
                            && ((applyCpu =
                            new CPUFreq.ApplyCpu(command.substring(1))).toString() != null)) {
                        synchronized (this) {
                            profileCommands.addAll(getApplyCpu(applyCpu, context));
                        }
                    }
                    profileCommands.add(command);
                }

                if (script) {
                    StringBuilder s = new StringBuilder("#!/system/bin/sh\n\n");
                    for (String command : profileCommands) {
                        s.append(command).append("\n");
                    }
                    RootFile file = new RootFile("/data/local/tmp/kerneladiutortmp.sh");
                    file.mkdir();
                    file.write(s.toString(), false);
                    file.execute();
                } else {
                    for (String command : profileCommands) {
                        // not submit(), already running inside a thread anyways
                        Shell.su(command).exec();
                    }
                }

                RootUtils.closeSU();

                if (toast) {
                    handler.post(() -> Utils.toast(R.string.apply_on_boot_complete, context));
                }

                listener.onFinish();
            }
        }).start();
        return true;
    }

    public static List<String> getApplyCpu(CPUFreq.ApplyCpu applyCpu) {
        return getApplyCpu(applyCpu, null);
    }

    private static List<String> getApplyCpu(CPUFreq.ApplyCpu applyCpu, Context context) {
        List<String> commands = new ArrayList<>();
        boolean cpulock = Utils.existFile(CPUFreq.CPU_LOCK_FREQ);
        if (cpulock) {
            commands.add(Control.write("0", CPUFreq.CPU_LOCK_FREQ));
        }
        boolean mpdecision = Utils.hasProp(MPDecision.HOTPLUG_MPDEC)
                && Utils.isPropRunning(MPDecision.HOTPLUG_MPDEC);
        if (mpdecision) {
            commands.add(Control.stopService(MPDecision.HOTPLUG_MPDEC));
        }
        for (int i = applyCpu.getMin(); i <= applyCpu.getMax(); i++) {
            boolean offline = !Utils.existFile(Utils.strFormat(applyCpu.getPath(), i));

            List<Integer> bigCpuRange = applyCpu.getBigCpuRange();
            List<Integer> LITTLECpuRange = applyCpu.getLITTLECpuRange();
            String coreCtlMinPath = null;
            String msmPerformanceMinPath = null;
            if (offline) {

                if (applyCpu.isBigLITTLE()) {
                    if (Utils.existFile(Utils.strFormat(CoreCtl.CORE_CTL, i))) {
                        coreCtlMinPath = Utils.strFormat(CoreCtl.CORE_CTL + CoreCtl.MIN_CPUS, i);
                        commands.add(Control.write(String.valueOf(bigCpuRange.size()), coreCtlMinPath));
                    }

                    if (Utils.existFile(MSMPerformance.MAX_CPUS)) {
                        msmPerformanceMinPath = MSMPerformance.MAX_CPUS;
                        commands.add(Control.write(LITTLECpuRange.size() + ":" + bigCpuRange.size(),
                                msmPerformanceMinPath));
                    }
                }

                commands.add(Control.write("1", Utils.strFormat(CPUFreq.CPU_ONLINE, i)));
            }
            commands.add(Control.chmod("644", Utils.strFormat(applyCpu.getPath(), i)));
            commands.add(Control.write(applyCpu.getValue(), Utils.strFormat(applyCpu.getPath(), i)));
            commands.add(Control.chmod("444", Utils.strFormat(applyCpu.getPath(), i)));
            if (offline) {

                if (coreCtlMinPath != null) {
                    commands.add(Control.write(String.valueOf(context == null ?
                            CPUFreq.getInstance().mCoreCtlMinCpu : Prefs.getInt("core_ctl_min_cpus_big",
                            applyCpu.getCoreCtlMin(), context)), coreCtlMinPath));
                }
                if (msmPerformanceMinPath != null) {
                    commands.add(Control.write("-1:-1", msmPerformanceMinPath));
                }

                commands.add(Control.write("0", Utils.strFormat(CPUFreq.CPU_ONLINE, i)));
            }
        }
        if (mpdecision) {
            commands.add(Control.startService(MPDecision.HOTPLUG_MPDEC));
        }
        if (cpulock) {
            commands.add(Control.write("1", CPUFreq.CPU_LOCK_FREQ));
        }
        return commands;
    }

    public static class CancelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sCancel = true;
        }

    }

}
