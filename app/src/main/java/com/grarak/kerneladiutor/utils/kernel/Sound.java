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

package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.01.15.
 */
public class Sound implements Constants {

    private static String SPEAKER_GAIN_FILE;

    public static void setVolumeGain(String value, Context context) {
        Control.runCommand(value, VOLUME_BOOST, Control.CommandType.GENERIC, context);
    }

    public static String getVolumeGain() {
        return Utils.readFile(VOLUME_BOOST);
    }

    public static List<String> getVolumeGainLimits() {
        return getFrancoSoundLimits();
    }

    public static boolean hasVolumeGain() {
        return Utils.existFile(VOLUME_BOOST);
    }

    public static void setMicrophoneGain(String value, Context context) {
        Control.runCommand(value, MIC_BOOST, Control.CommandType.GENERIC, context);
    }

    public static String getMicrophoneGain() {
        return Utils.readFile(MIC_BOOST);
    }

    public static List<String> getMicrophoneGainLimits() {
        return getFrancoSoundLimits();
    }

    public static boolean hasMicrophoneGain() {
        return Utils.existFile(MIC_BOOST);
    }

    public static void setHeadphonePowerAmpGain(String value, Context context) {
        Control.runCommand(value + " " + value, HEADPHONE_POWERAMP_GAIN, Control.CommandType.FAUX_GENERIC, context);
    }

    public static String getCurHeadphonePowerAmpGain() {
        return Utils.readFile(HEADPHONE_POWERAMP_GAIN).split(" ")[0];
    }

    public static List<String> getHeadphonePowerAmpGainLimits() {
        List<String> list = new ArrayList<>();
        for (int i = -6; i < 7; i++)
            list.add(String.valueOf(i));
        return list;
    }

    public static boolean hasHeadphonePowerAmpGain() {
        return Utils.existFile(HEADPHONE_POWERAMP_GAIN);
    }

    public static void setSpeakerGain(String value, Context context) {
        Control.runCommand(value + " " + value, SPEAKER_GAIN_FILE,
                SPEAKER_GAIN_FILE.equals(SPEAKER_GAIN) ? Control.CommandType.FAUX_GENERIC : Control.CommandType.GENERIC, context);
    }

    public static String getCurSpeakerGain() {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_GAIN:
                return Utils.readFile(SPEAKER_GAIN).split(" ")[0];
            case SPEAKER_BOOST:
                return Utils.readFile(SPEAKER_BOOST);
        }
        return null;
    }

    public static List<String> getSpeakerGainLimits() {
        switch (SPEAKER_GAIN_FILE) {
            case SPEAKER_GAIN:
                return getHeadphoneGainLimits();
            case SPEAKER_BOOST:
                return getFrancoSoundLimits();
        }
        return null;
    }

    public static boolean hasSpeakerGain() {
        for (String file : SPEAKER_GAIN_ARRAY)
            if (Utils.existFile(file)) {
                SPEAKER_GAIN_FILE = file;
                return true;
            }
        return false;
    }

    public static List<String> getFrancoSoundLimits() {
        List<String> list = new ArrayList<>();
        for (int i = -20; i < 21; i++)
            list.add(String.valueOf(i));
        return list;
    }

    public static void setCamMicrophoneGain(String value, Context context) {
        Control.runCommand(value, CAM_MICROPHONE_GAIN, Control.CommandType.FAUX_GENERIC, context);
    }

    public static String getCurCamMicrophoneGain() {
        return Utils.readFile(CAM_MICROPHONE_GAIN);
    }

    public static List<String> getCamMicrophoneGainLimits() {
        return getHeadphoneGainLimits();
    }

    public static boolean hasCamMicrophoneGain() {
        return Utils.existFile(CAM_MICROPHONE_GAIN);
    }

    public static void setHandsetMicrophoneGain(String value, Context context) {
        Control.runCommand(value, HANDSET_MICROPONE_GAIN, Control.CommandType.FAUX_GENERIC, context);
    }

    public static String getCurHandsetMicrophoneGain() {
        return Utils.readFile(HANDSET_MICROPONE_GAIN);
    }

    public static List<String> getHandsetMicrophoneGainLimits() {
        return getHeadphoneGainLimits();
    }

    public static boolean hasHandsetMicrophoneGain() {
        return Utils.existFile(HANDSET_MICROPONE_GAIN);
    }

    public static void setHeadphoneGain(String value, Context context) {
        Control.runCommand(value + " " + value, HEADPHONE_GAIN, Control.CommandType.FAUX_GENERIC, context);
    }

    public static String getCurHeadphoneGain() {
        return Utils.readFile(HEADPHONE_GAIN).split(" ")[0];
    }

    public static List<String> getHeadphoneGainLimits() {
        List<String> list = new ArrayList<>();
        for (int i = -30; i < 21; i++)
            list.add(String.valueOf(i));
        return list;
    }

    public static boolean hasHeadphoneGain() {
        return Utils.existFile(HEADPHONE_GAIN);
    }

    public static void activateSoundControl(boolean active, Context context) {
        Control.runCommand(active ? "Y" : "N", SOUND_CONTROL_ENABLE, Control.CommandType.GENERIC, context);
    }

    public static boolean isSoundControlActive() {
        return Utils.readFile(SOUND_CONTROL_ENABLE).equals("Y");
    }

    public static boolean hasSoundControlEnable() {
        return Utils.existFile(SOUND_CONTROL_ENABLE);
    }

    public static boolean hasSound() {
        for (String[] array : SOUND_ARRAY)
            for (String file : array)
                if (Utils.existFile(file)) return true;
        return false;
    }

    public static void activateLockOutputGain(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", LOCK_OUTPUT_GAIN, Control.CommandType.GENERIC, context);
    }

    public static boolean isLockOutputGainActive() {
        return Utils.readFile(LOCK_OUTPUT_GAIN).equals("1");
    }

    public static boolean hasLockOutputGain() {
        return Utils.existFile(LOCK_OUTPUT_GAIN);
    }

    public static void activateLockMicGain(boolean active, Context context) {
        Control.runCommand(active ? "1" : "0", LOCK_MIC_GAIN, Control.CommandType.GENERIC, context);
    }

    public static boolean isLockMicGainActive() {
        return Utils.readFile(LOCK_MIC_GAIN).equals("1");
    }

    public static boolean hasLockMicGain() {
        return Utils.existFile(LOCK_MIC_GAIN);
    }

}
