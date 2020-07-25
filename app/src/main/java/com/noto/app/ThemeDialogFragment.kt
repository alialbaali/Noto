package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.observe
import com.noto.app.databinding.FragmentDialogThemeBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.sharedViewModel

const val SYSTEM_THEME = 0
const val LIGHT_THEME = 1
const val DARK_THEME = 2
const val THEME_KEY = "Theme"

@ExperimentalCoroutinesApi
class ThemeDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogThemeBinding

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogThemeBinding.inflate(inflater, container, false)

        viewModel.theme.observe(viewLifecycleOwner) { theme ->
            when (theme) {
                SYSTEM_THEME -> binding.rbSystemTheme.isChecked = true
                LIGHT_THEME -> binding.rbLightTheme.isChecked = true
                DARK_THEME -> binding.rbDarkTheme.isChecked = true
            }
        }

        binding.rbSystemTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(SYSTEM_THEME)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        binding.rbLightTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(LIGHT_THEME)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.rbDarkTheme.setOnClickListener {
            dismiss()
            viewModel.setTheme(DARK_THEME)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        return binding.root
    }

}