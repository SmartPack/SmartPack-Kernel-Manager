/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.fragments.tools.download;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.cards.download.KernelCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.WebpageReader;
import com.grarak.kerneladiutor.utils.json.Downloads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 20.06.15.
 */
public class DownloadsFragment extends RecyclerViewFragment {

    private static final String LINK_ARG = "link";

    private ProgressBar progressBar;
    private TextView progressText;
    private final List<String> jsons = new ArrayList<>();
    private WebpageReader jsonWebpageReader;
    private final List<WebpageReader> kernelWebpageReaders = new ArrayList<>();

    public static DownloadsFragment newInstance(String link) {
        Bundle args = new Bundle();
        args.putString(LINK_ARG, link);
        DownloadsFragment fragment = new DownloadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean showApplyOnBoot() {
        return false;
    }

    @Override
    public RecyclerView getRecyclerView() {
        View view = getParentView(R.layout.downloads_fragment);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressText = (TextView) view.findViewById(R.id.progresstext);
        return (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void animateRecyclerView() {
    }

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), Utils.isRTL(getActivity())
                        ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        progressText.setText(getString(R.string.downloading_db));
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        jsonWebpageReader = new WebpageReader(new WebpageReader.WebpageCallback() {
            @Override
            public void onCallback(String raw, String html) {
                if (!isAdded()) return;
                final Downloads.Kernels kernels;
                if (raw != null && !raw.isEmpty() && (kernels = new Downloads.Kernels(raw)).readable()) {
                    progressText.setText(getString(R.string.loading_counting, 0, kernels.length()));
                    jsons.clear();
                    kernelWebpageReaders.clear();
                    for (int i = 0; i < kernels.length(); i++) {
                        kernelWebpageReaders.add(new WebpageReader(new WebpageReader.WebpageCallback() {
                            @Override
                            public void onCallback(String raw, String html) {
                                if (!isAdded()) return;
                                jsons.add(raw);

                                progressText.setText(getString(R.string.loading_counting, jsons.size(),
                                        kernels.length()));
                            }
                        }));
                        kernelWebpageReaders.get(i).execute(kernels.getLink(i));
                    }

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            while (true) if (kernels.length() == jsons.size()) return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            List<String> names = new ArrayList<>();
                            List<Downloads.KernelContent> contents = new ArrayList<>();
                            for (String json : jsons) {
                                Downloads.KernelContent kernelContent = new Downloads.KernelContent(json);
                                if (json != null && !json.isEmpty() && kernelContent.readable()) {
                                    names.add(Html.fromHtml(kernelContent.getName()).toString());
                                    contents.add(kernelContent);
                                }
                            }

                            Collections.sort(names);
                            for (String name : names)
                                for (Downloads.KernelContent content : contents) {
                                    String n = content.getName();
                                    if (n != null && name.equals(Html.fromHtml(n).toString())
                                            && content.getLogo() != null
                                            && content.getShortDescription() != null
                                            && content.getLongDescription() != null)
                                        addView(new KernelCardView.DKernelCard(content));
                                }

                            hideProgress();
                        }
                    }.execute();
                } else hideProgress();
            }
        });
    }

    @Override
    public void postInit(Bundle savedInstanceState) {
        super.postInit(savedInstanceState);
        jsonWebpageReader.execute(getArguments().getString(LINK_ARG));
    }

    private void hideProgress() {
        if (getCount() < 1) progressText.setText(getString(R.string.no_kernels));
        else progressText.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        jsonWebpageReader.cancel();
        for (WebpageReader webpageReader : kernelWebpageReaders) webpageReader.cancel();
    }
}
