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
    private static final String HIGHPERF_MODE_ENABLE = "/sys/devices/virtual/misc/soundcontrol/highperf_enabled";
    private static final String SPEAKER_BOOST = "/sys/devices/virtual/misc/soundcontrol/speaker_boost";

    private static final String TPA6165_REGISTERS_LIST = "/sys/kernel/debug/tpa6165/registers";
    private static final String TPA6165_SET_REG = "/sys/kernel/debug/tpa6165/set_reg";

    private static final String MIC_BOOST = "/sys/devices/virtual/misc/soundcontrol/mic_boost";
    private static final String VOLUME_BOOST = "/sys/devices/virtual/misc/soundcontrol/volume_boost";

    private static final String HEADPHONE_FLAR = "/sys/kernel/sound_control/headphone_gain";
    private static final String MICROPHONE_FLAR = "/sys/kernel/sound_control/mic_gain";
    private static final String SPEAKER_FLAR = "/sys/kernel/sound_control/speaker_gain";

    private static final String WCD9320_SPEAKER_LEAKAGE = "/sys/module/snd_soc_wcd9320/parameters/spkr_drv_wrnd";

    private static final String BOEFFLA_SOUND = "/sys/class/misc/boeffla_sound/boeffla_sound";
    private static final String BOEFFLA_SPEAKER = "/sys/class/misc/boeffla_sound/speaker_volume";
    private static final String BOEFFLA_HP = "/sys/class/misc/boeffla_sound/headphone_volume";
    private static final String BOEFFLA_VERSION = "/sys/class/misc/boeffla_sound/version";

    private final List<String> mSpeakerGainFiles = new ArrayList<>();

    private final List<String> mFrancoLimits = new ArrayList<>();
    private final List<String> mFlarLimits = new ArrayList<>();
    private final List<String> mFlarHpLimits = new ArrayList<>();
    private final List<String> mboefflaLimits = new ArrayList<>();

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
            mboefflaLimits.add(String.valueOf(i));
        }
    }

    private String SPEAKER_GAIN_FILE;

    private Sound() {
        for (String file : mSpeakerGainFiles) {
            if (Utils.existFile(file)) {
                SPEAKER_GAIN_FILE = file;
                break;
            }
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

    public void setMicrophoneGain(String value, Context context) {
        run(Control.write(value, MIC_BOOST), MIC_BOOST, context);
    }

    public String getMicrophoneGain() {
        return Utils.readFile(MIC_BOOST);
    }

    public List<String> getMicrophoneGainLimits() {
        return mFrancoLimits;
    }

    public boolean hasMicrophoneGain() {
        return Utils.existFile(MIC_BOOST);
    }

    public void enableboefflasound(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", BOEFFLA_SOUND), BOEFFLA_SOUND, context);
    }

    public boolean isboefflasoundenabled() {
        return Utils.readFile(BOEFFLA_SOUND).equals("Boeffla sound status: 1");
    }

    public boolean hasboefflasound() {
       return Utils.existFile(BOEFFLA_SOUND);
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
                return mboefflaLimits;
            case BOEFFLA_HP:
                return mboefflaLimits;
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

    public boolean supported() {
        return hasSoundControlEnable() || hasHighPerfModeEnable() || haswcdspeakerleakage()
                || hasMicrophoneGain() || hasVolumeGain() || hasboefflasound() || hasHeadphoneFlar() ||hasMicrophoneFlar()
                || hasboefflahp() || hasboefflaspeaker();
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

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SOUND, id, context);
    }

    public void setboefflaspeaker(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= -30 && newGain <= 30) {
            SoundRun(value + " " + value, BOEFFLA_SPEAKER, BOEFFLA_SPEAKER, context);
        }
    }

    public List<String> getboefflaLimits() {
        return mboefflaLimits;
    }

    public String getboefflaspeaker() {
        String value = Utils.readFile(BOEFFLA_SPEAKER);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= -30 && gain <= 30) {
            return String.valueOf(gain);
        }
        return "";
    }

    public boolean hasboefflaspeaker() {
        return Utils.existFile(BOEFFLA_SPEAKER);
    }

    public void setboefflahp(String channel, String value, Context context) {
        int newGain = Utils.strToInt(value);
        switch (channel) {
            case "all":
                if (newGain >= -30 && newGain <= 30) {
                    SoundRun(value + " " + value, BOEFFLA_HP, BOEFFLA_HP, context);
                }
            case "left":
                String currentGainLeft = getboefflahp("right");
                if (newGain >= -30 && newGain <= 30) {
                    SoundRun(value + " " + currentGainLeft, BOEFFLA_HP, BOEFFLA_HP, context);
                }
            case "right":
                String currentGainRight = getboefflahp("left");
                if (newGain >= -30 && newGain <= 30) {
                    SoundRun(value + " " + currentGainRight, BOEFFLA_HP, BOEFFLA_HP, context);
                }
        }
    }

    public static String getboefflahp(String channel) {
        String[] values = Utils.readFile(BOEFFLA_HP).split(" ");
        int gainLeft = Utils.strToInt(values[0]),
            gainRight = Utils.strToInt(values[1]);
        switch (channel) {
            case "all":
            case "left":
                if (gainLeft >= -30 && gainLeft <= 30) {
                    return String.valueOf(gainLeft);
                }
           case "right":
                if (gainRight >= -30 && gainRight <= 30) {
                    return String.valueOf(gainRight);
                }
        }
        return "";
    }

    public boolean hasboefflahp() {
        return Utils.existFile(BOEFFLA_HP);
    }

    public void setHeadphoneFlar(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= -40 && newGain <= 20) {
            SoundRun(value + " " + value, HEADPHONE_FLAR, HEADPHONE_FLAR, context);
        }
    }

    public String getHeadphoneFlar() {
        String value = Utils.readFile(HEADPHONE_FLAR);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 216 && gain <= 255) {
            return String.valueOf(gain - 256);
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
        return Utils.readFile(MICROPHONE_FLAR);
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
   

}
