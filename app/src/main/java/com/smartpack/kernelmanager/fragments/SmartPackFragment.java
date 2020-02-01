/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.Manifest;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;
import com.smartpack.kernelmanager.recyclerview.GenericInputView;
import com.smartpack.kernelmanager.utils.KernelUpdater;
import com.smartpack.kernelmanager.utils.SmartPack;
import com.smartpack.kernelmanager.utils.UpdateChannel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_flash));
        DrawableCompat.setTint(drawable, Color.WHITE);
        return drawable;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public int getSpanCount() {
        int span = Utils.isTablet(getActivity()) ? Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 5 : 4 : Utils.getOrientation(getActivity()) ==
                Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
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

    private void SmartPackInit(List<RecyclerViewItem> items) {
        TitleView smartpack = new TitleView();
        smartpack.setText(!KernelUpdater.getKernelName().equals("Unavailable") ? KernelUpdater.getKernelName() :
                getString(R.string.kernel_information));

        items.add(smartpack);

        DescriptionView kernelinfo = new DescriptionView();
        kernelinfo.setTitle(getString(R.string.kernel_current));
        kernelinfo.setSummary(RootUtils.runCommand("uname -r"));

        items.add(kernelinfo);

        GenericInputView updateChannel = new GenericInputView();
        updateChannel.setTitle(getString(R.string.update_channel));
        updateChannel.setValue((!KernelUpdater.getKernelName().equals("Unavailable"))
                ? KernelUpdater.getUpdateChannel() : getString(R.string.update_channel_summary));
        updateChannel.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                if (!Utils.isNetworkAvailable(getActivity())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                if (value.isEmpty()) {
                    KernelUpdater.clearUpdateInfo();
                    Utils.toast(R.string.update_channel_empty, getActivity());
                    reload();
                    return;
                }
                KernelUpdater.acquireUpdateInfo(value, getActivity());
                reload();

            }
        });

        items.add(updateChannel);

        if (KernelUpdater.getLatestVersion().equals("Unavailable")) {
            CardView cardView = new CardView(getActivity());
            cardView.setFullSpan(true);
            cardView.setOnMenuListener(new CardView.OnMenuListener() {
                @Override
                public void onMenuReady(CardView cardView, androidx.appcompat.widget.PopupMenu popupMenu) {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.update_channel_create));

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, new UpdateChannel()).commit();
                            return false;
                        }
                    });
                }
            });

            DescriptionView info = new DescriptionView();
            info.setDrawable(getResources().getDrawable(R.drawable.ic_info));
            info.setTitle(getString(R.string.update_channel_info, Utils.getInternalDataStorage()));
            info.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (!Utils.isNetworkAvailable(getActivity())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    Utils.launchUrl("https://smartpack.github.io/kerneldownloads/", getActivity());
                }
            });

            cardView.addItem(info);
            items.add(cardView);
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
            changelogs.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (KernelUpdater.getChangeLog().contains("https://") ||
                            KernelUpdater.getChangeLog().contains("http://")) {
                        if (!Utils.isNetworkAvailable(getActivity())) {
                            Utils.toast(R.string.no_internet, getActivity());
                            return;
                        }
                        Utils.launchUrl(KernelUpdater.getChangeLog(), getActivity());
                    } else {
                        new Dialog(getActivity())
                                .setTitle(KernelUpdater.getKernelName() + " " + KernelUpdater.getLatestVersion())
                                .setMessage(KernelUpdater.getChangeLog())
                                .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> {
                                })
                                .show();
                    }
                }
            });

            items.add(changelogs);
        }

        if (!KernelUpdater.getSupport().equals("Unavailable")) {
            DescriptionView support = new DescriptionView();
            support.setTitle(getString(R.string.support));
            support.setSummary(getString(R.string.support_summary));
            support.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (!Utils.isNetworkAvailable(getActivity())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    if (KernelUpdater.getSupport().contains("https://") ||
                            KernelUpdater.getSupport().contains("http://")) {
                        Utils.launchUrl(KernelUpdater.getSupport(), getActivity());
                    } else {
                        Utils.toast(R.string.unknown_link, getActivity());
                    }
                }
            });

            items.add(support);
        }

        if (!KernelUpdater.getUrl().equals("Unavailable")) {
            DescriptionView download = new DescriptionView();
            download.setTitle(getString(R.string.download));
            download.setSummary(getString(R.string.get_it_summary));
            download.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (mPermissionDenied) {
                        Utils.toast(R.string.permission_denied_write_storage, getActivity());
                        return;
                    }
                    if (!Utils.isNetworkAvailable(getActivity())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    KernelUpdater.downloadKernel(getActivity());
                }
            });

            items.add(download);
        }

        if (!KernelUpdater.getLatestVersion().equals("Unavailable")) {
            DescriptionView donations = new DescriptionView();
            donations.setTitle(getString(R.string.donations));
            donations.setSummary(getString(R.string.donations_summary));
            donations.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (!Utils.isNetworkAvailable(getActivity())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    if (KernelUpdater.getDonationLink().contains("https://") ||
                                KernelUpdater.getDonationLink().contains("http://")) {
                        Utils.launchUrl(KernelUpdater.getSupport(), getActivity());
                        Utils.launchUrl(KernelUpdater.getDonationLink(), getActivity());
                    } else {
                        Utils.toast(getString(R.string.unknown_link), getActivity());
                    }

                }
            });

            items.add(donations);
        }

        if (!KernelUpdater.getKernelName().equals("Unavailable")) {
            SwitchView update_check = new SwitchView();
            update_check.setSummary(getString(R.string.check_update));
            update_check.setChecked(Prefs.getBoolean("update_check", false, getActivity()));
            update_check.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchview, boolean isChecked) {
                    Prefs.saveBoolean("update_check", isChecked, getActivity());
                    if (Prefs.getBoolean("update_check", true, getActivity())) {
                        Utils.toast(getString(R.string.update_check_message, !KernelUpdater.getKernelName().
                                equals("Unavailable") ? KernelUpdater.getKernelName() : "this"), getActivity());
                    }
                }
            });

            items.add(update_check);
        }
    }

    private void OtherOptionsInit(List<RecyclerViewItem> items) {
        TitleView others = new TitleView();
        others.setText(getString(R.string.other_options));

        items.add(others);

        DescriptionView logcat = new DescriptionView();
        logcat.setTitle(getString(R.string.logcat));
        logcat.setSummary(getString(R.string.logcat_summary));
        logcat.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                SmartPack.prepareLogFolder();
                new Execute().execute("logcat -d > " + logFolder + "/logcat");
                new Execute().execute("logcat  -b radio -v time -d > " + logFolder + "/radio");
                new Execute().execute("logcat -b events -v time -d > " + logFolder + "/events");
            }
        });
        items.add(logcat);

        if (Utils.existFile("/proc/last_kmsg")) {
            DescriptionView lastkmsg = new DescriptionView();
            lastkmsg.setTitle(getString(R.string.last_kmsg));
            lastkmsg.setSummary(getString(R.string.last_kmsg_summary));
            lastkmsg.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (mPermissionDenied) {
                        Utils.toast(R.string.permission_denied_write_storage, getActivity());
                        return;
                    }
                    SmartPack.prepareLogFolder();
                    new Execute().execute("cat /proc/last_kmsg > " + logFolder + "/last_kmsg");
                }
            });
            items.add(lastkmsg);
        }

        DescriptionView dmesg = new DescriptionView();
        dmesg.setTitle(getString(R.string.driver_message));
        dmesg.setSummary(getString(R.string.driver_message_summary));
        dmesg.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (mPermissionDenied) {
                    Utils.toast(R.string.permission_denied_write_storage, getActivity());
                    return;
                }
                SmartPack.prepareLogFolder();
                new Execute().execute("dmesg > " + logFolder + "/dmesg");
            }
        });
        items.add(dmesg);

        if (Utils.existFile("/sys/fs/pstore/dmesg-ramoops*")) {
            DescriptionView dmesgRamoops = new DescriptionView();
            dmesgRamoops.setTitle(getString(R.string.driver_ramoops));
            dmesgRamoops.setSummary(getString(R.string.driver_ramoops_summary));
            dmesgRamoops.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (mPermissionDenied) {
                        Utils.toast(R.string.permission_denied_write_storage, getActivity());
                        return;
                    }
                    SmartPack.prepareLogFolder();
                    new Execute().execute("cat /sys/fs/pstore/dmesg-ramoops* > " + logFolder + "/dmesg-ramoops");
                }
            });
            items.add(dmesgRamoops);
        }

        if (Utils.existFile("/sys/fs/pstore/console-ramoops*")) {
            DescriptionView ramoops = new DescriptionView();
            ramoops.setTitle(getString(R.string.console_ramoops));
            ramoops.setSummary(getString(R.string.console_ramoops_summary));
            ramoops.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (mPermissionDenied) {
                        Utils.toast(R.string.permission_denied_write_storage, getActivity());
                        return;
                    }
                    SmartPack.prepareLogFolder();
                    new Execute().execute("cat /sys/fs/pstore/console-ramoops* > " + logFolder + "/console-ramoops");
                }
            });
            items.add(ramoops);
        }

        GenericInputView shell = new GenericInputView();
        shell.setTitle(getString(R.string.shell));
        shell.setValue(getString(R.string.shell_summary));
        shell.setFullSpan(true);
        shell.setOnGenericValueListener(new GenericInputView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericInputView genericSelectView, String value) {
                runCommand(value);
            }
        });

        items.add(shell);

        DescriptionView reboot_options = new DescriptionView();
        reboot_options.setTitle(getString(R.string.reboot_options));
        reboot_options.setSummary(getString(R.string.reboot_options_summary));
        reboot_options.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                mOptionsDialog = new Dialog(getActivity()).setItems(getResources().getStringArray(
                        R.array.reboot_options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i) {
                            case 0:
                                new Dialog(getActivity())
                                        .setMessage(getString(R.string.sure_question))
                                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, ii) -> {
                                        })
                                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                                            new Execute().execute(Utils.existFile("/system/bin/svc") ? "svc power shutdown"
                                                    : Utils.prepareReboot() + " -p");
                                        })
                                        .show();
                                break;
                            case 1:
                                new Dialog(getActivity())
                                        .setMessage(getString(R.string.sure_question))
                                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, ii) -> {
                                        })
                                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                                            new Execute().execute(Utils.existFile("/system/bin/svc") ? "svc power reboot"
                                                    : Utils.prepareReboot());
                                        })
                                        .show();
                                break;
                            case 2:
                                new Dialog(getActivity())
                                        .setMessage(getString(R.string.sure_question))
                                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, ii) -> {
                                        })
                                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                                            new Execute().execute(Utils.existFile("/system/bin/svc") ? "svc power reboot"
                                                    : Utils.prepareReboot() + " recovery");
                                        })
                                        .show();
                                break;
                            case 3:
                                new Dialog(getActivity())
                                        .setMessage(getString(R.string.sure_question))
                                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, ii) -> {
                                        })
                                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                                            new Execute().execute(Utils.existFile("/system/bin/svc") ? "svc power reboot"
                                                    : Utils.prepareReboot() + " bootloader");
                                        })
                                        .show();
                                break;
                        }
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mOptionsDialog = null;
                    }
                });
                mOptionsDialog.show();
            }
        });

        items.add(reboot_options);

        DescriptionView reset = new DescriptionView();
        reset.setTitle(getString(R.string.reset_settings));
        reset.setSummary(getString(R.string.reset_settings_summary));
        reset.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                new Dialog(getActivity())
                        .setMessage(getString(R.string.reset_settings_message))
                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                        })
                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                            new Dialog(getActivity())
                                    .setMessage(getString(R.string.reboot_message))
                                    .setNegativeButton(getString(R.string.reboot_later), (dialogInterface, i) -> {
                                        new Execute().execute("rm -rf /data/data/com.smartpack.kernelmanager/");
                                        new Execute().execute("pm clear com.smartpack.kernelmanager && " +
                                                "am start -n com.smartpack.kernelmanager/com.grarak.kerneladiutor.activities.MainActivity");
                                    })
                                    .setPositiveButton(getString(R.string.reboot_now), (dialog2, id2) -> {
                                        new Execute().execute("rm -rf /data/data/com.smartpack.kernelmanager/");
                                        new Execute().execute(Utils.existFile("/system/bin/svc") ? "svc power reboot"
                                                : Utils.prepareReboot());
                                    })
                                    .show();
                        })
                        .show();
            }
        });
        items.add(reset);
    }

    private class Execute extends AsyncTask<String, Void, Void> {
        private ProgressDialog mExecuteDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mExecuteDialog = new ProgressDialog(getActivity());
            mExecuteDialog.setMessage(getString(R.string.executing) + ("..."));
            mExecuteDialog.setCancelable(false);
            mExecuteDialog.show();
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
            mExecuteDialog.dismiss();
        }
    }

    private void runCommand(final String value) {
        new AsyncTask<Void, Void, String>() {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.executing) + ("..."));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                Utils.sleep(1);
                return RootUtils.runCommand(value);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                new Dialog(getActivity())
                        .setTitle(value)
                        .setMessage(s != null && !s.isEmpty()? s : "")
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                        })
                        .show();
            }
        }.execute();
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();
        if (mPermissionDenied) {
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            return;
        }

        Intent manualflash  = new Intent(Intent.ACTION_GET_CONTENT);
        manualflash.setType("application/zip");
        startActivityForResult(manualflash, 0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());
            mPath = Utils.getFilePath(file);
            if (Utils.isDocumentsUI(uri)) {
                ViewUtils.dialogDocumentsUI(getActivity());
                return;
            }
            if (!Utils.getExtension(mPath).equals("zip")) {
                Utils.toast(getString(R.string.file_selection_error), getActivity());
                return;
            }
            if (SmartPack.fileSize(new File(mPath)) >= 100000000) {
                Utils.toast(getString(R.string.file_size_limit, (SmartPack.fileSize(new File(mPath)) / 1000000)), getActivity());
            }
            Dialog manualFlash = new Dialog(getActivity());
            manualFlash.setIcon(R.mipmap.ic_launcher);
            manualFlash.setTitle(getString(R.string.flasher));
            manualFlash.setMessage(getString(R.string.sure_message, file.getName().replace("primary:", "")) + ("\n\n") +
                    getString(R.string.warning) + (" ") + getString(R.string.flasher_warning));
            manualFlash.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            manualFlash.setPositiveButton(getString(R.string.flash), (dialogInterface, i) -> {
                SmartPack.flashingTask(new File(mPath), getActivity());
            });
            manualFlash.show();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        // Initialize kernel update check - Once in a day
        if (Utils.isNetworkAvailable(getActivity()) && Prefs.getBoolean("update_check", true, getActivity())
                && !KernelUpdater.getUpdateChannel().equals("Unavailable") && KernelUpdater.lastModified() +
                89280000L < System.currentTimeMillis()) {
            KernelUpdater.updateInfo(Utils.readFile(Utils.getInternalDataStorage() + "/updatechannel"));
        }
    }
}