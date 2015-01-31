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
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.CheckBoxCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.KSM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class KSMFragment extends RecyclerViewFragment implements Constants,
        CheckBoxCardItem.DCheckBoxCard.OnDCheckBoxCardListener, SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener {

    private CardViewItem.DCardView[] mInfos;

    private CheckBoxCardItem.DCheckBoxCard mEnableKsmCard;

    private List<String> mPagesToScanValues = new ArrayList<>(), mSleepMillisecondsValues = new ArrayList<>();
    private SeekBarCardView.DSeekBarCardView mPagesToScanCard, mSleepMillisecondsCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        ksmInfoInit();
        ksmInit();
    }

    private void ksmInfoInit() {
        mInfos = new CardViewItem.DCardView[KSM_INFOS.length];
        for (int i = 0; i < mInfos.length; i++) {
            mInfos[i] = new CardViewItem.DCardView();
            mInfos[i].setTitle(getResources().getStringArray(R.array.ksm_infos)[i]);
            mInfos[i].setDescription(String.valueOf(KSM.getInfos(i)));

            addView(mInfos[i]);
        }
    }

    private void ksmInit() {
        mEnableKsmCard = new CheckBoxCardItem.DCheckBoxCard();
        mEnableKsmCard.setTitle(getString(R.string.ksm_enable));
        mEnableKsmCard.setDescription(getString(R.string.ksm_enable_summary));
        mEnableKsmCard.setChecked(KSM.isKsmActive());
        mEnableKsmCard.setOnDCheckBoxCardListener(this);

        addView(mEnableKsmCard);

        mPagesToScanValues.clear();
        for (int i = 0; i < 1025; i++) mPagesToScanValues.add(String.valueOf(i));
        mPagesToScanCard = new SeekBarCardView.DSeekBarCardView(mPagesToScanValues);
        mPagesToScanCard.setTitle(getString(R.string.ksm_pages_to_scan));
        mPagesToScanCard.setProgress(mPagesToScanValues.indexOf(String.valueOf(KSM.getPagesToScan())));
        mPagesToScanCard.setOnDSeekBarCardListener(this);

        addView(mPagesToScanCard);

        mSleepMillisecondsValues.clear();
        for (int i = 0; i < 5001; i++) mSleepMillisecondsValues.add(i + getString(R.string.ms));
        mSleepMillisecondsCard = new SeekBarCardView.DSeekBarCardView(mSleepMillisecondsValues);
        mSleepMillisecondsCard.setTitle(getString(R.string.ksm_sleep_milliseconds));
        mSleepMillisecondsCard.setProgress(mSleepMillisecondsValues.indexOf(KSM.getSleepMilliseconds()
                + getString(R.string.ms)));
        mSleepMillisecondsCard.setOnDSeekBarCardListener(this);

        addView(mSleepMillisecondsCard);
    }

    @Override
    public void onChecked(CheckBoxCardItem.DCheckBoxCard dCheckBoxCard, boolean checked) {
        if (dCheckBoxCard == mEnableKsmCard) KSM.activateKSM(checked, getActivity());
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mPagesToScanCard)
            KSM.setPagesToScan(Utils.stringToInt(mPagesToScanValues.get(position)), getActivity());
        if (dSeekBarCardView == mSleepMillisecondsCard)
            KSM.setSleepMilliseconds(Utils.stringToInt(mSleepMillisecondsValues.get(position)
                    .replace(getString(R.string.ms), "")), getActivity());
    }

    @Override
    public boolean onRefresh() {

        for (int i = 0; i < KSM_INFOS.length; i++)
            mInfos[i].setDescription(String.valueOf(KSM.getInfos(i)));

        return true;
    }

}
