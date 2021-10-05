package com.noto.app.library

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.noto.app.domain.model.LayoutManager

class NoteItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val layoutManager: LayoutManager,
    private val callback: () -> Unit,
) : EpoxyModelTouchCallback<NoteItem>(epoxyController, NoteItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: NoteItem?, adapterPosition: Int): Int {
        val dragFlags = when (layoutManager) {
            LayoutManager.Linear -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
            LayoutManager.Grid -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        }
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: NoteItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        callback()
    }
}