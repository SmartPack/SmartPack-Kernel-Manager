/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>
 */

package com.smartpack.kernelmanager.utils.kernel.cpu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bvalosek.cpuspy.CpuSpyApp;
import com.bvalosek.cpuspy.CpuStateMonitor;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.views.XYGraph;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.overallstatistics.FrequencyButtonView;
import com.smartpack.kernelmanager.views.recyclerview.overallstatistics.FrequencyTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 24, 2018
 */

public class CPUTimes extends RecyclerViewFragment {

    private static final String TAG = CPUTimes.class.getSimpleName();

    private CPUFreq mCPUFreq;

    private CardView mFreqBig;
    private CardView mFreqLITTLE;
    private CpuSpyApp mCpuSpyBig;
    private CpuSpyApp mCpuSpyLITTLE;

    private FrequencyTask mFrequencyTask;

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance();
        addViewPagerFragment(new CPUTimes.CPUUsageFragment());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        frequenciesInit(items);
    }

    private void frequenciesInit(List<RecyclerViewItem> items) {
        FrequencyButtonView frequencyButtonView = new FrequencyButtonView();
        frequencyButtonView.setRefreshListener(v -> updateFrequency());
        frequencyButtonView.setResetListener(v -> {
            CpuStateMonitor cpuStateMonitor = mCpuSpyBig.getCpuStateMonitor();
            CpuStateMonitor cpuStateMonitorLITTLE = null;
            if (mCpuSpyLITTLE != null) {
                cpuStateMonitorLITTLE = mCpuSpyLITTLE.getCpuStateMonitor();
            }
            try {
                cpuStateMonitor.setOffsets();
                if (cpuStateMonitorLITTLE != null) {
                    cpuStateMonitorLITTLE.setOffsets();
                }
            } catch (CpuStateMonitor.CpuStateMonitorException ignored) {
            }
            mCpuSpyBig.saveOffsets(getActivity());
            if (mCpuSpyLITTLE != null) {
                mCpuSpyLITTLE.saveOffsets(getActivity());
            }
            updateView(cpuStateMonitor, mFreqBig);
            if (cpuStateMonitorLITTLE != null) {
                updateView(cpuStateMonitorLITTLE, mFreqLITTLE);
            }
            adjustScrollPosition();
        });
        frequencyButtonView.setRestoreListener(v -> {
            CpuStateMonitor cpuStateMonitor = mCpuSpyBig.getCpuStateMonitor();
            CpuStateMonitor cpuStateMonitorLITTLE = null;
            if (mCpuSpyLITTLE != null) {
                cpuStateMonitorLITTLE = mCpuSpyLITTLE.getCpuStateMonitor();
            }
            cpuStateMonitor.removeOffsets();
            if (cpuStateMonitorLITTLE != null) {
                cpuStateMonitorLITTLE.removeOffsets();
            }
            mCpuSpyBig.saveOffsets(getActivity());
            if (mCpuSpyLITTLE != null) {
                mCpuSpyLITTLE.saveOffsets(getActivity());
            }
            updateView(cpuStateMonitor, mFreqBig);
            if (mCpuSpyLITTLE != null) {
                updateView(cpuStateMonitorLITTLE, mFreqLITTLE);
            }
            adjustScrollPosition();
        });
        items.add(frequencyButtonView);

        mFreqBig = new CardView(getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            mFreqBig.setTitle(getString(R.string.cluster_big));
        } else {
            mFreqBig.setTitle(getString(R.string.cpu));
        }
        items.add(mFreqBig);

        if (mCPUFreq.isBigLITTLE()) {
            mFreqLITTLE = new CardView(getActivity());
            mFreqLITTLE.setTitle(getString(R.string.cluster_little));
            items.add(mFreqLITTLE);
        }

        mCpuSpyBig = new CpuSpyApp(mCPUFreq.getBigCpu(), getActivity());
        if (mCPUFreq.isBigLITTLE()) {
            mCpuSpyLITTLE = new CpuSpyApp(mCPUFreq.getLITTLECpu(), getActivity());
        }

        updateFrequency();
    }

    private void updateFrequency() {
        if (mFrequencyTask == null) {
            mFrequencyTask = new FrequencyTask();
            mFrequencyTask.execute(this);
        }
    }

    private static class FrequencyTask extends AsyncTask<CPUTimes, Void, CPUTimes> {

        private CpuStateMonitor mBigMonitor;
        private CpuStateMonitor mLITTLEMonitor;

        @Override
        protected CPUTimes doInBackground(CPUTimes... overallFragments) {
            CPUTimes fragment = overallFragments[0];
            mBigMonitor = fragment.mCpuSpyBig.getCpuStateMonitor();
            if (fragment.mCPUFreq.isBigLITTLE()) {
                mLITTLEMonitor = fragment.mCpuSpyLITTLE.getCpuStateMonitor();
            }
            try {
                mBigMonitor.updateStates();
            } catch (CpuStateMonitor.CpuStateMonitorException ignored) {
                Log.e(TAG, "Problem getting CPU states");
            }
            if (fragment.mCPUFreq.isBigLITTLE()) {
                try {
                    mLITTLEMonitor.updateStates();
                } catch (CpuStateMonitor.CpuStateMonitorException ignored) {
                    Log.e(TAG, "Problem getting CPU states");
                }
            }
            return fragment;
        }

        @Override
        protected void onPostExecute(CPUTimes fragment) {
            super.onPostExecute(fragment);
            fragment.updateView(mBigMonitor, fragment.mFreqBig);
            if (fragment.mCPUFreq.isBigLITTLE()) {
                fragment.updateView(mLITTLEMonitor, fragment.mFreqLITTLE);
            }
            fragment.adjustScrollPosition();
            fragment.mFrequencyTask = null;
        }
    }

    private void updateView(CpuStateMonitor monitor, CardView card) {
        if (!isAdded() || card == null) return;
        card.clearItems();

        // update the total state time
        DescriptionView totalTime = new DescriptionView();
        totalTime.setTitle(getString(R.string.uptime));
        totalTime.setSummary(sToString(monitor.getTotalStateTime() / 100L));
        card.addItem(totalTime);

        /* Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing) */
        List<String> extraStates = new ArrayList<>();
        for (CpuStateMonitor.CpuState state : monitor.getStates()) {
            if (state.getDuration() > 0) {
                generateStateRow(monitor, state, card);
            } else {
                if (state.getFreq() == 0) {
                    extraStates.add(getString(R.string.deep_sleep));
                } else {
                    extraStates.add(state.getFreq() / 1000 + getString(R.string.mhz));
                }
            }
        }

        if (monitor.getStates().size() == 0) {
            card.clearItems();
            DescriptionView errorView = new DescriptionView();
            errorView.setTitle(getString(R.string.error_frequencies));
            card.addItem(errorView);
            return;
        }

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            StringBuilder str = new StringBuilder();

            for (String s : extraStates) {
                if (n++ > 0)
                    str.append(", ");
                str.append(s);
            }

            DescriptionView unusedText = new DescriptionView();
            unusedText.setTitle(getString(R.string.unused_frequencies));
            unusedText.setSummary(str.toString());
            card.addItem(unusedText);
        }
    }

    /**
     * @return A nicely formatted String representing tSec seconds
     */
    private String sToString(long tSec) {
        int h = (int) tSec / 60 / 60;
        int m = (int) tSec / 60 % 60;
        int s = (int) tSec % 60;
        String sDur;
        sDur = h + ":";
        if (m < 10) sDur += "0";
        sDur += m + ":";
        if (s < 10) sDur += "0";
        sDur += s;

        return sDur;
    }

    /**
     * Creates a View that correpsonds to a CPU freq state row as specified
     * by the state parameter
     */
    private void generateStateRow(CpuStateMonitor monitor, CpuStateMonitor.CpuState state,
                                  CardView frequencyCard) {
        // what percentage we've got
        float per = (float) state.getDuration() * 100 / monitor.getTotalStateTime();

        String sFreq;
        if (state.getFreq() == 0) {
            sFreq = getString(R.string.deep_sleep);
        } else {
            sFreq = state.getFreq() / 1000 + getString(R.string.mhz);
        }

        // duration
        long tSec = state.getDuration() / 100;
        String sDur = sToString(tSec);

        FrequencyTableView frequencyState = new FrequencyTableView();
        frequencyState.setFrequency(sFreq);
        frequencyState.setPercentage((int) per);
        frequencyState.setDuration(sDur);

        frequencyCard.addItem(frequencyState);
    }

    @Override
    protected void refreshThread() {
        super.refreshThread();
    }

    public static class CPUUsageFragment extends BaseFragment {

        private Handler mHandler;

        private List<View> mUsages;
        private Thread mThread;
        private float[] mCPUUsages;
        private int[] mFreqs;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            mHandler = new Handler();
            mUsages = new ArrayList<>();
            LinearLayout rootView = new LinearLayout(getActivity());
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setGravity(Gravity.CENTER);
            rootView.setOrientation(LinearLayout.VERTICAL);

            LinearLayout subView = null;
            for (int i = 0; i < CPUFreq.getInstance(getActivity()).getCpuCount(); i++) {
                if (subView == null || i % 2 == 0) {
                    subView = new LinearLayout(getActivity());
                    subView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    rootView.addView(subView);
                }

                View view = inflater.inflate(R.layout.fragment_usage_view, subView, false);
                view.setLayoutParams(new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
                ((TextView) view.findViewById(R.id.usage_core_text)).setText(getString(R.string.core, i + 1));
                mUsages.add(view);
                subView.addView(view);
            }
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mHandler.post(mRefresh);
        }

        @Override
        public void onPause() {
            super.onPause();
            mHandler.removeCallbacks(mRefresh);
            if (mThread != null) {
                mThread.interrupt();
                mThread = null;
            }
        }

        private Runnable mRefresh = new Runnable() {
            @Override
            public void run() {
                refresh();
                mHandler.postDelayed(this, 1000);
            }
        };

        public void refresh() {
            if (mThread == null) {
                mThread = new Thread(() -> {
                    while (mThread != null) {
                        try {
                            CPUFreq cpuFreq = CPUFreq.getInstance(getActivity());
                            mCPUUsages = cpuFreq.getCpuUsage();
                            if (mFreqs == null) {
                                mFreqs = new int[cpuFreq.getCpuCount()];
                            }
                            for (int i = 0; i < mFreqs.length; i++) {
                                if (getActivity() == null) break;
                                mFreqs[i] = cpuFreq.getCurFreq(i);
                            }

                            if (getActivity() == null) {
                                mThread = null;
                            }
                        } catch (InterruptedException ignored) {
                            mThread = null;
                        }
                    }
                });
                mThread.start();
            }

            if (mFreqs == null || mCPUUsages == null || mUsages == null) return;
            for (int i = 0; i < mUsages.size(); i++) {
                View usageView = mUsages.get(i);
                TextView usageOfflineText = usageView.findViewById(R.id.usage_offline_text);
                TextView usageLoadText = usageView.findViewById(R.id.usage_load_text);
                TextView usageFreqText = usageView.findViewById(R.id.usage_freq_text);
                XYGraph usageGraph = usageView.findViewById(R.id.usage_graph);
                if (mFreqs[i] == 0) {
                    usageOfflineText.setVisibility(View.VISIBLE);
                    usageLoadText.setVisibility(View.GONE);
                    usageFreqText.setVisibility(View.GONE);
                    usageGraph.addPercentage(0);
                } else {
                    usageOfflineText.setVisibility(View.GONE);
                    usageLoadText.setVisibility(View.VISIBLE);
                    usageFreqText.setVisibility(View.VISIBLE);
                    usageFreqText.setText(Utils.strFormat("%d" + getString(R.string.mhz), mFreqs[i] / 1000));
                    usageLoadText.setText(Utils.strFormat("%d%%", Math.round(mCPUUsages[i + 1])));
                    usageGraph.addPercentage(Math.round(mCPUUsages[i + 1]));
                }
            }
        }

    }

}