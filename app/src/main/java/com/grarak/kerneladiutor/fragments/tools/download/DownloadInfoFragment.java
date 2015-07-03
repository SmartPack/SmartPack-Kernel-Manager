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

import android.os.Bundle;

import com.grarak.kerneladiutor.elements.cards.download.DownloadInfoCardView;
import com.grarak.kerneladiutor.utils.Downloads;

/**
 * Created by willi on 01.07.15.
 */
public class DownloadInfoFragment extends ParentFragment.DownloadRecyclerViewFragment {

    public static DownloadInfoFragment newInstance(Downloads.KernelContent kernelContent) {
        DownloadInfoFragment fragment = new DownloadInfoFragment();
        fragment.kernelContent = kernelContent;
        return fragment;
    }

    private Downloads.KernelContent kernelContent;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (kernelContent != null && kernelContent.getShortDescription() != null
                && kernelContent.getLongDescription() != null)
            addView(new DownloadInfoCardView.DDDownloadInfoCard(kernelContent));
    }

}
