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

    private CPUFreq mCPUFreq;
    private MSMThermal mMSMThermal;

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mMSMThermal = MSMThermal.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.warning),
                getString(R.string.thermal_info)));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Thermald.supported()) {
            thermaldInit(items);
        }
        if (mMSMThermal.supported()) {
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
        if (mMSMThermal.hasIntelliThermalEnable()) {
            SwitchView intelliThermal = new SwitchView();
            intelliThermal.setTitle(getString(R.string.intellithermal));
            intelliThermal.setSummary(getString(R.string.intellithermal_summary));
            intelliThermal.setChecked(mMSMThermal.isIntelliThermalEnabled());
            intelliThermal.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableIntelliThermal(isChecked, getActivity());
                }
            });

            items.add(intelliThermal);
        }

        if (mMSMThermal.hasIntelliThermalOptimizedEnable()) {
            SwitchView intelliThermalOptimized = new SwitchView();
            intelliThermalOptimized.setTitle(getString(R.string.intellithermal_optimized));
            intelliThermalOptimized.setSummary(getString(R.string.intellithermal_optimized_summary));
            intelliThermalOptimized.setChecked(mMSMThermal.isIntelliThermalOptimizedEnabled());
            intelliThermalOptimized.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableIntelliThermalOptimized(isChecked, getActivity());
                }
            });

            items.add(intelliThermalOptimized);
        }

        if (mMSMThermal.hasThermalDebugMode()) {
            SwitchView debugMode = new SwitchView();
            debugMode.setTitle(getString(R.string.debug_mask));
            debugMode.setSummary(getString(R.string.thermal_debug_mask_summary));
            debugMode.setChecked(mMSMThermal.isThermalDebugModeEnabled());
            debugMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableThermalDebugMode(isChecked, getActivity());
                }
            });

            items.add(debugMode);
        }

        if (mMSMThermal.hasCoreControl()) {
            SwitchView coreControl = new SwitchView();
            coreControl.setSummary(getString(R.string.core_control));
            coreControl.setChecked(mMSMThermal.isCoreControlEnabled());
            coreControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableCoreControl(isChecked, getActivity());
                }
            });

            items.add(coreControl);
        }

        if (mMSMThermal.hasVddRestrictionEnable()) {
            SwitchView vddRestriction = new SwitchView();
            vddRestriction.setSummary(getString(R.string.vdd_restriction));
            vddRestriction.setChecked(mMSMThermal.isVddRestrictionEnabled());
            vddRestriction.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableVddRestriction(isChecked, getActivity());
                }
            });

            items.add(vddRestriction);
        }

        if (mMSMThermal.hasLimitTempDegC()) {
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
            limitTempDegC.setProgress(mMSMThermal.getLimitTempDegC() - 50);
            limitTempDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setLimitTempDegC(position + 50, getActivity());
                }
            });

            items.add(limitTempDegC);
        }

        if (mMSMThermal.hasCoreLimitTempDegC()) {
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
            coreLimitTempDegC.setProgress(mMSMThermal.getCoreLimitTempDegC() - 50);
            coreLimitTempDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setCoreLimitTempDegC(position + 50, getActivity());
                }
            });

            items.add(coreLimitTempDegC);
        }

        if (mMSMThermal.hasCoreTempHysteresisDegC()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= 20; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView coreTempHysteresisDegC = new SeekBarView();
            coreTempHysteresisDegC.setTitle(getString(R.string.cpu_temp_hysteresis));
            coreTempHysteresisDegC.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            coreTempHysteresisDegC.setItems(list);
            coreTempHysteresisDegC.setProgress(mMSMThermal.getCoreTempHysteresisDegC());
            coreTempHysteresisDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setCoreTempHysteresisDegC(position, getActivity());
                }
            });

            items.add(coreTempHysteresisDegC);
        }

        if (mMSMThermal.hasFreqStep()) {
            SeekBarView freqStep = new SeekBarView();
            freqStep.setTitle(getString(R.string.freq_step));
            freqStep.setMax(10);
            freqStep.setMin(1);
            freqStep.setProgress(mMSMThermal.getFreqStep() - 1);
            freqStep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setFreqStep(position + 1, getActivity());
                }
            });

            items.add(freqStep);
        }

        if (mMSMThermal.hasImmediatelyLimitStop()) {
            SwitchView immediatelyLimitStop = new SwitchView();
            immediatelyLimitStop.setSummary(getString(R.string.immediately_limit_stop));
            immediatelyLimitStop.setChecked(mMSMThermal.isImmediatelyLimitStopEnabled());
            immediatelyLimitStop.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableImmediatelyLimitStop(isChecked, getActivity());
                }
            });

            items.add(immediatelyLimitStop);
        }

        if (mMSMThermal.hasPollMs()) {
            SeekBarView pollMs = new SeekBarView();
            pollMs.setTitle(getString(R.string.poll));
            pollMs.setUnit(getString(R.string.ms));
            pollMs.setMax(3000);
            pollMs.setOffset(10);
            pollMs.setProgress(mMSMThermal.getPollMs() / 10);
            pollMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setPollMs(position * 10, getActivity());
                }
            });

            items.add(pollMs);
        }

        if (mMSMThermal.hasTempHysteresisDegC()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= 20; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView tempHysteresisDegC = new SeekBarView();
            tempHysteresisDegC.setTitle(getString(R.string.temp_hysteresis));
            tempHysteresisDegC.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            tempHysteresisDegC.setItems(list);
            tempHysteresisDegC.setProgress(mMSMThermal.getTempHysteresisDegC());
            tempHysteresisDegC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setTempHysteresisDegC(position, getActivity());
                }
            });

            items.add(tempHysteresisDegC);
        }

        if (mMSMThermal.hasThermalLimitLow()) {
            SeekBarView limitLow = new SeekBarView();
            limitLow.setTitle(getString(R.string.thermal_limit_low));
            limitLow.setMax(30);
            limitLow.setMin(1);
            limitLow.setProgress(mMSMThermal.getThermalLimitLow() - 1);
            limitLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setThermalLimitLow(position + 1, getActivity());
                }
            });

            items.add(limitLow);
        }

        if (mMSMThermal.hasThermalLimitHigh()) {
            SeekBarView limitHigh = new SeekBarView();
            limitHigh.setTitle(getString(R.string.thermal_limit_high));
            limitHigh.setMax(30);
            limitHigh.setMin(1);
            limitHigh.setProgress(mMSMThermal.getThermalLimitHigh() - 1);
            limitHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setThermalLimitHigh(position + 1, getActivity());
                }
            });

            items.add(limitHigh);
        }

        if (mMSMThermal.hasTempSafety()) {
            SwitchView tempSafety = new SwitchView();
            tempSafety.setSummary(getString(R.string.temp_safety));
            tempSafety.setChecked(mMSMThermal.isTempSafetyEnabled());
            tempSafety.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableTempSafety(isChecked, getActivity());
                }
            });

            items.add(tempSafety);
        }

        if (mMSMThermal.hasTempThrottleEnable()) {
            SwitchView tempThrottle = new SwitchView();
            tempThrottle.setTitle(getString(R.string.temp_throttle));
            tempThrottle.setSummary(getString(R.string.temp_throttle_summary));
            tempThrottle.setChecked(mMSMThermal.isTempThrottleEnabled());
            tempThrottle.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableTempThrottle(isChecked, getActivity());
                }
            });

            items.add(tempThrottle);
        }

        if (mMSMThermal.hasTempLimit()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());

            SeekBarView tempLimit = new SeekBarView();
            tempLimit.setTitle(getString(R.string.temp_limit));
            tempLimit.setSummary(getString(R.string.temp_limit_summary));
            tempLimit.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            tempLimit.setItems(mMSMThermal.getTempLimitList(fahrenheit));
            tempLimit.setProgress(mMSMThermal.getCurTempLimit() - mMSMThermal.getTempLimitMin());
            tempLimit.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setTempLimit(position + mMSMThermal.getTempLimitMin(), getActivity());
                }
            });

            items.add(tempLimit);
        }

        if (mMSMThermal.hasFreqLimitDebug()) {
            SwitchView freqLimitDebug = new SwitchView();
            freqLimitDebug.setTitle(getString(R.string.freq_limit_debug));
            freqLimitDebug.setSummary(getString(R.string.freq_limit_debug_summary));
            freqLimitDebug.setChecked(mMSMThermal.isFreqLimitDebugEnabled());
            freqLimitDebug.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    mMSMThermal.enableFreqLimitDebug(isChecked, getActivity());
                }
            });

            items.add(freqLimitDebug);
        }

        if (mMSMThermal.hasMinFreqIndex() && mCPUFreq.getFreqs() != null) {
            SelectView minFreqIndex = new SelectView();
            minFreqIndex.setTitle(getString(R.string.temp_limit_min_freq));
            minFreqIndex.setSummary(getString(R.string.temp_limit_min_freq_summary));
            minFreqIndex.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            minFreqIndex.setItem((mMSMThermal.getMinFreqIndex() / 1000) + getString(R.string.mhz));
            minFreqIndex.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mMSMThermal.setMinFreqIndex(mCPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(minFreqIndex);
        }

        if (mMSMThermal.hasAllowedLowLow()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedLowLow = new SeekBarView();
            allowedLowLow.setTitle(getString(R.string.allowed_low_low));
            allowedLowLow.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedLowLow.setItems(list);
            allowedLowLow.setProgress(mMSMThermal.getAllowedLowLow() - 40);
            allowedLowLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setAllowedLowLow(position + 40, getActivity());
                }
            });

            items.add(allowedLowLow);
        }

        if (mMSMThermal.hasAllowedLowHigh()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedLowHigh = new SeekBarView();
            allowedLowHigh.setTitle(getString(R.string.allowed_low_high));
            allowedLowHigh.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedLowHigh.setItems(list);
            allowedLowHigh.setProgress(mMSMThermal.getAllowedLowHigh() - 40);
            allowedLowHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setAllowedLowHigh(position + 40, getActivity());
                }
            });

            items.add(allowedLowHigh);
        }

        if (mMSMThermal.hasAllowedLowFreq() && mCPUFreq.getFreqs() != null) {
            SelectView allowedLowFreq = new SelectView();
            allowedLowFreq.setSummary(getString(R.string.allowed_low_freq));
            allowedLowFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            allowedLowFreq.setItem((mMSMThermal.getAllowedLowFreq() / 1000) + getString(R.string.mhz));
            allowedLowFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mMSMThermal.setAllowedLowFreq(mCPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(allowedLowFreq);
        }

        if (mMSMThermal.hasAllowedMidLow()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView alloweMidLow = new SeekBarView();
            alloweMidLow.setTitle(getString(R.string.allowed_mid_low));
            alloweMidLow.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            alloweMidLow.setItems(list);
            alloweMidLow.setProgress(mMSMThermal.getAllowedMidLow() - 40);
            alloweMidLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setAllowedMidLow(position + 40, getActivity());
                }
            });

            items.add(alloweMidLow);
        }

        if (mMSMThermal.hasAllowedMidHigh()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedMidHigh = new SeekBarView();
            allowedMidHigh.setTitle(getString(R.string.allowed_mid_high));
            allowedMidHigh.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedMidHigh.setItems(list);
            allowedMidHigh.setProgress(mMSMThermal.getAllowedMidHigh() - 40);
            allowedMidHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setAllowedMidHigh(position + 40, getActivity());
                }
            });

            items.add(allowedMidHigh);
        }

        if (mMSMThermal.hasAllowedMidFreq() && mCPUFreq.getFreqs() != null) {
            SelectView allowedMidFreq = new SelectView();
            allowedMidFreq.setSummary(getString(R.string.allowed_mid_freq));
            allowedMidFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            allowedMidFreq.setItem((mMSMThermal.getAllowedMidFreq() / 1000) + getString(R.string.mhz));
            allowedMidFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mMSMThermal.setAllowedMidFreq(mCPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(allowedMidFreq);
        }

        if (mMSMThermal.hasAllowedMaxLow()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView alloweMaxLow = new SeekBarView();
            alloweMaxLow.setTitle(getString(R.string.allowed_max_low));
            alloweMaxLow.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            alloweMaxLow.setItems(list);
            alloweMaxLow.setProgress(mMSMThermal.getAllowedMaxLow() - 40);
            alloweMaxLow.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setAllowedMaxLow(position + 40, getActivity());
                }
            });

            items.add(alloweMaxLow);
        }

        if (mMSMThermal.hasAllowedMaxHigh()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView allowedMaxHigh = new SeekBarView();
            allowedMaxHigh.setTitle(getString(R.string.allowed_max_high));
            allowedMaxHigh.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            allowedMaxHigh.setItems(list);
            allowedMaxHigh.setProgress(mMSMThermal.getAllowedMaxHigh() - 40);
            allowedMaxHigh.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setAllowedMaxHigh(position + 40, getActivity());
                }
            });

            items.add(allowedMaxHigh);
        }

        if (mMSMThermal.hasAllowedMaxFreq() && mCPUFreq.getFreqs() != null) {
            SelectView allowedMaxFreq = new SelectView();
            allowedMaxFreq.setSummary(getString(R.string.allowed_max_freq));
            allowedMaxFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            allowedMaxFreq.setItem((mMSMThermal.getAllowedMaxFreq() / 1000) + getString(R.string.mhz));
            allowedMaxFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mMSMThermal.setAllowedMaxFreq(mCPUFreq.getFreqs().get(position), getActivity());
                }
            });

            items.add(allowedMaxFreq);
        }

        if (mMSMThermal.hasCheckIntervalMs()) {
            SeekBarView checkIntervalMs = new SeekBarView();
            checkIntervalMs.setTitle(getString(R.string.check_interval));
            checkIntervalMs.setUnit(getString(R.string.ms));
            checkIntervalMs.setMax(3000);
            checkIntervalMs.setOffset(50);
            checkIntervalMs.setProgress(mMSMThermal.getCheckIntervalMs() / 50);
            checkIntervalMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setCheckIntervalMs(position * 50, getActivity());
                }
            });

            items.add(checkIntervalMs);
        }

        if (mMSMThermal.hasShutdownTemp()) {
            boolean fahrenheit = Utils.useFahrenheit(getActivity());
            List<String> list = new ArrayList<>();
            for (int i = 40; i <= 100; i++) {
                list.add(String.valueOf(Utils.roundTo2Decimals(fahrenheit ? Utils.celsiusToFahrenheit(i) : i)));
            }

            SeekBarView shutDownTemp = new SeekBarView();
            shutDownTemp.setTitle(getString(R.string.shutdown_temp));
            shutDownTemp.setUnit(getString(fahrenheit ? R.string.fahrenheit : R.string.celsius));
            shutDownTemp.setItems(list);
            shutDownTemp.setProgress(mMSMThermal.getShutdownTemp() - 40);
            shutDownTemp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMThermal.setShutdownTemp(position + 40, getActivity());
                }
            });

            items.add(shutDownTemp);
        }

    }

}
