package com.noto.app.folder

import android.view.View
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.noto.app.databinding.NoteItemBinding

class NoteItemTouchHelperCallback(
    private val callbackInfo: NoteItemTouchHelperCallbackInfo,
) : EpoxyModelTouchCallback<NoteItem>(callbackInfo.epoxyController, NoteItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = true

    override fun getMovementFlagsForModel(model: NoteItem?, adapterPosition: Int): Int {
        return makeMovementFlags(callbackInfo.dragFlags, 0)
    }

    override fun onDragStarted(model: NoteItem?, itemView: View?, adapterPosition: Int) {
        super.onDragStarted(model, itemView, adapterPosition)
        if (model != null && itemView != null) callbackInfo.onItemSelected(model, NoteItemBinding.bind(itemView))
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: NoteItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        if (modelBeingMoved != null && itemView != null) callbackInfo.onItemMoved(modelBeingMoved, NoteItemBinding.bind(itemView))
    }

    override fun onDragReleased(model: NoteItem?, itemView: View?) {
        super.onDragReleased(model, itemView)
        if (model != null && itemView != null) callbackInfo.onItemReleased(model, NoteItemBinding.bind(itemView))
    }

}