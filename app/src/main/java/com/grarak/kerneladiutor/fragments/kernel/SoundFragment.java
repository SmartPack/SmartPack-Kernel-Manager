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
import com.grarak.kerneladiutor.utils.kernel.Sound;

/**
 * Created by willi on 06.01.15.
 */
public class SoundFragment extends RecyclerViewFragment implements
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener,
        SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener {

    private SwitchCompatCardItem.DSwitchCompatCard mSoundControlEnableCard;
    private SeekBarCardView.DSeekBarCardView mHeadphoneGainCard;
    private SeekBarCardView.DSeekBarCardView mHandsetMicrophoneGainCard;
    private SeekBarCardView.DSeekBarCardView mCamMicrophoneGainCard;
    private SeekBarCardView.DSeekBarCardView mSpeakerGainCard;
    private SeekBarCardView.DSeekBarCardView mHeadphonePowerAmpGainCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Sound.hasSoundControlEnable()) soundControlEnableInit();
        if (Sound.hasHeadphoneGain()) headphoneGainInit();
        if (Sound.hasHandsetMicrophoneGain()) handsetMicrophoneGainInit();
        if (Sound.hasCamMicrophoneGain()) camMicrophoneGainInit();
        if (Sound.hasSpeakerGain()) speakerGainInit();
        if (Sound.hasHeadphonePowerAmpGain()) headphonePowerAmpGainInit();
    }

    private void soundControlEnableInit() {
        mSoundControlEnableCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mSoundControlEnableCard.setDescription(getString(R.string.sound_control));
        mSoundControlEnableCard.setChecked(Sound.isSoundControlActive());
        mSoundControlEnableCard.setOnDSwitchCompatCardListener(this);

        addView(mSoundControlEnableCard);
    }

    private void headphoneGainInit() {
        mHeadphoneGainCard = new SeekBarCardView.DSeekBarCardView(Sound.getHeadphoneGainLimits());
        mHeadphoneGainCard.setTitle(getString(R.string.headphone_gain));
        mHeadphoneGainCard.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getCurHeadphoneGain()));
        mHeadphoneGainCard.setOnDSeekBarCardListener(this);

        addView(mHeadphoneGainCard);
    }

    private void handsetMicrophoneGainInit() {
        mHandsetMicrophoneGainCard = new SeekBarCardView.DSeekBarCardView(Sound.getHandsetMicrophoneGainLimits());
        mHandsetMicrophoneGainCard.setTitle(getString(R.string.handset_microphone_gain));
        mHandsetMicrophoneGainCard.setProgress(Sound.getHandsetMicrophoneGainLimits().indexOf(
                Sound.getCurHandsetMicrophoneGain()));
        mHandsetMicrophoneGainCard.setOnDSeekBarCardListener(this);

        addView(mHandsetMicrophoneGainCard);
    }

    private void camMicrophoneGainInit() {
        mCamMicrophoneGainCard = new SeekBarCardView.DSeekBarCardView(Sound.getCamMicrophoneGainLimits());
        mCamMicrophoneGainCard.setTitle(getString(R.string.cam_microphone_gain));
        mCamMicrophoneGainCard.setProgress(Sound.getCamMicrophoneGainLimits().indexOf(Sound.getCurCamMicrophoneGain()));
        mCamMicrophoneGainCard.setOnDSeekBarCardListener(this);

        addView(mCamMicrophoneGainCard);
    }

    private void speakerGainInit() {
        mSpeakerGainCard = new SeekBarCardView.DSeekBarCardView(Sound.getSpeakerGainLimits());
        mSpeakerGainCard.setTitle(getString(R.string.speaker_gain));
        mSpeakerGainCard.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getCurSpeakerGain()));
        mSpeakerGainCard.setOnDSeekBarCardListener(this);

        addView(mSpeakerGainCard);
    }

    private void headphonePowerAmpGainInit() {
        mHeadphonePowerAmpGainCard = new SeekBarCardView.DSeekBarCardView(Sound.getHeadphonePowerAmpGainLimits());
        mHeadphonePowerAmpGainCard.setTitle(getString(R.string.headphone_poweramp_gain));
        mHeadphonePowerAmpGainCard.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(
                Sound.getCurHeadphonePowerAmpGain()));
        mHeadphonePowerAmpGainCard.setOnDSeekBarCardListener(this);

        addView(mHeadphonePowerAmpGainCard);
    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mSoundControlEnableCard)
            Sound.activateSoundControl(checked, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mHeadphoneGainCard)
            Sound.setHeadphoneGain(Sound.getHeadphoneGainLimits().get(position), getActivity());
        else if (dSeekBarCardView == mHandsetMicrophoneGainCard)
            Sound.setHandsetMicrophoneGain(Sound.getHandsetMicrophoneGainLimits().get(position), getActivity());
        else if (dSeekBarCardView == mCamMicrophoneGainCard)
            Sound.setCamMicrophoneGain(Sound.getCamMicrophoneGainLimits().get(position), getActivity());
        else if (dSeekBarCardView == mSpeakerGainCard)
            Sound.setSpeakerGain(Sound.getSpeakerGainLimits().get(position), getActivity());
        else if (dSeekBarCardView == mHeadphonePowerAmpGainCard)
            Sound.setHeadphonePowerAmpGain(Sound.getHeadphonePowerAmpGainLimits().get(position), getActivity());
    }
}
