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

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.database.Settings;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.ScriptManager;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by willi on 04.08.16.
 */
public class OnBootFragment extends RecyclerViewFragment {

    private Settings mSettings;
    private Profiles mProfiles;

    private boolean mLoaded;

    private StringBuilder mAllCommands;

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
            mSettings = new Settings(requireActivity());
        }
        if (mProfiles == null) {
            mProfiles = new Profiles(requireActivity());
        }
        if (!mLoaded) {
            mLoaded = true;
            load(items);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
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

        DescriptionView exportAll = new DescriptionView();
        exportAll.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_export, requireActivity()));
        exportAll.setSummary(getString(R.string.export_all));
        exportAll.setOnItemClickListener(item -> {
            if (mAllCommands.toString().isEmpty()) {
                Utils.toast(R.string.export_all_warning, getActivity());
                return;
            }
            exportDialog();
        });

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
            mAllCommands.append(applyOnBootItem.mCommand).append("\n\n");
            DescriptionView applyOnBootView = new DescriptionView();
            applyOnBootView.setSummary((i + 1) + ". " + applyOnBootItem.mCategory.replace("_onboot", "")
                    + ": " + applyOnBootItem.mCommand);

            applyOnBootView.setOnItemClickListener(item -> {
                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question,
                        applyOnBootItem.mCommand),
                        (dialogInterface, i1) -> {
                        }, (dialogInterface, i1) -> {
                            mSettings.delete(applyOnBootItem.mPosition);
                            mSettings.commit();
                            reload();
                        }, dialogInterface -> mDeleteDialog = null, getActivity());
                mDeleteDialog.show();
            });

            applyOnBoot.add(applyOnBootView);
        }

        if (applyOnBoot.size() > 0) {
            items.add(applyOnBootTitle);
            items.add(exportAll);
            items.addAll(applyOnBoot);
        }

        List<RecyclerViewItem> profiles = new ArrayList<>();
        TitleView profileTitle = new TitleView();
        profileTitle.setText(getString(R.string.profile));

        for (final Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
            if (profileItem.isOnBootEnabled()) {
                DescriptionView profileView = new DescriptionView();
                profileView.setSummary(profileItem.getName());
                profileView.setOnItemClickListener(item -> {
                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.disable_question,
                            profileItem.getName()),
                            (dialogInterface, i) -> {
                            }, (dialogInterface, i) -> {
                                profileItem.enableOnBoot(false);
                                mProfiles.commit();
                                reload();
                            }, dialogInterface -> mDeleteDialog = null, getActivity());
                    mDeleteDialog.show();
                });

                profiles.add(profileView);
            }
        }

        if (profiles.size() > 0) {
            items.add(profileTitle);
            items.addAll(profiles);
        }

        if (!ScriptManager.list().isEmpty()) {
            boolean script_onboot = Prefs.getBoolean("scripts_onboot", false, getActivity());
            final Set<String> onBootScripts = Prefs.getStringSet("on_boot_scripts", new HashSet<>(), getActivity());
            for (final String script : ScriptManager.list()) {
                if (script_onboot && Utils.getExtension(script).equals("sh")) {
                    DescriptionView scriptItems = new DescriptionView();
                    scriptItems.setSummary(getString(R.string.script_manger) + ": " + script);
                    scriptItems.setOnItemClickListener(item -> {
                        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.remove_script_question, script),
                                (dialogInterface, i) -> {
                                }, (dialogInterface, i) -> {
                                    ScriptManager.delete(script);
                                    if (ScriptManager.list().isEmpty()) {
                                        Prefs.saveBoolean("scripts_onboot", false, getActivity());
                                    }
                                    reload();
                                }, dialogInterface -> mDeleteDialog = null, getActivity());
                        mDeleteDialog.show();
                    });

                    items.add(scriptItems);
                } else if (Prefs.getStringSet("on_boot_scripts", new HashSet<>(), getActivity()).contains(script)
                        && Utils.getExtension(script).equals("sh")) {
                    DescriptionView scriptItems = new DescriptionView();
                    scriptItems.setSummary(getString(R.string.script_manger) + ": " + script);
                    scriptItems.setOnItemClickListener(item -> {
                        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question, script),
                                (dialogInterface, i) -> {
                                }, (dialogInterface, i) -> {
                                    onBootScripts.remove(script);
                                    reload();
                                }, dialogInterface -> mDeleteDialog = null, getActivity());
                        mDeleteDialog.show();
                    });

                    items.add(scriptItems);
                }
            }
        }
    }

    private void exportDialog() {
        ViewUtils.dialogEditText("",
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.toast(R.string.name_empty, getActivity());
                        return;
                    }
                    if (!text.endsWith(".sh")) {
                        text += ".sh";
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    if (Utils.existFile(Utils.getInternalDataStorage() + "/" + text)) {
                        Utils.toast(getString(R.string.script_exists, text), getActivity());
                        return;
                    }
                    Utils.prepareInternalDataStorage();
                    Utils.create("#!/system/bin/sh\n\n## Created by SmartPack-Kernel Manager\n\n" + mAllCommands.toString() + "## The END", Utils.getInternalDataStorage() + "/" + text);
                    Utils.toast(getString(R.string.export_all_message, Utils.getInternalDataStorage()), getActivity());
                }, getActivity()).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private static class ApplyOnBootItem {
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
    public void onStart() {
        super.onStart();
        if (mAllCommands == null) {
            mAllCommands = new StringBuilder();
        } else {
            mAllCommands.setLength(0);
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
