/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.fragments.kernel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grarak.kerneladiutor.PathReaderActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.elements.DividerCardView;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.elements.UsageCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.kernel.CPU;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 22.12.14.
 */
public class CPUFragment extends RecyclerViewFragment implements Constants, View.OnClickListener,
        PopupCardItem.DPopupCard.OnDPopupCardListener, CardViewItem.DCardView.OnDCardListener,
        SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener,
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener {

    private UsageCardView.DUsageCard mUsageCard;
    private CheckBox[] mCoreCheckBox;
    private ProgressBar[] mCoreProgressBar;
    private TextView[] mCoreFreqText;

    private PopupCardItem.DPopupCard mMaxFreqCard, mMinFreqCard, mMaxScreenOffFreqCard;

    private PopupCardItem.DPopupCard mGovernorCard;
    private CardViewItem.DCardView mGovernorTunableCard;

    private PopupCardItem.DPopupCard mMcPowerSavingCard;

    private PopupCardItem.DPopupCard mCFSSchedulerCard;

    private SeekBarCardView.DSeekBarCardView mTempLimitCard;

    private SwitchCompatCardItem.DSwitchCompatCard mCpuBoostEnableCard;
    private SwitchCompatCardItem.DSwitchCompatCard mCpuBoostDebugMaskCard;
    private SeekBarCardView.DSeekBarCardView mCpuBoostMsCard;
    private PopupCardItem.DPopupCard mCpuBoostSyncThresholdCard;
    private SeekBarCardView.DSeekBarCardView mCpuBoostInputMsCard;
    private PopupCardItem.DPopupCard mCpuBoostInputFreqCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        usageInit();
        if (CPU.getFreqs() != null) {
            coreInit();
            freqInit();
        }
        governorInit();
        if (CPU.hasMcPowerSaving()) mcPowerSavingInit();
        if (CPU.hasCFSScheduler()) cfsSchedulerInit();
        if (CPU.hasTempLimit()) tempLimitInit();
        if (CPU.hasCpuBoost()) cpuBoostInit();
    }

    private void usageInit() {
        mUsageCard = new UsageCardView.DUsageCard();
        mUsageCard.setText(getString(R.string.cpu_usage));
        addView(mUsageCard);

        getHandler().post(cpuUsage);
    }

    private void coreInit() {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        mCoreCheckBox = new CheckBox[CPU.getCoreCount()];
        mCoreProgressBar = new ProgressBar[mCoreCheckBox.length];
        mCoreFreqText = new TextView[mCoreCheckBox.length];
        for (int i = 0; i < mCoreCheckBox.length; i++) {
            View view = inflater.inflate(R.layout.coreview, container, false);

            mCoreCheckBox[i] = (CheckBox) view.findViewById(R.id.checkbox);
            mCoreCheckBox[i].setText(getString(R.string.core, i + 1));
            mCoreCheckBox[i].setOnClickListener(this);

            mCoreProgressBar[i] = (ProgressBar) view.findViewById(R.id.progressbar);
            mCoreProgressBar[i].setMax(CPU.getFreqs().size());

            mCoreFreqText[i] = (TextView) view.findViewById(R.id.freq);

            layout.addView(view);
        }

        CardViewItem.DCardView coreCard = new CardViewItem.DCardView();
        coreCard.setTitle(getString(R.string.current_freq));
        coreCard.setView(layout);

        addView(coreCard);
    }

    private void freqInit() {
        List<String> freqs = new ArrayList<>();
        for (int freq : CPU.getFreqs()) freqs.add(freq / 1000 + getString(R.string.mhz));

        String MHZ = getString(R.string.mhz);

        mMaxFreqCard = new PopupCardItem.DPopupCard(freqs);
        mMaxFreqCard.setTitle(getString(R.string.cpu_max_freq));
        mMaxFreqCard.setDescription(getString(R.string.cpu_max_freq_summary));
        mMaxFreqCard.setItem(CPU.getMaxFreq(0) / 1000 + MHZ);
        mMaxFreqCard.setOnDPopupCardListener(this);

        mMinFreqCard = new PopupCardItem.DPopupCard(freqs);
        mMinFreqCard.setTitle(getString(R.string.cpu_min_freq));
        mMinFreqCard.setDescription(getString(R.string.cpu_min_freq_summary));
        mMinFreqCard.setItem(CPU.getMinFreq(0) / 1000 + MHZ);
        mMinFreqCard.setOnDPopupCardListener(this);

        addView(mMaxFreqCard);
        addView(mMinFreqCard);

        if (CPU.hasMaxScreenOffFreq()) {
            mMaxScreenOffFreqCard = new PopupCardItem.DPopupCard(freqs);
            mMaxScreenOffFreqCard.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mMaxScreenOffFreqCard.setDescription(getString(R.string.cpu_max_screen_off_freq_summary));
            mMaxScreenOffFreqCard.setItem(CPU.getMaxScreenOffFreq(0) / 1000 + MHZ);
            mMaxScreenOffFreqCard.setOnDPopupCardListener(this);

            addView(mMaxScreenOffFreqCard);
        }
    }

    private void governorInit() {
        mGovernorCard = new PopupCardItem.DPopupCard(CPU.getAvailableGovernors());
        mGovernorCard.setTitle(getString(R.string.cpu_governor));
        mGovernorCard.setDescription(getString(R.string.cpu_governor_summary));
        mGovernorCard.setItem(CPU.getCurGovernor(0));
        mGovernorCard.setOnDPopupCardListener(this);

        mGovernorTunableCard = new CardViewItem.DCardView();
        mGovernorTunableCard.setTitle(getString(R.string.cpu_governor_tunables));
        mGovernorTunableCard.setDescription(getString(R.string.cpu_governor_tunables_summary));
        mGovernorTunableCard.setOnDCardListener(this);

        addView(mGovernorCard);
        addView(mGovernorTunableCard);
    }

    private void mcPowerSavingInit() {
        mMcPowerSavingCard = new PopupCardItem.DPopupCard(new ArrayList<>(Arrays.asList(
                CPU.getMcPowerSavingItems(getActivity()))));
        mMcPowerSavingCard.setTitle(getString(R.string.mc_power_saving));
        mMcPowerSavingCard.setDescription(getString(R.string.mc_power_saving_summary));
        mMcPowerSavingCard.setItem(CPU.getCurMcPowerSaving());
        mMcPowerSavingCard.setOnDPopupCardListener(this);

        addView(mMcPowerSavingCard);
    }

    private void cfsSchedulerInit() {
        mCFSSchedulerCard = new PopupCardItem.DPopupCard(CPU.getAvailableCFSSchedulers());
        mCFSSchedulerCard.setTitle(getString(R.string.cfs_scheduler_policy));
        mCFSSchedulerCard.setDescription(getString(R.string.cfs_scheduler_policy_summary));
        mCFSSchedulerCard.setItem(CPU.getCurrentCFSScheduler());
        mCFSSchedulerCard.setOnDPopupCardListener(this);

        addView(mCFSSchedulerCard);
    }

    private void tempLimitInit() {
        mTempLimitCard = new SeekBarCardView.DSeekBarCardView(CPU.getTempLimitList());
        mTempLimitCard.setTitle(getString(R.string.temp_limit));
        mTempLimitCard.setDescription(getString(R.string.temp_limit_summary));
        mTempLimitCard.setProgress(CPU.getCurTempLimit() - CPU.getTempLimitMin());
        mTempLimitCard.setOnDSeekBarCardListener(this);

        addView(mTempLimitCard);
    }

    private void cpuBoostInit() {
        List<DAdapter.DView> views = new ArrayList<>();
        if (CPU.hasCpuBoostEnable()) {
            mCpuBoostEnableCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mCpuBoostEnableCard.setDescription(getString(R.string.cpu_boost));
            mCpuBoostEnableCard.setChecked(CPU.isCpuBoostActive());
            mCpuBoostEnableCard.setOnDSwitchCompatCardListener(this);

            views.add(mCpuBoostEnableCard);
        }

        if (CPU.hasCpuBoostDebugMask()) {
            mCpuBoostDebugMaskCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mCpuBoostDebugMaskCard.setTitle(getString(R.string.debug_mask));
            mCpuBoostDebugMaskCard.setDescription(getString(R.string.debug_mask_summary));
            mCpuBoostDebugMaskCard.setChecked(CPU.isCpuBoostDebugMaskActive());
            mCpuBoostDebugMaskCard.setOnDSwitchCompatCardListener(this);

            views.add(mCpuBoostDebugMaskCard);
        }

        if (CPU.hasCpuBoostMs()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 5001; i += 10)
                list.add(i + getString(R.string.ms));

            mCpuBoostMsCard = new SeekBarCardView.DSeekBarCardView(list);
            mCpuBoostMsCard.setTitle(getString(R.string.interval));
            mCpuBoostMsCard.setDescription(getString(R.string.interval_summary));
            mCpuBoostMsCard.setProgress(CPU.getCpuBootMs() / 10);
            mCpuBoostMsCard.setOnDSeekBarCardListener(this);

            views.add(mCpuBoostMsCard);
        }

        if (CPU.hasCpuBoostSyncThreshold()) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mCpuBoostSyncThresholdCard = new PopupCardItem.DPopupCard(list);
            mCpuBoostSyncThresholdCard.setTitle(getString(R.string.sync_threshold));
            mCpuBoostSyncThresholdCard.setDescription(getString(R.string.sync_threshold_summary));
            mCpuBoostSyncThresholdCard.setItem(CPU.getCpuBootSyncThreshold());
            mCpuBoostSyncThresholdCard.setOnDPopupCardListener(this);

            views.add(mCpuBoostSyncThresholdCard);
        }

        if (CPU.hasCpuBoostInputMs()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 5001; i += 10)
                list.add(i + getString(R.string.ms));

            mCpuBoostInputMsCard = new SeekBarCardView.DSeekBarCardView(list);
            mCpuBoostInputMsCard.setTitle(getString(R.string.input_interval));
            mCpuBoostInputMsCard.setDescription(getString(R.string.input_interval_summary));
            mCpuBoostInputMsCard.setProgress(CPU.getCpuBootInputMs() / 10);
            mCpuBoostInputMsCard.setOnDSeekBarCardListener(this);

            views.add(mCpuBoostInputMsCard);
        }

        if (CPU.hasCpuBoostInputFreq()) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mCpuBoostInputFreqCard = new PopupCardItem.DPopupCard(list);
            mCpuBoostInputFreqCard.setTitle(getString(R.string.input_boost_freq));
            mCpuBoostInputFreqCard.setDescription(getString(R.string.input_boost_freq_summary));
            mCpuBoostInputFreqCard.setItem(CPU.getCpuBootInputFreq());
            mCpuBoostInputFreqCard.setOnDPopupCardListener(this);

            views.add(mCpuBoostInputFreqCard);
        }

        if (views.size() > 0) {
            DividerCardView.DDividerCard mCpuBoostDividerCard = new DividerCardView.DDividerCard();
            mCpuBoostDividerCard.setText(getString(R.string.cpu_boost));

            addView(mCpuBoostDividerCard);
            addAllViews(views);
        }

    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mCoreCheckBox.length; i++)
            if (v == mCoreCheckBox[i]) {
                if (i == 0) return;
                CPU.activateCore(i, ((CheckBox) v).isChecked(), getActivity());
            }
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mMaxFreqCard)
            CPU.setMaxFreq(CPU.getFreqs().get(position), getActivity());
        else if (dPopupCard == mMinFreqCard)
            CPU.setMinFreq(CPU.getFreqs().get(position), getActivity());
        else if (dPopupCard == mMaxScreenOffFreqCard)
            CPU.setMaxScreenOffFreq(CPU.getFreqs().get(position), getActivity());
        else if (dPopupCard == mGovernorCard)
            CPU.setGovernor(CPU.getAvailableGovernors().get(position), getActivity());
        else if (dPopupCard == mMcPowerSavingCard) CPU.setMcPowerSaving(position, getActivity());
        else if (dPopupCard == mCFSSchedulerCard)
            CPU.setCFSScheduler(CPU.getAvailableCFSSchedulers().get(position), getActivity());
        else if (dPopupCard == mCpuBoostSyncThresholdCard)
            CPU.setCpuBoostSyncThreshold(position == 0 ? 0 : CPU.getFreqs().get(position - 1), getActivity());
        else if (dPopupCard == mCpuBoostInputFreqCard)
            CPU.setCpuBoostInputFreq(position == 0 ? 0 : CPU.getFreqs().get(position - 1), getActivity());
    }

    @Override
    public void onClick(CardViewItem.DCardView dCardView) {
        if (dCardView == mGovernorTunableCard) {
            String governor = CPU.getCurGovernor(0);
            Intent i = new Intent(getActivity(), PathReaderActivity.class);
            Bundle args = new Bundle();
            args.putInt(PathReaderActivity.ARG_TYPE, PathReaderActivity.PATH_TYPE.GOVERNOR.ordinal());
            args.putString(PathReaderActivity.ARG_TITLE, governor);
            args.putString(PathReaderActivity.ARG_PATH, CPU_GOVERNOR_TUNABLES + "/" + governor);
            args.putString(PathReaderActivity.ARG_ERROR, getString(R.string.not_tunable, governor));
            i.putExtras(args);

            startActivity(i);
        }
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mTempLimitCard)
            CPU.setTempLimit(position + CPU.getTempLimitMin(), getActivity());
        else if (dSeekBarCardView == mCpuBoostMsCard)
            CPU.setCpuBoostMs(position * 10, getActivity());
        else if (dSeekBarCardView == mCpuBoostInputMsCard)
            CPU.setCpuBoostInputMs(position * 10, getActivity());
    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mCpuBoostEnableCard)
            CPU.activateCpuBoost(checked, getActivity());
        else if (dSwitchCompatCard == mCpuBoostDebugMaskCard)
            CPU.activateCpuBoostDebugMask(checked, getActivity());
    }

    @Override
    public boolean onRefresh() {
        String MHZ = getString(R.string.mhz);

        if (mCoreCheckBox != null)
            for (int i = 0; i < mCoreCheckBox.length; i++) {
                int cur = CPU.getCurFreq(i);
                if (mCoreCheckBox[i] != null) mCoreCheckBox[i].setChecked(cur != 0);
                if (mCoreProgressBar[i] != null)
                    mCoreProgressBar[i].setProgress(CPU.getFreqs().indexOf(cur) + 1);
                if (mCoreFreqText[i] != null)
                    mCoreFreqText[i].setText(cur == 0 ? getString(R.string.offline) : cur / 1000 + MHZ);
            }

        if (mMaxFreqCard != null) mMaxFreqCard.setItem(CPU.getMaxFreq(0) / 1000 + MHZ);
        if (mMinFreqCard != null) mMinFreqCard.setItem(CPU.getMinFreq(0) / 1000 + MHZ);
        if (mGovernorCard != null) mGovernorCard.setItem(CPU.getCurGovernor(0));

        return true;
    }

    private final Runnable cpuUsage = new Runnable() {
        @Override
        public void run() {
            if (mUsageCard != null)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final float usage = CPU.getCpuUsage();
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mUsageCard.setProgress(Math.round(usage));
                                }
                            });
                        } catch (NullPointerException ignored) {
                        }
                    }
                }).start();

            getHandler().postDelayed(cpuUsage, 3000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getHandler().removeCallbacks(cpuUsage);
    }

}
