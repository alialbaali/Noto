package com.noto.app.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.Window
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.Language

private const val IconSize = 512
private const val IconSpacing = 128

fun Context.createPinnedShortcut(folder: Folder): ShortcutInfoCompat {
    val intent = Intent(Constants.Intent.ActionCreateNote, null).apply {
        putExtra(Constants.FolderId, folder.id)
        component = enabledComponentName
    }
    val backgroundColor = folder.color.toResource().let(this::colorResource)
    val iconColor = colorResource(android.R.color.white)
    val bitmap = createBitmap(IconSize, IconSize).applyCanvas {
        drawColor(backgroundColor)
        drawableResource(R.drawable.ic_round_edit_24)?.mutate()?.let { drawable ->
            drawable.setTint(iconColor)
            drawable.setBounds(IconSpacing, IconSpacing, width - IconSpacing, height - IconSpacing)
            drawable.draw(this)
        }
    }
    return ShortcutInfoCompat.Builder(this, folder.id.toString())
        .setIntent(intent)
        .setShortLabel(folder.getTitle(this))
        .setLongLabel(folder.getTitle(this))
        .setIcon(IconCompat.createWithBitmap(bitmap))
        .build()
}

fun Context.applyNightModeConfiguration(window: Window) {
    val insetsController = WindowInsetsControllerCompat(window, window.decorView.rootView)
    val currentConfiguration = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
    if (currentConfiguration == Configuration.UI_MODE_NIGHT_YES) {
        // For API 21 and above
        insetsController.isAppearanceLightStatusBars = false

        // For API 27 and above
        insetsController.isAppearanceLightNavigationBars = false
    } else if (currentConfiguration == Configuration.UI_MODE_NIGHT_NO) {
        // For API 21 and above
        insetsController.isAppearanceLightStatusBars = true

        // For API 27 and above
        insetsController.isAppearanceLightNavigationBars = true
    }
}

fun Context.applySystemBarsColors(window: Window, applyDefaults: Boolean = true) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.statusBarColor = colorResource(android.R.color.black)
        window.navigationBarColor = colorResource(android.R.color.black)
    } else {
        if (applyDefaults) {
            window.statusBarColor = colorAttributeResource(R.attr.notoBackgroundColor)
            window.navigationBarColor = colorAttributeResource(R.attr.notoBackgroundColor)
        }
    }
}

val Context.enabledComponentName: ComponentName
    get() {
        val appActivityComponentName = ComponentName(this, AppActivity::class.java)
        val iconsComponentNames = Icon.values()
            .map { it.toActivityAliasName(isAppActivityIconEnabled = false) }
            .map { ComponentName(this, it) }
        val enabledComponentName = iconsComponentNames
            .firstOrNull {
                packageManager?.getComponentEnabledSetting(it) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } ?: appActivityComponentName
        return enabledComponentName
    }

fun Context.getComponentNameForIcon(icon: Icon): ComponentName {
    val activityComponentName = ComponentName(this, AppActivity::class.java)
    val isAppActivityEnabled = enabledComponentName == activityComponentName
    val iconClassName = icon.toActivityAliasName(isAppActivityEnabled)
    return ComponentName(this, iconClassName)
}

fun Context.localize(language: Language): Context {
    val locale = language.toLocale()
    val configuration = this.resources?.configuration
    val localizedConfiguration = Configuration(configuration).also {
        it.setLocale(locale)
        it.setLayoutDirection(locale)
    }
    return this.createConfigurationContext(localizedConfiguration) ?: this
}

fun Context.pixelsOf(unit: Int, size: Float) = TypedValue.applyDimension(unit, size, resources.displayMetrics)