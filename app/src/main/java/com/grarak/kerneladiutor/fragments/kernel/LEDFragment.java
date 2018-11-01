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
import com.grarak.kerneladiutor.utils.Device;
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

    private LED mLED;

    @Override
    protected void init() {
        super.init();

        mLED = LED.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mLED.hasdisplaybacklight()) {
            displaybacklightInit(items);
        }
        if (mLED.hasBacklightMin()) {
            BacklightMinInit(items);
        }
        if (mLED.hascharginglight()) {
            charginglightInit(items);
        }
        if (mLED.hasLEDFade()) {
            LEDFadeInit(items);
        }
        if (mLED.hasIntensity()) {
            intensityInit(items);
        }
        if (mLED.hasSpeed()) {
            speedInit(items);
        }
        brightnessInit(items);
        delayInit(items);
        fadeInit(items);
    }

    private void intensityInit(List<RecyclerViewItem> items) {
        SeekBarView intensity = new SeekBarView();
        intensity.setTitle(getString(R.string.intensity));
        intensity.setUnit("%");
        intensity.setProgress(mLED.getIntensity());
        intensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mLED.setIntensity(position, getActivity());
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
        speed.setItems(mLED.getSpeedMenu(getActivity()));
        speed.setProgress(mLED.getSpeed());
        speed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mLED.setSpeed(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(speed);
    }

    private void displaybacklightInit(List<RecyclerViewItem> items) {
	SeekBarView displaybacklight = new SeekBarView();
	displaybacklight.setTitle(getString(R.string.display_backlight));
	if ((mLED.getdisplaybacklight() >= 256) && (mLED.getdisplaybacklight() <= 1275)) {
            // Increase maximum range (Max: 1275; Offset: 25)
            displaybacklight.setMax(1275);
            displaybacklight.setOffset(25);
            displaybacklight.setProgress(mLED.getdisplaybacklight() / 25 );
            displaybacklight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mLED.setdisplaybacklight((position * 25), getActivity());
		}

		        @Override
		        public void onMove(SeekBarView seekBarView, int position, String value) {
		        }
            });
	} else {
	// Set normal range (Max: 255; Offset: 5)
            displaybacklight.setMax(255);
            displaybacklight.setOffset(5);
            displaybacklight.setProgress(mLED.getdisplaybacklight() / 5 );
            displaybacklight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mLED.setdisplaybacklight((position * 5), getActivity());
		}

		        @Override
		        public void onMove(SeekBarView seekBarView, int position, String value) {
		        }
            });
	}

	items.add(displaybacklight);
    }

    private void BacklightMinInit(List<RecyclerViewItem> items) {
	SeekBarView BacklightMin = new SeekBarView();
	BacklightMin.setTitle(getString(R.string.backlight_min));
	if ((mLED.getdisplaybacklight() >= 256) && (mLED.getdisplaybacklight() <= 1275)) {
            // Based on the current Maximum Backlight of the display, increase maximum range, if necessary (Max: 1275; Offset: 25)
            BacklightMin.setMax(1275);
            BacklightMin.setOffset(25);
            BacklightMin.setProgress(mLED.getBacklightMin() / 25 );
            BacklightMin.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mLED.setBacklightMin((position * 25), getActivity());
		}
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });
            items.add(BacklightMin);
	} else {
            // Set normal range (Max: 255; Offset: 5)
            BacklightMin.setMax(255);
            BacklightMin.setOffset(5);
            BacklightMin.setProgress(mLED.getBacklightMin() / 5 );
            BacklightMin.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mLED.setBacklightMin((position * 5), getActivity());
		}
		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });
            items.add(BacklightMin);
	}
    }

    private void charginglightInit(List<RecyclerViewItem> items) {
	SeekBarView charginglight = new SeekBarView();
	charginglight.setTitle(getString(R.string.charging_light));
	charginglight.setMax(255);
	charginglight.setOffset(5);
	charginglight.setProgress(mLED.getcharginglight() / 5 );
	charginglight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
		mLED.setcharginglight((position * 5), getActivity());
            }
            @Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
            }
	});
	items.add(charginglight);
    }

    private void LEDFadeInit(List<RecyclerViewItem> items) {
	SwitchView LEDFade = new SwitchView();
	LEDFade.setTitle(getString(R.string.fade));
	LEDFade.setSummary(getString(R.string.fade_summary));
	LEDFade.setChecked(mLED.isLEDFadeEnabled());
	LEDFade.addOnSwitchListener(new SwitchView.OnSwitchListener() {
	@Override
	public void onChanged(SwitchView switchView, boolean isChecked) {
		mLED.enableLEDFade(isChecked, getActivity());
            }
	});

	items.add(LEDFade);
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
        if (mLED.hasFade()) {
            SwitchView fade = new SwitchView();
            fade.setTitle(getString(R.string.fade));
            fade.setSummary(getString(R.string.fade_summary));
            fade.setChecked(mLED.isFadeEnabled());
            fade.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mLED.enableFade(isChecked, getActivity());
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

}
