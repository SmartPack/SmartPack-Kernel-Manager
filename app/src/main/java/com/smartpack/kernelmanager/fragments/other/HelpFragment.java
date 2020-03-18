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
package com.smartpack.kernelmanager.fragments.other;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 25.07.16.
 */
public class HelpFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<Integer, Integer> sHelps = new LinkedHashMap<>();

    static {
        sHelps.put(R.string.how_different, R.string.how_different_summary);
        sHelps.put(R.string.can_i_use, R.string.can_i_use_summary);
        sHelps.put(R.string.cpu_freq_not_sticking, R.string.cpu_freq_not_sticking_summary);
        sHelps.put(R.string.feature_not_appearing, R.string.feature_not_appearing_summary);
        sHelps.put(R.string.add_new_features, R.string.add_new_features_summary);
        sHelps.put(R.string.what_best, R.string.what_best_summary);
        sHelps.put(R.string.cache_dalvik, R.string.cache_dalvik_summary);
        sHelps.put(R.string.found_bugs, R.string.found_bugs_summary);
        sHelps.put(R.string.can_i_contribute, R.string.can_i_contribute_summary);
    }

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        for (int id : sHelps.keySet()) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setTitle(getString(id));
            descriptionView.setSummary(getString(sHelps.get(id)));

            items.add(descriptionView);
        }
    }

}
