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
package com.grarak.kerneladiutor.utils.kernel.sound;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 25.06.16.
 */
public class Sound {

    private static Sound sIOInstance;

    public static Sound getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new Sound();
        }
        return sIOInstance;
    }

    private static final String SOUND_CONTROL_ENABLE = "/sys/module/snd_soc_wcd9320/parameters/enable_fs";
    private static final String WCD9320_SPEAKER_LEAKAGE = "/sys/module/snd_soc_wcd9320/parameters/spkr_drv_wrnd";

    private static final String TPA6165_REGISTERS_LIST = "/sys/kernel/debug/tpa6165/registers";
    private static final String TPA6165_SET_REG = "/sys/kernel/debug/tpa6165/set_reg";

    private static final String SOUNDCONTROL = "/sys/devices/virtual/misc/soundcontrol";
    private static final String HIGHPERF_MODE_ENABLE = SOUNDCONTROL + "/highperf_enabled";
    private static final String SPEAKER_BOOST = SOUNDCONTROL + "/speaker_boost";
    private static final String MIC_BOOST = SOUNDCONTROL + "/mic_boost";
    private static final String VOLUME_BOOST = SOUNDCONTROL + "/volume_boost";
    private static final String HEADSET_BOOST = SOUNDCONTROL + "/headset_boost";

    private static final String SOUND_CONTROL = "/sys/kernel/sound_control";
    private static final String HEADPHONE_FLAR = SOUND_CONTROL + "/headphone_gain";
    private static final String MICROPHONE_FLAR = SOUND_CONTROL + "/mic_gain";
    private static final String SPEAKER_FLAR = SOUND_CONTROL + "/speaker_gain";
    private static final String EARPIECE_FLAR = SOUND_CONTROL + "/earpiece_gain";

    private static final String BOEFFLA_SOUND = "/sys/class/misc/boeffla_sound";
    private static final String BOEFFLA_SOUND_ENABLE = BOEFFLA_SOUND + "/boeffla_sound";
    private static final String BOEFFLA_SPEAKER = BOEFFLA_SOUND + "/speaker_volume";
    private static final String BOEFFLA_HP = BOEFFLA_SOUND + "/headphone_volume";
    private static final String BOEFFLA_EP = BOEFFLA_SOUND + "/earpiece_volume";
    private static final String BOEFFLA_MIC = BOEFFLA_SOUND + "/mic_level_call";
    private static final String BOEFFLA_VERSION = BOEFFLA_SOUND + "/version";

    private static final String FAUX_SOUND = "/sys/kernel/sound_control_3";
    private static final String FAUX_SOUND_ENABLE = FAUX_SOUND + "/gpl_sound_control_enabled";
    private static final String FAUX_SPEAKER = FAUX_SOUND + "/gpl_speaker_gain";
    private static final String FAUX_HP = FAUX_SOUND + "/gpl_headphone_gain";
    private static final String FAUX_MIC = FAUX_SOUND + "/gpl_mic_gain";
    private static final String FAUX_MIC_LOCK = FAUX_SOUND + "/gpl_sound_control_rec_locked";

    private final List<String> mSpeakerGainFiles = new ArrayList<>();
    private final List<String> mFrancoLimits = new ArrayList<>();
    private final List<String> mFlarLimits = new ArrayList<>();
    private final List<String> mFlarHpLimits = new ArrayList<>();
    private final List<String> mBoefflaLimits = new ArrayList<>();
    private final List<String> mBoefflaEPLimits = new ArrayList<>();
    private final List<String> mBoefflaMICLimits = new ArrayList<>();
    private final List<String> mFauxLimits = new ArrayList<>();

    {
        mSpeakerGainFiles.add(SPEAKER_BOOST);
        mSpeakerGainFiles.add(SPEAKER_FLAR);
    }

    {
        for (int i = -20; i < 21; i++) {
            mFrancoLimits.add(String.valueOf(i));
        }

        for (int i = -10; i < 21; i++) {
            mFlarLimits.add(String.valueOf(i));
        }

        for (int i = -40; i < 21; i++) {
            mFlarHpLimits.add(String.valueOf(i));
        }
        for (int i = -30; i < 31; i++) {
            mBoefflaLimits.add(String.valueOf(i));
        }
        for (int i = -10; i < 21; i++) {
            mBoefflaEPLimits.add(String.valueOf(i));
        }
        for (int i = -20; i < 31; i++) {
            mBoefflaMICLimits.add(String.valueOf(i));
        }
        for (int i = -30; i < 21; i++) {
            mFauxLimits.add(String.valueOf(i));
        }
    }

    private String SPEAKER_GAIN_FILE;
    private String SOUND_CONTROL_DIR;

    private Sound() {
        for (String file : mSpeakerGainFiles) {
            if (Utils.existFile(file)) {
                SPEAKER_GAIN_FILE = file;
                break;
            }
        }
        if (Utils.existFile(SOUNDCONTROL)) {
            SOUND_CONTROL_DIR = SOUNDCONTROL;
        } else if (Utils.existFile(SOUND_CONTROL)) {
            SOUND_CONTROL_DIR = SOUND_CONTROL;
        } else if (Utils.existFile(SOUND_CONTROL_ENABLE)) {
            SOUND_CONTROL_DIR = SOUND_CONTROL_ENABLE;
        } else if (Utils.existFile(TPA6165_SET_REG)) {
            SOUND_CONTROL_DIR = TPA6165_SET_REG;
        } else if (Utils.existFile(TPA6165_REGISTERS_LIST)) {
            SOUND_CONTROL_DIR = TPA6165_REGISTERS_LIST;
        } else if (Utils.existFile(FAUX_SOUND)) {
            SOUND_CONTROL_DIR = FAUX_SOUND;
        } else if (Utils.existFile(BOEFFLA_SOUND)) {
            SOUND_CONTROL_DIR = BOEFFLA_SOUND;
        } else if (Utils.existFile(WCD9320_SPEAKER_LEAKAGE)) {
            SOUND_CONTROL_DIR = WCD9320_SPEAKER_LEAKAGE;
        }
    }

    public void setVolumeGain(String value, Context context) {
        run(Control.write(value, VOLUME_BOOST), VOLUME_BOOST, context);
    }

    public String getVolumeGain() {
        return Utils.readFile(VOLUME_BOOST);
    }

    public List<String> getVolumeGainLimits() {
        return mFrancoLimits;
    }

    public boolean hasVolumeGain() {
        return Utils.existFile(VOLUME_BOOST);
    }

    public void setHeadSetGain(int value, Context context) {
        run(Control.write(String.valueOf(value), HEADSET_BOOST), HEADSET_BOOST, context);
    }

    public static int getHeadSetGain() {
        return Utils.strToInt(Utils.readFile(HEADSET_BOOST));
    }

    public static boolean hasHeadSetGain() {
        return Utils.existFile(HEADSET_BOOST);
    }

    public void setMicrophoneGain(String value, Context context) {
        run(Control.write(value, MIC_BOOST), MIC_BOOST, context);
    }

    public String getMicrophoneGain() {
        return Utils.readFile(MIC_BOOST);
    }

    public List<String> getMicrophoneGainLimits() {
        return mFrancoLimits;
    }

    public List<String> getBoefflaMICLimits() {
        return mBoefflaMICLimits;
    }

    public boolean hasMicrophoneGain() {
        return Utils.existFile(MIC_BOOST);
    }

    public void enableboefflasound(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", BOEFFLA_SOUND_ENABLE), BOEFFLA_SOUND_ENABLE, context);
    }

    public boolean isboefflasoundenabled() {
        return Utils.readFile(BOEFFLA_SOUND_ENABLE).equals("Boeffla sound status: 1");
    }

    public boolean hasboefflasoundenable() {
       return Utils.existFile(BOEFFLA_SOUND_ENABLE);
    }

    public String getboefflasoundVersion()  {
        return Utils.readFile(BOEFFLA_VERSION);
    }

    public void setHeadphoneTpaGain(String value, Context context) {
        /*
         * Headphone Amp Gain is register 0x7.
         * Zero corresponds to 185 (0xb9). The min value is -24 (0xa1) and max is 6 (0xbf).
         */
        run(Control.chmod("222", TPA6165_SET_REG), TPA6165_SET_REG, context);
        run(Control.write("0x07 0x" + Integer.toHexString(185 + Utils.strToInt(value)),
                TPA6165_SET_REG), TPA6165_SET_REG, context);
    }

    public String getHeadphoneTpaGain() {
        try {
            return String.valueOf(Integer.decode(RootUtils.runCommand("cat " + TPA6165_REGISTERS_LIST
                    + " | awk \"/0x7/\" | cut -c9-13")) - 185);
        } catch (NumberFormatException ignored) {
            return "";
        }
    }

    public List<String> getHeadphoneTpaGainLimits() {
        List<String> list = new ArrayList<>();
        for (int i = -24; i < 7; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    public boolean hasHeadphoneTpaGain() {
        return Utils.existFile(TPA6165_SET_REG) && Utils.existFile(TPA6165_REGISTERS_LIST);
    }

    public void setSpeakerGain(String value, Context context) {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_BOOST:
                run(Control.write(value, SPEAKER_BOOST), SPEAKER_BOOST, context);
                break;
            case SPEAKER_FLAR:
                run(Control.write(value, SPEAKER_FLAR), SPEAKER_FLAR, context);
                break;
        }
    }

    public String getSpeakerGain() {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_BOOST:
                return Utils.readFile(SPEAKER_BOOST);
            case SPEAKER_FLAR:
                return Utils.readFile(SPEAKER_FLAR);
        }
        return "";
    }

    public List<String> getSpeakerGainLimits() {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_BOOST:
                return mFrancoLimits;
            case SPEAKER_FLAR:
                return mFlarLimits;
            case BOEFFLA_SPEAKER:
                return mBoefflaLimits;
            case BOEFFLA_HP:
                return mBoefflaLimits;
            case BOEFFLA_EP:
                return mBoefflaEPLimits;
            case BOEFFLA_MIC:
                return mBoefflaMICLimits;
        }
        return new ArrayList<>();
    }

    public void enableHighPerfMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HIGHPERF_MODE_ENABLE), HIGHPERF_MODE_ENABLE, context);
    }

    public boolean isHighPerfModeEnabled() {
        return Utils.readFile(HIGHPERF_MODE_ENABLE).equals("1");
    }

    public boolean hasHighPerfModeEnable() {
        return Utils.existFile(HIGHPERF_MODE_ENABLE);
    }

    public void enableSoundControl(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SOUND_CONTROL_ENABLE), SOUND_CONTROL_ENABLE, context);
    }

    public boolean isSoundControlEnabled() {
        return Utils.readFile(SOUND_CONTROL_ENABLE).equals("Y");
    }

    public boolean hasSoundControlEnable() {
        return Utils.existFile(SOUND_CONTROL_ENABLE);
    }

    public void enablefauxsound(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FAUX_SOUND_ENABLE), FAUX_SOUND_ENABLE, context);
    }

    public boolean isfauxsoundEnabled() {
        return Utils.readFile(FAUX_SOUND_ENABLE).equals("1");
    }

    public boolean hasfauxsoundenable() {
       return Utils.existFile(FAUX_SOUND_ENABLE);
    }

    public void enablefauxmiclock(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FAUX_MIC_LOCK), FAUX_MIC_LOCK, context);
    }

    public boolean isfauxmiclockEnabled() {
        return Utils.readFile(FAUX_MIC_LOCK).equals("1");
    }

    public boolean hasfauxmiclock() {
       return Utils.existFile(FAUX_MIC_LOCK);
    }

    public boolean supported() {
        return hasSoundControlDir();
    }

    private int getChecksum(int arg0, int arg1) {
        return (Integer.MAX_VALUE ^ (arg0 & 0xff) + (arg1 & 0xff));
    }

    private void SoundRun(String value, String path, String id, Context context) {
        int checksum = value.contains(" ") ?
                getChecksum(Utils.strToInt(value.split(" ")[0]),
                        Utils.strToInt(value.split(" ")[1])) :
                getChecksum(Utils.strToInt(value), 0);
        run(Control.write(value + " " + checksum, path), id, context);
        run(Control.write(value, path), id + "nochecksum", context);
    }

    public void setfauxspeaker(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= 0 && newGain <= 20) {
            SoundRun(value + " " + value, FAUX_SPEAKER, FAUX_SPEAKER, context);
        } else if (newGain <= -1 && newGain >= -30) {
            value = String.valueOf(newGain + 256);
            SoundRun(value + " " + value, FAUX_SPEAKER, FAUX_SPEAKER, context);
        }
    }

    public void setfauxmic(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= 0 && newGain <= 20) {
            SoundRun(value, FAUX_MIC, FAUX_MIC, context);
        } else if (newGain <= -1 && newGain >= -30) {
            value = String.valueOf(newGain + 256);
            SoundRun(value, FAUX_MIC, FAUX_MIC, context);
        }
    }

    public void setfauxhp(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= 0 && newGain <= 20) {
            SoundRun(value + " " + value, FAUX_HP, FAUX_HP, context);
        } else if (newGain <= -1 && newGain >= -30) {
            value = String.valueOf(newGain + 256);
            SoundRun(value + " " + value, FAUX_HP, FAUX_HP, context);
        }
    }

    public List<String> getFauxLimits() {
        return mFauxLimits;
    }

    public String getfauxspeaker() {
        String value = Utils.readFile(FAUX_SPEAKER);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 226 && gain <= 255) {
            return String.valueOf(gain - 256);
        } else if (gain == 172) {
            return String.valueOf(gain - 172);
        }
        return "";
    }

    public String getfauxmic() {
        String value = Utils.readFile(FAUX_MIC);
        int gain = Utils.strToInt(value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 226 && gain <= 255) {
            return String.valueOf(gain - 256);
        } else if (gain == 172) {
            return String.valueOf(gain - 172);
        }
        return "";
    }

    public String getfauxhp() {
        String value = Utils.readFile(FAUX_HP);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 226 && gain <= 255) {
            return String.valueOf(gain - 256);
        }
        return "";
    }

    public boolean hasfauxspeaker() {
        return Utils.existFile(FAUX_SPEAKER);
    }

    public boolean hasfauxmic() {
        return Utils.existFile(FAUX_MIC);
    }

    public boolean hasfauxhp() {
        return Utils.existFile(FAUX_HP);
    }

    public void setboefflaspeaker(String value, Context context) {
        SoundRun(value + " " + value, BOEFFLA_SPEAKER, BOEFFLA_SPEAKER, context);
    }

    public void setboefflaep(String value, Context context) {
        SoundRun(value + " " + value, BOEFFLA_EP, BOEFFLA_EP, context);
    }

    public List<String> getBoefflaLimits() {
        return mBoefflaLimits;
    }

    public List<String> getBoefflaEPLimits() {
        return mBoefflaEPLimits;
    }

    public String getboefflaspeaker() {
        String value = Utils.readFile(BOEFFLA_SPEAKER);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= -30 && gain <= 30) {
            return String.valueOf(gain);
        }
        return "";
    }

    public String getboefflaep() {
        String value = Utils.readFile(BOEFFLA_EP);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= -10 && gain <= 20) {
            return String.valueOf(gain);
        }
        return "";
    }

    public boolean hasboefflaspeaker() {
        return Utils.existFile(BOEFFLA_SPEAKER);
    }

    public boolean hasboefflaep() {
        return Utils.existFile(BOEFFLA_EP);
    }

    public void setboefflamic(String value, Context context) {
        run(Control.write(value, BOEFFLA_MIC), BOEFFLA_MIC, context);
    }

    public String getboefflamic() {
        return Utils.readFile(BOEFFLA_MIC).replace("Mic", "").replace("level", "").replace("call", "").replace(" ", "");
    }

    public List<String> getBoefflamicLimits() {
        return mBoefflaMICLimits;
    }

    public boolean hasboefflamic() {
        return Utils.existFile(BOEFFLA_MIC);
    }

    public void setboefflahpall(String value, Context context) {
        SoundRun(value + " " + value, BOEFFLA_HP, BOEFFLA_HP, context);
    }

    public void setboefflahp(String channel, String value, Context context) {
        switch (channel) {
            case "left":
                String currentGainRight = getboefflahp("right");
                SoundRun(value + " " + currentGainRight, BOEFFLA_HP, BOEFFLA_HP, context);
                break;
            case "right":
                String currentGainLeft = getboefflahp("left");
                SoundRun(currentGainLeft + " " + value, BOEFFLA_HP, BOEFFLA_HP, context);
                break;
        }
    }

    public String getboefflahp(String channel) {
        String[] values = Utils.readFile(BOEFFLA_HP).split(" ");
        String gainLeft = String.valueOf(Utils.strToInt(values[0])),
            gainRight = String.valueOf(Utils.strToInt(values[1]));
        switch (channel) {
            case "all":
            case "left":
                return gainLeft;
            case "right":
                return gainRight;
        }
        return "";
    }

    public boolean hasboefflahp() {
        return Utils.existFile(BOEFFLA_HP);
    }

    public void setflarep(String value, Context context) {
        SoundRun(value + " " + value, EARPIECE_FLAR, EARPIECE_FLAR, context);
    }

    public String getflarep() {
        String value = Utils.readFile(EARPIECE_FLAR);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 246 && gain <= 255) {
            return String.valueOf(gain - 256);
        }
        return "";
    }

    public boolean hasflarep() {
        return Utils.existFile(EARPIECE_FLAR);
    }

    public void setHeadphoneFlarAll(String value, Context context) {
        SoundRun(value + " " + value, HEADPHONE_FLAR, HEADPHONE_FLAR, context);
    }

    public void setHeadphoneFlar(String channel, String value, Context context) {
        switch (channel) {
            case "left":
                String currentGainRight = getHeadphoneFlar("right");
                SoundRun(value + " " + currentGainRight, HEADPHONE_FLAR, HEADPHONE_FLAR, context);
                break;
            case "right":
                String currentGainLeft = getHeadphoneFlar("left");
                SoundRun(currentGainLeft + " " + value, HEADPHONE_FLAR, HEADPHONE_FLAR, context);
                break;
        }
    }

    public String getHeadphoneFlar(String channel) {
        String[] values = Utils.readFile(HEADPHONE_FLAR).split(" ");
        int gainLeft = Utils.strToInt(values[0]),
            gainRight = Utils.strToInt(values[1]);
        switch (channel) {
            case "all":
            case "left":
		if (gainLeft >= 216) {
		    return String.valueOf(gainLeft - 256);
		} else {
		    return String.valueOf(gainLeft);
		}
            case "right":
		if (gainRight >= 216) {
		    return String.valueOf(gainRight - 256);
		} else {
		    return String.valueOf(gainRight);
		}
        }
        return "";
    }

    public List<String> getHeadphoneFlarLimits() {
        return mFlarHpLimits;
    }

    public boolean hasHeadphoneFlar() {
        return Utils.existFile(HEADPHONE_FLAR);
    }

    public void setMicrophoneFlar(String value, Context context) {
        run(Control.write(value, MICROPHONE_FLAR), MICROPHONE_FLAR, context);
    }

    public String getMicrophoneFlar() {
        int value = Utils.strToInt(Utils.readFile(MICROPHONE_FLAR));
        if (value >= 0 && value <= 20) {
            return String.valueOf(value);
        } else if (value >= 246 && value <= 255) {
            return String.valueOf(value - 256);
        }
        return "";
    }

    public List<String> getMicrophoneFlarLimits() {
        return mFlarLimits;
    }

    public boolean hasMicrophoneFlar() {
        return Utils.existFile(MICROPHONE_FLAR);
    }

    public void enablewcdspeakerleakage(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", WCD9320_SPEAKER_LEAKAGE), WCD9320_SPEAKER_LEAKAGE, context);
    }

    public boolean iswcdspeakerleakage() {
        return Utils.readFile(WCD9320_SPEAKER_LEAKAGE).equals("1");
    }

    public boolean haswcdspeakerleakage() {
       return Utils.existFile(WCD9320_SPEAKER_LEAKAGE);
    }

    public boolean hasSpeakerGain() {
        return SPEAKER_GAIN_FILE != null;
    }

    public boolean hasSoundControlDir() {
        return Utils.existFile(SOUND_CONTROL_DIR);
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SOUND, id, context);
    }

}
