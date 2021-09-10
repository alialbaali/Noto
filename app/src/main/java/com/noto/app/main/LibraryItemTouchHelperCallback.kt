package com.noto.app.main

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.domain.model.LayoutManager
import com.noto.app.util.colorResource
import com.noto.app.util.dimenResource
import com.noto.app.util.drawableResource
import com.noto.app.util.toResource

class LibraryItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val layoutManager: LayoutManager,
    private val swipeCallback: (EpoxyViewHolder, Int) -> Unit,
    private val dragCallback: () -> Unit,
) : EpoxyModelTouchCallback<LibraryItem>(epoxyController, LibraryItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: LibraryItem?, adapterPosition: Int): Int {
        val dragFlags = when (layoutManager) {
            LayoutManager.Linear -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
            LayoutManager.Grid -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        }
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

//    override fun onSwipeCompleted(model: LibraryItem?, itemView: View?, position: Int, direction: Int) {
//        super.onSwipeCompleted(model, itemView, position, direction)
//        swipeCallback(model, position, direction)
//    }

    override fun onSwiped(viewHolder: EpoxyViewHolder, direction: Int) {
        super.onSwiped(viewHolder, direction)
        swipeCallback(viewHolder, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val view = viewHolder.itemView
        val model = viewHolder.model as LibraryItem
        val resources = recyclerView.rootView.resources
        val archiveDrawable = resources.drawableResource(R.drawable.ic_round_archive_24)!!
        val halfDrawableHeight = archiveDrawable.intrinsicHeight / 2
        val top = view.top + ((view.bottom - view.top) / 2 - halfDrawableHeight)
        val margin = resources.dimenResource(R.dimen.spacing_normal).toInt()
        val radius = resources.dimenResource(R.dimen.spacing_small)
//        val color = ColorUtils.setAlphaComponent(resources.colorResource(model.library.color.toResource()), 100)
        val color = resources.colorResource(model.library.color.toResource())
        val paint = Paint().apply { this.color = color }
        archiveDrawable.setTint(resources.colorResource(R.color.colorBackground))
        val pinDrawable = if (model.library.isPinned)
            resources.drawableResource(R.drawable.ic_round_pin_off_24)!!
        else
            resources.drawableResource(R.drawable.ic_round_pin_24)!!
        pinDrawable.setTint(resources.colorResource(R.color.colorBackground))

        if (dX > 0) {
            c.drawRoundRect(
                RectF(view.left.toFloat(), view.top.toFloat(), (view.right.toFloat() + dX).coerceAtMost(view.right.toFloat()), view.bottom.toFloat()),
                radius,
                radius,
                paint,
            )
            archiveDrawable.setBounds(
                view.left + margin,
                top,
                view.left + margin + archiveDrawable.intrinsicWidth,
                top + archiveDrawable.intrinsicHeight,
            )
            archiveDrawable.draw(c)
        } else if (dX < 0) {
            c.drawRoundRect(
                RectF((view.left.toFloat() + dX).coerceAtLeast(view.left.toFloat()), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat()),
                radius,
                radius,
                paint,
            )
            pinDrawable.setBounds(
                view.right - margin - halfDrawableHeight * 2,
                top,
                view.right - margin,
                top + pinDrawable.intrinsicHeight,
            )
            pinDrawable.draw(c)
        }
    }

    override fun clearView(model: LibraryItem?, itemView: View?) {
        super.clearView(model, itemView)
        dragCallback()
    }
}