package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class LMK implements Constants {

    public static void setMinFree(String minFree, Context context) {
        Control.runCommand(minFree, LMK_MINFREE, Control.CommandType.GENERIC, context);
    }

    public static int getMinFree(List<String> minfrees, int position) {
        return Utils.stringToInt(minfrees.get(position));
    }

    public static List<String> getMinFrees() {
        String value = Utils.readFile(LMK_MINFREE);
        if (value != null) return new ArrayList<>(Arrays.asList(value.split(",")));
        return null;
    }

}
