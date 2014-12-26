package com.grarak.kerneladiutor.elements;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by willi on 21.12.14.
 */
public class DAdapter {

    public interface DView {

        public void onBindViewHolder(Holder viewHolder);

        public Holder onCreateViewHolder(ViewGroup viewGroup);

    }

    public static class Adapter extends RecyclerView.Adapter<Holder> {

        private final List<DView> DViews;

        public Adapter(List<DView> DViews) {
            this.DViews = DViews;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(Holder viewHolder, int i) {
            DViews.get(i).onBindViewHolder(viewHolder);
        }

        @Override
        public int getItemCount() {
            return DViews.size();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return DViews.get(i).onCreateViewHolder(viewGroup);
        }

    }

}