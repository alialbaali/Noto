package com.noto.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import androidx.documentfile.provider.DocumentFile
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note

fun Context.exportNote(uri: Uri, library: Library, note: Note): Uri? {
    val fileName = note.title.ifBlank { note.body }
    return DocumentFile.fromTreeUri(this, uri)
        ?.let { it.findFile("Noto") ?: it.createDirectory("Noto") }
        ?.let { it.findFile(library.title) ?: it.createDirectory(library.title) }
        ?.createFile("text/plain", fileName)
        ?.uri
        ?.also { documentUri ->
            val noteContent = note.format()
            contentResolver
                .openOutputStream(documentUri, "w")
                ?.use { it.write(noteContent.toByteArray()) }
        }
}

fun Context.createPinnedShortcut(library: Library): ShortcutInfoCompat {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT, null, this, AppActivity::class.java).apply {
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
        .setShortLabel(library.title)
        .setLongLabel(library.title)
        .setIcon(IconCompat.createWithBitmap(bitmap))
        .build()
}