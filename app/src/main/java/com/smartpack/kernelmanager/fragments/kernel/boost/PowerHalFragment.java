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
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.VoxPopuli;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class PowerHalFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        powerhalTunablesInit(items);
    }

    private void powerhalTunablesInit(List<RecyclerViewItem> items) {
        TitleView powerhal = new TitleView();
        powerhal.setText(getString(R.string.powerhal_tunables));
        items.add(powerhal);

        for (int i = 0; i < VoxPopuli.VoxpopuliTunablesize(); i++) {
            if (VoxPopuli.VoxpopuliTunableexists(i)) {
                GenericSelectView tunables = new GenericSelectView();
                tunables.setSummary(VoxPopuli.getVoxpopuliTunableName(i));
                tunables.setValue(VoxPopuli.getVoxpopuliTunableValue(i));
                tunables.setValueRaw(tunables.getValue());
                tunables.setInputType(InputType.TYPE_CLASS_NUMBER);
                final int position = i;
                tunables.setOnGenericValueListener((genericSelectView, value) -> {
                    VoxPopuli.setVoxpopuliTunableValue(value, position, getActivity());
                    genericSelectView.setValue(value);
                });

                items.add(tunables);
            }
        }
    }

}