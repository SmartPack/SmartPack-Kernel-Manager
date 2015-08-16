/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.elements.cards.download;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CircleChart;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.DownloadTask;
import com.grarak.kerneladiutor.utils.json.Downloads;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.Recovery;
import com.kerneladiutor.library.Tools;
import com.kerneladiutor.library.root.RootFile;
import com.kerneladiutor.library.root.RootUtils;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

/**
 * Created by willi on 01.07.15.
 */
public class DownloadCardView extends BaseCardView {

    private TextView nameText;
    private TextView descriptionText;
    private TextView changelogText;
    private FloatingActionButton downloadButton;

    public DownloadCardView(Context context, final Downloads.Download download) {
        super(context, R.layout.download_cardview);

        final String name = download.getName();
        nameText.setText(Html.fromHtml(name));
        nameText.setMovementMethod(LinkMovementMethod.getInstance());

        String description;
        if ((description = download.getDescription()) != null) {
            descriptionText.setText(Html.fromHtml(description));
            descriptionText.setMovementMethod(LinkMovementMethod.getInstance());
        } else descriptionText.setVisibility(GONE);

        List<String> changelog = download.getChangelogs();
        if (changelog.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String change : changelog)
                if (stringBuilder.length() == 0)
                    stringBuilder.append("\u2022").append(" ").append(change);
                else stringBuilder.append("<br>").append("\u2022").append(" ").append(change);
            changelogText.setText(Html.fromHtml(stringBuilder.toString()));
            changelogText.setMovementMethod(LinkMovementMethod.getInstance());
        }

        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownload(name, download.getMD5sum(), download.getUrl(), download.getInstallMethod());
            }
        });

        if (Utils.isTV(getContext())) {
            downloadButton.setFocusable(true);
            downloadButton.setFocusableInTouchMode(true);
        }
    }

    @Override
    public void setFocus() {
    }

    private void showDownload(final String name, final String md5, String link, final String installMethod) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.download_dialog, null, false);
        final CircleChart circleProgress = (CircleChart) view.findViewById(R.id.circle_progress);
        final View progressView = view.findViewById(R.id.progress_view);

        final TextView downloadingText = (TextView) view.findViewById(R.id.downloading_text);
        downloadingText.setText(getContext().getString(R.string.loading));
        AppCompatButton cancelButton = (AppCompatButton) view.findViewById(R.id.cancel_button);
        final AppCompatButton resumePauseButton = (AppCompatButton) view.findViewById(R.id.resume_pause_button);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(false).create();
        alertDialog.show();

        final DownloadTask downloadTask = new DownloadTask(getContext(), new DownloadTask.OnDownloadListener() {
            @Override
            public void onUpdate(int currentSize, int totalSize) {
                circleProgress.setVisibility(VISIBLE);
                progressView.setVisibility(GONE);

                String current = Utils.round(((double) currentSize) / 1024 / 1024, 2);
                String total = Utils.round(((double) totalSize) / 1024 / 1024, 2);
                downloadingText.setText(getContext().getString(R.string.downloading_counting,
                        current, total) + getContext().getString(R.string.mb));
                Log.i(Constants.TAG, currentSize + " " + totalSize);

                circleProgress.setProgress((int) (((double) currentSize) * 100 / ((double) totalSize)));
            }

            @Override
            public void onSuccess(String path) {
                alertDialog.dismiss();
                checkMD5(name, md5, path, installMethod);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onFailure(String error) {
                Utils.toast(error, getContext());
                alertDialog.dismiss();
            }
        }, Environment.getExternalStorageDirectory().toString() + "/KernelAdiutor/download.zip");
        downloadTask.execute(link);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTask.cancel();
                alertDialog.dismiss();
            }
        });

        resumePauseButton.setOnClickListener(new OnClickListener() {
            private boolean pause;

            @Override
            public void onClick(View view) {
                pause = !pause;
                resumePauseButton.setText(getContext().getString(pause ? R.string.resume : R.string.pause));
                if (pause) downloadTask.pause();
                else downloadTask.resume();
            }
        });
    }

    private void checkMD5(final String name, final String md5, final String path, final String installMethod) {
        new AsyncTask<String, Void, Boolean>() {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage(getContext().getString(R.string.checking_md5));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return Utils.checkMD5(md5, new File(path));
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                mProgressDialog.dismiss();

                if (aBoolean) {
                    new AlertDialog.Builder(getContext()).setMessage(getContext().getString(R.string.download_success, name))
                            .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {

                                private int seleted;

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (installMethod != null) {
                                        RootUtils.runCommand(installMethod.replace("$FILE",
                                                Tools.getInternalStorage() + "/KernelAdiutor/download.zip"));
                                        RootUtils.runCommand("rm -f " +
                                                Tools.getInternalStorage() + "/KernelAdiutor/download.zip");
                                        RootUtils.runCommand("reboot");
                                    } else {
                                        final Recovery recovery =
                                                new Recovery(Recovery.RECOVERY_COMMAND.FLASH_ZIP, new File(path));
                                        String[] items = {
                                                getContext().getString(R.string.cwm_recovery),
                                                getContext().getString(R.string.twrp),
                                                getContext().getString(R.string.manual_flashing)
                                        };
                                        new AlertDialog.Builder(getContext()).setSingleChoiceItems(items, 0,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        seleted = i;
                                                    }
                                                }).setPositiveButton(getContext().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (seleted == 2) {
                                                            Utils.toast(getContext().getString(R.string.file_location, path),
                                                                    getContext());
                                                            return;
                                                        }

                                                        Recovery.RECOVERY type = seleted == 1 ?
                                                                Recovery.RECOVERY.TWRP : Recovery.RECOVERY.CWM;
                                                        RootFile recoveryFile = new RootFile("/cache/recovery/"
                                                                + recovery.getFile(type));
                                                        for (String command : recovery.getCommands(type))
                                                            recoveryFile.write(command, true);
                                                        RootUtils.runCommand("reboot recovery");
                                                    }
                                                }).show();
                                    }
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(getContext()).setTitle(getContext().getString(R.string.md5_mismatch))
                            .setMessage(getContext().getString(R.string.md5_mismatch_summary))
                            .setNeutralButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }
            }
        }.execute(path);
    }

    @Override
    public void setMargin() {
        int padding = getResources().getDimensionPixelSize(R.dimen.basecard_padding);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, padding, 0, 0);
        setLayoutParams(layoutParams);
    }

    @Override
    public void setRadius() {
        setRadius(0);
    }

    @Override
    public void setUpInnerLayout(View view) {
        nameText = (TextView) view.findViewById(R.id.name);
        descriptionText = (TextView) view.findViewById(R.id.description);
        changelogText = (TextView) view.findViewById(R.id.changelog_text);
        downloadButton = (FloatingActionButton) view.findViewById(R.id.download_button);
    }

    public static class DDownloadCard implements DAdapter.DView {

        private final Downloads.Download download;

        public DDownloadCard(Downloads.Download download) {
            this.download = download;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new DownloadCardView(viewGroup.getContext(), download)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }
    }

}
