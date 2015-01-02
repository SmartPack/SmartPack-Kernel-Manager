package com.grarak.kerneladiutor.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 02.01.15.
 */
public class Misc implements Constants {

    private static String[] TCP_CONGESTIONS;

    public static void setTcpCongestion(String tcpCongestion, Context context) {
        Control.runCommand(tcpCongestion, null, Control.CommandType.TCP_CONGESTION, context);
    }

    public static String getCurTcpCongestion() {
        return getTcpAvailableCongestions().get(0);
    }

    public static List<String> getTcpAvailableCongestions() {
        if (TCP_CONGESTIONS == null)
            TCP_CONGESTIONS = Utils.readFile(TCP_AVAILABLE_CONGESTIONS).split(" ");
        return new ArrayList<>(Arrays.asList(TCP_CONGESTIONS));
    }

}
