/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.fragments.kernel.boost;

import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUBoost;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.kernel.cpu.boost.CPUInputBoost;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 19, 2020
 */

public class CPUBoostFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;
    private CPUBoost mCPUBoost;
    private CPUInputBoost mCPUInputBoost;

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mCPUBoost = CPUBoost.getInstance();
        mCPUInputBoost = CPUInputBoost.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mCPUBoost.supported() || mCPUInputBoost.supported()) {
            cpuBoostInit(items);
        }
    }

    private void cpuBoostInit(List<RecyclerViewItem> items) {
        TitleView cpuBoost = new TitleView();
        cpuBoost.setText(getString(R.string.cpu_boost));
        items.add(cpuBoost);

        if (mCPUBoost.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setSummary(getString(R.string.cpu_boost));
            enable.setChecked(mCPUBoost.isEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> mCPUBoost.enableCpuBoost(isChecked, getActivity()));

            items.add(enable);
        }

        if (mCPUBoost.hasCpuBoostDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mCPUBoost.isCpuBoostDebugMaskEnabled());
            debugMask.addOnSwitchListener((switchView, isChecked) -> mCPUBoost.enableCpuBoostDebugMask(isChecked, getActivity()));

            items.add(debugMask);
        }

        if (mCPUBoost.hasCpuBoostMs()) {
            SeekBarView ms = new SeekBarView();
            ms.setTitle(getString(R.string.interval));
            ms.setSummary(getString(R.string.interval_summary));
            ms.setUnit(" ms");
            ms.setMax(5000);
            ms.setOffset(10);
            ms.setProgress(mCPUBoost.getCpuBootMs() / 10);
            ms.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostMs(position * 10, getActivity());
                    getHandler().postDelayed(() -> ms.setProgress(mCPUBoost.getCpuBootMs() / 10),
                            500);
                }
            });

            items.add(ms);
        }

        if (mCPUBoost.hasCpuBoostSyncThreshold() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SelectView syncThreshold = new SelectView();
            syncThreshold.setTitle(getString(R.string.sync_threshold));
            syncThreshold.setSummary(getString(R.string.sync_threshold_summary));
            syncThreshold.setItems(list);
            syncThreshold.setItem(mCPUBoost.getCpuBootSyncThreshold());
            syncThreshold.setOnItemSelected((selectView, position, item) -> mCPUBoost.setCpuBoostSyncThreshold(position == 0 ? 0 : mCPUFreq.getFreqs().get(position - 1),
                    getActivity()));

            items.add(syncThreshold);
        }

        if (mCPUBoost.hasCpuBoostInputMs()) {
            SeekBarView inputMs = new SeekBarView();
            inputMs.setTitle(getString(R.string.input_interval));
            inputMs.setSummary(getString(R.string.input_interval_summary));
            inputMs.setUnit(" ms");
            inputMs.setMax(5000);
            inputMs.setProgress(mCPUBoost.getCpuBootInputMs());
            inputMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCPUBoost.setCpuBoostInputMs(position, getActivity());
                    getHandler().postDelayed(() -> inputMs.setProgress(mCPUBoost.getCpuBootInputMs()),
                            500);
                }
            });

            items.add(inputMs);
        }

        if (mCPUBoost.hasSchedBoostOnInput()) {
            SwitchView SchedBoostOnInput = new SwitchView();
            SchedBoostOnInput.setSummary(getString(R.string.sched_boost_Input));
            SchedBoostOnInput.setChecked(mCPUBoost.isSchedBoostOnInputEnabled());
            SchedBoostOnInput.addOnSwitchListener((switchView, isChecked) -> {
                mCPUBoost.enableSchedBoostOnInput(isChecked, getActivity());
                getHandler().postDelayed(() -> SchedBoostOnInput.setChecked(mCPUBoost.isSchedBoostOnInputEnabled()),
                        500);
            });

            items.add(SchedBoostOnInput);
        }

        if (CPUBoost.hasCpuTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(CPUBoost.isCpuTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked) -> mCPUBoost.enableCpuTouchBoost(isChecked, getActivity()));

            items.add(touchBoost);
        }

        if (mCPUInputBoost.hascpuinputboost()) {
            SwitchView cpuinputboost = new SwitchView();
            cpuinputboost.setTitle(getString(R.string.cpu_input_boost));
            cpuinputboost.setSummary(getString(R.string.cpu_input_boost_summary));
            cpuinputboost.setChecked(mCPUInputBoost.iscpuinputboostEnabled());
            cpuinputboost.addOnSwitchListener((switchView, isChecked) -> mCPUInputBoost.enablecpuinputboost(isChecked, getActivity()));

            items.add(cpuinputboost);
        }

        if (mCPUInputBoost.hascpuiboostduration()) {
            GenericSelectView cpuiboostduration = new GenericSelectView();
            cpuiboostduration.setTitle(getString(R.string.cpuiboost_duration) + (" (ms)"));
            cpuiboostduration.setSummary(getString(R.string.cpuiboost_duration_summary));
            cpuiboostduration.setValue(mCPUInputBoost.getcpuiboostduration());
            cpuiboostduration.setInputType(InputType.TYPE_CLASS_NUMBER);
            cpuiboostduration.setOnGenericValueListener((genericSelectView, value) -> {
                mCPUInputBoost.setcpuiboostduration(value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> cpuiboostduration.setValue(mCPUInputBoost.getcpuiboostduration()),
                        500);
            });

            items.add(cpuiboostduration);
        }

        if (mCPUInputBoost.haswakeboostduration()) {
            GenericSelectView wakeBoostMS = new GenericSelectView();
            wakeBoostMS.setTitle(getString(R.string.wake_boost_duration) + (" (ms)"));
            wakeBoostMS.setSummary(("Set ") + getString(R.string.wake_boost_duration));
            wakeBoostMS.setValue(mCPUInputBoost.getwakeboostduration());
            wakeBoostMS.setInputType(InputType.TYPE_CLASS_NUMBER);
            wakeBoostMS.setOnGenericValueListener((genericSelectView, value) -> {
                mCPUInputBoost.setwakeboostduration(value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> wakeBoostMS.setValue(mCPUInputBoost.getwakeboostduration()),
                        500);
            });

            items.add(wakeBoostMS);
        }

        if (mCPUInputBoost.hascpuiboostfreq()) {
            GenericSelectView iboostfreq = new GenericSelectView();
            iboostfreq.setTitle(getString(R.string.input_boost_freq) + (" (Hz)"));
            iboostfreq.setSummary(getString(R.string.input_boost_freq_summary));
            iboostfreq.setValue(mCPUInputBoost.getcpuiboostfreq());
            iboostfreq.setInputType(InputType.TYPE_CLASS_NUMBER);
            iboostfreq.setOnGenericValueListener((genericSelectView, value) -> {
                mCPUInputBoost.setcpuiboostfreq(value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> iboostfreq.setValue(mCPUInputBoost.getcpuiboostfreq()),
                        500);
            });

            items.add(iboostfreq);
        }

        if (mCPUInputBoost.hascpuinputboostlf()) {
            SelectView cpuiboostlf = new SelectView();
            cpuiboostlf.setTitle(getString(R.string.input_boost_freq));
            cpuiboostlf.setSummary(getString(R.string.cluster_little));
            cpuiboostlf.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            cpuiboostlf.setItem((mCPUInputBoost.getcpuinputboostlf() / 1000)
                    + getString(R.string.mhz));
            cpuiboostlf.setOnItemSelected((selectView, position, item)
                    -> mCPUInputBoost.setcpuinputboostlf(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));
            getHandler().postDelayed(() -> cpuiboostlf.setItem((mCPUInputBoost.getcpuinputboostlf() / 1000)
                    + getString(R.string.mhz)),
                    500);

            items.add(cpuiboostlf);
        }

        if (mCPUInputBoost.hascpuinputboosthf()) {
            SelectView cpuiboosthf = new SelectView();
            cpuiboosthf.setSummary(getString(R.string.cluster_big));
            cpuiboosthf.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpuiboosthf.setItem((mCPUInputBoost.getcpuinputboosthf() / 1000)
                    + getString(R.string.mhz));
            cpuiboosthf.setOnItemSelected((selectView, position, item)
                    -> mCPUInputBoost.setcpuinputboosthf(mCPUFreq.getFreqs().get(position), getActivity()));
            getHandler().postDelayed(() -> cpuiboosthf.setItem((mCPUInputBoost.getcpuinputboosthf() / 1000)
                    + getString(R.string.mhz)),
                    500);

            items.add(cpuiboosthf);
        }

        if (mCPUInputBoost.hasremoveinputboostlf()) {
            SelectView iboostfreq = new SelectView();
            iboostfreq.setTitle(getString(R.string.cluster_return_freq));
            iboostfreq.setSummary(getString(R.string.cluster_little));
            iboostfreq.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
            iboostfreq.setItem((mCPUInputBoost.getremoveinputboostlf() / 1000)
                    + getString(R.string.mhz));
            iboostfreq.setOnItemSelected((selectView, position, item)
                    -> mCPUInputBoost.setremoveinputboostlf(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));
            getHandler().postDelayed(() -> iboostfreq.setItem((mCPUInputBoost.getremoveinputboostlf() / 1000)
                    + getString(R.string.mhz)),
                    500);

            items.add(iboostfreq);
        }

        if (mCPUInputBoost.hasremoveinputboosthf()) {
            SelectView iboostfreq = new SelectView();
            iboostfreq.setSummary(getString(R.string.cluster_little));
            iboostfreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            iboostfreq.setItem((mCPUInputBoost.getremoveinputboosthf() / 1000)
                    + getString(R.string.mhz));
            iboostfreq.setOnItemSelected((selectView, position, item)
                    -> mCPUInputBoost.setremoveinputboosthf(mCPUFreq.getFreqs().get(position), getActivity()));
            getHandler().postDelayed(() -> iboostfreq.setItem((mCPUInputBoost.getremoveinputboosthf() / 1000)
                    + getString(R.string.mhz)),
                    500);

            items.add(iboostfreq);
        }

        if (mCPUInputBoost.hasinputboostFreq()) {
            SelectView cpuiboostfreq = new SelectView();
            cpuiboostfreq.setTitle(getString(R.string.input_boost_freq));
            cpuiboostfreq.setSummary(getString(R.string.input_boost_freq_summary));
            cpuiboostfreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpuiboostfreq.setItem((mCPUInputBoost.getinputboostFreq() / 1000)
                    + getString(R.string.mhz));
            cpuiboostfreq.setOnItemSelected((selectView, position, item)
                    -> mCPUInputBoost.setinputboostFreq(mCPUFreq.getFreqs().get(position), getActivity()));
            getHandler().postDelayed(() -> cpuiboostfreq.setItem((mCPUInputBoost.getinputboostFreq() / 1000)
                    + getString(R.string.mhz)),
                    500);

            items.add(cpuiboostfreq);
        }

        if (mCPUBoost.hasCpuBoostInputFreq()) {
            List<Integer> freqs = mCPUBoost.getCpuBootInputFreq();
            for (int i = 0; i < freqs.size(); i++) {
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.disabled));
                list.addAll(mCPUFreq.getAdjustedFreq(i, getActivity()));

                SelectView inputCard = new SelectView();
                if (freqs.size() > 1) {
                    inputCard.setTitle(getString(R.string.input_boost_freq_core, i + 1));
                } else {
                    inputCard.setTitle(getString(R.string.input_boost_freq));
                }
                inputCard.setSummary(getString(R.string.input_boost_freq_summary));
                inputCard.setItems(list);
                inputCard.setItem(freqs.get(i));

                final int core = i;
                inputCard.setOnItemSelected((selectView, position, item) -> mCPUBoost.setCpuBoostInputFreq(position == 0 ? 0
                        : mCPUFreq.getFreqs(core).get(position - 1), core, getActivity()));

                items.add(inputCard);
            }
        }

        if (mCPUBoost.hasCpuBoostWakeup()) {
            SwitchView wakeup = new SwitchView();
            wakeup.setTitle(getString(R.string.wakeup_boost));
            wakeup.setSummary(getString(R.string.wakeup_boost_summary));
            wakeup.setChecked(mCPUBoost.isCpuBoostWakeupEnabled());
            wakeup.addOnSwitchListener((switchView, isChecked) -> mCPUBoost.enableCpuBoostWakeup(isChecked, getActivity()));

            items.add(wakeup);
        }

        if (mCPUBoost.hasCpuBoostHotplug()) {
            SwitchView hotplug = new SwitchView();
            hotplug.setTitle(getString(R.string.hotplug_boost));
            hotplug.setSummary(getString(R.string.hotplug_boost_summary));
            hotplug.setChecked(mCPUBoost.isCpuBoostHotplugEnabled());
            hotplug.addOnSwitchListener((switchView, isChecked) -> mCPUBoost.enableCpuBoostHotplug(isChecked, getActivity()));

            items.add(hotplug);
        }
    }

}