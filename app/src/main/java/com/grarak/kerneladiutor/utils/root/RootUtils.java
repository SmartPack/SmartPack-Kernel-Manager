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

    public static void runCommand(final String command) {
        if (su == null) su = new SU();
        su.run(command);
    }

    public static boolean rooted() {
        return RootTools.isRootAvailable();
    }

    public static boolean rootAccess() {
        return RootTools.isAccessGiven();
    }

    public static boolean busyboxInstalled() {
        return RootTools.isBusyboxAvailable();
    }

    public static String readFile(String file) {
        if (su == null) su = new SU();
        return su.runCommand("cat " + file);
    }

    public static boolean fileExist(String file) {
        if (su == null) su = new SU();
        String output = su.runCommand("[ -e " + file + " ] && echo true");
        return output != null && output.contains("true");
    }

    public static boolean moduleActive(String module) {
        if (su == null) su = new SU();
        String output = su.runCommand("echo `ps | grep " + module + " | grep -v \"grep "
                + module + "\" | awk '{print $1}'`");
        return output != null && output.length() > 0;
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
                process = Runtime.getRuntime().exec("su");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } catch (IOException e) {
                Log.e(TAG, "Failed to run shell as su");
            }
        }

        public synchronized void run(final String command) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bufferedWriter.write(command + "\n");
                        bufferedWriter.flush();

                        Log.i(TAG, "run " + command);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to run " + command);
                    }
                }
            }).start();
        }

        public synchronized String runCommand(final String command) {
            StringBuilder sb = new StringBuilder();

            try {
                String callback = "/shellCallback/";
                bufferedWriter.write(command + "\necho " + callback + "\n");
                bufferedWriter.flush();

                int i;
                char[] buffer = new char[16];
                while (true) {
                    i = bufferedReader.read(buffer);
                    sb.append(buffer, 0, i);
                    if ((i = sb.indexOf(callback)) > -1) {
                        sb.delete(i, i + callback.length());
                        break;
                    }
                }

                Log.i(TAG, "Output of " + command + " : " + sb.toString().trim());
            } catch (IOException e) {
                Log.e(TAG, "Failed to run " + command);
            }

            return sb.toString().trim();
        }

        public void close() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bufferedWriter.write("exit\n");
                        bufferedWriter.flush();

                        process.waitFor();
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
