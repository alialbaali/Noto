package com.noto.app.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.noto.app.theme.NotoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableDialogItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.clip(MaterialTheme.shapes.small),
        enabled = enabled,
        color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = modifier
                .padding(NotoTheme.dimensions.medium)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                LocalContentColor provides if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary,
                content = content,
            )
        }
    }
}