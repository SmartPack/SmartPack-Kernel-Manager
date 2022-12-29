/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>
 */

package com.smartpack.kernelmanager.fragments.tools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.InputType;
import android.view.Menu;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.CustomControls;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.GenericInputView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;
import in.sunilpaulmathew.sCommon.Utils.sExecutor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 15, 2018
 */

public class CustomControlsFragment extends RecyclerViewFragment {

    private boolean mAlertDialogue = true;
    private int mControllerType = -1;
    private Dialog mDeleteDialog;
    private Dialog mSelectionMenu;

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getColoredIcon(R.drawable.ic_add, requireActivity());
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.custom_controls),
                Utils.htmlFrom(getString(R.string.custom_controls_summary))));

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
    }

    private void reload() {
        getHandler().postDelayed(() -> {
            clearItems();
            new sExecutor() {
                private List<RecyclerViewItem> items;
                @Override
                public void onPreExecute() {
                    showProgress();
                    items = new ArrayList<>();
                }

                @Override
                public void doInBackground() {
                    switchItemsList(items);
                    genericItemsList(items);
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

    @SuppressLint("StringFormatInvalid")
    private void switchItemsList(List<RecyclerViewItem> items) {
        for (final String switchItems : CustomControls.switchList()) {
            if (CustomControls.switchFile().length() > 0 && Utils.existFile(Utils.readFile(
                    CustomControls.switchFile() + "/" + switchItems))) {
                SwitchView itemslist = new SwitchView();
                itemslist.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
                itemslist.setSummary(Utils.readFile(CustomControls.switchFile()+ "/" + switchItems));
                itemslist.setChecked(CustomControls.isSwitchEnabled(Utils.readFile(CustomControls.switchFile() + "/" + switchItems)));
                itemslist.addOnSwitchListener((switchView, isChecked) -> {
                    CustomControls.enableSwitch(isChecked, Utils.readFile(CustomControls.switchFile() + "/" + switchItems), getActivity());
                    getHandler().postDelayed(() -> itemslist.setChecked(CustomControls.isSwitchEnabled(Utils.readFile(
                            CustomControls.switchFile() + "/" + switchItems))),
                            500);
                });
                itemslist.setOnMenuListener((itemslist1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.delete));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.share));
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case 0:
                                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                        (dialogInterface, i) -> {
                                        }, (dialogInterface, i) -> {
                                            CustomControls.delete(CustomControls.switchFile() + "/" + switchItems);
                                            reload();
                                        }, dialogInterface -> mDeleteDialog = null, getActivity());
                                mDeleteDialog.show();
                                break;
                            case 1:
                                Utils.shareItem(getActivity(), switchItems, CustomControls.switchFile() + "/" +
                                        switchItems,getString(R.string.share_controller, CustomControls.switchFile()) +
                                        " -> Import -> Switch'.\n\n" + getString(R.string.share_app_message, BuildConfig.VERSION_NAME));
                                break;
                        }
                        return false;
                    });
                });

                items.add(itemslist);
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void genericItemsList(List<RecyclerViewItem> items) {
        for (final String genericItems : CustomControls.genericList()) {
            if (CustomControls.switchFile().length() > 0 && Utils.existFile(Utils.readFile(CustomControls
                    .genericFile() + "/" + genericItems))) {
                GenericInputView itemslist = new GenericInputView();
                itemslist.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
                itemslist.setTitle(Utils.readFile(CustomControls.genericFile()+ "/" + genericItems));
                itemslist.setValue(CustomControls.getGenericValue(Utils.readFile(CustomControls.genericFile() + "/" + genericItems)));
                itemslist.setInputType(InputType.TYPE_CLASS_NUMBER);
                itemslist.setOnGenericValueListener((genericInputView, value) -> {
                    CustomControls.setGenericValue(value, Utils.readFile(CustomControls.genericFile() + "/" + genericItems), getActivity());
                    getHandler().postDelayed(() -> itemslist.setValue(CustomControls.getGenericValue(Utils.readFile(
                            CustomControls.genericFile() + "/" + genericItems))),
                            500);
                });

                itemslist.setOnMenuListener((itemslist1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.delete));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.share));
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case 0:
                                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                        (dialogInterface, i) -> {
                                        }, (dialogInterface, i) -> {
                                            CustomControls.delete(CustomControls.genericFile() + "/" + genericItems);
                                            reload();
                                        }, dialogInterface -> mDeleteDialog = null, getActivity());
                                mDeleteDialog.show();
                                break;
                            case 1:
                                Utils.shareItem(getActivity(), genericItems, CustomControls.genericFile() +
                                        "/" + genericItems,getString(R.string.share_controller, CustomControls.genericFile()) +
                                        " -> Import -> Generic'.\n\n" + getString(R.string.share_app_message, BuildConfig.VERSION_NAME));
                                break;
                        }
                        return false;
                    });
                });

                items.add(itemslist);
            }
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        mSelectionMenu = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.custom_controls_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            mSelectionMenu = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                                    R.array.create_controls_options), (dialogInterface1, i1) -> {
                                        switch (i1) {
                                            case 0:
                                                createController(0, "/sys");
                                                break;
                                            case 1:
                                                createController(1, "/sys");
                                                break;
                                        }
                                    }).setOnDismissListener(dialogInterface12 -> mSelectionMenu = null);
                            mSelectionMenu.show();
                            break;
                        case 1:
                            mSelectionMenu = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                                    R.array.create_controls_options), (dialogInterface13, i12) -> {
                                        switch (i12) {
                                            case 0:
                                                createController(2, Environment.getExternalStorageDirectory().toString());
                                                break;
                                            case 1:
                                                createController(3, Environment.getExternalStorageDirectory().toString());
                                                break;
                                        }
                                    }).setOnDismissListener(dialogInterface14 -> mSelectionMenu = null);
                            mSelectionMenu.show();
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> mSelectionMenu = null);
        mSelectionMenu.show();
    }

    private void createController(int controllerType, String path) {
        mControllerType = controllerType;
        new FilePicker(
                null,
                path,
                ViewUtils.getThemeAccentColor(requireContext()),
                createController,
                requireActivity()).launch();
    }

    @SuppressLint("StringFormatInvalid")
    ActivityResultLauncher<Intent> createController = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null && FilePicker.getSelectedFile().exists()) {
                    File mSelectedFile = FilePicker.getSelectedFile();
                    if (mControllerType == -1) {
                        return;
                    }
                    if (mControllerType == 0 || mControllerType == 1) {
                        if (!Utils.getExtension(mSelectedFile.getName()).isEmpty() || mSelectedFile.getName().startsWith("/storage/")) {
                            Utils.snackbar(getRootView(), getString(R.string.invalid_path, mSelectedFile.getName()));
                            return;
                        }
                    } else {
                        if ((mControllerType == 2 || mControllerType == 3) && !Utils.existFile(Utils.readFile(mSelectedFile.getAbsolutePath()))) {
                            Utils.snackbar(getRootView(), getString(R.string.unsupported_controller, mSelectedFile.getName()));
                            return;
                        }
                    }
                    File controls = (mControllerType == 0 || mControllerType == 2 ? CustomControls.switchFile() : CustomControls.genericFile());
                    if (Utils.existFile(controls + "/" + mSelectedFile.getAbsolutePath().replaceFirst("/", "").
                            replace("/", "-")) || Utils.existFile(controls + "/" + mSelectedFile.getName())) {
                        Utils.snackbar(getRootView(), getString(R.string.already_added, mSelectedFile.getName()));
                        return;
                    }
                    Dialog selectControl = new Dialog(requireActivity());
                    selectControl.setMessage(getString(R.string.select_question, mSelectedFile.getName()));
                    selectControl.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    });
                    selectControl.setPositiveButton(getString(R.string.ok), (dialog1, id1) -> {
                        if (controls.exists() && controls.isFile()) {
                            controls.delete();
                        }
                        Utils.mkdir(controls.getAbsolutePath());
                        if (mControllerType == 0 || mControllerType == 1) {
                            CustomControls.exportPath(mSelectedFile.getAbsolutePath(), (mControllerType == 0 ? CustomControls.switchFile().toString() : CustomControls.genericFile().toString()));
                        } else {
                            Utils.copy(mSelectedFile.getAbsolutePath(), (mControllerType == 2 ? CustomControls.switchFile().toString() : CustomControls.genericFile().toString()));
                        }
                        reload();
                    });
                    selectControl.show();

                }
            }
    );

    /*
     * Taken and used almost as such from https://github.com/morogoku/MTweaks-KernelAdiutorMOD/
     * Ref: https://github.com/morogoku/MTweaks-KernelAdiutorMOD/blob/dd5a4c3242d5e1697d55c4cc6412a9b76c8b8e2e/app/src/main/java/com/moro/mtweaks/fragments/kernel/BoefflaWakelockFragment.java#L133
     */
    private void warningDialog() {
        View checkBoxView = View.inflate(getActivity(), R.layout.rv_checkbox, null);
        MaterialCheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
        checkBox.setChecked(true);
        checkBox.setText(getString(R.string.always_show));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> mAlertDialogue = isChecked);

        new Dialog(requireActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.custom_controls_message))
                .setCancelable(false)
                .setView(checkBoxView)
                .setNeutralButton(getString(R.string.documentation), (dialog, id)
                        -> Utils.launchUrl("https://smartpack.github.io/spkm/customcontrols/", getActivity()))
                .setPositiveButton(getString(R.string.got_it), (dialog, id)
                        -> Prefs.saveBoolean("custom_control_warning", mAlertDialogue, getActivity()))

                .show();
    }

    @Override
    public void onStart(){
        super.onStart();

        boolean showDialog = Prefs.getBoolean("custom_control_warning", true, getActivity());
        if (showDialog) {
            warningDialog();
        }
    }

}