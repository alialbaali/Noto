package com.noto.app.util

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LoadingIndicatorItemBinding
import com.noto.app.domain.model.NotoColor

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.loading_indicator_item)
abstract class LoadingIndicatorItem : EpoxyModelWithHolder<LoadingIndicatorItem.Holder>() {

    @EpoxyAttribute
    var color: NotoColor? = null

    override fun bind(holder: Holder) {
        with(holder.binding) {
            color?.toResource()?.let { resource ->
                indicator.setIndicatorColor(root.context.colorResource(resource))
            }
        }
    }

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