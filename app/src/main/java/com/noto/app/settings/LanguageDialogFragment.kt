package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.AppViewModel
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LanguageDialogFragmentBinding
import com.noto.app.domain.model.Language
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LanguageDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LanguageDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    fun LanguageDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.language)
        }
    }

    fun LanguageDialogFragmentBinding.setupState() {
        viewModel.language
            .onEach { language ->
                when (language) {
                    Language.System -> rbSystem.isChecked = true
                    Language.English -> rbEnglish.isChecked = true
                    Language.Turkish -> rbTurkish.isChecked = true
                    Language.Arabic -> rbArabic.isChecked = true
                    Language.Indonesian -> rbIndonesian.isChecked = true
                    Language.Russian -> rbRussian.isChecked = true
                    Language.Tamil -> rbTamil.isChecked = true
                }
            }
            .launchIn(lifecycleScope)
    }

    fun LanguageDialogFragmentBinding.setupListeners() {
        rbSystem.setOnClickListener {
            viewModel.updateLanguage(Language.System)
            dismiss()
        }

        rbEnglish.setOnClickListener {
            viewModel.updateLanguage(Language.English)
            dismiss()
        }

        rbTurkish.setOnClickListener {
            viewModel.updateLanguage(Language.Turkish)
            dismiss()
        }

        rbArabic.setOnClickListener {
            viewModel.updateLanguage(Language.Arabic)
            dismiss()
        }

        rbIndonesian.setOnClickListener {
            viewModel.updateLanguage(Language.Indonesian)
            dismiss()
        }

        rbRussian.setOnClickListener {
            viewModel.updateLanguage(Language.Russian)
            dismiss()
        }

        rbTamil.setOnClickListener {
            viewModel.updateLanguage(Language.Tamil)
            dismiss()
        }
    }
}