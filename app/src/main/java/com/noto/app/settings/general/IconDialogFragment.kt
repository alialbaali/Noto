package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.noto.app.components.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.IconDialogFragmentBinding
import com.noto.app.domain.model.Icon
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class IconDialogFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = IconDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.icon)
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = GridLayoutManager(context, 2)
        rv.itemAnimator = VerticalListItemAnimator()
        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.ll.isSelected = isScrolling }
            .launchIn(lifecycleScope)

        viewModel.icon
            .onEach { selectedIcon ->
                rv.withModels {
                    Icon.entries.forEach { icon ->
                        iconItem {
                            id(icon.name)
                            icon(icon)
                            isSelected(icon == selectedIcon)
                            onClickListener { _ ->
                                viewModel.updateIcon(icon)
                                dismiss()
                            }
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)
    }
}