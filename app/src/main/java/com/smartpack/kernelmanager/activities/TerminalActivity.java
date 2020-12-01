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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 23, 2020
 */

public class TerminalActivity extends BaseActivity {

    private AppCompatEditText mShellCommand;
    private AppCompatTextView mClearAll, mShellOutput;
    private boolean mRunning = false;
    private CharSequence mHistory = null;
    private int i;
    private List<String> mResult = null;
    private NestedScrollView mScrollView;
    private String whoAmI = RootUtils.runAndGetOutput("whoami");
    private StringBuilder mLastCommand = new StringBuilder();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mRecent = findViewById(R.id.recent_button);
        AppCompatImageButton mSave = findViewById(R.id.enter_button);
        mShellCommand = findViewById(R.id.shell_command);
        AppCompatTextView mShellCommandTitle = findViewById(R.id.shell_command_title);
        mShellOutput = findViewById(R.id.shell_output);
        mScrollView = findViewById(R.id.scroll_view);

        mShellCommandTitle.setText(whoAmI);

        mShellCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    runCommand();
                }
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());
        mRecent.setOnClickListener(v -> {
            String[] lines = mLastCommand.toString().split(",");
            PopupMenu popupMenu = new PopupMenu(this, mShellCommand);
            Menu menu = popupMenu.getMenu();
            if (mLastCommand.toString().isEmpty()) {
                return;
            }
            for (i = 0; i < lines.length; i++) {
                menu.add(Menu.NONE, i, Menu.NONE, lines[i]);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                for (i = 0; i < lines.length; i++) {
                    if (item.getItemId() == i) {
                        mShellCommand.setText(lines[i]);
                    }
                }
                return false;
            });
            popupMenu.show();
        });
        mSave.setOnClickListener(v -> runCommand());
        mClearAll = findViewById(R.id.clear_all);
        mClearAll.setOnClickListener(v -> {
            if (mRunning) {
                RootUtils.closeSU();
            } else {
                clearAll();
            }
        });

        refreshStatus();
    }

    public void refreshStatus() {
        new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(() -> {
                            if (mRunning) {
                                mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                                mClearAll.setText(R.string.cancel);
                                try {
                                    mShellOutput.setText(Utils.getOutput(mResult));
                                } catch (ConcurrentModificationException | NullPointerException ignored) {
                                }
                            } else {
                                mShellOutput.setTextIsSelectable(true);
                                mClearAll.setText(R.string.clear);
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

    @SuppressLint({"SetTextI18n", "StaticFieldLeak"})
    private void runCommand() {
        if (mShellCommand.getText() != null) {
            String[] array = Objects.requireNonNull(mShellCommand.getText()).toString().trim().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String s : array) {
                if (s != null && !s.isEmpty())
                    sb.append(" ").append(s);
            }
            final String[] mCommand = {sb.toString().replaceFirst(" ","")};
            mLastCommand.append(mCommand[0]).append(",");
            if (mCommand[0].endsWith("\n")) {
                mCommand[0] = mCommand[0].replace("\n","");
            }
            if (mShellCommand.getText() != null && !mCommand[0].isEmpty()) {
                if (mCommand[0].equals("clear")) {
                    clearAll();
                } else if (mCommand[0].equals("exit")) {
                    onBackPressed();
                } else if (mCommand[0].equals("su") || mCommand[0].contains("su ")) {
                    mShellCommand.setText(null);
                } else {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            mShellOutput.setTextIsSelectable(false);
                            mHistory = mShellOutput.getText();
                            mRunning = true;
                            mResult = new ArrayList<>();
                        }
                        @SuppressLint("WrongThread")
                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (mShellCommand.getText() != null && !mCommand[0].isEmpty()) {
                                mResult.add(whoAmI + ": " + mCommand[0]);
                                RootUtils.runAndGetLiveOutput(mCommand[0], mResult);
                                if (Utils.getOutput(mResult).equals(whoAmI + ": " + mCommand[0] + "\n")) {
                                    mResult.add(whoAmI + ": " + mCommand[0] + "\n" + mCommand[0]);
                                }
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            mShellCommand.setText(null);
                            mShellOutput.setText(Utils.getOutput(mResult) + "\n\n" + mHistory);
                            mHistory = null;
                            mRunning = false;
                        }
                    }.execute();
                }
            }
        }
    }

    private void clearAll() {
        mShellOutput.setText(null);
        mShellCommand.setText(null);
    }

    @Override
    public void onBackPressed() {
        if (mRunning) return;
        super.onBackPressed();
    }

}