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
package com.smartpack.kernelmanager.fragments.kernel.boost;

import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.StuneBoost;
import com.smartpack.kernelmanager.views.recyclerview.GenericInputView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class StuneBoostFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (StuneBoost.getParent() != null) {
            stuneBoostInit(items);
        }
        stuneBoostAdvInit(items);
    }

    private void stuneBoostInit(List<RecyclerViewItem> items) {
        TitleView stuneBoost = new TitleView();
        stuneBoost.setText(getString(R.string.stune_boost_sett));
        items.add(stuneBoost);

        if (StuneBoost.hasDynStuneBoost()) {
            SeekBarView dynstuneBoost = new SeekBarView();
            dynstuneBoost.setTitle(getString(R.string.dyn_stune_boost));
            dynstuneBoost.setProgress(StuneBoost.getDynStuneBoost());
            dynstuneBoost.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    StuneBoost.setDynStuneBoost(position, getActivity());
                }
            });

            items.add(dynstuneBoost);

            if (StuneBoost.hasDynStuneBoostDuration()) {
                GenericSelectView stuneBoostDuration = new GenericSelectView();
                stuneBoostDuration.setTitle(getString(R.string.stune_boost_ms) + (" (ms)"));
                stuneBoostDuration.setSummary(("Set ") + getString(R.string.stune_boost_ms));
                stuneBoostDuration.setValue(StuneBoost.getDynStuneBoostDuration());
                stuneBoostDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
                stuneBoostDuration.setOnGenericValueListener((genericSelectView, value) -> {
                    StuneBoost.setDynStuneBoostDuration(value, getActivity());
                    genericSelectView.setValue(value);
                });

                items.add(stuneBoostDuration);
            }
        }
    }
    private void stuneBoostAdvInit(List<RecyclerViewItem> items) {
        TitleView stuneBoostAdv = new TitleView();
        stuneBoostAdv.setText(getString(R.string.stune_boost_sett));
        items.add(stuneBoostAdv);

        for (int i = 0; i < StuneBoost.size(); i++) {
            if (StuneBoost.exists(i)) {
                GenericInputView advStune = new GenericInputView();
                advStune.setTitle(StuneBoost.getName(i));
                advStune.setValue(StuneBoost.getValue(i));
                advStune.setValueRaw(advStune.getValue());
                advStune.setInputType(InputType.TYPE_CLASS_NUMBER);

                final int position = i;
                advStune.setOnGenericValueListener((genericSelectView, value) -> {
                    StuneBoost.setValue(value, position, getActivity());
                    genericSelectView.setValue(value);
                });

                items.add(advStune);
            }
        }
    }

}