package com.noto.app.label

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback

class LabelOrderItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val callback: () -> Unit,
) : EpoxyModelTouchCallback<LabelOrderItem>(epoxyController, LabelOrderItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: LabelOrderItem?, adapterPosition: Int): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: LabelOrderItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        callback()
    }
}