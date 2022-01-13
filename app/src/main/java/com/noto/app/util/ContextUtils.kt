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

fun Context.createPinnedShortcut(library: Library): ShortcutInfoCompat {
    val intent = Intent(Constants.Intent.ActionCreateNote, null, this, AppActivity::class.java).apply {
        putExtra(Constants.LibraryId, library.id)
    }

    val resourceId = library.color.toResource()

    val size = 512

    val bitmap = createBitmap(size, size).applyCanvas {
        drawColor(colorResource(android.R.color.white))
        drawableResource(R.drawable.ic_round_edit_24)?.mutate()?.let { drawable ->
            drawable.setTint(colorResource(resourceId))
            val spacing = 128
            drawable.setBounds(spacing, spacing, width - spacing, height - spacing)
            drawable.draw(this)
        }
    }

    return ShortcutInfoCompat.Builder(this, library.id.toString())
        .setIntent(intent)
        .setShortLabel(library.getTitle(this))
        .setLongLabel(library.getTitle(this))
        .setIcon(IconCompat.createWithBitmap(bitmap))
        .build()
}