package com.grarak.kerneladiutor.fragments.kernel;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.utils.kernel.CPUHotplug;

/**
 * Created by willi on 06.02.15.
 */
public class CPUHotplugFragment extends RecyclerViewFragment implements
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener {

    private SwitchCompatCardItem.DSwitchCompatCard mMpdecisionCard;

    private SwitchCompatCardItem.DSwitchCompatCard mIntelliPlugCard, mIntelliPlugEcoCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (CPUHotplug.hasMpdecision()) mpdecisionInit();
        if (CPUHotplug.hasIntelliPlug()) intelliPlugInit();
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
        mIntelliPlugCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mIntelliPlugCard.setTitle(getString(R.string.intelliplug));
        mIntelliPlugCard.setDescription(getString(R.string.intelliplug_summary));
        mIntelliPlugCard.setChecked(CPUHotplug.isIntelliPlugActive());
        mIntelliPlugCard.setOnDSwitchCompatCardListener(this);

        addView(mIntelliPlugCard);

        if (CPUHotplug.hasIntelliPlugEco()) {
            mIntelliPlugEcoCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mIntelliPlugEcoCard.setTitle(getString(R.string.intelliplug_eco_mode_summary));
            mIntelliPlugEcoCard.setDescription(getString(R.string.intelliplug_eco_mode_summary));
            mIntelliPlugEcoCard.setChecked(CPUHotplug.isIntelliPlugEcoActive());
            mIntelliPlugEcoCard.setOnDSwitchCompatCardListener(this);

            addView(mIntelliPlugEcoCard);
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
    }

}
