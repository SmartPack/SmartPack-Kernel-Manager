package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.01.15.
 */
public class Wake implements Constants {

    private static String DT2W_FILE;
    private static String S2W_FILE;

    public static void setS2w(int value, Context context) {
        Control.runCommand(String.valueOf(value), S2W_FILE, Control.CommandType.GENERIC, context);
    }

    public static int getS2wValue() {
        if (S2W_FILE != null) return Integer.parseInt(Utils.readFile(S2W_FILE));
        return 0;
    }

    public static List<String> getS2wMenu(Context context) {
        List<String> list = new ArrayList<>();
        if (S2W_FILE != null) {
            if (S2W_FILE.equals(S2W_ONLY)) {
                list.add(context.getString(R.string.disabled));
                list.add(context.getString(R.string.s2s));
            } else if (S2W_FILE.equals(SW2)) {
                list.add(context.getString(R.string.disabled));
                list.add(context.getString(R.string.s2w) + " + " + context.getString(R.string.s2s));
                list.add(context.getString(R.string.s2s));
            }
        }
        return list;
    }

    public static boolean hasS2w() {
        if (S2W_FILE == null)
            for (String file : S2W_ARRY)
                if (Utils.existFile(file)) {
                    S2W_FILE = file;
                    break;
                }
        return S2W_FILE != null;
    }

    public static void setDt2w(int value, Context context) {
        Control.runCommand(String.valueOf(value), DT2W_FILE, Control.CommandType.GENERIC, context);
    }

    public static int getDt2wValue() {
        try {
            if (DT2W_FILE != null)
                return Integer.parseInt(Utils.readFile(DT2W_FILE));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<String> getDt2wMenu(Context context) {
        List<String> list = new ArrayList<>();
        if (DT2W_FILE != null) {
            if (DT2W_FILE.equals(LGE_TOUCH_CORE_DT2W)) {
                list.add(context.getString(R.string.disabled));
                list.add(context.getString(R.string.center));
                list.add(context.getString(R.string.full));
                list.add(context.getString(R.string.bottom_half));
                list.add(context.getString(R.string.top_half));
            } else {
                list.add(context.getString(R.string.disabled));
                list.add(context.getString(R.string.enabled));
            }
        }
        return list;
    }

    public static boolean hasDt2w() {
        if (DT2W_FILE == null)
            for (String file : DT2W_ARRAY)
                if (Utils.existFile(file)) {
                    DT2W_FILE = file;
                    break;
                }
        return DT2W_FILE != null;
    }

    public static boolean hasWake() {
        for (String[] wakes : WAKE_ARRAY)
            for (String file : wakes)
                if (Utils.existFile(file)) return true;
        return false;
    }

}
