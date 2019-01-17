/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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

import android.text.InputType;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.kernel.vm.VM;
import com.grarak.kerneladiutor.utils.kernel.vm.ZRAM;
import com.grarak.kerneladiutor.utils.kernel.vm.ZSwap;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import com.smartpack.kernelmanager.utils.ProgressBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class VMFragment extends RecyclerViewFragment {

    private List<GenericSelectView> mVMs = new ArrayList<>();

    private Device.MemInfo mMemInfo;
    private ProgressBarView mem;
    private ProgressBarView swap;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        mMemInfo = Device.MemInfo.getInstance();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        memoryInit(items);
        VMInit(items);
        if (ZRAM.supported()) {
            zramInit(items);
        }
        zswapInit(items);
    }

    private void memoryInit (List<RecyclerViewItem> items){
        CardView memcard = new CardView(getActivity());
        memcard.setTitle(getString(R.string.memory));

        long swap_total = mMemInfo.getItemMb("SwapTotal");
        long mem_total = mMemInfo.getItemMb("MemTotal");
        long swap_progress = swap_total - mMemInfo.getItemMb("SwapFree");
        long mem_progress = mem_total - (mMemInfo.getItemMb("Cached") + mMemInfo.getItemMb("MemFree"));

        if ((mem_total) != 0) {
            mem = new ProgressBarView();
            mem.setTitle(getString(R.string.ram));
            mem.setItems(mem_total, mem_progress);
            mem.setUnit(getResources().getString(R.string.mb));
            mem.setProgressColor(getResources().getColor(R.color.blue_accent));
            memcard.addItem(mem);
        }

        if ((swap_total) != 0) {
            swap = new ProgressBarView();
            swap.setTitle(getString(R.string.swap));
            swap.setItems(swap_total, swap_progress);
            swap.setUnit(getResources().getString(R.string.mb));
            swap.setProgressColor(getResources().getColor(R.color.green_accent));
            memcard.addItem(swap);
        }

        if (memcard.size() > 0) {
            items.add(memcard);
        }
    }

    private void VMInit(List<RecyclerViewItem> items) {
        CardView vmCard = new CardView(getActivity());
        vmCard.setTitle(getString(R.string.virtual_memory));
        mVMs.clear();

        for (int i = 0; i < VM.size(); i++) {
            if (VM.exists(i)) {
                GenericSelectView vm = new GenericSelectView();
                vm.setSummary(VM.getName(i));
                vm.setValue(VM.getValue(i));
                vm.setValueRaw(vm.getValue());
                vm.setInputType(InputType.TYPE_CLASS_NUMBER);

                final int position = i;
                vm.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                    @Override
                    public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                        VM.setValue(value, position, getActivity());
                        genericSelectView.setValue(value);
                        refreshVMs();
                    }
                });

                vmCard.addItem(vm);
                mVMs.add(vm);
            }
        }

        if (vmCard.size() > 0) {
            items.add(vmCard);
        }
    }

    private void zramInit(List<RecyclerViewItem> items) {
        CardView zRAM = new CardView(getActivity());
        zRAM.setTitle(getString(R.string.zram));

        SeekBarView zram = new SeekBarView();
        zram.setTitle(getString(R.string.disksize));
        zram.setSummary(getString(R.string.disksize_summary));
        zram.setUnit(getString(R.string.mb));
        zram.setMax(2048);
        zram.setOffset(8);
        zram.setProgress(ZRAM.getDisksize() / 8);
        zram.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                ZRAM.setDisksize(position * 8, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        zRAM.addItem(zram);

        if (ZRAM.hasZRAMAlgo()) {
            SelectView zramAlgo = new SelectView();
            zramAlgo.setTitle(getString(R.string.zram_algo));
            zramAlgo.setSummary(getString(R.string.zram_algo_summary));
            zramAlgo.setItems(ZRAM.getAvailableZRAMAlgos());
            zramAlgo.setItem(ZRAM.getZRAMAlgo());
            zramAlgo.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    ZRAM.setZRAMAlgo(item, getActivity());
                }
            });

            zRAM.addItem(zramAlgo);
	}

        if (zRAM.size() > 0) {
            items.add(zRAM);
        }
    }

    private void zswapInit(List<RecyclerViewItem> items) {
        CardView zswapCard = new CardView(getActivity());
        zswapCard.setTitle(getString(R.string.zswap));

        if (ZSwap.hasEnable()) {
            SwitchView zswap = new SwitchView();
            zswap.setTitle(getString(R.string.zswap));
            zswap.setSummary(getString(R.string.zswap_summary));
            zswap.setChecked(ZSwap.isEnabled());
            zswap.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    ZSwap.enable(isChecked, getActivity());
                }
            });

            zswapCard.addItem(zswap);
        }

        if (ZSwap.hasMaxPoolPercent()) {
            SeekBarView maxPoolPercent = new SeekBarView();
            maxPoolPercent.setTitle(getString(R.string.memory_pool));
            maxPoolPercent.setSummary(getString(R.string.memory_pool_summary));
            maxPoolPercent.setUnit("%");
            maxPoolPercent.setMax(50);
            maxPoolPercent.setProgress(ZSwap.getMaxPoolPercent());
            maxPoolPercent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ZSwap.setMaxPoolPercent(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            zswapCard.addItem(maxPoolPercent);
        }

        if (ZSwap.hasMaxCompressionRatio()) {
            SeekBarView maxCompressionRatio = new SeekBarView();
            maxCompressionRatio.setTitle(getString(R.string.maximum_compression_ratio));
            maxCompressionRatio.setSummary(getString(R.string.maximum_compression_ratio_summary));
            maxCompressionRatio.setUnit("%");
            maxCompressionRatio.setProgress(ZSwap.getMaxCompressionRatio());
            maxCompressionRatio.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ZSwap.setMaxCompressionRatio(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            zswapCard.addItem(maxCompressionRatio);
        }

        if (zswapCard.size() > 0) {
            items.add(zswapCard);
        }
    }

    private void refreshVMs() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mVMs.size(); i++) {
                    mVMs.get(i).setValue(VM.getValue(i));
                    mVMs.get(i).setValueRaw(mVMs.get(i).getValue());
                }
            }
        }, 250);
    }

}
