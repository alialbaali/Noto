package com.noto.app

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.noto.app.domain.model.Theme
import com.noto.app.util.colorResource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {

    private val viewModel by viewModel<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.theme
            .onEach { theme -> setupTheme(theme) }
            .launchIn(lifecycleScope)
        super.onCreate(savedInstanceState)
        setupState()
    }

    @Suppress("DEPRECATION")
    private fun setupState() {
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
        when (theme) {
            Theme.System -> {
                setTheme(R.style.LightDarkTheme)
                restartNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            Theme.Light -> {
                setTheme(R.style.LightDarkTheme)
                restartNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Theme.Dark -> {
                setTheme(R.style.LightDarkTheme)
                restartNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Theme.SystemBlack -> {
                setTheme(R.style.LightBlackTheme)
                restartNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            Theme.Black -> {
                setTheme(R.style.LightBlackTheme)
                restartNightMode()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun restartNightMode() = AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
}