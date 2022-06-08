package com.noto.app.folder

import android.os.Build
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.noto.app.domain.model.Layout
import com.noto.app.util.colorResource
import com.noto.app.util.toResource

class NoteItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val layout: Layout,
    private val callback: () -> Unit,
) : EpoxyModelTouchCallback<NoteItem>(epoxyController, NoteItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: NoteItem?, adapterPosition: Int): Int {
        val dragFlags = when (layout) {
            Layout.Linear -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
            Layout.Grid -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        }
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: NoteItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        callback()
    }

    override fun onDragStarted(model: NoteItem?, itemView: View?, adapterPosition: Int) {
        super.onDragStarted(model, itemView, adapterPosition)
        itemView?.isSelected = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val color = model?.color?.toResource()
            if (color != null) {
                val resource = itemView?.context?.colorResource(color)
                if (resource != null) {
                    itemView.outlineAmbientShadowColor = resource
                    itemView.outlineSpotShadowColor = resource
                }
            }
        }
    }

    override fun onDragReleased(model: NoteItem?, itemView: View?) {
        super.onDragReleased(model, itemView)
        itemView?.isSelected = false
    }
}