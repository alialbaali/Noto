package com.noto.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter? = null,
    painterColor: Color = Color.Unspecified,
    description: String? = null,
) {
    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .background(MaterialTheme.colorScheme.surface)
            .padding(NotoTheme.dimensions.medium),
        horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium),
    ) {
        if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = painterColor,
            )
        }

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.small)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = titleColor,
                )

                when (type) {
                    is SettingsItemType.None -> {}
                    is SettingsItemType.Text -> {
                        Spacer(Modifier.width(NotoTheme.dimensions.medium))
                        Text(
                            text = type.value,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1F, fill = false)
                        )
                    }
                    is SettingsItemType.Switch -> {
                        Spacer(Modifier.width(NotoTheme.dimensions.medium))
                        Switch(
                            checked = type.isChecked,
                            onCheckedChange = null,
                            modifier = Modifier.height(24.dp),
                        )
                    }
                }
            }

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}