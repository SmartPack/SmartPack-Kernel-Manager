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

package com.grarak.kerneladiutor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import com.grarak.kerneladiutor.utils.Utils;

public class TextActivity extends AppCompatActivity {

    public static final String ARG_TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.DARKTHEME = Utils.getBoolean("darktheme", false, this))
            super.setTheme(R.style.AppThemeActionBarDark);

        TextView text = new TextView(this);
        setContentView(text);

        text.setTextSize(20);
        text.setGravity(Gravity.CENTER);
        text.setText(getIntent().getExtras().getString(ARG_TEXT));
    }

}
