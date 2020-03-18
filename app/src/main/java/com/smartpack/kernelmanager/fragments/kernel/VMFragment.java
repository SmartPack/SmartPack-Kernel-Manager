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
package com.smartpack.kernelmanager.fragments.kernel;

import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.kernel.vm.VM;
import com.smartpack.kernelmanager.utils.kernel.vm.ZRAM;
import com.smartpack.kernelmanager.utils.kernel.vm.ZSwap;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class VMFragment extends RecyclerViewFragment {

    private List<GenericSelectView> mVMs = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        VMInit(items);
        if (ZRAM.supported()) {
            zramInit(items);
        }
        zswapInit(items);
    }

    private void VMInit(List<RecyclerViewItem> items) {
        CardView vmCard = new CardView(getActivity());
        vmCard.setTitle(getString(R.string.virtual_memory));
        mVMs.clear();

        if (!(Prefs.getBoolean("vm_tunables", false, getActivity())))
            Prefs.saveBoolean("vm_tunables", false, getActivity());

        final SwitchView vmTunables = new SwitchView();
        vmTunables.setSummary(getString(R.string.virtual_memory_tunables));
        vmTunables.setChecked(Prefs.getBoolean("vm_tunables", false, getActivity()));

        vmCard.addItem(vmTunables);

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
                        getHandler().postDelayed(() -> {
                                    for (int i = 0; i < mVMs.size(); i++) {
                                        vm.setValue(VM.getValue(i));
                                    }},
                                500);
                    }
                });

                class vmTunablesManager {
                    private void showVMTunables (boolean enable) {
                        if (enable) {
                            vmCard.addItem(vm);
                        } else {
                            vmCard.removeItem(vm);
                        }
                    }
                }

                final vmTunablesManager manager = new vmTunablesManager();
                if (Prefs.getBoolean("vm_tunables", false, getActivity())) {
                    manager.showVMTunables(true);
                } else {
                    manager.showVMTunables(false);
                }
                vmTunables.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                    @Override
                    public void onChanged(SwitchView switchview, boolean isChecked) {
                        Prefs.saveBoolean("vm_tunables", isChecked, getActivity());
                        manager.showVMTunables(isChecked);
                    }
                });
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
        zram.setMax(2560);
        zram.setOffset(8);
        zram.setProgress(ZRAM.getDisksize() / 8);
        zram.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                ZRAM.setDisksize(position * 8, getActivity());
                getHandler().postDelayed(() -> {
                            zram.setProgress(ZRAM.getDisksize() / 8);
                        },
                        500);
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        zRAM.addItem(zram);

        int zRAMvalue = ZRAM.getDisksize();

        if (ZRAM.hasZRAMAlgo()) {
            SelectView zramAlgo = new SelectView();
            zramAlgo.setTitle(getString(R.string.zram_algo));
            zramAlgo.setSummary(getString(R.string.zram_algo_summary) + ("\n") + getString(R.string.warning) +
                    (" ") + getString(R.string.zram_algo_warning));
            zramAlgo.setItems(ZRAM.getAvailableZRAMAlgos());
            zramAlgo.setItem(ZRAM.getZRAMAlgo());
            zramAlgo.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    ZRAM.setDisksize(0, getActivity());
                    ZRAM.setZRAMAlgo(item, getActivity());
                    ZRAM.setDisksize((zRAMvalue), getActivity());
                    getHandler().postDelayed(() -> {
                                zramAlgo.setItem(ZRAM.getZRAMAlgo());
                            },
                            500);
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
		    getHandler().postDelayed(() -> {
		    zswap.setChecked(ZSwap.isEnabled());
		    },
	    	500);
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
		    getHandler().postDelayed(() -> {
		    maxPoolPercent.setProgress(ZSwap.getMaxPoolPercent());
		    },
	    	500);
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
		    getHandler().postDelayed(() -> {
		    maxCompressionRatio.setProgress(ZSwap.getMaxCompressionRatio());
		    },
	    	500);
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

}
