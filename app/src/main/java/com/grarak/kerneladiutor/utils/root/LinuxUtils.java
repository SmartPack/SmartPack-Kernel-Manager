package com.grarak.kerneladiutor.utils.root;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This code is from http://stackoverflow.com/a/13342738
 */
public class LinuxUtils {

    /**
     * Return the first line of /proc/stat or null if failed.
     */
    public String readSystemStat() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String value = reader.readLine();
            reader.close();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Compute and return the total CPU usage, in percent.
     *
     * @param start first content of /proc/stat. Not null.
     * @param end   second content of /proc/stat. Not null.
     * @return the CPU use in percent, or -1f if the stats are inverted or on
     * error
     * @see {@link #readSystemStat()}
     */
    public float getSystemCpuUsage(String start, String end) {
        String[] stat = start.split(" ");
        long idle1 = getSystemIdleTime(stat);
        long up1 = getSystemUptime(stat);

        stat = end.split(" ");
        long idle2 = getSystemIdleTime(stat);
        long up2 = getSystemUptime(stat);

        // don't know how it is possible but we should care about zero and
        // negative values.
        float cpu = -1f;
        if (idle1 >= 0 && up1 >= 0 && idle2 >= 0 && up2 >= 0) {
            if ((up2 + idle2) > (up1 + idle1) && up2 >= up1) {
                cpu = (up2 - up1) / (float) ((up2 + idle2) - (up1 + idle1));
                cpu *= 100.0f;
            }
        }

        return cpu;
    }

    /**
     * Return the sum of uptimes read from /proc/stat.
     *
     * @param stat see {@link #readSystemStat()}
     */
    public long getSystemUptime(String[] stat) {
        long l = 0L;
        for (int i = 2; i < stat.length; i++) {
            if (i != 5) { // bypass any idle mode. There is currently only one.
                try {
                    l += Long.parseLong(stat[i]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return -1L;
                }
            }
        }

        return l;
    }

    /**
     * Return the sum of idle times read from /proc/stat.
     *
     * @param stat see {@link #readSystemStat()}
     */
    public long getSystemIdleTime(String[] stat) {
        try {
            return Long.parseLong(stat[5]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1L;
    }

}