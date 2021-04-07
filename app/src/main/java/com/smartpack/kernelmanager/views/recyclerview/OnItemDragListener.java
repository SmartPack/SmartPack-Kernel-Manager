package com.smartpack.kernelmanager.views.recyclerview;

public interface OnItemDragListener {
    /**
     *
     * @param item RecyclerViewItem in its new position (after move)
     * @param fromPosition initial position
     * @param toPosition final position
     */
    void onItemDrag(RecyclerViewItem item, int fromPosition, int toPosition);
}
