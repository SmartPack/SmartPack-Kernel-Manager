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
package com.smartpack.kernelmanager.fragments.kernel;

import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.led.LED;
import com.smartpack.kernelmanager.utils.kernel.led.Sec;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 31.07.16.
 */
public class DisplayLEDFragment extends RecyclerViewFragment {

    private LED mLED;

    @Override
    protected void init() {
        super.init();

        mLED = LED.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mLED.hasBacklightMax() || mLED.hasBacklightMin() || mLED.hascharginglight() || mLED.hasIntensity()
                || mLED.hasLEDBrightnessB() || mLED.hasLEDBrightnessG() || mLED.hasLEDBrightnessR()
                || mLED.hasLEDBrightnessW() || mLED.hasSpeed() || mLED.hasWhiteLED() || mLED.hasYellowLED()
                || Sec.hasNotificationRampDown() || Sec.hasNotificationRampUp() || Sec.hasNotificationRampControl()
                || mLED.hasFade() || Sec.hasNotificationDelayOff() || Sec.hasNotificationDelayOn()
                || Sec.hasLowpowerCurrent() || Sec.hasHighpowerCurrent()) {
            displayandledInit(items);
        }
    }

    private void displayandledInit(List<RecyclerViewItem> items) {
        CardView DisplyAndLED = new CardView(getActivity());
        DisplyAndLED.setTitle(getString(R.string.led));

	if (mLED.hasBacklightMax()) {
            GenericSelectView backlightMax = new GenericSelectView();
            backlightMax.setTitle(getString(R.string.display_backlight));
            backlightMax.setSummary("Max");
            backlightMax.setValue(mLED.getBacklightMax());
            backlightMax.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightMax.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mLED.setBacklightMax(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    backlightMax.setValue(mLED.getBacklightMax());
		    },
	    	500);
                }
            });

            DisplyAndLED.addItem(backlightMax);
	}

	if (mLED.hasBacklightMin()) {
            GenericSelectView BacklightMin = new GenericSelectView();
	    if (!mLED.hasBacklightMax()) {
            	BacklightMin.setTitle(getString(R.string.display_backlight));
	    }
            BacklightMin.setSummary("Min");
            BacklightMin.setValue(mLED.getBacklightMin());
            BacklightMin.setInputType(InputType.TYPE_CLASS_NUMBER);
            BacklightMin.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mLED.setBacklightMin(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    BacklightMin.setValue(mLED.getBacklightMin());
		    },
	    	500);
                }
            });

            DisplyAndLED.addItem(BacklightMin);
	}

	if (mLED.hasIntensity()) {
            SeekBarView intensity = new SeekBarView();
            intensity.setTitle(getString(R.string.led_notification));
            intensity.setSummary(getString(R.string.led_intensity));
            intensity.setUnit("%");
            intensity.setProgress(mLED.getIntensity());
            intensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		    mLED.setIntensity(position, getActivity());
		    getHandler().postDelayed(() -> {
		    intensity.setProgress(mLED.getIntensity());
		    },
	    	500);
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            DisplyAndLED.addItem(intensity);
	}

	if (mLED.hascharginglight()) {
            SeekBarView charginglight = new SeekBarView();
            charginglight.setTitle(getString(R.string.led_notification));
            charginglight.setSummary(getString(R.string.led_intensity));
            charginglight.setMax(255);
            charginglight.setProgress(mLED.getcharginglight());
            charginglight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		    mLED.setcharginglight((position), getActivity());
		    getHandler().postDelayed(() -> {
		    charginglight.setProgress(mLED.getcharginglight());
		    },
	    	500);
		}
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });
            DisplyAndLED.addItem(charginglight);
	}

	if (mLED.hasSpeed()) {
            SeekBarView speed = new SeekBarView();
	    if (mLED.hasIntensity() || mLED.hascharginglight()) {
	    // We do not need a title in this case
	    } else {
            	speed.setTitle(getString(R.string.led_notification));
	    }
            speed.setSummary(getString(R.string.led_speed));
            speed.setItems(mLED.getSpeedMenu(getActivity()));
            speed.setProgress(mLED.getSpeed());
            speed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		    mLED.setSpeed(position, getActivity());
		    getHandler().postDelayed(() -> {
		    speed.setProgress(mLED.getSpeed());
		    },
	    	500);
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            DisplyAndLED.addItem(speed);
	}

        if (mLED.hasFade()) {
            SwitchView fade = new SwitchView();
	    if (mLED.hasIntensity() || mLED.hascharginglight()) {
	    // We do not need a title in this case
	    } else {
            	fade.setTitle(getString(R.string.led_notification));
	    }
            fade.setSummary(getString(R.string.fade));
            fade.setChecked(mLED.isFadeEnabled());
            fade.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mLED.enableFade(isChecked, getActivity());
		    getHandler().postDelayed(() -> {
		    fade.setChecked(mLED.isFadeEnabled());
		    },
	    	500);
                }
            });

            DisplyAndLED.addItem(fade);
        }

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
		    getHandler().postDelayed(() -> {
		    highpowerCurrent.setProgress(Sec.getHighpowerCurrent() / 5);
		    },
	    	500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(highpowerCurrent);
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
		    getHandler().postDelayed(() -> {
		    lowpowerCurrent.setProgress(Sec.getLowpowerCurrent() / 5);
		    },
	    	500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(lowpowerCurrent);
        }

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
		    getHandler().postDelayed(() -> {
		    notificationDelayOn.setProgress(Sec.getNotificationDelayOn() / 100);
		    },
	    	500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(notificationDelayOn);
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
		    getHandler().postDelayed(() -> {
		    notificationDelayOff.setProgress(Sec.getNotificationDelayOff() / 100);
		    },
	    	500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(notificationDelayOff);
        }

        if (Sec.hasNotificationRampControl()) {
            SwitchView notificationRampControl = new SwitchView();
            notificationRampControl.setTitle(getString(R.string.fade_ramp_control));
            notificationRampControl.setSummary(getString(R.string.fade_ramp_control_summary));
            notificationRampControl.setChecked(Sec.isNotificationRampControlEnabled());
            notificationRampControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Sec.enableNotificationRampControl(isChecked, getActivity());
		    getHandler().postDelayed(() -> {
		    notificationRampControl.setChecked(Sec.isNotificationRampControlEnabled());
		    },
	    	500);
                }
            });

            DisplyAndLED.addItem(notificationRampControl);
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
		    getHandler().postDelayed(() -> {
		    notificationRampUp.setProgress(Sec.getNotificationRampUp() / 100);
		    },
	    	500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(notificationRampUp);
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
		    getHandler().postDelayed(() -> {
		    notificationRampDown.setProgress(Sec.getNotificationRampDown() / 100);
		    },
	    	500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(notificationRampDown);
        }

        if (mLED.hasLEDBrightnessR()) {
            SeekBarView maxBrightness = new SeekBarView();
            maxBrightness.setTitle(getString(R.string.led_brightness));
            maxBrightness.setSummary(getString(R.string.red));
            maxBrightness.setMax(255);
            maxBrightness.setProgress(mLED.getLEDBrightnessR());
            maxBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mLED.setLEDBrightnessR(position, getActivity());
                    getHandler().postDelayed(() -> {
                                maxBrightness.setProgress(mLED.getLEDBrightnessR());
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(maxBrightness);
        }

        if (mLED.hasLEDBrightnessG()) {
            SeekBarView maxBrightness = new SeekBarView();
            maxBrightness.setSummary(getString(R.string.green));
            maxBrightness.setMax(255);
            maxBrightness.setProgress(mLED.getLEDBrightnessG());
            maxBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mLED.setLEDBrightnessG(position, getActivity());
                    getHandler().postDelayed(() -> {
                                maxBrightness.setProgress(mLED.getLEDBrightnessG());
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(maxBrightness);
        }

        if (mLED.hasLEDBrightnessB()) {
            SeekBarView maxBrightness = new SeekBarView();
            maxBrightness.setSummary(getString(R.string.blue));
            maxBrightness.setMax(255);
            maxBrightness.setProgress(mLED.getLEDBrightnessB());
            maxBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mLED.setLEDBrightnessB(position, getActivity());
                    getHandler().postDelayed(() -> {
                                maxBrightness.setProgress(mLED.getLEDBrightnessB());
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(maxBrightness);
        }

        if (mLED.hasLEDBrightnessW()) {
            SeekBarView maxBrightness = new SeekBarView();
            maxBrightness.setSummary(getString(R.string.white));
            maxBrightness.setMax(255);
            maxBrightness.setProgress(mLED.getLEDBrightnessW());
            maxBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mLED.setLEDBrightnessW(position, getActivity());
                    getHandler().postDelayed(() -> {
                                maxBrightness.setProgress(mLED.getLEDBrightnessW());
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(maxBrightness);
        }

        if (mLED.hasWhiteLED()) {
            SeekBarView whiteLEDBrightness = new SeekBarView();
            whiteLEDBrightness.setTitle(getString(R.string.flash_led));
            whiteLEDBrightness.setSummary(getString(R.string.white));
            whiteLEDBrightness.setUnit(" %");
            whiteLEDBrightness.setProgress(mLED.getWhiteLED() / 2);
            whiteLEDBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mLED.setWhiteLED((position * 2), getActivity());
                    getHandler().postDelayed(() -> {
                                whiteLEDBrightness.setProgress(mLED.getWhiteLED() / 2);
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(whiteLEDBrightness);
        }

        if (mLED.hasYellowLED()) {
            SeekBarView yellowLEDBrightness = new SeekBarView();
            if (!mLED.hasWhiteLED()) {
                yellowLEDBrightness.setTitle(getString(R.string.flash_led));
            }
            yellowLEDBrightness.setSummary(getString(R.string.yellow));
            yellowLEDBrightness.setUnit(" %");
            yellowLEDBrightness.setProgress(mLED.getYellowLED() / 2);
            yellowLEDBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mLED.setYellowLED((position * 2), getActivity());
                    getHandler().postDelayed(() -> {
                                yellowLEDBrightness.setProgress(mLED.getYellowLED() / 2);
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            DisplyAndLED.addItem(yellowLEDBrightness);
        }

        if (DisplyAndLED.size() > 0) {
            items.add(DisplyAndLED);
        }
    }

}
