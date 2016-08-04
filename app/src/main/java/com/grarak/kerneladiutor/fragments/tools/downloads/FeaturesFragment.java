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

import android.text.method.LinkMovementMethod;

import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.List;

/**
 * Created by willi on 07.07.16.
 */
public class FeaturesFragment extends RecyclerViewFragment {

    public static FeaturesFragment newInstance(List<SupportedDownloads.KernelContent.Feature> features) {
        FeaturesFragment fragment = new FeaturesFragment();
        fragment.mFeatures = features;
        return fragment;
    }

    private List<SupportedDownloads.KernelContent.Feature> mFeatures;

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
        for (SupportedDownloads.KernelContent.Feature feature : mFeatures) {
            DescriptionView descriptionView = new DescriptionView();

            if (feature.hasItems()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String subFeature : feature.getItems()) {
                    if (stringBuilder.length() == 0) {
                        stringBuilder.append("\u2022").append(" ").append(subFeature);
                    } else {
                        stringBuilder.append("<br>").append("\u2022").append(" ").append(subFeature);
                    }
                }
                descriptionView.setTitle(Utils.htmlFrom(feature.getItem()));
                descriptionView.setSummary(Utils.htmlFrom(stringBuilder.toString()));
            } else {
                descriptionView.setSummary(Utils.htmlFrom(feature.getItem()));
            }
            descriptionView.setMovementMethod(LinkMovementMethod.getInstance());

            items.add(descriptionView);
        }
    }

}
