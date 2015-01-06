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
        Control.runCommand(value + " " + value, SPEAKER_GAIN, Control.CommandType.FAUX_GENERIC, context);
    }

    public static String getCurSpeakerGain() {
        return Utils.readFile(SPEAKER_GAIN).split(" ")[0];
    }

    public static List<String> getSpeakerGainLimits() {
        return getHeadphoneGainLimits();
    }

    public static boolean hasSpeakerGain() {
        return Utils.existFile(SPEAKER_GAIN);
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
        return Utils.readFile(HEADPHONE_GAIN.split(" ")[0]);
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

    public static boolean hasSound() {
        for (String file : SOUND_ARRAY)
            if (Utils.existFile(file)) return true;
        return false;
    }

}
