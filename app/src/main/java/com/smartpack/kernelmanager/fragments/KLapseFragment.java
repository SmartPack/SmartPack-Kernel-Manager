/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.fragments;

import android.text.InputType;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import com.smartpack.kernelmanager.utils.KLapse;

import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 29, 2018
 */

public class KLapseFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (KLapse.supported()) {
            klapsInit(items);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    private void klapsInit(List<RecyclerViewItem> items) {
        CardView klapseCard = new CardView(getActivity());
        klapseCard.setTitle(getString(R.string.klapse));

        int nightR = KLapse.getklapseRed();
        int nightG = KLapse.getklapseGreen();
        int nightB = KLapse.getklapseBlue();

        if (KLapse.hasEnable()) {
            SelectView enable = new SelectView();
            enable.setSummary(getString(R.string.klapse_summary));
            enable.setItems(KLapse.enable(getActivity()));
            enable.setItem(KLapse.getklapseEnable());
            enable.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    KLapse.setklapseEnable(position, getActivity());
                    getHandler().postDelayed(() -> {
                                KLapse.setklapseRed((nightR), getActivity());
                                KLapse.setklapseGreen((nightG), getActivity());
                                KLapse.setklapseBlue((nightB), getActivity());
                            },
                            100);
                }
            });

            klapseCard.addItem(enable);
        }

        if (KLapse.hasklapseStart() || KLapse.hasklapseStartMin()) {
            SeekBarView klapseStart = new SeekBarView();
            klapseStart.setTitle((KLapse.hasklapseStart()) ? getString(R.string.night_mode_schedule) + (" (Hour)")
                    : getString(R.string.night_mode_schedule) + (" (Minute)"));
            klapseStart.setSummary(getString(R.string.start_time));
            if (KLapse.hasklapseStart()) {
                klapseStart.setMax(23);
		klapseStart.setProgress(KLapse.getklapseStart());
            } else {
                klapseStart.setMax(1440);
		klapseStart.setOffset(15);
		klapseStart.setProgress(KLapse.getklapseStart() / 15);
            }
            klapseStart.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
		    if (KLapse.hasklapseStart()) {
                    	KLapse.setklapseStart((position), getActivity());
		    } else {
                    	KLapse.setklapseStart((position * 15), getActivity());
		    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(klapseStart);
        }

        if (KLapse.hasklapseStop() || KLapse.hasklapseStopMin()) {
            SeekBarView klapseStop = new SeekBarView();
            klapseStop.setSummary(getString(R.string.end_time));
            if (KLapse.hasklapseStop()) {
                klapseStop.setMax(23);
		klapseStop.setProgress(KLapse.getklapseStop());
            } else {
                klapseStop.setMax(1440);
		klapseStop.setOffset(15);
		klapseStop.setProgress(KLapse.getklapseStop() / 15);
            }
            klapseStop.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
		    if (KLapse.hasklapseStop()) {
                    	KLapse.setklapseStop((position), getActivity());
		    } else {
                    	KLapse.setklapseStop((position * 15), getActivity());
		    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(klapseStop);
        }

        if (KLapse.hasScalingRate()) {
            GenericSelectView scalingRate = new GenericSelectView();
            scalingRate.setTitle(getString(R.string.scaling_rate));
            scalingRate.setSummary(getString(R.string.scaling_rate_summary));
            scalingRate.setValue(KLapse.getScalingRate());
            scalingRate.setInputType(InputType.TYPE_CLASS_NUMBER);
            scalingRate.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setScalingRate(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(scalingRate);
        }

        if (KLapse.hasFadeBackMinutes()) {
            GenericSelectView fadebackMinutes = new GenericSelectView();
            fadebackMinutes.setTitle(getString(R.string.fadeback_time));
            fadebackMinutes.setSummary(getString(R.string.fadeback_time_summary));
            fadebackMinutes.setValue(KLapse.getFadeBackMinutes());
            fadebackMinutes.setInputType(InputType.TYPE_CLASS_NUMBER);
            fadebackMinutes.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setFadeBackMinutes(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(fadebackMinutes);
        }

        if (KLapse.hasklapseRed()) {
            SeekBarView targetRed = new SeekBarView();
            targetRed.setTitle(getString(R.string.nightmode_rgb));
            targetRed.setSummary(getString(R.string.red));
            targetRed.setMax(256);
            targetRed.setProgress(KLapse.getklapseRed());
            targetRed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setklapseRed((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(targetRed);
        }

        if (KLapse.hasklapseGreen()) {
            SeekBarView targetGreen = new SeekBarView();
            targetGreen.setSummary(getString(R.string.green));
            targetGreen.setMax(256);
            targetGreen.setProgress(KLapse.getklapseGreen());
            targetGreen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setklapseGreen((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(targetGreen);
        }

        if (KLapse.hasklapseBlue()) {
            SeekBarView targetBlue = new SeekBarView();
            targetBlue.setSummary(getString(R.string.blue));
            targetBlue.setMax(256);
            targetBlue.setProgress(KLapse.getklapseBlue());
            targetBlue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setklapseBlue((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(targetBlue);
        }

        if (KLapse.hasDayTimeRed()) {
            SeekBarView dayTimeRed = new SeekBarView();
            dayTimeRed.setTitle(getString(R.string.daytime_rgb));
            dayTimeRed.setSummary(getString(R.string.red));
            dayTimeRed.setMax(256);
            dayTimeRed.setProgress(KLapse.getDayTimeRed());
            dayTimeRed.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setDayTimeRed((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(dayTimeRed);
        }

        if (KLapse.hasDayTimeGreen()) {
            SeekBarView dayTimeGreen = new SeekBarView();
            dayTimeGreen.setSummary(getString(R.string.green));
            dayTimeGreen.setMax(256);
            dayTimeGreen.setProgress(KLapse.getDayTimeGreen());
            dayTimeGreen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setDayTimeGreen((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(dayTimeGreen);
        }

        if (KLapse.hasDayTimeBlue()) {
            SeekBarView dayTimeBlue = new SeekBarView();
            dayTimeBlue.setSummary(getString(R.string.blue));
            dayTimeBlue.setMax(256);
            dayTimeBlue.setProgress(KLapse.getDayTimeBlue());
            dayTimeBlue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setDayTimeBlue((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(dayTimeBlue);
        }

        if (KLapse.hasPulseFreq()) {
            GenericSelectView pulseFreq = new GenericSelectView();
            pulseFreq.setTitle(getString(R.string.pulse_freq));
            pulseFreq.setSummary(getString(R.string.pulse_freq_summary));
            pulseFreq.setValue(KLapse.getPulseFreq());
            pulseFreq.setInputType(InputType.TYPE_CLASS_NUMBER);
            pulseFreq.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setPulseFreq(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(pulseFreq);
        }

        if (KLapse.hasFlowFreq()) {
            GenericSelectView flowFreq = new GenericSelectView();
            flowFreq.setTitle(getString(R.string.flow_freq));
            flowFreq.setSummary(getString(R.string.flow_freq_summary));
            flowFreq.setValue(KLapse.getFlowFreq());
            flowFreq.setInputType(InputType.TYPE_CLASS_NUMBER);
            flowFreq.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setFlowFreq(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(flowFreq);
        }

        if (KLapse.hasBacklightRange()) {
            GenericSelectView backlightRangeMin = new GenericSelectView();
            backlightRangeMin.setTitle(getString(R.string.backlight_range));
            backlightRangeMin.setSummary("Min");
            backlightRangeMin.setValue(KLapse.getBacklightRange("min"));
            backlightRangeMin.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightRangeMin.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setBacklightRange("min", value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(backlightRangeMin);

            GenericSelectView backlightRangeMax = new GenericSelectView();
            backlightRangeMax.setSummary("Max");
            backlightRangeMax.setValue(KLapse.getBacklightRange("max"));
            backlightRangeMax.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightRangeMax.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setBacklightRange("max", value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(backlightRangeMax);
        }

        if (KLapse.hasBLRangeLower()) {
            GenericSelectView backlightRange = new GenericSelectView();
            backlightRange.setTitle(getString(R.string.backlight_range));
            backlightRange.setSummary("Min");
            backlightRange.setValue(KLapse.getBLRangeLower());
            backlightRange.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightRange.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setBLRangeLower(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(backlightRange);
        }

        if (KLapse.hasBLRangeUpper()) {
            GenericSelectView backlightRange = new GenericSelectView();
            backlightRange.setTitle(getString(R.string.backlight_range));
            backlightRange.setSummary("Max");
            backlightRange.setValue(KLapse.getBLRangeUpper());
            backlightRange.setInputType(InputType.TYPE_CLASS_NUMBER);
            backlightRange.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setBLRangeUpper(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(backlightRange);
        }

        if (KLapse.hasBrightnessFactor() || KLapse.hasDimmerFactor()) {
            SeekBarView Dimmer = new SeekBarView();
            Dimmer.setTitle(getString(R.string.dimming));
            if (KLapse.hasDimmerFactor()) {
                Dimmer.setSummary(getString(R.string.dimming_summary));
                Dimmer.setMax(100);
                Dimmer.setMin(10);
		Dimmer.setProgress(KLapse.getBrightnessFactor() - 10);
            } else {
                Dimmer.setSummary(getString(R.string.brightness_factor_summary));
                Dimmer.setMax(10);
                Dimmer.setMin(2);
		Dimmer.setProgress(KLapse.getBrightnessFactor() - 2);
            }
            Dimmer.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
		    if (KLapse.hasDimmerFactor()) {
                    	KLapse.setBrightnessFactor(position + 10, getActivity());
		    } else {
                    	KLapse.setBrightnessFactor(position + 2, getActivity());
		    }
                }
            });

            klapseCard.addItem(Dimmer);
        }

        SeekBarView brightFactStart = new SeekBarView();
        SeekBarView brightFactStop = new SeekBarView();

        if (KLapse.hasAutoBrightnessFactor()) {
            SwitchView autoBrightness = new SwitchView();
            autoBrightness.setTitle(getString(R.string.auto_dimming));
            autoBrightness.setSummary(getString(R.string.auto_dimming_summary));
            autoBrightness.setChecked(KLapse.isAutoBrightnessFactorEnabled());
            autoBrightness.addOnSwitchListener((switchView, isChecked) -> {
                KLapse.enableAutoBrightnessFactor(isChecked, getActivity());
                getHandler().postDelayed(() -> {
                    // Show or hide other Brightness options on the basis of the status of this switch
                    if (KLapse.isAutoBrightnessFactorEnabled()) {
                        if (KLapse.hasBrightFactStart()) {
                            brightFactStart.setProgress(KLapse.getBrightFactStart());
                            klapseCard.addItem(brightFactStart);
                        }
			if (KLapse.hasDimmerStart()) {
                            brightFactStart.setProgress(KLapse.getBrightFactStart() / 15);
                            klapseCard.addItem(brightFactStart);
                        }
                        if (KLapse.hasBrightFactStop()) {
                            brightFactStop.setProgress(KLapse.getBrightFactStop());
                            klapseCard.addItem(brightFactStop);
                        }
                        if (KLapse.hasDimmerStop()) {
                            brightFactStop.setProgress(KLapse.getBrightFactStop() / 15);
                            klapseCard.addItem(brightFactStop);
                        }
                    } else {
                        klapseCard.removeItem(brightFactStart);
                        klapseCard.removeItem(brightFactStop);
                    }
                }, 100);
            });

            klapseCard.addItem(autoBrightness);
        }

        if (KLapse.hasBrightFactStart() || KLapse.hasDimmerStart()) {
            brightFactStart.setTitle((KLapse.hasDimmerStart()) ? getString(R.string.auto_dimming_schedule) + (" (Minute)")
                    : getString(R.string.auto_dimming_schedule) + (" (Hour)"));
            brightFactStart.setSummary(getString(R.string.start_time));
            if (KLapse.hasDimmerStart()) {
                brightFactStart.setMax(1440);
		brightFactStart.setOffset(15);
		brightFactStart.setProgress(KLapse.getBrightFactStart() / 15);
            } else {
                brightFactStart.setMax(23);
		brightFactStart.setProgress(KLapse.getBrightFactStart());
            }
            brightFactStart.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
		    if (KLapse.hasDimmerStart()) {
                    	KLapse.setBrightFactStart((position * 15), getActivity());
		    } else {
                    	KLapse.setBrightFactStart((position), getActivity());
		    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            if (KLapse.isAutoBrightnessFactorEnabled()) {
                klapseCard.addItem(brightFactStart);
            } else {
                klapseCard.removeItem(brightFactStart);
            }
        }

        if (KLapse.hasBrightFactStop() || KLapse.hasDimmerStop()) {
            brightFactStop.setSummary(getString(R.string.end_time));
            if (KLapse.hasDimmerStop()) {
                brightFactStop.setMax(1440);
                brightFactStop.setOffset(15);
		brightFactStop.setProgress(KLapse.getBrightFactStop() / 15);
            } else {
                brightFactStop.setMax(23);
		brightFactStop.setProgress(KLapse.getBrightFactStop());
            }
            brightFactStop.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
		    if (KLapse.hasDimmerStop()) {
                    	KLapse.setBrightFactStop((position * 15), getActivity());
		    } else {
                    	KLapse.setBrightFactStop((position), getActivity());
		    }
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            if (KLapse.isAutoBrightnessFactorEnabled()) {
                klapseCard.addItem(brightFactStop);
            } else {
                klapseCard.removeItem(brightFactStop);
            }
        }

        if (klapseCard.size() > 0) {
            items.add(klapseCard);
        }

    }
}
