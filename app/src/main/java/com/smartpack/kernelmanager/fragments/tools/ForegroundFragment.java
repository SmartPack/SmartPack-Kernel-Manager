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
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.SmartPack;

import java.io.File;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 30, 2020
 */

public class ForegroundFragment extends BaseFragment {

    private static AppCompatTextView mFlashingResult;
    private static AppCompatTextView mCancelButton;
    private static AppCompatTextView mSaveButton;
    private static AppCompatTextView mRebootButton;
    private static ProgressDialog mProgressDialog;

    private static final String FLASHER_LOG = Utils.getInternalDataStorage() + "/flasher_log";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_foreground, container, false);

        mFlashingResult = rootView.findViewById(R.id.flashing_result);
        mCancelButton = rootView.findViewById(R.id.cancel_text);
        mSaveButton = rootView.findViewById(R.id.save_text);
        mRebootButton = rootView.findViewById(R.id.reboot_text);
        mCancelButton.setOnClickListener(v -> cancel());
        mSaveButton.setOnClickListener(v -> saveLog());
        mRebootButton.setOnClickListener(v -> reboot());

        return rootView;
    }

    public void setText(String string) {
        if (mFlashingResult != null) {
            mFlashingResult.setText(string);
        }
    }

    void showCancel() {
        if (mCancelButton != null) {
            mCancelButton.setText(R.string.cancel);
            mCancelButton.setVisibility(View.VISIBLE);
        }
    }

    void showSave() {
        if (mSaveButton != null) {
            mSaveButton.setText(R.string.save_log);
            mSaveButton.setVisibility(View.VISIBLE);
        }
    }

    void showReboot() {
        if (mRebootButton != null) {
            mRebootButton.setText(R.string.reboot);
            mRebootButton.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void reboot() {
        if (Utils.mFlashing) {
            Utils.toast(R.string.flashing_progress, getActivity());
        } else {
            Utils.dismissForeground();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setMessage(getString(R.string.rebooting) + ("..."));
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    RootUtils.runCommand(Utils.prepareReboot());
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
    }

    private void saveLog() {
        if (Utils.mFlashing) {
            Utils.toast(R.string.flashing_progress, getActivity());
        } else {
            Utils.create(SmartPack.mFlashingResult.toString(), FLASHER_LOG + "_" +
                    new File(Utils.mPath).getName().replace(".zip", ""));
            Utils.toast(getString(R.string.flash_log_summary, FLASHER_LOG + "_" +
                    new File(Utils.mPath).getName().replace(".zip", "")), getActivity());
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            Utils.dismissForeground();
        }
    }

    private void cancel() {
        if (Utils.mFlashing) {
            Utils.toast(R.string.flashing_progress, getActivity());
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            Utils.dismissForeground();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (SmartPack.mFlashingResult == null) {
            SmartPack.mFlashingResult = new StringBuilder();
        } else {
            SmartPack.mFlashingResult.setLength(0);
        }
        if (SmartPack.mFlashingResult.toString().isEmpty()) {
            Utils.dismissForeground();
        }
    }

}