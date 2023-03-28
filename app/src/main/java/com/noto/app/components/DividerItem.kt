package com.noto.app.components

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.DividerItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.divider_item)
abstract class DividerItem : EpoxyModelWithHolder<DividerItem.Holder>() {

    class Holder : EpoxyHolder() {
        lateinit var binding: DividerItemBinding

        override fun bindView(itemView: View) {
            binding = DividerItemBinding.bind(itemView)
        }
    }
}