package com.noto.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.noto.app.NotoTheme

sealed interface SettingsItemType {
    object None : SettingsItemType

    @JvmInline
    value class Text(val value: String) : SettingsItemType

    @JvmInline
    value class Switch(val isChecked: Boolean) : SettingsItemType
}

@Composable
fun SettingsItem(
    title: String,
    type: SettingsItemType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter? = null,
    description: String? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(NotoTheme.dimensions.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (painter != null) Arrangement.Start else Arrangement.SpaceBetween,
    ) {
        if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.width(NotoTheme.dimensions.medium))
        }
        Column(Modifier.weight(1F)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
            if (description != null) {
                Text(text = description, style = MaterialTheme.typography.labelSmall)
            }
        }
        when (type) {
            is SettingsItemType.None -> {}
            is SettingsItemType.Text -> Text(
                text = type.value,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1F, fill = false),
            )
            is SettingsItemType.Switch -> Switch(
                checked = type.isChecked,
                onCheckedChange = null,
                modifier = Modifier.height(24.dp),
            )
        }
    }
}