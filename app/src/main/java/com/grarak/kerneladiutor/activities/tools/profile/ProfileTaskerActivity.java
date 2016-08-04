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
package com.grarak.kerneladiutor.activities.tools.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.fragments.tools.ProfileFragment;
import com.grarak.kerneladiutor.services.profile.Tasker;

import java.util.List;

/**
 * Created by willi on 21.07.16.
 */
public class ProfileTaskerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initToolBar();

        getSupportActionBar().setTitle(getString(R.string.profile_select));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(),
                "profile_fragment").commit();
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("create_fragment");
        if (fragment == null) {
            return ProfileFragment.newInstance(true);
        }
        return fragment;
    }

    public void finish(String name, List<String> commands) {
        StringBuilder command = new StringBuilder();
        command.append(name).append(Tasker.DIVIDER);
        for (String c : commands) {
            if (command.length() != 0) {
                command.append(Tasker.DIVIDER);
            }
            command.append(c);
        }

        Intent resultIntent = new Intent();
        Bundle resultBundle = new Bundle();
        resultBundle.putInt(Tasker.BUNDLE_EXTRA_INT_VERSION_CODE, BuildConfig.VERSION_CODE);
        resultBundle.putString(Tasker.BUNDLE_EXTRA_STRING_MESSAGE, command.toString());
        resultIntent.putExtra(Tasker.EXTRA_BUNDLE, resultBundle);
        resultIntent.putExtra(Tasker.EXTRA_STRING_BLURB, name);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void finish() {
        getSupportFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

}
