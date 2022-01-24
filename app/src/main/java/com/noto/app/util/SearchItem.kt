package com.noto.app.util

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.SearchItemBinding
import com.noto.app.domain.model.NotoColor

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.search_item)
abstract class SearchItem : EpoxyModelWithHolder<SearchItem.Holder>() {

    @EpoxyAttribute
    var color: NotoColor? = null

    @EpoxyAttribute
    lateinit var searchTerm: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callback: (String) -> Unit

    override fun bind(holder: Holder) = with(holder.binding) {
        color?.let { color ->
            val colorResource = root.context.colorResource(color.toResource())
            val colorStateList = colorResource.toColorStateList()
            tilSearch.boxBackgroundColor = colorResource.withDefaultAlpha()
            tilSearch.setEndIconTintList(colorStateList)
            etSearch.setHintTextColor(colorStateList)
            etSearch.setTextColor(colorStateList)
        } ?: run {
            val surfaceColor = root.context.attributeColoResource(R.attr.notoSurfaceColor)
            val secondaryColor = root.context.attributeColoResource(R.attr.notoSecondaryColor)
            val primaryColor = root.context.attributeColoResource(R.attr.notoPrimaryColor)
            tilSearch.boxBackgroundColor = surfaceColor
            tilSearch.setEndIconTintList(secondaryColor.toColorStateList())
            etSearch.setHintTextColor(secondaryColor)
            etSearch.setTextColor(primaryColor)
        }
        etSearch.doOnTextChanged { text, _, _, _ ->
            if (text != null)
                callback(text.toString())
        }
        if (!etSearch.isFocused) {
            etSearch.setText(searchTerm)
            etSearch.setSelection(searchTerm.length)
        }
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: SearchItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = SearchItemBinding.bind(itemView)
        }
    }
}