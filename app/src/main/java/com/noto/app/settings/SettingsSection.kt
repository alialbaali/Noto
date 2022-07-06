package com.noto.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.noto.app.NotoTheme

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(NotoTheme.dimensions.medium),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small),
        content = content
    )
}