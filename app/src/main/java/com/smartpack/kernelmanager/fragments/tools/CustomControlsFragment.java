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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

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

import in.sunilpaulmathew.rootfilepicker.activities.FilePickerActivity;
import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 15, 2018
 */

public class CustomControlsFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private boolean mAlertDialogue = true;
    private boolean mLoaded;
    private boolean mPermissionDenied;

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
        if (!mLoaded) {
            mLoaded = true;
            requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    clearItems();
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            switchItemsList(items);
                            genericItemsList(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> items) {
                            super.onPostExecute(items);
                            for (RecyclerViewItem item : items) {
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

    private void switchItemsList(List<RecyclerViewItem> items) {
        for (final String switchItems : CustomControls.switchList(requireActivity())) {
            if (CustomControls.switchFile(requireActivity()).length() > 0 && Utils.existFile(Utils.readFile(
                    CustomControls.switchFile(requireActivity()).toString() + "/" + switchItems))) {
                SwitchView itemslist = new SwitchView();
                itemslist.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
                itemslist.setSummary(Utils.readFile(CustomControls.switchFile(requireActivity()).toString()+ "/" + switchItems));
                itemslist.setChecked(CustomControls.isSwitchEnabled(Utils.readFile(
                        CustomControls.switchFile(requireActivity()).toString() + "/" + switchItems)));
                itemslist.addOnSwitchListener((switchView, isChecked) -> {
                    CustomControls.enableSwitch(isChecked, Utils.readFile(
                            CustomControls.switchFile(requireActivity()).toString() + "/" + switchItems), getActivity());
                    getHandler().postDelayed(() -> {
                                itemslist.setChecked(CustomControls.isSwitchEnabled(Utils.readFile(
                                        CustomControls.switchFile(requireActivity()).toString() + "/" + switchItems)));
                            },
                            500);
                });
                itemslist.setOnMenuListener((itemslist1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.delete));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.share));
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @SuppressLint("StringFormatInvalid")
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 0:
                                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                            (dialogInterface, i) -> {
                                            }, (dialogInterface, i) -> {
                                                CustomControls.delete(CustomControls.switchFile(requireActivity()).toString() + "/" + switchItems);
                                                reload();
                                            }, dialogInterface -> mDeleteDialog = null, getActivity());
                                    mDeleteDialog.show();
                                    break;
                                case 1:
                                    Utils.shareItem(getActivity(), switchItems, CustomControls.switchFile(requireActivity()).toString() +
                                            "/" + switchItems,getString(R.string.share_controller, CustomControls.switchFile(requireActivity())) +
                                            " -> Import -> Switch'.\n\n" + getString(R.string.share_app_message, BuildConfig.VERSION_NAME));
                                    break;
                            }
                            return false;
                        }
                    });
                });

                items.add(itemslist);
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void genericItemsList(List<RecyclerViewItem> items) {
        for (final String genericItems : CustomControls.genericList(requireActivity())) {
            if (CustomControls.switchFile(requireActivity()).length() > 0 && Utils.existFile(Utils.readFile(
                    CustomControls.genericFile(requireActivity()).toString() + "/" + genericItems))) {
                GenericInputView itemslist = new GenericInputView();
                itemslist.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
                itemslist.setTitle(Utils.readFile(CustomControls.genericFile(requireActivity()).toString()+ "/" + genericItems));
                itemslist.setValue(CustomControls.getGenericValue(Utils.readFile(
                        CustomControls.genericFile(requireActivity()).toString() + "/" + genericItems)));
                itemslist.setInputType(InputType.TYPE_CLASS_NUMBER);
                itemslist.setOnGenericValueListener((genericInputView, value) -> {
                    CustomControls.setGenericValue(value, Utils.readFile(CustomControls.genericFile(requireActivity()).toString() +
                            "/" + genericItems), getActivity());
                    getHandler().postDelayed(() -> {
                                itemslist.setValue(CustomControls.getGenericValue(Utils.readFile(
                                        CustomControls.genericFile(requireActivity()).toString() + "/" + genericItems)));
                            },
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
                                            CustomControls.delete(CustomControls.genericFile(requireActivity()).toString() + "/" + genericItems);
                                            reload();
                                        }, dialogInterface -> mDeleteDialog = null, getActivity());
                                mDeleteDialog.show();
                                break;
                            case 1:
                                Utils.shareItem(getActivity(), genericItems, CustomControls.genericFile(requireActivity()).toString() +
                                        "/" + genericItems,getString(R.string.share_controller, CustomControls.genericFile(requireActivity())) +
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
        if (mPermissionDenied) {
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
            return;
        }

        mSelectionMenu = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.custom_controls_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            mSelectionMenu = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                                    R.array.create_controls_options), (dialogInterface1, i1) -> {
                                        switch (i1) {
                                            case 0:
                                                FilePicker.setPath("/sys");
                                                Intent filePicker = new Intent(getActivity(), FilePickerActivity.class);
                                                startActivityForResult(filePicker, 0);
                                                break;
                                            case 1:
                                                FilePicker.setPath("/sys");
                                                Intent genericIntent = new Intent(getActivity(), FilePickerActivity.class);
                                                startActivityForResult(genericIntent, 1);
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
                                                FilePicker.setPath(Environment.getExternalStorageDirectory().toString());
                                                Intent switchIntent = new Intent(getActivity(), FilePickerActivity.class);
                                                startActivityForResult(switchIntent, 2);
                                                break;
                                            case 1:
                                                FilePicker.setPath(Environment.getExternalStorageDirectory().toString());
                                                Intent genericIntent = new Intent(getActivity(), FilePickerActivity.class);
                                                startActivityForResult(genericIntent, 3);
                                                break;
                                        }
                                    }).setOnDismissListener(dialogInterface14 -> mSelectionMenu = null);
                            mSelectionMenu.show();
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> mSelectionMenu = null);
        mSelectionMenu.show();
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            mPermissionDenied = true;
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
        }
    }

    @Override
    public void onPermissionGranted(int request) {
        super.onPermissionGranted(request);
        if (request == 0) {
            mPermissionDenied = false;
            reload();
        }
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            File mSelectedFile = FilePicker.getSelectedFile();
            if (requestCode == 0 || requestCode == 1) {
                if (!Utils.getExtension(mSelectedFile.getName()).isEmpty() || mSelectedFile.getName().startsWith("/storage/")) {
                    Utils.snackbar(getRootView(), getString(R.string.invalid_path, mSelectedFile.getName()));
                    return;
                }
            } else {
                if ((requestCode == 2 || requestCode == 3) && !Utils.existFile(Utils.readFile(mSelectedFile.getAbsolutePath()))) {
                    Utils.snackbar(getRootView(), getString(R.string.unsupported_controller, Utils.readFile(mSelectedFile.getName())));
                    return;
                }
            }
            File controls = (requestCode == 0 || requestCode == 2 ? CustomControls.switchFile(requireActivity()) : CustomControls.genericFile(requireActivity()));
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
                controls.mkdirs();
                if (requestCode == 0 || requestCode == 1) {
                    CustomControls.exportPath(mSelectedFile.getAbsolutePath(), (requestCode == 0 ? CustomControls.switchFile(requireActivity()).toString() : CustomControls.genericFile(requireActivity()).toString()));
                } else {
                    Utils.copy(mSelectedFile.getAbsolutePath(), (requestCode == 2 ? CustomControls.switchFile(requireActivity()).toString() : CustomControls.genericFile(requireActivity()).toString()));
                }
                reload();
            });
            selectControl.show();
        }
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
        mPermissionDenied = false;
        mLoaded = false;
    }

}