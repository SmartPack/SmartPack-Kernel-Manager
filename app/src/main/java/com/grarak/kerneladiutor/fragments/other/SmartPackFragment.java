/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
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

package com.grarak.kerneladiutor.fragments.other;

import android.os.Build;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

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

	if (Device.hasSmartPackInstalled()) {
            DescriptionView currentspversion = new DescriptionView();
            currentspversion.setTitle(("Current ") + getString(R.string.version));
            if (Device.hasSmartPackVersion()) {
		currentspversion.setSummary(Device.getSmartPackVersion());
            } else {
		currentspversion.setSummary(RootUtils.runCommand("uname -r"));
            }
            smartpack.addItem(currentspversion);

            if (Device.isSamsungmsm8974()) {
		    DescriptionView xdapage = new DescriptionView();
		    xdapage.setTitle(getString(R.string.support));
		    xdapage.setSummary(getString(R.string.support_summary));
		    xdapage.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://forum.xda-developers.com/galaxy-s5/development/kernel-smartpack-project-stock-t3568810", getActivity());
		            } else {
				Utils.launchUrl("https://forum.xda-developers.com/galaxy-s5/unified-development/kernel-project-kltexxx-t3564206", getActivity());
		            }
			}
		    });

		    DescriptionView changelogsp = new DescriptionView();
		    changelogsp.setTitle(getString(R.string.change_logs));
		    changelogsp.setSummary(getString(R.string.change_logs_summary));
		    changelogsp.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_kltexxx/Stock/change-logs.md", getActivity());
		            } else if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_kltexxx/Nougat/change-logs.md", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_kltexxx/Oreo/change-logs.md", getActivity());
		            }
			}
		    });

		    DescriptionView spsource = new DescriptionView();
		    spsource.setTitle(getString(R.string.source_code));
		    spsource.setSummary(getString(R.string.source_code_summary));
		    spsource.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/tree/stock", getActivity());
		            } else if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/tree/Nougat", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/tree/Oreo", getActivity());
		            }
			}
		    });

		    smartpack.addItem(changelogsp);
		    smartpack.addItem(xdapage);
		    smartpack.addItem(spsource);

            } else if (Device.isOnePlusdumpling()) {
		    DescriptionView xdapage = new DescriptionView();
		    xdapage.setTitle(getString(R.string.support));
		    xdapage.setSummary(getString(R.string.support_summary));
		    xdapage.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            Utils.launchUrl("https://forum.xda-developers.com/oneplus-5t/development/kernel-smartpack-linaro-gcc-7-x-oxygen-t3832458", getActivity());
			}
		    });

		    DescriptionView changelogsp = new DescriptionView();
		    changelogsp.setTitle(getString(R.string.change_logs));
		    changelogsp.setSummary(getString(R.string.change_logs_summary));
		    changelogsp.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_OP5T/Oreo/change-logs.md", getActivity());
		            }
			}
		    });

		    DescriptionView spsource = new DescriptionView();
		    spsource.setTitle(getString(R.string.source_code));
		    spsource.setSummary(getString(R.string.source_code_summary));
		    spsource.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/tree/Oreo", getActivity());
		            }
			}
		    });

		    smartpack.addItem(changelogsp);
		    smartpack.addItem(xdapage);
		    smartpack.addItem(spsource);

            } else if (Device.isMotoG3()) {
		    DescriptionView xdapage = new DescriptionView();
		    xdapage.setTitle(getString(R.string.support));
		    xdapage.setSummary(getString(R.string.support_summary));
		    xdapage.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            Utils.launchUrl("https://forum.xda-developers.com/2015-moto-g/orig-development/kernel-smartpack-linaro-gcc-7-x-lineage-t3834515", getActivity());
			}
		    });

		    DescriptionView changelogsp = new DescriptionView();
		    changelogsp.setTitle(getString(R.string.change_logs));
		    changelogsp.setSummary(getString(R.string.change_logs_summary));
		    changelogsp.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Project_osprey/Oreo/change-logs.md", getActivity());
		            }
			}
		    });

		    DescriptionView spsource = new DescriptionView();
		    spsource.setTitle(getString(R.string.source_code));
		    spsource.setSummary(getString(R.string.source_code_summary));
		    spsource.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
			@Override
			public void onClick(RecyclerViewItem item) {
		            if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_osprey/tree/Oreo", getActivity());
		            }
			}
		    });

		    smartpack.addItem(changelogsp);
		    smartpack.addItem(xdapage);
		    smartpack.addItem(spsource);

            }
	}

	DescriptionView website = new DescriptionView();
	website.setTitle(getString(R.string.website));
	website.setSummary(getString(R.string.website_summary));
	website.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
		Utils.launchUrl("https://sunilpaulmathew.github.io/smartpack/", getActivity());
            }
	});

	if (Device.isSamsungmsm8974()) {
		DescriptionView downloads = new DescriptionView();
		downloads.setTitle(getString(R.string.downloads));
		downloads.setSummary(getString(R.string.downloads_summary));
		downloads.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
		    @Override
		    public void onClick(RecyclerViewItem item) {
			if (Device.iskltekor()) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/stock/kernel-release/SmartPack-Kernel-kltekor.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Nougat/kernel-release/SmartPack-Kernel-kltekor.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Oreo/kernel-release/SmartPack-Kernel-kltekor.zip?raw=true", getActivity());
		            }
			} else if (Device.isklte()) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/stock/kernel-release/SmartPack-Kernel-klte.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Nougat/kernel-release/SmartPack-Kernel-klte.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Oreo/kernel-release/SmartPack-Kernel-klte.zip?raw=true", getActivity());
		            }
			} else if (Device.iskltedv()) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/stock/kernel-release/SmartPack-Kernel-kltespr.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Nougat/kernel-release/SmartPack-Kernel-kltespr.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Oreo/kernel-release/SmartPack-Kernel-kltedv.zip?raw=true", getActivity());
		            }
			} else if (Device.isklteduos()) {
		            if (Build.VERSION.SDK_INT == 23) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/stock/kernel-release/SmartPack-Kernel-klteduos.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Nougat/kernel-release/SmartPack-Kernel-klteduos.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Oreo/kernel-release/SmartPack-Kernel-klteduos.zip?raw=true", getActivity());
		            }
			} else if (Device.iskltejpn()) {
		            if (Build.VERSION.SDK_INT == 25) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Nougat/kernel-release/SmartPack-Kernel-kltekdi.zip?raw=true", getActivity());
		            } else if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/blob/Oreo/kernel-release/SmartPack-Kernel-kltekdi.zip?raw=true", getActivity());
		            }
			}
		    }
		});
	smartpack.addItem(downloads);

	} else if (Device.isOnePlusdumpling()) {
		DescriptionView downloads = new DescriptionView();
		downloads.setTitle(getString(R.string.downloads));
		downloads.setSummary(getString(R.string.downloads_summary));
		downloads.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
		    @Override
		    public void onClick(RecyclerViewItem item) {
			if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_OP5T/blob/Oreo/kernel-release/SmartPack-Kernel-dumpling.zip?raw=true", getActivity());
			}
		    }
		});
	smartpack.addItem(downloads);

	} else if (Device.isMotoG3()) {
		DescriptionView downloads = new DescriptionView();
		downloads.setTitle(getString(R.string.downloads));
		downloads.setSummary(getString(R.string.downloads_summary));
		downloads.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
		    @Override
		    public void onClick(RecyclerViewItem item) {
			if (Build.VERSION.SDK_INT == 27) {
				Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Project_osprey/blob/Oreo/kernel-release/SmartPack-Kernel-osprey.zip?raw=true", getActivity());
			}
		    }
		});
	smartpack.addItem(downloads);
	}

	DescriptionView recoveryreboot = new DescriptionView();
	recoveryreboot.setTitle(getString(R.string.reboot_recovery));
	recoveryreboot.setSummary(getString(R.string.reboot_recovery_summary));
	recoveryreboot.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
		RootUtils.runCommand("reboot recovery");
            }
	});

	smartpack.addItem(website);
	smartpack.addItem(recoveryreboot);
	items.add(smartpack);
    }

    public static boolean supported() {
        return Device.hasSmartPackVersion() || Device.isSamsungmsm8974() || Device.isOnePlusdumpling() || Device.isMotoG3() || Device.hasSmartPackInstalled();
    }

}
