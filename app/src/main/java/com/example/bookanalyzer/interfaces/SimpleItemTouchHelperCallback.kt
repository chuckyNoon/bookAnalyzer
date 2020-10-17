package com.example.bookanalyzer.interfaces

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.ui.adapters.BookItemsAdapter
import kotlinx.android.synthetic.main.item_book.view.*

class SimpleItemTouchHelperCallback(private val presenter: StartScreenPresenter) :
    ItemTouchHelper.Callback() {
    private var lastDraggedViewHolder: ItemTouchHelperViewHolder? = null
    override fun isLongPressDragEnabled(): Boolean {
        return false //need to fix
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        presenter.onBookMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        presenter.onBookDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (lastDraggedViewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            lastDraggedViewHolder?.onItemClear()
        }
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder?
            itemViewHolder?.onItemSelected()
            lastDraggedViewHolder = itemViewHolder
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = viewHolder.itemView.view_foreground

        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val foregroundView: View =
                (viewHolder as BookItemsAdapter.ItemViewHolder).view.view_foreground
            drawBackground(viewHolder, dX, actionState)
            getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View =
            (viewHolder as BookItemsAdapter.ItemViewHolder).view.view_foreground
        drawBackground(viewHolder, dX, actionState)
        getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    private fun drawBackground(viewHolder: RecyclerView.ViewHolder, dX: Float, actionState: Int) {
        val backgroundView: View =
            (viewHolder as BookItemsAdapter.ItemViewHolder).view.view_background
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            backgroundView.left = Math.max(dX, 0f).toInt()
        }
    }
}