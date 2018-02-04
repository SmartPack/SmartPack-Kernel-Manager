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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class SoundFragment extends RecyclerViewFragment {

    private Sound mSound;

    @Override
    protected void init() {
        super.init();

        mSound = Sound.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mSound.hasSoundControlEnable()) {
            soundControlEnableInit(items);
        }
        if (mSound.hasHighPerfModeEnable()) {
            highPerfModeEnableInit(items);
        }
        if (mSound.hasHeadphoneGain()) {
            headphoneGainInit(items);
        }
        if (mSound.hasHandsetMicrophoneGain()) {
            handsetMicrophoneGainInit(items);
        }
        if (mSound.hasCamMicrophoneGain()) {
            camMicrophoneGainInit(items);
        }
        if (mSound.hasHeadphoneFlar()) {
            headphoneFlarInit(items);
        }
        if (mSound.hasSpeakerGain()) {
            speakerGainInit(items);
        }
        if (mSound.hasMicrophoneFlar()) {
            microphoneFlarInit(items);
        }
        if (mSound.hasHeadphonePowerAmpGain()) {
            headphonePowerAmpGainInit(items);
        }
        if (mSound.hasHeadphoneTpaGain()) {
            headphoneTpaGainInit(items);
        }
        if (mSound.hasLockOutputGain()) {
            lockOutputGainInit(items);
        }
        if (mSound.hasLockMicGain()) {
            lockMicGainInit(items);
        }
        if (mSound.hasMicrophoneGain()) {
            microphoneGainInit(items);
        }
        if (mSound.hasVolumeGain()) {
            volumeGainInit(items);
        }
    }

    private void soundControlEnableInit(List<RecyclerViewItem> items) {
        SwitchView soundControl = new SwitchView();
        soundControl.setSummary(getString(R.string.sound_control));
        soundControl.setChecked(mSound.isSoundControlEnabled());
        soundControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mSound.enableSoundControl(isChecked, getActivity());
            }
        });

        items.add(soundControl);
    }

    private void highPerfModeEnableInit(List<RecyclerViewItem> items) {
        SwitchView highPerfMode = new SwitchView();
        highPerfMode.setSummary(getString(R.string.headset_highperf_mode));
        highPerfMode.setChecked(mSound.isHighPerfModeEnabled());
        highPerfMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mSound.enableHighPerfMode(isChecked, getActivity());
            }
        });

        items.add(highPerfMode);
    }

    private void headphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneGain = new SeekBarView();
        headphoneGain.setTitle(getString(R.string.headphone_gain));
        headphoneGain.setItems(mSound.getHeadphoneGainLimits());
        headphoneGain.setProgress(mSound.getHeadphoneGainLimits().indexOf(mSound.getHeadphoneGain()));
        headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphoneGain);
    }

    private void handsetMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView handsetMicrophoneGain = new SeekBarView();
        handsetMicrophoneGain.setTitle(getString(R.string.handset_microphone_gain));
        handsetMicrophoneGain.setItems(mSound.getHandsetMicrophoneGainLimits());
        handsetMicrophoneGain.setProgress(mSound.getHandsetMicrophoneGainLimits()
                .indexOf(mSound.getHandsetMicrophoneGain()));
        handsetMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHandsetMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(handsetMicrophoneGain);
    }

    private void camMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView camMicrophoneGain = new SeekBarView();
        camMicrophoneGain.setTitle(getString(R.string.cam_microphone_gain));
        camMicrophoneGain.setItems(mSound.getCamMicrophoneGainLimits());
        camMicrophoneGain.setProgress(mSound.getCamMicrophoneGainLimits().indexOf(mSound.getCamMicrophoneGain()));
        camMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setCamMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(camMicrophoneGain);
    }

    private void speakerGainInit(List<RecyclerViewItem> items) {
        SeekBarView speakerGain = new SeekBarView();
        speakerGain.setTitle(getString(R.string.speaker_gain));
        speakerGain.setItems(mSound.getSpeakerGainLimits());
        speakerGain.setProgress(mSound.getSpeakerGainLimits().indexOf(mSound.getSpeakerGain()));
        speakerGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setSpeakerGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(speakerGain);
    }

    private void headphonePowerAmpGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphonePowerAmpGain = new SeekBarView();
        headphonePowerAmpGain.setTitle(getString(R.string.headphone_poweramp_gain));
        headphonePowerAmpGain.setItems(mSound.getHeadphonePowerAmpGainLimits());
        headphonePowerAmpGain.setProgress(mSound.getHeadphonePowerAmpGainLimits()
                .indexOf(mSound.getHeadphonePowerAmpGain()));
        headphonePowerAmpGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphonePowerAmpGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphonePowerAmpGain);
    }

    private void headphoneTpaGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneTpaGain = new SeekBarView();
        headphoneTpaGain.setTitle(getString(R.string.headphone_tpa6165_gain));
        headphoneTpaGain.setItems(mSound.getHeadphoneTpaGainLimits());
        headphoneTpaGain.setProgress(mSound.getHeadphoneTpaGainLimits()
                .indexOf(mSound.getHeadphoneTpaGain()));
        headphoneTpaGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphoneTpaGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphoneTpaGain);
    }

    private void lockOutputGainInit(List<RecyclerViewItem> items) {
        SwitchView lockOutputGain = new SwitchView();
        lockOutputGain.setTitle(getString(R.string.lock_output_gain));
        lockOutputGain.setSummary(getString(R.string.lock_output_gain_summary));
        lockOutputGain.setChecked(mSound.isLockOutputGainEnabled());
        lockOutputGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mSound.enableLockOutputGain(isChecked, getActivity());
            }
        });

        items.add(lockOutputGain);
    }

    private void lockMicGainInit(List<RecyclerViewItem> items) {
        SwitchView lockMicGain = new SwitchView();
        lockMicGain.setTitle(getString(R.string.lock_mic_gain));
        lockMicGain.setSummary(getString(R.string.lock_mic_gain_summary));
        lockMicGain.setChecked(mSound.isLockMicGainEnabled());
        lockMicGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mSound.enableLockMicGain(isChecked, getActivity());
            }
        });

        items.add(lockMicGain);
    }

    private void microphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneGain = new SeekBarView();
        microphoneGain.setTitle(getString(R.string.microphone_gain));
        microphoneGain.setItems(mSound.getMicrophoneGainLimits());
        microphoneGain.setProgress(mSound.getMicrophoneGainLimits().indexOf(mSound.getMicrophoneGain()));
        microphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneGain);
    }

    private void volumeGainInit(List<RecyclerViewItem> items) {
        SeekBarView volumeGain = new SeekBarView();
        volumeGain.setTitle(getString(R.string.volume_gain));
        volumeGain.setItems(mSound.getVolumeGainLimits());
        volumeGain.setProgress(mSound.getVolumeGainLimits().indexOf(mSound.getVolumeGain()));
        volumeGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setVolumeGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(volumeGain);
    }

    private void headphoneFlarInit(List<RecyclerViewItem> items) {

        TitleView title = new TitleView();
        title.setText(getString(R.string.sound_control));

        SeekBarView headphoneFlar = new SeekBarView();
        headphoneFlar.setTitle(getString(R.string.headphone_gain));
        headphoneFlar.setItems(mSound.getHeadphoneFlarLimits());
        headphoneFlar.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar()));
        headphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphoneFlar(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });
        items.add(title);
        items.add(headphoneFlar);
    }

    private void microphoneFlarInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneFlar = new SeekBarView();
        microphoneFlar.setTitle(getString(R.string.microphone_gain));
        microphoneFlar.setItems(mSound.getMicrophoneFlarLimits());
        microphoneFlar.setProgress(mSound.getMicrophoneFlarLimits().indexOf(mSound.getMicrophoneFlar()));
        microphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setMicrophoneFlar(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneFlar);
    }

}
