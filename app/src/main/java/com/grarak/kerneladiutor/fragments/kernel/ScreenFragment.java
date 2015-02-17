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
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.utils.kernel.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ScreenFragment extends RecyclerViewFragment implements SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener,
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener {

    private SeekBarCardView.DSeekBarCardView[] mColorCalibrationCard;

    private SwitchCompatCardItem.DSwitchCompatCard mBackLightDimmerEnableCard;
    private SeekBarCardView.DSeekBarCardView mMinBrightnessCard;
    private SeekBarCardView.DSeekBarCardView mBackLightDimmerThresholdCard;
    private SeekBarCardView.DSeekBarCardView mBackLightDimmerOffsetCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Screen.hasColorCalibration()) screenColorInit();
        backlightDimmerInit();
    }

    private void screenColorInit() {
        List<String> colors = Screen.getColorCalibration();
        mColorCalibrationCard = new SeekBarCardView.DSeekBarCardView[colors.size()];
        for (int i = 0; i < mColorCalibrationCard.length; i++) {
            mColorCalibrationCard[i] = new SeekBarCardView.DSeekBarCardView(Screen.getColorCalibrationLimits());
            mColorCalibrationCard[i].setTitle(getColor(i));
            mColorCalibrationCard[i].setProgress(Screen.getColorCalibrationLimits().indexOf(colors.get(i)));
            mColorCalibrationCard[i].setOnDSeekBarCardListener(this);

            addView(mColorCalibrationCard[i]);
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
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mMinBrightnessCard)
            Screen.setMinBrightness(position, getActivity());
        if (dSeekBarCardView == mBackLightDimmerThresholdCard)
            Screen.setBackLightDimmerThreshold(position, getActivity());
        if (dSeekBarCardView == mBackLightDimmerOffsetCard)
            Screen.setBackLightDimmerOffset(position, getActivity());

        for (SeekBarCardView.DSeekBarCardView seekBarCardView : mColorCalibrationCard)
            if (dSeekBarCardView == seekBarCardView) {
                List<String> colors = Screen.getColorCalibration();
                List<String> list = Screen.getColorCalibrationLimits();
                String color = "";

                for (int i = 0; i < mColorCalibrationCard.length; i++)
                    if (dSeekBarCardView == mColorCalibrationCard[i])
                        color += color.isEmpty() ? list.get(position) : " " + list.get(position);
                    else
                        color += color.isEmpty() ? colors.get(i) : " " + colors.get(i);

                Screen.setColorCalibration(color, getActivity());
            }
    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mBackLightDimmerEnableCard)
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
