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

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.database.Settings;
import com.smartpack.kernelmanager.database.tools.profiles.Profiles;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.AsyncTasks;
import com.smartpack.kernelmanager.utils.tools.Scripts;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SwipeableDescriptionView;
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

    private Dialog mDeleteDialog;

    @Override
    protected void init() {
        super.init();

        if (Utils.isFDroidFlavor(requireActivity())) {
            enableSwipeToDismiss();
            enableDragAndDrop();
        }

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.welcome),
                getString(R.string.on_boot_welcome_summary) + (Utils.isFDroidFlavor(requireActivity()) ?
                        " " + getString(R.string.on_boot_swipe_drag_message) : "")));

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
        getHandler().postDelayed(() -> {
            clearItems();
            new AsyncTasks() {
                private List<RecyclerViewItem> items;

                @Override
                public void onPreExecute() {
                    showProgress();
                    items = new ArrayList<>();
                }

                @Override
                public void doInBackground() {
                    load(items);
                }

                @Override
                public void onPostExecute() {
                    for (RecyclerViewItem item : items) {
                        addItem(item);
                    }
                    hideProgress();
                }
            }.execute();
        }, 250);
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
            if (Utils.isFDroidFlavor(requireActivity())) {
                SwipeableDescriptionView applyOnBootView = new SwipeableDescriptionView();
                applyOnBootView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_drag_handle, requireContext()));
                applyOnBootView.setSummary(applyOnBootItem.mCategory.replace("_onboot", "")
                        + ": " + applyOnBootItem.mCommand);
                applyOnBootView.setOnItemSwipedListener((item, position) -> {
                    mSettings.delete(position);
                    mSettings.commit();
                });

                applyOnBootView.setOnItemDragListener((item, fromPosition, toPosition) -> {
                    mSettings.swap(fromPosition, toPosition);
                    mSettings.commit();
                });

                applyOnBoot.add(applyOnBootView);
            } else {
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
        }

        if (Utils.isFDroidFlavor(requireActivity()) && applyOnBoot.size() > 1) {
            items.addAll(applyOnBoot);
        } else if (!Utils.isFDroidFlavor(requireActivity()) && applyOnBoot.size() > 0) {
            items.add(applyOnBootTitle);
            items.addAll(applyOnBoot);
        }

        List<RecyclerViewItem> profiles = new ArrayList<>();
        TitleView profileTitle = new TitleView();
        profileTitle.setText(getString(R.string.profile));

        for (final Profiles.ProfileItem profileItem : mProfiles.getAllProfiles()) {
            if (profileItem.isOnBootEnabled()) {
                if (Utils.isFDroidFlavor(requireActivity())) {
                    SwipeableDescriptionView profileView = new SwipeableDescriptionView();
                    profileView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_drag_handle, requireContext()));
                    profileView.setSummary(getString(R.string.profile) + ": " + profileItem.getName());
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
                    profileView.setOnItemSwipedListener((item, position) -> {
                        Utils.snackbar(getRootView(), getString(R.string.swipe_message, getString(R.string.profile)));
                        reload();
                    });
                    profileView.setOnItemDragListener((item, fromPosition, toPosition) -> {
                        Utils.snackbar(getRootView(), getString(R.string.drag_message, getString(R.string.profile)));
                        reload();
                    });

                    profiles.add(profileView);
                } else {
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
        }

        if (profiles.size() > 0) {
            if (!Utils.isFDroidFlavor(requireActivity())) {
                items.add(profileTitle);
            }
            items.addAll(profiles);
        }

        if (!Scripts.list(requireActivity()).isEmpty()) {
            boolean script_onboot = Prefs.getBoolean("scripts_onboot", false, getActivity());
            final Set<String> onBootScripts = Prefs.getStringSet("on_boot_scripts", new HashSet<>(), getActivity());
            for (final String script : Scripts.list(requireActivity())) {
                if (script_onboot && Utils.getExtension(script).equals("sh")) {
                    if (Utils.isFDroidFlavor(requireActivity())) {
                        SwipeableDescriptionView scriptItem = new SwipeableDescriptionView();
                        scriptItem.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_drag_handle, requireContext()));
                        scriptItem.setSummary(getString(R.string.script_manger) + ": " + script);
                        scriptItem.setOnItemClickListener(item -> {
                            mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.remove_script_question, script),
                                    (dialogInterface, i) -> {
                                    }, (dialogInterface, i) -> {
                                        Scripts.delete(script, requireActivity());
                                        if (Scripts.list(requireActivity()).isEmpty()) {
                                            Prefs.saveBoolean("scripts_onboot", false, getActivity());
                                        }
                                        reload();
                                    }, dialogInterface -> mDeleteDialog = null, getActivity());
                            mDeleteDialog.show();
                        });
                        scriptItem.setOnItemSwipedListener((item, position) -> {
                            Utils.snackbar(getRootView(), getString(R.string.swipe_message, getString(R.string.script)));
                            reload();
                        });
                        scriptItem.setOnItemDragListener((item, fromPosition, toPosition) -> {
                            Utils.snackbar(getRootView(), getString(R.string.drag_message, getString(R.string.script)));
                            reload();
                        });

                        items.add(scriptItem);
                    } else {
                        DescriptionView scriptItem = new DescriptionView();
                        scriptItem.setSummary(getString(R.string.script_manger) + ": " + script);
                        scriptItem.setOnItemClickListener(item -> {
                            mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.remove_script_question, script),
                                    (dialogInterface, i) -> {
                                    }, (dialogInterface, i) -> {
                                        Scripts.delete(script, requireActivity());
                                        if (Scripts.list(requireActivity()).isEmpty()) {
                                            Prefs.saveBoolean("scripts_onboot", false, getActivity());
                                        }
                                        reload();
                                    }, dialogInterface -> mDeleteDialog = null, getActivity());
                            mDeleteDialog.show();
                        });

                        items.add(scriptItem);
                    }
                } else if (Prefs.getStringSet("on_boot_scripts", new HashSet<>(), getActivity()).contains(script)
                        && Utils.getExtension(script).equals("sh")) {
                    if (Utils.isFDroidFlavor(requireActivity())) {
                        SwipeableDescriptionView scriptItem = new SwipeableDescriptionView();
                        scriptItem.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_drag_handle, requireContext()));
                        scriptItem.setSummary(getString(R.string.script_manger) + ": " + script);
                        scriptItem.setOnItemClickListener(item -> {
                            mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question, script),
                                    (dialogInterface, i) -> {
                                    }, (dialogInterface, i) -> {
                                        onBootScripts.remove(script);
                                        reload();
                                    }, dialogInterface -> mDeleteDialog = null, getActivity());
                            mDeleteDialog.show();
                        });
                        scriptItem.setOnItemSwipedListener((item, position) -> {
                            Utils.snackbar(getRootView(), getString(R.string.swipe_message, getString(R.string.script)));
                            reload();
                        });
                        scriptItem.setOnItemDragListener((item, fromPosition, toPosition) -> {
                            Utils.snackbar(getRootView(), getString(R.string.drag_message, getString(R.string.script)));
                            reload();
                        });

                        items.add(scriptItem);
                    } else {
                        DescriptionView scriptItem = new DescriptionView();
                        scriptItem.setSummary(getString(R.string.script_manger) + ": " + script);
                        scriptItem.setOnItemClickListener(item -> {
                            mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question, script),
                                    (dialogInterface, i) -> {
                                    }, (dialogInterface, i) -> {
                                        onBootScripts.remove(script);
                                        reload();
                                    }, dialogInterface -> mDeleteDialog = null, getActivity());
                            mDeleteDialog.show();
                        });

                        items.add(scriptItem);
                    }
                }
            }
        }
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
    public void onDestroy() {
        super.onDestroy();
        mSettings = null;
        mProfiles = null;
        mLoaded = false;
    }
}
