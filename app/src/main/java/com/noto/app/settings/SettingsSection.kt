package com.noto.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.noto.app.NotoTheme

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
        if (title != null) Text(text = title, modifier = Modifier.padding(horizontal = NotoTheme.dimensions.medium), style = MaterialTheme.typography.titleSmall)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small),
            content = content
        )
    }
}