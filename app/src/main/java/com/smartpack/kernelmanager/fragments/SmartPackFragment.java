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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import com.smartpack.kernelmanager.utils.SmartPack;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 24, 2018
 */

public class SmartPackFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        SmartPackInit(items);
    }

    private void SmartPackInit(List<RecyclerViewItem> items) {

	CardView smartpack = new CardView(getActivity());
	smartpack.setTitle(getString(R.string.smartpack_kernel));

	if (SmartPack.hasSmartPackInstalled()) {
            DescriptionView currentspversion = new DescriptionView();
            currentspversion.setTitle(("Current ") + getString(R.string.version));
            if (SmartPack.hasSmartPackVersion()) {
		currentspversion.setSummary(SmartPack.getSmartPackVersion());
            } else {
		currentspversion.setSummary(RootUtils.runCommand("uname -r"));
            }
            smartpack.addItem(currentspversion);
	}

	if (SmartPack.supported() && (Build.VERSION.SDK_INT >= 27)) {
            DescriptionView spversion = new DescriptionView();
            spversion.setTitle(("Latest ") + getString(R.string.version));
            if ((SmartPack.hasSmartPackInstalled()) && (SmartPack.SmartPackRelease())) {
		if (SmartPack.getSmartPackVersionNumber() < SmartPack.getlatestSmartPackVersionNumber()) {
		    spversion.setSummary(("~ New Update (") + SmartPack.getlatestSmartPackVersion() + (") Available ~") + getString(R.string.recheck));
		} else {
		    spversion.setSummary(SmartPack.getlatestSmartPackVersion() + ("\n~ ") + getString(R.string.up_to_date_message) + (" ~") + getString(R.string.recheck));
		}
            } else if (SmartPack.SmartPackRelease()) {
		spversion.setSummary(("~ SmartPack-Kernel ") + SmartPack.getlatestSmartPackVersion() + (" is available ~") + getString(R.string.recheck));
            } else {
		spversion.setSummary(getString(R.string.latest_version_check));
            }
            spversion.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
		@Override
		public void onClick(RecyclerViewItem item) {
		// Delete latest kernel version information
		if (SmartPack.haslatestSmartPackVersion()) {
		    RootUtils.runCommand("rm -r /data/data/com.smartpack.kernelmanager/version");
		}
		// Get latest kernel version information
		if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 27)) {
		    RootUtils.runCommand("curl -L -o /data/data/com.smartpack.kernelmanager/version https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Oreo/anykernel_SmartPack/ramdisk/version?raw=true");
		} else if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 28)) {
		    RootUtils.runCommand("curl -L -o /data/data/com.smartpack.kernelmanager/version https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Pie/anykernel_SmartPack/ramdisk/version?raw=true");
		}
		if ((SmartPack.hasSmartPackInstalled()) && (SmartPack.SmartPackRelease())) {
		    if (SmartPack.getSmartPackVersionNumber() < SmartPack.getlatestSmartPackVersionNumber()) {
			spversion.setSummary(("~ New Update (") + SmartPack.getlatestSmartPackVersion() + (") Available ~") + getString(R.string.recheck));
		    } else {
			spversion.setSummary(SmartPack.getlatestSmartPackVersion() + ("\n~ ") + getString(R.string.up_to_date_message) + (" ~") + getString(R.string.recheck));
		    }
            	} else if (SmartPack.SmartPackRelease()) {
		    spversion.setSummary(("~ SmartPack-Kernel ") + SmartPack.getlatestSmartPackVersion() + (" is available ~") + getString(R.string.recheck));
		} else {
		    spversion.setSummary(getString(R.string.update_check_failed) + getString(R.string.recheck));
		}
            }
            });
            smartpack.addItem(spversion);

            DescriptionView downloads = new DescriptionView();
            downloads.setTitle(getString(R.string.get_it));
            downloads.setSummary(getString(R.string.get_it_summary));
            downloads.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
		@Override
		public void onClick(RecyclerViewItem item) {
		    // Check and delete an old version of the kernel from the download folder, if any...
		    if (SmartPack.isSmartPackDownloaded()) {
			RootUtils.runCommand("rm -rf /sdcard/Download/SmartPack-Kernel.zip");
		    }
		    // Show an alert dialogue and let the user know the process...
		    AlertDialog.Builder downloads = new AlertDialog.Builder(getActivity());
		    downloads.setTitle(getString(R.string.download_flash));
		    downloads.setMessage(getString(R.string.downloads_message));
		    downloads.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
		    });
		    downloads.setPositiveButton(getString(R.string.download), (dialog1, id1) -> {
		    // Initiate device specific downloads...
		    if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 27)) {
			RootUtils.runCommand("curl -L -o /sdcard/Download/SmartPack-Kernel.zip https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Oreo/kernel-release/SmartPack-Kernel-dumpling.zip?raw=true");
		    } else if ((SmartPack.isOnePlusmsm8998()) && (Build.VERSION.SDK_INT == 28)) {
			RootUtils.runCommand("curl -L -o /sdcard/Download/SmartPack-Kernel.zip https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Pie/kernel-release/SmartPack-Kernel-dumpling.zip?raw=true");
		    }
		    // Proceed only if the download was successful...
		    if (SmartPack.isSmartPackDownloaded()) {
			// Show Auto-flash message if we recognize recovery...
			if (SmartPack.hasRecovery()) {
		            AlertDialog.Builder flash = new AlertDialog.Builder(getActivity());
		            flash.setTitle(getString(R.string.autoflash));
		            flash.setMessage(getString(R.string.flash_message));
		            flash.setNegativeButton(getString(R.string.manual_flashing), (dialogInterface, i) -> {
		            });
		            flash.setPositiveButton(getString(R.string.install_now), (dialog2, id2) -> {
				RootUtils.runCommand("echo --update_package=/sdcard/Download/SmartPack-Kernel.zip > /cache/recovery/command");
				RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
				RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
				RootUtils.runCommand("reboot recovery");
		            });
		            flash.show();
			// If not, show an "Auto-flash not possible" message...
			} else {
		            AlertDialog.Builder noflash = new AlertDialog.Builder(getActivity());
		            noflash.setTitle(getString(R.string.warning));
		            noflash.setMessage(getString(R.string.no_flash_message));
		            noflash.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
		            });
		            noflash.setPositiveButton(getString(R.string.reboot), (dialog2, id2) -> {
				RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
				RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
				RootUtils.runCommand("reboot recovery");
		            });
		            noflash.show();
			}
		    // Shown "Download failed" message...
		    } else {
			AlertDialog.Builder downloadfailed = new AlertDialog.Builder(getActivity());
			downloadfailed.setTitle(getString(R.string.appupdater_download_filed));
			downloadfailed.setMessage(getString(R.string.download_failed_message));
			downloadfailed.setPositiveButton(getString(R.string.exit), (dialog2, id2) -> {
			});
			downloadfailed.show();
			}
		    });
		    downloads.show();
		} 
            });
            smartpack.addItem(downloads);
	}

	if ((SmartPack.hasSmartPackInstalled()) && (Build.VERSION.SDK_INT >= 25)) {
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
	}

	if ((SmartPack.hasSmartPackInstalled()) && (Build.VERSION.SDK_INT >= 27)) {

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
            AlertDialog.Builder resetsettings = new AlertDialog.Builder(getActivity());
            resetsettings.setTitle(getString(R.string.warning));
            resetsettings.setMessage(getString(R.string.reset_settings_message));
            resetsettings.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            resetsettings.setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
            AlertDialog.Builder reboot = new AlertDialog.Builder(getActivity());
            reboot.setTitle(getString(R.string.reboot));
            reboot.setMessage(getString(R.string.reboot_message));
            reboot.setNegativeButton(getString(R.string.reboot_later), (dialogInterface, i) -> {
		RootUtils.runCommand("rm -rf /data/data/com.smartpack.kernelmanager/");
		RootUtils.runCommand("pm clear com.smartpack.kernelmanager && am start -n com.smartpack.kernelmanager/com.grarak.kerneladiutor.activities.MainActivity");
            });
            reboot.setPositiveButton(getString(R.string.reboot_now), (dialog2, id2) -> {
		RootUtils.runCommand("rm -rf /data/data/com.smartpack.kernelmanager/");
		RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
		RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
		RootUtils.runCommand("pm clear com.smartpack.kernelmanager && reboot");
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
            	new GetLOG().execute("logcat -d > /sdcard/logcat");
            	new GetLOG().execute("logcat  -b radio -v time -d > /sdcard/radio");
            	new GetLOG().execute("logcat -b events -v time -d > /sdcard/events");
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
		    new GetLOG().execute("cat /proc/last_kmsg > /sdcard/last_kmsg");
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
            	new GetLOG().execute("dmesg > /sdcard/dmesg");
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
		    new GetLOG().execute("cat /sys/fs/pstore/dmesg-ramoops* > /sdcard/dmesg-ramoops");
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
		    new GetLOG().execute("cat /sys/fs/pstore/console-ramoops* > /sdcard/console-ramoops");
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
		AlertDialog.Builder wipecache = new AlertDialog.Builder(getActivity());
		wipecache.setTitle(getString(R.string.sure_question));
		wipecache.setMessage(getString(R.string.wipe_cache_message));
		wipecache.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
		});
		wipecache.setPositiveButton(getString(R.string.wipe_cache), (dialog1, id1) -> {
		    RootUtils.runCommand("echo --wipe_cache > /cache/recovery/command");
		    RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
		    RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
		    RootUtils.runCommand("reboot recovery");
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
		AlertDialog.Builder wipedata = new AlertDialog.Builder(getActivity());
		wipedata.setTitle(getString(R.string.sure_question));
		wipedata.setMessage(getString(R.string.wipe_data_message));
		wipedata.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
		});
		wipedata.setPositiveButton(getString(R.string.wipe_data), (dialog1, id1) -> {
		    RootUtils.runCommand("echo --wipe_data > /cache/recovery/command");
		    RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
		    RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
		    RootUtils.runCommand("reboot recovery");
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
            AlertDialog.Builder recoveryreboot = new AlertDialog.Builder(getActivity());
            recoveryreboot.setTitle(getString(R.string.sure_question));
            recoveryreboot.setMessage(getString(R.string.recovery_message));
            recoveryreboot.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            recoveryreboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
		RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
		RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
            	RootUtils.runCommand("reboot recovery");
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
            AlertDialog.Builder bootloaderreboot = new AlertDialog.Builder(getActivity());
            bootloaderreboot.setTitle(getString(R.string.sure_question));
            bootloaderreboot.setMessage(getString(R.string.bootloader_message));
            bootloaderreboot.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            bootloaderreboot.setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
		RootUtils.runCommand("am broadcast android.intent.action.ACTION_SHUTDOWN && sync");
		RootUtils.runCommand("echo 3 > /proc/sys/vm/drop_caches && sync && sleep 3");
            	RootUtils.runCommand("reboot bootloader");
            });
            bootloaderreboot.show();
            }
	});
	advanced.addItem(bootloaderreboot);

	items.add(advanced);
    }

    private class GetLOG extends AsyncTask<String, Void, Void> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.saving));
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

}
