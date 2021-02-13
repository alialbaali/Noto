package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ThemeDialogFragmentBinding
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ThemeDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ThemeDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = stringResource(R.string.theme)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state.theme) {
                Theme.SystemTheme -> rbSystemTheme.isChecked = true
                Theme.LightTheme -> rbLightTheme.isChecked = true
                Theme.DarkTheme -> rbDarkTheme.isChecked = true
            }
        }

        rbSystemTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(Theme.SystemTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        rbLightTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(Theme.LightTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        rbDarkTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(Theme.DarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}