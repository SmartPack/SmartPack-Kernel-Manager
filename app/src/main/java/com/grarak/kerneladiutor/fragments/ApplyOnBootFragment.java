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
package com.grarak.kerneladiutor.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.tools.profile.ProfileActivity;
import com.grarak.kerneladiutor.utils.Prefs;

/**
 * Created by willi on 03.05.16.
 */
public class ApplyOnBootFragment extends BaseFragment {

    public static final String CPU = "cpu_onboot";
    public static final String CPU_VOLTAGE = "cpuvoltage_onboot";
    public static final String CPU_HOTPLUG = "cpuhotplug_onboot";
    public static final String THERMAL = "thermal_onboot";
    public static final String GPU = "gpu_onboot";
    public static final String SCREEN = "screen_onboot";
    public static final String WAKE = "wake_onboot";
    public static final String SOUND = "sound_onboot";
    public static final String BATTERY = "battery_onboot";
    public static final String LED = "led_onboot";
    public static final String IO = "io_onboot";
    public static final String KSM = "ksm_onboot";
    public static final String LMK = "lmk_onboot";
    public static final String VM = "vm_onboot";
    public static final String ENTROPY = "entropy_onboot";
    public static final String MISC = "misc_onboot";

    public static ApplyOnBootFragment newInstance(String category) {
        Bundle args = new Bundle();
        args.putString("category", category);
        ApplyOnBootFragment fragment = new ApplyOnBootFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean retainInstance() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof ProfileActivity) {
            View rootView = inflater.inflate(R.layout.fragment_description, container, false);

            TextView title = (TextView) rootView.findViewById(R.id.title);
            TextView summary = (TextView) rootView.findViewById(R.id.summary);

            title.setText(getString(R.string.apply_on_boot));
            summary.setText(getString(R.string.apply_on_boot_not_available));

            return rootView;
        } else {
            View rootView = inflater.inflate(R.layout.fragment_apply_on_boot, container, false);

            final String category = getArguments().getString("category");
            SwitchCompat switcher = (SwitchCompat) rootView.findViewById(R.id.switcher);
            switcher.setChecked(Prefs.getBoolean(category, false, getActivity()));
            switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Prefs.saveBoolean(category, isChecked, getActivity());
                }
            });
            return rootView;
        }
    }

}
