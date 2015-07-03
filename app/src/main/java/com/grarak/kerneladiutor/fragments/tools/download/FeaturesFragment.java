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

import com.grarak.kerneladiutor.elements.cards.download.FeatureCardView;
import com.grarak.kerneladiutor.utils.Downloads;

import java.util.List;

/**
 * Created by willi on 21.06.15.
 */
public class FeaturesFragment extends ParentFragment.DownloadRecyclerViewFragment {

    public static FeaturesFragment newInstance(List<Downloads.Feature> features) {
        FeaturesFragment fragment = new FeaturesFragment();
        fragment.features = features;
        return fragment;
    }

    private List<Downloads.Feature> features;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (features != null) for (Downloads.Feature feature : features)
            addView(new FeatureCardView.DFeatureCard(feature));
    }

}
