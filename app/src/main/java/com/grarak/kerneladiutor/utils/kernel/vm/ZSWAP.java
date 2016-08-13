package com.grarak.kerneladiutor.utils.kernel.vm;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 13.08.16.
 */

public class ZSwap {

    private static final String ZSWAP = "/sys/module/zswap/parameters/enabled";
    private static final String MAX_POOL_PERCENT = "/sys/module/zswap/parameters/max_pool_percent";
    private static final String MAX_COMPRESSION_RATIO = "/sys/module/zswap/parameters/max_compression_ratio";

    public static void setMaxCompressionRatio(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_COMPRESSION_RATIO), MAX_COMPRESSION_RATIO, context);
    }

    public static int getMaxCompressionRatio() {
        return Utils.strToInt(Utils.readFile(MAX_COMPRESSION_RATIO));
    }

    public static boolean hasMaxCompressionRatio() {
        return Utils.existFile(MAX_COMPRESSION_RATIO);
    }

    public static void setMaxPoolPercent(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_POOL_PERCENT), MAX_POOL_PERCENT, context);
    }

    public static int getMaxPoolPercent() {
        return Utils.strToInt(Utils.readFile(MAX_POOL_PERCENT));
    }

    public static boolean hasMaxPoolPercent() {
        return Utils.existFile(MAX_POOL_PERCENT);
    }

    public static void enable(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", ZSWAP), ZSWAP, context);
    }

    public static boolean isEnabled() {
        return Utils.readFile(ZSWAP).equals("Y");
    }

    public static boolean hasEnable() {
        return Utils.existFile(ZSWAP);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
