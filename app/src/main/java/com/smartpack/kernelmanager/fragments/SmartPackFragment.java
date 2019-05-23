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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.FilePickerActivity;
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
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 24, 2018
 */

public class SmartPackFragment extends RecyclerViewFragment {
    private boolean mPermissionDenied;

    private Dialog mSelectionMenu;
    private Dialog mFlashingDialog;
    private Dialog mFlashDialog;

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
    }

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.smartpack),
                getString(R.string.flasher_summary)));
    }

    private void SmartPackInit(List<RecyclerViewItem> items) {
        CardView smartpack = new CardView(getActivity());
        smartpack.setTitle(getString(R.string.smartpack_kernel));

        String KernelZip = Environment.getExternalStorageDirectory().toString() + "/Download/SmartPack-Kernel.zip";
        String RecoveryFlashCommand = "echo --update_package=/" + KernelZip + " > /cache/recovery/command";
        String RebootCommand = "am broadcast android.intent.action.ACTION_SHUTDOWN && sync && echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3 && reboot";

        if (SmartPack.supported() && SmartPack.hasSmartPackInstalled()) {
            DescriptionView currentspversion = new DescriptionView();
            currentspversion.setTitle(("Current ") + getString(R.string.version));
            if (SmartPack.hasSmartPackVersion()) {
                currentspversion.setSummary(SmartPack.getSmartPackVersion());
            } else {
                currentspversion.setSummary(RootUtils.runCommand("uname -r"));
            }
            smartpack.addItem(currentspversion);
        }

        if (SmartPack.supported() && Build.VERSION.SDK_INT >= 27) {
            DescriptionView spversion = new DescriptionView();
            spversion.setTitle(("Latest ") + getString(R.string.version));
            if ((SmartPack.hasSmartPackInstalled()) && (SmartPack.SmartPackRelease())) {
                if (SmartPack.getSmartPackVersionNumber() < SmartPack.getlatestSmartPackVersionNumber()) {
                    spversion.setSummary(("~ New Update (") + SmartPack.getlatestSmartPackVersion() + (") Available ~") + getString(R.string.get_it));
                } else {
                    spversion.setSummary(SmartPack.getlatestSmartPackVersion() + ("\n~ ") + getString(R.string.up_to_date_message) + (" ~") + getString(R.string.recheck));
                }
            } else if (SmartPack.SmartPackRelease()) {
                spversion.setSummary(("~ SmartPack-Kernel ") + SmartPack.getlatestSmartPackVersion() + (" is available ~") + getString(R.string.get_it));
            } else {
                spversion.setSummary(getString(R.string.latest_version_check));
            }
            spversion.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    // Delete latest kernel version information
                    if (SmartPack.haslatestSmartPackVersion()) {
                        SmartPack.deleteVersionInfo();
                    }
                    // Get latest kernel version information
                    SmartPack.getVersionInfo();
                    if ((SmartPack.hasSmartPackInstalled()) && (SmartPack.SmartPackRelease())) {
                        if (SmartPack.getSmartPackVersionNumber() < SmartPack.getlatestSmartPackVersionNumber()) {
                            spversion.setSummary(("~ New Update (") + SmartPack.getlatestSmartPackVersion() + (") Available ~") + getString(R.string.get_it));
                        } else {
                            spversion.setSummary(SmartPack.getlatestSmartPackVersion() + ("\n~ ") + getString(R.string.up_to_date_message) + (" ~") + getString(R.string.recheck));
                            // Show an alert message if the device is already on latest SmartPack-Kernel
                            Dialog uptodate = new Dialog(getActivity());
                            uptodate.setTitle(getString(R.string.appupdater_update_not_available));
                            uptodate.setMessage(("\n") + getString(R.string.up_to_date_message));
                            uptodate.setNegativeButton(getString(R.string.ok), (dialogInterface, i) -> {
                            });
                            uptodate.show();
                        }
                    } else if (SmartPack.SmartPackRelease()) {
                        spversion.setSummary(("~ SmartPack-Kernel ") + SmartPack.getlatestSmartPackVersion() + (" is available ~") + getString(R.string.get_it));
                    } else {
                        spversion.setSummary(getString(R.string.update_check_failed) + getString(R.string.recheck));
                    }
                    // Initialize SmartPack-Kernel auto install/update
                    if (SmartPack.hasSmartPackInstalled() && SmartPack.SmartPackRelease() && SmartPack.getSmartPackVersionNumber() < SmartPack.getlatestSmartPackVersionNumber()  || SmartPack.SmartPackRelease() && (!(SmartPack.hasSmartPackInstalled()))) {
                        // Check and delete an old version of the kernel from the download folder, if any...
                        if (SmartPack.isSmartPackDownloaded()) {
                            SmartPack.deleteLatestKernel();
                        }
                        // Show an alert dialogue and let the user know the process...
                        Dialog downloads = new Dialog(getActivity());
                        downloads.setTitle(("SmartPack-Kernel ") + SmartPack.getlatestSmartPackVersion() + (" is available..."));
                        downloads.setMessage(getString(R.string.downloads_message));
                        downloads.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                        });
                        downloads.setPositiveButton(getString(R.string.download), (dialog1, id1) -> {
                            // Initiate device specific downloads...
                            SmartPack.getLatestKernel();
                            // Check and create, if necessary, internal storage folder
                            SmartPack.makeInternalStorageFolder();
                            // Extract the above downloaded kernel for auto-flash...
                            if (!SmartPack.hasFlashFolder()) {
                                SmartPack.makeFlashFolder();
                            } else {
                                SmartPack.cleanFlashFolder();
                            }
                            SmartPack.extractLatestKernel();
                            // Proceed only if the download was successful...
                            if (SmartPack.isSmartPackDownloaded()) {
                                // Proceed to auto-flash if the extraction was successful...
                                if (SmartPack.isZIPFileExtracted()) {
                                    Dialog flash = new Dialog(getActivity());
                                    flash.setTitle(getString(R.string.autoflash));
                                    flash.setMessage(getString(R.string.autoflash_message));
                                    flash.setNegativeButton(getString(R.string.manual_flashing), (dialogInterface, i) -> {
                                        SmartPack.cleanFlashFolder();
                                    });
                                    flash.setPositiveButton(getString(R.string.install_now), (dialog2, id2) -> {
                                        /*
                                         * Flashing recovery zip without rebooting to custom recovery
                                         * Credits to osm0sis @ xda-developers.com
                                         */
                                        String flashFolder = Utils.getInternalDataStorage() + "/flash";
                                        String RECOVERY_API = "3";
                                        new Execute().execute("cd " + flashFolder);
                                        new Execute().execute("mount -o remount,rw / && mkdir /tmp");
                                        new Execute().execute("mke2fs -F tmp.ext4 250000 && mount -o loop tmp.ext4 /tmp/");
                                        new Execute().execute("sh META-INF/com/google/android/update-binary " + RECOVERY_API + " 1 " + KernelZip);
                                        new Execute().execute("cd ../ && rm -r flash/");
                                        new Execute().execute(RebootCommand);
                                    });
                                    flash.show();
                                    // Otherwise, show a flash via recovery message, only if we recognize recovery...
                                } else if (SmartPack.hasRecovery()) {
                                    Dialog flash = new Dialog(getActivity());
                                    flash.setTitle(getString(R.string.autoflash));
                                    flash.setMessage(getString(R.string.flash_message));
                                    flash.setNegativeButton(getString(R.string.manual_flashing), (dialogInterface, i) -> {
                                        SmartPack.cleanFlashFolder();
                                    });
                                    flash.setPositiveButton(getString(R.string.install_now), (dialog2, id2) -> {
                                        new Execute().execute(RecoveryFlashCommand);
                                        SmartPack.cleanFlashFolder();
                                        new Execute().execute(RebootCommand + " recovery");
                                    });
                                    flash.show();
                                    // If everything failed, show an "Auto-flash not possible" message...
                                } else {
                                    Dialog noflash = new Dialog(getActivity());
                                    noflash.setTitle(getString(R.string.warning));
                                    noflash.setMessage(getString(R.string.no_flash_message));
                                    noflash.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                                    });
                                    noflash.setPositiveButton(getString(R.string.reboot), (dialog2, id2) -> {
                                        new Execute().execute(RebootCommand + " recovery");
                                    });
                                    noflash.show();
                                }
                                // Shown "Download failed" message...
                            } else {
                                Dialog downloadfailed = new Dialog(getActivity());
                                downloadfailed.setTitle(getString(R.string.appupdater_download_filed));
                                downloadfailed.setMessage(getString(R.string.download_failed_message));
                                downloadfailed.setPositiveButton(getString(R.string.exit), (dialog2, id2) -> {
                                });
                                downloadfailed.show();
                            }
                        });
                        downloads.show();
                    }}
            });
            smartpack.addItem(spversion);
        }

        if (SmartPack.supported() && SmartPack.hasSmartPackInstalled()) {
            DescriptionView xdapage = new DescriptionView();
            xdapage.setTitle(getString(R.string.support));
            xdapage.setSummary(getString(R.string.support_summary));
            xdapage.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (SmartPack.isOnePlusmsm8998()) {
                        Utils.launchUrl("https://forum.xda-developers.com/oneplus-5t/development/kernel-smartpack-linaro-gcc-7-x-oxygen-t3832458", getActivity());
                    }
                }
            });
            smartpack.addItem(xdapage);

            DescriptionView spsource = new DescriptionView();
            spsource.setTitle(getString(R.string.source_code));
            spsource.setSummary(getString(R.string.source_code_summary));
            spsource.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (SmartPack.isOnePlusmsm8998()) {
                        Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T", getActivity());
                    }
                }
            });
            smartpack.addItem(spsource);

            DescriptionView changelogsp = new DescriptionView();
            changelogsp.setTitle(getString(R.string.change_logs));
            changelogsp.setSummary(getString(R.string.change_logs_summary));
            changelogsp.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 27)) {
                        Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_OP5T/Oreo/change-logs.md", getActivity());
                    } else if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 28)) {
                        Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_OP5T/Pie/change-logs.md", getActivity());
                    }
                }
            });
            smartpack.addItem(changelogsp);
        }

        if (SmartPack.supported()) {
            DescriptionView website = new DescriptionView();
            website.setTitle(getString(R.string.website));
            website.setSummary(getString(R.string.website_summary));
            website.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (SmartPack.isOnePlusmsm8998()) {
                        Utils.launchUrl("https://smartpack.github.io/op5t/", getActivity());
                    } else {
                        Utils.launchUrl("https://smartpack.github.io/", getActivity());
                    }
                }
            });
            smartpack.addItem(website);
        }

        if (smartpack.size() > 0) {
            items.add(smartpack);
        }

        CardView advanced = new CardView(getActivity());
        advanced.setTitle(getString(R.string.advance_options));

        DescriptionView reset = new DescriptionView();
        reset.setTitle(getString(R.string.reset_settings));
        reset.setSummary(getString(R.string.reset_settings_summary));
        reset.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog resetsettings = new Dialog(getActivity());
                resetsettings.setTitle(getString(R.string.warning));
                resetsettings.setMessage(getString(R.string.reset_settings_message));
                resetsettings.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                resetsettings.setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                    Dialog reboot = new Dialog(getActivity());
                    reboot.setTitle(getString(R.string.reboot));
                    reboot.setMessage(getString(R.string.reboot_message));
                    reboot.setNegativeButton(getString(R.string.reboot_later), (dialogInterface, i) -> {
                        new Execute().execute("rm -rf /data/data/com.smartpack.kernelmanager/");
                        new Execute().execute("pm clear com.smartpack.kernelmanager && am start -n com.smartpack.kernelmanager/com.grarak.kerneladiutor.activities.MainActivity");
                    });
                    reboot.setPositiveButton(getString(R.string.reboot_now), (dialog2, id2) -> {
                        new Execute().execute("rm -rf /data/data/com.smartpack.kernelmanager/");
                        new Execute().execute(RebootCommand);
                    });
                    reboot.show();
                });
                resetsettings.show();
            }
        });
        advanced.addItem(reset);

        DescriptionView logcat = new DescriptionView();
        logcat.setTitle(getString(R.string.logcat));
        logcat.setSummary(getString(R.string.logcat_summary));
        logcat.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                new Execute().execute("logcat -d > /sdcard/logcat");
                new Execute().execute("logcat  -b radio -v time -d > /sdcard/radio");
                new Execute().execute("logcat -b events -v time -d > /sdcard/events");
            }
        });
        advanced.addItem(logcat);

        if (Utils.existFile("/proc/last_kmsg")) {
            DescriptionView lastkmsg = new DescriptionView();
            lastkmsg.setTitle(getString(R.string.last_kmsg));
            lastkmsg.setSummary(getString(R.string.last_kmsg_summary));
            lastkmsg.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    new Execute().execute("cat /proc/last_kmsg > /sdcard/last_kmsg");
                }
            });
            advanced.addItem(lastkmsg);
        }

        DescriptionView dmesg = new DescriptionView();
        dmesg.setTitle(getString(R.string.driver_message));
        dmesg.setSummary(getString(R.string.driver_message_summary));
        dmesg.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                new Execute().execute("dmesg > /sdcard/dmesg");
            }
        });
        advanced.addItem(dmesg);

        if (Utils.existFile("/sys/fs/pstore/dmesg-ramoops*")) {
            DescriptionView dmesgRamoops = new DescriptionView();
            dmesgRamoops.setTitle(getString(R.string.driver_ramoops));
            dmesgRamoops.setSummary(getString(R.string.driver_ramoops_summary));
            dmesgRamoops.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    new Execute().execute("cat /sys/fs/pstore/dmesg-ramoops* > /sdcard/dmesg-ramoops");
                }
            });
            advanced.addItem(dmesgRamoops);
        }

        if (Utils.existFile("/sys/fs/pstore/console-ramoops*")) {
            DescriptionView ramoops = new DescriptionView();
            ramoops.setTitle(getString(R.string.console_ramoops));
            ramoops.setSummary(getString(R.string.console_ramoops_summary));
            ramoops.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    new Execute().execute("cat /sys/fs/pstore/console-ramoops* > /sdcard/console-ramoops");
                }
            });
            advanced.addItem(ramoops);
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
                    wipecache.setTitle(getString(R.string.sure_question));
                    wipecache.setMessage(getString(R.string.wipe_cache_message));
                    wipecache.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    });
                    wipecache.setPositiveButton(getString(R.string.wipe_cache), (dialog1, id1) -> {
                        new Execute().execute("echo --wipe_cache > /cache/recovery/command");
                        new Execute().execute(RebootCommand + " recovery");
                    });
                    wipecache.show();
                }
            });
            advanced.addItem(wipe_cache);

            DescriptionView wipe_data = new DescriptionView();
            wipe_data.setTitle(getString(R.string.wipe_data));
            wipe_data.setSummary(getString(R.string.wipe_data_summary));
            wipe_data.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    Dialog wipedata = new Dialog(getActivity());
                    wipedata.setTitle(getString(R.string.sure_question));
                    wipedata.setMessage(getString(R.string.wipe_data_message));
                    wipedata.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    });
                    wipedata.setPositiveButton(getString(R.string.wipe_data), (dialog1, id1) -> {
                        new Execute().execute("echo --wipe_data > /cache/recovery/command");
                        new Execute().execute(RebootCommand + " recovery");
                    });
                    wipedata.show();
                }
            });
            advanced.addItem(wipe_data);
        }

        DescriptionView recoveryreboot = new DescriptionView();
        recoveryreboot.setTitle(getString(R.string.reboot_recovery));
        recoveryreboot.setSummary(getString(R.string.reboot_recovery_summary));
        recoveryreboot.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog recoveryreboot = new Dialog(getActivity());
                recoveryreboot.setTitle(getString(R.string.sure_question));
                recoveryreboot.setMessage(getString(R.string.recovery_message));
                recoveryreboot.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                recoveryreboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                    new Execute().execute(RebootCommand + " recovery");
                });
                recoveryreboot.show();
            }
        });
        advanced.addItem(recoveryreboot);

        DescriptionView bootloaderreboot = new DescriptionView();
        bootloaderreboot.setTitle(getString(R.string.reboot_bootloader));
        bootloaderreboot.setSummary(getString(R.string.reboot_bootloader_summary));
        bootloaderreboot.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Dialog bootloaderreboot = new Dialog(getActivity());
                bootloaderreboot.setTitle(getString(R.string.sure_question));
                bootloaderreboot.setMessage(getString(R.string.bootloader_message));
                bootloaderreboot.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });
                bootloaderreboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                    new Execute().execute(RebootCommand + " bootloader");
                });
                bootloaderreboot.show();
            }
        });
        advanced.addItem(bootloaderreboot);

        items.add(advanced);
    }

    private class Execute extends AsyncTask<String, Void, Void> {
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
        protected Void doInBackground(String... params) {
            RootUtils.runCommand(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();
        if (mPermissionDenied) {
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            return;
        }

        mSelectionMenu = new Dialog(getActivity()).setItems(getResources().getStringArray(
                R.array.flasher), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent manualflash = new Intent(getActivity(), FilePickerActivity.class);
                manualflash.putExtra(FilePickerActivity.PATH_INTENT, "/sdcard");
                manualflash.putExtra(FilePickerActivity.EXTENSION_INTENT, ".zip");
                startActivityForResult(manualflash, 0);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mSelectionMenu = null;
            }
        });
        mSelectionMenu.show();
    }

    private void showFlashingDialog(final File file) {
        final LinkedHashMap<String, SmartPack.FLASHMENU> menu = getflashMenu();
        mFlashingDialog = new Dialog(getActivity()).setItems(menu.keySet().toArray(
                new String[menu.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SmartPack.FLASHMENU flashmenu = menu.values().toArray(new SmartPack.FLASHMENU[menu.size()])[i];
                if (file != null) {
                    manualFlash(flashmenu, file, true);
                }
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mFlashingDialog = null;
            }
        });
        mFlashingDialog.show();
    }

    private void manualFlash(final SmartPack.FLASHMENU flashmenu, final File file, final boolean flashing) {
        mFlashDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {

                    private ProgressDialog mProgressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mProgressDialog = new ProgressDialog(getActivity());
                        mProgressDialog.setMessage(getString(R.string.flashing));
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        SmartPack.manualFlash(file);
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
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mFlashDialog = null;
            }
        }, getActivity());
        mFlashDialog.show();
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            mPermissionDenied = true;
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
        }
    }

    private LinkedHashMap<String, SmartPack.FLASHMENU> getflashMenu() {
        LinkedHashMap<String, SmartPack.FLASHMENU> flashingMenu = new LinkedHashMap<>();
        flashingMenu.put(getString(R.string.flasher_message), SmartPack.FLASHMENU.FLASH);
        return flashingMenu;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            showFlashingDialog(new File(data.getStringExtra(FilePickerActivity.RESULT_INTENT)));
        }
    }
}
