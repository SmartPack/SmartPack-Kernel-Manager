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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 09.07.15.
 */
public class EditTextActivity extends BaseActivity {

    public static final String NAME_ARG = "name";
    public static final String TEXT_ARG = "text";

    private String originalText;
    private AppCompatEditText editText;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editText = (AppCompatEditText) findViewById(R.id.edittext);
        editText.setTextColor(getResources().getColor(Utils.DARKTHEME ? R.color.white : R.color.black));
        if ((originalText = getIntent().getExtras().getString(TEXT_ARG)) != null)
            editText.setText(originalText);

        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) {
            if ((name = getIntent().getExtras().getString(NAME_ARG)) != null)
                actionBar.setTitle(name);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public int getParentViewId() {
        return R.layout.activity_edittext;
    }

    @Override
    public View getParentView() {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edittext_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                Bundle args = new Bundle();
                args.putString("name", name);
                if (originalText == null || !originalText.equals(editText.getText().toString()))
                    args.putString("text", editText.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(args);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
