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
import com.grarak.kerneladiutor.elements.DDivider;
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.Led;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.10.15.
 */
public class LedFragment extends RecyclerViewFragment implements SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener, SwitchCardView.DSwitchCard.OnDSwitchCardListener {

    private SeekBarCardView.DSeekBarCard mLedHighpowerCurrentCard;
    private SeekBarCardView.DSeekBarCard mLedLowpowerCurrentCard;
    private SeekBarCardView.DSeekBarCard mLedNotificationDelayOnCard;
    private SeekBarCardView.DSeekBarCard mLedNotificationDelayOffCard;
    private SwitchCardView.DSwitchCard mLedNotificationRampControlCard;
    private SeekBarCardView.DSeekBarCard mLedNotificationRampUpCard;
    private SeekBarCardView.DSeekBarCard mLedNotificationRampDownCard;
    private SwitchCardView.DSwitchCard mLedPatternCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Led.hasLedHighpowerCurrent()) ledHighpowerCurrentInit();
        if (Led.hasLedLowpowerCurrent()) ledLowpowerCurrentInit();
        if (Led.hasLedNotificationDelayOn()) ledNotificationDelayOnInit();
        if (Led.hasLedNotificationDelayOff()) ledNotificationDelayOffInit();
        if (Led.hasLedNotificationRampControl()) ledNotificationRampControlInit();
        if (Led.hasLedNotificationRampUp()) ledNotificationRampUpInit();
        if (Led.hasLedNotificationRampDown()) ledNotificationRampDownInit();
        if (Led.hasLedPattern()) ledPatternInit();
    }

    private void ledHighpowerCurrentInit() {
        DDivider mLedHighpowerCurrentDivider = new DDivider();
        mLedHighpowerCurrentDivider.setText(getString(R.string.led_brightness));

        addView(mLedHighpowerCurrentDivider);

        List<String> list = new ArrayList<>();
        for (int i = 0; i <= 51; i++)
            list.add((i * 5) + getString(R.string.ma));

        mLedHighpowerCurrentCard = new SeekBarCardView.DSeekBarCard(list);
        mLedHighpowerCurrentCard.setTitle(getString(R.string.led_highpower_current));
        mLedHighpowerCurrentCard.setDescription(getString(R.string.led_highpower_current_summary));
        mLedHighpowerCurrentCard.setProgress(Led.getLedHighpowerCurrent() / 5);
        mLedHighpowerCurrentCard.setOnDSeekBarCardListener(this);

        addView(mLedHighpowerCurrentCard);
    }

    private void ledLowpowerCurrentInit() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= 51; i++)
            list.add((i * 5) + getString(R.string.ma));

        mLedLowpowerCurrentCard = new SeekBarCardView.DSeekBarCard(list);
        mLedLowpowerCurrentCard.setTitle(getString(R.string.led_lowpower_current));
        mLedLowpowerCurrentCard.setDescription(getString(R.string.led_lowpower_current_summary));
        mLedLowpowerCurrentCard.setProgress(Led.getLedLowpowerCurrent() / 5);
        mLedLowpowerCurrentCard.setOnDSeekBarCardListener(this);

        addView(mLedLowpowerCurrentCard);
    }

    private void ledNotificationDelayOnInit() {
        DDivider mLedNotificationDelayOnDivider = new DDivider();
        mLedNotificationDelayOnDivider.setText(getString(R.string.led_notification_delay));

        addView(mLedNotificationDelayOnDivider);

        List<String> list = new ArrayList<>();
        for (double i = 0; i < 101; i+= 5)
            list.add((i / 10) + getString(R.string.sec));

        mLedNotificationDelayOnCard = new SeekBarCardView.DSeekBarCard(list);
        mLedNotificationDelayOnCard.setTitle(getString(R.string.led_notification_delay_on));
        mLedNotificationDelayOnCard.setDescription(getString(R.string.led_notification_delay_on_summary));
        mLedNotificationDelayOnCard.setProgress(Led.getLedNotificationDelayOn());
        mLedNotificationDelayOnCard.setOnDSeekBarCardListener(this);

        addView(mLedNotificationDelayOnCard);
    }

    private void ledNotificationDelayOffInit() {
        List<String> list = new ArrayList<>();
        for (double i = 0; i < 101; i+= 5)
            list.add((i / 10) + getString(R.string.sec));

        mLedNotificationDelayOffCard = new SeekBarCardView.DSeekBarCard(list);
        mLedNotificationDelayOffCard.setTitle(getString(R.string.led_notification_delay_off));
        mLedNotificationDelayOffCard.setDescription(getString(R.string.led_notification_delay_off_summary));
        mLedNotificationDelayOffCard.setProgress(Led.getLedNotificationDelayOff());
        mLedNotificationDelayOffCard.setOnDSeekBarCardListener(this);

        addView(mLedNotificationDelayOffCard);
    }

    private void ledNotificationRampControlInit() {
        DDivider mLedNotificationRampControlDivider = new DDivider();
        mLedNotificationRampControlDivider.setText(getString(R.string.led_notification_ramp_control));

        addView(mLedNotificationRampControlDivider);

        mLedNotificationRampControlCard = new SwitchCardView.DSwitchCard();
        mLedNotificationRampControlCard.setTitle(getString(R.string.led_notification_ramp_control));
        mLedNotificationRampControlCard.setDescription(getString(R.string.led_notification_ramp_control_summary));
        mLedNotificationRampControlCard.setChecked(Led.isLedNotificationRampControlActive());
        mLedNotificationRampControlCard.setOnDSwitchCardListener(this);

        addView(mLedNotificationRampControlCard);
    }

    private void ledNotificationRampUpInit() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 21; i++)
            list.add((i * 100) + getString(R.string.ms));

        mLedNotificationRampUpCard = new SeekBarCardView.DSeekBarCard(list);
        mLedNotificationRampUpCard.setTitle(getString(R.string.led_notification_ramp_up));
        mLedNotificationRampUpCard.setDescription(getString(R.string.led_notification_ramp_up_summary));
        mLedNotificationRampUpCard.setProgress(Led.getLedNotificationRampUp() / 100);
        mLedNotificationRampUpCard.setOnDSeekBarCardListener(this);

        addView(mLedNotificationRampUpCard);
    }

    private void ledNotificationRampDownInit() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 21; i++)
            list.add((i * 100) + getString(R.string.ms));

        mLedNotificationRampDownCard = new SeekBarCardView.DSeekBarCard(list);
        mLedNotificationRampDownCard.setTitle(getString(R.string.led_notification_ramp_down));
        mLedNotificationRampDownCard.setDescription(getString(R.string.led_notification_ramp_down_summary));
        mLedNotificationRampDownCard.setProgress(Led.getLedNotificationRampDown() / 100);
        mLedNotificationRampDownCard.setOnDSeekBarCardListener(this);

        addView(mLedNotificationRampDownCard);
    }

    private void ledPatternInit() {
        DDivider mLedPatternDivider = new DDivider();
        mLedPatternDivider.setText(getString(R.string.led_pattern));

        addView(mLedPatternDivider);

        mLedPatternCard = new SwitchCardView.DSwitchCard();
        mLedPatternCard.setTitle(getString(R.string.led_pattern));
        mLedPatternCard.setDescription(getString(R.string.led_pattern_summary));
        mLedPatternCard.setChecked(Led.isLedPatternActive());
        mLedPatternCard.setOnDSwitchCardListener(this);

        addView(mLedPatternCard);
    }

    @Override
    public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
        if (dSwitchCard == mLedNotificationRampControlCard)
            Led.activateLedNotificationRampControl(checked, getActivity());
        else if (dSwitchCard == mLedPatternCard)
            Led.activateLedPattern(checked, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
        if (dSeekBarCard == mLedHighpowerCurrentCard) Led.setLedHighpowerCurrent(position * 5, getActivity());
        else if (dSeekBarCard == mLedLowpowerCurrentCard) Led.setLedLowpowerCurrent(position * 5, getActivity());
        else if (dSeekBarCard == mLedNotificationDelayOnCard) Led.setLedNotificationDelayOn(position_translate(position), getActivity());
        else if (dSeekBarCard == mLedNotificationDelayOffCard) Led.setLedNotificationDelayOff(position_translate(position), getActivity());
        else if (dSeekBarCard == mLedNotificationRampUpCard) Led.setLedNotificationRampUp(position * 100, getActivity());
        else if (dSeekBarCard == mLedNotificationRampDownCard) Led.setLedNotificationRampDown(position * 100, getActivity());
    }

    private Integer position_translate(Integer position){
        Integer result = 0;
        switch (position){
            case 0:
                result = 0;
                break;
            case 1:
                result = 500;
                break;
            case 2:
                result = 1000;
                break;
            case 3:
                result = 1500;
                break;
            case 4:
                result = 2000;
                break;
            case 5:
                result = 2500;
                break;
            case 6:
                result = 3000;
                break;
            case 7:
                result = 3500;
                break;
            case 8:
                result = 4000;
                break;
            case 9:
                result = 4500;
                break;
            case 10:
                result = 5000;
                break;
            case 11:
                result = 5500;
                break;
            case 12:
                result = 6000;
                break;
            case 13:
                result = 6500;
                break;
            case 14:
                result = 7000;
                break;
            case 15:
                result = 7500;
                break;
            case 16:
                result = 8000;
                break;
            case 17:
                result = 8500;
                break;
            case 18:
                result = 9000;
                break;
            case 19:
                result = 9500;
                break;
            case 20:
                result = 10000;
                break;
            default:
                result = 1000;
                break;
        }
        return result;

    }

}
