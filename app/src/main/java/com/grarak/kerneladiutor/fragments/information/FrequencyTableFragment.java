//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
//
//-----------------------------------------------------------------------------
// Modified by Willi Ye to work as Fragment and with with big.LITTLE

package com.grarak.kerneladiutor.fragments.information;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
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
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.DividerCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPU;

import java.util.ArrayList;
import java.util.List;

/**
 * main activity class
 */
public class FrequencyTableFragment extends RecyclerViewFragment implements Constants {

    private SwipeRefreshLayout refreshLayout;

    private CpuSpyApp cpuSpyApp;

    private CardViewItem.DCardView uptimeCard;
    private CardViewItem.DCardView additionalCard;
    private CardViewItem.DCardView frequencyCard;
    private LinearLayout uiStatesView;

    private CpuSpyApp cpuSpyAppLITTLE;

    private CardViewItem.DCardView uptimeCardLITTLE;
    private CardViewItem.DCardView additionalCardLITTLE;
    private CardViewItem.DCardView frequencyCardLITTLE;
    private LinearLayout uiStatesViewLITTLE;

    @Override
    public int getSpan() {
        return Utils.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;
    }

    @Override
    public boolean showApplyOnBoot() {
        return false;
    }

    @Override
    public RecyclerView getRecyclerView() {
        View view = getParentView(R.layout.swiperefresh_fragment);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_primary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RefreshStateDataTask().execute();
            }
        });

        return (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);

        fabView.setVisibility(View.GONE);
        fabView = null;

        backgroundView.setVisibility(View.GONE);
        backgroundView = null;
    }

    /**
     * Initialize the Fragment
     */
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        cpuSpyApp = new CpuSpyApp(CPU.getBigCore());

        if (CPU.isBigLITTLE()) {
            DividerCardView.DDividerCard bigDivider = new DividerCardView.DDividerCard();
            bigDivider.setText(getString(R.string.big));
            bigDivider.toLowerCase();
            addView(bigDivider);
        }

        uptimeCard = new CardViewItem.DCardView();
        uptimeCard.setTitle(getString(R.string.uptime));
        addView(uptimeCard);

        additionalCard = new CardViewItem.DCardView();
        additionalCard.setTitle(getString(R.string.unused_cpu_states));
        addView(additionalCard);

        uiStatesView = new LinearLayout(getActivity());
        uiStatesView.setOrientation(LinearLayout.VERTICAL);
        frequencyCard = new CardViewItem.DCardView();
        frequencyCard.setTitle(getString(R.string.frequency_table));
        frequencyCard.setView(uiStatesView);
        frequencyCard.setFullSpan(true);
        addView(frequencyCard);

        if (CPU.isBigLITTLE()) {
            cpuSpyAppLITTLE = new CpuSpyApp(CPU.getLITTLEcore());

            DividerCardView.DDividerCard LITTLEDivider = new DividerCardView.DDividerCard();
            LITTLEDivider.setText(getString(R.string.little));
            addView(LITTLEDivider);

            uptimeCardLITTLE = new CardViewItem.DCardView();
            uptimeCardLITTLE.setTitle(getString(R.string.uptime));
            addView(uptimeCardLITTLE);

            additionalCardLITTLE = new CardViewItem.DCardView();
            additionalCardLITTLE.setTitle(getString(R.string.unused_cpu_states));
            addView(additionalCardLITTLE);

            uiStatesViewLITTLE = new LinearLayout(getActivity());
            uiStatesViewLITTLE.setOrientation(LinearLayout.VERTICAL);
            frequencyCardLITTLE = new CardViewItem.DCardView();
            frequencyCardLITTLE.setTitle(getString(R.string.frequency_table));
            frequencyCardLITTLE.setView(uiStatesViewLITTLE);
            frequencyCardLITTLE.setFullSpan(true);
            addView(frequencyCardLITTLE);
        }
    }

    @Override
    public void postInit(Bundle savedInstanceState) {
        super.postInit(savedInstanceState);
        new RefreshStateDataTask().execute();
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
        // what it do maybe
        switch (item.getItemId()) {
        /* pressed the load menu button */
            case R.id.menu_reset:
                try {
                    cpuSpyApp.getCpuStateMonitor().setOffsets();
                } catch (CpuStateMonitor.CpuStateMonitorException e) {
                    e.printStackTrace();
                }
                cpuSpyApp.saveOffsets(getActivity());

                if (cpuSpyAppLITTLE != null) {
                    try {
                        cpuSpyAppLITTLE.getCpuStateMonitor().setOffsets();
                    } catch (CpuStateMonitor.CpuStateMonitorException e) {
                        e.printStackTrace();
                    }
                    cpuSpyAppLITTLE.saveOffsets(getActivity());
                }
                break;
            case R.id.menu_restore:
                cpuSpyApp.getCpuStateMonitor().removeOffsets();
                cpuSpyApp.saveOffsets(getActivity());
                if (cpuSpyAppLITTLE != null) {
                    cpuSpyAppLITTLE.getCpuStateMonitor().removeOffsets();
                    cpuSpyAppLITTLE.saveOffsets(getActivity());
                }
                break;
        }
        updateView();

        // made it
        return true;
    }

    private void updateView() {
        updateView(uiStatesView, cpuSpyApp, frequencyCard, uptimeCard, additionalCard);
        if (cpuSpyAppLITTLE != null)
            updateView(uiStatesViewLITTLE, cpuSpyAppLITTLE, frequencyCardLITTLE, uptimeCardLITTLE, additionalCardLITTLE);
    }

    /**
     * Generate and update all UI elements
     */
    private void updateView(LinearLayout uiStatesView, CpuSpyApp cpuSpyApp, CardViewItem.DCardView frequencyCard,
                            CardViewItem.DCardView uptimeCard, CardViewItem.DCardView additionalCard) {
        if (!isAdded()) return;
        /**
         * Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing)
         */
        uiStatesView.removeAllViews();
        List<String> extraStates = new ArrayList<>();
        for (CpuStateMonitor.CpuState state : cpuSpyApp.getCpuStateMonitor().getStates()) {
            if (state.duration > 0) {
                addView(frequencyCard);
                try {
                    generateStateRow(state, uiStatesView, cpuSpyApp.getCpuStateMonitor());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else
                extraStates.add(state.freq == 0 ? getString(R.string.deep_sleep) : state.freq / 1000
                        + getString(R.string.mhz));
        }

        // show the red warning label if no states found
        if (cpuSpyApp.getCpuStateMonitor().getStates().size() == 0) {
            removeView(uptimeCard);
            removeView(frequencyCard);
        }

        // update the total state time
        uptimeCard.setDescription(sToString(cpuSpyApp.getCpuStateMonitor().getTotalStateTime() / 100));

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            StringBuilder stringBuilder = new StringBuilder();

            for (String s : extraStates) {
                if (n++ > 0) stringBuilder.append(",").append(" ");
                stringBuilder.append(s);
            }

            additionalCard.setDescription(stringBuilder.toString());
        } else additionalCard.setDescription("-");
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
     * View that corresponds to a CPU freq state row as specified by
     * the state parameter
     */
    private void generateStateRow(CpuStateMonitor.CpuState state, ViewGroup parent, CpuStateMonitor monitor) {
        // inflate the XML into a view in the parent
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.state_row, parent, false);

        // what percentage we've got
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
        bar.setProgress(Math.round(per));

        // add it to parent and return
        parent.addView(layout);

        if (Utils.isTV(getActivity())) {
            layout.setFocusable(true);
            layout.setFocusableInTouchMode(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                TypedArray ta = getActivity().obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
                Drawable d = ta.getDrawable(0);
                ta.recycle();
                layout.setBackground(d);
            }
        }
    }

    /**
     * Keep updating the state data off the UI thread for slow devices
     */
    private class RefreshStateDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        /**
         * Stuff to do on a separate thread
         */
        @Override
        protected Void doInBackground(Void... v) {
            try {
                cpuSpyApp.getCpuStateMonitor().updateStates();
            } catch (CpuStateMonitor.CpuStateMonitorException e) {
                Log.e(TAG, "FrequencyTable: Problem getting CPU states");
            }
            if (cpuSpyAppLITTLE != null) {
                try {
                    cpuSpyAppLITTLE.getCpuStateMonitor().updateStates();
                } catch (CpuStateMonitor.CpuStateMonitorException e) {
                    Log.e(TAG, "FrequencyTable: Problem getting CPU LITTLE states");
                }
            }
            return null;
        }

        /**
         * Executed on UI thread after task
         */
        @Override
        protected void onPostExecute(Void v) {
            updateView();
            refreshLayout.setRefreshing(false);
        }
    }

}