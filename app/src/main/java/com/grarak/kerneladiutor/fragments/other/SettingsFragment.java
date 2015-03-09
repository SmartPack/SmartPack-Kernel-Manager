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
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 09.03.15.
 */
public class SettingsFragment extends RecyclerViewFragment {

    @Override
    public boolean showApplyOnBoot() {
        return false;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        applyonbootDelayInit();
    }

    private void applyonbootDelayInit() {
        final List<String> list = new ArrayList<>();
        for (int i = 15; i < 481; i *= 2)
            list.add(i + getString(R.string.sec));

        PopupCardItem.DPopupCard mApplyonbootDelayCard = new PopupCardItem.DPopupCard(list);
        mApplyonbootDelayCard.setDescription(getString(R.string.apply_on_boot_delay));
        mApplyonbootDelayCard.setItem(Utils.getInt("applyonbootdelay", 15, getActivity()) + getString(R.string.sec));
        mApplyonbootDelayCard.setOnDPopupCardListener(new PopupCardItem.DPopupCard.OnDPopupCardListener() {
            @Override
            public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
                Utils.saveInt("applyonbootdelay", Utils.stringToInt(list.get(position)
                        .replace(getString(R.string.sec), "")), getActivity());
            }
        });

        addView(mApplyonbootDelayCard);
    }

}
