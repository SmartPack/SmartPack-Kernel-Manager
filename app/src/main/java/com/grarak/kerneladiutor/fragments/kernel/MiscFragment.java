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
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
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
public class MiscFragment extends RecyclerViewFragment implements PopupCardItem.DPopupCard.OnDPopupCardListener,
        SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener,
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener {

    private PopupCardItem.DPopupCard mTcpCongestionCard;

    private SeekBarCardView.DSeekBarCardView mVibrationCard;

    private SwitchCompatCardItem.DSwitchCompatCard mSmb135xWakeLockCard;
    private SwitchCompatCardItem.DSwitchCompatCard mSensorIndWakeLockCard;
    private SwitchCompatCardItem.DSwitchCompatCard mMsmHsicHostWakeLockCard;

    private SwitchCompatCardItem.DSwitchCompatCard mLoggerEnableCard;

    private PopupCardItem.DPopupCard mSelinuxCard;

    private SwitchCompatCardItem.DSwitchCompatCard mDynamicFsyncCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        tcpCongestionInit();
        if (Misc.hasVibration()) vibrationInit();
        if (Misc.hasSmb135xWakeLock()) smb135xWakeLockInit();
        if (Misc.hasSensorIndWakeLock()) sensorIndWakeLockInit();
        if (Misc.hasMsmHsicHostWakeLock()) msmHsicHostWakeLockInit();
        if (Misc.hasLoggerEnable()) loggerInit();
        if (Misc.hasSelinux()) selinuxInit();
        if (Misc.hasDynamicFsync()) dynamicFsyncInit();
    }

    private void tcpCongestionInit() {
        try {
            mTcpCongestionCard = new PopupCardItem.DPopupCard(Misc.getTcpAvailableCongestions());
            mTcpCongestionCard.setTitle(getString(R.string.tcp));
            mTcpCongestionCard.setDescription(getString(R.string.tcp_summary));
            mTcpCongestionCard.setItem(Misc.getCurTcpCongestion());
            mTcpCongestionCard.setOnDPopupCardListener(this);

            addView(mTcpCongestionCard);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Failed to read TCP");
        }
    }

    private void vibrationInit() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 101; i++)
            list.add(i + "%");

        int max = Misc.getVibrationMax();
        int min = Misc.getVibrationMin();
        float offset = (max - min) / (float) 101;

        mVibrationCard = new SeekBarCardView.DSeekBarCardView(list);
        mVibrationCard.setTitle(getString(R.string.vibration_strength));
        mVibrationCard.setProgress(Math.round((Misc.getCurVibration() - min) / offset));
        mVibrationCard.setOnDSeekBarCardListener(this);

        addView(mVibrationCard);
    }

    private void smb135xWakeLockInit() {
        mSmb135xWakeLockCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mSmb135xWakeLockCard.setTitle(getString(R.string.smb135x_wakelock));
        mSmb135xWakeLockCard.setDescription(getString(R.string.smb135x_wakelock_summary));
        mSmb135xWakeLockCard.setChecked(Misc.isSmb135xWakeLockActive());
        mSmb135xWakeLockCard.setOnDSwitchCompatCardListener(this);

        addView(mSmb135xWakeLockCard);
    }

    private void sensorIndWakeLockInit() {
        mSensorIndWakeLockCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mSensorIndWakeLockCard.setTitle(getString(R.string.sensor_ind_wakelock));
        mSensorIndWakeLockCard.setDescription(getString(R.string.sensor_ind_wakelock_summary));
        mSensorIndWakeLockCard.setChecked(Misc.isSensorIndWakeLockActive());
        mSensorIndWakeLockCard.setOnDSwitchCompatCardListener(this);

        addView(mSensorIndWakeLockCard);
    }

    private void msmHsicHostWakeLockInit() {
        mMsmHsicHostWakeLockCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mMsmHsicHostWakeLockCard.setTitle(getString(R.string.msm_hsic_host_wakelock));
        mMsmHsicHostWakeLockCard.setDescription(getString(R.string.msm_hsic_host_wakelock_summary));
        mMsmHsicHostWakeLockCard.setChecked(Misc.isMsmHsicHostWakeLockActive());
        mMsmHsicHostWakeLockCard.setOnDSwitchCompatCardListener(this);

        addView(mMsmHsicHostWakeLockCard);
    }

    private void loggerInit() {
        mLoggerEnableCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mLoggerEnableCard.setDescription(getString(R.string.android_logger));
        mLoggerEnableCard.setChecked(Misc.isLoggerActive());
        mLoggerEnableCard.setOnDSwitchCompatCardListener(this);

        addView(mLoggerEnableCard);
    }

    private void selinuxInit() {
        String[] items = getResources().getStringArray(R.array.selinux_items);
        mSelinuxCard = new PopupCardItem.DPopupCard(new ArrayList<>(Arrays.asList(items)));
        mSelinuxCard.setTitle(getString(R.string.selinux));
        mSelinuxCard.setDescription(getString(R.string.selinux_summary));
        mSelinuxCard.setItem(Misc.getSelinux());
        mSelinuxCard.setOnDPopupCardListener(this);

        addView(mSelinuxCard);
    }

    private void dynamicFsyncInit() {
        mDynamicFsyncCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mDynamicFsyncCard.setTitle(getString(R.string.dynamic_fsync));
        mDynamicFsyncCard.setDescription(getString(R.string.dynamic_fsync_summary));
        mDynamicFsyncCard.setChecked(Misc.isDynamicFsyncActive());
        mDynamicFsyncCard.setOnDSwitchCompatCardListener(this);

        addView(mDynamicFsyncCard);
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mTcpCongestionCard)
            Misc.setTcpCongestion(Misc.getTcpAvailableCongestions().get(position), getActivity());
        else if (dPopupCard == mSelinuxCard)
            Misc.setSelinux(position, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mVibrationCard) {
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
        }
    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mSmb135xWakeLockCard)
            Misc.activateSmb135xWakeLock(checked, getActivity());
        else if (dSwitchCompatCard == mSensorIndWakeLockCard)
            Misc.activateSensorIndWakeLock(checked, getActivity());
        else if (dSwitchCompatCard == mMsmHsicHostWakeLockCard)
            Misc.activateMsmHsicHostWakeLock(checked, getActivity());
        else if (dSwitchCompatCard == mLoggerEnableCard)
            Misc.activateLogger(checked, getActivity());
        else if (dSwitchCompatCard == mDynamicFsyncCard)
            Misc.activateDynamicFsync(checked, getActivity());
    }
}
