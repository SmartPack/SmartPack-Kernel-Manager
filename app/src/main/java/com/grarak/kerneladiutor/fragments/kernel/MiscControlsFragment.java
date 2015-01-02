package com.grarak.kerneladiutor.fragments.kernel;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CheckBoxCardItem;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Misc;

/**
 * Created by willi on 02.01.15.
 */
public class MiscControlsFragment extends RecyclerViewFragment implements PopupCardItem.DPopupCard.OnDPopupCardListener,
        CheckBoxCardItem.DCheckBoxCard.OnDCheckBoxCardListener {

    private PopupCardItem.DPopupCard mTcpCongestionCard;

    private CheckBoxCardItem.DCheckBoxCard mForceFastChargeCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        tcpCongestionInit();
        if (Misc.hasForceFastCharge()) forceFastChargeInit();
    }

    private void tcpCongestionInit() {
        mTcpCongestionCard = new PopupCardItem.DPopupCard(Misc.getTcpAvailableCongestions());
        mTcpCongestionCard.setTitle(getString(R.string.tcp));
        mTcpCongestionCard.setDescription(getString(R.string.tcp_summary));
        mTcpCongestionCard.setItem(Misc.getCurTcpCongestion());
        mTcpCongestionCard.setOnDPopupCardListener(this);

        addView(mTcpCongestionCard);
    }

    private void forceFastChargeInit() {
        mForceFastChargeCard = new CheckBoxCardItem.DCheckBoxCard();
        mForceFastChargeCard.setTitle(getString(R.string.usb_fast_charge));
        mForceFastChargeCard.setDescription(getString(R.string.usb_fast_charge_summary));
        mForceFastChargeCard.setChecked(Misc.isForceFastChargeActive());
        mForceFastChargeCard.setOnDCheckBoxCardListener(this);

        addView(mForceFastChargeCard);
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mTcpCongestionCard)
            Misc.setTcpCongestion(Misc.getTcpAvailableCongestions().get(position), getActivity());
    }

    @Override
    public void onChecked(CheckBoxCardItem.DCheckBoxCard dCheckBoxCard, boolean checked) {
        if (dCheckBoxCard == mForceFastChargeCard)
            Misc.activateForceFastCharge(checked, getActivity());
    }

}
