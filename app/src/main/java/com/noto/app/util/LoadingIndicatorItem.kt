package com.noto.app.util

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LoadingIndicatorItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.loading_indicator_item)
abstract class LoadingIndicatorItem : EpoxyModelWithHolder<LoadingIndicatorItem.Holder>() {

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LoadingIndicatorItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LoadingIndicatorItemBinding.bind(itemView)
        }
    }
}