package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.noto.app.databinding.ThemeDialogFragmentBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ThemeDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: ThemeDialogFragmentBinding

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = ThemeDialogFragmentBinding.inflate(inflater, container, false)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state.theme) {
                Theme.SystemTheme -> binding.rbSystemTheme.isChecked = true
                Theme.LightTheme -> binding.rbLightTheme.isChecked = true
                Theme.DarkTheme -> binding.rbDarkTheme.isChecked = true
            }
        }

        binding.rbSystemTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(Theme.SystemTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        binding.rbLightTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(Theme.LightTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.rbDarkTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(Theme.DarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        return binding.root
    }

}