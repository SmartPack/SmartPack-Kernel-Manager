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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpuvoltage.Voltage;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUVoltage extends RecyclerViewFragment {

    private List<GenericSelectView> mVoltages = new ArrayList<>();

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.CPU_VOLTAGE));
        addViewPagerFragment(GlobalOffsetFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVoltages.clear();

        if (Voltage.hasOverrideVmin()) {
            SwitchView overrideVmin = new SwitchView();
            overrideVmin.setTitle(getString(R.string.override_vmin));
            overrideVmin.setSummary(getString(R.string.override_vmin_summary));
            overrideVmin.setChecked(Voltage.isOverrideVminEnabled());
            overrideVmin.setFullSpan(true);
            overrideVmin.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Voltage.enableOverrideVmin(isChecked, getActivity());
                }
            });

            items.add(overrideVmin);
        }

        List<String> freqs = Voltage.getFreqs();
        List<String> voltages = Voltage.getVoltages();
        if (freqs != null && voltages != null && freqs.size() == voltages.size()) {
            for (int i = 0; i < freqs.size(); i++) {
                GenericSelectView view = initView(new GenericSelectView(), freqs.get(i), voltages.get(i));
                mVoltages.add(view);
            }
        }
        items.addAll(mVoltages);
    }

    private void reload() {
        List<String> freqs = Voltage.getFreqs();
        List<String> voltages = Voltage.getVoltages();
        if (freqs != null && voltages != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                initView(mVoltages.get(i), freqs.get(i), voltages.get(i));
            }
        }
    }

    private GenericSelectView initView(GenericSelectView view, final String freq, String voltage) {
        String freqText = Voltage.isVddVoltage() ? String.valueOf(Utils.strToInt(freq) / 1000) : freq;
        view.setSummary(freqText + getString(R.string.mhz));
        view.setValue(voltage + getString(R.string.mv));
        view.setValueRaw(voltage);
        view.setInputType(InputType.TYPE_CLASS_NUMBER);
        view.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                Voltage.setVoltage(freq, value, getActivity());
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reload();
                    }
                }, 200);
            }
        });
        return view;
    }

    public static class GlobalOffsetFragment extends BaseFragment {

        public static GlobalOffsetFragment newInstance(CPUVoltage cpuVoltage) {
            GlobalOffsetFragment fragment = new GlobalOffsetFragment();
            fragment.mCPUVoltage = cpuVoltage;
            return fragment;
        }

        private CPUVoltage mCPUVoltage;
        private int mGlobaloffset;

        @Override
        protected boolean retainInstance() {
            return false;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_global_offset, container, false);
            final TextView offset = (TextView) rootView.findViewById(R.id.offset);
            offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
            rootView.findViewById(R.id.button_minus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGlobaloffset -= 5;
                    offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                    Voltage.setGlobalOffset(-5, getActivity());
                    if (mCPUVoltage != null) {
                        mCPUVoltage.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCPUVoltage.reload();
                            }
                        }, 200);
                    }
                }
            });
            rootView.findViewById(R.id.button_plus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGlobaloffset += 5;
                    offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                    Voltage.setGlobalOffset(5, getActivity());
                    if (mCPUVoltage != null) {
                        mCPUVoltage.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCPUVoltage.reload();
                            }
                        }, 200);
                    }
                }
            });
            return rootView;
        }
    }
}
