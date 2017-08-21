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

    private static final String SOUND_CONTROL_ENABLE = "/sys/module/snd_soc_wcd9320/parameters/enable_fs";
    private static final String HIGHPERF_MODE_ENABLE = "/sys/devices/virtual/misc/soundcontrol/highperf_enabled";
    private static final String HEADPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_gain";
    private static final String HANDSET_MICROPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_mic_gain";
    private static final String CAM_MICROPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_cam_mic_gain";
    private static final String SPEAKER_BOOST = "/sys/devices/virtual/misc/soundcontrol/speaker_boost";
    private static final String HEADPHONE_POWERAMP_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_pa_gain";

    private static final String TPA6165_REGISTERS_LIST = "/sys/kernel/debug/tpa6165/registers";
    private static final String TPA6165_SET_REG = "/sys/kernel/debug/tpa6165/set_reg";

    private static final String SPEAKER_GAIN = "/sys/kernel/sound_control_3/gpl_speaker_gain";
    private static final String LOCK_OUTPUT_GAIN = "/sys/kernel/sound_control_3/gpl_sound_control_locked";
    private static final String LOCK_MIC_GAIN = "/sys/kernel/sound_control_3/gpl_sound_control_rec_locked";

    private static final String MIC_BOOST = "/sys/devices/virtual/misc/soundcontrol/mic_boost";
    private static final String VOLUME_BOOST = "/sys/devices/virtual/misc/soundcontrol/volume_boost";

    private static final String HEADPHONE_FLAR = "/sys/kernel/sound_control/headphone_gain";
    private static final String MICROPHONE_FLAR = "/sys/kernel/sound_control/mic_gain";
    private static final String SPEAKER_FLAR = "/sys/kernel/sound_control/speaker_gain";

    private static final List<String> sSpeakerGainFiles = new ArrayList<>();

    private static final List<String> sFauxLimits = new ArrayList<>();
    private static final List<String> sFrancoLimits = new ArrayList<>();
    private static final List<String> sFlarLimits = new ArrayList<>();
    private static final List<String> sFlarHpLimits = new ArrayList<>();

    static {
        sSpeakerGainFiles.add(SPEAKER_GAIN);
        sSpeakerGainFiles.add(SPEAKER_BOOST);
        sSpeakerGainFiles.add(SPEAKER_FLAR);
    }

    static {
        for (int i = -30; i < 21; i++) {
            sFauxLimits.add(String.valueOf(i));
        }

        for (int i = -20; i < 21; i++) {
            sFrancoLimits.add(String.valueOf(i));
        }

        for (int i = -10; i < 21; i++) {
            sFlarLimits.add(String.valueOf(i));
        }
        
        for (int i = -40; i < 21; i++) {
            sFlarHpLimits.add(String.valueOf(i));
        }
    }

    private static String SPEAKER_GAIN_FILE;

    public static void setVolumeGain(String value, Context context) {
        run(Control.write(value, VOLUME_BOOST), VOLUME_BOOST, context);
    }

    public static String getVolumeGain() {
        return Utils.readFile(VOLUME_BOOST);
    }

    public static List<String> getVolumeGainLimits() {
        return sFrancoLimits;
    }

    public static boolean hasVolumeGain() {
        return Utils.existFile(VOLUME_BOOST);
    }

    public static void setMicrophoneGain(String value, Context context) {
        run(Control.write(value, MIC_BOOST), MIC_BOOST, context);
    }

    public static String getMicrophoneGain() {
        return Utils.readFile(MIC_BOOST);
    }

    public static List<String> getMicrophoneGainLimits() {
        return sFrancoLimits;
    }

    public static boolean hasMicrophoneGain() {
        return Utils.existFile(MIC_BOOST);
    }

    public static void enableLockMicGain(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LOCK_MIC_GAIN), LOCK_MIC_GAIN, context);
    }

    public static boolean isLockMicGainEnabled() {
        return Utils.readFile(LOCK_MIC_GAIN).equals("1");
    }

    public static boolean hasLockMicGain() {
        return Utils.existFile(LOCK_MIC_GAIN);
    }

    public static void enableLockOutputGain(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LOCK_OUTPUT_GAIN), LOCK_OUTPUT_GAIN, context);
    }

    public static boolean isLockOutputGainEnabled() {
        return Utils.readFile(LOCK_OUTPUT_GAIN).equals("1");
    }

    public static boolean hasLockOutputGain() {
        return Utils.existFile(LOCK_OUTPUT_GAIN);
    }

    public static void setHeadphonePowerAmpGain(String value, Context context) {
        value = String.valueOf(38 - Utils.strToInt(value));
        fauxRun(value + " " + value, HEADPHONE_POWERAMP_GAIN, HEADPHONE_POWERAMP_GAIN, context);
    }

    public static String getHeadphonePowerAmpGain() {
        return String.valueOf(38 - Utils.strToInt(Utils.readFile(HEADPHONE_POWERAMP_GAIN).split(" ")[0]));
    }

    public static List<String> getHeadphonePowerAmpGainLimits() {
        List<String> list = new ArrayList<>();
        for (int i = -6; i < 7; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    public static boolean hasHeadphonePowerAmpGain() {
        return Utils.existFile(HEADPHONE_POWERAMP_GAIN);
    }

    public static void setHeadphoneTpaGain(String value, Context context) {
        /*
         * Headphone Amp Gain is register 0x7.
         * Zero corresponds to 185 (0xb9). The min value is -24 (0xa1) and max is 6 (0xbf).
         */
        run(Control.chmod("222", TPA6165_SET_REG), TPA6165_SET_REG, context);
        run(Control.write("0x07 0x" + Integer.toHexString(185 + Utils.strToInt(value)),
                TPA6165_SET_REG), TPA6165_SET_REG, context);
    }

    public static String getHeadphoneTpaGain() {
        try {
            return String.valueOf(Integer.decode(RootUtils.runCommand("cat " + TPA6165_REGISTERS_LIST
                    + " | awk \"/0x7/\" | cut -c9-13")) - 185);
        } catch (NumberFormatException ignored) {
            return "";
        }
    }

    public static List<String> getHeadphoneTpaGainLimits() {
        List<String> list = new ArrayList<>();
        for (int i = -24; i < 7; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    public static boolean hasHeadphoneTpaGain() {
        return Utils.existFile(TPA6165_SET_REG) && Utils.existFile(TPA6165_REGISTERS_LIST);
    }

    public static void setSpeakerGain(String value, Context context) {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_GAIN:
                int newGain = Utils.strToInt(value);
                if (newGain >= 0 && newGain <= 20) {
                    // Zero / 1 to 20 (positive gain range)
                    fauxRun(value + " " + value, SPEAKER_GAIN, SPEAKER_GAIN, context);
                } else if (newGain <= -1 && newGain >= -30) {
                    // -1 to -30 (negative gain range)
                    value = String.valueOf(newGain + 256);
                    fauxRun(value + " " + value, SPEAKER_GAIN, SPEAKER_GAIN, context);
                }
                break;
            case SPEAKER_BOOST:
                run(Control.write(value, SPEAKER_BOOST), SPEAKER_BOOST, context);
                break;
            case SPEAKER_FLAR:
                run(Control.write(value, SPEAKER_FLAR), SPEAKER_FLAR, context);
                break;
        }
    }

    public static String getSpeakerGain() {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_GAIN:
                int gain = Utils.strToInt(Utils.readFile(SPEAKER_GAIN).split(" ")[0]);
                if (gain >= 0 && gain <= 20) {
                    return String.valueOf(gain);
                } else if (gain >= 226 && gain <= 255) {
                    return String.valueOf(gain - 256);
                }
                break;
            case SPEAKER_BOOST:
                return Utils.readFile(SPEAKER_BOOST);
            case SPEAKER_FLAR:
                return Utils.readFile(SPEAKER_FLAR);
        }
        return "";
    }

    public static List<String> getSpeakerGainLimits() {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_GAIN:
                return sFauxLimits;
            case SPEAKER_BOOST:
                return sFrancoLimits;
            case SPEAKER_FLAR:
                return sFlarLimits;
        }
        return new ArrayList<>();
    }

    public static boolean hasSpeakerGain() {
        if (SPEAKER_GAIN_FILE == null) {
            for (String file : sSpeakerGainFiles)
                if (Utils.existFile(file)) {
                    SPEAKER_GAIN_FILE = file;
                    return true;
                }
        }
        return SPEAKER_GAIN_FILE != null;
    }

    public static void setCamMicrophoneGain(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= 0 && newGain <= 20) {
            fauxRun(value, CAM_MICROPHONE_GAIN, CAM_MICROPHONE_GAIN, context);
        } else if (newGain <= -1 && newGain >= -30) {
            value = String.valueOf(newGain + 256);
            fauxRun(value, CAM_MICROPHONE_GAIN, CAM_MICROPHONE_GAIN, context);
        }
    }

    public static String getCamMicrophoneGain() {
        int gain = Utils.strToInt(Utils.readFile(CAM_MICROPHONE_GAIN));
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 226 && gain <= 255) {
            return String.valueOf(gain - 256);
        }

        return null;
    }

    public static List<String> getCamMicrophoneGainLimits() {
        return sFauxLimits;
    }

    public static boolean hasCamMicrophoneGain() {
        return Utils.existFile(CAM_MICROPHONE_GAIN);
    }

    public static void setHandsetMicrophoneGain(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= 0 && newGain <= 20) {
            fauxRun(value, HANDSET_MICROPHONE_GAIN, HANDSET_MICROPHONE_GAIN, context);
        } else if (newGain <= -1 && newGain >= -30) {
            value = String.valueOf(newGain + 256);
            fauxRun(value, HANDSET_MICROPHONE_GAIN, HANDSET_MICROPHONE_GAIN, context);
        }
    }

    public static String getHandsetMicrophoneGain() {
        int gain = Utils.strToInt(Utils.readFile(HANDSET_MICROPHONE_GAIN));
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 226 && gain <= 255) {
            return String.valueOf(gain - 256);
        }
        return "";
    }

    public static List<String> getHandsetMicrophoneGainLimits() {
        return sFauxLimits;
    }

    public static boolean hasHandsetMicrophoneGain() {
        return Utils.existFile(HANDSET_MICROPHONE_GAIN);
    }

    public static void setHeadphoneGain(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= 0 && newGain <= 20) {
            fauxRun(value + " " + value, HEADPHONE_GAIN, HEADPHONE_GAIN, context);
        } else if (newGain <= -1 && newGain >= -30) {
            value = String.valueOf(newGain + 256);
            fauxRun(value + " " + value, HEADPHONE_GAIN, HEADPHONE_GAIN, context);
        }
    }

    public static String getHeadphoneGain() {
        String value = Utils.readFile(HEADPHONE_GAIN);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 226 && gain <= 255) {
            return String.valueOf(gain - 256);
        }
        return "";
    }

    public static List<String> getHeadphoneGainLimits() {
        return sFauxLimits;
    }

    public static boolean hasHeadphoneGain() {
        return Utils.existFile(HEADPHONE_GAIN);
    }

    public static void enableHighPerfMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HIGHPERF_MODE_ENABLE), HIGHPERF_MODE_ENABLE, context);
    }

    public static boolean isHighPerfModeEnabled() {
        return Utils.readFile(HIGHPERF_MODE_ENABLE).equals("1");
    }

    public static boolean hasHighPerfModeEnable() {
        return Utils.existFile(HIGHPERF_MODE_ENABLE);
    }

    public static void enableSoundControl(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", SOUND_CONTROL_ENABLE), SOUND_CONTROL_ENABLE, context);
    }

    public static boolean isSoundControlEnabled() {
        return Utils.readFile(SOUND_CONTROL_ENABLE).equals("Y");
    }

    public static boolean hasSoundControlEnable() {
        return Utils.existFile(SOUND_CONTROL_ENABLE);
    }

    public static boolean supported() {
        return hasSoundControlEnable() || hasHighPerfModeEnable() || hasHeadphoneGain()
                || hasHandsetMicrophoneGain() || hasCamMicrophoneGain() || hasSpeakerGain()
                || hasHeadphonePowerAmpGain() || hasLockOutputGain() || hasLockMicGain()
                || hasMicrophoneGain() || hasVolumeGain() || hasHeadphoneFlar() 
                || hasMicrophoneFlar();
    }

    private static long getChecksum(int a, int b) {
        return (Integer.MAX_VALUE * 2L + 1L) ^ (a + b);
    }

    private static void fauxRun(String value, String path, String id, Context context) {
        long checksum = value.contains(" ") ?
                getChecksum(Utils.strToInt(value.split(" ")[0]),
                        Utils.strToInt(value.split(" ")[1])) :
                getChecksum(Utils.strToInt(value), 0);
        run(Control.write(value + " " + checksum, path), id, context);
        run(Control.write(value, path), id + "nochecksum", context);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SOUND, id, context);
    }

    public static void setHeadphoneFlar(String value, Context context) {
        int newGain = Utils.strToInt(value);
        if (newGain >= -40 && newGain <= 20) {
            fauxRun(value + " " + value, HEADPHONE_FLAR, HEADPHONE_FLAR, context);
        }
    }

    public static String getHeadphoneFlar() {
        String value = Utils.readFile(HEADPHONE_FLAR);
        int gain = Utils.strToInt(value.contains(" ") ? value.split(" ")[0] : value);
        if (gain >= 0 && gain <= 20) {
            return String.valueOf(gain);
        } else if (gain >= 216 && gain <= 255) {
            return String.valueOf(gain - 256);
        }
        return "";
    }

    public static List<String> getHeadphoneFlarLimits() {
        return sFlarHpLimits;
    }

    public static boolean hasHeadphoneFlar() {
        return Utils.existFile(HEADPHONE_FLAR);
    }

    public static void setMicrophoneFlar(String value, Context context) {
        run(Control.write(value, MICROPHONE_FLAR), MICROPHONE_FLAR, context);
    }

    public static String getMicrophoneFlar() {
        return Utils.readFile(MICROPHONE_FLAR);
    }

    public static List<String> getMicrophoneFlarLimits() {
        return sFlarLimits;
    }

    public static boolean hasMicrophoneFlar() {
        return Utils.existFile(MICROPHONE_FLAR);
    }

}
