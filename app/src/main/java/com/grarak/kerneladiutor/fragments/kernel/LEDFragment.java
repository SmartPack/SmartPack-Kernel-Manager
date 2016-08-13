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
import com.grarak.kerneladiutor.utils.kernel.led.Sec;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 31.07.16.
 */
public class LEDFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (LED.hasIntensity()) {
            intensityInit(items);
        }
        if (LED.hasSpeed()) {
            speedInit(items);
        }
        brightnessInit(items);
        delayInit(items);
        fadeInit(items);
        if (Sec.hasPattern()) {
            testInit(items);
        }
    }

    private void intensityInit(List<RecyclerViewItem> items) {
        SeekBarView intensity = new SeekBarView();
        intensity.setTitle(getString(R.string.intensity));
        intensity.setUnit("%");
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
        SeekBarView speed = new SeekBarView();
        speed.setTitle(getString(R.string.speed));
        speed.setItems(LED.getSpeedMenu(getActivity()));
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

    private void brightnessInit(List<RecyclerViewItem> items) {
        CardView brightnessCard = new CardView(getActivity());
        brightnessCard.setTitle(getString(R.string.brightness));

        if (Sec.hasHighpowerCurrent()) {
            SeekBarView highpowerCurrent = new SeekBarView();
            highpowerCurrent.setTitle(getString(R.string.bright_light_environment));
            highpowerCurrent.setSummary(getString(R.string.bright_light_environment_summary));
            highpowerCurrent.setUnit(getString(R.string.ma));
            highpowerCurrent.setMax(250);
            highpowerCurrent.setOffset(5);
            highpowerCurrent.setProgress(Sec.getHighpowerCurrent() / 5);
            highpowerCurrent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Sec.setHighpowerCurrent(position * 5, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            brightnessCard.addItem(highpowerCurrent);
        }

        if (Sec.hasLowpowerCurrent()) {
            SeekBarView lowpowerCurrent = new SeekBarView();
            lowpowerCurrent.setTitle(getString(R.string.low_light_environment));
            lowpowerCurrent.setSummary(getString(R.string.low_light_environment_summary));
            lowpowerCurrent.setUnit(getString(R.string.ma));
            lowpowerCurrent.setMax(250);
            lowpowerCurrent.setOffset(5);
            lowpowerCurrent.setProgress(Sec.getLowpowerCurrent() / 5);
            lowpowerCurrent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Sec.setLowpowerCurrent(position * 5, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            brightnessCard.addItem(lowpowerCurrent);
        }

        if (brightnessCard.size() > 0) {
            items.add(brightnessCard);
        }
    }

    private void delayInit(List<RecyclerViewItem> items) {
        CardView delayCard = new CardView(getActivity());
        delayCard.setTitle(getString(R.string.delay));

        if (Sec.hasNotificationDelayOn()) {
            SeekBarView notificationDelayOn = new SeekBarView();
            notificationDelayOn.setTitle(getString(R.string.on));
            notificationDelayOn.setUnit(getString(R.string.ms));
            notificationDelayOn.setMax(5000);
            notificationDelayOn.setOffset(100);
            notificationDelayOn.setProgress(Sec.getNotificationDelayOn() / 100);
            notificationDelayOn.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Sec.setNotificationDelayOn(position * 10, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            delayCard.addItem(notificationDelayOn);
        }

        if (Sec.hasNotificationDelayOff()) {
            SeekBarView notificationDelayOff = new SeekBarView();
            notificationDelayOff.setTitle(getString(R.string.off));
            notificationDelayOff.setUnit(getString(R.string.ms));
            notificationDelayOff.setMax(5000);
            notificationDelayOff.setOffset(100);
            notificationDelayOff.setProgress(Sec.getNotificationDelayOff() / 100);
            notificationDelayOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Sec.setNotificationDelayOff(position * 10, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            delayCard.addItem(notificationDelayOff);
        }

        if (delayCard.size() > 0) {
            items.add(delayCard);
        }
    }

    private void fadeInit(List<RecyclerViewItem> items) {
        if (LED.hasFade()) {
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

        CardView fadeCard = new CardView(getActivity());
        fadeCard.setTitle(getString(R.string.fade));

        if (Sec.hasNotificationRampControl()) {
            SwitchView notificationRampControl = new SwitchView();
            notificationRampControl.setTitle(getString(R.string.fade_ramp_control));
            notificationRampControl.setSummary(getString(R.string.fade_ramp_control_summary));
            notificationRampControl.setChecked(Sec.isNotificationRampControlEnabled());
            notificationRampControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Sec.enableNotificationRampControl(isChecked, getActivity());
                }
            });

            fadeCard.addItem(notificationRampControl);
        }

        if (Sec.hasNotificationRampUp()) {
            SeekBarView notificationRampUp = new SeekBarView();
            notificationRampUp.setTitle(getString(R.string.fade_in));
            notificationRampUp.setSummary(getString(R.string.fade_in_summary));
            notificationRampUp.setUnit(getString(R.string.ms));
            notificationRampUp.setMax(2000);
            notificationRampUp.setOffset(100);
            notificationRampUp.setProgress(Sec.getNotificationRampUp() / 100);
            notificationRampUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Sec.setNotificationRampUp(position * 100, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            fadeCard.addItem(notificationRampUp);
        }

        if (Sec.hasNotificationRampDown()) {
            SeekBarView notificationRampDown = new SeekBarView();
            notificationRampDown.setTitle(getString(R.string.fade_out));
            notificationRampDown.setSummary(getString(R.string.fade_out_summary));
            notificationRampDown.setUnit(getString(R.string.ms));
            notificationRampDown.setMax(2000);
            notificationRampDown.setOffset(100);
            notificationRampDown.setProgress(Sec.getNotificationRampDown() / 100);
            notificationRampDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Sec.setNotificationRampDown(position * 100, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            fadeCard.addItem(notificationRampDown);
        }

        if (fadeCard.size() > 0) {
            items.add(fadeCard);
        }
    }

    private void testInit(List<RecyclerViewItem> items) {
        SwitchView test = new SwitchView();
        test.setTitle(getString(R.string.test));
        test.setSummary(getString(R.string.led_test_summary));
        test.setChecked(Sec.isTestingPattern());
        test.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sec.testPattern(isChecked);
            }
        });

        items.add(test);
    }

}
