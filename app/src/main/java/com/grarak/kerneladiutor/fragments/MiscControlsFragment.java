package com.grarak.kerneladiutor.fragments;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Misc;

/**
 * Created by willi on 02.01.15.
 */
public class MiscControlsFragment extends RecyclerViewFragment implements PopupCardItem.DPopupCard.OnDPopupCardListener {

    private PopupCardItem.DPopupCard mTcpCongestionCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        tcpCongestionInit();
    }

    private void tcpCongestionInit() {
        mTcpCongestionCard = new PopupCardItem.DPopupCard(Misc.getTcpAvailableCongestions());
        mTcpCongestionCard.setTitle(getString(R.string.tcp));
        mTcpCongestionCard.setDescription(getString(R.string.tcp_summary));
        mTcpCongestionCard.setItem(Misc.getCurTcpCongestion());
        mTcpCongestionCard.setOnDPopupCardListener(this);

        addView(mTcpCongestionCard);
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mTcpCongestionCard)
            Misc.setTcpCongestion(Misc.getTcpAvailableCongestions().get(position), getActivity());
    }
}
