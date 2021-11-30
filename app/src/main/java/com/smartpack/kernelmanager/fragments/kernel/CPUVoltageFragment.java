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

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.kernel.cpuvoltage.Voltage;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUVoltageFragment extends RecyclerViewFragment {

    private Voltage mVoltage;

    private final List<GenericSelectView> mVoltages = new ArrayList<>();

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 2;
    }

    @Override
    protected void init() {
        super.init();

        mVoltage = Voltage.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(GlobalOffsetFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVoltages.clear();

        if (mVoltage.hasOverrideVmin()) {
            SwitchView overrideVmin = new SwitchView();
            overrideVmin.setTitle(getString(R.string.override_vmin));
            overrideVmin.setSummary(getString(R.string.override_vmin_summary));
            overrideVmin.setChecked(mVoltage.isOverrideVminEnabled());
            overrideVmin.setFullSpan(true);
            overrideVmin.addOnSwitchListener((switchView, isChecked) -> mVoltage.enableOverrideVmin(isChecked, getActivity()));

            items.add(overrideVmin);
        }

        List<String> freqs = mVoltage.getFreqs();
        List<String> voltages = mVoltage.getVoltages();
        if (freqs != null && voltages != null && freqs.size() == voltages.size()) {
            for (int i = 0; i < freqs.size(); i++) {
                GenericSelectView view = new GenericSelectView();
                initView(view, freqs.get(i), voltages.get(i));
                mVoltages.add(view);
            }
        }
        items.addAll(mVoltages);
    }

    private void reload() {
        List<String> freqs = mVoltage.getFreqs();
        List<String> voltages = mVoltage.getVoltages();
        if (freqs != null && voltages != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                initView(mVoltages.get(i), freqs.get(i), voltages.get(i));
            }
        }
    }

    private void initView(GenericSelectView view, final String freq, String voltage) {
        String freqText = mVoltage.isVddVoltage() ? String.valueOf(Utils.strToInt(freq) / 1000) : freq;
        view.setTitle(freqText + getString(R.string.mhz));
        view.setSummary(voltage + getString(R.string.mv));
        view.setValue("");
        view.setValueRaw(voltage);
        view.setInputType(InputType.TYPE_CLASS_NUMBER);
        view.setOnGenericValueListener((genericSelectView, value) -> {
            mVoltage.setVoltage(freq, value, getActivity());
            getHandler().postDelayed(this::reload, 200);
        });
    }

    public static class GlobalOffsetFragment extends BaseFragment {

        public static GlobalOffsetFragment newInstance(CPUVoltageFragment cpuVoltageFragment) {
            GlobalOffsetFragment fragment = new GlobalOffsetFragment();
            fragment.mCPUVoltageFragment = cpuVoltageFragment;
            return fragment;
        }

        private CPUVoltageFragment mCPUVoltageFragment;
        private int mGlobaloffset;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_global_offset, container, false);
            final MaterialTextView offset = rootView.findViewById(R.id.offset);
            offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
            rootView.findViewById(R.id.button_minus).setOnClickListener(v -> {
                mGlobaloffset -= 5;
                offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                Voltage.getInstance().setGlobalOffset(-5, getActivity());
                if (mCPUVoltageFragment != null) {
                    mCPUVoltageFragment.getHandler().postDelayed(() -> mCPUVoltageFragment.reload(), 200);
                }
            });
            rootView.findViewById(R.id.button_plus).setOnClickListener(v -> {
                mGlobaloffset += 5;
                offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                Voltage.getInstance().setGlobalOffset(5, getActivity());
                if (mCPUVoltageFragment != null) {
                    mCPUVoltageFragment.getHandler().postDelayed(() -> mCPUVoltageFragment.reload(), 200);
                }
            });
            return rootView;
        }
    }

}