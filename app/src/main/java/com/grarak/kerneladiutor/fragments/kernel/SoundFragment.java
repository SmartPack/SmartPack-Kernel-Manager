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
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
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
        if (mSound.hasSoundControlDir()) {
            SoundControlInit(items);
        }
    }

    private void SoundControlInit(List<RecyclerViewItem> items) {
        CardView SoundControlCard = new CardView(getActivity());
        SoundControlCard.setTitle(getString(R.string.sound_control));

	if (mSound.hasboefflasoundenable()) {
            SwitchView boefflasoundenable = new SwitchView();
            boefflasoundenable.setTitle(getString(R.string.boefflasound) + " Version: " + mSound.getboefflasoundVersion());
            boefflasoundenable.setSummary(getString(R.string.boefflasound_summary));
            boefflasoundenable.setChecked(mSound.isboefflasoundenabled());
            boefflasoundenable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
			mSound.enableboefflasound(isChecked, getActivity());
		}
            });

            SoundControlCard.addItem(boefflasoundenable);
	}

	if (mSound.hasfauxsoundenable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.faux_sound));
            enable.setSummary(getString(R.string.faux_sound_summary));
            enable.setChecked(mSound.isfauxsoundEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
			mSound.enablefauxsound(isChecked, getActivity());
                }
            });

            SoundControlCard.addItem(enable);
	}

	if (mSound.hasSoundControlEnable()) {
            SwitchView soundControl = new SwitchView();
            soundControl.setTitle(getString(R.string.sound_control));
            soundControl.setSummary(("Enable ") + getString(R.string.sound_control));
            soundControl.setChecked(mSound.isSoundControlEnabled());
            soundControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
                    mSound.enableSoundControl(isChecked, getActivity());
		}
		});

            SoundControlCard.addItem(soundControl);
	}

        if (mSound.hasHighPerfModeEnable()) {
            SwitchView highPerfMode = new SwitchView();
            highPerfMode.setTitle(getString(R.string.headset_highperf_mode));
            highPerfMode.setSummary(("Enable ") + getString(R.string.headset_highperf_mode));
            highPerfMode.setChecked(mSound.isHighPerfModeEnabled());
            highPerfMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
                    mSound.enableHighPerfMode(isChecked, getActivity());
		}
		});

            SoundControlCard.addItem(highPerfMode);
        }

	if (mSound.haswcdspeakerleakage()) {
            SwitchView wcdspeakerleakage = new SwitchView();
            wcdspeakerleakage.setTitle(getString(R.string.speaker_leakage));
            wcdspeakerleakage.setSummary(getString(R.string.speaker_leakage_summary));
            wcdspeakerleakage.setChecked(mSound.iswcdspeakerleakage());
            wcdspeakerleakage.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
                    mSound.enablewcdspeakerleakage(isChecked, getActivity());
		}
            });

            SoundControlCard.addItem(wcdspeakerleakage);
	}

	if (mSound.hasboefflaspeaker()) {
            SeekBarView boefflaspeaker = new SeekBarView();
            boefflaspeaker.setTitle(getString(R.string.speaker_gain));
            boefflaspeaker.setItems(mSound.getBoefflaLimits());
            boefflaspeaker.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflaspeaker()));
            boefflaspeaker.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflaspeaker(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(boefflaspeaker);
	}

	if (mSound.hasfauxspeaker()) {
            SeekBarView fauxspeaker = new SeekBarView();
            fauxspeaker.setTitle(getString(R.string.speaker_gain));
            fauxspeaker.setItems(mSound.getFauxLimits());
            fauxspeaker.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxspeaker()));
            fauxspeaker.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxspeaker(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(fauxspeaker);
	}

	if (mSound.hasSpeakerGain()) {
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

            SoundControlCard.addItem(speakerGain);
	}

	if (mSound.hasboefflamic()) {
            SeekBarView boefflamic = new SeekBarView();
            boefflamic.setTitle(getString(R.string.microphone_gain) + (" (Calls)"));
            boefflamic.setItems(mSound.getBoefflamicLimits());
            boefflamic.setProgress(mSound.getBoefflamicLimits().indexOf(mSound.getboefflamic()));
            boefflamic.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflamic(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(boefflamic);
	}

	if (mSound.hasfauxmic()) {
            SeekBarView fauxmic = new SeekBarView();
            fauxmic.setTitle(getString(R.string.microphone_gain));
            fauxmic.setItems(mSound.getFauxLimits());
            fauxmic.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxmic()));
            fauxmic.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxmic(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(fauxmic);
	}

	if (mSound.hasfauxmiclock()) {
            SwitchView fauxmiclock = new SwitchView();
            fauxmiclock.setTitle(getString(R.string.lock_mic_gain));
            fauxmiclock.setSummary(getString(R.string.lock_mic_gain_summary));
            fauxmiclock.setChecked(mSound.isfauxmiclockEnabled());
            fauxmiclock.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
		        mSound.enablefauxmiclock(isChecked, getActivity());
		}
            });

            SoundControlCard.addItem(fauxmiclock);
	}

	if (mSound.hasMicrophoneGain()) {
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

            SoundControlCard.addItem(microphoneGain);
	}

	if (mSound.hasMicrophoneFlar()) {
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

            SoundControlCard.addItem(microphoneFlar);
	}

	if (mSound.hasboefflaep()) {
            SeekBarView boefflaep = new SeekBarView();
            boefflaep.setTitle(getString(R.string.earpiece_gain));
            boefflaep.setItems(mSound.getBoefflaEPLimits());
            boefflaep.setProgress(mSound.getBoefflaEPLimits().indexOf(mSound.getboefflaep()));
            boefflaep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflaep(value, getActivity());
		}

		    @Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(boefflaep);
	}

	if (mSound.hasflarep()) {
            SeekBarView flarep = new SeekBarView();
            flarep.setTitle(getString(R.string.earpiece_gain));
            flarep.setItems(mSound.getMicrophoneFlarLimits());
            flarep.setProgress(mSound.getMicrophoneFlarLimits().indexOf(mSound.getflarep()));
            flarep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setflarep(value, getActivity());
		}

		    @Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(flarep);
	}

        if (mSound.hasVolumeGain()) {
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

            SoundControlCard.addItem(volumeGain);
	}

	if (mSound.hasHeadSetGain()) {
            SeekBarView headphoneGain = new SeekBarView();
            headphoneGain.setTitle(getString(R.string.headphone_gain));
            headphoneGain.setMax(20);
            headphoneGain.setProgress(mSound.getHeadSetGain());
            headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadSetGain((position), getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(headphoneGain);
	}

        if (mSound.hasHeadphoneTpaGain()) {
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

            SoundControlCard.addItem(headphoneTpaGain);
	}

	if (mSound.hasboefflahp()) {
            if (!(Prefs.getBoolean("perchannel", false, getActivity())))
		Prefs.saveBoolean("perchannel", false, getActivity());

            final SwitchView perChannel = new SwitchView();
            perChannel.setSummary(getString(R.string.per_channel_controls));
            perChannel.setChecked(Prefs.getBoolean("perchannel", false, getActivity()));

            SoundControlCard.addItem(perChannel);

            SeekBarView boefflahp = new SeekBarView();
            boefflahp.setTitle(getString(R.string.headphone_gain));
            boefflahp.setItems(mSound.getBoefflaLimits());
            boefflahp.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("all")));
            boefflahp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mSound.setboefflahpall(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SeekBarView boefflahpl = new SeekBarView();
            boefflahpl.setTitle(getString(R.string.headphone_gain) + (" (Left)"));
            boefflahpl.setItems(mSound.getBoefflaLimits());
            boefflahpl.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("left")));
            boefflahpl.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mSound.setboefflahp("left", value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SeekBarView boefflahpr = new SeekBarView();
            boefflahpr.setTitle(getString(R.string.headphone_gain) + (" (Right)"));
            boefflahpr.setItems(mSound.getBoefflaLimits());
            boefflahpr.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("right")));
            boefflahpr.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mSound.setboefflahp("right", value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            class SeekBarManager {
                public void showPerChannelSeekbars (boolean enable) {
                if (enable == true) {
                    SoundControlCard.removeItem(boefflahp);
                    SoundControlCard.addItem(boefflahpl);
                    SoundControlCard.addItem(boefflahpr);
                } else {
                    SoundControlCard.removeItem(boefflahpl);
                    SoundControlCard.removeItem(boefflahpr);
                    SoundControlCard.addItem(boefflahp);
                }
            }
        }

        final SeekBarManager manager = new SeekBarManager();
        if (Prefs.getBoolean("perchannel", false, getActivity()) == true) {
            manager.showPerChannelSeekbars(true);
        } else {
            manager.showPerChannelSeekbars(false);
        }
        perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("perchannel", isChecked, getActivity());
                manager.showPerChannelSeekbars(isChecked);
                }
            });
	}

	if (mSound.hasfauxhp()) {
            SeekBarView fauxhp = new SeekBarView();
            fauxhp.setTitle(getString(R.string.headphone_gain));
            fauxhp.setItems(mSound.getFauxLimits());
            fauxhp.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxhp()));
            fauxhp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setfauxhp(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(fauxhp);
	}

	if (mSound.hasHeadphoneFlar()) {
            if (!(Prefs.getBoolean("perchannel", false, getActivity())))
		Prefs.saveBoolean("perchannel", false, getActivity());

            final SwitchView perChannel = new SwitchView();
            perChannel.setSummary(getString(R.string.per_channel_controls));
            perChannel.setChecked(Prefs.getBoolean("perchannel", false, getActivity()));

            SoundControlCard.addItem(perChannel);

            SeekBarView headphoneFlar = new SeekBarView();
            headphoneFlar.setTitle(getString(R.string.headphone_gain));
            headphoneFlar.setItems(mSound.getHeadphoneFlarLimits());
            headphoneFlar.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar("all")));
            headphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadphoneFlarAll(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SeekBarView headphoneFlarl = new SeekBarView();
            headphoneFlarl.setTitle(getString(R.string.headphone_gain) + (" (Left)"));
            headphoneFlarl.setItems(mSound.getHeadphoneFlarLimits());
            headphoneFlarl.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar("left")));
            headphoneFlarl.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadphoneFlar("left", value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SeekBarView headphoneFlarr = new SeekBarView();
            headphoneFlarr.setTitle(getString(R.string.headphone_gain) + (" (Right)"));
            headphoneFlarr.setItems(mSound.getHeadphoneFlarLimits());
            headphoneFlarr.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar("right")));
            headphoneFlarr.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadphoneFlar("right", value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            class SeekBarManager {
                public void showPerChannelSeekbars (boolean enable) {
                if (enable == true) {
                    SoundControlCard.removeItem(headphoneFlar);
                    SoundControlCard.addItem(headphoneFlarl);
                    SoundControlCard.addItem(headphoneFlarr);
                } else {
                    SoundControlCard.removeItem(headphoneFlarl);
                    SoundControlCard.removeItem(headphoneFlarr);
                    SoundControlCard.addItem(headphoneFlar);
                }
            }
        }

        final SeekBarManager manager = new SeekBarManager();
        if (Prefs.getBoolean("perchannel", false, getActivity()) == true) {
            manager.showPerChannelSeekbars(true);
        } else {
            manager.showPerChannelSeekbars(false);
        }
        perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("perchannel", isChecked, getActivity());
                manager.showPerChannelSeekbars(isChecked);
                }
            });
	}

        if (SoundControlCard.size() > 0) {
            items.add(SoundControlCard);
        }
    }

}
