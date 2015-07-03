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

package com.grarak.kerneladiutor.fragments.other;

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;

/**
 * Created by willi on 22.03.15.
 */
public class FAQFragment extends RecyclerViewFragment {

    @Override
    public boolean showApplyOnBoot() {
        return false;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        CardViewItem.DCardView mMisspelledCard = new CardViewItem.DCardView();
        mMisspelledCard.setTitle(getString(R.string.misspelled));
        mMisspelledCard.setDescription(getString(R.string.misspelled_summary));
        addView(mMisspelledCard);

        CardViewItem.DCardView mCPUFreqNotSticking = new CardViewItem.DCardView();
        mCPUFreqNotSticking.setTitle(getString(R.string.cpu_freq_not_sticking));
        mCPUFreqNotSticking.setDescription(getString(R.string.cpu_freq_not_sticking_summary));
        addView(mCPUFreqNotSticking);

        CardViewItem.DCardView mFeatureNotAppearingCard = new CardViewItem.DCardView();
        mFeatureNotAppearingCard.setTitle(getString(R.string.feature_not_appearing));
        mFeatureNotAppearingCard.setDescription(getString(R.string.feature_not_appearing_summary));
        addView(mFeatureNotAppearingCard);

        CardViewItem.DCardView mFeatureFunctionCard = new CardViewItem.DCardView();
        mFeatureFunctionCard.setTitle(getString(R.string.feature_function));
        mFeatureFunctionCard.setDescription(getString(R.string.feature_function_summary));
        addView(mFeatureFunctionCard);

        CardViewItem.DCardView mAddNewFeaturesCard = new CardViewItem.DCardView();
        mAddNewFeaturesCard.setTitle(getString(R.string.add_new_features));
        mAddNewFeaturesCard.setDescription(getString(R.string.add_new_features_summary));
        addView(mAddNewFeaturesCard);
    }

}
