package com.noto.app.components

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.noto.app.AppViewModel
import com.noto.app.R
import com.noto.app.domain.model.Theme
import com.noto.app.util.applyNightModeConfiguration
import com.noto.app.util.applySystemBarsColors
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {

    private val viewModel by viewModel<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        viewModel.currentTheme?.toAndroidTheme()?.also(::setTheme)
        super.onCreate(savedInstanceState)
        setupState()
    }

    private fun setupState() {
        viewModel.theme
            .onEach(::setupTheme)
            .launchIn(lifecycleScope)

        applyNightModeConfiguration(window)
        applySystemBarsColors(window)
    }

    private fun setupTheme(theme: Theme) {
        if (viewModel.currentTheme != theme) {
            viewModel.setCurrentTheme(theme)
            when (theme) {
                Theme.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                Theme.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Theme.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Theme.SystemBlack -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                Theme.Black -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            recreate()
        }
    }

    private fun Theme.toAndroidTheme() = when (this) {
        Theme.System, Theme.Light, Theme.Dark -> R.style.LightDarkTheme
        Theme.SystemBlack, Theme.Black -> R.style.LightBlackTheme
    }
}