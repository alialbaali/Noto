package com.noto.app.folder

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.noto.app.domain.model.Layout

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
}