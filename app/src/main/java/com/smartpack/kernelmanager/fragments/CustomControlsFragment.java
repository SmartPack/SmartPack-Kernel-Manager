/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.InputType;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.smartpack.kernelmanager.utils.CustomControls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 15, 2018
 */

public class CustomControlsFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private boolean mLoaded;
    private boolean mPermissionDenied;

    private Dialog mSelectionMenu;

    private String mPath;

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add));
        DrawableCompat.setTint(drawable, Color.WHITE);
        return drawable;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.custom_controls),
                getString(R.string.custom_controls_summary)));

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
                            load(items);
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

    private void load(List items) {
        File switchFile = new File(Utils.getInternalDataStorage() + "/controls/switch");
        if (switchFile.exists()) {
            CardView switchItemsCard = new CardView(getActivity());
            switchItemsCard.setTitle(getString(R.string.control_switch) + " Items");
            for (final File switchItems : switchFile.listFiles()) {
                if (switchItems.isFile() && CustomControls.hasCustomControl(Utils.readFile(switchItems.toString()))) {
                    SwitchView itemslist = new SwitchView();
                    itemslist.setSummary(CustomControls.getItemPath(switchItems.toString()));
                    itemslist.setChecked(CustomControls.isSwitchEnabled(Utils.readFile(switchItems.toString())));
                    itemslist.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                        @Override
                        public void onChanged(SwitchView switchView, boolean isChecked) {
                            CustomControls.enableSwitch(isChecked, Utils.readFile(switchItems.toString()), getActivity());
                            getHandler().postDelayed(() -> {
                                        itemslist.setChecked(CustomControls.isSwitchEnabled(Utils.readFile(switchItems.toString())));
                                    },
                                    500);
                        }
                    });

                    switchItemsCard.addItem(itemslist);
                }
            }
            items.add(switchItemsCard);
        }

        File genericFile = new File(Utils.getInternalDataStorage() + "/controls/generic");
        if (genericFile.exists()) {
            CardView genericItemsCard = new CardView(getActivity());
            genericItemsCard.setTitle(getString(R.string.control_generic) + " Items");
            for (final File genericItems : genericFile.listFiles()) {
                if (genericItems.isFile() && CustomControls.hasCustomControl(Utils.readFile(genericItems.toString()))) {
                    GenericSelectView itemslist = new GenericSelectView();
                    itemslist.setSummary(CustomControls.getItemPath(genericItems.toString()));
                    itemslist.setValue(CustomControls.getGenericValue(Utils.readFile(genericItems.toString())));
                    itemslist.setInputType(InputType.TYPE_CLASS_NUMBER);
                    itemslist.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                        @Override
                        public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                            CustomControls.setGenericValue(value, Utils.readFile(genericItems.toString()), getActivity());
                            getHandler().postDelayed(() -> {
                                        itemslist.setValue(CustomControls.getGenericValue(Utils.readFile(genericItems.toString())));
                                    },
                                    500);
                        }
                    });

                    genericItemsCard.addItem(itemslist);
                }
            }
            items.add(genericItemsCard);
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
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            return;
        }

        mSelectionMenu = new Dialog(getActivity()).setItems(getResources().getStringArray(
                R.array.custom_controls_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        createCustomControlSwitch();
                        break;
                    case 1:
                        createCustomControlGeneric();
                        break;
                }
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mSelectionMenu = null;
            }
        });
        mSelectionMenu.show();
    }

    private void createCustomControlSwitch() {
        Dialog create_control_switch = new Dialog(getActivity());
        create_control_switch.setIcon(R.mipmap.ic_launcher);
        create_control_switch.setTitle(getString(R.string.custom_controls));
        create_control_switch.setMessage(getString(R.string.custom_controls_message, Utils.getInternalDataStorage() + "/controls/switch/"));
        create_control_switch.setNeutralButton(getString(R.string.documentation), (dialog1, id1) -> {
            if (!Utils.isNetworkAvailable(getContext())) {
                Utils.toast(R.string.no_internet, getActivity());
                return;
            }
            Utils.launchUrl("https://smartpack.github.io/spkm/customcontrols/", getActivity());
        });
        create_control_switch.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
        });
        create_control_switch.setPositiveButton(getString(R.string.create), (dialog1, id1) -> {
            Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 0);
        });
        create_control_switch.show();
    }

    private void createCustomControlGeneric() {
        Dialog create_control_general = new Dialog(getActivity());
        create_control_general.setIcon(R.mipmap.ic_launcher);
        create_control_general.setTitle(getString(R.string.custom_controls));
        create_control_general.setMessage(getString(R.string.custom_controls_message, Utils.getInternalDataStorage() + "/controls/generic/"));
        create_control_general.setNeutralButton(getString(R.string.documentation), (dialog1, id1) -> {
            if (!Utils.isNetworkAvailable(getContext())) {
                Utils.toast(R.string.no_internet, getActivity());
                return;
            }
            Utils.launchUrl("https://smartpack.github.io/spkm/customcontrols/", getActivity());
        });
        create_control_general.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
        });
        create_control_general.setPositiveButton(getString(R.string.create), (dialog1, id1) -> {
            Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 1);
        });
        create_control_general.show();
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            mPermissionDenied = true;
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());
            mPath = Utils.getFilePath(file);

            if (!Utils.getExtension(file.getName()).isEmpty()) {
                Utils.toast(getString(R.string.invalid_path, mPath), getActivity());
                return;
            }
            File controls = new File(Utils.getInternalDataStorage() + (requestCode == 0 ?
                    "/controls/switch" : "/controls/generic"));
            if (Utils.existFile(controls + "/" + mPath.replaceFirst("/", "").
                    replace("/", "-"))) {
                Utils.toast(getString(R.string.already_added, mPath), getActivity());
                return;
            }
            Dialog selectControl = new Dialog(getActivity());
            selectControl.setMessage(getString(R.string.select_question, mPath));
            selectControl.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
            });
            selectControl.setPositiveButton(getString(R.string.ok), (dialog1, id1) -> {
                if (controls.exists() && controls.isFile()) {
                    controls.delete();
                }
                controls.mkdirs();
                CustomControls.exportPath(mPath, Utils.getInternalDataStorage() +
                        (requestCode == 0 ? "/controls/switch" : "/controls/generic"));
                reload();
            });
            selectControl.show();
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