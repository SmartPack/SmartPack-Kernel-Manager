package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class LMK implements Constants {

    private static boolean su;

    public static void setMinFree(String minFree, Context context) {
        Control.runCommand(minFree, LMK_MINFREE, Control.CommandType.GENERIC, context);
    }

    public static int getMinFree(List<String> minfrees, int position) {
        String value = readFile(LMK_MINFREE);
        if (value != null)
            return Integer.parseInt(minfrees.get(position));
        return 0;
    }

    public static List<String> getMinFrees() {
        String value = readFile(LMK_MINFREE);
        if (value != null) return new ArrayList<>(Arrays.asList(value.split(",")));
        return null;
    }

    public static boolean hasMinFree() {
        String value = Utils.readFile(LMK_MINFREE);
        if (value == null) su = true;
        return true;
    }

    private static String readFile(String file) {
        if (su) return RootUtils.readFile(file);
        return Utils.readFile(file);
    }

}
