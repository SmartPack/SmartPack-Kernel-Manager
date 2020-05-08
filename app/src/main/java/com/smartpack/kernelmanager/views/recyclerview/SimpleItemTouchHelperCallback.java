package com.smartpack.kernelmanager.views.recyclerview;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean isSwipeEnabled = false;
    private boolean isDragEnabled = false;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    public void setDragEnabled(boolean dragEnabled) {
        isDragEnabled = dragEnabled;
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        isSwipeEnabled = swipeEnabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isSwipeEnabled;
    }

    @Override
    public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
