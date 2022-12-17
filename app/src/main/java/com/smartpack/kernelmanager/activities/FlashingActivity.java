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
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2020
 */

public class FlashingActivity extends BaseActivity {

    private AppCompatImageButton mSaveButton;
    private MaterialTextView mFlashingHeading, mFlashingResult;
    private MaterialCardView mCancelButton, mRebootButton;
    private LinearLayout mProgressLayout;
    private NestedScrollView mScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashing);

        mCancelButton = findViewById(R.id.cancel_button);
        mFlashingHeading = findViewById(R.id.flashing_title);
        mFlashingResult = findViewById(R.id.output_text);
        MaterialTextView mProgressMessage = findViewById(R.id.progress_message);
        mSaveButton = findViewById(R.id.save_button);
        mRebootButton = findViewById(R.id.reboot_button);
        mProgressLayout = findViewById(R.id.progress_layout);
        mScrollView = findViewById(R.id.scroll_view);
        mProgressMessage.setText(getString(R.string.flashing));
        mProgressMessage.setVisibility(View.VISIBLE);
        mSaveButton.setOnClickListener(v -> {
            Utils.create("## Flasher log created by SmartPack-Kernel Manager\n\n" + Common.getFlashingResult().toString(),
                    Utils.getInternalDataStorage() + "/flasher_log-" + Common.getZipName().replace(".zip", ""));
            Utils.snackbar(findViewById(android.R.id.content), getString(R.string.flash_log_summary, Utils.getInternalDataStorage()
                    + "/flasher_log-" + Common.getZipName().replace(".zip", "")));
        });
        mCancelButton.setOnClickListener(v -> onBackPressed());
        mRebootButton.setOnClickListener(v -> {
            onBackPressed();
            Utils.rebootCommand(this);
        });

        Thread mRefreshThread = new RefreshThread(this);
        mRefreshThread.start();
    }

    private class RefreshThread extends Thread {
        WeakReference<FlashingActivity> mInstallerActivityRef;
        RefreshThread(FlashingActivity activity) {
            mInstallerActivityRef = new WeakReference<>(activity);
        }
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(250);
                    final FlashingActivity activity = mInstallerActivityRef.get();
                    if(activity == null){
                        break;
                    }
                    activity.runOnUiThread(() -> {
                        if (Common.isFlashing()) {
                            mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                        } else {
                            mProgressLayout.setVisibility(View.GONE);
                            mSaveButton.setVisibility(Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ? View.VISIBLE : View.GONE);
                            mRebootButton.setVisibility(Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ? View.VISIBLE : View.GONE);
                            mCancelButton.setVisibility(View.VISIBLE);
                        }
                        mFlashingHeading.setText(Common.isFlashing() ? getString(R.string.flashing) : Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ?
                                getString(R.string.flashing_finished) : getString(R.string.flashing_failed));
                        mFlashingResult.setText(!Utils.getOutput(Common.getFlashingOutput()).isEmpty() ? Utils.getOutput(Common.getFlashingOutput())
                                .replace("\nsuccess","") : Common.isFlashing() ? "" : Common.getFlashingResult().toString());
                    });
                }
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (Common.isFlashing()) return;
        super.onBackPressed();
    }

}