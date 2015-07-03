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
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Sound;

/**
 * Created by willi on 06.01.15.
 */
public class SoundFragment extends RecyclerViewFragment implements
        SwitchCardView.DSwitchCard.OnDSwitchCardListener,
        SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener {

    private SwitchCardView.DSwitchCard mSoundControlEnableCard;
    private SeekBarCardView.DSeekBarCard mHeadphoneGainCard;
    private SeekBarCardView.DSeekBarCard mHandsetMicrophoneGainCard;
    private SeekBarCardView.DSeekBarCard mCamMicrophoneGainCard;
    private SeekBarCardView.DSeekBarCard mSpeakerGainCard;
    private SeekBarCardView.DSeekBarCard mHeadphonePowerAmpGainCard;
    private SeekBarCardView.DSeekBarCard mMicrophoneGainCard;
    private SeekBarCardView.DSeekBarCard mVolumeGainCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Sound.hasSoundControlEnable()) soundControlEnableInit();
        if (Sound.hasHeadphoneGain()) headphoneGainInit();
        if (Sound.hasHandsetMicrophoneGain()) handsetMicrophoneGainInit();
        if (Sound.hasCamMicrophoneGain()) camMicrophoneGainInit();
        if (Sound.hasSpeakerGain()) speakerGainInit();
        if (Sound.hasHeadphonePowerAmpGain()) headphonePowerAmpGainInit();
        if (Sound.hasMicrophoneGain()) microphoneGainInit();
        if (Sound.hasVolumeGain()) volumeGainInit();
    }

    private void soundControlEnableInit() {
        mSoundControlEnableCard = new SwitchCardView.DSwitchCard();
        mSoundControlEnableCard.setDescription(getString(R.string.sound_control));
        mSoundControlEnableCard.setChecked(Sound.isSoundControlActive());
        mSoundControlEnableCard.setOnDSwitchCardListener(this);

        addView(mSoundControlEnableCard);
    }

    private void headphoneGainInit() {
        mHeadphoneGainCard = new SeekBarCardView.DSeekBarCard(Sound.getHeadphoneGainLimits());
        mHeadphoneGainCard.setTitle(getString(R.string.headphone_gain));
        mHeadphoneGainCard.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getCurHeadphoneGain()));
        mHeadphoneGainCard.setOnDSeekBarCardListener(this);

        addView(mHeadphoneGainCard);
    }

    private void handsetMicrophoneGainInit() {
        mHandsetMicrophoneGainCard = new SeekBarCardView.DSeekBarCard(Sound.getHandsetMicrophoneGainLimits());
        mHandsetMicrophoneGainCard.setTitle(getString(R.string.handset_microphone_gain));
        mHandsetMicrophoneGainCard.setProgress(Sound.getHandsetMicrophoneGainLimits().indexOf(
                Sound.getCurHandsetMicrophoneGain()));
        mHandsetMicrophoneGainCard.setOnDSeekBarCardListener(this);

        addView(mHandsetMicrophoneGainCard);
    }

    private void camMicrophoneGainInit() {
        mCamMicrophoneGainCard = new SeekBarCardView.DSeekBarCard(Sound.getCamMicrophoneGainLimits());
        mCamMicrophoneGainCard.setTitle(getString(R.string.cam_microphone_gain));
        mCamMicrophoneGainCard.setProgress(Sound.getCamMicrophoneGainLimits().indexOf(Sound.getCurCamMicrophoneGain()));
        mCamMicrophoneGainCard.setOnDSeekBarCardListener(this);

        addView(mCamMicrophoneGainCard);
    }

    private void speakerGainInit() {
        mSpeakerGainCard = new SeekBarCardView.DSeekBarCard(Sound.getSpeakerGainLimits());
        mSpeakerGainCard.setTitle(getString(R.string.speaker_gain));
        mSpeakerGainCard.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getCurSpeakerGain()));
        mSpeakerGainCard.setOnDSeekBarCardListener(this);

        addView(mSpeakerGainCard);
    }

    private void headphonePowerAmpGainInit() {
        mHeadphonePowerAmpGainCard = new SeekBarCardView.DSeekBarCard(Sound.getHeadphonePowerAmpGainLimits());
        mHeadphonePowerAmpGainCard.setTitle(getString(R.string.headphone_poweramp_gain));
        mHeadphonePowerAmpGainCard.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(
                Sound.getCurHeadphonePowerAmpGain()));
        mHeadphonePowerAmpGainCard.setOnDSeekBarCardListener(this);

        addView(mHeadphonePowerAmpGainCard);
    }

    private void microphoneGainInit() {
        mMicrophoneGainCard = new SeekBarCardView.DSeekBarCard(Sound.getMicrophoneGainLimits());
        mMicrophoneGainCard.setTitle(getString(R.string.microphone_gain));
        mMicrophoneGainCard.setProgress(Sound.getMicrophoneGainLimits().indexOf(Sound.getMicrophoneGain()));
        mMicrophoneGainCard.setOnDSeekBarCardListener(this);

        addView(mMicrophoneGainCard);
    }

    private void volumeGainInit() {
        mVolumeGainCard = new SeekBarCardView.DSeekBarCard(Sound.getVolumeGainLimits());
        mVolumeGainCard.setTitle(getString(R.string.volume_gain));
        mVolumeGainCard.setProgress(Sound.getVolumeGainLimits().indexOf(Sound.getVolumeGain()));
        mVolumeGainCard.setOnDSeekBarCardListener(this);

        addView(mVolumeGainCard);
    }

    @Override
    public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
        if (dSwitchCard == mSoundControlEnableCard)
            Sound.activateSoundControl(checked, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
        if (dSeekBarCard == mHeadphoneGainCard)
            Sound.setHeadphoneGain(Sound.getHeadphoneGainLimits().get(position), getActivity());
        else if (dSeekBarCard == mHandsetMicrophoneGainCard)
            Sound.setHandsetMicrophoneGain(Sound.getHandsetMicrophoneGainLimits().get(position), getActivity());
        else if (dSeekBarCard == mCamMicrophoneGainCard)
            Sound.setCamMicrophoneGain(Sound.getCamMicrophoneGainLimits().get(position), getActivity());
        else if (dSeekBarCard == mSpeakerGainCard)
            Sound.setSpeakerGain(Sound.getSpeakerGainLimits().get(position), getActivity());
        else if (dSeekBarCard == mHeadphonePowerAmpGainCard)
            Sound.setHeadphonePowerAmpGain(Sound.getHeadphonePowerAmpGainLimits().get(position), getActivity());
        else if (dSeekBarCard == mMicrophoneGainCard)
            Sound.setMicrophoneGain(Sound.getMicrophoneGainLimits().get(position), getActivity());
        else if (dSeekBarCard == mVolumeGainCard)
            Sound.setVolumeGain(Sound.getVolumeGainLimits().get(position), getActivity());
    }
}
