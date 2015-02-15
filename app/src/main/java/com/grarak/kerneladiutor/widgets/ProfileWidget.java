package com.grarak.kerneladiutor.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.grarak.kerneladiutor.MainActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.tools.ProfileFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.ProfileDB;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.List;

/**
 * Created by willi on 13.02.15.
 */
public class ProfileWidget extends AppWidgetProvider {

    private static final String PROFILE_BUTTON = "profile_button";
    private static final String LIST_ITEM_CLICK = "list_item";

    private static final String ITEM_ARG = "item_extra";

    private static List<ProfileDB.ProfileItem> items;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.profile_widget_layout);
            widget.setRemoteAdapter(R.id.profile_list, svcIntent);

            widget.setPendingIntentTemplate(R.id.profile_list, getPendingIntent(context, LIST_ITEM_CLICK));
            widget.setOnClickPendingIntent(R.id.profile_button, getPendingIntent(context, PROFILE_BUTTON));

            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }

    }

    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction().equals(PROFILE_BUTTON)) {
            Bundle args = new Bundle();
            args.putString(MainActivity.LAUNCH_INTENT, ProfileFragment.class.getSimpleName());
            Intent launch = new Intent(context, MainActivity.class);
            launch.putExtras(args);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.destroy();
            context.startActivity(launch);
        } else if (intent.getAction().equals(LIST_ITEM_CLICK)) {
            if (!Utils.PROFILE_APPLY) {
                Utils.PROFILE_APPLY = true;
                Utils.toast(context.getString(R.string.press_again_to_apply), context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            Utils.PROFILE_APPLY = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                Utils.PROFILE_APPLY = false;
                int position = intent.getIntExtra(ITEM_ARG, 0);
                for (int i = 0; i < items.get(position).getSys().size(); i++) {
                    Control.commandSaver(context, items.get(position).getSys().get(i),
                            items.get(position).getCommands().get(i));
                    RootUtils.runCommand(items.get(position).getCommands().get(i));
                }
                Utils.toast(context.getString(R.string.applied), context);
            }
        }

        super.onReceive(context, intent);
    }

    private static class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context context;

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
            profileDB.create();

            items = profileDB.getAllProfiles();
            profileDB.close();
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
