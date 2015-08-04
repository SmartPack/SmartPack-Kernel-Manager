/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
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

package com.grarak.kerneladiutor.tasker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.grarak.kerneladiutor.BaseActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.tools.ProfileFragment;

import java.util.List;

/**
 * Created by willi on 28.07.15.
 */
public class AddProfileActivity extends BaseActivity {

    public static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    private static final String EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";
    public static final String DIVIDER = "wkefnewnfewp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        Bundle localeBundle = getIntent().getBundleExtra(EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setTitle(getString(R.string.profile));
            if (PluginBundleManager.isBundleValid(localeBundle)) {
                String message;
                if ((message = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE)) != null)
                    actionBar.setSubtitle(message.split(DIVIDER)[0]);
            }
        }

        setFragment(R.id.content_frame, ProfileFragment.newInstance());
    }

    @Override
    public View getParentView() {
        return null;
    }

    @Override
    public int getParentViewId() {
        return R.layout.activity_fragment;
    }

    @Override
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    public void finish(String name, List<String> commands) {
        StringBuilder command = new StringBuilder();
        command.append(name).append(DIVIDER);
        boolean first = true;
        for (String c : commands) {
            if (first) first = false;
            else command.append(DIVIDER);
            command.append(c);
        }

        Intent resultIntent = new Intent();
        Bundle resultBundle = PluginBundleManager.generateBundle(command.toString());
        resultIntent.putExtra(EXTRA_BUNDLE, resultBundle);
        resultIntent.putExtra(EXTRA_STRING_BLURB, generateBlurb(name));

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private String generateBlurb(String message) {
        final int maxBlurbLength = getResources().getInteger(R.integer.twofortyfouram_locale_maximum_blurb_length);
        if (message.length() > maxBlurbLength) return message.substring(0, maxBlurbLength);
        return message;
    }

}
