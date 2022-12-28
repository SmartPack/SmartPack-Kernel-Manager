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

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 23, 2020
 */
public class TerminalActivity extends BaseActivity {

    private AppCompatAutoCompleteTextView mShellCommand;
    private AppCompatImageButton mRecent;
    private MaterialTextView mClearAll;
    private int i;
    private List<String> mResult = null, mLastCommand = null;
    private RecyclerView mRecyclerView;
    private ShellOutputAdapter mShellOutputAdapter = null;
    private final String whoAmI = RootUtils.runAndGetOutput("whoami");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mRecent = findViewById(R.id.recent_button);
        MaterialCardView mCard = findViewById(R.id.send_button);
        mShellCommand = findViewById(R.id.shell_command);
        mRecyclerView = findViewById(R.id.recycler_view);

        mResult = new ArrayList<>();
        mLastCommand = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mShellCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    mRecent.setImageDrawable(ViewUtils.getWhiteColoredIcon(R.drawable.ic_send, TerminalActivity.this));
                    if (s.toString().contains("\n")) {
                        if (!s.toString().endsWith("\n")) {
                            mShellCommand.setText(s.toString().replace("\n", ""));
                        }
                        runCommand(s.toString().replace("\n", ""));
                    }
                } else {
                    if (mLastCommand.size() > 0) {
                        mRecent.setImageDrawable(ViewUtils.getWhiteColoredIcon(R.drawable.ic_up, TerminalActivity.this));
                    }
                }
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());

        mCard.setOnClickListener(v -> {
            if (!mShellCommand.getText().toString().trim().isEmpty()) {
                runCommand(mShellCommand.getText().toString().trim());
            } else {
                PopupMenu popupMenu = new PopupMenu(this, mShellCommand);
                Menu menu = popupMenu.getMenu();
                if (mLastCommand.size() == 0) {
                    return;
                }
                for (i = 0; i < mLastCommand.size(); i++) {
                    menu.add(Menu.NONE, i, Menu.NONE, mLastCommand.get(i));
                }
                popupMenu.setOnMenuItemClickListener(item -> {
                    for (i = 0; i < mLastCommand.size(); i++) {
                        if (item.getItemId() == i) {
                            mShellCommand.setText(mLastCommand.get(i));
                            mShellCommand.setSelection(mShellCommand.getText().length());
                        }
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

        mClearAll = findViewById(R.id.clear_all);

        mClearAll.setOnClickListener(v -> clearAll());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (mResult != null && mResult.size() > 0 && !mResult.get(mResult.size() - 1).equals("Terminal: Finish")) {
                updateUI(mResult);
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    private void runCommand(String command) {
        mShellCommand.setText(null);
        mLastCommand.add(command);
        if (command.equals("clear")) {
            clearAll();
        } else if (command.equals("exit")) {
            onBackPressed();
        } else if (command.equals("su") || command.startsWith("su ")) {
            mResult.add("<font color=" + ViewUtils.getThemeAccentColor(TerminalActivity.this)
                    + ">" + whoAmI + "@" + Build.MODEL + "</font># <i>" + command);
            mResult.add("<b></b>");
            mResult.add("Terminal: Finish");
            updateUI(mResult);
        } else {
            new sExecutor() {
                @Override
                public void onPreExecute() {
                    mClearAll.setText(R.string.cancel);
                    mResult.add("<font color=" + ViewUtils.getThemeAccentColor(TerminalActivity.this)
                            + ">" + whoAmI + "@" + Build.MODEL + "</font># <i>" + command);
                }
                @Override
                public void doInBackground() {
                    RootUtils.runAndGetLiveOutput(command, mResult);
                    mResult.add("<b></b>");
                    mResult.add("Terminal: Finish");
                    try {
                        TimeUnit.MILLISECONDS.sleep(250);
                    } catch (InterruptedException ignored) {}
                }
                @Override
                public void onPostExecute() {
                    updateUI(mResult);
                    if (mLastCommand.size() > 0) {
                        mRecent.setImageDrawable(ViewUtils.getWhiteColoredIcon(R.drawable.ic_up, TerminalActivity.this));
                    }
                    mClearAll.setText(R.string.clear);
                }
            }.execute();
        }
    }

    private void updateUI(List<String> data) {
        List<String> mData = new ArrayList<>();
        try {
            for (String result : data) {
                if (!result.trim().isEmpty() && !result.equals("Terminal: Finish")) {
                    mData.add(result);
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }

        new sExecutor() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void doInBackground() {
                mShellOutputAdapter = new ShellOutputAdapter(mData);
            }

            @Override
            public void onPostExecute() {
                mRecyclerView.setAdapter(mShellOutputAdapter);
                mRecyclerView.scrollToPosition(mData.size() - 1);
            }
        }.execute();
    }

    private void clearAll() {
        if (mResult.size() > 0 && !mResult.get(mResult.size() - 1).equals("Terminal: Finish")) {
            RootUtils.closeSU();
        }
        mResult.clear();
        updateUI(mResult);
    }

    @Override
    public void onStart() {
        super.onStart();
        mShellCommand.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if (mResult != null && mResult.size() > 0 && !mResult.get(mResult.size() - 1).equals("Terminal: Finish")) return;
        super.onBackPressed();
    }

    public static class ShellOutputAdapter extends RecyclerView.Adapter<ShellOutputAdapter.ViewHolder> {

        private final List<String> data;

        public ShellOutputAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ShellOutputAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_shell_output, parent, false);
            return new ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull ShellOutputAdapter.ViewHolder holder, int position) {
            holder.mOutput.setText(Utils.htmlFrom(this.data.get(position)));
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final MaterialTextView mOutput;

            public ViewHolder(View view) {
                super(view);
                this.mOutput = view.findViewById(R.id.shell_output);
            }
        }

    }

}