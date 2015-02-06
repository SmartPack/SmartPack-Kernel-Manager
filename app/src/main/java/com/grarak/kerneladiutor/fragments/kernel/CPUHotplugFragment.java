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
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.kernel.CPUHotplug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 06.02.15.
 */
public class CPUHotplugFragment extends RecyclerViewFragment implements
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener,
        PopupCardItem.DPopupCard.OnDPopupCardListener, SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener {

    private SwitchCompatCardItem.DSwitchCompatCard mMpdecisionCard;

    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugCard;
    private PopupCardItem.DPopupCard mIntelliPlugProfileCard;
    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugEcoCard;
    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugTouchBoostCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugHysteresisCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugThresoldCard;
    private PopupCardItem.DPopupCard mIntelliPlugScreenOffMaxCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (CPUHotplug.hasMpdecision()) mpdecisionInit();
        if (CPUHotplug.hasIntelliPlug()) intelliPlugInit();
    }

    private void mpdecisionInit() {
        mMpdecisionCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mMpdecisionCard.setTitle(getString(R.string.mpdecision));
        mMpdecisionCard.setDescription(getString(R.string.mpdecision_summary));
        mMpdecisionCard.setChecked(CPUHotplug.isMpdecisionActive());
        mMpdecisionCard.setOnDSwitchCompatCardListener(this);

        addView(mMpdecisionCard);
    }

    private void intelliPlugInit() {
        mIntelliPlugCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mIntelliPlugCard.setTitle(getString(R.string.intelliplug));
        mIntelliPlugCard.setDescription(getString(R.string.intelliplug_summary));
        mIntelliPlugCard.setChecked(CPUHotplug.isIntelliPlugActive());
        mIntelliPlugCard.setOnDSwitchCompatCardListener(this);

        addView(mIntelliPlugCard);

        if (CPUHotplug.hasIntelliPlugProfile()) {
            mIntelliPlugProfileCard = new PopupCardItem.DPopupCard(
                    new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.intelliplug_profile_items))));
            mIntelliPlugProfileCard.setTitle(getString(R.string.intelliplug_profile));
            mIntelliPlugProfileCard.setDescription(getString(R.string.intelliplug_profile_summary));
            mIntelliPlugProfileCard.setItem(CPUHotplug.getIntelliPlugProfile());
            mIntelliPlugProfileCard.setOnDPopupCardListener(this);

            addView(mIntelliPlugProfileCard);
        }

        if (CPUHotplug.hasIntelliPlugEco()) {
            mIntelliPlugEcoCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugEcoCard.setTitle(getString(R.string.intelliplug_eco_mode_summary));
            mIntelliPlugEcoCard.setDescription(getString(R.string.intelliplug_eco_mode_summary));
            mIntelliPlugEcoCard.setChecked(CPUHotplug.isIntelliPlugEcoActive());
            mIntelliPlugEcoCard.setOnDSwitchCompatCardListener(this);

            addView(mIntelliPlugEcoCard);
        }

        if (CPUHotplug.hasIntelliPlugTouchBoost()) {
            mIntelliPlugTouchBoostCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugTouchBoostCard.setTitle(getString(R.string.intelliplug_touch_boost));
            mIntelliPlugTouchBoostCard.setDescription(getString(R.string.intelliplug_touch_boost_summary));
            mIntelliPlugTouchBoostCard.setChecked(CPUHotplug.isIntelliPlugTouchBoostActive());
            mIntelliPlugTouchBoostCard.setOnDSwitchCompatCardListener(this);

            addView(mIntelliPlugTouchBoostCard);
        }

        if (CPUHotplug.hasIntelliPlugHysteresis()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 17; i++)
                list.add(String.valueOf(i));

            mIntelliPlugHysteresisCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugHysteresisCard.setTitle(getString(R.string.intelliplug_hysteresis));
            mIntelliPlugHysteresisCard.setDescription(getString(R.string.intelliplug_hysteresis_summary));
            mIntelliPlugHysteresisCard.setProgress(CPUHotplug.getIntelliPlugHysteresis());
            mIntelliPlugHysteresisCard.setOnDSeekBarCardListener(this);

            addView(mIntelliPlugHysteresisCard);
        }

        if (CPUHotplug.hasIntelliPlugThresold()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 1001; i++)
                list.add(String.valueOf(i));

            mIntelliPlugThresoldCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugThresoldCard.setTitle(getString(R.string.intelliplug_thresold));
            mIntelliPlugThresoldCard.setProgress(CPUHotplug.getIntelliPlugThresold());
            mIntelliPlugThresoldCard.setOnDSeekBarCardListener(this);

            addView(mIntelliPlugThresoldCard);
        }

        if (CPUHotplug.hasIntelliPlugScreenOffMax()) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mIntelliPlugScreenOffMaxCard = new PopupCardItem.DPopupCard(list);
            mIntelliPlugScreenOffMaxCard.setDescription(getString(R.string.intelliplug_screen_off_max));
            mIntelliPlugScreenOffMaxCard.setItem(CPUHotplug.getIntelliPlugScreenOffMax());
            mIntelliPlugScreenOffMaxCard.setOnDPopupCardListener(this);

            addView(mIntelliPlugScreenOffMaxCard);
        }

    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mMpdecisionCard)
            CPUHotplug.activateMpdecision(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugCard)
            CPUHotplug.activateIntelliPlug(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugEcoCard)
            CPUHotplug.activateIntelliPlugEco(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugTouchBoostCard)
            CPUHotplug.activateIntelliPlugTouchBoost(checked, getActivity());
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mIntelliPlugProfileCard)
            CPUHotplug.setIntelliPlugProfile(position, getActivity());
        if (dPopupCard == mIntelliPlugScreenOffMaxCard)
            CPUHotplug.setIntelliPlugScreenOffMax(position, getActivity());
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mIntelliPlugHysteresisCard)
            CPUHotplug.setIntelliPlugHysteresis(position, getActivity());
        if (dSeekBarCardView == mIntelliPlugThresoldCard)
            CPUHotplug.setIntelliPlugThresold(position, getActivity());
    }

}
