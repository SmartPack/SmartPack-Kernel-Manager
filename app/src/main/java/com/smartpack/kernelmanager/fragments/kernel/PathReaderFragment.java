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

import com.google.android.material.snackbar.Snackbar;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.ApplyOnBootFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.smartpack.kernelmanager.utils.root.Control;
import com.smartpack.kernelmanager.utils.tools.AsyncTasks;
import com.smartpack.kernelmanager.utils.tools.PathReader;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 04.05.16.
 */
public class PathReaderFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(PathReader.getTitle().toUpperCase(), getString(R.string.tunables_title, PathReader.getTitle())));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
    }

    private void reload() {
        getHandler().postDelayed(() -> {
            clearItems();
            new AsyncTasks() {
                private List<RecyclerViewItem> items;

                @Override
                public void onPreExecute() {
                    showProgress();
                    items = new ArrayList<>();
                }

                @Override
                public void doInBackground() {
                    load(items);
                }

                @Override
                public void onPostExecute() {
                    for (RecyclerViewItem item : items) {
                        addItem(item);
                    }
                    hideProgress();
                    if (itemsSize() < 1 && PathReader.getError() != null) {
                        Snackbar.make(getRootView(), PathReader.getError(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }, 250);
    }

    private void load(List<RecyclerViewItem> items) {
        if (PathReader.getPath() == null) return;
        String path = PathReader.getPath();
        if (path.contains("%d")) {
            path = Utils.strFormat(PathReader.getPath(), PathReader.getMin());
        }
        for (final File file : Objects.requireNonNull(SuFile.open(path).listFiles())) {
            final String name = file.getName();
            final String value = Utils.readFile(file.getAbsolutePath());
            if (!value.isEmpty() && !value.contains("\n")) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setTitle(name);
                descriptionView.setSummary(value);
                descriptionView.setOnItemClickListener(item -> {
                    List<Integer> freqs = CPUFreq.getInstance(getActivity()).getFreqs(PathReader.getMin());
                    int freq = Utils.strToInt(value);
                    if (freqs != null && freq != 0 && freqs.contains(freq)) {
                        String[] values = new String[freqs.size()];
                        for (int i = 0; i < values.length; i++) {
                            values[i] = String.valueOf(freqs.get(i));
                        }
                        showArrayDialog(value, values, PathReader.getPath() + "/" + name, name);
                    } else {
                        showEditTextDialog(value, name);
                    }
                });
                items.add(descriptionView);
            }
        }
    }

    private void showArrayDialog(final String value, final String[] values, final String path,
                                 final String name) {
        new Dialog(requireActivity()).setItems(
                getResources().getStringArray(R.array.path_reader_options),
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            new Dialog(requireActivity()).setItems(values, (dialog1, which1) -> {
                                run(path, values[which1], path);
                                reload();
                            }).setTitle(name).show();
                            break;
                        case 1:
                            showEditTextDialog(value, name);
                            break;
                    }
                }).show();
    }

    private void showEditTextDialog(String value, final String name) {
        ViewUtils.dialogEditText(value, (dialog, which) -> {
        }, text -> {
            run(PathReader.getPath() + "/" + name, text, PathReader.getPath() + "/" + name);
            reload();
        }, getActivity()).show();
    }

    private void run(String path, String value, String id) {
        if (ApplyOnBootFragment.CPU.equals(PathReader.getCategory()) && PathReader.getPath().contains("%d")) {
            CPUFreq.getInstance(getActivity()).applyCpu(path, value, PathReader.getMin(), PathReader.getMax(), getActivity());
        } else {
            Control.runSetting(Control.write(value, path), PathReader.getCategory(), id, getActivity());
        }
    }

}