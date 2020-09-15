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

package com.smartpack.kernelmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 15, 2020
 */

public class NoRootActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroot);

        LinearLayout bbi = findViewById(R.id.bbi);
        AppCompatTextView mainTitle = findViewById(R.id.title_main);
        AppCompatTextView mainMessage = findViewById(R.id.message_main);
        bbi.setOnClickListener(v -> launchBBI());
        if (Utils.mHasRoot && !Utils.mHasBusybox) {
            bbi.setVisibility(View.VISIBLE);
            mainTitle.setText(getString(R.string.no_busybox));
            mainMessage.setText(R.string.busybox_message);
        } else {
            mainTitle.setText(R.string.no_root);
            mainMessage.setText(R.string.no_root_message);
        }
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatTextView mCancel = findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(v -> super.onBackPressed());
    }

    private void launchBBI() {
        Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.busyboxinstaller", this);
    }
}