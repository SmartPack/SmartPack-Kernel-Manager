package com.grarak.kerneladiutor.utils.root;

import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by willi on 14.12.14.
 */
public class RootUtils implements Constants {

    public static SU su;

    public static boolean rooted() {
        return RootTools.isRootAvailable();
    }

    public static boolean rootAccess() {
        return RootTools.isAccessGiven();
    }

    public static boolean busyboxInstalled() {
        return RootTools.isBusyboxAvailable();
    }

    public static void mount(boolean writeable, String mountpoint) {
        runCommand(writeable ? "mount -o rw,remount " + mountpoint : "mount -o ro,remount " + mountpoint);
    }

    public static boolean moduleActive(String module) {
        String output = runCommand("echo `ps | grep " + module + " | grep -v \"grep "
                + module + "\" | awk '{print $1}'`");
        return output != null && output.length() > 0;
    }

    public static String runCommand(String command) {
        if (su == null) su = new SU();
        return su.runCommand(command);
    }

    /**
     * Based on AndreiLux's SU code in Synapse
     * https://github.com/AndreiLux/Synapse/blob/master/src/main/java/com/af/synapse/utils/Utils.java#L238
     */
    public static class SU {

        private Process process;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;

        public SU() {
            try {
                Log.i(TAG, "SU initialized");
                process = Runtime.getRuntime().exec("su");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } catch (IOException e) {
                Log.e(TAG, "Failed to run shell as su");
            }
        }

        public synchronized String runCommand(final String command) {
            try {
                StringBuilder sb = new StringBuilder();
                String callback = "/shellCallback/";
                bufferedWriter.write(command + "\necho " + callback + "\n");
                bufferedWriter.flush();

                int i;
                char[] buffer = new char[256];
                while (true) {
                    sb.append(buffer, 0, bufferedReader.read(buffer));
                    if ((i = sb.indexOf(callback)) > -1) {
                        sb.delete(i, i + callback.length());
                        break;
                    }
                }
                return sb.toString().trim();
            } catch (IOException e) {
                Log.e(TAG, "Failed to run " + command);
                return null;
            }

        }

        public void close() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bufferedWriter.write("exit\n");
                        bufferedWriter.flush();

                        process.waitFor();
                        Log.i(TAG, "SU closed");
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close BufferWriter");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

}
