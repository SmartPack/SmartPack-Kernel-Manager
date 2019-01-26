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
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.gpu.AdrenoIdler;
import com.grarak.kerneladiutor.utils.kernel.gpu.GPUFreq;
import com.grarak.kerneladiutor.utils.kernel.gpu.SimpleGPU;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.XYGraphView;

import com.smartpack.kernelmanager.utils.Adrenoboost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFragment extends RecyclerViewFragment {

    private GPUFreq mGPUFreq;

    private XYGraphView m2dCurFreq;
    private XYGraphView mCurFreq;

    private PathReaderFragment mGPUGovernorTunableFragment;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mGPUGovernorTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mGPUFreq = GPUFreq.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        gpuInit(items);
        if (SimpleGPU.supported()) {
            simpleGpuInit(items);
        }
        if (AdrenoIdler.supported()) {
            adrenoIdlerInit(items);
        }
        if (Adrenoboost.supported()) {
            adrenoboostInit(items);
        }
    }

    private void gpuInit(List<RecyclerViewItem> items) {
        CardView gpuCard = new CardView(getActivity());
        gpuCard.setTitle(getString(R.string.gpu));

        if (mGPUFreq.has2dCurFreq() && mGPUFreq.get2dAvailableFreqs() != null) {
            m2dCurFreq = new XYGraphView();
            m2dCurFreq.setTitle(getString(R.string.gpu_2d_freq));
            gpuCard.addItem(m2dCurFreq);
        }

        if (mGPUFreq.hasCurFreq() && mGPUFreq.getAvailableFreqs() != null) {
            mCurFreq = new XYGraphView();
            mCurFreq.setTitle(getString(R.string.gpu_freq));
            gpuCard.addItem(mCurFreq);
        }

        if (mGPUFreq.has2dMaxFreq() && mGPUFreq.get2dAvailableFreqs() != null) {
            SelectView max2dFreq = new SelectView();
            max2dFreq.setTitle(getString(R.string.gpu_2d_max_freq));
            max2dFreq.setSummary(getString(R.string.gpu_2d_max_freq_summary));
            max2dFreq.setItems(mGPUFreq.get2dAdjustedFreqs(getActivity()));
            max2dFreq.setItem((mGPUFreq.get2dMaxFreq() / 1000000) + getString(R.string.mhz));
            max2dFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mGPUFreq.set2dMaxFreq(mGPUFreq.get2dAvailableFreqs().get(position), getActivity());
                }
            });

            gpuCard.addItem(max2dFreq);
        }

        if (mGPUFreq.hasMaxFreq() && mGPUFreq.getAvailableFreqs() != null) {
            SelectView maxFreq = new SelectView();
            maxFreq.setTitle(getString(R.string.gpu_max_freq));
            maxFreq.setSummary(getString(R.string.gpu_max_freq_summary));
            maxFreq.setItems(mGPUFreq.getAdjustedFreqs(getActivity()));
            maxFreq.setItem((mGPUFreq.getMaxFreq() / mGPUFreq.getMaxFreqOffset()) + getString(R.string.mhz));
            maxFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mGPUFreq.setMaxFreq(mGPUFreq.getAvailableFreqs().get(position), getActivity());
                }
            });

            gpuCard.addItem(maxFreq);
        }

        if (mGPUFreq.hasMinFreq() && mGPUFreq.getAvailableFreqs() != null) {
            SelectView minFreq = new SelectView();
            minFreq.setTitle(getString(R.string.gpu_min_freq));
            minFreq.setSummary(getString(R.string.gpu_min_freq_summary));
            minFreq.setItems(mGPUFreq.getAdjustedFreqs(getActivity()));
            minFreq.setItem((mGPUFreq.getMinFreq() / mGPUFreq.getMinFreqOffset()) + getString(R.string.mhz));
            minFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mGPUFreq.setMinFreq(mGPUFreq.getAvailableFreqs().get(position), getActivity());
                }
            });

            gpuCard.addItem(minFreq);
        }

        if (mGPUFreq.has2dGovernor()) {
            SelectView governor2d = new SelectView();
            governor2d.setTitle(getString(R.string.gpu_2d_governor));
            governor2d.setSummary(getString(R.string.gpu_2d_governor_summary));
            governor2d.setItems(mGPUFreq.get2dAvailableGovernors());
            governor2d.setItem(mGPUFreq.get2dGovernor());
            governor2d.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mGPUFreq.set2dGovernor(item, getActivity());
                }
            });

            gpuCard.addItem(governor2d);
        }

        if (mGPUFreq.hasGovernor()) {
            SelectView governor = new SelectView();
            governor.setTitle(getString(R.string.gpu_governor));
            governor.setSummary(getString(R.string.gpu_governor_summary));
            governor.setItems(mGPUFreq.getAvailableGovernors());
            governor.setItem(mGPUFreq.getGovernor());
            governor.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    mGPUFreq.setGovernor(item, getActivity());
                }
            });

            gpuCard.addItem(governor);

            if (mGPUFreq.hasTunables(governor.getValue())) {
                DescriptionView tunables = new DescriptionView();
                tunables.setTitle(getString(R.string.gpu_governor_tunables));
                tunables.setSummary(getString(R.string.governor_tunables_summary));
                tunables.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        String governor = mGPUFreq.getGovernor();
                        setForegroundText(governor);
                        mGPUGovernorTunableFragment.setError(getString(R.string.tunables_error, governor));
                        mGPUGovernorTunableFragment.setPath(mGPUFreq.getTunables(mGPUFreq.getGovernor()),
                                ApplyOnBootFragment.GPU);
                        showForeground();
                    }
                });

                gpuCard.addItem(tunables);
            }
        }

        if (gpuCard.size() > 0) {
            items.add(gpuCard);
        }
    }

    private void simpleGpuInit(List<RecyclerViewItem> items) {
        CardView simpleGpu = new CardView(getActivity());
        simpleGpu.setTitle(getString(R.string.simple_gpu_algorithm));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.simple_gpu_algorithm_summary));
	enable.setChecked(SimpleGPU.isSimpleGpuEnabled());
	enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
		SimpleGPU.enableSimpleGpu(isChecked, getActivity());
            }
	});

	simpleGpu.addItem(enable);

	SeekBarView laziness = new SeekBarView();
	laziness.setTitle(getString(R.string.laziness));
	laziness.setSummary(getString(R.string.laziness_summary));
	laziness.setMax(10);
	laziness.setProgress(SimpleGPU.getSimpleGpuLaziness());
	laziness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
		SimpleGPU.setSimpleGpuLaziness(position, getActivity());
            }
	});

	simpleGpu.addItem(laziness);

	SeekBarView rampThreshold = new SeekBarView();
	rampThreshold.setTitle(getString(R.string.ramp_thresold));
	rampThreshold.setSummary(getString(R.string.ramp_thresold_summary));
	rampThreshold.setMax(10);
	rampThreshold.setProgress(SimpleGPU.getSimpleGpuRampThreshold());
	rampThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
		SimpleGPU.setSimpleGpuRampThreshold(position, getActivity());
            }
	});

	simpleGpu.addItem(rampThreshold);

        if (simpleGpu.size() > 0) {
            items.add(simpleGpu);
        }
    }

    private void adrenoIdlerInit(List<RecyclerViewItem> items) {
        CardView adrenoIdler = new CardView(getActivity());
        adrenoIdler.setTitle(getString(R.string.adreno_idler));

	SwitchView enable = new SwitchView();
	enable.setSummary(getString(R.string.adreno_idler_summary));
	enable.setChecked(AdrenoIdler.isAdrenoIdlerEnabled());
	enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
		AdrenoIdler.enableAdrenoIdler(isChecked, getActivity());
            }
	});

	adrenoIdler.addItem(enable);

	SeekBarView downDiff = new SeekBarView();
	downDiff.setTitle(getString(R.string.down_differential));
	downDiff.setSummary(getString(R.string.down_differential_summary));
	downDiff.setMax(99);
	downDiff.setProgress(AdrenoIdler.getAdrenoIdlerDownDiff());
	downDiff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
		AdrenoIdler.setAdrenoIdlerDownDiff(position, getActivity());
            }
	});

	adrenoIdler.addItem(downDiff);

	SeekBarView idleWait = new SeekBarView();
	idleWait.setTitle(getString(R.string.idle_wait));
	idleWait.setSummary(getString(R.string.idle_wait_summary));
	idleWait.setMax(99);
	idleWait.setProgress(AdrenoIdler.getAdrenoIdlerIdleWait());
	idleWait.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
		AdrenoIdler.setAdrenoIdlerIdleWait(position, getActivity());
            }
	});

	adrenoIdler.addItem(idleWait);

	SeekBarView idleWorkload = new SeekBarView();
	idleWorkload.setTitle(getString(R.string.workload));
	idleWorkload.setSummary(getString(R.string.workload_summary));
	idleWorkload.setMax(10);
	idleWorkload.setMin(1);
	idleWorkload.setProgress(AdrenoIdler.getAdrenoIdlerIdleWorkload() - 1);
	idleWorkload.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
		AdrenoIdler.setAdrenoIdlerIdleWorkload(position + 1, getActivity());
            }
	});

	adrenoIdler.addItem(idleWorkload);

	if (adrenoIdler.size() > 0) {
            items.add(adrenoIdler);
	}
    }

    private void adrenoboostInit(List<RecyclerViewItem> items) {
        CardView adrenoboost = new CardView(getActivity());
        adrenoboost.setTitle(getString(R.string.adrenoboost));

	if (Adrenoboost.supported()) {
  		 List<String> list = new ArrayList<>();
            list.add("Off");
            list.add("Low");
            list.add("Medium");
            list.add("High");
            SeekBarView boost = new SeekBarView();
            boost.setSummary(getString(R.string.adrenoboost_summary));
            boost.setItems(list);
            boost.setProgress(Adrenoboost.getAdrenoBoost());
            boost.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
                 @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Adrenoboost.setAdrenoBoost(position, getActivity());
                }
            });

	    adrenoboost.addItem(boost);
	}
	if (adrenoboost.size() > 0) {
            items.add(adrenoboost);
	}
    }

    private Integer m2dFreq;
    private List<Integer> m2dFreqs;
    private Integer mBusy;
    private Integer mFreq;
    private List<Integer> mFreqs;

    @Override
    protected void refreshThread() {
        super.refreshThread();

        m2dFreq = mGPUFreq.get2dCurFreq();
        m2dFreqs = mGPUFreq.get2dAvailableFreqs();
        mBusy = mGPUFreq.hasBusy() ? mGPUFreq.getBusy() : null;
        mFreq = mGPUFreq.getCurFreq();
        mFreqs = mGPUFreq.getAvailableFreqs();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (m2dCurFreq != null && m2dFreq != null && m2dFreqs != null) {
            float maxFreq = m2dFreqs.get(m2dFreqs.size() - 1);
            m2dCurFreq.setText((m2dFreq / 1000000) + getString(R.string.mhz));
            float per = (float) m2dFreq / maxFreq * 100f;
            m2dCurFreq.addPercentage(Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }

        if (mCurFreq != null && mFreq != null && mFreqs != null) {
            int load = -1;
            String text = "";
            if (mBusy != null) {
                load = mBusy;
                load = load > 100 ? 100 : load < 0 ? 0 : load;
                text += load + "% - ";
            }

            int freq = mFreq;
            float maxFreq = mFreqs.get(mFreqs.size() - 1);
            text += freq / mGPUFreq.getCurFreqOffset() + getString(R.string.mhz);
            mCurFreq.setText(text);
            float per = (float) freq / maxFreq * 100f;
            mCurFreq.addPercentage(load >= 0 ? load : Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }
    }
}
