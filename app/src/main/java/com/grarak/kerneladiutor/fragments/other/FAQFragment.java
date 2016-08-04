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
package com.grarak.kerneladiutor.fragments.other;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 25.07.16.
 */
public class FAQFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<Integer, Integer> sFAQs = new LinkedHashMap<>();

    static {
        sFAQs.put(R.string.misspelled, R.string.misspelled_summary);
        sFAQs.put(R.string.have_to_donate, R.string.have_to_donate_summary);
        sFAQs.put(R.string.cpu_freq_not_sticking, R.string.cpu_freq_not_sticking_summary);
        sFAQs.put(R.string.feature_not_appearing, R.string.feature_not_appearing_summary);
        sFAQs.put(R.string.feature_function, R.string.feature_function_summary);
        sFAQs.put(R.string.add_new_features, R.string.add_new_features_summary);
    }

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        for (int id : sFAQs.keySet()) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setTitle(getString(id));
            descriptionView.setSummary(getString(sFAQs.get(id)));

            items.add(descriptionView);
        }
    }

}
