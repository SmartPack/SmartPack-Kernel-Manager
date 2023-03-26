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
package com.smartpack.kernelmanager.views.recyclerview;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.other.Contributors;

/**
 * Created by willi on 23.07.16.
 */
public class ContributorView extends RecyclerViewItem {

    private final Contributors.Contributor mContributor;

    public ContributorView(Contributors.Contributor contributor) {
        mContributor = contributor;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_contributor_view;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);

        AppCompatImageView image = view.findViewById(R.id.image);
        MaterialTextView name = view.findViewById(R.id.name);
        MaterialTextView contributions = view.findViewById(R.id.contributions);

        ViewUtils.loadImagefromUrl(mContributor.getAvatarUrl(), image).execute();
        name.setText(mContributor.getLogin());
        contributions.setText(view.getResources().getString(R.string.commits, mContributor.getContributions()));

        view.setOnClickListener(v -> Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Manager/commits?author=" + mContributor.getLogin(), (Activity) v.getContext()));
    }

}