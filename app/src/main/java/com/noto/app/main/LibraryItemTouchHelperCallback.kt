package com.noto.app.main

import android.os.Build
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.noto.app.util.colorResource
import com.noto.app.util.toResource

class LibraryItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val callback: () -> Unit,
) : EpoxyModelTouchCallback<LibraryItem>(epoxyController, LibraryItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: LibraryItem?, adapterPosition: Int): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: LibraryItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        callback()
    }

    override fun onDragStarted(model: LibraryItem?, itemView: View?, adapterPosition: Int) {
        super.onDragStarted(model, itemView, adapterPosition)
        itemView?.isSelected = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val color = model?.library?.color?.toResource()
            if (color != null) {
                val resource = itemView?.context?.colorResource(color)
                if (resource != null) {
                    itemView.outlineAmbientShadowColor = resource
                    itemView.outlineSpotShadowColor = resource
                }
            }
        }
    }

    override fun onDragReleased(model: LibraryItem?, itemView: View?) {
        super.onDragReleased(model, itemView)
        itemView?.isSelected = false
    }
}