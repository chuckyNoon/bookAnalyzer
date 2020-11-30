package com.example.bookanalyzer.ui.adapters.book_items_adapter

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.view_models.BooksViewModel
import kotlinx.android.synthetic.main.item_book.view.*

class SimpleItemTouchHelperCallback(private val viewModel: BooksViewModel) :
    ItemTouchHelper.Callback() {

    private var lastDraggedViewHolder: ItemTouchHelperViewHolder? = null

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
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        viewModel.onBookDismiss(viewHolder.adapterPosition)
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
        val foregroundView: View = viewHolder.itemView.foregroundView

        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val foregroundView: View =
                (viewHolder as BooksAdapter.BookHolder).binding.foregroundView
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
            (viewHolder as BooksAdapter.BookHolder).binding.foregroundView
        drawBackground(viewHolder, dX, actionState)
        getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    private fun drawBackground(viewHolder: RecyclerView.ViewHolder, dX: Float, actionState: Int) {
        val backgroundView: View =
            (viewHolder as BooksAdapter.BookHolder).binding.backgroundView.root
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            backgroundView.left = dX.coerceAtLeast(0f).toInt()
        }
    }
}