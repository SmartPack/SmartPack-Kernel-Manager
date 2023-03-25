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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.ForegroundActivity;
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
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.root.Control;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

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

    private LinkedHashMap<String, String> mCommands;
    private Dialog mDeleteDialog;
    private Dialog mApplyDialog;
    private Dialog mOptionsDialog;
    private Dialog mSelectDialog;

    @Override
    protected boolean showViewPager() {
        return true;
    }

    @Override
    protected boolean showTopFab() {
        return !mTaskerMode;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getWhiteColoredIcon(R.drawable.ic_add, requireActivity());
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
                        (compoundButton, b) -> {
                            Prefs.saveBoolean("profiletile", b, getActivity());
                            Tile.publishProfileTile(mProfiles.getAllProfiles(), getActivity());
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
        if (mOptionsDialog != null) {
            mOptionsDialog.show();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
    }

    private void reload() {
        getHandler().postDelayed(() -> {
            clearItems();
            new UILoader(ProfileFragment.this).execute();
        }, 250);
    }

    private static class UILoader extends sExecutor {

        private List<RecyclerViewItem> items;
        private UILoader(ProfileFragment fragment) {
            mRefFragment = new WeakReference<>(fragment);
        }
        private final WeakReference<ProfileFragment> mRefFragment;

        @Override
        public void onPreExecute() {
            mRefFragment.get().showProgress();
            items = new ArrayList<>();
        }

        @Override
        public void doInBackground() {
            mRefFragment.get().load(items);
        }

        @Override
        public void onPostExecute() {
            ProfileFragment fragment = mRefFragment.get();
            for (RecyclerViewItem item : items) {
                fragment.addItem(item);
            }
            fragment.hideProgress();
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

            final DescriptionView descriptionView = new DescriptionView();
            descriptionView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_file, requireContext()));
            descriptionView.setSummary(profileItems.get(i).getName());
            if (Prefs.getBoolean("enable_onboot", true, getActivity()) && mProfiles.getAllProfiles().get(position).isOnBootEnabled()) {
                descriptionView.setIndicator(ViewUtils.getColoredIcon(R.drawable.ic_flash, requireActivity()));
            }
            descriptionView.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
            descriptionView.setOnItemClickListener(item -> applyProfile(descriptionView, position));
            descriptionView.setOnMenuListener((cardView1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.apply));
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.append));
                menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.edit));
                menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.details));
                final MenuItem onBoot = menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.on_boot)).setCheckable(true);
                onBoot.setChecked(Prefs.getBoolean("enable_onboot", true, getActivity()) &&
                        mProfiles.getAllProfiles().get(position).isOnBootEnabled());
                menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.export));
                menu.add(Menu.NONE, 6, Menu.NONE, getString(R.string.delete));
                popupMenu.setOnMenuItemClickListener(item -> {
                    List<Profiles.ProfileItem> profileItem = mProfiles.getAllProfiles();
                    switch (item.getItemId()) {
                        case 0:
                            applyProfile(descriptionView, position);
                            break;
                        case 1:
                            Intent create = createProfileActivityIntent();
                            create.putExtra(ProfileActivity.POSITION_INTENT, position);
                            appendToProfile.launch(create);
                            break;
                        case 2:
                            Intent edit = new Intent(getActivity(), ProfileEditActivity.class);
                            edit.putExtra(ProfileEditActivity.POSITION_INTENT, position);
                            editProfile.launch(edit);
                            break;
                        case 3:
                            if (profileItem.get(position).getName() != null) {
                                List<Profiles.ProfileItem.CommandItem> commands = profileItem.get(position).getCommands();
                                if (commands.size() > 0) {
                                    StringBuilder mCommandsText = new StringBuilder();
                                    for (Profiles.ProfileItem.CommandItem command : commands) {
                                        CPUFreq.ApplyCpu applyCpu;
                                        if (command.getCommand().startsWith("#")
                                                & ((applyCpu =
                                                new CPUFreq.ApplyCpu(command.getCommand().substring(1))).toString() != null)) {
                                            for (String applyCpuCommand : ApplyOnBoot.getApplyCpu(applyCpu)) {
                                                mCommandsText.append(applyCpuCommand).append("\n");
                                            }
                                        } else {
                                            mCommandsText.append(command.getCommand()).append("\n");
                                        }
                                    }
                                    Common.setDetailsTitle(profileItem.get(position).getName().toUpperCase());
                                    Common.setDetailsTxt(mCommandsText.toString());
                                    Intent details = new Intent(getActivity(), ForegroundActivity.class);
                                    startActivity(details);
                                } else {
                                    Utils.snackbar(getRootView(), getString(R.string.profile_empty));
                                }
                            }
                            break;
                        case 4:
                            if (Prefs.getBoolean("enable_onboot", true, getActivity())) {
                                onBoot.setChecked(!onBoot.isChecked());
                                profileItem.get(position).enableOnBoot(onBoot.isChecked());
                                mProfiles.commit();
                            } else {
                                Utils.snackbar(getRootView(), getString(R.string.enable_onboot_message));
                            }
                            reload();
                            break;
                        case 5:
                            showExportDialog(profileItem.get(position));
                            break;
                        case 6:
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
                });
            });

            items.add(descriptionView);
        }

        if (!mTaskerMode) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(requireActivity(), Widget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.profile_list);
            Tile.publishProfileTile(profileItems, getActivity());
        }
    }

    private void applyProfile(DescriptionView descriptionView, int position) {
        if (mTaskerMode) {
            mSelectDialog = ViewUtils.dialogBuilder(getString(R.string.select_question,
                    descriptionView.getSummary()), (dialogInterface, i12) -> {
            }, (dialogInterface, i12) -> ((ProfileTaskerActivity) requireActivity()).finish(
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
                        for (String applyCpuCommand : ApplyOnBoot.getApplyCpu(applyCpu)) {
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
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.profile_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            createProfile.launch(createProfileActivityIntent());
                            break;
                        case 1:
                            FilePicker filePicker = new FilePicker(importProfile, requireActivity());
                            filePicker.setExtension("json");
                            filePicker.setPath(Environment.getExternalStorageDirectory().toString());
                            filePicker.setAccentColor(ViewUtils.getThemeAccentColor(requireContext()));
                            filePicker.launch();
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

    ActivityResultLauncher<Intent> appendToProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    LinkedHashMap<String, String> commandsList = new LinkedHashMap<>();
                    ArrayList<String> ids = result.getData().getStringArrayListExtra(ProfileActivity.RESULT_ID_INTENT);
                    ArrayList<String> commands = result.getData().getStringArrayListExtra(ProfileActivity.RESULT_COMMAND_INTENT);
                    assert ids != null;
                    for (int i = 0; i < ids.size(); i++) {
                        assert commands != null;
                        commandsList.put(ids.get(i), commands.get(i));
                    }
                    Profiles.ProfileItem profileItem = mProfiles.getAllProfiles().get(result.getData().getIntExtra(
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
            }
    );

    ActivityResultLauncher<Intent> createProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    LinkedHashMap<String, String> commandsList = new LinkedHashMap<>();
                    ArrayList<String> ids = result.getData().getStringArrayListExtra(ProfileActivity.RESULT_ID_INTENT);
                    ArrayList<String> commands = result.getData().getStringArrayListExtra(ProfileActivity.RESULT_COMMAND_INTENT);
                    assert ids != null;
                    for (int i = 0; i < ids.size(); i++) {
                        assert commands != null;
                        commandsList.put(ids.get(i), commands.get(i));
                    }
                    create(commandsList);
                }
            }
    );

    ActivityResultLauncher<Intent> editProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    reload();
                }
            }
    );

    ActivityResultLauncher<Intent> importProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && FilePicker.getSelectedFile().exists()) {
                    File mSelectedFile = FilePicker.getSelectedFile();
                    if (!Utils.getExtension(mSelectedFile.getAbsolutePath()).equals("json")) {
                        Utils.snackbar(getRootView(), getString(R.string.wrong_extension, ".json"));
                        return;
                    }
                    Dialog selectProfile = new Dialog(requireActivity());
                    selectProfile.setMessage(getString(R.string.select_question, mSelectedFile.getName()));
                    selectProfile.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    });
                    selectProfile.setPositiveButton(getString(R.string.ok), (dialog1, id1) -> {
                        ImportProfile importProfile = new ImportProfile(mSelectedFile.getAbsolutePath());
                        if (!importProfile.readable()) {
                            Utils.snackbar(getRootView(), getString(R.string.import_malformed));
                            return;
                        }
                        if (!importProfile.matchesVersion()) {
                            Utils.snackbar(getRootView(), getString(R.string.import_wrong_version));
                            return;
                        }
                        showImportDialog(importProfile, mSelectedFile.getName().replace(".json", ""));
                    });
                    selectProfile.show();
                }
            }
    );

    private void showImportDialog(final ImportProfile importProfile, String fileName) {
        ViewUtils.dialogEditText(fileName, (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.snackbar(getRootView(), getString(R.string.name_empty));
                return;
            }

            for (Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
                if (text.equals(profileItem.getName())) {
                    Utils.snackbar(getRootView(), getString(R.string.already_exists, text));
                    return;
                }
            }

            mProfiles.putProfile(text, importProfile.getResults());
            mProfiles.commit();
            reload();
        }, getActivity()).setTitle(getString(R.string.name)).setOnDismissListener(
                dialogInterface -> {
                }).show();
    }

    private void create(final LinkedHashMap<String, String> commands) {
        mCommands = commands;
        ViewUtils.dialogEditText(null, (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.snackbar(getRootView(), getString(R.string.name_empty));
                return;
            }

            for (Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
                if (text.equals(profileItem.getName())) {
                    Utils.snackbar(getRootView(), getString(R.string.already_exists, text));
                    return;
                }
            }

            mProfiles.putProfile(text, commands);
            mProfiles.commit();
            reload();
        }, getActivity()).setOnDismissListener(dialogInterface -> mCommands = null).setTitle(getString(R.string.name)).show();
    }

    private void showExportDialog(Profiles.ProfileItem exportProfile) {
        ViewUtils.dialogEditText(exportProfile.getName(), (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.snackbar(getRootView(), getString(R.string.name_empty));
                return;
            }

            if (new ExportProfile(exportProfile, mProfiles.getVersion()).export(text)) {
                Utils.snackbar(getRootView(), getString(R.string.exported_item, text,
                        SuFile.open(Utils.getInternalDataStorage(), "profiles").getAbsolutePath()));
            } else {
                Utils.snackbar(getRootView(), getString(R.string.already_exists, text));
            }
        }, getActivity()).setOnDismissListener(dialogInterface -> {
        }).setTitle(getString(R.string.name)).show();
    }

    public static class TaskerToastFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_apply_on_boot, container, false);

            ((MaterialTextView) rootView.findViewById(R.id.title)).setText(getString(R.string.profile_tasker_toast));
            SwitchMaterial switchCompat = rootView.findViewById(R.id.switcher);
            switchCompat.setChecked(Prefs.getBoolean("showtaskertoast", true, getActivity()));
            switchCompat.setOnCheckedChangeListener((compoundButton, b) -> Prefs.saveBoolean("showtaskertoast", b, getActivity()));

            return rootView;
        }
    }

}