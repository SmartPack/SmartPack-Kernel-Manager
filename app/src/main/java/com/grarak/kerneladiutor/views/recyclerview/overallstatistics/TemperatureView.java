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
package com.grarak.kerneladiutor.views.recyclerview.overallstatistics;

import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.Temperature;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 04.08.16.
 */
public class TemperatureView extends RecyclerViewItem {

    private View mCPUParent;
    private View mGPUParent;

    private TextView mCPU;
    private TextView mGPU;
    private TextView mBattery;

    private double mBatteryTemp;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_temperature_view;
    }

    @Override
    public void onCreateView(View view) {
        mCPUParent = view.findViewById(R.id.cpu_parent);
        mGPUParent = view.findViewById(R.id.gpu_parent);

        mCPU = view.findViewById(R.id.cpu);
        mGPU = view.findViewById(R.id.gpu);
        mBattery = view.findViewById(R.id.battery);

        super.onCreateView(view);
    }

    public void setBattery(double temp) {
        mBatteryTemp = temp;
        refresh();
    }

    @Override
    protected void refresh() {
        if (mCPUParent != null) {
            Temperature temperature = Temperature.getInstance(mCPUParent.getContext());
            if (temperature.hasCPU()) {
                mCPUParent.setVisibility(View.VISIBLE);
                mCPU.setText(temperature.getCPU(mCPU.getContext()));
            } else {
                mCPUParent.setVisibility(View.GONE);
            }

            if (temperature.hasGPU()) {
                mGPUParent.setVisibility(View.VISIBLE);
                mGPU.setText(temperature.getGPU(mGPU.getContext()));
            } else {
                mGPUParent.setVisibility(View.GONE);
            }

            double temp = mBatteryTemp;
            boolean useFahrenheit = Utils.useFahrenheit(mBattery.getContext());
            if (useFahrenheit) temp = Utils.celsiusToFahrenheit(temp);
            mBattery.setText(Utils.roundTo2Decimals(temp) + mBattery.getContext().getString(useFahrenheit ?
                    R.string.fahrenheit : R.string.celsius));
        }
    }
}
