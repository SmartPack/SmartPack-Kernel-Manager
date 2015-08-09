package com.grarak.kerneladiutor.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.ProfileDB;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

/**
 * Created by willi on 09.08.15.
 */
public class ProfileTileReceiver extends BroadcastReceiver {

    private static final String STATE = "state";
    private static final String NAME = "name";
    private static final String COMMANDS = "commands";
    private static final String ACTION_TOGGLE_STATE = "com.grarak.kerneladiutor.ACTION_TOGGLE_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_TOGGLE_STATE.equals(intent.getAction())) {
            log(intent.getIntExtra(STATE, -1) + "");
            String[] commands = intent.getStringArrayExtra(COMMANDS);
            log("Applying: " + intent.getStringExtra(NAME));
            if (commands == null) return;
            if (!RootUtils.rootAccess()) {
                Utils.toast(context.getString(R.string.no_root), context);
                return;
            }
            if (!RootUtils.busyboxInstalled()) {
                Utils.toast(context.getString(R.string.no_busybox), context);
                return;
            }
            RootUtils.closeSU();
            RootUtils.SU su = new RootUtils.SU();
            for (String command : commands) {
                su.runCommand(command);
                Log.i(Constants.TAG + ": " + getClass().getSimpleName(), "Run: " + command);
            }
            su.close();
        }
    }

    private void log(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    public static void publishProfileTile(List<ProfileDB.ProfileItem> profiles, Context context) {
        if (!Utils.hasCMSDK()) return;
        if (profiles == null || profiles.size() < 1 || !Utils.getBoolean("profiletile", true, context)) {
            CMStatusBarManager.getInstance(context).removeTile(0);
            return;
        }

        Intent intent = new Intent();
        intent.setAction(ACTION_TOGGLE_STATE);
        intent.putExtra(STATE, 0);

        ArrayList<CustomTile.ExpandedGridItem> expandedGridItems = new ArrayList<>();
        for (ProfileDB.ProfileItem item : profiles) {
            CustomTile.ExpandedGridItem expandedGridItem = new CustomTile.ExpandedGridItem();
            expandedGridItem.setExpandedGridItemTitle(item.getName());
            expandedGridItem.setExpandedGridItemDrawable(R.drawable.ic_launcher_preview);

            intent.putExtra(NAME, item.getName());
            intent.putExtra(COMMANDS, item.getCommands().toArray(new String[item.getCommands().size()]));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            expandedGridItem.setExpandedGridItemOnClickIntent(pendingIntent);
            expandedGridItems.add(expandedGridItem);
        }

        CustomTile.GridExpandedStyle gridExpandedStyle = new CustomTile.GridExpandedStyle();
        gridExpandedStyle.setGridItems(expandedGridItems);

        CustomTile mCustomTile = new CustomTile.Builder(context)
                .setExpandedStyle(gridExpandedStyle)
                .setLabel(R.string.profile)
                .setContentDescription("lololo")
                .setIcon(R.drawable.ic_launcher_preview)
                .build();
        CMStatusBarManager.getInstance(context).publishTile(0, mCustomTile);
    }

}
