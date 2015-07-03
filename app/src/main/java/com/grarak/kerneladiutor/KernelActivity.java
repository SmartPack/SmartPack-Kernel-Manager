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

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.grarak.kerneladiutor.fragments.tools.download.ParentFragment;
import com.grarak.kerneladiutor.utils.Downloads;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 20.06.15.
 */
public class KernelActivity extends AppCompatActivity {

    public static final String KERNEL_JSON_ARG = "kernel_json";
    private View logoContainer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Downloads.KernelContent kernelContent =
                new Downloads.KernelContent(getIntent().getExtras().getString(KERNEL_JSON_ARG));

        if (Utils.DARKTHEME = Utils.getBoolean("darktheme", false, this))
            super.setTheme(R.style.AppThemeDark);
        setContentView(R.layout.activity_kernel);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Utils.DARKTHEME) {
            toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Dark);
            findViewById(R.id.container).setBackgroundColor(getResources().getColor(R.color.black));
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            String name;
            if ((name = kernelContent.getName()) != null)
                actionBar.setTitle(Html.fromHtml(name).toString());
        }

        logoContainer = findViewById(R.id.logo_container);
        ImageView logoView = (ImageView) findViewById(R.id.logo);
        Utils.loadImagefromUrl(kernelContent.getLogo(), logoView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.color_primary_dark));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                ParentFragment.newInstance(kernelContent)).commitAllowingStateLoss();
    }

    public View getLogoContainer() {
        return logoContainer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
