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

package com.grarak.kerneladiutor.elements;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

/**
 * Created by willi on 08.08.15.
 */
public class DDivider extends DParent {

    private View parentView;

    private TextView titleView;
    private View moreView;

    private String titleText;
    private String descriptionText;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        super.onBindViewHolder(viewHolder);

        parentView = viewHolder.itemView;

        titleView = (TextView) parentView.findViewById(R.id.title);
        moreView = parentView.findViewById(R.id.more_view);

        if (titleText != null) titleView.setText(titleText);
        setUpDescription();
        setFullSpan(true);
    }

    public void setText(String text) {
        titleText = text;
        if (titleView != null) titleView.setText(titleText);
    }

    public void setDescription(String description) {
        descriptionText = description;
        setUpDescription();
    }

    private void setUpDescription() {
        if (parentView != null && moreView != null && descriptionText != null) {
            moreView.setVisibility(View.VISIBLE);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(parentView.getContext()).setMessage(descriptionText).show();
                }
            });
        }
    }

    @Override
    public View getView(ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.divider_view, viewGroup, false);
    }

}
