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

import android.app.TimePickerDialog;
import android.text.InputType;
import android.widget.TimePicker;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
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
        klapseCard.setTitle(KLapse.hasklapseVersion() ? getString(R.string.klapse) + " v" + KLapse.getklapseVersion() : getString(R.string.klapse));

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

        if (KLapse.hasklapseStart()) {
            int startTime = Utils.strToInt(KLapse.getklapseStartRaw());
            int startHr = startTime / 60;
            int startMin = startTime - (startHr * 60);

            DescriptionView klapseStart = new DescriptionView();
            klapseStart.setTitle(getString(R.string.night_mode_schedule));
            klapseStart.setSummary(getString(R.string.start_time) + ": " + KLapse.getklapseStart());
            klapseStart.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    KLapse.setklapseStart((hourOfDay * 60) + minute, getActivity());
                                    getHandler().postDelayed(() -> {
                                                klapseStart.setSummary(getString(R.string.start_time) + ": " + KLapse.getklapseStart());
                                            },
                                            500);
                                }
                            }, startHr, startMin, false);
                    timePickerDialog.show();
                }
            });

            klapseCard.addItem(klapseStart);
        }

        if (KLapse.hasklapseStop()) {
            int EndTime = Utils.strToInt(KLapse.getklapseStopRaw());
            int EndHr = EndTime / 60;
            int EndMin = EndTime - (EndHr * 60);

            DescriptionView klapseStop = new DescriptionView();
            klapseStop.setSummary(getString(R.string.end_time) + ": " + KLapse.getklapseStop());
            klapseStop.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    KLapse.setklapseStop((hourOfDay * 60) + minute, getActivity());
                                    getHandler().postDelayed(() -> {
                                                klapseStop.setSummary(getString(R.string.end_time) + ": " + KLapse.getklapseStop());
                                            },
                                            500);
                                }
                            }, EndHr, EndMin, false);
                    timePickerDialog.show();
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

        if (KLapse.hasDimmerFactor()) {
            SeekBarView Dimmer = new SeekBarView();
            Dimmer.setTitle(getString(R.string.dimming));
            Dimmer.setSummary(getString(R.string.dimming_summary));
            Dimmer.setMax(100);
            Dimmer.setMin(10);
            Dimmer.setProgress(KLapse.getBrightnessFactor() - 10);
            Dimmer.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setBrightnessFactor(position + 10, getActivity());
                }
            });

            klapseCard.addItem(Dimmer);
        }

        DescriptionView brightFactStart = new DescriptionView();
        DescriptionView brightFactStop = new DescriptionView();

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
                        brightFactStart.setSummary(getString(R.string.start_time) + ": " + KLapse.getBrightFactStart());
                        klapseCard.addItem(brightFactStart);
                        brightFactStop.setSummary(getString(R.string.end_time) + ": " + KLapse.getBrightFactStop());
                        klapseCard.addItem(brightFactStop);
                    } else {
                        klapseCard.removeItem(brightFactStart);
                        klapseCard.removeItem(brightFactStop);
                    }
                }, 100);
            });

            klapseCard.addItem(autoBrightness);
        }

        if (KLapse.hasDimmerStart()) {
            int startTime = Utils.strToInt(KLapse.getBrightFactStartRaw());
            int startHr = startTime / 60;
            int startMin = startTime - (startHr * 60);

            brightFactStart.setTitle(getString(R.string.auto_dimming_schedule));
            brightFactStart.setSummary(getString(R.string.start_time) + ": " + KLapse.getBrightFactStart());
            brightFactStart.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    KLapse.setBrightFactStart((hourOfDay * 60) + minute, getActivity());
                                    getHandler().postDelayed(() -> {
                                                brightFactStart.setSummary(getString(R.string.start_time) + ": " + KLapse.getBrightFactStart());
                                            },
                                            500);
                                }
                            }, startHr, startMin, false);
                    timePickerDialog.show();
                }
            });

            if (KLapse.isAutoBrightnessFactorEnabled()) {
                klapseCard.addItem(brightFactStart);
            } else {
                klapseCard.removeItem(brightFactStart);
            }
        }

        if (KLapse.hasDimmerStop()) {
            int EndTime = Utils.strToInt(KLapse.getBrightFactStopRaw());
            int EndHr = EndTime / 60;
            int EndMin = EndTime - (EndHr * 60);

            brightFactStop.setSummary(getString(R.string.end_time) + ": " + KLapse.getBrightFactStop());
            brightFactStop.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    KLapse.setBrightFactStop((hourOfDay * 60) + minute, getActivity());
                                    getHandler().postDelayed(() -> {
                                                brightFactStop.setSummary(getString(R.string.end_time) + ": " + KLapse.getBrightFactStop());
                                            },
                                            500);
                                }
                            }, EndHr, EndMin, false);
                    timePickerDialog.show();
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
