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
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Wake;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.01.15.
 */
public class WakeFragment extends RecyclerViewFragment implements PopupCardView.DPopupCard.OnDPopupCardListener,
        SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener,
        SwitchCardView.DSwitchCard.OnDSwitchCardListener {

    private PopupCardView.DPopupCard mDt2wCard;
    private PopupCardView.DPopupCard mS2wCard;
    private PopupCardView.DPopupCard mT2wCard;
    private PopupCardView.DPopupCard mWakeMiscCard;
    private PopupCardView.DPopupCard mSleepMiscCard;
    private SwitchCardView.DSwitchCard[] mGestureCards;

    private SeekBarCardView.DSeekBarCard mWakeTimeoutCard;
    private SwitchCardView.DSwitchCard mPowerKeySuspendCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Wake.hasDt2w()) dt2wInit();
        if (Wake.hasS2w()) s2wInit();
        if (Wake.hasT2w()) t2wInit();
        if (Wake.hasWakeMisc()) wakeMiscInit();
        if (Wake.hasSleepMisc()) sleepMiscInit();
        if (Wake.hasGesture()) gestureInit();
        if (Wake.hasWakeTimeout()) wakeTimeoutInit();
        if (Wake.hasPowerKeySuspend()) powerKeySuspendInit();
    }

    private void dt2wInit() {
        mDt2wCard = new PopupCardView.DPopupCard(Wake.getDt2wMenu(getActivity()));
        mDt2wCard.setTitle(getString(R.string.dt2w));
        mDt2wCard.setDescription(getString(R.string.dt2w_summary));
        mDt2wCard.setItem(Wake.getDt2wValue());
        mDt2wCard.setOnDPopupCardListener(this);

        addView(mDt2wCard);
    }

    private void s2wInit() {
        mS2wCard = new PopupCardView.DPopupCard(Wake.getS2wMenu(getActivity()));
        mS2wCard.setTitle(getString(R.string.s2w));
        mS2wCard.setDescription(getString(R.string.s2w_summary));
        mS2wCard.setItem(Wake.getS2wValue());
        mS2wCard.setOnDPopupCardListener(this);

        addView(mS2wCard);
    }

    private void t2wInit() {
        mT2wCard = new PopupCardView.DPopupCard(Wake.getT2wMenu(getActivity()));
        mT2wCard.setTitle(getString(R.string.t2w));
        mT2wCard.setDescription(getString(R.string.t2w_summary));
        mT2wCard.setItem(Wake.getT2w());
        mT2wCard.setOnDPopupCardListener(this);

        addView(mT2wCard);
    }

    private void wakeMiscInit() {
        mWakeMiscCard = new PopupCardView.DPopupCard(Wake.getWakeMiscMenu(getActivity()));
        mWakeMiscCard.setDescription(getString(R.string.wake));
        mWakeMiscCard.setItem(Wake.getWakeMisc());
        mWakeMiscCard.setOnDPopupCardListener(this);

        addView(mWakeMiscCard);
    }

    private void sleepMiscInit() {
        mSleepMiscCard = new PopupCardView.DPopupCard(Wake.getSleepMiscMenu(getActivity()));
        mSleepMiscCard.setDescription(getString(R.string.sleep));
        mSleepMiscCard.setItem(Wake.getSleepMisc());
        mSleepMiscCard.setOnDPopupCardListener(this);

        addView(mSleepMiscCard);
    }

    private void gestureInit() {
        List<String> gestures = Wake.getGestures(getActivity());
        mGestureCards = new SwitchCardView.DSwitchCard[gestures.size()];
        for (int i = 0; i < mGestureCards.length; i++) {
            mGestureCards[i] = new SwitchCardView.DSwitchCard();
            mGestureCards[i].setDescription(gestures.get(i));
            mGestureCards[i].setChecked(Wake.isGestureActive(i));
            mGestureCards[i].setOnDSwitchCardListener(this);

            addView(mGestureCards[i]);
        }
    }

    private void wakeTimeoutInit() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i < 31; i++)
            list.add(i + getString(R.string.min));

        mWakeTimeoutCard = new SeekBarCardView.DSeekBarCard(list);
        mWakeTimeoutCard.setTitle(getString(R.string.wake_timeout));
        mWakeTimeoutCard.setDescription(getString(R.string.wake_timeout_summary));
        mWakeTimeoutCard.setProgress(Wake.getWakeTimeout());
        mWakeTimeoutCard.setOnDSeekBarCardListener(this);

        addView(mWakeTimeoutCard);
    }

    private void powerKeySuspendInit() {
        mPowerKeySuspendCard = new SwitchCardView.DSwitchCard();
        mPowerKeySuspendCard.setTitle(getString(R.string.power_key_suspend));
        mPowerKeySuspendCard.setDescription(getString(R.string.power_key_suspend_summary));
        mPowerKeySuspendCard.setChecked(Wake.isPowerKeySuspendActive());
        mPowerKeySuspendCard.setOnDSwitchCardListener(this);

        addView(mPowerKeySuspendCard);
    }

    @Override
    public void onItemSelected(PopupCardView.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mDt2wCard) Wake.setDt2w(position, getActivity());
        else if (dPopupCard == mS2wCard) Wake.setS2w(position, getActivity());
        else if (dPopupCard == mT2wCard) Wake.setT2w(position, getActivity());
        else if (dPopupCard == mWakeMiscCard) Wake.setWakeMisc(position, getActivity());
        else if (dPopupCard == mSleepMiscCard) Wake.setSleepMisc(position, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
        if (dSeekBarCard == mWakeTimeoutCard) Wake.setWakeTimeout(position, getActivity());
    }

    @Override
    public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
        if (dSwitchCard == mPowerKeySuspendCard)
            Wake.activatePowerKeySuspend(checked, getActivity());
        else {
            for (int i = 0; i < mGestureCards.length; i++)
                if (dSwitchCard == mGestureCards[i]) {
                    Wake.activateGesture(checked, i, getActivity());
                    return;
                }
        }
    }

}
