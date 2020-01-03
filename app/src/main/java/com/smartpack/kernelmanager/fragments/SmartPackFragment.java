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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.Manifest;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import com.smartpack.kernelmanager.utils.SmartPack;

import java.io.File;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 24, 2018
 */

public class SmartPackFragment extends RecyclerViewFragment {

    private boolean mPermissionDenied;

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
    protected void addItems(List<RecyclerViewItem> items) {
        SmartPackInit(items);
	requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.smartpack),
                getString(R.string.flasher_summary)));
    }

    private void SmartPackInit(List<RecyclerViewItem> items) {
        CardView smartpack = new CardView(getActivity());
        smartpack.setTitle(getString(R.string.smartpack));

        DescriptionView reset = new DescriptionView();
        reset.setTitle(getString(R.string.reset_settings));
        reset.setSummary(getString(R.string.reset_settings_summary));
        reset.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog resetsettings = new Dialog(getActivity());
                resetsettings.setIcon(R.mipmap.ic_launcher);
                resetsettings.setTitle(getString(R.string.warning));
                resetsettings.setMessage(getString(R.string.reset_settings_message));
                resetsettings.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                resetsettings.setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                    Dialog reboot = new Dialog(getActivity());
                    reboot.setIcon(R.mipmap.ic_launcher);
                    reboot.setTitle(getString(R.string.reboot));
                    reboot.setMessage(getString(R.string.reboot_message));
                    reboot.setNeutralButton(getString(R.string.reboot_later), (dialogInterface, i) -> {
                        new Execute().execute("rm -rf /data/data/com.smartpack.kernelmanager/");
                        new Execute().execute("pm clear com.smartpack.kernelmanager && am start -n com.smartpack.kernelmanager/com.grarak.kerneladiutor.activities.MainActivity");
                    });
                    reboot.setPositiveButton(getString(R.string.reboot_now), (dialog2, id2) -> {
                        new Execute().execute("rm -rf /data/data/com.smartpack.kernelmanager/");
                        new Execute().execute(Utils.prepareReboot());
                    });
                    reboot.show();
                });
                resetsettings.show();
            }
        });
        smartpack.addItem(reset);

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
        smartpack.addItem(logcat);

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
            smartpack.addItem(lastkmsg);
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
        smartpack.addItem(dmesg);

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
            smartpack.addItem(dmesgRamoops);
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
            smartpack.addItem(ramoops);
        }

        // Show wipe (Cache/Data) functions only if we recognize recovery...
        if (SmartPack.hasRecovery()) {
            DescriptionView wipe_cache = new DescriptionView();
            wipe_cache.setTitle(getString(R.string.wipe_cache));
            wipe_cache.setSummary(getString(R.string.wipe_cache_summary));
            wipe_cache.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    Dialog wipecache = new Dialog(getActivity());
                    wipecache.setIcon(R.mipmap.ic_launcher);
                    wipecache.setTitle(getString(R.string.sure_question));
                    wipecache.setMessage(getString(R.string.wipe_cache_message));
                    wipecache.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    });
                    wipecache.setPositiveButton(getString(R.string.wipe_cache), (dialog1, id1) -> {
                        new Execute().execute("echo --wipe_cache > /cache/recovery/command");
                        new Execute().execute(Utils.prepareReboot() + " recovery");
                    });
                    wipecache.show();
                }
            });
            smartpack.addItem(wipe_cache);

            DescriptionView wipe_data = new DescriptionView();
            wipe_data.setTitle(getString(R.string.wipe_data));
            wipe_data.setSummary(getString(R.string.wipe_data_summary));
            wipe_data.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    Dialog wipedata = new Dialog(getActivity());
                    wipedata.setIcon(R.mipmap.ic_launcher);
                    wipedata.setTitle(getString(R.string.sure_question));
                    wipedata.setMessage(getString(R.string.wipe_data_message));
                    wipedata.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    });
                    wipedata.setPositiveButton(getString(R.string.wipe_data), (dialog1, id1) -> {
                        new Execute().execute("echo --wipe_data > /cache/recovery/command");
                        new Execute().execute(Utils.prepareReboot() + " recovery");
                    });
                    wipedata.show();
                }
            });
            smartpack.addItem(wipe_data);
        }

        DescriptionView turnoff = new DescriptionView();
        turnoff.setTitle(getString(R.string.turn_off));
        turnoff.setSummary(getString(R.string.turn_off_summary));
        turnoff.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog turnoff = new Dialog(getActivity());
                turnoff.setIcon(R.mipmap.ic_launcher);
                turnoff.setTitle(getString(R.string.sure_question));
                turnoff.setMessage(getString(R.string.turn_off_message));
                turnoff.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                turnoff.setPositiveButton(getString(R.string.turn_off), (dialog1, id1) -> {
                    new Execute().execute(Utils.prepareReboot() + " -p");
                });
                turnoff.show();
            }
        });
        smartpack.addItem(turnoff);

        DescriptionView reboot = new DescriptionView();
        reboot.setTitle(getString(R.string.reboot));
        reboot.setSummary(getString(R.string.reboot_summary));
        reboot.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog reboot = new Dialog(getActivity());
                reboot.setIcon(R.mipmap.ic_launcher);
                reboot.setTitle(getString(R.string.sure_question));
                reboot.setMessage(getString(R.string.normal_reboot_message));
                reboot.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                reboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                    new Execute().execute(Utils.prepareReboot());
                });
                reboot.show();
            }
        });
        smartpack.addItem(reboot);

        DescriptionView recoveryreboot = new DescriptionView();
        recoveryreboot.setTitle(getString(R.string.reboot_recovery));
        recoveryreboot.setSummary(getString(R.string.reboot_recovery_summary));
        recoveryreboot.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog recoveryreboot = new Dialog(getActivity());
                recoveryreboot.setIcon(R.mipmap.ic_launcher);
                recoveryreboot.setTitle(getString(R.string.sure_question));
                recoveryreboot.setMessage(getString(R.string.recovery_message));
                recoveryreboot.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                recoveryreboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                    new Execute().execute(Utils.prepareReboot() + " recovery");
                });
                recoveryreboot.show();
            }
        });
        smartpack.addItem(recoveryreboot);

        DescriptionView bootloaderreboot = new DescriptionView();
        bootloaderreboot.setTitle(getString(R.string.reboot_bootloader));
        bootloaderreboot.setSummary(getString(R.string.reboot_bootloader_summary));
        bootloaderreboot.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog bootloaderreboot = new Dialog(getActivity());
                bootloaderreboot.setIcon(R.mipmap.ic_launcher);
                bootloaderreboot.setTitle(getString(R.string.sure_question));
                bootloaderreboot.setMessage(getString(R.string.bootloader_message));
                bootloaderreboot.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                bootloaderreboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                    new Execute().execute(Utils.prepareReboot() + " bootloader");
                });
                bootloaderreboot.show();
            }
        });
        smartpack.addItem(bootloaderreboot);

        items.add(smartpack);
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
            RootUtils.runCommand(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mExecuteDialog.dismiss();
        }
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

    private void manualFlash(final File file) {
        new AsyncTask<Void, Void, String>() {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.flashing) + (" ") + file.getName());
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                SmartPack.prepareManualFlash(file);
                return SmartPack.manualFlash(file);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (s != null && !s.isEmpty()) {
                    new Dialog(getActivity())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.flash_log))
                            .setMessage(s)
                            .setCancelable(false)
                            .setNeutralButton(getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(getString(R.string.reboot), (dialog, id) -> {
                                new Execute().execute(Utils.prepareReboot());
                            })
                            .show();
                }
            }
        }.execute();
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
            SmartPack.cleanLogs();
            RootUtils.runCommand("echo '" + mPath + "' > " + Utils.getInternalDataStorage() + "/last_flash.txt");
            if (!Utils.getExtension(file.getName()).equals("zip")) {
                Utils.toast(getString(R.string.file_selection_error), getActivity());
                return;
            }
            if (SmartPack.fileSize(new File(mPath)) >= 100000000) {
                Utils.toast(getString(R.string.file_size_limit, (SmartPack.fileSize(new File(mPath)) / 1000000)), getActivity());
            }
            Dialog manualFlash = new Dialog(getActivity());
            manualFlash.setIcon(R.mipmap.ic_launcher);
            manualFlash.setTitle(getString(R.string.flasher));
            manualFlash.setMessage(getString(R.string.sure_message, file.getName()) + ("\n\n") +
                    getString(R.string.warning) + (" ") + getString(R.string.flasher_warning));
            manualFlash.setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            manualFlash.setPositiveButton(getString(R.string.flash), (dialogInterface, i) -> {
                manualFlash(new File(mPath));
            });
            manualFlash.show();
        }
    }
}