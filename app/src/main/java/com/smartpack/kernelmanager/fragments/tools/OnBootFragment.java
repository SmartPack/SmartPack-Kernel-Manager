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
package com.smartpack.kernelmanager.fragments.tools;

import android.content.DialogInterface;
import android.os.AsyncTask;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.database.Settings;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;
import com.smartpack.kernelmanager.utils.tools.ScriptManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 04.08.16.
 */
public class OnBootFragment extends RecyclerViewFragment {

    private Settings mSettings;
    private Profiles mProfiles;

    private boolean mLoaded;

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private Dialog mDeleteDialog;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.welcome),
                getString(R.string.on_boot_welcome_summary)));

        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mSettings == null) {
            mSettings = new Settings(getActivity());
        }
        if (mProfiles == null) {
            mProfiles = new Profiles(getActivity());
        }
        if (!mLoaded) {
            mLoaded = true;
            load(items);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            clearItems();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            load(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);
                            for (RecyclerViewItem item : recyclerViewItems) {
                                addItem(item);
                            }
                            hideProgress();
                            mLoader = null;
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private void load(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> applyOnBoot = new ArrayList<>();
        TitleView applyOnBootTitle = new TitleView();
        applyOnBootTitle.setText(getString(R.string.apply_on_boot));

        List<Settings.SettingsItem> settings = mSettings.getAllSettings();
        HashMap<String, Boolean> applyOnBootEnabled = new HashMap<>();
        List<ApplyOnBootItem> applyOnBootItems = new ArrayList<>();
        for (int i = 0; i < settings.size(); i++) {
            if (!applyOnBootEnabled.containsKey(settings.get(i).getCategory())) {
                applyOnBootEnabled.put(settings.get(i).getCategory(),
                        Prefs.getBoolean(settings.get(i).getCategory(), false, getActivity()));
            }
            if (applyOnBootEnabled.get(settings.get(i).getCategory())) {
                applyOnBootItems.add(new ApplyOnBootItem(settings.get(i).getSetting(),
                        settings.get(i).getCategory(), i));
            }
        }

        for (int i = 0; i < applyOnBootItems.size(); i++) {
            final ApplyOnBootItem applyOnBootItem = applyOnBootItems.get(i);
            DescriptionView applyOnBootView = new DescriptionView();
            applyOnBootView.setSummary((i + 1) + ". " + applyOnBootItem.mCategory.replace("_onboot", "")
                    + ": " + applyOnBootItem.mCommand);

            applyOnBootView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question,
                            applyOnBootItem.mCommand),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mSettings.delete(applyOnBootItem.mPosition);
                                    mSettings.commit();
                                    reload();
                                }
                            }, new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    mDeleteDialog = null;
                                }
                            }, getActivity());
                    mDeleteDialog.show();
                }
            });

            applyOnBoot.add(applyOnBootView);
        }

        if (applyOnBoot.size() > 0) {
            items.add(applyOnBootTitle);
            items.addAll(applyOnBoot);
        }

        List<RecyclerViewItem> profiles = new ArrayList<>();
        TitleView profileTitle = new TitleView();
        profileTitle.setText(getString(R.string.profile));

        for (final Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
            if (profileItem.isOnBootEnabled()) {
                DescriptionView profileView = new DescriptionView();
                profileView.setSummary(profileItem.getName());
                profileView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.disable_question,
                                profileItem.getName()),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        profileItem.enableOnBoot(false);
                                        mProfiles.commit();
                                        reload();
                                    }
                                }, new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        mDeleteDialog = null;
                                    }
                                }, getActivity());
                        mDeleteDialog.show();
                    }
                });

                profiles.add(profileView);
            }
        }

        if (profiles.size() > 0) {
            items.add(profileTitle);
            items.addAll(profiles);
        }

        if (Prefs.getBoolean("scripts_onboot", false, getActivity()) == true
                && !ScriptManager.list().isEmpty()) {
            for (final String script : ScriptManager.list()) {
                if (Utils.getExtension(script).equals("sh")) {
                    DescriptionView scriptItems = new DescriptionView();
                    scriptItems.setSummary(getString(R.string.script_manger) + ": " + script);
                    scriptItems.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                        @Override
                        public void onClick(RecyclerViewItem item) {
                            mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.remove_script_question, script),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ScriptManager.delete(script);
                                            if (ScriptManager.list().isEmpty()) {
                                                Prefs.saveBoolean("scripts_onboot", false, getActivity());
                                            }
                                            reload();
                                        }
                                    }, new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            mDeleteDialog = null;
                                        }
                                    }, getActivity());
                            mDeleteDialog.show();
                        }
                    });

                    items.add(scriptItems);
                }
            }
        }
    }

    private class ApplyOnBootItem {
        private final String mCommand;
        private final String mCategory;
        private final int mPosition;

        private ApplyOnBootItem(String command, String category, int position) {
            mCommand = command;
            mCategory = category;
            mPosition = position;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSettings = null;
        mProfiles = null;
        mLoaded = false;
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
    }
}
