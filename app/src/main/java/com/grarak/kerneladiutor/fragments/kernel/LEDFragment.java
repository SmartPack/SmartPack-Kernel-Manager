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
package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.led.LED;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.07.16.
 */
public class LEDFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.LED));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (LED.hasFade()) {
            fadeInit(items);
        }
        if (LED.hasIntensity()) {
            intensityInit(items);
        }
        if (LED.hasSpeed()) {
            speedInit(items);
        }
    }

    private void fadeInit(List<RecyclerViewItem> items) {
        SwitchView fade = new SwitchView();
        fade.setTitle(getString(R.string.fade));
        fade.setSummary(getString(R.string.fade_summary));
        fade.setChecked(LED.isFadeEnabled());
        fade.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                LED.enableFade(isChecked, getActivity());
            }
        });

        items.add(fade);
    }

    private void intensityInit(List<RecyclerViewItem> items) {
        SeekBarView intensity = new SeekBarView();
        intensity.setTitle(getString(R.string.intensity));
        intensity.setProgress(LED.getIntensity());
        intensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                LED.setIntensity(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(intensity);
    }

    private void speedInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.stock));
        list.add(getString(R.string.continuous_light));
        for (int i = 2; i < 21; i++) {
            list.add(String.valueOf(i));
        }

        SeekBarView speed = new SeekBarView();
        speed.setTitle(getString(R.string.speed));
        speed.setItems(list);
        speed.setProgress(LED.getSpeed());
        speed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                LED.setSpeed(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(speed);
    }

}
