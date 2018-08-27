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
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

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
        if (mSound.haswcdspeakerleakage()) {
            speakerleakageInit(items);
        }
        if (mSound.hasboefflasound()) {
            boefflasoundInit(items);
        }
        if (mSound.hasSoundControlEnable()) {
            soundControlEnableInit(items);
        }
        if (mSound.hasHighPerfModeEnable()) {
            highPerfModeEnableInit(items);
        }
        if (mSound.hasHeadphoneFlar()) {
            headphoneFlarInit(items);
        }
        if (mSound.hasMicrophoneFlar()) {
            microphoneFlarInit(items);
        }
        if (mSound.hasHeadphoneTpaGain()) {
            headphoneTpaGainInit(items);
        }
        if (mSound.hasMicrophoneGain()) {
            microphoneGainInit(items);
        }
        if (mSound.hasVolumeGain()) {
            volumeGainInit(items);
        }
        if (mSound.hasfauxsound()) {
            hasfauxsoundInit(items);
        }
    }

    private void speakerleakageInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> speakerleakage = new ArrayList<>();

        TitleView title = new TitleView();
        title.setText(getString(R.string.speaker_leakage));

        if (mSound.haswcdspeakerleakage()) {
            SwitchView wcdspeakerleakage = new SwitchView();
            wcdspeakerleakage.setSummary(getString(R.string.speaker_leakage_summary));
            wcdspeakerleakage.setChecked(mSound.iswcdspeakerleakage());
            wcdspeakerleakage.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mSound.enablewcdspeakerleakage(isChecked, getActivity());
                }
            });

            speakerleakage.add(wcdspeakerleakage);
        }

        if (speakerleakage.size() > 0) {
            items.add(title);
            items.addAll(speakerleakage);
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

    private void boefflasoundInit(List<RecyclerViewItem> items) {

        CardView boefflasoundCard = new CardView(getActivity());
        boefflasoundCard.setTitle(getString(R.string.sound_control));

        final SwitchView boefflasoundenable = new SwitchView();
        boefflasoundenable.setTitle(getString(R.string.boefflasound) + " Version: " + mSound.getboefflasoundVersion());
        boefflasoundenable.setSummary(getString(R.string.boefflasound_summary));
        boefflasoundenable.setChecked(mSound.isboefflasoundenabled());
        boefflasoundenable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mSound.enableboefflasound(isChecked, getActivity());
            }
        });

        boefflasoundCard.addItem(boefflasoundenable);

	if (mSound.hasboefflaspeaker()) {
		final SeekBarView boefflaspeaker = new SeekBarView();
		boefflaspeaker.setTitle(getString(R.string.speaker_gain));
		boefflaspeaker.setItems(mSound.getboefflaLimits());
		boefflaspeaker.setProgress(mSound.getboefflaLimits().indexOf(mSound.getboefflaspeaker()));
		boefflaspeaker.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		    @Override
		    public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflaspeaker(value, getActivity());
		    }

		    @Override
		    public void onMove(SeekBarView seekBarView, int position, String value) {
		    }
		});

		boefflasoundCard.addItem(boefflaspeaker);
	}

	if (mSound.hasboefflaep()) {
		final SeekBarView boefflaep = new SeekBarView();
		boefflaep.setTitle(getString(R.string.boefflaep));
		boefflaep.setItems(mSound.getboefflaEPLimits());
		boefflaep.setProgress(mSound.getboefflaEPLimits().indexOf(mSound.getboefflaep()));
		boefflaep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		    @Override
		    public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflaep(value, getActivity());
		    }

		    @Override
		    public void onMove(SeekBarView seekBarView, int position, String value) {
		    }
		});

		boefflasoundCard.addItem(boefflaep);
	}

	if (mSound.hasboefflamic()) {
		SeekBarView boefflamic = new SeekBarView();
		boefflamic.setTitle(getString(R.string.microphone_gain) + (" (Calls)"));
		boefflamic.setItems(mSound.getboefflamicLimits());
		boefflamic.setProgress(mSound.getboefflamicLimits().indexOf(mSound.getboefflamic()));
		boefflamic.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		    @Override
		    public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setboefflamic(value, getActivity());
		    }

		    @Override
		    public void onMove(SeekBarView seekBarView, int position, String value) {
		    }
		});

		boefflasoundCard.addItem(boefflamic);
	}

        if (!(Prefs.getBoolean("boefflahp_perchannel", false, getActivity())))
            Prefs.saveBoolean("boefflahp_perchannel", false, getActivity());

        final SwitchView perChannel = new SwitchView();
        perChannel.setTitle(getString(R.string.per_channel_controls));
        perChannel.setSummary(getString(R.string.per_channel_controls_summary));
        perChannel.setChecked(Prefs.getBoolean("boefflahp_perchannel", false, getActivity()));

        boefflasoundCard.addItem(perChannel);

        final SeekBarView boefflahp = new SeekBarView();
        boefflahp.setTitle(getString(R.string.headphone_gain));
        boefflahp.setItems(mSound.getboefflaLimits());
        boefflahp.setProgress(mSound.getboefflaLimits().indexOf(mSound.getboefflahp("all")));
        boefflahp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setboefflahp("all", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        final SeekBarView boefflahpLeft = new SeekBarView();
        boefflahpLeft.setTitle(getString(R.string.headphone_gain_left));
        boefflahpLeft.setItems(mSound.getboefflaLimits());
        boefflahpLeft.setProgress(mSound.getboefflaLimits().indexOf(mSound.getboefflahp("left")));
        boefflahpLeft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setboefflahp("left", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        final SeekBarView boefflahpRight = new SeekBarView();
        boefflahpRight.setTitle(getString(R.string.headphone_gain_right));
        boefflahpRight.setItems(mSound.getboefflaLimits());
        boefflahpRight.setProgress(mSound.getboefflaLimits().indexOf(mSound.getboefflahp("right")));
        boefflahpRight.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
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
                    boefflasoundCard.removeItem(boefflahp);
                    boefflasoundCard.addItem(boefflahpLeft);
                    boefflasoundCard.addItem(boefflahpRight);
                } else {
                    boefflasoundCard.removeItem(boefflahpLeft);
                    boefflasoundCard.removeItem(boefflahpRight);
                    boefflasoundCard.addItem(boefflahp);
                }
            }
        }

        final SeekBarManager manager = new SeekBarManager();
        if (Prefs.getBoolean("boefflahp_perchannel", false, getActivity()) == true) {
            manager.showPerChannelSeekbars(true);
        } else {
            manager.showPerChannelSeekbars(false);
        }
        perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("boefflahp_perchannel", isChecked, getActivity());
                manager.showPerChannelSeekbars(isChecked);
            }
        });

        if (boefflasoundCard.size() > 0) {
            items.add(boefflasoundCard);
       }
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

    private void hasfauxsoundInit(List<RecyclerViewItem> items) {

        CardView fauxsoundCard = new CardView(getActivity());
        fauxsoundCard.setTitle(getString(R.string.sound_control));

        SwitchView fauxsound = new SwitchView();
        fauxsound.setTitle(getString(R.string.faux_sound));
        fauxsound.setSummary(getString(R.string.faux_sound_summary));
        fauxsound.setChecked(mSound.isfauxsoundEnabled());
        fauxsound.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                mSound.enablefauxsound(isChecked, getActivity());
            }
        });
        fauxsoundCard.addItem(fauxsound);

	if (mSound.hasfauxspeaker()) {
            SeekBarView fauxspeaker = new SeekBarView();
            fauxspeaker.setTitle(getString(R.string.speaker_gain));
            fauxspeaker.setItems(mSound.getfauxLimits());
            fauxspeaker.setProgress(mSound.getfauxLimits().indexOf(mSound.getfauxspeaker()));
            fauxspeaker.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxspeaker(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            fauxsoundCard.addItem(fauxspeaker);
	}

	if (mSound.hasfauxmic()) {
            SeekBarView fauxmic = new SeekBarView();
            fauxmic.setTitle(getString(R.string.microphone_gain));
            fauxmic.setItems(mSound.getfauxLimits());
            fauxmic.setProgress(mSound.getfauxLimits().indexOf(mSound.getfauxmic()));
            fauxmic.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
		@Override
		public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxmic(value, getActivity());
		}

		@Override
		public void onMove(SeekBarView seekBarView, int position, String value) {
		}
            });

            fauxsoundCard.addItem(fauxmic);
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
            fauxsoundCard.addItem(fauxmiclock);
	}

	if (mSound.hasfauxhp()) {
            if (!(Prefs.getBoolean("fauxhp_perchannel", false, getActivity())))
		Prefs.saveBoolean("fauxhp_perchannel", false, getActivity());

		final SwitchView perChannel = new SwitchView();
		perChannel.setTitle(getString(R.string.per_channel_controls));
		perChannel.setSummary(getString(R.string.per_channel_controls_summary));
		perChannel.setChecked(Prefs.getBoolean("fauxhp_perchannel", false, getActivity()));

		fauxsoundCard.addItem(perChannel);

		final SeekBarView fauxhp = new SeekBarView();
		fauxhp.setTitle(getString(R.string.headphone_gain));
		fauxhp.setItems(mSound.getfauxLimits());
		fauxhp.setProgress(mSound.getfauxLimits().indexOf(mSound.getfauxhp("all")));
		fauxhp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxhp("all", value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
		});

		final SeekBarView fauxhpleft = new SeekBarView();
		fauxhpleft.setTitle(getString(R.string.headphone_gain_left));
		fauxhpleft.setItems(mSound.getfauxLimits());
		fauxhpleft.setProgress(mSound.getfauxLimits().indexOf(mSound.getfauxhp("left")));
		fauxhpleft.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxhp("left", value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
		});

		final SeekBarView fauxhpright = new SeekBarView();
		fauxhpright.setTitle(getString(R.string.headphone_gain_right));
		fauxhpright.setItems(mSound.getfauxLimits());
		fauxhpright.setProgress(mSound.getfauxLimits().indexOf(mSound.getfauxhp("right")));
		fauxhpright.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
		        mSound.setfauxhp("right", value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
		});

		class SeekBarManager {
                    public void showPerChannelSeekbars (boolean enable) {
                    if (enable == true) {
		        fauxsoundCard.removeItem(fauxhp);
		        fauxsoundCard.addItem(fauxhpleft);
		        fauxsoundCard.addItem(fauxhpright);
                    } else {
		        fauxsoundCard.removeItem(fauxhpleft);
		        fauxsoundCard.removeItem(fauxhpright);
		        fauxsoundCard.addItem(fauxhp);
                    }
		}
            }

            final SeekBarManager manager = new SeekBarManager();
            if (Prefs.getBoolean("fauxhp_perchannel", false, getActivity()) == true) {
		manager.showPerChannelSeekbars(true);
            } else {
		manager.showPerChannelSeekbars(false);
            }
            perChannel.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchview, boolean isChecked) {
                    Prefs.saveBoolean("fauxhp_perchannel", isChecked, getActivity());
                    manager.showPerChannelSeekbars(isChecked);
		}
            });
	}

	if (fauxsoundCard.size() > 0) {
            items.add(fauxsoundCard);
	}
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
