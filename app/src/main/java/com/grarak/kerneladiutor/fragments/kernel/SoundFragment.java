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

	SwitchView enable = new SwitchView();
	SeekBarView speakerGain = new SeekBarView();
	SeekBarView micGain = new SeekBarView();
	SwitchView micLock = new SwitchView();
	SeekBarView epGain = new SeekBarView();
	final SwitchView perChannel = new SwitchView();
	SeekBarView headphoneGain = new SeekBarView();
	SeekBarView headphoneGainLeft = new SeekBarView();
	SeekBarView headphoneGainRight = new SeekBarView();

	if (mSound.hasboefflasoundenable()) {
            enable.setTitle(getString(R.string.boefflasound) + " Version: " + mSound.getboefflasoundVersion());
            enable.setSummary(getString(R.string.boefflasound_summary));
            enable.setChecked(mSound.isboefflasoundenabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
	    	mSound.enableboefflasound(isChecked, getActivity());
	    	getHandler().postDelayed(() -> {
	    	// Show or hide other boeffla sound options on the basis of the main driver status
	    	if (mSound.isboefflasoundenabled()) {
		    if (mSound.hasboefflaspeaker()) {
		    	speakerGain.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflaspeaker()));
		    	SoundControlCard.addItem(speakerGain);
		    }
		    if (mSound.hasboefflamic()) {
		    	micGain.setProgress(mSound.getBoefflamicLimits().indexOf(mSound.getboefflamic()));
		    	SoundControlCard.addItem(micGain);
		    }
		    if (mSound.hasboefflaep()) {
		    	epGain.setProgress(mSound.getBoefflaEPLimits().indexOf(mSound.getboefflaep()));
		    	SoundControlCard.addItem(epGain);
		    }
		    if (mSound.hasboefflahp()) {
			perChannel.setChecked(Prefs.getBoolean("perchannel", false, getActivity()));
			SoundControlCard.addItem(perChannel);
			headphoneGain.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("all")));
			headphoneGainLeft.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("left")));
			headphoneGainRight.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("right")));
			if (Prefs.getBoolean("perchannel", false, getActivity()) == true) {
			    SoundControlCard.removeItem(headphoneGain);
			    SoundControlCard.addItem(headphoneGainLeft);
			    SoundControlCard.addItem(headphoneGainRight);
			} else {
			    SoundControlCard.removeItem(headphoneGainLeft);
			    SoundControlCard.removeItem(headphoneGainRight);
			    SoundControlCard.addItem(headphoneGain);
                	}
		    }
	    	} else {
		    SoundControlCard.removeItem(speakerGain);
		    SoundControlCard.removeItem(micGain);
		    SoundControlCard.removeItem(epGain);
		    SoundControlCard.removeItem(perChannel);
		    SoundControlCard.removeItem(headphoneGain);
		    SoundControlCard.removeItem(headphoneGainLeft);
		    SoundControlCard.removeItem(headphoneGainRight);
	    	}
	    }, 100);
	    });

            SoundControlCard.addItem(enable);
	}

	if (mSound.hasfauxsoundenable()) {
            enable.setTitle(getString(R.string.faux_sound));
            enable.setSummary(getString(R.string.faux_sound_summary));
            enable.setChecked(mSound.isfauxsoundEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
	    	mSound.enablefauxsound(isChecked, getActivity());
	    	getHandler().postDelayed(() -> {
	    	// Show or hide other boeffla sound options on the basis of the main driver status
	    	if (mSound.isfauxsoundEnabled()) {
		    if (mSound.hasfauxspeaker()) {
		    	speakerGain.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxspeaker()));
		    	SoundControlCard.addItem(speakerGain);
		    }
		    if (mSound.hasfauxmic()) {
		    	micGain.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxmic()));
		    	SoundControlCard.addItem(micGain);
		    }
		    if (mSound.hasfauxmiclock()) {
		    	micLock.setChecked(mSound.isfauxmiclockEnabled());
		    	SoundControlCard.addItem(micLock);
		    }
		    if (mSound.hasfauxhp()) {
		    	headphoneGain.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxhp()));
		    	SoundControlCard.addItem(headphoneGain);
		    }
	    	} else {
		    SoundControlCard.removeItem(speakerGain);
		    SoundControlCard.removeItem(micGain);
		    SoundControlCard.removeItem(micLock);
		    SoundControlCard.removeItem(headphoneGain);
	    	}
	    }, 100);
	    });

            SoundControlCard.addItem(enable);
	}

	if (mSound.hasSoundControlEnable()) {
            enable.setTitle(getString(R.string.sound_control));
            enable.setSummary(("Enable ") + getString(R.string.sound_control));
            enable.setChecked(mSound.isSoundControlEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
                    mSound.enableSoundControl(isChecked, getActivity());
		}
		});

            SoundControlCard.addItem(enable);
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
            speakerGain.setTitle(getString(R.string.speaker_gain));
            speakerGain.setItems(mSound.getBoefflaLimits());
            speakerGain.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflaspeaker()));
            speakerGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflaspeaker(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

	    if (mSound.isboefflasoundenabled()) {
            	SoundControlCard.addItem(speakerGain);
	    } else {
            	SoundControlCard.removeItem(speakerGain);
	    } 
	}

	if (mSound.hasfauxspeaker()) {
            speakerGain.setTitle(getString(R.string.speaker_gain));
            speakerGain.setItems(mSound.getFauxLimits());
            speakerGain.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxspeaker()));
            speakerGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxspeaker(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            if (!mSound.hasfauxsoundenable() || mSound.hasfauxsoundenable() && mSound.isfauxsoundEnabled()) {
            	SoundControlCard.addItem(speakerGain);
	    } else {
            	SoundControlCard.removeItem(speakerGain);
	    }
	}

	if (mSound.hasSpeakerGain()) {
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
            micGain.setTitle(getString(R.string.microphone_gain) + (" (Calls)"));
            micGain.setItems(mSound.getBoefflamicLimits());
            micGain.setProgress(mSound.getBoefflamicLimits().indexOf(mSound.getboefflamic()));
            micGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflamic(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

	    if (mSound.isboefflasoundenabled()) {
            	SoundControlCard.addItem(micGain);
	    } else {
            	SoundControlCard.removeItem(micGain);
	    } 
	}

	if (mSound.hasfauxmic()) {
            micGain.setTitle(getString(R.string.microphone_gain));
            micGain.setItems(mSound.getFauxLimits());
            micGain.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxmic()));
            micGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxmic(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            if (!mSound.hasfauxsoundenable() || mSound.hasfauxsoundenable() && mSound.isfauxsoundEnabled()) {
            	SoundControlCard.addItem(micGain);
	    } else {
            	SoundControlCard.removeItem(micGain);
	    }
	}

	if (mSound.hasfauxmiclock()) {
            micLock.setTitle(getString(R.string.lock_mic_gain));
            micLock.setSummary(getString(R.string.lock_mic_gain_summary));
            micLock.setChecked(mSound.isfauxmiclockEnabled());
            micLock.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
		        mSound.enablefauxmiclock(isChecked, getActivity());
		}
            });

            if (!mSound.hasfauxsoundenable() || mSound.hasfauxsoundenable() && mSound.isfauxsoundEnabled()) {
            	SoundControlCard.addItem(micLock);
	    } else {
            	SoundControlCard.removeItem(micLock);
	    }
	}

	if (mSound.hasMicrophoneGain()) {
            micGain.setTitle(getString(R.string.microphone_gain));
            micGain.setItems(mSound.getMicrophoneGainLimits());
            micGain.setProgress(mSound.getMicrophoneGainLimits().indexOf(mSound.getMicrophoneGain()));
            micGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setMicrophoneGain(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(micGain);
	}

	if (mSound.hasMicrophoneFlar()) {
            micGain.setTitle(getString(R.string.microphone_gain));
            micGain.setItems(mSound.getMicrophoneFlarLimits());
            micGain.setProgress(mSound.getMicrophoneFlarLimits().indexOf(mSound.getMicrophoneFlar()));
            micGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setMicrophoneFlar(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(micGain);
	}

	if (mSound.hasboefflaep()) {
            epGain.setTitle(getString(R.string.earpiece_gain));
            epGain.setItems(mSound.getBoefflaEPLimits());
            epGain.setProgress(mSound.getBoefflaEPLimits().indexOf(mSound.getboefflaep()));
            epGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflaep(value, getActivity());
		}

		    @Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

	    if (mSound.isboefflasoundenabled()) {
            	SoundControlCard.addItem(epGain);
	    } else {
            	SoundControlCard.removeItem(epGain);
	    } 
	}

	if (mSound.hasflarep()) {
            epGain.setTitle(getString(R.string.earpiece_gain));
            epGain.setItems(mSound.getMicrophoneFlarLimits());
            epGain.setProgress(mSound.getMicrophoneFlarLimits().indexOf(mSound.getflarep()));
            epGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setflarep(value, getActivity());
		}

		    @Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            SoundControlCard.addItem(epGain);
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

            perChannel.setSummary(getString(R.string.per_channel_controls));
            perChannel.setChecked(Prefs.getBoolean("perchannel", false, getActivity()));

	    if (mSound.isboefflasoundenabled()) {
            	SoundControlCard.addItem(perChannel);
	    } else {
            	SoundControlCard.removeItem(perChannel);
	    } 

            headphoneGain.setTitle(getString(R.string.headphone_gain));
            headphoneGain.setItems(mSound.getBoefflaLimits());
            headphoneGain.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("all")));
            headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mSound.setboefflahpall(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            headphoneGainLeft.setTitle(getString(R.string.headphone_gain) + (" (Left)"));
            headphoneGainLeft.setItems(mSound.getBoefflaLimits());
            headphoneGainLeft.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("left")));
            headphoneGainLeft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
			mSound.setboefflahp("left", value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            headphoneGainRight.setTitle(getString(R.string.headphone_gain) + (" (Right)"));
            headphoneGainRight.setItems(mSound.getBoefflaLimits());
            headphoneGainRight.setProgress(mSound.getBoefflaLimits().indexOf(mSound.getboefflahp("right")));
            headphoneGainRight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
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
                if (mSound.isboefflasoundenabled() && enable == true) {
                    SoundControlCard.removeItem(headphoneGain);
                    SoundControlCard.addItem(headphoneGainLeft);
                    SoundControlCard.addItem(headphoneGainRight);
		} else if (!mSound.isboefflasoundenabled()) {
                    SoundControlCard.removeItem(headphoneGainLeft);
                    SoundControlCard.removeItem(headphoneGainRight);
                    SoundControlCard.removeItem(headphoneGain);
                } else {
                    SoundControlCard.removeItem(headphoneGainLeft);
                    SoundControlCard.removeItem(headphoneGainRight);
                    SoundControlCard.addItem(headphoneGain);
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
            headphoneGain.setTitle(getString(R.string.headphone_gain));
            headphoneGain.setItems(mSound.getFauxLimits());
            headphoneGain.setProgress(mSound.getFauxLimits().indexOf(mSound.getfauxhp()));
            headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setfauxhp(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            if (!mSound.hasfauxsoundenable() || mSound.hasfauxsoundenable() && mSound.isfauxsoundEnabled()) {
            	SoundControlCard.addItem(headphoneGain);
	    } else {
            	SoundControlCard.removeItem(headphoneGain);
	    }
	}

	if (mSound.hasHeadphoneFlar()) {
            if (!(Prefs.getBoolean("perchannel", false, getActivity())))
		Prefs.saveBoolean("perchannel", false, getActivity());

            perChannel.setSummary(getString(R.string.per_channel_controls));
            perChannel.setChecked(Prefs.getBoolean("perchannel", false, getActivity()));

            SoundControlCard.addItem(perChannel);

            headphoneGain.setTitle(getString(R.string.headphone_gain));
            headphoneGain.setItems(mSound.getHeadphoneFlarLimits());
            headphoneGain.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar("all")));
            headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadphoneFlarAll(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            headphoneGainLeft.setTitle(getString(R.string.headphone_gain) + (" (Left)"));
            headphoneGainLeft.setItems(mSound.getHeadphoneFlarLimits());
            headphoneGainLeft.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar("left")));
            headphoneGainLeft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadphoneFlar("left", value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            headphoneGainRight.setTitle(getString(R.string.headphone_gain) + (" (Right)"));
            headphoneGainRight.setItems(mSound.getHeadphoneFlarLimits());
            headphoneGainRight.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar("right")));
            headphoneGainRight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
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
                    SoundControlCard.removeItem(headphoneGain);
                    SoundControlCard.addItem(headphoneGainLeft);
                    SoundControlCard.addItem(headphoneGainRight);
                } else {
                    SoundControlCard.removeItem(headphoneGainLeft);
                    SoundControlCard.removeItem(headphoneGainRight);
                    SoundControlCard.addItem(headphoneGain);
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
