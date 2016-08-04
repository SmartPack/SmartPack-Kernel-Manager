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
import com.grarak.kerneladiutor.utils.kernel.io.IO;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;

import java.util.List;

/**
 * Created by willi on 27.06.16.
 */
public class IOFragment extends RecyclerViewFragment {

    private PathReaderFragment mIOTunableFragment;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mIOTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.IO));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        internalStorageInit(items);
        if (IO.hasExternal()) {
            externalStorageInit(items);
        }
    }

    private void internalStorageInit(List<RecyclerViewItem> items) {
        CardView internalCard = new CardView(getActivity());
        internalCard.setTitle(getString(R.string.internal_storage));

        SelectView scheduler = new SelectView();
        scheduler.setTitle(getString(R.string.scheduler));
        scheduler.setSummary(getString(R.string.scheduler_summary));
        scheduler.setItems(IO.getInternalSchedulers());
        scheduler.setItem(IO.getInternalScheduler());
        scheduler.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                IO.setInternalScheduler(item, getActivity());
            }
        });

        internalCard.addItem(scheduler);

        DescriptionView tunable = new DescriptionView();
        tunable.setTitle(getString(R.string.scheduler_tunable));
        tunable.setSummary(getString(R.string.scheduler_tunable_summary));
        tunable.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                showTunables(IO.getInternalScheduler(), IO.getInternalIOSched());
            }
        });

        internalCard.addItem(tunable);

        SeekBarView readahead = new SeekBarView();
        readahead.setTitle(getString(R.string.read_ahead));
        readahead.setSummary(getString(R.string.read_ahead_summary));
        readahead.setUnit(getString(R.string.kb));
        readahead.setMax(8192);
        readahead.setMin(128);
        readahead.setOffset(128);
        readahead.setProgress(IO.getInternalReadahead() / 128 - 1);
        readahead.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                IO.setInternalReadahead((position + 1) * 128, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        internalCard.addItem(readahead);

        items.add(internalCard);
    }

    private void externalStorageInit(List<RecyclerViewItem> items) {
        CardView externalCard = new CardView(getActivity());
        externalCard.setTitle(getString(R.string.external_storage));

        SelectView scheduler = new SelectView();
        scheduler.setTitle(getString(R.string.scheduler));
        scheduler.setSummary(getString(R.string.scheduler_summary));
        scheduler.setItems(IO.getExternalSchedulers());
        scheduler.setItem(IO.getExternalScheduler());
        scheduler.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                IO.setExternalScheduler(item, getActivity());
            }
        });

        externalCard.addItem(scheduler);

        DescriptionView tunable = new DescriptionView();
        tunable.setTitle(getString(R.string.scheduler_tunable));
        tunable.setSummary(getString(R.string.scheduler_tunable_summary));
        tunable.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                showTunables(IO.getExternalScheduler(), IO.getExternalIOSched());
            }
        });

        externalCard.addItem(tunable);

        SeekBarView readahead = new SeekBarView();
        readahead.setTitle(getString(R.string.read_ahead));
        readahead.setSummary(getString(R.string.read_ahead_summary));
        readahead.setUnit(getString(R.string.kb));
        readahead.setMax(8192);
        readahead.setMin(128);
        readahead.setOffset(128);
        readahead.setProgress(IO.getExternalReadahead() / 128 - 1);
        readahead.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                IO.setExternalReadahead((position + 1) * 128, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        externalCard.addItem(readahead);

        items.add(externalCard);
    }

    private void showTunables(String scheduler, String path) {
        setForegroundText(scheduler);
        mIOTunableFragment.setError(getString(R.string.tunables_error, scheduler));
        mIOTunableFragment.setPath(path, ApplyOnBootFragment.IO);
        showForeground();
    }

}
