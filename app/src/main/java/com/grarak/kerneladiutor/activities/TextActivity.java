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
package com.grarak.kerneladiutor.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 14.04.16.
 */
public class TextActivity extends BaseActivity {

    public static final String MESSAGE_INTENT = "message_intent";
    public static final String SUMMARY_INTENT = "summary_intent";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        String message = getIntent().getStringExtra(MESSAGE_INTENT);
        final String url = getIntent().getStringExtra(SUMMARY_INTENT);

        if (message != null)
            ((TextView) findViewById(R.id.message_text)).setText(message);
        if (url != null)
            findViewById(R.id.help_fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.launchUrl(url, TextActivity.this);
                }
            });
    }

}
