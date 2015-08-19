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
import com.grarak.kerneladiutor.elements.DDivider;
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.GPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class GPUFragment extends RecyclerViewFragment implements PopupCardView.DPopupCard.OnDPopupCardListener,
        SwitchCardView.DSwitchCard.OnDSwitchCardListener,
        SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener {

    private CardViewItem.DCardView mCur2dFreqCard, mCurFreqCard;

    private PopupCardView.DPopupCard mMax2dFreqCard, mMaxFreqCard;

    private PopupCardView.DPopupCard m2dGovernorCard, mGovernorCard;

    private SwitchCardView.DSwitchCard mSimpleGpuCard;
    private SeekBarCardView.DSeekBarCard mSimpleGpuLazinessCard, mSimpleGpuRampThresoldCard;

    private SwitchCardView.DSwitchCard mAdrenoIdlerCard;
    private SeekBarCardView.DSeekBarCard mAdrenoIdlerDownDiffCard, mAdrenoIdlerIdleWaitCard, mAdrenoIdlerIdleWorkloadCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        curFreqInit();
        maxFreqInit();
        governorInit();
        if (GPU.hasSimpleGpu()) simpleGpuInit();
        if (GPU.hasAdrenoIdler()) adrenoIdlerInit();
    }

    private void curFreqInit() {
        if (GPU.hasGpu2dCurFreq()) {
            mCur2dFreqCard = new CardViewItem.DCardView();
            mCur2dFreqCard.setTitle(getString(R.string.gpu_2d_cur_freq));

            addView(mCur2dFreqCard);
        }

        if (GPU.hasGpuCurFreq()) {
            mCurFreqCard = new CardViewItem.DCardView();
            mCurFreqCard.setTitle(getString(R.string.gpu_cur_freq));

            addView(mCurFreqCard);
        }
    }

    private void maxFreqInit() {
        if (GPU.hasGpu2dMaxFreq() && GPU.hasGpu2dFreqs()) {
            List<String> freqs = new ArrayList<>();
            for (int freq : GPU.getGpu2dFreqs())
                freqs.add(freq / 1000000 + getString(R.string.mhz));

            mMax2dFreqCard = new PopupCardView.DPopupCard(freqs);
            mMax2dFreqCard.setTitle(getString(R.string.gpu_2d_max_freq));
            mMax2dFreqCard.setDescription(getString(R.string.gpu_2d_max_freq_summary));
            mMax2dFreqCard.setItem(GPU.getGpu2dMaxFreq() / 1000000 + getString(R.string.mhz));
            mMax2dFreqCard.setOnDPopupCardListener(this);

            addView(mMax2dFreqCard);
        }

        if (GPU.hasGpuMaxFreq() && GPU.hasGpuFreqs()) {
            List<String> freqs = new ArrayList<>();
            for (int freq : GPU.getGpuFreqs())
                freqs.add(freq / 1000000 + getString(R.string.mhz));

            mMaxFreqCard = new PopupCardView.DPopupCard(freqs);
            mMaxFreqCard.setTitle(getString(R.string.gpu_max_freq));
            mMaxFreqCard.setDescription(getString(R.string.gpu_max_freq_summary));
            mMaxFreqCard.setItem(GPU.getGpuMaxFreq() / 1000000 + getString(R.string.mhz));
            mMaxFreqCard.setOnDPopupCardListener(this);

            addView(mMaxFreqCard);
        }
    }

    private void governorInit() {
        if (GPU.hasGpu2dGovernor()) {
            m2dGovernorCard = new PopupCardView.DPopupCard(GPU.getGpu2dGovernors());
            m2dGovernorCard.setTitle(getString(R.string.gpu_2d_governor));
            m2dGovernorCard.setDescription(getString(R.string.gpu_2d_governor_summary));
            m2dGovernorCard.setItem(GPU.getGpu2dGovernor());
            m2dGovernorCard.setOnDPopupCardListener(this);

            addView(m2dGovernorCard);
        }

        if (GPU.hasGpuGovernor()) {
            mGovernorCard = new PopupCardView.DPopupCard(GPU.getGpuGovernors());
            mGovernorCard.setTitle(getString(R.string.gpu_governor));
            mGovernorCard.setDescription(getString(R.string.gpu_governor_summary));
            mGovernorCard.setItem(GPU.getGpuGovernor());
            mGovernorCard.setOnDPopupCardListener(this);

            addView(mGovernorCard);
        }
    }

    private void simpleGpuInit() {
        DDivider mSimpleGpuDividerCard = new DDivider();
        mSimpleGpuDividerCard.setText(getString(R.string.simple_gpu_algorithm));

        addView(mSimpleGpuDividerCard);

        mSimpleGpuCard = new SwitchCardView.DSwitchCard();
        mSimpleGpuCard.setTitle(getString(R.string.simple_gpu_algorithm));
        mSimpleGpuCard.setDescription(getString(R.string.simple_gpu_algorithm_summary));
        mSimpleGpuCard.setChecked(GPU.isSimpleGpuActive());
        mSimpleGpuCard.setOnDSwitchCardListener(this);

        addView(mSimpleGpuCard);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 11; i++)
            list.add(String.valueOf(i));

        mSimpleGpuLazinessCard = new SeekBarCardView.DSeekBarCard(list);
        mSimpleGpuLazinessCard.setTitle(getString(R.string.laziness));
        mSimpleGpuLazinessCard.setDescription(getString(R.string.laziness_summary));
        mSimpleGpuLazinessCard.setProgress(GPU.getSimpleGpuLaziness());
        mSimpleGpuLazinessCard.setOnDSeekBarCardListener(this);

        addView(mSimpleGpuLazinessCard);

        mSimpleGpuRampThresoldCard = new SeekBarCardView.DSeekBarCard(list);
        mSimpleGpuRampThresoldCard.setTitle(getString(R.string.ramp_thresold));
        mSimpleGpuRampThresoldCard.setDescription(getString(R.string.ramp_thresold_summary));
        mSimpleGpuRampThresoldCard.setProgress(GPU.getSimpleGpuRampThreshold());
        mSimpleGpuRampThresoldCard.setOnDSeekBarCardListener(this);

        addView(mSimpleGpuRampThresoldCard);
    }

    private void adrenoIdlerInit() {
        DDivider mAndrenoIdlerDivider = new DDivider();
        mAndrenoIdlerDivider.setText(getString(R.string.adreno_idler));

        addView(mAndrenoIdlerDivider);

        mAdrenoIdlerCard = new SwitchCardView.DSwitchCard();
        mAdrenoIdlerCard.setTitle(getString(R.string.adreno_idler));
        mAdrenoIdlerCard.setDescription(getString(R.string.adreno_idler_summary));
        mAdrenoIdlerCard.setChecked(GPU.isAdrenoIdlerActive());
        mAdrenoIdlerCard.setOnDSwitchCardListener(this);

        addView(mAdrenoIdlerCard);

        {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) list.add(String.valueOf(i));

            mAdrenoIdlerDownDiffCard = new SeekBarCardView.DSeekBarCard(list);
            mAdrenoIdlerDownDiffCard.setTitle(getString(R.string.down_differential));
            mAdrenoIdlerDownDiffCard.setDescription(getString(R.string.down_differential_summary));
            mAdrenoIdlerDownDiffCard.setProgress(GPU.getAdrenoIdlerDownDiff());
            mAdrenoIdlerDownDiffCard.setOnDSeekBarCardListener(this);

            addView(mAdrenoIdlerDownDiffCard);

            mAdrenoIdlerIdleWaitCard = new SeekBarCardView.DSeekBarCard(list);
            mAdrenoIdlerIdleWaitCard.setTitle(getString(R.string.idle_wait));
            mAdrenoIdlerIdleWaitCard.setDescription(getString(R.string.idle_wait_summary));
            mAdrenoIdlerIdleWaitCard.setProgress(GPU.getAdrenoIdlerIdleWait());
            mAdrenoIdlerIdleWaitCard.setOnDSeekBarCardListener(this);

            addView(mAdrenoIdlerIdleWaitCard);
        }

        {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 11; i++) list.add(String.valueOf(i));

            mAdrenoIdlerIdleWorkloadCard = new SeekBarCardView.DSeekBarCard(list);
            mAdrenoIdlerIdleWorkloadCard.setTitle(getString(R.string.workload));
            mAdrenoIdlerIdleWorkloadCard.setDescription(getString(R.string.workload_summary));
            mAdrenoIdlerIdleWorkloadCard.setProgress(GPU.getAdrenoIdlerIdleWorkload() - 1);
            mAdrenoIdlerIdleWorkloadCard.setOnDSeekBarCardListener(this);

            addView(mAdrenoIdlerIdleWorkloadCard);
        }
    }

    @Override
    public void onItemSelected(PopupCardView.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mMax2dFreqCard)
            GPU.setGpu2dMaxFreq(GPU.getGpu2dFreqs().get(position), getActivity());
        else if (dPopupCard == mMaxFreqCard)
            GPU.setGpuMaxFreq(GPU.getGpuFreqs().get(position), getActivity());
        else if (dPopupCard == m2dGovernorCard)
            GPU.setGpu2dGovernor(GPU.getGpu2dGovernors().get(position), getActivity());
        else if (dPopupCard == mGovernorCard)
            GPU.setGpuGovernor(GPU.getGpuGovernors().get(position), getActivity());
    }

    @Override
    public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
        if (dSwitchCard == mSimpleGpuCard) GPU.activateSimpleGpu(checked, getActivity());
        else if (dSwitchCard == mAdrenoIdlerCard) GPU.activateAdrenoIdler(checked, getActivity());
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
        if (dSeekBarCard == mSimpleGpuLazinessCard)
            GPU.setSimpleGpuLaziness(position, getActivity());
        else if (dSeekBarCard == mSimpleGpuRampThresoldCard)
            GPU.setSimpleGpuRampThreshold(position, getActivity());
        else if (dSeekBarCard == mAdrenoIdlerDownDiffCard)
            GPU.setAdrenoIdlerDownDiff(position, getActivity());
        else if (dSeekBarCard == mAdrenoIdlerIdleWaitCard)
            GPU.setAdrenoIdlerIdleWait(position, getActivity());
        else if (dSeekBarCard == mAdrenoIdlerIdleWorkloadCard)
            GPU.setAdrenoIdlerIdleWorkload(position + 1, getActivity());
    }

    @Override
    public boolean onRefresh() {
        if (mCur2dFreqCard != null)
            mCur2dFreqCard.setDescription((GPU.getGpu2dCurFreq() / 1000000) + getString(R.string.mhz));

        if (mCurFreqCard != null)
            mCurFreqCard.setDescription((GPU.getGpuCurFreq() / 1000000) + getString(R.string.mhz));
        return true;
    }
}
