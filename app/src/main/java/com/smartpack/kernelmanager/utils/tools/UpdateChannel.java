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
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.utils.tools;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentManager;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.fragments.tools.SmartPackFragment;
import com.smartpack.kernelmanager.views.recyclerview.GenericInputView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 25, 2020
 */

public class UpdateChannel extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private boolean mPermissionDenied;

    private String UPDATE_CHANNEL = Utils.getInternalDataStorage() + "/Update_Channel";

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        createInit(items);
        requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                            createInit(items);
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

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.update_channel_create),
                getText(R.string.update_channel_message)));
    }

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    private void createInit(List<RecyclerViewItem> items) {
        DescriptionView info = new DescriptionView();
        info.setDrawable(getResources().getDrawable(R.drawable.ic_info));
        info.setTitle(getText(R.string.update_channel_instructions));
        info.setFullSpan(true);

        items.add(info);

        GenericInputView kernel_name = new GenericInputView();
        kernel_name.setTitle(getString(R.string.kernel_name));
        kernel_name.setValue(Utils.existFile(UPDATE_CHANNEL + "/kernel_name") ?
                Utils.readFile(UPDATE_CHANNEL + "/kernel_name") : getString(R.string.kernel_name_summary));
        kernel_name.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                if (value.isEmpty()) {
                    KernelUpdater.clearUpdateInfo();
                    Utils.toast(getString(R.string.item_empty, getString(R.string.kernel_name)), getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                kernel_name.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/kernel_name");
            }
        });

        items.add(kernel_name);

        GenericInputView kernel_version = new GenericInputView();
        kernel_version.setTitle(getString(R.string.kernel_version));
        kernel_version.setValue(Utils.existFile(UPDATE_CHANNEL + "/kernel_version") ?
                Utils.readFile(UPDATE_CHANNEL + "/kernel_version") : getString(R.string.kernel_version_summary));
        kernel_version.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                if (value.isEmpty()) {
                    KernelUpdater.clearUpdateInfo();
                    Utils.toast(getString(R.string.item_empty, getString(R.string.kernel_version)), getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                kernel_version.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/kernel_version");
            }
        });

        items.add(kernel_version);

        GenericInputView download_link = new GenericInputView();
        download_link.setTitle(getString(R.string.download_link));
        download_link.setValue(Utils.existFile(UPDATE_CHANNEL + "/download_link") ?
                Utils.readFile(UPDATE_CHANNEL + "/download_link") : getString(R.string.download_link_summary));
        download_link.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                if (value.isEmpty()) {
                    KernelUpdater.clearUpdateInfo();
                    Utils.toast(getString(R.string.item_empty, getString(R.string.download_link)), getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                download_link.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/download_link");
            }
        });

        items.add(download_link);

        GenericInputView change_logs = new GenericInputView();
        change_logs.setTitle(getString(R.string.change_logs));
        change_logs.setValue(Utils.existFile(UPDATE_CHANNEL + "/change_logs") ?
                Utils.readFile(UPDATE_CHANNEL + "/change_logs") : getString(R.string.changelog_summary));
        change_logs.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                change_logs.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/change_logs");
            }
        });

        items.add(change_logs);

        GenericInputView sha1 = new GenericInputView();
        sha1.setTitle(getString(R.string.sha1));
        sha1.setValue(Utils.existFile(UPDATE_CHANNEL + "/sha1") ?
                Utils.readFile(UPDATE_CHANNEL + "/sha1") : getString(R.string.sha1_summary));
        sha1.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                sha1.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/sha1");
            }
        });

        items.add(sha1);

        GenericInputView supportGroup = new GenericInputView();
        supportGroup.setTitle(getString(R.string.support_group));
        supportGroup.setValue(Utils.existFile(UPDATE_CHANNEL + "/support_group") ?
                Utils.readFile(UPDATE_CHANNEL + "/support_group") : getString(R.string.support_group_summary));
        supportGroup.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                supportGroup.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/support_group");
            }
        });

        items.add(supportGroup);

        GenericInputView donation_link = new GenericInputView();
        donation_link.setTitle(getString(R.string.donation_link));
        donation_link.setValue(Utils.existFile(UPDATE_CHANNEL + "/donation_link") ?
                Utils.readFile(UPDATE_CHANNEL + "/donation_link") : getString(R.string.donation_link_summary));
        donation_link.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                makeUpdateCHannelFolder();
                donation_link.setValue(value);
                Utils.create(value, UPDATE_CHANNEL + "/donation_link");
            }
        });

        items.add(donation_link);

        DescriptionView clear_all = new DescriptionView();
        clear_all.setTitle(getString(R.string.clear));
        clear_all.setSummary(getString(R.string.clear_summary));
        clear_all.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                File file = new File(UPDATE_CHANNEL);
                if (file.exists() && file.isDirectory() && file.list().length > 0) {
                    new Dialog(getActivity())
                            .setMessage(getString(R.string.sure_question))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                                clarAll();
                            })
                            .show();
                } else {
                    Utils.toast(getString(R.string.clear_message), getActivity());
                }
            }
        });

        items.add(clear_all);

        DescriptionView create = new DescriptionView();
        create.setTitle(getString(R.string.update_channel_create));
        create.setSummary(getString(R.string.submit_summary));
        create.setFullSpan(true);
        create.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (Utils.readFile(UPDATE_CHANNEL + "/kernel_name").isEmpty() &&
                        Utils.readFile(UPDATE_CHANNEL + "/kernel_version").isEmpty() &&
                        Utils.readFile(UPDATE_CHANNEL + "/download_link").isEmpty()) {
                    Utils.toast(getString(R.string.submit_failed), getActivity());
                } else {
                    createChannel();
                }
            }
        });

        items.add(create);
    }

    private void clarAll() {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.clearing) + ("..."));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                Utils.sleep(2);
                Utils.delete(UPDATE_CHANNEL);
                reload();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
            }
        }.execute();
    }

    private void createChannel() {
        ViewUtils.dialogEditText(Utils.readFile(UPDATE_CHANNEL + "/kernel_name"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }, new ViewUtils.OnDialogEditTextListener() {
                    @Override
                    public void onClick(String text) {
                        if (text.isEmpty()) {
                            Utils.toast(R.string.name_empty, getActivity());
                            return;
                        }
                        if (!text.endsWith(".json")) {
                            text += ".json";
                        }
                        if (text.contains(" ")) {
                            text = text.replace(" ", "_");
                        }
                        String json = Utils.getInternalDataStorage() + "/" + text;
                        if (Utils.existFile(json)) {
                            Utils.toast(getString(R.string.already_exists, text), getActivity());
                            return;
                        }
                        new AsyncTask<Void, Void, Void>() {
                            private ProgressDialog mProgressDialog;
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                mProgressDialog = new ProgressDialog(getActivity());
                                mProgressDialog.setMessage(getString(R.string.creating, json) + ("..."));
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();
                            }
                            @Override
                            protected Void doInBackground(Void... voids) {
                                String changelog = Utils.existFile(UPDATE_CHANNEL + "/change_logs") &&
                                        !Utils.readFile(UPDATE_CHANNEL + "/change_logs").isEmpty() ?
                                        Utils.readFile(UPDATE_CHANNEL + "/change_logs") : "";
                                String sha1 = Utils.existFile(UPDATE_CHANNEL + "/sha1") &&
                                        !Utils.readFile(UPDATE_CHANNEL + "/sha1").isEmpty() ?
                                        Utils.readFile(UPDATE_CHANNEL + "/sha1") : "";
                                String support_group = Utils.existFile(UPDATE_CHANNEL + "/support_group") &&
                                        !Utils.readFile(UPDATE_CHANNEL + "/support_group").isEmpty() ?
                                        Utils.readFile(UPDATE_CHANNEL + "/support_group") : "";
                                String donation_link = Utils.existFile(UPDATE_CHANNEL + "/donation_link") &&
                                        !Utils.readFile(UPDATE_CHANNEL + "/donation_link").isEmpty() ?
                                        Utils.readFile(UPDATE_CHANNEL + "/donation_link") : "";
                                Utils.sleep(3);
                                try {
                                    FileWriter writer = new FileWriter(json);
                                    JsonObject obj = new JsonObject();
                                    JsonObject kernel = new JsonObject();
                                    kernel.put("name", Utils.readFile(UPDATE_CHANNEL + "/kernel_name"));
                                    kernel.put("version", Utils.readFile(UPDATE_CHANNEL + "/kernel_version"));
                                    kernel.put("link", Utils.readFile(UPDATE_CHANNEL + "/download_link"));
                                    kernel.put("changelog_url", changelog);
                                    kernel.put("sha1", sha1);
                                    obj.put("kernel", kernel);
                                    JsonObject support = new JsonObject();
                                    support.put("link", support_group);
                                    support.put("donation", donation_link);
                                    obj.put("support", support);
                                    Jsoner.serialize(obj, writer);
                                    writer.close();
                                } catch (IOException e) {
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                try {
                                    mProgressDialog.dismiss();
                                } catch (IllegalArgumentException ignored) {
                                }
                                if (Utils.existFile(json)) {
                                    new Dialog(getActivity())
                                            .setMessage(getString(R.string.json_created, json))
                                            .setCancelable(false)
                                            .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                                                FragmentManager fragmentManager = getFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .replace(R.id.content_frame, new SmartPackFragment()).commit();
                                            })
                                            .show();
                                } else {
                                    Utils.toast(getString(R.string.json_not_created), getActivity());
                                }
                            }
                        }.execute();
                    }
                }, getActivity()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        }).show();
    }

    private void makeUpdateCHannelFolder(){
        File file = new File(UPDATE_CHANNEL);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            mPermissionDenied = true;
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
        }
    }

}