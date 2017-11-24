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
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.database.tools.profiles.Profiles;
import com.grarak.kerneladiutor.services.boot.ApplyOnBoot;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 17.07.16.
 */
public class Widget extends AppWidgetProvider {

    private static final String TAG = Widget.class.getSimpleName();

    private static final String LIST_ITEM_CLICK = "list_item";
    private static final String ITEM_ARG = "item_extra";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
            widget.setRemoteAdapter(R.id.profile_list, svcIntent);

            widget.setPendingIntentTemplate(R.id.profile_list, getPendingIntent(context, LIST_ITEM_CLICK));

            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }
    }

    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(LIST_ITEM_CLICK)) {
            final int position = intent.getIntExtra(ITEM_ARG, 0);
            Profiles.ProfileItem profileItem = new Profiles(context).getAllProfiles().get(position);
            if (!Prefs.getBoolean("profileclicked" + position, false, context)) {
                Prefs.saveBoolean("profileclicked" + position, true, context);
                Utils.toast(context.getString(R.string.press_again_to_apply, profileItem.getName()),
                        context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            Prefs.saveBoolean("profileclicked" + position, false, context);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                Prefs.saveBoolean("profileclicked" + position, false, context);
                RootUtils.SU su = new RootUtils.SU(true, TAG);

                List<String> adjustedCommands = new ArrayList<>();
                for (Profiles.ProfileItem.CommandItem command : profileItem.getCommands()) {
                    CPUFreq.ApplyCpu applyCpu;
                    synchronized (this) {
                        if (command.getCommand().startsWith("#")
                                && (applyCpu = new CPUFreq.ApplyCpu(command.getCommand()
                                .substring(1))).toString() != null) {
                            adjustedCommands.addAll(ApplyOnBoot.getApplyCpu(applyCpu, su));
                        } else {
                            adjustedCommands.add(command.getCommand());
                        }
                    }
                }

                for (String command : adjustedCommands) {
                    su.runCommand(command);
                }
                su.close();
                Utils.toast(context.getString(R.string.applied), context);
            }
        }

    }

    private static class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;
        private List<Profiles.ProfileItem> mItems;

        private ListViewFactory(Context context) {
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onCreate() {
            mItems = new Profiles(mContext).getAllProfiles();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public void onDataSetChanged() {
            onCreate();
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_profile_item);

            row.setTextViewText(R.id.text, mItems.get(position).getName());

            Intent i = new Intent();
            Bundle extras = new Bundle();

            extras.putInt(ITEM_ARG, position);
            i.putExtras(extras);
            row.setOnClickFillInIntent(R.id.text, i);

            return (row);
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDestroy() {
        }

    }

    public static class WidgetService extends RemoteViewsService {

        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new ListViewFactory(getApplicationContext());
        }

    }

}
