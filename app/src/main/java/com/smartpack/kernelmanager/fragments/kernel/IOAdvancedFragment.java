/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.fragments.kernel;

import android.content.Intent;
import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.TunablesActivity;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.io.IOAdvanced;
import com.smartpack.kernelmanager.utils.tools.AsyncTasks;
import com.smartpack.kernelmanager.utils.tools.PathReader;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 14, 2021
 */
public class IOAdvancedFragment extends RecyclerViewFragment {

    private IOAdvanced mIOAdvanced;

    private AsyncTasks mLoader;

    @Override
    protected void init() {
        super.init();

        mIOAdvanced = IOAdvanced.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(() -> {
                clearItems();
                mLoader = new AsyncTasks() {
                    private List<RecyclerViewItem> items;

                    @Override
                    public void onPreExecute() {
                        showProgress();
                    }

                    @Override
                    public void doInBackground() {
                        items = new ArrayList<>();
                        if (IOAdvanced.getIOBlockList().size() > 0) {
                            IOBlocksInit(items);
                        }
                    }

                    @Override
                    public void onPostExecute() {
                        for (RecyclerViewItem item : items) {
                            addItem(item);
                        }
                        hideProgress();
                        mLoader = null;
                    }
                };
                mLoader.execute();
            }, 250);
        }
    }

    private void IOBlocksInit(List<RecyclerViewItem> items) {
        CardView blocksCard = new CardView(getActivity());
        blocksCard.setTitle(getString(R.string.io_blocks, IOAdvanced.getCurrentBlock(requireActivity())));

        SelectView ioBlocks = new SelectView();
        ioBlocks.setTitle(getString(R.string.io_blocks_select));
        ioBlocks.setSummary(getString(R.string.io_blocks_select_summary));
        ioBlocks.setItems(IOAdvanced.getIOBlockList());
        ioBlocks.setItem(IOAdvanced.getCurrentBlock(requireActivity()));
        ioBlocks.setOnItemSelected((selectView, position, item) -> {
            if (!IOAdvanced.getCurrentBlock(requireActivity()).equals(IOAdvanced.getIOBlockList().get(position))) {
                IOAdvanced.setCurrentBlock(position, requireActivity());
                reload();
            }
        });

        blocksCard.addItem(ioBlocks);

        if (IOAdvanced.hasScheduler(requireActivity()) && !IOAdvanced.getScheduler(requireActivity()).equals("")) {
            SelectView scheduler = new SelectView();
            scheduler.setSummary(getString(R.string.scheduler));
            scheduler.setItems(IOAdvanced.getSchedulers(requireActivity()));
            scheduler.setItem(IOAdvanced.getScheduler(requireActivity()));
            scheduler.setOnItemSelected((selectView, position, item) -> {
                mIOAdvanced.setScheduler(item, getActivity());
                getHandler().postDelayed(() -> scheduler.setItem(IOAdvanced.getScheduler(requireActivity())),500);
            });

            blocksCard.addItem(scheduler);

            DescriptionView tunable = new DescriptionView();
            tunable.setSummary(getString(R.string.scheduler_tunable));
            tunable.setOnItemClickListener(item -> {
                PathReader.setTitle(IOAdvanced.getScheduler(requireActivity()));
                PathReader.setError(getString(R.string.tunables_error, IOAdvanced.getScheduler(requireActivity())));
                PathReader.setPath(IOAdvanced.getSchedulerPath(requireActivity()));
                PathReader.setCategory(ApplyOnBootFragment.IO);
                PathReader.setMax(-1);
                PathReader.setMin(-1);
                Intent intent = new Intent(requireActivity(), TunablesActivity.class);
                startActivity(intent);
            });

            blocksCard.addItem(tunable);
        }

        if (IOAdvanced.hasReadAhead(requireActivity())) {
            SeekBarView readahead = new SeekBarView();
            readahead.setSummary(getString(R.string.read_ahead));
            readahead.setUnit(getString(R.string.kb));
            readahead.setMax(8192);
            readahead.setMin(64);
            readahead.setOffset(64);
            readahead.setProgress(IOAdvanced.getReadAhead(requireActivity()) / 64 - 1);
            readahead.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIOAdvanced.setReadAhead((position + 1) * 64, getActivity());
                    getHandler().postDelayed(() -> readahead.setProgress(IOAdvanced.getReadAhead(requireActivity()) / 64 - 1),
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            blocksCard.addItem(readahead);
        }

        if (IOAdvanced.hasRotational(requireActivity())) {
            SwitchView rotational = new SwitchView();
            rotational.setSummary(getString(R.string.rotational));
            rotational.setChecked(IOAdvanced.isRotationalEnabled(requireActivity()));
            rotational.addOnSwitchListener((switchView, isChecked) -> {
                mIOAdvanced.enableRotational(isChecked, getActivity());
                getHandler().postDelayed(() -> rotational.setChecked(IOAdvanced.isRotationalEnabled(requireActivity())),500);
            });

            blocksCard.addItem(rotational);
        }

        if (IOAdvanced.hasIOStats(requireActivity())) {
            SwitchView iostats = new SwitchView();
            iostats.setSummary(getString(R.string.iostats));
            iostats.setChecked(IOAdvanced.isIOStatsEnabled(requireActivity()));
            iostats.addOnSwitchListener((switchView, isChecked) -> {
                mIOAdvanced.enableIOStats(isChecked, getActivity());
                getHandler().postDelayed(() -> iostats.setChecked(IOAdvanced.isIOStatsEnabled(requireActivity())),500);
            });

            blocksCard.addItem(iostats);
        }

        if (IOAdvanced.hasRandom(requireActivity())) {
            SwitchView addRandom = new SwitchView();
            addRandom.setSummary(getString(R.string.add_random));
            addRandom.setChecked(IOAdvanced.isRandomEnabled(requireActivity()));
            addRandom.addOnSwitchListener((switchView, isChecked) -> {
                mIOAdvanced.enableRandom(isChecked, getActivity());
                getHandler().postDelayed(() -> addRandom.setChecked(IOAdvanced.isRandomEnabled(requireActivity())),500);
            });

            blocksCard.addItem(addRandom);
        }

        if (IOAdvanced.hasAffinity(requireActivity())) {
            SeekBarView rqAffinity = new SeekBarView();
            rqAffinity.setSummary(getString(R.string.rq_affitiny));
            rqAffinity.setMax(2);
            rqAffinity.setProgress(IOAdvanced.getAffinity(requireActivity()));
            rqAffinity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIOAdvanced.setAffinity(position, getActivity());
                    getHandler().postDelayed(() -> rqAffinity.setProgress(IOAdvanced.getAffinity(requireActivity())),500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            blocksCard.addItem(rqAffinity);
        }

        if (IOAdvanced.hasNoMerges(requireActivity())) {
            SeekBarView Nomerges = new SeekBarView();
            Nomerges.setSummary(getString(R.string.nomerges));
            Nomerges.setMax(2);
            Nomerges.setProgress(IOAdvanced.getNoMerges(requireActivity()));
            Nomerges.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIOAdvanced.setNoMerges(position, getActivity());
                    getHandler().postDelayed(() -> Nomerges.setProgress(IOAdvanced.getNoMerges(requireActivity())),500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            blocksCard.addItem(Nomerges);
        }

        if (IOAdvanced.hasNRRequests(requireActivity())) {
            GenericSelectView NrRequests = new GenericSelectView();
            NrRequests.setSummary(getString(R.string.nr_requests));
            NrRequests.setValue(IOAdvanced.getNRRequests(requireActivity()));
            NrRequests.setValueRaw(NrRequests.getValue());
            NrRequests.setInputType(InputType.TYPE_CLASS_NUMBER);
            NrRequests.setOnGenericValueListener((genericSelectView, value) -> {
                mIOAdvanced.setNRRequests(value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> NrRequests.setValueRaw(NrRequests.getValue()),500);
            });

            blocksCard.addItem(NrRequests);
        }

        items.add(blocksCard);
    }

}