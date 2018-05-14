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
 * along with Kernel Adiutor. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.misc.Wakelocks;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Originally authored by Morogoku <morogoku@hotmail.com>
 *
 * Modified by sunilpaulmathew <sunil.kde@gmail.com>
 */

public class WakelockFragment extends RecyclerViewFragment {

    private List<CardView> mWakeCard = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        if (Wakelocks.boefflawlsupported()){
            boefflaWakelockInit(items);
        }
    }

    private void boefflaWakelockInit(List<RecyclerViewItem> items){
        mWakeCard.clear();

        DescriptionView warning= new DescriptionView();
        warning.setTitle(getString(R.string.warning));
        warning.setSummary(getString(R.string.wakelock_summary));
        items.add(warning);

        DescriptionView boefflawl = new DescriptionView();
        boefflawl.setTitle(getString(R.string.boeffla_wakelock));
        boefflawl.setSummary("Version: " + Wakelocks.getboefflawlVersion() + "\n" + getString(R.string.boeffla_wakelock_summary));
        items.add(boefflawl);

        SelectView borfflawlorder = new SelectView();
        borfflawlorder.setTitle(getString(R.string.wkl_order));
        borfflawlorder.setSummary(getString(R.string.wkl_order_summary));
        borfflawlorder.setItems(Arrays.asList(getResources().getStringArray(R.array.b_wakelocks_oder)));
        borfflawlorder.setItem(getString(R.string.wkl_time));
        borfflawlorder.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Wakelocks.setWakelockOrder(position);
                bwCardReload();
            }
        });
        items.add(borfflawlorder);

        List<Wakelocks.ListWake> wakelocksB = Wakelocks.getWakelockListBlocked();
        String titleB = getString(R.string.wkl_blocked);
        CardView cardB = new CardView(getActivity());
        bwCardInit(cardB, titleB, wakelocksB);
        mWakeCard.add(cardB);

        List<Wakelocks.ListWake> wakelocksA = Wakelocks.getWakelockListAllowed();
        String titleA = getString(R.string.wkl_allowed);
        CardView cardA = new CardView(getActivity());
        bwCardInit(cardA, titleA, wakelocksA);
        mWakeCard.add(cardA);

        items.addAll(mWakeCard);
    }

    private void bwCardInit(CardView card, String title, List<Wakelocks.ListWake> wakelocks){
        card.clearItems();
        card.setTitle(title);

        for(Wakelocks.ListWake wake : wakelocks){

            final String name = wake.getName();
            String wakeup = String.valueOf(wake.getWakeup());
            String time = String.valueOf(wake.getTime() / 1000);
            time = Utils.sToString(Utils.strToLong(time));

            SwitchView sw = new SwitchView();
            sw.setTitle(name);
            sw.setSummary(getString(R.string.wkl_total_time) + ": " + time + "\n" +
                    getString(R.string.wkl_wakep_count) + ": " + wakeup);
            sw.setChecked(!Wakelocks.isWakelockBlocked(name));
            sw.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    if(isChecked) {
                        Wakelocks.setWakelockAllowed(name, getActivity());
                    }else{
                        Wakelocks.setWakelockBlocked(name, getActivity());
                    }
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bwCardReload();
                        }
                    }, 50);
                }
            });

            card.addItem(sw);
        }
    }

    private void bwCardReload() {

        List<Wakelocks.ListWake> wakelocksB = Wakelocks.getWakelockListBlocked();
        String titleB = getString(R.string.wkl_blocked);
        bwCardInit(mWakeCard.get(0), titleB, wakelocksB);

        List<Wakelocks.ListWake> wakelocksA = Wakelocks.getWakelockListAllowed();
        String titleA = getString(R.string.wkl_allowed);
        bwCardInit(mWakeCard.get(1), titleA, wakelocksA);
    }

}
