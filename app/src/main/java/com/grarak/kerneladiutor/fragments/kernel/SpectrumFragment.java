/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.grarak.kerneladiutor.fragments.kernel;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.kernel.spectrum.Spectrum;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.List;

/**
 * Based on the original implementation of Spectrum Kernel Manager by frap129 <joe@frap129.org>
 *
 * Originally authored by Morogoku <morogoku@hotmail.com>
 *
 * Modified by sunilpaulmathew <sunil.kde@gmail.com>
 */

public class SpectrumFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.spec_title), getString(R.string.spec_info)));

    }

    private CardView oldCard;
    private DescriptionView oldDesc;


    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        final int balColor = ContextCompat.getColor(getContext(), R.color.colorBalance);
        final int perColor = ContextCompat.getColor(getContext(), R.color.colorPerformance);
        final int batColor = ContextCompat.getColor(getContext(), R.color.colorBattery);


        //CardView Balanced
        final CardView card0 = new CardView(getActivity());
        card0.setTitle(getString(R.string.spec_balanced));
        card0.setExpandable(false);

        final DescriptionView desc0 = new DescriptionView();
        desc0.setSummary(getString(R.string.spec_balanced_summary));
        desc0.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_balanced));

        card0.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card0, desc0, 0, balColor);
            }
        });

        card0.addItem(desc0);
        items.add(card0);


        //CardView Performance
        final CardView card1 = new CardView(getActivity());
        card1.setTitle(getString(R.string.spec_performance));
        card1.setExpandable(false);

        final DescriptionView desc1 = new DescriptionView();
        desc1.setSummary(getString(R.string.spec_performance_summary));
        desc1.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_performance));

        card1.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card1, desc1, 1, perColor);
            }

        });

        card1.addItem(desc1);
        items.add(card1);


        //CardView Battery
        final CardView card2 = new CardView(getActivity());
        card2.setTitle(getString(R.string.spec_battery));
        card2.setExpandable(false);

        final DescriptionView desc2 = new DescriptionView();
        desc2.setSummary(getString(R.string.spec_battery_summary));
        desc2.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card2.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card2, desc2, 2, batColor);
            }

        });

        card2.addItem(desc2);
        items.add(card2);

        //Detects the selected profile on launch
        int mProfile = Prefs.getInt("spectrum_profile", 0, getActivity());

        if(mProfile == 0){
            card0.GrxSetInitSelection(true, balColor);
            desc0.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card0;
            oldDesc = desc0;
        } else if(mProfile == 1){
            card1.GrxSetInitSelection(true, perColor);
            desc1.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card1;
            oldDesc = desc1;
        } else if(mProfile == 2){
            card2.GrxSetInitSelection(true, batColor);
            desc2.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card2;
            oldDesc = desc2;
        }

    }

    // Method that completes card onClick tasks
    private void cardClick(CardView card, DescriptionView desc, int prof, int color) {
        if (oldCard != card && oldDesc != desc) {
            ColorStateList ogColor = card.getCardBackgroundColor();
            ColorStateList odColor = desc.getTextColors();
            card.setCardBackgroundColor(color);
            desc.setTextColor(Color.WHITE);
            if(oldCard != null) oldCard.setCardBackgroundColor(ogColor);
            if(oldDesc != null) oldDesc.setTextColor(odColor);
            Spectrum.setProfile(prof);
            oldCard = card;
            oldDesc = desc;
            Prefs.saveInt("spectrum_profile", prof, getActivity());
        }
    }
}
