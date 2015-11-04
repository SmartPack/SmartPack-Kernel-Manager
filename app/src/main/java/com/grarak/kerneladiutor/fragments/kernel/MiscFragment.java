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
import android.util.Log;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.elements.DDivider;
import com.grarak.kerneladiutor.elements.cards.EditTextCardView;
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 02.01.15.
 */
public class MiscFragment extends RecyclerViewFragment implements PopupCardView.DPopupCard.OnDPopupCardListener,
        SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener,
        SwitchCardView.DSwitchCard.OnDSwitchCardListener,
        EditTextCardView.DEditTextCard.OnDEditTextCardListener {

    private SeekBarCardView.DSeekBarCard mVibrationCard;

    private SwitchCardView.DSwitchCard mLoggerEnableCard;

    private SwitchCardView.DSwitchCard mCrcCard;

    private SwitchCardView.DSwitchCard mFsyncCard;
    private SwitchCardView.DSwitchCard mDynamicFsyncCard;

    private PopupCardView.DPopupCard mPowerSuspendModeCard;
    private SwitchCardView.DSwitchCard mOldPowerSuspendStateCard;
    private SeekBarCardView.DSeekBarCard mNewPowerSuspendStateCard;

    private PopupCardView.DPopupCard mTcpCongestionCard;
    private EditTextCardView.DEditTextCard mHostnameCard;

    private SwitchCardView.DSwitchCard mSmb135xWakeLockCard;
    private SwitchCardView.DSwitchCard mSensorIndWakeLockCard;
    private SwitchCardView.DSwitchCard mMsmHsicHostWakeLockCard;
    private SwitchCardView.DSwitchCard mWlanrxWakelockCard;
    private SwitchCardView.DSwitchCard mWlanctrlWakelockCard;
    private SwitchCardView.DSwitchCard mWlanWakelockCard;
    private SeekBarCardView.DSeekBarCard mWlanrxWakelockDividerCard;
    private SeekBarCardView.DSeekBarCard mMsmHsicWakelockDividerCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Misc.hasVibration()) vibrationInit();
        if (Misc.hasLoggerEnable()) loggerInit();
        if (Misc.hasCrc()) crcInit();
        fsyncInit();
        if (Misc.hasPowerSuspend()) powersuspendInit();
        networkInit();
        wakelockInit();
    }

    private void vibrationInit() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 101; i++)
            list.add(i + "%");

        int max = Misc.getVibrationMax();
        int min = Misc.getVibrationMin();
        float offset = (max - min) / (float) 101;

        mVibrationCard = new SeekBarCardView.DSeekBarCard(list);
        mVibrationCard.setTitle(getString(R.string.vibration_strength));
        mVibrationCard.setProgress(Math.round((Misc.getCurVibration() - min) / offset));
        mVibrationCard.setOnDSeekBarCardListener(this);

        addView(mVibrationCard);
    }

    private void loggerInit() {
        mLoggerEnableCard = new SwitchCardView.DSwitchCard();
        mLoggerEnableCard.setDescription(getString(R.string.android_logger));
        mLoggerEnableCard.setChecked(Misc.isLoggerActive());
        mLoggerEnableCard.setOnDSwitchCardListener(this);

        addView(mLoggerEnableCard);
    }

    private void crcInit() {
        mCrcCard = new SwitchCardView.DSwitchCard();
        mCrcCard.setTitle(getString(R.string.crc));
        mCrcCard.setDescription(getString(R.string.crc_summary));
        mCrcCard.setChecked(Misc.isCrcActive());
        mCrcCard.setOnDSwitchCardListener(this);

        addView(mCrcCard);
    }

    private void fsyncInit() {
        if (Misc.hasFsync()) {
            mFsyncCard = new SwitchCardView.DSwitchCard();
            mFsyncCard.setTitle(getString(R.string.fsync));
            mFsyncCard.setDescription(getString(R.string.fsync_summary));
            mFsyncCard.setChecked(Misc.isFsyncActive());
            mFsyncCard.setOnDSwitchCardListener(this);

            addView(mFsyncCard);
        }

        if (Misc.hasDynamicFsync()) {
            mDynamicFsyncCard = new SwitchCardView.DSwitchCard();
            mDynamicFsyncCard.setTitle(getString(R.string.dynamic_fsync));
            mDynamicFsyncCard.setDescription(getString(R.string.dynamic_fsync_summary));
            mDynamicFsyncCard.setChecked(Misc.isDynamicFsyncActive());
            mDynamicFsyncCard.setOnDSwitchCardListener(this);

            addView(mDynamicFsyncCard);
        }
    }


    private void powersuspendInit() {
        if (Misc.hasPowerSuspendMode()) {
            mPowerSuspendModeCard = new PopupCardView.DPopupCard(new ArrayList<>(
                    Arrays.asList(getResources().getStringArray(R.array.powersuspend_items))));
            mPowerSuspendModeCard.setTitle(getString(R.string.power_suspend_mode));
            mPowerSuspendModeCard.setDescription(getString(R.string.power_suspend_mode_summary));
            mPowerSuspendModeCard.setItem(Misc.getPowerSuspendMode());
            mPowerSuspendModeCard.setOnDPopupCardListener(this);

            addView(mPowerSuspendModeCard);
        }

        if (Misc.hasOldPowerSuspendState()) {
            mOldPowerSuspendStateCard = new SwitchCardView.DSwitchCard();
            mOldPowerSuspendStateCard.setTitle(getString(R.string.power_suspend_state));
            mOldPowerSuspendStateCard.setDescription(getString(R.string.power_suspend_state_summary));
            mOldPowerSuspendStateCard.setChecked(Misc.isOldPowerSuspendStateActive());
            mOldPowerSuspendStateCard.setOnDSwitchCardListener(this);

            addView(mOldPowerSuspendStateCard);
        }

        if (Misc.hasNewPowerSuspendState()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 3; i++)
                list.add(String.valueOf(i));

            mNewPowerSuspendStateCard = new SeekBarCardView.DSeekBarCard(list);
            mNewPowerSuspendStateCard.setTitle(getString(R.string.power_suspend_state));
            mNewPowerSuspendStateCard.setDescription(getString(R.string.power_suspend_state_summary));
            mNewPowerSuspendStateCard.setProgress(Misc.getNewPowerSuspendState());
            mNewPowerSuspendStateCard.setOnDSeekBarCardListener(this);

            addView(mNewPowerSuspendStateCard);
        }
    }

    private void networkInit() {
        DDivider mNetworkDividerCard = new DDivider();
        mNetworkDividerCard.setText(getString(R.string.network));
        addView(mNetworkDividerCard);

        try {
            mTcpCongestionCard = new PopupCardView.DPopupCard(Misc.getTcpAvailableCongestions());
            mTcpCongestionCard.setTitle(getString(R.string.tcp));
            mTcpCongestionCard.setDescription(getString(R.string.tcp_summary));
            mTcpCongestionCard.setItem(Misc.getCurTcpCongestion());
            mTcpCongestionCard.setOnDPopupCardListener(this);

            addView(mTcpCongestionCard);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Failed to read TCP");
        }

        String hostname = Misc.getHostname();
        mHostnameCard = new EditTextCardView.DEditTextCard();
        mHostnameCard.setTitle(getString(R.string.hostname));
        mHostnameCard.setDescription(hostname);
        mHostnameCard.setValue(hostname);
        mHostnameCard.setOnDEditTextCardListener(this);

        addView(mHostnameCard);
    }

    private void wakelockInit() {
        List<DAdapter.DView> views = new ArrayList<>();

        if (Misc.hasSmb135xWakeLock()) {
            mSmb135xWakeLockCard = new SwitchCardView.DSwitchCard();
            mSmb135xWakeLockCard.setTitle(getString(R.string.smb135x_wakelock));
            mSmb135xWakeLockCard.setDescription(getString(R.string.smb135x_wakelock_summary));
            mSmb135xWakeLockCard.setChecked(Misc.isSmb135xWakeLockActive());
            mSmb135xWakeLockCard.setOnDSwitchCardListener(this);

            views.add(mSmb135xWakeLockCard);
        }

        if (Misc.hasSensorIndWakeLock()) {
            mSensorIndWakeLockCard = new SwitchCardView.DSwitchCard();
            mSensorIndWakeLockCard.setTitle(getString(R.string.sensor_ind_wakelock));
            mSensorIndWakeLockCard.setDescription(getString(R.string.sensor_ind_wakelock_summary));
            mSensorIndWakeLockCard.setChecked(Misc.isSensorIndWakeLockActive());
            mSensorIndWakeLockCard.setOnDSwitchCardListener(this);

            views.add(mSensorIndWakeLockCard);
        }

        if (Misc.hasMsmHsicHostWakeLock()) {
            mMsmHsicHostWakeLockCard = new SwitchCardView.DSwitchCard();
            mMsmHsicHostWakeLockCard.setTitle(getString(R.string.msm_hsic_host_wakelock));
            mMsmHsicHostWakeLockCard.setDescription(getString(R.string.msm_hsic_host_wakelock_summary));
            mMsmHsicHostWakeLockCard.setChecked(Misc.isMsmHsicHostWakeLockActive());
            mMsmHsicHostWakeLockCard.setOnDSwitchCardListener(this);

            views.add(mMsmHsicHostWakeLockCard);
        }

        if (Misc.hasWlanrxWakeLock()) {
            mWlanrxWakelockCard = new SwitchCardView.DSwitchCard();
            mWlanrxWakelockCard.setTitle(getString(R.string.wlan_rx_wakelock));
            mWlanrxWakelockCard.setDescription(getString(R.string.wlan_rx_wakelock_summary));
            mWlanrxWakelockCard.setChecked(Misc.isWlanrxWakeLockActive());
            mWlanrxWakelockCard.setOnDSwitchCardListener(this);

            views.add(mWlanrxWakelockCard);
        }

        if (Misc.hasWlanctrlWakeLock()) {
            mWlanctrlWakelockCard = new SwitchCardView.DSwitchCard();
            mWlanctrlWakelockCard.setTitle(getString(R.string.wlan_ctrl_wakelock));
            mWlanctrlWakelockCard.setDescription(getString(R.string.wlan_ctrl_wakelock_summary));
            mWlanctrlWakelockCard.setChecked(Misc.isWlanctrlWakeLockActive());
            mWlanctrlWakelockCard.setOnDSwitchCardListener(this);

            views.add(mWlanctrlWakelockCard);
        }

        if (Misc.hasWlanWakeLock()) {
            mWlanWakelockCard = new SwitchCardView.DSwitchCard();
            mWlanWakelockCard.setTitle(getString(R.string.wlan_wakelock));
            mWlanWakelockCard.setDescription(getString(R.string.wlan_wakelock_summary));
            mWlanWakelockCard.setChecked(Misc.isWlanWakeLockActive());
            mWlanWakelockCard.setOnDSwitchCardListener(this);

            views.add(mWlanWakelockCard);
        }

        if (Misc.hasWlanrxWakelockDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) list.add((100 / i) + "%");
            list.add("0%");

            mWlanrxWakelockDividerCard = new SeekBarCardView.DSeekBarCard(list);
            mWlanrxWakelockDividerCard.setTitle(getString(R.string.wlan_rx_wakelock_divider));
            mWlanrxWakelockDividerCard.setProgress(Misc.getWlanrxWakelockDivider());
            mWlanrxWakelockDividerCard.setOnDSeekBarCardListener(this);

            views.add(mWlanrxWakelockDividerCard);
        }

        if (Misc.hasMsmHsicWakelockDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) list.add((100 / i) + "%");
            list.add("0%");

            mMsmHsicWakelockDividerCard = new SeekBarCardView.DSeekBarCard(list);
            mMsmHsicWakelockDividerCard.setTitle(getString(R.string.msm_hsic_wakelock_divider));
            mMsmHsicWakelockDividerCard.setProgress(Misc.getMsmHsicWakelockDivider());
            mMsmHsicWakelockDividerCard.setOnDSeekBarCardListener(this);

            views.add(mMsmHsicWakelockDividerCard);
        }

        if (!views.isEmpty()) {
            DDivider mWakelockDividerCard = new DDivider();
            mWakelockDividerCard.setText(getString(R.string.wakelock));
            addView(mWakelockDividerCard);

            addAllViews(views);
        }
    }

    @Override
    public void onItemSelected(PopupCardView.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mTcpCongestionCard)
            Misc.setTcpCongestion(Misc.getTcpAvailableCongestions().get(position), getActivity());
        else if (dPopupCard == mPowerSuspendModeCard)
            Misc.setPowerSuspendMode(position, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
        if (dSeekBarCard == mVibrationCard) {
            int max = Misc.getVibrationMax();
            int min = Misc.getVibrationMin();
            float offset = (max - min) / (float) 101;
            Misc.setVibration(Math.round(offset * position) + min, getActivity());

            // Vibrate
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        Utils.vibrate(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (dSeekBarCard == mNewPowerSuspendStateCard)
            if (Misc.getPowerSuspendMode() == 1) {
                Misc.setNewPowerSuspend(position, getActivity());
            } else dSeekBarCard.setProgress(Misc.getNewPowerSuspendState());
        else if (dSeekBarCard == mWlanrxWakelockDividerCard)
            Misc.setWlanrxWakelockDivider(position, getActivity());
        else if (dSeekBarCard == mMsmHsicWakelockDividerCard)
            Misc.setMsmHsicWakelockDivider(position, getActivity());
    }

    @Override
    public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
        if (dSwitchCard == mLoggerEnableCard)
            Misc.activateLogger(checked, getActivity());
        else if (dSwitchCard == mCrcCard)
            Misc.activateCrc(checked, getActivity());
        else if (dSwitchCard == mFsyncCard)
            Misc.activateFsync(checked, getActivity());
        else if (dSwitchCard == mDynamicFsyncCard)
            Misc.activateDynamicFsync(checked, getActivity());
        else if (dSwitchCard == mOldPowerSuspendStateCard)
            if (Misc.getPowerSuspendMode() == 1) {
                Misc.activateOldPowerSuspend(checked, getActivity());
            } else dSwitchCard.setChecked(Misc.isOldPowerSuspendStateActive());
        else if (dSwitchCard == mSmb135xWakeLockCard)
            Misc.activateSmb135xWakeLock(checked, getActivity());
        else if (dSwitchCard == mSensorIndWakeLockCard)
            Misc.activateSensorIndWakeLock(checked, getActivity());
        else if (dSwitchCard == mMsmHsicHostWakeLockCard)
            Misc.activateMsmHsicHostWakeLock(checked, getActivity());
        else if (dSwitchCard == mWlanrxWakelockCard)
            Misc.activateWlanrxWakeLock(checked, getActivity());
        else if (dSwitchCard == mWlanctrlWakelockCard)
            Misc.activateWlanctrlWakeLock(checked, getActivity());
        else if (dSwitchCard == mWlanWakelockCard)
            Misc.activateWlanWakeLock(checked, getActivity());
    }

    @Override
    public void onApply(EditTextCardView.DEditTextCard dEditTextCard, String value) {
        dEditTextCard.setDescription(value);
        if (dEditTextCard == mHostnameCard) Misc.setHostname(value, getActivity());
    }
}
