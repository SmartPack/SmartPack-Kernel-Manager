package com.smartpack.kernelmanager.views.recyclerview;

public class SwipeableDescriptionView extends DescriptionView {
    // I'm starting to miss Kotlin's high-order functions and extended properties here

    private OnItemSwipedListener onItemSwipedListener;
    private OnItemDragListener onItemDragListener;

    public void setOnItemSwipedListener(OnItemSwipedListener onItemSwipedListener) {
        this.onItemSwipedListener = onItemSwipedListener;
    }

    public OnItemSwipedListener getOnItemSwipedListener() {
        return onItemSwipedListener;
    }

    public void setOnItemDragListener(OnItemDragListener onItemDragListener) {
        this.onItemDragListener = onItemDragListener;
    }

    public OnItemDragListener getOnItemDragListener() {
        return onItemDragListener;
    }
}
