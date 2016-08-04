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
package com.grarak.kerneladiutor.views.recyclerview.downloads;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.SupportedDownloads;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 06.07.16.
 */
public class KernelItemView extends RecyclerViewItem {

    private final SupportedDownloads.KernelContent mKernelContent;

    public KernelItemView(SupportedDownloads.KernelContent content) {
        mKernelContent = content;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_kernel_item_view;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);

        final ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView summary = (TextView) view.findViewById(R.id.summary);

        Utils.loadImagefromUrl(mKernelContent.getLogo(), icon, 300, 300);

        title.setText(Utils.htmlFrom(mKernelContent.getName()).toString());
        summary.setText(Utils.htmlFrom(mKernelContent.getShortDescription()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onClick(KernelItemView.this);
                }
            }
        });

        if (Utils.DARK_THEME) {
            title.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }
    }

}
