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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;

/**
 * Created by willi on 15.08.15.
 */
public abstract class DParent implements DAdapter.DView {

    private boolean fullspan;
    private View view;

    @Override
    public BaseFragment getFragment() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        view = viewHolder.itemView;
        setUpLayout();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new RecyclerView.ViewHolder(getView(viewGroup)) {
        };
    }

    public abstract View getView(ViewGroup viewGroup);

    public void setFullSpan(boolean fullspan) {
        this.fullspan = fullspan;
        setUpLayout();
    }

    private void setUpLayout() {
        if (fullspan && view != null) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            int padding = view.getContext().getResources().getDimensionPixelSize(R.dimen.basecard_padding);
            layoutParams.setMargins(padding, padding, padding, 0);
            view.setLayoutParams(layoutParams);
        }
    }

}
