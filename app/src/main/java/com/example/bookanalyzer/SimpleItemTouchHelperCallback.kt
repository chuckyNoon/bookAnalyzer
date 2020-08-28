package com.example.bookanalyzer

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.book_list_elem.view.*


/**
 * An implementation of [ItemTouchHelper.Callback] that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br></br>
 *
 * Expects the `RecyclerView.Adapter` to react to [ ] callbacks and the `RecyclerView.ViewHolder` to implement
 * [ItemTouchHelperViewHolder].
 *
 * @author Paul Burke (ipaulpro)
 */
class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder?
            itemViewHolder!!.onItemSelected()
            println("ff")
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val backgroundView: View = viewHolder.itemView.view_background
        val foregroundView: View = viewHolder.itemView.view_foreground

        backgroundView.right = 0

        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
       if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
           val foregroundView: View =
               (viewHolder as RecyclerListAdapter.ItemViewHolder).view.view_foreground
           drawBackground(viewHolder, dX, actionState)
           getDefaultUIUtil().onDraw(
               c, recyclerView, foregroundView, dX, dY,
               actionState, isCurrentlyActive
           )
       }else{
           super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
       }
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as RecyclerListAdapter.ItemViewHolder).view.view_foreground
        drawBackground(viewHolder, dX, actionState)
        getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    private fun drawBackground(viewHolder: RecyclerView.ViewHolder, dX: Float, actionState: Int) {
        val backgroundView: View = (viewHolder as RecyclerListAdapter.ItemViewHolder).view.view_background
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            backgroundView.left = Math.max(dX, 0f).toInt()
        }
    }
}