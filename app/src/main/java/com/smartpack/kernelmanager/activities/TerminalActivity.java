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
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.root.RootUtils;

import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 23, 2020
 */

public class TerminalActivity extends BaseActivity {

    private AppCompatEditText mShellCommand;
    private AppCompatEditText mShellOutput;
    private AppCompatTextView mProgressMessage;
    private int i;
    private LinearLayout mProgressLayout;
    private static String whoAmI = RootUtils.runAndGetOutput("whoami");
    private StringBuilder mLastCommand = new StringBuilder();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        mProgressLayout = findViewById(R.id.progress_layout);
        mProgressMessage = findViewById(R.id.progress_message);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mRecent = findViewById(R.id.recent_button);
        AppCompatImageButton mSave = findViewById(R.id.enter_button);
        mBack.setOnClickListener(v -> onBackPressed());
        mShellCommand = findViewById(R.id.shell_command);
        AppCompatTextView mShellCommandTitle = findViewById(R.id.shell_command_title);
        mShellOutput = findViewById(R.id.shell_output);
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
        AppCompatTextView mClearAll = findViewById(R.id.clear_all);
        mClearAll.setOnClickListener(v -> {
            clearAll();
        });
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
                        private String mResult;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            mProgressMessage.setText(getString(R.string.executing) + "...");
                            mProgressMessage.setVisibility(View.VISIBLE);
                            mProgressLayout.setVisibility(View.VISIBLE);
                        }
                        @SuppressLint("WrongThread")
                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (mShellCommand.getText() != null && !mCommand[0].isEmpty()) {
                                mResult = whoAmI + ": " + mCommand[0] + "\n" + RootUtils.runAndGetError(mCommand[0]);
                                if (mResult.equals(whoAmI + ": " + mCommand[0] + "\n")) {
                                    mResult = whoAmI + ": " + mCommand[0] + "\n" + mCommand[0];
                                }
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            mProgressMessage.setVisibility(View.GONE);
                            mProgressLayout.setVisibility(View.GONE);
                            mShellCommand.setText(null);
                            mShellOutput.setText(mResult + "\n\n" + mShellOutput.getText());
                            mShellOutput.setVisibility(View.VISIBLE);
                        }
                    }.execute();
                }
            }
        }
    }

    private void clearAll() {
        mShellOutput.setText(null);
        mShellOutput.setVisibility(View.GONE);
        mShellCommand.setText(null);
    }

}