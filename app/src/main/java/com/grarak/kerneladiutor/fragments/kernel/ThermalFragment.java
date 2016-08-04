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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.cpu.CPUFreq;
import com.grarak.kerneladiutor.utils.kernel.thermal.MSMThermal;
import com.grarak.kerneladiutor.utils.kernel.thermal.Thermald;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 12.05.16.
 */
public class ThermalFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.warning),
                getString(R.string.thermal_info)));
        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.THERMAL));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Thermald.supported()) {
            thermaldInit(items);
        }
        if (MSMThermal.supported()) {
            msmThermalInit(items);
        }
    }

    private void thermaldInit(List<RecyclerViewItem> items) {
        SwitchView thermald = new SwitchView();
        thermald.setTitle(getString(R.string.thermald));
        thermald.setSummary(getString(R.string.thermald_summary));
        thermald.setChecked(Thermald.isThermaldEnabled());
        thermald.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Thermald.enableThermald(isChecked, getActivity());
            }
        });

        items.add(thermald);
    }

    private void msmThermalInit(List<RecyclerViewItem> items) {
        if (MSMThermal.hasIntelliThermalEnable()) {
            SwitchView intelliThermal = new SwitchView();
            intelliThermal.setTitle(getString(R.string.intellithermal));
            intelliThermal.setSummary(getString(R.string.intellithermal_summary));
            intelliThermal.setChecked(MSMThermal.isIntelliThermalEnabled());
            intelliThermal.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableIntelliThermal(isChecked, getActivity());
                }
            });

            items.add(intelliThermal);
        }

        if (MSMThermal.hasIntelliThermalOptimizedEnable()) {
            SwitchView intelliThermalOptimized = new SwitchView();
            intelliThermalOptimized.setTitle(getString(R.string.intellithermal_optimized));
            intelliThermalOptimized.setSummary(getString(R.string.intellithermal_optimized_summary));
            intelliThermalOptimized.setChecked(MSMThermal.isIntelliThermalOptimizedEnabled());
            intelliThermalOptimized.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableIntelliThermalOptimized(isChecked, getActivity());
                }
            });

            items.add(intelliThermalOptimized);
        }

        if (MSMThermal.hasThermalDebugMode()) {
            SwitchView debugMode = new SwitchView();
            debugMode.setTitle(getString(R.string.debug_mask));
            debugMode.setSummary(getString(R.string.thermal_debug_mask_summary));
            debugMode.setChecked(MSMThermal.isThermalDebugModeEnabled());
            debugMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableThermalDebugMode(isChecked, getActivity());
                }
            });

            items.add(debugMode);
        }

        if (MSMThermal.hasCoreControl()) {
            SwitchView coreControl = new SwitchView();
            coreControl.setSummary(getString(R.string.core_control));
            coreControl.setChecked(MSMThermal.isCoreControlEnabled());
            coreControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableCoreControl(isChecked, getActivity());
                }
            });

            items.add(coreControl);
        }

        if (MSMThermal.hasVddRestrictionEnable()) {
            SwitchView vddRestriction = new SwitchView();
            vddRestriction.setSummary(getString(R.string.vdd_restriction));
            vddRestriction.setChecked(MSMThermal.isVddRestrictionEnabled());
            vddRestriction.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableVddRestriction(isChecked, getActivity());
                }
            });

            items.add(vddRestriction);
        }

        if (MSMThermal.hasLimitTempDegC()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 50; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView limitTempDegC = new SeekBarView();
            limitTempDegC.setTitle(getString(R.string.freq_throttle_temp));
            limitTempDegC.setSummary(getString(R.string.freq_throttle_temp_summary));
            limitTempDegC.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            limitTempDegC.setItems(list);
            limitTempDegC.setProgress(MSMThermal.getLimitTempDegC() - 50);
            limitTempDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setLimitTempDegC(position + 50, getActivity());
                }
            });

            items.add(limitTempDegC);
        }

        if (MSMThermal.hasCoreLimitTempDegC()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 50; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView coreLimitTempDegC = new SeekBarView();
            coreLimitTempDegC.setTitle(getString(R.string.cpu_throttle_temp));
            coreLimitTempDegC.setSummary(getString(R.string.cpu_throttle_temp_summary));
            coreLimitTempDegC.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            coreLimitTempDegC.setItems(list);
            coreLimitTempDegC.setProgress(MSMThermal.getCoreLimitTempDegC() - 50);
            coreLimitTempDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setCoreLimitTempDegC(position + 50, getActivity());
                }
            });

            items.add(coreLimitTempDegC);
        }

        if (MSMThermal.hasCoreTempHysteresisDegC()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= 20; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView coreTempHysteresisDegC = new SeekBarView();
            coreTempHysteresisDegC.setTitle(getString(R.string.cpu_temp_hysteresis));
            coreTempHysteresisDegC.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            coreTempHysteresisDegC.setItems(list);
            coreTempHysteresisDegC.setProgress(MSMThermal.getCoreTempHysteresisDegC());
            coreTempHysteresisDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setCoreTempHysteresisDegC(position, getActivity());
                }
            });

            items.add(coreTempHysteresisDegC);
        }

        if (MSMThermal.hasFreqStep()) {
            SeekBarView freqStep = new SeekBarView();
            freqStep.setTitle(getString(R.string.freq_step));
            freqStep.setMax(10);
            freqStep.setMin(1);
            freqStep.setProgress(MSMThermal.getFreqStep() - 1);
            freqStep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setFreqStep(position + 1, getActivity());
                }
            });

            items.add(freqStep);
        }

        if (MSMThermal.hasImmediatelyLimitStop()) {
            SwitchView immediatelyLimitStop = new SwitchView();
            immediatelyLimitStop.setSummary(getString(R.string.immediately_limit_stop));
            immediatelyLimitStop.setChecked(MSMThermal.isImmediatelyLimitStopEnabled());
            immediatelyLimitStop.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableImmediatelyLimitStop(isChecked, getActivity());
                }
            });

            items.add(immediatelyLimitStop);
        }

        if (MSMThermal.hasPollMs()) {
            SeekBarView pollMs = new SeekBarView();
            pollMs.setTitle(getString(R.string.poll));
            pollMs.setUnit(getString(R.string.ms));
            pollMs.setMax(3000);
            pollMs.setOffset(10);
            pollMs.setProgress(MSMThermal.getPollMs() / 10);
            pollMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setPollMs(position * 10, getActivity());
                }
            });

            items.add(pollMs);
        }

        if (MSMThermal.hasTempHysteresisDegC()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= 20; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView tempHysteresisDegC = new SeekBarView();
            tempHysteresisDegC.setTitle(getString(R.string.temp_hysteresis));
            tempHysteresisDegC.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            tempHysteresisDegC.setItems(list);
            tempHysteresisDegC.setProgress(MSMThermal.getTempHysteresisDegC());
            tempHysteresisDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setTempHysteresisDegC(position, getActivity());
                }
            });

            items.add(tempHysteresisDegC);
        }

        if (MSMThermal.hasThermalLimitLow()) {
            SeekBarView limitLow = new SeekBarView();
            limitLow.setTitle(getString(R.string.thermal_limit_low));
            limitLow.setMax(30);
            limitLow.setMin(1);
            limitLow.setProgress(MSMThermal.getThermalLimitLow() - 1);
            limitLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setThermalLimitLow(position + 1, getActivity());
                }
            });

            items.add(limitLow);
        }

        if (MSMThermal.hasThermalLimitHigh()) {
            SeekBarView limitHigh = new SeekBarView();
            limitHigh.setTitle(getString(R.string.thermal_limit_high));
            limitHigh.setMax(30);
            limitHigh.setMin(1);
            limitHigh.setProgress(MSMThermal.getThermalLimitHigh() - 1);
            limitHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setThermalLimitHigh(position + 1, getActivity());
                }
            });

            items.add(limitHigh);
        }

        if (MSMThermal.hasTempSafety()) {
            SwitchView tempSafety = new SwitchView();
            tempSafety.setSummary(getString(R.string.temp_safety));
            tempSafety.setChecked(MSMThermal.isTempSafetyEnabled());
            tempSafety.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableTempSafety(isChecked, getActivity());
                }
            });

            items.add(tempSafety);
        }

        if (MSMThermal.hasTempThrottleEnable()) {
            SwitchView tempThrottle = new SwitchView();
            tempThrottle.setTitle(getString(R.string.temp_throttle));
            tempThrottle.setSummary(getString(R.string.temp_throttle_summary));
            tempThrottle.setChecked(MSMThermal.isTempThrottleEnabled());
            tempThrottle.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableTempThrottle(isChecked, getActivity());
                }
            });

            items.add(tempThrottle);
        }

        if (MSMThermal.hasTempLimit()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());

            SeekBarView tempLimit = new SeekBarView();
            tempLimit.setTitle(getString(R.string.temp_limit));
            tempLimit.setSummary(getString(R.string.temp_limit_summary));
            tempLimit.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            tempLimit.setItems(MSMThermal.getTempLimitList(fahrenheit));
            tempLimit.setProgress(MSMThermal.getCurTempLimit() - MSMThermal.getTempLimitMin());
            tempLimit.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setTempLimit(position + MSMThermal.getTempLimitMin(), getActivity());
                }
            });

            items.add(tempLimit);
        }

        if (MSMThermal.hasFreqLimitDebug()) {
            SwitchView freqLimitDebug = new SwitchView();
            freqLimitDebug.setTitle(getString(R.string.freq_limit_debug));
            freqLimitDebug.setSummary(getString(R.string.freq_limit_debug_summary));
            freqLimitDebug.setChecked(MSMThermal.isFreqLimitDebugEnabled());
            freqLimitDebug.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    MSMThermal.enableFreqLimitDebug(isChecked, getActivity());
                }
            });

            items.add(freqLimitDebug);
        }

        if (MSMThermal.hasMinFreqIndex() && CPUFreq.getFreqs() != null) {
            SelectView minFreqIndex = new SelectView();
            minFreqIndex.setTitle(getString(R.string.temp_limit_min_freq));
            minFreqIndex.setSummary(getString(R.string.temp_limit_min_freq_summary));
            minFreqIndex.setItems(CPUFreq.getAdjustedFreq(getActivity()));
            minFreqIndex.setItem((MSMThermal.getMinFreqIndex() / 1000) + getString(R.string.mhz));
            minFreqIndex.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    MSMThermal.setMinFreqIndex(CPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(minFreqIndex);
        }

        if (MSMThermal.hasAllowedLowLow()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedLowLow = new SeekBarView();
            allowedLowLow.setTitle(getString(R.string.allowed_low_low));
            allowedLowLow.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedLowLow.setItems(list);
            allowedLowLow.setProgress(MSMThermal.getAllowedLowLow() - 40);
            allowedLowLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setAllowedLowLow(position + 40, getActivity());
                }
            });

            items.add(allowedLowLow);
        }

        if (MSMThermal.hasAllowedLowHigh()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedLowHigh = new SeekBarView();
            allowedLowHigh.setTitle(getString(R.string.allowed_low_high));
            allowedLowHigh.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedLowHigh.setItems(list);
            allowedLowHigh.setProgress(MSMThermal.getAllowedLowHigh() - 40);
            allowedLowHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setAllowedLowHigh(position + 40, getActivity());
                }
            });

            items.add(allowedLowHigh);
        }

        if (MSMThermal.hasAllowedLowFreq() && CPUFreq.getFreqs() != null) {
            SelectView allowedLowFreq = new SelectView();
            allowedLowFreq.setSummary(getString(R.string.allowed_low_freq));
            allowedLowFreq.setItems(CPUFreq.getAdjustedFreq(getActivity()));
            allowedLowFreq.setItem((MSMThermal.getAllowedLowFreq() / 1000) + getString(R.string.mhz));
            allowedLowFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    MSMThermal.setAllowedLowFreq(CPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(allowedLowFreq);
        }

        if (MSMThermal.hasAllowedMidLow()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView alloweMidLow = new SeekBarView();
            alloweMidLow.setTitle(getString(R.string.allowed_mid_low));
            alloweMidLow.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            alloweMidLow.setItems(list);
            alloweMidLow.setProgress(MSMThermal.getAllowedMidLow() - 40);
            alloweMidLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setAllowedMidLow(position + 40, getActivity());
                }
            });

            items.add(alloweMidLow);
        }

        if (MSMThermal.hasAllowedMidHigh()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedMidHigh = new SeekBarView();
            allowedMidHigh.setTitle(getString(R.string.allowed_mid_high));
            allowedMidHigh.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedMidHigh.setItems(list);
            allowedMidHigh.setProgress(MSMThermal.getAllowedMidHigh() - 40);
            allowedMidHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setAllowedMidHigh(position + 40, getActivity());
                }
            });

            items.add(allowedMidHigh);
        }

        if (MSMThermal.hasAllowedMidFreq() && CPUFreq.getFreqs() != null) {
            SelectView allowedMidFreq = new SelectView();
            allowedMidFreq.setSummary(getString(R.string.allowed_mid_freq));
            allowedMidFreq.setItems(CPUFreq.getAdjustedFreq(getActivity()));
            allowedMidFreq.setItem((MSMThermal.getAllowedMidFreq() / 1000) + getString(R.string.mhz));
            allowedMidFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    MSMThermal.setAllowedMidFreq(CPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(allowedMidFreq);
        }

        if (MSMThermal.hasAllowedMaxLow()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView alloweMaxLow = new SeekBarView();
            alloweMaxLow.setTitle(getString(R.string.allowed_max_low));
            alloweMaxLow.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            alloweMaxLow.setItems(list);
            alloweMaxLow.setProgress(MSMThermal.getAllowedMaxLow() - 40);
            alloweMaxLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setAllowedMaxLow(position + 40, getActivity());
                }
            });

            items.add(alloweMaxLow);
        }

        if (MSMThermal.hasAllowedMaxHigh()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedMaxHigh = new SeekBarView();
            allowedMaxHigh.setTitle(getString(R.string.allowed_max_high));
            allowedMaxHigh.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedMaxHigh.setItems(list);
            allowedMaxHigh.setProgress(MSMThermal.getAllowedMaxHigh() - 40);
            allowedMaxHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setAllowedMaxHigh(position + 40, getActivity());
                }
            });

            items.add(allowedMaxHigh);
        }

        if (MSMThermal.hasAllowedMaxFreq() && CPUFreq.getFreqs() != null) {
            SelectView allowedMaxFreq = new SelectView();
            allowedMaxFreq.setSummary(getString(R.string.allowed_max_freq));
            allowedMaxFreq.setItems(CPUFreq.getAdjustedFreq(getActivity()));
            allowedMaxFreq.setItem((MSMThermal.getAllowedMaxFreq() / 1000) + getString(R.string.mhz));
            allowedMaxFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    MSMThermal.setAllowedMaxFreq(CPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(allowedMaxFreq);
        }

        if (MSMThermal.hasCheckIntervalMs()) {
            SeekBarView checkIntervalMs = new SeekBarView();
            checkIntervalMs.setTitle(getString(R.string.check_interval));
            checkIntervalMs.setUnit(getString(R.string.ms));
            checkIntervalMs.setMax(3000);
            checkIntervalMs.setOffset(50);
            checkIntervalMs.setProgress(MSMThermal.getCheckIntervalMs() / 50);
            checkIntervalMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setCheckIntervalMs(position * 50, getActivity());
                }
            });

            items.add(checkIntervalMs);
        }

        if (MSMThermal.hasShutdownTemp()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView shutDownTemp = new SeekBarView();
            shutDownTemp.setTitle(getString(R.string.shutdown_temp));
            shutDownTemp.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            shutDownTemp.setItems(list);
            shutDownTemp.setProgress(MSMThermal.getShutdownTemp() - 40);
            shutDownTemp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MSMThermal.setShutdownTemp(position + 40, getActivity());
                }
            });

            items.add(shutDownTemp);
        }

    }

}
