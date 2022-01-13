package com.noto.app.main

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback

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
}