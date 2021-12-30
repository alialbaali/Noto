package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.MainVaultFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.map
import com.noto.app.util.*
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainVaultFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = MainVaultFragmentBinding.inflate(layoutInflater, container, false).withBinding {
        setupListeners()
        setupState()
    }

    private fun MainVaultFragmentBinding.setupListeners() {
        et.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val passcode = et.text.toString()
                if (passcode == viewModel.vaultPasscode.value)
                    viewModel.openVault()
                else
                    til.error = context?.stringResource(R.string.invalid_passcode)
                activity?.hideKeyboard(et)
                true
            } else {
                false
            }
        }

        val backCallback = {
            viewModel.closeVault()
            activity?.hideKeyboard(et)
        }

        activity?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner) {
                backCallback()
                if (isEnabled) {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }

        tb.setNavigationOnClickListener {
            backCallback()
            navController?.navigateUp()
        }
    }

    private fun MainVaultFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        viewModel.isVaultOpen
            .onEach { isVaultOpen ->
                if (isVaultOpen) {
                    blurView.animate()
                        .alpha(0F)
                        .withEndAction { blurView.isVisible = false }
                    activity?.hideKeyboard(et)
                } else {
                    blurView.animate()
                        .alpha(1F)
                        .withEndAction { blurView.isVisible = true }
                    et.requestFocus()
                    activity?.showKeyboard(et)
                    rv.post {
                        blurView.setupWith(rv)
                            .setFrameClearDrawable(activity?.window?.decorView?.background)
                            .setBlurAlgorithm(RenderScriptBlur(context))
                            .setBlurRadius(10F)
                            .setBlurAutoUpdate(true)
                            .setHasFixedTransformationMatrix(false)
                    }
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.vaultedLibraries,
            viewModel.sortingType,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
            viewModel.isVaultOpen,
        ) { libraries, sortingType, sortingOrder, isShowNotesCount, isVaultOpen ->
            setupLibraries(libraries.map { it.sorted(sortingType, sortingOrder) }, isShowNotesCount, isVaultOpen)
        }
            .launchIn(lifecycleScope)

        viewModel.layout
            .onEach { layout -> setupLayoutManager(layout) }
            .launchIn(lifecycleScope)
    }

    private fun MainVaultFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainVaultFragmentBinding.setupLibraries(state: UiState<List<Pair<Library, Int>>>, isShowNotesCount: Boolean, isVaultOpen: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val libraries = state.value

                rv.withModels {
                    val items = { items: List<Pair<Library, Int>> ->
                        items.forEach { entry ->
                            libraryItem {
                                id(entry.first.id)
                                library(entry.first)
                                notesCount(entry.second)
                                isManualSorting(false)
                                isShowNotesCount(isShowNotesCount)
                                isClickable(isVaultOpen)
                                isLongClickable(isVaultOpen)
                                onClickListener { _ ->
                                    navController?.navigateSafely(MainVaultFragmentDirections.actionMainVaultFragmentToLibraryFragment(entry.first.id))
                                }
                                onLongClickListener { _ ->
                                    navController?.navigateSafely(MainVaultFragmentDirections.actionMainVaultFragmentToLibraryDialogFragment(entry.first.id))
                                    true
                                }
                                onDragHandleTouchListener { _, _ -> false }
                            }
                        }
                    }
                    context?.let { context ->
                        if (libraries.isEmpty()) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.vault_is_empty))
                            }
                        } else {
                            val pinnedLibraries = libraries.filter { it.first.isPinned }
                            val notPinnedLibraries = libraries.filterNot { it.first.isPinned }

                            if (pinnedLibraries.isNotEmpty()) {
                                headerItem {
                                    id("pinned")
                                    title(context.stringResource(R.string.pinned))
                                }

                                items(pinnedLibraries)

                                if (notPinnedLibraries.isNotEmpty())
                                    headerItem {
                                        id("libraries")
                                        title(context.stringResource(R.string.libraries))
                                    }
                            }
                            items(notPinnedLibraries)
                        }
                    }
                }
            }
        }
    }
}