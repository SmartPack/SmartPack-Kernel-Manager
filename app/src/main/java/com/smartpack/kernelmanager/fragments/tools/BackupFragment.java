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
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Device;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.Backup;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;
import in.sunilpaulmathew.sCommon.Utils.sExecutor;

/**
 * Created by willi on 09.07.16.
 */
public class BackupFragment extends RecyclerViewFragment {

    private Dialog mOptionsDialog;
    private Dialog mBackupFlashingDialog;
    private Backup.PARTITION mBackupPartition;
    private Dialog mItemOptionsDialog;
    private Dialog mDeleteDialog;
    private Dialog mRestoreDialog;

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getColoredIcon(R.drawable.ic_add, requireActivity());
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

        if (Backup.getBootPartition() != null) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.boot_partition),
                    Backup.getBootPartition()));
        }
        if (Backup.getRecoveryPartition() != null) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.recovery_partition),
                    Backup.getRecoveryPartition()));
        }
        if (Backup.getFotaPartition() != null) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.fota_partition),
                    Backup.getFotaPartition()));
        }

        if (mOptionsDialog != null) {
            mOptionsDialog.show();
        }
        if (mBackupFlashingDialog != null) {
            mBackupFlashingDialog.show();
        }
        if (mBackupPartition != null) {
            backup(mBackupPartition);
        }
        if (mItemOptionsDialog != null) {
            mItemOptionsDialog.show();
        }
        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }
        if (mRestoreDialog != null) {
            mRestoreDialog.show();
        }
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
        if (Backup.getBootPartition() != null) {
            List<RecyclerViewItem> boot = new ArrayList<>();
            itemInit(boot, Backup.PARTITION.BOOT);
            if (boot.size() > 0) {
                TitleView titleView = new TitleView();
                titleView.setText(getString(R.string.boot_partition));
                items.add(titleView);
                items.addAll(boot);
            }
        }
        if (Backup.getRecoveryPartition() != null) {
            List<RecyclerViewItem> recovery = new ArrayList<>();
            itemInit(recovery, Backup.PARTITION.RECOVERY);
            if (recovery.size() > 0) {
                TitleView titleView = new TitleView();
                titleView.setText(getString(R.string.recovery_partition));
                items.add(titleView);
                items.addAll(recovery);
            }
        }
        if (Backup.getFotaPartition() != null) {
            List<RecyclerViewItem> fota = new ArrayList<>();
            itemInit(fota, Backup.PARTITION.FOTA);
            if (fota.size() > 0) {
                TitleView titleView = new TitleView();
                titleView.setText(getString(R.string.fota_partition));
                items.add(titleView);
                items.addAll(fota);
            }
        }
    }

    private void itemInit(List<RecyclerViewItem> items, final Backup.PARTITION partition) {
        if (SuFile.open(Backup.getPath(partition)).exists() && SuFile.open(Backup.getPath(partition)).length() > 0
                && Backup.getItemsList(partition).size() > 0) {
            for (final String backup : Backup.getItemsList(partition)) {
                final File image = SuFile.open(Backup.getPath(partition) + "/" + backup);
                if (image.isFile()) {
                    DescriptionView descriptionView = new DescriptionView();
                    descriptionView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_file, requireContext()));
                    descriptionView.setTitle(image.getName().replace(".img", ""));
                    descriptionView.setSummary((image.length() / 1024L / 1024L) + getString(R.string.mb));
                    descriptionView.setOnItemClickListener(item -> {
                        mItemOptionsDialog = new Dialog(requireActivity())
                                .setItems(getResources().getStringArray(R.array.backup_item_options),
                                        (dialogInterface, i) -> {
                                            switch (i) {
                                                case 0:
                                                    restore(partition, image, false);
                                                    break;
                                                case 1:
                                                    delete(image);
                                                    break;
                                            }
                                        })
                                .setOnDismissListener(dialogInterface -> mItemOptionsDialog = null);
                        mItemOptionsDialog.show();
                    });

                    items.add(descriptionView);
                }
            }
        }
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.backup_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            showBackupFlashingDialog(null);
                            break;
                        case 1:
                            FilePicker filePicker = new FilePicker(flashImage, requireActivity());
                            filePicker.setExtension("img");
                            filePicker.setPath(Environment.getExternalStorageDirectory().toString());
                            filePicker.setAccentColor(ViewUtils.getThemeAccentColor(requireContext()));
                            filePicker.launch();
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> mOptionsDialog = null);
        mOptionsDialog.show();
    }

    private void showBackupFlashingDialog(final File file) {
        final LinkedHashMap<String, Backup.PARTITION> menu = getPartitionMenu();
        mBackupFlashingDialog = new Dialog(requireActivity()).setItems(menu.keySet().toArray(
                new String[0]), (dialogInterface, i) -> {
                    Backup.PARTITION partition = menu.values().toArray(new Backup.PARTITION[0])[i];
                    if (file != null) {
                        restore(partition, file, true);
                    } else {
                        backup(partition);
                    }
                }).setOnDismissListener(dialogInterface -> mBackupFlashingDialog = null);
        mBackupFlashingDialog.show();
    }

    private void restore(final Backup.PARTITION partition, final File file, final boolean flashing) {
        mRestoreDialog = ViewUtils.dialogBuilder(flashing? getString(R.string.sure_message, file.getName()) :
                getString(R.string.restore_sure_message, file.getName()), (dialogInterface, i) -> {
                }, (dialogInterface, i) -> new sExecutor() {

                    @Override
                    public void onPreExecute() {
                        showProgressMessage(getString(flashing ? R.string.flashing : R.string.restoring));
                    }

                    @Override
                    public void doInBackground() {
                        Backup.restore(file, partition);
                    }

                    @Override
                    public void onPostExecute() {
                        hideProgressMessage();
                        // Show an option to reboot after flashing/restoring
                        Dialog dialog = new Dialog(requireActivity());
                        dialog.setIcon(R.mipmap.ic_launcher);
                        dialog.setMessage(getString(R.string.reboot_dialog, getString(flashing ?
                                R.string.flashing : R.string.restoring)));
                        dialog.setCancelable(false);
                        dialog.setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                        });
                        dialog.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                            Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                            new sExecutor() {

                                @Override
                                public void onPreExecute() {
                                    showProgressMessage(getString(R.string.executing) + ("..."));
                                }

                                @Override
                                public void doInBackground() {
                                    RootUtils.runCommand(Utils.prepareReboot());
                                }

                                @Override
                                public void onPostExecute() {
                                    hideProgressMessage();
                                }
                            }.execute();
                        });
                        dialog.show();
                    }
                }.execute(), dialogInterface -> mRestoreDialog = null, getActivity());
        mRestoreDialog.show();
    }

    private void delete(final File file) {
        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                (dialogInterface, i) -> {
                }, (dialogInterface, i) -> {
                    file.delete();
                    reload();
                }, dialogInterface -> mDeleteDialog = null, getActivity());
        mDeleteDialog.show();
    }

    private void backup(final Backup.PARTITION partition) {
        mBackupPartition = partition;
        ViewUtils.dialogEditText(partition == Backup.PARTITION.BOOT ? Device.getKernelVersion(false) : null,
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.snackbar(getRootView(), getString(R.string.name_empty));
                        return;
                    }

                    if (!text.endsWith(".img")) {
                        text += ".img";
                    }

                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }

                    if (Utils.existFile(Backup.getPath(partition) + "/" + text)) {
                        Utils.snackbar(getRootView(), getString(R.string.already_exists, text));
                        return;
                    }

                    final String path = text;
                    new sExecutor(){

                        @Override
                        public void onPreExecute() {
                            showProgressMessage(getString(R.string.backing_up));
                        }

                        @Override
                        public void doInBackground() {
                            Backup.backup(path, partition);
                        }

                        @Override
                        public void onPostExecute() {
                            hideProgressMessage();
                            reload();
                        }
                    }.execute();
                }, getActivity()).setOnDismissListener(dialogInterface -> mBackupPartition = null).show();
    }

    private LinkedHashMap<String, Backup.PARTITION> getPartitionMenu() {
        LinkedHashMap<String, Backup.PARTITION> partitions = new LinkedHashMap<>();
        if (Backup.getBootPartition() != null) {
            partitions.put(getString(R.string.boot_partition), Backup.PARTITION.BOOT);
        }
        if (Backup.getRecoveryPartition() != null) {
            partitions.put(getString(R.string.recovery_partition), Backup.PARTITION.RECOVERY);
        }
        if (Backup.getFotaPartition() != null) {
            partitions.put(getString(R.string.fota_partition), Backup.PARTITION.FOTA);
        }
        return partitions;
    }

    ActivityResultLauncher<Intent> flashImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && FilePicker.getSelectedFile().exists()) {
                    File mSelectedFile = FilePicker.getSelectedFile();
                    if (!Utils.getExtension(mSelectedFile.getAbsolutePath()).equals("img")) {
                        Utils.snackbar(getRootView(), getString(R.string.wrong_extension, ".img"));
                        return;
                    }
                    showBackupFlashingDialog(mSelectedFile);
                }
            }
    );

}