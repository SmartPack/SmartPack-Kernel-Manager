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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Menu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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

import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 24, 2018
 */

public class SmartPackFragment extends RecyclerViewFragment {

    private Dialog mOptionsDialog;

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getWhiteColoredIcon(R.drawable.ic_flash, requireActivity());
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
                    SmartPackInit(items);
                    OtherOptionsInit(items);
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

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.smartpack),
                getString(R.string.flasher_summary)));
    }

    @SuppressLint("StringFormatInvalid")
    private void SmartPackInit(List<RecyclerViewItem> items) {
        TitleView smartpack = new TitleView();
        smartpack.setText(!KernelUpdater.getKernelName().equals("Unavailable") ? KernelUpdater.getKernelName() :
                getString(R.string.kernel_information));

        items.add(smartpack);

        DescriptionView kernelinfo = new DescriptionView();
        kernelinfo.setTitle(getString(R.string.kernel_current));
        kernelinfo.setSummary(RootUtils.runAndGetOutput("uname -r"));

        items.add(kernelinfo);

        GenericInputView updateChannel = new GenericInputView();
        updateChannel.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
        updateChannel.setTitle(getString(R.string.update_channel));
        updateChannel.setValue((!KernelUpdater.getKernelName().equals("Unavailable"))
                ? KernelUpdater.getUpdateChannel() : getString(R.string.update_channel_summary));
        updateChannel.setOnGenericValueListener((genericSelectView, value) -> {
            if (value.isEmpty()) {
                KernelUpdater.updateChannelInfo().delete();
                KernelUpdater.updateInfo().delete();
                reload();
                return;
            }
            if (value.contains("/blob/")) {
                value = value.replace("/blob/", "/raw/");
            }
            acquireUpdateInfo(value, getActivity());

        });
        if (!KernelUpdater.getKernelName().equals("Unavailable")) {
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
                                        KernelUpdater.updateChannelInfo().delete();
                                        KernelUpdater.updateInfo().delete();
                                        reload();
                                    })
                                    .show();
                            break;
                        case 1:
                            Intent shareChannel = new Intent();
                            shareChannel.setAction(Intent.ACTION_SEND);
                            shareChannel.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                            shareChannel.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_channel_message,
                                    Utils.readFile(KernelUpdater.updateChannelInfo().getAbsolutePath())) + (Utils
                                    .isFDroidFlavor(requireActivity()) ? " F-Droid: https://f-droid.org/packages/com.smartpack.kernelmanager" :
                                    " Google Play: https://play.google.com/store/apps/details?id=com.smartpack.kernelmanager.release"));
                            shareChannel.setType("text/plain");
                            Intent shareIntent = Intent.createChooser(shareChannel, null);
                            startActivity(shareIntent);
                            break;
                    }
                    return false;
                });
            });
        }

        items.add(updateChannel);

        if (KernelUpdater.getLatestVersion().equals("Unavailable")) {
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
                    Intent createUpdateChannel = new Intent(getActivity(), UpdateChannelActivity.class);
                    startActivity(createUpdateChannel);
                    return false;
                });
            });

            items.add(info);
        }

        if (!KernelUpdater.getLatestVersion().equals("Unavailable")) {
            DescriptionView latest = new DescriptionView();
            latest.setTitle(getString(R.string.kernel_latest));
            latest.setSummary(KernelUpdater.getLatestVersion());

            items.add(latest);
        }

        if (!KernelUpdater.getChangeLog().equals("Unavailable")) {
            DescriptionView changelogs = new DescriptionView();
            changelogs.setTitle(getString(R.string.change_logs));
            changelogs.setSummary(getString(R.string.change_logs_summary));
            changelogs.setOnItemClickListener(item -> {
                if (KernelUpdater.getChangeLog().contains("https://") ||
                        KernelUpdater.getChangeLog().contains("http://")) {
                    Utils.launchUrl(KernelUpdater.getChangeLog(), getActivity());
                } else {
                    new Dialog(requireActivity())
                            .setTitle(KernelUpdater.getKernelName() + " " + KernelUpdater.getLatestVersion())
                            .setMessage(KernelUpdater.getChangeLog())
                            .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> {
                            })
                            .show();
                }
            });

            items.add(changelogs);
        }

        if (!KernelUpdater.getSupport().equals("Unavailable")) {
            DescriptionView support = new DescriptionView();
            support.setTitle(getString(R.string.support));
            support.setSummary(getString(R.string.support_summary));
            support.setOnItemClickListener(item -> {
                if (KernelUpdater.getSupport().contains("https://") || KernelUpdater.getSupport().contains("http://")) {
                    Utils.launchUrl(KernelUpdater.getSupport(), getActivity());
                } else {
                    Utils.snackbar(getRootView(), getString(R.string.unknown_link));
                }
            });

            items.add(support);
        }

        if (!KernelUpdater.getUrl().equals("Unavailable")) {
            DescriptionView download = new DescriptionView();
            download.setTitle(getString(R.string.download));
            download.setSummary(getString(R.string.get_it_summary));
            download.setOnItemClickListener(item -> downloadKernel());

            items.add(download);
        }

        if (!KernelUpdater.getLatestVersion().equals("Unavailable")) {
            DescriptionView donations = new DescriptionView();
            donations.setTitle(getString(R.string.donations));
            donations.setSummary(getString(R.string.donations_summary));
            donations.setOnItemClickListener(item -> {
                if (KernelUpdater.getDonationLink().contains("https://") || KernelUpdater.getDonationLink().contains("http://")) {
                    Utils.launchUrl(KernelUpdater.getDonationLink(), getActivity());
                } else {
                    Utils.snackbar(getRootView(), getString(R.string.unknown_link));
                }

            });

            items.add(donations);
        }

        if (!KernelUpdater.getKernelName().equals("Unavailable")) {
            SwitchView update_check = new SwitchView();
            update_check.setSummary(getString(R.string.check_update));
            update_check.setChecked(Prefs.getBoolean("update_check", false, getActivity()));
            update_check.addOnSwitchListener((switchview, isChecked) -> {
                Prefs.saveBoolean("update_check", isChecked, getActivity());
                if (Prefs.getBoolean("update_check", true, getActivity())) {
                    Utils.snackbar(getRootView(), getString(R.string.update_check_message, !KernelUpdater.getKernelName().
                            equals("Unavailable") ? KernelUpdater.getKernelName() : "this"));
                }
            });

            items.add(update_check);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void OtherOptionsInit(List<RecyclerViewItem> items) {
        if (!Utils.isPackageInstalled("com.smartpack.busyboxinstaller")) {
            // Advertise Own App
            TitleView bb = new TitleView();
            bb.setText(getString(R.string.busybox_installer));

            items.add(bb);

            DescriptionView busybox = new DescriptionView();
            busybox.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
            busybox.setSummary(getString(R.string.busybox_installer_summary));
            busybox.setFullSpan(true);
            busybox.setOnItemClickListener(item -> Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.busyboxinstaller", getActivity()));
            items.add(busybox);
        }

        TitleView others = new TitleView();
        others.setText(getString(R.string.other_options));

        items.add(others);

        DescriptionView logcat = new DescriptionView();
        logcat.setTitle(getString(R.string.logcat));
        logcat.setSummary(getString(R.string.logcat_summary));
        logcat.setOnItemClickListener(item -> {
            SmartPack.prepareFolder(SmartPack.getLogFolderPath());
            Execute("logcat -d > " + SmartPack.getLogFolderPath() + "/logcat-" + Utils.getTimeStamp()  + " && " +
                    "logcat  -b radio -v time -d > " + SmartPack.getLogFolderPath() + "/radio-" + Utils.getTimeStamp()  + " && " +
                    "logcat -b events -v time -d > " + SmartPack.getLogFolderPath() + "/events-" + Utils.getTimeStamp());
        });
        items.add(logcat);

        if (Utils.existFile("/proc/last_kmsg")) {
            DescriptionView lastkmsg = new DescriptionView();
            lastkmsg.setTitle(getString(R.string.last_kmsg));
            lastkmsg.setSummary(getString(R.string.last_kmsg_summary));
            lastkmsg.setOnItemClickListener(item -> {
                SmartPack.prepareFolder(SmartPack.getLogFolderPath());
                Execute("cat /proc/last_kmsg > " + SmartPack.getLogFolderPath() + "/last_kmsg-" + Utils.getTimeStamp());
            });
            items.add(lastkmsg);
        }

        DescriptionView dmesg = new DescriptionView();
        dmesg.setTitle(getString(R.string.driver_message));
        dmesg.setSummary(getString(R.string.driver_message_summary));
        dmesg.setOnItemClickListener(item -> {
            SmartPack.prepareFolder(SmartPack.getLogFolderPath());
            Execute("dmesg > " + SmartPack.getLogFolderPath() + "/dmesg-" + Utils.getTimeStamp());
        });
        items.add(dmesg);

        if (Utils.existFile("/sys/fs/pstore/dmesg-ramoops*")) {
            DescriptionView dmesgRamoops = new DescriptionView();
            dmesgRamoops.setTitle(getString(R.string.driver_ramoops));
            dmesgRamoops.setSummary(getString(R.string.driver_ramoops_summary));
            dmesgRamoops.setOnItemClickListener(item -> {
                SmartPack.prepareFolder(SmartPack.getLogFolderPath());
                Execute("cat /sys/fs/pstore/dmesg-ramoops* > " + SmartPack.getLogFolderPath() + "/dmesg-ramoops-" + Utils.getTimeStamp());
            });
            items.add(dmesgRamoops);
        }

        if (Utils.existFile("/sys/fs/pstore/console-ramoops*")) {
            DescriptionView ramoops = new DescriptionView();
            ramoops.setTitle(getString(R.string.console_ramoops));
            ramoops.setSummary(getString(R.string.console_ramoops_summary));
            ramoops.setOnItemClickListener(item -> {
                SmartPack.prepareFolder(SmartPack.getLogFolderPath());
                Execute("cat /sys/fs/pstore/console-ramoops* > " + SmartPack.getLogFolderPath() + "/console-ramoops-" + Utils.getTimeStamp());
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
                                Execute(Utils.prepareReboot() + " -p");
                                break;
                            case 1:
                                Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                                Execute(Utils.prepareReboot());
                                break;
                            case 2:
                                Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                                Execute(Utils.prepareReboot() + " recovery");
                                break;
                            case 3:
                                Utils.snackbar(getRootView(), getString(R.string.rebooting_message));
                                Execute(Utils.prepareReboot() + " bootloader");
                                break;
                        }
                    }).setOnDismissListener(dialog -> mOptionsDialog = null);
            mOptionsDialog.show();
        });

        items.add(reboot_options);
    }

    private void Execute(String commands) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                showProgressMessage(getString(R.string.executing) + ("..."));
            }

            @Override
            public void doInBackground() {
                Utils.sleep(1);
                RootUtils.runCommand(commands);
            }

            @Override
            public void onPostExecute() {
                hideProgressMessage();
            }
        }.execute();
    }

    private void downloadKernel() {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                showProgressMessage(getString(R.string.downloading_update, KernelUpdater.getKernelName() +
                        "-" + KernelUpdater.getLatestVersion()) + "...");
            }

            @Override
            public void doInBackground() {
                Utils.prepareInternalDataStorage();
                Utils.downloadFile(requireActivity().getExternalFilesDir("kernelUpdater") + "/Kernel.zip", KernelUpdater.getUrl());
            }

            @Override
            public void onPostExecute() {
                hideProgressMessage();
                if (KernelUpdater.getChecksum().equals("Unavailable") || !KernelUpdater.getChecksum().equals("Unavailable") &&
                        Utils.getChecksum(requireActivity().getExternalFilesDir("kernelUpdater") + "/Kernel.zip")
                                .contains(KernelUpdater.getChecksum())) {
                    new Dialog(requireActivity())
                            .setMessage(getString(R.string.download_completed,
                                    KernelUpdater.getKernelName() + "-" + KernelUpdater.getLatestVersion()))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(getString(R.string.flash), (dialog, id) -> SmartPack.flashingTask(new File(requireActivity()
                                    .getExternalFilesDir("kernelUpdater"), "Kernel.zip"), requireActivity()))
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

    public void acquireUpdateInfo(String value, Context context) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                showProgressMessage(getString(R.string.acquiring) + ("..."));
                KernelUpdater.updateInfo().delete();
            }

            @Override
            public void doInBackground() {
                KernelUpdater.acquireUpdateInfo(value);
                KernelUpdater.saveUpdateChannel(value);
                Utils.sleep(1);
            }

            @Override
            public void onPostExecute() {
                hideProgressMessage();
                if (KernelUpdater.getJSONObject() != null) {
                    KernelUpdater.saveUpdateInfo(requireActivity());
                    reload();
                } else {
                    Utils.toast(R.string.update_channel_invalid, context);
                }
            }
        }.execute();
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();
        FilePicker filePicker = new FilePicker(pickFileForFlashing, requireActivity());
        filePicker.setExtension("zip");
        filePicker.setPath(Environment.getExternalStorageDirectory().toString());
        filePicker.setAccentColor(ViewUtils.getThemeAccentColor(requireContext()));
        filePicker.launch();
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    ActivityResultLauncher<Intent> pickFileForFlashing = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && FilePicker.getSelectedFile().exists()) {
                    File mSelectedFile = FilePicker.getSelectedFile();
                    new Dialog(requireActivity())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.flasher))
                            .setMessage(getString(R.string.sure_message, mSelectedFile.getName()) + (SmartPack.fileSize(mSelectedFile) >= 100000000 ?
                                    ("\n\n") + getString(R.string.file_size_limit, (SmartPack.fileSize(mSelectedFile) / 1000000)) : ""))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(getString(R.string.flash), (dialogInterface, i) -> SmartPack.flashingTask(
                                    mSelectedFile, requireActivity())
                            ).show();
                }
            }
    );

}