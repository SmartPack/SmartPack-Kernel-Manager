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

package com.smartpack.kernelmanager.fragments.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.Menu;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.TerminalActivity;
import com.smartpack.kernelmanager.activities.UpdateChannelActivity;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.KernelUpdater;
import com.smartpack.kernelmanager.utils.tools.SmartPack;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.GenericInputView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 24, 2018
 */

public class SmartPackFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private boolean mPermissionDenied;

    private Dialog mOptionsDialog;

    private String mPath;

    private String logFolder = Utils.getInternalDataStorage() + "/logs";

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getColoredIcon(R.drawable.ic_flash, requireActivity());
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public int getSpanCount() {
        int span = Utils.getOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        if (itemsSize() != 0 && span > itemsSize()) {
            span = itemsSize();
        }
        return span;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
        requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                            SmartPackInit(items);
                            OtherOptionsInit(items);
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

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.smartpack),
                getString(R.string.flasher_summary)));
    }

    @SuppressLint("StringFormatInvalid")
    private void SmartPackInit(List<RecyclerViewItem> items) {
        TitleView smartpack = new TitleView();
        smartpack.setText(!KernelUpdater.getKernelName(requireActivity()).equals("Unavailable") ? KernelUpdater.getKernelName(requireActivity()) :
                getString(R.string.kernel_information));

        items.add(smartpack);

        DescriptionView kernelinfo = new DescriptionView();
        kernelinfo.setTitle(getString(R.string.kernel_current));
        kernelinfo.setSummary(RootUtils.runAndGetOutput("uname -r"));

        items.add(kernelinfo);

        GenericInputView updateChannel = new GenericInputView();
        updateChannel.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
        updateChannel.setTitle(getString(R.string.update_channel));
        updateChannel.setValue((!KernelUpdater.getKernelName(requireActivity()).equals("Unavailable"))
                ? KernelUpdater.getUpdateChannel(requireActivity()) : getString(R.string.update_channel_summary));
        updateChannel.setOnGenericValueListener((genericSelectView, value) -> {
            if (mPermissionDenied) {
                Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                return;
            }
            if (!Utils.isNetworkAvailable(requireActivity())) {
                Utils.snackbar(getRootView(), getString(R.string.no_internet));
                return;
            }
            if (value.isEmpty()) {
                new File(requireActivity().getFilesDir().getPath() + "/updatechannel").delete();
                new File(requireActivity().getFilesDir().getPath() + "/release").delete();
                Utils.snackbar(getRootView(), getString(R.string.update_channel_empty));
                reload();
                return;
            }
            if (value.contains("/blob/")) {
                value = value.replace("/blob/", "/raw/");
            }
            KernelUpdater.acquireUpdateInfo(value, getActivity());
            reload();

        });
        if (!KernelUpdater.getKernelName(requireActivity()).equals("Unavailable")) {
            updateChannel.setOnMenuListener((itemslist1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.remove));
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.share));
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 0:
                            new Dialog(requireActivity())
                                    .setMessage(getString(R.string.sure_question))
                                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                                    })
                                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                        new File(requireActivity().getFilesDir().getPath() + "/updatechannel").delete();
                                        new File(requireActivity().getFilesDir().getPath() + "/release").delete();
                                        reload();
                                    })
                                    .show();
                            break;
                        case 1:
                            if (Utils.isNetworkAvailable(requireActivity())) {
                                Intent shareChannel = new Intent();
                                shareChannel.setAction(Intent.ACTION_SEND);
                                shareChannel.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                shareChannel.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_channel_message,
                                        Utils.readFile(requireActivity().getFilesDir().getPath() + "/updatechannel")));
                                shareChannel.setType("text/plain");
                                Intent shareIntent = Intent.createChooser(shareChannel, null);
                                startActivity(shareIntent);
                            } else {
                                Utils.snackbar(getRootView(), getString(R.string.no_internet));
                            }
                            break;
                    }
                    return false;
                });
            });
        }

        items.add(updateChannel);

        if (KernelUpdater.getLatestVersion(requireActivity()).equals("Unavailable")) {
            DescriptionView info = new DescriptionView();
            info.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_info, requireContext()));
            info.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
            info.setTitle(getString(R.string.update_channel_info, Utils.getInternalDataStorage()));
            info.setOnItemClickListener(item -> Utils.launchUrl("https://smartpack.github.io/kerneldownloads/", getActivity()));
            info.setFullSpan(true);
            info.setOnMenuListener((info1, popupMenu) -> {
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.update_channel_create));
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (mPermissionDenied) {
                        Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                    } else {
                        Intent createUpdateChannel = new Intent(getActivity(), UpdateChannelActivity.class);
                        startActivity(createUpdateChannel);
                    }
                    return false;
                });
            });

            items.add(info);
        }

        if (!KernelUpdater.getLatestVersion(requireActivity()).equals("Unavailable")) {
            DescriptionView latest = new DescriptionView();
            latest.setTitle(getString(R.string.kernel_latest));
            latest.setSummary(KernelUpdater.getLatestVersion(requireActivity()));

            items.add(latest);
        }

        if (!KernelUpdater.getChangeLog(requireActivity()).equals("Unavailable")) {
            DescriptionView changelogs = new DescriptionView();
            changelogs.setTitle(getString(R.string.change_logs));
            changelogs.setSummary(getString(R.string.change_logs_summary));
            changelogs.setOnItemClickListener(item -> {
                if (KernelUpdater.getChangeLog(requireActivity()).contains("https://") ||
                        KernelUpdater.getChangeLog(requireActivity()).contains("http://")) {
                    Utils.launchUrl(KernelUpdater.getChangeLog(requireActivity()), getActivity());
                } else {
                    new Dialog(requireActivity())
                            .setTitle(KernelUpdater.getKernelName(requireActivity()) + " " + KernelUpdater.getLatestVersion(requireActivity()))
                            .setMessage(KernelUpdater.getChangeLog(requireActivity()))
                            .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> {
                            })
                            .show();
                }
            });

            items.add(changelogs);
        }

        if (!KernelUpdater.getSupport(requireActivity()).equals("Unavailable")) {
            DescriptionView support = new DescriptionView();
            support.setTitle(getString(R.string.support));
            support.setSummary(getString(R.string.support_summary));
            support.setOnItemClickListener(item -> {
                if (KernelUpdater.getSupport(requireActivity()).contains("https://") ||
                        KernelUpdater.getSupport(requireActivity()).contains("http://")) {
                    Utils.launchUrl(KernelUpdater.getSupport(requireActivity()), getActivity());
                } else {
                    Utils.snackbar(getRootView(), getString(R.string.unknown_link));
                }
            });

            items.add(support);
        }

        if (!KernelUpdater.getUrl(requireActivity()).equals("Unavailable")) {
            DescriptionView download = new DescriptionView();
            download.setTitle(getString(R.string.download));
            download.setSummary(getString(R.string.get_it_summary));
            download.setOnItemClickListener(item -> {
                if (mPermissionDenied) {
                    Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                    return;
                }
                downloadKernel();
            });

            items.add(download);
        }

        if (!KernelUpdater.getLatestVersion(requireActivity()).equals("Unavailable")) {
            DescriptionView donations = new DescriptionView();
            donations.setTitle(getString(R.string.donations));
            donations.setSummary(getString(R.string.donations_summary));
            donations.setOnItemClickListener(item -> {
                if (KernelUpdater.getDonationLink(requireActivity()).contains("https://") ||
                            KernelUpdater.getDonationLink(requireActivity()).contains("http://")) {
                    Utils.launchUrl(KernelUpdater.getDonationLink(requireActivity()), getActivity());
                } else {
                    Utils.snackbar(getRootView(), getString(R.string.unknown_link));
                }

            });

            items.add(donations);
        }

        if (!KernelUpdater.getKernelName(requireActivity()).equals("Unavailable") && Utils.isDownloadBinaries()) {
            SwitchView update_check = new SwitchView();
            update_check.setSummary(getString(R.string.check_update));
            update_check.setChecked(Prefs.getBoolean("update_check", false, getActivity()));
            update_check.addOnSwitchListener((switchview, isChecked) -> {
                Prefs.saveBoolean("update_check", isChecked, getActivity());
                if (Prefs.getBoolean("update_check", true, getActivity())) {
                    Utils.snackbar(getRootView(), getString(R.string.update_check_message, !KernelUpdater.getKernelName(requireActivity()).
                            equals("Unavailable") ? KernelUpdater.getKernelName(requireActivity()) : "this"));
                }
            });

            items.add(update_check);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void OtherOptionsInit(List<RecyclerViewItem> items) {
        if (!Utils.isPackageInstalled("com.smartpack.busyboxinstaller", requireActivity())) {
            // Advertise Own App
            TitleView bb = new TitleView();
            bb.setText(getString(R.string.busybox_installer));

            items.add(bb);

            DescriptionView busybox = new DescriptionView();
            busybox.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
            busybox.setSummary(getString(R.string.busybox_installer_summary));
            busybox.setFullSpan(true);
            busybox.setOnItemClickListener(item -> {
                Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.busyboxinstaller", getActivity());
            });
            items.add(busybox);
        }

        TitleView others = new TitleView();
        others.setText(getString(R.string.other_options));

        items.add(others);

        DescriptionView logcat = new DescriptionView();
        logcat.setTitle(getString(R.string.logcat));
        logcat.setSummary(getString(R.string.logcat_summary));
        logcat.setOnItemClickListener(item -> {
            if (mPermissionDenied) {
                Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                return;
            }
            SmartPack.prepareFolder(logFolder);
            new Execute().execute("logcat -d > " + logFolder + "/logcat-" + Utils.getTimeStamp());
            new Execute().execute("logcat  -b radio -v time -d > " + logFolder + "/radio-" + Utils.getTimeStamp());
            new Execute().execute("logcat -b events -v time -d > " + logFolder + "/events-" + Utils.getTimeStamp());
        });
        items.add(logcat);

        if (Utils.existFile("/proc/last_kmsg")) {
            DescriptionView lastkmsg = new DescriptionView();
            lastkmsg.setTitle(getString(R.string.last_kmsg));
            lastkmsg.setSummary(getString(R.string.last_kmsg_summary));
            lastkmsg.setOnItemClickListener(item -> {
                if (mPermissionDenied) {
                    Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                    return;
                }
                SmartPack.prepareFolder(logFolder);
                new Execute().execute("cat /proc/last_kmsg > " + logFolder + "/last_kmsg-" + Utils.getTimeStamp());
            });
            items.add(lastkmsg);
        }

        DescriptionView dmesg = new DescriptionView();
        dmesg.setTitle(getString(R.string.driver_message));
        dmesg.setSummary(getString(R.string.driver_message_summary));
        dmesg.setOnItemClickListener(item -> {
            if (mPermissionDenied) {
                Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                return;
            }
            SmartPack.prepareFolder(logFolder);
            new Execute().execute("dmesg > " + logFolder + "/dmesg-" + Utils.getTimeStamp());
        });
        items.add(dmesg);

        if (Utils.existFile("/sys/fs/pstore/dmesg-ramoops*")) {
            DescriptionView dmesgRamoops = new DescriptionView();
            dmesgRamoops.setTitle(getString(R.string.driver_ramoops));
            dmesgRamoops.setSummary(getString(R.string.driver_ramoops_summary));
            dmesgRamoops.setOnItemClickListener(item -> {
                if (mPermissionDenied) {
                    Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                    return;
                }
                SmartPack.prepareFolder(logFolder);
                new Execute().execute("cat /sys/fs/pstore/dmesg-ramoops* > " + logFolder + "/dmesg-ramoops-" + Utils.getTimeStamp());
            });
            items.add(dmesgRamoops);
        }

        if (Utils.existFile("/sys/fs/pstore/console-ramoops*")) {
            DescriptionView ramoops = new DescriptionView();
            ramoops.setTitle(getString(R.string.console_ramoops));
            ramoops.setSummary(getString(R.string.console_ramoops_summary));
            ramoops.setOnItemClickListener(item -> {
                if (mPermissionDenied) {
                    Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                    return;
                }
                SmartPack.prepareFolder(logFolder);
                new Execute().execute("cat /sys/fs/pstore/console-ramoops* > " + logFolder + "/console-ramoops-" + Utils.getTimeStamp());
            });
            items.add(ramoops);
        }

        DescriptionView shell = new DescriptionView();
        shell.setTitle(getString(R.string.shell));
        shell.setSummary(getString(R.string.shell_summary));
        shell.setOnItemClickListener(item -> {
            Intent terminal = new Intent(getActivity(), TerminalActivity.class);
            startActivity(terminal);
        });

        items.add(shell);

        DescriptionView reboot_options = new DescriptionView();
        reboot_options.setTitle(getString(R.string.reboot_options));
        reboot_options.setSummary(getString(R.string.reboot_options_summary));
        reboot_options.setOnItemClickListener(item -> {
            mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                    R.array.reboot_options), (dialog, i) -> {
                        switch (i) {
                            case 0:
                                new Execute().execute(Utils.prepareReboot() + " -p");
                                break;
                            case 1:
                                Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                                new Execute().execute(Utils.prepareReboot());
                                break;
                            case 2:
                                Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                                new Execute().execute(Utils.prepareReboot() + " recovery");
                                break;
                            case 3:
                                Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                                new Execute().execute(Utils.prepareReboot() + " bootloader");
                                break;
                        }
                    }).setOnDismissListener(dialog -> mOptionsDialog = null);
            mOptionsDialog.show();
        });

        items.add(reboot_options);
    }

    @SuppressLint("StaticFieldLeak")
    private class Execute extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressMessage(getString(R.string.executing) + ("..."));
        }

        @Override
        protected Void doInBackground(String... params) {
            Utils.sleep(1);
            RootUtils.runCommand(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressMessage();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadKernel() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressMessage(getString(R.string.downloading_update, KernelUpdater.getKernelName(requireActivity()) +
                        "-" + KernelUpdater.getLatestVersion(requireActivity())) + "...");
            }
            @Override
            protected Void doInBackground(Void... voids) {
                Utils.prepareInternalDataStorage();
                Utils.downloadFile(Utils.getInternalDataStorage() + "/Kernel.zip", KernelUpdater.getUrl(requireActivity()), getActivity());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideProgressMessage();
                if (KernelUpdater.getChecksum(requireActivity()).equals("Unavailable") || !KernelUpdater.getChecksum(requireActivity()).equals("Unavailable") &&
                        Utils.getChecksum(Utils.getInternalDataStorage() + "/Kernel.zip").contains(KernelUpdater.getChecksum(requireActivity()))) {
                    new Dialog(requireActivity())
                            .setMessage(getString(R.string.download_completed,
                                    KernelUpdater.getKernelName(requireActivity()) + "-" + KernelUpdater.getLatestVersion(requireActivity())))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(getString(R.string.flash), (dialog, id) -> {
                                SmartPack.flashingTask(new File(Utils.getInternalDataStorage() + "/Kernel.zip"), requireActivity());
                            })
                            .show();
                } else {
                    new Dialog(requireActivity())
                            .setMessage(getString(R.string.download_failed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                            })
                            .show();
                }
            }
        }.execute();
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();
        if (mPermissionDenied) {
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
            return;
        }

        Intent manualflash  = new Intent(Intent.ACTION_GET_CONTENT);
        manualflash.setType("application/*");
        startActivityForResult(manualflash, 0);
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            mPermissionDenied = true;
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
        }
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
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
            if (!mPath.endsWith(".zip")) {
                Utils.snackbar(getRootView(), getString(R.string.wrong_extension, ".zip"));
                return;
            }
            new Dialog(requireActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.flasher))
                    .setMessage(getString(R.string.sure_message, new File(mPath).getName()) + (SmartPack.fileSize(new File(mPath)) >= 100000000 ?
                            ("\n\n") + getString(R.string.file_size_limit, (SmartPack.fileSize(new File(mPath)) / 1000000)) : ""))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.flash), (dialogInterface, i) -> {
                        SmartPack.flashingTask(new File(mPath), requireActivity());
                    }).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
    }

}