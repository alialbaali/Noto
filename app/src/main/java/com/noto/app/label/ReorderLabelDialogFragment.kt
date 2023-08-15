package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.components.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.ReorderLabelDialogFragmentBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ReorderLabelDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LabelViewModel> { parametersOf(args.folderId, args.labelId) }

    private val args by navArgs<ReorderLabelDialogFragmentArgs>()

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ReorderLabelDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
    }

    private fun ReorderLabelDialogFragmentBinding.setupState() {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        tb.tvDialogTitle.text = context?.stringResource(R.string.labels_order)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toColorResourceId())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.labels,
        ) { folder, labels -> setupLabels(labels, folder.color) }
            .launchIn(lifecycleScope)
    }

    private fun ReorderLabelDialogFragmentBinding.setupLabels(labels: List<Label>, color: NotoColor) {
        rv.withModels {
            setupItemTouchHelper(this)
            labels.forEach { label ->
                labelOrderItem {
                    id(label.id)
                    label(label)
                    color(color)
                    onDragHandleTouchListener { view, event ->
                        if (event.action == MotionEvent.ACTION_DOWN)
                            rv.findContainingViewHolder(view)?.let { viewHolder ->
                                if (this@ReorderLabelDialogFragment::itemTouchHelper.isInitialized)
                                    itemTouchHelper.startDrag(viewHolder)
                            }
                        view.performClick()
                    }
                }
            }
        }
    }

    private fun ReorderLabelDialogFragmentBinding.setupItemTouchHelper(epoxyController: EpoxyController) {
        if (!this@ReorderLabelDialogFragment::itemTouchHelper.isInitialized) {
            val itemTouchHelperCallback = LabelOrderItemTouchHelperCallback(epoxyController) {
                rv.forEach { view ->
                    val viewHolder = rv.findContainingViewHolder(view) as EpoxyViewHolder
                    val model = viewHolder.model as? LabelOrderItem
                    if (model != null) viewModel.updateLabelPosition(model.label, viewHolder.bindingAdapterPosition)
                }
            }
            itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                .apply { attachToRecyclerView(rv) }
        }
    }
}