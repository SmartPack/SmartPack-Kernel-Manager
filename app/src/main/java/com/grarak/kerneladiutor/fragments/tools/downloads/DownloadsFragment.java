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

import android.Manifest;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.tools.DownloadsActivity;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.WebpageReader;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.downloads.KernelItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.07.16.
 */
public class DownloadsFragment extends RecyclerViewFragment {

    public static DownloadsFragment newInstance(SupportedDownloads support) {
        DownloadsFragment fragment = new DownloadsFragment();
        fragment.mSupport = support;
        return fragment;
    }

    private SupportedDownloads mSupport;
    private WebpageReader mWebpageReader;
    private final List<WebpageReader> mKernelWebpageReader = new ArrayList<>();
    private Snackbar mErrorBar;

    private SupportedDownloads.KernelContent mKernelContent;

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
    }

    @Override
    public void onViewFinished() {
        super.onViewFinished();
        if (mErrorBar != null) {
            error();
        }
    }

    @Override
    protected boolean needDelay() {
        return false;
    }

    @Override
    protected void postInit() {
        super.postInit();
        if (mWebpageReader == null && mSupport != null) {
            showProgress();
            mWebpageReader = new WebpageReader(getActivity(), new WebpageReader.WebpageListener() {

                private int mKernelCount;

                @Override
                public void onSuccess(String url, String raw, CharSequence html) {
                    if (!isAdded()) return;
                    hideProgress();
                    final SupportedDownloads.Kernels kernels = new SupportedDownloads.Kernels(raw);
                    mKernelWebpageReader.clear();

                    final List<SupportedDownloads.KernelContent> contents = new ArrayList<>();
                    if (kernels.readable()) {
                        for (int i = 0; i < kernels.length(); i++) {
                            WebpageReader reader = new WebpageReader(getActivity(), new WebpageReader.WebpageListener() {
                                @Override
                                public void onSuccess(String url, String raw, CharSequence html) {
                                    if (!isAdded()) return;
                                    mKernelCount++;
                                    SupportedDownloads.KernelContent content = new SupportedDownloads.KernelContent(raw);
                                    if (content.readable()) {
                                        contents.add(content);
                                    }
                                    if (mKernelCount == kernels.length()) {
                                        addViews(contents);
                                    }
                                }

                                @Override
                                public void onFailure(String url) {
                                }
                            });
                            reader.get(kernels.getLink(i));
                            mKernelWebpageReader.add(reader);
                        }
                    } else {
                        error();
                    }
                }

                @Override
                public void onFailure(String url) {
                    error();
                }

                private void addViews(List<SupportedDownloads.KernelContent> contents) {
                    if (contents.size() > 0) {
                        for (final SupportedDownloads.KernelContent content : contents) {
                            String n = content.getName();
                            if (n != null && content.getLogo() != null
                                    && content.getShortDescription() != null
                                    && content.getLongDescription() != null) {
                                KernelItemView kernelItemView = new KernelItemView(content);
                                kernelItemView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                                    @Override
                                    public void onClick(RecyclerViewItem item) {
                                        mKernelContent = content;
                                        requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    }
                                });
                                addItem(kernelItemView);
                            }
                        }
                        hideProgress();
                    } else {
                        error();
                    }
                }
            });
            mWebpageReader.get(mSupport.getLink());
        }
    }

    @Override
    public void onPermissionGranted(int request) {
        super.onPermissionGranted(request);
        if (request == 0 && mKernelContent != null) {
            Intent intent = new Intent(getActivity(), DownloadsActivity.class);
            intent.putExtra(DownloadsActivity.JSON_INTENT, mKernelContent.getJSON());
            startActivity(intent);
            mKernelContent = null;
        }
    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            mKernelContent = null;
        }
    }

    private void error() {
        hideProgress();
        mErrorBar = Snackbar.make(getRootView(), R.string.downloads_error, Snackbar.LENGTH_INDEFINITE);
        mErrorBar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebpageReader != null) {
            mWebpageReader.cancel();
            mWebpageReader = null;
        }
        if (mErrorBar != null) {
            mErrorBar.dismiss();
            mErrorBar = null;
        }
        for (WebpageReader reader : mKernelWebpageReader) {
            reader.cancel();
        }
    }
}
