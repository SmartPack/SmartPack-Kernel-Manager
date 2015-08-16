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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.ProfileDB;
import com.grarak.kerneladiutor.utils.root.Control;
import com.kerneladiutor.library.root.RootUtils;

import java.util.List;

/**
 * Created by willi on 13.02.15.
 */
public class ProfileWidget extends AppWidgetProvider {

    private static final String LIST_ITEM_CLICK = "list_item";

    private static final String ITEM_ARG = "item_extra";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            for (int appWidgetId : appWidgetIds) {
                Intent svcIntent = new Intent(context, WidgetService.class);
                svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

                RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.profile_widget_layout);
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
    public void onReceive(@NonNull final Context context, @NonNull Intent intent) {
        if (intent.getAction().equals(LIST_ITEM_CLICK)) {
            if (!Utils.getBoolean("profileclicked", false, context)) {
                Utils.saveBoolean("profileclicked", true, context);
                Utils.toast(context.getString(R.string.press_again_to_apply), context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            Utils.saveBoolean("profileclicked", false, context);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                Utils.saveBoolean("profileclicked", false, context);
                int position = intent.getIntExtra(ITEM_ARG, 0);
                ProfileDB profileDB = new ProfileDB(context);
                ProfileDB.ProfileItem profileItem = profileDB.getAllProfiles().get(position);
                RootUtils.SU su = new RootUtils.SU();

                List<String> paths = profileItem.getPath();
                for (int i = 0; i < paths.size(); i++) {
                    Control.commandSaver(context, paths.get(i), profileItem.getCommands().get(i));
                    su.runCommand(profileItem.getCommands().get(i));
                }
                su.close();
                Utils.toast(context.getString(R.string.applied), context);
            }
        }

        super.onReceive(context, intent);
    }

    private static class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context context;
        public static List<ProfileDB.ProfileItem> items;

        public ListViewFactory(Context context) {
            this.context = context;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onCreate() {
            ProfileDB profileDB = new ProfileDB(context);
            items = profileDB.getAllProfiles();
        }

        @Override
        public int getCount() {
            return items.size();
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
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.profile_list);

            row.setTextViewText(R.id.text, items.get(position).getName());

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
