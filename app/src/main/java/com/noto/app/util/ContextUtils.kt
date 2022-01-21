package com.noto.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Library

private const val IconSize = 512
private const val IconSpacing = 128

fun Context.createPinnedShortcut(library: Library): ShortcutInfoCompat {
    val intent = Intent(Constants.Intent.ActionCreateNote, null, this, AppActivity::class.java).apply {
        putExtra(Constants.LibraryId, library.id)
    }
    val backgroundColor = library.color.toResource().let(this::colorResource)
    val iconColor = colorResource(android.R.color.white)
    val bitmap = createBitmap(IconSize, IconSize).applyCanvas {
        drawColor(backgroundColor)
        drawableResource(R.drawable.ic_round_edit_24)?.mutate()?.let { drawable ->
            drawable.setTint(iconColor)
            drawable.setBounds(IconSpacing, IconSpacing, width - IconSpacing, height - IconSpacing)
            drawable.draw(this)
        }
    }
    return ShortcutInfoCompat.Builder(this, library.id.toString())
        .setIntent(intent)
        .setShortLabel(library.title)
        .setLongLabel(library.title)
        .setIcon(IconCompat.createWithBitmap(bitmap))
        .build()
}