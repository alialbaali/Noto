package com.noto.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.noto.app.domain.model.Theme
import com.noto.app.util.Constants
import com.noto.app.util.applyNightModeConfiguration
import com.noto.app.util.applySystemBarsColorsForApiLessThan23
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private var currentTheme = Theme.System

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        currentTheme = intent?.getStringExtra(Constants.Theme)?.let(Theme::valueOf) ?: Theme.System
        currentTheme.toAndroidTheme().also(::setTheme)
        super.onCreate(savedInstanceState)
        setupState()
    }

    private fun setupState() {
        viewModel.theme
            .onEach(::setupTheme)
            .launchIn(lifecycleScope)

        applyNightModeConfiguration(window)
        applySystemBarsColorsForApiLessThan23(window)
    }

    protected open fun setupTheme(theme: Theme) {
        when (theme) {
            Theme.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Theme.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.SystemBlack -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Theme.Black -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        if (theme != currentTheme) {
            intent = Intent().apply { putExtra(Constants.Theme, theme.name) }
            recreate()
        }
    }

    private fun Theme.toAndroidTheme() = when (this) {
        Theme.System, Theme.Light, Theme.Dark -> R.style.LightDarkTheme
        Theme.SystemBlack, Theme.Black -> R.style.LightBlackTheme
    }
}