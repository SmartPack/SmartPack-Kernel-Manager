//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
//
//-----------------------------------------------------------------------------
// Modified by Willi Ye to work as Fragment

package com.grarak.kerneladiutor.fragments;

// imports

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bvalosek.cpuspy.CpuSpyApp;
import com.bvalosek.cpuspy.CpuStateMonitor;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * main activity class
 */
public class FrequencyTableFragment extends RecyclerViewFragment implements Constants {

    private final CpuSpyApp cpuSpyApp = new CpuSpyApp();

    private CardViewItem.DCardView runningCard;
    private CardViewItem.DCardView frequencyCard;
    private CardViewItem.DCardView additionalCard;
    private LinearLayout _uiStatesView;

    /**
     * whether or not we're updating the data in the background
     */
    private boolean _updatingData;

    /**
     * Initialize the Fragment
     */
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.frequency_table_fragment, null, false);
        _uiStatesView = (LinearLayout) view.findViewById(R.id.ui_states_view);

        runningCard = new CardViewItem.DCardView();
        runningCard.setTitle(getString(R.string.running_time));

        frequencyCard = new CardViewItem.DCardView();
        frequencyCard.setTitle(getString(R.string.frequency_table));
        frequencyCard.setView(view);

        additionalCard = new CardViewItem.DCardView();
        additionalCard.setTitle(getString(R.string.unused_cpu_states));

        // see if we're updating data during a config change (rotate
        // screen)
        if (savedInstanceState != null)
            _updatingData = savedInstanceState.getBoolean("updatingData");

        if (isAdded()) refreshData();

        addView(runningCard);
        addView(frequencyCard);
    }

    /**
     * When the activity is about to change orientation
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("updatingData", _updatingData);
    }

    /**
     * called when we want to inflate the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frequency_table_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * called to handle a menu event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // what it do mayne
        switch (item.getItemId()) {
        /* pressed the load menu button */
            case R.id.menu_refresh:
                refreshData();
                break;
            case R.id.menu_reset:
                try {
                    cpuSpyApp.getCpuStateMonitor().setOffsets();
                } catch (CpuStateMonitor.CpuStateMonitorException e) {
                    e.printStackTrace();
                }

                cpuSpyApp.saveOffsets(getActivity());
                updateView();
                break;
            case R.id.menu_restore:
                cpuSpyApp.getCpuStateMonitor().removeOffsets();
                cpuSpyApp.saveOffsets(getActivity());
                updateView();
                break;
        }

        // made it
        return true;
    }

    /**
     * Generate and update all UI elements
     */
    private void updateView() {
        /**
         * Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing)
         */
        CpuStateMonitor monitor = cpuSpyApp.getCpuStateMonitor();
        _uiStatesView.removeAllViews();
        List<String> extraStates = new ArrayList<>();
        for (CpuStateMonitor.CpuState state : monitor.getStates()) {
            if (state.duration > 0) {
                addView(frequencyCard);
                generateStateRow(state, _uiStatesView);
            } else
                extraStates.add(state.freq == 0 ? getString(R.string.deep_sleep) : state.freq / 1000
                        + getString(R.string.mhz));
        }

        // show the red warning label if no states found
        if (monitor.getStates().size() == 0) {
            removeView(runningCard);
            removeView(frequencyCard);
        }

        // update the total state time
        long totTime = monitor.getTotalStateTime() / 100;
        runningCard.setDescription(sToString(totTime));

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            String str = "";

            for (String s : extraStates) {
                if (n++ > 0) str += ", ";
                str += s;
            }

            addView(additionalCard);
            additionalCard.setDescription(str);
        } else removeView(additionalCard);
    }

    /**
     * Attempt to update the time-in-state info
     */
    private void refreshData() {
        if (!_updatingData) new RefreshStateDataTask().execute((Void) null);
    }

    /**
     * @return A nicely formatted String representing tSec seconds
     */
    private static String sToString(long tSec) {
        long h = (long) Math.floor(tSec / (60 * 60));
        long m = (long) Math.floor((tSec - h * 60 * 60) / 60);
        long s = tSec % 60;
        String sDur;
        sDur = h + ":";
        if (m < 10) sDur += "0";
        sDur += m + ":";
        if (s < 10) sDur += "0";
        sDur += s;

        return sDur;
    }

    /**
     * @return a View that corresponds to a CPU freq state row as specified by
     * the state parameter
     */
    private View generateStateRow(CpuStateMonitor.CpuState state, ViewGroup parent) {
        // inflate the XML into a view in the parent
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.state_row, parent, false);

        // what percentage we've got
        CpuStateMonitor monitor = cpuSpyApp.getCpuStateMonitor();
        float per = (float) state.duration * 100 / monitor.getTotalStateTime();
        String sPer = (int) per + "%";

        // state name
        String sFreq = state.freq == 0 ? getString(R.string.deep_sleep) : state.freq / 1000 + "MHz";

        // duration
        long tSec = state.duration / 100;
        String sDur = sToString(tSec);

        // map UI elements to objects
        TextView freqText = (TextView) layout.findViewById(R.id.ui_freq_text);
        TextView durText = (TextView) layout.findViewById(R.id.ui_duration_text);
        TextView perText = (TextView) layout.findViewById(R.id.ui_percentage_text);
        ProgressBar bar = (ProgressBar) layout.findViewById(R.id.ui_bar);

        // modify the row
        freqText.setText(sFreq);
        perText.setText(sPer);
        durText.setText(sDur);
        bar.setProgress((int) per);

        // add it to parent and return
        parent.addView(layout);
        return layout;
    }

    /**
     * Keep updating the state data off the UI thread for slow devices
     */
    private class RefreshStateDataTask extends AsyncTask<Void, Void, Void> {

        /**
         * Stuff to do on a separate thread
         */
        @Override
        protected Void doInBackground(Void... v) {
            CpuStateMonitor monitor = cpuSpyApp.getCpuStateMonitor();
            try {
                monitor.updateStates();
            } catch (CpuStateMonitor.CpuStateMonitorException e) {
                Log.e(TAG, "FrequencyTable: Problem getting CPU states");
            }

            return null;
        }

        /**
         * Executed on the UI thread right before starting the task
         */
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "FrequencyTable: Starting data update");
            _updatingData = true;
        }

        /**
         * Executed on UI thread after task
         */
        @Override
        protected void onPostExecute(Void v) {
            Log.i(TAG, "FrequencyTable: Finished data update");
            _updatingData = false;
            updateView();
        }
    }

}