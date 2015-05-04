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
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.InformationCardView;
import com.grarak.kerneladiutor.elements.SwitchCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.Thermal;

/**
 * Created by willi on 03.05.15.
 */
public class ThermalFragment extends RecyclerViewFragment implements SwitchCardView.DSwitchCard.OnDSwitchCardListener {

    private SwitchCardView.DSwitchCard mThermaldCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (!Utils.getBoolean("hideinfocardthermal", false, getActivity())) {
            final InformationCardView.DInformationCard mInformationCard = new InformationCardView.DInformationCard();
            mInformationCard.setText(getString(R.string.thermal_info));
            mInformationCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeView(mInformationCard);
                    Utils.saveBoolean("hideinfocardthermal", true, getActivity());
                }
            });

            addView(mInformationCard);
        }

        if (Thermal.hasThermald()) thermaldInit();
    }

    private void thermaldInit() {
        mThermaldCard = new SwitchCardView.DSwitchCard();
        mThermaldCard.setTitle(getString(R.string.thermald_summary));
        mThermaldCard.setDescription(getString(R.string.thermald_summary));
        mThermaldCard.setChecked(Thermal.isThermaldActive());
        mThermaldCard.setOnDSwitchCardListener(this);

        addView(mThermaldCard);
    }

    @Override
    public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
        if (dSwitchCard == mThermaldCard) Thermal.activateThermald(checked, getActivity());
    }
}
