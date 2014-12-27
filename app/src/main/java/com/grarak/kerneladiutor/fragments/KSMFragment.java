package com.grarak.kerneladiutor.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    private CheckBoxCardItem.DCheckBoxCard mEnableKsm;

    private List<String> mPagesToScanValues = new ArrayList<>(), mSleepMillisecondsValues = new ArrayList<>();
    private SeekBarCardView.DSeekBarCardView mPagesToScan, mSleepMilliseconds;

    private GridLayoutManager gridLayoutManager;

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        gridLayoutManager = new GridLayoutManager(getActivity(), getSpanCount());
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (gridLayoutManager != null)
            gridLayoutManager.setSpanCount(getSpanCount());
        super.onConfigurationChanged(newConfig);
    }

    private int getSpanCount() {
        return Utils.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
    }

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
        mEnableKsm = new CheckBoxCardItem.DCheckBoxCard();
        mEnableKsm.setTitle(getString(R.string.ksm_enable));
        mEnableKsm.setDescription(getString(R.string.ksm_enable_summary));
        mEnableKsm.setChecked(KSM.isKsmActive());
        mEnableKsm.setOnDCheckBoxCardListener(this);

        addView(mEnableKsm);

        mPagesToScanValues.clear();
        for (int i = 0; i < 1025; i++) mPagesToScanValues.add(String.valueOf(i));
        mPagesToScan = new SeekBarCardView.DSeekBarCardView(mPagesToScanValues);
        mPagesToScan.setTitle(getString(R.string.ksm_pages_to_scan));
        mPagesToScan.setProgress(mPagesToScanValues.indexOf(String.valueOf(KSM.getPagesToScan())));
        mPagesToScan.setOnDSeekBarCardListener(this);

        addView(mPagesToScan);

        mSleepMillisecondsValues.clear();
        for (int i = 0; i < 5001; i++) mSleepMillisecondsValues.add(i + getString(R.string.ms));
        mSleepMilliseconds = new SeekBarCardView.DSeekBarCardView(mSleepMillisecondsValues);
        mSleepMilliseconds.setTitle(getString(R.string.ksm_sleep_milliseconds));
        mSleepMilliseconds.setProgress(mSleepMillisecondsValues.indexOf(KSM.getSleepMilliseconds() + getString(R.string.ms)));
        mSleepMilliseconds.setOnDSeekBarCardListener(this);

        addView(mSleepMilliseconds);
    }

    @Override
    public void onChecked(CheckBoxCardItem.DCheckBoxCard dCheckBoxCard, boolean checked) {
        if (dCheckBoxCard == mEnableKsm) KSM.activateKSM(checked, getActivity());
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mPagesToScan)
            KSM.setPagesToScan(Integer.parseInt(mPagesToScanValues.get(position)), getActivity());
        if (dSeekBarCardView == mSleepMilliseconds)
            KSM.setSleepMilliseconds(Integer.parseInt(mSleepMillisecondsValues.get(position)
                    .replace(getString(R.string.ms), "")), getActivity());
    }

    @Override
    public boolean onRefresh() {

        for (int i = 0; i < KSM_INFOS.length; i++)
            mInfos[i].setDescription(String.valueOf(KSM.getInfos(i)));

        return true;
    }

}
