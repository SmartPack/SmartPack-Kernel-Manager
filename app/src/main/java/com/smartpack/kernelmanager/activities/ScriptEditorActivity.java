/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
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
package com.smartpack.kernelmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.tools.Scripts;

/**
 * Created by willi on 01.07.16.
 */
public class ScriptEditorActivity extends BaseActivity {

    public static final String TITLE_INTENT = "title";
    public static final String TEXT_INTENT = "text";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editscript);

        AppCompatEditText mEditText = findViewById(R.id.edittext);
        AppCompatImageButton mBack = findViewById(R.id.back);
        AppCompatImageButton mSave = findViewById(R.id.save);
        MaterialTextView mTitle = findViewById(R.id.title);

        String title = getIntent().getStringExtra(TITLE_INTENT);
        if (title != null) {
            mTitle.setText(title);
        }

        CharSequence text = getIntent().getCharSequenceExtra(TEXT_INTENT);
        if (text != null) {
            mEditText.append(text);
        }

        mEditText.requestFocus();

        mBack.setOnClickListener(v -> finish());

        mSave.setOnClickListener(v -> {
            if (mEditText.getText() == null || mEditText.getText().toString().trim().isEmpty()) {
                return;
            }
            if (Utils.existFile(Scripts.scriptExistsCheck(mTitle.getText().toString()))) {
                Scripts.write(mTitle.getText().toString(), mEditText.getText().toString().trim());
            } else {
                Intent intent = new Intent();
                intent.putExtra(TEXT_INTENT, mEditText.getText());
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        });
    }

}
