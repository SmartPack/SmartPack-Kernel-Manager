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

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.NavigationActivity;
import com.smartpack.kernelmanager.activities.tools.profile.ProfileActivity;
import com.smartpack.kernelmanager.activities.tools.profile.ProfileEditActivity;
import com.smartpack.kernelmanager.activities.tools.profile.ProfileTaskerActivity;
import com.smartpack.kernelmanager.database.tools.profiles.ExportProfile;
import com.smartpack.kernelmanager.database.tools.profiles.ImportProfile;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.fragments.SwitcherFragment;
import com.smartpack.kernelmanager.services.boot.ApplyOnBoot;
import com.smartpack.kernelmanager.services.profile.Tile;
import com.smartpack.kernelmanager.services.profile.Widget;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.root.Control;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 10.07.16.
 */
public class ProfileFragment extends RecyclerViewFragment {

    public static ProfileFragment newInstance(boolean tasker) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.mTaskerMode = tasker;
        return fragment;
    }

    private boolean mTaskerMode;
    private Profiles mProfiles;
    private String mPath;

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;
    private boolean mLoaded;

    private LinkedHashMap<String, String> mCommands;
    private Dialog mDeleteDialog;
    private Dialog mApplyDialog;
    private Profiles.ProfileItem mExportProfile;
    private Dialog mOptionsDialog;
    private ImportProfile mImportProfile;
    private Dialog mSelectDialog;

    private DetailsFragment mDetailsFragment;

    @Override
    protected boolean showViewPager() {
        return true;
    }

    @Override
    protected boolean showTopFab() {
        return !mTaskerMode;
    }

    @Override
    protected BaseFragment getForegroundFragment() {
        return mTaskerMode ? null : (mDetailsFragment = new DetailsFragment());
    }

    @Override
    protected Drawable getTopFabDrawable() {
        return getResources().getDrawable(R.drawable.ic_add);
    }

    @Override
    public int getSpanCount() {
        int span = Utils.isTablet(requireActivity()) ? Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 4 : 3 : Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        if (itemsSize() != 0 && span > itemsSize()) {
            span = itemsSize();
        }
        return span;
    }

    @Override
    protected void init() {
        super.init();
        if (!isAdded()) return;

        if (mTaskerMode) {
            addViewPagerFragment(new TaskerToastFragment());
        } else {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.profile_tasker),
                    getString(R.string.profile_tasker_summary)));
            if (Utils.hasCMSDK()) {
                addViewPagerFragment(SwitcherFragment.newInstance(getString(R.string.profile_tile),
                        getString(R.string.profile_tile_summary),
                        Prefs.getBoolean("profiletile", false, getActivity()),
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                Prefs.saveBoolean("profiletile", b, getActivity());
                                Tile.publishProfileTile(mProfiles.getAllProfiles(), getActivity());
                            }
                        }));
            }
        }

        if (mCommands != null) {
            create(mCommands);
        }
        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }
        if (mApplyDialog != null) {
            mApplyDialog.show();
        }
        if (mExportProfile != null) {
            showExportDialog();
        }
        if (mOptionsDialog != null) {
            mOptionsDialog.show();
        }
        if (mImportProfile != null) {
            showImportDialog(mImportProfile);
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
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
                    clearItems();
                    mLoader = new UILoader(ProfileFragment.this);
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private static class UILoader extends AsyncTask<Void, Void, List<RecyclerViewItem>> {

        private WeakReference<ProfileFragment> mRefFragment;

        private UILoader(ProfileFragment fragment) {
            mRefFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefFragment.get().showProgress();
        }

        @Override
        protected List<RecyclerViewItem> doInBackground(Void... voids) {
            List<RecyclerViewItem> items = new ArrayList<>();
            mRefFragment.get().load(items);
            return items;
        }

        @Override
        protected void onPostExecute(List<RecyclerViewItem> items) {
            super.onPostExecute(items);
            ProfileFragment fragment = mRefFragment.get();
            for (RecyclerViewItem item : items) {
                fragment.addItem(item);
            }
            fragment.hideProgress();
            fragment.mLoader = null;
        }
    }

    private void load(List<RecyclerViewItem> items) {
        mProfiles = new Profiles(requireActivity());
        List<Profiles.ProfileItem> profileItems = mProfiles.getAllProfiles();
        if (mTaskerMode && profileItems.size() == 0) {
            Snackbar.make(getRootView(), R.string.no_profiles, Snackbar.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < profileItems.size(); i++) {
            final int position = i;
            final CardView cardView = new CardView(getActivity());
            cardView.setOnMenuListener((cardView1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.append));
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.edit));
                menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.details));
                final MenuItem onBoot = menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.on_boot)).setCheckable(true);
                onBoot.setChecked(mProfiles.getAllProfiles().get(position).isOnBootEnabled());
                menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.export));
                menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.delete));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        List<Profiles.ProfileItem> items1 = mProfiles.getAllProfiles();
                        switch (item.getItemId()) {
                            case 0:
                                Intent create = createProfileActivityIntent();
                                create.putExtra(ProfileActivity.POSITION_INTENT, position);
                                startActivityForResult(create, 2);
                                break;
                            case 1:
                                Intent edit = new Intent(getActivity(), ProfileEditActivity.class);
                                edit.putExtra(ProfileEditActivity.POSITION_INTENT, position);
                                startActivityForResult(edit, 3);
                                break;
                            case 2:
                                if (items1.get(position).getName() != null) {
                                    List<Profiles.ProfileItem.CommandItem> commands = items1.get(position).getCommands();
                                    if (commands.size() > 0) {
                                        setForegroundText(items1.get(position).getName().toUpperCase());
                                        mDetailsFragment.setText(commands);
                                        showForeground();
                                    } else {
                                        Utils.toast(R.string.profile_empty, getActivity());
                                    }
                                }
                                break;
                            case 3:
                                onBoot.setChecked(!onBoot.isChecked());
                                items1.get(position).enableOnBoot(onBoot.isChecked());
                                mProfiles.commit();
                                break;
                            case 4:
                                mExportProfile = items1.get(position);
                                requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                break;
                            case 5:
                                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                        (dialogInterface, i1) -> {
                                        }, (dialogInterface, i1) -> {
                                            mProfiles.delete(position);
                                            mProfiles.commit();
                                            reload();
                                        }, dialogInterface -> mDeleteDialog = null, getActivity());
                                mDeleteDialog.show();
                                break;
                        }
                        return false;
                    }
                });
            });

            final DescriptionView descriptionView = new DescriptionView();
            descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_file));
            descriptionView.setSummary(profileItems.get(i).getName());
            descriptionView.setOnItemClickListener(item -> {
                if (mTaskerMode) {
                    mSelectDialog = ViewUtils.dialogBuilder(getString(R.string.select_question,
                            descriptionView.getSummary()), (dialogInterface, i12) -> {
                            }, (dialogInterface, i12) -> ((ProfileTaskerActivity) getActivity()).finish(
                                    descriptionView.getSummary().toString(),
                                    mProfiles.getAllProfiles().get(position).getCommands()), dialogInterface -> mSelectDialog = null, getActivity());
                    mSelectDialog.show();
                } else {
                    mApplyDialog = ViewUtils.dialogBuilder(getString(R.string.apply_question,
                            descriptionView.getSummary()), (dialogInterface, i12) -> {
                            }, (dialogInterface, i12) -> {
                        for (Profiles.ProfileItem.CommandItem command : mProfiles.getAllProfiles()
                                .get(position).getCommands()) {
                            CPUFreq.ApplyCpu applyCpu;
                            if (command.getCommand().startsWith("#") && ((applyCpu =
                                    new CPUFreq.ApplyCpu(command.getCommand().substring(1)))
                                    .toString() != null)) {
                                for (String applyCpuCommand : ApplyOnBoot.getApplyCpu(applyCpu,
                                        RootUtils.getSU())) {
                                    Control.runSetting(applyCpuCommand, null, null, null);
                                }
                            } else {
                                Control.runSetting(command.getCommand(), null, null, null);
                            }
                        }
                    }, dialogInterface -> mApplyDialog = null, getActivity());
                    try {
                        mApplyDialog.show();
                    } catch (NullPointerException ignored) {
                    }
                }
            });

            if (mTaskerMode) {
                items.add(descriptionView);
            } else {
                cardView.addItem(descriptionView);
                items.add(cardView);
            }
        }

        if (!mTaskerMode) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(requireActivity(), Widget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.profile_list);
            Tile.publishProfileTile(profileItems, getActivity());
        }
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.profile_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            startActivityForResult(createProfileActivityIntent(), 0);
                            break;
                        case 1:
                            Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, 1);
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> mOptionsDialog = null);
        mOptionsDialog.show();
    }

    private Intent createProfileActivityIntent() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);

        NavigationActivity activity = (NavigationActivity) requireActivity();
        ArrayList<NavigationActivity.NavigationFragment> fragments = new ArrayList<>();
        boolean add = false;
        for (NavigationActivity.NavigationFragment fragment : activity.getFragments()) {
            if (fragment.mId == R.string.kernel) {
                add = true;
                continue;
            }
            if (!add) continue;
            if (fragment.mFragmentClass == null) break;
            if (activity.getActualFragments().get(fragment.mId) != null) {
                fragments.add(fragment);
            }
        }
        intent.putParcelableArrayListExtra(ProfileActivity.FRAGMENTS_INTENT, fragments);

        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 0 || requestCode == 2) {
            LinkedHashMap<String, String> commandsList = new LinkedHashMap<>();
            ArrayList<String> ids = data.getStringArrayListExtra(ProfileActivity.RESULT_ID_INTENT);
            ArrayList<String> commands = data.getStringArrayListExtra(ProfileActivity.RESULT_COMMAND_INTENT);
            assert ids != null;
            for (int i = 0; i < ids.size(); i++) {
                assert commands != null;
                commandsList.put(ids.get(i), commands.get(i));
            }

            if (requestCode == 0) {
                create(commandsList);
            } else {
                Profiles.ProfileItem profileItem = mProfiles.getAllProfiles().get(data.getIntExtra(
                        ProfileActivity.POSITION_INTENT, 0));

                for (Profiles.ProfileItem.CommandItem commandItem : profileItem.getCommands()) {
                    if (ids.contains(commandItem.getPath())) {
                        profileItem.delete(commandItem);
                    }
                }

                for (String path : commandsList.keySet()) {
                    profileItem.putCommand(new Profiles.ProfileItem.CommandItem(
                            path, commandsList.get(path)));
                }
                mProfiles.commit();
            }
        } else if (requestCode == 1) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getFilePath(file);
            }
            if (!Utils.getExtension(mPath).equals("json")) {
                Utils.toast(getString(R.string.wrong_extension, ".json"), getActivity());
                return;
            }
            Dialog selectProfile = new Dialog(requireActivity());
            selectProfile.setMessage(getString(R.string.select_question, file.getName().replace("primary:", "")
                    .replace("file%3A%2F%2F%2F", "").replace("%2F", "/")));
            selectProfile.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
            });
            selectProfile.setPositiveButton(getString(R.string.ok), (dialog1, id1) -> {
                ImportProfile importProfile = new ImportProfile(mPath);
                if (!importProfile.readable()) {
                    Utils.toast(R.string.import_malformed, getActivity());
                    return;
                }
                if (!importProfile.matchesVersion()) {
                    Utils.toast(R.string.import_wrong_version, getActivity());
                    return;
                }
                showImportDialog(importProfile);
            });
            selectProfile.show();
        } else if (requestCode == 3) {
            reload();
        }
    }

    private void showImportDialog(final ImportProfile importProfile) {
        mImportProfile = importProfile;
        ViewUtils.dialogEditText(null, (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.toast(R.string.name_empty, getActivity());
                return;
            }

            for (Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
                if (text.equals(profileItem.getName())) {
                    Utils.toast(getString(R.string.already_exists, text), getActivity());
                    return;
                }
            }

            mProfiles.putProfile(text, importProfile.getResults());
            mProfiles.commit();
            reload();
        }, getActivity()).setTitle(getString(R.string.name)).setOnDismissListener(
                dialogInterface -> mImportProfile = null).show();
    }

    private void create(final LinkedHashMap<String, String> commands) {
        mCommands = commands;
        ViewUtils.dialogEditText(null, (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.toast(R.string.name_empty, getActivity());
                return;
            }

            for (Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
                if (text.equals(profileItem.getName())) {
                    Utils.toast(getString(R.string.already_exists, text), getActivity());
                    return;
                }
            }

            mProfiles.putProfile(text, commands);
            mProfiles.commit();
            reload();
        }, getActivity()).setOnDismissListener(dialogInterface -> mCommands = null).setTitle(getString(R.string.name)).show();
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);

        if (request == 0) {
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
        }
    }

    @Override
    public void onPermissionGranted(int request) {
        super.onPermissionGranted(request);

        if (request == 0) {
            showExportDialog();
        }
    }

    private void showExportDialog() {
        ViewUtils.dialogEditText(null, (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.toast(R.string.name_empty, getActivity());
                return;
            }

            if (new ExportProfile(mExportProfile, mProfiles.getVersion()).export(text)) {
                Utils.toast(getString(R.string.exported_item, text, Utils.getInternalDataStorage()
                        + "/profiles"), getActivity());
            } else {
                Utils.toast(getString(R.string.already_exists, text), getActivity());
            }
        }, getActivity()).setOnDismissListener(dialogInterface -> mExportProfile = null).setTitle(getString(R.string.name)).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
        mLoaded = false;
    }

    public static class DetailsFragment extends BaseFragment {

        private TextView mCodeText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_details, container, false);

            mCodeText = rootView.findViewById(R.id.code_text);

            return rootView;
        }

        private void setText(List<Profiles.ProfileItem.CommandItem> commands) {
            StringBuilder commandsText = new StringBuilder();
            for (Profiles.ProfileItem.CommandItem command : commands) {
                CPUFreq.ApplyCpu applyCpu;
                if (command.getCommand().startsWith("#")
                        & ((applyCpu =
                        new CPUFreq.ApplyCpu(command.getCommand().substring(1))).toString() != null)) {
                    for (String applyCpuCommand : ApplyOnBoot.getApplyCpu(applyCpu, RootUtils.getSU())) {
                        commandsText.append(applyCpuCommand).append("\n");
                    }
                } else {
                    commandsText.append(command.getCommand()).append("\n");
                }
            }
            commandsText.setLength(commandsText.length() - 1);

            if (mCodeText != null) {
                mCodeText.setText(commandsText.toString());
            }
        }
    }

    public static class TaskerToastFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_apply_on_boot, container, false);

            ((TextView) rootView.findViewById(R.id.title)).setText(getString(R.string.profile_tasker_toast));
            SwitchCompat switchCompat = rootView.findViewById(R.id.switcher);
            switchCompat.setChecked(Prefs.getBoolean("showtaskertoast", true, getActivity()));
            switchCompat.setOnCheckedChangeListener((compoundButton, b) -> Prefs.saveBoolean("showtaskertoast", b, getActivity()));

            return rootView;
        }
    }

}