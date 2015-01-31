//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
//
//-----------------------------------------------------------------------------

package com.bvalosek.cpuspy;

// imports

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CpuStateMonitor is a class responsible for querying the system and getting
 * the time-in-state information, as well as allowing the user to set/reset
 * offsets to "restart" the state timers
 */
public class CpuStateMonitor implements Constants {

    private List<CpuState> _states = new ArrayList<>();
    private Map<Integer, Long> _offsets = new HashMap<>();

    /**
     * exception class
     */
    public class CpuStateMonitorException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public CpuStateMonitorException(String s) {
            super(s);
        }
    }

    /**
     * simple struct for states/time
     */
    public class CpuState implements Comparable<CpuState> {
        /**
         * init with freq and duration
         */
        public CpuState(int a, long b) {
            freq = a;
            duration = b;
        }

        public int freq = 0;
        public long duration = 0;

        /**
         * for sorting, compare the freqs
         */
        public int compareTo(@NonNull CpuState state) {
            Integer a = freq;
            Integer b = state.freq;
            return a.compareTo(b);
        }
    }

    /**
     * @return List of CpuState with the offsets applied
     */
    public List<CpuState> getStates() {
        List<CpuState> states = new ArrayList<>();

        /*
         * check for an existing offset, and if it's not too big, subtract it
         * from the duration, otherwise just add it to the return List
         */
        for (CpuState state : _states) {
            long duration = state.duration;
            if (_offsets.containsKey(state.freq)) {
                long offset = _offsets.get(state.freq);
                if (offset <= duration) {
                    duration -= offset;
                } else {
                    /*
                     * offset > duration implies our offsets are now invalid, so
                     * clear and recall this function
                     */
                    _offsets.clear();
                    return getStates();
                }
            }

            states.add(new CpuState(state.freq, duration));
        }

        return states;
    }

    /**
     * @return Sum of all state durations including deep sleep, accounting for
     * offsets
     */
    public long getTotalStateTime() {
        long sum = 0;
        long offset = 0;

        for (CpuState state : _states) {
            sum += state.duration;
        }

        for (Map.Entry<Integer, Long> entry : _offsets.entrySet()) {
            offset += entry.getValue();
        }

        return sum - offset;
    }

    /**
     * @return Map of freq->duration of all the offsets
     */
    public Map<Integer, Long> getOffsets() {
        return _offsets;
    }

    /**
     * Sets the offset map (freq->duration offset)
     */
    public void setOffsets(Map<Integer, Long> offsets) {
        _offsets = offsets;
    }

    /**
     * Updates the current time in states and then sets the offset map to the
     * current duration, effectively "zeroing out" the timers
     */
    public void setOffsets() throws CpuStateMonitorException {
        _offsets.clear();
        updateStates();

        for (CpuState state : _states) {
            _offsets.put(state.freq, state.duration);
        }
    }

    /**
     * removes state offsets
     */
    public void removeOffsets() {
        _offsets.clear();
    }

    /**
     * @return a list of all the CPU frequency states, which contains both a
     * frequency and a duration (time spent in that state
     */
    public List<CpuState> updateStates() throws CpuStateMonitorException {
        _states.clear();
        readInStates();

        /*
         * deep sleep time determined by difference between elapsed (total) boot
         * time and the system uptime (awake)
         */
        long sleepTime = (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 10;
        _states.add(new CpuState(0, sleepTime));

        Collections.sort(_states, Collections.reverseOrder());

        return _states;
    }

    /**
     * read from a provided BufferedReader the state lines into the States
     * member field
     */
    private void readInStates() {
        String value = Utils.readFile(CPU_TIME_STATE);

        // split open line and convert to Integers
        if (value == null) return;
        for (String line : value.split("\\r?\\n")) {
            String[] nums = line.split(" ");
            if (nums.length > 0)
                _states.add(new CpuState(Utils.stringToInt(nums[0]), Utils.stringToLong(nums[1])));
        }
    }

}