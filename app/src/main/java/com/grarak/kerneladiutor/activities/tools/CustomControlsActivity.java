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
package com.grarak.kerneladiutor.activities.tools;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.fragments.tools.customcontrols.CreateFragment;
import com.grarak.kerneladiutor.utils.tools.customcontrols.Items;

import java.util.ArrayList;

/**
 * Created by willi on 30.06.16.
 */
public class CustomControlsActivity extends BaseActivity {

    public static final String SETTINGS_INTENT = "settings";
    public static final String RESULT_INTENT = "result";

    private ArrayList<Items.Setting> mSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initToolBar();

        mSettings = getIntent().getParcelableArrayListExtra(SETTINGS_INTENT);
        for (Items.Setting setting : mSettings) {
            if (setting.getId().equals("id")) {
                getSupportActionBar().setTitle(getString(Items.Control.getControl(setting.getName(null)
                        .toString()).getRes()));
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(),
                "create_fragment").commit();
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("create_fragment");
        if (fragment == null) {
            return CreateFragment.newInstance(mSettings);
        }
        return fragment;
    }

    @Override
    public void finish() {
        getSupportFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

}
