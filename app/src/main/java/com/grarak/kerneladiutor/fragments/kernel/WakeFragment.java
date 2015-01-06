package com.grarak.kerneladiutor.fragments.kernel;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Wake;

/**
 * Created by willi on 02.01.15.
 */
public class WakeFragment extends RecyclerViewFragment implements PopupCardItem.DPopupCard.OnDPopupCardListener {

    private PopupCardItem.DPopupCard mDt2wCard;
    private PopupCardItem.DPopupCard mS2wCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Wake.hasDt2w()) dt2wInit();
        if (Wake.hasS2w()) s2wInit();
    }

    private void dt2wInit() {
        mDt2wCard = new PopupCardItem.DPopupCard(Wake.getDt2wMenu(getActivity()));
        mDt2wCard.setTitle(getString(R.string.dt2w));
        mDt2wCard.setDescription(getString(R.string.dt2w_summary));
        mDt2wCard.setItem(Wake.getDt2wValue());
        mDt2wCard.setOnDPopupCardListener(this);

        addView(mDt2wCard);
    }

    private void s2wInit() {
        mS2wCard = new PopupCardItem.DPopupCard(Wake.getS2wMenu(getActivity()));
        mS2wCard.setTitle(getString(R.string.s2w));
        mS2wCard.setDescription(getString(R.string.s2w_summary));
        mS2wCard.setItem(Wake.getS2wValue());
        mS2wCard.setOnDPopupCardListener(this);

        addView(mS2wCard);
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mDt2wCard) Wake.setDt2w(position, getActivity());
        if (dPopupCard == mS2wCard) Wake.setS2w(position, getActivity());
    }
}
