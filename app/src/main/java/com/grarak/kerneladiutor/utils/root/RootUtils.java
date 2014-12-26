package com.grarak.kerneladiutor.utils.root;

import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by willi on 14.12.14.
 */
public class RootUtils implements Constants {

    public static void runCommand(final String command) {
        new Thread() {
            public void run() {
                try {
                    RootTools.getShell(true).add(new CommandCapture(0, command)).commandCompleted(0, 0);
                    Log.i(TAG, "open shell: " + command);
                } catch (IOException e) {
                    Log.e(TAG, "failed to run " + command);
                } catch (TimeoutException ignored) {
                    Log.e(TAG, "Timeout: Cannot gain root access");
                } catch (RootDeniedException e) {
                    Log.e(TAG, "Root access denied");
                }
            }
        }.start();
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

    // Thanks to Performance Control creators for this code!
    public static boolean moduleActive(String module) {
        String output = null;
        try {
            output = getOutput("echo `ps | grep " + module
                            + " | grep -v \"grep " + module + "\" | awk '{print $1}'`",
                    true);
        } catch (IOException e) {
            Log.e(TAG, "failed to get status of " + module);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return output != null && output.length() > 0 && !output.equals("error");
    }

    // Thanks to Performance Control creators for this code!
    private static String getOutput(String command, boolean debug) throws IOException,
            InterruptedException {
        Process process = Runtime.getRuntime().exec("sh");
        final DataOutputStream processStream = new DataOutputStream(
                process.getOutputStream());
        processStream.writeBytes("exec " + command + "\n");
        processStream.flush();

        int exit;
        String output = null;
        exit = process.waitFor();

        StringBuffer buffer = null;
        final DataInputStream inputStream = new DataInputStream(
                process.getInputStream());

        if (inputStream.available() > 0) {
            buffer = new StringBuffer(inputStream.readLine());
            while (inputStream.available() > 0)
                buffer.append("\n").append(inputStream.readLine());
        }
        inputStream.close();
        if (buffer != null) output = buffer.toString();

        if (debug) Log.d(TAG, "Output of " + command + ": " + output);

        return exit != 1 && exit == 0 ? output : "error";
    }

}
