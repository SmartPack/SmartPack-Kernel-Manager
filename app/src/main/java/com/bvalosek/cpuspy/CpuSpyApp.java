//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
// (C) Willi Ye, 2015 <williye97@gmail.com>
//
//-----------------------------------------------------------------------------
// Modified by Willi Ye to work with big.LITTLE

package com.bvalosek.cpuspy;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * main application class
 */
public class CpuSpyApp {

    private final String PREF_OFFSETS;

    /**
     * the long-living object used to monitor the system frequency states
     */
    private final CpuStateMonitor _monitor;

    public CpuSpyApp(int core, Context context) {
        PREF_OFFSETS = "offsets" + core;
        _monitor = new CpuStateMonitor(core);
        loadOffsets(context);
    }

    /**
     * @return the internal CpuStateMonitor object
     */
    public CpuStateMonitor getCpuStateMonitor() {
        return _monitor;
    }

    /**
     * Load the saved string of offsets from preferences and put it into the
     * state monitor
     */
    public void loadOffsets(Context context) {
        String prefs = Prefs.getString(PREF_OFFSETS, "", context);

        if (prefs.length() < 1) return;

        // split the string by peroids and then the info by commas and load
        Map<Integer, Long> offsets = new HashMap<>();
        String[] sOffsets = prefs.split(",");
        for (String offset : sOffsets) {
            String[] parts = offset.split(" ");
            offsets.put(Utils.strToInt(parts[0]), Utils.strToLong(parts[1]));
        }

        _monitor.setOffsets(offsets);
    }

    /**
     * Save the state-time offsets as a string e.g. "100 24, 200 251, 500 124
     * etc
     */
    public void saveOffsets(Context context) {
        // build the string by iterating over the freq->duration map
        String str = "";
        for (Map.Entry<Integer, Long> entry : _monitor.getOffsets().entrySet())
            str += entry.getKey() + " " + entry.getValue() + ",";

        Prefs.saveString(PREF_OFFSETS, str, context);
    }
}
