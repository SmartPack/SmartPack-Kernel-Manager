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

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.elements.DividerCardView;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.kernel.CPUHotplug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.02.15.
 */
public class CPUHotplugFragment extends RecyclerViewFragment implements
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener,
        PopupCardItem.DPopupCard.OnDPopupCardListener, SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener {

    private SwitchCompatCardItem.DSwitchCompatCard mMpdecisionCard;

    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugCard;
    private PopupCardItem.DPopupCard mIntelliPlugProfileCard;
    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugEcoCard;
    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugTouchBoostCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugHysteresisCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugThresholdCard;
    private PopupCardItem.DPopupCard mIntelliPlugScreenOffMaxCard;
    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugDebugCard;
    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugSuspendCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugCpusBoostedCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugMinCpusOnlineCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugMaxCpusOnlineCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugMaxCpusOnlineSuspCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugSuspendDeferTimeCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugDeferSamplingCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugBoostLockDurationCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugDownLockDurationCard;
    private SeekBarCardView.DSeekBarCardView mIntelliPlugFShiftCard;

    private SwitchCompatCardItem.DSwitchCompatCard mBluPlugCard;
    private SwitchCompatCardItem.DSwitchCompatCard mBluPlugPowersaverModeCard;
    private SeekBarCardView.DSeekBarCardView mBluPlugMinOnlineCard;
    private SeekBarCardView.DSeekBarCardView mBluPlugMaxOnlineCard;
    private SeekBarCardView.DSeekBarCardView mBluPlugMaxCoresScreenOffCard;
    private PopupCardItem.DPopupCard mBluPlugMaxFreqScreenOffCard;
    private SeekBarCardView.DSeekBarCardView mBluPlugUpThresholdCard;
    private SeekBarCardView.DSeekBarCardView mBluPlugUpTimerCntCard;
    private SeekBarCardView.DSeekBarCardView mBluPlugDownTimerCntCard;

    private SwitchCompatCardItem.DSwitchCompatCard mMsmHotplugEnabledCard;
    private SwitchCompatCardItem.DSwitchCompatCard mMsmHotplugDebugMaskCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugMinCpusOnlineCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugMaxCpusOnlineCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugCpusBoostedCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugCpusOnlineSuspCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugBoostLockDurationCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugDownLockDurationCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugHistorySizeCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugUpdateRateCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugFastLaneLoadCard;
    private PopupCardItem.DPopupCard mMsmHotplugFastLaneMinFreqCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugOfflineLoadCard;
    private SwitchCompatCardItem.DSwitchCompatCard mMsmHotplugIoIsBusyCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugSuspendMaxCpusCard;
    private PopupCardItem.DPopupCard mMsmHotplugSuspendFreqCard;
    private SeekBarCardView.DSeekBarCardView mMsmHotplugSuspendDeferTimeCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (CPUHotplug.hasMpdecision()) mpdecisionInit();
        if (CPUHotplug.hasIntelliPlug()) intelliPlugInit();
        if (CPUHotplug.hasBluPlug()) bluPlugInit();
        if (CPUHotplug.hasMsmHotplug()) msmHotplugInit();
    }

    private void mpdecisionInit() {
        mMpdecisionCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mMpdecisionCard.setTitle(getString(R.string.mpdecision));
        mMpdecisionCard.setDescription(getString(R.string.mpdecision_summary));
        mMpdecisionCard.setChecked(CPUHotplug.isMpdecisionActive());
        mMpdecisionCard.setOnDSwitchCompatCardListener(this);

        addView(mMpdecisionCard);
    }

    private void intelliPlugInit() {
        List<DAdapter.DView> views = new ArrayList<>();

        if (CPUHotplug.hasIntelliPlugEnable()) {
            mIntelliPlugCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugCard.setTitle(getString(R.string.intelliplug));
            mIntelliPlugCard.setDescription(getString(R.string.intelliplug_summary));
            mIntelliPlugCard.setChecked(CPUHotplug.isIntelliPlugActive());
            mIntelliPlugCard.setOnDSwitchCompatCardListener(this);

            views.add(mIntelliPlugCard);
        }

        if (CPUHotplug.hasIntelliPlugProfile()) {
            mIntelliPlugProfileCard = new PopupCardItem.DPopupCard(CPUHotplug.getIntelliPlugProfileMenu(getActivity()));
            mIntelliPlugProfileCard.setTitle(getString(R.string.profile));
            mIntelliPlugProfileCard.setDescription(getString(R.string.profile_summary));
            mIntelliPlugProfileCard.setItem(CPUHotplug.getIntelliPlugProfile());
            mIntelliPlugProfileCard.setOnDPopupCardListener(this);

            views.add(mIntelliPlugProfileCard);
        }

        if (CPUHotplug.hasIntelliPlugEco()) {
            mIntelliPlugEcoCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugEcoCard.setTitle(getString(R.string.eco_mode_summary));
            mIntelliPlugEcoCard.setDescription(getString(R.string.eco_mode_summary));
            mIntelliPlugEcoCard.setChecked(CPUHotplug.isIntelliPlugEcoActive());
            mIntelliPlugEcoCard.setOnDSwitchCompatCardListener(this);

            views.add(mIntelliPlugEcoCard);
        }

        if (CPUHotplug.hasIntelliPlugTouchBoost()) {
            mIntelliPlugTouchBoostCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugTouchBoostCard.setTitle(getString(R.string.touch_boost));
            mIntelliPlugTouchBoostCard.setDescription(getString(R.string.touch_boost_summary));
            mIntelliPlugTouchBoostCard.setChecked(CPUHotplug.isIntelliPlugTouchBoostActive());
            mIntelliPlugTouchBoostCard.setOnDSwitchCompatCardListener(this);

            views.add(mIntelliPlugTouchBoostCard);
        }

        if (CPUHotplug.hasIntelliPlugHysteresis()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 17; i++)
                list.add(String.valueOf(i));

            mIntelliPlugHysteresisCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugHysteresisCard.setTitle(getString(R.string.hysteresis));
            mIntelliPlugHysteresisCard.setDescription(getString(R.string.hysteresis_summary));
            mIntelliPlugHysteresisCard.setProgress(CPUHotplug.getIntelliPlugHysteresis());
            mIntelliPlugHysteresisCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugHysteresisCard);
        }

        if (CPUHotplug.hasIntelliPlugThresold()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 1001; i++)
                list.add(String.valueOf(i));

            mIntelliPlugThresholdCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugThresholdCard.setTitle(getString(R.string.threshold));
            mIntelliPlugThresholdCard.setProgress(CPUHotplug.getIntelliPlugThresold());
            mIntelliPlugThresholdCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugThresholdCard);
        }

        if (CPUHotplug.hasIntelliPlugScreenOffMax() && CPU.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mIntelliPlugScreenOffMaxCard = new PopupCardItem.DPopupCard(list);
            mIntelliPlugScreenOffMaxCard.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mIntelliPlugScreenOffMaxCard.setDescription(getString(R.string.cpu_max_screen_off_freq_summary));
            mIntelliPlugScreenOffMaxCard.setItem(CPUHotplug.getIntelliPlugScreenOffMax());
            mIntelliPlugScreenOffMaxCard.setOnDPopupCardListener(this);

            views.add(mIntelliPlugScreenOffMaxCard);
        }

        if (CPUHotplug.hasIntelliPlugDebug()) {
            mIntelliPlugDebugCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugDebugCard.setTitle(getString(R.string.debug));
            mIntelliPlugDebugCard.setDescription(getString(R.string.debug_summary));
            mIntelliPlugDebugCard.setChecked(CPUHotplug.isIntelliPlugDebugActive());
            mIntelliPlugDebugCard.setOnDSwitchCompatCardListener(this);

            views.add(mIntelliPlugDebugCard);
        }

        if (CPUHotplug.hasIntelliPlugSuspend()) {
            mIntelliPlugSuspendCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugSuspendCard.setTitle(getString(R.string.suspend));
            mIntelliPlugSuspendCard.setDescription(getString(R.string.suspend_summary));
            mIntelliPlugSuspendCard.setChecked(CPUHotplug.isIntelliPlugSuspendActive());
            mIntelliPlugSuspendCard.setOnDSwitchCompatCardListener(this);

            views.add(mIntelliPlugSuspendCard);
        }

        if (CPUHotplug.hasIntelliPlugCpusBoosted()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mIntelliPlugCpusBoostedCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugCpusBoostedCard.setTitle(getString(R.string.cpus_boosted));
            mIntelliPlugCpusBoostedCard.setDescription(getString(R.string.cpus_boosted_summary));
            mIntelliPlugCpusBoostedCard.setProgress(CPUHotplug.getIntelliPlugCpusBoosted() - 1);
            mIntelliPlugCpusBoostedCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugCpusBoostedCard);
        }

        if (CPUHotplug.hasIntelliPlugMinCpusOnline()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mIntelliPlugMinCpusOnlineCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugMinCpusOnlineCard.setTitle(getString(R.string.min_cpus_online));
            mIntelliPlugMinCpusOnlineCard.setDescription(getString(R.string.min_cpus_online_summary));
            mIntelliPlugMinCpusOnlineCard.setProgress(CPUHotplug.getIntelliPlugMinCpusOnline() - 1);
            mIntelliPlugMinCpusOnlineCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugMinCpusOnlineCard);
        }

        if (CPUHotplug.hasIntelliPlugMaxCpusOnline()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mIntelliPlugMaxCpusOnlineCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugMaxCpusOnlineCard.setTitle(getString(R.string.max_cpus_online));
            mIntelliPlugMaxCpusOnlineCard.setDescription(getString(R.string.max_cpus_online_summary));
            mIntelliPlugMaxCpusOnlineCard.setProgress(CPUHotplug.getIntelliPlugMaxCpusOnline() - 1);
            mIntelliPlugMaxCpusOnlineCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugMaxCpusOnlineCard);
        }

        if (CPUHotplug.hasIntelliPlugMaxCpusOnlineSusp()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mIntelliPlugMaxCpusOnlineSuspCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugMaxCpusOnlineSuspCard.setTitle(getString(R.string.max_cpus_online_susp));
            mIntelliPlugMaxCpusOnlineSuspCard.setDescription(getString(R.string.max_cpus_online_susp_summary));
            mIntelliPlugMaxCpusOnlineSuspCard.setProgress(CPUHotplug.getIntelliPlugMaxCpusOnlineSusp() - 1);
            mIntelliPlugMaxCpusOnlineSuspCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugMaxCpusOnlineSuspCard);
        }

        if (CPUHotplug.hasIntelliPlugSuspendDeferTime()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 501; i++)
                list.add((i * 10) + getString(R.string.ms));

            mIntelliPlugSuspendDeferTimeCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugSuspendDeferTimeCard.setTitle(getString(R.string.suspend_defer_time));
            mIntelliPlugSuspendDeferTimeCard.setProgress(list.indexOf(String.valueOf(
                    CPUHotplug.getIntelliPlugSuspendDeferTime())));
            mIntelliPlugSuspendDeferTimeCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugSuspendDeferTimeCard);
        }

        if (CPUHotplug.hasIntelliPlugDeferSampling()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 1001; i++)
                list.add(i + getString(R.string.ms));

            mIntelliPlugDeferSamplingCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugDeferSamplingCard.setTitle(getString(R.string.defer_sampling));
            mIntelliPlugDeferSamplingCard.setProgress(CPUHotplug.getIntelliPlugDeferSampling());
            mIntelliPlugDeferSamplingCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugDeferSamplingCard);
        }

        if (CPUHotplug.hasIntelliPlugBoostLockDuration()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 5001; i++)
                list.add(i + getString(R.string.ms));

            mIntelliPlugBoostLockDurationCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugBoostLockDurationCard.setTitle(getString(R.string.boost_lock_duration));
            mIntelliPlugBoostLockDurationCard.setDescription(getString(R.string.boost_lock_duration_summary));
            mIntelliPlugBoostLockDurationCard.setProgress(CPUHotplug.getIntelliPlugBoostLockDuration() - 1);
            mIntelliPlugBoostLockDurationCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugBoostLockDurationCard);
        }

        if (CPUHotplug.hasIntelliPlugDownLockDuration()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 5001; i++)
                list.add(i + getString(R.string.ms));

            mIntelliPlugDownLockDurationCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugDownLockDurationCard.setTitle(getString(R.string.down_lock_duration));
            mIntelliPlugDownLockDurationCard.setDescription(getString(R.string.down_lock_duration_summary));
            mIntelliPlugDownLockDurationCard.setProgress(CPUHotplug.getIntelliPlugDownLockDuration() - 1);
            mIntelliPlugDownLockDurationCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugDownLockDurationCard);
        }

        if (CPUHotplug.hasIntelliPlugFShift()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 4; i++)
                list.add(String.valueOf(i));

            mIntelliPlugFShiftCard = new SeekBarCardView.DSeekBarCardView(list);
            mIntelliPlugFShiftCard.setTitle(getString(R.string.fshift));
            mIntelliPlugFShiftCard.setProgress(CPUHotplug.getIntelliPlugFShift());
            mIntelliPlugFShiftCard.setOnDSeekBarCardListener(this);

            views.add(mIntelliPlugFShiftCard);
        }

        if (views.size() > 0) {
            DividerCardView.DDividerCard mIntelliPlugDividerCard = new DividerCardView.DDividerCard();
            mIntelliPlugDividerCard.setText(getString(R.string.intelliplug));
            addView(mIntelliPlugDividerCard);

            addAllView(views);
        }

    }

    private void bluPlugInit() {
        List<DAdapter.DView> views = new ArrayList<>();

        if (CPUHotplug.hasBluPlugEnable()) {
            mBluPlugCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mBluPlugCard.setTitle(getString(R.string.blu_plug));
            mBluPlugCard.setDescription(getString(R.string.blu_plug_summary));
            mBluPlugCard.setChecked(CPUHotplug.isBluPlugActive());
            mBluPlugCard.setOnDSwitchCompatCardListener(this);

            views.add(mBluPlugCard);
        }

        if (CPUHotplug.hasBluPlugPowersaverMode()) {
            mBluPlugPowersaverModeCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mBluPlugPowersaverModeCard.setTitle(getString(R.string.powersaver_mode));
            mBluPlugPowersaverModeCard.setDescription(getString(R.string.powersaver_mode_summary));
            mBluPlugPowersaverModeCard.setChecked(CPUHotplug.isBluPlugPowersaverModeActive());
            mBluPlugPowersaverModeCard.setOnDSwitchCompatCardListener(this);

            views.add(mBluPlugPowersaverModeCard);
        }

        if (CPUHotplug.hasBluPlugMinOnline()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mBluPlugMinOnlineCard = new SeekBarCardView.DSeekBarCardView(list);
            mBluPlugMinOnlineCard.setTitle(getString(R.string.min_online));
            mBluPlugMinOnlineCard.setDescription(getString(R.string.min_online_summary));
            mBluPlugMinOnlineCard.setProgress(CPUHotplug.getBluPlugMinOnline() - 1);
            mBluPlugMinOnlineCard.setOnDSeekBarCardListener(this);

            views.add(mBluPlugMinOnlineCard);
        }

        if (CPUHotplug.hasBluPlugMaxOnline()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mBluPlugMaxOnlineCard = new SeekBarCardView.DSeekBarCardView(list);
            mBluPlugMaxOnlineCard.setTitle(getString(R.string.max_online));
            mBluPlugMaxOnlineCard.setDescription(getString(R.string.max_online_summary));
            mBluPlugMaxOnlineCard.setProgress(CPUHotplug.getBluPlugMaxOnline() - 1);
            mBluPlugMaxOnlineCard.setOnDSeekBarCardListener(this);

            views.add(mBluPlugMaxOnlineCard);
        }

        if (CPUHotplug.hasBluPlugMaxCoresScreenOff()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < CPU.getCoreCount(); i++)
                list.add(String.valueOf(i + 1));

            mBluPlugMaxCoresScreenOffCard = new SeekBarCardView.DSeekBarCardView(list);
            mBluPlugMaxCoresScreenOffCard.setTitle(getString(R.string.max_cores_screen_off));
            mBluPlugMaxCoresScreenOffCard.setDescription(getString(R.string.max_cores_screen_off_summary));
            mBluPlugMaxCoresScreenOffCard.setProgress(CPUHotplug.getBluPlugMaxCoresScreenOff() - 1);
            mBluPlugMaxCoresScreenOffCard.setOnDSeekBarCardListener(this);

            views.add(mBluPlugMaxCoresScreenOffCard);
        }

        if (CPUHotplug.hasBluPlugMaxFreqScreenOff() && CPU.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mBluPlugMaxFreqScreenOffCard = new PopupCardItem.DPopupCard(list);
            mBluPlugMaxFreqScreenOffCard.setTitle(getString(R.string.cpu_max_screen_off_freq));
            mBluPlugMaxFreqScreenOffCard.setDescription(getString(R.string.cpu_max_screen_off_freq_summary));
            mBluPlugMaxFreqScreenOffCard.setItem(CPUHotplug.getBluPlugMaxFreqScreenOff());
            mBluPlugMaxFreqScreenOffCard.setOnDPopupCardListener(this);

            views.add(mBluPlugMaxFreqScreenOffCard);
        }

        if (CPUHotplug.hasBluPlugUpThreshold()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 101; i++)
                list.add(i + "%");

            mBluPlugUpThresholdCard = new SeekBarCardView.DSeekBarCardView(list);
            mBluPlugUpThresholdCard.setTitle(getString(R.string.up_threshold));
            mBluPlugUpThresholdCard.setDescription(getString(R.string.up_threshold_summary));
            mBluPlugUpThresholdCard.setProgress(CPUHotplug.getBluPlugUpThreshold());
            mBluPlugUpThresholdCard.setOnDSeekBarCardListener(this);

            views.add(mBluPlugUpThresholdCard);
        }

        if (CPUHotplug.hasBluPlugUpTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++)
                list.add(String.valueOf(i / (float) 2).replace(".0", ""));

            mBluPlugUpTimerCntCard = new SeekBarCardView.DSeekBarCardView(list);
            mBluPlugUpTimerCntCard.setTitle(getString(R.string.up_timer_cnt));
            mBluPlugUpTimerCntCard.setDescription(getString(R.string.up_timer_cnt_summary));
            mBluPlugUpTimerCntCard.setProgress(CPUHotplug.getBluPlugUpTimerCnt());
            mBluPlugUpTimerCntCard.setOnDSeekBarCardListener(this);

            views.add(mBluPlugUpTimerCntCard);
        }

        if (CPUHotplug.hasBluPlugDownTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++)
                list.add(String.valueOf(i / (float) 2).replace(".0", ""));

            mBluPlugDownTimerCntCard = new SeekBarCardView.DSeekBarCardView(list);
            mBluPlugDownTimerCntCard.setTitle(getString(R.string.down_timer_cnt));
            mBluPlugDownTimerCntCard.setDescription(getString(R.string.down_timer_cnt_summary));
            mBluPlugDownTimerCntCard.setProgress(CPUHotplug.getBluPlugDownTimerCnt());
            mBluPlugDownTimerCntCard.setOnDSeekBarCardListener(this);

            views.add(mBluPlugDownTimerCntCard);
        }

        if (views.size() > 0) {
            DividerCardView.DDividerCard mBluPlugDividerCard = new DividerCardView.DDividerCard();
            mBluPlugDividerCard.setText(getString(R.string.blu_plug));

            addView(mBluPlugDividerCard);
            addAllView(views);
        }

    }

    private void msmHotplugInit() {
        List<DAdapter.DView> views = new ArrayList<>();

        if (CPUHotplug.hasMsmHotplugEnable()) {
            mMsmHotplugEnabledCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mMsmHotplugEnabledCard.setTitle(getString(R.string.msm_hotplug));
            mMsmHotplugEnabledCard.setDescription(getString(R.string.msm_hotplug_summary));
            mMsmHotplugEnabledCard.setChecked(CPUHotplug.isMsmHotplugActive());
            mMsmHotplugEnabledCard.setOnDSwitchCompatCardListener(this);

            views.add(mMsmHotplugEnabledCard);
        }

        if (CPUHotplug.hasMsmHotplugDebugMask()) {
            mMsmHotplugDebugMaskCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mMsmHotplugDebugMaskCard.setTitle(getString(R.string.debug_mask));
            mMsmHotplugDebugMaskCard.setDescription(getString(R.string.debug_mask_summary));
            mMsmHotplugDebugMaskCard.setChecked(CPUHotplug.isMsmHotplugDebugMaskActive());
            mMsmHotplugDebugMaskCard.setOnDSwitchCompatCardListener(this);

            views.add(mMsmHotplugDebugMaskCard);
        }

        if (CPUHotplug.hasMsmHotplugMinCpusOnline()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= CPU.getCoreCount(); i++)
                list.add(String.valueOf(i));

            mMsmHotplugMinCpusOnlineCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugMinCpusOnlineCard.setTitle(getString(R.string.min_cpus_online));
            mMsmHotplugMinCpusOnlineCard.setDescription(getString(R.string.min_cpus_online_summary));
            mMsmHotplugMinCpusOnlineCard.setProgress(CPUHotplug.getMsmHotplugMinCpusOnline() - 1);
            mMsmHotplugMinCpusOnlineCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugMinCpusOnlineCard);
        }

        if (CPUHotplug.hasMsmHotplugMaxCpusOnline()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= CPU.getCoreCount(); i++)
                list.add(String.valueOf(i));

            mMsmHotplugMaxCpusOnlineCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugMaxCpusOnlineCard.setTitle(getString(R.string.max_cpus_online));
            mMsmHotplugMaxCpusOnlineCard.setDescription(getString(R.string.max_cpus_online_summary));
            mMsmHotplugMaxCpusOnlineCard.setProgress(CPUHotplug.getMsmHotplugMaxCpusOnline() - 1);
            mMsmHotplugMaxCpusOnlineCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugMaxCpusOnlineCard);
        }

        if (CPUHotplug.hasMsmHotplugCpusBoosted()) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int i = 1; i <= CPU.getCoreCount(); i++)
                list.add(String.valueOf(i));

            mMsmHotplugCpusBoostedCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugCpusBoostedCard.setTitle(getString(R.string.cpus_boosted));
            mMsmHotplugCpusBoostedCard.setDescription(getString(R.string.cpus_boosted_summary));
            mMsmHotplugCpusBoostedCard.setProgress(CPUHotplug.getMsmHotplugCpusBoosted());
            mMsmHotplugCpusBoostedCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugCpusBoostedCard);
        }

        if (CPUHotplug.hasMsmHotplugMaxCpusOnlineSusp()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= CPU.getCoreCount(); i++)
                list.add(String.valueOf(i));

            mMsmHotplugCpusOnlineSuspCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugCpusOnlineSuspCard.setTitle(getString(R.string.max_cpus_screen_off));
            mMsmHotplugCpusOnlineSuspCard.setDescription(getString(R.string.max_cpus_screen_off_summary));
            mMsmHotplugCpusOnlineSuspCard.setProgress(CPUHotplug.getMsmHotplugMaxCpusOnlineSusp() - 1);
            mMsmHotplugCpusOnlineSuspCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugCpusOnlineSuspCard);
        }

        if (CPUHotplug.hasMsmHotplugBoostLockDuration()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 5001; i++)
                list.add(String.valueOf(i));

            mMsmHotplugBoostLockDurationCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugBoostLockDurationCard.setTitle(getString(R.string.boost_lock_duration));
            mMsmHotplugBoostLockDurationCard.setDescription(getString(R.string.boost_lock_duration_summary));
            mMsmHotplugBoostLockDurationCard.setProgress(CPUHotplug.getMsmHotplugBoostLockDuration() - 1);
            mMsmHotplugBoostLockDurationCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugBoostLockDurationCard);
        }

        if (CPUHotplug.hasMsmHotplugDownLockDuration()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 5001; i++)
                list.add(String.valueOf(i));

            mMsmHotplugDownLockDurationCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugDownLockDurationCard.setTitle(getString(R.string.down_lock_duration));
            mMsmHotplugDownLockDurationCard.setDescription(getString(R.string.down_lock_duration_summary));
            mMsmHotplugDownLockDurationCard.setProgress(CPUHotplug.getMsmHotplugDownLockDuration() - 1);
            mMsmHotplugDownLockDurationCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugDownLockDurationCard);
        }

        if (CPUHotplug.hasMsmHotplugHistorySize()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 61; i++)
                list.add(String.valueOf(i));

            mMsmHotplugHistorySizeCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugHistorySizeCard.setTitle(getString(R.string.history_size));
            mMsmHotplugHistorySizeCard.setDescription(getString(R.string.history_size_summary));
            mMsmHotplugHistorySizeCard.setProgress(CPUHotplug.getMsmHotplugHistorySize() - 1);
            mMsmHotplugHistorySizeCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugHistorySizeCard);
        }

        if (CPUHotplug.hasMsmHotplugUpdateRate()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 61; i++)
                list.add(String.valueOf(i));

            mMsmHotplugUpdateRateCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugUpdateRateCard.setTitle(getString(R.string.update_rate));
            mMsmHotplugUpdateRateCard.setDescription(getString(R.string.update_rate_summary));
            mMsmHotplugUpdateRateCard.setProgress(CPUHotplug.getMsmHotplugUpdateRate());
            mMsmHotplugUpdateRateCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugUpdateRateCard);
        }

        if (CPUHotplug.hasMsmHotplugFastLaneLoad()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 401; i++)
                list.add(String.valueOf(i));

            mMsmHotplugFastLaneLoadCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugFastLaneLoadCard.setTitle(getString(R.string.fast_lane_load));
            mMsmHotplugFastLaneLoadCard.setDescription(getString(R.string.fast_lane_load_summary));
            mMsmHotplugFastLaneLoadCard.setProgress(CPUHotplug.getMsmHotplugFastLaneLoad());
            mMsmHotplugFastLaneLoadCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugFastLaneLoadCard);
        }

        if (CPUHotplug.hasMsmHotplugFastLaneMinFreq()) {
            List<String> list = new ArrayList<>();
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mMsmHotplugFastLaneMinFreqCard = new PopupCardItem.DPopupCard(list);
            mMsmHotplugFastLaneMinFreqCard.setTitle(getString(R.string.fast_lane_min_freq));
            mMsmHotplugFastLaneMinFreqCard.setDescription(getString(R.string.fast_lane_min_freq_summary));
            mMsmHotplugFastLaneMinFreqCard.setItem((CPUHotplug.getMsmHotplugFastLaneMinFreq() / 1000) + getString(R.string.mhz));
            mMsmHotplugFastLaneMinFreqCard.setOnDPopupCardListener(this);

            views.add(mMsmHotplugFastLaneMinFreqCard);
        }

        if (CPUHotplug.hasMsmHotplugOfflineLoad()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 101; i++)
                list.add(String.valueOf(i));

            mMsmHotplugOfflineLoadCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugOfflineLoadCard.setTitle(getString(R.string.offline_load));
            mMsmHotplugOfflineLoadCard.setDescription(getString(R.string.offline_load_summary));
            mMsmHotplugOfflineLoadCard.setProgress(CPUHotplug.getMsmHotplugOfflineLoad());
            mMsmHotplugOfflineLoadCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugOfflineLoadCard);
        }

        if (CPUHotplug.hasMsmHotplugIoIsBusy()) {
            mMsmHotplugIoIsBusyCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mMsmHotplugIoIsBusyCard.setTitle(getString(R.string.io_is_busy));
            mMsmHotplugIoIsBusyCard.setDescription(getString(R.string.io_is_busy_summary));
            mMsmHotplugIoIsBusyCard.setChecked(CPUHotplug.isMsmHotplugIoIsBusyActive());
            mMsmHotplugIoIsBusyCard.setOnDSwitchCompatCardListener(this);

            views.add(mMsmHotplugIoIsBusyCard);
        }

        if (CPUHotplug.hasMsmHotplugSuspendMaxCpus()) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            for (int i = 1; i <= CPU.getCoreCount(); i++)
                list.add(String.valueOf(i));

            mMsmHotplugSuspendMaxCpusCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugSuspendMaxCpusCard.setTitle(getString(R.string.suspend_max_cpus));
            mMsmHotplugSuspendMaxCpusCard.setDescription(getString(R.string.suspend_max_cpus_summary));
            mMsmHotplugSuspendMaxCpusCard.setProgress(CPUHotplug.getMsmHotplugSuspendMaxCpus());
            mMsmHotplugSuspendMaxCpusCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugSuspendMaxCpusCard);
        }

        if (CPUHotplug.hasMsmHotplugSuspendFreq()) {
            List<String> list = new ArrayList<>();
            for (int freq : CPU.getFreqs())
                list.add((freq / 1000) + getString(R.string.mhz));

            mMsmHotplugSuspendFreqCard = new PopupCardItem.DPopupCard(list);
            mMsmHotplugSuspendFreqCard.setTitle(getString(R.string.suspend_freq));
            mMsmHotplugSuspendFreqCard.setDescription(getString(R.string.suspend_freq_summary));
            mMsmHotplugSuspendFreqCard.setItem((CPUHotplug.getMsmHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            mMsmHotplugSuspendFreqCard.setOnDPopupCardListener(this);

            views.add(mMsmHotplugSuspendFreqCard);
        }

        if (CPUHotplug.hasMsmHotplugSuspendDeferTime()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= 5001; i += 10)
                list.add(String.valueOf(i));

            mMsmHotplugSuspendDeferTimeCard = new SeekBarCardView.DSeekBarCardView(list);
            mMsmHotplugSuspendDeferTimeCard.setTitle(getString(R.string.defer_time));
            mMsmHotplugSuspendDeferTimeCard.setProgress(CPUHotplug.getMsmHotplugSuspendDeferTime());
            mMsmHotplugSuspendDeferTimeCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHotplugSuspendDeferTimeCard);
        }
        
        if (views.size() > 0) {
            DividerCardView.DDividerCard mMsmHotplugDividerCard = new DividerCardView.DDividerCard();
            mMsmHotplugDividerCard.setText(getString(R.string.msm_hotplug));

            addView(mMsmHotplugDividerCard);
            addAllView(views);
        }

    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mMpdecisionCard)
            CPUHotplug.activateMpdecision(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugCard)
            CPUHotplug.activateIntelliPlug(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugEcoCard)
            CPUHotplug.activateIntelliPlugEco(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugTouchBoostCard)
            CPUHotplug.activateIntelliPlugTouchBoost(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugDebugCard)
            CPUHotplug.activateIntelliPlugDebug(checked, getActivity());
        if (dSwitchCompatCard == mIntelliPlugSuspendCard)
            CPUHotplug.activateIntelliPlugSuspend(checked, getActivity());
        if (dSwitchCompatCard == mBluPlugCard)
            CPUHotplug.activateBluPlug(checked, getActivity());
        if (dSwitchCompatCard == mBluPlugPowersaverModeCard)
            CPUHotplug.activateBluPlugPowersaverMode(checked, getActivity());
        if (dSwitchCompatCard == mMsmHotplugEnabledCard)
            CPUHotplug.activateMsmHotplug(checked, getActivity());
        if (dSwitchCompatCard == mMsmHotplugDebugMaskCard)
            CPUHotplug.activateMsmHotplugDebugMask(checked, getActivity());
        if (dSwitchCompatCard == mMsmHotplugIoIsBusyCard)
            CPUHotplug.activateMsmHotplugIoIsBusy(checked, getActivity());
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mIntelliPlugProfileCard)
            CPUHotplug.setIntelliPlugProfile(position, getActivity());
        if (dPopupCard == mIntelliPlugScreenOffMaxCard)
            CPUHotplug.setIntelliPlugScreenOffMax(position, getActivity());
        if (dPopupCard == mBluPlugMaxFreqScreenOffCard)
            CPUHotplug.setBluPlugMaxFreqScreenOff(position, getActivity());
        if (dPopupCard == mMsmHotplugFastLaneMinFreqCard)
            CPUHotplug.setMsmHotplugFastLaneMinFreq(CPU.getFreqs().get(position), getActivity());
        if (dPopupCard == mMsmHotplugSuspendFreqCard)
            CPUHotplug.setMsmHotplugSuspendFreq(CPU.getFreqs().get(position), getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mIntelliPlugHysteresisCard)
            CPUHotplug.setIntelliPlugHysteresis(position, getActivity());
        if (dSeekBarCardView == mIntelliPlugThresholdCard)
            CPUHotplug.setIntelliPlugThresold(position, getActivity());
        if (dSeekBarCardView == mIntelliPlugCpusBoostedCard)
            CPUHotplug.setIntelliPlugCpusBoosted(position + 1, getActivity());
        if (dSeekBarCardView == mIntelliPlugMinCpusOnlineCard)
            CPUHotplug.setIntelliPlugMinCpusOnline(position + 1, getActivity());
        if (dSeekBarCardView == mIntelliPlugMaxCpusOnlineCard)
            CPUHotplug.setIntelliPlugMaxCpusOnline(position + 1, getActivity());
        if (dSeekBarCardView == mIntelliPlugMaxCpusOnlineSuspCard)
            CPUHotplug.setIntelliPlugMaxCpusOnlineSusp(position + 1, getActivity());
        if (dSeekBarCardView == mIntelliPlugSuspendDeferTimeCard)
            CPUHotplug.setIntelliPlugSuspendDeferTime(position * 10, getActivity());
        if (dSeekBarCardView == mIntelliPlugDeferSamplingCard)
            CPUHotplug.setIntelliPlugDeferSampling(position, getActivity());
        if (dSeekBarCardView == mIntelliPlugBoostLockDurationCard)
            CPUHotplug.setIntelliPlugBoostLockDuration(position + 1, getActivity());
        if (dSeekBarCardView == mIntelliPlugDownLockDurationCard)
            CPUHotplug.setIntelliPlugDownLockDuration(position + 1, getActivity());
        if (dSeekBarCardView == mIntelliPlugFShiftCard)
            CPUHotplug.setIntelliPlugFShift(position, getActivity());
        if (dSeekBarCardView == mBluPlugMinOnlineCard)
            CPUHotplug.setBluPlugMinOnline(position + 1, getActivity());
        if (dSeekBarCardView == mBluPlugMaxOnlineCard)
            CPUHotplug.setBluPlugMaxOnline(position + 1, getActivity());
        if (dSeekBarCardView == mBluPlugMaxCoresScreenOffCard)
            CPUHotplug.setBluPlugMaxCoresScreenOff(position + 1, getActivity());
        if (dSeekBarCardView == mBluPlugUpThresholdCard)
            CPUHotplug.setBluPlugUpThreshold(position, getActivity());
        if (dSeekBarCardView == mBluPlugUpTimerCntCard)
            CPUHotplug.setBluPlugUpTimerCnt(position, getActivity());
        if (dSeekBarCardView == mBluPlugDownTimerCntCard)
            CPUHotplug.setBluPlugDownTimerCnt(position, getActivity());
        if (dSeekBarCardView == mMsmHotplugMinCpusOnlineCard)
            CPUHotplug.setMsmHotplugMinCpusOnline(position + 1, getActivity());
        if (dSeekBarCardView == mMsmHotplugMaxCpusOnlineCard)
            CPUHotplug.setMsmHotplugMaxCpusOnline(position + 1, getActivity());
        if (dSeekBarCardView == mMsmHotplugCpusBoostedCard)
            CPUHotplug.setMsmHotplugCpusBoosted(position, getActivity());
        if (dSeekBarCardView == mMsmHotplugCpusOnlineSuspCard)
            CPUHotplug.setMsmHotplugMaxCpusOnlineSusp(position + 1, getActivity());
        if (dSeekBarCardView == mMsmHotplugBoostLockDurationCard)
            CPUHotplug.setMsmHotplugBoostLockDuration(position + 1, getActivity());
        if (dSeekBarCardView == mMsmHotplugDownLockDurationCard)
            CPUHotplug.setMsmHotplugDownLockDuration(position + 1, getActivity());
        if (dSeekBarCardView == mMsmHotplugHistorySizeCard)
            CPUHotplug.setMsmHotplugHistorySize(position + 1, getActivity());
        if (dSeekBarCardView == mMsmHotplugUpdateRateCard)
            CPUHotplug.setMsmHotplugUpdateRate(position, getActivity());
        if (dSeekBarCardView == mMsmHotplugFastLaneLoadCard)
            CPUHotplug.setMsmHotplugFastLaneLoad(position, getActivity());
        if (dSeekBarCardView == mMsmHotplugOfflineLoadCard)
            CPUHotplug.setMsmHotplugOfflineLoad(position, getActivity());
        if (dSeekBarCardView == mMsmHotplugSuspendMaxCpusCard)
            CPUHotplug.setMsmHotplugSuspendMaxCpus(position, getActivity());
        if (dSeekBarCardView == mMsmHotplugSuspendDeferTimeCard)
            CPUHotplug.setMsmHotplugSuspendDeferTime(position, getActivity());
    }

}
