package com.noto.app.components

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AndroidIcon(@DrawableRes id: Int, contentDescription: String?, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context -> ImageView(context) },
        modifier = modifier,
        update = { imageView ->
            imageView.setImageResource(id)
            imageView.contentDescription = contentDescription
        }
    )
}