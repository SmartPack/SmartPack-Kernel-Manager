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

package com.smartpack.kernelmanager.fragments;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import com.smartpack.kernelmanager.utils.Wakelocks;
import com.smartpack.kernelmanager.utils.WakeLockInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.warning),
                getString(R.string.wakelock_summary)));
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Wakelocks.boefflawlsupported()){
            boefflaWakelockInit(items);
        }
	wakelockInit(items);
    }

    private void boefflaWakelockInit(List<RecyclerViewItem> items){
        List<RecyclerViewItem> bwl = new ArrayList<>();

        TitleView title = new TitleView();
        title.setText(getString(R.string.boeffla_wakelock) + "\n" + "Version: " + Wakelocks.getboefflawlVersion());
        mWakeCard.clear();

        SelectView borfflawlorder = new SelectView();
        borfflawlorder.setTitle(getString(R.string.wkl_order));
        borfflawlorder.setSummary(getString(R.string.wkl_order_summary));
        borfflawlorder.setItems(Arrays.asList(getResources().getStringArray(R.array.b_wakelocks_oder)));
        borfflawlorder.setItem(Wakelocks.getWakelockOrder());
        borfflawlorder.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Wakelocks.setWakelockOrder(position);
                bwCardReload();
            }
        });
        bwl.add(borfflawlorder);

        List<WakeLockInfo> wakelocksinfo = Wakelocks.getWakelockInfo();

        CardView cardViewB = new CardView(getActivity());
        String titleB = getString(R.string.wkl_blocked);
        grxbwCardInit(cardViewB, titleB, wakelocksinfo, false);
        mWakeCard.add(cardViewB);

        CardView cardViewA = new CardView(getActivity());
        String titleA = getString(R.string.wkl_allowed);
        CardView cardA = new CardView(getActivity());
        grxbwCardInit(cardViewA, titleA, wakelocksinfo, true);
        mWakeCard.add(cardViewA);

        bwl.addAll(mWakeCard);

        items.add(title);
        items.addAll(bwl);
    }

    private void grxbwCardInit(CardView card, String title, List<WakeLockInfo> wakelocksinfo, Boolean state){
        card.clearItems();
        card.setTitle(title);

        for(WakeLockInfo wakeLockInfo : wakelocksinfo){
            if(wakeLockInfo.wState == state) {
                final String name = wakeLockInfo.wName;
                String wakeup = String.valueOf(wakeLockInfo.wWakeups);
                String time = String.valueOf(wakeLockInfo.wTime / 1000);
                time = Utils.sToString(Utils.strToLong(time));

                SwitchView sw = new SwitchView();
                sw.setTitle(name);
                sw.setSummary(getString(R.string.wkl_total_time) + ": " + time + "\n" +
				getString(R.string.wkl_wakep_count) + ": " + wakeup);
                sw.setChecked(wakeLockInfo.wState);
                sw.addOnSwitchListener((switchView, isChecked) -> {
                    if (isChecked) {
                        Wakelocks.setWakelockAllowed(name, getActivity());
                    } else {
                        Wakelocks.setWakelockBlocked(name, getActivity());
                    }
                    getHandler().postDelayed(this::bwCardReload, 250);
                });
                card.addItem(sw);
            }
        }
    }

    private void bwCardReload() {

	List<WakeLockInfo> wakelocksinfo = Wakelocks.getWakelockInfo();
        String titleB = getString(R.string.wkl_blocked);
        grxbwCardInit(mWakeCard.get(0), titleB, wakelocksinfo, false);

        String titleA = getString(R.string.wkl_allowed);
        grxbwCardInit(mWakeCard.get(1), titleA, wakelocksinfo, true);

    }

    private void wakelockInit(List<RecyclerViewItem> items) {
	CardView wakelocks = new CardView(getActivity());
	wakelocks.setTitle(getString(R.string.wakelocks_other));
	for (final Wakelocks.Wakelock wakelock : Wakelocks.getWakelocks()) {
            if (!wakelock.exists()) continue;
		String description = wakelock.getDescription(getActivity());
		SwitchView switchView = new SwitchView();
            if (description == null) {
		switchView.setSummary(wakelock.getTitle(getActivity()));
            } else {
                switchView.setTitle(wakelock.getTitle(getActivity()));
                switchView.setSummary(description);
            }
            switchView.setChecked(wakelock.isEnabled());
            switchView.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    wakelock.enable(isChecked, getActivity());
                }
            });
             wakelocks.addItem(switchView);
	}
	if (Wakelocks.hasWlanrxDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) {
                list.add((100 / i) + "%");
            }
            list.add("0%");
            SeekBarView wlanrxDivider = new SeekBarView();
            wlanrxDivider.setTitle(getString(R.string.wlan_rx_wakelock_divider));
            wlanrxDivider.setItems(list);
            wlanrxDivider.setProgress(Wakelocks.getWlanrxDivider());
            wlanrxDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setWlanrxDivider(position, getActivity());
                }
		@Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            wakelocks.addItem(wlanrxDivider);
	}
	if (Wakelocks.hasWlanctrlDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) {
                list.add((100 / i) + "%");
            }
            list.add("0%");
            SeekBarView wlanctrlDivider = new SeekBarView();
            wlanctrlDivider.setTitle(getString(R.string.wlan_ctrl_wakelock_divider));
            wlanctrlDivider.setItems(list);
            wlanctrlDivider.setProgress(Wakelocks.getWlanctrlDivider());
            wlanctrlDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setWlanctrlDivider(position, getActivity());
                }
		@Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            wakelocks.addItem(wlanctrlDivider);
	}
	if (Wakelocks.hasMsmHsicDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) {
                list.add((100 / i) + "%");
            }
            list.add("0%");
            SeekBarView msmHsicDivider = new SeekBarView();
            msmHsicDivider.setTitle(getString(R.string.msm_hsic_wakelock_divider));
            msmHsicDivider.setItems(list);
            msmHsicDivider.setProgress(Wakelocks.getMsmHsicDivider());
            msmHsicDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setMsmHsicDivider(position, getActivity());
                }
		@Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            wakelocks.addItem(msmHsicDivider);
	}
	if (Wakelocks.hasBCMDHDDivider()) {
            SeekBarView bcmdhdDivider = new SeekBarView();
            bcmdhdDivider.setTitle(getString(R.string.bcmdhd_wakelock_divider));
            bcmdhdDivider.setMax(9);
            bcmdhdDivider.setMin(1);
            bcmdhdDivider.setProgress(Wakelocks.getBCMDHDDivider() - 1);
            bcmdhdDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setBCMDHDDivider(position + 1, getActivity());
                }
		@Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            wakelocks.addItem(bcmdhdDivider);
	}
	if (wakelocks.size() > 0) {
            items.add(wakelocks);
	}
    }

}
