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

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.screen.Calibration;
import com.grarak.kerneladiutor.utils.kernel.screen.Gamma;
import com.grarak.kerneladiutor.utils.kernel.screen.GammaProfiles;
import com.grarak.kerneladiutor.utils.kernel.screen.Misc;
import com.grarak.kerneladiutor.views.ColorTable;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DropDownView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import com.smartpack.kernelmanager.utils.KLapse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.05.16.
 */
public class ScreenFragment extends RecyclerViewFragment {

    private Calibration mCalibration;
    private Misc mMisc;

    private SeekBarView mColors[];
    private SeekBarView mMinColor;

    private GenericSelectView mKGammaBlue;
    private GenericSelectView mKGammaGreen;
    private GenericSelectView mKGammaRed;

    private GenericSelectView mGammaControlRedGreys;
    private GenericSelectView mGammaControlRedMids;
    private GenericSelectView mGammaControlRedBlacks;
    private GenericSelectView mGammaControlRedWhites;
    private GenericSelectView mGammaControlGreenGreys;
    private GenericSelectView mGammaControlGreenMids;
    private GenericSelectView mGammaControlGreenBlacks;
    private GenericSelectView mGammaControlGreenWhites;
    private GenericSelectView mGammaControlBlueGreys;
    private GenericSelectView mGammaControlBlueMids;
    private GenericSelectView mGammaControlBlueBlacks;
    private GenericSelectView mGammaControlBlueWhites;
    private GenericSelectView mGammaControlContrast;
    private GenericSelectView mGammaControlBrightness;
    private GenericSelectView mGammaControlSaturation;

    private GenericSelectView mDsiPanelBlueNegative;
    private GenericSelectView mDsiPanelBluePositive;
    private GenericSelectView mDsiPanelGreenNegative;
    private GenericSelectView mDsiPanelGreenPositive;
    private GenericSelectView mDsiPanelRedNegative;
    private GenericSelectView mDsiPanelRedPositive;
    private GenericSelectView mDsiPanelWhitePoint;

    @Override
    protected void init() {
        super.init();

        mCalibration = Calibration.getInstance();
        mMisc = Misc.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(new ColorTableFragment());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
	if (KLapse.supported()) {
            klapsInit(items);
	}
	if (mMisc.haskcalRed() || mMisc.haskcalGreen() || mMisc.haskcalBlue()) {
            kcalColorInit(items);
	}
        screenColorInit(items);
        List<RecyclerViewItem> gammas = new ArrayList<>();
        if (Gamma.hasKGamma()) {
            kgammaInit(gammas);
        } else if (Gamma.hasGammaControl()) {
            gammacontrolInit(gammas);
        } else if (Gamma.hasDsiPanel()) {
            dsipanelInit(gammas);
        }
        if (gammas.size() > 0) {
            CardView gamma = new CardView(getActivity());
            gamma.setTitle(getString(R.string.gamma));
            for (RecyclerViewItem item : gammas) {
                gamma.addItem(item);
            }
            items.add(gamma);
        }
        lcdBackLightInit(items);
        backlightDimmerInit(items);
        if (mMisc.hasNegativeToggle()) {
            negativeToggleInit(items);
        }
        mdnieGlobalInit(items);
        if (mMisc.hasGloveMode()) {
            gloveModeInit(items);
        }
    }

    private void klapsInit(List<RecyclerViewItem> items) {
	CardView klapseCard = new CardView(getActivity());
	klapseCard.setTitle(getString(R.string.klapse));

        if (KLapse.hasEnable()) {
            SelectView enable = new SelectView();
            enable.setSummary(getString(R.string.klapse_summary));
            enable.setItems(KLapse.enable(getActivity()));
            enable.setItem(KLapse.getklapseEnable());
            enable.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
		    KLapse.setklapseEnable(position, getActivity());
                }
            });

	    klapseCard.addItem(enable);
	}

        if (KLapse.hasklapseStart()) {
            SeekBarView klapseStart = new SeekBarView();
            klapseStart.setTitle(getString(R.string.night_mode_schedule));
            klapseStart.setSummary(getString(R.string.start_time));
            klapseStart.setMax(23);
            klapseStart.setProgress(KLapse.getklapseStart());
            klapseStart.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setklapseStart((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(klapseStart);
        }

        if (KLapse.hasklapseStop()) {
            SeekBarView klapseStop = new SeekBarView();
            klapseStop.setSummary(getString(R.string.end_time));
            klapseStop.setMax(23);
            klapseStop.setProgress(KLapse.getklapseStop());
            klapseStop.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setklapseStop((position), getActivity());
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

        if (KLapse.hasAutoBrightnessFactor()) {
	    SwitchView autoBrightness = new SwitchView();
	    autoBrightness.setTitle(getString(R.string.auto_brightness));
	    autoBrightness.setSummary(getString(R.string.auto_brightness_summary));
	    autoBrightness.setChecked(KLapse.isAutoBrightnessFactorEnabled());
	    autoBrightness.addOnSwitchListener(new SwitchView.OnSwitchListener() {
	    	@Override
	    	public void onChanged(SwitchView switchView, boolean isChecked) {
		    KLapse.enableAutoBrightnessFactor(isChecked, getActivity());
	    	}
	    });

	    klapseCard.addItem(autoBrightness);
	}

        if (KLapse.hasBrightFactStart()) {
            SeekBarView brightFactStart = new SeekBarView();
            brightFactStart.setTitle(getString(R.string.auto_brightness_schedule));
            brightFactStart.setSummary(getString(R.string.start_time));
            brightFactStart.setMax(23);
            brightFactStart.setProgress(KLapse.getBrightFactStart());
            brightFactStart.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setBrightFactStart((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(brightFactStart);
        }

        if (KLapse.hasBrightFactStop()) {
            SeekBarView brightFactStop = new SeekBarView();
            brightFactStop.setSummary(getString(R.string.end_time));
            brightFactStop.setMax(23);
            brightFactStop.setProgress(KLapse.getBrightFactStop());
            brightFactStop.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    KLapse.setBrightFactStop((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            klapseCard.addItem(brightFactStop);
        }

        if (KLapse.hasBrightnessFactor()) {
            GenericSelectView brightnessFactor = new GenericSelectView();
            brightnessFactor.setTitle(getString(R.string.brightness_factor));
            brightnessFactor.setSummary(getString(R.string.brightness_factor_summary));
            brightnessFactor.setValue(KLapse.getBrightnessFactor());
            brightnessFactor.setInputType(InputType.TYPE_CLASS_NUMBER);
            brightnessFactor.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    KLapse.setBrightnessFactor(value, getActivity());
                    genericSelectView.setValue(value);
                }
            });

            klapseCard.addItem(brightnessFactor);
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

	if (klapseCard.size() > 0) {
            items.add(klapseCard);
	}

    }

    private void kcalColorInit(List<RecyclerViewItem> items) {
	CardView kcalCard = new CardView(getActivity());
	kcalCard.setTitle(getString(R.string.screen_color));

        if (mMisc.haskcalRed()) {
            SeekBarView kcal = new SeekBarView();
            kcal.setTitle(getString(R.string.red));
            kcal.setMax(256);
            kcal.setProgress(mMisc.getkcalRed());
            kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setkcalRed((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            kcalCard.addItem(kcal);
        }

        if (mMisc.haskcalGreen()) {
            SeekBarView kcal = new SeekBarView();
            kcal.setTitle(getString(R.string.green));
            kcal.setMax(256);
            kcal.setProgress(mMisc.getkcalGreen());
            kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setkcalGreen((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            kcalCard.addItem(kcal);
        }

        if (mMisc.haskcalBlue()) {
            SeekBarView kcal = new SeekBarView();
            kcal.setTitle(getString(R.string.blue));
            kcal.setMax(256);
            kcal.setProgress(mMisc.getkcalBlue());
            kcal.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setkcalBlue((position), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            kcalCard.addItem(kcal);
        }


	if (kcalCard.size() > 0) {
            items.add(kcalCard);
	}
    }

    private void screenColorInit(List<RecyclerViewItem> items) {
        if (mCalibration.hasColors()) {

            CardView screenColor = new CardView(getActivity());
            screenColor.setTitle(getString(R.string.screen_color));

            List<String> colors = mCalibration.getColors();
            final List<String> limits = mCalibration.getLimits();
            mColors = new SeekBarView[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
                mColors[i] = new SeekBarView();
                mColors[i].setTitle(getResources().getStringArray(R.array.colors)[i]);
                mColors[i].setItems(limits);
                mColors[i].setProgress(limits.indexOf(colors.get(i)));
                mColors[i].setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                        if (mMinColor != null && position < mMinColor.getProgress()) {
                            mMinColor.setProgress(position);
                        }
                    }

                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        if (mMinColor != null) {
                            int current = Utils.strToInt(mCalibration.getLimits().get(position));
                            if (mCalibration.getMinColor() > current) {
                                mCalibration.setMinColor(current, getActivity());
                            }
                        }

                        int r = mColors[0].getProgress();
                        int g = mColors[1].getProgress();
                        int b = mColors[2].getProgress();
                        mCalibration.setColors(limits.get(r) + " " + limits.get(g) + " " + limits.get(b),
                                getActivity());
                    }
                });

                screenColor.addItem(mColors[i]);
            }

            if (screenColor.size() > 0) {
                items.add(screenColor);
            }

            if (mCalibration.hasMinColor()) {
                mMinColor = new SeekBarView();
                mMinColor.setTitle(getString(R.string.min_rgb));
                mMinColor.setItems(mCalibration.getLimits());
                mMinColor.setProgress(mCalibration.getLimits().indexOf(String.valueOf(mCalibration.getMinColor())));
                mMinColor.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        mCalibration.setMinColor(Utils.strToInt(value), getActivity());

                        StringBuilder colors = new StringBuilder();
                        for (String color : mCalibration.getColors()) {
                            if (Utils.strToInt(value) > Utils.strToInt(color)) {
                                colors.append(value).append(" ");
                            } else {
                                colors.append(color).append(" ");
                            }
                        }
                        colors.setLength(colors.length() - 1);
                        mCalibration.setColors(colors.toString(), getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                        for (SeekBarView color : mColors) {
                            if (position > color.getProgress()) {
                                color.setProgress(position);
                            }
                        }
                    }
                });

                items.add(mMinColor);
            }
        }

        if (mCalibration.hasInvertScreen()) {
            SwitchView invertScreen = new SwitchView();
            invertScreen.setSummary(getString(R.string.invert_screen));
            invertScreen.setChecked(mCalibration.isInvertScreenEnabled());
            invertScreen.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCalibration.enableInvertScreen(isChecked, getActivity());
                }
            });

            items.add(invertScreen);
        }

        if (mCalibration.hasSaturationIntensity()) {
            int saturation = mCalibration.getSaturationIntensity();
            final SeekBarView saturationIntensity = new SeekBarView();
            saturationIntensity.setTitle(getString(R.string.saturation_intensity));
            saturationIntensity.setMax(158);
            saturationIntensity.setProgress(saturation == 128 ? 30 : saturation - 225);
            saturationIntensity.setEnabled(saturation != 128);
            saturationIntensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCalibration.setSaturationIntensity(position + 225, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(saturationIntensity);

            SwitchView grayscaleMode = new SwitchView();
            grayscaleMode.setSummary(getString(R.string.grayscale_mode));
            grayscaleMode.setChecked(saturation == 128);
            grayscaleMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    saturationIntensity.setEnabled(!isChecked);
                    mCalibration.enableGrayscaleMode(isChecked, getActivity());
                    if (!isChecked) {
                        saturationIntensity.setProgress(30);
                    }
                }
            });

            items.add(grayscaleMode);
        }

        if (mCalibration.hasScreenHue()) {
            SeekBarView screenHue = new SeekBarView();
            screenHue.setTitle(getString(R.string.screen_hue));
            screenHue.setSummary(getString(R.string.screen_hue_summary));
            screenHue.setMax(1536);
            screenHue.setProgress(mCalibration.getScreenHue());
            screenHue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCalibration.setScreenHue(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenHue);
        }

        if (mCalibration.hasScreenValue()) {
            SeekBarView screenValue = new SeekBarView();
            screenValue.setTitle(getString(R.string.screen_value));
            screenValue.setMax(255);
            screenValue.setProgress(mCalibration.getScreenValue() - 128);
            screenValue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCalibration.setScreenValue(position + 128, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenValue);
        }

        if (mCalibration.hasScreenContrast()) {
            SeekBarView screenContrast = new SeekBarView();
            screenContrast.setTitle(getString(R.string.screen_contrast));
            screenContrast.setMax(255);
            screenContrast.setProgress(mCalibration.getScreenContrast() - 128);
            screenContrast.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCalibration.setScreenContrast(position + 128, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenContrast);
        }

        if (mCalibration.hasScreenHBM()) {
            SwitchView screenHBM = new SwitchView();
            screenHBM.setSummary(getString(R.string.high_brightness_mode));
            screenHBM.setChecked(mCalibration.isScreenHBMEnabled());
            screenHBM.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCalibration.enableScreenHBM(isChecked, getActivity());
                }
            });

            items.add(screenHBM);
        }

        if (mCalibration.hasSRGB()) {
            SwitchView sRGB = new SwitchView();
            sRGB.setSummary(getString(R.string.srgb));
            sRGB.setChecked(mCalibration.isSRGBEnabled());
            sRGB.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mCalibration.enableSRGB(isChecked, getActivity());
                }
            });

            items.add(sRGB);
        }
    }

    private void kgammaInit(List<RecyclerViewItem> items) {
        if (mKGammaBlue == null) {
            mKGammaBlue = new GenericSelectView();
            mKGammaBlue.setSummary(getString(R.string.blue));
            mKGammaBlue.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setKGammaBlue(value, getActivity());
                    kgammaInit(null);
                }
            });
        }
        String blue = Gamma.getKGammaBlue();
        mKGammaBlue.setValue(blue);
        mKGammaBlue.setValueRaw(blue);

        if (items != null) {
            items.add(mKGammaBlue);
        }

        if (mKGammaGreen == null) {
            mKGammaGreen = new GenericSelectView();
            mKGammaGreen.setSummary(getString(R.string.green));
            mKGammaGreen.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setKGammaGreen(value, getActivity());
                    kgammaInit(null);
                }
            });
        }
        String green = Gamma.getKGammaGreen();
        mKGammaGreen.setValue(green);
        mKGammaGreen.setValueRaw(green);

        if (items != null) {
            items.add(mKGammaGreen);
        }

        if (mKGammaRed == null) {
            mKGammaRed = new GenericSelectView();
            mKGammaRed.setSummary(getString(R.string.red));
            mKGammaRed.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setKGammaRed(value, getActivity());
                    kgammaInit(null);
                }
            });
        }
        String red = Gamma.getKGammaRed();
        mKGammaRed.setValue(red);
        mKGammaRed.setValueRaw(red);

        if (items != null) {
            items.add(mKGammaRed);
        }

        if (items != null) {
            List<String> profileList = new ArrayList<>();
            final GammaProfiles.KGammaProfiles gammaProfiles = Gamma.getKGammaProfiles(getActivity());
            for (int i = 0; i < gammaProfiles.length(); i++) {
                profileList.add(gammaProfiles.getName(i));
            }

            DropDownView profiles = new DropDownView();
            profiles.setTitle(getString(R.string.profile));
            profiles.setSummary(getString(R.string.gamma_profiles_summary));
            profiles.setItems(profileList);
            profiles.setSelection(Prefs.getInt("kgamma_profile", -1, getActivity()));
            profiles.setOnDropDownListener(new DropDownView.OnDropDownListener() {
                @Override
                public void onSelect(DropDownView dropDownView, int position, String value) {
                    Gamma.setKGammaProfile(position, gammaProfiles, getActivity());
                    kgammaInit(null);
                    Prefs.saveInt("kgamma_profile", position, getActivity());
                }
            });

            items.add(profiles);
        }
    }

    private void gammacontrolInit(List<RecyclerViewItem> items) {
        if (mGammaControlRedGreys == null) {
            mGammaControlRedGreys = new GenericSelectView();
            mGammaControlRedGreys.setSummary(getString(R.string.red_greys));
            mGammaControlRedGreys.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedGreys(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redGreys = Gamma.getRedGreys();
        mGammaControlRedGreys.setValue(redGreys);
        mGammaControlRedGreys.setValueRaw(redGreys);

        if (items != null) {
            items.add(mGammaControlRedGreys);
        }

        if (mGammaControlRedMids == null) {
            mGammaControlRedMids = new GenericSelectView();
            mGammaControlRedMids.setSummary(getString(R.string.red_mids));
            mGammaControlRedMids.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedMids(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redMids = Gamma.getRedMids();
        mGammaControlRedMids.setValue(redMids);
        mGammaControlRedMids.setValueRaw(redMids);

        if (items != null) {
            items.add(mGammaControlRedMids);
        }

        if (mGammaControlRedBlacks == null) {
            mGammaControlRedBlacks = new GenericSelectView();
            mGammaControlRedBlacks.setSummary(getString(R.string.red_blacks));
            mGammaControlRedBlacks.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedBlacks(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redBlacks = Gamma.getRedBlacks();
        mGammaControlRedBlacks.setValue(redBlacks);
        mGammaControlRedBlacks.setValueRaw(redBlacks);

        if (items != null) {
            items.add(mGammaControlRedBlacks);
        }

        if (mGammaControlRedWhites == null) {
            mGammaControlRedWhites = new GenericSelectView();
            mGammaControlRedWhites.setSummary(getString(R.string.red_whites));
            mGammaControlRedWhites.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedWhites(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redWhites = Gamma.getRedWhites();
        mGammaControlRedWhites.setValue(redWhites);
        mGammaControlRedWhites.setValueRaw(redWhites);

        if (items != null) {
            items.add(mGammaControlRedWhites);
        }

        if (mGammaControlGreenGreys == null) {
            mGammaControlGreenGreys = new GenericSelectView();
            mGammaControlGreenGreys.setSummary(getString(R.string.green_greys));
            mGammaControlGreenGreys.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenGreys(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenGreys = Gamma.getGreenGreys();
        mGammaControlGreenGreys.setValue(greenGreys);
        mGammaControlGreenGreys.setValueRaw(greenGreys);

        if (items != null) {
            items.add(mGammaControlGreenGreys);
        }

        if (mGammaControlGreenMids == null) {
            mGammaControlGreenMids = new GenericSelectView();
            mGammaControlGreenMids.setSummary(getString(R.string.green_mids));
            mGammaControlGreenMids.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenMids(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenMids = Gamma.getGreenMids();
        mGammaControlGreenMids.setValue(greenMids);
        mGammaControlGreenMids.setValueRaw(greenMids);

        if (items != null) {
            items.add(mGammaControlGreenMids);
        }

        if (mGammaControlGreenBlacks == null) {
            mGammaControlGreenBlacks = new GenericSelectView();
            mGammaControlGreenBlacks.setSummary(getString(R.string.green_blacks));
            mGammaControlGreenBlacks.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenBlacks(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenBlacks = Gamma.getGreenBlacks();
        mGammaControlGreenBlacks.setValue(greenBlacks);
        mGammaControlGreenBlacks.setValueRaw(greenBlacks);

        if (items != null) {
            items.add(mGammaControlGreenBlacks);
        }

        if (mGammaControlGreenWhites == null) {
            mGammaControlGreenWhites = new GenericSelectView();
            mGammaControlGreenWhites.setSummary(getString(R.string.green_whites));
            mGammaControlGreenWhites.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenWhites(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenWhites = Gamma.getGreenWhites();
        mGammaControlGreenWhites.setValue(greenWhites);
        mGammaControlGreenWhites.setValueRaw(greenWhites);

        if (items != null) {
            items.add(mGammaControlGreenWhites);
        }

        if (mGammaControlBlueGreys == null) {
            mGammaControlBlueGreys = new GenericSelectView();
            mGammaControlBlueGreys.setSummary(getString(R.string.blue_greys));
            mGammaControlBlueGreys.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueGreys(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueGreys = Gamma.getBlueGreys();
        mGammaControlBlueGreys.setValue(blueGreys);
        mGammaControlBlueGreys.setValueRaw(blueGreys);

        if (items != null) {
            items.add(mGammaControlBlueGreys);
        }

        if (mGammaControlBlueMids == null) {
            mGammaControlBlueMids = new GenericSelectView();
            mGammaControlBlueMids.setSummary(getString(R.string.blue_mids));
            mGammaControlBlueMids.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueMids(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueMids = Gamma.getBlueMids();
        mGammaControlBlueMids.setValue(blueMids);
        mGammaControlBlueMids.setValueRaw(blueMids);

        if (items != null) {
            items.add(mGammaControlBlueMids);
        }

        if (mGammaControlBlueBlacks == null) {
            mGammaControlBlueBlacks = new GenericSelectView();
            mGammaControlBlueBlacks.setSummary(getString(R.string.blue_blacks));
            mGammaControlBlueBlacks.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueBlacks(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueBlacks = Gamma.getBlueBlacks();
        mGammaControlBlueBlacks.setValue(blueBlacks);
        mGammaControlBlueBlacks.setValueRaw(blueBlacks);

        if (items != null) {
            items.add(mGammaControlBlueBlacks);
        }

        if (mGammaControlBlueWhites == null) {
            mGammaControlBlueWhites = new GenericSelectView();
            mGammaControlBlueWhites.setSummary(getString(R.string.blue_whites));
            mGammaControlBlueWhites.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueWhites(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueWhites = Gamma.getBlueWhites();
        mGammaControlBlueWhites.setValue(blueWhites);
        mGammaControlBlueWhites.setValueRaw(blueWhites);

        if (items != null) {
            items.add(mGammaControlBlueWhites);
        }

        if (mGammaControlContrast == null) {
            mGammaControlContrast = new GenericSelectView();
            mGammaControlContrast.setSummary(getString(R.string.contrast));
            mGammaControlContrast.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGammaContrast(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String contrast = Gamma.getGammaContrast();
        mGammaControlContrast.setValue(contrast);
        mGammaControlContrast.setValueRaw(contrast);

        if (items != null) {
            items.add(mGammaControlContrast);
        }

        if (mGammaControlBrightness == null) {
            mGammaControlBrightness = new GenericSelectView();
            mGammaControlBrightness.setSummary(getString(R.string.brightness));
            mGammaControlBrightness.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGammaBrightness(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String brightness = Gamma.getGammaBrightness();
        mGammaControlBrightness.setValue(brightness);
        mGammaControlBrightness.setValueRaw(brightness);

        if (items != null) {
            items.add(mGammaControlBrightness);
        }

        if (mGammaControlSaturation == null) {
            mGammaControlSaturation = new GenericSelectView();
            mGammaControlSaturation.setSummary(getString(R.string.saturation_intensity));
            mGammaControlSaturation.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGammaSaturation(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String saturation = Gamma.getGammaSaturation();
        mGammaControlSaturation.setValue(saturation);
        mGammaControlSaturation.setValueRaw(saturation);

        if (items != null) {
            items.add(mGammaControlSaturation);
        }

        if (items != null) {
            List<String> profileList = new ArrayList<>();
            final GammaProfiles.GammaControlProfiles gammaProfiles = Gamma.getGammaControlProfiles(getActivity());
            for (int i = 0; i < gammaProfiles.length(); i++) {
                profileList.add(gammaProfiles.getName(i));
            }

            DropDownView profiles = new DropDownView();
            profiles.setTitle(getString(R.string.profile));
            profiles.setSummary(getString(R.string.gamma_profiles_summary));
            profiles.setItems(profileList);
            profiles.setSelection(Prefs.getInt("gamma_control_profile", -1, getActivity()));
            profiles.setOnDropDownListener(new DropDownView.OnDropDownListener() {
                @Override
                public void onSelect(DropDownView dropDownView, int position, String value) {
                    Gamma.setGammaControlProfile(position, gammaProfiles, getActivity());
                    gammacontrolInit(null);
                    Prefs.saveInt("gamma_control_profile", position, getActivity());
                }
            });

            items.add(profiles);
        }
    }

    private void dsipanelInit(List<RecyclerViewItem> items) {
        if (mDsiPanelBlueNegative == null) {
            mDsiPanelBlueNegative = new GenericSelectView();
            mDsiPanelBlueNegative.setSummary(getString(R.string.blue_negative));
            mDsiPanelBlueNegative.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueNegative(value, getActivity());
                }
            });
        }

        String blueNegative = Gamma.getBlueNegative();
        mDsiPanelBlueNegative.setValue(blueNegative);
        mDsiPanelBlueNegative.setValueRaw(blueNegative);

        if (items != null) {
            items.add(mDsiPanelBlueNegative);
        }

        if (mDsiPanelBluePositive == null) {
            mDsiPanelBluePositive = new GenericSelectView();
            mDsiPanelBluePositive.setSummary(getString(R.string.blue_positive));
            mDsiPanelBluePositive.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBluePositive(value, getActivity());
                }
            });
        }

        String bluePositive = Gamma.getBluePositive();
        mDsiPanelBluePositive.setValue(bluePositive);
        mDsiPanelBluePositive.setValueRaw(bluePositive);

        if (items != null) {
            items.add(mDsiPanelBluePositive);
        }

        if (mDsiPanelGreenNegative == null) {
            mDsiPanelGreenNegative = new GenericSelectView();
            mDsiPanelGreenNegative.setSummary(getString(R.string.green_negative));
            mDsiPanelGreenNegative.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenNegative(value, getActivity());
                }
            });
        }

        String greenNegative = Gamma.getGreenNegative();
        mDsiPanelGreenNegative.setValue(greenNegative);
        mDsiPanelGreenNegative.setValueRaw(greenNegative);

        if (items != null) {
            items.add(mDsiPanelGreenNegative);
        }

        if (mDsiPanelGreenPositive == null) {
            mDsiPanelGreenPositive = new GenericSelectView();
            mDsiPanelGreenPositive.setSummary(getString(R.string.green_positive));
            mDsiPanelGreenPositive.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenPositive(value, getActivity());
                }
            });
        }

        String greenPositive = Gamma.getGreenPositive();
        mDsiPanelGreenPositive.setValue(greenPositive);
        mDsiPanelGreenPositive.setValueRaw(greenPositive);

        if (items != null) {
            items.add(mDsiPanelGreenPositive);
        }

        if (mDsiPanelRedNegative == null) {
            mDsiPanelRedNegative = new GenericSelectView();
            mDsiPanelRedNegative.setSummary(getString(R.string.red_negative));
            mDsiPanelRedNegative.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedNegative(value, getActivity());
                }
            });
        }

        String redNegative = Gamma.getRedNegative();
        mDsiPanelRedNegative.setValue(redNegative);
        mDsiPanelRedNegative.setValueRaw(redNegative);

        if (items != null) {
            items.add(mDsiPanelRedNegative);
        }

        if (mDsiPanelRedPositive == null) {
            mDsiPanelRedPositive = new GenericSelectView();
            mDsiPanelRedPositive.setSummary(getString(R.string.red_positive));
            mDsiPanelRedPositive.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedPositive(value, getActivity());
                }
            });
        }

        String redPositive = Gamma.getRedPositive();
        mDsiPanelRedPositive.setValue(redPositive);
        mDsiPanelRedPositive.setValueRaw(redPositive);

        if (items != null) {
            items.add(mDsiPanelRedPositive);
        }

        if (mDsiPanelWhitePoint == null) {
            mDsiPanelWhitePoint = new GenericSelectView();
            mDsiPanelWhitePoint.setSummary(getString(R.string.white_point));
            mDsiPanelWhitePoint.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setWhitePoint(value, getActivity());
                }
            });
        }

        String whitePoint = Gamma.getWhitePoint();
        mDsiPanelWhitePoint.setValue(whitePoint);
        mDsiPanelWhitePoint.setValueRaw(whitePoint);

        if (items != null) {
            items.add(mDsiPanelWhitePoint);
        }

        if (items != null) {
            List<String> profileList = new ArrayList<>();
            final GammaProfiles.DsiPanelProfiles gammaProfiles = Gamma.getDsiPanelProfiles(getActivity());
            for (int i = 0; i < gammaProfiles.length(); i++) {
                profileList.add(gammaProfiles.getName(i));
            }

            DropDownView profiles = new DropDownView();
            profiles.setTitle(getString(R.string.profile));
            profiles.setSummary(getString(R.string.gamma_profiles_summary));
            profiles.setItems(profileList);
            profiles.setSelection(Prefs.getInt("dsi_panel_profile", -1, getActivity()));
            profiles.setOnDropDownListener(new DropDownView.OnDropDownListener() {
                @Override
                public void onSelect(DropDownView dropDownView, int position, String value) {
                    Gamma.setDsiPanelProfile(position, gammaProfiles, getActivity());
                    dsipanelInit(null);
                    Prefs.saveInt("dsi_panel_profile", position, getActivity());
                }
            });

            items.add(profiles);
        }
    }

    private void lcdBackLightInit(List<RecyclerViewItem> items) {
        CardView lcdBackLightCard = new CardView(getActivity());
        lcdBackLightCard.setTitle(getString(R.string.lcd_backlight));

        if (mMisc.hasBrightnessMode()) {
            SwitchView brightnessMode = new SwitchView();
            brightnessMode.setSummary(getString(R.string.brightness_mode));
            brightnessMode.setChecked(mMisc.isBrightnessModeEnabled());
            brightnessMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMisc.enableBrightnessMode(isChecked, getActivity());
                }
            });

            lcdBackLightCard.addItem(brightnessMode);
        }

        if (mMisc.hasLcdMinBrightness()) {
            SeekBarView lcdMinBrightness = new SeekBarView();
            lcdMinBrightness.setTitle(getString(R.string.min_brightness));
            lcdMinBrightness.setSummary(getString(R.string.min_brightness_summary));
            lcdMinBrightness.setMax(114);
            lcdMinBrightness.setMin(2);
            lcdMinBrightness.setProgress(mMisc.getLcdMinBrightness() - 2);
            lcdMinBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setLcdMinBrightness(position + 2, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            lcdBackLightCard.addItem(lcdMinBrightness);
        }

        if (mMisc.hasLcdMaxBrightness()) {
            SeekBarView lcdMaxBrightness = new SeekBarView();
            lcdMaxBrightness.setTitle(getString(R.string.max_brightness));
            lcdMaxBrightness.setSummary(getString(R.string.max_brightness_summary));
            lcdMaxBrightness.setMax(114);
            lcdMaxBrightness.setMin(2);
            lcdMaxBrightness.setProgress(mMisc.getLcdMaxBrightness() - 2);
            lcdMaxBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setLcdMaxBrightness(position + 2, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            lcdBackLightCard.addItem(lcdMaxBrightness);
        }

        if (lcdBackLightCard.size() > 0) {
            items.add(lcdBackLightCard);
        }
    }

    private void backlightDimmerInit(List<RecyclerViewItem> items) {
        CardView backLightDimmerCard = new CardView(getActivity());
        backLightDimmerCard.setTitle(getString(R.string.backlight_dimmer));

        if (mMisc.hasBackLightDimmerEnable()) {
            SwitchView backLightDimmer = new SwitchView();
            backLightDimmer.setSummary(getString(R.string.backlight_dimmer));
            backLightDimmer.setChecked(mMisc.isBackLightDimmerEnabled());
            backLightDimmer.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMisc.enableBackLightDimmer(isChecked, getActivity());
                }
            });

            backLightDimmerCard.addItem(backLightDimmer);
        }

        if (mMisc.hasMinBrightness()) {
            SeekBarView minBrightness = new SeekBarView();
            minBrightness.setTitle(getString(R.string.min_brightness));
            minBrightness.setSummary(getString(R.string.min_brightness_summary));
            minBrightness.setMax(mMisc.getMaxMinBrightness());
            minBrightness.setProgress(mMisc.getCurMinBrightness());
            minBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setMinBrightness(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            backLightDimmerCard.addItem(minBrightness);
        }

        if (mMisc.hasBackLightDimmerThreshold()) {
            SeekBarView threshold = new SeekBarView();
            threshold.setTitle(getString(R.string.threshold));
            threshold.setMax(50);
            threshold.setProgress(mMisc.getBackLightDimmerThreshold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setBackLightDimmerThreshold(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            backLightDimmerCard.addItem(threshold);
        }

        if (mMisc.hasBackLightDimmerOffset()) {
            SeekBarView dimmerOffset = new SeekBarView();
            dimmerOffset.setTitle(getString(R.string.offset));
            dimmerOffset.setMax(50);
            dimmerOffset.setProgress(mMisc.getBackLightDimmerOffset());
            dimmerOffset.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setBackLightDimmerOffset(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            backLightDimmerCard.addItem(dimmerOffset);
        }

        if (backLightDimmerCard.size() > 0) {
            items.add(backLightDimmerCard);
        }
    }

    private void negativeToggleInit(List<RecyclerViewItem> items) {
        SwitchView negative = new SwitchView();
        negative.setTitle(getString(R.string.negative_toggle));
        negative.setSummary(getString(R.string.negative_toggle_summary));
        negative.setChecked(mMisc.isNegativeToggleEnabled());
        negative.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableNegativeToggle(isChecked, getActivity());
            }
        });

        items.add(negative);
    }

    private void mdnieGlobalInit(List<RecyclerViewItem> items) {
        CardView mdnieCard = new CardView(getActivity());
        mdnieCard.setTitle(getString(R.string.mdnie_global_controls));

        if (mMisc.hasRegisterHook()) {
            SwitchView registerHook = new SwitchView();
            registerHook.setTitle(getString(R.string.register_hook));
            registerHook.setSummary(getString(R.string.register_hook_summary));
            registerHook.setChecked(mMisc.isRegisterHookEnabled());
            registerHook.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMisc.enableRegisterHook(isChecked, getActivity());
                }
            });

            mdnieCard.addItem(registerHook);
        }

        if (mMisc.hasMasterSequence()) {
            SwitchView masterSequence = new SwitchView();
            masterSequence.setTitle(getString(R.string.master_sequence));
            masterSequence.setSummary(getString(R.string.master_sequence_summary));
            masterSequence.setChecked(mMisc.isMasterSequenceEnable());
            masterSequence.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMisc.enableMasterSequence(isChecked, getActivity());
                }
            });

            mdnieCard.addItem(masterSequence);
        }

        if (mdnieCard.size() > 0) {
            items.add(mdnieCard);
        }
    }

    private void gloveModeInit(List<RecyclerViewItem> items) {
        SwitchView glove = new SwitchView();
        glove.setTitle(getString(R.string.glove_mode));
        glove.setSummary(getString(R.string.glove_mode_summary));
        glove.setChecked(mMisc.isGloveModeEnabled());
        glove.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mMisc.enableGloveMode(isChecked, getActivity());
            }
        });

        items.add(glove);
    }

    public static class ColorTableFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return new ColorTable(getActivity());
        }
    }

}
