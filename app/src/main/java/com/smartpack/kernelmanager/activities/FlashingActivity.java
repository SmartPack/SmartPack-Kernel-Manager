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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.tools.SmartPack;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2020
 */

public class FlashingActivity extends BaseActivity {

    private static AppCompatImageButton mSaveButton;
    private static AppCompatTextView mCancelButton;
    private static AppCompatTextView mFlashingResult;
    private static AppCompatTextView mRebootButton;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashing);

        initToolBar();
        mCancelButton = findViewById(R.id.cancel_button);
        mFlashingResult = findViewById(R.id.output_text);
        mSaveButton = findViewById(R.id.save_button);
        mRebootButton = findViewById(R.id.reboot_button);
        mSaveButton.setOnClickListener(v -> {
            Utils.create("## Flasher log created by SmartPack-Kernel Manager\n\n" + SmartPack.mFlashingResult.toString(),
                    Utils.getInternalDataStorage() + "/flasher_log");
            Utils.toast(getString(R.string.flash_log_summary, Utils.getInternalDataStorage() + "/flasher_log"), getApplicationContext());
        });
        mCancelButton.setOnClickListener(v -> {
            onBackPressed();
        });
        mRebootButton.setOnClickListener(v -> {
            onBackPressed();
            Utils.rebootCommand(this);
        });
        refreshText();
    }

    public void refreshText() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(() -> {
                            if (SmartPack.mFlashingResult != null) {
                                mFlashingResult.setText(SmartPack.mFlashingResult.toString());
                                if (!SmartPack.mFlashing) {
                                    mCancelButton.setVisibility(View.VISIBLE);
                                    mSaveButton.setVisibility(View.VISIBLE);
                                    mRebootButton.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                } catch (InterruptedException ignored) { }
            }
        };
        t.start();
    }

    @Override
    public void onBackPressed() {
        if (SmartPack.mFlashing) return;
        super.onBackPressed();
    }

}