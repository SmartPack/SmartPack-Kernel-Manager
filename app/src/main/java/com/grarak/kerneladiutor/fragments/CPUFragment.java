package com.grarak.kerneladiutor.fragments;

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
import com.grarak.kerneladiutor.elements.CheckBoxCardItem;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.kernel.CPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 22.12.14.
 */
public class CPUFragment extends RecyclerViewFragment implements Constants, View.OnClickListener,
        PopupCardItem.DPopupCard.OnDPopupCardListener, CardViewItem.DCardView.OnDCardListener,
        CheckBoxCardItem.DCheckBoxCard.OnDCheckBoxCardListener {

    private CheckBox[] mCoreCheckBox;
    private ProgressBar[] mCoreProgressBar;
    private TextView[] mCoreFreqText;

    private PopupCardItem.DPopupCard mMaxFreq, mMinFreq;

    private PopupCardItem.DPopupCard mGovernor;
    private CardViewItem.DCardView mGovernorTunable;

    private PopupCardItem.DPopupCard mMcPowerSaving;

    private CheckBoxCardItem.DCheckBoxCard mMpdecision;

    private CheckBoxCardItem.DCheckBoxCard mIntelliPlug, mIntelliPlugEco;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        coreInit();
        freqInit();
        governorInit();
        if (CPU.hasMcPowerSaving()) mcPowerSavingInit();
        if (CPU.hasMpdecision()) mpdecisionInit();
        if (CPU.hasIntelliPlug()) intelliPlugInit();
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

        mMaxFreq = new PopupCardItem.DPopupCard(freqs);
        mMaxFreq.setTitle(getString(R.string.cpu_max_freq));
        mMaxFreq.setDescription(getString(R.string.cpu_max_freq_summary));
        mMaxFreq.setItem(CPU.getMaxFreq(0) / 1000 + MHZ);
        mMaxFreq.setOnDPopupCardListener(this);

        mMinFreq = new PopupCardItem.DPopupCard(freqs);
        mMinFreq.setTitle(getString(R.string.cpu_min_freq));
        mMinFreq.setDescription(getString(R.string.cpu_min_freq_summary));
        mMinFreq.setItem(CPU.getMinFreq(0) / 1000 + MHZ);
        mMinFreq.setOnDPopupCardListener(this);

        addView(mMaxFreq);
        addView(mMinFreq);
    }

    private void governorInit() {
        mGovernor = new PopupCardItem.DPopupCard(CPU.getAvailableGovernors());
        mGovernor.setTitle(getString(R.string.cpu_governor));
        mGovernor.setDescription(getString(R.string.cpu_governor_summary));
        mGovernor.setItem(CPU.getCurGovernor(0));
        mGovernor.setOnDPopupCardListener(this);

        mGovernorTunable = new CardViewItem.DCardView();
        mGovernorTunable.setTitle(getString(R.string.cpu_governor_tunables));
        mGovernorTunable.setDescription(getString(R.string.cpu_governor_tunables_summary));
        mGovernorTunable.setOnDCardListener(this);

        addView(mGovernor);
        addView(mGovernorTunable);
    }

    private void mcPowerSavingInit() {
        mMcPowerSaving = new PopupCardItem.DPopupCard(CPU.getMcPowerSavingItems(getActivity()));
        mMcPowerSaving.setTitle(getString(R.string.mc_power_saving));
        mMcPowerSaving.setDescription(getString(R.string.mc_power_saving_summary));
        mMcPowerSaving.setItem(CPU.getCurMcPowerSaving());
        mMcPowerSaving.setOnDPopupCardListener(this);

        addView(mMcPowerSaving);
    }

    private void mpdecisionInit() {
        mMpdecision = new CheckBoxCardItem.DCheckBoxCard();
        mMpdecision.setTitle(getString(R.string.mpdecision));
        mMpdecision.setDescription(getString(R.string.mpdecision_summary));
        mMpdecision.setChecked(CPU.isMpdecisionActive());
        mMpdecision.setOnDCheckBoxCardListener(this);

        addView(mMpdecision);
    }

    private void intelliPlugInit() {
        mIntelliPlug = new CheckBoxCardItem.DCheckBoxCard();
        mIntelliPlug.setTitle(getString(R.string.intelliplug));
        mIntelliPlug.setDescription(getString(R.string.intelliplug_summary));
        mIntelliPlug.setChecked(CPU.isIntelliPlugActive());
        mIntelliPlug.setOnDCheckBoxCardListener(this);

        addView(mIntelliPlug);

        if (!CPU.hasIntelliPlugEco()) return;
        mIntelliPlugEco = new CheckBoxCardItem.DCheckBoxCard();
        mIntelliPlugEco.setTitle(getString(R.string.intelliplug_eco_mode_summary));
        mIntelliPlugEco.setDescription(getString(R.string.intelliplug_eco_mode_summary));
        mIntelliPlugEco.setChecked(CPU.isIntelliPlugEcoActive());
        mIntelliPlugEco.setOnDCheckBoxCardListener(this);

        addView(mIntelliPlugEco);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mCoreCheckBox.length; i++)
            if (v == mCoreCheckBox[i])
                CPU.activateCore(i, ((CheckBox) v).isChecked(), getActivity());
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mMaxFreq) CPU.setMaxFreq(CPU.getFreqs().get(position), getActivity());
        if (dPopupCard == mMinFreq) CPU.setMinFreq(CPU.getFreqs().get(position), getActivity());
        if (dPopupCard == mGovernor)
            CPU.setGovernor(CPU.getAvailableGovernors().get(position), getActivity());
        if (dPopupCard == mMcPowerSaving) CPU.setMcPowerSaving(position, getActivity());
    }

    @Override
    public void onClick(CardViewItem.DCardView dCardView) {
        if (dCardView == mGovernorTunable) {
            String governor = CPU.getCurGovernor(0);
            Intent i = new Intent(getActivity(),
                    PathReaderActivity.class);
            Bundle args = new Bundle();
            args.putString(PathReaderActivity.ARG_TITLE, governor);
            args.putString(PathReaderActivity.ARG_PATH, String.format(CPU_GOVERNOR_TUNABLES, governor));
            args.putString(PathReaderActivity.ARG_ERROR, getString(R.string.not_tunable, governor));
            i.putExtras(args);

            startActivity(i);
        }
    }

    @Override
    public void onChecked(CheckBoxCardItem.DCheckBoxCard dCheckBoxCard, boolean checked) {
        if (dCheckBoxCard == mMpdecision) CPU.activateMpdecision(checked, getActivity());
        if (dCheckBoxCard == mIntelliPlug) CPU.activateIntelliPlug(checked, getActivity());
        if (dCheckBoxCard == mIntelliPlugEco) CPU.activateIntelliPlugEco(checked, getActivity());
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

        if (mMaxFreq != null) mMaxFreq.setItem(CPU.getMaxFreq(0) / 1000 + MHZ);
        if (mMinFreq != null) mMinFreq.setItem(CPU.getMinFreq(0) / 1000 + MHZ);
        if (mGovernor != null) mGovernor.setItem(CPU.getCurGovernor(0));

        return true;
    }

}
