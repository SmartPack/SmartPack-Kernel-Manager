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

import android.text.InputType;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.kernel.io.IO;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.GenericSelectView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SeekBarView;
import com.smartpack.kernelmanager.views.recyclerview.SelectView;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 27.06.16.
 */
public class IOFragment extends RecyclerViewFragment {

    private PathReaderFragment mIOTunableFragment;
    private IO mIO;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mIOTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mIO = IO.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        storageInit(IO.Storage.Internal, items);
        if (mIO.hasExternal()) {
            storageInit(IO.Storage.External, items);
        }
        if (mIO.hasDm0()) {
            storageInit(IO.Storage.Dm0, items);
        }
    }

    private void storageInit(final IO.Storage storage, List<RecyclerViewItem> items) {
        CardView StorageCard = new CardView(getActivity());
        if (storage == IO.Storage.Internal) {
            StorageCard.setTitle(getString(R.string.internal_storage));
        } else if (storage == IO.Storage.External) {
            StorageCard.setTitle(getString(R.string.external_storage));
        } else if (storage == IO.Storage.Dm0) {
            StorageCard.setTitle(getString(R.string.dm0));
        }

        if (mIO.hasScheduler(storage)) {
            SelectView scheduler = new SelectView();
            scheduler.setTitle(getString(R.string.scheduler));
            scheduler.setSummary(getString(R.string.scheduler_summary));
            scheduler.setItems(mIO.getSchedulers(storage));
            scheduler.setItem(mIO.getScheduler(storage));
            scheduler.setOnItemSelected((selectView, position, item) -> {
                mIO.setScheduler(storage, item, getActivity());
                getHandler().postDelayed(() -> {
                            scheduler.setItem(mIO.getScheduler(storage));
                        },
                        500);
            });

            StorageCard.addItem(scheduler);

            DescriptionView tunable = new DescriptionView();
            tunable.setTitle(getString(R.string.scheduler_tunable));
            tunable.setSummary(getString(R.string.scheduler_tunable_summary));
            tunable.setOnItemClickListener(item -> showTunables(mIO.getScheduler(storage), mIO.getIOSched(storage)));

            StorageCard.addItem(tunable);
        }

        if (mIO.hasReadahead(storage)) {
            SeekBarView readahead = new SeekBarView();
            readahead.setTitle(getString(R.string.read_ahead));
            readahead.setSummary(getString(R.string.read_ahead_summary));
            readahead.setUnit(getString(R.string.kb));
            readahead.setMax(8192);
            readahead.setMin(64);
            readahead.setOffset(64);
            readahead.setProgress(mIO.getReadahead(storage) / 64 - 1);
            readahead.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIO.setReadahead(storage, (position + 1) * 64, getActivity());
                    getHandler().postDelayed(() -> readahead.setProgress(mIO.getReadahead(storage) / 64 - 1),
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            StorageCard.addItem(readahead);
        }

        if (mIO.hasRotational(storage)) {
            SwitchView rotational = new SwitchView();
            rotational.setTitle(getString(R.string.rotational));
            rotational.setSummary(getString(R.string.rotational_summary));
            rotational.setChecked(mIO.isRotationalEnabled(storage));
            rotational.addOnSwitchListener((switchView, isChecked) -> {
                mIO.enableRotational(storage, isChecked, getActivity());
                getHandler().postDelayed(() -> {
                            rotational.setChecked(mIO.isRotationalEnabled(storage));
                        },
                        500);
            });

            StorageCard.addItem(rotational);
        }

        if (mIO.hasIOStats(storage)) {
            SwitchView iostats = new SwitchView();
            iostats.setTitle(getString(R.string.iostats));
            iostats.setSummary(getString(R.string.iostats_summary));
            iostats.setChecked(mIO.isIOStatsEnabled(storage));
            iostats.addOnSwitchListener((switchView, isChecked) -> {
                mIO.enableIOstats(storage, isChecked, getActivity());
                getHandler().postDelayed(() -> {
                            iostats.setChecked(mIO.isIOStatsEnabled(storage));
                        },
                        500);
            });

            StorageCard.addItem(iostats);
        }

        if (mIO.hasAddRandom(storage)) {
            SwitchView addRandom = new SwitchView();
            addRandom.setTitle(getString(R.string.add_random));
            addRandom.setSummary(getString(R.string.add_random_summary));
            addRandom.setChecked(mIO.isAddRandomEnabled(storage));
            addRandom.addOnSwitchListener((switchView, isChecked) -> {
                mIO.enableAddRandom(storage, isChecked, getActivity());
                getHandler().postDelayed(() -> {
                            addRandom.setChecked(mIO.isAddRandomEnabled(storage));
                        },
                        500);
            });

            StorageCard.addItem(addRandom);
        }

        if (mIO.hasRqAffinity(storage)) {
            SeekBarView rqAffinity = new SeekBarView();
            rqAffinity.setTitle(getString(R.string.rq_affitiny));
            rqAffinity.setSummary(getString(R.string.rq_affinity_summary));
            rqAffinity.setMax(2);
            rqAffinity.setProgress(mIO.getRqAffinity(storage));
            rqAffinity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIO.setRqAffinity(storage, position, getActivity());
                    getHandler().postDelayed(() -> {
                                rqAffinity.setProgress(mIO.getRqAffinity(storage));
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            StorageCard.addItem(rqAffinity);
        }

        if (mIO.hasNomerges(storage)) {
            SeekBarView Nomerges = new SeekBarView();
            Nomerges.setTitle(getString(R.string.nomerges));
            Nomerges.setSummary(getString(R.string.nomerges_summary));
            Nomerges.setMax(2);
            Nomerges.setProgress(mIO.getNomerges(storage));
            Nomerges.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIO.setNomerges(storage, position, getActivity());
                    getHandler().postDelayed(() -> {
                                Nomerges.setProgress(mIO.getNomerges(storage));
                            },
                            500);
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            StorageCard.addItem(Nomerges);
        }

        if (mIO.hasNrRequests(storage)) {
            GenericSelectView NrRequests = new GenericSelectView();
            NrRequests.setTitle(getString(R.string.nr_requests));
            NrRequests.setSummary(getString(R.string.nr_requests_summary));
            NrRequests.setValue(mIO.getNrRequests(storage));
            NrRequests.setValueRaw(NrRequests.getValue());
            NrRequests.setInputType(InputType.TYPE_CLASS_NUMBER);
            NrRequests.setOnGenericValueListener((genericSelectView, value) -> {
                mIO.setNrRequests(storage, value, getActivity());
                genericSelectView.setValue(value);
                getHandler().postDelayed(() -> {
                            NrRequests.setValueRaw(NrRequests.getValue());
                        },
                        500);
            });

            StorageCard.addItem(NrRequests);
        }

        if (StorageCard.size() > 0) {
            items.add(StorageCard);
        }
    }

    private void showTunables(String scheduler, String path) {
        setForegroundText(scheduler);
        mIOTunableFragment.setError(getString(R.string.tunables_error, scheduler));
        mIOTunableFragment.setPath(path, ApplyOnBootFragment.IO);
        showForeground();
    }

}