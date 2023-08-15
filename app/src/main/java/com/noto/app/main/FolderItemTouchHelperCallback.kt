package com.noto.app.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.util.*

class FolderItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val onSwipe: (EpoxyViewHolder, Int) -> Unit,
    private val onDrag: () -> Unit,
) : EpoxyModelTouchCallback<FolderItem>(epoxyController, FolderItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: FolderItem?, adapterPosition: Int): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = if (model?.folder?.isGeneral == true) 0 else ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onSwiped(viewHolder: EpoxyViewHolder, direction: Int) {
        super.onSwiped(viewHolder, direction)
        onSwipe(viewHolder, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val view = viewHolder.itemView
        val context = view.context
        val model = viewHolder.model as FolderItem
        val color = context.colorResource(model.folder.color.toColorResourceId())
        val margin = context.dimenResource(R.dimen.spacing_normal).toInt()
        val radius = context.dimenResource(R.dimen.spacing_small)
        val paint = Paint().apply { this.color = color.withDefaultAlpha() }
        if (dX > 0) {
            moveInContent(context, color, view, dX, c, radius, paint, margin)
        } else if (dX < 0) {
            moveOutContent(context, color, view, dX, c, radius, paint, margin)
        }
    }

    private fun moveOutContent(
        context: Context,
        color: Int,
        view: View,
        dX: Float,
        c: Canvas,
        radius: Float,
        paint: Paint,
        margin: Int,
    ) {
        context.drawableResource(R.drawable.ic_round_folder_move_out_24)
            ?.apply { setTint(color) }
            ?.let { drawable ->
                val halfDrawableHeight = drawable.intrinsicHeight / 2
                val top = view.top + ((view.bottom - view.top) / 2 - halfDrawableHeight)
                val rectF = RectF(
                    (view.left.toFloat() + dX).coerceAtLeast(view.left.toFloat()),
                    view.top.toFloat(),
                    view.right.toFloat(),
                    view.bottom.toFloat()
                )
                c.drawRoundRect(
                    rectF,
                    radius,
                    radius,
                    paint,
                )
                drawable.setBounds(
                    view.right - margin - halfDrawableHeight * 2,
                    top,
                    view.right - margin,
                    top + drawable.intrinsicHeight,
                )
                drawable.draw(c)
            }
    }

    private fun moveInContent(
        context: Context,
        color: Int,
        view: View,
        dX: Float,
        c: Canvas,
        radius: Float,
        paint: Paint,
        margin: Int,
    ) {
        context.drawableResource(R.drawable.ic_round_folder_move_in_24)
            ?.apply { setTint(color) }
            ?.let { drawable ->
                val halfDrawableHeight = drawable.intrinsicHeight / 2
                val top = view.top + ((view.bottom - view.top) / 2 - halfDrawableHeight)
                val rectF = RectF(
                    view.left.toFloat(),
                    view.top.toFloat(),
                    (view.right.toFloat() + dX).coerceAtMost(view.right.toFloat()),
                    view.bottom.toFloat()
                )
                c.drawRoundRect(
                    rectF,
                    radius,
                    radius,
                    paint,
                )
                drawable.setBounds(
                    view.left + margin,
                    top,
                    view.left + margin + drawable.intrinsicWidth,
                    top + drawable.intrinsicHeight,
                )
                drawable.draw(c)
            }
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: FolderItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        onDrag()
    }

    override fun onDragStarted(model: FolderItem?, itemView: View?, adapterPosition: Int) {
        super.onDragStarted(model, itemView, adapterPosition)
        itemView?.isSelected = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val color = model?.folder?.color?.toColorResourceId()
            if (color != null) {
                val resource = itemView?.context?.colorResource(color)
                if (resource != null) {
                    itemView.outlineAmbientShadowColor = resource
                    itemView.outlineSpotShadowColor = resource
                }
            }
        }
    }

    override fun onDragReleased(model: FolderItem?, itemView: View?) {
        super.onDragReleased(model, itemView)
        itemView?.isSelected = false
    }
}