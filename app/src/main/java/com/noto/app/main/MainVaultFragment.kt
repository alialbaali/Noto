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
import com.noto.app.domain.model.Folder
import com.noto.app.util.*
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainVaultFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<MainViewModel>()

    private val selectedDestinationId by lazy { navController?.lastDestinationId }

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
        tvDialogTitle.text = context?.stringResource(R.string.folders_vault)
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

        btnOpenUsingPasscode.setOnClickListener {
            openVaultUsingPasscode()
        }

        btnOpenUsingBio.setOnClickListener {
            openVaultUsingBio()
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
        rv.itemAnimator = VerticalListItemAnimator()

        combine(viewModel.isVaultOpen, viewModel.isBioAuthEnabled) { isVaultOpen, isBioAuthEnabled ->
            if (isBioAuthEnabled) {
                btnOpenUsingPasscode.text = context?.stringResource(R.string.open_vault_using_passcode)
                btnOpenUsingBio.isVisible = true
            } else {
                btnOpenUsingPasscode.text = context?.stringResource(R.string.open_vault)
                btnOpenUsingBio.isVisible = false
            }
            if (!isVaultOpen && isBioAuthEnabled)
                openVaultUsingBio()
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
            viewModel.vaultedFolders,
            viewModel.isShowNotesCount,
            viewModel.isVaultOpen,
        ) { folders, isShowNotesCount, isVaultOpen ->
            setupFolders(folders, isShowNotesCount, isVaultOpen)
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainVaultFragmentBinding.setupFolders(state: UiState<List<Pair<Folder, Int>>>, isShowNotesCount: Boolean, isVaultOpen: Boolean) {
        if (state is UiState.Success) {
            val folders = state.value
            rv.withModels {
                context?.let { context ->
                    if (folders.isEmpty()) {
                        placeholderItem {
                            id("placeholder")
                            placeholder(context.stringResource(R.string.vault_is_empty))
                        }
                    } else {
                        buildFoldersModels(context, folders) { folders ->
                            folders.forEachRecursively { entry, depth ->
                                folderItem {
                                    id(entry.first.id)
                                    folder(entry.first)
                                    notesCount(entry.second)
                                    isManualSorting(false)
                                    isSelected(entry.first.id == selectedDestinationId)
                                    isShowNotesCount(isShowNotesCount)
                                    depth(depth)
                                    isClickable(isVaultOpen)
                                    isLongClickable(isVaultOpen)
                                    onClickListener { _ ->
                                        dismiss()
                                        if (entry.first.id != selectedDestinationId)
                                            navController?.navigateSafely(MainVaultFragmentDirections.actionMainVaultFragmentToFolderFragment(entry.first.id))
                                    }
                                    onLongClickListener { _ ->
                                        dismiss()
                                        navController?.navigateSafely(MainVaultFragmentDirections.actionMainVaultFragmentToFolderDialogFragment(
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

    private fun MainVaultFragmentBinding.openVaultUsingBio() {
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
    }
}