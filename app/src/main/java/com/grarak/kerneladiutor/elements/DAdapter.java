package com.grarak.kerneladiutor.elements;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
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

        private List<RecyclerView.ViewHolder> viewHolders = new ArrayList<>();

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
        }

        @Override
        public int getItemCount() {
            return DViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = DViews.get(viewType).onCreateViewHolder(parent);
            viewHolders.add(viewHolder);
            return viewHolder;
        }

        public List<RecyclerView.ViewHolder> getViewHolders() {
            return viewHolders;
        }

    }

}