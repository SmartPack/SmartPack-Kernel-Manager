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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 30, 2020
 */

public class UpdateChannelActivity extends BaseActivity {

    private AppCompatEditText mKernelNameHint;
    private AppCompatEditText mKernelVersionHint;
    private AppCompatEditText mDownloadLinkHint;
    private AppCompatEditText mChangelogHint;
    private AppCompatEditText mSHA1Hint;
    private AppCompatEditText mSupportHint;
    private AppCompatEditText mDonationHint;
    private CardView mCardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatechannel);

        mCardView = findViewById(R.id.updatechannel_card);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        mSave.setOnClickListener(v -> {
            if (mKernelNameHint.getText() != null && !mKernelNameHint.getText().toString().equals("")
                    && mKernelVersionHint.getText() != null && !mKernelVersionHint.getText().toString().equals("")
                    && mDownloadLinkHint.getText() != null && !mDownloadLinkHint.getText().toString().equals("")) {
                saveUpdateChannel();
            } else {
                Utils.snackbar(mCardView, getString(R.string.submit_failed));
            }
        });
        AppCompatTextView mClearAll = findViewById(R.id.clear_all);
        mClearAll.setOnClickListener(v -> {
            if (isTextEntered()) {
                new Dialog(this)
                        .setMessage(getString(R.string.clear_all_summary) + " " + getString(R.string.sure_question))
                        .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                        })
                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                            clearAll();
                        })
                        .show();
            } else {
                Utils.snackbar(mCardView, getString(R.string.clear_message));
            }
        });
        mKernelNameHint = findViewById(R.id.kernel_name_hint);
        mKernelVersionHint = findViewById(R.id.kernel_version_hint);
        mDownloadLinkHint = findViewById(R.id.download_link_hint);
        mChangelogHint = findViewById(R.id.changelog_hint);
        mSHA1Hint = findViewById(R.id.sha1_hint);
        mSupportHint = findViewById(R.id.support_hint);
        mDonationHint = findViewById(R.id.donation_link_hint);
    }

    private void clearAll() {
        mKernelNameHint.setText(null);
        mKernelVersionHint.setText(null);
        mDownloadLinkHint.setText(null);
        mChangelogHint.setText(null);
        mSHA1Hint.setText(null);
        mSupportHint.setText(null);
        mDonationHint.setText(null);
    }

    private void saveUpdateChannel() {
        ViewUtils.dialogEditText(Objects.requireNonNull(mKernelNameHint.getText()).toString(),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.snackbar(mCardView, getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".json")) {
                        text += ".json";
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    if (Utils.existFile(Utils.getInternalDataStorage() + "/" + text)) {
                        Utils.snackbar(mCardView, getString(R.string.already_exists, text));
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject();
                        JSONObject kernel = new JSONObject();
                        kernel.put("name", mKernelNameHint.getText());
                        kernel.put("version", mKernelVersionHint.getText());
                        kernel.put("link", mDownloadLinkHint.getText());
                        kernel.put("changelog_url", mChangelogHint.getText());
                        kernel.put("sha1", mSHA1Hint.getText());
                        obj.put("kernel", kernel);
                        JSONObject support = new JSONObject();
                        support.put("link", mSupportHint.getText());
                        support.put("donation", mDonationHint.getText());
                        obj.put("support", support);
                        Utils.prepareInternalDataStorage();
                        Utils.create(obj.toString(), Utils.getInternalDataStorage() + "/" + text);
                        new Dialog(this)
                                .setMessage(getString(R.string.json_created,
                                        Utils.getInternalDataStorage() + "/" + text))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                                })
                                .show();
                    } catch (JSONException ignored) {
                    }
                }, this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private boolean isTextEntered() {
        return mKernelNameHint.getText() != null && !mKernelNameHint.getText().toString().equals("")
                || mKernelVersionHint.getText() != null && !mKernelVersionHint.getText().toString().equals("")
                || mDownloadLinkHint.getText() != null && !mDownloadLinkHint.getText().toString().equals("")
                || mChangelogHint.getText() != null && !mChangelogHint.getText().toString().equals("")
                || mSHA1Hint.getText() != null && !mSHA1Hint.getText().toString().equals("")
                || mSupportHint.getText() != null && !mSupportHint.getText().toString().equals("")
                || mDonationHint.getText() != null && !mDonationHint.getText().toString().equals("");
    }

    @Override
    public void onBackPressed() {
        if (isTextEntered()) {
            new Dialog(this)
                    .setMessage(getString(R.string.update_channel_create_warning) + " " + getString(R.string.sure_question))
                    .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                        super.onBackPressed();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

}
