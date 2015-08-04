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
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.Entropy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 10.07.15.
 */
public class EntropyFragment extends RecyclerViewFragment implements PopupCardView.DPopupCard.OnDPopupCardListener {

    private CardViewItem.DCardView mAvailableCard;
    private CardViewItem.DCardView mPoolsizeCard;
    private PopupCardView.DPopupCard mReadCard;
    private PopupCardView.DPopupCard mWriteCard;

    private final List<String> items = new ArrayList<>();

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        int poolsize = Entropy.getPoolsize();

        mAvailableCard = new CardViewItem.DCardView();
        mAvailableCard.setTitle(getString(R.string.available));
        mAvailableCard.setDescription(getAvailableDescription(Entropy.getAvailable(), poolsize));

        addView(mAvailableCard);

        mPoolsizeCard = new CardViewItem.DCardView();
        mPoolsizeCard.setTitle(getString(R.string.poolsize));
        mPoolsizeCard.setDescription(String.valueOf(poolsize));

        addView(mPoolsizeCard);

        items.clear();
        for (int i = 64; i < poolsize; i *= 2) if (i < poolsize) items.add(String.valueOf(i));
        items.add(String.valueOf(poolsize));

        mReadCard = new PopupCardView.DPopupCard(items);
        mReadCard.setDescription(getString(R.string.read));
        mReadCard.setItem(String.valueOf(Entropy.getRead()));
        mReadCard.setOnDPopupCardListener(this);

        addView(mReadCard);

        mWriteCard = new PopupCardView.DPopupCard(items);
        mWriteCard.setDescription(getString(R.string.write));
        mWriteCard.setItem(String.valueOf(Entropy.getWrite()));
        mWriteCard.setOnDPopupCardListener(this);

        addView(mWriteCard);
    }

    @Override
    public void onItemSelected(PopupCardView.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mReadCard)
            Entropy.setRead(Utils.stringToInt(items.get(position)), getActivity());
        else if (dPopupCard == mWriteCard)
            Entropy.setWrite(Utils.stringToInt(items.get(position)), getActivity());
    }

    private String getAvailableDescription(int available, int poolsize) {
        return Utils.round((double) available * 100 / (double) poolsize, 2) + "% (" + available + ")";
    }

    @Override
    public boolean onRefresh() {
        int poolsize = Entropy.getPoolsize();
        if (mAvailableCard != null)
            mAvailableCard.setDescription(getAvailableDescription(Entropy.getAvailable(), poolsize));
        if (mPoolsizeCard != null) mPoolsizeCard.setDescription(String.valueOf(poolsize));
        if (mReadCard != null) mReadCard.setItem(String.valueOf(Entropy.getRead()));
        if (mWriteCard != null) mWriteCard.setItem(String.valueOf(Entropy.getWrite()));
        return true;
    }

}
