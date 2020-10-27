package com.noto.app

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class BaseItemTouchHelper(private val listener: BaseItemTouchHelperListener) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean = false

    open override fun isItemViewSwipeEnabled(): Boolean = false

    abstract override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        listener.onMoveViewHolder(viewHolder, target)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        listener.onClearViewHolder(viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwipeViewHolder(viewHolder)
        return
    }

}

class LibraryItemTouchHelper(private val listener: BaseItemTouchHelperListener) : BaseItemTouchHelper(listener) {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }
}

class NotoItemTouchHelper(private val listener: BaseItemTouchHelperListener) : BaseItemTouchHelper(listener) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        val color = ResourcesCompat.getColor(recyclerView.resources, R.color.colorAccentTeal, null)

        val itemView = viewHolder.itemView

        val drawable = ResourcesCompat.getDrawable(recyclerView.resources, R.drawable.ic_delete_24px, null)

        val paint = Paint()

        val icon = drawable!!.toBitmap()


        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val width = itemView.height.div(3)

            if (dX > 0) {
                paint.color = color
                val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                c.drawRect(background, paint)
//                val icon_dest = RectF(
//                    itemView.left.toFloat() + width,
//                    itemView.top.toFloat() + width,
//                    itemView.left.toFloat() + 2 * width,
//                    itemView.bottom.toFloat() - width
//                )
//                c.drawBitmap(icon, null, icon_dest, paint)
            } else {
                paint.color = color
                val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                c.drawRect(background, paint)
                val icon_dest = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, icon_dest, paint)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

//
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            val width = viewHolder.itemView.width.toFloat()
//            val alpha = 1.0f - abs(dX) / width
//            viewHolder.itemView.alpha = alpha
//            viewHolder.itemView.translationX = dX
//        } else {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//        }


//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

class BlockItemTouchHelper(private val listener: BaseItemTouchHelperListener) : BaseItemTouchHelper(listener) {

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

}

interface BaseItemTouchHelperListener {

    fun onMoveViewHolder(fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder)

    fun onSwipeViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    fun onClearViewHolder(viewHolder: RecyclerView.ViewHolder)
}