package com.noto.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.noto.app.domain.model.Theme

@Composable
fun NotoTheme(theme: Theme, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDimensions provides dimensions) {
        MaterialTheme(theme.colorScheme, shapes, typography, content)
    }
}

object NotoTheme {
    val dimensions: Dimensions
        @Composable
        get() = LocalDimensions.current
}