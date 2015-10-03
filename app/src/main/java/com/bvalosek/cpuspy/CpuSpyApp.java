//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
//
//-----------------------------------------------------------------------------
// Modified by Willi Ye to work with big.LITTLE

package com.bvalosek.cpuspy;

import android.app.Application;
import android.content.Context;

import com.grarak.kerneladiutor.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * main application class
 */
public class CpuSpyApp extends Application {

    private final String PREF_OFFSETS;

    /**
     * the long-living object used to monitor the system frequency states
     */
    private final CpuStateMonitor _monitor;

    public CpuSpyApp(int core) {
        PREF_OFFSETS = "offsets" + core;
        _monitor = new CpuStateMonitor(core);
    }

    /**
     * On application start, load the saved offsets and stash the current kernel
     * version string
     */
    @Override
    public void onCreate() {
        super.onCreate();
        loadOffsets(getApplicationContext());
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
        String prefs = Utils.getString(PREF_OFFSETS, "", context);

        if (prefs.length() < 1) return;

        // split the string by peroids and then the info by commas and load
        Map<Integer, Long> offsets = new HashMap<>();
        String[] sOffsets = prefs.split(",");
        for (String offset : sOffsets) {
            String[] parts = offset.split(" ");
            offsets.put(Utils.stringToInt(parts[0]), Utils.stringToLong(parts[1]));
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

        Utils.saveString(PREF_OFFSETS, str, context);
    }
}
