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
package com.grarak.kerneladiutor.fragments.tools.downloads;

import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.downloads.DownloadKernelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.07.16.
 */
public class DownloadKernelFragment extends RecyclerViewFragment {

    public static DownloadKernelFragment newInstance(List<SupportedDownloads.KernelContent.Download> downloads) {
        DownloadKernelFragment fragment = new DownloadKernelFragment();
        fragment.mDownloads = downloads;
        return fragment;
    }

    private List<SupportedDownloads.KernelContent.Download> mDownloads;
    private List<DownloadKernelView> mViews = new ArrayList<>();

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected boolean isForeground() {
        return true;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        for (SupportedDownloads.KernelContent.Download download : mDownloads) {
            DownloadKernelView downloadKernelView = new DownloadKernelView(getActivity(), download);
            mViews.add(downloadKernelView);
            items.add(downloadKernelView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (DownloadKernelView downloadKernelView : mViews) {
            downloadKernelView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (DownloadKernelView downloadKernelView : mViews) {
            downloadKernelView.pause();
        }
    }

}
