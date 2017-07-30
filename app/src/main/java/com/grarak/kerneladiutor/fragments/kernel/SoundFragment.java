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

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Sound.hasSoundControlEnable()) {
            soundControlEnableInit(items);
        }
        if (Sound.hasHighPerfModeEnable()) {
            highPerfModeEnableInit(items);
        }
        if (Sound.hasHeadphoneGain()) {
            headphoneGainInit(items);
        }
        if (Sound.hasHandsetMicrophoneGain()) {
            handsetMicrophoneGainInit(items);
        }
        if (Sound.hasCamMicrophoneGain()) {
            camMicrophoneGainInit(items);
        }
        if (Sound.hasHeadphoneFlar()) {
            headphoneFlarInit(items);
        }
        if (Sound.hasSpeakerGain()) {
            speakerGainInit(items);
        }
        if (Sound.hasMicrophoneFlar()) {
            microphoneFlarInit(items);
        }        
        if (Sound.hasHeadphonePowerAmpGain()) {
            headphonePowerAmpGainInit(items);
        }
        if (Sound.hasHeadphoneTpaGain()) {
            headphoneTpaGainInit(items);
        }
        if (Sound.hasLockOutputGain()) {
            lockOutputGainInit(items);
        }
        if (Sound.hasLockMicGain()) {
            lockMicGainInit(items);
        }
        if (Sound.hasMicrophoneGain()) {
            microphoneGainInit(items);
        }
        if (Sound.hasVolumeGain()) {
            volumeGainInit(items);
        }
    }

    private void soundControlEnableInit(List<RecyclerViewItem> items) {
        SwitchView soundControl = new SwitchView();
        soundControl.setSummary(getString(R.string.sound_control));
        soundControl.setChecked(Sound.isSoundControlEnabled());
        soundControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableSoundControl(isChecked, getActivity());
            }
        });

        items.add(soundControl);
    }

    private void highPerfModeEnableInit(List<RecyclerViewItem> items) {
        SwitchView highPerfMode = new SwitchView();
        highPerfMode.setSummary(getString(R.string.headset_highperf_mode));
        highPerfMode.setChecked(Sound.isHighPerfModeEnabled());
        highPerfMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableHighPerfMode(isChecked, getActivity());
            }
        });

        items.add(highPerfMode);
    }

    private void headphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneGain = new SeekBarView();
        headphoneGain.setTitle(getString(R.string.headphone_gain));
        headphoneGain.setItems(Sound.getHeadphoneGainLimits());
        headphoneGain.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain()));
        headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneGain(value, getActivity());
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
        handsetMicrophoneGain.setItems(Sound.getHandsetMicrophoneGainLimits());
        handsetMicrophoneGain.setProgress(Sound.getHandsetMicrophoneGainLimits()
                .indexOf(Sound.getHandsetMicrophoneGain()));
        handsetMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHandsetMicrophoneGain(value, getActivity());
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
        camMicrophoneGain.setItems(Sound.getCamMicrophoneGainLimits());
        camMicrophoneGain.setProgress(Sound.getCamMicrophoneGainLimits().indexOf(Sound.getCamMicrophoneGain()));
        camMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setCamMicrophoneGain(value, getActivity());
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
        speakerGain.setItems(Sound.getSpeakerGainLimits());
        speakerGain.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain()));
        speakerGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setSpeakerGain(value, getActivity());
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
        headphonePowerAmpGain.setItems(Sound.getHeadphonePowerAmpGainLimits());
        headphonePowerAmpGain.setProgress(Sound.getHeadphonePowerAmpGainLimits()
                .indexOf(Sound.getHeadphonePowerAmpGain()));
        headphonePowerAmpGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphonePowerAmpGain(value, getActivity());
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
        headphoneTpaGain.setItems(Sound.getHeadphoneTpaGainLimits());
        headphoneTpaGain.setProgress(Sound.getHeadphoneTpaGainLimits()
                .indexOf(Sound.getHeadphoneTpaGain()));
        headphoneTpaGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneTpaGain(value, getActivity());
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
        lockOutputGain.setChecked(Sound.isLockOutputGainEnabled());
        lockOutputGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableLockOutputGain(isChecked, getActivity());
            }
        });

        items.add(lockOutputGain);
    }

    private void lockMicGainInit(List<RecyclerViewItem> items) {
        SwitchView lockMicGain = new SwitchView();
        lockMicGain.setTitle(getString(R.string.lock_mic_gain));
        lockMicGain.setSummary(getString(R.string.lock_mic_gain_summary));
        lockMicGain.setChecked(Sound.isLockMicGainEnabled());
        lockMicGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableLockMicGain(isChecked, getActivity());
            }
        });

        items.add(lockMicGain);
    }

    private void microphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneGain = new SeekBarView();
        microphoneGain.setTitle(getString(R.string.microphone_gain));
        microphoneGain.setItems(Sound.getMicrophoneGainLimits());
        microphoneGain.setProgress(Sound.getMicrophoneGainLimits().indexOf(Sound.getMicrophoneGain()));
        microphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setMicrophoneGain(value, getActivity());
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
        volumeGain.setItems(Sound.getVolumeGainLimits());
        volumeGain.setProgress(Sound.getVolumeGainLimits().indexOf(Sound.getVolumeGain()));
        volumeGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setVolumeGain(value, getActivity());
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
        headphoneFlar.setItems(Sound.getHeadphoneFlarLimits());
        headphoneFlar.setProgress(Sound.getHeadphoneFlarLimits().indexOf(Sound.getHeadphoneFlar()));
        headphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneFlar(value, getActivity());
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
        microphoneFlar.setItems(Sound.getMicrophoneFlarLimits());
        microphoneFlar.setProgress(Sound.getMicrophoneFlarLimits().indexOf(Sound.getMicrophoneFlar()));
        microphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setMicrophoneFlar(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneFlar);
    }

}
