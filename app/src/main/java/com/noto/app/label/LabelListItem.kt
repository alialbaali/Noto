package com.noto.app.label

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LabelListItemBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.setFullSpan

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class LabelListItem : EpoxyModelWithHolder<LabelListItem.Holder>() {

    @EpoxyAttribute
    lateinit var labels: List<LabelItemModel>

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onAllLabelClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onLabelClickListener: (Label) -> Unit

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onLabelLongClickListener: (Label) -> Boolean

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onNewLabelClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.withModels {
            allLabelItem {
                id("all")
                isSelected(labels.none { it.isSelected })
                color(color)
                onClickListener(onAllLabelClickListener)
            }

            labels.forEach { model ->
                labelItem {
                    id(model.label.id)
                    label(model.label)
                    isSelected(model.isSelected)
                    color(color)
                    onClickListener { _ ->
                        onLabelClickListener(model.label)
                    }
                    onLongClickListener { _ ->
                        onLabelLongClickListener(model.label)
                    }
                }
            }

            newLabelItem {
                id("new")
                color(color)
                onClickListener(onNewLabelClickListener)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    override fun getDefaultLayout(): Int = R.layout.label_list_item

    class Holder : EpoxyHolder() {
        lateinit var binding: LabelListItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LabelListItemBinding.bind(itemView)
        }
    }
}