/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.fragments.kernel;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ScreenFragment extends RecyclerViewFragment implements SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener,
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener {

    private List<String> mColorCalibrationLimits;
    private SeekBarCardView.DSeekBarCardView[] mColorCalibrationCard;
    private SeekBarCardView.DSeekBarCardView mColorCalibrationMinCard;
    private SwitchCompatCardItem.DSwitchCompatCard mInvertScreenCard;
    private SeekBarCardView.DSeekBarCardView mSaturationIntensityCard;
    private SwitchCompatCardItem.DSwitchCompatCard mGrayscaleModeCard;
    private SeekBarCardView.DSeekBarCardView mScreenHueCard;
    private SeekBarCardView.DSeekBarCardView mScreenValueCard;
    private SeekBarCardView.DSeekBarCardView mScreenContrastCard;

    private SwitchCompatCardItem.DSwitchCompatCard mBackLightDimmerEnableCard;
    private SeekBarCardView.DSeekBarCardView mMinBrightnessCard;
    private SeekBarCardView.DSeekBarCardView mBackLightDimmerThresholdCard;
    private SeekBarCardView.DSeekBarCardView mBackLightDimmerOffsetCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        screenColorInit();
        backlightDimmerInit();
    }

    private void screenColorInit() {
        if (Screen.hasColorCalibration()) {
            List<String> colors = Screen.getColorCalibration();
            mColorCalibrationLimits = Screen.getColorCalibrationLimits();
            mColorCalibrationCard = new SeekBarCardView.DSeekBarCardView[colors.size()];
            for (int i = 0; i < mColorCalibrationCard.length; i++) {
                mColorCalibrationCard[i] = new SeekBarCardView.DSeekBarCardView(Screen.getColorCalibrationLimits());
                mColorCalibrationCard[i].setTitle(getColor(i));
                mColorCalibrationCard[i].setProgress(Screen.getColorCalibrationLimits().indexOf(colors.get(i)));
                mColorCalibrationCard[i].setOnDSeekBarCardListener(this);

                addView(mColorCalibrationCard[i]);
            }
        }

        if (Screen.hasColorCalibrationMin() && mColorCalibrationLimits != null) {
            mColorCalibrationMinCard = new SeekBarCardView.DSeekBarCardView(Screen.getColorCalibrationLimits());
            mColorCalibrationMinCard.setTitle(getString(R.string.min_rgb));
            mColorCalibrationMinCard.setProgress(Screen.getColorCalibrationMin());
            mColorCalibrationMinCard.setOnDSeekBarCardListener(this);

            addView(mColorCalibrationMinCard);
        }

        if (Screen.hasInvertScreen()) {
            mInvertScreenCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mInvertScreenCard.setDescription(getString(R.string.invert_screen));
            mInvertScreenCard.setChecked(Screen.isInvertScreenActive());
            mInvertScreenCard.setOnDSwitchCompatCardListener(this);

            addView(mInvertScreenCard);
        }

        if (Screen.hasSaturationIntensity()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 159; i++)
                list.add(String.valueOf(i));

            int saturation = Screen.getSaturationIntensity();
            mSaturationIntensityCard = new SeekBarCardView.DSeekBarCardView(list);
            mSaturationIntensityCard.setTitle(getString(R.string.saturation_intensity));
            mSaturationIntensityCard.setProgress(saturation == 128 ? 30 : saturation - 225);
            mSaturationIntensityCard.setEnabled(saturation != 128);
            mSaturationIntensityCard.setOnDSeekBarCardListener(this);

            addView(mSaturationIntensityCard);

            mGrayscaleModeCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mGrayscaleModeCard.setDescription(getString(R.string.grayscale_mode));
            mGrayscaleModeCard.setChecked(saturation == 128);
            mGrayscaleModeCard.setOnDSwitchCompatCardListener(this);

            addView(mGrayscaleModeCard);
        }

        if (Screen.hasScreenHue()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 1537; i++)
                list.add(String.valueOf(i));

            mScreenHueCard = new SeekBarCardView.DSeekBarCardView(list);
            mScreenHueCard.setTitle(getString(R.string.screen_hue));
            mScreenHueCard.setDescription(getString(R.string.screen_hue_summary));
            mScreenHueCard.setProgress(Screen.getScreenHue());
            mScreenHueCard.setOnDSeekBarCardListener(this);

            addView(mScreenHueCard);
        }

        if (Screen.hasScreenValue()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 256; i++)
                list.add(String.valueOf(i));

            mScreenValueCard = new SeekBarCardView.DSeekBarCardView(list);
            mScreenValueCard.setTitle(getString(R.string.screen_value));
            mScreenValueCard.setProgress(Screen.getScreenValue() - 128);
            mScreenValueCard.setOnDSeekBarCardListener(this);

            addView(mScreenValueCard);
        }

        if (Screen.hasScreenContrast()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 256; i++)
                list.add(String.valueOf(i));

            mScreenContrastCard = new SeekBarCardView.DSeekBarCardView(list);
            mScreenContrastCard.setTitle(getString(R.string.screen_contrast));
            mScreenContrastCard.setProgress(Screen.getScreenContrast() - 128);
            mScreenContrastCard.setOnDSeekBarCardListener(this);

            addView(mScreenContrastCard);
        }
    }

    private void backlightDimmerInit() {
        if (Screen.hasBackLightDimmerEnable()) {
            mBackLightDimmerEnableCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mBackLightDimmerEnableCard.setDescription(getString(R.string.backlight_dimmer));
            mBackLightDimmerEnableCard.setChecked(Screen.isBackLightDimmerActive());
            mBackLightDimmerEnableCard.setOnDSwitchCompatCardListener(this);

            addView(mBackLightDimmerEnableCard);
        }

        if (Screen.hasMinBrightness()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= Screen.getMaxMinBrightness(); i++)
                list.add(String.valueOf(i));

            mMinBrightnessCard = new SeekBarCardView.DSeekBarCardView(list);
            mMinBrightnessCard.setTitle(getString(R.string.min_brightness));
            mMinBrightnessCard.setDescription(getString(R.string.min_brightness_summary));
            mMinBrightnessCard.setProgress(Screen.getCurMinBrightness());
            mMinBrightnessCard.setOnDSeekBarCardListener(this);

            addView(mMinBrightnessCard);
        }

        if (Screen.hasBackLightDimmerThreshold()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 51; i++)
                list.add(String.valueOf(i));

            mBackLightDimmerThresholdCard = new SeekBarCardView.DSeekBarCardView(list);
            mBackLightDimmerThresholdCard.setTitle(getString(R.string.backlight_dimmer_threshold));
            mBackLightDimmerThresholdCard.setProgress(Screen.getBackLightDimmerThreshold());
            mBackLightDimmerThresholdCard.setOnDSeekBarCardListener(this);

            addView(mBackLightDimmerThresholdCard);
        }

        if (Screen.hasBackLightDimmerOffset()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 51; i++)
                list.add(String.valueOf(i));

            mBackLightDimmerOffsetCard = new SeekBarCardView.DSeekBarCardView(list);
            mBackLightDimmerOffsetCard.setTitle(getString(R.string.backlight_dimmer_offset));
            mBackLightDimmerOffsetCard.setProgress(Screen.getBackLightDimmerOffset());
            mBackLightDimmerOffsetCard.setOnDSeekBarCardListener(this);

            addView(mBackLightDimmerOffsetCard);
        }
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        for (SeekBarCardView.DSeekBarCardView seekBarCardView : mColorCalibrationCard)
            if (dSeekBarCardView == seekBarCardView) {
                setColor(dSeekBarCardView, position);
                if (mColorCalibrationMinCard != null)
                    if (position < mColorCalibrationMinCard.getProgress()) {
                        dSeekBarCardView.setProgress(mColorCalibrationMinCard.getProgress());
                        setColor(dSeekBarCardView, mColorCalibrationMinCard.getProgress());
                    }
                return;
            }

        if (dSeekBarCardView == mColorCalibrationMinCard) {
            Screen.setColorCalibrationMin(Integer.parseInt(mColorCalibrationLimits.get(position)), getActivity());
            for (SeekBarCardView.DSeekBarCardView seekBarCardView : mColorCalibrationCard)
                if (position > seekBarCardView.getProgress()) {
                    seekBarCardView.setProgress(position);
                    setColor(seekBarCardView, position);
                }
        } else if (dSeekBarCardView == mSaturationIntensityCard)
            Screen.setSaturationIntensity(position + 225, getActivity());
        else if (dSeekBarCardView == mScreenHueCard)
            Screen.setScreenHue(position, getActivity());
        else if (dSeekBarCardView == mScreenValueCard)
            Screen.setScreenValue(position + 128, getActivity());
        else if (dSeekBarCardView == mScreenContrastCard)
            Screen.setScreenContrast(position + 128, getActivity());
    }

    private void setColor(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        String command = "";
        for (SeekBarCardView.DSeekBarCardView colorCard : mColorCalibrationCard) {
            String color;
            if (dSeekBarCardView == colorCard)
                color = mColorCalibrationLimits.get(position);
            else color = mColorCalibrationLimits.get(colorCard.getProgress());
            command += command.isEmpty() ? color : " " + color;
        }
        Screen.setColorCalibration(command, getActivity());
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mMinBrightnessCard)
            Screen.setMinBrightness(position, getActivity());
        else if (dSeekBarCardView == mBackLightDimmerThresholdCard)
            Screen.setBackLightDimmerThreshold(position, getActivity());
        else if (dSeekBarCardView == mBackLightDimmerOffsetCard)
            Screen.setBackLightDimmerOffset(position, getActivity());
    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mInvertScreenCard)
            Screen.activateInvertScreen(checked, getActivity());
        else if (dSwitchCompatCard == mGrayscaleModeCard) {
            mSaturationIntensityCard.setEnabled(!checked);
            Screen.activateGrayscaleMode(checked, getActivity());
            if (!checked) mSaturationIntensityCard.setProgress(30);
        } else if (dSwitchCompatCard == mBackLightDimmerEnableCard)
            Screen.activateBackLightDimmer(checked, getActivity());
    }

    private String getColor(int position) {
        switch (position) {
            case 0:
                return getString(R.string.red);
            case 1:
                return getString(R.string.green);
            case 2:
                return getString(R.string.blue);
            default:
                return null;
        }
    }

}
