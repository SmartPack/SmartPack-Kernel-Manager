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
package com.grarak.kerneladiutor.services.profile;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.database.tools.profiles.Profiles;
import com.grarak.kerneladiutor.services.boot.ApplyOnBoot;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

/**
 * Created by willi on 21.07.16.
 */
public class Tile extends BroadcastReceiver {

    private static final String TAG = Tile.class.getSimpleName();

    private static final String NAME = "name";
    private static final String COMMANDS = "commands";
    private static final String ACTION_TOGGLE_STATE = "com.grarak.kerneladiutor.action.ACTION_TOGGLE_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_TOGGLE_STATE.equals(intent.getAction())) {
            String name = intent.getStringExtra(NAME);
            if (name != null) Log.i(TAG, name);
            String[] commands = intent.getStringArrayExtra(COMMANDS);
            if (commands != null) {
                List<String> adjustedCommands = new ArrayList<>();
                RootUtils.SU su = new RootUtils.SU(true, TAG);
                for (String command : commands) {
                    synchronized (this) {
                        CPUFreq.ApplyCpu applyCpu;
                        if (command.startsWith("#") && command.contains("%d")
                                && (applyCpu = new CPUFreq.ApplyCpu(command.substring(1))).toString() != null) {
                            adjustedCommands.addAll(ApplyOnBoot.getApplyCpu(applyCpu, su));
                        } else {
                            adjustedCommands.add(command);
                        }
                    }
                }

                for (String command : adjustedCommands) {
                    su.runCommand(command);
                }
            }
        }
    }

    public static void publishProfileTile(List<Profiles.ProfileItem> profiles, Context context) {
        if (!Utils.hasCMSDK()) return;
        if (profiles == null || profiles.size() < 1 || !Prefs.getBoolean("profiletile", true, context)) {
            try {
                CMStatusBarManager.getInstance(context).removeTile(0);
            } catch (RuntimeException ignored) {
            }
            return;
        }

        Intent intent = new Intent();
        intent.setAction(ACTION_TOGGLE_STATE);

        ArrayList<CustomTile.ExpandedListItem> expandedListItems = new ArrayList<>();
        for (int i = 0; i < profiles.size(); i++) {
            CustomTile.ExpandedListItem expandedListItem = new CustomTile.ExpandedListItem();
            expandedListItem.setExpandedListItemTitle(profiles.get(i).getName());
            expandedListItem.setExpandedListItemDrawable(R.drawable.ic_launcher_preview);

            List<String> commands = new ArrayList<>();
            for (Profiles.ProfileItem.CommandItem commandItem : profiles.get(i).getCommands()) {
                commands.add(commandItem.getCommand());
            }
            intent.putExtra(NAME, profiles.get(i).getName());
            intent.putExtra(COMMANDS, commands.toArray(new String[commands.size()]));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);

            expandedListItem.setExpandedListItemOnClickIntent(pendingIntent);
            expandedListItems.add(expandedListItem);
        }

        CustomTile.ListExpandedStyle listExpandedStyle = new CustomTile.ListExpandedStyle();
        listExpandedStyle.setListItems(expandedListItems);

        CustomTile mCustomTile = new CustomTile.Builder(context)
                .setExpandedStyle(listExpandedStyle)
                .setLabel(R.string.profile)
                .setIcon(R.drawable.ic_launcher_preview)
                .build();
        try {
            CMStatusBarManager.getInstance(context).publishTile(0, mCustomTile);
        } catch (Exception e) {
            Prefs.saveBoolean("profiletile", false, context);
        }
    }

}
