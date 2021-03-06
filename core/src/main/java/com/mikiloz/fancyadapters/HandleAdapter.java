package com.mikiloz.fancyadapters;// Created by Miguel Vera Belmonte on 18/08/2016.

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public abstract class HandleAdapter<T, VH extends HandleAdapter.ViewHolder>
        extends SelectableViewAdapter<T, VH> {

    private boolean actionCancelled = false;    // Whether the element was just dropped

    /**
     * The constructor takes the following parameters:
     *
     * @param items        the items that are going to be represented in a {@link RecyclerView}
     * @param recyclerView a link to the {@link RecyclerView} that is going to be used
     * @param dragFlags the directions in which the item can be dragged
     * @param swipeFlags the directions in which the item can be swiped
     */
    public HandleAdapter(List<T> items, RecyclerView recyclerView, int dragFlags, int swipeFlags) {
        super(items, recyclerView, dragFlags, swipeFlags);
        triggerOnDrop = false;
        selectableViewBehavior = SelectableViewBehavior.IGNORE_CLICK_EVENTS;
    }

    public abstract class ViewHolder extends SelectableViewAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            selectableView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
                        if (!isActionModeEnabled()) {
                            startDrag((VH)ViewHolder.this, getLayoutPosition());
                        }
                    } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                        actionCancelled = false;
                        if (selectableViewBehavior == SelectableViewBehavior.RESPOND_TO_CLICK_EVENTS) {
                            if (isActionModeEnabled()) {
                                toggleItem((VH) ViewHolder.this, getLayoutPosition());
                            } else {
                                triggerSelectionMode((VH) ViewHolder.this, getLayoutPosition());
                            }
                        }
                    } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_CANCEL) {
                        actionCancelled = true;
                    }
                    return true;
                }
            });

            selectedIndicatorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectableViewBehavior == SelectableViewBehavior.RESPOND_TO_CLICK_EVENTS) {
                        toggleItem((VH) ViewHolder.this, getLayoutPosition());
                    }
                }
            });

            itemView.setHapticFeedbackEnabled(true);
        }

        @Override
        public int getSelectableViewID() {
            return getHandleViewID();
        }

        /**
         * This method is meant to return the ID of the handle {@link View}. This View should be
         * contained within the element you're inflating as your {@link HandleAdapter.ViewHolder}.
         * @return the ID of the handle {@link View}
         */
        public abstract int getHandleViewID();

        /**
         * This method should return the resource ID of the {@link View} you'll be swapping your
         * handle into.
         * @return the resource ID of the {@link View} (in the form of R.layout.something)
         */
        public abstract int getSelectedIndicatorResourceID();

    }


}
