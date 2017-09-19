/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.views.recyclerview.datasharing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.server.DeviceInfo;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 20.09.17.
 */

public class DataSharingDeviceView extends RecyclerViewItem {

    private DeviceInfo mDeviceInfo;
    private int mRank;

    private View mDialogView;

    public DataSharingDeviceView(DeviceInfo deviceInfo, int rank) {
        mDeviceInfo = deviceInfo;
        mRank = rank;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_datasharing_device;
    }

    @Override
    public void onCreateView(View view) {
        TextView rank = view.findViewById(R.id.rank);
        TextView name = view.findViewById(R.id.name);
        TextView board = view.findViewById(R.id.board);
        TextView fingerprint = view.findViewById(R.id.fingerprint);

        rank.setText(String.valueOf(mRank / 10 == 0 ? "0" + mRank : mRank));

        String vendor = mDeviceInfo.getVendor();
        vendor = vendor.substring(0, 1).toUpperCase() + vendor.substring(1);
        name.setText(vendor + " " + mDeviceInfo.getModel());

        board.setText(mDeviceInfo.getBoard());
        fingerprint.setText(mDeviceInfo.getFingerprint());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup parent = (ViewGroup) mDialogView.getParent();
                if (parent != null) parent.removeView(mDialogView);
                new Dialog(view.getContext())
                        .setView(mDialogView).show();
            }
        });

        super.onCreateView(view);

        if (mDialogView != null) return;
        mDialogView = LayoutInflater.from(view.getContext()).inflate(
                R.layout.dialog_datasharing_device, null, false);

        TextView title = mDialogView.findViewById(R.id.title);
        TextView androidVersion = mDialogView.findViewById(R.id.android_version);
        TextView kernelVersion = mDialogView.findViewById(R.id.kernel_version);
        board = mDialogView.findViewById(R.id.board);
        fingerprint = mDialogView.findViewById(R.id.fingerprint);
        TextView sot = mDialogView.findViewById(R.id.sot);
        TextView cpuScore = mDialogView.findViewById(R.id.cpu_score);
        TextView cpuInfo = mDialogView.findViewById(R.id.cpu_information);
        TextView settings = mDialogView.findViewById(R.id.settings);

        title.setText(vendor + " " + mDeviceInfo.getModel());
        androidVersion.setText(mDeviceInfo.getAndroidVersion());
        kernelVersion.setText(mDeviceInfo.getKernelVersion());
        board.setText(mDeviceInfo.getBoard());
        fingerprint.setText(mDeviceInfo.getFingerprint());

        long seconds = mDeviceInfo.getAverageSOT();
        int h = (int) seconds / 60 / 60;
        int m = (int) seconds / 60 % 60;
        int s = (int) seconds % 60;

        sot.setText(h + ":" + m + ":" + s + " (" + view.getContext().getString(
                R.string.screen_always_on) + ")");
        cpuScore.setText(mDeviceInfo.getCpu() + " (" + view.getContext().getString(
                R.string.lower_better) + ")");
        cpuInfo.setText(mDeviceInfo.getCpuInfo());
        if (mDeviceInfo.getCommands().size() == 0) {
            settings.setText(view.getContext().getString(R.string.default_settings));
        } else {
            StringBuilder commands = new StringBuilder();
            for (String command : mDeviceInfo.getCommands()) {
                commands.append(command).append("\n");
            }
            commands.setLength(commands.length() - 1);
            settings.setText(commands.toString());
        }
    }

}
