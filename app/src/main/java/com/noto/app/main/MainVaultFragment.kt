package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.MainVaultFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.map
import com.noto.app.util.*
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainVaultFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<MainViewModel>()

    private val selectedLibraryId by lazy { navController?.lastLibraryId }

    private var shouldAnimateBlur = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = MainVaultFragmentBinding.inflate(layoutInflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupListeners()
        setupState()
    }

    private fun MainVaultFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = context?.stringResource(R.string.libraries_vault)
    }

    private fun MainVaultFragmentBinding.setupListeners() {
        et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                openVaultUsingPasscode()
                true
            } else {
                false
            }
        }

        btnOpen.setOnClickListener {
            openVaultUsingPasscode()
        }

        btnClose.setOnClickListener {
            activity?.hideKeyboard(et)
            viewModel.closeVault()
            et.setText("")
            til.error = null
            navController?.navigateUp()
        }
    }

    private fun MainVaultFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        combine(viewModel.isVaultOpen, viewModel.isBioAuthEnabled) { isVaultOpen, isBioAuthEnabled ->
            if (!isVaultOpen && isBioAuthEnabled)
                context?.let { context ->
                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(context.stringResource(R.string.open_vault))
                        .setNegativeButtonText(context.stringResource(R.string.use_passcode))
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                        .build()

                    val biometricPrompt = BiometricPrompt(
                        this@MainVaultFragment,
                        ContextCompat.getMainExecutor(requireContext()),
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                openVault()
                            }
                        }
                    )

                    biometricPrompt.authenticate(promptInfo)
                }

        }.launchIn(lifecycleScope)

        viewModel.isVaultOpen
            .onEach { isVaultOpen ->
                if (isVaultOpen) {
                    et.clearFocus()
                    activity?.hideKeyboard(et)
                    btnClose.isVisible = true
                    if (shouldAnimateBlur)
                        blurView.animate()
                            .alpha(0F)
                            .withEndAction {
                                shouldAnimateBlur = false
                                blurView.isVisible = false
                            }
                    else
                        blurView.isVisible = false
                } else {
                    if (shouldAnimateBlur)
                        blurView.animate()
                            .alpha(1F)
                            .withEndAction {
                                shouldAnimateBlur = false
                                blurView.isVisible = true
                            }
                    else
                        blurView.isVisible = true
                    et.requestFocus()
                    activity?.showKeyboard(et)
                    btnClose.isVisible = false
                    fl.post {
                        blurView.setupWith(fl)
                            .setFrameClearDrawable(activity?.window?.decorView?.background)
                            .setBlurAlgorithm(RenderScriptBlur(context))
                            .setBlurRadius(25F)
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
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainVaultFragmentBinding.setupLibraries(state: UiState<List<Pair<Library, Int>>>, isShowNotesCount: Boolean, isVaultOpen: Boolean) {
        if (state is UiState.Success) {
            val libraries = state.value
            rv.withModels {
                context?.let { context ->
                    if (libraries.isEmpty()) {
                        placeholderItem {
                            id("placeholder")
                            placeholder(context.stringResource(R.string.vault_is_empty))
                        }
                    } else {
                        buildLibrariesModels(context, libraries) { libraries ->
                            libraries.forEach { entry ->
                                libraryItem {
                                    id(entry.first.id)
                                    library(entry.first)
                                    notesCount(entry.second)
                                    isManualSorting(false)
                                    isSelected(entry.first.id == selectedLibraryId)
                                    isShowNotesCount(isShowNotesCount)
                                    isClickable(isVaultOpen)
                                    isLongClickable(isVaultOpen)
                                    onClickListener { _ ->
                                        if (entry.first.id != selectedLibraryId)
                                            navController?.navigateSafely(MainVaultFragmentDirections.actionMainVaultFragmentToLibraryFragment(entry.first.id))
                                        dismiss()
                                    }
                                    onLongClickListener { _ ->
                                        navController?.navigateSafely(MainVaultFragmentDirections.actionMainVaultFragmentToLibraryDialogFragment(
                                            entry.first.id))
                                        true
                                    }
                                    onDragHandleTouchListener { _, _ -> false }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun MainVaultFragmentBinding.openVault() {
        activity?.hideKeyboard(et)
        shouldAnimateBlur = true
        viewModel.openVault()
    }

    private fun MainVaultFragmentBinding.openVaultUsingPasscode() {
        val passcode = et.text.toString()
        when {
            passcode.isBlank() -> til.error = context?.stringResource(R.string.passcode_empty_message)
            passcode.hash() == viewModel.vaultPasscode.value -> openVault()
            else -> til.error = context?.stringResource(R.string.invalid_passcode)
        }
    }
}