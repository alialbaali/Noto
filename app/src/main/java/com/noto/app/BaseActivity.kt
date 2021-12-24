package com.noto.app

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.noto.app.domain.model.Theme
import com.noto.app.util.Constants
import com.noto.app.util.colorResource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private val currentTheme
        get() = intent?.getStringExtra(Constants.ThemeKey)?.let(Theme::valueOf)

    override fun onCreate(savedInstanceState: Bundle?) {
        currentTheme?.toAndroidTheme()?.also(::setTheme)
        super.onCreate(savedInstanceState)
        setupState()
    }

    @Suppress("DEPRECATION")
    private fun setupState() {
        viewModel.theme
            .onEach(::setupTheme)
            .launchIn(lifecycleScope)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = colorResource(android.R.color.black)
            window.navigationBarColor = colorResource(android.R.color.black)
        }

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = 0
            }
        }
    }

    private fun setupTheme(theme: Theme) {
        if (currentTheme != theme) {
            intent?.putExtra(Constants.ThemeKey, theme.name)
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