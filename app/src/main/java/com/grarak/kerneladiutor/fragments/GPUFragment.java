package com.grarak.kerneladiutor.fragments;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.GPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class GPUFragment extends RecyclerViewFragment implements PopupCardItem.DPopupCard.OnPopupCardListener {

    private CardViewItem.DCardView mCur2dFreqCard, mCur3dFreqCard;

    private PopupCardItem.DPopupCard mMax2dFreqCard, mMax3dFreqCard;

    private PopupCardItem.DPopupCard m2dGovernor, m3dGovernor;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        curFreqInit();
        maxFreqInit();
        governorInit();
    }

    private void curFreqInit() {
        if (GPU.hasGpu2dCurFreq()) {
            mCur2dFreqCard = new CardViewItem.DCardView();
            mCur2dFreqCard.setTitle(getString(R.string.gpu_2d_cur_freq));

            addView(mCur2dFreqCard);
        }

        if (GPU.hasGpu3dCurFreq()) {
            mCur3dFreqCard = new CardViewItem.DCardView();
            mCur3dFreqCard.setTitle(getString(R.string.gpu_3d_cur_freq));

            addView(mCur3dFreqCard);
        }
    }

    private void maxFreqInit() {
        if (GPU.hasGpu2dMaxFreq() && GPU.hasGpu2dFreqs()) {
            List<String> freqs = new ArrayList<>();
            for (int freq : GPU.getGpu2dFreqs())
                freqs.add(freq / 1000000 + getString(R.string.mhz));

            mMax2dFreqCard = new PopupCardItem.DPopupCard(freqs);
            mMax2dFreqCard.setTitle(getString(R.string.gpu_2d_max_freq));
            mMax2dFreqCard.setDescription(getString(R.string.gpu_2d_max_freq_summary));
            mMax2dFreqCard.setItem(GPU.getGpu2dMaxFreq() / 1000000 + getString(R.string.mhz));
            mMax2dFreqCard.setOnPopupCardListener(this);

            addView(mMax2dFreqCard);
        }

        if (GPU.hasGpu3dMaxFreq() && GPU.hasGpu3dFreqs()) {
            List<String> freqs = new ArrayList<>();
            for (int freq : GPU.getGpu3dFreqs())
                freqs.add(freq / 1000000 + getString(R.string.mhz));

            mMax3dFreqCard = new PopupCardItem.DPopupCard(freqs);
            mMax3dFreqCard.setTitle(getString(R.string.gpu_3d_max_freq));
            mMax3dFreqCard.setDescription(getString(R.string.gpu_3d_max_freq_summary));
            mMax3dFreqCard.setItem(GPU.getGpu3dMaxFreq() / 1000000 + getString(R.string.mhz));
            mMax3dFreqCard.setOnPopupCardListener(this);

            addView(mMax3dFreqCard);
        }
    }

    private void governorInit() {
        if (GPU.hasGpu2dGovernor()) {
            m2dGovernor = new PopupCardItem.DPopupCard(GPU.getGpu2dGovernors());
            m2dGovernor.setTitle(getString(R.string.gpu_2d_governor));
            m2dGovernor.setDescription(getString(R.string.gpu_2d_governor_summary));
            m2dGovernor.setItem(GPU.getGpu2dGovernor());
            m2dGovernor.setOnPopupCardListener(this);

            addView(m2dGovernor);
        }

        if (GPU.hasGpu3dGovernor()) {
            m3dGovernor = new PopupCardItem.DPopupCard(GPU.getGpu3dGovernors());
            m3dGovernor.setTitle(getString(R.string.gpu_3d_governor));
            m3dGovernor.setDescription(getString(R.string.gpu_3d_governor_summary));
            m3dGovernor.setItem(GPU.getGpu3dGovernor());
            m3dGovernor.setOnPopupCardListener(this);

            addView(m3dGovernor);
        }
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mMax2dFreqCard)
            GPU.setGpu2dMaxFreq(GPU.getGpu2dFreqs().get(position), getActivity());
        if (dPopupCard == mMax3dFreqCard)
            GPU.setGpu3dMaxFreq(GPU.getGpu3dFreqs().get(position), getActivity());
        if (dPopupCard == m2dGovernor)
            GPU.setGpu2dGovernor(GPU.getGpu2dGovernors().get(position), getActivity());
        if (dPopupCard == m3dGovernor)
            GPU.setGpu3dGovernor(GPU.getGpu3dGovernors().get(position), getActivity());
    }

    @Override
    public boolean onRefresh() {

        String MHZ = getString(R.string.mhz);

        if (mCur2dFreqCard != null)
            mCur2dFreqCard.setDescription(GPU.getGpu2dCurFreq() / 1000000 + MHZ);

        if (mCur3dFreqCard != null)
            mCur3dFreqCard.setDescription(GPU.getGpu3dCurFreq() / 1000000 + MHZ);

        return true;
    }
}
