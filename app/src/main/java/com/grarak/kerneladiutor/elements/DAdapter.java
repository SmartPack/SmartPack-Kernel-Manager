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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.grarak.cardview.BaseCardView;
import com.grarak.kerneladiutor.R;

import java.util.List;

/**
 * Created by willi on 21.12.14.
 */
public class DAdapter {

    public interface DView {

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder);

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup);

    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<DView> DViews;
        private int count = -1;

        public Adapter(List<DView> DViews) {
            this.DViews = DViews;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DViews.get(position).onBindViewHolder(holder);
            setAnimation(holder.itemView, position);
        }

        @Override
        public int getItemCount() {
            return DViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return DViews.get(viewType).onCreateViewHolder(parent);
        }

        private void setAnimation(View viewToAnimate, int position) {
            if (position > count && viewToAnimate instanceof BaseCardView) {
                viewToAnimate.startAnimation(AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.recyclerview));
                count = position;
            }
        }

    }

}